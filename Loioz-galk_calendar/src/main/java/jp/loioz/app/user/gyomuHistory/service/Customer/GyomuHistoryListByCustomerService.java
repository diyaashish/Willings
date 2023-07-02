package jp.loioz.app.user.gyomuHistory.service.Customer;

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
import jp.loioz.app.common.form.WrapHeaderForm;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.ankenManagement.dto.AnkenTantoDispDto;
import jp.loioz.app.user.gyomuHistory.form.Customer.GyomuHistoryListByCustomerSearchForm;
import jp.loioz.app.user.gyomuHistory.form.Customer.GyomuHistoryListByCustomerViewForm;
import jp.loioz.app.user.gyomuHistory.form.Customer.GyomuHistoryListByCustomerViewForm.Anken;
import jp.loioz.app.user.gyomuHistory.form.Customer.GyomuHistoryListByCustomerViewForm.GyomuHistory;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.GroupingUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenTantoDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.GyomuHistoryHeaderDto;
import jp.loioz.dto.GyomuHistoryListBean;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TPersonEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class GyomuHistoryListByCustomerService extends DefaultService {

	/** アカウント共通サービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 案件共通サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 分野共通サービスクラス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 業務履歴Daoクラス */
	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * viewの作成を行う
	 * 
	 * @param searchForm
	 * @return viewForm
	 */
	public GyomuHistoryListByCustomerViewForm createViewForm(GyomuHistoryListByCustomerSearchForm searchForm) {

		// 共通ヘッダーと同じなので、取得処理を行わない
		if (Objects.isNull(searchForm.getSearchAnkenId())) {
			return new GyomuHistoryListByCustomerViewForm();
		}

		// 案件IDを検索条件に含めた場合はヘッダー独自処理とする
		GyomuHistoryListByCustomerViewForm viewForm = new GyomuHistoryListByCustomerViewForm();
		WrapHeaderForm wrapHeader = new WrapHeaderForm();

		// 顧客情報の取得と設定
		TPersonEntity personEntity = tPersonDao.selectById(searchForm.getCustomerId());
		List<TAnkenEntity> tAnkenEntities = tAnkenDao.selectByCustomerId(searchForm.getCustomerId());
		
		//分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Ankenクラスが重複するため、絶対パス
		List<jp.loioz.app.common.form.WrapHeaderForm.Anken> relatedAnkenList = tAnkenEntities.stream().map(entity -> {
			return jp.loioz.app.common.form.WrapHeaderForm.Anken.builder()
					.ankenId(AnkenId.of(entity.getAnkenId()))
					.ankenName(AnkenName.fromEntity(entity))
					.bunya(bunyaMap.get(entity.getBunyaId()))
					.labelName(commonBunyaService.bunyaAndAnkenName(entity.getAnkenName(), entity.getBunyaId(), bunyaMap))
					.build();
		}).collect(Collectors.toList());
		wrapHeader.setTransitionType(TransitionType.CUSTOMER);
		wrapHeader.setCustomerType(CustomerType.of(personEntity.getCustomerType()));
		wrapHeader.setCustomerId(CustomerId.of(searchForm.getCustomerId()));
		wrapHeader.setCustomerName(PersonName.fromEntity(personEntity).getName());
		wrapHeader.setRelatedAnken(relatedAnkenList);

		// 担当者情報の取得と設定
		// 担当者情報
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();
		List<AnkenTantoDto> ankenTantoDto = commonAnkenService.getAnkenTantoListByAnkenId(searchForm.getSearchAnkenId());

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

		wrapHeader.setTantoLaywer(tantoLaywer);
		wrapHeader.setTantoJimu(tantoJimu);

		viewForm.setWrapHeader(wrapHeader);
		return viewForm;
	}

	/**
	 * 業務履歴-ヘッダーの情報を取得します
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void setHeader(GyomuHistoryListByCustomerViewForm viewForm, GyomuHistoryListByCustomerSearchForm searchForm) {

		// 業務履歴 顧客側のヘッダー情報を取得します。
		List<GyomuHistoryHeaderDto> customerHeader = tGyomuHistoryDao.selectCustomerHeader(searchForm.getCustomerId());
		// 案件IDを設定する
		customerHeader.forEach(header -> {
			header.setAnkenId(AnkenId.of(header.getId()));
		});
		viewForm.setHeader(customerHeader);
	}

	/**
	 * 業務履歴一覧の情報を取得します。
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void setGyomuHistoryList(GyomuHistoryListByCustomerViewForm viewForm, GyomuHistoryListByCustomerSearchForm searchForm) {

		// 業務履歴 顧客側の一覧取得処理
		// ページャ情報の中に一覧情報も格納されています。
		Page<Long> page = searchCustomerGyomuHistorySeqList(searchForm);

		// ページャに格納されているSEQリストを取り出す
		List<Long> gyomuHistorySeqList = page.getContent();

		// 取得したSEQリストと検索条件を元に一覧情報を取得します
		List<GyomuHistoryListBean> gyomuHistoryListBeans = tGyomuHistoryDao.selectCustomerGyomuHistoryBeansBySearchConditions(
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
	private Page<Long> searchCustomerGyomuHistorySeqList(GyomuHistoryListByCustomerSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 業務履歴情報の取得
		List<Long> seqList = tGyomuHistoryDao.selectCustomerGyomuHistorySeqListBySearchConditions(searchForm, options);
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

		// 検索条件 ->業務履歴情報の変換条件
		Function<GyomuHistoryListBean, GyomuHistory> gyomuHistoryMapper = bean -> GyomuHistory.builder()
				.gyomuHistorySeq(bean.getGyomuHistorySeq())
				.transitionType(TransitionType.of(bean.getTransitionType()))
				.subject(bean.getSubject())
				.mainText(bean.getMainText())
				.isImportant(SystemFlg.codeToBoolean(bean.getImportantFlg()))
				.isKotei(SystemFlg.codeToBoolean(bean.getKoteiFlg()))
				.isSentDengon(SystemFlg.codeToBoolean(bean.getDengonSentFlg()))
				.supportedAt(bean.getSupportedAt())
				.createrName(accountNameMap.get(bean.getCreatedBy()))
				.customerId(CustomerId.of(bean.getCustomerId()))
				.customerName(bean.getCustomerName())
				.build();

		// 検索条件 ->案件情報の変換条件
		Function<GyomuHistoryListBean, Anken> ankenMapper = bean -> Anken.builder()
				.ankenId(Objects.nonNull(bean.getAnkenId()) ? AnkenId.of(bean.getAnkenId()) : null)
				.ankenName(AnkenName.of(bean.getAnkenName(), bean.getBunyaId()))
				.bunya(bunyaMap.get(bean.getBunyaId()))
				.labelName(StringUtils.isNotEmpty(bean.getAnkenName()) ? bean.getAnkenName() : "(案件名未入力)")
				.build();

		// SEQ,IDをキーとして、変換条件に基づき、業務履歴情報を設定します。
		List<GyomuHistory> gyomuHistoryList = convertGyomuHistoryDataForBeans(
				gyomuHistoryListBeans,
				gyomuHistoryMapper,
				ankenMapper);

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
			Function<GyomuHistoryListBean, Anken> ankenMapper) {

		// 業務履歴グループ化キー：業務履歴SEQ
		Function<GyomuHistoryListBean, Object> gyomuHistoryKey = GyomuHistoryListBean::getGyomuHistorySeq;

		// 業務履歴情報を加工
		List<GyomuHistory> gyomuHistoryList = GroupingUtils
				.addMapping(gyomuHistoryMapper, gyomuHistoryKey)
				.addMapping(ankenMapper)
				.create(beans);

		return gyomuHistoryList;
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
		return dispDto;
	}

}
