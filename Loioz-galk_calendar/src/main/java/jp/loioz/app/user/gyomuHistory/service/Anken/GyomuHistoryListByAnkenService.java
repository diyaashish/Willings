package jp.loioz.app.user.gyomuHistory.service.Anken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryListByAnkenSearchForm;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryListByAnkenViewForm;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryListByAnkenViewForm.Customer;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryListByAnkenViewForm.GyomuHistory;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.GroupingUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.GyomuHistoryHeaderDto;
import jp.loioz.dto.GyomuHistoryListBean;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TSaibanJikenEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class GyomuHistoryListByAnkenService extends DefaultService {

	/** アカウント共通サービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 分野共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 業務履歴Daoクラス */
	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 裁判事件Daoクラス */
	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * viewFormの作成
	 * 
	 * @param searchFrom
	 * @return viewForm
	 */
	public GyomuHistoryListByAnkenViewForm createViewForm(GyomuHistoryListByAnkenSearchForm searchForm) {

		// viewFormオブジェクトの作成
		GyomuHistoryListByAnkenViewForm viewForm = new GyomuHistoryListByAnkenViewForm();

		// 案件IDの取得
		Long ankenId = searchForm.getTransitionAnkenId();
		// 案件情報の取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
		// 分野情報の設定
		viewForm.setBunya(tAnkenEntity.getBunyaId());

		return viewForm;
	}

	/**
	 * 業務履歴-ヘッダーの情報を取得します
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void setHeader(GyomuHistoryListByAnkenViewForm viewForm, GyomuHistoryListByAnkenSearchForm searchForm) {

		// 業務履歴 顧客側のヘッダー情報を取得します。
		List<GyomuHistoryHeaderDto> ankenHeader = tGyomuHistoryDao.selectAnkenHeader(searchForm.getTransitionAnkenId());
		// 顧客IDを再度設定する
		ankenHeader.forEach(header -> {
			header.setCustomerId(CustomerId.of(header.getId()));
		});
		viewForm.setHeader(ankenHeader);
	}

	/**
	 * 業務履歴一覧の情報を取得します。
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void setGyomuHistoryList(
			GyomuHistoryListByAnkenViewForm viewForm,
			GyomuHistoryListByAnkenSearchForm searchForm) {

		// 業務履歴 顧客側の一覧取得処理
		// ページャ情報の中に一覧情報も格納されています。
		Page<Long> page = searchAnkenGyomuHistorySeqList(searchForm);

		// ページャに格納されているSEQリストを取り出す
		List<Long> gyomuHistorySeqList = page.getContent();

		// 取得したSEQリストと検索条件を元に一覧情報を取得します
		List<GyomuHistoryListBean> gyomuHistoryListBeans = tGyomuHistoryDao.selectAnkenGyomuHistoryBeansBySearchConditions(
				searchForm,
				gyomuHistorySeqList);

		// 取得した業務履歴情報を画面表示用に加工します
		List<GyomuHistory> gyoumuHistoryList = convertViewDatasForBeans(gyomuHistoryListBeans);

		// データをセット
		viewForm.setCount(page.getTotalElements());
		viewForm.setPage(page);
		viewForm.setGyomuHistoryList(gyoumuHistoryList);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 業務履歴 顧客側の一覧取得処理
	 * 
	 * <pre>
	 * ページャー情報を同時に取得します。
	 * </pre>
	 * 
	 * @param searchForm
	 * @return
	 */
	private Page<Long> searchAnkenGyomuHistorySeqList(GyomuHistoryListByAnkenSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 業務履歴情報の取得
		List<Long> seqList = tGyomuHistoryDao.selectAnkenGyomuHistorySeqListBySearchConditions(searchForm, options);
		Page<Long> page = new PageImpl<>(seqList, pageable, options.getCount());

		return page;
	}

	/**
	 * 取得した一覧情報をBeanから表示用データに加工します。
	 * 
	 * 
	 * @param gyomuHistoryListBeans
	 * @param accountList
	 * @return 加工した業務履歴情報
	 */
	private List<GyomuHistory> convertViewDatasForBeans(List<GyomuHistoryListBean> gyomuHistoryListBeans) {

		// アカウント名
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();
		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));
		
		// 裁判情報
		List<Long> saibanMinjiSeqList = gyomuHistoryListBeans.stream()
				.filter(e -> e.getSaibanSeq() != null && !bunyaMap.get(e.getBunyaId()).isKeiji()).map(GyomuHistoryListBean::getSaibanSeq)
				.collect(Collectors.toList());
		List<Long> saibanKeijiSeqList = gyomuHistoryListBeans.stream()
				.filter(e -> e.getSaibanSeq() != null && bunyaMap.get(e.getBunyaId()).isKeiji()).map(GyomuHistoryListBean::getSaibanSeq)
				.collect(Collectors.toList());
		List<TSaibanJikenEntity> minjiSaibanList = tSaibanJikenDao.selectBySaibanSeq(saibanMinjiSeqList);
		List<TSaibanJikenEntity> keijiSaibanList = tSaibanJikenDao.selectByKeijiSaibanSeq(saibanKeijiSeqList);
		Map<Long, String> saibanJikenNameMap = new HashMap<Long, String>();
		Map<Long, CaseNumber> saibanJikenNoMap = new HashMap<Long, CaseNumber>();
		minjiSaibanList.forEach(e -> {
			saibanJikenNameMap.put(e.getSaibanSeq(), e.getJikenName());
			saibanJikenNoMap.put(e.getSaibanSeq(), CaseNumber.fromEntity(e));
		});
		keijiSaibanList.forEach(e -> {
			saibanJikenNameMap.put(e.getSaibanSeq(), e.getJikenName());
			saibanJikenNoMap.put(e.getSaibanSeq(), CaseNumber.fromEntity(e));
		});

		// 検索条件 ->業務履歴情報の変換条件
		Function<GyomuHistoryListBean, GyomuHistory> gyomuHistoryMapper = bean -> GyomuHistory.builder()
				.gyomuHistorySeq(bean.getGyomuHistorySeq())
				.transitionType(TransitionType.of(bean.getTransitionType()))
				.saibanSeq(bean.getSaibanSeq())
				.subject(bean.getSubject())
				.mainText(bean.getMainText())
				.isImportant(SystemFlg.codeToBoolean(bean.getImportantFlg()))
				.isKotei(SystemFlg.codeToBoolean(bean.getKoteiFlg()))
				.isSentDengon(SystemFlg.codeToBoolean(bean.getDengonSentFlg()))
				.supportedAt(bean.getSupportedAt())
				.createrName(accountNameMap.get(bean.getCreatedBy()))
				.ankenId(AnkenId.of(bean.getAnkenId()))
				.ankenName(AnkenName.of(bean.getAnkenName(), bean.getBunyaId()))
				.bunya(bunyaMap.get(bean.getBunyaId()))
				.labelName(StringUtils.isNotEmpty(bean.getAnkenName()) ? bean.getAnkenName() : "(案件名未入力)")
				.saibanBranchNo(bean.getSaibanBranchNo())
				.jikenName(saibanJikenNameMap.get(bean.getSaibanSeq()))
				.caseNumber(saibanJikenNoMap.get(bean.getSaibanSeq()))
				.build();

		// 検索条件 ->顧客情報の変換条件
		Function<GyomuHistoryListBean, Customer> customerMapper = bean -> Customer.builder()
				.customerId(Objects.nonNull(bean.getCustomerId()) ? CustomerId.of(bean.getCustomerId()) : null)
				.customerName(bean.getCustomerName())
				.build();

		// SEQ,IDをキーとして、変換条件に基づき、業務履歴情報を設定します。
		List<GyomuHistory> gyomuHistoryList = convertGyomuHistoryDataForBeans(
				gyomuHistoryListBeans,
				gyomuHistoryMapper,
				customerMapper);

		return gyomuHistoryList;
	}

	/**
	 * 変換条件を元に業務履歴情報を加工します。
	 * 
	 * <pre>
	 * 業務履歴SEQをキーとしてグループ化。
	 * ※ 業務履歴と案件情報が1対N
	 * </pre>
	 * 
	 * @param beans
	 * @param gyomuHistoryMapper
	 * @param ankenMapper
	 * @return
	 */
	private List<GyomuHistory> convertGyomuHistoryDataForBeans(
			List<GyomuHistoryListBean> beans,
			Function<GyomuHistoryListBean, GyomuHistory> gyomuHistoryMapper,
			Function<GyomuHistoryListBean, Customer> customerMapper) {

		// 業務履歴グループ化キー：業務履歴SEQ
		Function<GyomuHistoryListBean, Object> gyomuHistoryKey = GyomuHistoryListBean::getGyomuHistorySeq;

		// 業務履歴情報を加工
		List<GyomuHistory> gyomuHistoryList = GroupingUtils
				.addMapping(gyomuHistoryMapper, gyomuHistoryKey)
				.addMapping(customerMapper)
				.create(beans);

		return gyomuHistoryList;
	}

}
