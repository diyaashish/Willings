package jp.loioz.app.user.funcOldKaikeiSetting.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.funcOldKaikeiSetting.form.FuncOldKaikeiSettingInputForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.entity.MNyushukkinKomokuEntity;

/**
 * 入出金項目編集サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FuncOldKaikeiSettingEditService extends DefaultService {

	/** 入出金項目マスタDaoクラス */
	@Autowired
	private MNyushukkinKomokuDao mNyushukkinKomokuDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;
	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 入出金項目編集モーダル：新規作成用入力フォームオブジェクトの作成
	 *
	 * @return
	 */
	public FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm createFuncOldKaikeiSettingEditModalInputForm() {

		// 入力フォームオブジェクトを作成
		FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm = new FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(inputForm);

		// 初期値は入金に設定する
		inputForm.setNyushukkinType(NyushukkinType.NYUKIN.getCd());
		inputForm.setTaxFlg(TaxFlg.FREE_TAX.getCd());
		return inputForm;
	}

	/**
	 * 入出金項目編集モーダル：編集用入力フォームオブジェクトの作成
	 *
	 * @param nyushukkinKomokuId
	 * @return
	 * @throws AppException
	 */
	public FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm createFuncOldKaikeiSettingEditModalInputForm(Long nyushukkinKomokuId) throws AppException {

		// 入力フォームオブジェクトを作成
		FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm = new FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm();

		// 表示用プロパティの設定
		this.setDispProperties(inputForm);

		MNyushukkinKomokuEntity mNyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(nyushukkinKomokuId);
		if (mNyushukkinKomokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		inputForm.setNyushukkinKomokuId(mNyushukkinKomokuEntity.getNyushukkinKomokuId());
		inputForm.setNyushukkinType(mNyushukkinKomokuEntity.getNyushukkinType());
		inputForm.setKomokuName(mNyushukkinKomokuEntity.getKomokuName());
		inputForm.setTaxFlg(mNyushukkinKomokuEntity.getTaxFlg());
		inputForm.setDisabledFlg(mNyushukkinKomokuEntity.getDisabledFlg());

		return inputForm;
	}

	/**
	 * 入出金項目編集モーダル 表示用プロパティの設定
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm) {
		// 現象なし
	}

	/**
	 * 入出金項目マスタの登録処理
	 *
	 * @param inputForm
	 * @throws AppException
	 */
	public void regist(FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm) throws AppException {

		// 登録上限チェック
		if (CommonConstant.DEPOSIR_MASTER_REGIST_LIMIT <= mNyushukkinKomokuDao.maxDisp()) {
			throw new AppException(MessageEnum.MSG_E00067, null, String.valueOf(CommonConstant.DEPOSIR_MASTER_REGIST_LIMIT));
		}

		// エンティティの作成
		MNyushukkinKomokuEntity mNyushukkinKomokuEntity = new MNyushukkinKomokuEntity();
		long dispOrder = mNyushukkinKomokuDao.maxDisp();

		// 登録するデータの作成
		mNyushukkinKomokuEntity.setNyushukkinType(inputForm.getNyushukkinType());
		mNyushukkinKomokuEntity.setKomokuName(inputForm.getKomokuName());
		mNyushukkinKomokuEntity.setTaxFlg(inputForm.getTaxFlg());
		mNyushukkinKomokuEntity.setDispOrder(dispOrder + 1);
		mNyushukkinKomokuEntity.setUndeletableFlg(SystemFlg.FLG_OFF.getCd());
		mNyushukkinKomokuEntity.setDisabledFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// 登録処理
			mNyushukkinKomokuDao.insert(mNyushukkinKomokuEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 入出金項目マスタの更新処理
	 *
	 * @param inputForm
	 * @throws AppException
	 */
	public void update(FuncOldKaikeiSettingInputForm.FuncOldKaikeiSettingEditModalInputForm inputForm) throws AppException {

		MNyushukkinKomokuEntity mNyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(inputForm.getNyushukkinKomokuId());
		if (mNyushukkinKomokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		mNyushukkinKomokuEntity.setNyushukkinType(inputForm.getNyushukkinType());
		mNyushukkinKomokuEntity.setKomokuName(inputForm.getKomokuName());

		try {
			// 更新処理
			mNyushukkinKomokuDao.update(mNyushukkinKomokuEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 入出金項目マスタの利用停止・再開処理
	 *
	 * @param nyushukkinKomokuId
	 * @throws AppException
	 */
	public void using(Long nyushukkinKomokuId) throws AppException {

		// 利用停止データの取得
		MNyushukkinKomokuEntity mNyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(nyushukkinKomokuId);
		if (mNyushukkinKomokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			if (SystemFlg.FLG_ON.equalsByCode(mNyushukkinKomokuEntity.getDisabledFlg())) {
				// 再開
				mNyushukkinKomokuEntity.setDisabledFlg(SystemFlg.FLG_OFF.getCd());
			} else {
				// 利用停止
				mNyushukkinKomokuEntity.setDisabledFlg(SystemFlg.FLG_ON.getCd());
			}

			// 更新
			mNyushukkinKomokuDao.update(mNyushukkinKomokuEntity);

			// 表示順番号の振り直し
			this.resetAllDispOrder();

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 入出金項目マスタの削除処理（論理）
	 *
	 * @param nyushukkinKomokuId
	 * @throws AppException
	 */
	public void delete(Long nyushukkinKomokuId) throws AppException {

		// 利用停止データの取得
		MNyushukkinKomokuEntity mNyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(nyushukkinKomokuId);
		if (mNyushukkinKomokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 論理削除
			mNyushukkinKomokuEntity.setDeletedAt(LocalDateTime.now());
			mNyushukkinKomokuEntity.setDeletedBy(SessionUtils.getLoginAccountSeq());

			// 更新
			mNyushukkinKomokuDao.update(mNyushukkinKomokuEntity);

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

		List<MNyushukkinKomokuEntity> mNyushukkinKomokuEntities = mNyushukkinKomokuDao.selectEnableAll();
		long order = 1;
		List<MNyushukkinKomokuEntity> updateEntites = new ArrayList<>();
		for (MNyushukkinKomokuEntity entity : mNyushukkinKomokuEntities) {
			// 振り直す番号と現在の番号が異なる場合データのみ更新
			if (order != entity.getDispOrder()) {
				entity.setDispOrder(order);
				updateEntites.add(entity);
			}
			order++;
		}

		try {
			// 更新処理
			mNyushukkinKomokuDao.update(updateEntites);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

	}

}