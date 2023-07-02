package jp.loioz.app.user.kaikeiManagement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.seasar.doma.jdbc.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.service.CommonSelectBoxService;
import jp.loioz.app.common.validation.accessDB.CommonAnkenValidator;
import jp.loioz.app.common.validation.accessDB.CommonKaikeiValidator;
import jp.loioz.app.user.kaikeiManagement.dto.HoshuMeisaiListDto;
import jp.loioz.app.user.kaikeiManagement.form.AnkenMeisaiEditForm;
import jp.loioz.app.user.kaikeiManagement.form.HoshuMeisaiEditForm;
import jp.loioz.app.user.kaikeiManagement.form.NyushukkinEditForm;
import jp.loioz.app.user.kaikeiManagement.form.NyushukkinYoteiEditForm;
import jp.loioz.app.user.kaikeiManagement.form.ShiharaiPlanEditForm;
import jp.loioz.app.user.kaikeiManagement.form.ShiharaiPlanEditForm.ShiharaiPlanYotei;
import jp.loioz.app.user.kaikeiManagement.form.ajax.AutoCalcTankaAjaxRequest;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.GensenChoshu;
import jp.loioz.common.constant.CommonConstant.GensenChoshuRate;
import jp.loioz.common.constant.CommonConstant.GensenTarget;
import jp.loioz.common.constant.CommonConstant.HoshuHasuType;
import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.common.constant.CommonConstant.NyushukkinKomokuType;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.SeisanShiharaiMonthDay;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.data.LoiozDecimalFormat;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TCustomerCommonDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import jp.loioz.dto.AnkenMeisaiDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.HoshuMeisaiBean;
import jp.loioz.dto.KaikeiKirokuBean;
import jp.loioz.dto.KaikeiKirokuDto;
import jp.loioz.dto.KaikeiManagementRelationDto;
import jp.loioz.dto.NyushukkinKomokuListDto;
import jp.loioz.dto.NyushukkinYoteiEditDto;
import jp.loioz.dto.ShiharaiPlanDto;
import jp.loioz.entity.MNyushukkinKomokuEntity;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TSeisanKirokuEntity;

/**
 * 会計管理編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KaikeiEditService extends DefaultService {

	/** テナント用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 共通プルダウンサービス */
	@Autowired
	private CommonSelectBoxService commonSelectBoxService;

	/** 会計系の共通サービス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 会計系の共通サービス */
	@Autowired
	private CommonAccountService commonAccountiService;

	/** 分野共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 共通案件バリデーター */
	@Autowired
	private CommonAnkenValidator commonAnkenValidator;

	/** 共通会計バリデーター */
	@Autowired
	private CommonKaikeiValidator commonKaikeiValidator;

	/** 顧客井共通のDaoクラス */
	@Autowired
	private TCustomerCommonDao tCustomerCommonDao;

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

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================
	// ****************************************************************
	// 案件明細
	// ****************************************************************
	/**
	 * 初期データを設定します。<br>
	 *
	 * <pre>
	 *案件明細情報の新規登録用です。
	 * </pre>
	 *
	 * @param transitionFlg 遷移元判定フラグ
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @return 画面表示情報
	 */
	public AnkenMeisaiEditForm createAnkenMeisaiEditForm(String transitionFlg, Long transitionAnkenId, Long transitionCustomerId) {

		// 案件管理から遷移する場合かどうか
		boolean isTransitionFromAnken = SystemFlg.FLG_OFF.getCd().equals(transitionFlg);

		AnkenMeisaiEditForm ankenMeisaiEditForm = new AnkenMeisaiEditForm();
		AnkenMeisaiDto ankenMeisaiDto = new AnkenMeisaiDto();

		// 発生日
		LocalDate now = LocalDate.now();
		ankenMeisaiDto.setHasseiDate(DateUtils.parseToString(now, DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		// タイムチャージ時間の指定方法
		ankenMeisaiDto.setTimeChargeTimeShitei(TimeChargeTimeShitei.START_END_TIME.getCd());

		// --------------------------------------------------------------
		// 関連情報の取得
		// --------------------------------------------------------------
		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();
		if (isTransitionFromAnken) {
			// 案件に紐づく顧客リストの取得
			Predicate<KaikeiManagementRelationDto> nonConpletedMapper = dto -> !AnkenStatus.isSeisanComp(dto.getAnkenStatusCd());
			relationDtoList = getRelationListForAnken(transitionAnkenId).stream().filter(nonConpletedMapper).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(relationDtoList)) {
				Function<KaikeiManagementRelationDto, Long> dtoMapper = entity -> entity.getCustomerId().asLong();
				Long customerId = getAnkenMeisaiRelateAnkenCustomerOptionParam(relationDtoList, dtoMapper);
				ankenMeisaiDto.setCustomerId(customerId);
				// 税抜、税込選択は前回の登録情報を引き継がない
				ankenMeisaiDto.setTaxFlg(TaxFlg.FOREIGN_TAX.getCd());
				ankenMeisaiDto.setTaxRate(getTaxRate(transitionAnkenId, customerId, now));
				ankenMeisaiDto.setGensenchoshuFlg(getPrevGensenChoshu(transitionAnkenId, customerId));
			} else {
				ankenMeisaiDto.setTaxFlg(TaxFlg.FOREIGN_TAX.getCd());
				ankenMeisaiDto.setTaxRate(TaxRate.TEN_PERCENT.getCd());
				ankenMeisaiDto.setGensenchoshuFlg(GensenChoshu.DO.getCd());
			}

		} else {
			// 顧客に紐づく案件リストの取得
			Predicate<KaikeiManagementRelationDto> nonConpletedMapper = dto -> !AnkenStatus.isSeisanComp(dto.getAnkenStatusCd());
			relationDtoList = getRelationListForCustomer(transitionCustomerId).stream().filter(nonConpletedMapper).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(relationDtoList)) {
				Function<KaikeiManagementRelationDto, Long> dtoMapper = entity -> entity.getAnkenId().asLong();
				Long ankenId = getAnkenMeisaiRelateAnkenCustomerOptionParam(relationDtoList, dtoMapper);
				ankenMeisaiDto.setAnkenId(ankenId);
				// 税抜、税込選択は前回の登録情報を引き継がない
				ankenMeisaiDto.setTaxFlg(TaxFlg.FOREIGN_TAX.getCd());
				ankenMeisaiDto.setTaxRate(getTaxRate(ankenId, transitionCustomerId, now));
				ankenMeisaiDto.setGensenchoshuFlg(getPrevGensenChoshu(ankenId, transitionCustomerId));
			} else {
				ankenMeisaiDto.setTaxFlg(TaxFlg.FOREIGN_TAX.getCd());
				ankenMeisaiDto.setTaxRate(TaxRate.TEN_PERCENT.getCd());
				ankenMeisaiDto.setGensenchoshuFlg(GensenChoshu.DO.getCd());
			}
		}

		// --------------------------------------------------------------
		// FormにDtoを設定します。
		// --------------------------------------------------------------
		ankenMeisaiEditForm.setTransitionCustomerId(transitionCustomerId);
		ankenMeisaiEditForm.setTransitionAnkenId(transitionAnkenId);
		ankenMeisaiEditForm.setKaikeiManagementList(relationDtoList);
		ankenMeisaiEditForm.setAnkenMeisaiDto(ankenMeisaiDto);

		return ankenMeisaiEditForm;
	}

	/**
	 * 会計画面モーダルの顧客(案件)プルダウンの初期設定するIDを取得する
	 * 
	 * @param relateList
	 * @param dtoMapper
	 * @returns
	 */
	private Long getAnkenMeisaiRelateAnkenCustomerOptionParam(
			List<KaikeiManagementRelationDto> relateList,
			Function<KaikeiManagementRelationDto, Long> dtoMapper) {

		// 有効な顧客で絞り込み
		List<KaikeiManagementRelationDto> validityList = relateList.stream().filter(KaikeiManagementRelationDto::isCanKaikeiCreate).collect(Collectors.toList());
		if (validityList != null && validityList.size() == 1) {
			// 一件のみなら、それを設定
			return validityList.stream().findFirst().map(dtoMapper).orElse(null);
		} else {
			// 複数 or なにもない場合、nullを返却
			return null;
		}
	}

	/**
	 * 初期データを設定します。<br>
	 *
	 * <pre>
	 * 案件明細情報の更新用です。
	 * </pre>
	 *
	 * @param kaikeiKirokuSeq 会計記録SEQ
	 * @param transitionFlg 遷移元判定フラグ
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @return 画面表示情報
	 * @throws AppException
	 */
	public AnkenMeisaiEditForm setInitDataForAnkenMeisai(Long kaikeiKirokuSeq, String transitionFlg, Long transitionAnkenId,
			Long transitionCustomerId) throws AppException {

		// 案件管理から遷移する場合かどうか
		boolean isTransitionFromAnken = SystemFlg.FLG_OFF.getCd().equals(transitionFlg);

		AnkenMeisaiEditForm ankenMeisaiEditForm = new AnkenMeisaiEditForm();

		if (kaikeiKirokuSeq == null) {
			// 会計記録SEQがnull = 新規登録とみなします。
			return this.createAnkenMeisaiEditForm(transitionFlg, transitionAnkenId, transitionCustomerId);
		}

		// 更新フラグをtrueに設定
		ankenMeisaiEditForm.setUpdateFlg(true);
		// --------------------------------------------------------------
		// 案件明細情報の取得
		// --------------------------------------------------------------
		TKaikeiKirokuEntity kaikeiKirokuEntity = tKaikeiKirokuDao.selectByKaikeiKirokuSeq(kaikeiKirokuSeq);

		if (kaikeiKirokuEntity == null) {
			// 排他エラーとして例外処理をします。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// Entity -> Dto
		AnkenMeisaiDto ankenMeisaiDto = this.setAnkenMeisaiDto(kaikeiKirokuEntity);

		// 明細が精算書に紐づいている場合 -> 編集不可
		if (ankenMeisaiDto.getSeisanId() != null) {
			ankenMeisaiEditForm.setDisabledFlg(true);
		}

		// 案件ステータスが精算完了と判断される場合 -> 編集不可
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenMeisaiDto.getAnkenId(), ankenMeisaiDto.getCustomerId());
		String ankenStatus = tAnkenCustomerEntity.getAnkenStatus();
		boolean isAnkenSeisanCompleted = AnkenStatus.isSeisanComp(ankenStatus);
		if (isAnkenSeisanCompleted) {
			ankenMeisaiEditForm.setDisabledFlg(true);
		}
		
		
		// 編集不可メッセージ表示フラグの設定（ステータスの状態によるもの）
		Consumer<Boolean> setShowAnkenCompletedMsg = bool -> ankenMeisaiEditForm.setShowAnkenCompletedMsg(bool);
		Consumer<Boolean> setShowSeisanCompletedMsg = bool -> ankenMeisaiEditForm.setShowSeisanCompletedMsg(bool);
		commonKaikeiService.setShowCantEditMsgFlg(tAnkenCustomerEntity.getKanryoDate(), ankenStatus, setShowAnkenCompletedMsg, setShowSeisanCompletedMsg);
		// 編集不可メッセージ表示フラグの設定（対象の会計データの状態によるもの）
		if (kaikeiKirokuEntity.getSeisanSeq() != null) {
			// 会計記録の精算SEQがNULLじゃない（精算書に紐づけられているもの）の場合
			ankenMeisaiEditForm.setShowUsedSeisanshoMsg(true);
		} 
		
		// --------------------------------------------------------------
		// 関連情報の取得
		// --------------------------------------------------------------
		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();
		if (isTransitionFromAnken) {
			// 案件に紐づく顧客リストの取得
			relationDtoList = getRelationListForAnken(transitionAnkenId);

		} else {
			// 顧客に紐づく案件リストの取得
			relationDtoList = getRelationListForCustomer(transitionCustomerId);
		}

		// --------------------------------------------------------------
		// FormにDtoを設定します。
		// --------------------------------------------------------------
		ankenMeisaiEditForm.setTransitionFlg(transitionFlg);
		ankenMeisaiEditForm.setTransitionAnkenId(transitionAnkenId);
		ankenMeisaiEditForm.setTransitionCustomerId(transitionCustomerId);
		ankenMeisaiEditForm.setKaikeiManagementList(relationDtoList);
		ankenMeisaiEditForm.setAnkenMeisaiDto(ankenMeisaiDto);

		return ankenMeisaiEditForm;
	}

	/**
	 * 過去のタイムチャージの単価を取得します。<br>
	 *
	 * <pre>
	 * 弁護士項目「タイムチャージ」を選択した時用です。
	 * タイムチャージの登録履歴がある場合、単価を引き継ぎます。
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 過去のタイムチャージ単価
	 */
	public String getRecentTanka(Long ankenId, Long customerId) throws AppException {

		String recentTanka = "";

		List<TKaikeiKirokuEntity> kaikeiKirokuList = tKaikeiKirokuDao.selectByAnkenIdAndCustomerIdAndHoshuKomoku(ankenId, customerId,
				LawyerHoshu.TIME_CHARGE.getCd(), false);

		if (!ListUtils.isEmpty(kaikeiKirokuList)) {
			// 降順で取得しているので、リストの先頭が最新になります。
			BigDecimal tanka = kaikeiKirokuList.get(0).getTimeChargeTanka();
			recentTanka = commonKaikeiService.toDispAmountLabel(tanka);
		}

		return recentTanka;
	}

	/**
	 * 消費税率を取得します。<br>
	 *
	 * <pre>
	 * 報酬登録モーダルで、案件を切り替えた際に使用します。
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @param hasseiDate 発生日
	 * @return 消費税率Cd
	 */
	public String getTaxRate(Long ankenId, Long customerId, LocalDate hasseiDate) {

		String taxRateStr = "";
		LocalDate baseDate = null;
		LocalDate taxRevisionDate = CommonConstant.TAX_TEN_PERSENT_STARTING_DATE;

		// 受任日を取得します。
		TAnkenCustomerEntity ankenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		// 受任日を入力している場合は、受任日を消費税の基準日とする
		if (ankenCustomerEntity != null && ankenCustomerEntity.getJuninDate() != null) {
			baseDate = ankenCustomerEntity.getJuninDate();

		} else {
			baseDate = hasseiDate;
		}

		if (baseDate == null) {
			// 受任日も発生日も未設定の場合は、初期値で「10％」を設定する
			baseDate = LocalDate.now();
			taxRateStr = TaxRate.of(baseDate).getCd();
		}

		// 消費税10%の2019年10月1日より前かどうか判定
		if (!(taxRevisionDate.isBefore(baseDate) || taxRevisionDate.equals(baseDate))) {
			// 基準日が2019年10月01日より前の場合、消費税は8%
			taxRateStr = TaxRate.EIGHT_PERCENT.getCd();

		} else {
			// 基準日が2019年10月01日以降の場合、消費税は10%
			taxRateStr = Optional.of(TaxRate.of(baseDate)).orElse(TaxRate.TEN_PERCENT).getCd();

		}

		return taxRateStr;
	}

	/**
	 * 案件明細情報(弁護士報酬)の新規登録を行います。
	 *
	 * @param ankenMeisaiEditForm 案件明細情報の編集用フォーム
	 * @throws AppException
	 */
	public void saveAnkenMeisai(AnkenMeisaiEditForm ankenMeisaiEditForm) throws AppException {

		// 入力した情報を取得します。
		AnkenMeisaiDto inputAnkenMeisaiDto = ankenMeisaiEditForm.getAnkenMeisaiDto();

		// 遷移元のIDを設定します。
		if (SystemFlg.FLG_OFF.getCd().equals(ankenMeisaiEditForm.getTransitionFlg())) {
			// 案件管理から遷移する場合
			inputAnkenMeisaiDto.setAnkenId(ankenMeisaiEditForm.getTransitionAnkenId());

		} else {
			// 顧客管理から遷移する場合
			inputAnkenMeisaiDto.setCustomerId(ankenMeisaiEditForm.getTransitionCustomerId());
		}

		// 案件明細情報(弁護士報酬)を登録します。
		this.saveAnkenMeisai(inputAnkenMeisaiDto);
	}

	/**
	 * 案件明細情報(弁護士報酬)の更新を行います。
	 *
	 * @param ankenMeisaiEditForm 報酬情報の編集用フォーム
	 * @throws AppException
	 */
	public void updateAnkenMeisai(AnkenMeisaiEditForm ankenMeisaiEditForm) throws AppException {

		// 画面で入力した情報を取得します。
		AnkenMeisaiDto inputAnkenMeisaiDto = ankenMeisaiEditForm.getAnkenMeisaiDto();

		// 遷移先のIDを設定します。
		if (SystemFlg.FLG_OFF.getCd().equals(ankenMeisaiEditForm.getTransitionFlg())) {
			// 案件管理から遷移する場合
			inputAnkenMeisaiDto.setAnkenId(ankenMeisaiEditForm.getTransitionAnkenId());

		} else {
			// 顧客管理から遷移する場合
			inputAnkenMeisaiDto.setCustomerId(ankenMeisaiEditForm.getTransitionCustomerId());
		}

		// 案件明細情報(弁護士報酬)を登録します。
		this.updateAnkenMeisai(inputAnkenMeisaiDto);
	}

	/**
	 * 案件明細情報(弁護士報酬)を削除します。
	 *
	 * @param ankenMeisaiEditForm 案件明細情報の編集用フォーム
	 * @throws AppException
	 */
	public void deleteAnkenMeisai(AnkenMeisaiEditForm ankenMeisaiEditForm) throws AppException {

		// 削除対象の情報を取得します。
		AnkenMeisaiDto targetAnkenMeisaiDto = ankenMeisaiEditForm.getAnkenMeisaiDto();

		// 会計記録SEQに紐づく案件明細情報を取得します。
		TKaikeiKirokuEntity kaikeiKirokuEntity = tKaikeiKirokuDao.selectByKaikeiKirokuSeq(targetAnkenMeisaiDto.getKaikeiKirokuSeq());
		if (kaikeiKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 削除します。
			tKaikeiKirokuDao.delete(kaikeiKirokuEntity);

		} catch (OptimisticLockException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * 源泉徴収の計算式、源泉徴収額、消費税、報酬計を計算します。<br>
	 *
	 * <pre>
	 * 報酬登録モーダルで、金額を入力した際の自動計算用です。
	 * </pre>
	 *
	 * @param kingaku 金額
	 * @param taxFlgCd 消費税フラグCd（税抜、税込の選択値）
	 * @param taxRateCd 消費税率Cd
	 * @param gensenFlg 源泉徴収フラグ
	 * @return 計算式、源泉徴収額、消費税、報酬計のMap
	 */
	public Map<String, String> autoCalc(BigDecimal kingaku, String taxFlgCd, String taxRateCd, String gensenFlg) {

		// --------------------------------------------------
		// 消費税を計算
		// --------------------------------------------------
		// 消費税の端数処理方法を取得
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		String taxHasuType = tenantEntity.getTaxHasuType();

		BigDecimal tax = new BigDecimal(0);
		BigDecimal kingakuExcludingTax = new BigDecimal(0);

		if (TaxFlg.INTERNAL_TAX.getCd().equals(taxFlgCd)) {
			// 出金額が税込の場合

			// 消費税額
			tax = commonKaikeiService.calcInternalTax(kingaku, taxRateCd, taxHasuType);
			// 出金額の税抜額
			kingakuExcludingTax = kingaku.subtract(tax);

		} else {
			// 出金額が税抜の場合

			// 消費税額
			tax = commonKaikeiService.calcTax(kingaku, taxRateCd, taxHasuType);
			// 出金額の税抜額
			kingakuExcludingTax = kingaku;
		}
		String hoshuGakuStr = commonKaikeiService.toDispAmountLabel(kingakuExcludingTax);

		// カンマつきに変換
		String taxStr = commonKaikeiService.toDispAmountLabel(tax);

		// --------------------------------------------------
		// 源泉徴収の計算式を求める
		// --------------------------------------------------
		String calcFormula1 = "";
		String calcFormula2 = "";

		// 100万円との比較
		BigDecimal oneMillion = new BigDecimal(1000000);
		int isLargerThanOneMillion = kingakuExcludingTax.compareTo(oneMillion);

		if (isLargerThanOneMillion == 1) {
			// **************************
			// 100万円より高額
			// **************************
			// 源泉徴収率が10.21％になる部分の金額の計算式
			String tenGensenRateStr = GensenChoshuRate.TEN.getVal();
			calcFormula1 = commonKaikeiService.toDispAmountLabel(oneMillion) + "円 × " + tenGensenRateStr + "%";

			// 源泉徴収額が20.42％になる部分の計算式
			BigDecimal twentyPerKingaku = kingakuExcludingTax.subtract(oneMillion);
			String twentyGensenRateStr = GensenChoshuRate.TWENTY.getVal();
			calcFormula2 = " + " + commonKaikeiService.toDispAmountLabel(twentyPerKingaku) + "円 × " + twentyGensenRateStr + "%";

		} else {
			// **************************
			// 100万円以下
			// **************************
			String gensenRateStr = GensenChoshuRate.TEN.getVal();
			calcFormula1 = commonKaikeiService.toDispAmountLabel(kingakuExcludingTax) + "円 × " + gensenRateStr + "%";
		}

		// --------------------------------------------------
		// 源泉徴収額を計算
		// --------------------------------------------------
		BigDecimal gensenGaku = new BigDecimal(0);
		String gensenGakuStr = "";

		if (GensenChoshu.DO.getCd().equals(gensenFlg)) {
			// 源泉徴収額を計算し、端数は切り捨て
			gensenGaku = commonKaikeiService.calcGensenChoshu(kingakuExcludingTax);

			// カンマつきに変換（源泉徴収は表示上はマイナス表記とする）
			gensenGakuStr = commonKaikeiService.toDispAmountLabel(gensenGaku.negate());
		}

		// --------------------------------------------------
		// 報酬計を計算
		// 報酬計 = 金額 + 消費税 - 源泉徴収
		// --------------------------------------------------
		List<BigDecimal> calcList = new ArrayList<BigDecimal>();

		calcList.add(kingakuExcludingTax);
		calcList.add(tax);

		// 金額 + 消費税 を計算します。
		BigDecimal kingakuAndTax = commonKaikeiService.calcTotal(calcList);

		// (金額+消費税) - 源泉徴収 を計算します。
		BigDecimal totalKingaku = kingakuAndTax.subtract(gensenGaku);
		String totalKingakuStr = commonKaikeiService.toDispAmountLabel(totalKingaku);

		// --------------------------------------------------
		// 算出したデータをMap型で返却
		// --------------------------------------------------
		Map<String, String> result = new HashMap<>();
		result.put("calcFormula1", Optional.of(calcFormula1).orElse(new String("")));// 計算式(100万以下)
		result.put("calcFormula2", Optional.of(calcFormula2).orElse(new String("")));// 計算式(100万超)
		result.put("gensenGaku", Optional.of(gensenGakuStr).orElse(new String(""))); // 源泉徴収額
		result.put("hoshuGaku", Optional.of(hoshuGakuStr).orElse(new String("")));// 報酬額
		result.put("taxGaku", Optional.of(taxStr).orElse(new String("")));// 消費税
		result.put("totalKingaku", Optional.of(totalKingakuStr).orElse(new String("")));// 報酬計

		return result;
	}

	/**
	 * 単価の計算
	 *
	 * @param request
	 * @return
	 * @throws AppException
	 */
	public Map<String, String> autoCalcTanka(AutoCalcTankaAjaxRequest request) throws AppException {

		// 単価をDicemal型に変換
		BigDecimal tanka = LoiozNumberUtils.parseAsBigDecimal(request.getTanka());

		// *********************************************
		// 単価 × 時間 の金額を計算
		// *********************************************

		// 時間(分) を Mathの時間に変換する。
		BigDecimal decimalMinutes = BigDecimal.valueOf(request.getTimeChargeTime());

		// 単価 × 時間 を計算します。
		BigDecimal hourKingaku = hoshuCal(tanka, decimalMinutes);

		// 計算式、源泉徴収額、消費税、報酬計を計算します。
		Map<String, String> resultMap = this.autoCalc(hourKingaku, request.getTaxFlg(), request.getTaxRate(), request.getGensenFlg());

		return resultMap;
	}

	/**
	 * 前回の情報から、選択状態とするTaxFlgのEnumのCD値を返却する（税込or税抜）。
	 * 
	 * @param ankenId
	 * @param customerId
	 * @return TaxFlgのEnumのCD
	 */
	public String getPrevTaxFlg(Long ankenId, Long customerId) {

		String taxFlg = TaxFlg.FOREIGN_TAX.getCd();

		TKaikeiKirokuEntity prevKaikeiKirokuEntity = tKaikeiKirokuDao.selectPrevByAnkenIdAndCustomerId(ankenId, customerId);

		if (prevKaikeiKirokuEntity != null) {
			taxFlg = prevKaikeiKirokuEntity.getTaxFlg();
		}

		return taxFlg;
	}

	/**
	 * 前回の情報から、源泉徴収するかどうかを判断する。
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 0：源泉徴収しない、1：源泉徴収する
	 */
	public String getPrevGensenChoshu(Long ankenId, Long customerId) {

		String gensenChoshu = GensenChoshu.DO.getCd();

		TKaikeiKirokuEntity prevKaikeiKirokuEntity = tKaikeiKirokuDao.selectPrevByAnkenIdAndCustomerId(ankenId, customerId);

		if (prevKaikeiKirokuEntity != null) {
			gensenChoshu = prevKaikeiKirokuEntity.getGensenchoshuFlg();
		}

		return gensenChoshu;
	}

	// ****************************************************************
	// 報酬明細
	// ****************************************************************
	/**
	 * 報酬明細Formを作成
	 * 
	 * @param transitionType 遷移元種別
	 * @param transitionAnkenId 遷移元ID
	 * @param transitionCustomerId 遷移元ID
	 * @param seisanCompDispFlg 精算済みデータ表示チェック
	 * @param ankenId 選択した案件ID(顧客側の案件行を押下のみ)
	 * @param customerId 選択した顧客ID(案件側の顧客行押下時のみ)
	 * @return 報酬明細情報
	 */
	public HoshuMeisaiEditForm createHoshuMeisaiForm(TransitionType transitionType, Long transitionAnkenId, Long transitionCustomerId,
			boolean seisanCompDispFlg, Long ankenId, Long customerId) {

		// 報酬明細情報を取得します。
		HoshuMeisaiEditForm hoshuMeisaiEditForm = new HoshuMeisaiEditForm();

		// 未完了Filter条件
		Predicate<TAnkenCustomerEntity> nonCompAnkenFilter = e -> !AnkenStatus.isSeisanComp(e.getAnkenStatus());

		if (TransitionType.ANKEN == transitionType) {
			// ------------------------------------------------------
			// 案件管理から遷移する場合
			// ------------------------------------------------------

			// 遷移元案件IDを設定
			hoshuMeisaiEditForm.setTransitionAnkenId(transitionAnkenId);

			// 報酬明細Formに報酬明細一覧を設定する
			this.setHoshuMeisaiListForAnken(hoshuMeisaiEditForm, customerId, seisanCompDispFlg);

			// 案件に紐づく顧客のリストを取得します。
			List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByAnkenId(transitionAnkenId);
			int notCompletedAnkenCount = Long.valueOf(tAnkenCustomerEntities.stream().filter(nonCompAnkenFilter).count()).intValue();
			hoshuMeisaiEditForm.setNotCompletedAnkenCount(notCompletedAnkenCount);

		} else if (TransitionType.CUSTOMER == transitionType) {
			// ===========================================
			// 顧客管理から遷移する場合
			// ===========================================

			// 遷移元顧客IDを設定
			hoshuMeisaiEditForm.setTransitionCustomerId(transitionCustomerId);

			// 精算済データを表示にする場合
			this.setHoshuMeisaiListForCustomer(hoshuMeisaiEditForm, ankenId, seisanCompDispFlg);

			// 顧客に紐づく案件のリストを取得します。
			List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByCustomerId(transitionCustomerId);
			int notCompletedAnkenCount = Long.valueOf(tAnkenCustomerEntities.stream().filter(nonCompAnkenFilter).count()).intValue();
			hoshuMeisaiEditForm.setNotCompletedAnkenCount(notCompletedAnkenCount);

		} else {
			// ここには来ない想定
			throw new RuntimeException();
		}

		hoshuMeisaiEditForm.setSeisanCompDispFlg(seisanCompDispFlg);
		hoshuMeisaiEditForm.setSelectedAnkenId(ankenId);
		hoshuMeisaiEditForm.setSelectedCustomerId(customerId);

		return hoshuMeisaiEditForm;
	}

	// ****************************************************************
	// 会計記録
	// ****************************************************************
	/**
	 * 初期データを設定します。<br>
	 *
	 * <pre>
	 * 入出金情報の新規登録用です。
	 * </pre>
	 *
	 * @param transitionFlg 遷移元判定フラグ
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @return 画面表示情報
	 */
	public NyushukkinEditForm createNyushukkinEditForm(String transitionFlg, Long transitionAnkenId, Long transitionCustomerId) {

		// 案件管理から遷移するかどうか
		boolean isTransitionFromAnken = SystemFlg.FLG_OFF.getCd().equals(transitionFlg);

		NyushukkinEditForm nyushukkinEditForm = new NyushukkinEditForm();
		KaikeiKirokuDto kaikeiKirokuDto = new KaikeiKirokuDto();

		// 案件・顧客情報
		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();

		// ***********************************************************
		// 入金項目の取得
		// ***********************************************************
		List<MNyushukkinKomokuEntity> nyukinKomokuEntityList = mNyushukkinKomokuDao.selectByDeletableByNyushukkinType(NyushukkinType.NYUKIN.getCd());
		// Entity -> Dto
		List<NyushukkinKomokuListDto> nyukinKomokuList = this.setNyushukkinKomokuDtoForNew(nyukinKomokuEntityList);

		// ***********************************************************
		// 出金項目の取得
		// ***********************************************************
		List<MNyushukkinKomokuEntity> shukkinKomokuEntityList = mNyushukkinKomokuDao.selectByDeletableByNyushukkinType(NyushukkinType.SHUKKIN.getCd());
		// Entity -> Dto
		List<NyushukkinKomokuListDto> shukkinKomokuList = this.setNyushukkinKomokuDtoForNew(shukkinKomokuEntityList);

		// ***********************************************************
		// その他、必要な情報を取得します。
		// ***********************************************************
		if (isTransitionFromAnken) {
			// --------------------------------------------------------------
			// 案件管理から遷移する場合
			// --------------------------------------------------------------
			Predicate<KaikeiManagementRelationDto> nonConpletedMapper = dto -> !AnkenStatus.isSeisanComp(dto.getAnkenStatusCd());
			relationDtoList = this.getRelationListForAnken(transitionAnkenId).stream().filter(nonConpletedMapper).collect(Collectors.toList());

		} else {
			// --------------------------------------------------------------
			// 顧客管理から遷移する場合
			// --------------------------------------------------------------
			Predicate<KaikeiManagementRelationDto> nonConpletedMapper = dto -> !AnkenStatus.isSeisanComp(dto.getAnkenStatusCd());
			relationDtoList = this.getRelationListForCustomer(transitionCustomerId).stream().filter(nonConpletedMapper).collect(Collectors.toList());
		}

		// ***********************************************************
		// デフォルト値を設定します。
		// ***********************************************************
		// 課税、非課税
		if (!ListUtils.isEmpty(nyukinKomokuList)) {
			nyushukkinEditForm.setTaxation(SystemFlg.FLG_ON.getCd().equals(nyukinKomokuList.get(0).getTaxFlg()));
			kaikeiKirokuDto.setTaxFlg(nyukinKomokuList.get(0).getTaxFlg());
			kaikeiKirokuDto.setTaxText(DefaultEnum.getVal(DefaultEnum.getEnum(TaxFlg.class, nyukinKomokuList.get(0).getTaxFlg())));
		}

		// 発生日
		LocalDate now = LocalDate.now();
		kaikeiKirokuDto.setHasseiDate(DateUtils.parseToString(now, DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		// 前回登録した情報を復元する
		TKaikeiKirokuEntity prevKaikeiKirokuEntity = this.getItemFromPrev(transitionAnkenId, transitionCustomerId);

		if (prevKaikeiKirokuEntity != null) {
			// 入出金項目
			kaikeiKirokuDto.setNyushukkinType(prevKaikeiKirokuEntity.getNyushukkinType());
		} else {
			// 入出金項目(デフォルト値を入力)
			kaikeiKirokuDto.setNyushukkinType(CommonConstant.NyushukkinType.NYUKIN.getCd());
		}

		if (transitionAnkenId != null) {
			// 顧客ID(顧客IDの復元)
			Function<KaikeiManagementRelationDto, Long> dtoMapper = entity -> entity.getCustomerId().asLong();
			Long customerId = this.getKaikeiKirokuRelateAnkenCustomerOptionParam(prevKaikeiKirokuEntity, relationDtoList, TKaikeiKirokuEntity::getCustomerId, dtoMapper);
			kaikeiKirokuDto.setCustomerId(customerId);
		} else {
			// 案件ID(案件IDの復元)
			Function<KaikeiManagementRelationDto, Long> dtoMapper = entity -> entity.getAnkenId().asLong();
			Long ankenId = this.getKaikeiKirokuRelateAnkenCustomerOptionParam(prevKaikeiKirokuEntity, relationDtoList, TKaikeiKirokuEntity::getAnkenId, dtoMapper);
			kaikeiKirokuDto.setAnkenId(ankenId);
		}

		// ***********************************************************
		// Formに設定します。
		// ***********************************************************
		nyushukkinEditForm.setTransitionCustomerId(transitionCustomerId);
		nyushukkinEditForm.setTransitionAnkenId(transitionAnkenId);
		nyushukkinEditForm.setNyukinKomokuList(nyukinKomokuList);
		nyushukkinEditForm.setShukkinKomokuList(shukkinKomokuList);
		nyushukkinEditForm.setKaikeiManagementList(relationDtoList);
		nyushukkinEditForm.setKaikeiKirokuDto(kaikeiKirokuDto);

		return nyushukkinEditForm;
	}

	/**
	 * 会計画面モーダルの顧客(案件)プルダウンの初期設定するIDを取得する
	 * 
	 * @param prevEntity(null考慮)
	 * @param relateList
	 * @param entityMapper
	 * @param dtoMapper
	 * @returns
	 */
	private Long getKaikeiKirokuRelateAnkenCustomerOptionParam(
			TKaikeiKirokuEntity prevEntity,
			List<KaikeiManagementRelationDto> relateList,
			Function<TKaikeiKirokuEntity, Long> entityMapper,
			Function<KaikeiManagementRelationDto, Long> dtoMapper) {

		// 有効な顧客で絞り込み
		List<KaikeiManagementRelationDto> validityList = relateList.stream().filter(KaikeiManagementRelationDto::isCanKaikeiCreate).collect(Collectors.toList());
		if (prevEntity != null && validityList.stream().anyMatch(e -> Objects.equals(dtoMapper.apply(e), entityMapper.apply(prevEntity)))) {
			// 有効な顧客の中に存在する場合
			return entityMapper.apply(prevEntity);
		}
		if (validityList != null && validityList.size() == 1) {
			// 一件のみなら、それを設定
			return validityList.stream().findFirst().map(dtoMapper).orElse(null);
		} else {
			// 複数 or なにもない場合、nullを返却
			return null;
		}
	}

	/**
	 * 初期データを設定します。<br>
	 *
	 * <pre>
	 * 入出金情報の更新用です。
	 * </pre>
	 *
	 * @param kaikeiKirokuSeq 会計記録SEQ
	 * @param transitionFlg 遷移元判定フラグ
	 * @param transitionAnkenId 遷移元案件ID
	 * @param transitionCustomerId 遷移元顧客ID
	 * @return 画面表示情報
	 * @throws AppException
	 */
	public NyushukkinEditForm setInitDataForNyushukkin(Long kaikeiKirokuSeq, String transitionFlg, Long transitionAnkenId,
			Long transitionCustomerId) throws AppException {

		if (kaikeiKirokuSeq == null) {
			// 会計記録SEQがnull = 新規登録とみなします。
			return this.createNyushukkinEditForm(transitionFlg, transitionAnkenId, transitionCustomerId);
		}

		NyushukkinEditForm nyushukkinEditForm = new NyushukkinEditForm();
		// 更新フラグをtrueに設定
		nyushukkinEditForm.setUpdateFlg(true);
		// 案件管理から遷移する場合かどうか
		boolean isTransitionFromAnken = SystemFlg.FLG_OFF.getCd().equals(transitionFlg);

		// --------------------------------------------------------------
		// 案件明細情報の取得
		// --------------------------------------------------------------
		TKaikeiKirokuEntity kaikeiKirokuEntity = tKaikeiKirokuDao.selectByKaikeiKirokuSeq(kaikeiKirokuSeq);

		if (kaikeiKirokuEntity == null) {
			// 排他エラーとして例外処理をします。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 精算完了かを取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(kaikeiKirokuEntity.getAnkenId(), kaikeiKirokuEntity.getCustomerId());

		// 案件・顧客情報
		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();

		// ***********************************************************
		// 入出金情報(会計記録情報)の取得
		// ***********************************************************
		KaikeiKirokuBean kaikeiKirokuBean = tKaikeiKirokuDao.selectNyushukkinByKaikeiKirokuSeq(kaikeiKirokuSeq);

		if (kaikeiKirokuBean == null) {
			// 排他エラーチェックをします。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// Bean -> Dto
		KaikeiKirokuDto kaikeiKirokuDto = this.setKaikeiKirokuDto(kaikeiKirokuBean);

		// ***********************************************************
		// 入出金項目リストの取得
		// ***********************************************************
		Long nyushukkinKomokuId = kaikeiKirokuDto.getNyushukkinKomokuId();

		// 入金項目
		List<NyushukkinKomokuListDto> nyukinKomokuList = this.getNyushukkinKomokuList(NyushukkinType.NYUKIN.getCd(), nyushukkinKomokuId);

		// 出金項目
		List<NyushukkinKomokuListDto> shukkinKomokuList = this.getNyushukkinKomokuList(NyushukkinType.SHUKKIN.getCd(), nyushukkinKomokuId);

		// ***********************************************************
		// その他、必要な情報を取得します。
		// ***********************************************************
		if (isTransitionFromAnken) {
			// --------------------------------------------------------------
			// 案件管理から遷移する場合
			// --------------------------------------------------------------
			// 案件・顧客情報
			relationDtoList = this.getRelationListForAnken(transitionAnkenId);

		} else {
			// --------------------------------------------------------------
			// 顧客管理から遷移する場合
			// --------------------------------------------------------------
			// 案件・顧客情報
			relationDtoList = this.getRelationListForCustomer(transitionCustomerId);
		}

		// ***********************************************************
		// FormにDtoを設定します。
		// ***********************************************************
		if (isTransitionFromAnken) {
			nyushukkinEditForm.setTransitionAnkenId(transitionAnkenId);

		} else {
			nyushukkinEditForm.setTransitionCustomerId(transitionCustomerId);
		}

		// 精算書seqが存在した場合（清算書発行済である場合）変更させない
		if (kaikeiKirokuDto.getSeisanSeq() != null) {
			nyushukkinEditForm.setDisabledFlg(true);
			// 削除可能かの判断
			if (NyushukkinKomokuType.SEIKYU.getCd().equals(kaikeiKirokuBean.getNyushukkinKomokuId().toString())
					|| NyushukkinKomokuType.CANNOT_KAISHU.getCd().equals(kaikeiKirokuBean.getNyushukkinKomokuId().toString())
					|| NyushukkinKomokuType.HENKIN.getCd().equals(kaikeiKirokuBean.getNyushukkinKomokuId().toString())) {
				// ----------------------------------------------------
				// 精算完了済の場合は登録させない
				// ----------------------------------------------------
				boolean seisanCompFlg = this.seisanCompCheck(kaikeiKirokuDto.getAnkenId(), kaikeiKirokuDto.getCustomerId());
				if (seisanCompFlg) {
					nyushukkinEditForm.setAbleDeleteFlg(false);
				} else {
					nyushukkinEditForm.setAbleDeleteFlg(true);
				}
			} else {
				nyushukkinEditForm.setAbleDeleteFlg(false);
			}
		} else {
			// 入金プールの場合は更新削除させない
			if (NyushukkinKomokuType.NYUKIN_POOL.getCd().equals(kaikeiKirokuBean.getNyushukkinKomokuId().toString())) {
				nyushukkinEditForm.setDisabledFlg(true);
				nyushukkinEditForm.setAbleDeleteFlg(false);
			} else {
				nyushukkinEditForm.setAbleDeleteFlg(true);
			}
		}

		// 入出金予定が存在した場合入出金選択を変更させない
		if (kaikeiKirokuDto.getNyushukkinYoteiSeq() != null) {
			nyushukkinEditForm.setNyushukkinDisabledFlg(true);
		}

		String ankenStatus = tAnkenCustomerEntity.getAnkenStatus();
		
		// 案件ステータスによってDisabledを判定する
		if (AnkenStatus.isSeisanComp(ankenStatus)) {
			nyushukkinEditForm.setDisabledFlg(true);
			nyushukkinEditForm.setAbleDeleteFlg(false);
		}
		
		// 編集不可メッセージ表示フラグの設定（ステータスの状態によるもの）
		Consumer<Boolean> setShowAnkenCompletedMsg = bool -> nyushukkinEditForm.setShowAnkenCompletedMsg(bool);
		Consumer<Boolean> setShowSeisanCompletedMsg = bool -> nyushukkinEditForm.setShowSeisanCompletedMsg(bool);
		commonKaikeiService.setShowCantEditMsgFlg(tAnkenCustomerEntity.getKanryoDate(), ankenStatus, setShowAnkenCompletedMsg, setShowSeisanCompletedMsg);
		String nyushukkinKomokuIdStr = kaikeiKirokuBean.getNyushukkinKomokuId().toString();
		// 編集不可メッセージ表示フラグの設定（対象の会計データの状態によるもの）
		if (kaikeiKirokuBean.getSeisanSeq() == null) {
			// 会計記録の精算SEQがNULL（精算書に紐づけられていないもの）の場合
			// 他案件からの振替入金の場合
			if (NyushukkinKomokuType.NYUKIN_POOL.getCd().equals(nyushukkinKomokuIdStr)) {
				nyushukkinEditForm.setShowNyukinPoolMsg(true);
			}
		} else {
			// 会計記録の精算SEQがNULLじゃない（精算書に紐づけられているもの）の場合
			
			if (NyushukkinKomokuType.SHUKKIN_POOL.getCd().equals(nyushukkinKomokuIdStr)) {
				// 他案件への振替出金の場合
				nyushukkinEditForm.setShowShukkinPoolMsg(true);
			} else {
				nyushukkinEditForm.setShowUsedSeisanshoMsg(true);
			}
		}
		
		nyushukkinEditForm.setNyukinKomokuList(nyukinKomokuList);
		nyushukkinEditForm.setShukkinKomokuList(shukkinKomokuList);
		nyushukkinEditForm.setKaikeiManagementList(relationDtoList);
		nyushukkinEditForm.setKaikeiKirokuDto(kaikeiKirokuDto);

		return nyushukkinEditForm;
	}

	/**
	 * 入出金情報の新規登録を行います。
	 *
	 * @param nyushukkinEditForm 入出金情報の編集用フォーム
	 * @throws AppException
	 */
	public void saveNyushukkin(NyushukkinEditForm nyushukkinEditForm) throws AppException {

		// 入力した情報を取得します。
		KaikeiKirokuDto inputNyushukkinDto = nyushukkinEditForm.getKaikeiKirokuDto();

		// 遷移元のIDを設定します。
		if (SystemFlg.FLG_OFF.getCd().equals(nyushukkinEditForm.getTransitionFlg())) {
			// 案件管理から遷移する場合
			inputNyushukkinDto.setAnkenId(nyushukkinEditForm.getTransitionAnkenId());

		} else {
			// 顧客管理から遷移する場合
			inputNyushukkinDto.setCustomerId(nyushukkinEditForm.getTransitionCustomerId());
		}

		// 入出金情報を登録します。
		this.saveNyushukkin(inputNyushukkinDto);
	}

	/**
	 * 入出金情報の更新を行います。
	 *
	 * @param nyushukkinEditForm 入出金情報の編集用フォーム
	 * @throws AppException
	 */
	public void updateNyushukkin(NyushukkinEditForm nyushukkinEditForm) throws AppException {

		// 画面で入力した情報を取得します。
		KaikeiKirokuDto inputKaikeiKirokuDto = nyushukkinEditForm.getKaikeiKirokuDto();

		// 遷移元のIDを設定します。
		if (SystemFlg.FLG_OFF.getCd().equals(nyushukkinEditForm.getTransitionFlg())) {
			// 案件管理から遷移する場合
			inputKaikeiKirokuDto.setAnkenId(nyushukkinEditForm.getTransitionAnkenId());

		} else {
			// 顧客管理から遷移する場合
			inputKaikeiKirokuDto.setCustomerId(nyushukkinEditForm.getTransitionCustomerId());
		}

		// 入出金情報を更新します。
		this.updateNyushukkin(inputKaikeiKirokuDto);
	}

	/**
	 * 入出金情報を削除します。
	 *
	 * @param nyushukkinEditForm 入出金情報の編集用フォーム
	 * @throws AppException
	 */
	public String deleteNyushukkin(NyushukkinEditForm nyushukkinEditForm) throws AppException {

		// 返却するメッセージキーを定義
		String resultMessageKey = "";
		// ---------------------------------------------------------
		// 必要な情報の取得
		// ---------------------------------------------------------
		// 削除対象の情報を取得します。
		KaikeiKirokuDto targetKaikeiKirokuDto = nyushukkinEditForm.getKaikeiKirokuDto();

		// 会計記録SEQに紐づく入出金情報を取得します。
		TKaikeiKirokuEntity kaikeiKirokuEntity = tKaikeiKirokuDao.selectByKaikeiKirokuSeq(targetKaikeiKirokuDto.getKaikeiKirokuSeq());
		if (kaikeiKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// ---------------------------------------------------------
		// 各情報の削除・更新処理
		// ---------------------------------------------------------
		try {
			// 入出金情報の削除
			tKaikeiKirokuDao.delete(kaikeiKirokuEntity);
		} catch (OptimisticLockException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}

		// 入出金情報の更新
		Long seisanSeq = kaikeiKirokuEntity.getSeisanSeq();
		if (seisanSeq != null) {
			this.updateNyushukkin(seisanSeq);
		}

		// 入出金予定情報の更新
		Long nyushukkinYoteiSeq = kaikeiKirokuEntity.getNyushukkinYoteiSeq();
		if (nyushukkinYoteiSeq != null) {
			this.updateYoteiForNyushukkin(nyushukkinYoteiSeq);
		}
		// 削除パターンによりメッセージを設定
		if (NyushukkinType.NYUKIN.getCd().equals(targetKaikeiKirokuDto.getNyushukkinType()) && nyushukkinYoteiSeq != null) {
			// 入金で入出金予定が存在する場合
			resultMessageKey = MessageEnum.MSG_I00017.getMessageKey();
		} else if (NyushukkinType.SHUKKIN.getCd().equals(targetKaikeiKirokuDto.getNyushukkinType()) && nyushukkinYoteiSeq != null) {
			// 出金で入出金予定が存在する場合
			resultMessageKey = MessageEnum.MSG_I00018.getMessageKey();
		} else {
			// 上記以外の場合
			resultMessageKey = MessageEnum.MSG_I00001.getMessageKey();
		}

		return resultMessageKey;
	}

	/**
	 * 入出金予定モーダルの初期情報設定
	 *
	 * @param viewForm
	 */
	public void setInitNyushukkinYoteiForm(NyushukkinYoteiEditForm viewForm) {

		// 初期データの設定

		NyushukkinYoteiEditDto nyushukkinYoteiEditDto = viewForm.getNyushukkinYoteiEditDto();
		nyushukkinYoteiEditDto.setAnkenId(viewForm.getTransitionAnkenId());
		nyushukkinYoteiEditDto.setCustomerId(viewForm.getTransitionCustomerId());

		TNyushukkinYoteiEntity prevNyukinYoteiEntity = this.getNyukinYoteiFromPrev(viewForm.getTransitionAnkenId(), viewForm.getTransitionCustomerId());
		if (prevNyukinYoteiEntity != null) {
			// 入出金項目
			nyushukkinYoteiEditDto.setNyushukkinType(prevNyukinYoteiEntity.getNyushukkinType());
			if (prevNyukinYoteiEntity.getNyukinShiharaishaType() != null) {
				// 支払者を指定する場合
				nyushukkinYoteiEditDto.setNyukinShiharaishaType(prevNyukinYoteiEntity.getNyukinShiharaishaType());
				nyushukkinYoteiEditDto.setNyukinShiharaishaKanyoshaSeq(prevNyukinYoteiEntity.getNyukinKanyoshaSeq());
				nyushukkinYoteiEditDto.setShukkinShiharaiSakiType(TargetType.CUSTOMER.getCd());
			} else {
				// 支払先を指定する場合
				nyushukkinYoteiEditDto.setShukkinShiharaiSakiType(prevNyukinYoteiEntity.getShukkinShiharaiSakiType());
				nyushukkinYoteiEditDto.setShukkinShiharaiSakiKanyoshaSeq(prevNyukinYoteiEntity.getShukkinKanyoshaSeq());
				nyushukkinYoteiEditDto.setNyukinShiharaishaType(TargetType.CUSTOMER.getCd());
			}
			if (prevNyukinYoteiEntity.getNyukinSakiKozaSeq() != null) {
				// 入金先口座の復元
				nyushukkinYoteiEditDto.setNyukinSakiKozaSeq(prevNyukinYoteiEntity.getNyukinSakiKozaSeq());
			} else {
				// 出金先口座の復元
				nyushukkinYoteiEditDto.setShukkinShiharaishaKozaSeq(prevNyukinYoteiEntity.getShukkinSakiKozaSeq());
			}
		} else {
			// 入出金項目(デフォルト値を入力)
			nyushukkinYoteiEditDto.setNyushukkinType(CommonConstant.NyushukkinType.NYUKIN.getCd());
			nyushukkinYoteiEditDto.setCustomerId(viewForm.getTransitionCustomerId());
			nyushukkinYoteiEditDto.setAnkenId(viewForm.getTransitionAnkenId());
			nyushukkinYoteiEditDto.setNyukinShiharaishaType(TargetType.CUSTOMER.getCd());
			nyushukkinYoteiEditDto.setShukkinShiharaiSakiType(TargetType.CUSTOMER.getCd());
		}

		// 案件・顧客情報

		// 現在の軸（顧客軸 or 案件軸）
		TransitionType transitionType = TransitionType.of(viewForm.getTransitionAnkenId(), viewForm.getTransitionCustomerId());

		// 口座リスト、関与者リスト
		List<GinkoKozaDto> ginkoKozaListForTenantAndAccount = new ArrayList<GinkoKozaDto>();
		List<CustomerKanyoshaPulldownDto> kanyoshaList = new ArrayList<CustomerKanyoshaPulldownDto>();

		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();

		if (TransitionType.ANKEN == transitionType) {
			// 顧客ID(顧客IDの復元)
			Predicate<KaikeiManagementRelationDto> nonConpletedMapper = dto -> !AnkenStatus.isSeisanComp(dto.getAnkenStatusCd());
			relationDtoList = this.getRelationListForAnken(viewForm.getTransitionAnkenId()).stream().filter(nonConpletedMapper).collect(Collectors.toList());

			Function<KaikeiManagementRelationDto, Long> dtoMapper = entity -> entity.getCustomerId().asLong();
			Long customerId = this.getNyushukkinYoteiRelateAnkenCustomerOptionParam(prevNyukinYoteiEntity, relationDtoList, TNyushukkinYoteiEntity::getCustomerId, dtoMapper);
			nyushukkinYoteiEditDto.setCustomerId(customerId);

			// 口座リスト、関与者リスト
			ginkoKozaListForTenantAndAccount = commonKaikeiService.getTenantAccountGinkoKozaList(viewForm.getTransitionAnkenId());
			kanyoshaList = tCustomerCommonDao.selectKanyoshaPulldownByAnkenId(viewForm.getTransitionAnkenId());

		} else if (TransitionType.CUSTOMER == transitionType) {
			// 案件ID(案件IDの復元)
			Predicate<KaikeiManagementRelationDto> nonConpletedMapper = dto -> !AnkenStatus.isSeisanComp(dto.getAnkenStatusCd());
			relationDtoList = this.getRelationListForCustomer(viewForm.getTransitionCustomerId()).stream().filter(nonConpletedMapper).collect(Collectors.toList());

			Function<KaikeiManagementRelationDto, Long> dtoMapper = entity -> entity.getAnkenId().asLong();
			Long ankenId = this.getNyushukkinYoteiRelateAnkenCustomerOptionParam(prevNyukinYoteiEntity, relationDtoList, TNyushukkinYoteiEntity::getAnkenId, dtoMapper);
			nyushukkinYoteiEditDto.setAnkenId(ankenId);

			// 口座リスト、関与者リスト
			if (ankenId != null) {
				ginkoKozaListForTenantAndAccount = commonKaikeiService.getTenantAccountGinkoKozaList(ankenId);
				kanyoshaList = tCustomerCommonDao.selectKanyoshaPulldownByAnkenId(ankenId);
			}

			// 前回登録された案件IDと今回自動設定した案件ID異なる場合
			if (prevNyukinYoteiEntity != null && !Objects.equals(ankenId, prevNyukinYoteiEntity.getAnkenId())) {
				// 案件に紐付いて選択肢を切り替えるものは不整合になってしまうので、NULLを設定
				nyushukkinYoteiEditDto.setNyukinShiharaishaType(TargetType.CUSTOMER.getCd());
				nyushukkinYoteiEditDto.setShukkinShiharaiSakiType(TargetType.CUSTOMER.getCd());
				nyushukkinYoteiEditDto.setShukkinShiharaiSakiKanyoshaSeq(null);// 関与者
				nyushukkinYoteiEditDto.setNyukinSakiKozaSeq(null); // 入金先口座SEQ
				nyushukkinYoteiEditDto.setShukkinShiharaishaKozaSeq(null);// 出金支払先口座SEQ
			}
		} else {
			// なにもしない
		}

		List<SelectOptionForm> nyukinKomokuList = commonSelectBoxService.getNyuShukkinKomokuSelectBoxFilterByType(NyushukkinType.NYUKIN);
		List<SelectOptionForm> shukkinKomokuList = commonSelectBoxService.getNyuShukkinKomokuSelectBoxFilterByType(NyushukkinType.SHUKKIN);

		// 取得したデータを設定する
		viewForm.setNyukinKomokuList(nyukinKomokuList);
		viewForm.setShukkinKomokuList(shukkinKomokuList);
		viewForm.setKaikeiManagementList(relationDtoList);
		viewForm.setTenantAccountGinkoKozaList(ginkoKozaListForTenantAndAccount);
		viewForm.setKanyoshaList(kanyoshaList);
	}

	/**
	 * 会計画面モーダルの顧客(案件)プルダウンの初期設定するIDを取得する
	 * 
	 * @param prevEntity(null考慮)
	 * @param relateList
	 * @param entityMapper
	 * @param dtoMapper
	 * @returns
	 */
	private Long getNyushukkinYoteiRelateAnkenCustomerOptionParam(
			TNyushukkinYoteiEntity prevEntity,
			List<KaikeiManagementRelationDto> relateList,
			Function<TNyushukkinYoteiEntity, Long> entityMapper,
			Function<KaikeiManagementRelationDto, Long> dtoMapper) {

		// 有効な顧客で絞り込み
		List<KaikeiManagementRelationDto> validityList = relateList.stream().filter(KaikeiManagementRelationDto::isCanKaikeiCreate).collect(Collectors.toList());
		if (prevEntity != null && validityList.stream().anyMatch(e -> Objects.equals(dtoMapper.apply(e), entityMapper.apply(prevEntity)))) {
			// 有効な顧客の中に存在する場合
			return entityMapper.apply(prevEntity);
		}
		if (validityList != null && validityList.size() == 1) {
			// 一件のみなら、それを設定
			return validityList.stream().findFirst().map(dtoMapper).orElse(null);
		} else {
			// 複数 or なにもない場合、nullを返却
			return null;
		}
	}

	/**
	 * 入出金予定モーダルの編集情報設定
	 *
	 * @param viewForm
	 */
	public void setEditNyushukkinYoteiForm(NyushukkinYoteiEditForm viewForm) throws AppException {

		Map<Long, String> accountNameMap = commonAccountiService.getAccountNameMap();
		NyushukkinYoteiEditDto nyushukkinYoteiEditDto = viewForm.getNyushukkinYoteiEditDto();
		Long nyushukkinYoteiSeq = nyushukkinYoteiEditDto.getNyushukkinYoteiSeq();

		// 新規作成のため、何もせずに返却
		if (nyushukkinYoteiSeq == null) {
			return;
		}

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		if (tNyushukkinYoteiEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		nyushukkinYoteiEditDto.setNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		nyushukkinYoteiEditDto.setCustomerId(tNyushukkinYoteiEntity.getCustomerId());
		nyushukkinYoteiEditDto.setAnkenId(tNyushukkinYoteiEntity.getAnkenId());
		nyushukkinYoteiEditDto.setNyushukkinType(tNyushukkinYoteiEntity.getNyushukkinType());
		nyushukkinYoteiEditDto.setNyushukkinYoteiDate(DateUtils.parseToString(tNyushukkinYoteiEntity.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		nyushukkinYoteiEditDto.setNyushukkinYoteiGaku(LoiozNumberUtils.parseAsString(tNyushukkinYoteiEntity.getNyushukkinYoteiGaku()));
		nyushukkinYoteiEditDto.setDispNyushukkinYoteiGaku(commonKaikeiService.toDispAmountLabel(tNyushukkinYoteiEntity.getNyushukkinYoteiGaku()));
		nyushukkinYoteiEditDto.setNyushukkinDate(DateUtils.parseToString(tNyushukkinYoteiEntity.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		nyushukkinYoteiEditDto.setNyushukkinGaku(LoiozNumberUtils.parseAsString(tNyushukkinYoteiEntity.getNyushukkinGaku()));
		nyushukkinYoteiEditDto.setDispNyushukkinGaku(commonKaikeiService.toDispAmountLabel(tNyushukkinYoteiEntity.getNyushukkinGaku()));
		nyushukkinYoteiEditDto.setTekiyo(tNyushukkinYoteiEntity.getTekiyo());
		nyushukkinYoteiEditDto.setSeisanSeq(tNyushukkinYoteiEntity.getSeisanSeq());
		nyushukkinYoteiEditDto.setDispCreatedAt(DateUtils.parseToString(tNyushukkinYoteiEntity.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		nyushukkinYoteiEditDto.setDispCreatedBy(accountNameMap.get(tNyushukkinYoteiEntity.getCreatedBy()));
		nyushukkinYoteiEditDto.setDispLastEditAt(DateUtils.parseToString(tNyushukkinYoteiEntity.getLastEditAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		nyushukkinYoteiEditDto.setDispLastEditBy(accountNameMap.get(tNyushukkinYoteiEntity.getLastEditBy()));

		TransitionType transitionType = TransitionType.of(viewForm.getTransitionAnkenId(), viewForm.getTransitionCustomerId());
		if (TransitionType.ANKEN == transitionType) {
			viewForm.setKaikeiManagementList(this.getRelationListForAnken(tNyushukkinYoteiEntity.getAnkenId()));
		} else if (TransitionType.CUSTOMER == transitionType) {
			viewForm.setKaikeiManagementList(this.getRelationListForCustomer(tNyushukkinYoteiEntity.getCustomerId()));
			viewForm.setTenantAccountGinkoKozaList(commonKaikeiService.getTenantAccountGinkoKozaList(tNyushukkinYoteiEntity.getAnkenId()));
			viewForm.setKanyoshaList(tCustomerCommonDao.selectKanyoshaPulldownByAnkenId(tNyushukkinYoteiEntity.getAnkenId()));
		} else {
			// 何もしない
		}

		if (NyushukkinType.SHUKKIN.equalsByCode(tNyushukkinYoteiEntity.getNyushukkinType())) {
			nyushukkinYoteiEditDto.setShukkinKomokuId(tNyushukkinYoteiEntity.getNyushukkinKomokuId());
			nyushukkinYoteiEditDto.setShukkinShiharaiSakiType(tNyushukkinYoteiEntity.getShukkinShiharaiSakiType());
			nyushukkinYoteiEditDto.setShukkinShiharaishaKozaSeq(tNyushukkinYoteiEntity.getShukkinSakiKozaSeq());
			if (TargetType.KANYOSHA.equalsByCode(tNyushukkinYoteiEntity.getShukkinShiharaiSakiType())) {
				nyushukkinYoteiEditDto.setShukkinShiharaiSakiKanyoshaSeq(tNyushukkinYoteiEntity.getShukkinKanyoshaSeq());
			} else {
				// なにもしない
			}

			// 使用中の出金項目IDはプルダウンに追加する
			List<SelectOptionForm> nyukinKomokuList = viewForm.getShukkinKomokuList();
			if (!nyukinKomokuList.stream().anyMatch(entity -> Objects.equals(entity.getValue(), tNyushukkinYoteiEntity.getNyushukkinKomokuId().toString()))) {
				MNyushukkinKomokuEntity mNyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(tNyushukkinYoteiEntity.getNyushukkinKomokuId());
				nyukinKomokuList.add(new SelectOptionForm(mNyushukkinKomokuEntity.getNyushukkinKomokuId(), mNyushukkinKomokuEntity.getKomokuName()));
			}

			// 使用中の口座はプルダウンに追加する
			List<GinkoKozaDto> tenantAccountGinkoKozaList = viewForm.getTenantAccountGinkoKozaList();
			commonKaikeiService.margeRelateKozaDto(tenantAccountGinkoKozaList, tNyushukkinYoteiEntity.getShukkinSakiKozaSeq());

		} else if (NyushukkinType.NYUKIN.equalsByCode(tNyushukkinYoteiEntity.getNyushukkinType())) {
			nyushukkinYoteiEditDto.setNyukinKomokuId(tNyushukkinYoteiEntity.getNyushukkinKomokuId());
			nyushukkinYoteiEditDto.setNyukinShiharaishaType(tNyushukkinYoteiEntity.getNyukinShiharaishaType());
			nyushukkinYoteiEditDto.setNyukinSakiKozaSeq(tNyushukkinYoteiEntity.getNyukinSakiKozaSeq());
			if (TargetType.KANYOSHA.equalsByCode(tNyushukkinYoteiEntity.getNyukinShiharaishaType())) {
				nyushukkinYoteiEditDto.setNyukinShiharaishaKanyoshaSeq(tNyushukkinYoteiEntity.getNyukinKanyoshaSeq());
			} else {
				// なにもしない
			}

			// 使用中の入金項目IDはプルダウンに追加する
			List<SelectOptionForm> nyukinKomokuList = viewForm.getNyukinKomokuList();
			if (!nyukinKomokuList.stream().anyMatch(entity -> Objects.equals(entity.getValue(), tNyushukkinYoteiEntity.getNyushukkinKomokuId().toString()))) {
				MNyushukkinKomokuEntity mNyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(tNyushukkinYoteiEntity.getNyushukkinKomokuId());
				nyukinKomokuList.add(new SelectOptionForm(mNyushukkinKomokuEntity.getNyushukkinKomokuId(), mNyushukkinKomokuEntity.getKomokuName()));
			}

			// 使用中の口座はプルダウンに追加する
			List<GinkoKozaDto> tenantAccountGinkoKozaList = viewForm.getTenantAccountGinkoKozaList();
			commonKaikeiService.margeRelateKozaDto(tenantAccountGinkoKozaList, tNyushukkinYoteiEntity.getNyukinSakiKozaSeq());

		} else {
			// 想定外のため、エラー
			throw new RuntimeException();
		}

		// 案件ステータスによってDisabledを判定する
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(tNyushukkinYoteiEntity.getAnkenId(), tNyushukkinYoteiEntity.getCustomerId());
		String ankenStatus = tAnkenCustomerEntity.getAnkenStatus();
		if (AnkenStatus.isSeisanComp(ankenStatus)) {
			viewForm.setDisabledFlg(true);
		}
		
		BigDecimal nyushukkinGaku = tNyushukkinYoteiEntity.getNyushukkinGaku();
		LocalDate nyushukkinDate = tNyushukkinYoteiEntity.getNyushukkinDate();
		
		// 入金額と入金日が入力されている場合、入出金予定を編集できないようにする -> 実費明細の削除を想定
		if (nyushukkinGaku != null && nyushukkinDate != null) {
			viewForm.setDisabledFlg(true);
		}
		
		// 編集不可メッセージ表示フラグの設定（ステータスの状態によるもの）
		Consumer<Boolean> setShowAnkenCompletedMsg = bool -> viewForm.setShowAnkenCompletedMsg(bool);
		Consumer<Boolean> setShowSeisanCompletedMsg = bool -> viewForm.setShowSeisanCompletedMsg(bool);
		commonKaikeiService.setShowCantEditMsgFlg(tAnkenCustomerEntity.getKanryoDate(), ankenStatus, setShowAnkenCompletedMsg, setShowSeisanCompletedMsg);
		// 編集不可メッセージ表示フラグの設定（対象の会計データの状態によるもの）
		if (nyushukkinGaku != null && nyushukkinDate != null) {
			// 実績情報が設定済みの場合
			viewForm.setShowYoteiCompletedMsg(true);
		}
		
		// 精算作成によって作成された出金予定かを判断する。
		if (tNyushukkinYoteiEntity.getSeisanSeq() != null) {
			viewForm.setSeisanFlg(true);
		}

		viewForm.setUpdateFlg(true);
	}

	/**
	 * 入金予定編集 DB整合性チェック
	 * 
	 * @param form
	 * @return 整合性チェックがNGの場合はエラーメッセージのMessageEnumを返却する。整合性チェックOKの場合はnullを返却。
	 */
	public MessageEnum nyushukkinEditFromNyukinAccessDBValidated(NyushukkinYoteiEditForm form) {

		NyushukkinYoteiEditDto dto = form.getNyushukkinYoteiEditDto();

		// 案件と関与者のリレーションを確認 (入金支払関与者)
		if (!commonAnkenValidator.isValidRelatingKanyosha(dto.getAnkenId(), dto.getNyukinShiharaishaKanyoshaSeq())) {
			return MessageEnum.MSG_E00025;
		}

		// 入金項目IDのデータ整合姓チェック
		if (!commonKaikeiValidator.isValidNyuShukkinKomokuEqulasType(dto.getNyukinKomokuId(), NyushukkinType.of(dto.getNyushukkinType()))) {
			return MessageEnum.MSG_E00025;
		}

		Long nyushukkinYoteiSeq = dto.getNyushukkinYoteiSeq();
		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		if (tNyushukkinYoteiEntity != null) {
			// 編集の場合

			// 入金口座の整合性
			Long nyukinSakiKozaSeq = tNyushukkinYoteiEntity.getNyukinSakiKozaSeq();
			boolean sameAsNowSavedKozaSeq = (nyukinSakiKozaSeq != null && nyukinSakiKozaSeq.equals(dto.getNyukinSakiKozaSeq()));
			if (sameAsNowSavedKozaSeq) {
				// 現在保存されている値の更新の場合はチェックを行わない（そのまま保存可能とする）
			} else {
				if (!commonKaikeiValidator.isValidRelatingUsersKozaByAnken(dto.getAnkenId(), dto.getNyukinSakiKozaSeq())) {
					return MessageEnum.MSG_E00025;
				}
			}

		} else {
			// 新規登録の場合

			// 入金口座の整合性
			if (!commonKaikeiValidator.isValidRelatingUsersKozaByAnken(dto.getAnkenId(), dto.getNyukinSakiKozaSeq())) {
				return MessageEnum.MSG_E00025;
			}
		}

		return null;
	}

	/**
	 * 入金予定の新規登録処理
	 *
	 * @param inputData
	 * @throws AppException
	 */
	public void registNyukinYotei(NyushukkinYoteiEditDto inputData) throws AppException {

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = new TNyushukkinYoteiEntity();
		tNyushukkinYoteiEntity.setNyushukkinYoteiSeq(inputData.getNyushukkinYoteiSeq());
		tNyushukkinYoteiEntity.setCustomerId(inputData.getCustomerId());
		tNyushukkinYoteiEntity.setAnkenId(inputData.getAnkenId());
		tNyushukkinYoteiEntity.setNyushukkinType(inputData.getNyushukkinType());
		tNyushukkinYoteiEntity.setNyushukkinKomokuId(inputData.getNyukinKomokuId());
		tNyushukkinYoteiEntity.setNyukinShiharaishaType(inputData.getNyukinShiharaishaType());
		if (TargetType.KANYOSHA.equalsByCode(inputData.getNyukinShiharaishaType())) {
			tNyushukkinYoteiEntity.setNyukinKanyoshaSeq(inputData.getNyukinShiharaishaKanyoshaSeq());
		} else if (TargetType.CUSTOMER.equalsByCode(inputData.getNyukinShiharaishaType())) {
			tNyushukkinYoteiEntity.setNyukinCustomerId(inputData.getCustomerId());
		} else {
			// 何もしない
		}
		tNyushukkinYoteiEntity.setNyukinSakiKozaSeq(inputData.getNyukinSakiKozaSeq());
		tNyushukkinYoteiEntity
				.setNyushukkinYoteiDate(DateUtils.parseToLocalDate(inputData.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tNyushukkinYoteiEntity.setNyushukkinYoteiGaku(LoiozNumberUtils.parseAsBigDecimal(inputData.getNyushukkinYoteiGaku()));
		tNyushukkinYoteiEntity.setNyushukkinDate(DateUtils.parseToLocalDate(inputData.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tNyushukkinYoteiEntity.setNyushukkinGaku(LoiozNumberUtils.parseAsBigDecimal(inputData.getNyushukkinGaku()));
		tNyushukkinYoteiEntity.setTekiyo(inputData.getTekiyo());

		try {
			// 入金データの作成
			tNyushukkinYoteiDao.insert(tNyushukkinYoteiEntity);
		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 実績が登録されていない場合、ここで返却
		if (tNyushukkinYoteiEntity.getNyushukkinDate() == null && tNyushukkinYoteiEntity.getNyushukkinGaku() == null) {
			return;
		}

		// 会計記録データの作成
		TKaikeiKirokuEntity tKaikeiKirokuEntity = new TKaikeiKirokuEntity();
		this.setKaikeiKirokuEntityForNyushukkinYotei(tKaikeiKirokuEntity, tNyushukkinYoteiEntity, true);

		try {
			// 会計記録データの作成
			tKaikeiKirokuDao.insert(tKaikeiKirokuEntity);
		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 入金予定の更新処理
	 *
	 * @param inputData
	 * @throws AppException
	 */
	public void updateNyukinYotei(NyushukkinYoteiEditDto inputData) throws AppException {

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(inputData.getNyushukkinYoteiSeq());
		if (tNyushukkinYoteiEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 紐づく会計記録を取得する
		TKaikeiKirokuEntity tKaikeiKirokuEntity = tKaikeiKirokuDao.selectByNyushukkinYoteiSeq(tNyushukkinYoteiEntity.getNyushukkinYoteiSeq());

		tNyushukkinYoteiEntity.setCustomerId(inputData.getCustomerId());
		tNyushukkinYoteiEntity.setAnkenId(inputData.getAnkenId());
		tNyushukkinYoteiEntity.setNyushukkinType(inputData.getNyushukkinType());
		tNyushukkinYoteiEntity.setNyushukkinKomokuId(inputData.getNyukinKomokuId());
		tNyushukkinYoteiEntity.setNyukinShiharaishaType(inputData.getNyukinShiharaishaType());
		if (TargetType.KANYOSHA.equalsByCode(inputData.getNyukinShiharaishaType())) {
			tNyushukkinYoteiEntity.setNyukinKanyoshaSeq(inputData.getNyukinShiharaishaKanyoshaSeq());
			tNyushukkinYoteiEntity.setNyukinCustomerId(null);
		} else if (TargetType.CUSTOMER.equalsByCode(inputData.getNyukinShiharaishaType())) {
			tNyushukkinYoteiEntity.setNyukinKanyoshaSeq(null);
			tNyushukkinYoteiEntity.setNyukinCustomerId(inputData.getCustomerId());
		} else {
			// 何もしない
		}
		tNyushukkinYoteiEntity.setNyukinSakiKozaSeq(inputData.getNyukinSakiKozaSeq());
		tNyushukkinYoteiEntity
				.setNyushukkinYoteiDate(DateUtils.parseToLocalDate(inputData.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tNyushukkinYoteiEntity.setNyushukkinYoteiGaku(LoiozNumberUtils.parseAsBigDecimal(inputData.getNyushukkinYoteiGaku()));
		tNyushukkinYoteiEntity.setNyushukkinDate(DateUtils.parseToLocalDate(inputData.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tNyushukkinYoteiEntity.setNyushukkinGaku(LoiozNumberUtils.parseAsBigDecimal(inputData.getNyushukkinGaku()));
		tNyushukkinYoteiEntity.setTekiyo(inputData.getTekiyo());
		tNyushukkinYoteiEntity.setLastEditAt(LocalDateTime.now());
		tNyushukkinYoteiEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());

		// 出金 -> 入金 の場合は、出金データをnullに設定
		tNyushukkinYoteiEntity.setShukkinShiharaiSakiType(null);
		tNyushukkinYoteiEntity.setShukkinKanyoshaSeq(null);
		tNyushukkinYoteiEntity.setShukkinCustomerId(null);
		tNyushukkinYoteiEntity.setShukkinSakiKozaSeq(null);

		try {
			// 入出金予定の更新
			tNyushukkinYoteiDao.update(tNyushukkinYoteiEntity);

			// 紐づく会計記録の更新処理
			this.updateKaikeiKirokuByNyushukkinYotei(tKaikeiKirokuEntity, tNyushukkinYoteiEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 入金予定の削除処理
	 *
	 * @param inputData
	 * @throws AppException
	 */
	public void deleteNyukinYotei(NyushukkinYoteiEditDto inputData) throws AppException {

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(inputData.getNyushukkinYoteiSeq());
		if (tNyushukkinYoteiEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入出金予定SEQに紐づく入金情報を取得します。
		TKaikeiKirokuEntity tKaikeiKirokuEntity = tKaikeiKirokuDao.selectByNyushukkinYoteiSeq(tNyushukkinYoteiEntity.getNyushukkinYoteiSeq());
		try {
			// 削除処理
			tNyushukkinYoteiDao.delete(tNyushukkinYoteiEntity);
			// 出金情報が取得できた場合、削除処理
			Optional.ofNullable(tKaikeiKirokuEntity).ifPresent(tKaikeiKirokuDao::delete);

		} catch (OptimisticLockException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * 出金予定編集 DB整合性チェック
	 * 
	 * @param form
	 * @return 整合性チェックがNGの場合はエラーメッセージのMessageEnumを返却する。整合性チェックOKの場合はnullを返却。
	 */
	public MessageEnum nyushukkinEditFromShukkinAccessDBValidated(NyushukkinYoteiEditForm form) {

		NyushukkinYoteiEditDto dto = form.getNyushukkinYoteiEditDto();

		// 案件と関与者のリレーションを確認 (出金支払関与者)
		if (!commonAnkenValidator.isValidRelatingKanyosha(dto.getAnkenId(), dto.getShukkinShiharaiSakiKanyoshaSeq())) {
			return MessageEnum.MSG_E00025;
		}

		// 出金項目IDのデータ整合姓チェック
		if (!commonKaikeiValidator.isValidNyuShukkinKomokuEqulasType(dto.getShukkinKomokuId(), NyushukkinType.of(dto.getNyushukkinType()))) {
			return MessageEnum.MSG_E00025;
		}

		Long nyushukkinYoteiSeq = dto.getNyushukkinYoteiSeq();
		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		if (tNyushukkinYoteiEntity != null) {
			// 編集の場合

			// 出金口座の整合性
			Long shukkinSakiKozaSeq = tNyushukkinYoteiEntity.getShukkinSakiKozaSeq();
			boolean sameAsNowSavedKozaSeq = (shukkinSakiKozaSeq != null && shukkinSakiKozaSeq.equals(dto.getShukkinShiharaishaKozaSeq()));
			if (sameAsNowSavedKozaSeq) {
				// 現在保存されている値の更新の場合はチェックを行わない（そのまま保存可能とする）
			} else {
				if (!commonKaikeiValidator.isValidRelatingUsersKozaByAnken(dto.getAnkenId(), dto.getShukkinShiharaishaKozaSeq())) {
					return MessageEnum.MSG_E00025;
				}
			}

		} else {
			// 新規登録の場合

			// 出金口座の整合性
			if (!commonKaikeiValidator.isValidRelatingUsersKozaByAnken(dto.getAnkenId(), dto.getShukkinShiharaishaKozaSeq())) {
				return MessageEnum.MSG_E00025;
			}
		}

		return null;
	}

	/**
	 * 出金予定の新規登録処理
	 *
	 * @param inputData
	 * @throws AppException
	 */
	public void registShukkinYotei(NyushukkinYoteiEditDto inputData) throws AppException {

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = new TNyushukkinYoteiEntity();
		tNyushukkinYoteiEntity.setNyushukkinYoteiSeq(inputData.getNyushukkinYoteiSeq());
		tNyushukkinYoteiEntity.setCustomerId(inputData.getCustomerId());
		tNyushukkinYoteiEntity.setAnkenId(inputData.getAnkenId());
		tNyushukkinYoteiEntity.setNyushukkinType(inputData.getNyushukkinType());
		tNyushukkinYoteiEntity.setNyushukkinKomokuId(inputData.getShukkinKomokuId());
		tNyushukkinYoteiEntity.setShukkinShiharaiSakiType(inputData.getShukkinShiharaiSakiType());
		if (TargetType.KANYOSHA.equalsByCode(inputData.getShukkinShiharaiSakiType())) {
			tNyushukkinYoteiEntity.setShukkinKanyoshaSeq(inputData.getShukkinShiharaiSakiKanyoshaSeq());
		} else if (TargetType.CUSTOMER.equalsByCode(inputData.getNyukinShiharaishaType())) {
			tNyushukkinYoteiEntity.setShukkinCustomerId(inputData.getCustomerId());
		} else {
			// 何もしない
		}
		tNyushukkinYoteiEntity.setShukkinSakiKozaSeq(inputData.getShukkinShiharaishaKozaSeq());
		tNyushukkinYoteiEntity
				.setNyushukkinYoteiDate(DateUtils.parseToLocalDate(inputData.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tNyushukkinYoteiEntity.setNyushukkinYoteiGaku(LoiozNumberUtils.parseAsBigDecimal(inputData.getNyushukkinYoteiGaku()));
		tNyushukkinYoteiEntity.setNyushukkinDate(DateUtils.parseToLocalDate(inputData.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tNyushukkinYoteiEntity.setNyushukkinGaku(LoiozNumberUtils.parseAsBigDecimal(inputData.getNyushukkinGaku()));
		tNyushukkinYoteiEntity.setTekiyo(inputData.getTekiyo());

		try {
			tNyushukkinYoteiDao.insert(tNyushukkinYoteiEntity);
		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 実績が登録されていない場合、ここで返却
		if (tNyushukkinYoteiEntity.getNyushukkinDate() == null && tNyushukkinYoteiEntity.getNyushukkinGaku() == null) {
			return;
		}

		// 会計記録の設定
		TKaikeiKirokuEntity tKaikeiKirokuEntity = new TKaikeiKirokuEntity();
		this.setKaikeiKirokuEntityForNyushukkinYotei(tKaikeiKirokuEntity, tNyushukkinYoteiEntity, true);

		try {
			// 会計記録の作成
			tKaikeiKirokuDao.insert(tKaikeiKirokuEntity);
		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 出金予定の更新処理
	 *
	 * @param inputData
	 * @throws AppException
	 */
	public void updateShukkinYotei(NyushukkinYoteiEditDto inputData) throws AppException {

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(inputData.getNyushukkinYoteiSeq());
		if (tNyushukkinYoteiEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 紐づく会計記録を取得する
		TKaikeiKirokuEntity tKaikeiKirokuEntity = tKaikeiKirokuDao.selectByNyushukkinYoteiSeq(tNyushukkinYoteiEntity.getNyushukkinYoteiSeq());

		tNyushukkinYoteiEntity.setCustomerId(inputData.getCustomerId());
		tNyushukkinYoteiEntity.setAnkenId(inputData.getAnkenId());
		tNyushukkinYoteiEntity.setNyushukkinType(inputData.getNyushukkinType());
		tNyushukkinYoteiEntity.setNyushukkinKomokuId(inputData.getShukkinKomokuId());
		tNyushukkinYoteiEntity.setShukkinShiharaiSakiType(inputData.getShukkinShiharaiSakiType());
		if (TargetType.KANYOSHA.equalsByCode(inputData.getShukkinShiharaiSakiType())) {
			tNyushukkinYoteiEntity.setShukkinCustomerId(null);
			tNyushukkinYoteiEntity.setShukkinKanyoshaSeq(inputData.getShukkinShiharaiSakiKanyoshaSeq());
		} else if (TargetType.CUSTOMER.equalsByCode(inputData.getNyukinShiharaishaType())) {
			tNyushukkinYoteiEntity.setShukkinKanyoshaSeq(null);
			tNyushukkinYoteiEntity.setShukkinCustomerId(inputData.getCustomerId());
		} else {
			// 何もしない
		}
		tNyushukkinYoteiEntity.setShukkinSakiKozaSeq(inputData.getShukkinShiharaishaKozaSeq());
		tNyushukkinYoteiEntity
				.setNyushukkinYoteiDate(DateUtils.parseToLocalDate(inputData.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tNyushukkinYoteiEntity.setNyushukkinYoteiGaku(LoiozNumberUtils.parseAsBigDecimal(inputData.getNyushukkinYoteiGaku()));
		tNyushukkinYoteiEntity.setNyushukkinDate(DateUtils.parseToLocalDate(inputData.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tNyushukkinYoteiEntity.setNyushukkinGaku(LoiozNumberUtils.parseAsBigDecimal(inputData.getNyushukkinGaku()));
		tNyushukkinYoteiEntity.setTekiyo(inputData.getTekiyo());
		tNyushukkinYoteiEntity.setLastEditAt(LocalDateTime.now());
		tNyushukkinYoteiEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());

		// 出金 -> 入金 の場合は、出金データをnullに設定
		tNyushukkinYoteiEntity.setNyukinShiharaishaType(null);
		tNyushukkinYoteiEntity.setNyukinCustomerId(null);
		tNyushukkinYoteiEntity.setNyukinKanyoshaSeq(null);
		tNyushukkinYoteiEntity.setNyukinSakiKozaSeq(null);

		try {
			// 入出金予定の更新
			tNyushukkinYoteiDao.update(tNyushukkinYoteiEntity);

			// 紐づく会計記録の更新処理
			this.updateKaikeiKirokuByNyushukkinYotei(tKaikeiKirokuEntity, tNyushukkinYoteiEntity);

			// 出金の場合のみ、紐付いている精算に紐づくデータすべてに精算処理日と登録する
			if (tNyushukkinYoteiEntity.getNyushukkinDate() != null && tNyushukkinYoteiEntity.getNyushukkinGaku() != null
					&& tNyushukkinYoteiEntity.getSeisanSeq() != null) {

				// 精算に紐づく && 会計記録がない場合は存在しない
				List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeisanSeq(tNyushukkinYoteiEntity.getSeisanSeq());
				tKaikeiKirokuEntities.forEach(entity -> entity.setSeisanDate(LocalDate.now()));
				tKaikeiKirokuDao.update(tKaikeiKirokuEntities);
			}

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 出金予定の削除処理
	 *
	 * @param inputData
	 * @throws AppException
	 */
	public void deleteShukkinYotei(NyushukkinYoteiEditDto inputData) throws AppException {

		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(inputData.getNyushukkinYoteiSeq());
		if (tNyushukkinYoteiEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入出金予定SEQに紐づく出金情報を取得します。
		TKaikeiKirokuEntity tKaikeiKirokuEntity = tKaikeiKirokuDao.selectByNyushukkinYoteiSeq(tNyushukkinYoteiEntity.getNyushukkinYoteiSeq());
		try {
			// 削除処理
			tNyushukkinYoteiDao.delete(tNyushukkinYoteiEntity);
			// 出金情報が取得できた場合、削除処理
			Optional.ofNullable(tKaikeiKirokuEntity).ifPresent(tKaikeiKirokuDao::delete);

		} catch (OptimisticLockException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}
	}

	/**
	 * 前回の情報から、入金項目と出金項目のいずれかを表示 ※報酬データは取得しない
	 * 
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return entity
	 */
	public TKaikeiKirokuEntity getItemFromPrev(Long ankenId, Long customerId) {
		return tKaikeiKirokuDao.selectPrevByAnkenIdOrCustomerId(ankenId, customerId);
	}

	/**
	 * 入出金予定から会計記録データを設定します
	 *
	 * @param tKaikeiKirokuEntity
	 * @param tNyushukkinYoteiEntity
	 * @param nyushukkinType
	 */
	private void setKaikeiKirokuEntityForNyushukkinYotei(TKaikeiKirokuEntity tKaikeiKirokuEntity, TNyushukkinYoteiEntity tNyushukkinYoteiEntity,
			boolean isRegist) {

		tKaikeiKirokuEntity.setCustomerId(tNyushukkinYoteiEntity.getCustomerId());
		tKaikeiKirokuEntity.setAnkenId(tNyushukkinYoteiEntity.getAnkenId());
		tKaikeiKirokuEntity.setNyushukkinType(tNyushukkinYoteiEntity.getNyushukkinType());
		tKaikeiKirokuEntity.setNyushukkinKomokuId(tNyushukkinYoteiEntity.getNyushukkinKomokuId());
		tKaikeiKirokuEntity.setHasseiDate(tNyushukkinYoteiEntity.getNyushukkinDate());
		tKaikeiKirokuEntity.setTekiyo(tNyushukkinYoteiEntity.getTekiyo());

		// 新規登録時のみ設定する
		if (isRegist) {
			tKaikeiKirokuEntity.setNyushukkinYoteiSeq(tNyushukkinYoteiEntity.getNyushukkinYoteiSeq());
			tKaikeiKirokuEntity.setSeisanSeq(tNyushukkinYoteiEntity.getSeisanSeq());
			tKaikeiKirokuEntity.setUncollectableFlg(SystemFlg.FLG_OFF.getCd());
		}

		if (NyushukkinType.NYUKIN.equalsByCode(tNyushukkinYoteiEntity.getNyushukkinType())) {
			tKaikeiKirokuEntity.setShukkinGaku(null);
			tKaikeiKirokuEntity.setNyukinGaku(tNyushukkinYoteiEntity.getNyushukkinGaku());
		} else if (NyushukkinType.SHUKKIN.equalsByCode(tNyushukkinYoteiEntity.getNyushukkinType())) {
			tKaikeiKirokuEntity.setShukkinGaku(tNyushukkinYoteiEntity.getNyushukkinGaku());
			tKaikeiKirokuEntity.setNyukinGaku(null);
		} else {
			// 許容しないケース
			throw new RuntimeException("引数の値がnullは許容しない");
		}

		// --------------------------------------------------
		// 消費税率を特定します。
		// --------------------------------------------------
		MNyushukkinKomokuEntity nyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(tKaikeiKirokuEntity.getNyushukkinKomokuId());
		if (nyushukkinKomokuEntity != null) {

			tKaikeiKirokuEntity.setTaxFlg(nyushukkinKomokuEntity.getTaxFlg());

			if (!TaxFlg.FREE_TAX.equalsByCode(nyushukkinKomokuEntity.getTaxFlg())) {
				// 課税の場合は消費税率を特定します。
				String taxRate = this.getTaxRate(tNyushukkinYoteiEntity.getAnkenId(), tNyushukkinYoteiEntity.getCustomerId(),
						tKaikeiKirokuEntity.getHasseiDate());
				tKaikeiKirokuEntity.setTaxRate(taxRate);

				// ※外税、内税に関わらず、税額の設定は行わない
			}
		}

	}

	/**
	 * 会計記録の登録・更新・削除処理
	 *
	 * @param tKaikeiKirokuEntity
	 * @param tNyushukkinYoteiEntity
	 */
	private void updateKaikeiKirokuByNyushukkinYotei(TKaikeiKirokuEntity tKaikeiKirokuEntity, TNyushukkinYoteiEntity tNyushukkinYoteiEntity) {

		if (tKaikeiKirokuEntity == null) {
			if (tNyushukkinYoteiEntity.getNyushukkinDate() != null && tNyushukkinYoteiEntity.getNyushukkinGaku() != null) {
				// 会計記録が登録されていない && 入金日と入金額が登録されているデータへの更新 -> 会計記録を作成する
				tKaikeiKirokuEntity = new TKaikeiKirokuEntity();
				this.setKaikeiKirokuEntityForNyushukkinYotei(tKaikeiKirokuEntity, tNyushukkinYoteiEntity, true);
				tKaikeiKirokuDao.insert(tKaikeiKirokuEntity);
				return;
			} else {
				// なにもしない
				return;
			}
		}

		if (tNyushukkinYoteiEntity.getNyushukkinDate() == null || tNyushukkinYoteiEntity.getNyushukkinGaku() == null) {
			// 会計記録が登録されている && 入金日と入金額が登録されていないデータへの更新 -> 会計記録を削除する
			tKaikeiKirokuDao.delete(tKaikeiKirokuEntity);
		} else {
			// 会計記録が登録されている && 入金日と入金額が登録されているデータ場合 -> 会計記録を更新する
			this.setKaikeiKirokuEntityForNyushukkinYotei(tKaikeiKirokuEntity, tNyushukkinYoteiEntity, false);
			tKaikeiKirokuDao.update(tKaikeiKirokuEntity);
		}

	}

	// ****************************************************************
	// 支払計画
	// ****************************************************************
	/**
	 * 支払計画 画面表示情報の設定
	 *
	 * @param seisanSeq
	 * @param form
	 * @throws AppException
	 */
	public void setInitDataForShiharaiPlan(Long seisanSeq, ShiharaiPlanEditForm form) throws AppException {

		// 精算記録の取得
		TSeisanKirokuEntity seisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		// 排他エラーチェックをします。
		if (seisanKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// Entity -> Dto
		ShiharaiPlanDto shiharaiPlanDto = new ShiharaiPlanDto();
		shiharaiPlanDto.setSeisanSeq(seisanKirokuEntity.getSeisanSeq());
		shiharaiPlanDto.setSeikyuType(seisanKirokuEntity.getSeikyuType());
		if (SeikyuType.IKKATSU.equalsByCode(seisanKirokuEntity.getSeikyuType())) {
			shiharaiPlanDto.setShiharaiDate(DateUtils.parseToString(seisanKirokuEntity.getShiharaiDt(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		} else if (SeikyuType.BUNKATSU.equalsByCode(seisanKirokuEntity.getSeikyuType())) {
			shiharaiPlanDto.setHasu(seisanKirokuEntity.getHasu());
			shiharaiPlanDto.setMonthShiharaiGaku(LoiozNumberUtils.parseAsString(seisanKirokuEntity.getMonthShiharaiGaku()));
			shiharaiPlanDto.setDispMonthShiharaiGaku(commonKaikeiService.toDispAmountLabel(seisanKirokuEntity.getMonthShiharaiGaku()));
			shiharaiPlanDto.setShiharaiYear(String.valueOf(seisanKirokuEntity.getShiharaiStartDt().getYear()));
			shiharaiPlanDto.setShiharaiMonth(String.valueOf(seisanKirokuEntity.getShiharaiStartDt().getMonthValue()));
			shiharaiPlanDto.setShiharaiDay(seisanKirokuEntity.getMonthShiharaiDate());
			if (DateUtils.END_OF_MONTH.equals(seisanKirokuEntity.getMonthShiharaiDate())) {
				shiharaiPlanDto.setShiharaiDayType(SeisanShiharaiMonthDay.LASTDAY.getCd());
			} else {
				shiharaiPlanDto.setShiharaiDayType(SeisanShiharaiMonthDay.DESIGNATEDDAY.getCd());
			}
		} else {
			// 想定外のケース
			throw new RuntimeException();
		}

		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);
		List<TNyushukkinYoteiEntity> nyukinList = tNyushukkinYoteiEntities.stream()
				.filter(entity -> NyushukkinType.NYUKIN.equalsByCode(entity.getNyushukkinType()))
				.collect(Collectors.toList());

		BigDecimal yoteiJissekiTotal = commonKaikeiService.calcTotal(
				nyukinList.stream()
						.map(entity -> {
							if (entity.getNyushukkinGaku() != null) {
								return entity.getNyushukkinGaku();
							} else {
								return entity.getNyushukkinYoteiGaku();
							}
						}).collect(Collectors.toList()));
		BigDecimal jissekiTotal = commonKaikeiService.calcTotal(
				nyukinList.stream()
						.filter(entity -> entity.getNyushukkinGaku() != null)
						.map(entity -> entity.getNyushukkinGaku())
						.collect(Collectors.toList()));
		BigDecimal zankin = seisanKirokuEntity.getSeisanGaku().subtract(jissekiTotal);
		BigDecimal sagaku = yoteiJissekiTotal.subtract(seisanKirokuEntity.getSeisanGaku());

		// 表示額の計算
		shiharaiPlanDto.setSeikyuGaku(commonKaikeiService.toDispAmountLabel(seisanKirokuEntity.getSeisanGaku()));
		shiharaiPlanDto.setNyukinGaku(commonKaikeiService.toDispAmountLabel(jissekiTotal));
		shiharaiPlanDto.setZankin(commonKaikeiService.toDispAmountLabel(zankin));
		shiharaiPlanDto.setSagaku(commonKaikeiService.toDispAmountLabel(sagaku));
		shiharaiPlanDto.setPlanComplete(BigDecimal.ZERO.compareTo(zankin) == 0);
		shiharaiPlanDto.setExpected(BigDecimal.ZERO.compareTo(sagaku) == 0);

		// 一覧表示用
		List<ShiharaiPlanYotei> shiharaiPlanYoteiList = tNyushukkinYoteiEntities.stream()
				.map(entity -> convert2ShiharaiPlanYotei(entity))
				.collect(Collectors.toList());

		
		// 精算完了（精算完了日か、完了チェックのどちらかが設定されている）か
		form.setSeisanCompleted(this.seisanCompCheck(seisanKirokuEntity.getAnkenId(), seisanKirokuEntity.getCustomerId()));
		
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(seisanKirokuEntity.getAnkenId(), seisanKirokuEntity.getCustomerId());
		String ankenStatus = tAnkenCustomerEntity.getAnkenStatus();
		
		// 編集不可メッセージ表示フラグの設定（ステータスの状態によるもの）
		Consumer<Boolean> setShowAnkenCompletedMsg = bool -> form.setShowAnkenCompletedMsg(bool);
		Consumer<Boolean> setShowSeisanCompletedMsg = bool -> form.setShowSeisanCompletedMsg(bool);
		commonKaikeiService.setShowCantEditMsgFlg(tAnkenCustomerEntity.getKanryoDate(), ankenStatus, setShowAnkenCompletedMsg, setShowSeisanCompletedMsg);
		// 編集不可メッセージ表示フラグの設定（対象の会計データの状態によるもの）
		if (shiharaiPlanDto.isPlanComplete()) {
			// 支払計画が完了している場合（支払計画の全ての入金予定に実績が入り、残金が0）場合
			form.setShowPlanCompletedMsg(true);
		}
		
		form.setShiharaiPlanDto(shiharaiPlanDto);
		form.setShiharaiPlanYoteiList(shiharaiPlanYoteiList);
	}

	/**
	 * 入出金予定Entity -> ShiharaiPlanYoteiに変換する
	 *
	 * @param tNyushukkinYoteiEntity
	 * @return
	 */
	private ShiharaiPlanYotei convert2ShiharaiPlanYotei(TNyushukkinYoteiEntity tNyushukkinYoteiEntity) {
		ShiharaiPlanYotei shiharaiPlanYotei = ShiharaiPlanYotei.builder()
				.nyushukkinYoteiSeq(tNyushukkinYoteiEntity.getNyushukkinYoteiSeq())
				.nyushukkinYoteiDate(DateUtils.parseToString(tNyushukkinYoteiEntity.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
				.nyushukkinYoteiGaku(LoiozNumberUtils.parseAsString(tNyushukkinYoteiEntity.getNyushukkinYoteiGaku()))
				.dispNyushukkinYoteiGaku(commonKaikeiService.toDispAmountLabel(tNyushukkinYoteiEntity.getNyushukkinYoteiGaku()))
				.nyushukkinDate(DateUtils.parseToString(tNyushukkinYoteiEntity.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
				.nyushukkinGaku(LoiozNumberUtils.parseAsString(tNyushukkinYoteiEntity.getNyushukkinGaku()))
				.dispNyushukkinGaku(commonKaikeiService.toDispAmountLabel(tNyushukkinYoteiEntity.getNyushukkinGaku()))
				.tekiyo(tNyushukkinYoteiEntity.getTekiyo())
				.isCompleted(tNyushukkinYoteiEntity.getNyushukkinGaku() != null)
				.build();
		return shiharaiPlanYotei;
	}

	/**
	 * 支払計画 再計算処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public void shiharaiPlanReCalc(ShiharaiPlanEditForm form) throws AppException {

		ShiharaiPlanDto shiharaiPlanDto = form.getShiharaiPlanDto();

		Long seisanSeq = shiharaiPlanDto.getSeisanSeq();

		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		// 排他エラーチェックをします。
		if (tSeisanKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		tSeisanKirokuEntity.setSeikyuType(shiharaiPlanDto.getSeikyuType());

		String seikyuType = tSeisanKirokuEntity.getSeikyuType();
		if (SeikyuType.IKKATSU.equalsByCode(seikyuType)) {
			// 一括の場合

			// 支払い日の値を設定
			tSeisanKirokuEntity.setShiharaiDt(DateUtils.parseToLocalDate(shiharaiPlanDto.getShiharaiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			// 端数、支払い開始日、月の支払い日、月の支払い額はNULL
			tSeisanKirokuEntity.setHasu(null);
			tSeisanKirokuEntity.setShiharaiStartDt(null);
			tSeisanKirokuEntity.setMonthShiharaiDate(null);
			tSeisanKirokuEntity.setMonthShiharaiGaku(null);

		} else if (SeikyuType.BUNKATSU.equalsByCode(seikyuType)) {
			// 分割の場合

			// 支払い日はNULL
			tSeisanKirokuEntity.setShiharaiDt(null);
			// 端数、支払い開始日、月の支払い日、月の支払い額の値を設定
			tSeisanKirokuEntity.setHasu(shiharaiPlanDto.getHasu());
			tSeisanKirokuEntity.setShiharaiStartDt(
					LocalDate.of(
							Integer.parseInt(shiharaiPlanDto.getShiharaiYear()),
							Integer.parseInt(shiharaiPlanDto.getShiharaiMonth()),
							Integer.parseInt(shiharaiPlanDto.getShiharaiDay())));
			if (SeisanShiharaiMonthDay.LASTDAY.equalsByCode(shiharaiPlanDto.getShiharaiDayType())) {
				tSeisanKirokuEntity.setMonthShiharaiDate(DateUtils.END_OF_MONTH);
			} else if (SeisanShiharaiMonthDay.DESIGNATEDDAY.equalsByCode(shiharaiPlanDto.getShiharaiDayType())) {
				tSeisanKirokuEntity.setMonthShiharaiDate(shiharaiPlanDto.getShiharaiDay());
			}
			tSeisanKirokuEntity.setMonthShiharaiGaku(LoiozNumberUtils.parseAsBigDecimal(shiharaiPlanDto.getMonthShiharaiGaku()));
		}

		// 精算時の計算処理
		List<TNyushukkinYoteiEntity> clacedList = new ArrayList<>();
		if (shiharaiPlanDto.isSummarizing()) {
			// 残金を1件にまとめる時 -> 独自処理
			clacedList = this.clacSummarizeShiharaiYotei(tSeisanKirokuEntity);
		} else {
			// それ以外の場合 -> 共通処理
			clacedList = commonKaikeiService.clacSeisanYotei(tSeisanKirokuEntity);
		}

		BigDecimal totalNyukinGaku = commonKaikeiService.calcTotal(clacedList.stream()
				.filter(entity -> entity.getNyushukkinGaku() != null)
				.map(TNyushukkinYoteiEntity::getNyushukkinGaku)
				.collect(Collectors.toList()));
		BigDecimal yoteiJissekiTotal = commonKaikeiService.calcTotal(clacedList.stream()
				.map(entity -> {
					if (entity.getNyushukkinGaku() != null) {
						return entity.getNyushukkinGaku();
					} else {
						return entity.getNyushukkinYoteiGaku();
					}
				}).collect(Collectors.toList()));
		BigDecimal sagaku = yoteiJissekiTotal.subtract(tSeisanKirokuEntity.getSeisanGaku());

		// 表示額の計算
		shiharaiPlanDto.setDispMonthShiharaiGaku(commonKaikeiService.toDispAmountLabel(tSeisanKirokuEntity.getMonthShiharaiGaku()));
		shiharaiPlanDto.setSeikyuGaku(commonKaikeiService.toDispAmountLabel(tSeisanKirokuEntity.getSeisanGaku()));
		shiharaiPlanDto.setNyukinGaku(commonKaikeiService.toDispAmountLabel(totalNyukinGaku));
		shiharaiPlanDto.setZankin(commonKaikeiService.toDispAmountLabel(tSeisanKirokuEntity.getSeisanGaku().subtract(totalNyukinGaku)));
		shiharaiPlanDto.setSagaku(commonKaikeiService.toDispAmountLabel(sagaku));
		shiharaiPlanDto.setExpected(BigDecimal.ZERO.compareTo(sagaku) == 0);

		// 一覧表示用
		List<ShiharaiPlanYotei> shiharaiPlanYoteiList = clacedList.stream()
				.map(entity -> convert2ShiharaiPlanYotei(entity))
				.sorted(Comparator
						.comparing(entity -> DateUtils.parseToLocalDate(entity.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED)))
				.collect(Collectors.toList());

		// 追加データのみindexを付与
		int index = 1;
		for (ShiharaiPlanYotei data : shiharaiPlanYoteiList) {
			if (data.getNyushukkinYoteiSeq() == null) {
				data.setNewAddIndex(Long.valueOf(index));
				index++;
			}
		}

		form.setShiharaiPlanDto(shiharaiPlanDto);
		form.setShiharaiPlanYoteiList(shiharaiPlanYoteiList);
	}

	/**
	 * 「残金を1件にまとめる」ときの支払計画を作成
	 *
	 * @param tSeisanKirokuEntity
	 * @param summarizeYoteiDate
	 * @return
	 */
	private List<TNyushukkinYoteiEntity> clacSummarizeShiharaiYotei(TSeisanKirokuEntity tSeisanKirokuEntity) {

		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectBySeisanSeq(tSeisanKirokuEntity.getSeisanSeq());
		List<TNyushukkinYoteiEntity> resultList = new ArrayList<TNyushukkinYoteiEntity>();

		BigDecimal seisanGaku = tSeisanKirokuEntity.getSeisanGaku();
		BigDecimal jisseki = new BigDecimal(0);
		BigDecimal zankin = new BigDecimal(0);

		// 取得できた場合は、一度作成した精算に紐づく実績を計算から除外する
		if (!ListUtils.isEmpty(tNyushukkinYoteiEntity)) {
			List<TNyushukkinYoteiEntity> jissekiList = tNyushukkinYoteiEntity.stream()
					.filter(entity -> NyushukkinType.NYUKIN.equalsByCode(entity.getNyushukkinType()))// 出金に再計算はない
					.filter(entity -> entity.getNyushukkinGaku() != null)
					.collect(Collectors.toList());
			resultList.addAll(jissekiList);
			jisseki = commonKaikeiService.calcTotal(resultList.stream().map(TNyushukkinYoteiEntity::getNyushukkinGaku).collect(Collectors.toList()));
		}

		// 残金を算出する ※精算額 - 実績(新規作成の場合は実績は0円の想定)
		zankin = seisanGaku.subtract(jisseki);

		// 残金の総額分 1件のデータを作成
		TNyushukkinYoteiEntity entity = commonKaikeiService.createNyushukkinYoteiBySeisanKiroku(tSeisanKirokuEntity);
		entity.setNyushukkinYoteiGaku(zankin);
		entity.setNyushukkinYoteiDate(LocalDate.now());// 予定日は当日
		resultList.add(entity);
		return resultList;
	}

	/**
	 * 入力された支払計画から差額を算出する
	 *
	 * @param seisanSeq
	 * @param form
	 * @return
	 * @throws AppException
	 */
	public BigDecimal shiharaiPlanCalcSagaku(ShiharaiPlanEditForm form) throws AppException {

		Long seisanSeq = form.getShiharaiPlanDto().getSeisanSeq();

		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		// 排他エラーチェックをします。
		if (tSeisanKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 追加データと更新データを一つのリストにする
		List<ShiharaiPlanYotei> inputAllData = new ArrayList<>();
		inputAllData.addAll(form.getShiharaiPlanYoteiEdit().values());
		inputAllData.addAll(form.getShiharaiPlanYoteiNewAdd().values());

		// 入力された情報をトータルを算出
		BigDecimal yoteiJissekiTotal = commonKaikeiService.calcTotal(inputAllData.stream()
				.map(data -> {
					if (data.decimalNyushukkinGaku() != null) {
						return data.decimalNyushukkinGaku();
					} else {
						return data.decimalNyushukkinYoteiGaku();
					}
				}).collect(Collectors.toList()));

		// 差額を算出して返却
		BigDecimal sagaku = yoteiJissekiTotal.subtract(tSeisanKirokuEntity.getSeisanGaku());
		return sagaku;
	}

	/**
	 * 支払い計画の保存処理
	 *
	 * @param form
	 * @throws AppException
	 */
	public void shiharaiPlanSave(ShiharaiPlanEditForm form) throws AppException {

		ShiharaiPlanDto shiharaiPlanDto = form.getShiharaiPlanDto();
		Long seisanSeq = form.getShiharaiPlanDto().getSeisanSeq();

		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		// 排他エラーチェックをします。
		if (tSeisanKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		tSeisanKirokuEntity.setSeikyuType(shiharaiPlanDto.getSeikyuType());
		tSeisanKirokuEntity.setLastEditAt(LocalDateTime.now());
		tSeisanKirokuEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());
		if (SeikyuType.IKKATSU.equalsByCode(shiharaiPlanDto.getSeikyuType())) {
			tSeisanKirokuEntity.setHasu(null);
			tSeisanKirokuEntity.setShiharaiDt(DateUtils.parseToLocalDate(shiharaiPlanDto.getShiharaiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			tSeisanKirokuEntity.setShiharaiStartDt(null);
			tSeisanKirokuEntity.setMonthShiharaiDate(null);
			tSeisanKirokuEntity.setMonthShiharaiGaku(null);
		} else if (SeikyuType.BUNKATSU.equalsByCode(shiharaiPlanDto.getSeikyuType())) {
			tSeisanKirokuEntity.setHasu(shiharaiPlanDto.getHasu());
			tSeisanKirokuEntity.setShiharaiDt(null);
			tSeisanKirokuEntity.setShiharaiStartDt(
					LocalDate.of(
							Integer.parseInt(shiharaiPlanDto.getShiharaiYear()),
							Integer.parseInt(shiharaiPlanDto.getShiharaiMonth()),
							Integer.parseInt(shiharaiPlanDto.getShiharaiDay())));
			tSeisanKirokuEntity.setMonthShiharaiDate(shiharaiPlanDto.getShiharaiDay());
			tSeisanKirokuEntity.setMonthShiharaiGaku(LoiozNumberUtils.parseAsBigDecimal(shiharaiPlanDto.getMonthShiharaiGaku()));
		} else {
			// 不正データの場合
			throw new RuntimeException();
		}

		try {
			// 精算記録の更新を行う
			tSeisanKirokuDao.update(tSeisanKirokuEntity);
		} catch (OptimisticLockingFailureException ex) {
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 登録・更新するデータがすべて実績登録済になるかどうか
		boolean needRegistSeisanDate = this.needRegistSeisanDate2KaikeiKiroku(tSeisanKirokuEntity, form);

		// 登録されている入出金予定
		List<TNyushukkinYoteiEntity> dbNyushukkinYotei = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);
		Map<Long, TNyushukkinYoteiEntity> dbMap = dbNyushukkinYotei.stream().collect(Collectors.toMap(
				TNyushukkinYoteiEntity::getNyushukkinYoteiSeq,
				Function.identity()));

		// 新規登録データ
		List<TNyushukkinYoteiEntity> insertNyushukkinYoteiList = new ArrayList<>();
		insertNyushukkinYoteiList = form.getShiharaiPlanYoteiNewAdd().values().stream().map(data -> {
			TNyushukkinYoteiEntity entity = commonKaikeiService.createNyushukkinYoteiBySeisanKiroku(tSeisanKirokuEntity);
			entity.setNyushukkinYoteiDate(DateUtils.parseToLocalDate(data.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setNyushukkinYoteiGaku(LoiozNumberUtils.parseAsBigDecimal(data.getNyushukkinYoteiGaku()));
			entity.setNyushukkinDate(DateUtils.parseToLocalDate(data.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setNyushukkinGaku(LoiozNumberUtils.parseAsBigDecimal(data.getNyushukkinGaku()));
			entity.setTekiyo(data.getTekiyo());
			return entity;
		}).collect(Collectors.toList());

		// 更新データ
		List<TNyushukkinYoteiEntity> updateNyushukkinYoteiList = new ArrayList<>();
		updateNyushukkinYoteiList = form.getShiharaiPlanYoteiEdit().values().stream().map(data -> {
			TNyushukkinYoteiEntity entity = dbMap.get(data.getNyushukkinYoteiSeq());
			if (entity.getNyushukkinGaku() != null) {
				// 実績登録済は更新しないのでnullを返却
				return null;
			}
			entity.setNyushukkinYoteiDate(DateUtils.parseToLocalDate(data.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setNyushukkinYoteiGaku(LoiozNumberUtils.parseAsBigDecimal(data.getNyushukkinYoteiGaku()));
			entity.setNyushukkinDate(DateUtils.parseToLocalDate(data.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			entity.setNyushukkinGaku(LoiozNumberUtils.parseAsBigDecimal(data.getNyushukkinGaku()));
			entity.setTekiyo(data.getTekiyo());
			return entity;
		}).filter(Objects::nonNull).collect(Collectors.toList());// nullを除去してリスト化

		// DBのキーセットから更新するデータのキーが除外 -> 削除するデータのキー
		List<TNyushukkinYoteiEntity> deleteNyushukkinYoteiList = new ArrayList<>();
		LoiozCollectionUtils.subtract(dbMap.keySet(), form.getShiharaiPlanYoteiEdit().keySet()).forEach(dbDataKey -> {
			TNyushukkinYoteiEntity entity = dbMap.get(dbDataKey);
			deleteNyushukkinYoteiList.add(entity);
		});

		// 実績登録済デーは編集できない仕様なので、該当データがある場合、AppException
		if (deleteNyushukkinYoteiList.stream().anyMatch(entity -> entity.getNyushukkinGaku() != null)) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		try {
			// 支払計画の登録・更新・削除処理
			tNyushukkinYoteiDao.insert(insertNyushukkinYoteiList);
			tNyushukkinYoteiDao.update(updateNyushukkinYoteiList);
			tNyushukkinYoteiDao.delete(deleteNyushukkinYoteiList);
		} catch (OptimisticLockingFailureException ex) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 会計記録を作成するのは、実績に登録されている場合
		BiConsumer<List<TKaikeiKirokuEntity>, Stream<TNyushukkinYoteiEntity>> addInsertKaikeiKiroku = (insetKaikeiKirokuList, savedYoteiData) -> {
			savedYoteiData.filter(savedData -> savedData.getNyushukkinGaku() != null).map(savedData -> {
				TKaikeiKirokuEntity tKaikeiKirokuEntity = new TKaikeiKirokuEntity();
				this.setKaikeiKirokuEntityForNyushukkinYotei(tKaikeiKirokuEntity, savedData, true);
				return tKaikeiKirokuEntity;
			}).forEach(insetKaikeiKirokuList::add);
		};

		// 新規作成・更新データから会計記録に登録するデータをリスト化
		List<TKaikeiKirokuEntity> insetKaikeiKirokuList = new ArrayList<>();
		addInsertKaikeiKiroku.accept(insetKaikeiKirokuList, insertNyushukkinYoteiList.stream());
		addInsertKaikeiKiroku.accept(insetKaikeiKirokuList, updateNyushukkinYoteiList.stream());

		// 作成したデータがすべて実績登録済の場合、精算に紐づくデータに精算処理日を登録
		List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = new ArrayList<>();
		Consumer<TKaikeiKirokuEntity> setSeisanDate = entity -> entity.setSeisanDate(LocalDate.now());
		if (needRegistSeisanDate) {
			// 新規作成データに精算処理日を設定
			insetKaikeiKirokuList.forEach(setSeisanDate);
			// まだinsertしてないので、取得したデータもすべて精算処理日を設定
			tKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeisanSeq(seisanSeq);
			tKaikeiKirokuEntities.forEach(setSeisanDate);
		}

		try {
			// 会計記録(実績)の登録・更新処理
			tKaikeiKirokuDao.insert(insetKaikeiKirokuList);
			tKaikeiKirokuDao.update(tKaikeiKirokuEntities);
		} catch (OptimisticLockingFailureException ex) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

	/**
	 * 支払計画の登録後に紐づく会計記録に精算処理日を登録するかどうか
	 *
	 * @return
	 */
	private boolean needRegistSeisanDate2KaikeiKiroku(TSeisanKirokuEntity tSeisanKirokuEntity, ShiharaiPlanEditForm form) {

		// 新規追加されたデータと既存のデータを一つにまとめる
		List<ShiharaiPlanYotei> allInputData = new ArrayList<>();
		allInputData.addAll(form.getShiharaiPlanYoteiEdit().values());
		allInputData.addAll(form.getShiharaiPlanYoteiNewAdd().values());

		// すべてのデータに実績が入力されている。
		boolean jissekiAllRegist = allInputData.stream().allMatch(entity -> LoiozNumberUtils.parseAsBigDecimal(entity.getNyushukkinGaku()) != null);
		// 入力された実績の合計が、請求額と一致している
		boolean sagakuIsZero = 0 == tSeisanKirokuEntity.getSeisanGaku().compareTo(commonKaikeiService.calcTotal(
				allInputData.stream().map(entity -> LoiozNumberUtils.parseAsBigDecimal(entity.getNyushukkinGaku())).collect(Collectors.toList())));

		// 上記2つの条件がtrueの場合、精算に紐づく会計記録に「精算処理日を登録する」
		return jissekiAllRegist && sagakuIsZero;
	}

	/**
	 * 支払計画の登録処理 回収不能の場合
	 *
	 * @param form
	 * @throws AppException
	 */
	public void shiharaiPlanSaveForUncollectible(ShiharaiPlanEditForm form) throws AppException {

		Long seisanSeq = form.getShiharaiPlanDto().getSeisanSeq();
		LocalDate now = LocalDate.now();
		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		// 排他エラーチェックをします。
		if (tSeisanKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 登録されている入出金予定
		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);
		// 実績が登録されていないデータ -> 回首不能とするデータ
		List<TNyushukkinYoteiEntity> updateNyushukkinYoteiList = tNyushukkinYoteiEntities.stream()
				.filter(entity -> entity.getNyushukkinGaku() == null)
				.map(entity -> {
					entity.setNyushukkinDate(now);
					entity.setNyushukkinGaku(entity.getNyushukkinYoteiGaku());
					return entity;
				}).collect(Collectors.toList());

		try {
			// 支払計画の更新処理
			tNyushukkinYoteiDao.update(updateNyushukkinYoteiList);
		} catch (OptimisticLockingFailureException ex) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新データから会計記録に登録するデータをリスト化
		List<TKaikeiKirokuEntity> insertKaikeiKirokuList = updateNyushukkinYoteiList.stream()
				.map(savedData -> {
					TKaikeiKirokuEntity tKaikeiKirokuEntity = new TKaikeiKirokuEntity();
					this.setKaikeiKirokuEntityForNyushukkinYotei(tKaikeiKirokuEntity, savedData, true);
					tKaikeiKirokuEntity.setNyushukkinKomokuId(LoiozNumberUtils.parseAsLong(NyushukkinKomokuType.CANNOT_KAISHU.getCd()));
					tKaikeiKirokuEntity.setSeisanDate(now);
					tKaikeiKirokuEntity.setUncollectableFlg(SystemFlg.FLG_ON.getCd());
					tKaikeiKirokuEntity.setTekiyo("回収不能として処理されました。");
					return tKaikeiKirokuEntity;
				}).collect(Collectors.toList());

		List<TKaikeiKirokuEntity> updateKaikeiKirokuList = tKaikeiKirokuDao.selectBySeisanSeq(seisanSeq);
		updateKaikeiKirokuList.forEach(entity -> entity.setSeisanDate(now));

		try {
			// 会計記録(実績)の登録・更新処理
			tKaikeiKirokuDao.insert(insertKaikeiKirokuList);
			tKaikeiKirokuDao.update(updateKaikeiKirokuList);
		} catch (OptimisticLockingFailureException ex) {
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

	}

	/**
	 * 案件ID,顧客IDから精算完了かをチェックします
	 *
	 * @param ankenId
	 * @param customerId
	 * @return Boolean チェック結果
	 */
	public boolean seisanCompCheck(Long ankenId, Long customerId) {
		// 精算完了かをチェック
		return commonKaikeiService.isSeisanComplete(customerId, ankenId);
	}

	// ****************************************************************
	// 共通
	// ****************************************************************
	/**
	 * 案件IDに紐づく関連情報を取得します。
	 *
	 * @param ankenId 案件ID
	 * @return 関連情報のDtoリスト
	 */
	public List<KaikeiManagementRelationDto> getRelationListForAnken(Long ankenId) {

		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();

		// 顧客IDに紐づく案件情報を取得します。
		List<AnkenCustomerRelationBean> relationBeanList = tAnkenCustomerDao.selectRelation(ankenId, null);

		if (!relationBeanList.isEmpty()) {
			// Bean -> Dto
			relationDtoList = setRelationDtoList(relationBeanList);
		}

		return relationDtoList;
	}

	/**
	 * 顧客IDに紐づく関連情報を取得します。
	 *
	 * @param customerId 顧客ID
	 * @return 関連情報のDtoリスト
	 */
	public List<KaikeiManagementRelationDto> getRelationListForCustomer(Long customerId) {

		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();

		// 顧客IDに紐づく案件情報を取得します。
		List<AnkenCustomerRelationBean> relationBeanList = tAnkenCustomerDao.selectRelation(null, customerId);

		if (!relationBeanList.isEmpty()) {
			// Bean -> Dto
			relationDtoList = setRelationDtoList(relationBeanList);
		}

		return relationDtoList;
	}
	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 入出金項目リストを取得します。
	 *
	 * @param komokuType 入出金項目タイプ
	 * @param nyushukkinKomokuId 入出金項目ID
	 * @return 入出金項目リスト
	 */
	private List<NyushukkinKomokuListDto> getNyushukkinKomokuList(String komokuType, Long nyushukkinKomokuId) {

		List<NyushukkinKomokuListDto> komokuDtoList = new ArrayList<NyushukkinKomokuListDto>();

		// 入出金項目タイプに紐づく入出金項目リストを取得します。
		List<MNyushukkinKomokuEntity> komokuEntityList = mNyushukkinKomokuDao.selectByNyushukkinType(komokuType);

		if (!komokuEntityList.isEmpty()) {
			// Entity -> Dto
			komokuDtoList = setNyushukkinKomokuDtoForUpdate(komokuEntityList, nyushukkinKomokuId);
		}

		return komokuDtoList;
	}

	/**
	 * List<KaikeiManagementRelationBean> -> List<KaikeiManagementRelationDto>
	 *
	 * @param relationBeanList 会計管理関連情報のBeanリスト
	 * @return 会計管理関連情報のDtoリスト
	 */
	private List<KaikeiManagementRelationDto> setRelationDtoList(List<AnkenCustomerRelationBean> relationBeanList) {

		List<KaikeiManagementRelationDto> relationDtoList = new ArrayList<KaikeiManagementRelationDto>();

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		for (AnkenCustomerRelationBean bean : relationBeanList) {

			// Beanの値をDtoに設定します。
			KaikeiManagementRelationDto relationDto = new KaikeiManagementRelationDto();
			relationDto.setAnkenId(AnkenId.of(bean.getAnkenId()));
			String bunyaName = commonBunyaService.getBunyaName(bunyaMap.get(bean.getBunyaId()));
			relationDto.setBunyaName(bunyaName);
			relationDto.setAnkenName(bean.getAnkenName());
			relationDto.setCustomerId(CustomerId.of(bean.getCustomerId()));
			relationDto.setCustomerName(bean.getCustomerName());
			relationDto.setCanKaikeiCreate(!AnkenStatus.isSeisanComp(bean.getAnkenStatus()));
			relationDto.setAnkenStatusCd(bean.getAnkenStatus());

			relationDtoList.add(relationDto);
		}

		return relationDtoList;
	}
	
	// ****************************************************************
	// 案件明細
	// ****************************************************************
	/**
	 * 案件明細情報(弁護士報酬)の新規登録を行います。
	 *
	 * @param ankenMeisaiDto 案件明細情報のDto
	 * @throws AppException
	 */
	private void saveAnkenMeisai(AnkenMeisaiDto ankenMeisaiDto) throws AppException {

		// Dto -> Entity に変換します。
		TKaikeiKirokuEntity kaikeiKirokuEntity = this.convertTKaikeiKirokuEntity(ankenMeisaiDto);

		try {
			// 登録処理を行います。
			tKaikeiKirokuDao.insert(kaikeiKirokuEntity);

		} catch (OptimisticLockException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, ex);
		}
	}

	/**
	 * 報酬情報(弁護士報酬)を更新します。
	 *
	 * @param inputAnkenMeisaiDto 報酬情報のDto
	 * @throws AppException
	 */
	private void updateAnkenMeisai(AnkenMeisaiDto inputAnkenMeisaiDto) throws AppException {

		// 会計記録SEQに紐づく案件明細情報を取得します。
		TKaikeiKirokuEntity kaikeiKirokuEntity = tKaikeiKirokuDao.selectByKaikeiKirokuSeq(inputAnkenMeisaiDto.getKaikeiKirokuSeq());
		if (kaikeiKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力した情報をEntityに設定します。
		this.setTKaikeiKirokuEntity(inputAnkenMeisaiDto, kaikeiKirokuEntity);

		try {
			// 更新します。
			tKaikeiKirokuDao.update(kaikeiKirokuEntity);
		} catch (OptimisticLockException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

	}

	/**
	 * TKaikeiKirokuEntity -> AnkenMeisaiDto
	 *
	 * @param kaikeiKirokuEntity 会計記録情報のEntity
	 * @return 会計記録情報を管理するDtoクラス
	 */
	private AnkenMeisaiDto setAnkenMeisaiDto(TKaikeiKirokuEntity kaikeiKirokuEntity) {

		AnkenMeisaiDto ankenMeisaiDto = new AnkenMeisaiDto();

		// ---------------------------------------------------------
		// Entityの値をDtoに設定します。
		// ---------------------------------------------------------
		ankenMeisaiDto.setKaikeiKirokuSeq(kaikeiKirokuEntity.getKaikeiKirokuSeq());
		ankenMeisaiDto.setCustomerId(kaikeiKirokuEntity.getCustomerId());
		ankenMeisaiDto.setAnkenId(kaikeiKirokuEntity.getAnkenId());
		ankenMeisaiDto.setHasseiDate(DateUtils.parseToString(kaikeiKirokuEntity.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		ankenMeisaiDto.setHoshuKomokuId(kaikeiKirokuEntity.getHoshuKomokuId());
		ankenMeisaiDto.setTaxFlg(kaikeiKirokuEntity.getTaxFlg());
		ankenMeisaiDto.setTaxRate(kaikeiKirokuEntity.getTaxRate());
		ankenMeisaiDto.setGensenchoshuFlg(kaikeiKirokuEntity.getGensenchoshuFlg());
		ankenMeisaiDto.setWithholdingTax(kaikeiKirokuEntity.getGensenchoshuFlg());
		ankenMeisaiDto.setTimeChargeTimeShitei(kaikeiKirokuEntity.getTimeChargeTimeShitei());
		ankenMeisaiDto.setTimeChargeTime(String.valueOf(kaikeiKirokuEntity.getTimeChargeTime()));
		ankenMeisaiDto.setTekiyo(kaikeiKirokuEntity.getTekiyo());
		ankenMeisaiDto.setSeisanDate(DateUtils.parseToString(kaikeiKirokuEntity.getSeisanDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		ankenMeisaiDto.setSeisanId(kaikeiKirokuEntity.getSeisanSeq());
		if (kaikeiKirokuEntity.getCreatedBy() == null || kaikeiKirokuEntity.getCreatedAt() == null) {
			ankenMeisaiDto.setDispCreatedByNameDateTime("");
		} else {
			String CreatedByName = commonAccountiService.getAccountName(kaikeiKirokuEntity.getCreatedBy());
			ankenMeisaiDto.setDispCreatedByNameDateTime(CreatedByName + " "
					+ DateUtils.parseToString(kaikeiKirokuEntity.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		}
		if (kaikeiKirokuEntity.getLastEditBy() == null || kaikeiKirokuEntity.getLastEditAt() == null) {
			ankenMeisaiDto.setDispLastEditByNameDateTime("");
		} else {
			String LastEditByName = commonAccountiService.getAccountName(kaikeiKirokuEntity.getLastEditBy());
			ankenMeisaiDto.setDispLastEditByNameDateTime(LastEditByName + " "
					+ DateUtils.parseToString(kaikeiKirokuEntity.getLastEditAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		}

		if ((LawyerHoshu.TIME_CHARGE.getCd().equals(ankenMeisaiDto.getHoshuKomokuId()))
				&& TimeChargeTimeShitei.START_END_TIME.getCd().equals(kaikeiKirokuEntity.getTimeChargeTimeShitei())) {
			// 開始・終了時間を指定する場合

			// 開始日時、終了日時
			LocalDateTime startDateAndTime = kaikeiKirokuEntity.getTimeChargeStartTime();
			LocalDateTime endDateAndTime = kaikeiKirokuEntity.getTimeChargeEndTime();

			// 開始日、終了日
			LocalDate startDate = LocalDate.of(startDateAndTime.getYear(), startDateAndTime.getMonth(), startDateAndTime.getDayOfMonth());
			LocalDate endDate = LocalDate.of(endDateAndTime.getYear(), endDateAndTime.getMonth(), endDateAndTime.getDayOfMonth());
			ankenMeisaiDto.setTimeChargeStartDate(DateUtils.parseToString(startDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			ankenMeisaiDto.setTimeChargeEndDate(DateUtils.parseToString(endDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			// 開始時間、終了時間
			LocalTime startTime = LocalTime.of(startDateAndTime.getHour(), startDateAndTime.getMinute());
			LocalTime endTime = LocalTime.of(endDateAndTime.getHour(), endDateAndTime.getMinute());
			ankenMeisaiDto.setTimeChargeStartTime(startTime.toString());
			ankenMeisaiDto.setTimeChargeEndTime(endTime.toString());
		}

		// ---------------------------------------------------------
		// 出金額、単価
		// ---------------------------------------------------------
		ankenMeisaiDto.setKingaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuEntity.getShukkinGaku()));
		ankenMeisaiDto.setTimeChargeTanka(commonKaikeiService.toDispAmountLabel(kaikeiKirokuEntity.getTimeChargeTanka()));

		// ---------------------------------------------------------
		// 報酬額（出金額の税抜金額）
		// ---------------------------------------------------------
		BigDecimal kingakuExcludingTax = new BigDecimal(0);
		if (TaxFlg.INTERNAL_TAX.getCd().equals(kaikeiKirokuEntity.getTaxFlg())) {
			// 出金額が税込の場合
			// 出金額から税額を引く
			kingakuExcludingTax = kaikeiKirokuEntity.getShukkinGaku().subtract(kaikeiKirokuEntity.getTaxGaku());
		} else {
			// 出金額が税抜の場合
			kingakuExcludingTax = kaikeiKirokuEntity.getShukkinGaku();
		}
		ankenMeisaiDto.setHoshuGaku(commonKaikeiService.toDispAmountLabel(kingakuExcludingTax));

		// ---------------------------------------------------------
		// 税額
		// ---------------------------------------------------------
		ankenMeisaiDto.setTaxGaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuEntity.getTaxGaku()));

		// ---------------------------------------------------------
		// 源泉徴収額、報酬計、源泉徴収額の算出式
		// ---------------------------------------------------------
		String calcFormula1 = "";
		String calcFormula2 = "";
		if (GensenChoshu.DO.getCd().equals(kaikeiKirokuEntity.getGensenchoshuFlg())) {
			// 源泉徴収する場合

			// --------------------------------------------------
			// 源泉徴収額
			// --------------------------------------------------
			// 源泉徴収は表示上はマイナス表記とする
			ankenMeisaiDto.setGensenchoshuGaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuEntity.getGensenchoshuGaku().negate()));
			// --------------------------------------------------
			// 報酬計
			// --------------------------------------------------
			ankenMeisaiDto.setHoshuTotal(commonKaikeiService.toDispAmountLabel(
					kingakuExcludingTax.add(kaikeiKirokuEntity.getTaxGaku()).subtract(kaikeiKirokuEntity.getGensenchoshuGaku())));

			// --------------------------------------------------
			// 源泉徴収の計算式
			// --------------------------------------------------
			// 100万円との比較
			BigDecimal oneMillion = new BigDecimal(1000000);
			int isLargerThanOneMillion = kingakuExcludingTax.compareTo(oneMillion);

			if (isLargerThanOneMillion == 1) {
				// **************************
				// 100万円より高額
				// **************************
				// 源泉徴収率が10.21％になる部分の金額の計算式
				String tenGensenRateStr = GensenChoshuRate.TEN.getVal();
				calcFormula1 = commonKaikeiService.toDispAmountLabel(oneMillion) + "円 × " + tenGensenRateStr + "%";

				// 源泉徴収額が20.42％になる部分の計算式
				BigDecimal twentyPerKingaku = kingakuExcludingTax.subtract(oneMillion);
				String twentyGensenRateStr = GensenChoshuRate.TWENTY.getVal();
				calcFormula2 = " + " + commonKaikeiService.toDispAmountLabel(twentyPerKingaku) + "円 × " + twentyGensenRateStr + "%";
			} else {
				// **************************
				// 100万円以下
				// **************************
				String gensenRateStr = GensenChoshuRate.TEN.getVal();
				calcFormula1 = commonKaikeiService.toDispAmountLabel(kingakuExcludingTax) + "円 × " + gensenRateStr + "%";
			}
		} else {
			// 源泉徴収しない場合

			// --------------------------------------------------
			// 源泉徴収額
			// --------------------------------------------------
			ankenMeisaiDto.setGensenchoshuGaku(commonKaikeiService.toDispAmountLabel(BigDecimal.ZERO));
			// --------------------------------------------------
			// 報酬計
			// --------------------------------------------------
			ankenMeisaiDto
					.setHoshuTotal(commonKaikeiService.toDispAmountLabel(kingakuExcludingTax.add(kaikeiKirokuEntity.getTaxGaku())));
		}

		ankenMeisaiDto.setCalcFormula1(calcFormula1);
		ankenMeisaiDto.setCalcFormula2(calcFormula2);

		return ankenMeisaiDto;
	}

	/**
	 * AnkenMeisaiDto -> TKaikeiKirokuEntity の設定処理<br>
	 *
	 * <pre>
	 * 新規登録用です。
	 * </pre>
	 *
	 * @param ankenMeisaiDto 案件明細情報を管理するDto
	 * @return 会計記録Entity
	 * @throws AppException
	 */
	private TKaikeiKirokuEntity convertTKaikeiKirokuEntity(AnkenMeisaiDto ankenMeisaiDto) throws AppException {

		TKaikeiKirokuEntity kaikeiKirokuEntity = new TKaikeiKirokuEntity();

		// --------------------------------------------------
		// Dto -> Entity
		// --------------------------------------------------
		kaikeiKirokuEntity.setKaikeiKirokuSeq(ankenMeisaiDto.getKaikeiKirokuSeq());
		kaikeiKirokuEntity.setCustomerId(ankenMeisaiDto.getCustomerId());
		kaikeiKirokuEntity.setAnkenId(ankenMeisaiDto.getAnkenId());
		kaikeiKirokuEntity.setHoshuKomokuId(ankenMeisaiDto.getHoshuKomokuId());
		kaikeiKirokuEntity.setNyushukkinType(NyushukkinType.SHUKKIN.getCd());
		kaikeiKirokuEntity.setGensenchoshuFlg(ankenMeisaiDto.getGensenchoshuFlg());
		kaikeiKirokuEntity.setTaxFlg(ankenMeisaiDto.getTaxFlg());
		kaikeiKirokuEntity.setTaxRate(ankenMeisaiDto.getTaxRate());
		kaikeiKirokuEntity.setTekiyo(ankenMeisaiDto.getTekiyo());
		kaikeiKirokuEntity.setUncollectableFlg(SystemFlg.FLG_OFF.getCd());

		// --------------------------------------------------
		// タイムチャージ時間の指定方法
		// --------------------------------------------------
		if (LawyerHoshu.TIME_CHARGE.equalsByCode(ankenMeisaiDto.getHoshuKomokuId())) {
			if (TimeChargeTimeShitei.START_END_TIME.equalsByCode(ankenMeisaiDto.getTimeChargeTimeShitei())) {
				// 開始・終了時間を指定した場合

				// DateTime型に変換します。
				LocalDateTime timeChargeStartTime = LocalDateTime.of(
						DateUtils.parseToLocalDate(ankenMeisaiDto.getTimeChargeStartDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED),
						DateUtils.parseToLocalTime(ankenMeisaiDto.getTimeChargeStartTime(), DateUtils.TIME_FORMAT_HHMM));
				LocalDateTime timeChargeEndTime = LocalDateTime.of(
						DateUtils.parseToLocalDate(ankenMeisaiDto.getTimeChargeEndDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED),
						DateUtils.parseToLocalTime(ankenMeisaiDto.getTimeChargeEndTime(), DateUtils.TIME_FORMAT_HHMM));

				// 変換した値をセットします
				kaikeiKirokuEntity.setTimeChargeStartTime(timeChargeStartTime);
				kaikeiKirokuEntity.setTimeChargeEndTime(timeChargeEndTime);
				kaikeiKirokuEntity.setHasseiDate(
						DateUtils.parseToLocalDate(ankenMeisaiDto.getTimeChargeStartDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			} else {
				// 時間(分)のみを指定した場合
				kaikeiKirokuEntity.setTimeChargeStartTime(null);
				kaikeiKirokuEntity.setTimeChargeEndTime(null);
				kaikeiKirokuEntity.setHasseiDate(
						DateUtils.parseToLocalDate(ankenMeisaiDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			}

			// 時間(分)の指定方法、経過時間は指定方法に因らず設定します。
			kaikeiKirokuEntity.setTimeChargeTimeShitei(ankenMeisaiDto.getTimeChargeTimeShitei());
			kaikeiKirokuEntity.setTimeChargeTime(Integer.parseInt(ankenMeisaiDto.getTimeChargeTime()));

		} else {
			// タイムチャージ以外の場合
			kaikeiKirokuEntity.setTimeChargeStartTime(null);
			kaikeiKirokuEntity.setTimeChargeEndTime(null);
			kaikeiKirokuEntity.setTimeChargeTimeShitei(null);
			kaikeiKirokuEntity.setTimeChargeTime(null);
			kaikeiKirokuEntity.setHasseiDate(
					DateUtils.parseToLocalDate(ankenMeisaiDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		}

		// --------------------------------------------------
		// 出金額（報酬額）を計算します。
		// --------------------------------------------------
		BigDecimal shukkinGaku = new BigDecimal(0);
		if (LawyerHoshu.TIME_CHARGE.getCd().equals(ankenMeisaiDto.getHoshuKomokuId())) {
			// *************************************
			// タイムチャージ時
			// *************************************
			BigDecimal timeChargeTanka = LoiozNumberUtils.parseAsBigDecimal(ankenMeisaiDto.getTimeChargeTanka());

			// 単価 × 時間 を計算します。
			String displayTime = ankenMeisaiDto.getTimeChargeTime();
			String time = displayTime.replace("分", "");
			Long minutes = Long.parseLong(time);
			BigDecimal decimalMinutes = BigDecimal.valueOf(minutes);

			// 単価 × 時間 を計算します。
			shukkinGaku = this.hoshuCal(timeChargeTanka, decimalMinutes);

			kaikeiKirokuEntity.setTimeChargeTanka(timeChargeTanka);
			kaikeiKirokuEntity.setShukkinGaku(shukkinGaku);

		} else {
			// *************************************
			// タイムチャージ以外
			// *************************************

			// 金額
			shukkinGaku = LoiozNumberUtils.parseAsBigDecimal(ankenMeisaiDto.getKingaku());
			kaikeiKirokuEntity.setShukkinGaku(shukkinGaku);
		}

		// --------------------------------------------------
		// 消費税金額と出金額の税抜額
		// --------------------------------------------------
		// 消費税の端数処理方法を取得
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		String taxHasuType = tenantEntity.getTaxHasuType();

		BigDecimal taxGaku = new BigDecimal(0);
		BigDecimal shukkinGakuExcludingTax = new BigDecimal(0);

		if (TaxFlg.INTERNAL_TAX.getCd().equals(ankenMeisaiDto.getTaxFlg())) {
			// 出金額が税込の場合

			// 消費税額
			taxGaku = commonKaikeiService.calcInternalTax(shukkinGaku, kaikeiKirokuEntity.getTaxRate(), taxHasuType);
			// 出金額の税抜額
			shukkinGakuExcludingTax = shukkinGaku.subtract(taxGaku);

		} else {
			// 出金額が税抜の場合

			// 消費税額
			taxGaku = commonKaikeiService.calcTax(shukkinGaku, kaikeiKirokuEntity.getTaxRate(), taxHasuType);
			// 出金額の税抜額
			shukkinGakuExcludingTax = shukkinGaku;
		}

		kaikeiKirokuEntity.setTaxGaku(taxGaku);

		// --------------------------------------------------
		// 源泉徴収額
		// --------------------------------------------------
		BigDecimal gensenchoshuGaku = new BigDecimal(0);

		if (ankenMeisaiDto.getGensenchoshuFlg().equals(GensenChoshu.DO.getCd())) {
			// 源泉徴収する場合
			gensenchoshuGaku = commonKaikeiService.calcGensenChoshu(shukkinGakuExcludingTax);
		} else {
			// 源泉徴収しない場合
			gensenchoshuGaku = null;
		}

		kaikeiKirokuEntity.setGensenchoshuGaku(gensenchoshuGaku);

		return kaikeiKirokuEntity;
	}

	/**
	 * AnkenMeisaiDto -> TKaikeiKiorkuEntity の設定処理<br>
	 *
	 * <pre>
	 * 更新用です。
	 * </pre>
	 *
	 * @param ankenMeisaiDto 案件明細情報を管理するDto
	 * @param kaikeiKirokuEntity 会計記録情報Entity
	 * @throws AppException
	 */
	private void setTKaikeiKirokuEntity(AnkenMeisaiDto ankenMeisaiDto, TKaikeiKirokuEntity kaikeiKirokuEntity) throws AppException {

		// --------------------------------------------------
		// Dto -> Entity
		// --------------------------------------------------
		kaikeiKirokuEntity.setKaikeiKirokuSeq(ankenMeisaiDto.getKaikeiKirokuSeq());
		kaikeiKirokuEntity.setCustomerId(ankenMeisaiDto.getCustomerId());
		kaikeiKirokuEntity.setAnkenId(ankenMeisaiDto.getAnkenId());
		kaikeiKirokuEntity.setHoshuKomokuId(ankenMeisaiDto.getHoshuKomokuId());
		kaikeiKirokuEntity.setGensenchoshuFlg(ankenMeisaiDto.getGensenchoshuFlg());
		kaikeiKirokuEntity.setTaxFlg(ankenMeisaiDto.getTaxFlg());
		kaikeiKirokuEntity.setTaxRate(ankenMeisaiDto.getTaxRate());
		kaikeiKirokuEntity.setTekiyo(ankenMeisaiDto.getTekiyo());
		kaikeiKirokuEntity.setUncollectableFlg(SystemFlg.FLG_OFF.getCd());
		kaikeiKirokuEntity.setLastEditAt(LocalDateTime.now());
		kaikeiKirokuEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());

		// --------------------------------------------------
		// タイムチャージ時間の指定方法
		// --------------------------------------------------
		if (LawyerHoshu.TIME_CHARGE.getCd().equals(ankenMeisaiDto.getHoshuKomokuId())) {
			if (TimeChargeTimeShitei.START_END_TIME.getCd().equals(ankenMeisaiDto.getTimeChargeTimeShitei())) {
				// 開始・終了時間を指定した場合

				// DateTime型に変換します。
				LocalDateTime timeChargeStartTime = LocalDateTime.of(
						DateUtils.parseToLocalDate(ankenMeisaiDto.getTimeChargeStartDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED),
						DateUtils.parseToLocalTime(ankenMeisaiDto.getTimeChargeStartTime(), DateUtils.TIME_FORMAT_HHMM));
				LocalDateTime timeChargeEndTime = LocalDateTime.of(
						DateUtils.parseToLocalDate(ankenMeisaiDto.getTimeChargeEndDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED),
						DateUtils.parseToLocalTime(ankenMeisaiDto.getTimeChargeEndTime(), DateUtils.TIME_FORMAT_HHMM));

				// DateTime型に変換します。
				kaikeiKirokuEntity.setTimeChargeStartTime(timeChargeStartTime);
				kaikeiKirokuEntity.setTimeChargeEndTime(timeChargeEndTime);
				// 開始日を発生日として設定
				kaikeiKirokuEntity.setHasseiDate(
						DateUtils.parseToLocalDate(ankenMeisaiDto.getTimeChargeStartDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			} else {
				// 時間(分)を指定した場合
				kaikeiKirokuEntity.setTimeChargeStartTime(null);
				kaikeiKirokuEntity.setTimeChargeEndTime(null);
				kaikeiKirokuEntity.setHasseiDate(DateUtils.parseToLocalDate(ankenMeisaiDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			}

			// 時間の指定方法・経過時間は、指定方法に因らず設定します。
			kaikeiKirokuEntity.setTimeChargeTimeShitei(ankenMeisaiDto.getTimeChargeTimeShitei());
			kaikeiKirokuEntity.setTimeChargeTime(Integer.parseInt(ankenMeisaiDto.getTimeChargeTime()));

		} else {
			// タイムチャージ以外の場合
			kaikeiKirokuEntity.setHasseiDate(DateUtils.parseToLocalDate(ankenMeisaiDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			kaikeiKirokuEntity.setTimeChargeStartTime(null);
			kaikeiKirokuEntity.setTimeChargeEndTime(null);
			kaikeiKirokuEntity.setTimeChargeTimeShitei(null);
			kaikeiKirokuEntity.setTimeChargeTime(null);
		}

		// --------------------------------------------------
		// 出金額（報酬額）を計算します。
		// --------------------------------------------------
		BigDecimal shukkinGaku = new BigDecimal(0);
		if (LawyerHoshu.TIME_CHARGE.getCd().equals(ankenMeisaiDto.getHoshuKomokuId())) {
			// *************************************
			// タイムチャージ時
			// *************************************
			BigDecimal timeChargeTanka = LoiozNumberUtils.parseAsBigDecimal(ankenMeisaiDto.getTimeChargeTanka());

			// 単価 × 時間 を計算します。
			String displayTime = ankenMeisaiDto.getTimeChargeTime();
			String time = displayTime.replace("分", "");
			Long minutes = Long.parseLong(time);
			BigDecimal decimalMinutes = BigDecimal.valueOf(minutes);

			// 単価 × 時間 を計算します。
			shukkinGaku = this.hoshuCal(timeChargeTanka, decimalMinutes);

			kaikeiKirokuEntity.setTimeChargeTanka(timeChargeTanka);
			kaikeiKirokuEntity.setShukkinGaku(shukkinGaku);

		} else {
			// *************************************
			// タイムチャージ以外
			// *************************************

			// 金額
			shukkinGaku = LoiozNumberUtils.parseAsBigDecimal(ankenMeisaiDto.getKingaku());
			kaikeiKirokuEntity.setShukkinGaku(shukkinGaku);
		}

		// --------------------------------------------------
		// 消費税金額と出金額の税抜額
		// --------------------------------------------------
		// 消費税の端数処理方法を取得
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		String taxHasuType = tenantEntity.getTaxHasuType();

		BigDecimal taxGaku = new BigDecimal(0);
		BigDecimal shukkinGakuExcludingTax = new BigDecimal(0);

		if (TaxFlg.INTERNAL_TAX.getCd().equals(ankenMeisaiDto.getTaxFlg())) {
			// 出金額が税込の場合

			// 消費税額
			taxGaku = commonKaikeiService.calcInternalTax(shukkinGaku, kaikeiKirokuEntity.getTaxRate(), taxHasuType);
			// 出金額の税抜額
			shukkinGakuExcludingTax = shukkinGaku.subtract(taxGaku);

		} else {
			// 出金額が税抜の場合

			// 消費税額
			taxGaku = commonKaikeiService.calcTax(shukkinGaku, kaikeiKirokuEntity.getTaxRate(), taxHasuType);
			// 出金額の税抜額
			shukkinGakuExcludingTax = shukkinGaku;
		}

		kaikeiKirokuEntity.setTaxGaku(taxGaku);

		// --------------------------------------------------
		// 源泉徴収額
		// --------------------------------------------------
		BigDecimal gensenchoshuGaku = new BigDecimal(0);

		if (ankenMeisaiDto.getGensenchoshuFlg().equals(GensenChoshu.DO.getCd())) {
			// 源泉徴収する場合
			gensenchoshuGaku = commonKaikeiService.calcGensenChoshu(shukkinGakuExcludingTax);
		} else {
			// 源泉徴収しない場合
			gensenchoshuGaku = null;
		}

		kaikeiKirokuEntity.setGensenchoshuGaku(gensenchoshuGaku);
	}

	// ****************************************************************
	// 報酬明細
	// ****************************************************************
	/**
	 * 報酬明細の画面Formに報酬明細情報を設定します。<br>
	 *
	 * <pre>
	 * 案件管理から遷移する場合用です。<br>
	 * 案件IDに紐づく報酬情報を全て取得します。
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID(nullの場合以外の場合=顧客の行をクリックした場合 → 顧客IDで絞り込み)
	 * @param isIndicateSeisanComp 精算完了を表示するかどうか
	 * @return 報酬明細情報のフォーム
	 */
	private void setHoshuMeisaiListForAnken(HoshuMeisaiEditForm hoshuMeisaiEditForm, Long customerId, boolean isIndicateSeisanComp) {

		// 報酬情報を取得します。
		List<HoshuMeisaiBean> hoshuMeisaiBeanList;
		if (customerId != null || isIndicateSeisanComp) {
			// 顧客の行クリック、または、全ての報酬明細を確認で、完了データの表示チェックを入れている場合
			// すべて表示
			hoshuMeisaiBeanList = tKaikeiKirokuDao.selectHoshuMeisaiByAnkenId(hoshuMeisaiEditForm.getTransitionAnkenId(), customerId);
		} else {
			// 精算が未完了のもののみ表示
			hoshuMeisaiBeanList = tKaikeiKirokuDao.selectNotCompByAnkenId(hoshuMeisaiEditForm.getTransitionAnkenId(), customerId, AnkenStatus.getCompletedStatusCd());
		}

		// bean -> dto
		List<HoshuMeisaiListDto> hoshuMeisaiDtoList = this.convert2Dto(hoshuMeisaiBeanList);
		hoshuMeisaiEditForm.setHoshuMeisaiDtoList(hoshuMeisaiDtoList);
	}

	/**
	 * 報酬明細Formに報酬明細情報を設定します。<br>
	 *
	 * <pre>
	 * 顧客管理から遷移する場合用です。<br>
	 * 顧客IDに紐づく報酬情報を全て取得します。
	 * </pre>
	 * 
	 * @param hoshuMeisaiEditForm
	 * @param ankenId 案件ID(nullの場合以外の場合=案件の行をクリックした場合 → 案件IDで絞り込み)
	 * @param isIndicateSeisanComp 精算完了を表示するかどうか
	 */
	private void setHoshuMeisaiListForCustomer(HoshuMeisaiEditForm hoshuMeisaiEditForm, Long ankenId, boolean isIndicateSeisanComp) {

		// 報酬情報を取得します。
		List<HoshuMeisaiBean> hoshuMeisaiBeanList;
		if (ankenId != null || isIndicateSeisanComp) {
			// 案件の行クリック、または、全ての報酬明細を確認で、完了データの表示チェックを入れている場合
			// すべて表示
			hoshuMeisaiBeanList = tKaikeiKirokuDao.selectHoshuMeisaiByCustomerId(hoshuMeisaiEditForm.getTransitionCustomerId(), ankenId);
		} else {
			// 精算が未完了のもののみ表示
			hoshuMeisaiBeanList = tKaikeiKirokuDao.selectNotCompByCustomerId(hoshuMeisaiEditForm.getTransitionCustomerId(), ankenId, AnkenStatus.getCompletedStatusCd());
		}
		// bean -> dto
		List<HoshuMeisaiListDto> hoshuMeisaiDtoList = this.convert2Dto(hoshuMeisaiBeanList);
		hoshuMeisaiEditForm.setHoshuMeisaiDtoList(hoshuMeisaiDtoList);
	}

	/**
	 * 報酬明細Beanから報酬明細一覧Dtoに変換する
	 * 
	 * @param beanList
	 * @return
	 */
	private List<HoshuMeisaiListDto> convert2Dto(List<HoshuMeisaiBean> beanList) {
		if (CollectionUtils.isEmpty(beanList)) {
			return Collections.emptyList();
		}

		// convert処理
		Function<HoshuMeisaiBean, HoshuMeisaiListDto> convertFn = (bean) -> {
			HoshuMeisaiListDto dto = new HoshuMeisaiListDto();

			// 顧客の設定
			if (bean.getCustomerId() != null) {
				dto.setCustomerId(bean.getCustomerId());
				dto.setDispCustomerId(CustomerId.of(bean.getCustomerId()).toString());
				dto.setCustomerName(bean.getCustomerName());
			}
			// 案件の設定
			if (bean.getAnkenId() != null) {
				dto.setAnkenId(bean.getAnkenId());
				dto.setDispAnkenId(AnkenId.of(bean.getAnkenId()).toString());
				dto.setAnkenName(bean.getAnkenName());
			}
			// 精算IDの設定
			if (bean.getSeisanSeq() != null) {
				SeisanId seisanId = new SeisanId(bean.getCustomerId(), bean.getSeisanId());
				dto.setSeisanId(seisanId);
			}
			// 精算処理日の設定
			if (bean.getSeisanDate() != null) {
				dto.setSeisanDate(DateUtils.parseToString(bean.getSeisanDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
				dto.setCompleted(true); // 精算処理日が設定されている場合は処理済みとする
			}

			// 案件ステータスが精算完了と判断される場合は、処理済みとする
			if (AnkenStatus.isSeisanComp(bean.getAnkenStatus())) {
				dto.setCompleted(true);
			}

			BigDecimal hoshuGaku = bean.getKingaku() == null ? new BigDecimal(0) : bean.getKingaku();
			BigDecimal taxGaku = bean.getTaxGaku() == null ? new BigDecimal(0) : bean.getTaxGaku();
			BigDecimal gensenGaku = bean.getGensenchoshuGaku() == null ? new BigDecimal(0) : bean.getGensenchoshuGaku();

			// 税込の場合は、金額から税額を減算
			if (TaxFlg.INTERNAL_TAX.equalsByCode(bean.getTaxFlg())) {
				hoshuGaku = hoshuGaku.subtract(taxGaku);
			}

			// 合計を算出(税額考慮した報酬額 - 源泉徴収額)
			BigDecimal total = hoshuGaku.add(taxGaku);

			// 源泉対象の場合のみ、源泉額を減算
			if (GensenTarget.TAISHOU.equalsByCode(bean.getGensenchoshuFlg())) {
				total = total.subtract(gensenGaku);
			}

			// 発生日
			if (LawyerHoshu.TIME_CHARGE.equalsByCode(bean.getHoshuKomokuId()) && TimeChargeTimeShitei.START_END_TIME.equalsByCode(bean.getTimeChargeTimeShitei())) {
				// タイムチャージで、開始・終了日時を指定している場合
				// 発生日には開始日を設定する
				LocalDate timeChargeStartDate = DateUtils.convertLocalDate(bean.getTimeChargeStartTime());
				dto.setTimeChargeStartTime(bean.getTimeChargeStartTime());
				dto.setHasseiDateDate(timeChargeStartDate);
				dto.setHasseiDate(DateUtils.parseToString(timeChargeStartDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			} else {
				// それ以外の場合
				dto.setHasseiDateDate(bean.getHasseiDate());
				dto.setHasseiDate(DateUtils.parseToString(bean.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			}

			dto.setKaikeiKirokuSeq(bean.getKaikeiKirokuSeq());
			dto.setHoshuKomoku(LawyerHoshu.of(bean.getHoshuKomokuId()));
			dto.setHoshuGaku(hoshuGaku);
			dto.setDispHoshuGaku(commonKaikeiService.toDispAmountLabel(hoshuGaku));
			dto.setTaxGaku(taxGaku);
			dto.setDispTaxGaku(commonKaikeiService.toDispAmountLabel(taxGaku));
			dto.setGensenGaku(gensenGaku);
			dto.setDispGensenGaku(commonKaikeiService.toDispAmountLabel(gensenGaku.negate())); // 源泉徴収は表示上はマイナス表記とする
			dto.setTotal(total);
			dto.setDispTotal(commonKaikeiService.toDispAmountLabel(total));
			dto.setTekiyou(bean.getTekiyo());

			return dto;
		};

		List<HoshuMeisaiListDto> hoshuMeisaiListDtoList = beanList.stream().map(convertFn).collect(Collectors.toList());

		// データを発生日、開始日時でソート
		List<HoshuMeisaiListDto> sotedList = hoshuMeisaiListDtoList.stream()
				.sorted(Comparator.comparing(HoshuMeisaiListDto::getHasseiDateDate, Comparator.nullsFirst(LocalDate::compareTo))
						.thenComparing(Comparator.comparing(HoshuMeisaiListDto::getTimeChargeStartTime, Comparator.nullsFirst(LocalDateTime::compareTo))))
				.collect(Collectors.toList());

		return sotedList;
	}

	// ****************************************************************
	// 会計記録
	// ****************************************************************
	/**
	 * 入出金情報の新規登録を行います。
	 *
	 * @param kaikeiKirokuDto 入出金情報のDto
	 * @throws AppException
	 */
	private void saveNyushukkin(KaikeiKirokuDto kaikeiKirokuDto) throws AppException {

		// Dto -> Entity に変換します。
		TKaikeiKirokuEntity kaikeiKirokuEntity = this.convertTKaikeiKirokuEntity(kaikeiKirokuDto);

		try {
			// 登録処理を行います。
			tKaikeiKirokuDao.insert(kaikeiKirokuEntity);

		} catch (OptimisticLockException ex) {
			// エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * 入出金情報を更新します。
	 *
	 * @param inputKaikeiKirokuDto 入出金情報のDto
	 * @throws AppException
	 */
	private void updateNyushukkin(KaikeiKirokuDto inputKaikeiKirokuDto) throws AppException {
		// --------------------------------------------------------
		// 更新対象の情報を取得します。
		// --------------------------------------------------------
		// 入出金情報
		TKaikeiKirokuEntity kaikeiKirokuEntity = tKaikeiKirokuDao.selectByKaikeiKirokuSeq(inputKaikeiKirokuDto.getKaikeiKirokuSeq());
		if (kaikeiKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入出金予定情報
		TNyushukkinYoteiEntity nyushukkinYoteiEntity = null;
		if (kaikeiKirokuEntity.getNyushukkinYoteiSeq() != null) {
			// 入出金予定情報も更新します。
			nyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(kaikeiKirokuEntity.getNyushukkinYoteiSeq());
		}

		// --------------------------------------------------------
		// 入力した情報をEntityに設定します。
		// --------------------------------------------------------
		// 入出金情報
		this.setTKaikeiKirokuEntity(inputKaikeiKirokuDto, kaikeiKirokuEntity);

		// 入出金予定情報
		if (nyushukkinYoteiEntity != null) {
			this.setTNyushukkinYoteiEntity(inputKaikeiKirokuDto, nyushukkinYoteiEntity);
		}

		try {
			// 更新します。
			tKaikeiKirokuDao.update(kaikeiKirokuEntity);
			// 更新します。
			Optional.ofNullable(nyushukkinYoteiEntity).ifPresent(tNyushukkinYoteiDao::update);

		} catch (OptimisticLockException ex) {
			// エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	/**
	 * 入出金予定情報の更新<br>
	 *
	 * <pre>
	 * 入出金情報に紐づく予定情報に対する操作
	 * </pre>
	 *
	 * @param nyushukkinYoteiSeq 入出金予定SEQ
	 * @throws AppException
	 */
	private void updateYoteiForNyushukkin(Long nyushukkinYoteiSeq) throws AppException {

		// 入出金予定SEQに紐づく入出金予定情報を取得する。
		TNyushukkinYoteiEntity nyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);

		if (nyushukkinYoteiEntity != null) {
			// 実績を削除する。
			nyushukkinYoteiEntity.setNyushukkinDate(null);
			nyushukkinYoteiEntity.setNyushukkinGaku(null);

			try {
				// 更新処理
				tNyushukkinYoteiDao.update(nyushukkinYoteiEntity);
			} catch (OptimisticLockException ex) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00013, ex);
			}
		}
	}

	/**
	 * 入出金情報を更新します。<br>
	 *
	 * <pre>
	 * 入出金情報削除時、精算処理日が登録済みの場合は
	 * 精算処理日をリセットします。
	 * </pre>
	 *
	 * @param seisanSeq 精算SEQ
	 * @throws AppException
	 */
	private void updateNyushukkin(Long seisanSeq) throws AppException {

		List<TKaikeiKirokuEntity> updateTargetList = new ArrayList<TKaikeiKirokuEntity>();

		// 精算SEQに紐づく入出金情報を取得します。
		List<TKaikeiKirokuEntity> kaikeiKirokuEntityList = tKaikeiKirokuDao.selectBySeisanSeq(seisanSeq);

		// 取得結果がなければなにもしない
		if (ListUtils.isEmpty(kaikeiKirokuEntityList)) {
			return;
		}

		// 更新するデータだけ抽出して加工 条件 -> 精算日が登録済
		for (TKaikeiKirokuEntity entity : kaikeiKirokuEntityList) {
			if (entity.getSeisanDate() != null) {
				entity.setSeisanDate(null);
				updateTargetList.add(entity);
			}
		}

		try {
			// 更新します。
			updateTargetList.forEach(tKaikeiKirokuDao::update);
		} catch (OptimisticLockException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

	}

	/**
	 * kaikeiKirokuBean -> KaikeiKirokuDto
	 *
	 * @param kaikeiKirokuBean 会計記録情報Bean
	 * @return 会計記録情報を管理するDtoクラス
	 */
	private KaikeiKirokuDto setKaikeiKirokuDto(KaikeiKirokuBean kaikeiKirokuBean) {

		KaikeiKirokuDto kaikeiKirokuDto = new KaikeiKirokuDto();

		// Beanの値をDtoに設定します。
		kaikeiKirokuDto.setKaikeiKirokuSeq(kaikeiKirokuBean.getKaikeiKirokuSeq());
		kaikeiKirokuDto.setCustomerId(kaikeiKirokuBean.getCustomerId());
		kaikeiKirokuDto.setAnkenId(kaikeiKirokuBean.getAnkenId());
		kaikeiKirokuDto.setHasseiDate(DateUtils.parseToString(kaikeiKirokuBean.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		kaikeiKirokuDto.setNyushukkinKomokuId(kaikeiKirokuBean.getNyushukkinKomokuId());
		kaikeiKirokuDto.setNyushukkinType(kaikeiKirokuBean.getNyushukkinType());
		if (NyushukkinType.NYUKIN.getCd().equals(kaikeiKirokuBean.getNyushukkinType())) {
			kaikeiKirokuDto.setNyukinKomokuId(kaikeiKirokuBean.getNyushukkinKomokuId());
		} else {
			kaikeiKirokuDto.setShukkinKomokuId(kaikeiKirokuBean.getNyushukkinKomokuId());
		}
		kaikeiKirokuDto.setTaxFlg(kaikeiKirokuBean.getTaxFlg());
		kaikeiKirokuDto.setTaxText(DefaultEnum.getVal(DefaultEnum.getEnum(TaxFlg.class, kaikeiKirokuBean.getTaxFlg())));

		kaikeiKirokuDto.setNyukinGaku(kaikeiKirokuBean.getNyukinGaku());
		kaikeiKirokuDto.setShukkinGaku(kaikeiKirokuBean.getShukkinGaku());
		kaikeiKirokuDto.setTekiyo(kaikeiKirokuBean.getTekiyo());
		kaikeiKirokuDto.setSeisanSeq(kaikeiKirokuBean.getSeisanSeq());
		kaikeiKirokuDto.setNyushukkinYoteiSeq(kaikeiKirokuBean.getNyushukkinYoteiSeq());
		kaikeiKirokuDto.setYoteiSeisanSeq(kaikeiKirokuBean.getYoteiSeisanSeq());
		kaikeiKirokuDto.setNyushukkinYoteiSeq(kaikeiKirokuBean.getNyushukkinYoteiSeq());
		if (kaikeiKirokuBean.getCreatedBy() == null || kaikeiKirokuBean.getCreatedAt() == null) {
			kaikeiKirokuDto.setDispCreatedByNameDateTime("");
		} else {
			String CreatedByName = commonAccountiService.getAccountName(kaikeiKirokuBean.getCreatedBy());
			kaikeiKirokuDto.setDispCreatedByNameDateTime(
					CreatedByName + " " + DateUtils.parseToString(kaikeiKirokuBean.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		}
		if (kaikeiKirokuBean.getLastEditBy() == null || kaikeiKirokuBean.getLastEditAt() == null) {
			kaikeiKirokuDto.setDispLastEditByNameDateTime("");
		} else {
			String LastEditByName = commonAccountiService.getAccountName(kaikeiKirokuBean.getLastEditBy());
			kaikeiKirokuDto.setDispLastEditByNameDateTime(LastEditByName + " "
					+ DateUtils.parseToString(kaikeiKirokuBean.getLastEditAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		}

		// 金額を BigDecimal -> String に変換します。
		if (NyushukkinType.NYUKIN.getCd().equals(kaikeiKirokuBean.getNyushukkinType())) {
			// 入金の場合
			kaikeiKirokuDto.setKingaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuDto.getNyukinGaku()));

		} else if (NyushukkinType.SHUKKIN.getCd().equals(kaikeiKirokuBean.getNyushukkinType())) {
			// 出金の場合
			kaikeiKirokuDto.setKingaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuDto.getShukkinGaku()));
		}

		if (kaikeiKirokuBean.getPoolType() != null) {
			// プール種別が取得できる = プールした(された)会計記録情報
			kaikeiKirokuDto.setPool(true);
		} else {
			kaikeiKirokuDto.setPool(false);
		}

		return kaikeiKirokuDto;
	}

	/**
	 * KaikeiKirokuDto -> TKaikeiKirokuEntity の設定処理<br>
	 *
	 * <pre>
	 * 新規登録用です。
	 * </pre>
	 *
	 * @param kaikeiKirokuDto 入出金情報を管理するDto
	 * @return 入出金Entity
	 * @throws AppException
	 */
	private TKaikeiKirokuEntity convertTKaikeiKirokuEntity(KaikeiKirokuDto kaikeiKirokuDto) throws AppException {

		TKaikeiKirokuEntity kaikeiKirokuEntity = new TKaikeiKirokuEntity();

		// --------------------------------------------------
		// Dto -> Entity
		// --------------------------------------------------
		kaikeiKirokuEntity.setKaikeiKirokuSeq(kaikeiKirokuDto.getKaikeiKirokuSeq());
		kaikeiKirokuEntity.setCustomerId(kaikeiKirokuDto.getCustomerId());
		kaikeiKirokuEntity.setAnkenId(kaikeiKirokuDto.getAnkenId());
		kaikeiKirokuEntity.setHasseiDate(DateUtils.parseToLocalDate(kaikeiKirokuDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		kaikeiKirokuEntity.setNyushukkinType(kaikeiKirokuDto.getNyushukkinType());
		kaikeiKirokuEntity.setNyushukkinKomokuId(kaikeiKirokuDto.getNyushukkinKomokuId());
		kaikeiKirokuEntity.setTekiyo(kaikeiKirokuDto.getTekiyo());
		kaikeiKirokuEntity.setUncollectableFlg(SystemFlg.FLG_OFF.getCd());

		if (NyushukkinType.NYUKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
			kaikeiKirokuEntity.setNyushukkinKomokuId(kaikeiKirokuDto.getNyukinKomokuId());

		} else if (NyushukkinType.SHUKKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
			kaikeiKirokuEntity.setNyushukkinKomokuId(kaikeiKirokuDto.getShukkinKomokuId());
		}

		// --------------------------------------------------
		// String -> BigDecimal
		// --------------------------------------------------
		LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
		decimalFormat.setParseBigDecimal(true);

		BigDecimal kingaku = new BigDecimal(0);
		String kingakuStr = kaikeiKirokuDto.getKingaku();

		try {
			// 金額
			if (!StringUtils.isEmpty(kingakuStr)) {
				kingaku = (BigDecimal) decimalFormat.parse(kingakuStr);

				if (NyushukkinType.NYUKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
					// 入金の場合
					kaikeiKirokuEntity.setNyukinGaku(kingaku);
					kaikeiKirokuEntity.setShukkinGaku(null);

				} else if (NyushukkinType.SHUKKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
					// 出金の場合
					kaikeiKirokuEntity.setShukkinGaku(kingaku);
					kaikeiKirokuEntity.setNyukinGaku(null);
				}
			}

		} catch (ParseException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + "：" + kingakuStr + "が正しく変換できませんでした。");
			throw new AppException(MessageEnum.MSG_E00030, ex);
		}

		// --------------------------------------------------
		// 消費税率の特定
		// --------------------------------------------------
		MNyushukkinKomokuEntity nyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(kaikeiKirokuEntity.getNyushukkinKomokuId());
		if (nyushukkinKomokuEntity != null) {

			LocalDate hasseiDate = DateUtils.parseToLocalDate(kaikeiKirokuDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			String taxFlg = nyushukkinKomokuEntity.getTaxFlg();
			kaikeiKirokuEntity.setTaxFlg(taxFlg);

			if (!TaxFlg.FREE_TAX.getCd().equals(taxFlg)) {
				// 課税の場合は消費税率を特定します。
				String taxRate = this.getTaxRate(kaikeiKirokuDto.getAnkenId(), kaikeiKirokuDto.getCustomerId(), hasseiDate);
				kaikeiKirokuEntity.setTaxRate(taxRate);

				// ※外税、内税に関わらず、税額の設定は行わない
			}
		}
		return kaikeiKirokuEntity;
	}

	/**
	 * KaikeiKirokuDto -> TKaikeiKirokuEntity の設定処理<br>
	 *
	 * <pre>
	 * 更新用です。
	 * </pre>
	 *
	 * @param kaikeiKirokuDto 入出金情報を管理するDto
	 * @param kaikeiKirokuEntity 入出金情報Entity
	 * @throws AppException
	 */
	private void setTKaikeiKirokuEntity(KaikeiKirokuDto kaikeiKirokuDto, TKaikeiKirokuEntity kaikeiKirokuEntity) throws AppException {

		// --------------------------------------------------
		// Dto -> Entity
		// --------------------------------------------------
		kaikeiKirokuEntity.setKaikeiKirokuSeq(kaikeiKirokuDto.getKaikeiKirokuSeq());
		kaikeiKirokuEntity.setCustomerId(kaikeiKirokuDto.getCustomerId());
		kaikeiKirokuEntity.setAnkenId(kaikeiKirokuDto.getAnkenId());
		kaikeiKirokuEntity.setHasseiDate(DateUtils.parseToLocalDate(kaikeiKirokuDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		kaikeiKirokuEntity.setNyushukkinType(kaikeiKirokuDto.getNyushukkinType());
		kaikeiKirokuEntity.setNyushukkinKomokuId(kaikeiKirokuDto.getNyushukkinKomokuId());
		kaikeiKirokuEntity.setUncollectableFlg(SystemFlg.FLG_OFF.getCd());

		if (NyushukkinType.NYUKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
			kaikeiKirokuEntity.setNyushukkinKomokuId(kaikeiKirokuDto.getNyukinKomokuId());

		} else if (NyushukkinType.SHUKKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
			kaikeiKirokuEntity.setNyushukkinKomokuId(kaikeiKirokuDto.getShukkinKomokuId());
		}

		// --------------------------------------------------
		// String -> BigDecimal
		// --------------------------------------------------
		LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
		decimalFormat.setParseBigDecimal(true);

		BigDecimal kingaku = new BigDecimal(0);
		String kingakuStr = kaikeiKirokuDto.getKingaku();

		try {
			// 金額
			if (!StringUtils.isEmpty(kingakuStr)) {
				kingaku = (BigDecimal) decimalFormat.parse(kingakuStr);
				if (NyushukkinType.NYUKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
					// 入金の場合
					kaikeiKirokuEntity.setNyukinGaku(kingaku);
					kaikeiKirokuEntity.setShukkinGaku(null);

				} else if (NyushukkinType.SHUKKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
					// 出金の場合
					kaikeiKirokuEntity.setShukkinGaku(kingaku);
					kaikeiKirokuEntity.setNyukinGaku(null);
				}
			}
		} catch (ParseException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + "：" + kingakuStr + "が正しく変換できませんでした。");
			throw new AppException(MessageEnum.MSG_E00030, ex);
		}
		// --------------------------------------------------
		// 消費税率を特定します。
		// --------------------------------------------------
		MNyushukkinKomokuEntity nyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(kaikeiKirokuEntity.getNyushukkinKomokuId());
		if (nyushukkinKomokuEntity != null) {

			LocalDate hasseiDate = DateUtils.parseToLocalDate(kaikeiKirokuDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			String taxFlg = nyushukkinKomokuEntity.getTaxFlg();
			kaikeiKirokuEntity.setTaxFlg(taxFlg);

			if (!taxFlg.equals(TaxFlg.FREE_TAX.getCd())) {
				// 課税の場合は消費税率を特定します。
				String taxRate = this.getTaxRate(kaikeiKirokuDto.getAnkenId(), kaikeiKirokuDto.getCustomerId(), hasseiDate);
				kaikeiKirokuEntity.setTaxRate(taxRate);
				// 税額は設定しない（NULL更新）
				kaikeiKirokuEntity.setTaxGaku(null);
			} else {
				kaikeiKirokuEntity.setTaxRate(null);
				kaikeiKirokuEntity.setTaxGaku(null);
			}
		}

		kaikeiKirokuEntity.setTekiyo(kaikeiKirokuDto.getTekiyo());
		kaikeiKirokuEntity.setLastEditAt(LocalDateTime.now());
		Long accountSeq = SessionUtils.getLoginAccountSeq();
		kaikeiKirokuEntity.setLastEditBy(accountSeq);
	}

	/**
	 * KaikeiKirokuDto -> TNyushukkinYoteiEntity の設定処理<br>
	 *
	 * <pre>
	 * 更新用です。
	 * </pre>
	 *
	 * @param kaikeiKirokuDto 入出金情報を管理するDto
	 * @param TNyushukkinYoteiEntity 入出金情報予定Entity
	 */
	private void setTNyushukkinYoteiEntity(KaikeiKirokuDto kaikeiKirokuDto, TNyushukkinYoteiEntity nyushukkinYoteiEntity) throws AppException {
		// --------------------------------------------------
		// String -> BigDecimal
		// --------------------------------------------------
		LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
		decimalFormat.setParseBigDecimal(true);

		BigDecimal kingaku = new BigDecimal(0);
		String kingakuStr = kaikeiKirokuDto.getKingaku();
		try {
			// 金額
			if (!StringUtils.isEmpty(kingakuStr)) {
				kingaku = (BigDecimal) decimalFormat.parse(kingakuStr);
				nyushukkinYoteiEntity.setNyushukkinGaku(kingaku);
			}

		} catch (ParseException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + "：" + kingakuStr + "が正しく変換できませんでした。");
			throw new AppException(MessageEnum.MSG_E00030, ex);
		}
		// --------------------------------------------------
		// Dto -> Entity
		// --------------------------------------------------
		nyushukkinYoteiEntity.setNyushukkinDate(DateUtils.parseToLocalDate(kaikeiKirokuDto.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		nyushukkinYoteiEntity.setAnkenId(kaikeiKirokuDto.getAnkenId());
		nyushukkinYoteiEntity.setCustomerId(kaikeiKirokuDto.getCustomerId());
		if (NyushukkinType.NYUKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
			nyushukkinYoteiEntity.setNyushukkinKomokuId(kaikeiKirokuDto.getNyukinKomokuId());
		} else if (NyushukkinType.SHUKKIN.getCd().equals(kaikeiKirokuDto.getNyushukkinType())) {
			nyushukkinYoteiEntity.setNyushukkinKomokuId(kaikeiKirokuDto.getShukkinKomokuId());
		}
		nyushukkinYoteiEntity.setTekiyo(kaikeiKirokuDto.getTekiyo());
		nyushukkinYoteiEntity.setLastEditAt(LocalDateTime.now());
		Long accountSeq = SessionUtils.getLoginAccountSeq();
		nyushukkinYoteiEntity.setLastEditBy(accountSeq);
	}

	// ****************************************************************
	// 入出金予定
	// ****************************************************************

	/**
	 * Entity -> Dto の設定処理<br>
	 *
	 * <pre>
	 * 新規登録用
	 * </pre>
	 *
	 * @param nyushukkinKomokuEntityList 入出金項目情報のEntityリスト
	 * @return 入出金項目のDtoリスト
	 */
	private List<NyushukkinKomokuListDto> setNyushukkinKomokuDtoForNew(List<MNyushukkinKomokuEntity> nyushukkinKomokuEntityList) {

		List<NyushukkinKomokuListDto> nyushukkinKomokuList = new ArrayList<NyushukkinKomokuListDto>();

		for (MNyushukkinKomokuEntity entity : nyushukkinKomokuEntityList) {

			// Entityの値をDtoに設定します。
			NyushukkinKomokuListDto nyushukkinKomokuDto = new NyushukkinKomokuListDto();
			nyushukkinKomokuDto.setNyushukkinKomokuId(entity.getNyushukkinKomokuId());
			nyushukkinKomokuDto.setNyushukkinType(entity.getNyushukkinType());
			nyushukkinKomokuDto.setKomokuName(entity.getKomokuName());
			nyushukkinKomokuDto.setTaxFlg(entity.getTaxFlg());
			nyushukkinKomokuDto.setDispOrder(entity.getDispOrder());

			nyushukkinKomokuList.add(nyushukkinKomokuDto);
		}

		return nyushukkinKomokuList;
	}

	/**
	 * Entity -> Dto の設定処理<br>
	 *
	 * <pre>
	 * 更新用
	 * </pre>
	 *
	 * @param nyushukkinKomokuEntityList 入出金項目情報のEntityリスト
	 * @param nyushukkinKomokuId 入出金項目ID
	 * @return 入出金項目のDtoリスト
	 */
	private List<NyushukkinKomokuListDto> setNyushukkinKomokuDtoForUpdate(List<MNyushukkinKomokuEntity> nyushukkinKomokuEntityList,
			Long nyushukkinKomokuId) {

		List<NyushukkinKomokuListDto> nyushukkinKomokuList = new ArrayList<NyushukkinKomokuListDto>();

		for (MNyushukkinKomokuEntity entity : nyushukkinKomokuEntityList) {
			// （削除不可フラグがたってないもの（初期データ以外） かつ 無効フラグがたってないもの） か 会計記録で使用されている  入出金項目データをリストにする
			if ((SystemFlg.FLG_OFF.equalsByCode(entity.getUndeletableFlg())
					&& SystemFlg.FLG_OFF.equalsByCode(entity.getDisabledFlg()))
					|| entity.getNyushukkinKomokuId().equals(nyushukkinKomokuId)) {

				// Entityの値をDtoに設定します。
				NyushukkinKomokuListDto nyushukkinKomokuDto = new NyushukkinKomokuListDto();
				nyushukkinKomokuDto.setNyushukkinKomokuId(entity.getNyushukkinKomokuId());
				nyushukkinKomokuDto.setNyushukkinType(entity.getNyushukkinType());
				nyushukkinKomokuDto.setKomokuName(entity.getKomokuName());
				nyushukkinKomokuDto.setTaxFlg(entity.getTaxFlg());
				nyushukkinKomokuDto.setDispOrder(entity.getDispOrder());
				nyushukkinKomokuList.add(nyushukkinKomokuDto);
			}
		}

		return nyushukkinKomokuList;
	}

	/**
	 * 単価と時間をもとに報酬金額を計算します。
	 *
	 * @param tanka 単価
	 * @param decimalMinutes 時間
	 * @throws AppException
	 * @return hourKingaku 報酬金額
	 */
	public BigDecimal hoshuCal(BigDecimal tanka, BigDecimal decimalMinutes) throws AppException {

		BigDecimal sixty = new BigDecimal(60);
		// テナントの報酬の端数の報酬の端数処理を取得します
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		String hoshuHasuType = tenantEntity.getHoshuHasuType();
		RoundingMode roundingMode = null;
		// テナントの報酬の端数の報酬の端数処理に従い丸め処理を設定
		if (HoshuHasuType.TRUNCATION.equalsByCode(hoshuHasuType)) {

			roundingMode = RoundingMode.DOWN;

		} else if (HoshuHasuType.ROUND_UP.equalsByCode(hoshuHasuType)) {

			roundingMode = RoundingMode.UP;

		} else if (HoshuHasuType.ROUND_OFF.equalsByCode(hoshuHasuType)) {

			roundingMode = RoundingMode.HALF_UP;

		} else {
			// 想定外のケース
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
		// 単価 × 時間 を計算します。
		BigDecimal hourKingaku = new BigDecimal(0);

		// テナントの報酬の端数の報酬の端数処理に従い
		hourKingaku = tanka.multiply(decimalMinutes).divide(sixty, 0, roundingMode);
		return hourKingaku;
	}

	/**
	 * 前回の情報から、入金項目と出金項目のいずれかを表示（入出金予定）
	 *
	 * @param transitionAnkenId
	 * @param transitionCustomerId
	 * @return TNyushukkinYoteiEntity
	 */
	private TNyushukkinYoteiEntity getNyukinYoteiFromPrev(Long transitionAnkenId, Long transitionCustomerId) {

		return tNyushukkinYoteiDao.selectPrevByAnkenIdOrCustomerId(transitionAnkenId, transitionCustomerId);
	}
}