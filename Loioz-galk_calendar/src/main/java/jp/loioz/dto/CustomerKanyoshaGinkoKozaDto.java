package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class CustomerKanyoshaGinkoKozaDto {

	/** ID（顧客ID or 関与者ID） */
	private Long id;

	/** 名前（顧客名 or 関与者名） */
	private String name;

	/** 銀行名 */
	private String ginkoName;

	/** 支店名 */
	private String shitenName;

	/** 支店番号 */
	private String shitenNo;

	/** 口座種類 */
	private String kozaType;

	/** 口座番号 */
	private String kozaNo;

	/** 口座名義 */
	private String kozaName;

	/** ツールチップの内容 */
	private String toolTipContents;

	/**
	 * 画面表示用の銀行口座情報を生成する。<br>
	 * 
	 * <pre>
	 * マウスオーバー時に、ツールチップ内に表示する情報
	 * </pre>
	 * 
	 * @return 銀行口座情報
	 */
	public String getDisplayGinkoKozaInfo() {

		String ginkoKozaInfo = "";

		String displayGinkoName = "";
		String displayShitenName = "";
		String displayShitenNo = "";
		String displayKozaType = "";
		String displayKozaNo = "";
		String displayKozaName = "";

		if (!StringUtils.isEmpty(ginkoName)) {
			displayGinkoName = ginkoName;
		}
		if (!StringUtils.isEmpty(shitenName)) {
			displayShitenName = shitenName;
		}
		if (!StringUtils.isEmpty(shitenNo)) {
			displayShitenNo = shitenNo;
		}
		if (!StringUtils.isEmpty(kozaType)) {
			displayKozaType = DefaultEnum.getVal(DefaultEnum.getEnum(KozaType.class, kozaType));
		}
		if (!StringUtils.isEmpty(kozaNo)) {
			displayKozaNo = kozaNo;
		}
		if (!StringUtils.isEmpty(kozaName)) {
			displayKozaName = kozaName;
		}

		ginkoKozaInfo = "金融機関名：" + displayGinkoName + "\r\n" + "支店名　　：" + displayShitenName + "\r\n" + "支店番号　：" + displayShitenNo + "\r\n" + "口座種類　："
				+ displayKozaType + "\r\n" + "口座番号　：" + displayKozaNo + "\r\n" + "口座名義　：" + displayKozaName;

		return ginkoKozaInfo;
	}

	/**
	 * ツールチップの設定処理
	 */
	public void setDisplayGinkoKozaInfo() {
		this.toolTipContents = this.getDisplayGinkoKozaInfo();
	}
}
