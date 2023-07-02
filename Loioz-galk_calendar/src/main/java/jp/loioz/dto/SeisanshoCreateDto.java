package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.SeisanKirokuKubun;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.domain.value.AnkenId;
import lombok.Data;

@Data
public class SeisanshoCreateDto {
	// ********************************
	// DBと対となる情報
	// ********************************
	/** 精算SEQ */
	private Long seisanSeq;

	/** 精算ステータス */
	@Required // 精算額0円は精算なので判別できないことは想定しない
	private String seisanStatus;

	/** 請求 or 精算 */
	@Required // 精算額0円は精算なので判別できないことは想定しない
	@EnumType(value = SeisanKirokuKubun.class)
	private String seisanKubun;

	/** 精算方法 */
	@EnumType(value = SeikyuType.class)
	private String seikyuType;

	/** 月々の支払額 */
	@MinNumericValue(min = 1)
	private String monthlyPay;

	/** 初回支払日(分割時) */
	private String paymentFirstDay;

	/** 支払日(分割時) */
	@MinNumericValue(min = 1)
	@MaxNumericValue(max = 31)
	private String paymentDate;

	/** 支払日(分割時月末指定) */
	private String lastDay;

	/** 支払予定日(一括時) */
	@LocalDatePattern
	private String paymentYoteiDate;

	/** 支払期限(返金時) */
	@LocalDatePattern
	private String paymentLimitDt;

	/** 端数 */
	private String hasu;

	/** プールフラグ */
	private boolean poolFlg;

	/** 請求-支払者（顧客・関与者・自由入力） */
	@EnumType(value = TargetType.class)
	private String shiharaishaType;

	/** 請求-支払者：顧客ID */
	private Long shiharaiCustomerId;

	/** 請求-支払者：関与者Seq */
	private Long shiharaiKanyoshaSeq;

	/** 請求-口座SEQ */
	private Long seikyuKozaSeq;

	/** 返金-口座SEQ */
	private Long henkinKozaSeq;

	/** 返金-返金先（顧客・関与者・自由入力） */
	@EnumType(value = TargetType.class)
	private String henkinSakiType;

	/** 返金-返金先：顧客ID */
	private Long henkinCustomerId;

	/** 返金-返金先：関与者Seq */
	private Long henkinKanyoshaSeq;

	// ********************************
	// 画面表示用
	// ********************************
	/** 支払開始年(分割時) */
	private String shiharaiStartYear;

	/** 支払開始月(分割時) */
	@MinNumericValue(min = 1)
	@MaxNumericValue(max = 12)
	private String shiharaiStartMonth;

	/** 返金時の返金先口座のユニークID */
	private Long henkinSakiUniqueId;

	/** 精算額をプールする案件ID */
	private Long poolAnkenId;

	/** 精算額をプールする案件ID */
	private AnkenId displayPoolAnkenId;

	/** 精算額をプールする案件情報 */
	private String poolAnkenInfo;

	/** 支払先口座情報1 */
	private String shiharaiKozaInfo1;

	/** 支払先口座情報1 */
	private String shiharaiKozaInfo2;
}