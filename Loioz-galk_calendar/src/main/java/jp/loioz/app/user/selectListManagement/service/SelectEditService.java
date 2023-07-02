package jp.loioz.app.user.selectListManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.selectListManagement.form.SelectEditForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SelectType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MSelectListDao;
import jp.loioz.dto.SelectEditDto;
import jp.loioz.entity.MSelectListEntity;

/**
 * 選択肢マスタ編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SelectEditService extends DefaultService {

	/** 選択肢マスタのDaoクラス */
	@Autowired
	private MSelectListDao mSelectListDao;

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
	public SelectEditForm createViewForm() {
		SelectEditForm viewForm = new SelectEditForm();

		// デフォルト値として、選択肢区分に「相談経路」を設定します。
		viewForm.getSelectEditData().setSelectType(SelectType.SODANKEIRO.getCd());

		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	public SelectEditForm setData(SelectEditForm viewForm) throws AppException {

		// 更新元データの取得
		MSelectListEntity selectListEntity = mSelectListDao.selectSelectListBySeq(viewForm.getSelectEditData().getSelectSeq());
		if (selectListEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// Entity → Dto
		SelectEditDto selectEditData = new SelectEditDto();
		selectEditData.setSelectSeq(selectListEntity.getSelectSeq());
		selectEditData.setSelectType(selectListEntity.getSelectType());
		selectEditData.setSelectVal(selectListEntity.getSelectVal());
		selectEditData.setDispOrder(selectListEntity.getDispOrder().toString());
		selectEditData.setDeletedAt(selectListEntity.getDeletedAt());
		selectEditData.setVersionNo(selectListEntity.getVersionNo());
		// フォームにセット
		viewForm.setSelectEditData(selectEditData);

		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(SelectEditForm viewForm) throws AppException {

		// エンティティの作成
		MSelectListEntity registEntity = new MSelectListEntity();
		long dispOrder = mSelectListDao.maxDisp();

		// 登録するデータの作成
		SelectEditDto selectEditData = viewForm.getSelectEditData();
		registEntity.setSelectType(SelectType.SODANKEIRO.getCd());
		registEntity.setSelectVal(selectEditData.getSelectVal());
		registEntity.setDispOrder(dispOrder + 1);

		try {
			// 登録処理
			mSelectListDao.insert(registEntity);

		} catch (OptimisticLockingFailureException e) {
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
	public void update(SelectEditForm viewForm) throws AppException {

		// 更新データの取得
		MSelectListEntity updateEntity = mSelectListDao.selectSelectListBySeq(viewForm.getSelectEditData().getSelectSeq());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		SelectEditDto selectEditData = viewForm.getSelectEditData();
		updateEntity.setSelectVal(selectEditData.getSelectVal());

		try {
			// 更新処理
			mSelectListDao.update(updateEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 利用停止処理を行います。
	 *
	 * @param deleteForm 選択肢マスタ用のフォーム
	 * @throws AppException
	 */
	public void stopUsing(Long selectSeq) throws AppException {
		// 利用停止対象の選択肢情報を取得します。
		MSelectListEntity selectListEntity = mSelectListDao.selectSelectListBySeq(selectSeq);
		if (selectListEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新パターンを論理削除に設定します。
		// (利用停止 = 論理削除)
		selectListEntity.setFlgDelete();

		// 更新します。
		try {
			mSelectListDao.update(selectListEntity);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 利用再開処理を行います。
	 *
	 * @param deleteForm 選択肢マスタ用のフォーム
	 * @throws AppException
	 */
	public void restartUsing(Long selectSeq) throws AppException {
		// 利用再開対象の選択肢情報を取得します。
		MSelectListEntity selectListEntity = mSelectListDao.selectSelectListBySeq(selectSeq);
		if (selectListEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除情報を未設定に戻します。
		selectListEntity.setDeletedAt(null);
		selectListEntity.setDeletedBy(null);

		// 更新します。
		try {
			mSelectListDao.update(selectListEntity);
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
		long enabledCount = mSelectListDao.selectAll().size();

		// 登録上限のDBチェック
		if (enabledCount >= CommonConstant.SODAN_KEIRO_REGIST_LIMIT) {
			valid = false;
		}
		return valid;
	}

}