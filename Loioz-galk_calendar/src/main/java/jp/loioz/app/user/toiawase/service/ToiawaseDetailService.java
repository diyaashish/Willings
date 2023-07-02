package jp.loioz.app.user.toiawase.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.toiawase.form.ToiawaseDetailInputForm;
import jp.loioz.app.user.toiawase.form.ToiawaseDetailViewForm;
import jp.loioz.app.user.toiawase.form.ToiawaseDetailViewForm.Attachment;
import jp.loioz.app.user.toiawase.form.ToiawaseDetailViewForm.Detail;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.ToiawaseStatus;
import jp.loioz.common.constant.CommonConstant.ToiawaseType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TToiawaseAttachmentDao;
import jp.loioz.dao.TToiawaseDao;
import jp.loioz.dao.TToiawaseDetailDao;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TToiawaseAttachmentEntity;
import jp.loioz.entity.TToiawaseDetailEntity;
import jp.loioz.entity.TToiawaseEntity;

/**
 * 問い合わせ詳細サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ToiawaseDetailService extends DefaultService {

	/** テナントDao */
	@Autowired
	private MTenantDao mTenantDao;

	/** 問い合わせDao */
	@Autowired
	private TToiawaseDao tToiawaseDao;

	/** 問い合わせ - 詳細Dao */
	@Autowired
	private TToiawaseDetailDao tToiawaseDetailDao;

	/** 問い合わせ - 添付Dao */
	@Autowired
	private TToiawaseAttachmentDao tToiawaseAttachmentDao;

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
	 * 問い合わせ詳細の画面用オブジェクトを作成します
	 *
	 * @param toiawaseSeq
	 * @return
	 */
	public ToiawaseDetailViewForm createVewForm(Long toiawaseSeq) {

		ToiawaseDetailViewForm viewForm = new ToiawaseDetailViewForm();

		// テナント情報を設定
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		viewForm.setTenantName(mTenantEntity.getTenantName());

		// 問い合わせ情報の設定
		TToiawaseEntity tToiawaseEntity = tToiawaseDao.selectBySeq(toiawaseSeq);
		if (tToiawaseEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new DataNotFoundException("toiawaseSeq：" + toiawaseSeq + "が存在しませんでした。");
		}

		viewForm.setToiawaseSeq(tToiawaseEntity.getToiawaseSeq());
		viewForm.setSubject(tToiawaseEntity.getSubject());
		viewForm.setToiawaseType(ToiawaseType.of(tToiawaseEntity.getToiawaseType()));
		viewForm.setToiawaseStatus(ToiawaseStatus.of(tToiawaseEntity.getToiawaseStatus()));
		viewForm.setLastUpdatedAt(DateUtils.parseToString(tToiawaseEntity.getLastUpdateAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));

		// 詳細情報の設定
		List<Detail> toiawaseDetails = this.getDetail(toiawaseSeq);
		viewForm.setToiawaseDetails(toiawaseDetails);

		return viewForm;
	}

	/**
	 * 問い合わせ詳細情報の取得
	 *
	 * @param toiawaseSeq
	 * @return
	 */
	private List<Detail> getDetail(Long toiawaseSeq) {

		List<TToiawaseDetailEntity> tToiawaseDetailEntities = tToiawaseDetailDao.selectByToiawaseSeq(toiawaseSeq);
		List<TToiawaseAttachmentEntity> tToiawaseAttachmentEntity = tToiawaseAttachmentDao.selectByToiawaseSeq(toiawaseSeq);

		Map<Long, List<Attachment>> detailSeqToMap = tToiawaseAttachmentEntity.stream()
				.map(entity -> {
					Attachment attachment = new Attachment();
					attachment.setToiawaseAttachmentSeq(entity.getToiawaseAttachmentSeq());
					attachment.setToiawaseDetailSeq(entity.getToiawaseDetailSeq());
					attachment.setFileName(entity.getFileName());
					attachment.setFileExtension(entity.getFileExtension());
					return attachment;
				})
				.collect(Collectors.groupingBy(Attachment::getToiawaseDetailSeq));

		List<Detail> details = tToiawaseDetailEntities.stream()
				.map(entity -> {
					Detail detail = new Detail();
					detail.setToiawaseDetailSeq(entity.getToiawaseDetailSeq());
					detail.setBody(entity.getBody());
					detail.setRegistType(entity.getRegistType());
					detail.setTenantRead(SystemFlg.codeToBoolean(entity.getTenantReadFlg()));
					detail.setCreatedAt(entity.getCreatedAt());
					detail.setOpenDateAt(entity.getSysUpdatedAt());
					detail.setAttachmentList(detailSeqToMap.get(entity.getToiawaseDetailSeq()));
					return detail;
				}).sorted(Comparator.comparing(Detail::getSortKey)).collect(Collectors.toList());

		return details;
	}

	/**
	 * 詳細登録処理
	 *
	 * @param inputForm
	 * @throws AppException
	 */
	public void addDetail(ToiawaseDetailInputForm inputForm) throws AppException {

		// 問い合わせデータの取得
		TToiawaseEntity tToiawaseEntity = tToiawaseDao.selectBySeq(inputForm.getToiawaseSeq());
		if (ToiawaseStatus.COMPLETED.equalsByCode(tToiawaseEntity.getToiawaseStatus())) {
			// 解決済み後には追加できない仕様なので、排他処理
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00066, null);
		}
		tToiawaseEntity.setToiawaseStatus(ToiawaseStatus.INQUIRING.getCd());

		// 問い合わせ-詳細データの取得
		TToiawaseDetailEntity tToiawaseDetailEntity = new TToiawaseDetailEntity();
		tToiawaseDetailEntity.setToiawaseSeq(inputForm.getToiawaseSeq());
		tToiawaseDetailEntity.setBody(inputForm.getBody());

		try {
			// 問い合わせ - 詳細情報の登録処理
			commonService.insert(tToiawaseDetailEntity);

			// ファイルの登録処理
			fileService.toiawaseFileUpload(tToiawaseEntity.getToiawaseSeq(), tToiawaseDetailEntity.getToiawaseDetailSeq(), inputForm.getUploadFile());

			// 問い合わせの更新処理
			commonService.update(tToiawaseEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00012, e);

		}

		// システム管理者へのメール通知
		commonService.sendToiawaseMail2Admin(tToiawaseEntity.getToiawaseSeq(), tToiawaseDetailEntity.getToiawaseDetailSeq());

	}

	/**
	 * 詳細登録処理
	 *
	 * @param toiawaseSeq
	 * @throws AppException
	 */
	public void completed(Long toiawaseSeq) throws AppException {

		// 問い合わせデータの取得
		TToiawaseEntity tToiawaseEntity = tToiawaseDao.selectBySeq(toiawaseSeq);
		if (ToiawaseStatus.COMPLETED.equalsByCode(tToiawaseEntity.getToiawaseStatus())) {
			// 解決済み後には追加できない仕様なので、排他処理
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00066, null);
		}
		tToiawaseEntity.setToiawaseStatus(ToiawaseStatus.COMPLETED.getCd());

		try {
			// 問い合わせの更新処理
			commonService.update(tToiawaseEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00012, e);
		}

	}

	/**
	 * 問い合わせ詳細の既読処理
	 *
	 * @param toiawaseSeq
	 * @throws AppException
	 */
	public void detailOpen(Long toiawaseSeq) throws AppException {

		List<TToiawaseDetailEntity> tToiawaseDetailEntities = tToiawaseDetailDao.selectByToiawaseSeq(toiawaseSeq);
		List<TToiawaseDetailEntity> updateEntities = tToiawaseDetailEntities.stream()
				.filter(entity -> !SystemFlg.codeToBoolean(entity.getTenantReadFlg()))
				.map(entity -> {
					entity.setTenantReadFlg(SystemFlg.FLG_ON.getCd());
					return entity;
				}).collect(Collectors.toList());

		// 更新するデータがない場合は何もせず終了
		if (ListUtils.isEmpty(updateEntities)) {
			return;
		}

		try {
			// 既読フラグの更新処理
			commonService.update(updateEntities);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00012, e);
		}

	}
	
	/**
	 * システム管理者に問い合わせ解決済み通知を送信する
	 * 
	 * @param toiawaseSeq
	 */
	public void sendToiawaseCompletedMail2Admin(Long toiawaseSeq) {
		commonService.sendToiawaseCompletedMail2Admin(toiawaseSeq);
	}

}
