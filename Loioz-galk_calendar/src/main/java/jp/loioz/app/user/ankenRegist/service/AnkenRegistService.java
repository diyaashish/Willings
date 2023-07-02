package jp.loioz.app.user.ankenRegist.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonFileManagementService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.app.user.ankenRegist.dto.SearchedCustomerDto;
import jp.loioz.app.user.ankenRegist.form.AnkenRegistInputForm;
import jp.loioz.app.user.ankenRegist.form.AnkenRegistInputForm.Customer;
import jp.loioz.bean.AnkenTantoAccountBean;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.RegistCustomerType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenAddKeijiDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TPersonAddHojinDao;
import jp.loioz.dao.TPersonAddKojinDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAnkenAddKeijiEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TPersonAddHojinEntity;
import jp.loioz.entity.TPersonAddKojinEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 案件新規登録画面のserviceクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenRegistService extends DefaultService {

	/** 検索処理で表示する顧客数上限 */
	public static final int SEARCH_CUSTOMER_LIMIT_COUNT = 20;

	@Autowired
	private CommonFileManagementService commonFileManagementService;

	@Autowired
	private CommonAnkenService commonAnkenService;

	@Autowired
	private CommonBunyaService commonBunyaService;

	@Autowired
	private CommonPersonService commonCustomerService;

	@Autowired
	private MAccountDao mAccountDao;

	@Autowired
	private TPersonDao tPersonDao;

	@Autowired
	private TPersonAddKojinDao tPersonAddKojinDao;

	@Autowired
	private TPersonAddHojinDao tPersonAddHojinDao;

	@Autowired
	private TAnkenDao tAnkenDao;

	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private TAnkenAddKeijiDao tAnkenAddKeijiDao;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 顧客登録の入力フォームを作成する
	 * 
	 * @return
	 */
	public AnkenRegistInputForm.AnkenRegistBasicInputForm createNewAnkenBasicInputForm() {

		AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm = new AnkenRegistInputForm.AnkenRegistBasicInputForm();

		// デフォルト値を設定
		// 事務所案件
		inputForm.setAnkenType(AnkenType.JIMUSHO);
		// 売上計上先
		this.setDefaultSalesOwner(inputForm);

		// 新規追加
		inputForm.setRegistCustomerType(RegistCustomerType.NEW);
		// 顧客-個人
		inputForm.setCustomerType(CustomerType.KOJIN);

		// 表示情報設定
		this.setDisplayData(inputForm, inputForm.getAnkenType());

		// 検索条件の分野選択肢一覧を取得
		List<BunyaDto> bunyaList = commonBunyaService.getBunyaList(SystemFlg.codeToBoolean(SystemFlg.FLG_OFF.getCd()));
		inputForm.setBunyaList(bunyaList);

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
	 * @param ankenType
	 */
	public void setDisplayData(AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm, AnkenType ankenType) {

		// 受任区分
		List<SelectOptionForm> ankenTypeOptionList = AnkenType.getSelectOptions();
		inputForm.setAnkenTypeOptions(ankenTypeOptionList);
		
		// 案件担当（案件未登録のため空）
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = Collections.emptyList();
		
		// アカウント取得
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();
		
		// 売上計上先プルダウン
		inputForm.setSalesOwnerOptions(commonAnkenService.getSalesOwnerOptionList(ankenType, ankenTantoAccountBeanList, accountEntityList));
		
		// 担当弁護士プルダウン
		inputForm.setTantoLawyerOptions(commonAnkenService.getLawyerOptionList(ankenTantoAccountBeanList, accountEntityList));
		
		// 担当事務プルダウン
		inputForm.setTantoJimuOptions(commonAnkenService.getTantoJimuOptionList(ankenTantoAccountBeanList, accountEntityList));
		
		// 案件登録日
		inputForm.setAnkenCreatedDate(LocalDate.now());
	}

	/**
	 * 入力フォームの【個人】顧客情報を顧客情報リストにセットします
	 * 
	 * @param inputForm
	 */
	public void setKojinCustomer(AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm) {

		// 案件に紐づける顧客情報リスト
		List<Customer> customerList;
		if (LoiozCollectionUtils.isEmpty(inputForm.getCustomerList())) {
			customerList = new ArrayList<>();
		} else {
			customerList = inputForm.getCustomerList();
		}

		// 顧客情報保持クラス
		AnkenRegistInputForm.Customer newCustomer = new AnkenRegistInputForm.Customer();

		// 顧客情報セット
		setCustomerForInputForm(newCustomer, inputForm);

		customerList.add(newCustomer);
		inputForm.setCustomerList(customerList);
	}

	/**
	 * 入力フォームの【法人】顧客情報を顧客情報リストにセットします
	 * 
	 * @param inputForm
	 */
	public void setHojinCustomer(AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm) {

		// 案件に紐づける顧客情報リスト
		List<Customer> customerList;
		if (LoiozCollectionUtils.isEmpty(inputForm.getCustomerList())) {
			customerList = new ArrayList<>();
		} else {
			customerList = inputForm.getCustomerList();
		}

		// 顧客情報保持クラス
		AnkenRegistInputForm.Customer newCustomer = new AnkenRegistInputForm.Customer();

		// 顧客情報セット
		setCustomerForInputForm(newCustomer, inputForm);

		customerList.add(newCustomer);
		inputForm.setCustomerList(customerList);
	}

	/**
	 * 顧客情報にパラメータの登録済み顧客をセットします
	 * 
	 * @param inputForm
	 * @param customerId
	 */
	public void setCustomer(AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm, Long customerId) throws AppException {

		// 案件に紐づける顧客情報リスト
		List<Customer> customerList;
		if (LoiozCollectionUtils.isEmpty(inputForm.getCustomerList())) {
			customerList = new ArrayList<>();
		} else {
			customerList = inputForm.getCustomerList();
		}

		// 顧客情報取得
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(customerId);
		if (personEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 顧客情報保持クラス
		AnkenRegistInputForm.Customer newCustomer = new AnkenRegistInputForm.Customer();

		// 顧客情報セット
		setCustomerForEntity(newCustomer, personEntity);

		customerList.add(newCustomer);
		inputForm.setCustomerList(customerList);
	}

	/**
	 * 選択顧客からdeleteIndexの顧客を外します
	 * 
	 * @param inputForm
	 */
	public void deleteCustomer(AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm, int deleteIndex) {

		// 案件に紐づける顧客情報リスト
		List<Customer> customerList = inputForm.getCustomerList();

		// 顧客情報が無ければ処理終了
		if (LoiozCollectionUtils.isEmpty(customerList) || deleteIndex >= customerList.size()) {
			return;
		}

		// 顧客情報削除
		customerList.remove(deleteIndex);

		// 顧客リストをFormにセットする（0件であればnull）
		if (LoiozCollectionUtils.isEmpty(customerList)) {
			inputForm.setCustomerList(null);
		} else {
			inputForm.setCustomerList(customerList);
		}
	}

	/**
	 * 案件・顧客情報を登録します<br>
	 * 登録した案件IDを返します
	 * 
	 * @param basicInputForm
	 * @return
	 * @throws AppException
	 */
	public Long registAnkenCustomerBasic(AnkenRegistInputForm.AnkenRegistBasicInputForm basicInputForm) throws AppException {

		// 案件に登録する顧客の妥当性チェック
		MessageEnum errorMessageEnum = this.validAnkenRegistCustomer(basicInputForm.getCustomerList());
		if (errorMessageEnum != null) {
			throw new AppException(errorMessageEnum, null);
		}

		// 顧客情報を登録
		this.registCustomer(basicInputForm);

		// 案件の登録
		Long ankenId = this.registAnken(basicInputForm);

		// 案件刑事の登録
		this.registAnkenAddKeiji(ankenId, basicInputForm);

		// 案件-担当を更新
		this.registAnkenTanto(ankenId, basicInputForm);

		// 顧客の紐づけ
		this.registAnkenCustomer(ankenId, basicInputForm);

		return ankenId;
	}

	/**
	 * 検索ワードに該当する顧客情報を取得します<br>
	 * 選択済み顧客（selectedCustomerList）は対象外にします
	 * 
	 * @param searchWord
	 * @param selectedCustomerList
	 */
	public List<SearchedCustomerDto> getCustomer(String searchWord, List<Customer> selectedCustomerList) {

		// 検索結果格納顧客リスト
		List<SearchedCustomerDto> customerList = new ArrayList<>();

		// 選択済み顧客のIDのみリスト
		List<Long> selectedCustomerIdList = new ArrayList<>();
		if (LoiozCollectionUtils.isNotEmpty(selectedCustomerList)) {
			selectedCustomerIdList = selectedCustomerList.stream().filter(customer -> customer.getCustomerId() != null).map(customer -> customer.getCustomerId()).collect(Collectors.toList());
		}

		// 検索処理
		List<TPersonEntity> entityList = tPersonDao.selectByNameExclusionId(List.of(), StringUtils.removeSpaceCharacter(searchWord), SEARCH_CUSTOMER_LIMIT_COUNT);
		// 既に選択済みの顧客は除外する
		for (TPersonEntity entity : entityList) {
			// 選択済みでない顧客をセットする
			if (!selectedCustomerIdList.contains(entity.getCustomerId())) {
				SearchedCustomerDto customer = new SearchedCustomerDto();
				customer.setCustomerId(entity.getCustomerId());
				customer.setCustomerIdDisp(CustomerId.of(entity.getCustomerId()).toString());
				if (CustomerType.KOJIN.equalsByCode(entity.getCustomerType())) {
					customer.setCustomerName(entity.getCustomerNameSei() + " " + StringUtils.null2blank(entity.getCustomerNameMei()));
				} else {
					customer.setCustomerName(entity.getCustomerNameSei());
				}

				customerList.add(customer);
			}
		}
		return customerList;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 売上帰属に設定できるユーザーが1ユーザーしかいない場合は、デフォルトで売上計上先にユーザーをセットした状態にする。<br>
	 * 
	 * @param ankenRegistBasicInputForm
	 * @return
	 */
	public AnkenRegistInputForm.AnkenRegistBasicInputForm setDefaultSalesOwner(
			AnkenRegistInputForm.AnkenRegistBasicInputForm ankenRegistBasicInputForm) {

		// 案件担当（案件未登録のため空）
		List<AnkenTantoAccountBean> ankenTantoAccountBeanList = Collections.emptyList();
		// アカウント取得
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();
		// 売上計上先
		List<SelectOptionForm> salesOwnerOptionList = commonAnkenService.getTantoOptionList(accountEntityList,
				TantoType.SALES_OWNER, ankenTantoAccountBeanList);

		if (salesOwnerOptionList.size() == 1) {
			AnkenTantoSelectInputForm tanto = new AnkenTantoSelectInputForm(salesOwnerOptionList.get(0).getValueAsLong(), false);
			ankenRegistBasicInputForm.setSalesOwner(Arrays.asList(tanto));
		}
		return ankenRegistBasicInputForm;
	}

	/**
	 * 案件に登録する顧客の妥当性を検証する
	 * 
	 * @return 検証NGの場合はエラーメッセージ情報を返却。検証OKの場合はNULLを返却。
	 */
	private MessageEnum validAnkenRegistCustomer(List<Customer> registCustomerList) {

		// 顧客情報が無い場合は処理終了
		if (LoiozCollectionUtils.isEmpty(registCustomerList)) {
			return null;
		}

		// 顧客IDを取得（顧客IDがNULLのもの（既存顧客検索で追加したものではなく、新規で追加した顧客）は除外）
		List<Long> registCustomerIdList = registCustomerList.stream()
				.filter(e -> e.getCustomerId() != null)
				.map(e -> e.getCustomerId())
				.collect(Collectors.toList());

		// 登録顧客のIDがない場合は処理終了
		if (LoiozCollectionUtils.isEmpty(registCustomerIdList)) {
			return null;
		}

		List<TPersonEntity> personEntity = tPersonDao.selectPersonByPersonId(registCustomerIdList);
		if (personEntity.size() != registCustomerIdList.size()) {
			// 存在しない顧客のIDが含まれている場合（削除された場合など）->楽観ロックエラー
			return MessageEnum.MSG_E00025;
		}

		Optional<TPersonEntity> lawyerCustomerOpt = personEntity.stream()
				.filter(e -> CustomerType.LAWYER.getCd().equals(e.getCustomerType()))
				.findAny();
		if (lawyerCustomerOpt.isPresent()) {
			// 対象顧客の中に、顧客としての登録が不可能な「弁護士」タイプの顧客が存在する場合->楽観ロックエラー（既存名簿の検索の時点でも除外している）
			return MessageEnum.MSG_E00025;
		}

		return null;
	}

	/**
	 * 顧客情報を登録します<br>
	 * 登録後の顧客IDをbasicInputForm の customerList にセットします
	 * 
	 * @param basicInputForm
	 * @return
	 * @throws AppException
	 */
	private void registCustomer(AnkenRegistInputForm.AnkenRegistBasicInputForm basicInputForm) throws AppException {

		// 顧客情報取得
		List<Customer> customerList = basicInputForm.getCustomerList();

		// 顧客情報が無い場合は処理終了
		if (LoiozCollectionUtils.isEmpty(customerList)) {
			return;
		}

		// 新規登録の顧客情報を登録する
		for (Customer customer : customerList) {
			if (RegistCustomerType.NEW.equals(customer.getRegistCustomerType())) {

				TPersonEntity insertEntity = new TPersonEntity();

				// 種別（個人／法人）、登録日
				insertEntity.setCustomerType(DefaultEnum.getCd(customer.getCustomerType()));
				insertEntity.setCustomerCreatedDate(basicInputForm.getAnkenCreatedDate());

				if (CustomerType.KOJIN.equals(customer.getCustomerType())) {
					// 姓
					insertEntity.setCustomerNameSei(customer.getCustomerNameSei());
					insertEntity.setCustomerNameSeiKana(customer.getCustomerNameSeiKana());

					// 名
					insertEntity.setCustomerNameMei(customer.getCustomerNameMei());
					insertEntity.setCustomerNameMeiKana(customer.getCustomerNameMeiKana());
				} else {
					// 会社名
					insertEntity.setCustomerNameSei(customer.getCompanyName());
					insertEntity.setCustomerNameSeiKana(customer.getCompanyNameKana());
				}

				// 「顧客」として登録するので、customer_flgを立てて登録する（※後続の処理でt_anken_customerにレコードを登録すること）
				insertEntity.setCustomerFlg(SystemFlg.FLG_ON.getCd());
				// 「顧問」フラグは立てない
				insertEntity.setAdvisorFlg(SystemFlg.FLG_OFF.getCd());

				// 顧客登録
				int insertCount = tPersonDao.insert(insertEntity);

				if (insertCount != 1) {
					// 登録処理に失敗した場合
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
					throw new AppException(MessageEnum.MSG_E00012, null);
				}

				// 名簿IDを顧客IDにセットする
				Long personId = insertEntity.getPersonId();
				insertEntity.setCustomerId(personId);
				try {
					tPersonDao.update(insertEntity);
				} catch (OptimisticLockingFailureException ex) {
					// 楽観ロックエラー
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ex.getMessage());
					throw new AppException(MessageEnum.MSG_E00025, ex);
				}

				// 顧客IDをセット
				customer.setCustomerId(insertEntity.getCustomerId());

				if (CustomerType.KOJIN.equals(customer.getCustomerType())) {
					// 個人顧客付帯情報を登録
					this.ankenCustomerAddKojin(insertEntity.getPersonId());
				} else {
					// 法人顧客付帯情報を登録
					this.ankenCustomerAddHojin(insertEntity.getPersonId());
				}

				// ファイル管理の顧客フォルダを作成
				if (CustomerType.KOJIN.equals(customer.getCustomerType())) {
					this.ankenCustomerFolder(insertEntity.getCustomerId(), basicInputForm.getCustomerNameSei(), basicInputForm.getCustomerNameMei());
				} else {
					this.ankenCustomerFolder(insertEntity.getCustomerId(), basicInputForm.getCompanyName(), null);
				}
			}
		}

		// 顧客情報リストを更新
		basicInputForm.setCustomerList(customerList);
	}

	/**
	 * 個人付帯情報を登録する
	 * 
	 * @param personId
	 * @throws AppException
	 */
	private void ankenCustomerAddKojin(Long personId) throws AppException {

		TPersonAddKojinEntity entity = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personId);
		if (entity != null) {
			// 既に存在する場合 -> 楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		entity = new TPersonAddKojinEntity();
		entity.setPersonId(personId);

		// 個人付帯情報登録
		int insertCount = tPersonAddKojinDao.insert(entity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 法人付帯情報を登録する
	 * 
	 * @param personId
	 * @throws AppException
	 */
	private void ankenCustomerAddHojin(Long personId) throws AppException {

		TPersonAddHojinEntity entity = tPersonAddHojinDao.selectPersonAddHojinByPersonId(personId);
		if (entity != null) {
			// 既に存在する場合 -> 楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		entity = new TPersonAddHojinEntity();
		entity.setPersonId(personId);

		// 法人付帯情報登録
		int insertCount = tPersonAddHojinDao.insert(entity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 顧客のファイル管理のフォルダを登録（作成）
	 * 
	 * @param customerId
	 * @param customerNameSei
	 * @param customerNameMei
	 */
	private void ankenCustomerFolder(Long customerId, String customerNameSei, String customerNameMei) {

		// フォルダ名
		StringBuilder folderName = new StringBuilder();
		folderName.append(StringUtils.null2blank(customerNameSei));
		folderName.append(StringUtils.null2blank(customerNameMei));

		// ファイル管理の顧客フォルダを登録
		commonFileManagementService.createCustomerFolder(customerId, folderName.toString());
	}

	/**
	 * 案件情報を登録
	 * 
	 * @param basicInputForm
	 * @return
	 * @throws AppException
	 */
	private Long registAnken(AnkenRegistInputForm.AnkenRegistBasicInputForm basicInputForm) throws AppException {

		TAnkenEntity entity = new TAnkenEntity();
		entity.setAnkenType(basicInputForm.getAnkenType().getCd());
		entity.setAnkenCreatedDate(basicInputForm.getAnkenCreatedDate());
		entity.setAnkenName(basicInputForm.getAnkenName());
		entity.setBunyaId(Long.valueOf(basicInputForm.getBunya()));
		entity.setJianSummary(basicInputForm.getJianSummary());
		int insertCount = tAnkenDao.insert(entity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return entity.getAnkenId();
	}

	/**
	 * 案件-刑事情報を登録
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 * @throws AppException
	 */
	private void registAnkenAddKeiji(Long ankenId, AnkenRegistInputForm.AnkenRegistBasicInputForm basicInputForm) throws AppException {

		// 刑事案件以外なら処理終了
		if (!commonBunyaService.isKeiji(Long.valueOf(basicInputForm.getBunya()))) {
			return;
		}

		TAnkenAddKeijiEntity entity = new TAnkenAddKeijiEntity();
		entity.setAnkenId(ankenId);
		entity.setLawyerSelectType(BengoType.SHISEN.getCd());
		int insertCount = tAnkenAddKeijiDao.insert(entity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

	}

	/**
	 * 案件-担当情報を登録
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 * @throws AppException
	 */
	private void registAnkenTanto(Long ankenId, AnkenRegistInputForm.AnkenRegistBasicInputForm basicInputForm) throws AppException {

		// 売上計上先登録
		List<AnkenTantoSelectInputForm> salesOwnerList = basicInputForm.getSalesOwner();
		AtomicLong salesOwnerIndex = new AtomicLong(0);
		for (AnkenTantoSelectInputForm salesOwner : salesOwnerList) {
			if (salesOwner.getAccountSeq() != null) {
				TAnkenTantoEntity entity = new TAnkenTantoEntity();
				entity.setAnkenId(ankenId);
				entity.setAccountSeq(salesOwner.getAccountSeq());
				entity.setTantoType(TantoType.SALES_OWNER.getCd());
				entity.setAnkenMainTantoFlg(SystemFlg.booleanToCode(salesOwner.isMainTanto()));
				entity.setTantoTypeBranchNo(salesOwnerIndex.getAndIncrement());
				int insertCount = tAnkenTantoDao.insert(entity);

				if (insertCount != 1) {
					// 登録処理に失敗した場合
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
					throw new AppException(MessageEnum.MSG_E00012, null);
				}
			}
		}

		// 担当弁護士登録
		List<AnkenTantoSelectInputForm> tantoLawyerList = basicInputForm.getTantoLawyer();
		AtomicLong tantoLawyerIndex = new AtomicLong(0);
		for (AnkenTantoSelectInputForm tantoLawyer : tantoLawyerList) {
			if (tantoLawyer.getAccountSeq() != null) {
				TAnkenTantoEntity entity = new TAnkenTantoEntity();
				entity.setAnkenId(ankenId);
				entity.setAccountSeq(tantoLawyer.getAccountSeq());
				entity.setTantoType(TantoType.LAWYER.getCd());
				entity.setAnkenMainTantoFlg(SystemFlg.booleanToCode(tantoLawyer.isMainTanto()));
				entity.setTantoTypeBranchNo(tantoLawyerIndex.getAndIncrement());
				int insertCount = tAnkenTantoDao.insert(entity);

				if (insertCount != 1) {
					// 登録処理に失敗した場合
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
					throw new AppException(MessageEnum.MSG_E00012, null);
				}
			}
		}

		// 担当事務登録
		List<AnkenTantoSelectInputForm> tantoJimuList = basicInputForm.getTantoJimu();
		AtomicLong tantoJimuIndex = new AtomicLong(0);
		for (AnkenTantoSelectInputForm tantoJimu : tantoJimuList) {
			if (tantoJimu.getAccountSeq() != null) {
				TAnkenTantoEntity entity = new TAnkenTantoEntity();
				entity.setAnkenId(ankenId);
				entity.setAccountSeq(tantoJimu.getAccountSeq());
				entity.setTantoType(TantoType.JIMU.getCd());
				entity.setAnkenMainTantoFlg(SystemFlg.booleanToCode(tantoJimu.isMainTanto()));
				entity.setTantoTypeBranchNo(tantoJimuIndex.getAndIncrement());
				int insertCount = tAnkenTantoDao.insert(entity);

				if (insertCount != 1) {
					// 登録処理に失敗した場合
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
					throw new AppException(MessageEnum.MSG_E00012, null);
				}
			}
		}
	}

	/**
	 * 案件-顧客情報を登録
	 * 
	 * @param ankenId
	 * @param basicInputForm
	 * @throws AppException
	 */
	private void registAnkenCustomer(Long ankenId, AnkenRegistInputForm.AnkenRegistBasicInputForm basicInputForm) throws AppException {

		List<Customer> customerList = basicInputForm.getCustomerList();

		for (Customer customer : customerList) {
			TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(customer.getCustomerId());
			if (personEntity == null) {
				// 顧客データが無い場合 -> 楽観ロックエラーとする
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			TAnkenCustomerEntity tAnkenCustomerEntity = new TAnkenCustomerEntity();
			tAnkenCustomerEntity.setAnkenId(ankenId);
			tAnkenCustomerEntity.setCustomerId(customer.getCustomerId());
			tAnkenCustomerEntity.setKanryoFlg(SystemFlg.FLG_OFF.getCd());
			tAnkenCustomerEntity.setAnkenStatus(AnkenStatus.SODAN.getCd());
			int insertCount = tAnkenCustomerDao.insert(tAnkenCustomerEntity);

			if (insertCount != 1) {
				// 登録処理に失敗した場合
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00012, null);
			}

			// 名簿属性のFlgの更新
			commonCustomerService.updatePersonAttributeFlgs(customer.getCustomerId());
		}
	}

	/**
	 * AnkenRegistBasicInputFormの顧客情報を顧客情報保持クラスにセットします
	 * 
	 * @param newCustomer
	 * @param inputForm
	 */
	private void setCustomerForInputForm(AnkenRegistInputForm.Customer newCustomer, AnkenRegistInputForm.AnkenRegistBasicInputForm inputForm) {

		// inputFormが無ければ処理終了
		if (inputForm == null) {
			return;
		}

		// inputFormの顧客情報をCustomerクラスへセット
		newCustomer.setCustomerType(inputForm.getCustomerType());
		newCustomer.setRegistCustomerType(inputForm.getRegistCustomerType());
		if (CustomerType.KOJIN.equals(inputForm.getCustomerType())) {
			newCustomer.setCustomerNameMei(inputForm.getCustomerNameMei());
			newCustomer.setCustomerNameMeiKana(inputForm.getCustomerNameMeiKana());
			newCustomer.setCustomerNameSei(inputForm.getCustomerNameSei());
			newCustomer.setCustomerNameSeiKana(inputForm.getCustomerNameSeiKana());
		} else {
			newCustomer.setCompanyName(inputForm.getCompanyName());
			newCustomer.setCompanyNameKana(inputForm.getCompanyNameKana());
		}

	}

	/**
	 * TPersonEntityの名簿情報を顧客情報保持クラスにセットします。
	 * （名簿情報保持クラスの顧客登録区分は「登録済の顧客」固定）
	 * 
	 * @param newCustomer
	 * @param personEntity
	 */
	private void setCustomerForEntity(AnkenRegistInputForm.Customer newCustomer, TPersonEntity personEntity) {

		// entityが無ければ処理終了
		if (personEntity == null) {
			return;
		}

		// entityの顧客情報をCustomerクラスへセット
		newCustomer.setCustomerType(CustomerType.of(personEntity.getCustomerType()));
		newCustomer.setRegistCustomerType(RegistCustomerType.REGISTERED);
		newCustomer.setCustomerId(personEntity.getCustomerId());
		if (CustomerType.KOJIN.equalsByCode(personEntity.getCustomerType())) {
			newCustomer.setCustomerNameSei(personEntity.getCustomerNameSei());
			newCustomer.setCustomerNameSeiKana(personEntity.getCustomerNameSeiKana());
			newCustomer.setCustomerNameMei(personEntity.getCustomerNameMei());
			newCustomer.setCustomerNameMeiKana(personEntity.getCustomerNameMeiKana());
		} else {
			newCustomer.setCompanyName(personEntity.getCustomerNameSei());
			newCustomer.setCompanyNameKana(personEntity.getCustomerNameSei());
		}
	}

}
