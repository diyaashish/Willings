package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.GensenChoshu;
import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.LocalTimePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
public class AnkenMeisaiDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	// t_kaikei_kiroku
	/** 会計記録SEQ */
	private Long kaikeiKirokuSeq;

	/** 顧客ID */
	private Long customerId;

	/** 案件ID */
	private Long ankenId;

	/** 発生日 */
	@LocalDatePattern
	private String hasseiDate;

	/** 報酬項目ID */
	@Required
	@EnumType(value = LawyerHoshu.class)
	private String hoshuKomokuId;

	/** 金額 */
	@MinNumericValue(min = 0)
	private String kingaku;

	/** 「税抜、税込」選択 */
	@Required
	@EnumType(value = TaxFlg.class)
	private String taxFlg;

	/** 消費税金額 */
	private String taxGaku;

	/** タイムチャージ時間の指定方法 */
	@EnumType(value = TimeChargeTimeShitei.class)
	private String timeChargeTimeShitei;

	/** タイムチャージ単価 */
	@MinNumericValue(min = 1)
	private String timeChargeTanka;

	/** タイムチャージ開始日 */
	@LocalDatePattern
	private String timeChargeStartDate;

	/** タイムチャージ開始時間 */
	@LocalTimePattern
	private String timeChargeStartTime;

	/** タイムチャージ終了日 */
	@LocalDatePattern
	private String timeChargeEndDate;

	/** タイムチャージ終了時間 */
	@LocalTimePattern
	private String timeChargeEndTime;

	/** タイムチャージ時間 */
	@MinNumericValue(min = 1)
	private String timeChargeTime;

	/** 消費税率 */
	@Required
	@EnumType(value = TaxRate.class)
	private String taxRate;

	/** 源泉徴収フラグ */
	@Required
	@EnumType(value = GensenChoshu.class)
	private String gensenchoshuFlg;

	/** 源泉徴収額 */
	private String gensenchoshuGaku;

	/** 摘要 */
	@MaxDigit(max = 10000)
	private String tekiyo;

	/** 精算処理日 */
	private String seisanDate;

	/** 精算ID */
	private Long seisanId;

	// t_anken
	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private BunyaDto bunyaId;

	/** 分野表示用 */
	private String dispBunya;

	// t_person
	/** 顧客名 */
	private String customerName;

	// ********************************
	// 画面表示用
	// ********************************
	// 各報酬項目の金額

	/** 報酬計 */
	private String hoshuTotal;

	/** 報酬額 */
	private String hoshuGaku;

	// 画面表示に必要
	/** 源泉徴収計算式1 */
	private String calcFormula1;

	/** 源泉徴収計算式2 */
	private String calcFormula2;

	/** 源泉徴収するかどうか */
	private String withholdingTax;

	/** 表示用登録者名、登録日時 */
	private String dispCreatedByNameDateTime;

	/** 表示用更新者名、更新日時 */
	private String dispLastEditByNameDateTime;

}