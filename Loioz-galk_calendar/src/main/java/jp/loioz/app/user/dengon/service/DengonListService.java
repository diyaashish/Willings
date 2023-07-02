package jp.loioz.app.user.dengon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.user.dengon.form.DengonAnkenCustomerForm;
import jp.loioz.app.user.dengon.form.DengonListForm;
import jp.loioz.app.user.dengon.form.DengonMessageListForm;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.bean.DengonDetailBean;
import jp.loioz.bean.DengonListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.DengonStatus;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.MailBoxType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TDengonAccountStatusDao;
import jp.loioz.dao.TDengonDao;
import jp.loioz.dao.TDengonFolderDao;
import jp.loioz.dao.TDengonFolderInDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SaibanId;
import jp.loioz.dto.AccountEditDto;
import jp.loioz.dto.DengonCustomerDto;
import jp.loioz.dto.DengonFolderDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TDengonAccountStatusEntity;
import jp.loioz.entity.TDengonEntity;
import jp.loioz.entity.TDengonFolderInEntity;
import jp.loioz.entity.TGyomuHistoryEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;

/**
 * メッセージリスト画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DengonListService extends DefaultService {

	/** 伝言共通サービス */
	@Autowired
	private DengonCommonService dengonCommonService;

	/** 共通アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/* アカウント用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/* メッセージ管理用のDaoクラス */
	@Autowired
	private TDengonDao tDengonDao;

	/* メッセージフォルダー管理用のDaoクラス */
	@Autowired
	private TDengonFolderDao tDengonFolderDao;

	/* カスタムフォルダー中身管理用のDaoクラス */
	@Autowired
	private TDengonFolderInDao tDengonFolderInDao;

	/* メッセージステータス管理用のDaoクラス */
	@Autowired
	private TDengonAccountStatusDao tDengonAccountStatusDao;

	/* 業務履歴管理用のDaoクラス */
	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	/* 裁判事件用のDaoクラス */
	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	/* 裁判情報のDaoクラス */
	@Autowired
	private TSaibanDao tSaibanDao;

	/* ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示データの取得
	 *
	 * @param form
	 * @return 画面表示情報
	 */
	public DengonListForm setInitData() {

		DengonListForm form = new DengonListForm();

		/* 初期表示時は受信BOXのメッセージを表示 */
		List<DengonMessageListForm> receiveDengonList = this.searchDengonList(MailBoxType.RECEIVE.getCd(), null, 0);
		form.setDengonList(receiveDengonList);

		/* カスタムフォルダ情報の取得 */
		List<DengonFolderDto> customeFolderList = this.selectCustomeFolderAsSelectOptions(null, null);
		/* 受信者SEQリストを文字列に変換し結合したものを表示名に設定する */
		form.setCustomeFolderList(customeFolderList);

		// 受信BOX内のメッセージ未読件数の設定
		form.setUnreadCount(this.selectUnreadCountWhereRecieveBox());

		// 下書きの件数の設定
		form.setDraftCount(this.selectDraftCount());

		// フォルダが作成可能か判定フラグを設定する
		form.setCreatableFolder(this.creatableFolder());

		return form;
	}

	/**
	 * 選択したフォルダのメッセージ一覧を取得する
	 *
	 * @param mailBoxType
	 * @param dengonFolderSeq
	 * @return
	 */
	public List<DengonMessageListForm> searchDengonList(String mailBoxType, Long dengonFolderSeq, Integer page) {
		if (page == null) {
			page = Integer.valueOf(0);
		}

		// 受信メッセージ取得
		List<DengonListBean> dengonList = new ArrayList<DengonListBean>();

		// 受信するメッセージBOXの種別
		if (MailBoxType.RECEIVE.equalsByCode(mailBoxType)) {

			// 受信BOX
			dengonList = tDengonDao.selectDengonReceiveList(SessionUtils.getLoginAccountSeq(), page);

		} else if (MailBoxType.RECEIVE_CUSTOM.equalsByCode(mailBoxType) ||
				MailBoxType.RECEIVE_SUB_CUSTOM.equalsByCode(mailBoxType)) {

			// カスタムフォルダ、カスタムサブフォルダ
			dengonList = tDengonDao.selectDengonFolderList(SessionUtils.getLoginAccountSeq(), page, dengonFolderSeq);

		} else if (MailBoxType.SEND.equalsByCode(mailBoxType)) {

			// 送信メッセージ取得
			dengonList = tDengonDao.selectDengonSendList(SessionUtils.getLoginAccountSeq(), page);

		} else if (MailBoxType.DRAFT.equalsByCode(mailBoxType)) {

			// 下書き
			dengonList = tDengonDao.selectDengonDraftList(SessionUtils.getLoginAccountSeq(), page);

		} else if (MailBoxType.TRASHED.equalsByCode(mailBoxType)) {

			// ごみ箱
			if (dengonFolderSeq == null) {
				// ごみ箱
				dengonList = tDengonDao.selectDengonTrashedList(SessionUtils.getLoginAccountSeq(), page);
			} else {
				// ごみ箱（カスタムフォルダ）
				dengonList = tDengonDao.selectDengonFolderTrashedList(SessionUtils.getLoginAccountSeq(), dengonFolderSeq, page);

			}

		}

		// 受信一覧表示用
		List<DengonMessageListForm> formList = this.convertDengonBean2Dto(dengonList);

		return formList;
	}

	/**
	 * 検索ワードを含むメッセージ一覧を取得する
	 *
	 * @param searchMailText
	 * @param mailBoxType
	 * @param dengonFolderSeq
	 * @param page
	 * @return
	 */
	public List<DengonMessageListForm> selectDengonSearchList(String searchMailText, String mailBoxType, Long dengonFolderSeq, Integer page) {
		if (page == null) {
			page = Integer.valueOf(0);
		}

		// 対象の文字が含まれるアカウントSEQを取得
		List<Long> searchAccountSeqList = mAccountDao.selectAccountSeqListBySearchWord(searchMailText);

		List<DengonListBean> dengonSearchList = new ArrayList<DengonListBean>();

		if (MailBoxType.TRASHED.equalsByCode(mailBoxType)) {
			// ごみ箱の場合
			dengonSearchList = tDengonDao.selectDengonSearchListWhereTrashed(SessionUtils.getLoginAccountSeq(), page, searchMailText,
					searchAccountSeqList);

		} else {
			// 受信メッセージ
			dengonSearchList = tDengonDao.selectDengonSearchList(SessionUtils.getLoginAccountSeq(), page, searchMailText, mailBoxType,
					dengonFolderSeq,
					searchAccountSeqList);
		}

		// 受信一覧表示用
		List<DengonMessageListForm> formList = this.convertDengonBean2Dto(dengonSearchList);

		return formList;
	}

	/**
	 * フィルター分類でのメッセージ一覧を取得する
	 *
	 * @param filterType
	 * @param searchMailText
	 * @param dengonFolderSeq
	 * @param page
	 * @return
	 */
	public List<DengonMessageListForm> selectDengonFilterList(String filterType, String searchMailText, Long dengonFolderSeq, Integer page) {
		if (page == null) {
			page = Integer.valueOf(0);
		}

		List<DengonListBean> dengonList = tDengonDao.selectDengonFilterList(SessionUtils.getLoginAccountSeq(), page, filterType, searchMailText,
				dengonFolderSeq);
		List<DengonMessageListForm> formList = new ArrayList<DengonMessageListForm>();
		// 受信一覧表示用
		formList = this.convertDengonBean2Dto(dengonList);
		return formList;
	}

	/**
	 * 受信者名表示用のアカウント一覧を取得する
	 *
	 * @return
	 */
	public List<AccountEditDto> getReceiverAccount() {
		List<MAccountEntity> mAccountEntities = mAccountDao.selectEnabledAccount();
		List<AccountEditDto> accountList = mAccountEntities.stream().map(entity -> {
			AccountEditDto dto = new AccountEditDto();
			dto.setAccountSeq(entity.getAccountSeq());
			dto.setAccountNameSei(entity.getAccountNameSei());
			dto.setAccountNameMei(entity.getAccountNameMei());
			return dto;
		}).collect(Collectors.toList());

		return accountList;
	}

	/**
	 * メッセージ詳細データを取得する
	 *
	 * @param dengonSeq
	 * @param mailBoxType
	 * @param sendFlg
	 * @return
	 * @throws AppException
	 */
	public DengonMessageListForm searchDetail(Long dengonSeq, String mailBoxType, String sendFlg) throws AppException {

		if (dengonSeq == null) {
			return new DengonMessageListForm();
		}

		DengonDetailBean dengonDetailBean = null;
		boolean sendMailFlg = false;
		if ((MailBoxType.SEND.equalsByCode(mailBoxType) && SystemFlg.FLG_ON.equalsByCode(sendFlg)) ||
				(MailBoxType.DRAFT.equalsByCode(mailBoxType) && SystemFlg.FLG_ON.equalsByCode(sendFlg)) ||
				(MailBoxType.TRASHED.equalsByCode(mailBoxType) && SystemFlg.FLG_ON.equalsByCode(sendFlg))) {
			// 「送信BOX」、「送信・下書き」、「送信・ごみ箱」の場合

			dengonDetailBean = tDengonDao.selectDengonDetailWhereSend(dengonSeq);
			dengonDetailBean.setTrashedFlg(Boolean.valueOf(false));
			sendMailFlg = true;

		} else {
			// 「受信BOX」、「カスタムフォルダ」、「カスタムサブフォルダ」の場合

			// メッセージの詳細情報を取得
			dengonDetailBean = tDengonDao.selectDengonDetailWhereReceive(SessionUtils.getLoginAccountSeq(), dengonSeq);
		}

		// DTOに変換
		DengonMessageListForm messageForm = this.convertDengonBean2Dto(dengonDetailBean);

		// 送信メッセージか判定フラグ
		messageForm.setSendMailFlg(Boolean.valueOf(sendMailFlg));

		// 受信者SEQを名前に変換
		messageForm.setReceiveAccountNameList(this.getAppendReceiveName(messageForm.getReceiveAccountSeqList()));

		// メッセージ本文を表示用に処理する
		messageForm.setBody(this.dispBody(messageForm.getBody()));

		// 本文のURLをaタグに変換する
		messageForm.setBody(convURLLink(messageForm.getBody()));

		// フラグの更新処理
		if (messageForm.getOpenFlg() != null && !messageForm.getOpenFlg()) {
			this.updateOpenFlg(dengonSeq, "1");
		}

		return messageForm;
	}

	/**
	 * メニューのカスタムフォルダー一覧を取得する
	 *
	 * @return 遷移先の画面
	 */
	public List<DengonFolderDto> selectCustomeFolderAsSelectOptions(Boolean isSelectOptions, Boolean isTrashed) {
		/* カスタムフォルダ情報の取得 */
		return tDengonFolderDao.selectCustomeFolderAsSelectOptions(SessionUtils.getLoginAccountSeq(), isSelectOptions, isTrashed);
	}

	/**
	 * 既読/未読フラグの一括変更 (0->1, 1->0)
	 *
	 * @param dengonSeqList
	 * @param openFlg
	 * @throws AppException
	 */
	public void changeOpenFlg(String strDengonSeqList, String openFlg) throws AppException {

		List<Long> dengonSeqList = StringUtils.toListLong(strDengonSeqList);

		/* メッセージステータス情報の取得 */
		List<TDengonAccountStatusEntity> entitys = tDengonAccountStatusDao.selectListByKey(SessionUtils.getLoginAccountSeq(), dengonSeqList);

		/* 既読フラグの変更 */
		for (TDengonAccountStatusEntity entity : entitys) {
			entity.setOpenFlg(openFlg);
		}

		/* 更新件数 */
		int[] updateEntitysCount = null;

		try {
			/* 更新処理 */
			updateEntitysCount = tDengonAccountStatusDao.update(entitys);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateEntitysCount == null || Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
			// 更新処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 既読フラグの変更 (0->1, 1->0)
	 *
	 * @param dengonSeq
	 * @param openFlg
	 * @throws AppException
	 */
	public void updateOpenFlg(Long dengonSeq, String openFlg) {

		/* メッセージステータス情報の取得 */
		TDengonAccountStatusEntity entity = tDengonAccountStatusDao.selectByKey(SessionUtils.getLoginAccountSeq(), dengonSeq);

		if (Objects.equals(openFlg, entity.getOpenFlg())) {
			return;
		}

		if (StringUtils.isNotEmpty(openFlg)) {
			/* 未読・既読指定の場合 */
			entity.setOpenFlg(openFlg);
		} else {
			/* フラグを反転させる */
			entity.setOpenFlg("0".equals(entity.getOpenFlg()) ? "1" : "0");
		}

		// 既読フラグの更新処理
		try {
			tDengonAccountStatusDao.update(entity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
		}

	}

	/**
	 * 重要フラグの変更 (0->1, 1->0)
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public String updateImportantFlg(Long dengonSeq) throws AppException {

		/* メッセージステータス情報の取得 */
		TDengonAccountStatusEntity entity = tDengonAccountStatusDao.selectByKey(SessionUtils.getLoginAccountSeq(), dengonSeq);

		/* 更新件数 */
		int updateEntityCount = 0;
		String importantFlg = null;

		if (entity == null) {
			/* 情報が取得できない場合 */
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);

		} else {
			/* フラグを反転させる */
			importantFlg = SystemFlg.FLG_OFF.equalsByCode(entity.getImportantFlg()) ? "1" : "0";
			entity.setImportantFlg(importantFlg);

			// メッセージを更新
			updateEntityCount = tDengonAccountStatusDao.update(entity);

		}

		if (updateEntityCount != 1) {
			// 更新処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
		return importantFlg;
	}

	/**
	 * メッセージステータス要返信の変更 (1:要返信, 0:未処理)
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void updateYohenshin(Long dengonSeq) throws AppException {

		/* メッセージステータス情報の取得 */
		TDengonAccountStatusEntity entity = tDengonAccountStatusDao.selectByKey(SessionUtils.getLoginAccountSeq(), dengonSeq);

		/* 更新件数 */
		int updateEntityCount = 0;

		if (entity == null) {
			/* 情報が取得できない場合 */
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);

		} else {

			/* フラグを反転させる */
			String dengonStatusId = DengonStatus.YOUHENSHIN.equalsByCode(entity.getDengonStatusId()) ? null : DengonStatus.YOUHENSHIN.getCd();
			entity.setDengonStatusId(dengonStatusId);

			// メッセージのステータスを更新
			updateEntityCount = tDengonAccountStatusDao.update(entity);

		}

		if (updateEntityCount != 1) {
			// 更新処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 受信メッセージをごみ箱に入れる/取り出す
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void trashedReceiveDengon(Long dengonSeq) throws AppException {

		/* DBに登録されているメッセージアカウントステータス情報の取得 */
		TDengonAccountStatusEntity entity = tDengonAccountStatusDao.selectByKey(SessionUtils.getLoginAccountSeq(), dengonSeq);

		/* 情報が存在しない場合、送信メッセージなので処理を分ける */
		if (entity == null) {
			this.trashedSendDengon(dengonSeq);
			return;
		}

		/* ごみ箱フラグの変更 */
		String trashedFlg = SystemFlg.FLG_ON.equalsByCode(entity.getTrashedFlg()) ? SystemFlg.FLG_OFF.getCd() : SystemFlg.FLG_ON.getCd();
		entity.setTrashedFlg(trashedFlg);

		/* 更新件数 */
		int updateEntityCount = 0;

		try {
			/* 更新処理 */
			updateEntityCount = tDengonAccountStatusDao.update(entity);

			// メッセージがカスタムフォルダー内にある場合に所属情報を削除する
			TDengonFolderInEntity tDengonFolderInEntity = tDengonFolderInDao.selectByKey(dengonSeq, SessionUtils.getLoginAccountSeq());
			if (tDengonFolderInEntity != null) {
				int deleteEntityCount = tDengonFolderInDao.delete(tDengonFolderInEntity);

				if (deleteEntityCount != 1) {
					// 削除処理に失敗した場合
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

					throw new AppException(MessageEnum.MSG_E00013, null);
				}
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateEntityCount != 1) {
			// 更新処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 複数の受信メッセージをごみ箱に入れる/取り出す
	 *
	 * @param dengonSeqList Long型のSEQリスト
	 * @throws AppException
	 */
	public void trashedReceiveDengonList(List<Long> dengonSeqList) throws AppException {

		/* DBに登録されているメッセージアカウントステータス情報の取得 */
		List<TDengonAccountStatusEntity> entitys = tDengonAccountStatusDao.selectListByKey(SessionUtils.getLoginAccountSeq(), dengonSeqList);

		/* ごみ箱フラグの変更 */
		entitys.stream().forEach(e -> e.setTrashedFlg("1".equals(e.getTrashedFlg()) ? "0" : "1"));

		try {
			/* 更新処理 */
			tDengonAccountStatusDao.update(entitys);

			// メッセージがカスタムフォルダー内にある場合に所属情報を削除する
			List<TDengonFolderInEntity> tDengonFolderInEntityList = tDengonFolderInDao.selectListByDengonSeqList(dengonSeqList,
					SessionUtils.getLoginAccountSeq());
			if (tDengonFolderInEntityList != null && tDengonFolderInEntityList.size() > 0) {
				tDengonFolderInDao.delete(tDengonFolderInEntityList);
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * 複数の受信メッセージをごみ箱に入れる/取り出す
	 *
	 * @param strDengonSeqList 文字列型のSEQリスト
	 * @throws AppException
	 */
	public void trashedReceiveDengonList(String strDengonSeqList) throws AppException {

		/* 文字列型のSEQリストをList<Long>に変換する */
		List<Long> dengonSeqList = StringUtils.toListLong(strDengonSeqList);

		this.trashedReceiveDengonList(dengonSeqList);
	}

	/**
	 * 複数の受信メッセージをごみ箱に入れる/取り出す
	 *
	 * @param strDengonSeqList 文字列型のSEQリスト
	 * @throws AppException
	 */
	public void trashedSendDengonList(String strDengonSeqList) throws AppException {

		/* 文字列型のSEQリストをList<Long>に変換する */
		List<Long> dengonSeqList = StringUtils.toListLong(strDengonSeqList);

		this.trashedSendDengonList(dengonSeqList);
	}

	/**
	 * 複数の送信メッセージをごみ箱に入れる/取り出す
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void trashedSendDengonList(List<Long> dengonSeqList) throws AppException {

		/* DBに登録されているメッセージ情報の取得 */
		List<TDengonEntity> tDengonEntityList = tDengonDao.selectListByDengonSeqList(dengonSeqList);

		/* ごみ箱フラグの変更 */
		tDengonEntityList.stream().forEach(e -> e.setSendTrashedFlg("1".equals(e.getSendTrashedFlg()) ? "0" : "1"));

		try {
			/* 更新処理 */
			tDengonDao.update(tDengonEntityList);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * 送信メッセージをごみ箱に入れる/取り出す
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void trashedSendDengon(Long dengonSeq) throws AppException {

		/* DBに登録されているメッセージ情報の取得 */
		TDengonEntity entity = tDengonDao.selectByDengonSeq(dengonSeq);

		/* ごみ箱フラグの変更 */
		entity.setSendTrashedFlg("1".equals(entity.getSendTrashedFlg()) ? "0" : "1");

		try {
			/* 更新処理 */
			tDengonDao.update(entity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * 下書きを論理削除する
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void deletedDraft(Long dengonSeq) throws AppException {

		/* DBに登録されているカスタムフォルダー情報の取得 */
		TDengonEntity entity = tDengonDao.selectByDengonSeq(dengonSeq);

		try {
			/* 論理削除処理 */
			entity.setFlgDelete();
			tDengonDao.update(entity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * 複数の下書きを論理削除する
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void deletedDraftList(List<Long> dengonSeqList) throws AppException {

		/* DBに登録されているカスタムフォルダー情報の取得 */
		List<TDengonEntity> entityList = tDengonDao.selectListByDengonSeqList(dengonSeqList);

		try {
			/* ごみ箱フラグの変更 */
			entityList.stream().forEach(e -> e.setFlgDelete());
			tDengonDao.update(entityList);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * ごみ箱の受信メッセージを論理削除する
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void deletedReceiveDengon(Long dengonSeq) throws AppException {

		/* DBに登録されているカスタムフォルダー情報の取得 */
		TDengonAccountStatusEntity entity = tDengonAccountStatusDao.selectByKey(SessionUtils.getLoginAccountSeq(), dengonSeq);

		try {
			/* 論理削除処理 */
			entity.setFlgDelete();
			tDengonAccountStatusDao.update(entity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * ごみ箱の送信メッセージを論理削除する
	 *
	 * @param dengonSeq
	 * @throws AppException
	 */
	public void deletedSendDengon(Long dengonSeq) throws AppException {

		/* DBに登録されているカスタムフォルダー情報の取得 */
		TDengonEntity entity = tDengonDao.selectByDengonSeq(dengonSeq);

		try {
			/* 論理削除処理 */
			entity.setFlgDelete();
			tDengonDao.update(entity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * 複数件のごみ箱の受信メッセージを論理削除する
	 *
	 * @param dengonSeqList
	 * @throws AppException
	 */
	public void deletedReceiveDengonList(String receiveDengonSeqList) throws AppException {

		/* 文字列型のSEQリストをList<Long>に変換する */
		List<Long> dengonSeqList = StringUtils.toListLong(receiveDengonSeqList);

		/* DBに登録されているメッセージアカウントステータス情報の取得 */
		List<TDengonAccountStatusEntity> entitys = tDengonAccountStatusDao.selectListByKey(SessionUtils.getLoginAccountSeq(), dengonSeqList);

		try {
			/* 論理削除処理 */
			for (TDengonAccountStatusEntity entity : entitys) {
				entity.setFlgDelete();
			}
			tDengonAccountStatusDao.update(entitys);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}

	}

	/**
	 * 複数件のごみ箱の送信メッセージを論理削除する
	 *
	 * @param dengonSeqList
	 * @throws AppException
	 */
	public void deletedSendDengonList(String sendDengonSeqList) throws AppException {

		/* 文字列型のSEQリストをList<Long>に変換する */
		List<Long> dengonSeqList = StringUtils.toListLong(sendDengonSeqList);

		/* DBに登録されているメッセージアカウントステータス情報の取得 */
		List<TDengonEntity> entitys = tDengonDao.selectListByDengonSeqList(dengonSeqList);

		try {
			/* 論理削除処理 */
			for (TDengonEntity entity : entitys) {
				entity.setFlgDelete();
			}
			tDengonDao.update(entitys);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * 指定された文字列内のURLを、正規表現を使用し、 リンク（a href=...）に変換する。
	 *
	 * @param str 指定の文字列。
	 * @return リンクに変換された文字列。
	 */
	public static String convURLLink(String str) {
		Matcher matcher = CommonConstant.CONV_URL_LINK_PTN.matcher(str);
		return matcher.replaceAll("<a target=\"_blank\" href=\"$0\" rel=\"noopener\">$0</a>");
	}

	/**
	 * 受信BOX内のメッセージの未読件数を取得する
	 *
	 * @return 未読件数
	 */
	public int selectUnreadCountWhereRecieveBox() {
		return tDengonDao.selectUnreadCountWhereRecieveBox(SessionUtils.getLoginAccountSeq());
	}

	/**
	 * 保存されている下書きの件数を取得する
	 *
	 * @return 下書きの件数
	 */
	public int selectDraftCount() {
		return tDengonDao.selectDraftCount(SessionUtils.getLoginAccountSeq());
	}

	/**
	 * フォルダが作成可能か判定する
	 *
	 * @return
	 */
	public boolean creatableFolder() {
		boolean creatableParentFolder = tDengonFolderDao.countFolder(SessionUtils.getLoginAccountSeq(),
				true) < CommonConstant.DENGON_FOLDER_ADD_LIMIT;
		boolean creatableSubFolder = tDengonFolderDao.countFolder(SessionUtils.getLoginAccountSeq(), false) < CommonConstant.DENGON_FOLDER_ADD_LIMIT;

		return creatableParentFolder || creatableSubFolder;
	}

	/**
	 * 未読メッセージを開いた場合に未読件数を取得する
	 *
	 * @return unreadCount
	 */
	public Integer selectUnreadCount() {
		return tDengonDao.selectUnreadCount(SessionUtils.getLoginAccountSeq());
	}

	/**
	 * ごみ箱の伝言をカスタムフォルダに移動させる
	 *
	 * @param dengonSeqList
	 * @throws AppException
	 */
	public void removeTrashedDengon(List<Long> dengonSeqList, Long dengonFolderSeq) throws AppException {

		/* DBに登録されている伝言アカウントステータス情報の取得 */
		List<TDengonAccountStatusEntity> entitys = tDengonAccountStatusDao.selectListByKey(SessionUtils.getLoginAccountSeq(), dengonSeqList);

		/* ごみ箱の伝言でなければ処理を終える */
		if ("0".equals(entitys.get(0).getTrashedFlg())) {
			return;
		}

		/* ごみ箱フラグを変更 */
		entitys.stream().forEach(e -> e.setTrashedFlg("0"));

		try {
			/* 更新処理 */
			tDengonAccountStatusDao.update(entitys);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * メッセージ一覧表示用に加工
	 *
	 * @param beanList
	 * @param receiveFlg
	 * @return
	 */
	private List<DengonMessageListForm> convertDengonBean2Dto(List<DengonListBean> beanList) {

		List<Long> dengonSeqList = beanList.stream().map(DengonListBean::getDengonSeq).collect(Collectors.toList());
		Map<Long, List<DengonCustomerDto>> dengonSeqToCustomerMap = dengonCommonService.getSelectedCustomer(dengonSeqList);

		List<DengonMessageListForm> dengonList = new ArrayList<DengonMessageListForm>();

		// 受信者名表示用のアカウント一覧を取得する
		for (DengonListBean bean : beanList) {
			DengonMessageListForm form = new DengonMessageListForm();
			if (bean.getGyomuHistorySeq() != null) {
				this.getGyoumuHistoryInfo(form, bean.getGyomuHistorySeq());
			}
			form.setAnkenName(bean.getCreatedAt());
			form.setDengonSeq(bean.getDengonSeq());
			form.setGyomuHistorySeq(bean.getGyomuHistorySeq());
			form.setSaibanSeq(bean.getSaibanSeq());
			form.setSaibanBranchNo(bean.getSaibanBranchNo());
			form.setTitle(bean.getTitle());
			form.setSendAccountName(bean.getSendAccountName());
			form.setReceiveAccountNameList(bean.getReceiveAccountSeqList());
			form.setDengonStatusId(bean.getDengonStatusId());
			form.setDraftFlg(bean.getDraftFlg());
			form.setOpenFlg(bean.getOpenFlg());
			form.setImportantFlg(bean.getImportantFlg());
			form.setCreatedAt(bean.getCreatedAt());
			// 受信者
			form.setReceiveAccountNameList(this.getAppendReceiveName(bean.getReceiveAccountSeqList()));
			// 紐付け顧客
			form.setSelectedCustomerList(dengonSeqToCustomerMap.get(bean.getDengonSeq()));

			dengonList.add(form);
		}
		return dengonList;
	}

	/**
	 * メッセージ詳細表示用に加工
	 *
	 * @param beanList
	 * @param receiveFlg
	 * @return
	 */
	private DengonMessageListForm convertDengonBean2Dto(DengonDetailBean dengonDetailBean) {

		DengonMessageListForm form = new DengonMessageListForm();
		BeanUtils.copyProperties(dengonDetailBean, form);

		// 受信者
		String receiveAccountSeq = dengonDetailBean.getReceiveAccountSeqList();
		List<Long> receiveAccountSeqList = StringUtils.toListLong(receiveAccountSeq);
		if (receiveAccountSeqList == null) {
			// 存在しない（下書きの場合）
			form.setManyReceiveFlg(Boolean.valueOf(false));

		} else if (receiveAccountSeqList.size() > 1) {
			// 複数人
			form.setManyReceiveFlg(Boolean.valueOf(true));

		} else {
			// 一人
			form.setManyReceiveFlg(Boolean.valueOf(false));

		}
		// 受信者の名前変換
		form.setReceiveAccountNameList(this.getAppendReceiveName(receiveAccountSeq));

		if (dengonDetailBean.getGyomuHistorySeq() != null) {
			this.getGyoumuHistoryInfo(form, dengonDetailBean.getGyomuHistorySeq());
		}

		// 伝言-顧客情報の取得
		List<DengonCustomerDto> selectedCustomerList = dengonCommonService.getSelectedCustomer(form.getDengonSeq());
		form.setSelectedCustomerList(selectedCustomerList);

		return form;
	}

	/**
	 * 受信者SEQリストを名前に変換し結合して返す
	 *
	 * @param receiveAccountSeq
	 * @return appendReceiveName
	 */
	private String getAppendReceiveName(String receiveAccountSeq) {

		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		List<Long> receiveAccountSeqList = StringUtils.toListLong(receiveAccountSeq);

		/* リストの要素がなければnullを返却 */
		if (receiveAccountSeqList == null || receiveAccountSeqList.isEmpty()) {
			return null;
		}

		String appendReceiveName = receiveAccountSeqList.stream().map(seq -> accountNameMap.get(seq)).collect(Collectors.joining(", "));
		return appendReceiveName;
	}

	/**
	 * メッセージ本文を表示用に編集する
	 *
	 * @param body
	 * @return dispComment
	 */
	private String dispBody(String body) {
		List<String> bodyList = Arrays.asList(body.split("\r\n|\r|\n"));
		if (bodyList != null && bodyList.size() > 0) {
			bodyList.replaceAll(e -> e.replace("<", "&lt;"));
			bodyList.replaceAll(e -> e.replace(">", "&gt;"));
			bodyList.replaceAll(e -> e.replace(" ", "&nbsp;"));
			bodyList.replaceAll(e -> e.isEmpty() ? "<br>" : e + "<br>");
			body = bodyList.stream().collect(Collectors.joining(""));
		}

		return body;
	}

	/**
	 * 業務履歴に紐づく案件、顧客、裁判情報を取得、設定する。
	 *
	 * @param dengonEditForm
	 * @param gyomuhistorySeq
	 *
	 */

	private void getGyoumuHistoryInfo(DengonMessageListForm form, Long gyomuhistorySeq) {

		// 業務履歴情報を取得
		TGyomuHistoryEntity gyomuhistoryEntity = tGyomuHistoryDao.selectBySeq(gyomuhistorySeq);
		if (gyomuhistoryEntity == null) {
			form.setGyomuHistoryDeleted(true);
			return;
		}

		// 業務履歴の登録元の情報を取得する
		form.setTransitionType(gyomuhistoryEntity.getTransitionType());

		if (gyomuhistoryEntity != null && gyomuhistoryEntity.getSaibanSeq() != null) {
			// 案件に紐づく裁判情報を取得します。
			TSaibanEntity tSaibanEntity = tSaibanDao.selectBySeq(gyomuhistoryEntity.getSaibanSeq());
			TSaibanJikenEntity tSaibanJikenEntity = tSaibanJikenDao.selectSingleBySaibanSeq(gyomuhistoryEntity.getSaibanSeq());

			if (tSaibanEntity != null && tSaibanJikenEntity != null) {
				// 事件名を生成
				StringBuilder label = new StringBuilder();
				label.append(CaseNumber.of(
						EraType.of(tSaibanJikenEntity.getJikenGengo()),
						tSaibanJikenEntity.getJikenYear(),
						tSaibanJikenEntity.getJikenMark(),
						tSaibanJikenEntity.getJikenNo()));
				label.append(CommonConstant.SPACE);
				label.append(Objects.nonNull(tSaibanJikenEntity.getJikenName()) ? tSaibanJikenEntity.getJikenName() : "");
				form.setSaibanSeq(gyomuhistoryEntity.getSaibanSeq());
				form.setAnkenId(AnkenId.of(tSaibanEntity.getAnkenId()));
				SaibanId saibanId = new SaibanId(AnkenId.of(tSaibanEntity.getAnkenId()), tSaibanEntity.getSaibanBranchNo());
				form.setFwSaibanId(saibanId);
				form.setSaibanBranchNo(tSaibanEntity.getSaibanBranchNo());
				// 事件名
				form.setSaibanName(label.toString());
			}
		}

		// 1：顧客情報
		List<AnkenCustomerRelationBean> customerRelationBeanList = tGyomuHistoryDao.selectCustomerBeanBySeq(gyomuhistorySeq);
		if (!customerRelationBeanList.isEmpty()) {
			List<DengonAnkenCustomerForm> relatedCustomerList = new ArrayList<DengonAnkenCustomerForm>();
			for (AnkenCustomerRelationBean customerRelationBean : customerRelationBeanList) {
				DengonAnkenCustomerForm relatedCustomer = new DengonAnkenCustomerForm();
				relatedCustomer.setCustomerId(CustomerId.of(customerRelationBean.getCustomerId()));
				relatedCustomer.setCustomerName(customerRelationBean.getCustomerName());
				relatedCustomerList.add(relatedCustomer);
			}
			form.setRelatedCustomerList(relatedCustomerList);
		}

		// 2：案件情報
		List<AnkenCustomerRelationBean> ankenRelationBeanList = tGyomuHistoryDao.selectAnkenBeanBySeq(gyomuhistorySeq);
		if (!ankenRelationBeanList.isEmpty()) {
			List<DengonAnkenCustomerForm> relatedAnkenList = new ArrayList<DengonAnkenCustomerForm>();
			for (AnkenCustomerRelationBean ankenRelationBean : ankenRelationBeanList) {
				DengonAnkenCustomerForm relatedAnken = new DengonAnkenCustomerForm();
				relatedAnken.setAnkenId(AnkenId.of(ankenRelationBean.getAnkenId()));
				relatedAnken.setAnkenName(ankenRelationBean.getAnkenName());
				relatedAnkenList.add(relatedAnken);
			}
			form.setRelatedAnkenList(relatedAnkenList);
		}

	}
}