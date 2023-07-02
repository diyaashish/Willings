package jp.loioz.app.user.personManagement.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonFileManagementService;
import jp.loioz.app.user.personManagement.form.personEdit.PersonRegistInputForm;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.DeathFlg;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.SelectType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozObjectUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MSelectListDao;
import jp.loioz.dao.TPersonAddHojinDao;
import jp.loioz.dao.TPersonAddKojinDao;
import jp.loioz.dao.TPersonAddLawyerDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.entity.MSelectListEntity;
import jp.loioz.entity.TPersonAddHojinEntity;
import jp.loioz.entity.TPersonAddKojinEntity;
import jp.loioz.entity.TPersonAddLawyerEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 名簿新規登録画面のserviceクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PersonRegistService extends DefaultService {

	@Autowired
	private CommonFileManagementService commonFileManagementService;

	@Autowired
	private TPersonDao tPersonDao;

	@Autowired
	private TPersonAddKojinDao tPersonAddKojinDao;

	@Autowired
	private TPersonAddHojinDao tPersonAddHojinDao;

	@Autowired
	private TPersonAddLawyerDao tPersonAddLawyerDao;

	@Autowired
	private MSelectListDao mSelectListDao;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 名簿登録の入力フォームを作成する
	 * 
	 * @return
	 */
	public PersonRegistInputForm.PersonRegistBasicInputForm createNewPersonBasicInputForm() {

		PersonRegistInputForm.PersonRegistBasicInputForm inputForm = new PersonRegistInputForm.PersonRegistBasicInputForm();

		// 表示情報設定
		this.setDisplayData(inputForm);
		
		// デフォルト値を設定
		inputForm.setCustomerType(CustomerType.KOJIN);
		inputForm.setGender(Gender.UNKNOW);

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
	public void setDisplayData(PersonRegistInputForm.PersonRegistBasicInputForm inputForm) {

		// 性別選択肢
		List<SelectOptionForm> genderOptionList = Stream
				.of(Gender.MALE, Gender.FEMALE, Gender.UNKNOW)
				.map(SelectOptionForm::fromEnum)
				.collect(Collectors.toList());
		inputForm.setGenderOptionList(genderOptionList);

		// 選択肢情報を取得
		List<MSelectListEntity> selectEntityList = mSelectListDao.selectEnabled();

		// 選択肢区分でグループ化
		Map<String, List<SelectOptionForm>> selectOptionMap = selectEntityList.stream()
				.collect(Collectors.groupingBy(
						MSelectListEntity::getSelectType,
						Collectors.mapping(
								entity -> new SelectOptionForm(entity.getSelectSeq(), entity.getSelectVal()),
								Collectors.toList())));

		// 登録日
		inputForm.setCustomerCreatedDate(LocalDate.now());

		// 相談経路プルダウン
		List<SelectOptionForm> sodanRouteOptionList = selectOptionMap.getOrDefault(SelectType.SODANKEIRO.getCd(), Collections.emptyList());
		inputForm.setSodanRouteOptionList(sodanRouteOptionList);
	}

	/**
	 * 名簿基本情報を登録する
	 * 
	 * @param basicInputForm
	 * @throws AppException
	 */
	public void registPersonBasic(PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm) throws AppException {

		CustomerType customerType = basicInputForm.getCustomerType();

		// 名簿情報を登録
		registCustomer(basicInputForm);

		if (customerType == CustomerType.KOJIN) {
			// 個人名簿付帯情報を登録
			this.registPersonAddKojin(basicInputForm);
		} else if (customerType == CustomerType.HOJIN) {
			// 法人名簿付帯情報を登録
			this.registPersonAddHojin(basicInputForm);
		} else {
			// 弁護士名簿付帯情報を登録
			this.registPersonAddLawyer(basicInputForm);
		}

		// ファイル管理の顧客フォルダを作成
		this.registCustomerFolder(basicInputForm);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 名簿情報を登録する。<br>
	 * 登録した名簿IDを basicInputForm にセットする。
	 * 
	 * @param basicInputForm
	 * @throws AppException
	 */
	private void registCustomer(PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm) throws AppException {

		TPersonEntity insertEntity = new TPersonEntity();

		// 種別（個人／企業・団体／弁護士）、登録日
		insertEntity.setCustomerType(DefaultEnum.getCd(basicInputForm.getCustomerType()));
		insertEntity.setCustomerCreatedDate(basicInputForm.getCustomerCreatedDate());

		// 姓
		insertEntity.setCustomerNameSei(basicInputForm.getCustomerNameSei());
		insertEntity.setCustomerNameSeiKana(basicInputForm.getCustomerNameSeiKana());

		// 名
		insertEntity.setCustomerNameMei(basicInputForm.getCustomerNameMei());
		insertEntity.setCustomerNameMeiKana(basicInputForm.getCustomerNameMeiKana());

		// 特記事項
		insertEntity.setRemarks(basicInputForm.getRemarks());

		// 相談経路
		insertEntity.setSodanRoute(basicInputForm.getSodanRoute());
		insertEntity.setSodanRemarks(basicInputForm.getSodanRemarks());

		// 顧客フラグ
		insertEntity.setCustomerFlg(SystemFlg.FLG_OFF.getCd());

		// 顧問フラグ
		insertEntity.setAdvisorFlg(SystemFlg.FLG_OFF.getCd());

		// 名簿情報登録
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

		// 名簿IDをフォームにセット
		basicInputForm.setPersonId(personId);
		basicInputForm.setCustomerId(personId);
	}

	/**
	 * 個人付帯情報を登録する
	 * 
	 * @param basicInputForm
	 * @throws AppException
	 */
	private void registPersonAddKojin(PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm) throws AppException {

		TPersonAddKojinEntity entity = tPersonAddKojinDao.selectPersonAddKojinByPersonId(basicInputForm.getPersonId());
		if (entity != null) {
			// 既に存在する場合 -> 楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 名簿ID
		entity = new TPersonAddKojinEntity();
		entity.setPersonId(basicInputForm.getPersonId());

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
		}

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
	 * @param basicInputForm
	 * @throws AppException
	 */
	private void registPersonAddHojin(PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm) throws AppException {

		TPersonAddHojinEntity entity = tPersonAddHojinDao.selectPersonAddHojinByPersonId(basicInputForm.getPersonId());
		if (entity != null) {
			// 既に存在する場合 -> 楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 名簿ID
		entity = new TPersonAddHojinEntity();
		entity.setPersonId(basicInputForm.getPersonId());

		// 代表
		entity.setDaihyoName(basicInputForm.getDaihyoName());
		entity.setDaihyoNameKana(basicInputForm.getDaihyoNameKana());
		entity.setDaihyoPositionName(basicInputForm.getDaihyoPositionName());

		// 担当
		entity.setTantoName(basicInputForm.getTantoName());
		entity.setTantoNameKana(basicInputForm.getTantoNameKana());
		
		// 旧商号
		entity.setOldHojinName(basicInputForm.getOldHojinName());

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
	 * 弁護士付帯情報を登録する
	 * 
	 * @param basicInputForm
	 * @throws AppException
	 */
	private void registPersonAddLawyer(PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm) throws AppException {

		List<TPersonAddLawyerEntity> tPersonAddLawyerEntityList = tPersonAddLawyerDao.selectPersonAddLawyerByPersonId(List.of(basicInputForm.getPersonId()));

		if (LoiozCollectionUtils.isNotEmpty(tPersonAddLawyerEntityList)) {
			// 既に存在する場合 -> 楽観ロックエラーとする
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 登録データセット
		TPersonAddLawyerEntity tPersonAddLawyerEntity = new TPersonAddLawyerEntity();
		tPersonAddLawyerEntity.setPersonId(basicInputForm.getPersonId());
		tPersonAddLawyerEntity.setBushoName(basicInputForm.getBushoName());
		tPersonAddLawyerEntity.setJimushoName(basicInputForm.getJimushoName());

		// 弁護士付帯情報登録
		int insertCount = tPersonAddLawyerDao.insert(tPersonAddLawyerEntity);

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
	 * @param basicInputForm
	 */
	private void registCustomerFolder(PersonRegistInputForm.PersonRegistBasicInputForm basicInputForm) {

		// フォルダ名
		StringBuilder folderName = new StringBuilder();
		folderName.append(StringUtils.null2blank(basicInputForm.getCustomerNameSei()));
		folderName.append(StringUtils.null2blank(basicInputForm.getCustomerNameMei()));

		// ファイル管理の顧客フォルダを登録
		commonFileManagementService.createCustomerFolder(basicInputForm.getCustomerId(), folderName.toString());
	}
}
