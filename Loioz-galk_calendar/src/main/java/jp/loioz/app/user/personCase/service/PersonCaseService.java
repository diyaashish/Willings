package jp.loioz.app.user.personCase.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.accg.AccgPersonSummaryForm;
import jp.loioz.app.common.service.CommonAccgSummaryService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.user.personCase.form.PersonCaseViewForm;
import jp.loioz.app.user.personCase.form.PersonCaseViewForm.PersonAnkenListViewForm;
import jp.loioz.bean.AnkenBunyaBean;
import jp.loioz.bean.AnkenKanyoshaBean;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TConnectedExternalServiceDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dto.AccgSalesFeeDepositTotalDto;
import jp.loioz.dto.AnkenTantoDispDto;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TConnectedExternalServiceEntity;
import jp.loioz.entity.TSaibanEntity;

/**
 * 名簿-案件のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PersonCaseService extends DefaultService {

	/** 案件-顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 裁判Daoクラス */
	@Autowired
	private TSaibanDao tSaibanDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 接続中外部サービスDaoクラス */
	@Autowired
	private TConnectedExternalServiceDao tConnectedExternalServiceDao;

	/** 共通：アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 会計管理：サマリ情報-共通サービス */
	@Autowired
	private CommonAccgSummaryService commonAccgSummaryService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 名簿-案件一覧画面表示用オブジェクトを作成
	 * 
	 * @param personId
	 * @return
	 */
	public PersonCaseViewForm createViewForm(Long personId) {

		var viewForm = new PersonCaseViewForm();

		// 名簿案件一覧の取得
		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByCustomerId(personId);
		List<Long> ankenIdList = tAnkenCustomerEntities.stream().map(TAnkenCustomerEntity::getAnkenId).collect(Collectors.toList());
		Map<Long, AnkenBunyaBean> ankenBunyaBeanMap = this.getAnkenBunyaBeanIdMap(ankenIdList);
		Map<Long, Long> saibanFirstBranchNoAnkenIdKeyMap = this.getSaibanFirstBranchNoAnkenIdKeyMap(ankenIdList);
		Map<Long, String> ankenAitegataNameMap = this.getAitegataNameDispMap(ankenIdList);
		Map<Long, List<AnkenTantoDispDto>> ankenTantoLawyerDispMap = this.getTantoDispDtoListMap(ankenIdList, TantoType.LAWYER);
		Map<Long, List<AnkenTantoDispDto>> ankenTantoJimuDispMap = this.getTantoDispDtoListMap(ankenIdList, TantoType.JIMU);

		// 名簿ID 報酬・預り金
		Map<Long, AccgSalesFeeDepositTotalDto> feeDepositSummaryAnkenIdKeyMap = tAnkenCustomerEntities.stream()
				.map(e -> commonAccgSummaryService.getAccgSalesFeeDepositSummaryDto(e.getAnkenId(), e.getCustomerId()))
				.collect(Collectors.toMap(AccgSalesFeeDepositTotalDto::getAnkenId, Function.identity()));

		// サマリー情報の作成
		List<AccgSalesFeeDepositTotalDto> accgFeeDepositSummaryDtoList = new ArrayList<>(feeDepositSummaryAnkenIdKeyMap.values());
		this.accgTotalCaseData(viewForm, accgFeeDepositSummaryDtoList);

		// 一覧の作成
		List<PersonAnkenListViewForm> personAnkenListViewFormList = tAnkenCustomerEntities.stream().map(e -> {
			Long ankenId = e.getAnkenId();
			AnkenBunyaBean ankenBunyaBean = ankenBunyaBeanMap.get(ankenId);
			Long firstSaibanBranchNo = saibanFirstBranchNoAnkenIdKeyMap.get(ankenId);
			String aitegataName = ankenAitegataNameMap.get(ankenId);
			List<AnkenTantoDispDto> ankenTantoLawyerDispDtoList = ankenTantoLawyerDispMap.getOrDefault(ankenId, Collections.emptyList());
			List<AnkenTantoDispDto> ankenTantoJimuDispDtoList = ankenTantoJimuDispMap.getOrDefault(ankenId, Collections.emptyList());
			AccgSalesFeeDepositTotalDto accgSalesFeeDepositTotalDto = feeDepositSummaryAnkenIdKeyMap.get(e.getAnkenId());

			return this.convert2PersonCaseForm(e, ankenBunyaBean, ankenTantoLawyerDispDtoList, ankenTantoJimuDispDtoList, firstSaibanBranchNo, aitegataName, accgSalesFeeDepositTotalDto);
		}).sorted(Comparator.comparing(PersonAnkenListViewForm::getAnkenStatus).thenComparing(PersonAnkenListViewForm::getAnkenCreateDate)).collect(Collectors.toList());

		viewForm.setPersonId(personId);
		viewForm.setPersonAnkenListViewForm(personAnkenListViewFormList);

		// 連携中のストレージ情報を設定する
		TConnectedExternalServiceEntity tConnectedExternalServiceEntity = tConnectedExternalServiceDao.selectStorageConnectedService();
		if (tConnectedExternalServiceEntity != null) {
			ExternalService storageConnectedService = ExternalService.of(tConnectedExternalServiceEntity.getExternalServiceId());
			viewForm.setStorageConnectedService(storageConnectedService);
		}

		return viewForm;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件IDをキーと案件分野BeanMapを取得する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	private Map<Long, AnkenBunyaBean> getAnkenBunyaBeanIdMap(List<Long> ankenIdList) {
		// 案件ID配列からAnkenBean配列を取得・Map型する
		List<AnkenBunyaBean> ankenBunyaBeanList = tAnkenDao.selectAnkenBunyaBeanByAnkenId(ankenIdList);
		return ankenBunyaBeanList.stream().collect(Collectors.toMap(AnkenBunyaBean::getAnkenId, Function.identity()));
	}

	/**
	 * 案件IDをキーとした初期裁判枝番のMapを取得する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	private Map<Long, Long> getSaibanFirstBranchNoAnkenIdKeyMap(List<Long> ankenIdList) {

		// 案件IDをキーとした最初の裁判連番を保持する
		List<TSaibanEntity> tSaibanEntities = tSaibanDao.selectByAnkenIdList(ankenIdList);
		return tSaibanEntities.stream().collect(
				Collectors.toMap(
						TSaibanEntity::getAnkenId,
						TSaibanEntity::getSaibanBranchNo,
						BinaryOperator.minBy(Comparator.naturalOrder())));
	}

	/**
	 * 案件IDと担当種別をキーとして、担当者情報Mapを取得する
	 * 
	 * @param ankenIdList
	 * @param tantoType
	 * @return
	 */
	private Map<Long, List<AnkenTantoDispDto>> getTantoDispDtoListMap(List<Long> ankenIdList, TantoType tantoType) {

		// 案件担当者情報を取得する
		List<AnkenTantoDto> ankenTantoDtoList = tAnkenTantoDao.selectByAnkenIdListAndTantoType(ankenIdList);

		// アカウントSEQをキーとしたからアカウント名を保持するMapを保持しておく
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// 種別で絞り込み
		Map<Long, List<AnkenTantoDto>> ankenIdKeyAnkenTantoDtoMap = ankenTantoDtoList.stream()
				.filter(e -> tantoType.equalsByCode(e.getTantoType()))
				.collect(Collectors.groupingBy(AnkenTantoDto::getAnkenId));

		// 案件担当者の配列情報を画面表示で利用するDtoに変換する(Map内のvalueの型変換)
		Map<Long, List<AnkenTantoDispDto>> ankenIdKeyAnkenTantoDispDtoMap = new HashMap<>();
		for (Entry<Long, List<AnkenTantoDto>> entry : ankenIdKeyAnkenTantoDtoMap.entrySet()) {

			List<AnkenTantoDispDto> ankenTantoDispDtoList = entry.getValue().stream().map(e -> {
				AnkenTantoDispDto dto = new AnkenTantoDispDto();
				dto.setAccountSeq(e.getAccountSeq());
				dto.setAccountName(accountNameMap.get(e.getAccountSeq()));
				dto.setMain(SystemFlg.codeToBoolean(e.getAnkenMainTantoFlg()));
				dto.setTantoTypeBranchNo(e.getTantoTypeBranchNo());
				dto.setAccountColor(e.getAccountColor());
				dto.setAccountTypeStr(AccountType.of(e.getAccountType()).getVal());
				return dto;
			}).collect(Collectors.toList());

			ankenIdKeyAnkenTantoDispDtoMap.put(entry.getKey(), ankenTantoDispDtoList);
		}

		return ankenIdKeyAnkenTantoDispDtoMap;
	}

	/**
	 * 案件IDをキーとして、案件相手方名Mapを取得する
	 * 
	 * @param ankenIdList
	 * @return
	 */
	private Map<Long, String> getAitegataNameDispMap(List<Long> ankenIdList) {

		// 案件IDに紐づく関与者情報の取得
		List<AnkenKanyoshaBean> ankenKanyoshaList = tKanyoshaDao.selectAnkenRelatedKanyoshaByAnkenIdList(ankenIdList);

		// 相手方のみに絞り込み(代理人も除外)
		Predicate<AnkenKanyoshaBean> mainFilter = (e) -> !SystemFlg.codeToBoolean(e.getDairiFlg());
		Predicate<AnkenKanyoshaBean> aitegataTypeFilter = (e) -> KanyoshaType.AITEGATA.equalsByCode(e.getKanyoshaType());
		Map<Long, List<AnkenKanyoshaBean>> ankenIdToAitegataListMap = ankenKanyoshaList.stream()
				.filter(aitegataTypeFilter.and(mainFilter))
				.collect(Collectors.groupingBy(AnkenKanyoshaBean::getAnkenId));

		// 案件IDをキーとして、表示用の相手方名を保持するのMapを作成する
		Map<Long, String> aitegataNameDispMap = new HashMap<>();
		for (Entry<Long, List<AnkenKanyoshaBean>> entry : ankenIdToAitegataListMap.entrySet()) {
			String aitegataNameDisp = "";
			aitegataNameDisp += entry.getValue().stream().min(Comparator.comparing(AnkenKanyoshaBean::getKanyoshaSeq)).map(AnkenKanyoshaBean::getKanyoshaName).orElse("");

			// 複数人いる場合は「外〇〇名」で表示
			if (entry.getValue().size() >= 2) {
				aitegataNameDisp += String.format(" 外%d名", entry.getValue().size() - 1);
			}
			aitegataNameDispMap.put(entry.getKey(), aitegataNameDisp);
		}

		return aitegataNameDispMap;
	}

	/**
	 * DB取得値から会計名簿：案件一覧用Dtoに変換する
	 * 
	 * @param tAnkenCustomerEntity
	 * @param ankenBunyaBean
	 * @param ankenTantoLawyerDispDtoList
	 * @param ankenTantoJimuDispDtoList
	 * @param firstSaibanBranchNo
	 * @param aitegataName
	 * @param accgSalesFeeDepositTotalDto
	 * @return
	 */
	private PersonAnkenListViewForm convert2PersonCaseForm(
			TAnkenCustomerEntity tAnkenCustomerEntity, AnkenBunyaBean ankenBunyaBean,
			List<AnkenTantoDispDto> ankenTantoLawyerDispDtoList, List<AnkenTantoDispDto> ankenTantoJimuDispDtoList,
			Long firstSaibanBranchNo, String aitegataName, AccgSalesFeeDepositTotalDto accgSalesFeeDepositTotalDto) {

		PersonAnkenListViewForm personCaseListViewForm = new PersonAnkenListViewForm();

		// 案件と顧客が一意となるデータのDtoを作成
		Long personId = tAnkenCustomerEntity.getCustomerId();
		Long ankenId = tAnkenCustomerEntity.getAnkenId();

		personCaseListViewForm.setPersonId(personId);
		personCaseListViewForm.setAnkenId(ankenId);
		personCaseListViewForm.setAnkenCreateDate(DateUtils.parseToString(ankenBunyaBean.getAnkenCreatedDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		personCaseListViewForm.setBunyaName(ankenBunyaBean.getBunyaName());
		personCaseListViewForm.setBunyaTypeCd(ankenBunyaBean.getBunyaType());
		personCaseListViewForm.setAnkenType(AnkenType.of(ankenBunyaBean.getAnkenType()));
		personCaseListViewForm.setAnkenName(ankenBunyaBean.getAnkenName());
		personCaseListViewForm.setAnkenStatus(AnkenStatus.of(tAnkenCustomerEntity.getAnkenStatus()));
		personCaseListViewForm.setAcceptDate(DateUtils.parseToString(tAnkenCustomerEntity.getJuninDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		personCaseListViewForm.setCaseCompleteDate(DateUtils.parseToString(tAnkenCustomerEntity.getJikenKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		personCaseListViewForm.setCompleteDate(DateUtils.parseToString(tAnkenCustomerEntity.getKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		personCaseListViewForm.setComplete(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));

		// 案件に紐づく担当者情報
		personCaseListViewForm.setTantoLawyer(ankenTantoLawyerDispDtoList);
		personCaseListViewForm.setTantoJimu(ankenTantoJimuDispDtoList);

		// 案件に紐づく裁判情報
		personCaseListViewForm.setFirstSaibanBranchNo(firstSaibanBranchNo);
		personCaseListViewForm.setAitegataNameDisp(aitegataName);

		// 金額サマリー情報の設定
		AccgPersonSummaryForm accgPersonSummaryForm = new AccgPersonSummaryForm();

		// 名簿ID
		accgPersonSummaryForm.setPersonId(personId);
		// 案件ID
		accgPersonSummaryForm.setAnkenId(ankenId);

		// 報酬をセット
		// 報酬合計（税込）
		accgPersonSummaryForm.setFeeTotalAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getFeeTotalAmount()));
		// 未請求報酬
		accgPersonSummaryForm.setFeeUnclaimedTotalAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getFeeUnclaimedTotalAmount()));

		// 預り金セット
		accgPersonSummaryForm.setTotalDepositBalanceAmount(accgSalesFeeDepositTotalDto.getTotalDepositBalanceAmount());
		accgPersonSummaryForm.setTotalDepositAmount(accgSalesFeeDepositTotalDto.getTotalDepositAmount());
		accgPersonSummaryForm.setTotalWithdrawalAmount(accgSalesFeeDepositTotalDto.getTotalWithdrawalAmount());
		accgPersonSummaryForm.setTotalTenantBearAmount(accgSalesFeeDepositTotalDto.getTotalTenantBearAmount());

		// 売上をセット

		// 売上合計【見込】（税込）
		accgPersonSummaryForm.setSalesAmountExpect(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesAmountExpect()));
		// 売上合計【実績】（税込）
		accgPersonSummaryForm.setSalesAmountResult(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesAmountResult()));

		// 売上合計
		accgPersonSummaryForm.setSalesTaxIncludedAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesTaxIncludedTotalAmount()));
		// 入金待ち
		accgPersonSummaryForm.setSalesAwaitingPaymentAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesAwaitingPaymentAmount()));
		// 源泉徴収税
		accgPersonSummaryForm.setSalesWithholdingAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesWithholdingTotalAmount()));
		// 値引き額（税込み）
		accgPersonSummaryForm.setSalesDiscountAmount(AccountingUtils.blank2Zero(accgSalesFeeDepositTotalDto.getSalesDiscountTaxIncludedTotalAmount()));

		// 会計サマリ
		personCaseListViewForm.setAccgPersonSummaryForm(accgPersonSummaryForm);

		return personCaseListViewForm;
	}

	/**
	 * 複数の案件・顧客サマリー情報を集計し、共通サマリオブジェクトを作成
	 * 
	 * @param viewForm
	 * @param accgSalesFeeDepositTotalDtoList
	 * @return
	 */
	private PersonCaseViewForm accgTotalCaseData(PersonCaseViewForm viewForm,
			List<AccgSalesFeeDepositTotalDto> accgSalesFeeDepositTotalDtoList) {

		// 集計ロジック
		Function<Function<AccgSalesFeeDepositTotalDto, BigDecimal>, BigDecimal> calcFn = (mapper) -> {
			List<BigDecimal> decimalList = accgSalesFeeDepositTotalDtoList.stream().map(mapper).collect(Collectors.toList());
			return AccountingUtils.calcTotal(decimalList);
		};

		// 各種項目の集計
		BigDecimal feeTotalAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getFeeTotalAmount);
		viewForm.setFeeTotalAmount(feeTotalAmount);

		BigDecimal feeUnclaimedTotalAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getFeeUnclaimedTotalAmount);
		viewForm.setFeeUnclaimedTotalAmount(feeUnclaimedTotalAmount);

		// 預り金
		BigDecimal totalDepositBalanceAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getTotalDepositBalanceAmount);
		BigDecimal totalDepositAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getTotalDepositAmount);
		BigDecimal totalWithdrawalAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getTotalWithdrawalAmount);
		BigDecimal totalTenantBearAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getTotalTenantBearAmount);

		viewForm.setTotalDepositBalanceAmount(totalDepositBalanceAmount);
		viewForm.setTotalDepositAmount(totalDepositAmount);
		viewForm.setTotalWithdrawalAmount(totalWithdrawalAmount);
		viewForm.setTotalTenantBearAmount(totalTenantBearAmount);

		// 売上

		// 売上合計
		BigDecimal personTotalSalesAmountExpect = calcFn.apply(AccgSalesFeeDepositTotalDto::getSalesAmountExpect);
		viewForm.setPersonTotalSalesAmountExpect(personTotalSalesAmountExpect);

		// 入金済み
		BigDecimal personTotalSalesAmountResult = calcFn.apply(AccgSalesFeeDepositTotalDto::getSalesAmountResult);
		viewForm.setPersonTotalSalesAmountResult(personTotalSalesAmountResult);

		// 報酬合計（税込）
		BigDecimal personTotalSalesTaxIncludedAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getSalesTaxIncludedTotalAmount);
		viewForm.setPersonTotalSalesTaxIncludedAmount(personTotalSalesTaxIncludedAmount);

		// 源泉徴収税
		BigDecimal personTotalSalesWithholdingAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getSalesWithholdingTotalAmount);
		viewForm.setPersonTotalSalesWithholdingAmount(personTotalSalesWithholdingAmount);

		// 値引き合計（税込）
		BigDecimal personTotalSalesDiscountTaxIncludedTotalAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getSalesDiscountTaxIncludedTotalAmount);
		viewForm.setPersonTotalSalesDiscountAmount(personTotalSalesDiscountTaxIncludedTotalAmount);

		// 入金待ち
		BigDecimal personTotalSalesAwaitingPaymentAmount = calcFn.apply(AccgSalesFeeDepositTotalDto::getSalesAwaitingPaymentAmount);
		viewForm.setPersonTotalSalesAwaitingAmount(personTotalSalesAwaitingPaymentAmount);

		return viewForm;
	}

}
