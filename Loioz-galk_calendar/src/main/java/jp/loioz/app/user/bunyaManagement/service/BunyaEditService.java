package jp.loioz.app.user.bunyaManagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.bunyaManagement.form.BunyaEditForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MBunyaDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dto.BunyaEditDto;
import jp.loioz.entity.MBunyaEntity;

/**
 * 分野の設定編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BunyaEditService extends DefaultService {

	/** 分野マスタのDaoクラス */
	@Autowired
	private MBunyaDao mBunyaDao;
	
	/** 案件情報用のDaoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

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
	public BunyaEditForm createViewForm() {

		// フォーム生成
		BunyaEditForm viewForm = new BunyaEditForm();

		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	public BunyaEditForm setData(BunyaEditForm viewForm) throws AppException {

		// 更新元データの取得
		MBunyaEntity bunyaEntity = mBunyaDao.selectById(viewForm.getBunyaEditData().getBunyaId());

		if (bunyaEntity == null) {
			// 更新対象が存在しない場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// Entity → Dto
		BunyaEditDto bunyaEditData = viewForm.getBunyaEditData();
		bunyaEditData.setBunyaId(bunyaEntity.getBunyaId());
		bunyaEditData.setBunyaType(bunyaEntity.getBunyaType());
		bunyaEditData.setBunyaName(bunyaEntity.getBunyaName());
		bunyaEditData.setDispOrder(bunyaEntity.getDispOrder());
		bunyaEditData.setDisabledFlg(SystemFlg.codeToBoolean(bunyaEntity.getDisabledFlg()));

		// フォームにセット
		viewForm.setBunyaEditData(bunyaEditData);

		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(BunyaEditForm viewForm) throws AppException {

		// エンティティの作成
		MBunyaEntity registEntity = new MBunyaEntity();
		long dispOrder = mBunyaDao.maxDisp();

		// 登録するデータの作成
		BunyaEditDto bunyaEditData = viewForm.getBunyaEditData();
		registEntity.setBunyaType(bunyaEditData.getBunyaType());
		registEntity.setBunyaName(bunyaEditData.getBunyaName());
		registEntity.setDispOrder(dispOrder + 1);
		registEntity.setDisabledFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// 登録処理
			mBunyaDao.insert(registEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, e);
		}

	}

	/**
	 * 更新（１件）
	 *
	 * @param viewForm
	 */
	public void update(BunyaEditForm viewForm) throws AppException {
		// 更新前に再度確認する（分野区分変更可否チェック）
		if (this.checkTAnkenExistBunyaIdForUpdate(viewForm.getBunyaEditData())) {
			// 案件テーブルに対象の分野が存在した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新データの取得
		MBunyaEntity updateEntity = mBunyaDao.selectById(viewForm.getBunyaEditData().getBunyaId());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		BunyaEditDto bunyaEditData = viewForm.getBunyaEditData();
		updateEntity.setBunyaType(bunyaEditData.getBunyaType());
		updateEntity.setBunyaName(bunyaEditData.getBunyaName());

		try {
			// 更新処理
			mBunyaDao.update(updateEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 利用停止処理を行います。
	 *
	 * @param bunyaId
	 * @throws AppException
	 */
	public void stopUsing(Long bunyaId) throws AppException {
		// 利用停止対象の選択肢情報を取得します。
		MBunyaEntity mBunyaEntity = mBunyaDao.selectById(bunyaId);
		if (mBunyaEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 無効フラグをONに設定
		mBunyaEntity.setDisabledFlg(SystemFlg.FLG_ON.getCd());

		// 更新します。
		try {
			mBunyaDao.update(mBunyaEntity);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 利用再開処理を行います。
	 *
	 * @param bunyaId
	 * @throws AppException
	 */
	public void restartUsing(Long bunyaId) throws AppException {
		// 利用再開対象の選択肢情報を取得します。
		MBunyaEntity mBunyaEntity = mBunyaDao.selectById(bunyaId);
		if (mBunyaEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 無効フラグをOFFに設定
		mBunyaEntity.setDisabledFlg(SystemFlg.FLG_OFF.getCd());

		// 更新します。
		try {
			mBunyaDao.update(mBunyaEntity);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 論理削除（１件）
	 *
	 * @param viewForm
	 */
	public void delete(BunyaEditForm viewForm) throws AppException {
		// 論理削除前に案件情報に存在しないことを再度確認
		if (this.checkTAnkenExistBunyaId(viewForm.getBunyaEditData().getBunyaId())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除データの取得
		MBunyaEntity deleteEntity = mBunyaDao.selectById(viewForm.getBunyaEditData().getBunyaId());
		if (deleteEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 論理削除を設定
		deleteEntity.setFlgDelete();

		try {
			// 論理削除処理
			mBunyaDao.update(deleteEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 登録時件数上限値チェック
	 *
	 * @return 検証結果
	 */
	public boolean registLimitValidate() {
		boolean valid = true;
		long enabledCount = mBunyaDao.selectAll().size();

		// 登録上限のDBチェック
		if (enabledCount >= CommonConstant.BUNYA_REGIST_LIMIT) {
			valid = false;
		}
		return valid;
	}

	/**
	 * 重複チェック<br>
	 * 存在する分野名がないことを確認する
	 * 
	 * @param bunyaName 分野名
	 * @return true:エラーあり、false:エラーなし
	 */
	public boolean duplicateByBunyaName(String bunyaName) {

		boolean error = false;

		// 存在チェック
		List<MBunyaEntity> mBunyaEntities = mBunyaDao.selectByName(bunyaName);
		if (0 < mBunyaEntities.size()) {
			error = true;
		}

		return error;
	}

	/**
	 * 重複チェック(更新用)<br>
	 * 更新対象以外に存在する分野名がないことを確認する
	 * 
	 * @param bunyaId   分野Id
	 * @param bunyaName 分野名
	 * @return true:エラーあり、false:エラーなし
	 */
	public boolean duplicateByNotBunyaIdAndBunyaName(Long bunyaId, String bunyaName) {

		boolean error = false;

		// 存在チェック
		List<MBunyaEntity> mBunyaEntities = mBunyaDao.selectByNotIdAndName(bunyaId, bunyaName);
		if (0 < mBunyaEntities.size()) {
			error = true;
		}

		return error;
	}

	/**
	 * 案件テーブルの分野情報存在チェック<br>
	 * 区分変更がある場合案件に使用されている<br>
	 * 分野情報ではないことを確認する<br>
	 * 
	 * @param editData 更新データ
	 * @return true:エラーあり、false:エラーなし
	 */
	public boolean checkTAnkenExistBunyaIdForUpdate(BunyaEditDto editData) {

		boolean error = false;

		// 変更前データ取得
		MBunyaEntity mBunyaEntity = mBunyaDao.selectById(editData.getBunyaId());

		// 区分の変更がない場合はエラーなし
		if (editData.getBunyaType().equals(mBunyaEntity.getBunyaType())) {
			return error;
		}
		
		// 案件データの存在チェック
		if (this.checkTAnkenExistBunyaId(mBunyaEntity.getBunyaId())) {
			error = true;
			return error;
		}
		return error;
	}

	/**
	 * 案件テーブルの分野情報存在チェック
	 * 
	 * @param bunyaId
	 * @return true:エラーあり、false:エラーなし
	 */
	public boolean checkTAnkenExistBunyaId(Long bunyaId) {
		boolean error = false;
		// 案件データの存在チェック
		long tAnkenDataCount = tAnkenDao.selectCountByBunyaId(bunyaId);
		
		if (0 < tAnkenDataCount) {
			error = true;
		}

		return error;
	}

}