package jp.loioz.app.user.funcOldKaikeiSetting.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonTenantFuncSettingService;
import jp.loioz.app.user.funcOldKaikeiSetting.dto.FuncOldKaikeiSettingListItemDto;
import jp.loioz.app.user.funcOldKaikeiSetting.form.FuncOldKaikeiSettingInputForm;
import jp.loioz.app.user.funcOldKaikeiSetting.form.FuncOldKaikeiSettingSearchForm;
import jp.loioz.app.user.funcOldKaikeiSetting.form.FuncOldKaikeiSettingViewForm;
import jp.loioz.common.constant.CommonConstant.DisabledFlg;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.TenantFuncSettingConstant.TenantFuncSetting;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.domain.condition.FuncOldKaikeiSettingListSearchCondition;
import jp.loioz.entity.MNyushukkinKomokuEntity;

/**
 * 会計データの設定画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FuncOldKaikeiSettingService extends DefaultService {

	/** テナント機能設定の共通サービス */
	@Autowired
	private CommonTenantFuncSettingService commonTenantFuncSettingService;
	
	/** 入出金項目マスタDaoクラス */
	@Autowired
	private MNyushukkinKomokuDao ｍNyushukkinKomokuDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 旧会計管理設定画面の基本設定入力フォームを作成
	 * 
	 * @return basicSettingInputForm
	 */
	public FuncOldKaikeiSettingInputForm.FuncOldKaikeiBasicSettingInputForm createFuncOldKaikeiBasicSettingInputForm() {
		
		FuncOldKaikeiSettingInputForm.FuncOldKaikeiBasicSettingInputForm basicSettingInputForm = new FuncOldKaikeiSettingInputForm.FuncOldKaikeiBasicSettingInputForm();
		
		// テナント機能設定の「旧会計機能のON／OFF」の設定値をフォームに設定
		String settingValue = commonTenantFuncSettingService.getSavedSettingValue(TenantFuncSetting.OLD_KAIKEI_ON_OFF);
		basicSettingInputForm.setIsOldKaikeiOn(settingValue);
		
		return basicSettingInputForm;
	}
	
	/**
	 * 旧会計管理設定画面の表示フォームを作成
	 *
	 * @param form フォーム情報
	 */
	public FuncOldKaikeiSettingViewForm.FuncOldKaikeiSettingListViewForm createFuncOldKaikeiSettingListViewForm(FuncOldKaikeiSettingSearchForm.FuncOldKaikeiSettingListSearchForm searchForm) {

		FuncOldKaikeiSettingViewForm.FuncOldKaikeiSettingListViewForm viewForm = new FuncOldKaikeiSettingViewForm.FuncOldKaikeiSettingListViewForm();

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索条件
		FuncOldKaikeiSettingListSearchCondition conditions = searchForm.toFuncOldKaikeiSettingListSearchCondition();

		// 入出金項目を取得
		List<MNyushukkinKomokuEntity> mNyushukkinKomokuEntities = null;
		String nyushukkinType = conditions.getNyushukkinType();
		if (StringUtils.isNotEmpty(nyushukkinType)) {
			// 検索条件あり
			mNyushukkinKomokuEntities = ｍNyushukkinKomokuDao.selectByDeletableByNyushukkinType(conditions.getNyushukkinType());
		} else {
			// 検索条件なし
			mNyushukkinKomokuEntities = ｍNyushukkinKomokuDao.selectEnableAll();
		}

		// entityをDtoに変換
		Function<MNyushukkinKomokuEntity, FuncOldKaikeiSettingListItemDto> converter = this.createEntity2DtoConverter();
		List<FuncOldKaikeiSettingListItemDto> funcOldKaikeiSettingListItemDtoList = mNyushukkinKomokuEntities.stream().map(converter).collect(Collectors.toList());
		List<Long> nyushukkinKomokuIdList = mNyushukkinKomokuEntities.stream().map(MNyushukkinKomokuEntity::getNyushukkinKomokuId).collect(Collectors.toList());

		// 取得したデータをページ情報にセット
		Page<Long> page = new PageImpl<>(nyushukkinKomokuIdList, pageable, options.getCount());

		if (!conditions.hasCondition() && page.getTotalPages() <= 1) {
			// ソート可能条件 表示順が有効のものをすべて画面に表示されていること
			// 検索条件が設定されている場合、複数ページャの場合はソート不可（登録上限50件のため、複数ページャにはならないはず）
			viewForm.setCanSort(true);
		}

		viewForm.setFuncOldKaikeiSettingListItemDtoList(funcOldKaikeiSettingListItemDtoList);
		viewForm.setPage(page);

		return viewForm;
	}

	/**
	 * 旧会計管理の機能設定の保存
	 * 
	 * @param basicSettingInputForm
	 * @throws AppException 
	 */
	public void saveFuncOldKaikeiBasicSetting(FuncOldKaikeiSettingInputForm.FuncOldKaikeiBasicSettingInputForm basicSettingInputForm) throws AppException {
		
		String isOldKaikeiOn = basicSettingInputForm.getIsOldKaikeiOn();
		
		// 保存処理
		commonTenantFuncSettingService.updateSetting(TenantFuncSetting.OLD_KAIKEI_ON_OFF, isOldKaikeiOn);
	}
	
	/**
	 * 表示順変更処理
	 *
	 * @param targetId
	 * @param index
	 * @return
	 * @throws AppException
	 */
	public List<Long> updateDispOrder(String targetId, String index) throws AppException {
		String[] targetIdArray = targetId.split(",");
		String[] indexArray = index.split(",");

		/* 更新件数 */
		int[] updateEntitysCount = null;

		/* 最新のデータを取得する */
		List<MNyushukkinKomokuEntity> mNyushukkinKomokuEntities = ｍNyushukkinKomokuDao.selectEnableAll();

		/* 最新のデータ件数と異なれば排他エラーを呼ぶ */
		if (targetIdArray.length != mNyushukkinKomokuEntities.size()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 変更があるレコードのみ順次更新する */
		try {

			List<MNyushukkinKomokuEntity> entities = new ArrayList<MNyushukkinKomokuEntity>();
			for (int i = 0; i < indexArray.length; i++) {
				MNyushukkinKomokuEntity entity = ｍNyushukkinKomokuDao.selectById(Long.valueOf(targetIdArray[Integer.parseInt(indexArray[i])]));

				/* 既にデータがなければ排他エラーを呼ぶ */
				if (entity == null) {
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

					throw new AppException(MessageEnum.MSG_E00025, null);

				} else {
					entity.setDispOrder(Long.parseLong(indexArray[i]) + 1);
					entities.add(entity);
				}
			}

			updateEntitysCount = ｍNyushukkinKomokuDao.update(entities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
			// 更新処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}

		/* 最新のデータを取得する */
		mNyushukkinKomokuEntities = ｍNyushukkinKomokuDao.selectEnableAll();
		// 表示順に合わせて並び替え
		List<Long> dispOrderList = mNyushukkinKomokuEntities.stream()
				.sorted(Comparator.comparing(MNyushukkinKomokuEntity::getDispOrder))
				.map(s -> s.getDispOrder())
				.collect(Collectors.toList());

		return dispOrderList;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================
	
	/**
	 * Entity -> Dtoの変換関数を作成
	 * 
	 * @return
	 */
	private Function<MNyushukkinKomokuEntity, FuncOldKaikeiSettingListItemDto> createEntity2DtoConverter() {
		return (entity) -> {
			FuncOldKaikeiSettingListItemDto dto = new FuncOldKaikeiSettingListItemDto();
			dto.setNyushukkinKomokuId(entity.getNyushukkinKomokuId());
			dto.setNyushukkinType(NyushukkinType.of(entity.getNyushukkinType()));
			dto.setKomokuName(entity.getKomokuName());
			dto.setDispOrder(entity.getDispOrder());
			dto.setTaxFlg(TaxFlg.of(entity.getTaxFlg()));
			dto.setUndeletableFlg(entity.getUndeletableFlg());
			dto.setDisabledFlg(DisabledFlg.of(entity.getDisabledFlg()));
			return dto;
		};
	}

}