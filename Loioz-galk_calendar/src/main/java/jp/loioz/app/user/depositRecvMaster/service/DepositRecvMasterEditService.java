package jp.loioz.app.user.depositRecvMaster.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.depositRecvMaster.form.DepositRecvMasterInputFormForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MDepositItemDao;
import jp.loioz.entity.MDepositItemEntity;

/**
 * 預り金項目編集サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositRecvMasterEditService extends DefaultService {

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
	 * 預り金項目編集モーダル：新規作成用入力フォームオブジェクトの作成
	 *
	 * @return
	 */
	public DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm createDepositRecvMasterEditModalInputForm() {

		// 入力フォームオブジェクトを作成
		DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm = new DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(inputForm);

		// 初期値は入金に設定する
		inputForm.setDepositType(DepositType.NYUKIN.getCd());
		return inputForm;
	}

	/**
	 * 預り金項目編集モーダル：編集用入力フォームオブジェクトの作成
	 *
	 * @param depositItemSeq
	 * @return
	 * @throws AppException
	 */
	public DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm createDepositRecvMasterEditModalInputForm(Long depositItemSeq) throws AppException {

		// 入力フォームオブジェクトを作成
		DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm = new DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(inputForm);

		MDepositItemEntity mDepositItemEntity = mDepositItemDao.selectById(depositItemSeq);
		if (mDepositItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		inputForm.setDepositItemSeq(mDepositItemEntity.getDepositItemSeq());
		inputForm.setDepositType(mDepositItemEntity.getDepositType());
		inputForm.setDepositItemName(mDepositItemEntity.getDepositItemName());
		inputForm.setRemarks(mDepositItemEntity.getRemarks());

		return inputForm;
	}

	/**
	 * 預り金項目編集モーダル 表示用プロパティの設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm) {
		// 現象なし
	}

	/**
	 * 預り金項目マスタの登録処理
	 *
	 * @param inputForm
	 * @throws AppException
	 */
	public void regist(DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm) throws AppException {

		// 登録上限チェック
		if (CommonConstant.DEPOSIR_MASTER_REGIST_LIMIT <= mDepositItemDao.maxDisp()) {
			throw new AppException(MessageEnum.MSG_E00067, null, String.valueOf(CommonConstant.DEPOSIR_MASTER_REGIST_LIMIT));
		}

		// エンティティの作成
		MDepositItemEntity mDepositItemEntity = new MDepositItemEntity();
		long dispOrder = mDepositItemDao.maxDisp();

		// 登録するデータの作成
		mDepositItemEntity.setDepositType(inputForm.getDepositType());
		mDepositItemEntity.setDepositItemName(inputForm.getDepositItemName());
		mDepositItemEntity.setRemarks(inputForm.getRemarks());
		mDepositItemEntity.setDispOrder(dispOrder + 1);

		try {
			// 登録処理
			mDepositItemDao.insert(mDepositItemEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 預り金項目マスタの更新処理
	 *
	 * @param inputForm
	 * @throws AppException
	 */
	public void update(DepositRecvMasterInputFormForm.DepositRecvMasterEditModalInputForm inputForm) throws AppException {

		MDepositItemEntity mDepositItemEntity = mDepositItemDao.selectById(inputForm.getDepositItemSeq());
		if (mDepositItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		mDepositItemEntity.setDepositType(inputForm.getDepositType());
		mDepositItemEntity.setDepositItemName(inputForm.getDepositItemName());
		mDepositItemEntity.setRemarks(inputForm.getRemarks());

		try {
			// 更新処理
			mDepositItemDao.update(mDepositItemEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 預り金項目マスタの削除処理(論理削除)
	 *
	 * @param depositItemSeq
	 * @throws AppException
	 */
	public void delete(Long depositItemSeq) throws AppException {

		// 削除データの取得
		MDepositItemEntity mDepositItemEntity = mDepositItemDao.selectById(depositItemSeq);
		if (mDepositItemEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 論理削除
			mDepositItemDao.delete(mDepositItemEntity);

			// 表示順番号の振り直し
			this.resetAllDispOrder();

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
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

		List<MDepositItemEntity> mDepositItemEntities = mDepositItemDao.selectAllOrderByDispOrder();
		long order = 1;
		List<MDepositItemEntity> updateEntites = new ArrayList<>();
		for (MDepositItemEntity entity : mDepositItemEntities) {
			// 振り直す番号と現在の番号が異なる場合データのみ更新
			if (order != entity.getDispOrder()) {
				entity.setDispOrder(order);
				updateEntites.add(entity);
			}
			order++;
		}

		try {
			// 更新処理
			mDepositItemDao.update(updateEntites);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

	}

}