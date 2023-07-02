package jp.loioz.app.user.dengon.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonSelectBoxService;
import jp.loioz.app.user.dengon.controller.DengonListController;
import jp.loioz.app.user.dengon.form.DengonAnkenCustomerForm;
import jp.loioz.app.user.dengon.form.DengonEditForm;
import jp.loioz.app.user.dengon.form.DengonEditForm.Account;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.bean.DengonDetailBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.MailConstants.MailIdList;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.mail.MailService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TBushoShozokuAcctDao;
import jp.loioz.dao.TDengonAccountStatusDao;
import jp.loioz.dao.TDengonDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.domain.UriService;
import jp.loioz.domain.mail.builder.M0004MailBuilder;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.DengonCustomerDto;
import jp.loioz.dto.DengonEditDto;
import jp.loioz.dto.DengonSendDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TBushoShozokuAcctEntity;
import jp.loioz.entity.TDengonAccountStatusEntity;
import jp.loioz.entity.TDengonEntity;
import jp.loioz.entity.TGyomuHistoryEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;

/**
 * 伝言編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DengonEditService extends DefaultService {

	/** アカウント管理用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 伝言管理用のDaoクラス */
	@Autowired
	private TDengonDao tDengonDao;

	/** 伝言ステータス管理用のDaoクラス */
	@Autowired
	private TDengonAccountStatusDao tDengonAccountStatusDao;

	/** 業務履歴管理用のDaoクラス */
	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	/** 名簿情報Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 裁判管理用のDaoクラス */
	@Autowired
	private TSaibanDao tSaibanDao;

	/** 裁判事件用のDaoクラス */
	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	/** 部署所属アカウント */
	@Autowired
	private TBushoShozokuAcctDao tBushoShozokuAcctDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	/** 伝言共通サービス */
	@Autowired
	private DengonCommonService dengonCommonService;

	/** 共通：プルダウンサービス */
	@Autowired
	private CommonSelectBoxService commonSelectBoxService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** メール送信サービス */
	@Autowired
	private MailService mailService;

	/** URIサービス */
	@Autowired
	private UriService uriService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期登録画面表示
	 *
	 * @return new DengonEditForm()
	 */
	public DengonEditForm createViewForm() {
		return new DengonEditForm();
	}

	/**
	 * 編集画面表示
	 *
	 * @param dengonSeq
	 * @return form
	 * @throws AppException
	 */
	public DengonEditForm selectDengonEdit(Long dengonSeq) {

		// 部署情報を取得する
		DengonEditForm form = new DengonEditForm();

		/* 宛先用のアカウントリストを取得する */
		DengonEditDto dto = tDengonDao.selectDengonEdit(SessionUtils.getLoginAccountSeq(), dengonSeq);

		if (dto.getBunyaId() != null) {
			dto.setAnkenName(commonBunyaService.getBunya(dto.getBunyaId()).getVal() + "：" + dto.getAnkenName());
		}

		List<Long> receiveAccountSeqList = this.getAccountSeqList(dto.getReceiveAccountSeqList());

		/* 下書きの場合は宛先が存在しない場合があるので処理を分ける */
		List<MAccountEntity> receiveAccountEntityList = new ArrayList<MAccountEntity>();
		if (LoiozCollectionUtils.isNotEmpty(receiveAccountSeqList)) {
			receiveAccountEntityList = mAccountDao.selectAccountByAccountSeqList(receiveAccountSeqList);
		}
		// メッセージの詳細情報を取得
		DengonDetailBean dengonDetailBean = tDengonDao.selectDengonDetailWhereSend(dengonSeq);
		if (dengonDetailBean != null) {
			dto.setAnkenId(dengonDetailBean.getAnkenId());
			dto.setAnkenName(dengonDetailBean.getAnkenName());
			dto.setCustomerId(dengonDetailBean.getCustomerId());
			dto.setCustomerName(dengonDetailBean.getCustomerName());
			dto.setAnkenId(dengonDetailBean.getAnkenId());
			dto.setSaibanSeq(dengonDetailBean.getSaibanSeq());
			dto.setSaibanBranchNo(dengonDetailBean.getSaibanBranchNo());
			dto.setGyomuhistorySeq(dengonDetailBean.getGyomuHistorySeq());

			if (dengonDetailBean.getGyomuHistorySeq() != null) {
				this.getGyoumuHistoryInfo(form, dto, dengonDetailBean.getGyomuHistorySeq());
			}
		}
		form.setDto(dto);
		form.setAccountSeqList(receiveAccountSeqList);
		form.setReceiveAccountList(this.convert2Account(receiveAccountEntityList));

		// 紐付け顧客情報
		List<DengonCustomerDto> selectedCustomer = dengonCommonService.getSelectedCustomer(dengonSeq);
		form.setSelectedCustomerList(selectedCustomer);

		return form;
	}

	/**
	 * 業務履歴画面から遷移する場合の初期表示
	 *
	 * @param gyomuhistorySeq
	 * @return form
	 * @throws AppException
	 */
	public DengonEditForm selectDengonEditFromGyoumuHistory(Long gyomuhistorySeq) {

		DengonEditForm form = new DengonEditForm();
		DengonEditDto dto = new DengonEditDto();
		TDengonEntity dengonEntity = tDengonDao.selectByGyomuHistorySeq(gyomuhistorySeq);
		// 選択した業務履歴のタイトルと本文を取得します
		TGyomuHistoryEntity gyomuhistoryEntity = tGyomuHistoryDao.selectBySeq(gyomuhistorySeq);
		if (dengonEntity != null) {
			if (!dengonEntity.getReceiveAccountSeq().isEmpty()) {
				List<String> recieveAccountSeqStringList = Arrays.asList(dengonEntity.getReceiveAccountSeq().split(","));
				List<Long> recieveAccountSeqList = new ArrayList<Long>();
				for (String accountSeq : recieveAccountSeqStringList) {
					recieveAccountSeqList.add(Long.parseLong(accountSeq));
				}
				MAccountEntity sentAccountEntity = mAccountDao.selectBySeq(dengonEntity.getSendAccountSeq());
				List<MAccountEntity> recieveAccountEntityList = mAccountDao.selectBySeq(recieveAccountSeqList);
				if (sentAccountEntity != null) {
					Account sentAccount = convert2Account(sentAccountEntity);
					form.setSentAccountInfo(sentAccount);
				}
				if (!recieveAccountEntityList.isEmpty()) {
					List<Account> receivetAccountList = convert2Account(recieveAccountEntityList);
					form.setReceiveAccountList(receivetAccountList);
				}
			}
			dto.setTitle(dengonEntity.getTitle());
			dto.setBody(dengonEntity.getBody());
			dto.setReceiveAccountSeqList(dengonEntity.getReceiveAccountSeq());
			dto.setDraftFlg(dengonEntity.getDraftFlg());
			dto.setGyomuhistorySeq(dengonEntity.getGyomuHistorySeq());
			dto.setDengonSeq(dengonEntity.getDengonSeq());
			dto.setDraftFlg(dengonEntity.getDraftFlg());
			form.setSentFromGyoumuHistoryFlg(true);

			// 紐付け顧客情報
			List<DengonCustomerDto> selectedCustomer = dengonCommonService.getSelectedCustomer(dengonEntity.getDengonSeq());
			form.setSelectedCustomerList(selectedCustomer);

		} else {
			dto.setTitle(gyomuhistoryEntity.getSubject());
			dto.setBody(gyomuhistoryEntity.getMainText());
		}
		this.getGyoumuHistoryInfo(form, dto, gyomuhistorySeq);
		dto.setGyomuhistorySeq(gyomuhistorySeq);
		form.setUnDisplayDraftFlg(true);
		form.setDto(dto);

		return form;
	}

	/**
	 * 返信用の情報を取得する
	 *
	 * @param dengonSeq
	 * @return form
	 */
	public DengonEditForm selectDengonReply(Long dengonSeq, boolean isReplyAll) {

		DengonEditForm form = new DengonEditForm();

		/* 宛先用のアカウントリストを取得する */
		DengonEditDto dto = tDengonDao.selectDengonReply(dengonSeq);
		// 返信か全返信で処理を分ける
		List<Long> receiveAccountSeqList = new ArrayList<Long>();
		if (isReplyAll) {
			receiveAccountSeqList.add(dto.getSendAccountSeq());
			receiveAccountSeqList.addAll(this.getAccountSeqList(dto.getReceiveAccountSeqList()));
			receiveAccountSeqList.stream().distinct().collect(Collectors.toList());
		} else {
			receiveAccountSeqList.add(dto.getSendAccountSeq());
		}

		// メッセージの詳細情報を取得
		DengonDetailBean dengonDetailBean = tDengonDao.selectDengonDetailWhereReceive(SessionUtils.getLoginAccountSeq(), dengonSeq);
		if (dengonDetailBean != null) {
			dto.setAnkenId(dengonDetailBean.getAnkenId());
			dto.setAnkenName(dengonDetailBean.getAnkenName());
			dto.setCustomerId(dengonDetailBean.getCustomerId());
			dto.setCustomerName(dengonDetailBean.getCustomerName());
			dto.setAnkenId(dengonDetailBean.getAnkenId());
			dto.setSaibanSeq(dengonDetailBean.getSaibanSeq());
			dto.setSaibanBranchNo(dengonDetailBean.getSaibanBranchNo());
			dto.setGyomuhistorySeq(dengonDetailBean.getGyomuHistorySeq());

			this.getGyoumuHistoryInfo(form, dto, dengonDetailBean.getGyomuHistorySeq());

		}
		// 返信用に本文を加工する
		List<String> replyBodyList = Arrays.asList(dto.getBody().split("\r\n|\r|\n"));
		if (replyBodyList != null && replyBodyList.size() > 0) {
			replyBodyList.set(0, "\r\n\r\n>" + replyBodyList.get(0));
			String replyBody = replyBodyList.stream().collect(Collectors.joining("\r\n>"));

			// 返信用の記号を付与しても1万文字を超えないように末尾を削る
			if (replyBody.length() >= 10000) {
				replyBody = replyBody.substring(0, 9999);
			}
			dto.setBody(replyBody);
		}

		// 件名が初期状態で50文字に収まるように末尾を削る
		String title = dto.getTitle();
		if (title != null && title.length() > 0) {
			char chars[] = title.toCharArray();
			if (chars != null && chars.length > 50) {
				dto.setTitle(new String(Arrays.copyOfRange(chars, 0, 50)));
			}
		}
		StringBuilder orderByCondition = new StringBuilder("ORDER BY FIELD (account_seq,");
		int count = 0;
		for (Long accountSeq : receiveAccountSeqList) {
			orderByCondition.append(accountSeq.toString());
			if (count < receiveAccountSeqList.size() - 1) {
				orderByCondition.append(CommonConstant.COMMA);
			}
			count++;
		}
		orderByCondition.append(")");
		List<MAccountEntity> receiveAccountEntityList = mAccountDao.selectAccountByAccountSeqListOrderBySeqList(receiveAccountSeqList, orderByCondition.toString());

		form.setDto(dto);
		form.setAccountSeqList(receiveAccountSeqList);
		form.setReceiveAccountList(this.convert2Account(receiveAccountEntityList));

		// 紐付け顧客情報
		List<DengonCustomerDto> selectedCustomer = dengonCommonService.getSelectedCustomer(dengonSeq);
		form.setSelectedCustomerList(selectedCustomer);

		return form;
	}

	/**
	 * 作成した伝言を送信する
	 *
	 * @param form
	 * @throws AppException
	 */
	public Boolean send(DengonEditForm form) throws AppException {

		/* 画面情報の取得 */
		DengonEditDto dto = form.getDto();

		/* 宛先リストの取得 */
		List<Long> accountSeqList = form.getAccountSeqList();

		/* 伝言情報の設定 */
		TDengonEntity tDengonEntity = this.convertTDengonEntity(dto, accountSeqList);

		/* リロードフラグ */
		Boolean reloadFlg = false;
		try {
			/* 伝言情報の登録処理 */
			tDengonDao.insert(tDengonEntity);

			if (tDengonEntity.getGyomuHistorySeq() != null) {
				// 業務履歴SEQが設定されている場合、業務履歴テーブルの伝言送信フラグを1にする。
				TGyomuHistoryEntity gyomuHistoryEntity = tGyomuHistoryDao.selectBySeq(tDengonEntity.getGyomuHistorySeq());
				if (gyomuHistoryEntity != null && SystemFlg.FLG_OFF.equalsByCode(gyomuHistoryEntity.getDengonSentFlg())) {
					gyomuHistoryEntity.setDengonSentFlg(SystemFlg.FLG_ON.getCd());
					// 更新処理
					tGyomuHistoryDao.update(gyomuHistoryEntity);
					reloadFlg = true;
				}
			}

			/* 伝言SEQの取得 */
			Long dengonSeq = tDengonEntity.getDengonSeq();

			/* 伝言アカウントステータス情報の設定 */
			List<TDengonAccountStatusEntity> tDengonAccountStatusEntityList = this.convertTDengonAccountStatusEntityList(dengonSeq, accountSeqList);

			/* 伝言アカウントステータス情報の登録処理 */
			tDengonAccountStatusDao.insert(tDengonAccountStatusEntityList);

			// 伝言-顧客情報の登録処理
			dengonCommonService.registDengonCustomer(tDengonEntity.getDengonSeq(), form.getCustomerIdList());

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00012, ex);
		}

		// 伝言受信のメッセージを送信する
		if (form.isMailFlg()) {
			this.sendDengonMail(tDengonEntity.getDengonSeq());
		}
		return reloadFlg;
	}

	/**
	 * 一度下書き保存した伝言を送信する
	 *
	 * @param form
	 * @throws AppException
	 */
	public void sendWhereDraft(DengonEditForm form) throws AppException {

		/* 画面情報の取得 */
		DengonEditDto dto = form.getDto();

		/* 宛先リストの取得 */
		List<Long> accountSeqList = form.getAccountSeqList();

		/* DBに保存された下書き伝言を取得 */
		TDengonEntity tDengonEntity = tDengonDao.selectByDengonSeq(dto.getDengonSeq());

		/* 伝言情報の設定 */
		this.setTDengonEntity(tDengonEntity, dto, accountSeqList);

		try {
			/* 伝言情報の更新処理 */
			tDengonDao.update(tDengonEntity);

			/* 伝言アカウントステータス情報の設定 */
			List<TDengonAccountStatusEntity> tDengonAccountStatusEntityList = this.convertTDengonAccountStatusEntityList(tDengonEntity.getDengonSeq(),
					accountSeqList);

			/* 伝言アカウントステータス情報の更新処理 */
			tDengonAccountStatusDao.insert(tDengonAccountStatusEntityList);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		// 伝言受信のメッセージを送信する
		if (form.isMailFlg()) {
			this.sendDengonMail(tDengonEntity.getDengonSeq());
		}
	}

	/**
	 * 作成した伝言を下書き保存する
	 *
	 * @param form
	 * @throws AppException
	 */
	public void draftInsert(DengonEditForm form) throws AppException {

		/* 画面情報の取得 */
		DengonEditDto dto = form.getDto();

		/* 宛先リストの取得 */
		List<Long> accountSeqList = form.getAccountSeqList();

		/* 伝言情報の設定 */
		TDengonEntity tDengonEntity = this.convertTDengonEntity(dto, accountSeqList);

		try {
			/* 伝言情報の登録処理 */
			tDengonDao.insert(tDengonEntity);

			// 伝言-顧客情報の登録処理
			dengonCommonService.registDengonCustomer(tDengonEntity.getDengonSeq(), form.getCustomerIdList());

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00012, ex);
		}
	}

	/**
	 * 作成した伝言を下書き更新する
	 *
	 * @param form
	 * @throws AppException
	 */
	public void draftUpdate(DengonEditForm form) throws AppException {

		/* 画面情報の取得 */
		DengonEditDto dto = form.getDto();

		/* 宛先リストの取得 */
		List<Long> accountSeqList = form.getAccountSeqList();

		/* 伝言情報の設定 */
		TDengonEntity tDengonEntity = tDengonDao.selectByDengonSeq(dto.getDengonSeq());
		this.setTDengonEntity(tDengonEntity, dto, accountSeqList);

		try {
			// 伝言情報の更新処理
			tDengonDao.update(tDengonEntity);

			// 伝言-顧客情報の更新処理
			dengonCommonService.updateDengonCustomer(tDengonEntity.getDengonSeq(), form.getCustomerIdList());

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * 有効なアカウント情報の取得
	 * 
	 * @return
	 */
	public List<Account> getEnabledAccount() {
		List<MAccountEntity> mAccountEntities = mAccountDao.selectEnabledAccount();

		// 所属部署情報の取得
		List<TBushoShozokuAcctEntity> bushoShozokuAcctEntityList = tBushoShozokuAcctDao.selectByEnabledAccount();
		Map<Long, List<TBushoShozokuAcctEntity>> seqToShozokuuAcctMap = bushoShozokuAcctEntityList.stream()
				.collect(Collectors.groupingBy(TBushoShozokuAcctEntity::getAccountSeq));

		List<Account> accountList = mAccountEntities.stream().map(this::convert2Account).map(account -> {
			List<String> bushoCdList = new ArrayList<String>();
			bushoCdList.add(CommonConstant.SANKASHA_SELECT_OPTION_ALL); // 全員を追加
			List<TBushoShozokuAcctEntity> bushoshozoku = seqToShozokuuAcctMap.get(account.getAccountSeq());
			if (ListUtils.isEmpty(bushoshozoku)) {
				bushoCdList.add("0"); // 部署未所属
			} else {
				bushoshozoku.stream().map(entity -> entity.getBushoId().toString()).forEach(bushoCdList::add); // 所属部署IDをすべて追加
			}

			// 所属部署を設定
			account.setBushoShozoku(bushoCdList);

			return account;
		}).collect(Collectors.toList());

		return accountList;
	}

	/**
	 * 文字列のSEQリストをList<Long>に変換して返す
	 *
	 * @param accountSeq
	 * @return accountSeqList
	 */
	public List<Long> getAccountSeqList(String accountSeq) {
		/* 文字列がnullなら空のリストを返却 */
		if (StringUtils.isEmpty(accountSeq)) {
			return new ArrayList<Long>();
		} else {
			return Arrays.asList(accountSeq.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
		}
	}

	/**
	 * 部署リストの追加
	 * 
	 * @return
	 */
	public List<SelectOptionForm> getBushoList() {

		List<SelectOptionForm> bushoOptions = new ArrayList<>(commonSelectBoxService.getBushoSelectBox());

		// 全員を追加
		bushoOptions.add(0, new SelectOptionForm(CommonConstant.SANKASHA_SELECT_OPTION_ALL, "全員"));

		// 所属部署情報の取得
		List<Long> bushoShozokuAcctEntityList = tBushoShozokuAcctDao.selectMishozokuAccountSeq();

		// 部署に未所属のユーザーに部署未所属を追加
		if (!ListUtils.isEmpty(bushoShozokuAcctEntityList)) {
			bushoOptions.add(new SelectOptionForm("0", CommonConstant.BUMON_MI_SHOZOKU));
		}

		return bushoOptions;
	}

	/**
	 * 案件担当者のアカウントSEQを取得
	 * 
	 * @param ankenId
	 * @return
	 */
	public Map<Long, List<Long>> getAnkenTantoAccountSeq(List<Long> ankenId) {

		List<AnkenTantoDto> ankenTantoList = tAnkenTantoDao.selectByAnkenIdListAndTantoType(ankenId);

		Map<Long, List<AnkenTantoDto>> groupedAnkenTantoMap = ankenTantoList.stream()
				.filter(e -> !TantoType.SALES_OWNER.equalsByCode(e.getTantoType()))
				.collect(Collectors.groupingBy(AnkenTantoDto::getAnkenId));
		Map<Long, List<Long>> ankenIdToAnkenTantoMap = ankenId.stream().collect(Collectors.toMap(
				e -> e, e -> groupedAnkenTantoMap.getOrDefault(e, Collections.emptyList()).stream().map(AnkenTantoDto::getAccountSeq).collect(Collectors.toList())));
		return ankenIdToAnkenTantoMap;
	}

	/**
	 * 紐付け顧客の取得処理
	 *
	 * @param searchText
	 * @param exclusionCustomerId
	 * @return
	 */
	public List<DengonCustomerDto> getCustomerList(String searchText, List<Long> exclusionCustomerIdList) {

		List<TPersonEntity> personEntity = tPersonDao.selectExcludedCustomerByParams(exclusionCustomerIdList, searchText, CommonConstant.DENGON_CUSTOMER_SEARCH_LIMIT);
		if (ListUtils.isEmpty(personEntity)) {
			return Collections.emptyList();
		}

		return personEntity.stream().map(entity -> {
			DengonCustomerDto dto = new DengonCustomerDto();
			CustomerId customerId = CustomerId.of(entity.getCustomerId());
			dto.setCustomerId(customerId.asLong());
			dto.setCustomerIdDisp(customerId.toString());
			dto.setCustomerName(PersonName.fromEntity(entity).getName());
			return dto;
		}).collect(Collectors.toList());
	}

	/**
	 * 受信を通知するメールを送る
	 *
	 * @param dengonSeq
	 */
	public void sendDengonMail(Long dengonSeq) {
		// メッセージ受信者のメールアドレスを取得する
		TDengonEntity tDengonEntity = tDengonDao.selectByDengonSeq(dengonSeq);
		List<Long> accountSeqList = this.getAccountSeqList(tDengonEntity.getReceiveAccountSeq());
		// 送信者の情報を生成する
		List<DengonSendDto> dengonSendDtoList = mAccountDao.selectAccountByAccountSeqList(accountSeqList).stream().map(entity -> {
			DengonSendDto dto = new DengonSendDto();
			dto.setReceiveAccountName(PersonName.fromEntity(entity).getName());
			dto.setReceiveAccountMailAdress(entity.getAccountMailAddress());
			return dto;
		}).filter(dto -> StringUtils.isNotEmpty(dto.getReceiveAccountMailAdress())).collect(Collectors.toList());

		// 登録されたメールアドレスがなければ処理を終了
		if (dengonSendDtoList == null) {
			return;
		}

		// 伝言送信者名を取得する
		MAccountEntity mAccountEntity = mAccountDao.selectBySeq(tDengonEntity.getSendAccountSeq());
		String sendAccountName = PersonName.fromEntity(mAccountEntity).getName();

		// 伝言のURLを取得する
		String dengonUrl = uriService.getUserUrl(DengonListController.class, controller -> controller.index());

		// 本文を生成する
		StringBuilder customerNm = new StringBuilder("・顧客：");
		StringBuilder ankenNm = new StringBuilder("・案件：");
		StringBuilder insertBody = new StringBuilder(CommonConstant.BLANK);

		// 業歴Seqと紐づいている場合、紐づく顧客、案件情報を取得、設定する
		if (tDengonEntity.getGyomuHistorySeq() != null) {
			List<AnkenCustomerRelationBean> ankenRelationBeanList = tGyomuHistoryDao.selectAnkenBeanBySeq(tDengonEntity.getGyomuHistorySeq());
			List<AnkenCustomerRelationBean> customerRelationBeanList = tGyomuHistoryDao.selectCustomerBeanBySeq(tDengonEntity.getGyomuHistorySeq());
			// 業歴に紐づく案件情報を取得、設定する
			if (!ankenRelationBeanList.isEmpty()) {
				int ankenCount = 0;
				for (AnkenCustomerRelationBean ankenRelationBean : ankenRelationBeanList) {
					ankenNm.append(AnkenId.of(ankenRelationBean.getAnkenId()));
					ankenNm.append(StringUtils.defaultString(ankenRelationBean.getAnkenName()));
					if (ankenCount < ankenRelationBeanList.size() - 1) {
						ankenNm.append(CommonConstant.COMMA);
					}
				}
				insertBody.append(ankenNm);
				insertBody.append(CommonConstant.CRLF_CODE);
			}
			// 業歴に紐づく顧客情報を取得、設定する
			if (!customerRelationBeanList.isEmpty()) {
				int customerCount = 0;
				for (AnkenCustomerRelationBean customerRelationBean : customerRelationBeanList) {
					customerNm.append(CustomerId.of(customerRelationBean.getCustomerId()));
					customerNm.append(StringUtils.defaultString(customerRelationBean.getCustomerName()));
					if (customerCount < customerRelationBeanList.size() - 1) {
						customerNm.append(CommonConstant.COMMA);
					}
					customerCount++;
				}
				insertBody.append(customerNm);
				insertBody.append(CommonConstant.CRLF_CODE);
			}
		}

		// メール送信処理
		dengonSendDtoList.forEach(dto -> {
			// メール作成
			M0004MailBuilder mailBuilder = new M0004MailBuilder();
			mailService.loadMailTemplate(MailIdList.MAIL_4.getCd(), mailBuilder);

			// 1人ずつ送信する
			List<String> to = new ArrayList<String>();
			to.add(dto.getReceiveAccountMailAdress());
			// 送信先
			mailBuilder.makeAddressTo(to);
			// 件名
			mailBuilder.makeTitle(tDengonEntity.getTitle());
			// 本文
			mailBuilder.makeBody(dto.getReceiveAccountName(), insertBody.toString(), dengonUrl, sendAccountName);

			// メール送信
			mailService.sendAsync(mailBuilder);
		});
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * List<Entity> -> List<Account>
	 * 
	 * @param entities
	 * @return
	 */
	private List<Account> convert2Account(List<MAccountEntity> entities) {
		return entities.stream()
				.map(entity -> convert2Account(entity))
				.collect(Collectors.toList());
	}

	/**
	 * Entity -> Account
	 * 
	 * @param entity
	 * @return
	 */
	private Account convert2Account(MAccountEntity entity) {
		Account account = new Account();
		account.setAccountSeq(entity.getAccountSeq());
		account.setAccountType(entity.getAccountType());
		account.setAccountName(PersonName.fromEntity(entity).getName());
		account.setAccountNameKana(PersonName.fromEntity(entity).getNameKana());
		return account;
	}

	/**
	 * 新規登録時 Dto -> Entity の変換処理
	 *
	 * @param dto
	 * @param accountSeqList
	 * @return tDengonEntity
	 */
	private TDengonEntity convertTDengonEntity(DengonEditDto dto, List<Long> accountSeqList) {

		TDengonEntity tDengonEntity = new TDengonEntity();
		tDengonEntity.setSendAccountSeq(SessionUtils.getLoginAccountSeq());
		tDengonEntity.setTitle(dto.getTitle());
		tDengonEntity.setBody(dto.getBody());
		tDengonEntity.setDraftFlg(dto.getDraftFlg());
		tDengonEntity.setSendTrashedFlg("0");
		tDengonEntity.setGyomuHistorySeq(dto.getGyomuhistorySeq());

		/* アカウント一覧の変換 */
		if (accountSeqList != null && accountSeqList.size() != 0) {
			String receiveAccountSeq = accountSeqList.stream().map(String::valueOf).collect(Collectors.joining(","));
			tDengonEntity.setReceiveAccountSeq(receiveAccountSeq);
		} else {
			tDengonEntity.setReceiveAccountSeq("");
		}

		return tDengonEntity;
	}

	/**
	 * 更新時 Dto -> Entity の変換処理
	 *
	 * @param dto
	 * @param accountSeqList
	 * @return tDengonEntity
	 */
	private void setTDengonEntity(TDengonEntity tDengonEntity, DengonEditDto dto, List<Long> accountSeqList) {

		tDengonEntity.setTitle(dto.getTitle());
		tDengonEntity.setBody(dto.getBody());
		tDengonEntity.setDraftFlg(dto.getDraftFlg());
		tDengonEntity.setGyomuHistorySeq(dto.getGyomuhistorySeq());

		/* アカウント一覧の変換 */
		if (accountSeqList != null && accountSeqList.size() != 0) {
			String receiveAccountSeq = accountSeqList.stream().map(String::valueOf).collect(Collectors.joining(","));
			tDengonEntity.setReceiveAccountSeq(receiveAccountSeq);
		} else {
			tDengonEntity.setReceiveAccountSeq("");
		}
	}

	/**
	 * 伝言作成時に受信者の伝言アカウントステータス情報を生成する
	 *
	 * @param tDengonEntity
	 * @param accountSeqList
	 * @return tDengonAccountStatusEntityList
	 */
	private List<TDengonAccountStatusEntity> convertTDengonAccountStatusEntityList(Long dengonSeq, List<Long> accountSeqList) {

		List<TDengonAccountStatusEntity> tDengonAccountStatusEntityList = new ArrayList<TDengonAccountStatusEntity>();

		for (Long accountSeq : accountSeqList) {
			TDengonAccountStatusEntity entity = new TDengonAccountStatusEntity();

			entity.setAccountSeq(accountSeq);
			entity.setDengonSeq(dengonSeq);
			entity.setImportantFlg("0");
			entity.setOpenFlg("0");
			entity.setTrashedFlg("0");

			tDengonAccountStatusEntityList.add(entity);
		}

		return tDengonAccountStatusEntityList;
	}

	/**
	 * 業務履歴に紐づく案件、顧客、裁判情報を取得、設定する。
	 *
	 * @param dengonEditForm
	 * @param dto
	 * @param gyomuhistorySeq
	 */
	private void getGyoumuHistoryInfo(DengonEditForm dengonEditForm, DengonEditDto dto, Long gyomuhistorySeq) {

		// 選択した業務履歴のタイトルと本文を取得します
		TGyomuHistoryEntity gyomuhistoryEntity = tGyomuHistoryDao.selectBySeq(gyomuhistorySeq);

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
				label.append(Objects.nonNull(tSaibanJikenEntity.getJikenName()) ? tSaibanJikenEntity.getJikenName() : "(事件名未入力)");
				dto.setSaibanSeq(gyomuhistoryEntity.getSaibanSeq());
				dto.setAnkenId(AnkenId.of(tSaibanEntity.getAnkenId()));
				dto.setSaibanBranchNo(tSaibanEntity.getSaibanBranchNo());
				// 事件名
				dto.setSaibanName(label.toString());
			}
		}
		List<AnkenCustomerRelationBean> ankenRelationBeanList = tGyomuHistoryDao.selectAnkenBeanBySeq(gyomuhistorySeq);
		List<AnkenCustomerRelationBean> customerRelationBeanList = tGyomuHistoryDao.selectCustomerBeanBySeq(gyomuhistorySeq);
		// 業歴に紐づく案件情報を取得、設定する
		if (!ankenRelationBeanList.isEmpty()) {
			List<DengonAnkenCustomerForm> relatedAnkenList = new ArrayList<DengonAnkenCustomerForm>();
			for (AnkenCustomerRelationBean ankenRelationBean : ankenRelationBeanList) {
				DengonAnkenCustomerForm relatedAnken = new DengonAnkenCustomerForm();
				relatedAnken.setAnkenId(AnkenId.of(ankenRelationBean.getAnkenId()));
				relatedAnken.setAnkenName(ankenRelationBean.getAnkenName());
				relatedAnkenList.add(relatedAnken);
			}
			dengonEditForm.setRelatedAnkenList(relatedAnkenList);
		}
		// 業歴に紐づく顧客情報を取得、設定する
		if (!customerRelationBeanList.isEmpty()) {
			List<DengonAnkenCustomerForm> relatedCustomerList = new ArrayList<DengonAnkenCustomerForm>();
			for (AnkenCustomerRelationBean customerRelationBean : customerRelationBeanList) {
				DengonAnkenCustomerForm relatedCustomer = new DengonAnkenCustomerForm();
				relatedCustomer.setCustomerId(CustomerId.of(customerRelationBean.getCustomerId()));
				relatedCustomer.setCustomerName(customerRelationBean.getCustomerName());
				relatedCustomerList.add(relatedCustomer);
			}
			dengonEditForm.setRelatedCustomerList(relatedCustomerList);
		}
	}

}
