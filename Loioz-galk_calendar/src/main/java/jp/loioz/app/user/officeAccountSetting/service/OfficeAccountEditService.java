package jp.loioz.app.user.officeAccountSetting.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountEditForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountStatus;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AccountEditDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAuthTokenEntity;

/**
 * アカウント編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OfficeAccountEditService extends DefaultService {

	/** アカウント情報管理用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 契約プランの共通サービス */
	@Autowired
	private CommonPlanService commonPlanService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	/** ロガー */
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
	public OfficeAccountEditForm createViewForm() {
		OfficeAccountEditForm viewForm = new OfficeAccountEditForm();

		// 初期値を設定します
		AccountEditDto accountEditDto = viewForm.getAccountEditData();
		accountEditDto.setAccountType(AccountType.LAWYER.getCd());
		accountEditDto.setAccountKengen(AccountKengen.GENERAL.getCd());
		accountEditDto.setAccountOwnerFlg(SystemFlg.FLG_OFF.getCd());
		accountEditDto.setAccountStatus(AccountStatus.ENABLED.getCd());

		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 */
	public OfficeAccountEditForm setData(OfficeAccountEditForm viewForm) {

		// 更新元データの取得
		MAccountEntity mAccountEntity = mAccountDao.selectBySeq(viewForm.getAccountEditData().getAccountSeq());

		// Entity → Dto
		AccountEditDto accountEditData = new AccountEditDto();
		accountEditData.setAccountSeq(mAccountEntity.getAccountSeq());
		accountEditData.setAccountId(mAccountEntity.getAccountId());
		accountEditData.setAccountNameSei(mAccountEntity.getAccountNameSei());
		accountEditData.setAccountNameSeiKana(mAccountEntity.getAccountNameSeiKana());
		accountEditData.setAccountNameMei(mAccountEntity.getAccountNameMei());
		accountEditData.setAccountNameMeiKana(mAccountEntity.getAccountNameMeiKana());
		accountEditData.setAccountType(mAccountEntity.getAccountType());
		accountEditData.setAccountStatus(mAccountEntity.getAccountStatus());
		accountEditData.setAccountMailAddress(mAccountEntity.getAccountMailAddress());
		accountEditData.setAccountKengen(mAccountEntity.getAccountKengen());
		accountEditData.setAccountOwnerFlg(mAccountEntity.getAccountOwnerFlg());
		accountEditData.setAccountColor(mAccountEntity.getAccountColor());
		accountEditData.setVersionNo(mAccountEntity.getVersionNo());

		// フォームにセット
		viewForm.setAccountEditData(accountEditData);
		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(OfficeAccountEditForm viewForm) throws AppException {
		// エンティティの作成
		MAccountEntity registEntity = new MAccountEntity();

		// 登録するデータの作成
		AccountEditDto accountEditData = viewForm.getAccountEditData();
		registEntity.setAccountId(accountEditData.getAccountId());
		registEntity.setTenantSeq(SessionUtils.getTenantSeq());
		registEntity.setAccountId(accountEditData.getAccountId());
		registEntity.setAccountNameSei(accountEditData.getAccountNameSei());
		registEntity.setAccountNameSeiKana(accountEditData.getAccountNameSeiKana());
		registEntity.setAccountNameMei(accountEditData.getAccountNameMei());
		registEntity.setAccountNameMeiKana(accountEditData.getAccountNameMeiKana());
		registEntity.setAccountType(accountEditData.getAccountType());
		registEntity.setAccountMailAddress(accountEditData.getAccountMailAddress());
		registEntity.setAccountColor(accountEditData.getAccountColor());
		registEntity.setPassword(new BCryptPasswordEncoder().encode(accountEditData.getPassword().getPassword()));
		registEntity.setAccountStatus(CommonConstant.AccountStatus.ENABLED.getCd());

		// 経営者権限ありか判定
		String accountOwnerFlg = accountEditData.getAccountOwnerFlg();
		if (!CommonConstant.SystemFlg.FLG_ON.equalsByCode(accountOwnerFlg)) {
			// 経営者権限なし
			accountOwnerFlg = CommonConstant.SystemFlg.FLG_OFF.getCd();
		}
		registEntity.setAccountOwnerFlg(accountOwnerFlg);

		// システム管理者か判定
		String accountKengen = accountEditData.getAccountKengen();
		if (!CommonConstant.AccountKengen.SYSTEM_MNG.equalsByCode(accountKengen)) {
			// 一般権限
			accountKengen = CommonConstant.AccountKengen.GENERAL.getCd();
		}
		registEntity.setAccountKengen(accountKengen);

		try {
			// 登録処理
			mAccountDao.insert(registEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 更新（１件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void update(OfficeAccountEditForm viewForm) throws AppException {

		// 更新データの取得
		MAccountEntity updateEntity = mAccountDao.selectBySeq(viewForm.getAccountEditData().getAccountSeq());

		// 更新するデータを作成
		AccountEditDto accountEditData = viewForm.getAccountEditData();
		updateEntity.setAccountId(accountEditData.getAccountId());
		updateEntity.setAccountNameSei(accountEditData.getAccountNameSei());
		updateEntity.setAccountNameSeiKana(accountEditData.getAccountNameSeiKana());
		updateEntity.setAccountNameMei(accountEditData.getAccountNameMei());
		updateEntity.setAccountNameMeiKana(accountEditData.getAccountNameMeiKana());
		updateEntity.setAccountType(accountEditData.getAccountType());
		updateEntity.setAccountMailAddress(accountEditData.getAccountMailAddress());
		updateEntity.setAccountColor(accountEditData.getAccountColor());

		// 経営者権限ありか判定
		String accountOwnerFlg = accountEditData.getAccountOwnerFlg();
		if (!CommonConstant.SystemFlg.FLG_ON.equalsByCode(accountOwnerFlg)) {
			// 経営者権限なし
			accountOwnerFlg = CommonConstant.SystemFlg.FLG_OFF.getCd();
		}
		updateEntity.setAccountOwnerFlg(accountOwnerFlg);

		// システム管理者か判定
		String accountKengen = accountEditData.getAccountKengen();
		if (!CommonConstant.AccountKengen.SYSTEM_MNG.equalsByCode(accountKengen)) {
			// 一般権限
			accountKengen = CommonConstant.AccountKengen.GENERAL.getCd();
		}
		updateEntity.setAccountKengen(accountKengen);

		// パスワード未入力の場合は設定しない
		if (StringUtils.isNotEmpty(accountEditData.getPassword().getPassword())) {
			updateEntity.setPassword(new BCryptPasswordEncoder().encode(accountEditData.getPassword().getPassword()));
		}

		try {
			// 更新処理
			mAccountDao.update(updateEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

		// 更新後に権限を保持したユーザーの存在チェック
		long hasSystemMngAccountCount = mAccountDao.selectHasSystemMngAccountCount(null);
		if (hasSystemMngAccountCount == 0) {
			throw new AppException(MessageEnum.MSG_E00070, null);
		}

		// 更新ユーザー情報が自身の場合、Session情報を書き換える
		if (Objects.equals(updateEntity.getAccountSeq(), SessionUtils.getLoginAccountSeq())) {
			setSessionUserDetail(updateEntity, StringUtils.isNotEmpty(accountEditData.getPassword().getPassword()));
		}

	}

	/**
	 * セッション情報の更新
	 *
	 * @param mAccountEntity
	 * @param changePassWord
	 */
	private void setSessionUserDetail(MAccountEntity mAccountEntity, boolean changePassWord) {
		// Session情報を書き換える
		SessionUtils.setAccountId(mAccountEntity.getAccountId());
		SessionUtils.setAccountName(PersonName.fromEntity(mAccountEntity).getName());
		SessionUtils.setAccountType(AccountType.of(mAccountEntity.getAccountType()));
		SessionUtils.setAccountKengen(AccountKengen.of(mAccountEntity.getAccountKengen()));
		SessionUtils.setMailAddress(mAccountEntity.getAccountMailAddress());
		SessionUtils.setOwner(SystemFlg.codeToBoolean(mAccountEntity.getAccountOwnerFlg()));
		if (changePassWord) {
			// パスワードは変更した時だけ
			SessionUtils.setPassword(mAccountEntity.getPassword());
		}
	}

	/**
	 * 有効／停止の切り替え処理
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void changeStatus(OfficeAccountEditForm viewForm) throws AppException {

		// 更新データの取得
		MAccountEntity changeEntity = mAccountDao.selectBySeq(viewForm.getAccountEditData().getAccountSeq());
		String accountStatus = viewForm.getAccountEditData().getAccountStatus();

		if (AccountStatus.ENABLED.equalsByCode(accountStatus)) {
			// 有効 -> 無効
			accountStatus = AccountStatus.DISABLED.getCd();
		} else {
			// 無効 -> 有効
			accountStatus = AccountStatus.ENABLED.getCd();
		}
		changeEntity.setAccountStatus(accountStatus);

		try {
			// アカウントの有効／停止の更新処理
			mAccountDao.update(changeEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

		// 停止後に権限を保持したユーザーの存在チェック
		long hasSystemMngAccountCount = mAccountDao.selectHasSystemMngAccountCount(null);
		if (hasSystemMngAccountCount == 0) {
			throw new AppException(MessageEnum.MSG_E00071, null);
		}

		if (AccountStatus.DISABLED.equalsByCode(changeEntity.getAccountStatus())) {
			// 無効な場合は、その人のトークン情報を破棄する
			List<TAuthTokenEntity> myAuthToken = commonOAuthService.getConnectedExternalService(changeEntity.getAccountSeq());
			if (!CollectionUtils.isEmpty(myAuthToken)) {
				commonOAuthService.delete(myAuthToken);
			}
		}

	}

	/**
	 * ライセンス数制限チェック（保存処理用）
	 *
	 * @param viewForm
	 * @return
	 */
	public boolean checkLicenseLimitForAccountEditSave(OfficeAccountEditForm viewForm, boolean isNewRegist) {
		boolean error = false;

		String accountStatusCd = viewForm.getAccountEditData().getAccountStatus();
		AccountStatus accountStatus = DefaultEnum.getEnum(AccountStatus.class, accountStatusCd);

		if (accountStatus == AccountStatus.ENABLED) {
			// ライセンスを適用（有効）とする場合は上限チェックを行う

			// 利用しているライセンス数
			Long usingLicenseCount = commonPlanService.getNowUsingLicenseCount();
			// 利用可能なライセンス数
			Long useableLicenseCount = commonPlanService.getNowUseableLicenseCount(SessionUtils.getTenantSeq());

			if (isNewRegist) {
				// 新規登録時は、現利用ライセンス数+1を判定基準とする
				usingLicenseCount = usingLicenseCount + 1;
			} else {
				// 更新時は、無効から有効のときに現利用ライセンス数+1を判定基準とする
				// アカウント情報の取得処理
				MAccountEntity mAccountEntity = mAccountDao.selectBySeq(viewForm.getAccountEditData().getAccountSeq());
				if (AccountStatus.DISABLED.equalsByCode(mAccountEntity.getAccountStatus())) {
					usingLicenseCount = usingLicenseCount + 1;
				}
			}

			if (usingLicenseCount > useableLicenseCount) {
				// 利用可能なライセンスを全て利用している場合
				error = true;
			}
		}

		return error;
	}

	/**
	 * ライセンス数制限チェック（ステータス変更処理用）
	 * 
	 * @param viewForm
	 * @return
	 */
	public boolean checkLicenseLimitForChangeStatus(OfficeAccountEditForm viewForm) {
		boolean error = false;

		String accountStatus = viewForm.getAccountEditData().getAccountStatus();
		if (!AccountStatus.ENABLED.equalsByCode(accountStatus)) {
			// 無効 -> 有効 に変更する場合のみチェックを行う

			// 利用しているライセンス数
			Long usingLicenseCount = commonPlanService.getNowUsingLicenseCount();
			// 利用可能なライセンス数
			Long useableLicenseCount = commonPlanService.getNowUseableLicenseCount(SessionUtils.getTenantSeq());

			// ステータス変更後の利用ライセンス数の状態
			Long willUseLicenseCountAfterChangeStatus = usingLicenseCount + 1;

			if (willUseLicenseCountAfterChangeStatus > useableLicenseCount) {
				// 利用可能なライセンスを超える場合
				error = true;
			}
		}

		return error;
	}

}