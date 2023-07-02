package jp.loioz.app.user.personManagement.service;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static java.util.Comparator.reverseOrder;

import java.time.LocalDate;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonScheduleService;
import jp.loioz.app.user.personManagement.dto.PersonAddressDto;
import jp.loioz.app.user.personManagement.dto.PersonContactDto;
import jp.loioz.app.user.personManagement.dto.PersonKozaDto;
import jp.loioz.app.user.personManagement.dto.PersonScheduleListDto;
import jp.loioz.app.user.personManagement.form.personEdit.PersonEditInputForm;
import jp.loioz.app.user.personManagement.form.personEdit.PersonEditInputForm.Address;
import jp.loioz.app.user.personManagement.form.personEdit.PersonEditViewForm;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.bean.CustomerScheduleListBean;
import jp.loioz.bean.PersonBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AddressTypeHojin;
import jp.loioz.common.constant.CommonConstant.AddressTypeKojin;
import jp.loioz.common.constant.CommonConstant.AllowType;
import jp.loioz.common.constant.CommonConstant.ContactCategory;
import jp.loioz.common.constant.CommonConstant.ContactType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.DeathFlg;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.MailingType;
import jp.loioz.common.constant.CommonConstant.SelectType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransferAddressType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozObjectUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dao.MSelectListDao;
import jp.loioz.dao.PersonEditDao;
import jp.loioz.dao.TAdvisorContractDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TOldAddressDao;
import jp.loioz.dao.TPersonAddHojinDao;
import jp.loioz.dao.TPersonAddKojinDao;
import jp.loioz.dao.TPersonAddLawyerDao;
import jp.loioz.dao.TPersonContactDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenDto;
import jp.loioz.entity.MRoomEntity;
import jp.loioz.entity.MSelectListEntity;
import jp.loioz.entity.TAdvisorContractEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TOldAddressEntity;
import jp.loioz.entity.TPersonAddHojinEntity;
import jp.loioz.entity.TPersonAddKojinEntity;
import jp.loioz.entity.TPersonAddLawyerEntity;
import jp.loioz.entity.TPersonContactEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 名簿編集画面のserviceクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PersonEditService extends DefaultService {

	@Autowired
	private MRoomDao mRoomDao;

	@Autowired
	private TPersonDao tPersonDao;

	@Autowired
	private TPersonContactDao tPersonContactDao;

	@Autowired
	private TOldAddressDao tOldAddressDao;

	@Autowired
	private TPersonAddKojinDao tPersonAddKojinDao;

	@Autowired
	private TPersonAddHojinDao tPersonAddHojinDao;

	@Autowired
	private MSelectListDao mSelectListDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private PersonEditDao personEditDao;

	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	@Autowired
	private TPersonAddLawyerDao tPersonAddLawyerDao;

	@Autowired
	private TAdvisorContractDao tAdvisorContractDao;

	/** 共通アカウントサービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 共通案件サービス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 共通予定サービス */
	@Autowired
	private CommonScheduleService commonScheduleService;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する
	 *
	 * @return 画面表示情報
	 */
	public PersonEditViewForm createViewForm() {

		PersonEditViewForm form = new PersonEditViewForm();

		return form;
	}

	// ▼ 顧客の基本情報に関する処理

	/**
	 * 名簿の基本情報表示フォームを取得する
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditViewForm.PersonBasicViewForm createPersonBasicViewForm(Long personId) {

		PersonEditViewForm.PersonBasicViewForm viewForm = new PersonEditViewForm.PersonBasicViewForm();

		// 表示フォームは、基本的には入力フォームで保存されたデータを表示する形式になるので、入力フォームの取得処理を活用する。

		// 顧客の基本情報入力フォーム
		PersonEditInputForm.PersonBasicInputForm inputForm = this.createPersonBasicInputForm(personId);

		// 表示フォームに値を設定
		viewForm.setCustomerType(inputForm.getCustomerType());
		viewForm.setPersonAttribute(inputForm.getPersonAttribute());
		viewForm.setCustomerCreatedDate(inputForm.getCustomerCreatedDate());
		viewForm.setCustomerNameSei(inputForm.getCustomerNameSei());
		viewForm.setCustomerNameSeiKana(inputForm.getCustomerNameSeiKana());
		viewForm.setCustomerNameMei(inputForm.getCustomerNameMei());
		viewForm.setCustomerNameMeiKana(inputForm.getCustomerNameMeiKana());
		viewForm.setOldName(inputForm.getOldName());
		viewForm.setOldNameKana(inputForm.getOldNameKana());
		viewForm.setBirthEra(inputForm.getBirthEra());
		viewForm.setEraEpoch(inputForm.getEraEpoch());
		viewForm.setBirthYear(inputForm.getBirthYear());
		viewForm.setBirthMonth(inputForm.getBirthMonth());
		viewForm.setBirthDay(inputForm.getBirthDay());
		viewForm.setGender(inputForm.getGender());
		viewForm.setCountry(inputForm.getCountry());
		viewForm.setLanguage(inputForm.getLanguage());
		viewForm.setYago(inputForm.getYago());
		viewForm.setYagoKana(inputForm.getYagoKana());
		viewForm.setDaihyoName(inputForm.getDaihyoName());
		viewForm.setDaihyoNameKana(inputForm.getDaihyoNameKana());
		viewForm.setDaihyoPositionName(inputForm.getDaihyoPositionName());
		viewForm.setTantoName(inputForm.getTantoName());
		viewForm.setTantoNameKana(inputForm.getTantoNameKana());
		viewForm.setOldHojinName(inputForm.getOldHojinName());
		viewForm.setRemarks(inputForm.getRemarks());
		viewForm.setSodanRemarks(inputForm.getSodanRemarks());
		viewForm.setJob(inputForm.getJob());
		viewForm.setWorkPlace(inputForm.getWorkPlace());
		viewForm.setBushoName(inputForm.getBushoName());
		viewForm.setDeathFlg(inputForm.getDeathFlg());
		viewForm.setDeathEra(inputForm.getDeathEra());
		viewForm.setDeathEraEpoch(inputForm.getDeathEraEpoch());
		viewForm.setDeathYear(inputForm.getDeathYear());
		viewForm.setDeathMonth(inputForm.getDeathMonth());
		viewForm.setDeathDay(inputForm.getDeathDay());
		viewForm.setLawyerJimushoName(inputForm.getLawyerJimushoName());
		viewForm.setLawyerBushoName(inputForm.getLawyerBushoName());

		// 入力フォームでは不足する情報の取得、設定
		// 名簿ID
		viewForm.setPersonId(PersonId.of(personId));
		// 顧客IDオブジェクト
		viewForm.setCustomerId(CustomerId.of(inputForm.getCustomerId()));
		// 顧客フラグ
		List<TAnkenCustomerEntity> ankenCustomerEntityList = tAnkenCustomerDao.selectAnkenCustomerByPersonId(personId);
		if (LoiozCollectionUtils.isEmpty(ankenCustomerEntityList)) {
			// 案件-顧客データが無い場合は顧客でない
			viewForm.setCustomerFlg(false);
		} else {
			// 案件-顧客データが有る場合は顧客
			viewForm.setCustomerFlg(true);
		}

		// 相談経路
		Long sodanRouteSeq = inputForm.getSodanRoute();
		MSelectListEntity selectListEntity = mSelectListDao.selectSelectListBySeq(sodanRouteSeq);
		if (selectListEntity != null) {
			viewForm.setSodanRouteText(selectListEntity.getSelectVal());
		}

		return viewForm;
	}

	/**
	 * 名簿の基本情報入力フォームを取得する
	 * 
	 * @param personId
	 * @return PersonEditInputForm.PersonBasicInputForm
	 */
	public PersonEditInputForm.PersonBasicInputForm createPersonBasicInputForm(Long personId) {

		// 初期設定済みの入力フォームを取得
		PersonEditInputForm.PersonBasicInputForm inputForm = this.getInitializedPersonBasicInputForm(personId);

		// 名簿情報を取得
		// ※フォーム初期化時にデータの存在チェックは行っているので、ここでは行わない
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);

		// 顧客区分
		CustomerType customerType = LoiozObjectUtils.defaultIfNull(CustomerType.of(personEntity.getCustomerType()),
				CustomerType.KOJIN);
		inputForm.setCustomerType(customerType);
		inputForm.setCustomerTypeBeforeUpdate(customerType);

		// 名簿属性
		inputForm.setPersonAttribute(PersonAttribute.of(personEntity.getCustomerFlg(), personEntity.getAdvisorFlg(), personEntity.getCustomerType()));

		// 登録日
		inputForm.setCustomerCreatedDate(personEntity.getCustomerCreatedDate());

		// 姓名
		inputForm.setCustomerNameSei(personEntity.getCustomerNameSei());
		inputForm.setCustomerNameSeiKana(personEntity.getCustomerNameSeiKana());
		inputForm.setCustomerNameMei(personEntity.getCustomerNameMei());
		inputForm.setCustomerNameMeiKana(personEntity.getCustomerNameMeiKana());

		// 特記事項
		inputForm.setRemarks(personEntity.getRemarks());

		// 相談経路
		inputForm.setSodanRoute(personEntity.getSodanRoute());
		inputForm.setSodanRemarks(personEntity.getSodanRemarks());

		// 個人名簿付帯情報
		TPersonAddKojinEntity personAddKojin = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personId);
		if (personAddKojin != null) {
			// 旧姓
			inputForm.setOldName(personAddKojin.getOldName());
			inputForm.setOldNameKana(personAddKojin.getOldNameKana());

			// 生年月日
			LocalDate birthdate = personAddKojin.getBirthday();
			if (birthdate != null) {
				inputForm.setBirthEra(EraType.of(personAddKojin.getBirthdayDisplayType()));
				if (!EraType.SEIREKI.getCd().equals(personAddKojin.getBirthdayDisplayType())) {
					// 和暦の場合
					JapaneseDate fromWareki = JapaneseDate.of(birthdate.getYear(), birthdate.getMonthValue(),
							birthdate.getDayOfMonth());
					DateTimeFormatter kanjiFormatter = DateTimeFormatter.ofPattern("yy");
					String kanjiFormatted = kanjiFormatter.format(fromWareki);
					fromWareki.getEra().getValue();
					inputForm.setBirthYear(Long.valueOf(kanjiFormatted));

					// JapaneseEraからEraTypeを取得する
					inputForm.setBirthEra(EraType.of(String.valueOf(fromWareki.getEra().getValue() + 2)));
				} else {
					// 西暦の場合
					inputForm.setBirthYear(Long.valueOf(birthdate.getYear()));
				}
				inputForm.setBirthMonth(Long.valueOf(birthdate.getMonthValue()));
				inputForm.setBirthDay(Long.valueOf(birthdate.getDayOfMonth()));
				inputForm.setEraEpoch(EraEpoch.of(personAddKojin.getBirthdayDisplayType()));
			}

			// 性別
			inputForm.setGender(Gender.of(personAddKojin.getGenderType()));

			// 国籍、言語
			inputForm.setCountry(personAddKojin.getCountry());
			inputForm.setLanguage(personAddKojin.getLanguage());

			// 屋号
			inputForm.setYago(personAddKojin.getYago());
			inputForm.setYagoKana(personAddKojin.getYagoKana());

			// 職業
			inputForm.setJob(personAddKojin.getJob());

			// 勤務先
			inputForm.setWorkPlace(personAddKojin.getWorkPlace());

			// 部署
			inputForm.setBushoName(personAddKojin.getBushoName());

			// 死亡フラグ
			inputForm.setDeathFlg(DeathFlg.FLG_ON.equalsByCode(personAddKojin.getDeathFlg()));

			// 死亡日
			LocalDate deathDate = personAddKojin.getDeathDate();
			if (deathDate != null) {
				inputForm.setDeathEra(EraType.of(personAddKojin.getDeathDateDisplayType()));
				if (!EraType.SEIREKI.getCd().equals(personAddKojin.getDeathDateDisplayType())) {
					// 和暦の場合
					JapaneseDate fromWareki = JapaneseDate.of(deathDate.getYear(), deathDate.getMonthValue(),
							deathDate.getDayOfMonth());
					DateTimeFormatter kanjiFormatter = DateTimeFormatter.ofPattern("yy");
					String kanjiFormatted = kanjiFormatter.format(fromWareki);
					fromWareki.getEra().getValue();
					inputForm.setDeathYear(Long.valueOf(kanjiFormatted));

					// JapaneseEraからEraTypeを取得する
					inputForm.setDeathEra(EraType.of(String.valueOf(fromWareki.getEra().getValue() + 2)));
				} else {
					// 西暦の場合
					inputForm.setDeathYear(Long.valueOf(deathDate.getYear()));
				}
				inputForm.setDeathMonth(Long.valueOf(deathDate.getMonthValue()));
				inputForm.setDeathDay(Long.valueOf(deathDate.getDayOfMonth()));
				inputForm.setDeathEraEpoch(EraEpoch.of(personAddKojin.getDeathDateDisplayType()));
			}

		}

		// 法人名簿付帯情報
		TPersonAddHojinEntity personAddHojin = tPersonAddHojinDao.selectPersonAddHojinByPersonId(personId);

		if (personAddHojin != null) {
			// 代表
			inputForm.setDaihyoName(personAddHojin.getDaihyoName());
			inputForm.setDaihyoNameKana(personAddHojin.getDaihyoNameKana());
			inputForm.setDaihyoPositionName(personAddHojin.getDaihyoPositionName());
			// 担当
			inputForm.setTantoName(personAddHojin.getTantoName());
			inputForm.setTantoNameKana(personAddHojin.getTantoNameKana());
			// 旧商号
			inputForm.setOldHojinName(personAddHojin.getOldHojinName());
		}

		// 弁護士付帯情報
		List<TPersonAddLawyerEntity> personAddLawyerList = tPersonAddLawyerDao.selectPersonAddLawyerByPersonId(List.of(personId));

		if (LoiozCollectionUtils.isNotEmpty(personAddLawyerList)) {
			TPersonAddLawyerEntity entity = personAddLawyerList.stream().findFirst().get();
			// 事務所名
			inputForm.setLawyerJimushoName(entity.getJimushoName());
			// 部署・役職名
			inputForm.setLawyerBushoName(entity.getBushoName());
		}

		return inputForm;
	}

	/**
	 * 初期設定済みの顧客の基本情報入力フォームを取得
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditInputForm.PersonBasicInputForm getInitializedPersonBasicInputForm(Long personId) {

		PersonEditInputForm.PersonBasicInputForm inputForm = new PersonEditInputForm.PersonBasicInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm, personId);

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
	 * @param personId
	 */
	public void setDisplayData(PersonEditInputForm.PersonBasicInputForm inputForm, Long personId) {

		// 名簿情報を取得
		TPersonEntity personEntity = this.getPersonEntity(personId);

		// 名簿ID
		inputForm.setPersonId(personEntity.getPersonId());

		// 顧客ID
		inputForm.setCustomerId(personEntity.getCustomerId());

		// 性別選択肢
		List<SelectOptionForm> genderOptionList = Stream.of(Gender.MALE, Gender.FEMALE, Gender.UNKNOW)
				.map(SelectOptionForm::fromEnum).collect(Collectors.toList());
		inputForm.setGenderOptionList(genderOptionList);

		// 選択肢情報を取得
		List<MSelectListEntity> selectEntityList = mSelectListDao.selectEnabledIncludeDeleted(personEntity.getSodanRoute(),
				personEntity.getAddInfo());

		// 選択肢区分でグループ化
		Map<String, List<SelectOptionForm>> selectOptionMap = selectEntityList.stream()
				.collect(Collectors.groupingBy(MSelectListEntity::getSelectType,
						Collectors.mapping(entity -> new SelectOptionForm(entity.getSelectSeq(), entity.getSelectVal()),
								Collectors.toList())));

		// 相談経路プルダウン
		List<SelectOptionForm> sodanRouteOptionList = selectOptionMap.getOrDefault(SelectType.SODANKEIRO.getCd(),
				Collections.emptyList());
		inputForm.setSodanRouteOptionList(sodanRouteOptionList);
	}

	/**
	 * 基本情報の保存処理用のバリデーション処理。<br>
	 * 下記のバリデーションチェックを行う
	 * 
	 * <pre>
	 * ・種別の変更可能チェック
	 * </pre>
	 * 
	 * @param basicInputForm 画面の入力データ（保存されるデータ）
	 * @param personId
	 * @throws AppException チェックNGの場合にスロー
	 */
	public void accessDBValidatedForSaveCustomerBasic(PersonEditInputForm.PersonBasicInputForm basicInputForm, Long personId)
			throws AppException {

		// 名簿情報
		TPersonEntity personEntity = this.getPersonEntity(personId);

		// 現在（変更前）の顧客タイプ
		CustomerType customerType = CustomerType.of(personEntity.getCustomerType());

		// 入力フォームの顧客タイプ
		CustomerType inputCustomerType = basicInputForm.getCustomerType();

		// 既に案件使用されているか顧問契約をしている場合、「個人」、「企業・団体」⇔「弁護士」の種別変更を不可とする。
		if ((!CustomerType.LAWYER.equals(customerType) && CustomerType.LAWYER.equals(inputCustomerType))
				|| (CustomerType.LAWYER.equals(customerType) && !CustomerType.LAWYER.equals(inputCustomerType))) {
			// 名簿情報が顧客として登録されているか
			List<TAnkenCustomerEntity> ankenCustomerEntityList = tAnkenCustomerDao.selectAnkenCustomerByPersonId(personId);
			if (LoiozCollectionUtils.isNotEmpty(ankenCustomerEntityList)) {
				throw new AppException(MessageEnum.MSG_E00169, null);
			}

			// 名簿情報が関与者として登録されているか
			List<TKanyoshaEntity> tKanyoshaEntityList = tKanyoshaDao.selectKanyoshaListByPersonId(personId);
			if (LoiozCollectionUtils.isNotEmpty(tKanyoshaEntityList)) {
				throw new AppException(MessageEnum.MSG_E00168, null);
			}

			// 顧問契約をしているか
			List<TAdvisorContractEntity> advisorContractEntityList = tAdvisorContractDao.selectContractListPageByPersonId(personId);
			if(LoiozCollectionUtils.isNotEmpty(advisorContractEntityList)) {
				throw new AppException(MessageEnum.MSG_E00170, null);
			}
		}
	}

	/**
	 * 【個人】顧客の基本情報の保存
	 * 
	 * @param personId
	 * @param basicInputForm
	 */
	public void saveKojinPersonBasic(Long personId, PersonEditInputForm.PersonBasicInputForm basicInputForm) {

		// 顧客の基本情報を更新
		this.updatePersonBasic(personId, basicInputForm);

		// 個人名簿付帯情報を更新
		this.updateCustomerAddKojin(personId, basicInputForm);
	}

	/**
	 * 【法人】顧客の基本情報の保存
	 * 
	 * @param personId
	 * @param basicInputForm
	 */
	public void saveHojinPersonBasic(Long personId, PersonEditInputForm.PersonBasicInputForm basicInputForm) {

		// 顧客の基本情報を更新
		this.updatePersonBasic(personId, basicInputForm);

		// 法人名簿付帯情報を更新
		this.updateCustomerAddHojin(personId, basicInputForm);
	}

	/**
	 * 【弁護士】顧客の基本情報の保存
	 * 
	 * @param personId
	 * @param basicInputForm
	 */
	public void saveLawyerPersonBasic(Long personId, PersonEditInputForm.PersonBasicInputForm basicInputForm) {

		// 顧客の基本情報を更新
		this.updatePersonBasic(personId, basicInputForm);

		// 弁護士付帯情報を更新
		this.updatePersonAddLayer(personId, basicInputForm);
	}

	// ▼ 顧客の住所情報に関する処理

	/**
	 * 名簿の住所情報表示フォームを取得する
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditViewForm.PersonAddressViewForm createPersonAddressViewForm(Long personId) {

		PersonEditViewForm.PersonAddressViewForm viewForm = new PersonEditViewForm.PersonAddressViewForm();

		// 名簿情報
		TPersonEntity personEntity = this.getPersonEntity(personId);

		// 顧客タイプ
		CustomerType customerType = LoiozObjectUtils.defaultIfNull(CustomerType.of(personEntity.getCustomerType()),
				CustomerType.KOJIN);
		viewForm.setCustomerType(customerType);

		// 住所データをフォームに設定
		List<PersonAddressDto> addressRowDtoList = null;
		if (customerType == CustomerType.KOJIN) {
			// 個人の場合
			addressRowDtoList = this.getKojinAddressRowDtoList(personEntity);
		} else if (customerType == CustomerType.HOJIN) {
			// 法人の場合
			addressRowDtoList = this.getHojinAddressRowDtoList(personEntity);
		} else {
			// 弁護士の場合
			addressRowDtoList = this.getLawyerAddressRowDtoList(personEntity);
		}
		viewForm.setPersonAddressRowDtoList(addressRowDtoList);

		return viewForm;
	}

	/**
	 * 顧客の住所情報入力フォームを取得する
	 * 
	 * @param personId 対象の住所情報を持つ顧客
	 * @param isNewRegist 新規登録用の入力フォームかどうか
	 * @param isBaseAddress 対象の住所情報が居住地／所在地かどうか
	 * @param isTourokuAddress 対象の住所情報が住民票登録地／登記簿登録地かどうか
	 * @param isTransferAddress 対象の住所情報が郵送先住所情報かどうか
	 * @param oldAddressSeq その他住所（旧住所）の場合の対象データの連番値
	 * @param viewFormCustomerTypeCd 画面表示時の顧客種別コード
	 * @return
	 */
	public PersonEditInputForm.PersonAddressInputForm createPersonAddressInputForm(Long personId,
			boolean isNewRegist, boolean isBaseAddress, boolean isTourokuAddress, boolean isTransferAddress,
			Long oldAddressSeq, String viewFormCustomerTypeCd) throws AppException {

		PersonEditInputForm.PersonAddressInputForm inputForm = this
				.getInitializedPersonAddressInputForm(personId, viewFormCustomerTypeCd);

		// 名簿情報
		TPersonEntity personEntity = this.getPersonEntity(personId);
		CustomerType customerType = CustomerType.of(personEntity.getCustomerType());

		if (isNewRegist) {

			// 登録フォームの場合のフォーム情報の設定

			// 新規入力フォームかどうか
			inputForm.setNewRegist(isNewRegist);
			// 居住地登録か、その他住所登録か
			inputForm.setBaseAddress(isBaseAddress);

		} else {
			// 更新フォームの場合のフォーム情報の設定

			// どの住所データが更新対象かを判定する値は入力フォームに引き継ぐ
			inputForm.setBaseAddress(isBaseAddress);
			inputForm.setOldAddressSeq(oldAddressSeq);
			inputForm.setTourokuAddress(isTourokuAddress);
			inputForm.setTourokuAddressBeforeUpdate(isTourokuAddress);
			inputForm.setTransferAddress(isTransferAddress);
			inputForm.setTransferAddressBeforeUpdate(isTransferAddress);

			// 住所情報を設定
			PersonEditInputForm.Address address = null;
			if (customerType == CustomerType.KOJIN) {
				// 個人の場合
				address = this.getKojinEditAddressData(personEntity, isBaseAddress, isTourokuAddress,
						isTransferAddress, oldAddressSeq);
			} else if (customerType == CustomerType.HOJIN) {
				// 企業・団体の場合
				address = this.getHojinEditAddressData(personEntity, isBaseAddress, isTourokuAddress,
						isTransferAddress, oldAddressSeq);
			} else {
				// 弁護士の場合
				address = this.getLawyerEditAddressData(personEntity, isBaseAddress, isTourokuAddress,
						isTransferAddress, oldAddressSeq);
			}
			if (address == null || (StringUtils.isAllEmpty(address.getAddress1(), address.getAddress2(), address.getRemarks(), address.getZipCode()))) {
				// 更新処理なのに住所が取得できない場合は、楽観エラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			inputForm.setAddress(address);

			// 郵送方法を設定
			MailingType mailingType = MailingType.of(personEntity.getTransferType());
			inputForm.setMailingType(mailingType);
		}

		// 以下、登録、更新で共通のフォーム設定

		// 現在登録されている住所データ
		List<PersonAddressDto> addressRowDtoList = null;
		if (customerType == CustomerType.KOJIN) {
			// 個人の場合
			addressRowDtoList = this.getKojinAddressRowDtoList(personEntity);
		} else if (customerType == CustomerType.HOJIN) {
			// 企業・団体の場合
			addressRowDtoList = this.getHojinAddressRowDtoList(personEntity);
		} else {
			// 弁護士の場合
			addressRowDtoList = this.getLawyerAddressRowDtoList(personEntity);
		}

		// 入力フォームデータ（表示するデータ）
		PersonAddressDto inputFormDto = this.createAddressDto(inputForm);
		// 上記を除いた住所データリスト
		List<PersonAddressDto> dtoListExcludUpdateTarget = this.getAddressDtoListExcludUpdateTarget(addressRowDtoList, inputFormDto);

		// 登録住所が既に存在しているかどうか
		boolean isExistTourokuAddress = this.isExistTourokuAddress(dtoListExcludUpdateTarget);
		// 郵送先住所が既に存在しているかどうか
		boolean isExistTransferAddress = this.isExistTransferAddress(dtoListExcludUpdateTarget);

		// フォームに値を設定
		inputForm.setAlreadyExistTourokuAddress(isExistTourokuAddress);
		inputForm.setAlreadyExistTransferAddress(isExistTransferAddress);

		return inputForm;
	}

	/**
	 * 初期設定済みの名簿の住所情報入力フォームを取得
	 * 
	 * @param personId
	 * @param viewFormCustomerTypeCd 画面表示時の顧客種別コード
	 * @return
	 */
	public PersonEditInputForm.PersonAddressInputForm getInitializedPersonAddressInputForm(Long personId, String viewFormCustomerTypeCd) {

		PersonEditInputForm.PersonAddressInputForm inputForm = new PersonEditInputForm.PersonAddressInputForm();
		inputForm.setViewFormCustomerType(CustomerType.of(viewFormCustomerTypeCd));

		// 表示用データの設定
		this.setDisplayData(inputForm, personId);

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
	 * @param personId
	 */
	public void setDisplayData(PersonEditInputForm.PersonAddressInputForm inputForm, Long personId) {

		// 名簿情報を設定
		TPersonEntity personEntity = this.getPersonEntity(personId);
		CustomerType customerType = CustomerType.of(personEntity.getCustomerType());
		inputForm.setCustomerType(customerType);
	}

	/**
	 * 住所情報の保存処理用のバリデーション処理。<br>
	 * 下記のバリデーションチェックを行う
	 * 
	 * <pre>
	 * ・更新データの楽観ロックチェック
	 * ・住所情報の重複チェック
	 * ・登録地（住民票登録地/登記簿登録地）の重複チェック
	 * ・郵送先住所の重複チェック
	 * </pre>
	 * 
	 * @param addressInputForm 画面の入力データ（保存されるデータ）
	 * @param personId
	 * @throws AppException チェックNGの場合にスロー
	 */
	public void accessDBValidatedForSavePersonAddress(PersonEditInputForm.PersonAddressInputForm addressInputForm, Long personId)
			throws AppException {

		// 名簿情報
		TPersonEntity personEntity = this.getPersonEntity(personId);

		// 顧客タイプ
		CustomerType customerType = LoiozObjectUtils.defaultIfNull(CustomerType.of(personEntity.getCustomerType()),
				CustomerType.KOJIN);

		// 現在登録されている住所データ
		List<PersonAddressDto> addressRowDtoList = null;
		if (customerType == CustomerType.KOJIN) {
			// 個人の場合
			addressRowDtoList = this.getKojinAddressRowDtoList(personEntity);
		} else if (customerType == CustomerType.HOJIN) {
			// 法人の場合
			addressRowDtoList = this.getHojinAddressRowDtoList(personEntity);
		} else {
			// 弁護士の場合
			addressRowDtoList = this.getLawyerAddressRowDtoList(personEntity);
		}

		// 楽観ロックチェック
		try {
			this.ngOptimisticLock(addressInputForm, customerType, addressRowDtoList);
		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 画面の入力データ（更新対象）
		PersonAddressDto inputFormDto = this.createAddressDto(addressInputForm);
		// 画面の入力データ（更新対象）を除外したデータリスト
		List<PersonAddressDto> dtoListExcludUpdateTarget = this.getAddressDtoListExcludUpdateTarget(addressRowDtoList, inputFormDto);

		// 同じ住所データは登録不可
		boolean isExistSameAddress = this.isExistSameAddress(dtoListExcludUpdateTarget, inputFormDto);
		if (isExistSameAddress) {
			throw new AppException(MessageEnum.MSG_E00093, null);
		}
	}

	/**
	 * 名簿の住所情報の保存
	 * 
	 * @param personId
	 * @param basicInputForm
	 * @throws AppException
	 */
	public void savePersonAddress(Long personId, PersonEditInputForm.PersonAddressInputForm addressInputForm) throws AppException {

		// 住所情報の保存処理

		// 名簿情報
		TPersonEntity personEntity = this.getPersonEntity(personId);

		// 顧客タイプ
		CustomerType customerType = LoiozObjectUtils.defaultIfNull(CustomerType.of(personEntity.getCustomerType()),
				CustomerType.KOJIN);

		// 保存データの状態判定値
		// 新規登録かどうか
		boolean isNewRegist = addressInputForm.isNewRegist();
		// 居住地/所在地データかどうか
		boolean isBaseAddress = addressInputForm.isBaseAddress();
		// 登録地（住民票登録地/登記簿登録地）データかどうか
		boolean isTourokuAddress = addressInputForm.isTourokuAddress();
		// もともと登録地（住民票登録地/登記簿登録地）データだったかどうか
		boolean isTourokuAddressBeforeUpdate = addressInputForm.isTourokuAddressBeforeUpdate();
		// 郵送先住所データかどうか
		boolean isTransferAddress = addressInputForm.isTransferAddress();
		// もともと郵送先住所データだったかどうか
		boolean isTransferAddressBeforeUpdate = addressInputForm.isTransferAddressBeforeUpdate();
		// 旧住所データ連番
		Long oldAddressSeq = addressInputForm.getOldAddressSeq();

		// 画面で入力された住所情報（登録するデータ）
		PersonEditInputForm.Address inputFormAddress = addressInputForm.getAddress();
		MailingType mailingType = addressInputForm.getMailingType();

		if (isNewRegist) {
			// 新規登録の場合

			if (isBaseAddress) {
				// 居住地/所在地の追加の場合
				// 居住地/所在地データの登録処理（更新処理）
				this.updateBaseAddress(personId, inputFormAddress);
			}

			if (isTourokuAddress) {
				// 登録地として指定された場合
				// 登録地情報の登録処理（更新処理）
				this.updateTourokuAddress(personId, customerType, inputFormAddress, isBaseAddress, isTourokuAddressBeforeUpdate);
			}

			if (isTransferAddress) {
				// 郵送先として指定された場合
				// 郵送先住所情報の登録処理（更新処理）
				this.updateTransferAddress(personId, customerType, inputFormAddress, mailingType, isBaseAddress, isTransferAddressBeforeUpdate);
			}

			if (!isBaseAddress && !isTourokuAddress && !isTransferAddress) {
				// 基本住所の登録でも、登録地の登録でも、郵送先の登録でもない
				// -> 旧住所の登録となる
				this.insertOldAddress(personId, inputFormAddress);
			}
		} else {
			// 更新の場合

			if (oldAddressSeq == null) {
				// 旧住所の更新ではない場合

				if (isBaseAddress) {
					// 居住地/所在地データの更新処理
					this.updateBaseAddress(personId, inputFormAddress);
				}

				if (isTourokuAddressBeforeUpdate && !isTourokuAddress) {
					// もともと登録地として登録していたが、登録地ではなくなる場合
					// 登録地データの削除（null更新）処理
					this.nullUpdateTourokuAddress(personId, customerType);
				} else if (isTourokuAddress) {
					// 登録地として指定された場合
					// 登録地情報の更新処理
					this.updateTourokuAddress(personId, customerType, inputFormAddress, isBaseAddress, isTourokuAddressBeforeUpdate);
				}

				if (isTransferAddressBeforeUpdate && !isTransferAddress) {
					// もともと郵送先として登録していたが、郵送先ではなくなる場合
					// 郵送先データの削除（null更新）処理
					this.nullUpdateTransferAddress(personId);
				} else if (isTransferAddress) {
					// 郵送先住所として指定された場合
					// 郵送先住所情報の更新処理
					this.updateTransferAddress(personId, customerType, inputFormAddress, mailingType, isBaseAddress, isTransferAddressBeforeUpdate);
				}

				if (!isBaseAddress && !isTourokuAddress && !isTransferAddress) {
					// 基本住所の保存でも、登録地の保存でも、郵送先の保存でもない
					// -> もともと登録地か郵送先で保存されていたが、どちらでもなくなり、旧住所となる場合

					// もともと登録地か郵送先として登録されていたデータは、上記の処理で削除（null更新）されているため、旧住所の登録のみを行う
					this.insertOldAddress(personId, inputFormAddress);
				}
			} else {
				// 旧住所の更新の場合

				if (isTourokuAddress || isTransferAddress) {
					// もともと旧住所だったが、登録地か、郵送先として新たに指定された場合

					// 旧住所の削除
					this.deleteOldAddress(oldAddressSeq);

					if (isTourokuAddress) {
						// 登録地として指定された場合
						// 登録地情報の更新処理
						this.updateTourokuAddress(personId, customerType, inputFormAddress, isBaseAddress, isTourokuAddressBeforeUpdate);
					}

					if (isTransferAddress) {
						// 郵送先として指定された場合
						// 郵送先住所情報の更新処理
						this.updateTransferAddress(personId, customerType, inputFormAddress, mailingType, isBaseAddress, isTransferAddressBeforeUpdate);
					}
				} else {
					// 旧住所の更新
					this.updateOldAddress(oldAddressSeq, inputFormAddress);
				}
			}
		}
	}

	/**
	 * 住所情報の削除処理用のバリデーション処理。<br>
	 * 下記のバリデーションチェックを行う
	 * 
	 * <pre>
	 * ・更新時の顧客タイプチェック
	 * </pre>
	 * 
	 * @param customerTypeCd
	 * @param personId
	 * @throws AppException チェックNGの場合にスロー
	 */
	public void accessDBValidatedForDeletePersonAddress(String customerTypeCd, Long personId)
			throws AppException {

		// 名簿情報
		TPersonEntity personEntity = this.getPersonEntity(personId);

		// 顧客タイプ
		CustomerType customerType = LoiozObjectUtils.defaultIfNull(CustomerType.of(personEntity.getCustomerType()),
				CustomerType.KOJIN);

		// 楽観ロックチェック
		try {
			if (!customerType.equalsByCode(customerTypeCd)) {
				// 顧客タイプが変更されている場合
				throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
			}
		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	// ▼ 連絡先情報に関する処理

	/**
	 * 連絡先情報表示フォームを取得する
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditViewForm.PersonContactViewForm createPersonContactViewForm(Long personId) {

		PersonEditViewForm.PersonContactViewForm viewForm = new PersonEditViewForm.PersonContactViewForm();

		// 連絡先のリストをフォームに設定
		List<PersonContactDto> allContactList = this.getNowRegisContactListExcludeEmpty(personId);
		viewForm.setContactList(allContactList);

		// 連絡先の各カテゴリ（電話、FAXなど）について、追加登録可能かどうかの判定値をフォームに設定
		boolean telCountReachedLimit = this.isDataCountReachedLimit(allContactList, ContactCategory.TEL);
		boolean faxCountReachedLimit = this.isDataCountReachedLimit(allContactList, ContactCategory.FAX);
		boolean mailCountReachedLimit = this.isDataCountReachedLimit(allContactList, ContactCategory.EMAIL);
		viewForm.setCanAddTelContact(!telCountReachedLimit);
		viewForm.setCanAddFaxContact(!faxCountReachedLimit);
		viewForm.setCanAddMailContact(!mailCountReachedLimit);

		// 電話連絡可否、備考をフォームに設定
		TPersonEntity personEntity = this.getPersonEntity(personId);
		if (StringUtils.isNotEmpty(personEntity.getContactType())) {
			viewForm.setAllowType(AllowType.of(personEntity.getContactType()));
		}
		if (StringUtils.isNotEmpty(personEntity.getContactRemarks())) {
			viewForm.setAllowTypeRemarks(personEntity.getContactRemarks());
		}

		return viewForm;
	}

	/**
	 * 連絡先情報入力フォームを取得する
	 * 
	 * @param personId
	 * @param category 連絡先カテゴリ（電話、FAXなど）
	 * @param contactSeq 連絡先レコード連番
	 * @return
	 * @throws AppException
	 */
	public PersonEditInputForm.PersonContactInputForm createPersonContactInputForm(Long personId, ContactCategory category, Long contactSeq)
			throws AppException {

		PersonEditInputForm.PersonContactInputForm inputForm = this.getInitializedPersonContactInputForm(personId);

		// 連絡先カテゴリ設定
		inputForm.setCategory(category);

		if (contactSeq == null) {
			// 新規登録の場合

			// 現状では、特に情報の設定はない

		} else {
			// 更新の場合

			TPersonContactEntity entity = tPersonContactDao.selectBySeq(contactSeq);
			if (entity == null) {
				// 楽観ロックエラー
				logger.warn("データが他のユーザーに削除されたか、不正に書き換えられたSEQが送られてきたため一致する検索データが見つからなかった。");
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			// 連絡先データ連番設定
			inputForm.setContactSeq(contactSeq);

			// 連絡先データ設定
			switch (category) {
			case TEL:
				// 変更しようとしている連絡先情報の項目が空の場合は楽観ロックエラー
				if (StringUtils.isAllEmpty(entity.getTelNoType(), entity.getTelNo())) {
					// 楽観ロックエラー
					logger.warn("連絡先データが他のユーザーに削除された。");
					throw new AppException(MessageEnum.MSG_E00025, null);
				}
				this.setContactDataToForm(inputForm, entity.getYusenTelFlg(), entity.getTelNoType(),
						PersonEditInputForm.PersonContactInputForm::setValueTel, entity.getTelNo(), entity.getTelRemarks());
				break;
			case FAX:
				// 変更しようとしている連絡先情報の項目が空の場合は楽観ロックエラー
				if (StringUtils.isAllEmpty(entity.getFaxNoType(), entity.getFaxNo())) {
					// 楽観ロックエラー
					logger.warn("連絡先データが他のユーザーに削除された。");
					throw new AppException(MessageEnum.MSG_E00025, null);
				}
				this.setContactDataToForm(inputForm, entity.getYusenFaxFlg(), entity.getFaxNoType(),
						PersonEditInputForm.PersonContactInputForm::setValueFax, entity.getFaxNo(), entity.getFaxRemarks());
				break;
			case EMAIL:
				// 変更しようとしている連絡先情報の項目が空の場合は楽観ロックエラー
				if (StringUtils.isAllEmpty(entity.getMailAddressType(), entity.getMailAddress())) {
					// 楽観ロックエラー
					logger.warn("連絡先データが他のユーザーに削除された。");
					throw new AppException(MessageEnum.MSG_E00025, null);
				}
				this.setContactDataToForm(inputForm, entity.getYusenMailAddressFlg(), entity.getMailAddressType(),
						PersonEditInputForm.PersonContactInputForm::setValueMail, entity.getMailAddress(), entity.getMailAddressRemarks());
				break;
			default:
			}
		}

		// 以下、登録、更新で共通のフォーム設定

		// 同じカテゴリの連絡先データがすでに「優先」選択をしているかどうか
		boolean alreadySettingYusenFlg = this.alreadySettingYusenFlg(personId, inputForm);
		inputForm.setAlreadySettingYusenFlg(alreadySettingYusenFlg);

		return inputForm;
	}

	/**
	 * 電話連絡情報入力フォームを取得する
	 * 
	 * @param personId
	 * @return
	 * @throws AppException
	 */
	public PersonEditInputForm.PersonAllowTypeInputForm createPersonAllowTypeInputForm(Long personId)
			throws AppException {

		PersonEditInputForm.PersonAllowTypeInputForm inputForm = this.getInitializedPersonAllowTypeInputForm(personId);

		// 電話連絡可否、備考をフォームに設定
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (StringUtils.isNotEmpty(personEntity.getContactType())) {
			inputForm.setAllowType(personEntity.getContactType());
		}
		if (StringUtils.isNotEmpty(personEntity.getContactRemarks())) {
			inputForm.setAllowTypeRemarks(personEntity.getContactRemarks());
		}

		return inputForm;
	}

	/**
	 * 初期設定済みの顧客の連絡先情報入力フォームを取得
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditInputForm.PersonContactInputForm getInitializedPersonContactInputForm(Long personId) {

		PersonEditInputForm.PersonContactInputForm inputForm = new PersonEditInputForm.PersonContactInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm);

		return inputForm;
	}

	/**
	 * 初期設定済みの電話連絡情報入力フォームを取得
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditInputForm.PersonAllowTypeInputForm getInitializedPersonAllowTypeInputForm(Long personId) {

		PersonEditInputForm.PersonAllowTypeInputForm inputForm = new PersonEditInputForm.PersonAllowTypeInputForm();

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
	public void setDisplayData(PersonEditInputForm.PersonContactInputForm inputForm) {

		// 表示用のデータの設定処理は現状では特にない

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
	public void setDisplayData(PersonEditInputForm.PersonAllowTypeInputForm inputForm) {

		// 表示用のデータの設定処理は現状では特にない

	}

	/**
	 * 連絡先情報の保存処理用のバリデーション処理。<br>
	 * 下記のバリデーションチェックを行う
	 * 
	 * <pre>
	 * ・連絡先データの上限数チェック（楽観ロックエラー）
	 * </pre>
	 * 
	 * @param contactInputForm 画面の入力データ（保存しようとしているデータ）
	 * @param personId
	 * @throws AppException
	 */
	public void accessDBValidatedForSaveCustomerContact(PersonEditInputForm.PersonContactInputForm contactInputForm, Long personId)
			throws AppException {

		// 保存しようとしている連絡先データのカテゴリ
		ContactCategory inputCategory = contactInputForm.getCategory();
		if (inputCategory == null) {
			throw new IllegalStateException("保存時には連絡先のカテゴリはかならず設定されているはずだが、設定されていない。");
		}

		// 現在登録されている連絡先情報を取得
		List<PersonContactDto> nowRegistContactList = this.getNowRegisContactListExcludeEmpty(personId);

		Long updateContactSeq = contactInputForm.getContactSeq();
		if (updateContactSeq == null) {
			// 新規登録の場合

			// 連絡先の登録上限値チェック
			boolean inputCategoryContactCountReachedLimit = this.isDataCountReachedLimit(nowRegistContactList,
					inputCategory);
			if (inputCategoryContactCountReachedLimit) {
				// 上限値に達している場合
				// ※上限に達している場合、追加登録用の選択肢が画面に表示されないようになっており、このケースとなるのは
				// 他のユーザーと操作のタイミングがかぶった場合（排他の場合）のみとなる
				logger.warn("[楽観ロックエラー] 他のユーザーが連絡先を新規登録し、対象カテゴリの登録上限に達したため、登録処理は不可。");
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			return;
		}

		// 以下、更新の場合

	}

	/**
	 * 連絡先情報の保存
	 * 
	 * @param personId
	 * @param basicInputForm
	 * @throws AppException
	 */
	public void saveCustomerContact(Long personId, PersonEditInputForm.PersonContactInputForm contactInputForm) throws AppException {

		// 保存する連絡先のカテゴリ（前段のバリデーション処理でnullチェック済み）
		ContactCategory inputCategory = contactInputForm.getCategory();

		// 画面からの入力データが空かどうか
		boolean contactIsEmpty = contactInputForm.isEmpty();

		Long updateContactSeq = contactInputForm.getContactSeq();
		if (updateContactSeq == null) {
			// 新規登録の場合

			if (contactIsEmpty) {
				// 登録データが空の場合はなにもしない

			} else {

				if (contactInputForm.isYusenFlg()) {
					// 優先フラグが立っている場合
					// 対象カテゴリの他の連絡先の優先フラグを初期化する（更新）
					this.clearUpdateYusenFlg(personId, inputCategory);
				}

				// 登録処理を行う
				this.registContact(personId, inputCategory, contactInputForm);
			}
		} else {
			// 更新の場合

			if (contactIsEmpty) {
				// フォームの値が空の状態の場合 -> 対象データの削除処理を行う
				this.deleteContact(updateContactSeq, inputCategory);
			} else {

				if (contactInputForm.isYusenFlg()) {
					// 優先フラグが立っている場合
					// 対象カテゴリの他の連絡先の優先フラグを初期化する（更新）
					this.clearUpdateYusenFlg(personId, inputCategory);
				}

				// 更新処理を行う
				this.updateContact(updateContactSeq, inputCategory, contactInputForm);
			}
		}
	}

	/**
	 * 対象顧客の、対象カテゴリの全ての連絡先情報について、優先フラグの値を初期値で更新する。
	 * 
	 * @param personId
	 * @param inputCategory
	 * @throws AppException
	 */
	private void clearUpdateYusenFlg(Long personId, ContactCategory inputCategory) throws AppException {

		List<TPersonContactEntity> personContactEntityList = tPersonContactDao.selectPersonContactByPersonId(personId);

		// 対象カテゴリの優先フラグの値を初期化
		this.clearCategoryYusenTelFlgValueToEntitys(personContactEntityList, inputCategory);

		// 更新件数
		int[] resultArray = null;

		try {
			// 更新
			resultArray = tPersonContactDao.update(personContactEntityList);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		if (resultArray.length != personContactEntityList.size()) {
			// 実際の更新件数と更新対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00036, null);
		}
	}

	/**
	 * 連絡先情報の削除
	 * 
	 * @param categoryCd 連絡先区分コード
	 * @param contactSeq 連絡先SEQ
	 * @throws AppException
	 */
	public void deleteContact(Long categoryCd, Long contactSeq) throws AppException {

		// 連絡先区分取得
		ContactCategory inputCategory = ContactCategory.of(categoryCd.toString());

		// 連絡先削除
		this.deleteContact(contactSeq, inputCategory);
	}

	/**
	 * 電話連絡情報の保存
	 * 
	 * @param personId
	 * @param allowTypeInputForm
	 * @throws AppException
	 */
	public void saveCustomerContact(Long personId, PersonEditInputForm.PersonAllowTypeInputForm allowTypeInputForm) throws AppException {

		// 名簿データ取得
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);

		// 電話連絡情報をセット
		personEntity.setContactType(allowTypeInputForm.getAllowType());
		personEntity.setContactRemarks(allowTypeInputForm.getAllowTypeRemarks());

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPersonDao.update(personEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 電話連絡情報の削除
	 * 
	 * @param personId
	 * @throws AppException
	 */
	public void deleteAllowType(Long personId) throws AppException {

		// 名簿データ取得
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);

		// 電話連絡情報をブランクで更新
		personEntity.setContactType(null);
		personEntity.setContactRemarks(null);

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPersonDao.update(personEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	// ▼ 口座情報に関する処理

	/**
	 * 口座情報表示フォームを取得する
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditViewForm.PersonKozaViewForm createPersonKozaViewForm(Long personId) {

		PersonEditViewForm.PersonKozaViewForm viewForm = new PersonEditViewForm.PersonKozaViewForm();

		// 表示フォームに表示情報を取得する
		TPersonEntity personEntity = this.getPersonEntity(personId);

		// 口座情報をフォームに設定
		PersonKozaDto kozaDto = new PersonKozaDto();
		kozaDto.setGinkoName(personEntity.getGinkoName());
		kozaDto.setShitenName(personEntity.getShitenName());
		kozaDto.setShitenNo(personEntity.getShitenNo());
		kozaDto.setKozaType(KozaType.of(personEntity.getKozaType()));
		kozaDto.setKozaNo(personEntity.getKozaNo());
		kozaDto.setKozaName(personEntity.getKozaName());
		kozaDto.setKozaNameKana(personEntity.getKozaNameKana());
		kozaDto.setKozaRemarks(personEntity.getKozaRemarks());
		if (!kozaDto.isEmpty()) {
			viewForm.setKoza(kozaDto);
		}

		return viewForm;
	}

	/**
	 * 口座情報入力フォームを取得する
	 * 
	 * @param personId
	 * @param isNewRegist
	 * @return
	 */
	public PersonEditInputForm.PersonKozaInputForm createPersonKozaInputForm(Long personId,
			boolean isNewRegist) throws AppException {

		PersonEditInputForm.PersonKozaInputForm inputForm = this.getInitializedPersonKozaInputForm(personId);

		if (isNewRegist) {
			// 新規登録フォームの場合
			TPersonEntity personEntity = this.getPersonEntity(personId);
			// 新規登録時に口座情報が有る場合は楽観ロックエラー
			if (StringUtils.isAllEmpty(personEntity.getGinkoName(),
					personEntity.getShitenName(), personEntity.getShitenNo(), personEntity.getKozaType(),personEntity.getKozaNo(),
					personEntity.getKozaName(), personEntity.getKozaNameKana(), personEntity.getKozaRemarks())) {
				inputForm.setNewRegist(isNewRegist);
			} else {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}


		} else {
			// 更新フォームの場合

			TPersonEntity personEntity = this.getPersonEntity(personId);

			// 更新時に口座情報が無い場合は楽観ロックエラー
			if (StringUtils.isAllEmpty(personEntity.getGinkoName(), personEntity.getShitenName(),
					personEntity.getShitenNo(), personEntity.getKozaType(), personEntity.getKozaNo(),
					personEntity.getKozaName(), personEntity.getKozaNameKana(), personEntity.getKozaRemarks())) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}
			
			// 口座情報をフォームに設定
			inputForm.setGinkoName(personEntity.getGinkoName());
			inputForm.setShitenName(personEntity.getShitenName());
			inputForm.setShitenNo(personEntity.getShitenNo());
			inputForm.setKozaType(KozaType.of(personEntity.getKozaType()));
			inputForm.setKozaNo(personEntity.getKozaNo());
			inputForm.setKozaName(personEntity.getKozaName());
			inputForm.setKozaNameKana(personEntity.getKozaNameKana());
			inputForm.setKozaRemarks(personEntity.getKozaRemarks());
		}

		return inputForm;
	}

	/**
	 * 初期設定済みの口座情報入力フォームを取得
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditInputForm.PersonKozaInputForm getInitializedPersonKozaInputForm(Long personId) {

		PersonEditInputForm.PersonKozaInputForm inputForm = new PersonEditInputForm.PersonKozaInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm, personId);

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
	 * @param personId
	 */
	public void setDisplayData(PersonEditInputForm.PersonKozaInputForm inputForm, Long personId) {

		// 現状は特に処理なし
	}

	/**
	 * 口座情報の保存
	 * 
	 * @param customerId
	 * @param kozaInputForm
	 * @throws AppException
	 */
	public void savePersonKoza(Long personId, PersonEditInputForm.PersonKozaInputForm kozaInputForm) throws AppException {

		// 口座情報は、顧客テーブルのレコードとなり、
		// 顧客が存在する場合、既にレコードは存在しているため、口座情報での新規登録というものはなく、
		// 口座情報の保存は、新規も更新も同じ処理となる。

		TPersonEntity updateEntity = this.getPersonEntity(personId);

		// 新規登録時に口座情報が有る場合は楽観ロックエラー
		if (kozaInputForm.isNewRegist()
				&& !StringUtils.isAllEmpty(updateEntity.getGinkoName(), updateEntity.getShitenName(),
						updateEntity.getShitenNo(), updateEntity.getKozaType(), updateEntity.getKozaNo(),
						updateEntity.getKozaName(), updateEntity.getKozaNameKana(), updateEntity.getKozaRemarks())) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateEntity.setGinkoName(kozaInputForm.getGinkoName());
		updateEntity.setShitenName(kozaInputForm.getShitenName());
		updateEntity.setShitenNo(kozaInputForm.getShitenNo());
		updateEntity.setKozaType(DefaultEnum.getCd(kozaInputForm.getKozaType()));
		updateEntity.setKozaNo(kozaInputForm.getKozaNo());
		updateEntity.setKozaName(kozaInputForm.getKozaName());
		updateEntity.setKozaNameKana(kozaInputForm.getKozaNameKana());
		updateEntity.setKozaRemarks(kozaInputForm.getKozaRemarks());

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPersonDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 口座情報の削除
	 * 
	 * @param personId 名簿ID
	 * @throws AppException
	 */
	public void deleteKoza(Long personId) throws AppException {

		// 口座情報は、顧客テーブルのレコードとなるため
		// レコードを削除するのではなく、顧客テーブルの銀行に関する情報をnullで更新する。

		TPersonEntity updateEntity = this.getPersonEntity(personId);

		// 口座情報が削除されている場合は楽観ロックエラー
		if (StringUtils.isAllEmpty(updateEntity.getGinkoName(), updateEntity.getShitenName(),
				updateEntity.getShitenNo(), updateEntity.getKozaType(), updateEntity.getKozaNo(),
				updateEntity.getKozaName(), updateEntity.getKozaNameKana(), updateEntity.getKozaRemarks())) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateEntity.setGinkoName(null);
		updateEntity.setShitenName(null);
		updateEntity.setShitenNo(null);
		updateEntity.setKozaType(null);
		updateEntity.setKozaNo(null);
		updateEntity.setKozaName(null);
		updateEntity.setKozaNameKana(null);
		updateEntity.setKozaRemarks(null);

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPersonDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	// ▼ 案件情報に関する処理

	/**
	 * 案件情報表示フォームを取得する
	 * 
	 * @param customerId
	 * @return
	 */
	public PersonEditViewForm.CustomerAnkenViewForm createCustomerAnkenViewForm(Long customerId, boolean isAllAnkenList) {

		PersonEditViewForm.CustomerAnkenViewForm viewForm = new PersonEditViewForm.CustomerAnkenViewForm();

		// 顧客IDをセット
		viewForm.setCustomerId(CustomerId.of(customerId));

		// 顧客に紐づく案件を取得
		List<AnkenDto> ankenList = commonAnkenService.getAnkenListByCustomerId(customerId, isAllAnkenList);
		viewForm.setAnkenList(ankenList);
		viewForm.setAllAnkenList(isAllAnkenList);

		return viewForm;
	}

	// ▼ 関連する案件情報に関する処理

	/**
	 * 関連する案件情報表示フォームを取得する
	 * 
	 * @param customerId
	 * @param isAllAnkenList
	 * @return
	 */
	public PersonEditViewForm.RelatedAnkenViewForm createRelatedAnkenViewForm(Long customerId, boolean isAllAnkenList) {

		PersonEditViewForm.RelatedAnkenViewForm viewForm = new PersonEditViewForm.RelatedAnkenViewForm();

		// 顧客ID、全件取得フラグをセット
		viewForm.setCustomerId(CustomerId.of(customerId));
		viewForm.setAllAnkenList(isAllAnkenList);

		// 関与者に設定されている案件を取得
		List<AnkenDto> ankenList = commonAnkenService.getRelatedAnkenListByCustomerId(customerId, isAllAnkenList);
		viewForm.setAnkenList(ankenList);

		// 全ステータスの関連する案件数を取得
		List<AnkenDto> ankenCountList = commonAnkenService.getRelatedAnkenListByCustomerId(customerId, true);
		if (LoiozCollectionUtils.isNotEmpty(ankenCountList)) {
			viewForm.setAnkenCount(ankenCountList.size());
		} else {
			viewForm.setAnkenCount(0);
		}

		return viewForm;
	}

	// ▼ 予定情報に関する処理

	/**
	 * スケジュール一覧情報表示フォームを取得する
	 * 
	 * @param personId
	 * @return
	 */
	public PersonEditViewForm.PersonScheduleListViewForm createPersonScheduleListViewForm(Long personId, boolean isAllTimeScheduleList) {

		PersonEditViewForm.PersonScheduleListViewForm viewForm = new PersonEditViewForm.PersonScheduleListViewForm();

		// 名簿情報の取得
		TPersonEntity personEntity = getPersonEntity(personId);
		viewForm.setPersonId(personId);
		viewForm.setCustomerId(CustomerId.of(personEntity.getCustomerId()));
		viewForm.setCustomerName(PersonName.fromEntity(personEntity).getName());

		// 検索値の設定
		viewForm.setAllTimeScheduleList(isAllTimeScheduleList);

		// アカウント名Map
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// 部署名Map
		List<MRoomEntity> mRoomEntities = mRoomDao.selectAll();
		Map<Long, String> roomNameMap = mRoomEntities.stream().collect(Collectors.toMap(MRoomEntity::getRoomId, MRoomEntity::getRoomName));

		// 顧客に紐づくスケジュール情報を取得
		List<Long> scheduleSeqList = personEditDao.selectSeqByConditions(isAllTimeScheduleList, personEntity.getCustomerId(), SessionUtils.getLoginAccountSeq());
		List<CustomerScheduleListBean> customerScheduleListBeans = personEditDao.selectScheduleListBean(scheduleSeqList);

		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByShokaiMendanScheduleSeq(scheduleSeqList);
		// 予定SEQから案件-顧客エンティティを取得するMap
		Map<Long, List<TAnkenCustomerEntity>> scheduleSeqToAnkenCustomerEntityMap = tAnkenCustomerEntities.stream()
				.collect(Collectors.groupingBy(TAnkenCustomerEntity::getShokaiMendanScheduleSeq));

		// データの変換処理
		Map<Long, List<CustomerScheduleListBean>> scheduleSeqToMap = customerScheduleListBeans.stream()
				.collect(Collectors.groupingBy(CustomerScheduleListBean::getScheduleSeq, LinkedHashMap::new, Collectors.toList()));
		List<PersonScheduleListDto> peronScheduleListDtos = scheduleSeqToMap.values().stream()
				.map(e -> {
					PersonScheduleListDto dto = new PersonScheduleListDto();

					CustomerScheduleListBean customerScheduleListBean = e.stream().findFirst().get();
					dto.setScheduleSeq(customerScheduleListBean.getScheduleSeq());
					dto.setCustomerId(customerScheduleListBean.getCustomerId());
					dto.setAnkenId(customerScheduleListBean.getAnkenId());
					dto.setSaibanSeq(customerScheduleListBean.getSaibanSeq());
					dto.setSaibanBranchNo(customerScheduleListBean.getSaibanBranchNo());
					dto.setSaibanLimitSeq(customerScheduleListBean.getSaibanLimitSeq());
					dto.setSubject(customerScheduleListBean.getSubject());
					dto.setAllDay(SystemFlg.codeToBoolean(customerScheduleListBean.getAllDayFlg()));
					dto.setDateFrom(customerScheduleListBean.getDateFrom());
					dto.setTimeFrom(customerScheduleListBean.getTimeFrom());
					dto.setDateTo(customerScheduleListBean.getDateTo());
					dto.setTimeTo(customerScheduleListBean.getTimeTo());
					if (customerScheduleListBean.getRoomId() == null) {
						dto.setPlace(customerScheduleListBean.getPlace());
					} else {
						dto.setPlace(roomNameMap.get(customerScheduleListBean.getRoomId()));
					}

					// 初回面談かどうかを判別
					dto.setShokaiMendan(scheduleSeqToAnkenCustomerEntityMap.containsKey(customerScheduleListBean.getScheduleSeq()));

					// 子要素の作成
					List<String> accounNames = e.stream().map(bean -> {
						return accountNameMap.get(bean.getAccountSeq());
					}).collect(Collectors.toList());
					dto.setAccountNameList(accounNames);

					return dto;
				})
				.collect(Collectors.toList());

		viewForm.setScheduleList(peronScheduleListDtos);
		return viewForm;
	}

	/**
	 * 名簿予定詳細情報の取得
	 * 
	 * @param personId
	 * @param isAllTimeScheduleList
	 * @return
	 */
	public Map<Long, ScheduleDetail> getPersonScheduleDetails(Long personId, boolean isAllTimeScheduleList) {

		// 名簿情報の取得
		TPersonEntity personEntity = getPersonEntity(personId);

		// 顧客に紐づくスケジュール情報を取得
		List<Long> scheduleSeqList = personEditDao.selectSeqByConditions(isAllTimeScheduleList, personEntity.getCustomerId(), SessionUtils.getLoginAccountSeq());

		// 共通予定サービス処理：予定詳細情報の取得
		return commonScheduleService.getSchedulePKOne(scheduleSeqList);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	// ▼ 名簿の基本情報に関する処理

	/**
	 * 名簿の基本情報を更新する
	 * 
	 * @param personId
	 * @param basicInputForm
	 */
	private void updatePersonBasic(Long personId, PersonEditInputForm.PersonBasicInputForm basicInputForm) {

		// 名簿情報
		TPersonEntity entity = this.getPersonEntity(personId);

		// 種別（個人／企業・団体／弁護士）、登録日
		entity.setCustomerType(DefaultEnum.getCd(basicInputForm.getCustomerType()));
		entity.setCustomerCreatedDate(basicInputForm.getCustomerCreatedDate());

		// 姓
		entity.setCustomerNameSei(basicInputForm.getCustomerNameSei());
		entity.setCustomerNameSeiKana(basicInputForm.getCustomerNameSeiKana());

		// 名
		entity.setCustomerNameMei(basicInputForm.getCustomerNameMei());
		entity.setCustomerNameMeiKana(basicInputForm.getCustomerNameMeiKana());

		// 特記事項
		entity.setRemarks(basicInputForm.getRemarks());

		// 相談経路
		entity.setSodanRoute(basicInputForm.getSodanRoute());
		entity.setSodanRemarks(basicInputForm.getSodanRemarks());

		tPersonDao.update(entity);
	}

	/**
	 * 個人名簿付帯情報を更新する
	 * 
	 * @param personId
	 * @param basicInputForm
	 */
	private void updateCustomerAddKojin(Long personId, PersonEditInputForm.PersonBasicInputForm basicInputForm) {

		TPersonAddKojinEntity entity = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personId);

		boolean existsEntity = true;
		if (entity == null) {
			existsEntity = false;
			entity = new TPersonAddKojinEntity();
			entity.setPersonId(personId);
		}

		// 旧姓
		entity.setOldName(basicInputForm.getOldName());
		entity.setOldNameKana(basicInputForm.getOldNameKana());

		// 生年月日（西暦／和暦からLocalDateへ変換）
		EraType birthEra = basicInputForm.getBirthEra();
		Long birthYear = basicInputForm.getBirthYear();
		Long birthMonth = basicInputForm.getBirthMonth();
		Long birthDay = basicInputForm.getBirthDay();
		if (LoiozObjectUtils.allNotNull(birthEra, birthYear, birthMonth, birthDay)) {
			entity.setBirthday(DateUtils.parseToLocalDate(birthEra.getCd(), birthYear, birthMonth, birthDay));
			entity.setBirthdayDisplayType(birthEra.getCd());
		} else {
			entity.setBirthday(null);
			entity.setBirthdayDisplayType(null);
		}

		// 性別
		entity.setGenderType(DefaultEnum.getCd(basicInputForm.getGender()));

		// 国籍、言語
		entity.setCountry(basicInputForm.getCountry());
		entity.setLanguage(basicInputForm.getLanguage());

		// 屋号
		entity.setYago(basicInputForm.getYago());
		entity.setYagoKana(basicInputForm.getYagoKana());

		// 職業
		entity.setJob(basicInputForm.getJob());

		// 勤務先
		entity.setWorkPlace(basicInputForm.getWorkPlace());

		// 部署
		entity.setBushoName(basicInputForm.getBushoName());

		// 死亡フラグ
		entity.setDeathFlg(basicInputForm.getDeathFlg() ? DeathFlg.FLG_ON.getCd() : DeathFlg.FLG_OFF.getCd());

		// 死亡日（故人チェックボックスにチェックがある場合のみ）
		if (basicInputForm.getDeathFlg()) {
			// 西暦／和暦からLocalDateへ変換
			EraType deathEra = basicInputForm.getDeathEra();
			Long deathYear = basicInputForm.getDeathYear();
			Long deathMonth = basicInputForm.getDeathMonth();
			Long deathDay = basicInputForm.getDeathDay();
			if (LoiozObjectUtils.allNotNull(deathEra, deathYear, deathMonth, deathDay)) {
				entity.setDeathDate(DateUtils.parseToLocalDate(deathEra.getCd(), deathYear, deathMonth, deathDay));
				entity.setDeathDateDisplayType(deathEra.getCd());
			} else {
				entity.setDeathDate(null);
				entity.setDeathDateDisplayType(null);
			}
		} else {
			// 個人チェックボックスにチェックが無い場合はnullで上書きする
			entity.setDeathDate(null);
			entity.setDeathDateDisplayType(null);
		}

		if (existsEntity) {
			tPersonAddKojinDao.update(entity);
		} else {
			tPersonAddKojinDao.insert(entity);
		}
	}

	/**
	 * 法人名簿付帯情報を更新する
	 * 
	 * @param personId
	 * @param basicInputForm
	 */
	private void updateCustomerAddHojin(Long personId, PersonEditInputForm.PersonBasicInputForm basicInputForm) {

		TPersonAddHojinEntity entity = tPersonAddHojinDao.selectPersonAddHojinByPersonId(personId);

		boolean existsEntity = true;
		if (entity == null) {
			existsEntity = false;
			entity = new TPersonAddHojinEntity();
			entity.setPersonId(personId);
		}

		// 代表
		entity.setDaihyoName(basicInputForm.getDaihyoName());
		entity.setDaihyoNameKana(basicInputForm.getDaihyoNameKana());
		entity.setDaihyoPositionName(basicInputForm.getDaihyoPositionName());

		// 担当
		entity.setTantoName(basicInputForm.getTantoName());
		entity.setTantoNameKana(basicInputForm.getTantoNameKana());
		
		// 旧商号
		entity.setOldHojinName(basicInputForm.getOldHojinName());

		if (existsEntity) {
			tPersonAddHojinDao.update(entity);
		} else {
			tPersonAddHojinDao.insert(entity);
		}
	}

	/**
	 * 弁護士付帯情報を更新する
	 * 
	 * @param personId
	 * @param basicInputForm
	 */
	private void updatePersonAddLayer(Long personId, PersonEditInputForm.PersonBasicInputForm basicInputForm) {

		List<TPersonAddLawyerEntity> tPersonAddLawyerEntityList = tPersonAddLawyerDao.selectPersonAddLawyerByPersonId(List.of(personId));
		TPersonAddLawyerEntity tPersonAddLawyerEntity = new TPersonAddLawyerEntity();

		boolean existsEntity = true;
		if (LoiozCollectionUtils.isEmpty(tPersonAddLawyerEntityList)) {
			existsEntity = false;
			// 名簿ID
			tPersonAddLawyerEntity.setPersonId(personId);
		} else {
			tPersonAddLawyerEntity = tPersonAddLawyerEntityList.stream().findFirst().get();
		}

		// 事務所名
		tPersonAddLawyerEntity.setJimushoName(basicInputForm.getLawyerJimushoName());

		// 部署・役職名
		tPersonAddLawyerEntity.setBushoName(basicInputForm.getLawyerBushoName());

		if (existsEntity) {
			tPersonAddLawyerDao.update(tPersonAddLawyerEntity);
		} else {
			tPersonAddLawyerDao.insert(tPersonAddLawyerEntity);
		}
	}

	// ▼ 名簿の住所情報に関する処理

	/**
	 * 【個人】名簿の住所情報の表示データを取得する
	 * 
	 * @param personEntity
	 * @return
	 */
	private List<PersonAddressDto> getKojinAddressRowDtoList(TPersonEntity personEntity) {

		CustomerType customerType = CustomerType.of(personEntity.getCustomerType());
		if (customerType != CustomerType.KOJIN) {
			throw new IllegalArgumentException("顧客タイプは個人でなければいけません。");
		}

		Long personId = personEntity.getPersonId();

		// 表示する住所一覧データ
		List<PersonAddressDto> addressRowDtoList = new ArrayList<>();

		// 居住地
		PersonAddressDto kyojuuchiDto = new PersonAddressDto();
		kyojuuchiDto.setCustomerType(customerType);
		kyojuuchiDto.setBaseAddress(true);
		kyojuuchiDto.setZipCode(personEntity.getZipCode());
		kyojuuchiDto.setAddress1(personEntity.getAddress1());
		kyojuuchiDto.setAddress2(personEntity.getAddress2());
		kyojuuchiDto.setRemarks(personEntity.getAddressRemarks());
		if (personEntity.getTransferType() != null) {
			kyojuuchiDto.setTransferTypeName(MailingType.of(personEntity.getTransferType()).getVal());
		}
		if (!kyojuuchiDto.isEmpty()) {
			addressRowDtoList.add(kyojuuchiDto);
		}

		// 個人名簿付帯情報を取得
		PersonBean personAddKojin = tPersonAddKojinDao.selectAddKojinAndTransferTypeByPersonId(personId);
		AddressTypeKojin addressTypeKojin = AddressTypeKojin.of(personAddKojin.getJuminhyoAddressType());

		// 住民票登録地
		PersonAddressDto tourokuAddressDto = null;
		if (addressTypeKojin != null) {
			if (addressTypeKojin == AddressTypeKojin.KYOJUUCHI) {
				// 居住地と同じ -> 居住地のデータに住民票登録地として利用されているフラグをつける
				if (!kyojuuchiDto.isEmpty()) {
					kyojuuchiDto.setTourokuAddress(true);
				}
			} else {
				// 住民票登録地独自のデータを追加
				tourokuAddressDto = new PersonAddressDto();
				tourokuAddressDto.setCustomerType(customerType);
				tourokuAddressDto.setTourokuAddress(true);
				tourokuAddressDto.setZipCode(personAddKojin.getJuminhyoZipCode());
				tourokuAddressDto.setAddress1(personAddKojin.getJuminhyoAddress1());
				tourokuAddressDto.setAddress2(personAddKojin.getJuminhyoAddress2());
				tourokuAddressDto.setRemarks(personAddKojin.getJuminhyoRemarks());
				if (personAddKojin.getTransferType() != null) {
					tourokuAddressDto.setTransferTypeName(MailingType.of(personAddKojin.getTransferType()).getVal());
				}
				addressRowDtoList.add(tourokuAddressDto);
			}
		}

		// 郵送先住所
		TransferAddressType transferAddressType = TransferAddressType.of(personEntity.getTransferAddressType());
		if (transferAddressType != null) {
			if (transferAddressType == TransferAddressType.KYOJUUCHI) {
				// 居住地と同じ -> 居住地のデータに郵送先住所として利用されているフラグをつける
				if (!kyojuuchiDto.isEmpty()) {
					kyojuuchiDto.setTransferAddress(true);
				}
			} else {
				// 郵送先住所独自のデータ
				PersonAddressDto transferAddressDto = new PersonAddressDto();
				transferAddressDto.setCustomerType(customerType);
				transferAddressDto.setTransferAddress(true);
				transferAddressDto.setZipCode(personEntity.getTransferZipCode());
				transferAddressDto.setAddress1(personEntity.getTransferAddress1());
				transferAddressDto.setAddress2(personEntity.getTransferAddress2());
				transferAddressDto.setRemarks(personEntity.getTransferRemarks());
				if (personEntity.getTransferType() != null) {
					transferAddressDto.setTransferTypeName(MailingType.of(personEntity.getTransferType()).getVal());
				}
				if (transferAddressDto.isSameAddressData(tourokuAddressDto)) {
					// 郵送先住所が住民票登録地と同じ住所データの場合
					// - > 画面表示上はひとつのデータとする（住民票登録地側のデータに郵送先住所としても利用されているフラグをつける）
					tourokuAddressDto.setTransferAddress(true);
				} else {
					// データが異なる場合 -> 郵送先住所は独自のデータとして表示データに追加する
					addressRowDtoList.add(transferAddressDto);
				}
			}
		}

		// その他住所（旧住所）
		List<TOldAddressEntity> oldAddressEntityList = tOldAddressDao.selectOldAddressByPersonId(personId);
		List<PersonAddressDto> oldAddressDtoList = oldAddressEntityList.stream()
				.map(entity -> {
					PersonAddressDto oldAddressDto = new PersonAddressDto();
					oldAddressDto.setCustomerType(customerType);
					oldAddressDto.setOldAddressSeq(entity.getOldAddressSeq());
					oldAddressDto.setZipCode(entity.getZipCode());
					oldAddressDto.setAddress1(entity.getAddress1());
					oldAddressDto.setAddress2(entity.getAddress2());
					oldAddressDto.setRemarks(entity.getRemarks());
					return oldAddressDto;
				})
				.collect(Collectors.toCollection(ArrayList::new));
		addressRowDtoList.addAll(oldAddressDtoList);

		return addressRowDtoList;
	}

	/**
	 * 【企業・団体】顧客の住所情報の表示データを取得する
	 * 
	 * @param personEntity
	 * @return
	 */
	private List<PersonAddressDto> getHojinAddressRowDtoList(TPersonEntity personEntity) {

		CustomerType customerType = CustomerType.of(personEntity.getCustomerType());
		if (customerType != CustomerType.HOJIN) {
			throw new IllegalArgumentException("顧客タイプは企業・団体でなければいけません。");
		}

		Long personId = personEntity.getPersonId();

		// 表示する住所一覧データ
		List<PersonAddressDto> addressRowDtoList = new ArrayList<>();

		// 所在地
		PersonAddressDto shozaichiDto = new PersonAddressDto();
		shozaichiDto.setCustomerType(customerType);
		shozaichiDto.setBaseAddress(true);
		shozaichiDto.setZipCode(personEntity.getZipCode());
		shozaichiDto.setAddress1(personEntity.getAddress1());
		shozaichiDto.setAddress2(personEntity.getAddress2());
		shozaichiDto.setRemarks(personEntity.getAddressRemarks());
		if (personEntity.getTransferType() != null) {
			shozaichiDto.setTransferTypeName(MailingType.of(personEntity.getTransferType()).getVal());
		}
		if (!shozaichiDto.isEmpty()) {
			addressRowDtoList.add(shozaichiDto);
		}

		// 法人名簿付帯情報を取得
		PersonBean personAddHojin = tPersonAddHojinDao.selectAddHojinAndTransferTypeByPersonId(personId);
		AddressTypeHojin addressTypeHojin = AddressTypeHojin.of(personAddHojin.getTokiAddressType());

		// 登記簿登録地
		PersonAddressDto tourokuAddressDto = null;
		if (addressTypeHojin != null) {
			if (addressTypeHojin == AddressTypeHojin.SHOZAICHI) {
				// 所在地と同じ -> 所在地のデータに登記簿登録地として利用されているフラグをつける
				if (!shozaichiDto.isEmpty()) {
					shozaichiDto.setTourokuAddress(true);
				}
			} else {
				// 登記簿登録地独自のデータを追加
				tourokuAddressDto = new PersonAddressDto();
				tourokuAddressDto.setCustomerType(customerType);
				tourokuAddressDto.setTourokuAddress(true);
				tourokuAddressDto.setZipCode(personAddHojin.getTokiZipCode());
				tourokuAddressDto.setAddress1(personAddHojin.getTokiAddress1());
				tourokuAddressDto.setAddress2(personAddHojin.getTokiAddress2());
				tourokuAddressDto.setRemarks(personAddHojin.getTokiAddressRemarks());
				if (personAddHojin.getTransferType() != null) {
					tourokuAddressDto.setTransferTypeName(MailingType.of(personAddHojin.getTransferType()).getVal());
				}
				addressRowDtoList.add(tourokuAddressDto);
			}
		}

		// 郵送先住所
		TransferAddressType transferAddressType = TransferAddressType.of(personEntity.getTransferAddressType());
		if (transferAddressType != null) {
			if (transferAddressType == TransferAddressType.KYOJUUCHI) {
				// 所在地と同じ -> 所在地のデータに郵送先住所として利用されているフラグをつける
				if (!shozaichiDto.isEmpty()) {
					shozaichiDto.setTransferAddress(true);
				}
			} else {
				// 郵送先住所独自のデータ
				PersonAddressDto transferAddressDto = new PersonAddressDto();
				transferAddressDto.setCustomerType(customerType);
				transferAddressDto.setTransferAddress(true);
				transferAddressDto.setZipCode(personEntity.getTransferZipCode());
				transferAddressDto.setAddress1(personEntity.getTransferAddress1());
				transferAddressDto.setAddress2(personEntity.getTransferAddress2());
				transferAddressDto.setRemarks(personEntity.getTransferRemarks());
				if (personEntity.getTransferType() != null) {
					transferAddressDto.setTransferTypeName(MailingType.of(personEntity.getTransferType()).getVal());
				}
				if (transferAddressDto.isSameAddressData(tourokuAddressDto)) {
					// 郵送先住所が登記簿登録地と同じ住所データの場合
					// - > 画面表示上はひとつのデータとする（登記簿登録地側のデータに郵送先住所としても利用されているフラグをつける）
					tourokuAddressDto.setTransferAddress(true);
				} else {
					// データが異なる場合 -> 郵送先住所は独自のデータとして表示データに追加する
					addressRowDtoList.add(transferAddressDto);
				}
			}
		}

		// その他住所（旧住所）
		List<TOldAddressEntity> oldAddressEntityList = tOldAddressDao.selectOldAddressByPersonId(personId);
		List<PersonAddressDto> oldAddressDtoList = oldAddressEntityList.stream()
				.map(entity -> {
					PersonAddressDto oldAddressDto = new PersonAddressDto();
					oldAddressDto.setCustomerType(customerType);
					oldAddressDto.setOldAddressSeq(entity.getOldAddressSeq());
					oldAddressDto.setZipCode(entity.getZipCode());
					oldAddressDto.setAddress1(entity.getAddress1());
					oldAddressDto.setAddress2(entity.getAddress2());
					oldAddressDto.setRemarks(entity.getRemarks());
					return oldAddressDto;
				})
				.collect(Collectors.toCollection(ArrayList::new));
		addressRowDtoList.addAll(oldAddressDtoList);

		return addressRowDtoList;
	}

	/**
	 * 【弁護士】顧客の住所情報の表示データを取得する
	 * 
	 * @param personEntity
	 * @return
	 */
	private List<PersonAddressDto> getLawyerAddressRowDtoList(TPersonEntity personEntity) {

		CustomerType customerType = CustomerType.of(personEntity.getCustomerType());
		if (customerType != CustomerType.LAWYER) {
			throw new IllegalArgumentException("顧客タイプは弁護士でなければいけません。");
		}

		Long personId = personEntity.getPersonId();

		// 表示する住所一覧データ
		List<PersonAddressDto> addressRowDtoList = new ArrayList<>();

		// 居住地
		PersonAddressDto kyojuuchiDto = new PersonAddressDto();
		kyojuuchiDto.setCustomerType(customerType);
		kyojuuchiDto.setBaseAddress(true);
		kyojuuchiDto.setZipCode(personEntity.getZipCode());
		kyojuuchiDto.setAddress1(personEntity.getAddress1());
		kyojuuchiDto.setAddress2(personEntity.getAddress2());
		kyojuuchiDto.setRemarks(personEntity.getAddressRemarks());
		if (personEntity.getTransferType() != null) {
			kyojuuchiDto.setTransferTypeName(MailingType.of(personEntity.getTransferType()).getVal());
		}
		if (!kyojuuchiDto.isEmpty()) {
			addressRowDtoList.add(kyojuuchiDto);
		}

		// 郵送先住所
		TransferAddressType transferAddressType = TransferAddressType.of(personEntity.getTransferAddressType());
		if (transferAddressType != null) {
			if (transferAddressType == TransferAddressType.KYOJUUCHI) {
				// 居住地と同じ -> 居住地のデータに郵送先住所として利用されているフラグをつける
				if (!kyojuuchiDto.isEmpty()) {
					kyojuuchiDto.setTransferAddress(true);
				}
			} else {
				// 郵送先住所独自のデータ
				PersonAddressDto transferAddressDto = new PersonAddressDto();
				transferAddressDto.setCustomerType(customerType);
				transferAddressDto.setTransferAddress(true);
				transferAddressDto.setZipCode(personEntity.getTransferZipCode());
				transferAddressDto.setAddress1(personEntity.getTransferAddress1());
				transferAddressDto.setAddress2(personEntity.getTransferAddress2());
				transferAddressDto.setRemarks(personEntity.getTransferRemarks());
				if (personEntity.getTransferType() != null) {
					transferAddressDto.setTransferTypeName(MailingType.of(personEntity.getTransferType()).getVal());
				}
				if (transferAddressDto.isSameAddressData(kyojuuchiDto)) {
					// 郵送先住所が住民票登録地と同じ住所データの場合
					// - > 画面表示上はひとつのデータとする（住民票登録地側のデータに郵送先住所としても利用されているフラグをつける）
					kyojuuchiDto.setTransferAddress(true);
				} else {
					// データが異なる場合 -> 郵送先住所は独自のデータとして表示データに追加する
					addressRowDtoList.add(transferAddressDto);
				}
			}
		}

		// その他住所（旧住所）
		List<TOldAddressEntity> oldAddressEntityList = tOldAddressDao.selectOldAddressByPersonId(personId);
		List<PersonAddressDto> oldAddressDtoList = oldAddressEntityList.stream()
				.map(entity -> {
					PersonAddressDto oldAddressDto = new PersonAddressDto();
					oldAddressDto.setCustomerType(customerType);
					oldAddressDto.setOldAddressSeq(entity.getOldAddressSeq());
					oldAddressDto.setZipCode(entity.getZipCode());
					oldAddressDto.setAddress1(entity.getAddress1());
					oldAddressDto.setAddress2(entity.getAddress2());
					oldAddressDto.setRemarks(entity.getRemarks());
					return oldAddressDto;
				})
				.collect(Collectors.toCollection(ArrayList::new));
		addressRowDtoList.addAll(oldAddressDtoList);

		return addressRowDtoList;
	}

	/**
	 * 【個人】編集対象の住所データを取得する
	 * 
	 * @param personEntity 名簿情報エンティティ
	 * @param isBaseAddress 対象の住所情報が居住地かどうか
	 * @param isTourokuAddress 対象の住所情報が住民票登録地かどうか
	 * @param isTransferAddress 対象の住所情報が郵送先住所情報かどうか
	 * @param oldAddressSeq その他住所（旧住所）の場合の対象データの連番値
	 * @return
	 */
	private Address getKojinEditAddressData(TPersonEntity personEntity,
			boolean isBaseAddress, boolean isTourokuAddress, boolean isTransferAddress, Long oldAddressSeq) throws AppException {

		if (isBaseAddress) {
			// 居住地

			// 居住地データを取得
			Address address = Address.builder()
					.zipCode(personEntity.getZipCode())
					.address1(personEntity.getAddress1())
					.address2(personEntity.getAddress2())
					.remarks(personEntity.getAddressRemarks())
					.build();

			return address;
		}

		if (isTourokuAddress) {
			// 住民票登録地

			// 住民票登録地を取得
			TPersonAddKojinEntity personAddKojin = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personEntity.getPersonId());
			Address address = Address.builder()
					.zipCode(personAddKojin.getJuminhyoZipCode())
					.address1(personAddKojin.getJuminhyoAddress1())
					.address2(personAddKojin.getJuminhyoAddress2())
					.remarks(personAddKojin.getJuminhyoRemarks())
					.build();

			// 以下でチェックする郵送先住所のチェックがあったとしても、
			// 住民票登録地と郵送先住所の両方がチェックされている場合、２つは同じ住所データとなるため、
			// この時点で住民票登録地のデータを住所情報として返却して問題ない。
			return address;
		}

		if (isTransferAddress) {
			// 郵送先住所

			// 郵送先住所を取得
			Address address = Address.builder()
					.zipCode(personEntity.getTransferZipCode())
					.address1(personEntity.getTransferAddress1())
					.address2(personEntity.getTransferAddress2())
					.remarks(personEntity.getTransferRemarks())
					.build();

			return address;
		}

		// 上記でない場合はその他住所
		TOldAddressEntity tOldAddressEntity = tOldAddressDao.selectBySeq(oldAddressSeq);
		if (tOldAddressEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 郵送先住所を取得
		Address address = Address.builder()
				.zipCode(tOldAddressEntity.getZipCode())
				.address1(tOldAddressEntity.getAddress1())
				.address2(tOldAddressEntity.getAddress2())
				.remarks(tOldAddressEntity.getRemarks())
				.build();

		return address;
	}

	/**
	 * 【企業・団体】編集対象の住所データを取得する
	 * 
	 * @param personEntity 名簿情報エンティティ
	 * @param isBaseAddress 対象の住所情報が所在地かどうか
	 * @param isTourokuAddress 対象の住所情報が登記簿登録地かどうか
	 * @param isTransferAddress 対象の住所情報が郵送先住所情報かどうか
	 * @param oldAddressSeq その他住所（旧住所）の場合の対象データの連番値
	 * @return
	 */
	private Address getHojinEditAddressData(TPersonEntity personEntity,
			boolean isBaseAddress, boolean isTourokuAddress, boolean isTransferAddress, Long oldAddressSeq) throws AppException {

		if (isBaseAddress) {
			// 居住地

			// 居住地データを取得
			Address address = Address.builder()
					.zipCode(personEntity.getZipCode())
					.address1(personEntity.getAddress1())
					.address2(personEntity.getAddress2())
					.remarks(personEntity.getAddressRemarks())
					.build();

			return address;
		}

		if (isTourokuAddress) {
			// 登記簿登録地

			// 登記簿登録地を取得
			TPersonAddHojinEntity personAddHojin = tPersonAddHojinDao.selectPersonAddHojinByPersonId(personEntity.getPersonId());
			Address address = Address.builder()
					.zipCode(personAddHojin.getTokiZipCode())
					.address1(personAddHojin.getTokiAddress1())
					.address2(personAddHojin.getTokiAddress2())
					.remarks(personAddHojin.getTokiAddressRemarks())
					.build();

			// 以下でチェックする郵送先住所のチェックがあったとしても、
			// 登記簿登録地と郵送先住所の両方がチェックされている場合、２つは同じ住所データとなるため、
			// この時点で登記簿登録地のデータを住所情報として返却して問題ない。
			return address;
		}

		if (isTransferAddress) {
			// 郵送先住所

			// 郵送先住所を取得
			Address address = Address.builder()
					.zipCode(personEntity.getTransferZipCode())
					.address1(personEntity.getTransferAddress1())
					.address2(personEntity.getTransferAddress2())
					.remarks(personEntity.getTransferRemarks())
					.build();

			return address;
		}

		// 上記でない場合はその他住所
		TOldAddressEntity tOldAddressEntity = tOldAddressDao.selectBySeq(oldAddressSeq);
		if (tOldAddressEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 郵送先住所を取得
		Address address = Address.builder()
				.zipCode(tOldAddressEntity.getZipCode())
				.address1(tOldAddressEntity.getAddress1())
				.address2(tOldAddressEntity.getAddress2())
				.remarks(tOldAddressEntity.getRemarks())
				.build();

		return address;
	}

	/**
	 * 【弁護士】編集対象の住所データを取得する
	 * 
	 * @param personEntity 名簿情報エンティティ
	 * @param isBaseAddress 対象の住所情報が所在地かどうか
	 * @param isTourokuAddress 対象の住所情報が登記簿登録地かどうか
	 * @param isTransferAddress 対象の住所情報が郵送先住所情報かどうか
	 * @param oldAddressSeq その他住所（旧住所）の場合の対象データの連番値
	 * @return
	 */
	private Address getLawyerEditAddressData(TPersonEntity personEntity,
			boolean isBaseAddress, boolean isTourokuAddress, boolean isTransferAddress, Long oldAddressSeq) throws AppException {

		if (isBaseAddress) {
			// 居住地

			// 居住地データを取得
			Address address = Address.builder()
					.zipCode(personEntity.getZipCode())
					.address1(personEntity.getAddress1())
					.address2(personEntity.getAddress2())
					.remarks(personEntity.getAddressRemarks())
					.build();

			return address;
		}

		if (isTransferAddress) {
			// 郵送先住所

			// 郵送先住所を取得
			Address address = Address.builder()
					.zipCode(personEntity.getTransferZipCode())
					.address1(personEntity.getTransferAddress1())
					.address2(personEntity.getTransferAddress2())
					.remarks(personEntity.getTransferRemarks())
					.build();

			return address;
		}

		// 上記でない場合はその他住所
		TOldAddressEntity tOldAddressEntity = tOldAddressDao.selectBySeq(oldAddressSeq);
		if (tOldAddressEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 郵送先住所を取得
		Address address = Address.builder()
				.zipCode(tOldAddressEntity.getZipCode())
				.address1(tOldAddressEntity.getAddress1())
				.address2(tOldAddressEntity.getAddress2())
				.remarks(tOldAddressEntity.getRemarks())
				.build();

		return address;
	}

	/**
	 * 基本住所（居住地/所在地）情報の更新
	 * 
	 * @param personId
	 * @param inputFormAddress
	 * @throws AppException
	 */
	private void updateBaseAddress(Long personId, PersonEditInputForm.Address inputFormAddress) throws AppException {

		TPersonEntity updateEntity = tPersonDao.selectPersonByPersonId(personId);

		// DB値、画面入力値が空の場合は楽観ロックエラー（住所登録無し→無し状態の変更は画面側でできないようにしている）
		if (StringUtils.isAllEmpty(updateEntity.getZipCode(), updateEntity.getAddress1(), updateEntity.getAddress2(),
				updateEntity.getRemarks(), inputFormAddress.getZipCode(), inputFormAddress.getAddress1(),
				inputFormAddress.getAddress2(), inputFormAddress.getRemarks())) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateEntity.setZipCode(inputFormAddress.getZipCode());
		updateEntity.setAddress1(inputFormAddress.getAddress1());
		updateEntity.setAddress2(inputFormAddress.getAddress2());
		updateEntity.setAddressRemarks(inputFormAddress.getRemarks());

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPersonDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 登録地（住民票登録地/登記簿登録地）住所情報のnull更新
	 * 
	 * @param personId
	 * @param customerType
	 * @throws AppException
	 */
	private void nullUpdateTourokuAddress(Long personId, CustomerType customerType) throws AppException {

		// 件数チェック用
		int updateCount = 0;

		try {

			if (customerType == CustomerType.KOJIN) {
				// 個人の場合

				TPersonAddKojinEntity updateEntity = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personId);
				updateEntity.setJuminhyoAddressType(null);
				updateEntity.setJuminhyoZipCode(null);
				updateEntity.setJuminhyoAddress1(null);
				updateEntity.setJuminhyoAddress2(null);
				updateEntity.setJuminhyoRemarks(null);

				// 更新
				updateCount = tPersonAddKojinDao.update(updateEntity);

			} else {
				// 企業・団体の場合

				TPersonAddHojinEntity updateEntity = tPersonAddHojinDao.selectPersonAddHojinByPersonId(personId);
				updateEntity.setTokiAddressType(null);
				updateEntity.setTokiZipCode(null);
				updateEntity.setTokiAddress1(null);
				updateEntity.setTokiAddress2(null);
				updateEntity.setTokiAddressRemarks(null);

				// 更新
				updateCount = tPersonAddHojinDao.update(updateEntity);
			}

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 登録地（住民票登録地/登記簿登録地）住所情報の更新
	 * 
	 * @param personId
	 * @param customerType
	 * @param inputFormAddress
	 * @param isBaseAddress
	 * @param isTourokuAddressBeforeUpdate
	 * @throws AppException
	 */
	private void updateTourokuAddress(Long personId, CustomerType customerType,
			PersonEditInputForm.Address inputFormAddress, boolean isBaseAddress, boolean isTourokuAddressBeforeUpdate) throws AppException {

		// 件数チェック用
		int updateCount = 0;

		try {

			if (customerType == CustomerType.KOJIN) {
				// 個人の場合

				// 更新対象
				TPersonAddKojinEntity updateEntity = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personId);
				// 更新対象データの住民票登録地データをDtoとして取得
				PersonAddressDto beforeUpdateJuminhyoAddressDto = this.createJuminhyoAddressDto(updateEntity);

				// 更新対象と入力住所が空の場合は他ユーザーがデータを削除しているため楽観ロックエラー（画面上、空住所から空住所の更新は不可能）
				if (inputFormAddress.isEmpty() && StringUtils.isAllEmpty(updateEntity.getJuminhyoAddressType(),
						updateEntity.getJuminhyoZipCode(), updateEntity.getJuminhyoAddress1(),
						updateEntity.getJuminhyoAddress2(), updateEntity.getJuminhyoRemarks())) {
					// 楽観ロックエラー
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}
				if (inputFormAddress.isEmpty()) {
					// 住所情報が全て空で保存される場合は削除とみなす
					updateEntity.setJuminhyoAddressType(null);
					updateEntity.setJuminhyoZipCode(null);
					updateEntity.setJuminhyoAddress1(null);
					updateEntity.setJuminhyoAddress2(null);
					updateEntity.setJuminhyoRemarks(null);

					// 更新
					updateCount = tPersonAddKojinDao.update(updateEntity);

				} else {

					if (isBaseAddress) {
						// 更新対象データが居住地/所在地データの場合
						updateEntity.setJuminhyoAddressType(AddressTypeKojin.KYOJUUCHI.getCd());
						updateEntity.setJuminhyoZipCode(null);
						updateEntity.setJuminhyoAddress1(null);
						updateEntity.setJuminhyoAddress2(null);
						updateEntity.setJuminhyoRemarks(null);
					} else {
						// 更新対象データが居住地/所在地データではない場合
						updateEntity.setJuminhyoAddressType(AddressTypeKojin.OTHER.getCd());
						updateEntity.setJuminhyoZipCode(inputFormAddress.getZipCode());
						updateEntity.setJuminhyoAddress1(inputFormAddress.getAddress1());
						updateEntity.setJuminhyoAddress2(inputFormAddress.getAddress2());
						updateEntity.setJuminhyoRemarks(inputFormAddress.getRemarks());
					}

					// 更新
					updateCount = tPersonAddKojinDao.update(updateEntity);

					if (!isTourokuAddressBeforeUpdate) {
						// 住民票登録地の指定がもともとされていなかった（この保存処理で住民票登録地として登録された）場合

						// 住民票登録地の更新の結果、その他住所への再登録が必要な場合は登録を行う
						this.insertTourokuAddressAsOldAddressIfNecessary(personId, beforeUpdateJuminhyoAddressDto);
					}
				}
			} else if (customerType == CustomerType.HOJIN) {
				// 企業・団体の場合

				// 更新対象
				TPersonAddHojinEntity updateEntity = tPersonAddHojinDao.selectPersonAddHojinByPersonId(personId);
				// 更新対象データの登記簿登録地データをDtoとして取得
				PersonAddressDto beforeUpdateTokiAddressDto = this.createTokiAddressDto(updateEntity);

				// 更新対象と入力住所が空の場合は他ユーザーがデータを削除しているため楽観ロックエラー（画面上、空住所から空住所の更新は不可能）
				if (inputFormAddress.isEmpty() && StringUtils.isAllEmpty(updateEntity.getTokiAddressType(),
						updateEntity.getTokiZipCode(), updateEntity.getTokiAddress1(),
						updateEntity.getTokiAddress2(), updateEntity.getTokiAddressRemarks())) {
					// 楽観ロックエラー
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName);
					throw new AppException(MessageEnum.MSG_E00025, null);
				}

				if (inputFormAddress.isEmpty()) {
					// 住所情報が全て空で保存される場合は削除とみなす
					updateEntity.setTokiAddressType(null);
					updateEntity.setTokiZipCode(null);
					updateEntity.setTokiAddress1(null);
					updateEntity.setTokiAddress2(null);
					updateEntity.setTokiAddressRemarks(null);

					// 更新
					updateCount = tPersonAddHojinDao.update(updateEntity);

				} else {

					if (isBaseAddress) {
						// 更新対象データが居住地/所在地データの場合
						updateEntity.setTokiAddressType(AddressTypeHojin.SHOZAICHI.getCd());
						updateEntity.setTokiZipCode(null);
						updateEntity.setTokiAddress1(null);
						updateEntity.setTokiAddress2(null);
						updateEntity.setTokiAddressRemarks(null);
					} else {
						// 更新対象データが居住地/所在地データではない場合
						updateEntity.setTokiAddressType(AddressTypeHojin.OTHER.getCd());
						updateEntity.setTokiZipCode(inputFormAddress.getZipCode());
						updateEntity.setTokiAddress1(inputFormAddress.getAddress1());
						updateEntity.setTokiAddress2(inputFormAddress.getAddress2());
						updateEntity.setTokiAddressRemarks(inputFormAddress.getRemarks());
					}

					// 更新
					updateCount = tPersonAddHojinDao.update(updateEntity);

					if (!isTourokuAddressBeforeUpdate) {
						// 登記簿登録地の指定がもともとされていなかった（この保存処理で登記簿登録地として登録された）場合

						// 登記簿登録地の更新の結果、その他住所への再登録が必要な場合は登録を行う
						this.insertTourokuAddressAsOldAddressIfNecessary(personId, beforeUpdateTokiAddressDto);
					}
				}
			} else {
				// 弁護士の場合 登録地（住民票登録地/登記簿登録地）住所という概念が無いので何もしない
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * エンティティのデータをもとに、住民票登録地Dtoを作成する
	 * 
	 * @param updateEntity nullは許容しない
	 * @return
	 */
	private PersonAddressDto createJuminhyoAddressDto(TPersonAddKojinEntity updateEntity) {

		PersonAddressDto juminhyoAddressDto = new PersonAddressDto();

		AddressTypeKojin juminhyoAddressType = AddressTypeKojin.of(updateEntity.getJuminhyoAddressType());
		if (juminhyoAddressType != null) {
			switch (juminhyoAddressType) {
			case KYOJUUCHI:
				juminhyoAddressDto.setBaseAddress(true);
				break;
			case OTHER:
				juminhyoAddressDto.setTourokuAddress(true);
				break;
			default:
				// 何もしない
			}
		}

		juminhyoAddressDto.setZipCode(updateEntity.getJuminhyoZipCode());
		juminhyoAddressDto.setAddress1(updateEntity.getJuminhyoAddress1());
		juminhyoAddressDto.setAddress2(updateEntity.getJuminhyoAddress2());
		juminhyoAddressDto.setRemarks(updateEntity.getJuminhyoRemarks());

		return juminhyoAddressDto;
	}

	/**
	 * エンティティのデータをもとに、登記簿登録地Dtoを作成する
	 * 
	 * @param updateEntity nullは許容しない
	 * @return
	 */
	private PersonAddressDto createTokiAddressDto(TPersonAddHojinEntity updateEntity) {

		PersonAddressDto tokiAddressDto = new PersonAddressDto();

		AddressTypeHojin tokiAddressType = AddressTypeHojin.of(updateEntity.getTokiAddressType());
		if (tokiAddressType != null) {
			switch (tokiAddressType) {
			case SHOZAICHI:
				tokiAddressDto.setBaseAddress(true);
				break;
			case OTHER:
				tokiAddressDto.setTourokuAddress(true);
				break;
			default:
				// 何もしない
			}
		}

		tokiAddressDto.setZipCode(updateEntity.getTokiZipCode());
		tokiAddressDto.setAddress1(updateEntity.getTokiAddress1());
		tokiAddressDto.setAddress2(updateEntity.getTokiAddress2());
		tokiAddressDto.setRemarks(updateEntity.getTokiAddressRemarks());

		return tokiAddressDto;
	}

	/**
	 * 更新前の登録地データをその他住所として登録する（登録する必要がない場合は登録しない）<br>
	 * ※個人、法人で兼用。（住民票登録地、登記簿登録地のどちらのデータでもこのメソッドは利用可能）
	 * 
	 * @param personId
	 * @param beforeUpdateTourokuAddressDto 登録地更新が行われる前に登録されていた登録地住所情報
	 * @throws AppException
	 */
	private void insertTourokuAddressAsOldAddressIfNecessary(Long personId, PersonAddressDto beforeUpdateTourokuAddressDto) throws AppException {

		// 登録地として、単独で住所情報が登録されていたデータかどうか
		// （登録地データが、居住地を参照していた場合や、登録地が登録されていなかった（登録地の住所区分が登録されていなかった）場合はfalseとなる）
		boolean isTourokuAddress = beforeUpdateTourokuAddressDto.isTourokuAddress();

		if (!isTourokuAddress) {
			// 単独で存在していた住所情報ではないので、この登録地データが更新されたからといって、特に、その他住所の登録は不要
			return;
		}

		// 以下、単独で存在していた住所情報の場合 -> その他住所情報として再登録する必要の可能性あり

		// 郵送先データを取得
		TPersonEntity tPersonEntity = tPersonDao.selectPersonByPersonId(personId);
		PersonAddressDto transferAddressDto = new PersonAddressDto();
		transferAddressDto.setZipCode(tPersonEntity.getTransferZipCode());
		transferAddressDto.setAddress1(tPersonEntity.getTransferAddress1());
		transferAddressDto.setAddress2(tPersonEntity.getTransferAddress2());
		transferAddressDto.setRemarks(tPersonEntity.getTransferRemarks());

		// 更新前の登録地と郵送先住所が同じ住所の場合（ひとつの住所情報が登録地と郵送先住所の両方に指定されている場合）
		boolean isSameAddressTourokuAndTransfer = beforeUpdateTourokuAddressDto.isSameAddressData(transferAddressDto);

		if (isSameAddressTourokuAndTransfer) {
			// 更新前の登録地が変更されたとしても、同じ住所情報が郵送先住所に残るため、その他住所の登録は不要
			return;

		} else {
			// 更新前の登録地が変更された場合、もとの住所情報がなくなってしまうため、その他住所として再登録する
			// ※「登録地が変更された」とは、ある住所情報が登録地として「指定されている」という状態が変わったことを指し、
			// もともと登録地として登録されていた「住所情報」自体は引き続き存在している必要がある

			PersonEditInputForm.Address beforeUpdateTourokuAddressInput = PersonEditInputForm.Address.builder()
					.zipCode(beforeUpdateTourokuAddressDto.getZipCode())
					.address1(beforeUpdateTourokuAddressDto.getAddress1())
					.address2(beforeUpdateTourokuAddressDto.getAddress2())
					.remarks(beforeUpdateTourokuAddressDto.getRemarks())
					.build();

			// その他住所として再登録
			this.insertOldAddress(personId, beforeUpdateTourokuAddressInput);
		}
	}

	/**
	 * 郵送先住所情報のnull更新
	 * 
	 * @param personId
	 * @throws AppException
	 */
	private void nullUpdateTransferAddress(Long personId) throws AppException {

		TPersonEntity updateEntity = tPersonDao.selectPersonByPersonId(personId);

		updateEntity.setTransferAddressType(null);
		updateEntity.setTransferZipCode(null);
		updateEntity.setTransferAddress1(null);
		updateEntity.setTransferAddress2(null);
		updateEntity.setTransferRemarks(null);
		updateEntity.setTransferType(null);

		// 件数チェック用
		int updateCount = 0;

		try {

			updateCount = tPersonDao.update(updateEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 郵送先住所情報の更新
	 * 
	 * @param personId
	 * @param customerType
	 * @param inputFormAddress
	 * @param mailingType
	 * @param isBaseAddress
	 * @param isTransferAddressBeforeUpdate
	 * @throws AppException
	 */
	private void updateTransferAddress(Long personId, CustomerType customerType,
			PersonEditInputForm.Address inputFormAddress, MailingType mailingType, boolean isBaseAddress, boolean isTransferAddressBeforeUpdate) throws AppException {

		// 更新対象
		TPersonEntity updateEntity = tPersonDao.selectPersonByPersonId(personId);
		// 更新対象データの郵送先住所データをDtoとして取得
		PersonAddressDto beforeUpdateTransferAddressDto = this.createTransferAddressDto(updateEntity);

		// 件数チェック用
		int updateCount = 0;

		try {

			// 更新対象と入力住所が空の場合は他ユーザーがデータを削除しているため楽観ロックエラー（画面上、空住所から空住所の更新は不可能）
			if (inputFormAddress.isEmpty() && StringUtils.isAllEmpty(updateEntity.getTransferAddressType(), updateEntity.getTransferZipCode(), updateEntity.getTransferAddress1(),
					updateEntity.getTransferAddress2(), updateEntity.getTransferRemarks(), updateEntity.getTransferType())) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName);
				throw new AppException(MessageEnum.MSG_E00025, null);
			}

			if (inputFormAddress.isEmpty()) {
				// 住所情報が全て空で保存される場合は削除とみなす
				updateEntity.setTransferAddressType(null);
				updateEntity.setTransferZipCode(null);
				updateEntity.setTransferAddress1(null);
				updateEntity.setTransferAddress2(null);
				updateEntity.setTransferRemarks(null);
				updateEntity.setTransferType(null);

				// 更新
				updateCount = tPersonDao.update(updateEntity);

			} else {

				if (isBaseAddress) {
					// 更新対象データが居住地/所在地データの場合
					updateEntity.setTransferAddressType(TransferAddressType.KYOJUUCHI.getCd());
					updateEntity.setTransferZipCode(null);
					updateEntity.setTransferAddress1(null);
					updateEntity.setTransferAddress2(null);
					updateEntity.setTransferRemarks(null);
					updateEntity.setTransferType(mailingType.getCd());
				} else {
					// 更新対象データが居住地/所在地データではない場合
					updateEntity.setTransferAddressType(TransferAddressType.OTHER.getCd());
					updateEntity.setTransferZipCode(inputFormAddress.getZipCode());
					updateEntity.setTransferAddress1(inputFormAddress.getAddress1());
					updateEntity.setTransferAddress2(inputFormAddress.getAddress2());
					updateEntity.setTransferRemarks(inputFormAddress.getRemarks());
					updateEntity.setTransferType(mailingType.getCd());
				}

				// 更新
				updateCount = tPersonDao.update(updateEntity);

				if (!isTransferAddressBeforeUpdate) {
					// 郵送先住所の指定がもともとされていなかった（この保存処理で郵送先住所として登録された）場合

					// 郵送先住所の更新の結果、その他住所への再登録が必要な場合は登録を行う
					this.insertTransferAddressAsOldAddressIfNecessary(personId, customerType, beforeUpdateTransferAddressDto);
				}
			}
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * エンティティのデータをもとに、郵送先住所Dtoを作成する
	 * 
	 * @param updateEntity nullは許容しない
	 * @return
	 */
	private PersonAddressDto createTransferAddressDto(TPersonEntity updateEntity) {

		PersonAddressDto transferAddressDto = new PersonAddressDto();

		TransferAddressType transferAddressType = TransferAddressType.of(updateEntity.getTransferAddressType());
		if (transferAddressType != null) {
			switch (transferAddressType) {
			case KYOJUUCHI:
				transferAddressDto.setBaseAddress(true);
				break;
			case OTHER:
				transferAddressDto.setTransferAddress(true);
				break;
			default:
				// 何もしない
			}
		}

		transferAddressDto.setZipCode(updateEntity.getTransferZipCode());
		transferAddressDto.setAddress1(updateEntity.getTransferAddress1());
		transferAddressDto.setAddress2(updateEntity.getTransferAddress2());
		transferAddressDto.setRemarks(updateEntity.getTransferRemarks());

		return transferAddressDto;
	}

	/**
	 * 更新前の郵送先住所データをその他住所として登録する（登録する必要がない場合は登録しない）<br>
	 * ※個人、法人で兼用。
	 * 
	 * @param personId
	 * @param customerType
	 * @param beforeUpdateTransferAddressDto
	 * @throws AppException
	 */
	private void insertTransferAddressAsOldAddressIfNecessary(Long personId, CustomerType customerType, PersonAddressDto beforeUpdateTransferAddressDto) throws AppException {

		// 郵送先として、単独で住所情報が登録されていたデータかどうか
		// （郵送先データが、居住地を参照していた場合や、郵送先が登録されていなかった（郵送先の住所区分が登録されていなかった）場合はfalseとなる）
		boolean isTransferAddress = beforeUpdateTransferAddressDto.isTransferAddress();

		if (!isTransferAddress) {
			// 単独で存在していた住所情報ではないので、この郵送先データが更新されたからといって、特に、その他住所の登録は不要
			return;
		}

		// 以下、単独で存在していた住所情報の場合 -> その他住所情報として再登録する必要の可能性あり

		// 登録地データを取得
		PersonAddressDto tourokuAddressDto = new PersonAddressDto();
		if (customerType == CustomerType.KOJIN) {
			// 個人
			PersonBean personAddKojin = tPersonAddKojinDao.selectAddKojinAndTransferTypeByPersonId(personId);
			tourokuAddressDto.setZipCode(personAddKojin.getJuminhyoZipCode());
			tourokuAddressDto.setAddress1(personAddKojin.getJuminhyoAddress1());
			tourokuAddressDto.setAddress2(personAddKojin.getJuminhyoAddress2());
			tourokuAddressDto.setRemarks(personAddKojin.getJuminhyoRemarks());
		} else if (customerType == CustomerType.HOJIN) {
			// 企業・団体の場合
			PersonBean personAddHojin = tPersonAddHojinDao.selectAddHojinAndTransferTypeByPersonId(personId);
			tourokuAddressDto.setZipCode(personAddHojin.getTokiZipCode());
			tourokuAddressDto.setAddress1(personAddHojin.getTokiAddress1());
			tourokuAddressDto.setAddress2(personAddHojin.getTokiAddress2());
			tourokuAddressDto.setRemarks(personAddHojin.getTokiAddressRemarks());
		} else {
			// 弁護士の場合、登録地を作成しない仕様なのでデータを取得しない
		}

		// 更新前の郵送先住所と登録地住所が同じ住所の場合（ひとつの住所情報が郵送先住所と登録地の両方に指定されている場合）
		boolean isSameAddressTransferAndTouroku = beforeUpdateTransferAddressDto.isSameAddressData(tourokuAddressDto);

		if (isSameAddressTransferAndTouroku) {
			// 更新前の郵送先住所が変更されたとしても、同じ住所情報が登録地に残るため、その他住所の登録は不要
			return;

		} else {
			// 更新前の郵送先住所が変更された場合、もとの住所情報がなくなってしまうため、その他住所として再登録する
			// ※「郵送先住所が変更された」とは、ある住所情報が郵送先住所として「指定されている」という状態が変わったことを指し、
			// もともと郵送先住所として登録されていた「住所情報」自体は引き続き存在している必要がある

			PersonEditInputForm.Address beforeUpdateTransferAddressInput = PersonEditInputForm.Address.builder()
					.zipCode(beforeUpdateTransferAddressDto.getZipCode())
					.address1(beforeUpdateTransferAddressDto.getAddress1())
					.address2(beforeUpdateTransferAddressDto.getAddress2())
					.remarks(beforeUpdateTransferAddressDto.getRemarks())
					.build();

			// その他住所として再登録
			this.insertOldAddress(personId, beforeUpdateTransferAddressInput);
		}
	}

	/**
	 * 旧住所の削除
	 * 
	 * @param oldAddressSeq
	 * @throws AppException
	 */
	private void deleteOldAddress(Long oldAddressSeq) throws AppException {

		TOldAddressEntity deleteEntity = tOldAddressDao.selectBySeq(oldAddressSeq);

		// 件数チェック用
		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tOldAddressDao.delete(deleteEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 旧住所情報の更新（入力データが空の場合は削除）
	 * 
	 * @param oldAddressSeq
	 * @param inputFormAddress
	 * @throws AppException
	 */
	private void updateOldAddress(Long oldAddressSeq, PersonEditInputForm.Address inputFormAddress) throws AppException {

		if (inputFormAddress.isEmpty()) {
			// 住所情報が全て空で保存される場合は削除とみなす
			this.deleteOldAddress(oldAddressSeq);
			return;
		}

		// 住所情報が空でなければ更新処理を行う

		TOldAddressEntity updateEntity = tOldAddressDao.selectBySeq(oldAddressSeq);

		updateEntity.setZipCode(inputFormAddress.getZipCode());
		updateEntity.setAddress1(inputFormAddress.getAddress1());
		updateEntity.setAddress2(inputFormAddress.getAddress2());
		updateEntity.setRemarks(inputFormAddress.getRemarks());

		// 件数チェック用
		int updateCount = 0;

		try {

			updateCount = tOldAddressDao.update(updateEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 旧住所情報の登録（入力データが空の場合は何もしない）
	 * 
	 * @param personId
	 * @param inputFormAddress
	 * @throws AppException
	 */
	private void insertOldAddress(Long personId, PersonEditInputForm.Address inputFormAddress) throws AppException {

		if (inputFormAddress.isEmpty()) {
			// 登録情報が空の場合は何もしない
			return;
		}

		TOldAddressEntity insertEntity = new TOldAddressEntity();

		insertEntity.setPersonId(personId);
		insertEntity.setZipCode(inputFormAddress.getZipCode());
		insertEntity.setAddress1(inputFormAddress.getAddress1());
		insertEntity.setAddress2(inputFormAddress.getAddress2());
		insertEntity.setRemarks(inputFormAddress.getRemarks());

		int insertCount = tOldAddressDao.insert(insertEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	// 住所情報の保存バリデーション

	/**
	 * 更新データが保持する、更新前の状態のデータと、既存DBデータの状態が同じ状態かをチェックすることで楽観ロックチェックを行う<br>
	 * （楽観ロックエラーとなる場合はOptimisticLockingFailureExceptionをスローする）
	 * 
	 * @param addressInputForm 更新データ
	 * @param customerType 既存DBデータ
	 * @param addressRowDtoList 既存DBデータ
	 * @throws OptimisticLockingFailureException 楽観ロックエラーの場合にスロー
	 */
	private void ngOptimisticLock(PersonEditInputForm.PersonAddressInputForm addressInputForm, CustomerType customerType,
			List<PersonAddressDto> addressRowDtoList) throws OptimisticLockingFailureException {

		if (customerType != addressInputForm.getViewFormCustomerType()) {
			// 顧客タイプが変更されている場合
			throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
		}

		if (addressInputForm.isNewRegist()) {
			// 新規登録の楽観ロック

			// 画面からの値（更新前の状態）
			boolean saveTargetIsBaseAddress = addressInputForm.isBaseAddress();

			if (saveTargetIsBaseAddress) {
				// 登録対象が居住地/所在地データの場合

				// 居住地/所在地データが存在しているかチェック
				Optional<PersonAddressDto> baseAddressRowDtoOpt = addressRowDtoList.stream()
						.filter(dto -> dto.isBaseAddress()).findAny();
				if (!baseAddressRowDtoOpt.isEmpty()) {
					throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
				}
			} else {
				// 登録対象がその他住所の場合
				long otherAddressCount = addressRowDtoList.stream().filter(dto -> dto.isBaseAddress() == false).count();
				if (otherAddressCount >= CommonConstant.OTHER_ADDRESS_ADD_LIMIT) {
					// その他住所が上限数に達している場合
					throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
				}
			}

		} else {

			// 住所情報編集の楽観ロック

			// 画面からの値（更新前の状態）
			boolean saveTargetIsBaseAddress = addressInputForm.isBaseAddress();
			boolean saveTargetIsTourokuAddress = addressInputForm.isTourokuAddressBeforeUpdate();
			boolean saveTargetIsTransferAddress = addressInputForm.isTransferAddressBeforeUpdate();
			Long saveTargetOldAddressSeq = addressInputForm.getOldAddressSeq();

			if (saveTargetIsBaseAddress) {
				// 変更対象が居住地/所在地データの場合

				// 居住地/所在地データが存在しているかチェック
				Optional<PersonAddressDto> baseAddressRowDtoOpt = addressRowDtoList.stream()
						.filter(dto -> dto.isBaseAddress()).findAny();
				if (baseAddressRowDtoOpt.isEmpty()) {
					throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
				}

				// 登録地、郵送先住所のチェック状態が同じかチェック
				PersonAddressDto baseAddressRowDto = baseAddressRowDtoOpt.get();
				if (saveTargetIsTourokuAddress != baseAddressRowDto.isTourokuAddress()
						|| saveTargetIsTransferAddress != baseAddressRowDto.isTransferAddress()) {
					// DBから取得した対象データと、画面からの値（更新前の状態）が同じ状態ではない
					throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
				}

				// その他住所のSEQのチェック（居住地/所在地データの場合はその他データではないのでSEQはnullのはず）
				if (baseAddressRowDto.getOldAddressSeq() != null) {
					throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
				}

			} else {
				// 変更対象が居住地/所在地データ以外の場合
				if (saveTargetIsTourokuAddress) {
					// 更新対象が登録地（住民票登録地/登記簿登録地）のデータの場合

					// 登録地データが存在しているかチェック
					Optional<PersonAddressDto> tourokuAddressRowDtoOpt = addressRowDtoList.stream()
							.filter(dto -> dto.isTourokuAddress()).findAny();
					if (tourokuAddressRowDtoOpt.isEmpty()) {
						throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
					}

					// 郵送先住所のチェック状態が同じかチェック
					PersonAddressDto tourokuAddressRowDto = tourokuAddressRowDtoOpt.get();
					if (saveTargetIsTransferAddress != tourokuAddressRowDto.isTransferAddress()) {
						throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
					}

					// その他住所のSEQのチェック（登録地データの場合はその他データではないのでSEQはnullのはず）
					if (tourokuAddressRowDto.getOldAddressSeq() != null) {
						throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
					}

				} else if (saveTargetIsTransferAddress) {
					// 更新対象が郵送先住所のデータの場合

					// 郵送先住所データが存在しているかチェック
					Optional<PersonAddressDto> transferAddressRowDtoOpt = addressRowDtoList.stream()
							.filter(dto -> dto.isTransferAddress()).findAny();
					if (transferAddressRowDtoOpt.isEmpty()) {
						throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
					}

					// 登録地のチェック状態が同じかどうかチェック
					PersonAddressDto transferAddressRowDto = transferAddressRowDtoOpt.get();
					if (saveTargetIsTourokuAddress != transferAddressRowDto.isTourokuAddress()) {
						throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
					}

					// その他住所のSEQのチェック（郵送先住所データの場合はその他データではないのでSEQはnullのはず）
					if (transferAddressRowDto.getOldAddressSeq() != null) {
						throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
					}

				} else {
					// 更新対象が旧住所データ（居住地/所在地でも、登録地でも、郵送先住所でもない）の場合

					// 更新処理の場合、このケースではsaveTargetOldAddressSeqはnullとはならない

					// 更新対象の旧住所情報が存在しているかチェック
					Optional<PersonAddressDto> oldAddressRowDtoOpt = addressRowDtoList.stream()
							.filter(dto -> saveTargetOldAddressSeq.equals(dto.getOldAddressSeq())).findAny();
					if (oldAddressRowDtoOpt.isEmpty()) {
						throw new OptimisticLockingFailureException("更新対象データが他のユーザーに変更されている");
					}
				}
			}
		}
	}

	/**
	 * 入力フォームから住所情報Dtoを作成する
	 * 
	 * @param addressInputForm
	 * @return
	 */
	private PersonAddressDto createAddressDto(PersonEditInputForm.PersonAddressInputForm addressInputForm) {

		// 画面の入力情報を既存データと比較可能なDtoに詰め替え
		PersonAddressDto inputFormDto = new PersonAddressDto();
		inputFormDto.setBaseAddress(addressInputForm.isBaseAddress());
		inputFormDto.setTourokuAddress(addressInputForm.isTourokuAddress());
		inputFormDto.setTourokuAddressBeforeUpdate(addressInputForm.isTourokuAddressBeforeUpdate());
		inputFormDto.setTransferAddress(addressInputForm.isTransferAddress());
		inputFormDto.setTransferAddressBeforeUpdate(addressInputForm.isTransferAddressBeforeUpdate());
		inputFormDto.setOldAddressSeq(addressInputForm.getOldAddressSeq());
		PersonEditInputForm.Address inputFormAddress = addressInputForm.getAddress();
		if (inputFormAddress != null) {
			// 新規登録用のformの場合はinputFormAddressはnullとなる
			inputFormDto.setZipCode(inputFormAddress.getZipCode());
			inputFormDto.setAddress1(inputFormAddress.getAddress1());
			inputFormDto.setAddress2(inputFormAddress.getAddress2());
			inputFormDto.setRemarks(inputFormAddress.getRemarks());
		}

		return inputFormDto;
	}

	/**
	 * データリストから更新対象データを除いたリストを取得する
	 * 
	 * <pre>
	 * ※BaseAddress、TourokuAddress、TransferAddressはそれぞれ１つしか登録は不可であり、
	 *   また、BaseAddressのデータが既に登録済みの場合、BaseAddressの新規登録フォームの表示はそもそもできない（表示処理の実行自体が不可）
	 *   であることを前提としている。
	 * </pre>
	 * 
	 * @param addressRowDtoList データリスト
	 * @param updateTarget 更新対象データ
	 * @return データリストから更新対象データを除いたリスト
	 */
	private List<PersonAddressDto> getAddressDtoListExcludUpdateTarget(List<PersonAddressDto> addressRowDtoList,
			PersonAddressDto updateTarget) {

		List<PersonAddressDto> dtoListExcludUpdateTarget = addressRowDtoList.stream()
				.filter(dto -> !(dto.isBaseAddress() == updateTarget.isBaseAddress()
						&& dto.isTourokuAddress() == updateTarget.isTourokuAddressBeforeUpdate()
						&& dto.isTransferAddress() == updateTarget.isTransferAddressBeforeUpdate()
						&& (dto.getOldAddressSeq() == null ? updateTarget.getOldAddressSeq() == null : dto.getOldAddressSeq()
								.equals(updateTarget.getOldAddressSeq()))))
				.collect(Collectors.toList());

		return dtoListExcludUpdateTarget;
	}

	/**
	 * 更新対象データと同じ住所情報が既に登録されているかどうかを判定する
	 * 
	 * @param dtoListExcludUpdateTarget 更新対象を除いた現在登録されている住所データ
	 * @param inputFormDto 更新対象の住所データ
	 * @return true:存在している、false:存在していない
	 */
	private boolean isExistSameAddress(List<PersonAddressDto> dtoListExcludUpdateTarget, PersonAddressDto inputFormDto) {

		Optional<PersonAddressDto> sameAddressDto = dtoListExcludUpdateTarget.stream()
				.filter(dto -> dto.isSameAddressData(inputFormDto))
				.findAny();

		if (sameAddressDto.isPresent()) {
			// 同じ住所情報が登録されている
			return true;
		}

		return false;
	}

	/**
	 * 登録データの中に、登録住所データ（住民票登録地/登記簿登録地）が存在しているかを判定する
	 * 
	 * @param dtoListExcludUpdateTarget 更新対象を除いた現在登録されている住所データ
	 * @return true:存在している、false:存在していない
	 */
	private boolean isExistTourokuAddress(List<PersonAddressDto> dtoListExcludUpdateTarget) {

		Optional<PersonAddressDto> tourokuAddressDtoOpt = dtoListExcludUpdateTarget.stream()
				.filter(dto -> dto.isTourokuAddress()).findAny();

		if (tourokuAddressDtoOpt.isPresent()) {
			// 登録住所（住民票登録地/登記簿登録地）のデータが存在している
			return true;
		}

		return false;
	}

	/**
	 * 登録データの中に、郵送先住所データが存在しているかを判定する
	 * 
	 * @param dtoListExcludUpdateTarget 更新対象を除いた現在登録されている住所データ
	 * @return true:存在している、false:存在していない
	 */
	private boolean isExistTransferAddress(List<PersonAddressDto> dtoListExcludUpdateTarget) {

		Optional<PersonAddressDto> transferAddressDtoOpt = dtoListExcludUpdateTarget.stream()
				.filter(dto -> dto.isTransferAddress()).findAny();

		if (transferAddressDtoOpt.isPresent()) {
			// 郵送先住所のデータが存在している
			return true;
		}

		return false;
	}

	// ▼ 名簿の連絡先情報に関する処理

	/**
	 * 対象名簿の現在登録されている連絡先リストを取得する<br>
	 * （Dtoのデータが空のものは結果から除外する）
	 * 
	 * @param personId
	 * @return
	 */
	private List<PersonContactDto> getNowRegisContactListExcludeEmpty(Long personId) {

		// DBに登録されている名簿連絡先
		List<TPersonContactEntity> personContactEntityList = tPersonContactDao.selectPersonContactByPersonId(personId);

		// 名簿連絡先を取得し、フォームに設定
		List<PersonContactDto> telNoList = this.createContactDtoListExcludeEmpty(personContactEntityList,
				ContactCategory.TEL, TPersonContactEntity::getContactSeq, TPersonContactEntity::getTelNoType,
				TPersonContactEntity::getTelNo, TPersonContactEntity::getYusenTelFlg, TPersonContactEntity::getTelRemarks);
		List<PersonContactDto> faxNoList = this.createContactDtoListExcludeEmpty(personContactEntityList,
				ContactCategory.FAX, TPersonContactEntity::getContactSeq, TPersonContactEntity::getFaxNoType,
				TPersonContactEntity::getFaxNo, TPersonContactEntity::getYusenFaxFlg, TPersonContactEntity::getFaxRemarks);
		List<PersonContactDto> emailAddressList = this.createContactDtoListExcludeEmpty(personContactEntityList,
				ContactCategory.EMAIL, TPersonContactEntity::getContactSeq,
				TPersonContactEntity::getMailAddressType, TPersonContactEntity::getMailAddress,
				TPersonContactEntity::getYusenMailAddressFlg, TPersonContactEntity::getMailAddressRemarks);

		List<PersonContactDto> allContactList = new ArrayList<>();
		allContactList.addAll(telNoList);
		allContactList.addAll(faxNoList);
		allContactList.addAll(emailAddressList);

		return allContactList;
	}

	/**
	 * 対象名簿の現在登録されている連絡先リストを取得する<br>
	 * （Dtoのデータが空のものも結果に含む）
	 * 
	 * @param personId
	 * @return
	 */
	private List<PersonContactDto> getNowRegisContactListIncludeEmpty(Long personId) {

		// DBに登録されている名簿連絡先
		List<TPersonContactEntity> personContactEntityList = tPersonContactDao.selectPersonContactByPersonId(personId);

		// 顧客連絡先を取得し、フォームに設定
		List<PersonContactDto> telNoList = this.createContactDtoListIncludeEmpty(personContactEntityList,
				ContactCategory.TEL, TPersonContactEntity::getContactSeq, TPersonContactEntity::getTelNoType,
				TPersonContactEntity::getTelNo, TPersonContactEntity::getYusenTelFlg, TPersonContactEntity::getTelRemarks);
		List<PersonContactDto> faxNoList = this.createContactDtoListIncludeEmpty(personContactEntityList,
				ContactCategory.FAX, TPersonContactEntity::getContactSeq, TPersonContactEntity::getFaxNoType,
				TPersonContactEntity::getFaxNo, TPersonContactEntity::getYusenFaxFlg, TPersonContactEntity::getFaxRemarks);
		List<PersonContactDto> emailAddressList = this.createContactDtoListIncludeEmpty(personContactEntityList,
				ContactCategory.EMAIL, TPersonContactEntity::getContactSeq,
				TPersonContactEntity::getMailAddressType, TPersonContactEntity::getMailAddress,
				TPersonContactEntity::getYusenMailAddressFlg, TPersonContactEntity::getMailAddressRemarks);

		List<PersonContactDto> allContactList = new ArrayList<>();
		allContactList.addAll(telNoList);
		allContactList.addAll(faxNoList);
		allContactList.addAll(emailAddressList);

		return allContactList;
	}

	/**
	 * 連絡先情報リストのデータについて、 対象のカテゴリの連絡先情報のカウント数が上限に達しているかを判定する。
	 * 
	 * @param contactList 連絡先データリスト
	 * @param category 上限数に達しているかを判定する対象の連絡先カテゴリ
	 */
	private boolean isDataCountReachedLimit(List<PersonContactDto> contactList, ContactCategory category) {

		// 対象カテゴリデータの件数
		Long dataCount = contactList.stream().filter(dto -> dto.getCategory() == category).count();

		// 件数が上限値に達しているかを判定
		if (dataCount >= CommonConstant.CUSTOMER_CONTACT_ADD_LIMIT) {
			return true;
		}

		return false;
	}

	/**
	 * フォームにデータを設定
	 * 
	 * <pre>
	 * 引数の各データは、Entityの値をそのまま受けるため、String型としている。
	 * </pre>
	 * 
	 * @param <CustomerContactInputForm>
	 * @param inputForm
	 * @param yusenFlg
	 * @param type
	 * @param valueSetter 受けるsetterメソッドによって任意のプロパティにvalueの値を設定
	 * @param value
	 * @param remarks
	 */
	private <CustomerContactInputForm> void setContactDataToForm(
			PersonEditInputForm.PersonContactInputForm inputForm, String yusenFlg, String type,
			BiConsumer<PersonEditInputForm.PersonContactInputForm, String> valueSetter, String value, String remarks) {

		inputForm.setYusenFlg(SystemFlg.codeToBoolean(yusenFlg));
		inputForm.setType(ContactType.of(type));
		inputForm.setRemarks(remarks);
		valueSetter.accept(inputForm, value);
	}

	/**
	 * 入力フォームの状態をもとに、その入力フォームと同じカテゴリの連絡先データがすでに「優先」選択をしているかどうかを判定する
	 * 
	 * @param personId
	 * @param inputForm
	 * @return
	 */
	private boolean alreadySettingYusenFlg(Long personId, PersonEditInputForm.PersonContactInputForm inputForm) {

		// 現在の登録データを取得
		List<PersonContactDto> nowRegistContactList = this.getNowRegisContactListExcludeEmpty(personId);

		return this.alreadySettingYusenFlg(inputForm, nowRegistContactList);
	}

	/**
	 * 入力フォームの状態をもとに、その入力フォームと同じカテゴリの連絡先データがすでに「優先」選択をしているかどうかを判定する
	 * 
	 * @param inputForm
	 * @param nowRegistContactList
	 * @return
	 */
	private boolean alreadySettingYusenFlg(PersonEditInputForm.PersonContactInputForm inputForm, List<PersonContactDto> nowRegistContactList) {

		// 更新対象の連絡先連番
		Long contactSeq = inputForm.getContactSeq();
		// 連絡先カテゴリ
		ContactCategory inputCategory = inputForm.getCategory();

		// 現在の登録データから、更新対象自身を除外したデータリスト
		List<PersonContactDto> contactListExcludUpdateTarget = null;
		if (contactSeq == null) {
			// 新規の場合

			contactListExcludUpdateTarget = nowRegistContactList;
		} else {
			// 更新の場合

			// 自分自身を除外
			contactListExcludUpdateTarget = this.getContactListExcludUpdateTarget(nowRegistContactList, contactSeq,
					inputCategory);
		}

		// データリスト内の対象カテゴリデータについて、「優先」となっているデータが既に存在しているかどうか
		boolean alreadyExistYusenFlgDto = this.isExistYusenFlgDto(contactListExcludUpdateTarget, inputCategory);

		if (alreadyExistYusenFlgDto) {
			// 既に存在している場合 -> 優先フラグの設定は不可とする
			return true;
		}

		return false;
	}

	/**
	 * 現在の登録情報リストから、更新対象情報を除外したリストを取得する<br>
	 * （引数の２つの更新対象情報をもとに、現在の登録情報リストから、更新対象情報を一意に判定することが可能）
	 * 
	 * @param nowRegistContactList 現在の登録情報リスト
	 * @param updateTargetContactSeq 更新対象の連絡先連番
	 * @param updateTargetContactCategory 更新対象の連絡先カテゴリ
	 * @return
	 */
	private List<PersonContactDto> getContactListExcludUpdateTarget(List<PersonContactDto> nowRegistContactList,
			Long updateTargetContactSeq, ContactCategory updateTargetContactCategory) {

		// 更新対象のDtoを除外したリストを作成（連絡先Dtoは、連絡先連番と連絡先カテゴリの２つの値で一意に判定できる）
		List<PersonContactDto> contactListExcludUpdateTarget = nowRegistContactList.stream()
				.filter(dto -> !(dto.getContactSeq().equals(updateTargetContactSeq) && dto.getCategory() == updateTargetContactCategory))
				.collect(Collectors.toList());

		return contactListExcludUpdateTarget;
	}

	/**
	 * 捜索対象のデータリストから、対象の連絡先カテゴリについて「優先」となっているデータが存在するかを判定する
	 * 
	 * @param searchTargetContactList
	 * @param targetContactCategory
	 * @return
	 */
	private boolean isExistYusenFlgDto(List<PersonContactDto> searchTargetContactList, ContactCategory targetContactCategory) {

		Optional<PersonContactDto> inputCategoryYusenDtoOpt = searchTargetContactList.stream()
				.filter(dto -> dto.getCategory() == targetContactCategory)
				.filter(dto -> dto.isYusenFlg())
				.findAny();

		if (inputCategoryYusenDtoOpt.isPresent()) {
			// 「優先」指定されているデータが既に存在する
			return true;
		}

		return false;
	}

	/**
	 * 連絡先のDtoリストを作成する<br>
	 * （Dtoのデータが空のものは結果から除外する）
	 * 
	 * @param <T>
	 * @param entityList 名簿連絡先DB取得値
	 * @param contactCategory 取得するデータの連絡先カテゴリ
	 * @param seqMapper 連絡先SEQ取得関数
	 * @param typeCodeMapper 連絡先区分取得関数
	 * @param valueMapper 連絡先取得関数
	 * @param yusenMapper 優先フラグ取得関数
	 * @param remarksMapper 備考取得関数
	 * @return
	 */
	private <T> List<PersonContactDto> createContactDtoListExcludeEmpty(List<T> entityList,
			ContactCategory contactCategory, Function<T, Long> seqMapper, Function<T, String> typeCodeMapper,
			Function<T, String> valueMapper, Function<T, String> yusenMapper, Function<T, String> remarksMapper) {

		// DB取得値をDtoに変換（データか空のDtoも含む）
		List<PersonContactDto> dtoListIncludeEmpty = this.createContactDtoListIncludeEmpty(entityList, contactCategory,
				seqMapper, typeCodeMapper, valueMapper, yusenMapper, remarksMapper);

		// データが空のDtoはリストから除外
		List<PersonContactDto> dtoListExcludeEmpty = dtoListIncludeEmpty.stream()
				.filter(dto -> !dto.isEmpty())
				.collect(Collectors.toList());

		// 優先フラグが一番上、あとはタイプ別(nullは最後)
		dtoListExcludeEmpty.sort(
				Comparator.comparing(PersonContactDto::isYusenFlg, nullsLast(reverseOrder()))
						.thenComparing(PersonContactDto::getType, nullsLast(naturalOrder())));

		return dtoListExcludeEmpty;
	}

	/**
	 * 連絡先のDtoリストを作成する<br>
	 * （Dtoのデータが空のものも結果に含む）
	 * 
	 * @param <T>
	 * @param entityList 名簿連絡先DB取得値
	 * @param contactCategory 取得するデータの連絡先カテゴリ
	 * @param seqMapper 連絡先SEQ取得関数
	 * @param typeCodeMapper 連絡先区分取得関数
	 * @param valueMapper 連絡先取得関数
	 * @param yusenMapper 優先フラグ取得関数
	 * @param remarksMapper 備考取得関数
	 * @return
	 */
	private <T> List<PersonContactDto> createContactDtoListIncludeEmpty(List<T> entityList,
			ContactCategory contactCategory, Function<T, Long> seqMapper, Function<T, String> typeCodeMapper,
			Function<T, String> valueMapper, Function<T, String> yusenMapper, Function<T, String> remarksMapper) {

		// DB取得値をフォーム情報に変換
		List<PersonContactDto> dtoList = entityList.stream()
				.map(entity -> PersonContactDto.builder().contactSeq(seqMapper.apply(entity))
						.category(contactCategory).type(ContactType.of(typeCodeMapper.apply(entity)))
						.value(valueMapper.apply(entity)).yusenFlg(SystemFlg.codeToBoolean(yusenMapper.apply(entity)))
						.remarks(remarksMapper.apply(entity)).build())
				.collect(Collectors.toCollection(ArrayList::new));

		return dtoList;
	}

	/**
	 * 連絡先情報を登録する
	 * 
	 * <pre>
	 * 登録処理では、現在の登録状況により下記の２つの処理のいずれかが実行される
	 * 
	 * [登録エンティティについて、指定カテゴリ用のカラムが空（未登録状態）のものが存在する場合]
	 * ・対象エンティティについて、指定カテゴリ用のカラムに入力値を設定して更新処理を実行
	 * 
	 * [上記が存在しない場合]
	 * ・エンティティを新規に登録
	 * 
	 * </pre>
	 * 
	 * @param personId
	 * @param registTargetCategory
	 * @param inputForm
	 * @throws AppException
	 */
	private void registContact(Long personId, ContactCategory registTargetCategory, PersonEditInputForm.PersonContactInputForm inputForm)
			throws AppException {

		// 対象カテゴリのデータが空（未登録状態）のエンティティを取得（現在登録されているデータから取得）
		TPersonContactEntity categoryContactDataIsEmptyEntity = this.getCategoryContactDataIsEmptyEntity(personId,
				registTargetCategory);
		if (categoryContactDataIsEmptyEntity == null) {
			// 存在しなかった場合

			// 新規登録
			insertContact(personId, registTargetCategory, inputForm);
		} else {
			// 存在した場合

			// 更新処理
			this.updateContact(categoryContactDataIsEmptyEntity.getContactSeq(), registTargetCategory, inputForm);
		}
	}

	/**
	 * 指定カテゴリのカラムに値を設定した連絡先情報を登録する<br>
	 * （指定カテゴリ以外のカラムの値は初期状態で登録する）
	 * 
	 * @param personId
	 * @param registTargetCategory
	 * @param inputForm
	 * @throws AppException
	 */
	private void insertContact(Long personId, ContactCategory registTargetCategory, PersonEditInputForm.PersonContactInputForm inputForm)
			throws AppException {

		// 登録エンティティ作成
		TPersonContactEntity insertEntity = new TPersonContactEntity();
		insertEntity.setPersonId(personId);

		// エンティティに値を設定
		this.setCategoryValuesToEntity(insertEntity, registTargetCategory,
				inputForm.getType(), inputForm.getValue(), inputForm.isYusenFlg(), inputForm.getRemarks());

		// 念の為ここでもデータの上限値チェックを行う
		// ※Contoller側のDBバリデーションで同様の意味のバリデーションは行っているので、基本このバリデーションで弾かれることはないが、
		// 万が一、複数ユーザーの操作のタイミングにより、バリデーションをすり抜けた場合、想定しないデータ件数が登録されてしまうので、念の為ここでもチェックを行う
		List<TPersonContactEntity> nowRegistEntityList = tPersonContactDao.selectPersonContactByPersonId(personId);
		if (nowRegistEntityList.size() >= CommonConstant.CUSTOMER_CONTACT_ADD_LIMIT) {
			// 上限値に既に達してる場合
			// 万が一の確率でしか発生し得ないので、楽観ロックエラーとはせず、異常エラーとする
			throw new IllegalStateException("想定以上の連絡先レコードが登録されようとしている。");
		}

		// 登録
		this.insertCustomerContact(insertEntity);
	}

	/**
	 * 連絡先情報の登録
	 * 
	 * @param insertEntity
	 * @return
	 * @throws AppException
	 */
	private TPersonContactEntity insertCustomerContact(TPersonContactEntity insertEntity) throws AppException {

		// 登録
		int insertCount = tPersonContactDao.insert(insertEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}

		return insertEntity;
	}

	/**
	 * 連絡先情報の更新処理を行う
	 * 
	 * @param contactSeq
	 * @param updateTargetCategory
	 * @param inputForm
	 * @throws AppException
	 */
	private void updateContact(Long contactSeq, ContactCategory updateTargetCategory, PersonEditInputForm.PersonContactInputForm inputForm)
			throws AppException {

		// 更新対象取得
		TPersonContactEntity updateContactEntity = tPersonContactDao.selectBySeq(contactSeq);
		if (updateContactEntity == null) {
			// 楽観ロックエラー
			logger.warn("名簿情報か名簿の連絡情報データが他のユーザーに削除された。");
			throw new DataNotFoundException();
		}

		// 更新
		this.updateCustomerContact(updateContactEntity, updateTargetCategory, inputForm.getType(), inputForm.getValue(),
				inputForm.isYusenFlg(), inputForm.getRemarks());
	}

	/**
	 * 連絡先エンティティの指定カテゴリのカラムの値を引数の値で更新する
	 * 
	 * @param updateEntity 更新対象の連絡先エンティティ
	 * @param updateTargetCategory
	 * 更新対象の連絡先カテゴリ（エンティティのカラムのうち、このカテゴリ用の値を保持するカラムの値を更新する）
	 * @param type
	 * @param value
	 * @param yusenTelFlg
	 * @param remarks
	 * @throws AppException
	 */
	private void updateCustomerContact(TPersonContactEntity updateEntity, ContactCategory updateTargetCategory,
			ContactType type, String value, boolean yusenTelFlg, String remarks) throws AppException {

		// エンティティに値を設定
		this.setCategoryValuesToEntity(updateEntity, updateTargetCategory,
				type, value, yusenTelFlg, remarks);

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPersonContactDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 連絡先情報の削除処理を行う
	 * 
	 * <pre>
	 * 削除処理は、削除対象のエンティティの状態により下記の２パターンの対応が存在する
	 * ※下記の例では、deleteTargetCategoryをFAXとする
	 * 
	 * パターン１（削除対象以外のカラムの値がない（空の）場合）
	 *     [エンティティの状態] TEL用カラム：値なし、FAX用カラム：値あり、MAIL用カラム：値なし
	 *     [処理] エンティティを削除
	 * 
	 * パターン２（削除対象以外のカラムの値がある（空ではない）場合）
	 *     [エンティティの状態] TEL用カラム：値あり、FAX用カラム：値あり、MAIL用カラム：値なし
	 *     [処理] FAX用のカラムの値をNULL更新
	 * 
	 * </pre>
	 * 
	 * @param contactSeq
	 * @param deleteTargetCategory
	 * @throws AppException
	 */
	private void deleteContact(Long contactSeq, ContactCategory deleteTargetCategory) throws AppException {

		// 削除対象取得
		TPersonContactEntity deleteContactEntity = tPersonContactDao.selectBySeq(contactSeq);
		if (deleteContactEntity == null) {
			// 楽観ロックエラー
			logger.warn("名簿の連絡情報データが他のユーザーに削除された。");
			throw new DataNotFoundException();
		}

		// 削除しようとした連絡先が既に無い場合、楽観ロックエラー
		switch (deleteTargetCategory) {
		case TEL:
			if (StringUtils.isAllEmpty(deleteContactEntity.getTelNoType(), deleteContactEntity.getTelNo())) {
				// 楽観ロックエラー
				logger.warn("名簿の連絡情報データが他のユーザーに削除された。");
				throw new DataNotFoundException();
			}
			break;
		case FAX:
			if (StringUtils.isAllEmpty(deleteContactEntity.getFaxNoType(), deleteContactEntity.getFaxNo())) {
				// 楽観ロックエラー
				logger.warn("名簿の連絡情報データが他のユーザーに削除された。");
				throw new DataNotFoundException();
			}
			break;
		case EMAIL:
			if (StringUtils.isAllEmpty(deleteContactEntity.getMailAddressType(), deleteContactEntity.getMailAddress())) {
				// 楽観ロックエラー
				logger.warn("名簿の連絡情報データが他のユーザーに削除された。");
				throw new DataNotFoundException();
			}
			break;
		default:
		}

		boolean otherCategoryContactDataIsEmpty = this.checkOtherCategoryContactDataIsEmpty(deleteContactEntity, deleteTargetCategory);
		if (otherCategoryContactDataIsEmpty) {
			// 削除対象のカテゴリ以外の連絡先データがすべて空の場合
			// -> エンティティごと削除する
			this.deleteCustomerContact(deleteContactEntity);
		} else {
			// 削除対象のカテゴリ以外の連絡先データが存在する場合
			// -> 削除対象カテゴリのデータのみブランク更新する
			this.nullUpdateCustomerContact(deleteContactEntity, deleteTargetCategory);
		}
	}

	/**
	 * 対象のエンティティについて、指定カテゴリ以外の連絡先データが全て空かどうかをチェックする
	 * 
	 * @param entity
	 * @param category
	 * @return
	 */
	private boolean checkOtherCategoryContactDataIsEmpty(TPersonContactEntity entity, ContactCategory category) {

		boolean telIsEmpty = StringUtils.isAllEmpty(entity.getTelNoType(), entity.getTelNo());
		boolean faxIsEmpty = StringUtils.isAllEmpty(entity.getFaxNoType(), entity.getFaxNo());
		boolean emailIsEmpty = StringUtils.isAllEmpty(entity.getMailAddressType(), entity.getMailAddress());

		switch (category) {
		case TEL:
			return faxIsEmpty && emailIsEmpty;
		case FAX:
			return telIsEmpty && emailIsEmpty;
		case EMAIL:
			return telIsEmpty && faxIsEmpty;
		default:
			throw new IllegalArgumentException("想定していないカテゴリ値");
		}
	}

	/**
	 * 連絡先情報を削除する（物理削除）
	 * 
	 * @param entity
	 * @throws AppException
	 */
	private void deleteCustomerContact(TPersonContactEntity entity) throws AppException {

		// 件数チェック用
		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tPersonContactDao.delete(entity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 連絡先エンティティの指定カテゴリのカラムの値をnull更新する
	 * 
	 * @param updateEntity 更新対象の連絡先エンティティ
	 * @param updateTargetCategory
	 * 更新対象の連絡先カテゴリ（エンティティのカラムのうち、このカテゴリ用の値を保持するカラムの値を更新する）
	 * @throws AppException
	 */
	private void nullUpdateCustomerContact(TPersonContactEntity updateEntity, ContactCategory updateTargetCategory) throws AppException {

		this.updateCustomerContact(updateEntity, updateTargetCategory, null, null, false, null);
	}

	/**
	 * 指定の連絡先カテゴリのカラムの値が空（未登録状態）であるエンティティを取得する。<br>
	 * （戻り値のエンティティの状態は、指定の連絡先カテゴリのカラムの値は空で、指定以外の連絡先カテゴリのいずれかのカラムの値は何かしらの値が設定されている状態となる）
	 * 
	 * @param personId
	 * @param targetCategory
	 * @return
	 */
	private TPersonContactEntity getCategoryContactDataIsEmptyEntity(Long personId, ContactCategory targetCategory) {

		// 現在登録されている連絡リスト（データが空のものも含む）
		List<PersonContactDto> dotListIncludeEmpty = this.getNowRegisContactListIncludeEmpty(personId);

		// 指定のカテゴリで、データが空の最初のDtoを取得
		Optional<PersonContactDto> targetCategoryFirstEmptyDtoOpt = dotListIncludeEmpty.stream()
				.filter(dto -> dto.getCategory() == targetCategory)
				.filter(dto -> dto.isEmpty())
				.findFirst();

		if (targetCategoryFirstEmptyDtoOpt.isEmpty()) {
			return null;
		}
		PersonContactDto targetDto = targetCategoryFirstEmptyDtoOpt.get();

		// 指定カテゴリのデータが空であるDtoが保持するデータを持つエンティティを取得
		TPersonContactEntity emptyEntity = tPersonContactDao.selectBySeq(targetDto.getContactSeq());

		return emptyEntity;
	}

	/**
	 * エンティティの指定カテゴリ用のカラムに引数の値を設定する。
	 * 
	 * @param entity
	 * @param category どのカテゴリのカラムに値を設定するかを指定
	 * @param categoryValueType カラムに設定される値
	 * @param categoryValueValue カラムに設定される値
	 * @param categoryValueYusenFlg カラムに設定される値
	 * @param categoryRemarksValue
	 */
	private void setCategoryValuesToEntity(TPersonContactEntity entity, ContactCategory category,
			ContactType categoryValueType, String categoryValueValue, boolean categoryValueYusenFlg, String categoryRemarksValue) {

		switch (category) {
		case TEL:
			entity.setTelNoType(DefaultEnum.getCd(categoryValueType));
			entity.setTelNo(categoryValueValue);
			entity.setYusenTelFlg(SystemFlg.booleanToCode(categoryValueYusenFlg));
			entity.setTelRemarks(categoryRemarksValue);
			break;
		case FAX:
			entity.setFaxNoType(DefaultEnum.getCd(categoryValueType));
			entity.setFaxNo(categoryValueValue);
			entity.setYusenFaxFlg(SystemFlg.booleanToCode(categoryValueYusenFlg));
			entity.setFaxRemarks(categoryRemarksValue);
			break;
		case EMAIL:
			entity.setMailAddressType(DefaultEnum.getCd(categoryValueType));
			entity.setMailAddress(categoryValueValue);
			entity.setYusenMailAddressFlg(SystemFlg.booleanToCode(categoryValueYusenFlg));
			entity.setMailAddressRemarks(categoryRemarksValue);
			break;
		default:
			throw new IllegalArgumentException("想定していないカテゴリ値");
		}
	}

	/**
	 * エンティティリストについて対象カテゴリの優先フラグの値に初期値を設定する
	 * 
	 * @param entityList
	 * @param category
	 */
	private void clearCategoryYusenTelFlgValueToEntitys(List<TPersonContactEntity> entityList, ContactCategory category) {

		if (entityList == null || entityList.isEmpty()) {
			return;
		}

		String initYusenFlgValue = SystemFlg.FLG_OFF.getCd();

		switch (category) {
		case TEL:
			entityList.stream()
					.forEach(e -> e.setYusenTelFlg(initYusenFlgValue));
			break;
		case FAX:
			entityList.stream()
					.forEach(e -> e.setYusenFaxFlg(initYusenFlgValue));
			break;
		case EMAIL:
			entityList.stream()
					.forEach(e -> e.setYusenMailAddressFlg(initYusenFlgValue));
			break;
		default:
			throw new IllegalArgumentException("想定していないカテゴリ値");
		}
	}

	// 共通系の処理

	/**
	 * 名簿Entityを取得
	 * 
	 * @param personId
	 * @return
	 */
	private TPersonEntity getPersonEntity(Long personId) {

		TPersonEntity entity = tPersonDao.selectPersonByPersonId(personId);

		// 名簿情報が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("名簿情報が存在しません。[personId=" + personId + "]");
		}

		return entity;
	}

}