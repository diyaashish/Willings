package jp.loioz.app.user.feeMaster.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.feeMaster.form.FeeMasterInputForm;
import jp.loioz.common.constant.CommonConstant;
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
public class FeeMasterEditService extends DefaultService {

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
	 * 報酬項目一覧編集モーダル入力画面情報オブジェクトを作成する
	 * 
	 * @return
	 */
	public FeeMasterInputForm.FeeMasterItemEditInputForm createFeeMasterItemEditInputForm() {

		// 画面オブジェクトの作成
		FeeMasterInputForm.FeeMasterItemEditInputForm inputForm = new FeeMasterInputForm.FeeMasterItemEditInputForm();
		this.setDispProperties(inputForm);

		return inputForm;
	}

	/**
	 * 報酬項目一覧編集モーダル入力画面情報オブジェクトを作成する
	 * 
	 * @param feeItemSeq
	 * @return
	 */
	public FeeMasterInputForm.FeeMasterItemEditInputForm createFeeMasterItemEditInputForm(Long feeItemSeq) throws AppException {

		// 画面オブジェクトの作成
		FeeMasterInputForm.FeeMasterItemEditInputForm inputForm = createFeeMasterItemEditInputForm();

		// 対象データの取得
		MFeeItemEntity mFeeItemEntity = mFeeItemDao.selectBySeq(feeItemSeq);
		if (mFeeItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力用プロパティを設定する
		inputForm.setFeeItemSeq(mFeeItemEntity.getFeeItemSeq());
		inputForm.setFeeItemName(mFeeItemEntity.getFeeItemName());
		inputForm.setRemarks(mFeeItemEntity.getRemarks());

		return inputForm;
	}

	/**
	 * 報酬項目一覧編集モーダル入力画面情報オブジェクトの表示用プロパティを設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(FeeMasterInputForm.FeeMasterItemEditInputForm inputForm) {
		// 現状なし
	}

	/**
	 * 報酬項目 登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void regist(FeeMasterInputForm.FeeMasterItemEditInputForm inputForm) throws AppException {

		// 登録上限チェック
		if (CommonConstant.FEE_MASTER_REGIST_LIMIT <= mFeeItemDao.maxDisp()) {
			throw new AppException(MessageEnum.MSG_E00067, null, String.valueOf(CommonConstant.FEE_MASTER_REGIST_LIMIT));
		}

		long maxDisp = mFeeItemDao.maxDisp();

		MFeeItemEntity mFeeItemEntity = new MFeeItemEntity();
		mFeeItemEntity.setFeeItemName(inputForm.getFeeItemName());
		mFeeItemEntity.setRemarks(inputForm.getRemarks());
		mFeeItemEntity.setDispOrder(maxDisp + 1);

		try {
			// 新規登録
			mFeeItemDao.insert(mFeeItemEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

	}

	/**
	 * 報酬項目 更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void update(FeeMasterInputForm.FeeMasterItemEditInputForm inputForm) throws AppException {

		MFeeItemEntity mFeeItemEntity = mFeeItemDao.selectBySeq(inputForm.getFeeItemSeq());
		if (mFeeItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		mFeeItemEntity.setFeeItemName(inputForm.getFeeItemName());
		mFeeItemEntity.setRemarks(inputForm.getRemarks());

		try {
			// 新規登録
			mFeeItemDao.update(mFeeItemEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

	}

	/**
	 * 報酬項目 削除処理
	 * 
	 * @param feeItemSeq
	 * @throws AppException
	 */
	public void delete(Long feeItemSeq) throws AppException {

		MFeeItemEntity mFeeItemEntity = mFeeItemDao.selectBySeq(feeItemSeq);
		if (mFeeItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 物理削除処理
			mFeeItemDao.delete(mFeeItemEntity);

			// 表示順のフリ直し
			this.resetAllDispOrder();

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 表示順番号の振り直し
	 * 
	 * @throws AppException
	 */
	private void resetAllDispOrder() throws AppException {

		List<MFeeItemEntity> mFeeItemEntities = mFeeItemDao.selectAllOrderByDispOrder();
		long order = 1;
		List<MFeeItemEntity> updateEntites = new ArrayList<>();
		for (MFeeItemEntity entity : mFeeItemEntities) {
			// 振り直す番号と現在の番号が異なる場合データのみ更新
			if (order != entity.getDispOrder()) {
				entity.setDispOrder(order);
				updateEntites.add(entity);
			}
			order++;
		}

		try {
			// 更新処理
			mFeeItemDao.update(updateEntites);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

	}

}