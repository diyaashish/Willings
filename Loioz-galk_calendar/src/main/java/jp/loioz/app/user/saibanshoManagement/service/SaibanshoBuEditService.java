package jp.loioz.app.user.saibanshoManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.saibanshoManagement.form.SaibanshoBuEditForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MSaibanshoBuDao;
import jp.loioz.dto.SaibanshoBuEditDto;
import jp.loioz.entity.MSaibanshoBuEntity;

/**
 * 裁判所係属部編集画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanshoBuEditService extends DefaultService {

	/** 裁判所係属部Daoクラス */
	@Autowired
	private MSaibanshoBuDao mSaibanshoBuDao;

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
	public SaibanshoBuEditForm createViewForm() {
		SaibanshoBuEditForm viewForm = new SaibanshoBuEditForm();
		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	public SaibanshoBuEditForm setData(SaibanshoBuEditForm viewForm) throws AppException {

		// 更新元データの取得
		MSaibanshoBuEntity mSaibanshoBuEntity = mSaibanshoBuDao.selectById(viewForm.getSaibanshoBuEditData().getKeizokuBuId());
		if (mSaibanshoBuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// Entity → Dto
		SaibanshoBuEditDto saibanshoBuEditData = viewForm.getSaibanshoBuEditData();
		saibanshoBuEditData.setKeizokuBuId(mSaibanshoBuEntity.getKeizokuBuId());
		saibanshoBuEditData.setKeizokuBuName(mSaibanshoBuEntity.getKeizokuBuName());
		saibanshoBuEditData.setKeizokuBuTelNo(mSaibanshoBuEntity.getKeizokuBuTelNo());
		saibanshoBuEditData.setKeizokuBuFaxNo(mSaibanshoBuEntity.getKeizokuBuFaxNo());

		// フォームにセット
		viewForm.setSaibanshoBuEditData(saibanshoBuEditData);

		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(SaibanshoBuEditForm viewForm) throws AppException {

		// エンティティの作成
		MSaibanshoBuEntity registEntity = new MSaibanshoBuEntity();

		// 登録するデータの作成
		SaibanshoBuEditDto saibanshoBuEditData = viewForm.getSaibanshoBuEditData();
		registEntity.setSaibanshoId(Long.valueOf(saibanshoBuEditData.getSaibanshoId()));
		registEntity.setKeizokuBuName(saibanshoBuEditData.getKeizokuBuName());
		registEntity.setKeizokuBuTelNo(saibanshoBuEditData.getKeizokuBuTelNo());
		registEntity.setKeizokuBuFaxNo(saibanshoBuEditData.getKeizokuBuFaxNo());

		// 登録処理
		try {
			mSaibanshoBuDao.insert(registEntity);
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
	public void update(SaibanshoBuEditForm viewForm) throws AppException {

		// 更新データの取得
		MSaibanshoBuEntity updateEntity = mSaibanshoBuDao.selectById(viewForm.getSaibanshoBuEditData().getKeizokuBuId());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		SaibanshoBuEditDto saibanshoBuEditData = viewForm.getSaibanshoBuEditData();
		updateEntity.setSaibanshoId(Long.valueOf(saibanshoBuEditData.getSaibanshoId()));
		updateEntity.setKeizokuBuName(saibanshoBuEditData.getKeizokuBuName());
		updateEntity.setKeizokuBuTelNo(saibanshoBuEditData.getKeizokuBuTelNo());
		updateEntity.setKeizokuBuFaxNo(saibanshoBuEditData.getKeizokuBuFaxNo());

		// 更新処理
		try {
			mSaibanshoBuDao.update(updateEntity);
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
	public void delete(SaibanshoBuEditForm viewForm) throws AppException {

		// 削除データの取得
		MSaibanshoBuEntity deleteEntity = mSaibanshoBuDao.selectById(viewForm.getSaibanshoBuEditData().getKeizokuBuId());
		if (deleteEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除フラグを立てる
		deleteEntity.setFlgDelete();

		// 削除処理
		try {
			mSaibanshoBuDao.update(deleteEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

}