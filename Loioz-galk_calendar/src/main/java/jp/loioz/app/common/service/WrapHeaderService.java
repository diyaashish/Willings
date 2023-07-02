package jp.loioz.app.common.service;

import java.time.LocalDate;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.WrapHeaderForm;
import jp.loioz.app.common.form.WrapHeaderForm.Aitegata;
import jp.loioz.app.common.form.WrapHeaderForm.Anken;
import jp.loioz.app.common.form.WrapHeaderForm.Customer;
import jp.loioz.app.user.ankenManagement.dto.AnkenTantoDispDto;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.bean.AnkenRelatedKanyoshaBean;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenAddKeijiDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TConnectedExternalServiceDao;
import jp.loioz.dao.TPersonAddHojinDao;
import jp.loioz.dao.TPersonAddKojinDao;
import jp.loioz.dao.TPersonAddLawyerDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenRelatedParentSaibanDto;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAnkenAddKeijiEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TConnectedExternalServiceEntity;
import jp.loioz.entity.TPersonAddHojinEntity;
import jp.loioz.entity.TPersonAddKojinEntity;
import jp.loioz.entity.TPersonAddLawyerEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class WrapHeaderService extends DefaultService {

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 名簿-個人Daoクラス */
	@Autowired
	private TPersonAddKojinDao tPersonAddKojinDao;

	/** 名簿-企業・団体Daoクラス */
	@Autowired
	private TPersonAddHojinDao tPersonAddHojinDao;

	/** 名簿-弁護士Daoクラス */
	@Autowired
	private TPersonAddLawyerDao tPersonAddLawyerDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenAddKeijiDao tAnkenAddKeijiDao;

	/** 案件-顧客用のDaoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 裁判Daoクラス */
	@Autowired
	private TSaibanDao tSaibanDao;

	/** 連携中サービスDaoクラス */
	@Autowired
	private TConnectedExternalServiceDao tConnectedExternalServiceDao;

	/** 共通裁判サービスクラス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 共通アカウントサービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 会計管理の共通サービス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 共通分野サービスクラス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 顧客軸ヘッダーの作成処理
	 *
	 * @param customerId
	 * @return
	 */
	public WrapHeaderForm createWrapCustomerHeaderForm(Long customerId) {

		// 表示用データの取得
		TPersonEntity personEntity = tPersonDao.selectById(customerId);
		List<AnkenCustomerRelationBean> ankenCustomerBean = tAnkenCustomerDao.selectRelation(null, customerId);

		// 未完了の案件リスト
		List<AnkenCustomerRelationBean> ankenCustomerIncompleteBean = ankenCustomerBean.stream().filter(bean -> (!AnkenStatus.KANRYO.equalsByCode(bean.getAnkenStatus()) && !AnkenStatus.FUJUNIN.equalsByCode(bean.getAnkenStatus()))).collect(Collectors.toList());

		// 完了の案件リスト
		List<AnkenCustomerRelationBean> ankenCustomerCompletedBean = ankenCustomerBean.stream().filter(bean -> AnkenStatus.KANRYO.equalsByCode(bean.getAnkenStatus())).collect(Collectors.toList());
		// 不受任の案件リスト
		List<AnkenCustomerRelationBean> ankenCustomerRejectionBean = ankenCustomerBean.stream().filter(bean -> AnkenStatus.FUJUNIN.equalsByCode(bean.getAnkenStatus())).collect(Collectors.toList());
		// 完了、不受任のリストをマージ
		List<AnkenCustomerRelationBean> ankenCustomerCompletedAndRejectionBean = new ArrayList<>();
		ankenCustomerCompletedAndRejectionBean.addAll(ankenCustomerCompletedBean);
		ankenCustomerCompletedAndRejectionBean.addAll(ankenCustomerRejectionBean);
		// 分野一覧を取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));
		// 未完了の案件を取得
		List<Anken> relatedAnkenList = ankenCustomerIncompleteBean.stream()
				.map(bean -> {
					return Anken.builder()
							.ankenId(AnkenId.of(bean.getAnkenId()))
							.ankenName(AnkenName.of(bean.getAnkenName(), bean.getBunyaId()))
							.bunya(bunyaMap.get(bean.getBunyaId()))
							.labelName(commonBunyaService.bunyaAndAnkenName(bean.getAnkenName(), bean.getBunyaId(), bunyaMap))
							.ankenStatus(bean.getAnkenStatus())
							.build();
				}).collect(Collectors.toList());
		// 完了、不受任の案件を取得
		List<Anken> relatedAnkenCompletedList = ankenCustomerCompletedAndRejectionBean.stream()
				.map(bean -> {
					return Anken.builder()
							.ankenId(AnkenId.of(bean.getAnkenId()))
							.ankenName(AnkenName.of(bean.getAnkenName(), bean.getBunyaId()))
							.bunya(bunyaMap.get(bean.getBunyaId()))
							.labelName(commonBunyaService.bunyaAndAnkenName(bean.getAnkenName(), bean.getBunyaId(), bunyaMap))
							.ankenStatus(bean.getAnkenStatus())
							.build();
				}).collect(Collectors.toList());

		// 取得した情報をセット
		WrapHeaderForm form = new WrapHeaderForm();
		form.setTransitionType(TransitionType.CUSTOMER);
		form.setPersonId(PersonId.of(customerId));
		form.setPersonAttribute(PersonAttribute.of(personEntity.getCustomerFlg(), personEntity.getAdvisorFlg(), personEntity.getCustomerType()));
		form.setCustomerType(CustomerType.of(personEntity.getCustomerType()));
		form.setCustomerId(CustomerId.of(customerId));
		form.setCustomerName(PersonName.fromEntity(personEntity).getName());
		form.setCustomerNameKana(PersonName.fromEntity(personEntity).getNameKana());
		form.setCustomerCreatedDate(personEntity.getCustomerCreatedDate());

		form.setRelatedAnken(relatedAnkenList);
		form.setRelatedAnkenCompleted(relatedAnkenCompletedList);

		// 個人
		if (CustomerType.KOJIN.equalsByCode(personEntity.getCustomerType())) {
			// 個人の場合、旧姓、故人を取得する
			TPersonAddKojinEntity tPersonAddKojinEntity = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personEntity.getPersonId());
			form.setOldName(tPersonAddKojinEntity.getOldName());
			form.setDeathFlg(SystemFlg.codeToBoolean(StringUtils.defaultString(tPersonAddKojinEntity.getDeathFlg())));

			// 生年月日
			LocalDate birthdate = tPersonAddKojinEntity.getBirthday();
			if (birthdate != null) {
				form.setBirthEra(EraType.of(tPersonAddKojinEntity.getBirthdayDisplayType()));
				if (!EraType.SEIREKI.getCd().equals(tPersonAddKojinEntity.getBirthdayDisplayType())) {
					// 和暦の場合
					JapaneseDate fromWareki = JapaneseDate.of(birthdate.getYear(), birthdate.getMonthValue(),
							birthdate.getDayOfMonth());
					DateTimeFormatter kanjiFormatter = DateTimeFormatter.ofPattern("yy");
					String kanjiFormatted = kanjiFormatter.format(fromWareki);
					fromWareki.getEra().getValue();
					form.setBirthYear(Long.valueOf(kanjiFormatted));

					// JapaneseEraからEraTypeを取得する
					form.setBirthEra(EraType.of(String.valueOf(fromWareki.getEra().getValue() + 2)));
				} else {
					// 西暦の場合
					form.setBirthYear(Long.valueOf(birthdate.getYear()));
				}
				form.setBirthMonth(Long.valueOf(birthdate.getMonthValue()));
				form.setBirthDay(Long.valueOf(birthdate.getDayOfMonth()));
				form.setEraEpoch(EraEpoch.of(tPersonAddKojinEntity.getBirthdayDisplayType()));
			}

			// 性別
			form.setGender(Gender.of(tPersonAddKojinEntity.getGenderType()));

			// 死亡日
			LocalDate deathDate = tPersonAddKojinEntity.getDeathDate();
			if (deathDate != null) {
				form.setDeathEra(EraType.of(tPersonAddKojinEntity.getDeathDateDisplayType()));
				if (!EraType.SEIREKI.getCd().equals(tPersonAddKojinEntity.getDeathDateDisplayType())) {
					// 和暦の場合
					JapaneseDate fromWareki = JapaneseDate.of(deathDate.getYear(), deathDate.getMonthValue(),
							deathDate.getDayOfMonth());
					DateTimeFormatter kanjiFormatter = DateTimeFormatter.ofPattern("yy");
					String kanjiFormatted = kanjiFormatter.format(fromWareki);
					fromWareki.getEra().getValue();
					form.setDeathYear(Long.valueOf(kanjiFormatted));

					// JapaneseEraからEraTypeを取得する
					form.setDeathEra(EraType.of(String.valueOf(fromWareki.getEra().getValue() + 2)));
				} else {
					// 西暦の場合
					form.setDeathYear(Long.valueOf(deathDate.getYear()));
				}
				form.setDeathMonth(Long.valueOf(deathDate.getMonthValue()));
				form.setDeathDay(Long.valueOf(deathDate.getDayOfMonth()));
				form.setDeathEraEpoch(EraEpoch.of(tPersonAddKojinEntity.getDeathDateDisplayType()));
			}

		}

		// 企業・団体
		if (CustomerType.HOJIN.equalsByCode(personEntity.getCustomerType())) {
			TPersonAddHojinEntity personAddHojin = tPersonAddHojinDao.selectPersonAddHojinByPersonId(customerId);
			if (personAddHojin != null) {
				// 代表
				form.setDaihyoName(personAddHojin.getDaihyoName());
				form.setDaihyoNameKana(personAddHojin.getDaihyoNameKana());
				form.setDaihyoPositionName(personAddHojin.getDaihyoPositionName());
				// 担当
				form.setTantoName(personAddHojin.getTantoName());
				form.setTantoNameKana(personAddHojin.getTantoNameKana());
			}
		}

		// 弁護士
		if (CustomerType.LAWYER.equalsByCode(personEntity.getCustomerType())) {
			List<TPersonAddLawyerEntity> personAddLawyerList = tPersonAddLawyerDao.selectPersonAddLawyerByPersonId(List.of(customerId));
			if (LoiozCollectionUtils.isNotEmpty(personAddLawyerList)) {
				TPersonAddLawyerEntity entity = personAddLawyerList.stream().findFirst().get();
				// 事務所名
				form.setLawyerJimushoName(entity.getJimushoName());
				// 部署・役職名
				form.setLawyerBushoName(entity.getBushoName());
			}
		}

		// 旧会計のデータが存在するか
		boolean isExistsOldKaikeiData = commonKaikeiService.isExistsOldKaikeiDataOnPerson(customerId);
		form.setExistsOldKaikeiData(isExistsOldKaikeiData);

		// 連携中のストレージ情報を設定する
		TConnectedExternalServiceEntity tConnectedExternalServiceEntity = tConnectedExternalServiceDao.selectStorageConnectedService();
		if (tConnectedExternalServiceEntity != null) {
			ExternalService storageConnectedService = ExternalService.of(tConnectedExternalServiceEntity.getExternalServiceId());
			form.setStorageConnectedService(storageConnectedService);
		}

		return form;
	}

	/**
	 * 案件軸ヘッダーの作成処理
	 *
	 * @param customerId
	 * @return
	 */
	public WrapHeaderForm createWrapAnkenHeaderForm(Long ankenId) {

		// 表示用データの取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
		List<AnkenCustomerRelationBean> ankenCustomerBean = tAnkenCustomerDao.selectRelation(ankenId, null);
		List<Customer> relatedCustomerList = ankenCustomerBean.stream().map(bean -> {
			return Customer.builder()
					.customerId(CustomerId.of(bean.getCustomerId()))
					.customerName(bean.getCustomerName())
					.ankenStatus(bean.getAnkenStatus())
					.build();
		}).collect(Collectors.toList());
		// 相手方（関与者）を取得
		List<AnkenRelatedKanyoshaBean> ankenRelatedKanyoshaBeanList = commonAnkenService.getAnkenRelatedKanyoshaBeanList(ankenId, KanyoshaType.AITEGATA, SystemFlg.FLG_OFF);
		List<Aitegata> relatedAitegataList = ankenRelatedKanyoshaBeanList.stream().map(bean -> {
			return Aitegata.builder()
					.kanyoshaSeq(bean.getKanyoshaSeq())
					.kanyoshaName(StringUtils.null2blank(bean.getCustomerNameSei()) + " " + StringUtils.null2blank(bean.getCustomerNameMei()))
					.build();
		}).collect(Collectors.toList());

		// 担当者情報
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();
		List<AnkenTantoDto> ankenTantoDto = commonAnkenService.getAnkenTantoListByAnkenId(ankenId);

		// 担当弁護士を取得
		List<AnkenTantoDispDto> tantoLaywer = ankenTantoDto.stream()
				.filter(e -> TantoType.LAWYER.equalsByCode(e.getTantoType()))
				.map(e -> comvert2AnkenTantoDispDto(e, accountNameMap))
				.collect(Collectors.toList());

		// 担当事務を取得
		List<AnkenTantoDispDto> tantoJimu = ankenTantoDto.stream()
				.filter(e -> TantoType.JIMU.equalsByCode(e.getTantoType()))
				.map(e -> comvert2AnkenTantoDispDto(e, accountNameMap))
				.collect(Collectors.toList());

		// 取得した情報をセット
		WrapHeaderForm form = new WrapHeaderForm();
		// 分野情報取得
		BunyaDto bunyaDto = commonBunyaService.getBunya(tAnkenEntity.getBunyaId());
		boolean isKeiji = bunyaDto.isKeiji();
		if (isKeiji) {
			TAnkenAddKeijiEntity entity = tAnkenAddKeijiDao.selectByAnkenId(ankenId);
			form.setBengoType(BengoType.of(entity.getLawyerSelectType()));
		} else {
			form.setBengoType(null);
		}
		form.setTransitionType(TransitionType.ANKEN);
		form.setAnkenId(AnkenId.of(ankenId));
		form.setAnkenName(AnkenName.fromEntity(tAnkenEntity));
		form.setBunya(bunyaDto);
		form.setKeiji(isKeiji);
		form.setLabelName(commonBunyaService.bunyaAndAnkenName(tAnkenEntity.getAnkenName(), tAnkenEntity.getBunyaId()));
		form.setTantoLaywer(tantoLaywer);
		form.setTantoJimu(tantoJimu);
		form.setAnkenCreatedDate(tAnkenEntity.getAnkenCreatedDate());
		form.setAnkenType(AnkenType.of(tAnkenEntity.getAnkenType()));
		form.setRelatedCustomer(relatedCustomerList);
		form.setRelatedAitegata(relatedAitegataList);

		// 案件に紐づく裁判を取得する
		List<TSaibanEntity> entityList = tSaibanDao.selectByAnkenId(ankenId);
		if (LoiozCollectionUtils.isEmpty(entityList)) {
			// 裁判なし
			form.setExistSaiban(false);
		} else {
			// 裁判あり
			form.setExistSaiban(true);

			// 一番目の裁判を取得
			TSaibanEntity saibanEntity = entityList.get(0);
			Long saibanSeq = saibanEntity.getSaibanSeq();

			// 裁判に紐づく子裁判を取得
			List<AnkenRelatedParentSaibanDto> ankenRelatedParentSaibanDtoList = null;
			if (isKeiji) {
				// 刑事分野の場合、追起訴を除く裁判を取得
				ankenRelatedParentSaibanDtoList = commonSaibanService.getAnkenRelatedSaibanKeiji(ankenId);

			} else {
				// 民事分野の場合、親裁判のみ取得
				ankenRelatedParentSaibanDtoList = commonSaibanService.getAnkenRelatedSaibanMinji(ankenId);

			}
			// 1件のみの場合は、サブメニューで表示しない
			boolean parentSaibanFlg = false;
			if (!LoiozCollectionUtils.isEmpty(ankenRelatedParentSaibanDtoList)) {
				// 1件以上か判定
				if (ankenRelatedParentSaibanDtoList.size() > 1) {
					// 1件以上は、裁判のサブメニューを表示する
					parentSaibanFlg = true;
				}
			}
			form.setParentSaiban(parentSaibanFlg);
			form.setFirstSaibanSeq(saibanSeq);
			form.setFirstSaibanBranchNo(saibanEntity.getSaibanBranchNo());
			form.setSideMenuParentSaibanList(ankenRelatedParentSaibanDtoList);
		}

		// 旧会計のデータが存在するか
		boolean isExistsOldKaikeiData = commonKaikeiService.isExistsOldKaikeiDataOnAnken(ankenId);
		form.setExistsOldKaikeiData(isExistsOldKaikeiData);

		// 連携中のストレージ情報を設定する
		TConnectedExternalServiceEntity tConnectedExternalServiceEntity = tConnectedExternalServiceDao.selectStorageConnectedService();
		if (tConnectedExternalServiceEntity != null) {
			ExternalService storageConnectedService = ExternalService.of(tConnectedExternalServiceEntity.getExternalServiceId());
			form.setStorageConnectedService(storageConnectedService);
		}

		return form;
	}

	/**
	 * 案件-担当者リストから、条件に該当するデータを取得します
	 *
	 * <pre>
	 * 優先度
	 * → 種別ごとに判定。メイン担当フラグがONの場合、その人が優先
	 *     該当者がいない場合、枝番(BranchNo)が一番小さいデータ
	 * </pre>
	 *
	 * @param type 担当種別(弁護士、事務)
	 * @param ankenEntities 案件IDで絞り込まれていること
	 * @return 上記条件にマッチしたentity
	 */
	public static Long getAnkenTantoSeq(TantoType type, List<TAnkenTantoEntity> ankenEntities) {

		// アカウント種別でフィルタリング
		List<TAnkenTantoEntity> ankenTantoOfType = ankenEntities.stream()
				.filter(entity -> Objects.equals(entity.getTantoType(), type.getCd()))
				.collect(Collectors.toList());

		// 登録されていない場合はnullを返却
		if (ankenTantoOfType.isEmpty()) {
			return null;
		}

		// メイン担当フラグを持っている人がいるか確認する。
		boolean isMainUser = ankenTantoOfType.stream()
				.anyMatch(entity -> SystemFlg.codeToBoolean(entity.getAnkenMainTantoFlg()));

		// true の場合、メイン担当フラグがONのアカウントSEQ内のブランチNoが一番若いデータのSEQretrun;
		// falseの場合、枝番が一番若いアカウントSEQをreturn
		Optional<TAnkenTantoEntity> matchData;
		if (isMainUser) {
			matchData = ankenTantoOfType.stream()
					.filter(entity -> SystemFlg.codeToBoolean(entity.getAnkenMainTantoFlg()))
					.sorted(Comparator.comparing(TAnkenTantoEntity::getTantoTypeBranchNo))
					.findFirst();
		} else {
			matchData = ankenTantoOfType.stream()
					.min(Comparator.comparingLong(TAnkenTantoEntity::getTantoTypeBranchNo));
		}

		// 条件に一致するデータを返却
		if (matchData.isPresent()) {
			return matchData.get().getAccountSeq();
		} else {
			return null;
		}
	}

	/**
	 * アカウントSEQをキーとして、アカウント一覧から名前を取得する
	 *
	 * @param accountSeq
	 * @param mAccountEntities
	 * @return
	 */
	public static String getFecthName(Long accountSeq, List<MAccountEntity> mAccountEntities) {

		// 担当弁護士名を取得
		if (Objects.nonNull(accountSeq)) {
			MAccountEntity mAccountEntity = mAccountEntities.stream()
					.filter(entity -> Objects.equals(entity.getAccountSeq(), accountSeq))
					.findFirst().orElseGet(() -> null);
			if (Objects.nonNull(mAccountEntity)) {
				return PersonName.fromEntity(mAccountEntity).getName();
			}
		}
		return null;
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
		return dispDto;
	}

}
