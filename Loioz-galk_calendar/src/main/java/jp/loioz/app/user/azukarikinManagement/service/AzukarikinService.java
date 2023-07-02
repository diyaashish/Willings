package jp.loioz.app.user.azukarikinManagement.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.service.CommonSelectBoxService;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinSearchForm;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm.Anken;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm.Customer;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm.Kaikei;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.GensenTarget;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.utility.GroupingUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.AzukarikinDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.domain.condition.AzukarikinSearchCondition;
import jp.loioz.dto.AzukarikinAnkenTantoListBean;
import jp.loioz.dto.AzukarikinListBean;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MTenantEntity;

/**
 * 預り金・未収入金画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AzukarikinService extends DefaultService {

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 預り金・未収入金用のDaoクラス */
	@Autowired
	private AzukarikinDao azukarikinDao;

	/** テナント情報のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 会計計算ロジック（共通処理） */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 共通クラス：セレクトボックス */
	@Autowired
	private CommonSelectBoxService commonSelectBoxService;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面情報の作成
	 *
	 * @return viewForm
	 */
	public AzukarikinViewForm createViewForm() {

		AzukarikinViewForm viewForm = new AzukarikinViewForm();

		// 検索条件：担当者プルダウン
		List<SelectOptionForm> lawyerOptionList = commonSelectBoxService.getAccountSelectBoxFilterByType(AccountType.LAWYER);
		List<SelectOptionForm> tantoJimuOptionList = commonSelectBoxService.getAccountSelectBoxFilterByType(AccountType.JIMU);
		viewForm.setSalesOwnerOptionList(lawyerOptionList);
		viewForm.setTantoLawyerOptionList(lawyerOptionList);
		viewForm.setTantoJimuOptionList(tantoJimuOptionList);

		return viewForm;
	}

	/**
	 * 預り金・未収入金画面の表示情報を作成する
	 *
	 * @return 画面表示情報
	 */
	public AzukarikinViewForm setData(AzukarikinViewForm viewForm, AzukarikinSearchForm searchForm) {

		// テナント情報を取得
		MTenantEntity tanantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());

		// 預かり金・未収金一覧情報を検索
		Page<Long> page = this.searchAzukarikinIdList(searchForm);
		List<Long> customerIdList = page.getContent();
		List<AzukarikinListBean> azukarikinBeans = azukarikinDao.selectBeansByCustomerIdList(customerIdList);

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// 検索結果の案件件数を取得
		long ankenCount = azukarikinDao.selectAnkenCount(searchForm.toAzukarikinSearchCondition());

		// 売上計上先、担当弁護士、担当事務を取得
		List<Long> ankenIdList = azukarikinBeans.stream().map(bean -> bean.getAnkenId().asLong()).distinct().collect(Collectors.toList());
		List<AzukarikinAnkenTantoListBean> azukarikinAnkenTantoListBean = azukarikinDao.selectTantoByAnkenIdList(ankenIdList);
		Map<Long, List<AzukarikinAnkenTantoListBean>> tantoMap = azukarikinAnkenTantoListBean.stream()
				.collect(Collectors.groupingBy(AzukarikinAnkenTantoListBean::getAnkenId));

		// 検索結果->顧客情報の変換ルール
		Function<AzukarikinListBean, Customer> customerMapper = entity -> Customer.builder()
				.customerId(entity.getCustomerId())
				.customerName(entity.getCustomerName())
				.totalCustomerKaikei(new BigDecimal(0))
				.build();

		// 検索結果->案件情報の変換ルール
		Function<AzukarikinListBean, Anken> ankenMapper = entity -> Anken.builder()
				.ankenId(entity.getAnkenId())
				.ankenName(entity.getAnkenName())
				.bunya(bunyaMap.get(entity.getBunyaId()))
				.ankenStatus(AnkenStatus.of(entity.getAnkenStatus()))
				.totalAnkenKaikei(new BigDecimal(0))
				.juninDate(entity.getJuninDate())
				.lastNyukinDate(entity.getLastNyukinDate())
				.build();

		// 検索結果->会計情報の変換ルール
		Function<AzukarikinListBean, Kaikei> kaikeiMapper = entity -> Kaikei.builder()
				.kaikeiKirokuSeq(entity.getKaikeiKirokuSeq())
				.hoshuKomokuId(entity.getHoshuKomokuId())
				.nyushukkinType(entity.getNyushukkinType())
				.taxRate(entity.getTaxRate())
				.taxFlg(entity.getTaxFlg())
				.gensenchoshuFlg(entity.getGensenchoshuFlg())
				.gensen(entity.getGensenchoshuGaku())
				.nyukin(entity.getNyukinGaku())
				.shukkin(entity.getShukkinGaku())
				.hoshu(new BigDecimal(0))
				.tax(entity.getTaxGaku())
				.build();

		// 検索結果を画面表示用に変換
		List<Customer> row = customerEntitiesToRowData(azukarikinBeans, customerMapper, ankenMapper, kaikeiMapper);

		// 会計情報から金額を算出と担当者を設定する
		this.calcTotalByConvertedRowData(row, tanantEntity, tantoMap);

		// 画面表示情報に検索結果を設定
		viewForm.setRowData(row);
		viewForm.setCustomerCount(page.getTotalElements());
		viewForm.setAnkenCount(ankenCount);
		viewForm.setPage(page);
		viewForm.setOpenSearchArea(true);

		return viewForm;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 画面表示対象の顧客IDを検索する
	 *
	 * @param searchForm 検索条件
	 * @return 検索結果
	 */
	private Page<Long> searchAzukarikinIdList(AzukarikinSearchForm searchForm) {

		// 検索条件
		AzukarikinSearchCondition azukarikinSearchCondition = searchForm.toAzukarikinSearchCondition();

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// １ページ分の顧客IDを取得
		List<Long> customerIdList = azukarikinDao.selectCustomerIdBySearchConditions(azukarikinSearchCondition, options);
		Page<Long> page = new PageImpl<>(customerIdList, pageable, options.getCount());

		return page;
	}

	/**
	 * 検索結果を画面表示用預かり品・未収金一覧情報に変換する
	 *
	 * @param azukarikinListBean 検索結果
	 * @param customerMapper 顧客情報変換ルール
	 * @param ankenMapper 案件情報変換ルール
	 * @param kaikeiMapper 会計情報変換ルール
	 * @return 預かり品・未収金一覧情報
	 */
	private List<Customer> customerEntitiesToRowData(
			List<AzukarikinListBean> azukarikinListBean,
			Function<AzukarikinListBean, Customer> customerMapper,
			Function<AzukarikinListBean, Anken> ankenMapper,
			Function<AzukarikinListBean, Kaikei> kaikeiMapper) {

		// 顧客グループ化キー:顧客ID
		Function<AzukarikinListBean, Object> customerKey = AzukarikinListBean::getCustomerId;

		// 顧客グループ化キー:顧客ID
		Function<AzukarikinListBean, Object> ankenKey = AzukarikinListBean::getAnkenId;

		List<Customer> customerList = GroupingUtils
				.addMapping(customerMapper, customerKey)
				.addMapping(ankenMapper, ankenKey)
				.addMapping(kaikeiMapper)
				.create(azukarikinListBean);

		return customerList;
	}

	/**
	 * グループ化したデータから各項目の合計を計算する
	 *
	 * @param row 加工した行
	 * @param tenant 消費税の端数処理用
	 */
	private void calcTotalByConvertedRowData(List<Customer> row, MTenantEntity tenant, Map<Long, List<AzukarikinAnkenTantoListBean>> tantoMap) {

		for (Customer customer : row) {
			for (Anken anken : customer.getChildren()) {
				// 担当リストをセット
				List<AzukarikinAnkenTantoListBean> tantoList = tantoMap.getOrDefault(anken.getAnkenId().asLong(), Collections.emptyList());

				BiPredicate<AzukarikinAnkenTantoListBean, TantoType> accountTypeFilter = (bean, type) -> {
					return type.equalsByCode(bean.getTantoType());
				};
				List<String> salesOwnerName = tantoList.stream().filter(e -> accountTypeFilter.test(e, TantoType.SALES_OWNER)).map(AzukarikinAnkenTantoListBean::getAccountName).collect(Collectors.toList());
				List<String> lawyerName = tantoList.stream().filter(e -> accountTypeFilter.test(e, TantoType.LAWYER)).map(AzukarikinAnkenTantoListBean::getAccountName).collect(Collectors.toList());
				List<String> jimuName = tantoList.stream().filter(e -> accountTypeFilter.test(e, TantoType.JIMU)).map(AzukarikinAnkenTantoListBean::getAccountName).collect(Collectors.toList());

				anken.setSalesOwnerName(StringUtils.list2Comma(salesOwnerName));
				anken.setTantoLawyerName(StringUtils.list2Comma(lawyerName));
				anken.setTantoZimuName(StringUtils.list2Comma(jimuName));

				/* 案件に紐づく会計情報の合計 */
				List<BigDecimal> nyukinList = new ArrayList<>();
				List<BigDecimal> shukkinList = new ArrayList<>();
				List<BigDecimal> hoshuList = new ArrayList<>();

				for (Kaikei kaikei : anken.getChildren()) {
					if (Objects.isNull(kaikei.getKaikeiKirokuSeq())) {
						continue;
					}

					// 報酬の場合
					if (kaikei.getHoshuKomokuId() != null) {
						BigDecimal hoshu = kaikei.getShukkin() != null ? kaikei.getShukkin() : new BigDecimal(0);
						BigDecimal tax = kaikei.getTax() != null ? kaikei.getTax() : new BigDecimal(0);
						BigDecimal gensen = kaikei.getGensen() != null ? kaikei.getGensen() : new BigDecimal(0);

						if (TaxFlg.FOREIGN_TAX.equalsByCode(kaikei.getTaxFlg())) {
							// 外税の場合は、税額を加算
							hoshu = hoshu.add(tax);
						}
						if (GensenTarget.TAISHOU.equalsByCode(kaikei.getGensenchoshuFlg())) {
							hoshu = hoshu.subtract(gensen);
						}
						hoshuList.add(hoshu);
						continue;
					}

					// 入金の場合
					if (NyushukkinType.NYUKIN.equalsByCode(kaikei.getNyushukkinType())) {
						nyukinList.add(kaikei.getNyukin());
						continue;
					}

					// 出金の場合
					if (NyushukkinType.SHUKKIN.equalsByCode(kaikei.getNyushukkinType())) {
						shukkinList.add(kaikei.getShukkin());
						continue;
					}
				}

				// 会計情報から各項目の合計金額を設定
				anken.setTotalNyukin(commonKaikeiService.calcTotal(nyukinList));
				anken.setDispTotalNyukin(commonKaikeiService.toDispAmountLabel(anken.getTotalNyukin()));
				anken.setTotalShukkin(commonKaikeiService.calcTotal(shukkinList));
				anken.setDispTotalShukkin(commonKaikeiService.toDispAmountLabel(anken.getTotalShukkin()));
				anken.setHoshu(commonKaikeiService.calcTotal(hoshuList));
				anken.setDispHoshu(commonKaikeiService.toDispAmountLabel(anken.getHoshu()));

				// 案件計の計算を行う
				// 計算式：入金計 - (出金計 + 消費税計 + 報酬計 - 源泉徴収)

				// 入金計
				BigDecimal nyukinTotal = anken.getTotalNyukin();

				// (出金計 + 報酬計)の計算
				List<BigDecimal> subtractList = Arrays.asList(anken.getTotalShukkin(), anken.getHoshu());
				BigDecimal subtractTotal = commonKaikeiService.calcTotal(subtractList);

				// 案件計の計算
				BigDecimal clacAnkenTotal = nyukinTotal.subtract(subtractTotal);

				// 案件計の設定
				anken.setTotalAnkenKaikei(clacAnkenTotal);
				anken.setDispTotalAnkenKaikei(commonKaikeiService.toDispAmountLabel(anken.getTotalAnkenKaikei()));

				// 案件の子階層は表示しないため空に設定する
				anken.setChildren(Collections.emptyList());
			}

			// 顧客計の設定
			List<BigDecimal> ankenTotalList = customer.getChildren().stream()
					.map(Anken::getTotalAnkenKaikei)
					.collect(Collectors.toList());
			customer.setTotalCustomerKaikei(commonKaikeiService.calcTotal(ankenTotalList));
			customer.setDispTotalCustomerKaikei(commonKaikeiService.toDispAmountLabel(customer.getTotalCustomerKaikei()));
		}

	}
}
