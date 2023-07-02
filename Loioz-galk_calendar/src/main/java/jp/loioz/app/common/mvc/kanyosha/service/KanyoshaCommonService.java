package jp.loioz.app.common.mvc.kanyosha.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.mvc.kanyosha.dto.PersonSearchResultDto;
import jp.loioz.app.common.mvc.kanyosha.form.KanyoshaEditModalInputForm;
import jp.loioz.app.common.mvc.kanyosha.form.KanyoshaSearchModalSearchForm;
import jp.loioz.app.common.mvc.kanyosha.form.KanyoshaSearchModalViewForm;
import jp.loioz.app.common.service.CommonKanyoshaService;
import jp.loioz.bean.KanyoshaBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.KanyoshaModalDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenRelatedKanyoshaDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TPersonAddHojinDao;
import jp.loioz.dao.TPersonAddKojinDao;
import jp.loioz.dao.TPersonAddLawyerDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanRelatedKanyoshaDao;
import jp.loioz.domain.condition.KanyoshaModalCustomerSearchCondition;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenCustomerDto;
import jp.loioz.entity.TAnkenRelatedKanyoshaEntity;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TPersonAddHojinEntity;
import jp.loioz.entity.TPersonAddKojinEntity;
import jp.loioz.entity.TPersonAddLawyerEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanRelatedKanyoshaEntity;

/**
 * MVC関与者サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KanyoshaCommonService extends DefaultService {

	@Autowired
	private CommonKanyoshaService commonKanyoshaService;

	/** 関与者Daoクラス */
	@Autowired
	private KanyoshaModalDao kanyoshaModalDao;

	/** 名簿情報 */
	@Autowired
	private TPersonDao tPersonDao;

	/** 名簿-個人Dao */
	@Autowired
	private TPersonAddKojinDao tPersonAddKojinDao;

	/** 名簿-法人Dao */
	@Autowired
	private TPersonAddHojinDao tPersonAddHojinDao;

	/** 名簿-弁護士Dao */
	@Autowired
	private TPersonAddLawyerDao tPersonAddLawyerDao;

	/** 案件-顧客情報 */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 関与者一覧用のDaoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 案件-関与者の関連情報Dao */
	@Autowired
	private TAnkenRelatedKanyoshaDao tAnkenRelatedKanyoshaDao;

	/** 裁判-関与者の関連情報Dao */
	@Autowired
	private TSaibanRelatedKanyoshaDao tSaibanRelatedKanyoshaDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	/**
	 * 関与者編集モーダルの登録画面用オブジェクトを作成
	 * 
	 * @param ankenId
	 * @param customerType
	 * @return
	 */
	public KanyoshaEditModalInputForm createKanyoshaEditInputForm(Long ankenId, CustomerType customerType) throws AppException {
		KanyoshaEditModalInputForm inputForm = new KanyoshaEditModalInputForm();
		this.setDispProperties(inputForm);

		inputForm.setAnkenId(ankenId);
		inputForm.setCustomerType(customerType != null ? customerType : CustomerType.KOJIN);
		return inputForm;
	}

	/**
	 * 関与者編集モーダルの編集画面用オブジェクトを作成
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	public KanyoshaEditModalInputForm createKanyoshaEditInputForm(Long kanyoshaSeq) throws AppException {
		KanyoshaEditModalInputForm inputForm = new KanyoshaEditModalInputForm();
		inputForm.setKanyoshaSeq(kanyoshaSeq);

		TKanyoshaEntity tKanyoshaEntity = tKanyoshaDao.selectKanyoshaByKanyoshaSeq(kanyoshaSeq);
		if (tKanyoshaEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(tKanyoshaEntity.getPersonId());
		if (personEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 表示用プロパティを設定する
		this.setDispProperties(inputForm);

		// 名簿ID取得
		Long personId = personEntity.getPersonId();
		inputForm.setCustomerType(CustomerType.of(personEntity.getCustomerType()));
		inputForm.setCustomerNameSei(personEntity.getCustomerNameSei());
		inputForm.setCustomerNameMei(personEntity.getCustomerNameMei());
		inputForm.setCustomerNameSeiKana(personEntity.getCustomerNameSeiKana());
		inputForm.setCustomerNameMeiKana(personEntity.getCustomerNameMeiKana());
		// 弁護士の場合、事務所名を取得
		if (CommonConstant.CustomerType.LAWYER.equalsByCode(personEntity.getCustomerType())) {
			TPersonAddLawyerEntity tPersonAddLawyerEntity = tPersonAddLawyerDao.selectById(personId);
			inputForm.setJimushoName(tPersonAddLawyerEntity.getJimushoName());
		}

		inputForm.setAnkenId(tKanyoshaEntity.getAnkenId());
		inputForm.setKankei(tKanyoshaEntity.getKankei());
		inputForm.setRemarks(tKanyoshaEntity.getRemarks());

		return inputForm;
	}

	/**
	 * 関与者編集モーダルの表示用プロパティを設定する
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void setDispProperties(KanyoshaEditModalInputForm inputForm) throws AppException {

		// 関与者SEQがない場合は、何もせずに返却 ※ 登録にも編集にもどちらにも必要なプロパティが今後追加された場合はこのif文の前に記載する
		if (inputForm.getKanyoshaSeq() == null) {
			return;
		}

		// 以下は編集の場合のみ

		TKanyoshaEntity tKanyoshaEntity = tKanyoshaDao.selectKanyoshaByKanyoshaSeq(inputForm.getKanyoshaSeq());
		if (tKanyoshaEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(tKanyoshaEntity.getPersonId());
		if (personEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		Long personId = personEntity.getPersonId();
		inputForm.setPersonId(personId);
		inputForm.setPersonAttribute(PersonAttribute.of(personEntity.getCustomerFlg(), personEntity.getAdvisorFlg(), personEntity.getCustomerType()));
		inputForm.setZipCode(personEntity.getZipCode());
		inputForm.setAddress1(personEntity.getAddress1());
		inputForm.setAddress2(personEntity.getAddress2());

		// 編集の場合のみ、以下のプロパティは表示項目となる
		inputForm.setCustomerType(CustomerType.of(personEntity.getCustomerType()));
		inputForm.setCustomerNameSei(personEntity.getCustomerNameSei());
		inputForm.setCustomerNameMei(personEntity.getCustomerNameMei());
		inputForm.setCustomerNameSeiKana(personEntity.getCustomerNameSeiKana());
		inputForm.setCustomerNameMeiKana(personEntity.getCustomerNameMeiKana());
		// 弁護士の場合、事務所名を取得
		if (CommonConstant.CustomerType.LAWYER.equalsByCode(personEntity.getCustomerType())) {
			TPersonAddLawyerEntity tPersonAddLawyerEntity = tPersonAddLawyerDao.selectById(personId);
			inputForm.setJimushoName(tPersonAddLawyerEntity.getJimushoName());
		}
	}

	/**
	 * 関与者検索モーダルの画面表示用オブジェクトを作成
	 * 
	 * @param searchForm
	 * @return
	 */
	public KanyoshaSearchModalViewForm createKanyoshaSearchModalViewForm(KanyoshaSearchModalSearchForm searchForm) {
		KanyoshaSearchModalViewForm viewForm = new KanyoshaSearchModalViewForm();

		// 検索結果から除外する名簿IDを取得する（すでに案件に紐づいている名簿）
		List<Long> personIdList = this.getModalSearchExcludePersonIdList(searchForm);

		// 検索条件を作成し、検索
		KanyoshaModalCustomerSearchCondition searchCondition = searchForm.toKanyoshaModalCustomerSearchCondition(personIdList);
		List<TPersonEntity> tCustomerEntities = kanyoshaModalDao.selectPersonBySearchConditions(searchCondition);

		List<KanyoshaBean> kanyoshaBeans = tKanyoshaDao.selectKanyoshaBeanByAnkenId(searchForm.getAnkenId());
		Map<Long, KanyoshaBean> personIdToMap = kanyoshaBeans.stream().collect(Collectors.toMap(KanyoshaBean::getPersonId, Function.identity()));

		List<PersonSearchResultDto> personSearchResultDtoList = this.convert2Dto(tCustomerEntities, personIdToMap);
		// 表示最大件数を超えたか判定
		if (personSearchResultDtoList.size() == CommonConstant.OVER_VIEW_LIMIT) {
			viewForm.setOverViewCntFlg(true);
		} else {
			viewForm.setOverViewCntFlg(false);
		}

		viewForm.setAnkenId(searchForm.getAnkenId());
		viewForm.setMeiboList(personSearchResultDtoList);
		return viewForm;
	}

	/**
	 * 検索結果から除外する名簿IDを取得
	 * 
	 * @param ankenId
	 * @return
	 */
	private List<Long> getModalSearchExcludePersonIdList(KanyoshaSearchModalSearchForm searchForm) {

		// 案件ID
		Long ankenId = searchForm.getAnkenId();

		// 検索結果から除外する名簿IDを取得する（すでに案件に紐づいている名簿）
		Set<Long> personIdSet = new HashSet<>();

		// 該当案件の顧客として紐づいているので除外
		List<AnkenCustomerDto> ankenCustomerDtoList = tAnkenCustomerDao.selectAnkenCustomerDtoByAnkenId(ankenId);
		personIdSet.addAll(ankenCustomerDtoList.stream().map(AnkenCustomerDto::getPersonId).collect(Collectors.toSet()));

		KanyoshaType kanyoshaType = KanyoshaType.of(searchForm.getKanyoshaTypeCd());
		SystemFlg dairiFlg = SystemFlg.of(searchForm.getDairiFlg());

		if (kanyoshaType == null) {
			// すでに案件に紐づいているので除外
			List<TKanyoshaEntity> tKanyoshaEntities = tKanyoshaDao.selectKanyoshaListByAnkenId(ankenId);
			personIdSet.addAll(tKanyoshaEntities.stream().map(TKanyoshaEntity::getPersonId).collect(Collectors.toSet()));
			return new ArrayList<>(personIdSet);
		}

		List<KanyoshaBean> kanyoshaBeans = tKanyoshaDao.selectKanyoshaBeanByAnkenId(ankenId);
		Map<Long, KanyoshaBean> kanyoshaSeqToKanyoshaBean = kanyoshaBeans.stream().collect(Collectors.toMap(KanyoshaBean::getKanyoshaSeq, Function.identity()));

		// 以下、すでに案件に紐づいたデータ
		if (searchForm.getSaibanSeq() == null) {
			// 裁判SEQがnullの場合 -> 案件-関与者関係者が一覧に表示され、選択できない者を除外の対象とする
			List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectByAnkenId(ankenId);

			if (SystemFlg.FLG_OFF == dairiFlg && KanyoshaType.AITEGATA == kanyoshaType) {
				// 案件相手方 -> 役割を一つでも持っているデータは候補にならない
				Set<Long> excludeAitegataPersonId = tAnkenRelatedKanyoshaEntities.stream()
						.map(TAnkenRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeAitegataPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_ON == dairiFlg && KanyoshaType.AITEGATA == kanyoshaType) {
				// 案件相手方代理人 -> 相手方として登録されているデータは候補にならない
				Set<Long> excludeAitegataDairininPersonId = tAnkenRelatedKanyoshaEntities.stream()
						.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg()))
						.map(TAnkenRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeAitegataDairininPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_OFF == dairiFlg && KanyoshaType.KYOHANSHA == kanyoshaType) {
				// 案件共犯者 -> 役割を一つでも持っているデータは候補にならない
				Set<Long> excludeKyohanshaPersonId = tAnkenRelatedKanyoshaEntities.stream()
						.map(TAnkenRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeKyohanshaPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_ON == dairiFlg && KanyoshaType.KYOHANSHA == kanyoshaType) {
				// 案件共犯者弁護人 -> 代理人以外で利用されている, 被害者側に登録されているケースは候補にならない
				Set<Long> excludeKyohanshaBengoninPersonId = tAnkenRelatedKanyoshaEntities.stream()
						.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg()) || KanyoshaType.HIGAISHA.equalsByCode(e.getKanyoshaType())) // 代理人以外 もしくは 被害者側
						.map(TAnkenRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeKyohanshaBengoninPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_OFF == dairiFlg && KanyoshaType.HIGAISHA == kanyoshaType) {
				// 案件被害者 -> 役割を一つでも持っているデータは候補にならない
				Set<Long> excludeHigaishaPersonId = tAnkenRelatedKanyoshaEntities.stream()
						.map(TAnkenRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeHigaishaPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_ON == dairiFlg && KanyoshaType.HIGAISHA == kanyoshaType) {
				// 案件被害者代理人 -> 代理人以外で利用されている, 共犯者側に登録されているケースは候補にならない
				Set<Long> excludeKyohanshaBengoninPersonId = tAnkenRelatedKanyoshaEntities.stream()
						.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg()) || KanyoshaType.KYOHANSHA.equalsByCode(e.getKanyoshaType())) // 代理人以外 もしくは 相手方側
						.map(TAnkenRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeKyohanshaBengoninPersonId);
				return new ArrayList<>(personIdSet);
			}

			// ここまで処理が来る場合はエラー -> 案件に登録できない役割(kanyoshaType), 代理人かどうかの判別不能(dairiFlg)
			throw new RuntimeException("プロパティが想定外の値");
		}

		if (searchForm.getSaibanSeq() != null) {
			// 裁判SEQがnon-nullの場合 -> 裁判-関与者関係者が一覧に表示され、選択できない者を除外の対象とする

			List<TSaibanRelatedKanyoshaEntity> tSaibanRelatedKanyoshaEntities = tSaibanRelatedKanyoshaDao.selectBySaibanSeq(searchForm.getSaibanSeq());

			if (SystemFlg.FLG_OFF == dairiFlg && KanyoshaType.KYODOSOSHONIN == kanyoshaType) {
				// 裁判その他当事者 -> 役割を一つでも持っているデータは候補にならない
				Set<Long> excludeOtherPersonId = tSaibanRelatedKanyoshaEntities.stream()
						.map(TSaibanRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeOtherPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_ON == dairiFlg && KanyoshaType.KYODOSOSHONIN == kanyoshaType) {
				// 裁判その他当事者代理人 -> 代理人以外で利用されている, 相手方側に登録されているケースは候補にならない
				Set<Long> excludeOtherDairininPersonId = tSaibanRelatedKanyoshaEntities.stream()
						.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg()) || KanyoshaType.AITEGATA.equalsByCode(e.getKanyoshaType())) // 代理人以外 もしくは 相手方側
						.map(TSaibanRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeOtherDairininPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_OFF == dairiFlg && KanyoshaType.AITEGATA == kanyoshaType) {
				// 裁判相手方 -> 役割を一つでも持っているデータは候補にならない
				Set<Long> excludeAitegataPersonId = tSaibanRelatedKanyoshaEntities.stream()
						.map(TSaibanRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeAitegataPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_ON == dairiFlg && KanyoshaType.AITEGATA == kanyoshaType) {
				// 裁判相手方代理人 -> 代理人以外で利用されている, その他当事者（共同訴訟人）側に登録されているケースは候補にならない
				Set<Long> excludeAitegataDairininPersonId = tSaibanRelatedKanyoshaEntities.stream()
						.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg()) || KanyoshaType.KYODOSOSHONIN.equalsByCode(e.getKanyoshaType())) // 代理人以外 もしくは 共同訴訟人側
						.map(TSaibanRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeAitegataDairininPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_OFF == dairiFlg && KanyoshaType.KYOHANSHA == kanyoshaType) {
				// 裁判共犯者 -> 役割を一つでも持っているデータは候補にならない
				Set<Long> excludeKyohanshaPersonId = tSaibanRelatedKanyoshaEntities.stream()
						.map(TSaibanRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeKyohanshaPersonId);
				return new ArrayList<>(personIdSet);
			}

			if (SystemFlg.FLG_ON == dairiFlg && KanyoshaType.KYOHANSHA == kanyoshaType) {
				// 裁判共犯者弁護人 -> 共犯者として登録されている場合は代理人にはなれない
				Set<Long> excludeKyohanshaBengoninPersonId = tSaibanRelatedKanyoshaEntities.stream()
						.filter(e -> !SystemFlg.codeToBoolean(e.getDairiFlg())) // 代理人以外 もしくは 共同訴訟人側
						.map(TSaibanRelatedKanyoshaEntity::getKanyoshaSeq) // Entity -> kanyoshaSeq
						.map(e -> kanyoshaSeqToKanyoshaBean.get(e).getPersonId()) // kanyoshaSeq -> personId
						.collect(Collectors.toSet());
				personIdSet.addAll(excludeKyohanshaBengoninPersonId);
				return new ArrayList<>(personIdSet);
			}

			// ここまで処理が来る場合はエラー -> 案件に登録できない役割(kanyoshaType), 代理人かどうかの判別不能(dairiFlg)
			throw new RuntimeException("プロパティが想定外の値");
		}

		return new ArrayList<>(personIdSet);
	}

	/**
	 * TCustomerEntity -> PersonSearchResultDto
	 * 
	 * @param personEntityList
	 * @return
	 */
	private List<PersonSearchResultDto> convert2Dto(List<TPersonEntity> personEntityList, Map<Long, KanyoshaBean> personIdToKanyoshaBeanMap) {
		return personEntityList.stream().map(e -> {
			PersonSearchResultDto dto = new PersonSearchResultDto();
			dto.setPersonId(PersonId.of(e.getPersonId()));
			dto.setPersonAttribute(new PersonAttribute(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType()));
			dto.setPersonName(PersonName.fromEntity(e).getName());
			dto.setZipCode(e.getZipCode());
			dto.setAddress1(e.getAddress1());
			dto.setAddress2(e.getAddress2());
			dto.setKanyoshaSeq(personIdToKanyoshaBeanMap.getOrDefault(e.getPersonId(), new KanyoshaBean()).getKanyoshaSeq());
			return dto;
		}).collect(Collectors.toList());
	}

	/**
	 * 関与者編集モーダルの登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registKanyoshaInfo(KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		this.registKanyosha(personId, inputForm);
	}

	/**
	 * 関与者編集モーダルの更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateKanyoshaInfo(KanyoshaEditModalInputForm inputForm) throws AppException {

		Long kanyoshaSeq = inputForm.getKanyoshaSeq();
		TKanyoshaEntity tKanyoshaEntity = tKanyoshaDao.selectKanyoshaByKanyoshaSeq(kanyoshaSeq);
		if (tKanyoshaEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		tKanyoshaEntity.setKankei(inputForm.getKankei());
		tKanyoshaEntity.setRemarks(inputForm.getRemarks());

		try {
			// 更新処理
			tKanyoshaDao.update(tKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者共通モーダル 関与者の削除前チェック処理
	 * 
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public Map<String, Object> deleteKanyoshaBeforeCheck(Long kanyoshaSeq) throws AppException {

		// 共通関与者クラスで削除処理を行う
		return commonKanyoshaService.deleteCommonKanyoshaBeforeCheck(kanyoshaSeq);

	}

	/**
	 * 関与者共通モーダル 関与者の削除処理
	 * 
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void deleteKanyoshaInfo(Long kanyoshaSeq) throws AppException {

		// 共通関与者クラスで削除処理を行う
		commonKanyoshaService.deleteCommonKanyoshaInfo(kanyoshaSeq);

	}

	/**
	 * 関与者検索モーダルの検索結果から関与者を登録する
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 * @throws AppException
	 */
	public Long registKanyoshaSearchResult(Long ankenId, Long personId) throws AppException {
		return registKanyoshaForSearchResult(ankenId, personId);
	}

	// **********************************************************************************
	// 案件画面 相手方 / 相手方代理人の登録
	// **********************************************************************************

	/**
	 * 案件-相手方の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registKanyoshaInfoForAnkenAitegata(KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 相手方情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaEntity.setAnkenId(inputForm.getAnkenId());
		tAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.AITEGATA.getCd());
		tAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 案件-相手方代理人の登録処理
	 *
	 * @param inputForm
	 * @param aitegataKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaInfoForAnkenAitegataDairinin(KanyoshaEditModalInputForm inputForm, Long aitegataKanyoshaSeq) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 相手方代理人情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaEntity.setAnkenId(inputForm.getAnkenId());
		tAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.AITEGATA.getCd());
		tAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		// 相手方情報に代理人情報を登録
		TAnkenRelatedKanyoshaEntity tAnkenRelatedAitegataKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(inputForm.getAnkenId(), aitegataKanyoshaSeq);
		if (tAnkenRelatedAitegataKanyoshaEntity == null || Objects.nonNull(tAnkenRelatedAitegataKanyoshaEntity.getRelatedKanyoshaSeq())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		tAnkenRelatedAitegataKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaEntity);
			tAnkenRelatedKanyoshaDao.update(tAnkenRelatedAitegataKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者検索モーダルの検索結果から案件-相手方を登録する
	 *
	 * @param ankenId
	 * @param personId
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForAnkenAitegata(Long ankenId, Long personId, Long kanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 楽観チェック
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kanyoshaSeq);
		if (tAnkenRelatedKanyoshaEntity != null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 相手方情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaInsertEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaInsertEntity.setAnkenId(ankenId);
		tAnkenRelatedKanyoshaInsertEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaInsertEntity.setKanyoshaType(KanyoshaType.AITEGATA.getCd());
		tAnkenRelatedKanyoshaInsertEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaInsertEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaInsertEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 関与者検索モーダルの検索結果から関与者を登録する
	 *
	 * @param ankenId
	 * @param personId
	 * @param kanyoshaSeq
	 * @param aitegataKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForAnkenAitegataDairinin(Long ankenId, Long personId, Long kanyoshaSeq, Long aitegataKanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId)) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// すでに代理人として利用されている場合はデータが存在する
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kanyoshaSeq);
		if (tAnkenRelatedKanyoshaEntity != null &&
				!(KanyoshaType.AITEGATA.equalsByCode(tAnkenRelatedKanyoshaEntity.getKanyoshaType()) &&
						SystemFlg.FLG_ON.equalsByCode(tAnkenRelatedKanyoshaEntity.getDairiFlg()))) {
			// 相手方代理人として登録しようとした人が、相手方代理人以外としてすでに登録されている場合 -> エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		boolean existsDairininEntity = tAnkenRelatedKanyoshaEntity != null;

		// 相手方代理人情報を作成
		TAnkenRelatedKanyoshaEntity insertAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		insertAnkenRelatedKanyoshaEntity.setAnkenId(ankenId);
		insertAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.AITEGATA.getCd());
		insertAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		// 相手方情報に代理人情報を登録
		TAnkenRelatedKanyoshaEntity updateAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, aitegataKanyoshaSeq);
		if (updateAnkenRelatedKanyoshaEntity == null || Objects.nonNull(updateAnkenRelatedKanyoshaEntity.getRelatedKanyoshaSeq())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			if (!existsDairininEntity) {
				tAnkenRelatedKanyoshaDao.insert(insertAnkenRelatedKanyoshaEntity);
			}
			tAnkenRelatedKanyoshaDao.update(updateAnkenRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	// **********************************************************************************
	// 案件画面 共犯者 / 共犯者弁護人の登録
	// **********************************************************************************

	/**
	 * 案件-共犯者の登録処理
	 *
	 * @param inputForm
	 * @throws AppException
	 */
	public void registKanyoshaInfoForAnkenKyohansha(KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 共犯者情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaEntity.setAnkenId(inputForm.getAnkenId());
		tAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYOHANSHA.getCd());
		tAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 案件-共犯者弁護人の登録処理
	 *
	 * @param inputForm
	 * @param kyohanshaKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaInfoForAnkenKyohanshaBengonin(KanyoshaEditModalInputForm inputForm, Long kyohanshaKanyoshaSeq) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 共犯者弁護人情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaEntity.setAnkenId(inputForm.getAnkenId());
		tAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYOHANSHA.getCd());
		tAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		// 共犯者情報に代理人情報を登録
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKyohanshaKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(inputForm.getAnkenId(), kyohanshaKanyoshaSeq);
		if (tAnkenRelatedKyohanshaKanyoshaEntity == null || Objects.nonNull(tAnkenRelatedKyohanshaKanyoshaEntity.getRelatedKanyoshaSeq())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		tAnkenRelatedKyohanshaKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaEntity);
			tAnkenRelatedKanyoshaDao.update(tAnkenRelatedKyohanshaKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者検索モーダルの検索結果から案件-共犯者を登録する
	 *
	 * @param ankenId
	 * @param personId
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForAnkenKyohansha(Long ankenId, Long personId, Long kanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 楽観チェック
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kanyoshaSeq);
		if (tAnkenRelatedKanyoshaEntity != null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 共犯者情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaInsertEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaInsertEntity.setAnkenId(ankenId);
		tAnkenRelatedKanyoshaInsertEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaInsertEntity.setKanyoshaType(KanyoshaType.KYOHANSHA.getCd());
		tAnkenRelatedKanyoshaInsertEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaInsertEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaInsertEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者検索モーダルの検索結果から関与者を登録する
	 *
	 * @param ankenId
	 * @param personId
	 * @param kanyoshaSeq
	 * @param kyohanshaKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForAnkenKyohanshaBengonin(Long ankenId, Long personId, Long kanyoshaSeq, Long kyohanshaKanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || !CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// すでに代理人として利用されている場合はデータが存在する
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kanyoshaSeq);
		if (tAnkenRelatedKanyoshaEntity != null &&
				!(KanyoshaType.KYOHANSHA.equalsByCode(tAnkenRelatedKanyoshaEntity.getKanyoshaType()) &&
						SystemFlg.FLG_ON.equalsByCode(tAnkenRelatedKanyoshaEntity.getDairiFlg()))) {
			// 共犯者弁護人として登録しようとした人が、共犯者弁護人以外としてすでに登録されている場合 -> エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		boolean existsDairininEntity = tAnkenRelatedKanyoshaEntity != null;

		// 共犯者弁護人情報を作成
		TAnkenRelatedKanyoshaEntity insertAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		insertAnkenRelatedKanyoshaEntity.setAnkenId(ankenId);
		insertAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYOHANSHA.getCd());
		insertAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		// 共犯者情報に代理人情報を登録
		TAnkenRelatedKanyoshaEntity updateAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kyohanshaKanyoshaSeq);
		if (updateAnkenRelatedKanyoshaEntity == null || Objects.nonNull(updateAnkenRelatedKanyoshaEntity.getRelatedKanyoshaSeq())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			if (!existsDairininEntity) {
				tAnkenRelatedKanyoshaDao.insert(insertAnkenRelatedKanyoshaEntity);
			}
			tAnkenRelatedKanyoshaDao.update(updateAnkenRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	// **********************************************************************************
	// 案件画面 被害者/被害者代理人の登録
	// **********************************************************************************

	/**
	 * 案件-被害者の登録処理
	 *
	 * @param inputForm
	 * @throws AppException
	 */
	public void registKanyoshaInfoForAnkenHigaisha(KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 被害者情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaEntity.setAnkenId(inputForm.getAnkenId());
		tAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.HIGAISHA.getCd());
		tAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 案件-被害者代理人の登録処理
	 *
	 * @param inputForm
	 * @param higaishaKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaInfoForAnkenHigaishaDairinin(KanyoshaEditModalInputForm inputForm, Long higaishaKanyoshaSeq) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 被害者弁護人情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaEntity.setAnkenId(inputForm.getAnkenId());
		tAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.HIGAISHA.getCd());
		tAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		// 被害者情報に代理人情報を登録
		TAnkenRelatedKanyoshaEntity tAnkenRelatedHigaishaKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(inputForm.getAnkenId(), higaishaKanyoshaSeq);
		if (tAnkenRelatedHigaishaKanyoshaEntity == null || Objects.nonNull(tAnkenRelatedHigaishaKanyoshaEntity.getRelatedKanyoshaSeq())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		tAnkenRelatedHigaishaKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaEntity);
			tAnkenRelatedKanyoshaDao.update(tAnkenRelatedHigaishaKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者検索モーダルの検索結果から案件-被害者を登録する
	 *
	 * @param ankenId
	 * @param personId
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForAnkenHigaisha(Long ankenId, Long personId, Long kanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 楽観チェック
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kanyoshaSeq);
		if (tAnkenRelatedKanyoshaEntity != null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 被害者情報を作成
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaInsertEntity = new TAnkenRelatedKanyoshaEntity();
		tAnkenRelatedKanyoshaInsertEntity.setAnkenId(ankenId);
		tAnkenRelatedKanyoshaInsertEntity.setKanyoshaSeq(kanyoshaSeq);
		tAnkenRelatedKanyoshaInsertEntity.setKanyoshaType(KanyoshaType.HIGAISHA.getCd());
		tAnkenRelatedKanyoshaInsertEntity.setRelatedKanyoshaSeq(null);
		tAnkenRelatedKanyoshaInsertEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tAnkenRelatedKanyoshaDao.insert(tAnkenRelatedKanyoshaInsertEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者検索モーダルの検索結果から関与者を登録する
	 *
	 * @param ankenId
	 * @param personId
	 * @param kanyoshaSeq
	 * @param higaishaKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForAnkenHigaishaDairinin(Long ankenId, Long personId, Long kanyoshaSeq, Long higaishaKanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || !CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// すでに代理人として利用されている場合はデータが存在する
		TAnkenRelatedKanyoshaEntity tAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, kanyoshaSeq);
		if (tAnkenRelatedKanyoshaEntity != null &&
				!(KanyoshaType.HIGAISHA.equalsByCode(tAnkenRelatedKanyoshaEntity.getKanyoshaType()) &&
						SystemFlg.FLG_ON.equalsByCode(tAnkenRelatedKanyoshaEntity.getDairiFlg()))) {
			// 被害者代理人として登録しようとした人が、被害者代理人以外としてすでに登録されている場合 -> エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		boolean existsDairininEntity = tAnkenRelatedKanyoshaEntity != null;

		// 被害者代理人情報を作成
		TAnkenRelatedKanyoshaEntity insertAnkenRelatedKanyoshaEntity = new TAnkenRelatedKanyoshaEntity();
		insertAnkenRelatedKanyoshaEntity.setAnkenId(ankenId);
		insertAnkenRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertAnkenRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.HIGAISHA.getCd());
		insertAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertAnkenRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		// 被害者情報に代理人情報を登録
		TAnkenRelatedKanyoshaEntity updateAnkenRelatedKanyoshaEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(ankenId, higaishaKanyoshaSeq);
		if (updateAnkenRelatedKanyoshaEntity == null || Objects.nonNull(updateAnkenRelatedKanyoshaEntity.getRelatedKanyoshaSeq())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateAnkenRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			if (!existsDairininEntity) {
				tAnkenRelatedKanyoshaDao.insert(insertAnkenRelatedKanyoshaEntity);
			}
			tAnkenRelatedKanyoshaDao.update(updateAnkenRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	// **********************************************************************************
	// 裁判画面 その他当事者/その他当事者代理人の登録
	// **********************************************************************************

	/**
	 * 裁判-その他当事者の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registKanyoshaInfoForSaibanOther(Long saibanSeq, KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// その他当事者情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYODOSOSHONIN.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 裁判-その他当事者代理人の登録処理
	 *
	 * @param inputForm
	 * @param otherKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaInfoForSaibanOtherDairinin(Long saibanSeq, Long otherKanyoshaSeq, KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// その他当事者代理人情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYODOSOSHONIN.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		TSaibanRelatedKanyoshaEntity updateSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, otherKanyoshaSeq);
		if (updateSaibanRelatedKanyoshaEntity == null || updateSaibanRelatedKanyoshaEntity.getRelatedKanyoshaSeq() != null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);
			tSaibanRelatedKanyoshaDao.update(updateSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者検索モーダルの検索結果から案件-その他当事者を登録する
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param personId
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForSaibanOther(Long ankenId, Long saibanSeq, Long personId, Long kanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TSaibanRelatedKanyoshaEntity tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (tSaibanRelatedKanyoshaEntity != null) {
			// すでに他の役割で利用されていることは想定外
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// その他当事者情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYODOSOSHONIN.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 関与者検索モーダルの検索結果から関与者を登録する
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param personId
	 * @param kanyoshaSeq
	 * @param otherKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForSaibanOtherDairinin(Long ankenId, Long saibanSeq, Long personId, Long kanyoshaSeq, Long otherKanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || !CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// すでに代理人として利用されている場合はデータが存在する
		TSaibanRelatedKanyoshaEntity tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (tSaibanRelatedKanyoshaEntity != null &&
				!(KanyoshaType.KYODOSOSHONIN.equalsByCode(tSaibanRelatedKanyoshaEntity.getKanyoshaType()) &&
						SystemFlg.FLG_ON.equalsByCode(tSaibanRelatedKanyoshaEntity.getDairiFlg()))) {
			// 代理人として登録しようとした人が、その他当事者代理人として登録されていない場合はエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		boolean existsDairininEntity = tSaibanRelatedKanyoshaEntity != null;

		// その他当事者代理人情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYODOSOSHONIN.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		TSaibanRelatedKanyoshaEntity updateSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, otherKanyoshaSeq);
		if (updateSaibanRelatedKanyoshaEntity == null || updateSaibanRelatedKanyoshaEntity.getRelatedKanyoshaSeq() != null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			if (!existsDairininEntity) {
				tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);
			}
			tSaibanRelatedKanyoshaDao.update(updateSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	// **********************************************************************************
	// 裁判画面 相手方/相手方代理人の登録
	// **********************************************************************************

	/**
	 * 裁判-相手方の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registKanyoshaInfoForSaibanAitegata(Long saibanSeq, KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 相手方情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.AITEGATA.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 裁判-相手方代理人の登録処理
	 *
	 * @param inputForm
	 * @param aitegataKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaInfoForSaibanAitegataDairinin(Long saibanSeq, Long aitegataKanyoshaSeq, KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 相手方代理人情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.AITEGATA.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		TSaibanRelatedKanyoshaEntity updateSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, aitegataKanyoshaSeq);
		if (updateSaibanRelatedKanyoshaEntity == null || updateSaibanRelatedKanyoshaEntity.getRelatedKanyoshaSeq() != null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);
			tSaibanRelatedKanyoshaDao.update(updateSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者検索モーダルの検索結果から案件-相手方を登録する
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param personId
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForSaibanAitegata(Long ankenId, Long saibanSeq, Long personId, Long kanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			// 関与者が存在チェックと特定の種別の場合は想定外
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TSaibanRelatedKanyoshaEntity tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (tSaibanRelatedKanyoshaEntity != null) {
			// すでに他の役割で利用されていることは想定外
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 相手方情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.AITEGATA.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 関与者検索モーダルの検索結果から関与者を登録する
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param personId
	 * @param kanyoshaSeq
	 * @param aitegataKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForSaibanAitegataDairinin(Long ankenId, Long saibanSeq, Long personId, Long kanyoshaSeq, Long aitegataKanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId)) {
			// 関与者の存在チェック
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// すでに代理人として利用されている場合はデータが存在する
		TSaibanRelatedKanyoshaEntity tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (tSaibanRelatedKanyoshaEntity != null &&
				!(KanyoshaType.AITEGATA.equalsByCode(tSaibanRelatedKanyoshaEntity.getKanyoshaType()) &&
						SystemFlg.FLG_ON.equalsByCode(tSaibanRelatedKanyoshaEntity.getDairiFlg()))) {
			// 相手方代理人として登録しようとした人が、相手方代理人以外で登録されている
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		boolean existsDairininEntity = tSaibanRelatedKanyoshaEntity != null;

		// 相手方代理人情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.AITEGATA.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		TSaibanRelatedKanyoshaEntity updateSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, aitegataKanyoshaSeq);
		if (updateSaibanRelatedKanyoshaEntity == null || updateSaibanRelatedKanyoshaEntity.getRelatedKanyoshaSeq() != null) {
			// 紐づける先がすでに誰かと紐づいている場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			if (!existsDairininEntity) {
				tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);
			}
			tSaibanRelatedKanyoshaDao.update(updateSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	// **********************************************************************************
	// 裁判画面 共犯者/共犯者弁護人の登録
	// **********************************************************************************

	/**
	 * 裁判-共犯者の登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registKanyoshaInfoForSaibanKyohansha(Long saibanSeq, KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 共犯者情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYOHANSHA.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 裁判-共犯者弁護人の登録処理
	 *
	 * @param inputForm
	 * @param kyohanshaKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaInfoForSaibanKyohanshaBengonin(Long saibanSeq, Long kyohanshaKanyoshaSeq, KanyoshaEditModalInputForm inputForm) throws AppException {

		// 名簿の登録
		Long personId = this.registPerson(inputForm);

		// 関与者の登録
		Long kanyoshaSeq = this.registKanyosha(personId, inputForm);

		// 共犯者弁護人情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYOHANSHA.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		TSaibanRelatedKanyoshaEntity updateSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kyohanshaKanyoshaSeq);
		if (updateSaibanRelatedKanyoshaEntity == null || updateSaibanRelatedKanyoshaEntity.getRelatedKanyoshaSeq() != null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);
			tSaibanRelatedKanyoshaDao.update(updateSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 関与者検索モーダルの検索結果から案件-共犯者を登録する
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param personId
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForSaibanKyohansha(Long ankenId, Long saibanSeq, Long personId, Long kanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			// 関与者が存在チェックと特定の種別の場合は想定外
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		TSaibanRelatedKanyoshaEntity tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (tSaibanRelatedKanyoshaEntity != null) {
			// すでに他の役割で利用されていることは想定外
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 共犯者情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYOHANSHA.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_OFF.getCd());

		try {
			// ひも付き情報の登録
			tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 関与者検索モーダルの検索結果から関与者を登録する
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param personId
	 * @param kanyoshaSeq
	 * @param kyohanshaKanyoshaSeq
	 * @throws AppException
	 */
	public void registKanyoshaSearchResultForSaibanKyohanshaBengonin(Long ankenId, Long saibanSeq, Long personId, Long kanyoshaSeq, Long kyohanshaKanyoshaSeq) throws AppException {

		if (kanyoshaSeq == null) {
			// 関与者情報が作成されていない場合、関与者情報を作成
			kanyoshaSeq = registKanyoshaForSearchResult(ankenId, personId);
		}

		KanyoshaBean kanyoshaBean = tKanyoshaDao.selectKanyoshaBeanByKanyoshaSeq(kanyoshaSeq);
		if (kanyoshaBean == null || !Objects.equals(kanyoshaBean.getAnkenId(), ankenId) || !CustomerType.LAWYER.equalsByCode(kanyoshaBean.getCustomerType())) {
			// 関与者の存在チェックと許容する種別かどうか
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// すでに弁護人として利用されている場合はデータが存在する
		TSaibanRelatedKanyoshaEntity tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (tSaibanRelatedKanyoshaEntity != null &&
				!(KanyoshaType.KYOHANSHA.equalsByCode(tSaibanRelatedKanyoshaEntity.getKanyoshaType()) &&
						SystemFlg.FLG_ON.equalsByCode(tSaibanRelatedKanyoshaEntity.getDairiFlg()))) {
			// 弁護人として登録しようとした人が、共犯者弁護人として登録されていない場合はエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		boolean existsBengoninEntity = tSaibanRelatedKanyoshaEntity != null;

		// 共犯者弁護人情報を作成
		TSaibanRelatedKanyoshaEntity insertSaibanRelatedKanyoshaEntity = new TSaibanRelatedKanyoshaEntity();
		insertSaibanRelatedKanyoshaEntity.setSaibanSeq(saibanSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaSeq(kanyoshaSeq);
		insertSaibanRelatedKanyoshaEntity.setKanyoshaType(KanyoshaType.KYOHANSHA.getCd());
		insertSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(null);
		insertSaibanRelatedKanyoshaEntity.setMainFlg(SystemFlg.FLG_OFF.getCd());
		insertSaibanRelatedKanyoshaEntity.setDairiFlg(SystemFlg.FLG_ON.getCd());

		TSaibanRelatedKanyoshaEntity updateSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kyohanshaKanyoshaSeq);
		if (updateSaibanRelatedKanyoshaEntity == null || updateSaibanRelatedKanyoshaEntity.getRelatedKanyoshaSeq() != null) {
			// 紐づける先の整合性チェック
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		updateSaibanRelatedKanyoshaEntity.setRelatedKanyoshaSeq(kanyoshaSeq);

		try {
			// ひも付き情報の登録
			if (!existsBengoninEntity) {
				tSaibanRelatedKanyoshaDao.insert(insertSaibanRelatedKanyoshaEntity);
			}
			tSaibanRelatedKanyoshaDao.update(updateSaibanRelatedKanyoshaEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 名簿の登録
	 * 
	 * @param inputForm
	 * @return 登録時に発行された名簿ID
	 * @throws AppException
	 */
	private Long registPerson(KanyoshaEditModalInputForm inputForm) throws AppException {

		// 顧客情報の登録
		TPersonEntity personEntity = new TPersonEntity();
		personEntity.setCustomerType(inputForm.getCustomerType().getCd());
		personEntity.setCustomerFlg(SystemFlg.FLG_OFF.getCd());
		personEntity.setAdvisorFlg(SystemFlg.FLG_OFF.getCd());
		personEntity.setCustomerNameSei(inputForm.getCustomerNameSei());
		personEntity.setCustomerNameMei(inputForm.getCustomerNameMei());
		personEntity.setCustomerNameSeiKana(inputForm.getCustomerNameSeiKana());
		personEntity.setCustomerNameMeiKana(inputForm.getCustomerNameMeiKana());
		personEntity.setCustomerCreatedDate(LocalDate.now());

		try {
			// 登録処理
			tPersonDao.insert(personEntity);

			// 登録した名簿のIDを取得
			Long personId = personEntity.getPersonId();

			// 名簿IDに顧客IDを登録
			personEntity = tPersonDao.selectPersonByPersonId(personId); // 排他エラーになるので取得し直す
			personEntity.setCustomerId(personId);
			tPersonDao.update(personEntity);

			// 各付帯情報を登録する
			switch (inputForm.getCustomerType()) {
			case KOJIN:
				TPersonAddKojinEntity tCustomerAddKojinEntity = new TPersonAddKojinEntity();
				tCustomerAddKojinEntity.setPersonId(personEntity.getPersonId());
				tPersonAddKojinDao.insert(tCustomerAddKojinEntity);
				break;
			case HOJIN:
				TPersonAddHojinEntity tCustomerAddHojinEntity = new TPersonAddHojinEntity();
				tCustomerAddHojinEntity.setPersonId(personEntity.getPersonId());
				tPersonAddHojinDao.insert(tCustomerAddHojinEntity);
				break;
			case LAWYER:
				TPersonAddLawyerEntity tPersonAddLawyerEntity = new TPersonAddLawyerEntity();
				tPersonAddLawyerEntity.setPersonId(personEntity.getPersonId());
				tPersonAddLawyerDao.insert(tPersonAddLawyerEntity);
				break;
			default:
				break;
			}

			return personId;
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 関与者情報の登録処理
	 * 
	 * @param personId
	 * @param inputForm
	 * @return 登録時に発行された関与者SEQ
	 * @throws AppException
	 */
	private Long registKanyosha(Long personId, KanyoshaEditModalInputForm inputForm) throws AppException {

		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (personEntity == null) {
			// 名簿IDの整合性チェック
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<TKanyoshaEntity> tKanyoshaEntities = tKanyoshaDao.selectKanyoshaListByAnkenId(inputForm.getAnkenId());
		// すでに該当案件の関与者に登録しているケースはエラー
		if (tKanyoshaEntities.stream().anyMatch(e -> Objects.equals(personId, e.getPersonId()))) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 案件の顧客を関与者として登録するケースはエラー
		List<AnkenCustomerDto> ankenCustomerDtoList = tAnkenCustomerDao.selectAnkenCustomerDtoByAnkenId(inputForm.getAnkenId());
		if (ankenCustomerDtoList.stream().anyMatch(e -> Objects.equals(personId, e.getPersonId()))) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		Long maxDispOrder = tKanyoshaEntities.stream().map(TKanyoshaEntity::getDispOrder).max(Comparator.comparing(Long::longValue)).orElse(0L);

		TKanyoshaEntity tKanyoshaEntity = new TKanyoshaEntity();
		tKanyoshaEntity.setPersonId(personId);
		tKanyoshaEntity.setAnkenId(inputForm.getAnkenId());
		tKanyoshaEntity.setKankei(inputForm.getKankei());
		tKanyoshaEntity.setRemarks(inputForm.getRemarks());
		tKanyoshaEntity.setDispOrder(maxDispOrder + 1);

		try {
			// 登録処理
			tKanyoshaDao.insert(tKanyoshaEntity);

			return tKanyoshaEntity.getKanyoshaSeq();
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 検索モーダルの結果による関与者情報の登録処理
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 * @throws AppException
	 */
	private Long registKanyoshaForSearchResult(Long ankenId, Long personId) throws AppException {

		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (personEntity == null) {
			// 名簿IDの整合性チェック
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		List<TKanyoshaEntity> tKanyoshaEntities = tKanyoshaDao.selectKanyoshaListByAnkenId(ankenId);
		// すでに該当案件の関与者に登録しているケースはエラー
		if (tKanyoshaEntities.stream().anyMatch(e -> Objects.equals(personId, e.getPersonId()))) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 案件の顧客を関与者として登録するケースはエラー
		List<AnkenCustomerDto> ankenCustomerDtoList = tAnkenCustomerDao.selectAnkenCustomerDtoByAnkenId(ankenId);
		if (ankenCustomerDtoList.stream().anyMatch(e -> Objects.equals(personId, e.getPersonId()))) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		Long maxDispOrder = tKanyoshaEntities.stream().map(TKanyoshaEntity::getDispOrder).max(Comparator.comparing(Long::longValue)).orElse(0L);

		TKanyoshaEntity tKanyoshaEntity = new TKanyoshaEntity();
		tKanyoshaEntity.setPersonId(personId);
		tKanyoshaEntity.setAnkenId(ankenId);
		tKanyoshaEntity.setDispOrder(maxDispOrder + 1);

		try {
			// 登録処理
			tKanyoshaDao.insert(tKanyoshaEntity);

			return tKanyoshaEntity.getKanyoshaSeq();
		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

}
