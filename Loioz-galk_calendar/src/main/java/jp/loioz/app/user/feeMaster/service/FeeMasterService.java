package jp.loioz.app.user.feeMaster.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.feeMaster.dto.FeeMasterListItemDto;
import jp.loioz.app.user.feeMaster.form.FeeMasterViewForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MFeeItemDao;
import jp.loioz.entity.MFeeItemEntity;

/**
 * 報酬項目の設定画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeMasterService extends DefaultService {

	/** 報酬項目マスタDao */
	@Autowired
	private MFeeItemDao mFeeItemDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 報酬項目一覧フラグメント画面情報オブジェクトを作成する
	 * 
	 * @return
	 */
	public FeeMasterViewForm.FeeMasterListViewForm createFeeMasterListViewForm() {

		// 画面オブジェクトの作成
		FeeMasterViewForm.FeeMasterListViewForm viewFrom = new FeeMasterViewForm.FeeMasterListViewForm();

		// 報酬項目一覧：データ取得・加工・設定
		List<MFeeItemEntity> mFeeItemEntities = mFeeItemDao.selectAllOrderByDispOrder();
		Function<MFeeItemEntity, FeeMasterListItemDto> entity2DtoConverter = this.createEntity2DtoConverter();
		List<FeeMasterListItemDto> feeMasterList = mFeeItemEntities.stream().map(entity2DtoConverter).collect(Collectors.toList());

		viewFrom.setFeeMasterList(feeMasterList);
		return viewFrom;
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
		List<MFeeItemEntity> mFeeItemEntities = mFeeItemDao.selectAllOrderByDispOrder();

		/* 最新のデータ件数と異なれば排他エラーを呼ぶ */
		if (targetIdArray.length != mFeeItemEntities.size()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 変更があるレコードのみ順次更新する */
		try {

			List<MFeeItemEntity> entities = new ArrayList<MFeeItemEntity>();
			for (int i = 0; i < indexArray.length; i++) {
				MFeeItemEntity entity = mFeeItemDao.selectBySeq(Long.valueOf(targetIdArray[Integer.parseInt(indexArray[i])]));

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

			updateEntitysCount = mFeeItemDao.update(entities);

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
		mFeeItemEntities = mFeeItemDao.selectAll();
		// 表示順に合わせて並び替え
		List<Long> dispOrderList = mFeeItemEntities.stream()
				.sorted(Comparator.comparing(MFeeItemEntity::getDispOrder))
				.map(s -> s.getDispOrder())
				.collect(Collectors.toList());

		return dispOrderList;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * Entity -> Dto関数を作成
	 * 
	 * @return
	 */
	private Function<MFeeItemEntity, FeeMasterListItemDto> createEntity2DtoConverter() {
		return (entity) -> {
			FeeMasterListItemDto dto = new FeeMasterListItemDto();
			dto.setFeeItemSeq(entity.getFeeItemSeq());
			dto.setFeeItemName(entity.getFeeItemName());
			dto.setRemarks(entity.getRemarks());
			dto.setDispOrder(entity.getDispOrder());
			return dto;
		};
	}

}