package jp.loioz.app.user.roomManagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.roomManagement.form.RoomEditForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dto.RoomEditDto;
import jp.loioz.entity.MRoomEntity;

/**
 * 施設編集画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoomEditService extends DefaultService {

	/** 会議室情報管理用のDaoクラス */
	@Autowired
	private MRoomDao mRoomDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * viewFormの作成
	 *
	 * @return
	 */
	public RoomEditForm createViewForm() {
		RoomEditForm viewForm = new RoomEditForm();
		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	public RoomEditForm setData(RoomEditForm viewForm) throws AppException {

		// 更新元データの取得
		MRoomEntity mRoomEntity = mRoomDao.selectById(viewForm.getRoomEditData().getRoomId());
		if (mRoomEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// Entity → Dto
		RoomEditDto roomEditData = new RoomEditDto();
		roomEditData.setRoomId(mRoomEntity.getRoomId());
		roomEditData.setRoomName(mRoomEntity.getRoomName());
		roomEditData.setDispOrder(mRoomEntity.getDispOrder().toString());
		roomEditData.setVersionNo(mRoomEntity.getVersionNo());
		// フォームにセット
		viewForm.setRoomEditData(roomEditData);

		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(RoomEditForm viewForm) throws AppException {

		// エンティティの作成
		MRoomEntity registEntity = new MRoomEntity();
		long dispOrder = mRoomDao.maxDisp();

		// 登録するデータの作成
		RoomEditDto roomEditData = viewForm.getRoomEditData();
		registEntity.setRoomName(roomEditData.getRoomName());
		registEntity.setDispOrder(dispOrder + 1);

		// 登録処理
		try {
			mRoomDao.insert(registEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 更新（１件）
	 *
	 * @param viewForm
	 */
	public void update(RoomEditForm viewForm) throws AppException {

		// 更新データの取得
		MRoomEntity updateEntity = mRoomDao.selectById(viewForm.getRoomEditData().getRoomId());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		RoomEditDto roomEditData = viewForm.getRoomEditData();
		updateEntity.setRoomId(roomEditData.getRoomId());
		updateEntity.setRoomName(roomEditData.getRoomName());

		try {
			// 更新処理
			mRoomDao.update(updateEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 削除処理（論理）
	 *
	 * @param viewForm
	 */
	public void delete(RoomEditForm viewForm) throws AppException {

		// 削除データの取得
		MRoomEntity deleteEntity = mRoomDao.selectById(viewForm.getRoomEditData().getRoomId());
		if (deleteEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除フラグを立てる
		deleteEntity.setFlgDelete();

		try {
			// 削除処理
			mRoomDao.update(deleteEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		/* 最新のデータを取得する */
		List<MRoomEntity> entityList = mRoomDao.selectEnabled();

		// 登録件数を確認
		if (entityList.size() > 0) {

			try {

				// 全ての施設の表示順を更新
				List<MRoomEntity> entities = new ArrayList<MRoomEntity>();
				long orderNum = 1;
				for (MRoomEntity entity : entityList) {
					entity.setDispOrder(Long.valueOf(orderNum));
					entities.add(entity);
					orderNum++;
				}
				// 全ての並び順を更新
				mRoomDao.update(entities);

			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());

				throw new AppException(MessageEnum.MSG_E00013, ex);
			}

		}

	}

	/**
	 * 登録時件数上限値チェック
	 *
	 * @return 検証結果
	 */
	public boolean registLimitValidate() {
		boolean valid = true;
		long enabledCount = mRoomDao.selectEnabled().size();

		// 登録上限のDBチェック
		if (enabledCount >= CommonConstant.BUSHO_REGIST_LIMIT) {
			valid = false;
		}
		return valid;
	}
}