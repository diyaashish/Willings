package jp.loioz.app.user.seisansho.form;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.SeisanshoCreateDto;
import jp.loioz.dto.SeisanshoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 精算書画面のフォームクラス
 */
@Data
public class SeisanshoViewForm {

	/** 精算対象の案件ID */
	private AnkenId seisanAnkenId;

	/** 精算対象の顧客ID */
	private CustomerId seisanCustomerId;

	/** 精算SEQ */
	private Long seisanSeq;

	// ----------------------------------
	// 遷移元情報
	// ----------------------------------

	/** 遷移元判定フラグ（0：案件管理から遷移、1：顧客管理から遷移 */
	private String transitionFlg;

	/** 遷移元タイプ（1：案件、2：顧客 */
	private String transitionType;

	/** 案件ID */
	private Long transitionAnkenId;

	/** 顧客ID */
	private Long transitionCustomerId;

	/** 案件分野 */
	private Long bunyaId;

	// *****************************************
	// 案件、顧客情報
	// *****************************************

	/** 案件名 */
	private String ankenName;

	/** 分野名 */
	private String bunyaName;

	/** 顧客名 */
	private String customerName;

	/** 口座情報1 */
	private String kozaInfo1;

	/** 口座情報2 */
	private String kozaInfo2;

	/** 報酬一覧情報 */
	private List<SeisanshoDto> seisanHoshuList;

	/** 入出金一覧情報 */
	private List<SeisanshoDto> seisanNyushukkinList;

	// *****************************************
	// 精算合計
	// *****************************************
	/** 入金額の合計 */
	private String kaikeiKirokuNyukinTotal;

	/** 出金額の合計 */
	private String kaikeiKirokuShukkinTotal;

	/** 報酬額の合計 */
	private String kaikeiKirokuHoshuTotal;

	/** 報酬額の小計(報酬計 + 消費税計 - 源泉徴収計) */
	private String kaikeiKirokuHoshuShoukei;

	/** 報酬額の消費税合計 */
	private String kaikeiKirokuHoshuTaxTotal;

	/** 報酬額の消費税8％の合計 */
	private String kaikeiKirokuHoshuTax8perTotal;

	/** 報酬額の消費税10％の合計 */
	private String kaikeiKirokuHoshuTax10perTotal;

	/** 消費税の合計 */
	private String kaikeiKirokuTaxTotal;

	/** 源泉徴収の合計 */
	private String kaikeiKirokuGensenTotal;

	/** 差し引き計 */
	private String kaikeiKirokuSashiHikiTotal;

	/** 精算額の合計 */
	private BigDecimal seisangakuTotal;

	/** 精算額の合計(表示用) */
	private String dispSeisangakuTotal;

	// *****************************************
	// 精算設定
	// *****************************************
	/** 精算書(編集時) */
	private SeisanshoCreateDto seisanshoCreateDto;

	/** プール可能案件 */
	private List<Anken> canPoolAnkenList = Collections.emptyList();

	/** 支払開始年 */
	private List<ShiharaiStartYear> shiharaiStartYearList;

	/** 月々の支払額 */
	private List<MonthShiharaiDate> monthShiharaiDateList;

	/** 事務所・担当弁護士の口座リスト */
	private List<GinkoKozaDto> tenantAccountGinkoKozaList;

	/** 支払者選択関与者リスト */
	private List<CustomerKanyoshaPulldownDto> kanyoshaList;

	/** 精算完了したかどうか(顧客と案件の会計情報) */
	private boolean isSeisanComplete = false;

	/** 精算済の旨のメッセージ表示判定（完了状態の場合はそちらを優先するためfalseになる） */
	private boolean isShowSeisanCompletedMsg = false;
	
	/** 案件ステータスが完了である旨のメッセージ表示判定 */
	private boolean isShowAnkenCompletedMsg = false;
	
	/** 編集可能かどうか */
	private boolean isAbledEdit = false;

	/** 編集不可のメッセージ */
	private String infoMsg;

	/** 会計記録SEQ */
	private Long kaikeiKirokuSeq;

	/**
	 * 案件情報
	 */
	@Data
	@Builder
	@AllArgsConstructor
	public static class Anken {

		/** 案件ID */
		private Long ankenId;

		/** 案件ID(表示用) */
		private AnkenId displayAnkenId;

		/** 分野 */
		private String bunya;

		/** 案件名 */
		private String ankenName;

		/** 区分 */
		private String ankenType;
	}

	/**
	 * 支払開始年
	 */
	@Data
	public static class ShiharaiStartYear {

		/** 日付Cd */
		private String cd;

		/** 日付 */
		private String val;
	}

	/**
	 * 月々の支払日
	 */
	@Data
	public static class MonthShiharaiDate {

		/** 日付Cd */
		private String cd;

		/** 日付 */
		private String val;
	}
}
