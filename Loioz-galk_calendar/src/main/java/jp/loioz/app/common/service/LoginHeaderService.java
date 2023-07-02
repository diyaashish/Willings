package jp.loioz.app.common.service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.LoginHeaderForm;
import jp.loioz.app.common.form.userHeader.AnkenListDto;
import jp.loioz.app.common.form.userHeader.HeaderSearchListSearchForm;
import jp.loioz.app.common.form.userHeader.HeaderSearchListViewForm;
import jp.loioz.app.common.form.userHeader.PersonListDto;
import jp.loioz.app.common.form.userHeader.SaibanListDto;
import jp.loioz.app.user.info.form.InfoViewForm;
import jp.loioz.bean.AnkenRelatedSelfJoinKanyoshaBean;
import jp.loioz.bean.SaibanCustomerBean;
import jp.loioz.bean.SaibanRelatedSelfJoinKanyoshaBean;
import jp.loioz.bean.userHeader.UserHeaderSearchAnkenBean;
import jp.loioz.bean.userHeader.UserHeaderSearchPersonBean;
import jp.loioz.bean.userHeader.UserHeaderSearchSaibanBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BunyaType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.HeaderSearchTab;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenRelatedKanyoshaDao;
import jp.loioz.dao.TConnectedExternalServiceDao;
import jp.loioz.dao.TDengonDao;
import jp.loioz.dao.TInfoReadHistoryDao;
import jp.loioz.dao.TPersonContactDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanCustomerDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.dao.TSaibanRelatedKanyoshaDao;
import jp.loioz.dao.TTaskDao;
import jp.loioz.dao.TToiawaseDao;
import jp.loioz.dao.UserHeaderDao;
import jp.loioz.domain.condition.userHeader.UserHeaderAnkenSearchCondition;
import jp.loioz.domain.condition.userHeader.UserHeaderGetCountCondition;
import jp.loioz.domain.condition.userHeader.UserHeaderPersonSearchCondition;
import jp.loioz.domain.condition.userHeader.UserHeaderSaibanSearchCondition;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.InfoListForAccountDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TConnectedExternalServiceEntity;
import jp.loioz.entity.TPersonContactEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanJikenEntity;
import lombok.NonNull;

/**
 * ログインヘッダー用のサービスクラス
 */
@Component
@Service
@Transactional(rollbackFor = Exception.class)
public class LoginHeaderService extends DefaultService {

	/** 伝言用のDaoクラス */
	@Autowired
	private TDengonDao tDengonDao;

	/** タスク管理用のDaoクラス */
	@Autowired
	private TTaskDao ttaskDao;

	/** お知らせ一覧用のDaoクラス */
	@Autowired
	private TInfoReadHistoryDao tInfoReadHistoryDao;

	/** 問い合わせDaoクラス */
	@Autowired
	private TToiawaseDao tToiawaseDao;

	/** 接続済み外部サービスDaoクラス */
	@Autowired
	private TConnectedExternalServiceDao tConnectedExternalServiceDao;

	/** アカウントクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** ヘッダーDaoクラス */
	@Autowired
	private UserHeaderDao userHeaderDao;

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 名簿-連絡先Daoクラス */
	@Autowired
	private TPersonContactDao tPersonContactDao;

	/** 顧客-案件Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 案件-関与者関係者Daoクラス */
	@Autowired
	private TAnkenRelatedKanyoshaDao tAnkenRelatedKanyoshaDao;

	/** 裁判-事件Daoクラス */
	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	/** 裁判-顧客Daoクラス */
	@Autowired
	private TSaibanCustomerDao tSaibanCustomerDao;

	/** 裁判-関係者Daoクラス */
	@Autowired
	private TSaibanRelatedKanyoshaDao tSaibanRelatedKanyoshaDao;

	/** 共通サービス：名簿 */
	@Autowired
	private CommonPersonService commonPersonService;

	/** 共通サービス：裁判 */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/**
	 * ヘッダーの新着件数の取得
	 *
	 * @param String ユーザー名
	 * @return ユーザー情報
	 */
	public LoginHeaderForm createLoginHeaderCountForm(Long accountSeq) {

		LoginHeaderForm loginHeaderForm = new LoginHeaderForm();

		if (accountSeq != null) {
			int newDengonCount = tDengonDao.selectUnreadCount(accountSeq);
			int newTaskCount = ttaskDao.selectUnreadCount(accountSeq);
			int hasNewDetailToiawaseCount = tToiawaseDao.unreadCount();
			MAccountEntity mAccountEntity = mAccountDao.selectBySeq(accountSeq);

			// formに変換
			loginHeaderForm.setNewDengonCount(newDengonCount);
			loginHeaderForm.setNewTaskCount(newTaskCount);
			loginHeaderForm.setHasNewDetailToiawaseCount(hasNewDetailToiawaseCount);
			loginHeaderForm.setLoginColor(mAccountEntity.getAccountColor());

		} else {
			// tenantSeqとaccountSeqが取得できない場合0を設定
			loginHeaderForm.setNewDengonCount(0);
			loginHeaderForm.setNewTaskCount(0);
			loginHeaderForm.setNewInformation(0);
			loginHeaderForm.setLoginColor(CommonConstant.LOIOZ_BULE);
		}

		// 連携中のストレージ情報を設定する
		TConnectedExternalServiceEntity tConnectedExternalServiceEntity = tConnectedExternalServiceDao.selectStorageConnectedService();
		if (tConnectedExternalServiceEntity != null) {
			loginHeaderForm.setStorageConnectedService(ExternalService.of(tConnectedExternalServiceEntity.getExternalServiceId()));
		}

		return loginHeaderForm;
	}

	/**
	 * お知らせ情報の取得
	 *
	 * @param accountSeq アカウントSEQ
	 * @return
	 */
	public InfoViewForm createInfoForm(Long accountSeq) {
		LocalDateTime now = LocalDateTime.now();
		// 一覧情報の取得
		List<InfoListForAccountDto> infoList = tInfoReadHistoryDao.selectUnread(accountSeq, now);

		infoList.forEach(info -> {
			LocalDateTime updatedAt = info.getUpdatedAt();
			if (updatedAt != null) {
				// 日付(yyyy年M月d日)取得
				String updatedAtStr = DateUtils.parseToString(updatedAt, DateUtils.DATE_JP_YYYY_MM_DD);
				// (日～土)を追加
				updatedAtStr += "(" + updatedAt.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ")";
				info.setUpdatedAtStr(updatedAtStr);
			}
			// お知らせ区分
			info.setInfoTypeStr(CommonConstant.InfoType.of(info.getInfoType()).getVal());

		});

		InfoViewForm infoViewForm = new InfoViewForm();
		infoViewForm.setInfoList(infoList);
		if (infoList.size() > 0) {
			infoViewForm.setInfoOpenFlg(true);
		} else {
			infoViewForm.setInfoOpenFlg(false);
		}
		return infoViewForm;
	}

	/**
	 * 検索結果を保持したヘッダー検索一覧画面フォームを取得する
	 * 
	 * @param searchForm 検索条件フォーム
	 * @return
	 */
	public HeaderSearchListViewForm getSearchResultHeaderSearchListViewForm(HeaderSearchListSearchForm searchForm) {

		// 画面オブジェクトを作成する
		HeaderSearchListViewForm viewForm = this.createHeaderSearchListViewForm(searchForm);

		HeaderSearchTab tabType = searchForm.getHeaderSearchTab();
		switch (tabType) {
		case PERSON:
			// 名簿一覧オブジェクトの作成・設定
			HeaderSearchListViewForm.HeaderSearchPersonListFragmentViewForm personViewForm = this.createHeaderSearchPersonListFragmentViewForm(searchForm);
			viewForm.setHeaderSearchPersonListFragmentViewForm(personViewForm);
			break;
		case ANKEN:
			// 案件一覧オブジェクトの作成・設定
			HeaderSearchListViewForm.HeaderSearchAnkenListFragmentViewForm ankenViewForm = this.createHeaderSearchAnkenListFragmentViewForm(searchForm);
			viewForm.setHeaderSearchAnkenListFragmentViewForm(ankenViewForm);
			break;
		case SAIBAN:
			// 裁判一覧オブジェクトの作成・設定
			HeaderSearchListViewForm.HeaderSearchSaibanListFragmentViewForm saibanViewForm = this.createHeaderSearchSaibanListFragmentViewForm(searchForm);
			viewForm.setHeaderSearchSaibanListFragmentViewForm(saibanViewForm);
			break;
		default:
			// 不正なパラメータ
			throw new RuntimeException("想定外の場合");
		}

		return viewForm;
	}

	/**
	 * ヘッダー検索：一覧画面オブジェクトを作成する
	 * 
	 * @param searchForm
	 * @return
	 */
	private HeaderSearchListViewForm createHeaderSearchListViewForm(HeaderSearchListSearchForm searchForm) {

		HeaderSearchListViewForm viewForm = new HeaderSearchListViewForm();

		// 件数取得条件のセットアップ処理 99以上かの判定行うので、取得件数は100件
		Consumer<UserHeaderGetCountCondition> setUpCountConsumer = condition -> condition.setUpCountGetCondition(CommonConstant.HEADER_SEARCH_COUNTER_LIMIT + 1);

		Long personCount = searchForm.getPersonCount();
		Long ankenCount = searchForm.getAnkenCount();
		Long saibanCount = searchForm.getSaibanCount();

		HeaderSearchTab selectedTab = searchForm.getHeaderSearchTab();
		if (searchForm.getPersonCount() == null || HeaderSearchTab.PERSON == selectedTab) {
			// 名簿件数の取得
			UserHeaderPersonSearchCondition personSearchConditions = searchForm.toUserHeaderPersonSearchCondition();
			setUpCountConsumer.accept(personSearchConditions);
			personCount = userHeaderDao.getPersonListCountBySearchConditions(personSearchConditions);
		}
		if (searchForm.getAnkenCount() == null || HeaderSearchTab.ANKEN == selectedTab) {
			// 案件件数の取得
			UserHeaderAnkenSearchCondition ankenSearchConditions = searchForm.toUserHeaderAnkenSearchCondition();
			setUpCountConsumer.accept(ankenSearchConditions);
			ankenCount = userHeaderDao.getAnkenListCountBySearchConditions(ankenSearchConditions);
		}
		if (searchForm.getSaibanCount() == null || HeaderSearchTab.SAIBAN == selectedTab) {
			// 裁判件数の取得
			UserHeaderSaibanSearchCondition saibanSearchConditions = searchForm.toUserHeaderSaibanSearchCondition();
			setUpCountConsumer.accept(saibanSearchConditions);
			saibanCount = userHeaderDao.getSaibanListCountBySearchConditions(saibanSearchConditions);
		}

		// 件数の設定
		viewForm.setPersonCount(personCount);
		viewForm.setPersonCounterLimitOver(this.isCounterLimitOver(personCount));
		viewForm.setAnkenCount(ankenCount);
		viewForm.setAnkenCounterLimitOver(this.isCounterLimitOver(ankenCount));
		viewForm.setSaibanCount(saibanCount);
		viewForm.setSaibanCounterLimitOver(this.isCounterLimitOver(saibanCount));

		return viewForm;
	}

	/**
	 * カウンタ上限を超えるかどうか
	 * 
	 * @param count
	 * @return
	 */
	private boolean isCounterLimitOver(long count) {
		if (count > CommonConstant.HEADER_SEARCH_COUNTER_LIMIT) {
			return true;
		}

		return false;
	}

	/**
	 * ヘッダー検索：名簿一覧画面オブジェクトの作成
	 * 
	 * @param searchForm
	 * @return
	 */
	private HeaderSearchListViewForm.HeaderSearchPersonListFragmentViewForm createHeaderSearchPersonListFragmentViewForm(HeaderSearchListSearchForm searchForm) {

		HeaderSearchListViewForm.HeaderSearchPersonListFragmentViewForm viewForm = new HeaderSearchListViewForm.HeaderSearchPersonListFragmentViewForm();

		// 検索用オブジェクトを作成
		UserHeaderPersonSearchCondition searchConditions = searchForm.toUserHeaderPersonSearchCondition();
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<Long> personIdList = userHeaderDao.selectPersonIdBySearchConditions(searchConditions, options);

		// 一覧に表示する名簿の連絡先を取得
		List<TPersonContactEntity> tPersonContactEntities = tPersonContactDao.selectPersonContactByPersonIdList(personIdList);
		Map<Long, List<TPersonContactEntity>> personIdToPersonContactMap = tPersonContactEntities.stream().collect(Collectors.groupingBy(TPersonContactEntity::getPersonId));

		// 名簿情報を取得する
		List<UserHeaderSearchPersonBean> userHeaderSearchPersonBeanList = userHeaderDao.selectPersonBeanByPersonIdList(personIdList);
		List<PersonListDto> personList = userHeaderSearchPersonBeanList.stream()
				.map(e -> convert2Dto(e, personIdToPersonContactMap.getOrDefault(e.getPersonId(), Collections.emptyList())))
				.collect(Collectors.toList());

		// ページ情報の作成
		Page<Long> page = new PageImpl<>(personIdList, pageable, options.getCount());

		// データの設定
		viewForm.setPersonList(personList);
		viewForm.setPager(page);

		return viewForm;
	}

	/**
	 * UserHeaderSearchPersonBean -> PersonListDto
	 * 
	 * @param bean
	 * @param tPersonContactEntities
	 * @return
	 */
	private PersonListDto convert2Dto(UserHeaderSearchPersonBean bean, List<TPersonContactEntity> tPersonContactEntities) {
		PersonListDto dto = new PersonListDto();

		dto.setPersonId(PersonId.of(bean.getPersonId()));
		dto.setCustomerType(CustomerType.of(bean.getCustomerType()));
		dto.setCustomer(SystemFlg.codeToBoolean(bean.getCustomerFlg()));
		dto.setAdvisor(SystemFlg.codeToBoolean(bean.getAdvisorFlg()));

		PersonName personName = new PersonName(bean.getCustomerNameSei(), bean.getCustomerNameMei(), bean.getCustomerNameSeiKana(), bean.getCustomerNameMeiKana());
		dto.setPersonName(personName.getName());
		dto.setPersonNameKana(personName.getNameKana());

		dto.setZipCode(bean.getZipCode());
		dto.setAddress1(bean.getAddress1());
		dto.setAddress2(bean.getAddress2());
		dto.setTelNo(commonPersonService.getYusenTelNo(tPersonContactEntities));

		return dto;
	}

	/**
	 * ヘッダー検索：案件一覧画面オブジェクトの作成
	 * 
	 * @param searchForm
	 * @return
	 */
	private HeaderSearchListViewForm.HeaderSearchAnkenListFragmentViewForm createHeaderSearchAnkenListFragmentViewForm(HeaderSearchListSearchForm searchForm) {

		HeaderSearchListViewForm.HeaderSearchAnkenListFragmentViewForm viewForm = new HeaderSearchListViewForm.HeaderSearchAnkenListFragmentViewForm();

		// 検索用オブジェクトの作成
		UserHeaderAnkenSearchCondition searchConditions = searchForm.toUserHeaderAnkenSearchCondition();
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<Long> ankenIdList = userHeaderDao.selectAnkenIdBySearchConditions(searchConditions, options);

		// 案件情報を取得する
		List<UserHeaderSearchAnkenBean> userHeaderSearchAnkenBeanList = userHeaderDao.selectAnkenBeanByAnkenIdList(ankenIdList);

		// 案件の顧客情報の取得・作成
		Map<Long, List<String>> ankenIdToCustomerNameListMap = this.generateAnkenIdToCustomerNameListMap(ankenIdList);

		// 各種別の案件関係者情報の取得・作成
		List<AnkenRelatedSelfJoinKanyoshaBean> ankenRelatedSelfJoinKanyoshaBeanList = tAnkenRelatedKanyoshaDao.selectAnkenRelatedSelfJoinKanyoshaBeanByAnkenIdList(ankenIdList);
		Map<Long, List<String>> ankenIdToAitegataNameListMap = this.generateAnkenIdToKanyoshaTypeListMap(ankenRelatedSelfJoinKanyoshaBeanList, KanyoshaType.AITEGATA);
		Map<Long, List<String>> ankenIdToHigaishaNameListMap = this.generateAnkenIdToKanyoshaTypeListMap(ankenRelatedSelfJoinKanyoshaBeanList, KanyoshaType.HIGAISHA);

		List<AnkenListDto> ankenDtoList = userHeaderSearchAnkenBeanList.stream()
				.map(e -> {
					List<String> customerNameList = ankenIdToCustomerNameListMap.getOrDefault(e.getAnkenId(), Collections.emptyList());
					List<String> aitegataNameList = ankenIdToAitegataNameListMap.getOrDefault(e.getAnkenId(), Collections.emptyList());
					List<String> higaishaNameList = ankenIdToHigaishaNameListMap.getOrDefault(e.getAnkenId(), Collections.emptyList());
					return this.convert2Dto(e, customerNameList, aitegataNameList, higaishaNameList);
				})
				.collect(Collectors.toList());

		// ページ情報の作成
		Page<Long> page = new PageImpl<>(ankenIdList, pageable, options.getCount());

		// データの設定
		viewForm.setAnkenList(ankenDtoList);
		viewForm.setPager(page);

		return viewForm;
	}

	/**
	 * 案件IDをキーとした、顧客名配列マップを作成する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	private Map<Long, List<String>> generateAnkenIdToCustomerNameListMap(List<Long> ankenIdList) {

		Map<Long, List<String>> ankenIdToCustomerNameListMap = new HashMap<>();

		// 顧客-案件情報を取得
		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByAnkenId(ankenIdList);
		Map<Long, List<TAnkenCustomerEntity>> ankenIdToRelationMap = tAnkenCustomerEntities.stream().collect(Collectors.groupingBy(TAnkenCustomerEntity::getAnkenId));
		Set<Long> customerIdSet = tAnkenCustomerEntities.stream().map(TAnkenCustomerEntity::getCustomerId).collect(Collectors.toSet());

		if (CollectionUtils.isEmpty(customerIdSet)) {
			return ankenIdToCustomerNameListMap;
		}

		// 名前情報を取得する
		Map<Long, TPersonEntity> personIdToEntityMap = this.generatePersonEntityMap(new ArrayList<>(customerIdSet));

		// キーが案件ID、バリューが顧客名配列のエントリーをマップに追加
		Function<TAnkenCustomerEntity, String> personNameMapper = e -> PersonName.fromEntity(personIdToEntityMap.getOrDefault(e.getCustomerId(), new TPersonEntity())).getName();
		for (Entry<Long, List<TAnkenCustomerEntity>> entry : ankenIdToRelationMap.entrySet()) {
			Long ankenId = entry.getKey();
			List<String> customerNameList = entry.getValue().stream().map(personNameMapper).collect(Collectors.toList());
			ankenIdToCustomerNameListMap.put(ankenId, customerNameList);
		}

		return ankenIdToCustomerNameListMap;
	}

	/**
	 * 案件IDをキーとして、案件の関係者名名配列を作成する <br>
	 * ※ 当メソッドはDBデータ取得を行わないことを主目的として作成しているため、修正時もDBからデータ取得を行わないこと
	 * 
	 * @param beanList
	 * @param type
	 * @return
	 */
	private Map<Long, List<String>> generateAnkenIdToKanyoshaTypeListMap(List<AnkenRelatedSelfJoinKanyoshaBean> beanList, KanyoshaType type) {

		// フィルタリング条件の定義
		Predicate<AnkenRelatedSelfJoinKanyoshaBean> kanyoshaTypeFilter = bean -> type.equalsByCode(bean.getKanyoshaType()); // 関与者種別でフィルタリング
		Predicate<AnkenRelatedSelfJoinKanyoshaBean> nonDairiFilter = bean -> !SystemFlg.codeToBoolean(bean.getDairiFlg()); // 代理人ではない関係者でフィルタリング

		// Bean -> String 変換Mapper
		Function<AnkenRelatedSelfJoinKanyoshaBean, String> dispNameMapper = bean -> {
			String main = new PersonName(bean.getCustomerNameSei(), bean.getCustomerNameMei(), bean.getCustomerNameSeiKana(), bean.getCustomerNameMeiKana()).getName();
			String sub = new PersonName(bean.getAlternateCustomerNameSei(), bean.getAlternateCustomerNameMei(), bean.getAlternateCustomerNameSeiKana(), bean.getAlternateCustomerNameMeiKana()).getName();
			return String.format("%s %s", main, StringUtils.surroundBrackets(sub, true)).trim();
		};

		return beanList.stream()
				.filter(kanyoshaTypeFilter.and(nonDairiFilter))
				.collect(Collectors.groupingBy(
						AnkenRelatedSelfJoinKanyoshaBean::getAnkenId,
						Collectors.mapping(dispNameMapper, Collectors.toList())));
	}

	/**
	 * UserHeaderSearchAnkenBean -> AnkenListDto
	 * 
	 * @param bean
	 * @return
	 */
	private AnkenListDto convert2Dto(UserHeaderSearchAnkenBean bean, List<String> customerNameList, List<String> aitegataNameList, List<String> higaishaNameList) {
		AnkenListDto dto = new AnkenListDto();

		dto.setAnkenId(AnkenId.of(bean.getAnkenId()));
		dto.setBunyaId(bean.getBunyaId());
		dto.setBunyaName(bean.getBunyaName());
		dto.setAnkenName(bean.getAnkenName());
		dto.setAnkenType(AnkenType.of(bean.getAnkenType()));

		dto.setCustomerNameList(customerNameList);
		dto.setAitegataNameList(aitegataNameList);
		dto.setHigaishaNameList(higaishaNameList);

		return dto;
	}

	/**
	 * ヘッダー検索：裁判一覧画面オブジェクトの作成
	 * 
	 * @param searchForm
	 * @return
	 */
	private HeaderSearchListViewForm.HeaderSearchSaibanListFragmentViewForm createHeaderSearchSaibanListFragmentViewForm(HeaderSearchListSearchForm searchForm) {

		HeaderSearchListViewForm.HeaderSearchSaibanListFragmentViewForm viewForm = new HeaderSearchListViewForm.HeaderSearchSaibanListFragmentViewForm();

		// 検索用オブジェクトの作成
		UserHeaderSaibanSearchCondition searchConditions = searchForm.toUserHeaderSaibanSearchCondition();
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<Long> saibanSeqList = userHeaderDao.selectSaibanSeqBySearchConditions(searchConditions, options);

		// 事件情報の取得
		List<TSaibanJikenEntity> tSaibanJikenEntities = tSaibanJikenDao.selectBySaibanSeq(saibanSeqList);
		Map<Long, List<TSaibanJikenEntity>> saibanSeqToJikenEntityMap = tSaibanJikenEntities.stream().collect(Collectors.groupingBy(TSaibanJikenEntity::getSaibanSeq));

		// 顧客当事者情報の取得・作成
		List<SaibanCustomerBean> saibanCustomerBeanList = tSaibanCustomerDao.selectSaibanCustomerBeanBySaibanSeqList(saibanSeqList);
		Map<Long, List<String>> saibanSeqToCustomerTojishaNameListMap = this.generateCustomerTojishaNameList(saibanCustomerBeanList);

		// 各種別の裁判関係者情報の取得・作成
		List<SaibanRelatedSelfJoinKanyoshaBean> saibanRelatedSelfJoinKanyoshaBeanList = tSaibanRelatedKanyoshaDao.selectSaibanRelatedSelfJoinKanyoshaBeanBySaibanSeqList(saibanSeqList);
		Map<Long, List<String>> saibanSeqToAitegataNameListMap = this.generateSaibanSeqToKanyoshaTypeNameList(saibanRelatedSelfJoinKanyoshaBeanList, KanyoshaType.AITEGATA);

		// 画面表示情報の取得
		List<UserHeaderSearchSaibanBean> userHeaderSearchSaibanBeanList = userHeaderDao.selectSaibanBeanBySaibanSeqList(saibanSeqList);
		List<SaibanListDto> saibanList = userHeaderSearchSaibanBeanList.stream().map(e -> {
			List<TSaibanJikenEntity> saibanJikenEntities = saibanSeqToJikenEntityMap.getOrDefault(e.getSaibanSeq(), Collections.emptyList());
			List<String> customerTojishaNameList = saibanSeqToCustomerTojishaNameListMap.getOrDefault(e.getSaibanSeq(), Collections.emptyList());
			List<String> aitegataNameList = saibanSeqToAitegataNameListMap.getOrDefault(e.getSaibanSeq(), Collections.emptyList());

			return this.convert2Dto(e, saibanJikenEntities, customerTojishaNameList, aitegataNameList);
		}).collect(Collectors.toList());

		// ページャーオブジェクトの作成
		Page<Long> pager = new PageImpl<>(saibanSeqList, pageable, options.getCount());

		// データの設定
		viewForm.setSaibanList(saibanList);
		viewForm.setPager(pager);

		return viewForm;
	}

	/**
	 * 裁判SEQをキーとした、裁判顧客当事者名配列を生成する <br>
	 * ※ 当メソッドはDBデータ取得を行わないことを主目的として作成しているため、修正時もDBからデータ取得を行わないこと
	 * 
	 * @param beanList
	 * @param type
	 * @return
	 */
	private Map<Long, List<String>> generateCustomerTojishaNameList(List<SaibanCustomerBean> beanList) {

		// Bean -> String 変換Mapper
		Function<SaibanCustomerBean, String> dispNameMapper = bean -> {
			return new PersonName(
					bean.getCustomerNameSei(),
					bean.getCustomerNameMei(),
					bean.getCustomerNameSeiKana(),
					bean.getCustomerNameMeiKana())
							.getName();
		};

		return beanList.stream()
				.collect(Collectors.groupingBy(
						SaibanCustomerBean::getSaibanSeq,
						Collectors.mapping(dispNameMapper, Collectors.toList())));
	}

	/**
	 * 裁判SEQをキーとした、関係者名配列を生成する ※
	 * 当メソッドはDBデータ取得を行わないことを主目的として作成しているため、修正時もDBからデータ取得を行わないこと
	 * 
	 * @param beanList
	 * @param type
	 * @return
	 */
	private Map<Long, List<String>> generateSaibanSeqToKanyoshaTypeNameList(List<SaibanRelatedSelfJoinKanyoshaBean> beanList, @NonNull KanyoshaType type) {

		// フィルタリング条件の定義
		Predicate<SaibanRelatedSelfJoinKanyoshaBean> kanyoshaTypeFilter = bean -> type.equalsByCode(bean.getKanyoshaType()); // 関与者種別でフィルタリング
		Predicate<SaibanRelatedSelfJoinKanyoshaBean> nonDairiFilter = bean -> !SystemFlg.codeToBoolean(bean.getDairiFlg()); // 代理人ではない関係者でフィルタリング

		// Bean -> String 変換Mapper
		Function<SaibanRelatedSelfJoinKanyoshaBean, String> dispNameMapper = bean -> {
			String main = new PersonName(bean.getCustomerNameSei(), bean.getCustomerNameMei(), bean.getCustomerNameSeiKana(), bean.getCustomerNameMeiKana()).getName();
			String sub = new PersonName(bean.getAlternateCustomerNameSei(), bean.getAlternateCustomerNameMei(), bean.getAlternateCustomerNameSeiKana(), bean.getAlternateCustomerNameMeiKana()).getName();
			return String.format("%s %s", main, StringUtils.surroundBrackets(sub, true)).trim();
		};

		return beanList.stream()
				.filter(kanyoshaTypeFilter.and(nonDairiFilter))
				.collect(Collectors.groupingBy(
						SaibanRelatedSelfJoinKanyoshaBean::getSaibanSeq,
						Collectors.mapping(dispNameMapper, Collectors.toList())));
	}

	/**
	 * UserHeaderSearchSaibanBean -> SaibanListDto
	 * 
	 * @param bean
	 * @return
	 */
	private SaibanListDto convert2Dto(UserHeaderSearchSaibanBean bean, List<TSaibanJikenEntity> tSaibanJikenEntities, List<String> customerNameList, List<String> aitegataNameList) {
		SaibanListDto dto = new SaibanListDto();

		dto.setSaibanSeq(bean.getSaibanSeq());
		dto.setAnkenId(AnkenId.of(bean.getAnkenId()));
		dto.setSaibanBranchNo(bean.getSaibanBranchNo());
		dto.setBunyaId(bean.getBunyaId());
		dto.setBunyaName(bean.getBunyaName());
		dto.setBunyaType(BunyaType.of(bean.getBunyaType()));
		dto.setAnkenType(AnkenType.of(bean.getAnkenType()));

		dto.setJikenName(commonSaibanService.getMainJikenName(tSaibanJikenEntities));
		dto.setCaseNo(Optional.ofNullable(commonSaibanService.getMainCaseNumber(tSaibanJikenEntities)).map(CaseNumber::toString).orElse(""));
		dto.setSubCaseNo(this.getSubJikenName(tSaibanJikenEntities));

		dto.setCustomerNameList(customerNameList);
		dto.setAitegataNameList(aitegataNameList);

		return dto;
	}

	/**
	 * 追起訴の事件番号情報を取得する
	 * 
	 * @param tSaibanJikenEntities 本事件を含む事件情報
	 * @return
	 */
	private List<String> getSubJikenName(List<TSaibanJikenEntity> tSaibanJikenEntities) {
		return commonSaibanService.getSubJikenEntities(tSaibanJikenEntities)
				.stream().map(CaseNumber::fromEntity).map(CaseNumber::toString).collect(Collectors.toList());
	}

	// ***************************************************************************************

	/**
	 * IDをキーとして、名前Mapを作成
	 * 
	 * @param personId
	 * @return
	 */
	private Map<Long, TPersonEntity> generatePersonEntityMap(List<Long> personId) {

		List<TPersonEntity> tPersonEntities = tPersonDao.selectById(personId);
		return tPersonEntities.stream().collect(Collectors.toMap(TPersonEntity::getCustomerId, Function.identity()));
	}

}
