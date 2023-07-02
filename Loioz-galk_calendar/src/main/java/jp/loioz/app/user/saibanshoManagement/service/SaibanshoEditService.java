package jp.loioz.app.user.saibanshoManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.saibanshoManagement.form.SaibanshoEditForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MSaibanshoDao;
import jp.loioz.dto.SaibanshoEditDto;
import jp.loioz.entity.MSaibanshoEntity;

/**
 * 裁判所編集画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanshoEditService extends DefaultService {

	/** 裁判所情報管理用のDaoクラス */
	@Autowired
	private MSaibanshoDao mSaibanshoDao;

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
	public SaibanshoEditForm createViewForm() {
		SaibanshoEditForm viewForm = new SaibanshoEditForm();
		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	public SaibanshoEditForm setData(SaibanshoEditForm viewForm) throws AppException {

		// 更新元データの取得
		MSaibanshoEntity mSaibanshoEntity = mSaibanshoDao.selectById(viewForm.getSaibanshoEditData().getSaibanshoId());
		if (mSaibanshoEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// Entity → Dto
		SaibanshoEditDto saibanshoEditData = new SaibanshoEditDto();
		saibanshoEditData.setSaibanshoId(mSaibanshoEntity.getSaibanshoId());
		saibanshoEditData.setTodofukenId(mSaibanshoEntity.getTodofukenId());
		saibanshoEditData.setSaibanshoZip(mSaibanshoEntity.getSaibanshoZip());
		saibanshoEditData.setSaibanshoAddress1(mSaibanshoEntity.getSaibanshoAddress1());
		saibanshoEditData.setSaibanshoAddress2(mSaibanshoEntity.getSaibanshoAddress2());
		saibanshoEditData.setSaibanshoName(mSaibanshoEntity.getSaibanshoName());
		saibanshoEditData.setVersionNo(mSaibanshoEntity.getVersionNo());
		// フォームにセット
		viewForm.setSaibanshoEditData(saibanshoEditData);

		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(SaibanshoEditForm viewForm) throws AppException {

		// エンティティの作成
		MSaibanshoEntity registEntity = new MSaibanshoEntity();

		// 登録するデータの作成
		SaibanshoEditDto saibanshoEditData = viewForm.getSaibanshoEditData();
		registEntity.setTodofukenId(saibanshoEditData.getTodofukenId());
		registEntity.setSaibanshoZip(saibanshoEditData.getSaibanshoZip());
		registEntity.setSaibanshoAddress1(saibanshoEditData.getSaibanshoAddress1());
		registEntity.setSaibanshoAddress2(saibanshoEditData.getSaibanshoAddress2());
		registEntity.setSaibanshoName(saibanshoEditData.getSaibanshoName());

		// 登録処理
		try {
			mSaibanshoDao.insert(registEntity);
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
	public void update(SaibanshoEditForm viewForm) throws AppException {

		// 更新データの取得
		MSaibanshoEntity updateEntity = mSaibanshoDao.selectById(viewForm.getSaibanshoEditData().getSaibanshoId());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		SaibanshoEditDto saibanshoEditData = viewForm.getSaibanshoEditData();
		updateEntity.setSaibanshoId(saibanshoEditData.getSaibanshoId());
		updateEntity.setSaibanshoZip(saibanshoEditData.getSaibanshoZip());
		updateEntity.setSaibanshoAddress1(saibanshoEditData.getSaibanshoAddress1());
		updateEntity.setSaibanshoAddress2(saibanshoEditData.getSaibanshoAddress2());
		updateEntity.setSaibanshoName(saibanshoEditData.getSaibanshoName());

		// 更新処理
		try {
			mSaibanshoDao.update(updateEntity);
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
	public void delete(SaibanshoEditForm viewForm) throws AppException {

		// 削除データの取得
		MSaibanshoEntity deleteEntity = mSaibanshoDao.selectById(viewForm.getSaibanshoEditData().getSaibanshoId());
		if (deleteEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除フラグを立てる
		deleteEntity.setFlgDelete();

		// 削除処理
		try {
			mSaibanshoDao.update(deleteEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
	}

}