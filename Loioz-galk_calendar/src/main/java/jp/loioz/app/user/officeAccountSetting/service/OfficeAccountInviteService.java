package jp.loioz.app.user.officeAccountSetting.service;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonPlanService;
import jp.loioz.app.user.invitedAccountRegist.controller.InvitedAccountRegistController;
import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountInviteForm;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TInvitedAccountVerificationDao;
import jp.loioz.domain.mail.builder.M0010MailBuilder;
import jp.loioz.domain.verification.InvitedAccountVerificationService;
import jp.loioz.dto.AccountMailDto;
import jp.loioz.entity.TInvitedAccountVerificationEntity;

/**
 * 招待メール画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OfficeAccountInviteService extends DefaultService {

	/** メール送信用のDaoクラス */
	@Autowired
	private TInvitedAccountVerificationDao tinvitedAccountVerificationDao;

	/** 契約プランの共通サービス */
	@Autowired
	private CommonPlanService commonPlanService;

	/** アカウント認証(テナントDB)サービスクラス */
	@Autowired
	private InvitedAccountVerificationService invitedAccountVerificationService;

	/** メール送信サービス */
	@Autowired
	private MailService mailService;
	
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
	public OfficeAccountInviteForm createViewForm() {
		OfficeAccountInviteForm viewForm = new OfficeAccountInviteForm();
		boolean mailSendButtonFlg = false;
		
		// ライセンス数の設定
		Long useableLicenseCount = commonPlanService.getNowUseableLicenseCount(SessionUtils.getTenantSeq());
		Long usingLicenseCount = commonPlanService.getNowUsingLicenseCount();
		viewForm.setUseableLicenseCount(useableLicenseCount);
		viewForm.setUsingLicenseCount(usingLicenseCount);

		// 表示するメールアドレスリスト
		ArrayList<AccountMailDto> mailAddressList = new ArrayList<AccountMailDto>();
		
		// 現在「招待中」状態のメールアドレスを追加
		List<TInvitedAccountVerificationEntity> tinvitedAccountVerificationEntity = tinvitedAccountVerificationDao.selectInviting(LocalDateTime.now());
		for (TInvitedAccountVerificationEntity entity : tinvitedAccountVerificationEntity) {
			AccountMailDto accountMailDto =  new AccountMailDto();
			accountMailDto.setMailAddress(entity.getMailAddress());
			accountMailDto.setVerificationkey(entity.getVerificationKey());
			accountMailDto.setInsertFlg(false);
			mailAddressList.add(accountMailDto);
		}
		
		// 追加で招待可能な数
		int inviteableCount = useableLicenseCount.intValue() - usingLicenseCount.intValue() - tinvitedAccountVerificationEntity.size();
		if (inviteableCount > 0) {
			
			// 追加招待が存在するので、送信ボタンを表示
			mailSendButtonFlg = true;
			
			// 追加招待分はメールアドレスなどは空で設定
			for (int i=0; i<inviteableCount; i++) {
				AccountMailDto accountMailDto =  new AccountMailDto();
				accountMailDto.setMailAddress("");
				accountMailDto.setVerificationkey("");
				accountMailDto.setInsertFlg(true);
				mailAddressList.add(accountMailDto);
			}
		}
		
		viewForm.setMailAddressList(mailAddressList);
		viewForm.setMailSendButtonFlg(mailSendButtonFlg);
		
		return viewForm;
	}
	
	/**
	 * 送信処理
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public ArrayList<TInvitedAccountVerificationEntity> send(OfficeAccountInviteForm viewForm) throws AppException {
		
		ArrayList<TInvitedAccountVerificationEntity> registEntityList = new ArrayList<TInvitedAccountVerificationEntity>();
		
		// 招待メールの有効期限（現在日時から24時間後とする）
		LocalDateTime tempLimitDate = LocalDateTime.now().plusDays(1);
		
		// 登録データ作成
		List<AccountMailDto> insertMailAddressListExcludeEmpty = viewForm.getInsertMailAddressListExcludeEmpty();
		for (AccountMailDto dto : insertMailAddressListExcludeEmpty) {
			
			TInvitedAccountVerificationEntity registEntity = new TInvitedAccountVerificationEntity();
			
			// 認証キー
			String key = invitedAccountVerificationService.createVerificationKey();
			registEntity.setVerificationKey(key);
			
			// メールアドレス
			registEntity.setMailAddress(dto.getMailAddress());
			// 有効期限
			registEntity.setTempLimitDate(tempLimitDate);
			// 完了フラグ（初期登録はOFFで固定）
			registEntity.setCompleteFlg(SystemFlg.FLG_OFF.getCd());
			
			registEntityList.add(registEntity);
		}
		
		try {
			// 招待アカウントの認証情報を登録
			tinvitedAccountVerificationDao.insertList(registEntityList);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
		
		return registEntityList;
	}
	
	/**
	 * ライセンス数制限チェック（保存処理用）
	 *
	 * @param viewForm
	 * @return
	 */
	public boolean checkLicenseLimitForAccountMailSave(OfficeAccountInviteForm viewForm) {
		boolean error = false;

		// 利用可能なライセンス数
		Long useableLicenseCount = commonPlanService.getNowUseableLicenseCount(SessionUtils.getTenantSeq());
		// 利用しているライセンス数
		Long usingLicenseCount = commonPlanService.getNowUsingLicenseCount();
		
		// 現在「招待中」の招待アカウント認証情報
		List<TInvitedAccountVerificationEntity> tinvitedAccountVerificationEntity = tinvitedAccountVerificationDao.selectInviting(LocalDateTime.now());
		// 追加で招待可能な数
		int inviteableCount = useableLicenseCount.intValue() - usingLicenseCount.intValue() - tinvitedAccountVerificationEntity.size();
		
		// いまから招待しようとしている数
		List<AccountMailDto> insertMailAddressListExcludeEmpty = viewForm.getInsertMailAddressListExcludeEmpty();
		int sendMailCount = insertMailAddressListExcludeEmpty.size();
		
		if (sendMailCount > inviteableCount) {
			// 招待しようとしている数が、招待可能な数を超えている場合 -> エラー
			return error = true;
		}
		
		return error;
	}
	
	
	/**
	 * 入力されたメールアドレスの全てが空かどうかをチェック<br>
	 * ※全て空の場合はtrueを返却
	 *
	 * @param viewForm
	 * @return
	 */
	public boolean checkMailAddressIsEmpty(OfficeAccountInviteForm viewForm) {
		
		// 登録対象のメールアドレスで、空のデータを除外したリスト
		List<AccountMailDto> mailAddressListExcludeEmpty = viewForm.getInsertMailAddressListExcludeEmpty();
		// データが1件もなければtrue
		return CollectionUtils.isEmpty(mailAddressListExcludeEmpty);
	}

	/**
	 * 対象の認証アカウント情報の存在チェック
	 * 
	 * @param verificationKey
	 * @return
	 */
	public MessageEnum checkExistInvitedAccountVerification(String verificationKey) {
		TInvitedAccountVerificationEntity tinvitedAccountVerificationEntity = tinvitedAccountVerificationDao.selectByKey(verificationKey);
		
		if (tinvitedAccountVerificationEntity == null) {
			return MessageEnum.MSG_E00153;
		} else if (tinvitedAccountVerificationEntity.getCompleteFlg().equals(SystemFlg.FLG_ON.getCd())) {
			return MessageEnum.MSG_E00154;
		}
		return null;
	}

	/**
	 * 対象の認証アカウントを削除する
	 *
	 * @param viewForm
	 * @return
	 */
	public void deleteMailAddressisData(OfficeAccountInviteForm viewForm)throws AppException {
		try {
			TInvitedAccountVerificationEntity tinvitedAccountVerificationEntity = tinvitedAccountVerificationDao.selectByKey(viewForm.getVerificationkey());
			tinvitedAccountVerificationDao.delete(tinvitedAccountVerificationEntity);
		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, e);
		}
	}

	/**
	 * 入力されたメールアドレスに重複があるかチェック<br>
	 * ※重複があればtrueを返却
	 *
	 * @param viewForm
	 * @return
	 */
	public boolean checkFormMailAddressIsDuplicat(OfficeAccountInviteForm viewForm) {
		
		// 登録対象のメールアドレスで、空のデータを除外したリスト
		List<AccountMailDto> mailAddressDtoListExcludeEmpty = viewForm.getMailAddressListExcludeEmpty();
		// 入力されたメールアドレスのリスト
		List<String> mailAddressList = mailAddressDtoListExcludeEmpty.stream()
				.map(e -> e.getMailAddress())
				.collect(Collectors.toList());
		
		// 重複の存在有無
		boolean isDuplicat = (mailAddressList.size() != new HashSet<>(mailAddressList).size());
		return isDuplicat;
	}

	/**
	 * DBに登録されている、招待中のメールアドレスとの重複チェック
	 *
	 * @param viewForm
	 * @return
	 */
	public boolean checkDbMailAddressIsDuplicat(OfficeAccountInviteForm viewForm) {
		
		// 登録対象のメールアドレスで、空のデータを除外したリスト
		List<AccountMailDto> mailAddressDtoListExcludeEmpty = viewForm.getInsertMailAddressListExcludeEmpty();
		// 入力されたメールアドレスのリスト
		List<String> mailAddressList = mailAddressDtoListExcludeEmpty.stream()
				.map(e -> e.getMailAddress())
				.collect(Collectors.toList());
		
		// 入力されたメールアドレスの招待中データが存在するか
		List<TInvitedAccountVerificationEntity> invitingAccountVerification = tinvitedAccountVerificationDao.selectInvitingByMailAddress(mailAddressList, LocalDateTime.now());
		return !CollectionUtils.isEmpty(invitingAccountVerification);
	}

	/**
	 * アカウントメールを送信する
	 *
	 * @param mailAddress メールアドレス
	 * @param key 認証キー
	 */
	public void sendAccountForgetRequestMail(ArrayList<TInvitedAccountVerificationEntity> tInvitedAccountVerificationEntity) {
		
		for (TInvitedAccountVerificationEntity oTInvitedAccountVerificationEntity : tInvitedAccountVerificationEntity) {
			// メール認証URL
			ServletUriComponentsBuilder currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath();
			String verificationUri = MvcUriComponentsBuilder
					.relativeTo(currentContextPath.path("/"))
					.withMethodCall(on(InvitedAccountRegistController.class).index(oTInvitedAccountVerificationEntity.getVerificationKey()))
					.toUriString();

			// メール作成
			M0010MailBuilder mailBuilder = new M0010MailBuilder();
			mailService.loadMailTemplate(MailIdList.MAIL_10.getCd(), mailBuilder);
			mailBuilder.makeTitle(SessionUtils.getTenantName());
			mailBuilder.makeAddressTo(Arrays.asList(oTInvitedAccountVerificationEntity.getMailAddress()));
			mailBuilder.makeBody(oTInvitedAccountVerificationEntity.getMailAddress(),SessionUtils.getTenantName(),verificationUri);
			
			// メール送信
			mailService.sendAsync(mailBuilder);
		}
	}
}