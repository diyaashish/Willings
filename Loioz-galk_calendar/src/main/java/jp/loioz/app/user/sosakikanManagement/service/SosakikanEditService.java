package jp.loioz.app.user.sosakikanManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.sosakikanManagement.form.SosakikanEditForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MSosakikanDao;
import jp.loioz.dto.SosakikanEditDto;
import jp.loioz.entity.MSosakikanEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class SosakikanEditService extends DefaultService {

	/** 捜査機関情報管理用のDaoクラス */
	@Autowired
	private MSosakikanDao mSosakikanDao;

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
	public SosakikanEditForm createViewForm() {
		SosakikanEditForm viewForm = new SosakikanEditForm();
		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	public SosakikanEditForm setData(SosakikanEditForm viewForm) throws AppException {

		// 更新元データの取得
		MSosakikanEntity mSosakikanEntity = mSosakikanDao.selectNotDeletedById(viewForm.getSosakikanEditData().getSosakikanId());
		if (mSosakikanEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// Entity → Dto
		SosakikanEditDto saibanshoEditData = new SosakikanEditDto();
		saibanshoEditData.setSosakikanId(mSosakikanEntity.getSosakikanId());
		saibanshoEditData.setTodofukenId(mSosakikanEntity.getTodofukenId());
		saibanshoEditData.setSosakikanZip(mSosakikanEntity.getSosakikanZip());
		saibanshoEditData.setSosakikanAddress1(mSosakikanEntity.getSosakikanAddress1());
		saibanshoEditData.setSosakikanAddress2(mSosakikanEntity.getSosakikanAddress2());
		saibanshoEditData.setSosakikanName(mSosakikanEntity.getSosakikanName());
		saibanshoEditData.setSosakikanType(mSosakikanEntity.getSosakikanType());
		saibanshoEditData.setSosakikanTelNo(mSosakikanEntity.getSosakikanTelNo());
		saibanshoEditData.setSosakikanFaxNo(mSosakikanEntity.getSosakikanFaxNo());
		saibanshoEditData.setVersionNo(mSosakikanEntity.getVersionNo());

		// フォームにセット
		viewForm.setSosakikanEditData(saibanshoEditData);

		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(SosakikanEditForm viewForm) throws AppException {

		// エンティティの作成
		MSosakikanEntity registEntity = new MSosakikanEntity();

		// 登録するデータの作成
		SosakikanEditDto saibanshoEditData = viewForm.getSosakikanEditData();
		registEntity.setSosakikanZip(saibanshoEditData.getSosakikanZip());
		registEntity.setSosakikanAddress1(saibanshoEditData.getSosakikanAddress1());
		registEntity.setSosakikanAddress2(saibanshoEditData.getSosakikanAddress2());
		registEntity.setTodofukenId(saibanshoEditData.getTodofukenId());
		registEntity.setSosakikanName(saibanshoEditData.getSosakikanName());
		registEntity.setSosakikanType(saibanshoEditData.getSosakikanType());
		registEntity.setSosakikanTelNo(saibanshoEditData.getSosakikanTelNo());
		registEntity.setSosakikanFaxNo(saibanshoEditData.getSosakikanFaxNo());

		// 登録処理
		try {
			mSosakikanDao.insert(registEntity);
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
	public void update(SosakikanEditForm viewForm) throws AppException {

		// 更新データの取得
		MSosakikanEntity updateEntity = mSosakikanDao.selectNotDeletedById(viewForm.getSosakikanEditData().getSosakikanId());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		SosakikanEditDto saibanshoEditData = viewForm.getSosakikanEditData();
		updateEntity.setSosakikanId(saibanshoEditData.getSosakikanId());
		updateEntity.setSosakikanZip(saibanshoEditData.getSosakikanZip());
		updateEntity.setSosakikanAddress1(saibanshoEditData.getSosakikanAddress1());
		updateEntity.setSosakikanAddress2(saibanshoEditData.getSosakikanAddress2());
		updateEntity.setSosakikanName(saibanshoEditData.getSosakikanName());
		updateEntity.setSosakikanType(saibanshoEditData.getSosakikanType());
		updateEntity.setSosakikanTelNo(saibanshoEditData.getSosakikanTelNo());
		updateEntity.setSosakikanFaxNo(saibanshoEditData.getSosakikanFaxNo());

		// 更新処理
		try {
			mSosakikanDao.update(updateEntity);
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
	public void delete(SosakikanEditForm viewForm) throws AppException {

		// 削除データの取得
		MSosakikanEntity deleteEntity = mSosakikanDao.selectNotDeletedById(viewForm.getSosakikanEditData().getSosakikanId());
		if (deleteEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除フラグを立てる
		deleteEntity.setFlgDelete();

		// 削除処理
		try {
			mSosakikanDao.update(deleteEntity);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
	}
}
