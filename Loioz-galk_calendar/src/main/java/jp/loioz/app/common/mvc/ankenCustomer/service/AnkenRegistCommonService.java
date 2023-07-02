package jp.loioz.app.common.mvc.ankenCustomer.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.mvc.ankenCustomer.dto.AnkenSearchResultDto;
import jp.loioz.app.common.mvc.ankenCustomer.form.AnkenRegistForm;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonFileManagementService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.bean.AnkenAitegataBean;
import jp.loioz.bean.CustomerAnkenSearchListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TAnkenAddKeijiDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.condition.CustomerAnkenSearchCondition;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.TAnkenAddKeijiEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TPersonEntity;
import lombok.NonNull;

/**
 * MVC顧客-案件登録サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenRegistCommonService extends DefaultService {

	/** 共通サービス：ファイル管理 */
	@Autowired
	private CommonFileManagementService commonFileManagementService;

	/** 共通サービス：分野 */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 共通サービス：顧客 */
	@Autowired
	private CommonPersonService commonCustomerService;

	/** 共通サービス：案件 */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 名簿情報Dao */
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件-顧客情報Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 案件情報Dao */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件刑事付帯情報Dao */
	@Autowired
	private TAnkenAddKeijiDao tAnkenAddKeijiDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// 案件登録モーダル_案件登録フラグメント
	// =========================================================================
	/**
	 * 案件登録モーダルの入力用オブジェクトを作成する
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenRegistForm.RegistFragmentInputForm createAnkenRegistModalInputForm(@NonNull Long personId) {
		AnkenRegistForm.RegistFragmentInputForm inputForm = new AnkenRegistForm.RegistFragmentInputForm();
		this.setUpDispProperties(inputForm);
		inputForm.setPersonId(personId);
		return inputForm;
	}

	/**
	 * 案件登録フラグメントの画面表示用項目を設定する
	 * 
	 * @param inputForm
	 */
	public void setUpDispProperties(AnkenRegistForm.RegistFragmentInputForm inputForm) {

		// 分野セレクトボックスを設定(利用可能な分野のみ)
		List<BunyaDto> bunyaIdSelectOption = commonBunyaService.getBunyaList(false);
		inputForm.setBunyaIdSelectOption(bunyaIdSelectOption);
	}

	/**
	 * 案件の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public Long registAnken(AnkenRegistForm.RegistFragmentInputForm inputForm) throws AppException {

		// 登録前の整合性チェック
		this.registAnkenDbValidate(inputForm);

		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(inputForm.getPersonId());
		LocalDate now = LocalDate.now();

		// **************************************
		// 案件情報
		// **************************************
		TAnkenEntity tAnkenEntity = new TAnkenEntity();
		tAnkenEntity.setBunyaId(inputForm.getBunyaId());
		tAnkenEntity.setAnkenType(DefaultEnum.getCd(AnkenType.JIMUSHO));
		tAnkenEntity.setAnkenCreatedDate(now);

		tAnkenDao.insert(tAnkenEntity);

		// **************************************
		// 刑事付帯情報
		// **************************************
		Long ankenId = tAnkenEntity.getAnkenId();

		if (commonBunyaService.isKeiji(tAnkenEntity.getBunyaId())) {
			// 刑事の場合
			TAnkenAddKeijiEntity ankenAddKeijiEntity = new TAnkenAddKeijiEntity();
			ankenAddKeijiEntity.setAnkenId(ankenId);
			ankenAddKeijiEntity.setLawyerSelectType(BengoType.SHISEN.getCd());
			tAnkenAddKeijiDao.insert(ankenAddKeijiEntity);
		}

		// **************************************
		// 案件-顧客情報
		// **************************************
		TAnkenCustomerEntity tAnkenCustomerEntity = new TAnkenCustomerEntity();
		tAnkenCustomerEntity.setAnkenId(ankenId);
		tAnkenCustomerEntity.setCustomerId(personEntity.getCustomerId());
		tAnkenCustomerEntity.setKanryoFlg(SystemFlg.FLG_OFF.getCd());
		tAnkenCustomerEntity.setAnkenStatus(AnkenStatus.SODAN.getCd());
		tAnkenCustomerDao.insert(tAnkenCustomerEntity);

		// **************************************
		// ファイル管理-案件フォルダ登録
		// **************************************
		commonFileManagementService.createAnkenFolder(ankenId, "");

		// **************************************
		// 顧客情報
		// **************************************
		personEntity.setCustomerFlg(SystemFlg.FLG_ON.getCd());
		tPersonDao.update(personEntity);

		try {
			// 処理実行
			tPersonDao.update(personEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		return ankenId;
	}

	/**
	 * 案件登録のDB整合性バリデーション
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	private void registAnkenDbValidate(AnkenRegistForm.RegistFragmentInputForm inputForm) throws AppException {

		// 対象名簿が「弁護士」になっている場合は顧客登録は不可（登録候補の検索時にも表示されない）
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(inputForm.getPersonId());
		if (personEntity == null || CustomerType.LAWYER == CustomerType.of(personEntity.getCustomerType())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 分野の整合性チェック
		if (!commonBunyaService.isEnabled(inputForm.getBunyaId())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 登録上限の重複チェック
		if (commonAnkenService.isAnkenAdd(personEntity.getCustomerId())) {
			// 登録上限を超えて登録はエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00067, null, String.valueOf(CommonConstant.ANKEN_ADD_LIMIT));
		}
	}

	// =========================================================================
	// 案件検索モーダル_案件登録フラグメント
	// =========================================================================

	/**
	 * 案件検索フラグメントの画面表示オブジェクトを作成
	 * 
	 * @param searchForm
	 * @return
	 */
	public AnkenRegistForm.SearchFragmentViewForm createSearchFragmentViewForm(AnkenRegistForm.SearchFragmentSearchForm searchForm) {

		AnkenRegistForm.SearchFragmentViewForm viewForm = new AnkenRegistForm.SearchFragmentViewForm();
		this.setUpDispProperties(viewForm);

		// 検索処理を行ったときのみ、検索する
		if (searchForm.isOpenSearchResult()) {

			// 紐付け対象の名簿ID
			Long personId = searchForm.getPersonId();

			// 検索条件オブジェクトを作成する
			CustomerAnkenSearchCondition searchCondition = searchForm.toCustomerAnkenSearchCondition();

			// 除外する案件の条件を設定する
			Set<Long> excludeAnkenIdList = new HashSet<>();

			// 直接的な案件を除外対象に加える
			List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByCustomerId(personId);
			tAnkenCustomerEntities.stream().map(TAnkenCustomerEntity::getAnkenId).forEach(excludeAnkenIdList::add);

			// 間接的な案件を除外対象に加える
			List<TKanyoshaEntity> tKanyoshaEntities = tKanyoshaDao.selectKanyoshaListByPersonId(personId);
			tKanyoshaEntities.stream().map(TKanyoshaEntity::getAnkenId).forEach(excludeAnkenIdList::add);

			// 除外対象案件IDを検索条件オブジェクトに設定
			searchCondition.setExcludeAnkenIdList(new ArrayList<>(excludeAnkenIdList));

			// 案件一覧を取得する
			List<CustomerAnkenSearchListBean> customerAnkenSearchListBeanList = tAnkenCustomerDao.selectCustomerAnkenSearchBeanByConditions(searchCondition);
			List<Long> ankenIdList = customerAnkenSearchListBeanList.stream().map(CustomerAnkenSearchListBean::getAnkenId).collect(Collectors.toList());

			// 相手方情報の取得
			List<AnkenAitegataBean> ankenAitegataBean = tKanyoshaDao.selectAnkenAitegataBeanByAnkenIdList(ankenIdList);
			Function<AnkenAitegataBean, AnkenSearchResultDto.Aitegata> mapping = e -> {
				AnkenSearchResultDto.Aitegata aitegataDto = new AnkenSearchResultDto.Aitegata();
				aitegataDto.setAitegataName(e.getAitegataName());
				aitegataDto.setAitegataNameKana(e.getAitegataNameKana());
				aitegataDto.setOldName(e.getOldName());
				aitegataDto.setOldNameKana(e.getOldNameKana());
				return aitegataDto;
			};

			// 表示用に加工
			Map<Long, List<AnkenSearchResultDto.Aitegata>> ankenIdToAitegataMap = ankenAitegataBean.stream()
					.filter(e -> SystemFlg.FLG_OFF.equalsByCode(e.getDairiFlg()))
					.collect(Collectors.groupingBy(
							AnkenAitegataBean::getAnkenId,
							Collectors.mapping(mapping, Collectors.toList())));

			// Bean -> Dto
			List<AnkenSearchResultDto> searchResult = customerAnkenSearchListBeanList.stream().map(e -> {
				AnkenSearchResultDto ankenSearchResultDto = new AnkenSearchResultDto();
				ankenSearchResultDto.setAnkenId(AnkenId.of(e.getAnkenId()));
				ankenSearchResultDto.setBunya(e.getBunyaName());
				ankenSearchResultDto.setAnkenName(e.getAnkenName());
				ankenSearchResultDto.setCustomerName(e.getCustomerName());
				ankenSearchResultDto.setAitegataList(ankenIdToAitegataMap.getOrDefault(e.getAnkenId(), Collections.emptyList()));
				return ankenSearchResultDto;
			}).collect(Collectors.toList());

			viewForm.setSearchResultList(searchResult);
			// 表示最大件数を超えたか判定
			if (searchResult.size() == CommonConstant.OVER_VIEW_LIMIT) {
				viewForm.setOverViewCntFlg(true);
			} else {
				viewForm.setOverViewCntFlg(false);
			}
		}

		viewForm.setPersonId(searchForm.getPersonId());
		return viewForm;
	}

	/**
	 * 画面表示用オブジェクト
	 * 
	 * @param viewForm
	 */
	public void setUpDispProperties(AnkenRegistForm.SearchFragmentViewForm viewForm) {

		// 分野セレクトボックスを設定 ※検索プルダウンは利用停止も含む
		List<BunyaDto> bunyaIdSelectOption = commonBunyaService.getBunyaList(true);
		viewForm.setBunyaIdSelectOption(bunyaIdSelectOption);
	}

	/**
	 * 検索結果の案件を紐づける処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void registSearchResultAnken(Long personId, Long ankenId) throws AppException {

		// DB整合性チェック
		this.registSearchResultAnkenDbValidate(personId, ankenId);

		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		Long customerId = personEntity.getCustomerId();

		// エンティティの作成
		TAnkenCustomerEntity registEntity = new TAnkenCustomerEntity();

		// 登録するデータの作成
		registEntity.setAnkenId(ankenId);
		registEntity.setCustomerId(customerId);
		registEntity.setAnkenStatus(AnkenStatus.SODAN.getCd());
		registEntity.setKanryoFlg(SystemFlg.FLG_OFF.getCd());

		// 登録処理
		tAnkenCustomerDao.insert(registEntity);

		// 名簿属性のFlgの更新
		commonCustomerService.updatePersonAttributeFlgs(customerId);
	}

	/**
	 * DBの整合性チェック
	 * 
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	public void registSearchResultAnkenDbValidate(Long personId, Long ankenId) throws AppException {

		// 対象名簿が「弁護士」になっている場合は顧客登録は不可（登録候補の検索時にも表示されない）
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (personEntity == null || CustomerType.LAWYER == CustomerType.of(personEntity.getCustomerType())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 選択した案件の存在チェック
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);
		if (tAnkenEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 既に対象案件に顧客が紐づいている
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, personEntity.getCustomerId());
		if (tAnkenCustomerEntity != null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00026, null);
		}

		// 登録上限の重複チェック
		if (commonAnkenService.isAnkenAdd(personEntity.getCustomerId())) {
			// 登録上限を超えて登録はエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00067, null, String.valueOf(CommonConstant.ANKEN_ADD_LIMIT));
		}

		// 顧客の紐づけ上限件数チェック
		if (commonCustomerService.isCustomerAdd(ankenId)) {
			// 登録上限を超えて登録はエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00165, null, Long.toString(ankenId), String.valueOf(CommonConstant.CUSTOMER_ADD_LIMIT));
		}

		// 指定した案件の関与者として登録されているかチェック
		List<TKanyoshaEntity> tKanyoshaEntities = tKanyoshaDao.selectKanyoshaListByAnkenId(ankenId);
		if (tKanyoshaEntities.stream().anyMatch(e -> Objects.equal(e.getPersonId(), personId))) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

}
