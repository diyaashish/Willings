package jp.loioz.app.user.toiawase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.toiawase.form.ToiawaseCreateInputForm;
import jp.loioz.app.user.toiawase.form.ToiawaseCreateViewForm;
import jp.loioz.common.constant.CommonConstant.ToiawaseStatus;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.entity.TToiawaseDetailEntity;
import jp.loioz.entity.TToiawaseEntity;

/**
 * 問い合わせ新規作成サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ToiawaseCreateService extends DefaultService {

	/** 共通：問い合わせサービス */
	@Autowired
	private ToiawaseCommonService commonService;

	/** 問い合わせ */
	@Autowired
	private ToiawaseFileService fileService;

	/** ロガー */
	@Autowired
	private Logger logger;

	/**
	 * 問い合わせ作成の画面用オブジェクトを作成します
	 *
	 * @return
	 */
	public ToiawaseCreateViewForm createVewForm() {
		ToiawaseCreateViewForm viewFrom = new ToiawaseCreateViewForm();
		return viewFrom;
	}

	/**
	 * 問い合わせ作成
	 *
	 * @param inputForm
	 * @return
	 * @throws AppException
	 */
	public Long regist(ToiawaseCreateInputForm inputForm) throws AppException {

		// 問い合わせ
		TToiawaseEntity tToiawaseEntity = new TToiawaseEntity();
		tToiawaseEntity.setSubject(inputForm.getSubject());
		tToiawaseEntity.setToiawaseType(inputForm.getToiawaseType());
		tToiawaseEntity.setToiawaseStatus(ToiawaseStatus.INQUIRING.getCd());

		// 問い合わせ - 詳細
		TToiawaseDetailEntity tToiawaseDetailEntity = new TToiawaseDetailEntity();
		tToiawaseDetailEntity.setBody(inputForm.getBody());

		try {
			// 問い合わせの登録処理
			commonService.insert(tToiawaseEntity);

			// 問い合わせ - 詳細の登録処理
			tToiawaseDetailEntity.setToiawaseSeq(tToiawaseEntity.getToiawaseSeq());
			commonService.insert(tToiawaseDetailEntity);

			// ファイルの登録処理
			fileService.toiawaseFileUpload(tToiawaseEntity.getToiawaseSeq(), tToiawaseDetailEntity.getToiawaseDetailSeq(), inputForm.getUploadFile());

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, e);
		}

		// システム管理者（ADMIN）へのメール通知
		commonService.sendToiawaseMail2Admin(tToiawaseDetailEntity.getToiawaseSeq(), tToiawaseDetailEntity.getToiawaseDetailSeq());

		// アカウント権限：システム管理者へのメール通知
		commonService.sendToiawaseMail2SysMgt(tToiawaseDetailEntity.getToiawaseSeq());

		// 登録したSEQを返却
		return tToiawaseDetailEntity.getToiawaseSeq();

	}

}
