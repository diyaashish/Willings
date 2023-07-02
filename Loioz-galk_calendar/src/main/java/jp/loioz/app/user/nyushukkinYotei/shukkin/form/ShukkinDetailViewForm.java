package jp.loioz.app.user.nyushukkinYotei.shukkin.form;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchRifineType;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

/**
 * 出金予定画面 詳細情報表示用Formオブジェクト
 */
@Data
public class ShukkinDetailViewForm {

	/** 入出金予定SEQ */
	private Long nyushukkinYoteiSeq;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	// 担当者情報
	/** 担当弁護士名 */
	private List<String> tantoLawyer = Collections.emptyList();

	/** 担当事務名 */
	private List<String> tantoJimu = Collections.emptyList();

	// 支払先情報
	/** 支払先口座情報が設定されていない場合 true */
	private boolean noDataShiharaisakiKoza;

	/** 支払者名 */
	private String shiharaishaName;

	/** 支払者銀行名 */
	private String shiharaishaGinkoName;

	/** 支払者支店名 */
	private String shiharaishaShitenName;

	/** 支払者支店No */
	private String shiharaishaShitenNo;

	/** 支払者口座種別 */
	private KozaType shiharaishaKozaType;

	/** 支払者口座番号 */
	private String shiharaishaKozaNo;

	/** 支払者口座名 */
	private String shiharaishaKozaName;

	// 支払先情報
	/** 支払先名 */
	private String shiharaiSakiName;

	/** 支払先銀行名 */
	private String shiharaiSakiGinkoName;

	/** 支払先銀行支店名 */
	private String shiharaiSakiShitenName;

	/** 支払先銀行支店No */
	private String shiharaiSakiShitenNo;

	/** 支払先銀行支店種別 */
	private KozaType shiharaiSakiKozaType;

	/** 支払先銀行口座番号 */
	private String shiharaiSakiKozaNo;

	/** 支払先銀行口座名 */
	private String shiharaiSakiKozaName;

	/** 入金予定日 */
	private String nyushukkinYoteiDate;

	/** 入金予定額 （計算用） */
	private BigDecimal nyushukkinYoteiGaku;

	/** 入金予定額 （表示用） */
	private String dispNyushukkinYoteiGaku;

	/** 入金日 */
	private String nyushukkinDate;

	/** 入金額（計算用） */
	private BigDecimal nyushukkinGaku;

	/** 入金額（表示用） */
	private String dispNyushukkinGaku;

	/** 精算SEQ */
	private Long seisanSeq;

	/** 精算ID */
	private SeisanId seisanId;

	/** バージョンNo */
	private Long versionNo;

	/** 予定日がすぎているか判断 */
	private boolean isLimitOver;

	/** 状態 */
	private ShukkinSearchRifineType shukkinSearchRifineType;

	/** 精算完了かどうか */
	private boolean isSeisanCompleted = false;

	/**
	 * 支払先口座情報がすべて空か判定
	 * 
	 * @return
	 */
	public boolean shiharaiSakiKozaAllEmpty() {
		return StringUtils.isAllEmpty(
				this.shiharaiSakiGinkoName,
				this.shiharaiSakiShitenName,
				this.shiharaiSakiShitenNo,
				DefaultEnum.getVal(this.shiharaiSakiKozaType),
				this.shiharaiSakiKozaNo,
				this.shiharaiSakiKozaName);
	}

	/**
	 * 支払者口座情報がすべて空か判定
	 * 
	 * @return
	 */
	public boolean shiharaishaKozaAllEmpty() {
		return StringUtils.isAllEmpty(
				this.shiharaishaGinkoName,
				this.shiharaishaShitenName,
				this.shiharaishaShitenNo,
				DefaultEnum.getVal(this.shiharaishaKozaType),
				this.shiharaishaKozaNo,
				this.shiharaishaKozaName);
	}

}
