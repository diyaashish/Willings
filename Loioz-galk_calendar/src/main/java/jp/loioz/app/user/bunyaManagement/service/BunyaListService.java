package jp.loioz.app.user.bunyaManagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.bunyaManagement.form.BunyaListForm;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MBunyaDao;
import jp.loioz.dto.BunyaListDto;
import jp.loioz.entity.MBunyaEntity;

/**
 * 分野の設定画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BunyaListService extends DefaultService {

	/** 分野マスタ画面用のDaoクラス */
	@Autowired
	private MBunyaDao mBunyaDao;
	
	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 一覧情報の取得
	 */
	public BunyaListForm search(BunyaListForm viewForm) {

		//分野マスタデータ一覧取得
		List<MBunyaEntity> mBunyaEntities = mBunyaDao.selectAll();
		
		// viewにセット
		viewForm.setBunyaList(convertEntity2Dto(mBunyaEntities));

		return viewForm;
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
		List<MBunyaEntity> mBunyaEntities = mBunyaDao.selectAll();

		/* 最新のデータ件数と異なれば排他エラーを呼ぶ */
		if (targetIdArray.length != mBunyaEntities.size()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 変更があるレコードのみ順次更新する */
		try {
			List<MBunyaEntity> entities = new ArrayList<MBunyaEntity>();
			for (int i = 0; i < indexArray.length; i++) {
				MBunyaEntity entity = mBunyaDao.selectById(Long.valueOf(targetIdArray[Integer.parseInt(indexArray[i])]));

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

			updateEntitysCount = mBunyaDao.update(entities);

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
		mBunyaEntities = mBunyaDao.selectAll();
		// 表示順に合わせて並び替え
		List<Long> dispOrderList = mBunyaEntities.stream()
				.sorted(Comparator.comparing(MBunyaEntity::getDispOrder))
				.map(s -> s.getDispOrder())
				.collect(Collectors.toList());

		return dispOrderList;
	}

	/**
	 * 分野
	 * EntityをDtoに変換
	 *
	 * @param List<MBunyaEntity>
	 * @return
	 */
	public List<BunyaListDto> convertEntity2Dto(List<MBunyaEntity> mBunyaEntityList) {

		List<BunyaListDto> bunyaDtoList = new ArrayList<>();

		mBunyaEntityList.stream().forEach((entity) ->{

			BunyaListDto dto = new BunyaListDto();
			dto.setBunyaId(entity.getBunyaId());
			dto.setBunyaType(entity.getBunyaType());
			dto.setBunyaName(entity.getBunyaName());
			dto.setDispOrder(entity.getDispOrder());
			dto.setDisabledFlg(SystemFlg.codeToBoolean(entity.getDisabledFlg()));

			bunyaDtoList.add(dto);
		});

		return bunyaDtoList;
	}
}