package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.BunyaType;
import lombok.Data;

/**
 * 分野情報Dtoクラス<br>
 * 分野の設定（マスタ）画面追加対応<br>
 * enum Bunyaの廃止に伴う、置き換え用クラス。
 */
@Data
public class BunyaDto {

	/** 分野ID */
	private Long bunyaId;
	
	/** 分野区分 */
	private String bunyaType;

	/** 分野名 */
	private String bunyaName;

	/** 表示順 */
	private Long dispOrder;

	/** 無効フラグ */
	private boolean disabledFlg;

	/** 
	 * 分野IDを取得します。<br>
	 * enum Bunya廃止前と同様に利用できるよう<br>
	 * getterとは別に分野Id取得メソッドを定義する
	 */
	public Long getCd() {
		return this.bunyaId;
	}

	/** 
	 * 分野名を取得します。<br>
	 * enum Bunya廃止前と同様に利用できるよう<br>
	 * getterとは別に分野名取得メソッドを定義する
	 */
	public String getVal() {
		return this.bunyaName;
	}
	
	/** 分野区分が刑事か判定 */
	public boolean isKeiji() {
		return BunyaType.KEIJI.equalsByCode(this.bunyaType);
	}
}