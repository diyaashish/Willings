package jp.loioz.app.common.mvc.ankenCustomer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.mvc.ankenCustomer.dto.PersonSearchResultDto;
import jp.loioz.app.common.mvc.ankenCustomer.form.CustomerRegistForm;
import jp.loioz.app.common.service.CommonFileManagementService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TPersonAddHojinDao;
import jp.loioz.dao.TPersonAddKojinDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.condition.AnkenCustomerSearchCondition;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenCustomerListDto;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TPersonAddHojinEntity;
import jp.loioz.entity.TPersonAddKojinEntity;
import jp.loioz.entity.TPersonEntity;
import lombok.NonNull;

/**
 * MVC案件-顧客登録サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CustomerRegistCommonService extends DefaultService {

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件-顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 名簿個人付帯情報Daoクラス */
	@Autowired
	private TPersonAddKojinDao tPersonAddKojinDao;

	/** 名簿法人付帯情報Daoクラス */
	@Autowired
	private TPersonAddHojinDao tPersonAddHojinDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 顧客情報関連の共通サービス処理 */
	@Autowired
	private CommonPersonService commonCustomerService;

	/** 共通サービス：ファイル管理 */
	@Autowired
	private CommonFileManagementService commonFileManagementService;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// 顧客登録モーダル_顧客登録フラグメント
	// =========================================================================
	/**
	 * 顧客登録モーダルの入力用オブジェクトを作成する
	 * 
	 * @param ankenId
	 * @return
	 */
	public CustomerRegistForm.RegistFragmentInputForm createCustomerRegistModalInputForm(@NonNull Long ankenId) {
		CustomerRegistForm.RegistFragmentInputForm inputForm = new CustomerRegistForm.RegistFragmentInputForm();
		inputForm.setAnkenId(ankenId);
		inputForm.setCustomerType(CustomerType.KOJIN);
		return inputForm;
	}

	/**
	 * 名簿の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registCustomer(CustomerRegistForm.RegistFragmentInputForm inputForm) throws AppException {

		// 名簿テーブル
		TPersonEntity personEntity = new TPersonEntity();
		// 案件-顧客テーブル
		TAnkenCustomerEntity ankenCustomerEntity = new TAnkenCustomerEntity();

		// 名簿情報の設定
		personEntity.setCustomerNameSei(inputForm.getCustomerNameSei());
		personEntity.setCustomerNameSeiKana(inputForm.getCustomerNameSeiKana());
		personEntity.setCustomerNameMei(inputForm.getCustomerNameMei());
		personEntity.setCustomerNameMeiKana(inputForm.getCustomerNameMeiKana());
		personEntity.setCustomerType(inputForm.getCustomerType().getCd());
		personEntity.setCustomerCreatedDate(LocalDate.now());

		// 「顧客」として登録するので、customer_flgを立てて登録する（※後続の処理でt_anken_customerにレコードを登録すること）
		personEntity.setCustomerFlg(SystemFlg.FLG_ON.getCd());
		// 「顧問」フラグは立てない
		personEntity.setAdvisorFlg(SystemFlg.FLG_OFF.getCd());

		try {
			tPersonDao.insert(personEntity);
		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, e);
		}

		// 名簿IDを顧客IDにセットする
		Long personId = personEntity.getPersonId();
		personEntity.setCustomerId(personId);
		try {
			tPersonDao.update(personEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, ex);
		}

		// 登録した名簿情報をも元に、中間テーブルと子テーブルにもデータをインサートする
		ankenCustomerEntity.setCustomerId(personEntity.getCustomerId());
		ankenCustomerEntity.setAnkenId(inputForm.getAnkenId());
		ankenCustomerEntity.setKanryoFlg(SystemFlg.FLG_OFF.getCd());
		ankenCustomerEntity.setAnkenStatus(AnkenStatus.SODAN.getCd());

		try {
			tAnkenCustomerDao.insert(ankenCustomerEntity);
		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, e);
		}

		// 顧客-法人個人
		if (CustomerType.HOJIN == inputForm.getCustomerType()) {
			// 法人の時
			TPersonAddHojinEntity personAddHojinEntity = new TPersonAddHojinEntity();
			personAddHojinEntity.setPersonId(personEntity.getCustomerId());

			try {
				tPersonAddHojinDao.insert(personAddHojinEntity);
			} catch (OptimisticLockingFailureException e) {
				// 楽観エラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + e.getMessage());
				throw new AppException(MessageEnum.MSG_E00012, e);
			}

		} else if (CustomerType.KOJIN == inputForm.getCustomerType()) {
			// 個人の時
			TPersonAddKojinEntity personAddKojinEntity = new TPersonAddKojinEntity();
			personAddKojinEntity.setPersonId(personEntity.getPersonId());
			// 性別をデフォルトで不明に設定する
			personAddKojinEntity.setGenderType(Gender.UNKNOW.getCd());

			try {
				tPersonAddKojinDao.insert(personAddKojinEntity);
			} catch (OptimisticLockingFailureException e) {
				// 楽観エラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + e.getMessage());
				throw new AppException(MessageEnum.MSG_E00012, e);
			}
		}

		try {
			commonFileManagementService.createCustomerFolder(
					personEntity.getCustomerId(),
					PersonName.fromEntity(personEntity).getName());

		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, e);
		}

	}

	// =========================================================================
	// 顧客検索モーダル_顧客登録フラグメント
	// =========================================================================

	/**
	 * 顧客検索フラグメントの画面表示オブジェクトを作成
	 * 
	 * @param searchForm
	 * @return
	 */
	public CustomerRegistForm.SearchFragmentViewForm createSearchFragmentViewForm(CustomerRegistForm.SearchFragmentSearchForm searchForm) {

		CustomerRegistForm.SearchFragmentViewForm viewForm = new CustomerRegistForm.SearchFragmentViewForm();

		// 検索処理を行ったときのみ、検索する
		if (searchForm.isOpenSearchResult()) {

			// 検索条件
			AnkenCustomerSearchCondition searchCondition = searchForm.toAnkenCustomerSearchCondition();

			// 顧客一覧を取得する
			List<AnkenCustomerListDto> ankenCustomerDtoList = tAnkenCustomerDao.selectBySearchCondition(searchCondition);
			List<PersonSearchResultDto> searchResult = ankenCustomerDtoList.stream().map(e -> {

				// 画面表示用に変換
				PersonSearchResultDto dto = new PersonSearchResultDto();
				dto.setPersonId(e.getPersonId());
				dto.setCustomerName(e.getCustomerName());
				dto.setCustomerNameKana(e.getCustomerNameKana());
				dto.setPersonAttribute(PersonAttribute.of(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType()));
				dto.setZipCode(e.getZipCode());
				dto.setAddress1(e.getAddress1());
				dto.setAddress2(e.getAddress2());
				return dto;
			}).collect(Collectors.toList());

			viewForm.setSearchResultList(searchResult);
			// 表示最大件数を超えたか判定
			if (searchResult.size() == CommonConstant.OVER_VIEW_LIMIT) {
				viewForm.setOverViewCntFlg(true);
			} else {
				viewForm.setOverViewCntFlg(false);
			}
		}

		viewForm.setAnkenId(searchForm.getAnkenId());
		return viewForm;
	}

	/**
	 * 名簿検索後の案件-顧客登録処理
	 * 
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	public void registSearchResultCustomer(@NonNull Long ankenId, @NonNull Long personId) throws AppException {

		// 対象名簿が「弁護士」になっている場合は顧客登録は不可（登録候補の検索時にも表示されない）
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (personEntity == null || CustomerType.LAWYER == CustomerType.of(personEntity.getCustomerType())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		// 対象案件に関与者として既に登録されている名簿は、顧客としての登録は不可（登録候補の検索時にも表示されない）
		List<TKanyoshaEntity> ankenKanyoshaList = tKanyoshaDao.selectKanyoshaListByAnkenId(ankenId);
		if (ankenKanyoshaList.stream().anyMatch(e -> Objects.equals(personId, e.getPersonId()))) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// エンティティの作成
		TAnkenCustomerEntity registEntity = new TAnkenCustomerEntity();

		// 登録するデータの作成
		registEntity.setAnkenId(ankenId);
		registEntity.setCustomerId(personId);
		registEntity.setAnkenStatus(AnkenStatus.SODAN.getCd());
		registEntity.setKanryoFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// 案件顧客テーブル登録処理
			tAnkenCustomerDao.insert(registEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 名簿属性のFlgの更新
		commonCustomerService.updatePersonAttributeFlgs(personId);

	}

}
