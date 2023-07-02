package jp.loioz.app.user.mailTemplateEdit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Objects;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.mailTemplateEdit.form.MailTemplateEditInputForm;
import jp.loioz.app.user.mailTemplateEdit.form.MailTemplateEditViewForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.MailTemplateType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MMailTemplateDao;
import jp.loioz.entity.MMailTemplateEntity;

/**
 * メールテンプレートの作成画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MailTemplateEditService extends DefaultService {

	/** メールテンプレートDaoクラス */
	@Autowired
	private MMailTemplateDao mMailTemplateDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * メールテンプレート編集画面オブジェクトを取得する
	 * 
	 * @return 新規作成画面
	 */
	public MailTemplateEditViewForm createMailTemplateEditViewForm() {
		return new MailTemplateEditViewForm();
	}

	/**
	 * メールテンプレート編集画面オブジェクトを取得する
	 * 
	 * @param mailTemplateSeq
	 * @return 編集画面
	 */
	public MailTemplateEditViewForm createMailTemplateEditViewForm(Long mailTemplateSeq) {

		// データ存在チェックのため、データ取得を行う
		this.getMMailTemplateEntity(mailTemplateSeq);

		var viewForm = new MailTemplateEditViewForm();
		viewForm.setMailTemplateSeq(mailTemplateSeq);

		return viewForm;
	}

	/**
	 * テンプレート入力フォームオブジェクトを作成(新規作成)
	 * 
	 * @return
	 */
	public MailTemplateEditInputForm.TemplateInputForm createTemplateInputForm() {

		// オブジェクトの生成
		var inputForm = new MailTemplateEditInputForm.TemplateInputForm();

		// 表示用プロパティの設定
		setDispProperties(inputForm);

		// 初期値の設定
		inputForm.setTemplateType(MailTemplateType.INVOICE.getCd());

		return inputForm;
	}

	/**
	 * テンプレート入力フォームオブジェクトを作成(編集)
	 * 
	 * @param mailTemplateSeq
	 * @return
	 */
	public MailTemplateEditInputForm.TemplateInputForm createTemplateInputForm(Long mailTemplateSeq) {

		// フォームオブジェクトを作成
		var inputForm = new MailTemplateEditInputForm.TemplateInputForm();

		// 表示用プロパティの設定
		setDispProperties(inputForm);

		// データ取得
		MMailTemplateEntity mMailTemplateEntity = this.getMMailTemplateEntity(mailTemplateSeq);

		// データの設定
		inputForm.setMailTemplateSeq(mailTemplateSeq);
		inputForm.setTemplateType(mMailTemplateEntity.getTemplateType());
		inputForm.setTemplateTitle(mMailTemplateEntity.getTemplateTitle());
		inputForm.setMailCc(mMailTemplateEntity.getMailCc());
		inputForm.setMailBcc(mMailTemplateEntity.getMailBcc());
		inputForm.setMailReplyTo(mMailTemplateEntity.getMailReplyTo());
		inputForm.setSubject(mMailTemplateEntity.getSubject());
		inputForm.setContents(mMailTemplateEntity.getContents());
		inputForm.setDefaultUse(SystemFlg.codeToBoolean(mMailTemplateEntity.getDefaultUseFlg()));

		return inputForm;
	}

	/**
	 * テンプレート入力オブジェクトの表示用プロパティを設定する
	 * 
	 * @param inputForm
	 */
	public void setDispProperties(MailTemplateEditInputForm.TemplateInputForm inputForm) {
		// 現状無し
	}

	/**
	 * メールテンプレートの登録処理
	 * 
	 * @param inputForm
	 * @return
	 * @throws AppException
	 */
	public Long registMailTemplate(MailTemplateEditInputForm.TemplateInputForm inputForm) throws AppException {

		// 登録上限値チェック
		if (this.isRegistLimit()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00174, null, "メールテンプレート", String.valueOf(CommonConstant.MAIL_TEMPLATE_REGIST_LIMIT));
		}

		var entity = new MMailTemplateEntity();

		entity.setTemplateType(inputForm.getTemplateType());
		entity.setTemplateTitle(inputForm.getTemplateTitle());
		entity.setMailCc(inputForm.getMailCc());
		entity.setMailBcc(inputForm.getMailBcc());
		entity.setMailReplyTo(inputForm.getMailReplyTo());
		entity.setSubject(inputForm.getSubject());
		entity.setContents(inputForm.getContents());
		entity.setDefaultUseFlg(SystemFlg.booleanToCode(inputForm.isDefaultUse()));

		try {
			// メールテンプレートの登録処理
			mMailTemplateDao.insert(entity);

			Long mailTemplateSeq = entity.getMailTemplateSeq();
			if (inputForm.isDefaultUse()) {
				// 既定フラグをONにした場合のみ、同一種別の該当データ以外のすべてのデータを既定フラグOFFにする
				updateDefaultUseFlgOff(mailTemplateSeq, MailTemplateType.of(entity.getTemplateType()));
			}

			// メールテンプレートSEQを返却
			return mailTemplateSeq;

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * メールテンプレートの更新処理
	 *
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateMailTemplate(MailTemplateEditInputForm.TemplateInputForm inputForm) throws AppException {

		// データの取得
		MMailTemplateEntity entity = mMailTemplateDao.selectBySeq(inputForm.getMailTemplateSeq());
		if (entity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		entity.setTemplateType(inputForm.getTemplateType());
		entity.setTemplateTitle(inputForm.getTemplateTitle());
		entity.setMailCc(inputForm.getMailCc());
		entity.setMailBcc(inputForm.getMailBcc());
		entity.setMailReplyTo(inputForm.getMailReplyTo());
		entity.setSubject(inputForm.getSubject());
		entity.setContents(inputForm.getContents());
		entity.setDefaultUseFlg(SystemFlg.booleanToCode(inputForm.isDefaultUse()));

		try {
			// メールテンプレートの更新処理
			mMailTemplateDao.update(entity);

			if (inputForm.isDefaultUse()) {
				// 既定フラグをONにした場合のみ、同一種別の該当データ以外のすべてのデータを既定フラグOFFにする
				updateDefaultUseFlgOff(entity.getMailTemplateSeq(), MailTemplateType.of(entity.getTemplateType()));
			}

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * メールテンプレートの削除処理
	 *
	 * @param mailTemplateSeq
	 * @throws AppException
	 */
	public void deleteMailTemplate(Long mailTemplateSeq) throws AppException {

		// データの取得
		MMailTemplateEntity entity = mMailTemplateDao.selectBySeq(mailTemplateSeq);
		if (entity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// メールテンプレートの削除処理
			mMailTemplateDao.delete(entity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * メールテンプレート情報の取得
	 * 
	 * @param mailTemplateSeq
	 * @return
	 * @throws DataNotFoundException
	 */
	private MMailTemplateEntity getMMailTemplateEntity(Long mailTemplateSeq) throws DataNotFoundException {
		MMailTemplateEntity mMailTemplateEntity = mMailTemplateDao.selectBySeq(mailTemplateSeq);
		if (mMailTemplateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new DataNotFoundException();
		}
		return mMailTemplateEntity;
	}

	/**
	 * 登録上限かどうか
	 * 
	 * @return
	 */
	private boolean isRegistLimit() {
		long totalSize = mMailTemplateDao.getTotalSize();
		return totalSize >= CommonConstant.MAIL_TEMPLATE_REGIST_LIMIT;
	}

	/**
	 * 既定フラグの更新
	 * 
	 * @param excludeMailTemplateSeq 除外対象(登録元データ)
	 * @param mailTemplateType メール種別
	 */
	private void updateDefaultUseFlgOff(Long excludeMailTemplateSeq, MailTemplateType mailTemplateType) throws AppException {

		// 種別ごとに既定フラグを設定できるため、登録データ側の種別データのみ取得
		List<MMailTemplateEntity> mMailTemplateEntities = mMailTemplateDao.selectByTemplateType(mailTemplateType.getCd());

		// 対象以外のデータを既定フラグOFFに設定
		List<MMailTemplateEntity> updateEntities = mMailTemplateEntities.stream()
				.filter(e -> !Objects.equal(e.getMailTemplateSeq(), excludeMailTemplateSeq))
				.peek(e -> e.setDefaultUseFlg(SystemFlg.FLG_OFF.getCd()))
				.collect(Collectors.toList());

		// 更新対象データがない場合は何もせずに終了
		if (CollectionUtils.isEmpty(updateEntities)) {
			return;
		}

		try {
			// 更新処理
			mMailTemplateDao.update(updateEntities);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

}