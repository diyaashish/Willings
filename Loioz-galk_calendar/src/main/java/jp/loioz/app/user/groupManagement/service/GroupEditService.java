package jp.loioz.app.user.groupManagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.groupManagement.form.GroupEditForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MGroupDao;
import jp.loioz.dto.GroupEditDto;
import jp.loioz.entity.MGroupEntity;

/**
 * グループ編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupEditService extends DefaultService {
	/** グループデータ用のDaoクラス */
	@Autowired
	private MGroupDao mGroupDao;

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
	public GroupEditForm createViewForm() {
		GroupEditForm viewForm = new GroupEditForm();
		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	public GroupEditForm setData(GroupEditForm viewForm) throws AppException {

		// 更新元データの取得
		MGroupEntity mGroupEntity = mGroupDao.selectById(viewForm.getGroupEditData().getGroupId());
		if (mGroupEntity == null) {
			// 排他エラーチェックをします。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// Entity → Dto
		GroupEditDto bushoEditData = new GroupEditDto();
		bushoEditData.setGroupId(mGroupEntity.getGroupId());
		bushoEditData.setGroupName(mGroupEntity.getGroupName());
		bushoEditData.setDispOrder(mGroupEntity.getDispOrder().toString());
		bushoEditData.setVersionNo(mGroupEntity.getVersionNo());
		// フォームにセット
		viewForm.setGroupEditData(bushoEditData);

		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(GroupEditForm viewForm) throws AppException {

		// エンティティの作成
		MGroupEntity registEntity = new MGroupEntity();

		// 登録するデータの作成
		GroupEditDto bushoEditData = viewForm.getGroupEditData();
		registEntity.setGroupName(bushoEditData.getGroupName());
		registEntity.setDispOrder(Long.valueOf(bushoEditData.getDispOrder()));

		// 登録処理
		try {
			mGroupDao.insert(registEntity);
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
	public void update(GroupEditForm viewForm) throws AppException {

		// 更新データの取得
		MGroupEntity updateEntity = mGroupDao.selectById(viewForm.getGroupEditData().getGroupId());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		GroupEditDto bushoEditData = viewForm.getGroupEditData();
		updateEntity.setGroupId(bushoEditData.getGroupId());
		updateEntity.setGroupName(bushoEditData.getGroupName());
		updateEntity.setDispOrder(Long.valueOf(bushoEditData.getDispOrder()));

		// 更新処理
		try {
			mGroupDao.update(updateEntity);
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
	public void delete(GroupEditForm viewForm) throws AppException {

		// 削除データの取得
		MGroupEntity deleteEntity = mGroupDao.selectById(viewForm.getGroupEditData().getGroupId());
		if (deleteEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除フラグを立てる
		deleteEntity.setFlgDelete();

		// 削除処理
		try {
			mGroupDao.update(deleteEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
	}

	/**
	 * 表示順の重複チェック
	 *
	 * @param viewForm
	 * @return true: 重複あり、false：重複なし
	 */
	public boolean duplicateByDispOrder(GroupEditForm viewForm) {
		boolean error = false;
		// 存在チェック
		List<MGroupEntity> mGroupEntityList = mGroupDao.selectByDispOrder(viewForm.getGroupEditData().getGroupId(),
				Long.valueOf(viewForm.getGroupEditData().getDispOrder()));
		if (!mGroupEntityList.isEmpty()) {
			error = true;
		}
		return error;
	}
}