package jp.loioz.app.user.depositRecvMaster.service;

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
import jp.loioz.app.user.depositRecvMaster.dto.DepositRecvMasterListItemDto;
import jp.loioz.app.user.depositRecvMaster.form.DepositRecvMasterSearchForm;
import jp.loioz.app.user.depositRecvMaster.form.DepositRecvMasterViewForm;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MDepositItemDao;
import jp.loioz.domain.condition.DepositRecvMasterListSearchCondition;
import jp.loioz.entity.MDepositItemEntity;

/**
 * 会計データの設定画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositRecvMasterService extends DefaultService {

	/** 預り金項目マスタDaoクラス */
	@Autowired
	private MDepositItemDao mDepositItemDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 預り金項目画面の表示処理
	 *
	 * @param form フォーム情報
	 */
	public DepositRecvMasterViewForm.DepositRecvMasterListViewForm createDepositRecvMasterListViewForm(DepositRecvMasterSearchForm.DepositRecvMasterListSearchForm searchForm) {

		DepositRecvMasterViewForm.DepositRecvMasterListViewForm viewForm = new DepositRecvMasterViewForm.DepositRecvMasterListViewForm();

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索条件
		DepositRecvMasterListSearchCondition conditions = searchForm.toDepositRecvMasterListSearchCondition();

		// 預り金項目を取得
		List<MDepositItemEntity> mDepositItemEntities = mDepositItemDao.selectDepositRecvMasterListByConditions(conditions, options);

		// entityをDtoに変換
		Function<MDepositItemEntity, DepositRecvMasterListItemDto> converter = this.createEntity2DtoConverter();
		List<DepositRecvMasterListItemDto> depositRecvMasterListItemDtoList = mDepositItemEntities.stream().map(converter).collect(Collectors.toList());
		List<Long> mDepositItemSeqList = mDepositItemEntities.stream().map(MDepositItemEntity::getDepositItemSeq).collect(Collectors.toList());

		// 取得したデータをページ情報にセット
		Page<Long> page = new PageImpl<>(mDepositItemSeqList, pageable, options.getCount());

		if (!conditions.hasCondition() && page.getTotalPages() <= 1) {
			// ソート可能条件 表示順が有効のものをすべて画面に表示されていること
			// 検索条件が設定されている場合、複数ページャの場合はソート不可（登録上限50件のため、複数ページャにはならないはず）
			viewForm.setCanSort(true);
		}

		viewForm.setDepositRecvMasterListItemDtoList(depositRecvMasterListItemDtoList);
		viewForm.setPage(page);

		return viewForm;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

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
		List<MDepositItemEntity> mDepositItemEntities = mDepositItemDao.selectAllOrderByDispOrder();

		/* 最新のデータ件数と異なれば排他エラーを呼ぶ */
		if (targetIdArray.length != mDepositItemEntities.size()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 変更があるレコードのみ順次更新する */
		try {

			List<MDepositItemEntity> entities = new ArrayList<MDepositItemEntity>();
			for (int i = 0; i < indexArray.length; i++) {
				MDepositItemEntity entity = mDepositItemDao.selectById(Long.valueOf(targetIdArray[Integer.parseInt(indexArray[i])]));

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

			updateEntitysCount = mDepositItemDao.update(entities);

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
		mDepositItemEntities = mDepositItemDao.selectAll();
		// 表示順に合わせて並び替え
		List<Long> dispOrderList = mDepositItemEntities.stream()
				.sorted(Comparator.comparing(MDepositItemEntity::getDispOrder))
				.map(s -> s.getDispOrder())
				.collect(Collectors.toList());

		return dispOrderList;
	}

	/**
	 * Entity -> Dtoの変換関数を作成
	 * 
	 * @return
	 */
	private Function<MDepositItemEntity, DepositRecvMasterListItemDto> createEntity2DtoConverter() {
		return (entity) -> {
			DepositRecvMasterListItemDto dto = new DepositRecvMasterListItemDto();
			dto.setDepositItemSeq(entity.getDepositItemSeq());
			dto.setDepositType(DepositType.of(entity.getDepositType()));
			dto.setDepositItemName(entity.getDepositItemName());
			dto.setRemarks(entity.getRemarks());
			dto.setDispOrder(entity.getDispOrder());
			return dto;
		};
	}

}