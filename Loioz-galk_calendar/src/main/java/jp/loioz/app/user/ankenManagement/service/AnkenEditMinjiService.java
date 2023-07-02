package jp.loioz.app.user.ankenManagement.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKanyoshaService;
import jp.loioz.app.common.validation.accessDB.CommonKanyoshaValidator;
import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenKanyoshaViewDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenTantoDispDto;
import jp.loioz.app.user.ankenManagement.form.AnkenEditMinjiInputForm;
import jp.loioz.app.user.ankenManagement.form.AnkenEditMinjiViewForm;
import jp.loioz.bean.AnkenRelatedKanyoshaBean;
import jp.loioz.bean.AnkenTantoAccountBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenRelatedKanyoshaDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TAnkenRelatedKanyoshaEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanEntity;

/**
 * 案件管理画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenEditMinjiService extends DefaultService {

	/** アカウント共通サービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 案件共通サービス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 分野共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 関与者共通サービス */
	@Autowired
	private CommonKanyoshaService commonKanyoshaService;

	/** 共通：関与者バリデータクラス */
	@Autowired
	private CommonKanyoshaValidator commonKanyoshaValidator;

	@Autowired
	private MAccountDao mAccountDao;

	@Autowired
	private TAnkenDao tAnkenDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	@Autowired
	private TAnkenRelatedKanyoshaDao tAnkenRelatedKanyoshaDao;

	/** 名簿情報Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	@Autowired
	private TSaibanDao tSaibanDao;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する<br>
	 *
	 * <pre>
	 * 案件の分野が民事の場合
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @return 画面表示情報
	 */
	public AnkenEditMinjiViewForm createViewForm(Long ankenId) {

		AnkenEditMinjiViewForm form = new AnkenEditMinjiViewForm();
		form.setAnkenId(AnkenId.of(ankenId));

		return form;
	}

	/**
	 * 案件基本情報の表示用フォームオブジェクトを作成する
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenEditMinjiViewForm.AnkenBasicViewForm createAnkenBasicViewForm(Long ankenId) {

		AnkenEditMinjiViewForm.AnkenBasicViewForm viewForm = new AnkenEditMinjiViewForm.AnkenBasicViewForm();

		// 表示フォームは、基本的には入力フォームで保存されたデータを表示する形式になるので、入力フォームの取得処理を活用する。

		// 案件の基本情報入力フォーム
		AnkenEditMinjiInputForm.AnkenBasicInputForm inputForm = this.createAnkenBasicInputForm(ankenId);

		// 表示フォームに値を設定

		// 案件情報
		viewForm.setAnkenId(AnkenId.of(ankenId));
		viewForm.setAnkenType(inputForm.getAnkenType());
		viewForm.setAnkenName(inputForm.getAnkenName());
		viewForm.setBunya(commonBunyaService.getBunya(inputForm.getBunya()));
		viewForm.setAnkenCreatedDate(DateUtils.parseToString(inputForm.getAnkenCreatedDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		viewForm.setJianSummary(inputForm.getJianSummary());

		// 担当者情報
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();
		List<AnkenTantoDto> ankenTantoDto = commonAnkenService.getAnkenTantoListByAnkenId(ankenId);

		// 売上計上先
		List<AnkenTantoDispDto> salesOwner = ankenTantoDto.stream()
				.filter(e -> TantoType.SALES_OWNER.equalsByCode(e.getTantoType()))
				.map(e -> comvert2AnkenTantoDispDto(e, accountNameMap))
				.collect(Collectors.toList());
		viewForm.setSalesOwner(salesOwner);

		// 担当弁護士
		List<AnkenTantoDispDto> tantoLaywer = ankenTantoDto.stream()
				.filter(e -> TantoType.LAWYER.equalsByCode(e.getTantoType()))
				.map(e -> comvert2AnkenTantoDispDto(e, accountNameMap))
				.collect(Collectors.toList());
		viewForm.setTantoLaywer(tantoLaywer);

		// 担当事務
		List<AnkenTantoDispDto> tantoJimu = ankenTantoDto.stream()
				.filter(e -> TantoType.JIMU.equalsByCode(e.getTantoType()))
				.map(e -> comvert2AnkenTantoDispDto(e, accountNameMap))
				.collect(Collectors.toList());
		viewForm.setTantoJimu(tantoJimu);

		// 裁判件数
		List<TSaibanEntity> tSaibanEntities = tSaibanDao.selectByAnkenId(ankenId);
		int saibanCount = tSaibanEntities == null ? 0 : tSaibanEntities.size();
		viewForm.setSaibanCount(saibanCount);

		return viewForm;
	}

	/**
	 * 案件の基本情報入力フォームを取得する
	 * 
	 * @param ankenId
	 * @return AnkenEditInputForm.AnkenBasicInputForm
	 */
	public AnkenEditMinjiInputForm.AnkenBasicInputForm createAnkenBasicInputForm(Long ankenId) {

		// 案件情報を取得
		TAnkenEntity ankenEntity = this.getAnkenEntity(ankenId);

		// 初期設定済みの入力フォームを取得
		AnkenEditMinjiInputForm.AnkenBasicInputForm inputForm = this.getInitializedAnkenBasicInputForm(ankenId, ankenEntity);

		// 案件情報
		inputForm.setAnkenType(AnkenType.of(ankenEntity.getAnkenType()));
		inputForm.setAnkenCreatedDate(ankenEntity.getAnkenCreatedDate());
		inputForm.setAnkenName(ankenEntity.getAnkenName());
		inputForm.setBunya(ankenEntity.getBunyaId());
		inputForm.setJianSummary(ankenEntity.getJianSummary());

		// 案件担当者を取得
		List<TAnkenTantoEntity> ankenTantoEntityList = tAnkenTantoDao.selectByAnkenId(ankenId);

		// 案件担当者を設定
		Map<String, List<TAnkenTantoEntity>> groupedTanto = ankenTantoEntityList.stream()
				.filter(entity -> StringUtils.isNotEmpty(entity.getTantoType()))
				.sorted(Comparator.comparing(TAnkenTantoEntity::getTantoTypeBranchNo))
				.collect(Collectors.groupingBy(TAnkenTantoEntity::getTantoType));

		// 売上計上先
		List<AnkenTantoSelectInputForm> salesOwnerList = commonAnkenService.createTantoList(groupedTanto.getOrDefault(TantoType.SALES_OWNER.getCd(), Collections.emptyList()));
		inputForm.setSalesOwner(salesOwnerList);

		// 担当弁護士
		List<AnkenTantoSelectInputForm> tantoLawyerList = commonAnkenService.createTantoList(groupedTanto.getOrDefault(TantoType.LAWYER.getCd(), Collections.emptyList()));
		inputForm.setTantoLawyer(tantoLawyerList);

		// 担当事務
		List<AnkenTantoSelectInputForm> tantoJimuList = commonAnkenService.createTantoList(groupedTanto.getOrDefault(TantoType.JIMU.getCd(), Collections.emptyList()));
		inputForm.setTantoJimu(tantoJimuList);

		return inputForm;
	}

	/**
	 * 初期設定済みの案件の基本情報入力フォームを取得
	 * 
	 * @param ankenId
	 * @param ankenEntity
	 * @return
	 */
	public AnkenEditMinjiInputForm.AnkenBasicInputForm getInitializedAnkenBasicInputForm(Long ankenId, TAnkenEntity ankenEntity) {

		AnkenEditMinjiInputForm.AnkenBasicInputForm inputForm = new AnkenEditMinjiInputForm.AnkenBasicInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm, ankenId, AnkenType.of(ankenEntity.getAnkenType()));

		// 表示対象分野の取得
		BunyaDto bunya = commonBunyaService.getBunya(ankenEntity.getBunyaId());

		if (bunya.isDisabledFlg()) {
			// 表示対象の分野が利用停止の場合、プルダウン選択肢一覧に追加
			List<BunyaDto> bunyaList = inputForm.getBunyaList();
			bunyaList.add(bunya);
			// 表示順にソート
			List<BunyaDto> sortedList = bunyaList.stream()
					.sorted(Comparator.comparing(BunyaDto::getDispOrder))
					.collect(Collectors.toList());
			inputForm.setBunyaList(sortedList);
		}

		return inputForm;
	}

	/**
	 * 表示用プロパティの設定
	 * 
	 * @param basicInfoInputForm
	 * @param ankenId
	 * @param ankenType 担当者プルダウンの選択肢取得使用する。
	 */
	public void setDisplayData(AnkenEditMinjiInputForm.AnkenBasicInputForm inputForm, Long ankenId, AnkenType ankenType) {

		// 案件ID
		inputForm.setAnkenId(ankenId);

		// 受任区分
		List<SelectOptionForm> ankenTypeOptions = AnkenType.getSelectOptions();
		inputForm.setAnkenTypeOptions(ankenTypeOptions);

		// 分野プルダウン情報取得（利用可の分野のみ）
		List<BunyaDto> bunyaList = commonBunyaService.getBunyaList(SystemFlg.codeToBoolean(SystemFlg.FLG_OFF.getCd()));
		inputForm.setBunyaList(bunyaList);

		// 案件担当取得
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = tAnkenTantoDao.selectByIdForAccount(ankenId);

		// アカウント取得
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();

		// 売上計上先プルダウン
		inputForm.setSalesOwnerOptions(commonAnkenService.getSalesOwnerOptionList(ankenType, ankenTantoAccountBeanList, accountEntityList));

		// 担当弁護士プルダウン
		inputForm.setTantoLawyerOptions(commonAnkenService.getLawyerOptionList(ankenTantoAccountBeanList, accountEntityList));

		// 担当事務プルダウン
		inputForm.setTantoJimuOptions(commonAnkenService.getTantoJimuOptionList(ankenTantoAccountBeanList, accountEntityList));
	}

	/**
	 * 案件基本情報の更新
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 */
	public void saveAnkenBasic(Long ankenId, AnkenEditMinjiInputForm.AnkenBasicInputForm basicInputForm) throws AppException {

		if (commonAnkenService.isChangedBunya(ankenId, basicInputForm.getBunya())) {
			// 分野の変更を行うが、本メソッドが呼ばれた場合
			throw new RuntimeException("分野が変更されるケース");
		}

		try {
			// 案件の更新
			this.updateAnken(ankenId, basicInputForm);

			// 案件-担当を更新
			this.updateAnkenTanto(ankenId, basicInputForm);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件基本情報の更新
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 */
	public void saveAnkenBasicHasChangeBunya(Long ankenId, AnkenEditMinjiInputForm.AnkenBasicInputForm basicInputForm) throws AppException {

		if (!commonAnkenService.isChangedBunya(ankenId, basicInputForm.getBunya())) {
			// 分野の変更を行わないが、本メソッドが呼ばれた場合
			throw new RuntimeException("分野が変更されないケース");
		}

		try {
			// 分野の変更
			commonAnkenService.changeBunya(ankenId, basicInputForm.getBunya());

			// 案件の更新
			this.updateAnken(ankenId, basicInputForm);

			// 案件-担当を更新
			this.updateAnkenTanto(ankenId, basicInputForm);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件顧客情報表示項目を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenEditMinjiViewForm.AnkenCustomerListViewForm createAnkenCustomerListViewForm(Long ankenId) {

		AnkenEditMinjiViewForm.AnkenCustomerListViewForm ankenCustomerListViewForm = new AnkenEditMinjiViewForm.AnkenCustomerListViewForm();

		// 案件ID
		ankenCustomerListViewForm.setAnkenId(AnkenId.of(ankenId));

		// 名簿情報をMap化
		List<TPersonEntity> tPersonEntities = tPersonDao.selectByAnkenId(ankenId);
		Map<Long, TPersonEntity> seqToCustomerMap = tPersonEntities.stream()
				.collect(Collectors.toMap(TPersonEntity::getCustomerId, Function.identity()));

		// 案件に紐づく顧客情報を取得
		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByAnkenId(ankenId);
		List<AnkenCustomerDto> ankenCustomerDtoList = tAnkenCustomerEntities.stream().map(entity -> {
			TPersonEntity tPersonEntity = seqToCustomerMap.get(entity.getCustomerId());
			return convert2AnkenCustomerRow(entity, tPersonEntity);
		}).collect(Collectors.toList());

		ankenCustomerListViewForm.setAnkenCustomerDto(ankenCustomerDtoList);
		return ankenCustomerListViewForm;
	}

	/**
	 * 案件顧客情報の入力用オブジェクトを作成する
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	public AnkenEditMinjiInputForm.AnkenCustomerInputForm createAnkenCustomerInputForm(Long ankenId, Long customerId) {

		AnkenEditMinjiInputForm.AnkenCustomerInputForm inputForm = this.getInitializedAnkenCustomerInputForm(ankenId, customerId);

		// 案件顧客を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 案件に紐づく顧客情報
		inputForm.setJuninDate(DateUtils.parseToString(tAnkenCustomerEntity.getJuninDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setJikenshoriKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getJikenKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setSeisanKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setAnkenStatus(AnkenStatus.of(tAnkenCustomerEntity.getAnkenStatus()));

		// 完了チェック
		inputForm.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		// バージョンNo
		inputForm.setVersionNo(tAnkenCustomerEntity.getVersionNo());

		return inputForm;
	}

	/**
	 * 案件に紐づく顧客情報入力フォームを取得
	 * 
	 * @param customerId
	 * @return
	 */
	public AnkenEditMinjiInputForm.AnkenCustomerInputForm getInitializedAnkenCustomerInputForm(Long ankenId, Long customerId) {

		AnkenEditMinjiInputForm.AnkenCustomerInputForm inputForm = new AnkenEditMinjiInputForm.AnkenCustomerInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm, ankenId, customerId);

		return inputForm;
	}

	/**
	 * 案件に紐づく顧客情報入力フォームに表示用のデータを設定する。
	 * 
	 * <pre>
	 * リクエストパラメータや、画面の入力項目に表示する値ではない、
	 * 表示処理のみで利用する（HTML、もしくはHTMLから呼ばれるFormのメソッドでのみ参照される）データをinputフォームに設定する。
	 * 
	 * つまり、このメソッドで設定すプロパティがinputフォームから削除された場合、
	 * 影響があるのはHTMLとFormのみとならなければならなず、そうならないような箇所で
	 * このメソッドで設定するプロパティを参照してはいけない。
	 * </pre>
	 * 
	 * @param inputForm
	 * @param ankenId
	 * @param customerId
	 */
	public void setDisplayData(AnkenEditMinjiInputForm.AnkenCustomerInputForm inputForm, Long ankenId, Long customerId) {

		// 案件顧客を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 案件ID、顧客ID
		inputForm.setAnkenId(AnkenId.of(tAnkenCustomerEntity.getAnkenId()));
		inputForm.setCustomerId(CustomerId.of(tAnkenCustomerEntity.getCustomerId()));
	}

	/**
	 * 案件顧客情報の保存処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param rowInputForm
	 * @throws AppException
	 */
	public void saveAnkenCustomer(Long ankenId, Long customerId, AnkenEditMinjiInputForm.AnkenCustomerInputForm rowInputForm) throws AppException {

		// 既にDBに登録されている情報
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 画面入力項目を設定
		tAnkenCustomerEntity.setJuninDate(DateUtils.parseToLocalDate(rowInputForm.getJuninDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenCustomerEntity.setJikenKanryoDate(DateUtils.parseToLocalDate(rowInputForm.getJikenshoriKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenCustomerEntity.setKanryoDate(DateUtils.parseToLocalDate(rowInputForm.getSeisanKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenCustomerEntity.setKanryoFlg(SystemFlg.booleanToCode(rowInputForm.isCompleted()));
		tAnkenCustomerEntity.setAnkenStatus(commonAnkenService.getCurrentAnkenStatus(tAnkenCustomerEntity));
		tAnkenCustomerEntity.setVersionNo(rowInputForm.getVersionNo()); // ステータスに関係するのでバージョン管理を行う

		try {
			// 更新処理
			tAnkenCustomerDao.update(tAnkenCustomerEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件顧客一覧 顧客の削除処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @throws AppException
	 */
	public void deleteAnkenCustomer(Long ankenId, Long customerId) throws AppException {

		commonAnkenService.deleteAnkenCustomer(ankenId, customerId);
		commonAnkenService.deleteGyomuHistory(ankenId, customerId);
	}

	/**
	 * 案件 関与者情報の表示用オブジェクトを作成
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenEditMinjiViewForm.AnkenAitegataListViewForm createAnkenAitegataListViewForm(Long ankenId) {

		AnkenEditMinjiViewForm.AnkenAitegataListViewForm viewForm = new AnkenEditMinjiViewForm.AnkenAitegataListViewForm();

		// 関与者(相手方)を取得
		List<AnkenRelatedKanyoshaBean> ankenRelatedKanyoshaBeanList = commonAnkenService.getAnkenRelatedKanyoshaBeanList(ankenId, KanyoshaType.AITEGATA, SystemFlg.FLG_OFF);

		// 関与者(相手方代理人)を取得
		List<AnkenRelatedKanyoshaBean> ankenRelatedKanyoshaDairininBeanList = commonAnkenService.getAnkenRelatedKanyoshaBeanList(ankenId, KanyoshaType.AITEGATA, SystemFlg.FLG_ON);

		// 画面表示用に加工
		List<AnkenKanyoshaViewDto> ankenAitegataDtoList = commonAnkenService.generateKanyoshaViewDto(ankenRelatedKanyoshaBeanList, ankenRelatedKanyoshaDairininBeanList);
		viewForm.setAnkenKanyoshaViewDtoList(ankenAitegataDtoList);

		return viewForm;
	}

	/**
	 * 相手方の削除処理
	 * 
	 * @param ankenId
	 * @param aitegataKanyoshaSeq
	 * @throws AppException
	 */
	public void deleteAitegata(Long ankenId, Long aitegataKanyoshaSeq) throws AppException {

		// 削除データを取得
		TAnkenRelatedKanyoshaEntity aitegataEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, aitegataKanyoshaSeq);

		// 排他チェック
		if (aitegataEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 紐付けされている関与者
		Long relatedKanyosha = aitegataEntity.getRelatedKanyoshaSeq();

		boolean needDelete;
		if (relatedKanyosha == null) {
			needDelete = false;
		} else {
			// 案件に紐づくデータを取得
			List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(ankenId);

			// 紐付けを解除する代理人が、どの相手方にも紐付かなくなった場合は削除が必要
			long count = tAnkenRelatedKanyoshaEntities.stream()
					.filter(e -> {
						return Objects.equals(relatedKanyosha, e.getRelatedKanyoshaSeq())
								&& !Objects.equals(aitegataKanyoshaSeq, e.getKanyoshaSeq());
					}).count();
			needDelete = count == 0;
		}

		try {
			// 相手方の削除処理
			tAnkenRelatedKanyoshaDao.delete(aitegataEntity);
			// 相手方代理人の削除処理
			if (needDelete) {
				TAnkenRelatedKanyoshaEntity dairininEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, aitegataEntity.getRelatedKanyoshaSeq());
				tAnkenRelatedKanyoshaDao.delete(dairininEntity);
			}

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	// 共通系の処理

	/**
	 * 相手方代理人の削除処理
	 * 
	 * @param ankenId
	 * @param aitegataKanyoshaSeq
	 * @param dairininKanyoshaSeq
	 * @throws AppException
	 */
	public void deleteAitegataDairinin(Long ankenId, Long aitegataKanyoshaSeq, Long dairininKanyoshaSeq) throws AppException {

		// 相手方情報から紐付けをはずす
		TAnkenRelatedKanyoshaEntity aitegataEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, aitegataKanyoshaSeq);

		// 排他チェック
		if (aitegataEntity == null || !Objects.equals(aitegataEntity.getRelatedKanyoshaSeq(), dairininKanyoshaSeq)) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 代理人をNULLに更新
		aitegataEntity.setRelatedKanyoshaSeq(null);

		// 案件に紐づくデータを取得
		List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(ankenId);

		// 紐付けを解除する代理人が、どの相手方にも紐付かなくなった場合は削除が必要
		long count = tAnkenRelatedKanyoshaEntities.stream()
				.filter(e -> {
					return Objects.equals(dairininKanyoshaSeq, e.getRelatedKanyoshaSeq())
							&& !Objects.equals(aitegataKanyoshaSeq, e.getKanyoshaSeq());
				}).count();

		boolean needDelete = count == 0;

		try {
			// 登録処理
			tAnkenRelatedKanyoshaDao.update(aitegataEntity);

			if (needDelete) {
				// 削除処理
				TAnkenRelatedKanyoshaEntity dairininEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, dairininKanyoshaSeq);
				tAnkenRelatedKanyoshaDao.delete(dairininEntity);
			}

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 売上帰属に設定できるユーザーが1ユーザーしかいない場合は、デフォルトで売上計上先にユーザーをセットした状態にする。<br>
	 * 
	 * @param ankenBasicInputForm
	 * @return
	 */
	public AnkenEditMinjiInputForm.AnkenBasicInputForm setDefaultSalesOwner(AnkenEditMinjiInputForm.AnkenBasicInputForm ankenBasicInputForm) {

		// 案件担当取得
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = tAnkenTantoDao.selectByIdForAccount(ankenBasicInputForm.getAnkenId());
		// アカウント取得
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();
		// 売上計上先
		List<SelectOptionForm> salesOwnerOptionList = commonAnkenService.getTantoOptionList(accountEntityList,
				TantoType.SALES_OWNER, ankenTantoAccountBeanList);

		if (salesOwnerOptionList.size() == 1) {
			AnkenTantoSelectInputForm tanto = new AnkenTantoSelectInputForm(salesOwnerOptionList.get(0).getValueAsLong(), false);
			ankenBasicInputForm.setSalesOwner(Arrays.asList(tanto));
		}
		return ankenBasicInputForm;
	}

	/**
	 * 関与者情報の削除前のチェック処理
	 * 
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public Map<String, Object> deleteCommonKanyoshaFromAnkenBeforeCheck(Long kanyoshaSeq) throws AppException {
		return commonKanyoshaService.deleteCommonKanyoshaBeforeCheck(kanyoshaSeq);
	}

	/**
	 * 関与者情報の削除処理
	 * 
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void deleteKanyoshaFromAnken(Long kanyoshaSeq) throws AppException {
		commonKanyoshaService.deleteCommonKanyoshaInfo(kanyoshaSeq);
	}

	/**
	 * 関与者共通処理で関与者削除可否のチェックを実施する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	public boolean canDeleteKanyosha(Long kanyoshaSeq) {
		return commonKanyoshaValidator.canDeleteKanyosha(kanyoshaSeq);
	}

	/**
	 * 案件Entityを取得
	 * 
	 * @param ankenId
	 * @return
	 */
	private TAnkenEntity getAnkenEntity(Long ankenId) {

		TAnkenEntity entity = tAnkenDao.selectByAnkenId(ankenId);
		// 案件情報が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}

		return entity;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	// 共通系の処理

	/**
	 * 案件情報の更新
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 */
	private void updateAnken(Long ankenId, AnkenEditMinjiInputForm.AnkenBasicInputForm basicInputForm) {

		TAnkenEntity entity = tAnkenDao.selectById(ankenId);

		// 案件情報が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}

		entity.setBunyaId(basicInputForm.getBunya());
		entity.setAnkenName(basicInputForm.getAnkenName());
		entity.setAnkenCreatedDate(basicInputForm.getAnkenCreatedDate());
		entity.setAnkenType(DefaultEnum.getCd(basicInputForm.getAnkenType()));
		entity.setJianSummary(basicInputForm.getJianSummary());

		tAnkenDao.update(entity);
	}

	// 共通系の処理

	/**
	 * 案件-担当情報の更新
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 */
	private void updateAnkenTanto(Long ankenId, AnkenEditMinjiInputForm.AnkenBasicInputForm basicInputForm) {

		// 既にDBに登録されている情報
		List<TAnkenTantoEntity> ankenTantoEntityList = tAnkenTantoDao.selectByAnkenId(ankenId);

		// 担当種別でグループ化
		Map<String, List<TAnkenTantoEntity>> groupedTanto = ankenTantoEntityList.stream()
				.filter(entity -> StringUtils.isNotEmpty(entity.getTantoType()))
				.sorted(Comparator.comparing(TAnkenTantoEntity::getTantoTypeBranchNo))
				.collect(Collectors.groupingBy(TAnkenTantoEntity::getTantoType));

		// 更新処理
		updateAnkenTanto(ankenId, basicInputForm.getSalesOwner(),
				groupedTanto.getOrDefault(TantoType.SALES_OWNER.getCd(), Collections.emptyList()),
				TantoType.SALES_OWNER);
		updateAnkenTanto(ankenId, basicInputForm.getTantoLawyer(),
				groupedTanto.getOrDefault(TantoType.LAWYER.getCd(), Collections.emptyList()),
				TantoType.LAWYER);
		updateAnkenTanto(ankenId, basicInputForm.getTantoJimu(),
				groupedTanto.getOrDefault(TantoType.JIMU.getCd(), Collections.emptyList()),
				TantoType.JIMU);
	}

	// 共通系の処理

	/**
	 * 担当種別毎の案件担当者を更新する
	 *
	 * @param ankenId 案件ID
	 * @param rawInputItemList 画面入力情報
	 * @param entityList DB情報
	 * @param tantoType 担当種別
	 */
	private void updateAnkenTanto(Long ankenId, List<AnkenTantoSelectInputForm> rawInputItemList, List<TAnkenTantoEntity> entityList, TantoType tantoType) {

		// 画面からの入力情報
		List<AnkenTantoSelectInputForm> inputItemList = rawInputItemList.stream()
				.filter(item -> !item.isEmpty())
				.distinct()
				.limit(CommonConstant.ANKEN_TANTO_ADD_LIMIT)
				.collect(Collectors.toList());

		// i番目のデータの更新ルール
		AtomicLong i = new AtomicLong(0);
		BiConsumer<TAnkenTantoEntity, AnkenTantoSelectInputForm> entitySetter = (entity, inputItem) -> {
			entity.setAccountSeq(inputItem.getAccountSeq());
			entity.setTantoType(tantoType.getCd());
			entity.setAnkenMainTantoFlg(SystemFlg.booleanToCode(inputItem.isMainTanto()));
			entity.setTantoTypeBranchNo(i.getAndIncrement());
		};

		// 更新処理
		LoiozCollectionUtils.zipping(entityList, inputItemList, (dbValue, inputItem) -> {
			if (inputItem != null) {
				if (dbValue != null) {
					// 入力データあり、既存データあり
					// 既存の担当者を入力データで上書き
					TAnkenTantoEntity entity = dbValue;
					entitySetter.accept(entity, inputItem);
					tAnkenTantoDao.update(entity);

				} else {
					// 入力データあり、既存データなし
					// 担当者を新規登録
					TAnkenTantoEntity entity = new TAnkenTantoEntity();
					entity.setAnkenId(ankenId);
					entitySetter.accept(entity, inputItem);
					tAnkenTantoDao.insert(entity);
				}

			} else {
				if (dbValue != null) {
					// 入力データなし、既存データあり
					// 担当者の数が減ったので削除
					tAnkenTantoDao.delete(dbValue);
				} else {
					// 入力データなし、既存データなし
					// 何もしない
				}
			}
		});
	}

	/**
	 * 案件-顧客１件分のデータをエンティティから作成
	 * 
	 * @param tAnkenCustomerEntity
	 * @param tPersonEntity
	 * @return
	 */
	private AnkenCustomerDto convert2AnkenCustomerRow(TAnkenCustomerEntity tAnkenCustomerEntity, TPersonEntity tPersonEntity) {

		AnkenCustomerDto ankenCustomerRow = new AnkenCustomerDto();

		// 顧客情報
		ankenCustomerRow.setPersonId(tPersonEntity.getPersonId());
		ankenCustomerRow.setCustomerId(CustomerId.of(tPersonEntity.getCustomerId()));
		ankenCustomerRow.setCustomerName(PersonName.fromEntity(tPersonEntity).getName());
		ankenCustomerRow.setCustomerType(CustomerType.of(tPersonEntity.getCustomerType()));
		ankenCustomerRow.setPersonAttribute(PersonAttribute.of(tPersonEntity.getCustomerFlg(),
				tPersonEntity.getAdvisorFlg(),
				tPersonEntity.getCustomerType()));
		// 案件顧客情報
		ankenCustomerRow.setAnkenId(AnkenId.of(tAnkenCustomerEntity.getAnkenId()));
		ankenCustomerRow.setStatus(AnkenStatus.of(tAnkenCustomerEntity.getAnkenStatus()));
		ankenCustomerRow.setJuninDate(DateUtils.parseToString(tAnkenCustomerEntity.getJuninDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		ankenCustomerRow.setJikenshoriKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getJikenKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		ankenCustomerRow.setSeisanKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		ankenCustomerRow.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		return ankenCustomerRow;
	}

	/**
	 * 担当者情報を表示用Dtoに変換する
	 * 
	 * @param ankenTantoDto
	 * @param accountNameMap
	 * @return
	 */
	private AnkenTantoDispDto comvert2AnkenTantoDispDto(AnkenTantoDto ankenTantoDto, Map<Long, String> accountNameMap) {
		AnkenTantoDispDto dispDto = new AnkenTantoDispDto();
		dispDto.setAccountSeq(ankenTantoDto.getAccountSeq());
		dispDto.setAccountName(accountNameMap.get(ankenTantoDto.getAccountSeq()));
		dispDto.setMain(SystemFlg.codeToBoolean(ankenTantoDto.getAnkenMainTantoFlg()));
		dispDto.setTantoTypeBranchNo(ankenTantoDto.getTantoTypeBranchNo());
		dispDto.setAccountColor(ankenTantoDto.getAccountColor());
		dispDto.setAccountTypeStr(AccountType.of(ankenTantoDto.getAccountType()).getVal());
		return dispDto;
	}

}
