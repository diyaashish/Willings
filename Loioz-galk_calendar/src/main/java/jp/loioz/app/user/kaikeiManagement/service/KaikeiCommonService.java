package jp.loioz.app.user.kaikeiManagement.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.user.kaikeiManagement.dto.AnkenMeisaiListDto;
import jp.loioz.app.user.kaikeiManagement.dto.TimeChargeDownloadSelectDto;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListSearchForm;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListViewForm;
import jp.loioz.bean.AnkenMeisaiBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.DispSeisanKubun;
import jp.loioz.common.constant.CommonConstant.GensenTarget;
import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.SeisanKirokuKubun;
import jp.loioz.common.constant.CommonConstant.SeisanStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.domain.condition.KaikeiListMeisaiSearchCondition;
import jp.loioz.domain.condition.KaikeiListYoteiSearchCondition;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.domain.value.SeisanId;
import jp.loioz.dto.AnkenMeisaiDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.KaikeiKirokuBean;
import jp.loioz.dto.KaikeiKirokuDto;
import jp.loioz.dto.NyushukkinYoteiListBean;
import jp.loioz.dto.NyushukkinYoteiListDto;
import jp.loioz.dto.SeisanKirokuBean;
import jp.loioz.dto.SeisanKirokuDto;
import jp.loioz.entity.MNyushukkinKomokuEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 会計管理系画面の共通サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KaikeiCommonService extends DefaultService {

	/** 会計系の共通サービス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 名簿Daoクラス **/
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件-顧客のDaoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 入出金項目のDaoクラス */
	@Autowired
	private MNyushukkinKomokuDao mNyushukkinKomokuDao;

	/** 会計記録のDaoクラス */
	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	/** 入出金予定のDaoクラス */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** 精算記録のDaoクラス */
	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	/** 案件Daoクラス **/
	@Autowired
	private TAnkenDao tAnkenDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 会計管理画面の表示情報を作成します。<br>
	 *
	 * <pre>
	 * 案件明細一覧の「完了済データ非表示」チェックボックスの
	 * ON／OFFによる再検索を行う時用。
	 * </pre>
	 *
	 * @param searchForm 検索フォーム
	 * @return 画面表示情報
	 */
	public KaikeiListViewForm createViewFormForAnkenMeisai(KaikeiListSearchForm searchForm) {

		KaikeiListViewForm viewForm = new KaikeiListViewForm();

		if (SystemFlg.codeToBoolean(searchForm.getTransitionFlg())) {
			// 顧客軸の案件明細情報を設定する
			this.setAnkenMeisaiListForCustomer(viewForm, searchForm.getTransitionCustomerId(), SystemFlg.FLG_OFF.equalsByCode(searchForm.getAnkenMeisaiHiddenFlg()));
		} else {
			// 案件軸の案件明細情報を設定する
			this.setAnkenMeisaiListForAnken(viewForm, searchForm.getTransitionAnkenId());
		}
		return viewForm;
	}

	/**
	 * 会計管理画面の表示情報を作成します。
	 *
	 * @param searchForm 検索フォーム
	 * @return 画面表示情報
	 */
	public KaikeiListViewForm createViewForm(KaikeiListSearchForm searchForm) {

		KaikeiListViewForm viewForm = new KaikeiListViewForm();

		if (searchForm == null) {
			// 検索を行わない場合は、空の会計情報を設定して返却します。
			viewForm.setAnkenMeisaiListDtos(Collections.emptyList());
			viewForm.setKaikeiKirokuList(Collections.emptyList());
			viewForm.setNyushukkinYoteiList(Collections.emptyList());
			viewForm.setSeisanKirokuList(Collections.emptyList());
			return viewForm;
		}

		// 案件管理から遷移する場合かどうか
		boolean isTransitionFromAnken = SystemFlg.FLG_OFF.equalsByCode(searchForm.getTransitionFlg());
		viewForm.setTransitionFromAnken(isTransitionFromAnken);

		Long transitionCustomerId = searchForm.getTransitionCustomerId();
		Long transitionAnkenId = searchForm.getTransitionAnkenId();
		// ------------------------------------------------------
		// 案件明細
		// ------------------------------------------------------
		if (isTransitionFromAnken) {
			// 案件管理から遷移する場合
			this.setAnkenMeisaiListForAnken(viewForm, transitionAnkenId);
			// 案件のみとする
			searchForm.setTransitionCustomerId(null);

			// 案件IDの取得
			Long ankenId = searchForm.getTransitionAnkenId();
			// 案件情報の取得
			TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
			// 分野情報の設定
			viewForm.setBunya(tAnkenEntity.getBunyaId());

		} else {
			// 顧客管理から遷移する場合
			this.setAnkenMeisaiListForCustomer(viewForm, transitionCustomerId, true);
			// タイムチャージ出力ボタンを表示するかの判定
			this.checkDispTimeChargeBtn(viewForm, transitionCustomerId);
			// 案件のみとする
			searchForm.setTransitionAnkenId(null);

		}

		// ------------------------------------------------------
		// 会計記録
		// ------------------------------------------------------
		this.getKaikeiKirokuList(viewForm, searchForm);

		// ------------------------------------------------------
		// 入出金予定
		// ------------------------------------------------------
		searchForm.setYoteiHiddenFlg(SystemFlg.FLG_OFF.getCd());
		this.getNyushukkinYoteiList(viewForm, searchForm);

		// ------------------------------------------------------
		// 精算記録
		// ------------------------------------------------------
		if (!isTransitionFromAnken) {
			// 顧客管理から遷移する場合
			this.getSeisanKirokuListForCustomer(viewForm, transitionCustomerId);
		} else {
			// 案件管理から遷移する場合
			this.getSeisanKirokuListForAnken(viewForm, transitionAnkenId);

		}
		// タブ表示
		if (searchForm.getIsSeisanshoTab()) {
			viewForm.setIsSeisanshoTab(true);
		} else {
			viewForm.setIsSeisanshoTab(false);
		}
		return viewForm;
	}

	/**
	 * 案件明細情報の一行分を作成
	 * 
	 * @param ankenMeisaiBeanList 顧客-案件に紐づく明細情報(案件ー顧客が同じものしか考慮しない)
	 * @param tAnkenCustomerEntity 案件顧客情報
	 * @param tNyushukkinYoteiEntities 案件-顧客に紐づく入出金予定
	 * @param tKaikeiKirokuEntities 案件-顧客に紐づく精算記録
	 * @return
	 */
	public AnkenMeisaiListDto createAnkenMeisaiListDto(
			List<AnkenMeisaiBean> ankenMeisaiBeanList,
			TAnkenCustomerEntity tAnkenCustomerEntity,
			List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities,
			List<TKaikeiKirokuEntity> tKaikeiKirokuEntities) {

		// データが無い場合はnull
		if (CollectionUtils.isEmpty(ankenMeisaiBeanList)) {
			return null;
		}

		AnkenMeisaiListDto ankenMeisai = new AnkenMeisaiListDto();

		// 顧客と案件の会計情報を一行に集計するので、顧客情報と案件情報は同じ情報が想定 -> どれか一件を取得し、設定する。
		AnkenMeisaiBean anyBean = ankenMeisaiBeanList.stream().findAny().get();
		ankenMeisai.setCustomerId(anyBean.getCustomerId());
		ankenMeisai.setCustomerName(anyBean.getCustomerName());
		ankenMeisai.setAnkenId(anyBean.getAnkenId());
		ankenMeisai.setAnkenName(anyBean.getAnkenName());
		ankenMeisai.setAnkenStatus(AnkenStatus.of(anyBean.getAnkenStatus()));
		ankenMeisai.setSeisanKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		List<BigDecimal> nyukinList = new ArrayList<>();
		List<BigDecimal> shukkinList = new ArrayList<>();
		List<BigDecimal> hoshuList = new ArrayList<>();

		List<BigDecimal> seisanCompNyukinList = new ArrayList<>();
		List<BigDecimal> seisanCompShukkinList = new ArrayList<>();

		// 案件明細情報から、各データを合計算出用Listオブジェクトにaddする
		for (AnkenMeisaiBean meisaiBean : ankenMeisaiBeanList) {

			// 精算による実費かを判断
			boolean isSeisanData = meisaiBean.getSeisanDate() != null;

			// 報酬だった場合
			if (StringUtils.isNotEmpty(meisaiBean.getHoshuKomokuId())) {

				BigDecimal shukkin = meisaiBean.getShukkinGaku() != null ? meisaiBean.getShukkinGaku() : new BigDecimal(0);
				BigDecimal tax = meisaiBean.getTaxGaku() != null ? meisaiBean.getTaxGaku() : new BigDecimal(0);
				BigDecimal gensen = meisaiBean.getGensenchoshuGaku() != null ? meisaiBean.getGensenchoshuGaku() : new BigDecimal(0);
				if (TaxFlg.FOREIGN_TAX.equalsByCode(meisaiBean.getTaxFlg())) {
					shukkin = shukkin.add(tax);
				}

				if (GensenTarget.TAISHOU.equalsByCode(meisaiBean.getGensenchoshuFlg())) {
					shukkin = shukkin.subtract(gensen);
				}
				hoshuList.add(shukkin);

				if (isSeisanData) {
					seisanCompShukkinList.add(shukkin);
				}
				continue;
			}

			// 入金だった場合
			if (meisaiBean.getNyushukkinKomokuId() != null && NyushukkinType.NYUKIN.equalsByCode(meisaiBean.getNyushukkinType())) {

				BigDecimal nyukin = meisaiBean.getNyukinGaku() != null ? meisaiBean.getNyukinGaku() : new BigDecimal(0);
				nyukinList.add(nyukin);
				if (isSeisanData) {
					seisanCompNyukinList.add(nyukin);
				}
				continue;
			}

			// 出金だった場合
			if (meisaiBean.getNyushukkinKomokuId() != null && NyushukkinType.SHUKKIN.equalsByCode(meisaiBean.getNyushukkinType())) {
				BigDecimal shukkin = meisaiBean.getShukkinGaku() != null ? meisaiBean.getShukkinGaku() : new BigDecimal(0);
				shukkinList.add(shukkin);
				if (isSeisanData) {
					seisanCompShukkinList.add(shukkin);
				}
				continue;
			}

			// 報酬でも入出金でも無いデータは想定外
			throw new RuntimeException("想定外の会計データです。kaikeiSeq = 「" + meisaiBean.getKaikeiKirokuSeq() + "」");
		}

		BigDecimal nyukinTotal = commonKaikeiService.calcTotal(nyukinList);
		BigDecimal shukkinTotal = commonKaikeiService.calcTotal(shukkinList);
		BigDecimal hoshuTotal = commonKaikeiService.calcTotal(hoshuList);

		ankenMeisai.setNyukinTotal(nyukinTotal);
		ankenMeisai.setDispNyukinTotal(commonKaikeiService.toDispAmountLabel(nyukinTotal));
		ankenMeisai.setShukkinTotal(shukkinTotal);
		ankenMeisai.setDispShukkinTotal(commonKaikeiService.toDispAmountLabel(shukkinTotal));
		ankenMeisai.setHoshuTotal(hoshuTotal);
		ankenMeisai.setDispHoshuTotal(commonKaikeiService.toDispAmountLabel(hoshuTotal));

		// 案件合計を算出( 入金計 - (報酬計※源泉差引後 + 出金計) )
		BigDecimal total = nyukinTotal.subtract(hoshuTotal.add(shukkinTotal));
		ankenMeisai.setTotal(total);
		ankenMeisai.setDispTotal(commonKaikeiService.toDispAmountLabel(total));

		return ankenMeisai;
	}

	/**
	 * 会計データが登録されていない場合に表示する案件明細一覧Dtoを作成
	 * 
	 * @return
	 */
	public AnkenMeisaiListDto createNoDataAnkenMeisaiListDto() {
		AnkenMeisaiListDto ankenMeisai = new AnkenMeisaiListDto();

		BigDecimal zero = BigDecimal.ZERO;

		ankenMeisai.setNyukinTotal(zero);
		ankenMeisai.setDispNyukinTotal(commonKaikeiService.toDispAmountLabel(zero));
		ankenMeisai.setShukkinTotal(zero);
		ankenMeisai.setDispShukkinTotal(commonKaikeiService.toDispAmountLabel(zero));
		ankenMeisai.setHoshuTotal(zero);
		ankenMeisai.setDispHoshuTotal(commonKaikeiService.toDispAmountLabel(zero));
		ankenMeisai.setTotal(zero);
		ankenMeisai.setDispTotal(commonKaikeiService.toDispAmountLabel(zero));

		return ankenMeisai;
	}

	/**
	 * 会計管理画面の表示情報を作成します。<br>
	 *
	 * <pre>
	 * 会計記録一覧の「精算済データ非表示」チェックボックスの
	 * ON／OFFによる再検索を行う時用。
	 * </pre>
	 *
	 * @param searchForm 検索フォーム
	 * @return 画面表示情報
	 */
	public KaikeiListViewForm createViewFormForKaikeiKiroku(KaikeiListSearchForm searchForm) {

		KaikeiListViewForm viewForm = new KaikeiListViewForm();

		// 会計記録情報を検索します。
		this.getKaikeiKirokuList(viewForm, searchForm);

		return viewForm;
	}

	/**
	 * 会計管理画面の表示情報を作成します。<br>
	 *
	 * <pre>
	 * 入金予定一覧の「処理済データ非表示」チェックボックスの
	 * ON／OFFによる再検索を行う時用。
	 * </pre>
	 *
	 * @param searchForm 検索フォーム
	 * @return 画面表示情報
	 */
	public KaikeiListViewForm createViewFormForNyushukkinYotei(KaikeiListSearchForm searchForm) {

		KaikeiListViewForm viewForm = new KaikeiListViewForm();

		// 入金予定情報を検索します。
		this.getNyushukkinYoteiList(viewForm, searchForm);

		return viewForm;
	}
	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件明細情報を設定する
	 *
	 * <pre>
	 * ※案件管理から遷移した時用
	 * 　案件側の場合は、ステータスに制御せずにすべて表示
	 * </pre>
	 *
	 * @param viewForm 会計管理情報のフォーム
	 * @param ankenId 案件ID
	 */
	private void setAnkenMeisaiListForAnken(KaikeiListViewForm viewForm, Long ankenId) {

		List<AnkenMeisaiListDto> ankenMeisaiListDtos = new ArrayList<AnkenMeisaiListDto>();
		int notCompletedAnkenCount = 0;
		Long notCompletedAnkenId = 0L;
		Long notCompletedCustomerId = 0L;

		// -------------------------------------------------------
		// 案件明細情報の取得
		// -------------------------------------------------------

		// 案件に紐づく顧客のリストを取得
		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByAnkenId(ankenId);

		List<TPersonEntity> tPersonEntities = tPersonDao.selectByAnkenId(ankenId);
		Map<Long, TPersonEntity> customerMap = tPersonEntities.stream().collect(Collectors.toMap(TPersonEntity::getCustomerId, Function.identity()));

		// 案件に紐づく案件明細Beanをすべて取得
		List<AnkenMeisaiBean> ankenMeisaiBeanList = tKaikeiKirokuDao.selectAnkenMeisaiByAnkenId(ankenId);
		Map<Long, List<AnkenMeisaiBean>> customerIdToMeisaiMap = ankenMeisaiBeanList.stream().collect(Collectors.groupingBy(AnkenMeisaiBean::getCustomerId));

		// 案件に紐づく入出金予定をすべて取得
		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectByAnkenId(ankenId);
		Map<Long, List<TNyushukkinYoteiEntity>> customerIdToNyshukkinYoteiMap = tNyushukkinYoteiEntities.stream().collect(Collectors.groupingBy(TNyushukkinYoteiEntity::getCustomerId));

		// 案件に紐づく会計記録をすべて取得
		List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectByAnkenId(ankenId);
		Map<Long, List<TKaikeiKirokuEntity>> customerIdToKaikeiKirokuMap = tKaikeiKirokuEntities.stream().collect(Collectors.groupingBy(TKaikeiKirokuEntity::getCustomerId));

		for (TAnkenCustomerEntity tAnkenCustomerEntity : tAnkenCustomerEntities) {
			// 顧客ID
			Long customerId = tAnkenCustomerEntity.getCustomerId();

			// 未完了顧客をカウント
			if (!AnkenStatus.isSeisanComp(tAnkenCustomerEntity.getAnkenStatus())) {
				notCompletedAnkenCount = notCompletedAnkenCount + 1;
				notCompletedAnkenId = ankenId;
				notCompletedCustomerId = customerId;
			}

			// 案件ID、顧客IDに紐づく案件明細情報を取得します。
			List<AnkenMeisaiBean> meisaiBeanList = customerIdToMeisaiMap.getOrDefault(customerId, Collections.emptyList());
			List<TNyushukkinYoteiEntity> nyushukkinYoteiEntities = customerIdToNyshukkinYoteiMap.getOrDefault(customerId, Collections.emptyList());
			List<TKaikeiKirokuEntity> kaikeiKirokuEntities = customerIdToKaikeiKirokuMap.getOrDefault(customerId, Collections.emptyList());

			if (!CollectionUtils.isEmpty(meisaiBeanList)) {
				// 情報を加工します。
				AnkenMeisaiListDto ankenMeisaiListDto = this.createAnkenMeisaiListDto(meisaiBeanList, tAnkenCustomerEntity, nyushukkinYoteiEntities, kaikeiKirokuEntities);
				ankenMeisaiListDtos.add(ankenMeisaiListDto);
			} else {
				// 報酬、入出金に何も登録されていない場合に0円を表示する。
				AnkenMeisaiListDto ankenMeisaiListDto = this.createNoDataAnkenMeisaiListDto();
				ankenMeisaiListDto.setAnkenId(tAnkenCustomerEntity.getAnkenId());
				ankenMeisaiListDto.setAnkenName(""); // 表示しないので空
				ankenMeisaiListDto.setCustomerId(tAnkenCustomerEntity.getCustomerId());
				ankenMeisaiListDto.setCustomerName(PersonName.fromEntity(customerMap.get(customerId)).getName());
				ankenMeisaiListDto.setAnkenStatus(AnkenStatus.of(tAnkenCustomerEntity.getAnkenStatus()));
				ankenMeisaiListDto.setSeisanKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				ankenMeisaiListDtos.add(ankenMeisaiListDto);
			}
		}

		viewForm.setExistAnkenMeisai(!CollectionUtils.isEmpty(tAnkenCustomerEntities));
		viewForm.setAnkenMeisaiListDtos(ankenMeisaiListDtos);
		// 未完了案件の件数をセットする
		viewForm.setNotCompletedAnkenCount(notCompletedAnkenCount);
		if (notCompletedAnkenCount == 1) {
			// 未完了案件の件数が１の場合に対象の案件ID、顧客IDをセットする
			viewForm.setNotCompletedFirstAnkenId(notCompletedAnkenId);
			viewForm.setNotCompletedFirstCustomerId(notCompletedCustomerId);
		}
	}

	/**
	 * 案件明細情報を取得する
	 *
	 * <pre>
	 * ※顧客管理から遷移した時用
	 * </pre>
	 *
	 * @param viewForm 会計管理情報のフォーム
	 * @param customerId 顧客ID
	 * @param isNotIncludeCompletion 完了した案件の明細を含まないか
	 */
	private void setAnkenMeisaiListForCustomer(KaikeiListViewForm viewForm, Long customerId, boolean isNotIncludeCompletion) {

		List<AnkenMeisaiListDto> ankenMeisaiListDtos = new ArrayList<AnkenMeisaiListDto>();
		int notCompletedAnkenCount = 0;
		Long notCompletedAnkenId = 0L;
		Long notCompletedCustomerId = 0L;

		// 顧客に紐づく案件のリストを取得します。
		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByCustomerId(customerId);
		if (isNotIncludeCompletion) {
			// 未完了の案件に絞る。
			Predicate<TAnkenCustomerEntity> nonCompletedMapper = entity -> !AnkenStatus.isComp(entity.getAnkenStatus());
			tAnkenCustomerEntities = tAnkenCustomerEntities.stream().filter(nonCompletedMapper).collect(Collectors.toList());
		}

		// 案件明細情報を取得
		List<AnkenMeisaiBean> ankenMeisaiBeanList = tKaikeiKirokuDao.selectAnkenMeisaiByCustomerId(customerId);
		Map<Long, List<AnkenMeisaiBean>> ankenIdToMeisaiBeanMap = ankenMeisaiBeanList.stream().collect(Collectors.groupingBy(AnkenMeisaiBean::getAnkenId));

		List<TAnkenEntity> tAnkenEntities = tAnkenDao.selectByCustomerId(customerId);
		Map<Long, TAnkenEntity> ankenMap = tAnkenEntities.stream().collect(Collectors.toMap(TAnkenEntity::getAnkenId, Function.identity()));

		// 顧客に紐づく入出金予定をすべて取得
		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectByCustomerId(customerId);
		Map<Long, List<TNyushukkinYoteiEntity>> ankenIdToNyshukkinYoteiMap = tNyushukkinYoteiEntities.stream().collect(Collectors.groupingBy(TNyushukkinYoteiEntity::getAnkenId));

		// 顧客に紐づく会計記録をすべて取得
		List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectByCustomerId(customerId);
		Map<Long, List<TKaikeiKirokuEntity>> ankenIdToKaikeiKirokuMap = tKaikeiKirokuEntities.stream().collect(Collectors.groupingBy(TKaikeiKirokuEntity::getAnkenId));

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		for (TAnkenCustomerEntity tAnkenCustomerEntity : tAnkenCustomerEntities) {
			// 案件ID
			Long ankenId = tAnkenCustomerEntity.getAnkenId();

			// 未完了案件をカウント
			if (!AnkenStatus.isSeisanComp(tAnkenCustomerEntity.getAnkenStatus())) {
				notCompletedAnkenCount = notCompletedAnkenCount + 1;
				notCompletedAnkenId = ankenId;
				notCompletedCustomerId = customerId;
			}

			// 案件ID、顧客IDに紐づく案件明細情報を取得します。
			List<AnkenMeisaiBean> meisaiBeanList = ankenIdToMeisaiBeanMap.getOrDefault(ankenId, Collections.emptyList());
			List<TNyushukkinYoteiEntity> nyushukkinYoteiEntities = ankenIdToNyshukkinYoteiMap.getOrDefault(ankenId, Collections.emptyList());
			List<TKaikeiKirokuEntity> kaikeiKirokuEntities = ankenIdToKaikeiKirokuMap.getOrDefault(ankenId, Collections.emptyList());

			AnkenMeisaiDto ankenMeisaiDto = new AnkenMeisaiDto();
			if (!CollectionUtils.isEmpty(meisaiBeanList)) {
				// 情報を加工します。
				AnkenMeisaiListDto ankenMeisaiListDto = this.createAnkenMeisaiListDto(meisaiBeanList, tAnkenCustomerEntity, nyushukkinYoteiEntities, kaikeiKirokuEntities);
				ankenMeisaiListDtos.add(ankenMeisaiListDto);
			} else {
				// 報酬、入出金に何も登録されていない場合に0円を表示する。
				AnkenMeisaiListDto ankenMeisaiListDto = this.createNoDataAnkenMeisaiListDto();
				ankenMeisaiListDto.setAnkenId(tAnkenCustomerEntity.getAnkenId());
				ankenMeisaiListDto.setAnkenName(ankenMap.get(tAnkenCustomerEntity.getAnkenId()).getAnkenName());
				ankenMeisaiListDto.setCustomerId(tAnkenCustomerEntity.getCustomerId());
				ankenMeisaiListDto.setCustomerName(""); // 表示しないので空
				ankenMeisaiListDto.setAnkenStatus(AnkenStatus.of(tAnkenCustomerEntity.getAnkenStatus()));
				ankenMeisaiListDto.setSeisanKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				ankenMeisaiListDtos.add(ankenMeisaiListDto);
			}
			// 案件名取得
			TAnkenEntity ankenEntity = tAnkenDao.selectById(ankenId);
			if (ankenEntity.getAnkenName() != null) {
				ankenMeisaiDto.setAnkenName(ankenEntity.getAnkenName());
			} else {
				ankenMeisaiDto.setAnkenName("");
			}
			ankenMeisaiDto.setBunyaId(bunyaMap.get(ankenEntity.getBunyaId()));
			ankenMeisaiDto.setDispBunya(commonBunyaService.getBunyaName(bunyaMap.get(ankenEntity.getBunyaId())));

		}

		viewForm.setExistAnkenMeisai(!CollectionUtils.isEmpty(tAnkenCustomerEntities));
		viewForm.setAnkenMeisaiListDtos(ankenMeisaiListDtos);
		// 未完了案件の件数をセット
		viewForm.setNotCompletedAnkenCount(notCompletedAnkenCount);
		if (notCompletedAnkenCount == 1) {
			// 未完了案件の件数が１の場合に対象の案件ID、顧客IDをセットする
			viewForm.setNotCompletedFirstAnkenId(notCompletedAnkenId);
			viewForm.setNotCompletedFirstCustomerId(notCompletedCustomerId);
		}
	}

	// ****************************************************************
	// 精算記録(顧客軸画面から遷移)
	// ****************************************************************
	/**
	 * 精算記録情報を取得します。<br>
	 *
	 * <pre>
	 * 顧客管理から遷移した時用です。
	 * </pre>
	 *
	 * @param viewForm 会計管理情報のフォーム
	 * @param customerId 顧客ID
	 */
	private void getSeisanKirokuListForCustomer(KaikeiListViewForm viewForm, Long customerId) {

		boolean isExistSeisanKiroku = false;

		List<SeisanKirokuDto> seisanKirokuList = new ArrayList<SeisanKirokuDto>();

		// -------------------------------------------------------
		// 精算記録情報の取得
		// -------------------------------------------------------
		// 顧客に紐づく案件IDのリストを取得します。
		List<Long> ankenIds = tAnkenCustomerDao.selectAnkenIdsByCustomerId(customerId);

		if (!CollectionUtils.isEmpty(ankenIds)) {
			// 案件ID、顧客IDに紐づく精算記録情報を取得します。
			List<SeisanKirokuBean> seisanKirokuBeanList = tSeisanKirokuDao.selectByAnkenIdsAndCustomerId(ankenIds, customerId);

			if (!CollectionUtils.isEmpty(seisanKirokuBeanList)) {
				// 情報を加工します。
				isExistSeisanKiroku = true;
				seisanKirokuList = this.createSeisanKirokuDto(seisanKirokuBeanList);
			} else {
				viewForm.setExistSeisanKiroku(false);
			}

			viewForm.setExistSeisanKiroku(isExistSeisanKiroku);
			viewForm.setSeisanKirokuList(seisanKirokuList);
		}
	}

	// ****************************************************************
	// 精算記録（案件軸画面から遷移）
	// ****************************************************************
	/**
	 * 精算記録情報を取得します。<br>
	 *
	 * <pre>
	 * 案件軸画面から遷移した時用です。
	 * </pre>
	 *
	 * @param viewForm 会計管理情報のフォーム
	 * @param customerId 顧客ID
	 */
	private void getSeisanKirokuListForAnken(KaikeiListViewForm viewForm, Long ankenId) {

		boolean isExistSeisanKiroku = false;

		List<SeisanKirokuDto> seisanKirokuList = new ArrayList<SeisanKirokuDto>();

		// -------------------------------------------------------
		// 精算記録情報の取得
		// -------------------------------------------------------
		// 顧客に紐づく案件IDのリストを取得します。
		List<Long> customerIds = tAnkenCustomerDao.selectCustomerIdsByAnkenId(ankenId);

		if (!CollectionUtils.isEmpty(customerIds)) {
			// 案件ID、顧客IDに紐づく精算記録情報を取得します。
			List<SeisanKirokuBean> seisanKirokuBeanList = tSeisanKirokuDao.selectByCustomerIdsAndAnkenId(customerIds, ankenId);

			if (!CollectionUtils.isEmpty(seisanKirokuBeanList)) {
				// 情報を加工します。
				isExistSeisanKiroku = true;
				seisanKirokuList = this.createSeisanKirokuDto(seisanKirokuBeanList);
			} else {
				viewForm.setExistSeisanKiroku(false);
			}

			viewForm.setExistSeisanKiroku(isExistSeisanKiroku);
			viewForm.setSeisanKirokuList(seisanKirokuList);
		}
	}

	// ****************************************************************
	// タイムチャージ出力ボタン表示判定
	// ****************************************************************
	/**
	 * タイムチャージ出力ボタン表示します。<br>
	 *
	 * <pre>
	 * 顧客管理から遷移した時用です。
	 * 案件管理から遷移する場合は、タイムチャージ出力ボタンは表示しません。
	 * </pre>
	 *
	 * @param viewForm 会計管理情報のフォーム
	 * @param customerId 顧客ID
	 */
	private void checkDispTimeChargeBtn(KaikeiListViewForm viewForm, Long customerId) {
		// -------------------------------------------------------
		// 精算記録情報の取得
		// -------------------------------------------------------

		// 顧客に紐づく案件IDのリストを取得します。
		List<TKaikeiKirokuEntity> timeChargeData = tKaikeiKirokuDao.selectByHoshuKomoku(customerId, null, LawyerHoshu.TIME_CHARGE.getCd());
		if (CollectionUtils.isEmpty(timeChargeData)) {
			// 取得データがなければ、なにもしない
			return;
		} else {
			// タイムチャージ出力可能なデータが存在する
			viewForm.setDisplayTimeChargeBtn(true);
		}

		Map<Long, List<TKaikeiKirokuEntity>> ankenIdToTimeChageDataMap = timeChargeData.stream().collect(Collectors.groupingBy(TKaikeiKirokuEntity::getAnkenId));
		List<TAnkenEntity> ankenEntity = tAnkenDao.selectById(new ArrayList<>(ankenIdToTimeChageDataMap.keySet()));
		Map<Long, TAnkenEntity> ankenIdToAnkenDataMap = ankenEntity.stream().collect(Collectors.toMap(TAnkenEntity::getAnkenId, Function.identity()));

		List<TimeChargeDownloadSelectDto> timeChargeDownloadSelectDto = ankenIdToTimeChageDataMap.keySet().stream()
				.map(ankenId -> {
					TimeChargeDownloadSelectDto dto = new TimeChargeDownloadSelectDto();
					TAnkenEntity ankenData = ankenIdToAnkenDataMap.get(ankenId);
					dto.setAnkenId(AnkenId.of(ankenId));
					dto.setAnkenName(ankenData.getAnkenName());
					return dto;
				}).collect(Collectors.toList());
		viewForm.setTimeChargeDownloadSelectDto(timeChargeDownloadSelectDto);
	}

	/**
	 * 精算記録情報を、画面表示用に加工します。
	 *
	 * @param seisanKirokuBeanList 精算記録情報リスト
	 * @return 画面表示用の精算記録情報
	 */
	private List<SeisanKirokuDto> createSeisanKirokuDto(List<SeisanKirokuBean> seisanKirokuBeanList) {

		List<SeisanKirokuDto> seisanKirokuDtoList = new ArrayList<SeisanKirokuDto>();

		List<Long> seisanSeqList = new ArrayList<Long>();

		List<TKaikeiKirokuEntity> kaikeiKirokuEntityList = new ArrayList<TKaikeiKirokuEntity>();

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		for (SeisanKirokuBean seisanKirokuBean : seisanKirokuBeanList) {
			// 会計記録情報を特定するために、精算SEQリストを生成します。
			seisanSeqList.add(seisanKirokuBean.getSeisanSeq());
		}

		// 精算SEQリストに紐づく会計記録情報を取得します。
		kaikeiKirokuEntityList = tKaikeiKirokuDao.selectBySeisanSeqList(seisanSeqList);

		// **************************************
		// 表示用情報の設定
		// **************************************
		for (SeisanKirokuBean seisanKirokuBean : seisanKirokuBeanList) {

			SeisanKirokuDto seisanKirokuDto = new SeisanKirokuDto();
			Long seisanSeq = seisanKirokuBean.getSeisanSeq();

			// Bean -> Dto にコピー
			BeanUtils.copyProperties(seisanKirokuBean, seisanKirokuDto);

			// -------------------------------------------------
			// 精算ID
			// -------------------------------------------------
			seisanKirokuDto.setDisplaySeisanId(new SeisanId(seisanKirokuBean.getCustomerId(), seisanKirokuBean.getSeisanId()));

			// -------------------------------------------------
			// 登録日
			// -------------------------------------------------
			seisanKirokuDto.setCreatedAt(DateUtils.parseToString(seisanKirokuBean.getCreatedAt(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			// -------------------------------------------------
			// 精算ステータス
			// -------------------------------------------------
			seisanKirokuDto.setDispSeisanStatus(SeisanStatus.of(seisanKirokuBean.getSeisanStatus()).getVal());
			seisanKirokuDto.setSeisanStatus(seisanKirokuBean.getSeisanStatus());

			// -------------------------------------------------
			// 案件ID、案件名、分野
			// -------------------------------------------------
			seisanKirokuDto.setAnkenId(AnkenId.of(seisanKirokuBean.getAnkenId()));

			if (seisanKirokuBean.getAnkenName() != null) {
				seisanKirokuDto.setAnkenName(seisanKirokuBean.getAnkenName());
			} else {
				seisanKirokuDto.setAnkenName("");
			}
			seisanKirokuDto.setBunya(commonBunyaService.getBunyaName(bunyaMap.get(seisanKirokuBean.getBunyaId())));

			// -------------------------------------------------
			// 顧客名
			// -------------------------------------------------
			if (seisanKirokuBean.getCustomerName() != null) {
				seisanKirokuDto.setCustomerName(seisanKirokuBean.getCustomerName());
				seisanKirokuDto.setCustomerId(CustomerId.of(seisanKirokuBean.getCustomerId()));
			} else {
				seisanKirokuDto.setCustomerName("");
			}
			// -------------------------------------------------
			// 区分
			// -------------------------------------------------
			List<TKaikeiKirokuEntity> targetKaikeiKirokuEntityList = new ArrayList<TKaikeiKirokuEntity>();
			List<BigDecimal> kaishuGakuList = new ArrayList<BigDecimal>();
			BigDecimal kaishuGaku = new BigDecimal(0);

			for (TKaikeiKirokuEntity kaikeiKirokuEntity : kaikeiKirokuEntityList) {
				if (seisanSeq != null) {
					if (seisanSeq.equals(kaikeiKirokuEntity.getSeisanSeq())) {
						// 精算SEQから、対象の会計記録情報を特定します。

						targetKaikeiKirokuEntityList.add(kaikeiKirokuEntity);
					}
				}
			}
			if (SeisanKirokuKubun.SEIKYU.equalsByCode(seisanKirokuBean.getSeisanKubun().toString())) {
				// 「請求」で「一括」の場合「一括請求」になります。
				if (SeikyuType.IKKATSU.equalsByCode(seisanKirokuBean.getSeikyuType())) {
					seisanKirokuDto.setKubun(DispSeisanKubun.IKKATSU_SEIKYU.getVal());
				} else {
					// 「請求」で「分割」の場合「分割請求」になります。
					seisanKirokuDto.setKubun(DispSeisanKubun.BUNKATSU_SEIKYU.getVal());
				}
				seisanKirokuDto.setDisplaySeisanGaku(commonKaikeiService.toDispAmountLabel(seisanKirokuBean.getSeisanGaku()));
				// -------------------------------------------------
				// 回収額
				// -------------------------------------------------

				// 回収額(実際に入金された金額)を計算します。
				if (!CollectionUtils.isEmpty(targetKaikeiKirokuEntityList)) {
					for (TKaikeiKirokuEntity entity : targetKaikeiKirokuEntityList) {
						kaishuGakuList.add(entity.getNyukinGaku());
					}
				}

				// 回収額の合計を計算、Dtoに設定します。
				kaishuGaku = commonKaikeiService.calcTotal(kaishuGakuList);
				seisanKirokuDto.setKaishuGaku(kaishuGaku);
				seisanKirokuDto.setDisplayKaishuGaku(commonKaikeiService.toDispAmountLabel(kaishuGaku));
				seisanKirokuDto.setDispSeisanSeikyu(SeisanKirokuKubun.SEIKYU.getVal() + "書");

			} else {
				// 請求額がプラスの場合は、区分は「支払」になります。
				seisanKirokuDto.setKubun(DispSeisanKubun.SHIHARAI.getVal());
				seisanKirokuDto.setDisplayKaishuGaku(CommonConstant.HYPHEN);
				seisanKirokuDto.setDisplaySeisanGaku(commonKaikeiService.toDispAmountLabel(seisanKirokuBean.getSeisanGaku()));
				seisanKirokuDto.setDispSeisanSeikyu(SeisanKirokuKubun.SEISAN.getVal() + "書");
			}

			// -------------------------------------------------
			// 精算額
			// -------------------------------------------------

			seisanKirokuDtoList.add(seisanKirokuDto);
		}

		return seisanKirokuDtoList;
	}

	// ****************************************************************
	// 会計記録
	// ****************************************************************
	/**
	 * 会計記録情報を取得します。
	 *
	 * @param viewForm 会計管理情報のフォーム
	 * @param searchForm 検索条件
	 */
	private void getKaikeiKirokuList(KaikeiListViewForm viewForm, KaikeiListSearchForm searchForm) {

		// 検索条件
		KaikeiListMeisaiSearchCondition kaikeiListMeisaiSearchCondition = searchForm.toKaikeiListMeisaiSearchCondition();

		// 検索条件に紐づく会計記録情報を取得します。
		List<KaikeiKirokuBean> kaikeiKirokuBeanList = tKaikeiKirokuDao.selectBySearchCondition(kaikeiListMeisaiSearchCondition);

		if (!CollectionUtils.isEmpty(kaikeiKirokuBeanList)) {
			// 情報を画面表示用に加工します。
			List<KaikeiKirokuDto> kaikeiKirokuDtoList = this.getKaikeiKirokuDtoList(kaikeiKirokuBeanList);
			viewForm.setExistKaikeiKiroku(true);
			viewForm.setKaikeiKirokuList(kaikeiKirokuDtoList);
		} else {
			viewForm.setExistKaikeiKiroku(false);
		}

		// 画面表示用の合計金額を計算・設定します。
		this.calcTotalForKaikeiKiroku(viewForm, kaikeiKirokuBeanList);
	}

	// ****************************************************************
	// 入出金予定
	// ****************************************************************
	/**
	 * 入出金予定情報を取得します。
	 *
	 * @param viewForm 会計管理情報のフォーム情報
	 * @param searchForm 検索条件
	 */
	private void getNyushukkinYoteiList(KaikeiListViewForm viewForm, KaikeiListSearchForm searchForm) {

		// 検索条件
		KaikeiListYoteiSearchCondition kaikeiListYoteiSearchCondition = searchForm.toKaikeiListYoteiSearchCondition();

		// 入金予定情報を取得します。
		List<NyushukkinYoteiListBean> nyushukkinYoteiBeanList = tNyushukkinYoteiDao.selectNyuShukkinYoteiBySearchCondition(kaikeiListYoteiSearchCondition);

		if (!CollectionUtils.isEmpty(nyushukkinYoteiBeanList)) {
			// 情報を画面表示用に加工します。
			List<NyushukkinYoteiListDto> nyushukkinYoteiDtoList = this.getNyushukinYoteiDtoList(nyushukkinYoteiBeanList);
			viewForm.setExistNyushukkinYotei(true);
			viewForm.setNyushukkinYoteiList(nyushukkinYoteiDtoList);
		} else {
			viewForm.setExistNyushukkinYotei(false);
		}
	}

	/**
	 * 各項目の合計金額を計算します。<br>
	 *
	 * <pre>
	 * 会計記録一覧用です。
	 * </pre>
	 *
	 * @param viewForm 会計管理情報のフォーム
	 * @param kaikeiKirokuBeanList 会計記録情報Beanリスト
	 */
	private void calcTotalForKaikeiKiroku(KaikeiListViewForm viewForm, List<KaikeiKirokuBean> kaikeiKirokuBeanList) {

		List<BigDecimal> nyukinList = new ArrayList<BigDecimal>();
		List<BigDecimal> shukkinList = new ArrayList<BigDecimal>();

		// --------------------------------------------------------
		// 入金・出金を分けてリスト化
		// --------------------------------------------------------
		for (KaikeiKirokuBean bean : kaikeiKirokuBeanList) {

			if (NyushukkinType.NYUKIN.getCd().equals(bean.getNyushukkinType())) {
				// 入金の場合
				nyukinList.add(bean.getTaxIncludedNyukingaku());

			} else {
				// 出金の場合
				shukkinList.add(bean.getTaxIncludedShukkingaku());
			}
		}

		// --------------------------------------------------------
		// 各項目の合計金額計算
		// --------------------------------------------------------
		BigDecimal nyukinTotal = commonKaikeiService.calcTotal(nyukinList);
		BigDecimal shukkinTotal = commonKaikeiService.calcTotal(shukkinList);

		// --------------------------------------------------------
		// viewFormに設定します。
		// --------------------------------------------------------
		viewForm.setKaikeiKirokuNyukinTotal(commonKaikeiService.toDispAmountLabel(nyukinTotal));
		viewForm.setKaikeiKirokuShukkinTotal(commonKaikeiService.toDispAmountLabel(shukkinTotal));
	}

	/**
	 * List<KaikeiKirokuBean> -> List<KaikeiKirokuDto>
	 *
	 * @param kaikeiKirokuBeanList
	 * @return
	 */
	private List<KaikeiKirokuDto> getKaikeiKirokuDtoList(List<KaikeiKirokuBean> kaikeiKirokuBeanList) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// 入出金項目を取得しておきます。
		List<MNyushukkinKomokuEntity> nyushukkinKomokuList = mNyushukkinKomokuDao.selectAll();
		Map<Long, MNyushukkinKomokuEntity> nyushukkinKomokuMap = nyushukkinKomokuList.stream()
				.collect(Collectors.toMap(
						MNyushukkinKomokuEntity::getNyushukkinKomokuId,
						Function.identity()));

		// ---------------------------------------------------------------------
		// 画面表示用の設定処理
		// ---------------------------------------------------------------------
		Function<KaikeiKirokuBean, KaikeiKirokuDto> mapper = e -> this.convert2KaikeiMeisai(e, bunyaMap, nyushukkinKomokuMap);
		List<KaikeiKirokuDto> kaikeiKirokuDtoList = kaikeiKirokuBeanList.stream().map(mapper).collect(Collectors.toList());

		return kaikeiKirokuDtoList;
	}

	/**
	 * 入出金明細一覧DtoをBeanから変換する
	 * 
	 * @param kaikeiKirokuBean
	 * @param bunyaMap
	 * @param nyushukkinKomokuMap
	 * @return
	 */
	private KaikeiKirokuDto convert2KaikeiMeisai(KaikeiKirokuBean kaikeiKirokuBean, Map<Long, BunyaDto> bunyaMap, Map<Long, MNyushukkinKomokuEntity> nyushukkinKomokuMap) {

		KaikeiKirokuDto kaikeiKirokuDto = new KaikeiKirokuDto();

		// Beanの値をDtoにコピーします。
		BeanUtils.copyProperties(kaikeiKirokuBean, kaikeiKirokuDto);

		// kaikeiKirokuBean -> kaikeiKirokuDto に設定します。
		kaikeiKirokuDto.setDisplaySeisanId(new SeisanId(kaikeiKirokuBean.getCustomerId(), kaikeiKirokuBean.getSeisanId()));
		kaikeiKirokuDto.setNyushukkinType(kaikeiKirokuBean.getKomokuName());
		kaikeiKirokuDto.setHasseiDate(DateUtils.parseToString(kaikeiKirokuBean.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		kaikeiKirokuDto.setSeisanDate(DateUtils.parseToString(kaikeiKirokuBean.getSeisanDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		kaikeiKirokuDto.setDisplayAnkenId(AnkenId.of(kaikeiKirokuBean.getAnkenId()));
		kaikeiKirokuDto.setDisplayCustomerId(CustomerId.of(kaikeiKirokuBean.getCustomerId()));
		kaikeiKirokuDto.setCustomerName(kaikeiKirokuBean.getCustomerName());
		kaikeiKirokuDto.setAnkenName(kaikeiKirokuBean.getAnkenName());
		kaikeiKirokuDto.setBunya(commonBunyaService.getBunyaName(bunyaMap.get(kaikeiKirokuBean.getBunyaId())));
		kaikeiKirokuDto.setItem(kaikeiKirokuBean.getKomokuName());

		String bunya = commonBunyaService.getBunyaName(bunyaMap.get(kaikeiKirokuBean.getBunyaId()));
		String ankenName = kaikeiKirokuBean.getAnkenName();
		String dispAnkenName = StringUtils.trim(StringUtils.null2blank(bunya) + CommonConstant.SPACE + StringUtils.null2blank(ankenName));
		kaikeiKirokuDto.setDispAnkenName(dispAnkenName);

		// *******************************
		// 税込金額の計算
		// *******************************
		MNyushukkinKomokuEntity targetEntity = nyushukkinKomokuMap.get(kaikeiKirokuBean.getNyushukkinKomokuId());

		if (TaxFlg.FOREIGN_TAX.getCd().equals(targetEntity.getTaxFlg())) {
			// 課税（外税）の場合
			if (NyushukkinType.NYUKIN.getCd().equals(kaikeiKirokuBean.getNyushukkinType())) {
				// 入金の場合
				BigDecimal nyukingaku = kaikeiKirokuBean.getNyukinGaku();
				BigDecimal tax = kaikeiKirokuBean.getTaxGaku();
				BigDecimal taxIncludedNyukingaku = nyukingaku.add(tax);
				kaikeiKirokuDto.setDisplayNyukinGaku(commonKaikeiService.toDispAmountLabel(taxIncludedNyukingaku));
				kaikeiKirokuDto.setNyukinGaku(taxIncludedNyukingaku);
				kaikeiKirokuBean.setTaxIncludedNyukingaku(taxIncludedNyukingaku);

			} else {
				// 出金の場合
				BigDecimal shukkingaku = kaikeiKirokuBean.getShukkinGaku();
				kaikeiKirokuDto.setDisplayShukkinGaku(commonKaikeiService.toDispAmountLabel(shukkingaku));
				kaikeiKirokuDto.setShukkinGaku(shukkingaku);
				kaikeiKirokuBean.setTaxIncludedShukkingaku(shukkingaku);
			}

		} else {
			// 非課税 or 課税（内税）の場合
			kaikeiKirokuDto.setDisplayNyukinGaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuBean.getNyukinGaku()));
			kaikeiKirokuDto.setDisplayShukkinGaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuBean.getShukkinGaku()));

			kaikeiKirokuBean.setTaxIncludedNyukingaku(kaikeiKirokuBean.getNyukinGaku());
			kaikeiKirokuBean.setTaxIncludedShukkingaku(kaikeiKirokuBean.getShukkinGaku());
		}

		// 精算処理済かどうか
		if (kaikeiKirokuBean.getSeisanDate() != null || AnkenStatus.isSeisanComp(kaikeiKirokuBean.getAnkenStatus())) {
			kaikeiKirokuDto.setCompleted(true);
		}

		return kaikeiKirokuDto;
	}

	/**
	 * List<NyukinYoteiBean> -> List<NyukinYoteiDto>
	 *
	 * @param yoteiBeanList 入金予定情報Beanリスト
	 * @return 入金予定情報Dtoリスト
	 */
	private List<NyushukkinYoteiListDto> getNyushukinYoteiDtoList(List<NyushukkinYoteiListBean> yoteiBeanList) {

		// 入出金項目を取得しておきます。
		List<MNyushukkinKomokuEntity> nyushukkinKomokuList = mNyushukkinKomokuDao.selectAll();
		Map<Long, MNyushukkinKomokuEntity> nyushukkinKomokuMap = nyushukkinKomokuList.stream()
				.collect(Collectors.toMap(
						MNyushukkinKomokuEntity::getNyushukkinKomokuId,
						Function.identity()));

		Function<NyushukkinYoteiListBean, NyushukkinYoteiListDto> mapper = e -> this.convert2NyushukkinYotei(e, nyushukkinKomokuMap);
		List<NyushukkinYoteiListDto> yoteiDtoList = yoteiBeanList.stream().map(mapper).collect(Collectors.toList());
		return yoteiDtoList;
	}

	/**
	 * 入出金予定DtoをBeanから変換する
	 * 
	 * @param yoteiBean
	 * @param nyushukkinKomokuMap
	 */
	private NyushukkinYoteiListDto convert2NyushukkinYotei(NyushukkinYoteiListBean yoteiBean, Map<Long, MNyushukkinKomokuEntity> nyushukkinKomokuMap) {

		NyushukkinYoteiListDto yoteiDto = new NyushukkinYoteiListDto();

		// ----------------------------------------------------
		// Beanの値をDtoにコピーします。
		// ----------------------------------------------------
		BeanUtils.copyProperties(yoteiBean, yoteiDto);

		yoteiDto.setDisplayAnkenId(AnkenId.of(yoteiBean.getAnkenId()));
		yoteiDto.setDisplayCustomerId(CustomerId.of(yoteiBean.getCustomerId()));
		yoteiDto.setDisplaySeisanId(new SeisanId(yoteiBean.getCustomerId(), yoteiBean.getSeisanId()));
		yoteiDto.setNyushukkinYoteiDate(DateUtils.parseToString(yoteiBean.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		yoteiDto.setNyushukkinDate(DateUtils.parseToString(yoteiBean.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		// 入出金項目
		MNyushukkinKomokuEntity targetEntity = nyushukkinKomokuMap.get(yoteiBean.getNyushukkinKomokuId());
		yoteiDto.setNyushukkinKomokuName(targetEntity.getKomokuName());

		// 入出金の案件名を設定する
		String ankenName = yoteiBean.getAnkenName();
		yoteiDto.setAnkenName(ankenName);

		// ----------------------------------------------------
		// 処理済かどうかの判定
		// ----------------------------------------------------
		if (yoteiDto.getNyushukkinDate() != null && yoteiBean.getNyushukkinGaku() != null) {
			if (yoteiBean.getNyushukkinGaku().compareTo(BigDecimal.ZERO) != 0) {
				// 実績が設定されている = 処理済とみなします。
				yoteiDto.setCompleted(true);
			}
		}

		// 案件ステータスが精算完了済みの場合も完了とみなす
		if (AnkenStatus.isSeisanComp(yoteiBean.getAnkenStatus())) {
			yoteiDto.setCompleted(true);
		}

		// ----------------------------------------------------
		// 表示用金額への変換（カンマあり）とdata-Target（編集時のモーダル表示）の設定
		// ----------------------------------------------------
		// 入出金予定額を設定
		yoteiDto.setDisplayNyushukkinYoteiGaku(commonKaikeiService.toDispAmountLabel(yoteiBean.getNyushukkinYoteiGaku()));

		// 精算済みかどうかによって、編集時に開くモーダルを変更します。
		if (NyushukkinType.NYUKIN.getCd().equals(yoteiBean.getNyushukkinType())) {
			// 入金の場合、表示用の入金予定額を設定
			yoteiDto.setDisplayNyukinYoteiGaku(commonKaikeiService.toDispAmountLabel(yoteiBean.getNyushukkinYoteiGaku()));

			yoteiDto.setNyukinKomokuId(yoteiBean.getNyushukkinKomokuId());
			if (yoteiBean.getSeisanSeq() != null) {
				// 支払計画モーダルを開きます。
				yoteiDto.setDataTarget("#shiharaiPlanModal");
			} else {
				// 入金予定モーダルを開きます。
				yoteiDto.setDataTarget("#nyuShukkinYoteiModal");
			}
		} else {
			// 出金の場合、表示用の出金予定額を設定
			yoteiDto.setDisplayShukkinYoteiGaku(commonKaikeiService.toDispAmountLabel(yoteiBean.getNyushukkinYoteiGaku()));
			// 出金予定モーダルを開きます。
			yoteiDto.setDataTarget("#nyuShukkinYoteiModal");
		}

		return yoteiDto;
	}

}