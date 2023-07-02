package jp.loioz.app.user.ankenManagement.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import jp.loioz.app.common.validation.accessDB.CommonAnkenValidator;
import jp.loioz.app.common.validation.accessDB.CommonCustomerValidator;
import jp.loioz.app.common.validation.accessDB.CommonKanyoshaValidator;
import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerJikenDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerSekkenDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerZaikanDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenKanyoshaViewDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenSosakikanDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenTantoDispDto;
import jp.loioz.app.user.ankenManagement.dto.SosakikanSelectOptionDto;
import jp.loioz.app.user.ankenManagement.form.AnkenEditKeijiInputForm;
import jp.loioz.app.user.ankenManagement.form.AnkenEditKeijiViewForm;
import jp.loioz.bean.AnkenAddKeijiBean;
import jp.loioz.bean.AnkenRelatedKanyoshaBean;
import jp.loioz.bean.AnkenSosakikanBean;
import jp.loioz.bean.AnkenTantoAccountBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.KoryuStatus;
import jp.loioz.common.constant.CommonConstant.ShobunType;
import jp.loioz.common.constant.CommonConstant.SosakikanType;
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
import jp.loioz.dao.MSosakikanDao;
import jp.loioz.dao.TAnkenAddKeijiDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenJikenDao;
import jp.loioz.dao.TAnkenKoryuDao;
import jp.loioz.dao.TAnkenRelatedKanyoshaDao;
import jp.loioz.dao.TAnkenSekkenDao;
import jp.loioz.dao.TAnkenSosakikanDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TKeijiAnkenCustomerDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MSosakikanEntity;
import jp.loioz.entity.TAnkenAddKeijiEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TAnkenJikenEntity;
import jp.loioz.entity.TAnkenKoryuEntity;
import jp.loioz.entity.TAnkenRelatedKanyoshaEntity;
import jp.loioz.entity.TAnkenSekkenEntity;
import jp.loioz.entity.TAnkenSosakikanEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TKeijiAnkenCustomerEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanEntity;

/**
 * 案件管理（刑事）画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenEditKeijiService extends DefaultService {

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

	/** 共通：顧客バリデータクラス */
	@Autowired
	private CommonCustomerValidator commonCustomerValidator;

	/** 共通：案件バリデータクラス */
	@Autowired
	private CommonAnkenValidator commonAnkenValidator;

	/** 共通：関与者バリデータクラス */
	@Autowired
	private CommonKanyoshaValidator commonKanyoshaValidator;

	/** アカウントDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 捜査機関Daoクラス */
	@Autowired
	private MSosakikanDao mSosakikanDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件刑事Daoクラス */
	@Autowired
	private TAnkenAddKeijiDao tAnkenAddKeijiDao;

	/** 案件顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 案件顧客事件Daoクラス */
	@Autowired
	private TAnkenJikenDao tAnkenJikenDao;

	/** 案件接見Daoクラス */
	@Autowired
	private TAnkenSekkenDao tAnkenSekkenDao;

	/** 案件勾留Daoクラス */
	@Autowired
	private TAnkenKoryuDao tAnkenKoryuDao;

	/** 案件捜査機関Daoクラス */
	@Autowired
	private TAnkenSosakikanDao tAnkenSosakikanDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 案件関与者Daoクラス */
	@Autowired
	private TAnkenRelatedKanyoshaDao tAnkenRelatedKanyoshaDao;

	/** 案件刑事-顧客Daoクラス */
	@Autowired
	private TKeijiAnkenCustomerDao tKeijiAnkenCustomerDao;

	/** 名簿情報Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 裁判Daoクラス */
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
	public AnkenEditKeijiViewForm createViewForm(Long ankenId) {

		AnkenEditKeijiViewForm form = new AnkenEditKeijiViewForm();
		form.setAnkenId(AnkenId.of(ankenId));

		return form;
	}

	/**
	 * 案件基本情報の表示用フォームオブジェクトを作成する
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenEditKeijiViewForm.AnkenBasicViewForm createAnkenBasicViewForm(Long ankenId) {

		AnkenEditKeijiViewForm.AnkenBasicViewForm viewForm = new AnkenEditKeijiViewForm.AnkenBasicViewForm();

		// 表示フォームは、基本的には入力フォームで保存されたデータを表示する形式になるので、入力フォームの取得処理を活用する。

		// 案件の基本情報入力フォーム
		AnkenEditKeijiInputForm.AnkenBasicInputForm inputForm = this.createAnkenBasicInputForm(ankenId);

		// 表示フォームに値を設定

		// 案件情報
		viewForm.setAnkenId(AnkenId.of(ankenId));
		viewForm.setAnkenType(inputForm.getAnkenType());
		viewForm.setAnkenName(inputForm.getAnkenName());
		viewForm.setBunya(commonBunyaService.getBunya(inputForm.getBunya()));
		viewForm.setAnkenCreatedDate(DateUtils.parseToString(inputForm.getAnkenCreatedDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		viewForm.setJianSummary(inputForm.getJianSummary());
		viewForm.setLawyerSelectType(inputForm.getLawyerSelectType());

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
	public AnkenEditKeijiInputForm.AnkenBasicInputForm createAnkenBasicInputForm(Long ankenId) {

		// 案件情報を取得
		AnkenAddKeijiBean ankenKeijiBean = this.getAnkenKeiji(ankenId);

		// 初期設定済みの入力フォームを取得
		AnkenEditKeijiInputForm.AnkenBasicInputForm inputForm = this.getInitializedAnkenBasicInputForm(ankenId, ankenKeijiBean);

		// 案件情報
		inputForm.setAnkenType(AnkenType.of(ankenKeijiBean.getAnkenType()));
		inputForm.setAnkenCreatedDate(ankenKeijiBean.getAnkenCreatedDate());
		inputForm.setAnkenName(ankenKeijiBean.getAnkenName());
		inputForm.setBunya(ankenKeijiBean.getBunyaId());
		inputForm.setJianSummary(ankenKeijiBean.getJianSummary());
		inputForm.setLawyerSelectType(ankenKeijiBean.getLawyerSelectType());

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
	 * @param ankenKeijiBean
	 * @return
	 */
	public AnkenEditKeijiInputForm.AnkenBasicInputForm getInitializedAnkenBasicInputForm(Long ankenId, AnkenAddKeijiBean ankenKeijiBean) {

		AnkenEditKeijiInputForm.AnkenBasicInputForm inputForm = new AnkenEditKeijiInputForm.AnkenBasicInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm, ankenId, AnkenType.of(ankenKeijiBean.getAnkenType()));

		// 表示対象分野の取得
		BunyaDto bunya = commonBunyaService.getBunya(ankenKeijiBean.getBunyaId());

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
	 * @param ankenType 担当者プルダウンの選択肢制御
	 */
	public void setDisplayData(AnkenEditKeijiInputForm.AnkenBasicInputForm inputForm, Long ankenId, AnkenType ankenType) {

		// 案件ID
		inputForm.setAnkenId(ankenId);

		// 受任区分
		List<SelectOptionForm> ankenTypeOptionList = AnkenType.getSelectOptions();
		inputForm.setAnkenTypeOptions(ankenTypeOptionList);

		// 分野プルダウン情報（利用可の分野のみ）
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
	public void saveAnkenBasic(Long ankenId, AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm) throws AppException {

		if (commonAnkenService.isChangedBunya(ankenId, basicInputForm.getBunya())) {
			// 分野の変更を行うが、本メソッドが呼ばれた場合
			throw new RuntimeException("分野が変更されるケース");
		}

		try {

			// 案件の更新
			this.updateAnken(ankenId, basicInputForm);

			// 案件-刑事の更新
			this.updateAnkenAddKeiji(ankenId, basicInputForm);

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
	public void saveAnkenBasicHasChangeBunya(Long ankenId, AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm) throws AppException {

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
	public AnkenEditKeijiViewForm.AnkenCustomerListViewForm createAnkenCustomerListViewForm(Long ankenId) {

		AnkenEditKeijiViewForm.AnkenCustomerListViewForm ankenCustomerListViewForm = new AnkenEditKeijiViewForm.AnkenCustomerListViewForm();

		// 案件ID
		ankenCustomerListViewForm.setAnkenId(AnkenId.of(ankenId));

		// 名簿情報をMap化
		List<TPersonEntity> tPersonEntities = tPersonDao.selectByAnkenId(ankenId);
		Map<Long, TPersonEntity> seqToCustomerMap = tPersonEntities.stream()
				.collect(Collectors.toMap(TPersonEntity::getCustomerId, Function.identity()));

		// 顧客関連情報を取得
		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByAnkenId(ankenId);
		List<Long> customerIdList = tAnkenCustomerEntities.stream().map(TAnkenCustomerEntity::getCustomerId).collect(Collectors.toList());

		// 刑事案件情報
		List<TKeijiAnkenCustomerEntity> tKeijiAnkenCustomerEntities = tKeijiAnkenCustomerDao.selectByAnkenId(ankenId);
		// 事件情報
		List<TAnkenJikenEntity> TAnkenJikenEntities = tAnkenJikenDao.selectByAnkenIdCustomerIdList(ankenId, customerIdList);
		// 接見情報
		List<TAnkenKoryuEntity> tAnkenKoryuEntities = tAnkenKoryuDao.selectByAnkenIdCustomerIdList(ankenId, customerIdList);
		// 在監情報
		List<TAnkenSekkenEntity> tAnkenSekkenEntities = tAnkenSekkenDao.selectByAnkenIdCustomerIdList(ankenId, customerIdList);
		// マスタ捜査機関情報
		List<MSosakikanEntity> mSosakikanEntities = mSosakikanDao.selectAll();

		// 刑事案件-顧客情報をMap化
		Map<Long, TKeijiAnkenCustomerEntity> seqToGroupedCustomerAnkenKeijiMap = tKeijiAnkenCustomerEntities.stream()
				.collect(Collectors.toMap(TKeijiAnkenCustomerEntity::getCustomerId, Function.identity()));

		// 事件関連情報をMap化
		Map<Long, List<TAnkenJikenEntity>> seqToGroupedCustomerJikenMap = TAnkenJikenEntities.stream()
				.collect(Collectors.groupingBy(TAnkenJikenEntity::getCustomerId));
		Map<Long, List<TAnkenKoryuEntity>> seqToGroupedCustomerKoryuMap = tAnkenKoryuEntities.stream()
				.collect(Collectors.groupingBy(TAnkenKoryuEntity::getCustomerId));
		Map<Long, List<TAnkenSekkenEntity>> seqToGroupedCustomerSekkenMap = tAnkenSekkenEntities.stream()
				.collect(Collectors.groupingBy(TAnkenSekkenEntity::getCustomerId));
		Map<Long, String> idToSosakikanNameMap = mSosakikanEntities.stream().collect(Collectors.toMap(MSosakikanEntity::getSosakikanId, MSosakikanEntity::getSosakikanName));

		// 顧客情報を取得
		List<AnkenCustomerDto> ankenCustomerDtoList = tAnkenCustomerEntities.stream().map(entity -> {
			TPersonEntity tPersonEntity = seqToCustomerMap.get(entity.getCustomerId());
			return convert2AnkenCustomer(entity, tPersonEntity,
					seqToGroupedCustomerAnkenKeijiMap.get(entity.getCustomerId()),
					seqToGroupedCustomerJikenMap.get(entity.getCustomerId()),
					seqToGroupedCustomerSekkenMap.get(entity.getCustomerId()),
					seqToGroupedCustomerKoryuMap.get(entity.getCustomerId()),
					idToSosakikanNameMap);
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
	public AnkenEditKeijiInputForm.AnkenCustomerInputForm createAnkenCustomerInputForm(Long ankenId, Long customerId) {

		AnkenEditKeijiInputForm.AnkenCustomerInputForm inputForm = this.getInitializedAnkenCustomerInputForm(ankenId, customerId);

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
	public AnkenEditKeijiInputForm.AnkenCustomerInputForm getInitializedAnkenCustomerInputForm(Long ankenId, Long customerId) {

		AnkenEditKeijiInputForm.AnkenCustomerInputForm inputForm = new AnkenEditKeijiInputForm.AnkenCustomerInputForm();

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
	public void setDisplayData(AnkenEditKeijiInputForm.AnkenCustomerInputForm inputForm, Long ankenId, Long customerId) {

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
	public void saveAnkenCustomer(Long ankenId, Long customerId, AnkenEditKeijiInputForm.AnkenCustomerInputForm rowInputForm) throws AppException {

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

		// 案件と顧客の紐づけを解除します。
		commonAnkenService.deleteAnkenCustomer(ankenId, customerId);
		commonAnkenService.deleteGyomuHistory(ankenId, customerId);
	}

	/**
	 * 案件刑事-顧客情報の保存処理
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param koryuStauts
	 * @throws AppException
	 */
	public void saveAnkenCustomerKoryuStatus(Long ankenId, Long customerId, KoryuStatus koryuStauts) throws AppException {

		TKeijiAnkenCustomerEntity tKeijiAnkenCustomerEntity = tKeijiAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		try {
			if (tKeijiAnkenCustomerEntity == null) {
				// 新規作成
				tKeijiAnkenCustomerEntity = new TKeijiAnkenCustomerEntity();
				tKeijiAnkenCustomerEntity.setAnkenId(ankenId);
				tKeijiAnkenCustomerEntity.setCustomerId(customerId);
				tKeijiAnkenCustomerEntity.setKoryuStatus(DefaultEnum.getCd(koryuStauts));
				tKeijiAnkenCustomerDao.insert(tKeijiAnkenCustomerEntity);
			} else {
				// 更新
				tKeijiAnkenCustomerEntity.setKoryuStatus(DefaultEnum.getCd(koryuStauts));
				tKeijiAnkenCustomerDao.update(tKeijiAnkenCustomerEntity);
			}

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件顧客-事件情報表示項目を取得する
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	public AnkenEditKeijiViewForm.AnkenCustomerJikenListViewForm createAnkenCustomerJikenListViewForm(Long ankenId, Long customerId) {

		AnkenEditKeijiViewForm.AnkenCustomerJikenListViewForm viewForm = new AnkenEditKeijiViewForm.AnkenCustomerJikenListViewForm();

		// 案件顧客を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 完了チェック
		viewForm.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		// DBから情報取得
		List<TAnkenJikenEntity> tAnkenEntity = tAnkenJikenDao.selectByAnkenIdCustomerId(ankenId, customerId);

		// 事件情報
		List<AnkenCustomerJikenDto> jikenDtoList = new ArrayList<AnkenCustomerJikenDto>();
		if (tAnkenEntity != null) {
			jikenDtoList = tAnkenEntity.stream()
					.map(entity -> AnkenCustomerJikenDto.builder()
							.jikenSeq(entity.getJikenSeq())
							.ankenId(entity.getAnkenId())
							.customerId(entity.getCustomerId())
							.jikenName(entity.getJikenName())
							.taihoDate(DateUtils.parseToString(entity.getTaihoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.koryuSeikyuDate(DateUtils.parseToString(entity.getKoryuSeikyuDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.koryuExpirationDate(DateUtils.parseToString(entity.getKoryuExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.koryuExtendedExpirationDate(DateUtils.parseToString(entity.getKoryuExtendedExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.shobunType(entity.getShobunType())
							.shobunDate(DateUtils.parseToString(entity.getShobunDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.remarks(entity.getRemarks())
							.build())
					.collect(Collectors.toList());
		}
		viewForm.setAnkenCustomerJikenDtoList(jikenDtoList);

		return viewForm;
	}

	/**
	 * 事件情報の入力用オブジェクトを作成する
	 * 
	 * @param jikenSeq
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	public AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm createAnkenCustomerJikenListInputForm(Long jikenSeq, Long ankenId,
			Long customerId) {

		// 初期設定済みの入力フォームを取得
		AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm inputForm = this.getInitializedAnkenCustomerJikenListInputForm(jikenSeq);

		// 対象の案件と顧客
		inputForm.setTargetAnkenId(ankenId);
		inputForm.setTargetCustomerId(customerId);

		// 案件顧客を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 完了チェック
		inputForm.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		if (jikenSeq == null) {
			// 新規登録の場合

			// 特に情報の設定はしない

		} else {
			// 更新の場合

			// 案件顧客-事件を取得
			TAnkenJikenEntity tAnkenJikenEntity = tAnkenJikenDao.selectBySeq(jikenSeq);

			// 事件情報
			inputForm.setJikenSeq(tAnkenJikenEntity.getJikenSeq());
			inputForm.setJikenName(tAnkenJikenEntity.getJikenName());
			inputForm.setTaihoDate(DateUtils.parseToString(tAnkenJikenEntity.getTaihoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			inputForm.setKoryuSeikyuDate(DateUtils.parseToString(tAnkenJikenEntity.getKoryuSeikyuDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			inputForm.setKoryuExpirationDate(DateUtils.parseToString(tAnkenJikenEntity.getKoryuExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			inputForm.setKoryuExtendedExpirationDate(DateUtils.parseToString(tAnkenJikenEntity.getKoryuExtendedExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			inputForm.setShobunType(tAnkenJikenEntity.getShobunType());
			inputForm.setShobunDate(DateUtils.parseToString(tAnkenJikenEntity.getShobunDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		}

		return inputForm;
	}

	/**
	 * 事件情報入力フォームを取得
	 * 
	 * @param jikenSeq
	 * @return
	 */
	public AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm getInitializedAnkenCustomerJikenListInputForm(Long jikenSeq) {

		AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm inputForm = new AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm);

		return inputForm;
	}

	/**
	 * 入力フォームに表示用のデータを設定する。
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
	 */
	public void setDisplayData(AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm inputForm) {

		// 表示用

		// 処分
		List<SelectOptionForm> shobunTypeOptionList = Stream
				.of(ShobunType.KOHAN_SEIKYU, ShobunType.RYAKUSHIKI_MEIREI, ShobunType.SOKKETSU_SAIBAN, ShobunType.FUKISO)
				.map(SelectOptionForm::fromEnum)
				.collect(Collectors.toList());
		inputForm.setShobunTypeOptions(shobunTypeOptionList);

	}

	/**
	 * 案件-顧客-事件情報の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registAnkenCustomerJiken(AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm inputForm) throws AppException {

		long registedCount = tAnkenJikenDao.selectCountByAnkenIdAndCustomerId(inputForm.getTargetAnkenId(), inputForm.getTargetCustomerId());
		if (registedCount >= CommonConstant.JIKEN_INFO_ADD_LIMIT) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonAnkenValidator.existsAnken(inputForm.getTargetAnkenId())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonCustomerValidator.existsPerson(inputForm.getTargetCustomerId())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TAnkenJikenEntity tAnkenJikenEntity = new TAnkenJikenEntity();
		tAnkenJikenEntity.setAnkenId(inputForm.getTargetAnkenId());
		tAnkenJikenEntity.setCustomerId(inputForm.getTargetCustomerId());
		tAnkenJikenEntity.setJikenName(inputForm.getJikenName());
		tAnkenJikenEntity.setTaihoDate(DateUtils.parseToLocalDate(inputForm.getTaihoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setKoryuSeikyuDate(DateUtils.parseToLocalDate(inputForm.getKoryuSeikyuDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setKoryuExpirationDate(DateUtils.parseToLocalDate(inputForm.getKoryuExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setKoryuExtendedExpirationDate(DateUtils.parseToLocalDate(inputForm.getKoryuExtendedExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setShobunType(inputForm.getShobunType());
		tAnkenJikenEntity.setShobunDate(DateUtils.parseToLocalDate(inputForm.getShobunDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setRemarks(null);

		try {
			// 登録処理
			tAnkenJikenDao.insert(tAnkenJikenEntity);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件-顧客-事件情報の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateAnkenCustomerJiken(AnkenEditKeijiInputForm.AnkenCustomerJikenListInputForm inputForm) throws AppException {

		TAnkenJikenEntity tAnkenJikenEntity = tAnkenJikenDao.selectBySeq(inputForm.getJikenSeq());
		if (tAnkenJikenEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		tAnkenJikenEntity.setJikenName(inputForm.getJikenName());
		tAnkenJikenEntity.setTaihoDate(DateUtils.parseToLocalDate(inputForm.getTaihoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setKoryuSeikyuDate(DateUtils.parseToLocalDate(inputForm.getKoryuSeikyuDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setKoryuExpirationDate(DateUtils.parseToLocalDate(inputForm.getKoryuExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setKoryuExtendedExpirationDate(DateUtils.parseToLocalDate(inputForm.getKoryuExtendedExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setShobunType(inputForm.getShobunType());
		tAnkenJikenEntity.setShobunDate(DateUtils.parseToLocalDate(inputForm.getShobunDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenJikenEntity.setRemarks(null);

		try {
			// 更新処理
			tAnkenJikenDao.update(tAnkenJikenEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件-顧客-事件情報の削除処理
	 * 
	 * @param jikenSeq
	 * @throws AppException
	 */
	public void deleteAnkenCustomerJiken(Long jikenSeq) throws AppException {

		TAnkenJikenEntity tAnkenJikenEntity = tAnkenJikenDao.selectBySeq(jikenSeq);
		if (tAnkenJikenEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 削除処理
			tAnkenJikenDao.delete(tAnkenJikenEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件-顧客-接見情報の取得
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	public AnkenEditKeijiViewForm.AnkenCustomerSekkenListViewForm createAnkenCustomerSekkenListViewForm(Long ankenId, Long customerId) {

		AnkenEditKeijiViewForm.AnkenCustomerSekkenListViewForm viewForm = new AnkenEditKeijiViewForm.AnkenCustomerSekkenListViewForm();

		// 案件顧客を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 完了チェック
		viewForm.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		// DBから情報取得
		List<TAnkenSekkenEntity> tAnkenSekkenEntities = tAnkenSekkenDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 事件情報
		List<AnkenCustomerSekkenDto> sekkenDtoList = tAnkenSekkenEntities.stream().map(e -> {
			return AnkenCustomerSekkenDto.builder()
					.sekkenSeq(e.getSekkenSeq())
					.ankenId(ankenId)
					.customerId(customerId)
					.sekkenStartAt(DateUtils.parseToString(e.getSekkenStartAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM))
					.sekkenEndAt(DateUtils.parseToString(e.getSekkenEndAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM))
					.place(e.getPlace())
					.remarks(e.getRemarks())
					.build();
		}).collect(Collectors.toList());

		viewForm.setAnkenCustomerSekkenDtoList(sekkenDtoList);
		return viewForm;
	}

	/**
	 * 接見情報の入力用オブジェクトを作成する
	 * 
	 * @param sekkenSeq
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	public AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm createAnkenCustomerSekkenListInputForm(Long sekkenSeq, Long ankenId, Long customerId) {

		// 初期設定済みの入力フォームを取得
		AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm = this.getInitializedAnkenCustomerSekkenListInputForm(sekkenSeq);

		// 対象の案件と顧客
		inputForm.setTargetAnkenId(ankenId);
		inputForm.setTargetCustomerId(customerId);

		// 案件顧客を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 完了チェック
		inputForm.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		if (sekkenSeq == null) {
			// 新規登録の場合

			// 特に情報の設定はしない

		} else {
			// 更新の場合

			// 案件顧客-接見を取得
			TAnkenSekkenEntity tAnkenSekkenEntity = tAnkenSekkenDao.selectBySeq(sekkenSeq);

			// 接見情報
			inputForm.setSekkenSeq(tAnkenSekkenEntity.getSekkenSeq());
			inputForm.setSekkenStartAt(DateUtils.parseToString(tAnkenSekkenEntity.getSekkenStartAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
			inputForm.setSekkenEndAt(DateUtils.parseToString(tAnkenSekkenEntity.getSekkenEndAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
			inputForm.setPlace(tAnkenSekkenEntity.getPlace());
			inputForm.setRemarks(tAnkenSekkenEntity.getRemarks());
		}

		return inputForm;

	}

	/**
	 * 案件顧客-接見入力フォームを取得
	 * 
	 * @param sekkenSeq
	 * @return
	 */
	private AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm getInitializedAnkenCustomerSekkenListInputForm(Long sekkenSeq) {

		AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm = new AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm();

		this.setDisplayData(inputForm);

		return inputForm;
	}

	/**
	 * 入力フォームに表示用のデータを設定する。
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
	 */
	public void setDisplayData(AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm) {
		// 現状はなにもしない
	}

	/**
	 * 案件顧客-接見情報の登録
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registAnkenCustomerSekken(AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm) throws AppException {

		long registedCount = tAnkenSekkenDao.selectCountByAnkenIdAndCustomerId(inputForm.getTargetAnkenId(), inputForm.getTargetCustomerId());
		if (registedCount >= CommonConstant.SEKKEN_ADD_LIMIT) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonAnkenValidator.existsAnken(inputForm.getTargetAnkenId())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonCustomerValidator.existsPerson(inputForm.getTargetCustomerId())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TAnkenSekkenEntity tAnkenSekkenEntity = new TAnkenSekkenEntity();
		tAnkenSekkenEntity.setAnkenId(inputForm.getTargetAnkenId());
		tAnkenSekkenEntity.setCustomerId(inputForm.getTargetCustomerId());
		tAnkenSekkenEntity.setSekkenStartAt(inputForm.getParsedSekkenStartAt());
		tAnkenSekkenEntity.setSekkenEndAt(inputForm.getParsedSekkenEndAt());
		tAnkenSekkenEntity.setPlace(inputForm.getPlace());
		tAnkenSekkenEntity.setRemarks(inputForm.getRemarks());

		try {
			// 登録処理
			tAnkenSekkenDao.insert(tAnkenSekkenEntity);
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件顧客-接見情報の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateAnkenCustomerSekken(AnkenEditKeijiInputForm.AnkenCustomerSekkenListInputForm inputForm) throws AppException {

		TAnkenSekkenEntity tAnkenSekkenEntity = tAnkenSekkenDao.selectBySeq(inputForm.getSekkenSeq());
		if (tAnkenSekkenEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		tAnkenSekkenEntity.setSekkenStartAt(inputForm.getParsedSekkenStartAt());
		tAnkenSekkenEntity.setSekkenEndAt(inputForm.getParsedSekkenEndAt());
		tAnkenSekkenEntity.setPlace(inputForm.getPlace());
		tAnkenSekkenEntity.setRemarks(inputForm.getRemarks());

		try {
			// 更新処理
			tAnkenSekkenDao.update(tAnkenSekkenEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件顧客-接見情報の削除処理
	 *
	 * @param sekkenSeq
	 * @throws AppException
	 */
	public void deleteAnkenCustomerSekken(Long sekkenSeq) throws AppException {

		TAnkenSekkenEntity tAnkenSekkenEntity = tAnkenSekkenDao.selectBySeq(sekkenSeq);
		if (tAnkenSekkenEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 削除処理
			tAnkenSekkenDao.delete(tAnkenSekkenEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件-顧客-在監情報の取得
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	public AnkenEditKeijiViewForm.AnkenCustomerZaikanListViewForm createAnkenCustomerZaikanListViewForm(Long ankenId, Long customerId) {

		AnkenEditKeijiViewForm.AnkenCustomerZaikanListViewForm viewForm = new AnkenEditKeijiViewForm.AnkenCustomerZaikanListViewForm();

		// 案件顧客を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 完了チェック
		viewForm.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		// DBから情報取得
		List<TAnkenKoryuEntity> tAnkenKoryuEntities = tAnkenKoryuDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		List<MSosakikanEntity> mSosakikanEntities = mSosakikanDao.selectAll();

		// 捜査機関名Mapを作成
		Map<Long, String> sosakikanNameMap = mSosakikanEntities.stream().collect(Collectors.toMap(MSosakikanEntity::getSosakikanId, MSosakikanEntity::getSosakikanName));

		// 在監情報
		List<AnkenCustomerZaikanDto> zaikanDtoList = new ArrayList<AnkenCustomerZaikanDto>();
		if (tAnkenKoryuEntities != null) {
			zaikanDtoList = tAnkenKoryuEntities.stream()
					.map(entity -> AnkenCustomerZaikanDto.builder()
							.koryuSeq(entity.getKoryuSeq())
							.ankenId(entity.getAnkenId())
							.customerId(entity.getCustomerId())
							.koryuDate(entity.getKoryuDate())
							.hoshakuDate(entity.getHoshakuDate())
							.sosakikanId(entity.getSosakikanId())
							.koryuPlaceName(entity.getSosakikanId() != null ? sosakikanNameMap.get(entity.getSosakikanId()) : entity.getKoryuPlaceName())
							.sosakikanTelNo(entity.getSosakikanTelNo())
							.remarks(entity.getRemarks())
							.build())
					.collect(Collectors.toList());
		}
		viewForm.setAnkenCustomerZaikanDtoList(zaikanDtoList);
		return viewForm;
	}

	/**
	 * 在監情報の入力用オブジェクトを作成する
	 *
	 * @param koryuSeq
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	public AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm createAnkenCustomerZaikanListInputForm(Long koryuSeq, Long ankenId, Long customerId) {

		// 初期設定済みの入力フォームを取得
		AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm = this.getInitializedAnkenCustomerZaikanListInputForm(koryuSeq);

		// 対象の案件と顧客
		inputForm.setTargetAnkenId(ankenId);
		inputForm.setTargetCustomerId(customerId);

		// 案件顧客を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 完了チェック
		inputForm.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		if (koryuSeq == null) {
			// 新規登録の場合

			// 特に情報の設定はしない

		} else {
			// 更新の場合

			// 案件顧客-在監情報を取得
			TAnkenKoryuEntity tAnkenKoryuEntity = tAnkenKoryuDao.selectBySeq(koryuSeq);

			// 在監情報
			inputForm.setKoryuSeq(tAnkenKoryuEntity.getKoryuSeq());
			inputForm.setTargetAnkenId(tAnkenKoryuEntity.getAnkenId());
			inputForm.setTargetCustomerId(tAnkenKoryuEntity.getCustomerId());
			inputForm.setZaikanDate(DateUtils.parseToString(tAnkenKoryuEntity.getKoryuDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			inputForm.setHoshakuDate(DateUtils.parseToString(tAnkenKoryuEntity.getHoshakuDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			inputForm.setSosakikanId(tAnkenKoryuEntity.getSosakikanId());
			if (tAnkenKoryuEntity.getSosakikanId() != null) {
				MSosakikanEntity sosakikanEntity = mSosakikanDao.selectById(tAnkenKoryuEntity.getSosakikanId());
				inputForm.setZaikanPlace(sosakikanEntity.getSosakikanName());
			} else {
				inputForm.setZaikanPlace(tAnkenKoryuEntity.getKoryuPlaceName());
			}
			inputForm.setTelNo(tAnkenKoryuEntity.getSosakikanTelNo());
			inputForm.setRemarks(tAnkenKoryuEntity.getRemarks());
		}

		return inputForm;
	}

	/**
	 * 案件顧客-在監入力フォームを取得
	 *
	 * @param koryuSeq
	 * @return
	 */
	private AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm getInitializedAnkenCustomerZaikanListInputForm(Long koryuSeq) {

		AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm = new AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm();

		this.setDisplayData(inputForm);

		return inputForm;
	}

	/**
	 * 入力フォームに表示用のデータを設定する。
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
	 */
	public void setDisplayData(AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm) {

		// 捜査機関選択肢を取得
		List<MSosakikanEntity> mSosakikanEntities = mSosakikanDao.selectEnabled();
		List<SosakikanSelectOptionDto> sosakikanSelectOptionDtoList = mSosakikanEntities.stream()
				.filter(e -> !SosakikanType.KENSATSUCHO.equalsByCode(e.getSosakikanType()))
				.map(e -> {
					SosakikanSelectOptionDto option = new SosakikanSelectOptionDto();
					option.setSosakikanId(e.getSosakikanId());
					option.setSosakikanName(e.getSosakikanName());
					option.setSosakikanTelNo(e.getSosakikanTelNo());
					option.setSosakikanFaxNo(e.getSosakikanFaxNo());
					return option;
				})
				.collect(Collectors.toList());

		// 捜査機関
		inputForm.setSosakikanSelectOptionDto(sosakikanSelectOptionDtoList);
	}

	/**
	 * 捜査機関情報の表示用オブジェクトを作成
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenEditKeijiViewForm.AnkenSosakikanListViewForm createAnkenSosakikanListViewForm(Long ankenId) {

		AnkenEditKeijiViewForm.AnkenSosakikanListViewForm viewForm = new AnkenEditKeijiViewForm.AnkenSosakikanListViewForm();

		// 案件に紐づく捜査機関を取得
		List<AnkenSosakikanBean> ankenSosakikanBeanList = tAnkenSosakikanDao.selectJoinMstByAnkenId(ankenId);

		// 画面表示用に加工
		List<AnkenSosakikanDto> ankenSosakikanDtoList = ankenSosakikanBeanList.stream()
				.map(bean -> AnkenSosakikanDto.builder()
						.sosakikanSeq(bean.getSosakikanSeq())
						.ankenId(bean.getAnkenId())
						.sosakikanId(bean.getSosakikanId())
						.sosakikanName(bean.getSosakikanName())
						.sosakikanTantoBu(bean.getSosakikanTantoBu())
						.sosakikanTelNo(bean.getSosakikanTelNo())
						.sosakikanExtensionNo(bean.getSosakikanExtensionNo())
						.sosakikanFaxNo(bean.getSosakikanFaxNo())
						.sosakikanRoomNo(bean.getSosakikanRoomNo())
						.tantosha1Name(bean.getTantosha1Name())
						.tantosha2Name(bean.getTantosha2Name())
						.remarks(bean.getRemarks())
						.build())
				.collect(Collectors.toList());
		viewForm.setAnkenSosakikanDtoList(ankenSosakikanDtoList);

		return viewForm;
	}

	/**
	 * 案件顧客-在監場所の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registAnkenCustomerZaikan(AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm) throws AppException {

		long registedCount = tAnkenKoryuDao.selectCountByAnkenIdAndCustomerId(inputForm.getTargetAnkenId(), inputForm.getTargetCustomerId());
		if (registedCount >= CommonConstant.ZAIKAN_ADD_LIMIT) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonAnkenValidator.existsAnken(inputForm.getTargetAnkenId())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonCustomerValidator.existsPerson(inputForm.getTargetCustomerId())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TAnkenKoryuEntity tAnkenKoryuEntity = new TAnkenKoryuEntity();
		tAnkenKoryuEntity.setAnkenId(inputForm.getTargetAnkenId());
		tAnkenKoryuEntity.setCustomerId(inputForm.getTargetCustomerId());
		tAnkenKoryuEntity.setKoryuDate(inputForm.getParsedZaikanDate());
		tAnkenKoryuEntity.setHoshakuDate(inputForm.getParsedHoshakuDate());
		tAnkenKoryuEntity.setSosakikanTelNo(inputForm.getTelNo());
		tAnkenKoryuEntity.setRemarks(inputForm.getRemarks());

		if (inputForm.getSosakikanId() != null) {
			tAnkenKoryuEntity.setSosakikanId(inputForm.getSosakikanId());
			tAnkenKoryuEntity.setKoryuPlaceName(null);
		} else {
			tAnkenKoryuEntity.setSosakikanId(null);
			tAnkenKoryuEntity.setKoryuPlaceName(inputForm.getZaikanPlace());
		}

		try {
			// 登録処理
			tAnkenKoryuDao.insert(tAnkenKoryuEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件顧客-在監場所の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateAnkenCustomerZaikan(AnkenEditKeijiInputForm.AnkenCustomerZaikanListInputForm inputForm) throws AppException {

		TAnkenKoryuEntity tAnkenKoryuEntity = tAnkenKoryuDao.selectBySeq(inputForm.getKoryuSeq());
		if (tAnkenKoryuEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		tAnkenKoryuEntity.setKoryuDate(inputForm.getParsedZaikanDate());
		tAnkenKoryuEntity.setHoshakuDate(inputForm.getParsedHoshakuDate());
		tAnkenKoryuEntity.setSosakikanTelNo(inputForm.getTelNo());
		tAnkenKoryuEntity.setRemarks(inputForm.getRemarks());

		if (inputForm.getSosakikanId() != null) {
			tAnkenKoryuEntity.setSosakikanId(inputForm.getSosakikanId());
			tAnkenKoryuEntity.setKoryuPlaceName(null);
		} else {
			tAnkenKoryuEntity.setSosakikanId(null);
			tAnkenKoryuEntity.setKoryuPlaceName(inputForm.getZaikanPlace());
		}

		try {
			// 更新処理
			tAnkenKoryuDao.update(tAnkenKoryuEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件顧客-在監場所の削除処理
	 * 
	 * @param koryuSeq
	 * @throws AppException
	 */
	public void deleteAnkenCustomerZaikan(Long koryuSeq) throws AppException {

		TAnkenKoryuEntity tAnkenKoryuEntity = tAnkenKoryuDao.selectBySeq(koryuSeq);
		if (tAnkenKoryuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 削除処理
			tAnkenKoryuDao.delete(tAnkenKoryuEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 捜査機関の入力フォームを取得する
	 * 
	 * @param ankenId
	 * @param sosakikanSeq
	 * @return
	 * @throws AppException
	 */
	public AnkenEditKeijiInputForm.AnkenSosakikanListInputForm createAnkenSosakikanListInputForm(Long ankenId, Long sosakikanSeq)
			throws AppException {

		AnkenEditKeijiInputForm.AnkenSosakikanListInputForm inputForm = this.getInitializedAnkenSosakikanListInputForm(ankenId);

		if (sosakikanSeq == null) {
			// 新規登録の場合

			// 特に情報の設定はしない

		} else {
			// 更新の場合

			// 案件に紐づく捜査機関を取得
			List<AnkenSosakikanBean> ankenSosakikanBeanList = tAnkenSosakikanDao.selectJoinMstByAnkenId(ankenId);
			AnkenSosakikanBean ankenSosakikanBean = ankenSosakikanBeanList.stream()
					.filter(bean -> sosakikanSeq.equals(bean.getSosakikanSeq()))
					.findFirst()
					.orElse(null);

			if (ankenSosakikanBean == null) {
				// 楽観ロックエラー
				logger.warn("データが他のユーザーに削除されたか、不正に書き換えられたSEQが送られてきたため一致する検索データが見つからなかった。");
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// 捜査機関データ設定
			inputForm.setSosakikanSeq(sosakikanSeq);
			inputForm.setAnkenId(ankenSosakikanBean.getAnkenId());
			inputForm.setSosakikanId(ankenSosakikanBean.getSosakikanId());
			inputForm.setSosakikanName(ankenSosakikanBean.getSosakikanName());
			inputForm.setSosakikanTantoBu(ankenSosakikanBean.getSosakikanTantoBu());
			inputForm.setSosakikanTelNo(ankenSosakikanBean.getSosakikanTelNo());
			inputForm.setSosakikanExtensionNo(ankenSosakikanBean.getSosakikanExtensionNo());
			inputForm.setSosakikanFaxNo(ankenSosakikanBean.getSosakikanFaxNo());
			inputForm.setSosakikanRoomNo(ankenSosakikanBean.getSosakikanRoomNo());
			inputForm.setTantosha1Name(ankenSosakikanBean.getTantosha1Name());
			inputForm.setTantosha2Name(ankenSosakikanBean.getTantosha2Name());
			inputForm.setRemarks(ankenSosakikanBean.getRemarks());

		}

		return inputForm;
	}

	/**
	 * 初期設定済みの捜査機関入力フォームを取得
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenEditKeijiInputForm.AnkenSosakikanListInputForm getInitializedAnkenSosakikanListInputForm(Long ankenId) {

		AnkenEditKeijiInputForm.AnkenSosakikanListInputForm inputForm = new AnkenEditKeijiInputForm.AnkenSosakikanListInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm);

		return inputForm;
	}

	/**
	 * 入力フォームに表示用のデータを設定する。
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
	 */
	public void setDisplayData(AnkenEditKeijiInputForm.AnkenSosakikanListInputForm inputForm) {

		// 捜査機関選択肢を取得
		List<MSosakikanEntity> mSosakikanEntities = mSosakikanDao.selectEnabled();
		List<SosakikanSelectOptionDto> sosakikanSelectOptionDtoList = mSosakikanEntities.stream()
				.filter(e -> !SosakikanType.KOUTISHO.equalsByCode(e.getSosakikanType()))
				.map(e -> {
					SosakikanSelectOptionDto option = new SosakikanSelectOptionDto();
					option.setSosakikanId(e.getSosakikanId());
					option.setSosakikanName(e.getSosakikanName());
					option.setSosakikanTelNo(e.getSosakikanTelNo());
					option.setSosakikanFaxNo(e.getSosakikanFaxNo());
					return option;
				})
				.collect(Collectors.toList());

		// 捜査機関
		inputForm.setSosakikanSelectOptionDto(sosakikanSelectOptionDtoList);

	}

	/**
	 * 捜査機関の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registSosakikan(AnkenEditKeijiInputForm.AnkenSosakikanListInputForm inputForm) throws AppException {

		long registedCount = tAnkenSosakikanDao.selectCountByAnkenId(inputForm.getAnkenId());
		if (registedCount >= CommonConstant.KEIJI_SOSAKIKAN_ADD_LIMIT) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonAnkenValidator.existsAnken(inputForm.getAnkenId())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonAnkenValidator.isSosakikanExistsValid(inputForm.getSosakikanId())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TAnkenSosakikanEntity tAnkenSosakikanEntity = new TAnkenSosakikanEntity();
		tAnkenSosakikanEntity.setAnkenId(inputForm.getAnkenId());
		tAnkenSosakikanEntity.setSosakikanTantoBu(inputForm.getSosakikanTantoBu());
		tAnkenSosakikanEntity.setSosakikanTelNo(inputForm.getSosakikanTelNo());
		tAnkenSosakikanEntity.setSosakikanExtensionNo(inputForm.getSosakikanExtensionNo());
		tAnkenSosakikanEntity.setSosakikanFaxNo(inputForm.getSosakikanFaxNo());
		tAnkenSosakikanEntity.setSosakikanRoomNo(inputForm.getSosakikanRoomNo());
		tAnkenSosakikanEntity.setTantosha1Name(inputForm.getTantosha1Name());
		tAnkenSosakikanEntity.setTantosha2Name(inputForm.getTantosha2Name());
		tAnkenSosakikanEntity.setRemarks(inputForm.getRemarks());

		if (inputForm.getSosakikanId() != null) {
			// マスタに登録されている検察、警察を選択した場合
			tAnkenSosakikanEntity.setSosakikanId(inputForm.getSosakikanId());
			tAnkenSosakikanEntity.setSosakikanName(null);

		} else {
			// 自由入力されている場合
			tAnkenSosakikanEntity.setSosakikanName(inputForm.getSosakikanName());
			tAnkenSosakikanEntity.setSosakikanId(null);
		}

		try {
			// 案件捜査機関情報の登録
			tAnkenSosakikanDao.insert(tAnkenSosakikanEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 捜査機関の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateSosakikan(AnkenEditKeijiInputForm.AnkenSosakikanListInputForm inputForm) throws AppException {

		TAnkenSosakikanEntity tAnkenSosakikanEntity = tAnkenSosakikanDao.selectBySosakikanSeq(inputForm.getSosakikanSeq());
		if (tAnkenSosakikanEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		if (!commonAnkenValidator.isSosakikanExistsValid(inputForm.getSosakikanId())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新
		tAnkenSosakikanEntity.setSosakikanTantoBu(inputForm.getSosakikanTantoBu());
		tAnkenSosakikanEntity.setSosakikanTelNo(inputForm.getSosakikanTelNo());
		tAnkenSosakikanEntity.setSosakikanExtensionNo(inputForm.getSosakikanExtensionNo());
		tAnkenSosakikanEntity.setSosakikanFaxNo(inputForm.getSosakikanFaxNo());
		tAnkenSosakikanEntity.setSosakikanRoomNo(inputForm.getSosakikanRoomNo());
		tAnkenSosakikanEntity.setTantosha1Name(inputForm.getTantosha1Name());
		tAnkenSosakikanEntity.setTantosha2Name(inputForm.getTantosha2Name());
		tAnkenSosakikanEntity.setRemarks(inputForm.getRemarks());

		if (inputForm.getSosakikanId() != null) {
			// マスタに登録されている検察、警察を選択した場合
			tAnkenSosakikanEntity.setSosakikanId(inputForm.getSosakikanId());
			tAnkenSosakikanEntity.setSosakikanName(null);

		} else {
			// 自由入力されている場合
			tAnkenSosakikanEntity.setSosakikanName(inputForm.getSosakikanName());
			tAnkenSosakikanEntity.setSosakikanId(null);
		}

		try {
			// 案件捜査機関情報の登録
			tAnkenSosakikanDao.update(tAnkenSosakikanEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 捜査機関の削除処理
	 * 
	 * @param sosakikanSeq
	 * @throws AppException
	 */
	public void deleteSosakikan(Long sosakikanSeq) throws AppException {

		TAnkenSosakikanEntity tAnkenSosakikanEntity = tAnkenSosakikanDao.selectBySosakikanSeq(sosakikanSeq);
		if (tAnkenSosakikanEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 案件捜査機関情報の削除
			tAnkenSosakikanDao.delete(tAnkenSosakikanEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 共犯者（関与者）情報の表示用オブジェクトを作成
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenEditKeijiViewForm.AnkenKyohanshaListViewForm createAnkenKyohanshaListViewForm(Long ankenId) {

		AnkenEditKeijiViewForm.AnkenKyohanshaListViewForm viewForm = new AnkenEditKeijiViewForm.AnkenKyohanshaListViewForm();

		// 関与者（共犯者）を取得
		List<AnkenRelatedKanyoshaBean> ankenRelatedKanyoshaBeanList = commonAnkenService.getAnkenRelatedKanyoshaBeanList(ankenId, KanyoshaType.KYOHANSHA, SystemFlg.FLG_OFF);

		// 関与者（共犯者代理人）を取得
		List<AnkenRelatedKanyoshaBean> ankenRelatedKanyoshaDairininBeanList = commonAnkenService.getAnkenRelatedKanyoshaBeanList(ankenId, KanyoshaType.KYOHANSHA, SystemFlg.FLG_ON);

		// 画面表示用に加工
		List<AnkenKanyoshaViewDto> ankenKanyoshaViewDtoList = commonAnkenService.generateKanyoshaViewDto(ankenRelatedKanyoshaBeanList, ankenRelatedKanyoshaDairininBeanList);
		viewForm.setAnkenKanyoshaViewDtoList(ankenKanyoshaViewDtoList);

		return viewForm;
	}

	/**
	 * 共犯者の削除処理
	 * 
	 * @param ankenId
	 * @param aitegataKanyoshaSeq
	 * @throws AppException
	 */
	public void deleteKyohansha(Long ankenId, Long kyohanshaKanyoshaSeq) throws AppException {

		// 削除データを取得
		TAnkenRelatedKanyoshaEntity kyohanshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kyohanshaKanyoshaSeq);

		// 削除データの存在チェック
		if (kyohanshaEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 紐付けされている関与者
		Long relatedKanyosha = kyohanshaEntity.getRelatedKanyoshaSeq();

		boolean needDelete;
		if (relatedKanyosha == null) {
			needDelete = false;
		} else {
			// 案件に紐づくデータを取得
			List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(ankenId);

			// 紐付けを解除する弁護人が、どの相手方にも紐付かなくなった場合は削除が必要
			long count = tAnkenRelatedKanyoshaEntities.stream()
					.filter(e -> {
						return Objects.equals(relatedKanyosha, e.getRelatedKanyoshaSeq())
								&& !Objects.equals(kyohanshaKanyoshaSeq, e.getKanyoshaSeq());
					}).count();
			needDelete = count == 0;
		}

		try {

			// 共犯者の削除処理
			tAnkenRelatedKanyoshaDao.delete(kyohanshaEntity);

			// 共犯者 代理人の削除処理
			if (needDelete) {
				TAnkenRelatedKanyoshaEntity dairininEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kyohanshaEntity.getRelatedKanyoshaSeq());
				tAnkenRelatedKanyoshaDao.delete(dairininEntity);
			}

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 共犯者-弁護人の削除処理
	 * 
	 * @param ankenId
	 * @param kyohanshaKanyoshaSeq
	 * @param bengoninKanyoshaSeq
	 * @throws AppException
	 */
	public void deleteKyohanshaBengonin(Long ankenId, Long kyohanshaKanyoshaSeq, Long bengoninKanyoshaSeq) throws AppException {

		TAnkenRelatedKanyoshaEntity kyohanshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kyohanshaKanyoshaSeq);

		// データの存在チェック
		if (kyohanshaEntity == null || !Objects.equals(kyohanshaEntity.getRelatedKanyoshaSeq(), bengoninKanyoshaSeq)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		kyohanshaEntity.setRelatedKanyoshaSeq(null);

		// 案件に紐づくデータを取得
		List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(ankenId);

		// 紐付けを解除する弁護人が、どの相手方にも紐付かなくなった場合は削除が必要
		long count = tAnkenRelatedKanyoshaEntities.stream()
				.filter(e -> {
					return Objects.equals(bengoninKanyoshaSeq, e.getRelatedKanyoshaSeq())
							&& !Objects.equals(kyohanshaKanyoshaSeq, e.getKanyoshaSeq());
				}).count();

		boolean needDelete = count == 0;

		try {

			// 登録処理
			tAnkenRelatedKanyoshaDao.update(kyohanshaEntity);

			if (needDelete) {
				// 削除処理
				TAnkenRelatedKanyoshaEntity dairininEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, bengoninKanyoshaSeq);
				tAnkenRelatedKanyoshaDao.delete(dairininEntity);
			}

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 被害者（関与者）情報の表示用オブジェクトを作成
	 * 
	 * 名簿化対応 @author hoshima
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenEditKeijiViewForm.AnkenHigaishaListViewForm createAnkenHigaishaListViewForm(Long ankenId) {

		AnkenEditKeijiViewForm.AnkenHigaishaListViewForm viewForm = new AnkenEditKeijiViewForm.AnkenHigaishaListViewForm();

		// 関与者(被害者)を取得
		List<AnkenRelatedKanyoshaBean> ankenRelatedKanyoshaBeanList = commonAnkenService.getAnkenRelatedKanyoshaBeanList(ankenId, KanyoshaType.HIGAISHA, SystemFlg.FLG_OFF);

		// 関与者(被害者代理人)を取得
		List<AnkenRelatedKanyoshaBean> ankenRelatedKanyoshaDairininBeanList = commonAnkenService.getAnkenRelatedKanyoshaBeanList(ankenId, KanyoshaType.HIGAISHA, SystemFlg.FLG_ON);

		// 画面表示用に加工
		List<AnkenKanyoshaViewDto> ankenKanyoshaViewDtoList = commonAnkenService.generateKanyoshaViewDto(ankenRelatedKanyoshaBeanList, ankenRelatedKanyoshaDairininBeanList);
		viewForm.setAnkenKanyoshaViewDtoList(ankenKanyoshaViewDtoList);

		return viewForm;
	}

	/**
	 * 被害者の削除処理
	 * 
	 * @param ankenId
	 * @param higaishaKanyoshaSeq
	 * @throws AppException
	 */
	public void deleteHigaisha(Long ankenId, Long higaishaKanyoshaSeq) throws AppException {

		// 削除データを取得
		TAnkenRelatedKanyoshaEntity higaishaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, higaishaKanyoshaSeq);

		// データの存在チェック
		if (higaishaEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 紐付けされている関与者
		Long relatedKanyosha = higaishaEntity.getRelatedKanyoshaSeq();

		boolean needDelete;
		if (relatedKanyosha == null) {
			needDelete = false;
		} else {
			// 案件に紐づくデータを取得
			List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(ankenId);

			// 紐付けを解除する代理人が、どの被害者にも紐付かなくなった場合は削除が必要
			long count = tAnkenRelatedKanyoshaEntities.stream()
					.filter(e -> {
						return Objects.equals(relatedKanyosha, e.getRelatedKanyoshaSeq())
								&& !Objects.equals(higaishaKanyoshaSeq, e.getKanyoshaSeq());
					}).count();
			needDelete = count == 0;
		}

		try {
			// 被害者の削除処理
			tAnkenRelatedKanyoshaDao.delete(higaishaEntity);
			// 被害者代理人の削除処理
			if (needDelete) {
				TAnkenRelatedKanyoshaEntity dairininEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, higaishaEntity.getRelatedKanyoshaSeq());
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
	 * 被害者代理人の削除処理
	 * 
	 * @param ankenId
	 * @param higaishaKanyoshaSeq
	 * @param dairininKanyoshaSeq
	 * @throws AppException
	 */
	public void deleteHigaishaDairinin(Long ankenId, Long higaishaKanyoshaSeq, Long dairininKanyoshaSeq) throws AppException {

		TAnkenRelatedKanyoshaEntity higaishaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, higaishaKanyoshaSeq);

		// データの存在チェック
		if (higaishaEntity == null || !Objects.equals(higaishaEntity.getRelatedKanyoshaSeq(), dairininKanyoshaSeq)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		higaishaEntity.setRelatedKanyoshaSeq(null);

		// 案件に紐づくデータを取得
		List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(ankenId);

		// 紐付けを解除する代理人が、どの被害者にも紐付かなくなった場合は削除が必要
		long count = tAnkenRelatedKanyoshaEntities.stream()
				.filter(e -> {
					return Objects.equals(dairininKanyoshaSeq, e.getRelatedKanyoshaSeq())
							&& !Objects.equals(higaishaKanyoshaSeq, e.getKanyoshaSeq());
				}).count();

		boolean needDelete = count == 0;

		try {
			// 登録処理
			tAnkenRelatedKanyoshaDao.update(higaishaEntity);
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
	 * 売上帰属に設定できるユーザーが1ユーザーしかいない場合は、デフォルトで売上計上先にユーザーをセットした状態にする。<br>
	 * 
	 * @param ankenBasicInputForm
	 * @return
	 */
	public AnkenEditKeijiInputForm.AnkenBasicInputForm setDefaultSalesOwner(AnkenEditKeijiInputForm.AnkenBasicInputForm ankenBasicInputForm) {

		// 案件担当取得
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = tAnkenTantoDao.selectByIdForAccount(ankenBasicInputForm.getAnkenId());
		// アカウント取得
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();
		// 売上計上先
		List<SelectOptionForm> salesOwnerOptionList = commonAnkenService.getTantoOptionList(accountEntityList,
				TantoType.SALES_OWNER, ankenTantoAccountBeanList);

		if (salesOwnerOptionList.size() == 1) {
			AnkenTantoSelectInputForm tanto = new AnkenTantoSelectInputForm(salesOwnerOptionList.get(0).getValueAsLong(), false);
			ankenBasicInputForm.setSalesOwner(List.of(tanto));
		}
		return ankenBasicInputForm;
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

	// =========================================================================
	// private メソッド
	// =========================================================================

	// 共通系の処理

	/**
	 * 案件刑事情報を取得
	 * 
	 * @param ankenId
	 * @return
	 */
	private AnkenAddKeijiBean getAnkenKeiji(Long ankenId) {

		AnkenAddKeijiBean bean = tAnkenDao.selectAddKeijiById(ankenId);
		// 案件情報が存在しない場合
		if (bean == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}

		return bean;
	}

	/**
	 * 案件情報の更新
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 */
	private void updateAnken(Long ankenId, AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm) {

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

	/**
	 * 案件-刑事情報の更新
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 */
	private void updateAnkenAddKeiji(Long ankenId, AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm) {

		TAnkenAddKeijiEntity entity = tAnkenAddKeijiDao.selectByAnkenId(ankenId);

		// 案件-刑事情報が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("案件-刑事情報が存在しません。[ankenId=" + ankenId + "]");
		}

		entity.setLawyerSelectType(basicInputForm.getLawyerSelectType());

		tAnkenAddKeijiDao.update(entity);
	}

	/**
	 * 案件-担当情報の更新
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 */
	private void updateAnkenTanto(Long ankenId, AnkenEditKeijiInputForm.AnkenBasicInputForm basicInputForm) {

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
	 * @param tAnkenJikenEntityList
	 * @param TAnkenSekkenEntityList
	 * @param tAnkenKoryuEntityList
	 * @return
	 */
	private AnkenCustomerDto convert2AnkenCustomer(
			TAnkenCustomerEntity tAnkenCustomerEntity,
			TPersonEntity tPersonEntity,
			TKeijiAnkenCustomerEntity tKeijiAnkenCustomerEntity,
			List<TAnkenJikenEntity> tAnkenJikenEntityList,
			List<TAnkenSekkenEntity> tAnkenSekkenEntityList,
			List<TAnkenKoryuEntity> tAnkenKoryuEntityList,
			Map<Long, String> sosakikanNameMap) {

		AnkenCustomerDto ankenCustomer = new AnkenCustomerDto();

		// 顧客情報
		ankenCustomer.setPersonId(tPersonEntity.getPersonId());
		ankenCustomer.setCustomerId(CustomerId.of(tPersonEntity.getCustomerId()));
		ankenCustomer.setCustomerName(PersonName.fromEntity(tPersonEntity).getName());
		ankenCustomer.setPersonAttribute(PersonAttribute.of(tPersonEntity.getCustomerFlg(), tPersonEntity.getAdvisorFlg(), tPersonEntity.getCustomerType()));
		ankenCustomer.setCustomerType(CustomerType.of(tPersonEntity.getCustomerType()));

		// 案件顧客情報
		ankenCustomer.setAnkenId(AnkenId.of(tAnkenCustomerEntity.getAnkenId()));
		ankenCustomer.setStatus(AnkenStatus.of(tAnkenCustomerEntity.getAnkenStatus()));
		ankenCustomer.setJuninDate(DateUtils.parseToString(tAnkenCustomerEntity.getJuninDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		ankenCustomer.setJikenshoriKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getJikenKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		ankenCustomer.setSeisanKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		ankenCustomer.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		// 刑事案件顧客情報
		if (tKeijiAnkenCustomerEntity != null) {
			ankenCustomer.setKoryuStatus(KoryuStatus.of(tKeijiAnkenCustomerEntity.getKoryuStatus()));
		}

		// 事件情報
		List<AnkenCustomerJikenDto> jikenDtoList = null;
		if (tAnkenJikenEntityList != null) {
			jikenDtoList = tAnkenJikenEntityList.stream()
					.map(entity -> AnkenCustomerJikenDto.builder()
							.jikenSeq(entity.getJikenSeq())
							.ankenId(entity.getAnkenId())
							.customerId(entity.getCustomerId())
							.jikenName(entity.getJikenName())
							.taihoDate(DateUtils.parseToString(entity.getTaihoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.koryuSeikyuDate(DateUtils.parseToString(entity.getKoryuSeikyuDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.koryuExpirationDate(DateUtils.parseToString(entity.getKoryuExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.koryuExtendedExpirationDate(DateUtils.parseToString(entity.getKoryuExtendedExpirationDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.shobunType(entity.getShobunType())
							.shobunDate(DateUtils.parseToString(entity.getShobunDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
							.remarks(entity.getRemarks())
							.build())
					.collect(Collectors.toList());
		}
		ankenCustomer.setAnkenCustomerJikenDtoList(jikenDtoList);

		// 接見情報
		List<AnkenCustomerSekkenDto> sekkenDtoList = new ArrayList<AnkenCustomerSekkenDto>();
		if (tAnkenSekkenEntityList != null) {
			sekkenDtoList = tAnkenSekkenEntityList.stream()
					.map(entity -> AnkenCustomerSekkenDto.builder()
							.sekkenSeq(entity.getSekkenSeq())
							.ankenId(entity.getAnkenId())
							.customerId(entity.getCustomerId())
							.sekkenStartAt(DateUtils.parseToString(entity.getSekkenStartAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM))
							.sekkenEndAt(DateUtils.parseToString(entity.getSekkenEndAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM))
							.place(entity.getPlace())
							.remarks(entity.getRemarks())
							.build())
					.collect(Collectors.toList());
		}
		ankenCustomer.setAnkenCustomerSekkenDtoList(sekkenDtoList);

		// 在監情報
		List<AnkenCustomerZaikanDto> zaikanDtoList = new ArrayList<AnkenCustomerZaikanDto>();
		if (tAnkenKoryuEntityList != null) {
			zaikanDtoList = tAnkenKoryuEntityList.stream()
					.map(entity -> AnkenCustomerZaikanDto.builder()
							.koryuSeq(entity.getKoryuSeq())
							.ankenId(entity.getAnkenId())
							.customerId(entity.getCustomerId())
							.koryuDate(entity.getKoryuDate())
							.hoshakuDate(entity.getHoshakuDate())
							.sosakikanId(entity.getSosakikanId())
							.koryuPlaceName(entity.getSosakikanId() != null ? sosakikanNameMap.get(entity.getSosakikanId()) : entity.getKoryuPlaceName())
							.sosakikanTelNo(entity.getSosakikanTelNo())
							.remarks(entity.getRemarks())
							.build())
					.collect(Collectors.toList());
		}
		ankenCustomer.setAnkenCustomerZaikanDtoList(zaikanDtoList);

		return ankenCustomer;
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
