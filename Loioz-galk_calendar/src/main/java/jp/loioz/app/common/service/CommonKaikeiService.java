package jp.loioz.app.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.GensenChoshuRate;
import jp.loioz.common.constant.CommonConstant.Hasu;
import jp.loioz.common.constant.CommonConstant.NyushukkinKomokuType;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.SeisanKirokuKubun;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.constant.CommonConstant.TaxHasuType;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.KaikeiManagementDao;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TCustomerCommonDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.dto.CustomerKanyoshaGinkoKozaBean;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.GinkoKozaBean;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TGinkoKozaEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TSeisanKirokuEntity;

/**
 * 会計管理の共通サービス
 * TODO 新会計 必要なものはCommonAccgServiceに移動後、削除。 
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonKaikeiService extends DefaultService {

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件-担当者Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 案件-顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 銀行口座のDaoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/** 入出金予定Dao */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** 会計記録Dao */
	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	/** 精算記録Dao */
	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	/** テナントDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 会計管理画面のDaoクラス */
	@Autowired
	private KaikeiManagementDao kaikeiManagementDao;

	/** アカウント情報 共通サービス */
	@Autowired
	private CommonAccountService commonAccountService;

	@Autowired
	private TCustomerCommonDao tCustomerCommonDao;

	/** 金額項目のフォーマット */
	public static final String AMOUNT_LABEL_FORMAT = "#,###.##";

	/** 時間(mm)書き出し正規表現 */
	private static final String DURATION_OUT_REGEX = "%02d";

	/** 時間(HH:mm)読み込み正規表現 */
	private static final String DURATION_IN_REGEX = "^(?<hour>\\d+):(?<minute>\\d{1,2}+)$";

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 実金額を表示用金額に変換する（カンマあり）
	 *
	 * @param rawAmount 実金額
	 * @return 表示用金額
	 */
	public String toDispAmountLabel(BigDecimal rawAmount) {

		if (rawAmount == null) {
			return null;
		}

		DecimalFormat format = new DecimalFormat(AMOUNT_LABEL_FORMAT);
		String dispAmount = format.format(rawAmount);

		return dispAmount;
	}

	/**
	 * 金額の合計値を計算します。
	 *
	 * @param kingakuList 金額リスト
	 * @return 金額の合計値
	 */
	public BigDecimal calcTotal(List<BigDecimal> kingakuList) {

		BigDecimal todalKingaku = new BigDecimal(0);

		if (!ListUtils.isEmpty(kingakuList)) {
			for (BigDecimal kingaku : kingakuList) {
				// 合計値を計算します。
				BigDecimal calcKingaku = new BigDecimal(0);
				calcKingaku = todalKingaku;
				if (kingaku != null) {
					todalKingaku = calcKingaku.add(kingaku);
				}
			}
		}

		return todalKingaku;
	}

	/**
	 * 消費税を計算します。<br>
	 *
	 * <pre>
	 * 課税（内税）用です。<br>
	 * 消費税の計算・端数処理まで行います。
	 * </pre>
	 *
	 * @param kingaku 金額（税込）
	 * @param taxRateCd 消費税率Cd
	 * @param taxHasuType 消費税の端数処理方法
	 * @return 消費税
	 */
	public BigDecimal calcInternalTax(BigDecimal kingaku, String taxRateCd, String taxHasuType) {

		BigDecimal tax = new BigDecimal(0);

		if (taxRateCd != null) {
			// -------------------------------------------------------
			// 消費税の計算
			// -------------------------------------------------------
			// 消費税率
			TaxRate taxRateEnum = DefaultEnum.getEnum(TaxRate.class, taxRateCd);
			String taxRateStr = DefaultEnum.getVal(taxRateEnum);

			BigDecimal taxRate = new BigDecimal(taxRateStr);
			BigDecimal calcTax = new BigDecimal(0);
			BigDecimal calcNum = new BigDecimal(0);
			BigDecimal hundred = new BigDecimal(100);

			// 消費税を計算します。
			// 計算式：金額 × 消費税率(%) ÷ (100 + 消費税率(%))
			// 例：1,080 × 8 ÷ 108
			// ********************************************
			// 本来は、1,080 ÷ 108 × 8 ですが
			// これだと計算途中に小数を考慮しなければならないため
			// 順番を変更して掛け算から先に計算するようにします。
			// ********************************************
			// a = 100 + 消費税率(%)
			calcNum = hundred.add(taxRate);
			// b = 金額 ×消費税率(%)
			calcTax = kingaku.multiply(taxRate);

			// -------------------------------------------------------
			// 消費税の計算・端数処理
			// (消費税 = b ÷ a)
			// -------------------------------------------------------
			if (TaxHasuType.TRUNCATION.equalsByCode(taxHasuType)) {
				// 切り捨て（例：100.45円 → 100円）
				tax = calcTax.divide(calcNum, 0, RoundingMode.DOWN);

			} else if (TaxHasuType.ROUND_UP.equalsByCode(taxHasuType)) {
				// 切り上げ（例：100.45円 → 101円）
				tax = calcTax.divide(calcNum, 0, RoundingMode.UP);

			} else if (TaxHasuType.ROUND_OFF.equalsByCode(taxHasuType)) {
				// 四捨五入（例：100.45円 → 100円、100.5円 → 101円）
				tax = calcTax.divide(calcNum, 0, RoundingMode.HALF_UP);
			}
		}

		return tax;
	}

	/**
	 * 消費税を計算します。<br>
	 *
	 * <pre>
	 * 課税（外税）用です。<br>
	 * 消費税の計算・端数処理まで行います。
	 * </pre>
	 *
	 * @param kingaku 金額（税抜）
	 * @param taxRateCd 消費税率Cd
	 * @param taxHasuType 消費税の端数処理方法
	 * @return 消費税
	 */
	public BigDecimal calcTax(BigDecimal kingaku, String taxRateCd, String taxHasuType) {

		BigDecimal finalTax = new BigDecimal(0);

		if (taxRateCd != null) {
			// -------------------------------------------------------
			// 消費税の計算
			// -------------------------------------------------------
			// 消費税
			BigDecimal tax = new BigDecimal(0);

			// 消費税率
			TaxRate taxRateEnum = DefaultEnum.getEnum(TaxRate.class, taxRateCd);
			String taxRateStr = DefaultEnum.getVal(taxRateEnum);

			BigDecimal taxRate = new BigDecimal(taxRateStr);
			BigDecimal calcTax = new BigDecimal(0);
			BigDecimal hundred = new BigDecimal(100);
			calcTax = taxRate.divide(hundred);

			// 消費税を計算します。
			// （計算式：金額 × 消費税率）
			tax = kingaku.multiply(calcTax);

			// -------------------------------------------------------
			// 端数処理
			// -------------------------------------------------------
			if (TaxHasuType.TRUNCATION.equalsByCode(taxHasuType)) {
				// 切り捨て（例：100.45円 → 100円）
				finalTax = tax.setScale(0, RoundingMode.DOWN);

			} else if (TaxHasuType.ROUND_UP.equalsByCode(taxHasuType)) {
				// 切り上げ（例：100.45円 → 101円）
				finalTax = tax.setScale(0, RoundingMode.UP);

			} else if (TaxHasuType.ROUND_OFF.equalsByCode(taxHasuType)) {
				// 四捨五入（例：100.45円 → 100円、100.5円 → 101円）
				finalTax = tax.setScale(0, RoundingMode.HALF_UP);
			}
		}

		return finalTax;
	}

	/**
	 * 消費税率を文字列で返却
	 *
	 * @param lawyerHoshu
	 * @return 消費税率(文字列)
	 */
	public BigDecimal changeTaxRateDisp(String taxRate) {
		// taxRateCdから消費税率を取得し表示用に変換
		String taxRateDisp = CommonConstant.TaxRate.of(taxRate).getVal();
		BigDecimal src = new BigDecimal(taxRateDisp);
		return src.scaleByPowerOfTen(-2);
	}

	/**
	 * 源泉徴収率(文字列)を取得します。<br>
	 *
	 * @param kingaku 金額
	 * @return 源泉徴収率(文字列)
	 */
	public String getGensenChoshuRate(BigDecimal kingaku) {

		String gensenRateStr = "";

		// 100万円以上かどうか
		BigDecimal oneMillion = new BigDecimal(1000000);
		int isLargerThanOneMillion = kingaku.compareTo(oneMillion);

		if (isLargerThanOneMillion == 1) {
			// 100万円より高額 = 源泉徴収率は20.42%になります。
			gensenRateStr = GensenChoshuRate.TWENTY.getVal();

		} else {
			// 100万円以下 = 源泉徴収率は10.21%になります。
			gensenRateStr = GensenChoshuRate.TEN.getVal();
		}

		return gensenRateStr;
	}

	/**
	 * 源泉徴収額を計算します。<br>
	 *
	 * <pre>
	 * 源泉徴収率の計算から行います。
	 * </pre>
	 *
	 * @param kingaku 金額
	 * @return 源泉徴収額
	 */
	public BigDecimal calcGensenChoshu(BigDecimal kingaku) {

		String gensenRateStr = "";
		BigDecimal tenPerGensen = new BigDecimal(0);
		BigDecimal twentyPerGensen = new BigDecimal(0);
		BigDecimal finalGensen = new BigDecimal(0);

		BigDecimal oneMillion = new BigDecimal(1000000);

		// 100万円との比較
		int isLargerThanOneMillion = kingaku.compareTo(oneMillion);

		if (isLargerThanOneMillion == 1) {
			// **************************
			// 100万円より高額
			// **************************
			// 源泉徴収率が10.21％になる部分の金額
			gensenRateStr = GensenChoshuRate.TEN.getVal();
			tenPerGensen = this.calcGensenChoshu(gensenRateStr, oneMillion);

			// 源泉徴収額が20.42％になる部分の金額
			BigDecimal twentyPerKingaku = kingaku.subtract(oneMillion);
			gensenRateStr = GensenChoshuRate.TWENTY.getVal();
			twentyPerGensen = this.calcGensenChoshu(gensenRateStr, twentyPerKingaku);

			// 源泉徴収率を足します。
			finalGensen = tenPerGensen.add(twentyPerGensen);

		} else {
			// **************************
			// 100万円以下
			// **************************
			// 源泉徴収率は10.21%になります。
			gensenRateStr = GensenChoshuRate.TEN.getVal();

			// 源泉徴収額を計算します。
			finalGensen = this.calcGensenChoshu(gensenRateStr, kingaku);
		}

		return finalGensen;
	}

	/**
	 * 源泉徴収額を計算します。<br>
	 *
	 * <pre>
	 * 源泉徴収率の計算は行わず、金額のみの計算です。
	 * </pre>
	 *
	 * @param gensenRateStr 源泉徴収率の文字列
	 * @param kingaku 金額
	 * @return 源泉徴収額
	 */
	public BigDecimal calcGensenChoshu(String gensenRateStr, BigDecimal kingaku) {

		// 源泉徴収率(小数)に変換します。
		BigDecimal gensenRate = new BigDecimal(gensenRateStr);
		BigDecimal gensenDecimal = BigDecimal.valueOf(0);
		BigDecimal hundred = BigDecimal.valueOf(100);
		gensenDecimal = gensenRate.divide(hundred);

		// -------------------------------------------------------
		// 源泉徴収額の計算
		// -------------------------------------------------------
		// 金額 × 源泉徴収率
		BigDecimal gensenGaku = BigDecimal.valueOf(0);
		gensenGaku = kingaku.multiply(gensenDecimal);

		// *******************************
		// 小数点は切り捨てます。
		// ※国税庁発行の下記の源泉徴収の計算方法に従い、端数は切り捨てる。
		// https://www.nta.go.jp/taxes/tetsuzuki/shinsei/annai/gensen/fukko/pdf/02.pdf
		// *******************************
		BigDecimal roundDownGensen = gensenGaku.setScale(0, RoundingMode.DOWN);

		return roundDownGensen;
	}

	/**
	 * 分割支払の回数を計算します。<br>
	 *
	 * <pre>
	 * 精算書作成時、支払計画の再計算時に使用します。
	 * </pre>
	 *
	 * @param totalKingaku 合計金額
	 * @param monthKingaku 月々の支払額
	 * @return 分割支払いの回数
	 */
	public BigDecimal calcBunkatsuKaisu(BigDecimal totalKingaku, BigDecimal monthKingaku) throws AppException {

		BigDecimal bunkatsuCount = BigDecimal.valueOf(0);
		bunkatsuCount = totalKingaku.divide(monthKingaku, RoundingMode.DOWN);

		// 回数が許容範囲内か確認する
		try {
			int count = bunkatsuCount.intValueExact();
			if (count <= 0 || count > CommonConstant.BUNKATU_LIMIT_COUNT) {
				// 分割回数が0以下 または、100より大きい場合、エラーとする
				throw new AppException(MessageEnum.MSG_E00062, null);
			}

		} catch (ArithmeticException e) {
			// 共通処理で小数点は切られているので、intで許容できない値の場合
			throw new AppException(MessageEnum.MSG_E00062, e);
		}

		return bunkatsuCount;
	}

	/**
	 * 精算記録から精算処理の計算を行う
	 *
	 * @param entity
	 * @return
	 */
	public List<TNyushukkinYoteiEntity> clacSeisanYotei(@NonNull TSeisanKirokuEntity tSeisanKirokuEntity) throws AppException {

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
			jisseki = this.calcTotal(resultList.stream().map(TNyushukkinYoteiEntity::getNyushukkinGaku).collect(Collectors.toList()));
		}

		// 残金を算出する ※精算額 - 実績(新規作成の場合は実績は0円の想定)
		zankin = seisanGaku.subtract(jisseki);

		// 一括の場合は、残金の総額分 1件のデータを作成
		if (SeikyuType.IKKATSU.equalsByCode(tSeisanKirokuEntity.getSeikyuType())) {
			TNyushukkinYoteiEntity entity = this.createNyushukkinYoteiBySeisanKiroku(tSeisanKirokuEntity);
			entity.setNyushukkinYoteiGaku(zankin);
			entity.setNyushukkinYoteiDate(tSeisanKirokuEntity.getShiharaiDt());
			resultList.add(entity);
			return resultList;
		}

		// 分割回数を算出
		BigDecimal count = this.calcBunkatsuKaisu(zankin, tSeisanKirokuEntity.getMonthShiharaiGaku());
		int bunkatsuCount = count.intValue();
		int allBunkatsuCount = bunkatsuCount + resultList.size();
		if (allBunkatsuCount <= 0 || allBunkatsuCount > CommonConstant.BUNKATU_LIMIT_COUNT) {
			// 分割回数が0以下 または、100より大きい場合、エラーとする
			throw new AppException(MessageEnum.MSG_E00062, null);
		}

		// 端数金額を算出する
		BigDecimal totalNyukinYoteiGaku = tSeisanKirokuEntity.getMonthShiharaiGaku().multiply(count);
		BigDecimal hasuGaku = zankin.subtract(totalNyukinYoteiGaku);

		// 設定する日付
		Function<Integer, LocalDate> pulsMonth = num -> {

			if (DateUtils.END_OF_MONTH.equals(tSeisanKirokuEntity.getMonthShiharaiDate())) {
				LocalDate pulsedMonth = LocalDate.of(tSeisanKirokuEntity.getShiharaiStartDt().getYear(),
						tSeisanKirokuEntity.getShiharaiStartDt().getMonthValue(), Integer.parseInt(DateUtils.START_OF_MONTH)).plusMonths(num);
				return DateUtils.getLastDateOfThisMonth(pulsedMonth);
			}

			try {
				return LocalDate.of(tSeisanKirokuEntity.getShiharaiStartDt().getYear(), tSeisanKirokuEntity.getShiharaiStartDt().getMonthValue(),
						Integer.parseInt(tSeisanKirokuEntity.getMonthShiharaiDate())).plusMonths(num);
			} catch (DateTimeException e) {
				LocalDate firstDaysOfMonth = LocalDate
						.of(tSeisanKirokuEntity.getShiharaiStartDt().getYear(), tSeisanKirokuEntity.getShiharaiStartDt().getMonthValue(), 1)
						.plusMonths(num);
				return DateUtils.getLastDateOfThisMonth(firstDaysOfMonth);
			}
		};

		for (int i = 0; i < bunkatsuCount; i++) {
			TNyushukkinYoteiEntity entity = this.createNyushukkinYoteiBySeisanKiroku(tSeisanKirokuEntity);
			entity.setNyushukkinYoteiDate(pulsMonth.apply(i));
			if (Hasu.FIRST.equalsByCode(tSeisanKirokuEntity.getHasu()) && i == 0) {
				entity.setNyushukkinYoteiGaku(tSeisanKirokuEntity.getMonthShiharaiGaku().add(hasuGaku));
			} else if (Hasu.LAST.equalsByCode(tSeisanKirokuEntity.getHasu()) && i == bunkatsuCount - 1) {
				entity.setNyushukkinYoteiGaku(tSeisanKirokuEntity.getMonthShiharaiGaku().add(hasuGaku));
			} else {
				entity.setNyushukkinYoteiGaku(tSeisanKirokuEntity.getMonthShiharaiGaku());
			}
			resultList.add(entity);
		}

		return resultList;
	}

	/**
	 * 精算記録Entity から 入出金予定を作成する ※ 入出金予定日、予定額は計算する必要があるため未設定
	 *
	 * @param tSeisanKirokuEntity
	 * @return
	 */
	public TNyushukkinYoteiEntity createNyushukkinYoteiBySeisanKiroku(@NonNull TSeisanKirokuEntity tSeisanKirokuEntity) {

		// 入出金予定Entity
		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = new TNyushukkinYoteiEntity();

		// 各情報を設定します。
		tNyushukkinYoteiEntity.setSeisanSeq(tSeisanKirokuEntity.getSeisanSeq());
		tNyushukkinYoteiEntity.setCustomerId(tSeisanKirokuEntity.getCustomerId());
		tNyushukkinYoteiEntity.setAnkenId(tSeisanKirokuEntity.getAnkenId());

		if (SeisanKirokuKubun.SEISAN.equalsByCode(tSeisanKirokuEntity.getSeisanKubun())) {
			// 精算の場合 (返金)
			tNyushukkinYoteiEntity.setNyushukkinType(NyushukkinType.SHUKKIN.getCd());
			tNyushukkinYoteiEntity.setNyushukkinKomokuId(Long.valueOf(NyushukkinKomokuType.HENKIN.getCd()));
			tNyushukkinYoteiEntity.setShukkinSakiKozaSeq(tSeisanKirokuEntity.getHenkinKozaSeq());
			tNyushukkinYoteiEntity.setShukkinShiharaiSakiType(tSeisanKirokuEntity.getHenkinSakiType());
			if (TargetType.CUSTOMER.equalsByCode(tSeisanKirokuEntity.getHenkinSakiType())) {
				tNyushukkinYoteiEntity.setShukkinCustomerId(tSeisanKirokuEntity.getHenkinCustomerId());
			} else if (TargetType.KANYOSHA.equalsByCode(tSeisanKirokuEntity.getHenkinSakiType())) {
				tNyushukkinYoteiEntity.setShukkinKanyoshaSeq(tSeisanKirokuEntity.getHenkinKanyoshaSeq());
			}
		} else if (SeisanKirokuKubun.SEIKYU.equalsByCode(tSeisanKirokuEntity.getSeisanKubun())) {
			// 請求
			tNyushukkinYoteiEntity.setNyushukkinType(NyushukkinType.NYUKIN.getCd());
			tNyushukkinYoteiEntity.setNyushukkinKomokuId(Long.valueOf(NyushukkinKomokuType.SEIKYU.getCd()));
			tNyushukkinYoteiEntity.setNyukinSakiKozaSeq(tSeisanKirokuEntity.getSeikyuKozaSeq());
			tNyushukkinYoteiEntity.setNyukinShiharaishaType(tSeisanKirokuEntity.getSeikyuShiharaishaType());
			if (TargetType.CUSTOMER.equalsByCode(tSeisanKirokuEntity.getSeikyuShiharaishaType())) {
				tNyushukkinYoteiEntity.setNyukinCustomerId(tSeisanKirokuEntity.getSeikyuCustomerId());
			} else if (TargetType.KANYOSHA.equalsByCode(tSeisanKirokuEntity.getSeikyuShiharaishaType())) {
				// 関与者から選択する場合は、関与者IDを設定する。
				tNyushukkinYoteiEntity.setNyukinKanyoshaSeq(tSeisanKirokuEntity.getSeikyuKanyoshaSeq());
			}

		} else {
			// 想定外のケース
			throw new RuntimeException("Enum値が正常ではありません");
		}

		return tNyushukkinYoteiEntity;
	}

	/**
	 * 残金の取得
	 *
	 * @param seisanSeq
	 * @return
	 */
	public BigDecimal getZankin(@NonNull Long seisanSeq) {

		// 精算記録の取得
		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		if (tSeisanKirokuEntity == null) {
			// 精算記録が取得できない場合
			throw new RuntimeException("精算記録が取得できませんでした。 [seisanSeq : " + seisanSeq + "]");
		}
		// 精算に紐づく入出金予定を取得
		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);
		if (ListUtils.isEmpty(tNyushukkinYoteiEntities)) {
			// 精算に紐づく入出金予定がない場合
			throw new RuntimeException("精算に紐づく入出金予定が取得できませんでした。 [seisanSeq : " + seisanSeq + "]");
		}

		BigDecimal seisanGaku = tSeisanKirokuEntity.getSeisanGaku();
		BigDecimal jissekiTotal = tNyushukkinYoteiEntities.stream()
				.map(TNyushukkinYoteiEntity::getNyushukkinGaku)
				.filter(Objects::nonNull)
				.reduce(BigDecimal::add).orElse(new BigDecimal(0));

		return seisanGaku.subtract(jissekiTotal);
	}

	/**
	 * 事務所／担当弁護士の銀行口座情報を取得します。
	 *
	 * @param ankenId 案件ID
	 * @return 事務所／担当弁護士の口座情報
	 */
	public List<GinkoKozaDto> getTenantAccountGinkoKozaList(Long ankenId) {

		// マスタ情報の取得
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// 案件情報の取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
		List<Long> salesOwnerAccountSeqList = tAnkenTantoDao.selectByAnkenIdAndTantoType(ankenId, TantoType.SALES_OWNER.getCd());

		// 口座情報の取得
		List<TGinkoKozaEntity> tenantKozaList = tGinkoKozaDao.selectKozaListByTenantSeq(SessionUtils.getTenantSeq());
		List<TGinkoKozaEntity> salesOwnerKozaList = tGinkoKozaDao.selectKozaListByAccountSeqList(salesOwnerAccountSeqList);

		if (tAnkenEntity == null) {
			// データの取得ができませんでした
			throw new DataNotFoundException("ankenId:" + ankenId);
		}

		// Entity -> Bean 変換条件
		Function<TGinkoKozaEntity, GinkoKozaBean> mapper = entity -> {
			GinkoKozaBean bean = new GinkoKozaBean();
			BeanUtils.copyProperties(entity, bean);
			if (entity.getTenantSeq() != null) {
				bean.setTenantName(mTenantEntity.getTenantName());
			} else if (entity.getAccountSeq() != null) {
				bean.setAccountName(accountNameMap.get(entity.getAccountSeq()));
			} else {
				// 想定外のケース
				throw new RuntimeException();
			}
			return bean;
		};

		List<GinkoKozaBean> kozaBeanList = Collections.emptyList();
		if (AnkenType.JIMUSHO.equalsByCode(tAnkenEntity.getAnkenType())) {
			// 事務所の場合、表示する口座情報は「テナント口座」と「売上計上先の口座情報」
			kozaBeanList = LoiozCollectionUtils.mergeLists(tenantKozaList, salesOwnerKozaList).stream()
					.map(entity -> mapper.apply(entity))
					.collect(Collectors.toList());
		} else if (AnkenType.KOJIN.equalsByCode(tAnkenEntity.getAnkenType())) {
			// 個人の場合、表示する口座情報は「売上計上先の口座情報」
			kozaBeanList = salesOwnerKozaList.stream()
					.map(entity -> mapper.apply(entity))
					.collect(Collectors.toList());
		} else {
			// 想定外のケース
			throw new RuntimeException();
		}

		// Bean -> Dtoにして返却
		return kozaBeanList.stream()
				.map(bean -> setGinkoKozaDto(bean, false))
				.filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toList());
	}

	/**
	 * 銀行口座SEQをキーとして、銀行口座情報を取得します
	 *
	 * <pre>
	 * 編集画面表示時に、削除済みアカウントの口座情報でも登録されている口座情報はプルダウンに表示するため
	 * </pre>
	 *
	 * @param kozaSeq
	 * @return
	 */
	public void margeRelateKozaDto(List<GinkoKozaDto> pullDownList, Long ginkoAccountSeq) {

		// 銀行口座情報の取得
		GinkoKozaBean bean = tGinkoKozaDao.selectBeanBySeq(ginkoAccountSeq);

		if (bean == null) {
			// データが取得できなかった場合(すでに物理削除済など)なにもしない
			return;
		}

		// bean -> dto
		GinkoKozaDto dto = this.setGinkoKozaDto(bean, true).orElse(new GinkoKozaDto());

		dto.setDisplayName(dto.getListDisplayName());
		// 重複をデータもしくはDtoが空でなければ追加
		if (!pullDownList.stream().anyMatch(list -> Objects.equals(list.getGinkoAccountSeq(), dto.getGinkoAccountSeq()))) {
			pullDownList.add(dto);
		}

	}

	/**
	 * 銀行口座情報(事務所、案件担当)の取得を行います。
	 *
	 * @param shiharaiPlanEditForm 支払計画情報の編集用フォーム
	 * @throws AppException
	 */
	public GinkoKozaDto getKozaDetail(Long ginkoAccountSeq) throws AppException {

		GinkoKozaDto ginkoKozaDto = new GinkoKozaDto();

		// 口座情報の取得
		TGinkoKozaEntity entity = tGinkoKozaDao.selectByGinkoAccountSeq(ginkoAccountSeq);

		if (entity != null) {
			ginkoKozaDto.setGinkoAccountSeq(ginkoAccountSeq);
			ginkoKozaDto.setGinkoName(StringUtils.defaultString(entity.getGinkoName()));
			ginkoKozaDto.setKozaName(StringUtils.defaultString(entity.getKozaName()));
			ginkoKozaDto.setKozaNo(StringUtils.defaultString(entity.getKozaNo()));
			ginkoKozaDto.setKozaType(StringUtils.defaultString(entity.getKozaType()));
			ginkoKozaDto.setLabelName(StringUtils.defaultString(entity.getLabelName()));
			ginkoKozaDto.setShitenName(StringUtils.defaultString(entity.getShitenName()));
			ginkoKozaDto.setShitenNo(StringUtils.defaultString(entity.getShitenNo()));
		}

		return ginkoKozaDto;
	}

	/**
	 * 銀行口座情報(顧客)の取得を行います。
	 *
	 * @param customerId
	 * @return
	 */
	public GinkoKozaDto getCustomerKozaDetail(Long customerId) {

		GinkoKozaDto ginkoKozaDto = new GinkoKozaDto();
		CustomerKanyoshaGinkoKozaBean bean = kaikeiManagementDao.selectPersonGinkoKozaDetail(customerId);

		if (bean != null) {
			String ginkoName = bean.getGinkoName();
			String kozaName = bean.getKozaName();
			String kozaNo = bean.getKozaNo();
			String kozaType = bean.getKozaType();
			String shitenName = bean.getShitenName();
			String shitenNo = bean.getShitenNo();

			if (!StringUtils.isAllEmpty(ginkoName, kozaName, kozaNo, kozaType, shitenName, shitenNo)) {
				ginkoKozaDto.setGinkoName(StringUtils.defaultString(ginkoName));
				ginkoKozaDto.setKozaName(StringUtils.defaultString(kozaName));
				ginkoKozaDto.setKozaNo(StringUtils.defaultString(kozaNo));
				ginkoKozaDto.setKozaType(StringUtils.defaultString(kozaType));
				ginkoKozaDto.setShitenName(StringUtils.defaultString(shitenName));
				ginkoKozaDto.setShitenNo(StringUtils.defaultString(shitenNo));
			} else {
				return null;
			}
		} else {
			return null;
		}

		return ginkoKozaDto;
	}

	/**
	 * 銀行口座情報(関与者)の取得を行います。
	 *
	 * @param kanyoshaSeq
	 * @return
	 */
	public GinkoKozaDto getKanyoshaKozaDetail(Long kanyoshaSeq) {

		GinkoKozaDto ginkoKozaDto = new GinkoKozaDto();
		CustomerKanyoshaGinkoKozaBean bean = kaikeiManagementDao.selectKanyoshaGinkoKozaDetailByKanyoshaSeq(kanyoshaSeq);

		if (bean != null) {

			String ginkoName = bean.getGinkoName();
			String kozaName = bean.getKozaName();
			String kozaNo = bean.getKozaNo();
			String kozaType = bean.getKozaType();
			String shitenName = bean.getShitenName();
			String shitenNo = bean.getShitenNo();

			if (!StringUtils.isAllEmpty(ginkoName, kozaName, kozaNo, kozaType, shitenName, shitenNo)) {
				ginkoKozaDto.setGinkoName(StringUtils.defaultString(ginkoName));
				ginkoKozaDto.setKozaName(StringUtils.defaultString(kozaName));
				ginkoKozaDto.setKozaNo(StringUtils.defaultString(kozaNo));
				ginkoKozaDto.setKozaType(StringUtils.defaultString(kozaType));
				ginkoKozaDto.setShitenName(StringUtils.defaultString(shitenName));
				ginkoKozaDto.setShitenNo(StringUtils.defaultString(shitenNo));
			} else {
				return null;
			}
		}
		return ginkoKozaDto;
	}

	/**
	 * 案件に紐づく関与者のプルダウンを取得します
	 *
	 * @param ankenId
	 * @return
	 */
	public List<CustomerKanyoshaPulldownDto> customerKanyoshaPulldownList(Long ankenId) {

		List<CustomerKanyoshaPulldownDto> pullDown = tCustomerCommonDao.selectKanyoshaPulldownByAnkenId(ankenId);
		if (ListUtils.isEmpty(pullDown)) {
			return Collections.emptyList();
		}
		return pullDown;
	}

	/**
	 * TODO 日跨ぎを考慮した際に見直した。<br>
	 * 開始時間と終了時間の差分(経過時間)を求めます。<br>
	 *
	 * <pre>
	 * 弁護士報酬項目が「タイムチャージ」の時用です。
	 * </pre>
	 *
	 * @param startTimeStr 開始時間文字列
	 * @param endTimeStr 終了時間文字列
	 * @return 経過時間文字列
	 */
	public String calcStartAndEndTime(String startTimeStr, String endTimeStr) {

		String timeStr = "";

		Duration startTime = this.timeStringToDuration(startTimeStr);
		Duration endTime = this.timeStringToDuration(endTimeStr);

		// タイムチャージ時間 = 終了時刻 - 開始時刻
		Duration time = endTime.minus(startTime);
		timeStr = this.durationToTimeString(time);

		return timeStr;
	}

	/**
	 * 開始日時と終了日時の差分(経過時間)を求めます。<br>
	 *
	 * <pre>
	 * 弁護士報酬項目が「タイムチャージ」の時用です。
	 * </pre>
	 *
	 * @param startDateStr 開始日
	 * @param startTimeStr 開始時間
	 * @param endDateStr 終了日
	 * @param endTimeStr 終了時間
	 * @return 経過時間
	 */
	public Long calcDifferenceStartAndEndTime(String startDateStr, String startTimeStr, String endDateStr, String endTimeStr) {

		// どれかnullならnullで返却
		if (StringUtils.isAnyEmpty(startDateStr, startTimeStr, endDateStr, endTimeStr)) {
			return null;
		}

		// LocalDateTime型に変換
		LocalDateTime startDateAndTime = LocalDateTime.of(
				DateUtils.parseToLocalDate(startDateStr, DateUtils.DATE_FORMAT_SLASH_DELIMITED),
				DateUtils.parseToLocalTime(startTimeStr, DateUtils.TIME_FORMAT_HHMM));
		LocalDateTime endDateAndTime = LocalDateTime.of(
				DateUtils.parseToLocalDate(endDateStr, DateUtils.DATE_FORMAT_SLASH_DELIMITED),
				DateUtils.parseToLocalTime(endTimeStr, DateUtils.TIME_FORMAT_HHMM));

		// 開始時間と終了時間の差分を求めます。
		Long minutes = calcDifferenceStartAndEndTime(startDateAndTime, endDateAndTime);

		return minutes;
	}

	/**
	 * 開始日時と終了日時の差分(経過時間)を求める
	 *
	 * @param start 開始日時
	 * @param end 終了日時
	 * @return 差分の時間
	 */
	public Long calcDifferenceStartAndEndTime(LocalDateTime start, LocalDateTime end) {

		// 開始時間と終了時間の差分を求めます。
		return ChronoUnit.MINUTES.between(start, end);
	}

	/**
	 * 残りの精算額を計算します。
	 *
	 * @param seisanGaku
	 * @param registeAmountList
	 * @return 残金
	 */
	public BigDecimal calcRemaining(BigDecimal seisanGaku, List<BigDecimal> registeAmountList) {

		// 引数チェック
		if (Objects.isNull(seisanGaku)) {
			return null;
		}

		BigDecimal zankin = new BigDecimal(0);
		BigDecimal registedAmount = calcTotal(registeAmountList);

		// 残金を計算
		zankin = seisanGaku.subtract(registedAmount);
		return zankin;
	}

	/**
	 * 精算完了かどうかを確認します
	 *
	 * @param customerId
	 * @param ankenId
	 * @return 精算完了かどうか
	 */
	public boolean isSeisanComplete(Long customerId, Long ankenId) {

		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);

		if (tAnkenCustomerEntity == null) {
			// データの取得
			throw new DataNotFoundException("顧客ID : " + customerId + CommonConstant.SPACE + "案件ID : " + ankenId);
		}

		// 完了の場合
		if (AnkenStatus.isSeisanComp(tAnkenCustomerEntity.getAnkenStatus())) {
			return true;
		} else {
			return false;
		}

	}
	
	/**
	 * 編集不可メッセージの表示フラグの設定を行う
	 * 
	 * @param kanryoDate 精算完了日
	 * @param ankenStatusCd 案件ステータスCD
	 * @param setShowAnkenCompletedMsg 案件完了メッセージ表示フラグのsetter
	 * @param setShowSeisanCompletedMsg 精算済みメッセージ表示フラグのsetter
	 */
	public void setShowCantEditMsgFlg(LocalDate kanryoDate, String ankenStatusCd, Consumer<Boolean> setShowAnkenCompletedMsg, Consumer<Boolean> setShowSeisanCompletedMsg) {
		
		if (kanryoDate != null && AnkenStatus.isComp(ankenStatusCd)) {
			// 精算完了日、完了チェックの両方設定されている
			
			setShowAnkenCompletedMsg.accept(true);
		} else if (kanryoDate != null) {
			// 精算完了日が設定されている（完了チェックは未設定）
			
			setShowSeisanCompletedMsg.accept(true);
		} else if (AnkenStatus.isComp(ankenStatusCd)) {
			// 完了チェックが設定されている（精算完了日は未設定）
			
			setShowAnkenCompletedMsg.accept(true);
		} else {
			// 精算完了日、完了チェックの両方が未設定
			// -> なにも設定しない
		}
	}

	/**
	 * このテナントに旧会計管理データが存在するかをチェックする
	 * 
	 * @return
	 */
	public boolean isExistsOldKaikeiDataOnTenant() {
		
		// 入出金予定
		int nyushukkinDataCount = tNyushukkinYoteiDao.selectNyushukkinYoteiCount();
		// 会計記録
		int kaikeiDataCount = tKaikeiKirokuDao.selectKaikeiKirokuCount();
		
		// ※ 精算記録データのチェックはしない -> 精算記録データは、会計記録データがないと作成できないため、会計データの存在チェックとしては、会計記録のチェックのみをすればよい。
		
		if (nyushukkinDataCount > 0 || kaikeiDataCount > 0) {
			// 会計データが存在する
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 対象の名簿に会計データが存在するか（会計データを持っているか）をチェックする
	 * 
	 * <pre>
	 * ※注意
	 * 「対象の名簿に会計データが存在するか」のチェックのみで、
	 * 「対象の名簿が会計データで使われているか」のチェックはしていないので注意すること。
	 * 
	 * 例として、対象の名簿が、入出金予定の支払い先や、精算記録の請求先などで利用されていたとしても、
	 * 対象の名簿に会計データ（入出金予定、会計記録）が作成されていなければ、このメソッドの戻り値はfalseになる。
	 * </pre>
	 * 
	 * @param personId
	 * @return
	 */
	public boolean isExistsOldKaikeiDataOnPerson(Long personId) {
		
		// 入出金予定
		List<TNyushukkinYoteiEntity> nyushukkinData = tNyushukkinYoteiDao.selectByCustomerId(personId);
		// 会計記録
		List<TKaikeiKirokuEntity> kaikeiData = tKaikeiKirokuDao.selectByCustomerId(personId);
		
		// ※ 精算記録データのチェックはしない -> 精算記録データは、会計記録データがないと作成できないため、会計データの存在チェックとしては、会計記録のチェックのみをすればよい。
		
		if (!CollectionUtils.isEmpty(nyushukkinData) || !CollectionUtils.isEmpty(kaikeiData)) {
			// 会計データが存在する
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 対象の案件に会計データが存在するか（会計データを持っているか）をチェックする
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean isExistsOldKaikeiDataOnAnken(Long ankenId) {
		
		// 入出金予定
		List<TNyushukkinYoteiEntity> nyushukkinData = tNyushukkinYoteiDao.selectByAnkenId(ankenId);
		// 会計記録
		List<TKaikeiKirokuEntity> kaikeiData = tKaikeiKirokuDao.selectByAnkenId(ankenId);
		
		// ※ 精算記録データのチェックはしない -> 精算記録データは、会計記録データがないと作成できないため、会計データの存在チェックとしては、会計記録のチェックのみをすればよい。
		
		if (!CollectionUtils.isEmpty(nyushukkinData) || !CollectionUtils.isEmpty(kaikeiData)) {
			// 会計データが存在する
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 会計データを全て削除します
	 * 
	 */
	public void deleteAllKaikeiData() {
		// 会計記録データを全て削除
		tKaikeiKirokuDao.deleteAllTKaikeiKiroku();
		// 精算記録データを全て削除
		tSeisanKirokuDao.deleteAllTSisanKiroku();
		// 入出金予定データを全て削除
		tNyushukkinYoteiDao.deleteAllTNyushukkinYotei();
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * GinkoKozaBean -> GinkoKozaDto ※表示用プルダウンで使用することを想定 プルダウン表示条件外のものはNULLの可能性がある
	 *
	 * @param GinkoKozaBean 銀行口座情報のBean
	 * @param isDeleted 編集時のプルダウンは入力情報が空でも表示する
	 * @return 銀行口座情報のDto
	 */
	private Optional<GinkoKozaDto> setGinkoKozaDto(GinkoKozaBean bean, boolean isDeleted) {

		// Entityの値をDtoに設定します。
		GinkoKozaDto ginkoKozaDto = new GinkoKozaDto();

		String labelName = bean.getLabelName();
		String ginkoName = bean.getGinkoName();
		String shitenName = bean.getShitenName();
		String shitenNo = bean.getShitenNo();
		String kozaType = bean.getKozaType();
		String kozaNo = bean.getKozaNo();
		String kozaName = bean.getKozaName();

		if (!StringUtils.isAllEmpty(labelName, ginkoName, shitenName, shitenNo, kozaType, kozaNo, kozaName) || isDeleted) {
			// 表示名、金融機関名、支店名、支店番号、口座種類、口座番号、口座名義 のうち
			// いずれかが入力されている場合のみ リストとして表示する。

			// ---------------------------------------------------------------
			// 銀行口座
			// ---------------------------------------------------------------
			ginkoKozaDto.setGinkoAccountSeq(bean.getGinkoAccountSeq());
			ginkoKozaDto.setLabelName(StringUtils.defaultString(labelName));
			ginkoKozaDto.setGinkoName(StringUtils.defaultString(ginkoName));
			ginkoKozaDto.setShitenName(StringUtils.defaultString(shitenName));
			ginkoKozaDto.setShitenNo(StringUtils.defaultString(shitenNo));
			ginkoKozaDto.setKozaType(StringUtils.defaultString(kozaType));
			ginkoKozaDto.setKozaNo(StringUtils.defaultString(kozaNo));
			ginkoKozaDto.setKozaName(StringUtils.defaultString(kozaName));
			ginkoKozaDto.setTenantSeq(bean.getTenantSeq());
			ginkoKozaDto.setAccountSeq(bean.getAccountSeq());
			ginkoKozaDto.setToolTipContents(ginkoKozaDto.getDisplayGinkoKozaInfo());
			// ---------------------------------------------------------------
			// テナント名
			// ---------------------------------------------------------------
			ginkoKozaDto.setTenantName(bean.getTenantName());
			// ---------------------------------------------------------------
			// アカウント名
			// ---------------------------------------------------------------
			ginkoKozaDto.setAccountName(bean.getAccountName());

			// 表示名の設定
			ginkoKozaDto.setListDisplayName();

		} else {
			ginkoKozaDto = null;
		}
		return Optional.ofNullable(ginkoKozaDto);
	}

	/**
	 * 時分文字列から、{@link Duration}に変換します。<br>
	 * 正しく変換できない場合は、nullを返します。
	 *
	 * @param string 時分文字列
	 * @return {@link Duration} （変換できない場合はnullを返します。）
	 */
	private Duration timeStringToDuration(String string) {

		if (StringUtils.isEmpty(string)) {
			return null;
		}

		// 時間指定において、省略可能な文字列を指定している場合は正規化します。
		// （以降の処理を共通化するため、事前にフォーマット調整をします。
		String adjustedString = adjustTimeStringFormat(string);

		Matcher m = Pattern.compile(DURATION_IN_REGEX).matcher(adjustedString);
		if (m.matches()) {

			long hour = Long.parseLong(m.group("hour"));
			long minute = Long.parseLong(m.group("minute"));

			if (!ChronoField.MINUTE_OF_HOUR.range().isValidValue(minute)) {
				return null;
			}

			Duration d = Duration.ofHours(hour).plusMinutes(minute);
			return d;

		} else {
			return null;
		}
	}

	/**
	 * {@link Duration}から、時分文字列に変換します。<br>
	 * 正しく変換できなければnullを返します。<br>
	 *
	 * <pre>
	 * durationToTimeString([5時間30分]) -> "05:30"
	 * durationToTimeString([145時間05分]) -> "145:05"
	 * durationToTimeString(null) -> null
	 * </pre>
	 *
	 * @param duration 時間
	 * @return 変換できれば時分文字列を、変換できなければnullを返します。
	 */
	private String durationToTimeString(Duration duration) {

		if (duration != null) {

			long minites = duration.toMinutes();

			return String.format(DURATION_OUT_REGEX, minites);

		} else {
			return null;
		}
	}

	/**
	 * 時間文字列を正規フォーマットに調整します。
	 *
	 * @param orgTimeString 元の時間文字列
	 * @return 調整後の文字列を返します。
	 */
	private static String adjustTimeStringFormat(String orgTimeString) {

		String destString = orgTimeString;

		if (orgTimeString.indexOf(":") == -1) {
			// 区切り文字が見つからない場合、省略しているものとして文字列を調整します。
			// ここでは妥当性の確認はせず、フォーマットをHH:MMに変換するだけにします。

			int timeLength = orgTimeString.length();

			if (timeLength == 4) {
				// 4桁の場合、HH:MMに整形します。
				String hh = orgTimeString.substring(0, 2);
				String mm = orgTimeString.substring(2, 4);

				destString = String.format("%s:%s", hh, mm);

			} else if (timeLength == 3) {
				// 3桁の場合、H:MMに整形します。
				String hh = orgTimeString.substring(0, 1);
				String mm = orgTimeString.substring(1, 3);

				destString = String.format("%s:%s", hh, mm);

			} else {
				// 1桁 or 2桁の場合

				if (orgTimeString.equals("0") || orgTimeString.equals("00")) {
					// すべてが0の場合のみ00:00として整形します。
					destString = "00:00";
				}
			}
		}

		return destString;
	}

}