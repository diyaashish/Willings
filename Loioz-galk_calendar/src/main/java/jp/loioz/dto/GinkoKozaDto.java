package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.KozaRelateType;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

@Data
public class GinkoKozaDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	// t_ginko_koza
	/** 銀行口座SEQ */
	private Long ginkoAccountSeq;

	/** 口座連番 */
	private Long branchNo;

	/** 表示名 */
	private String labelName;

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

	/** テナントSEQ */
	private Long tenantSeq;

	/** アカウントSEQ */
	private Long accountSeq;

	// m_tenant
	/** テナント名 */
	private String tenantName;

	// m_account
	/** アカウント名 */
	private String accountName;

	/** 口座紐づき種別 */
	private KozaRelateType kozaRelateType;

	// ********************************
	// 画面表示用
	// ********************************
	/** 画面表示用の銀行口座 */
	private String displayName;

	/** ツールチップの中身 */
	private String toolTipContents;

	/**
	 * 画面表示用の文字列を取得します。
	 *
	 * @return 組み合わせ文字列
	 */
	public String getListDisplayName() {

		String displayName = "";

		if (tenantSeq != null) {
			// 事務所口座の場合は、事務所：表示名を表示します。
			displayName = "事務所：" + labelName;
			return displayName;

		} else if (accountSeq != null) {
			// 売上計上先弁護士口座の場合は、
			// 「売上計上先弁護士の名前：口座の表示名」を表示します。
			displayName = accountName + "：" + labelName;
			return displayName;

		}

		return displayName;
	}

	/**
	 * ListDisplayNameのSetter
	 */
	public void setListDisplayName() {
		this.displayName = this.getListDisplayName();
	}

	/**
	 * 画面表示用の銀行口座情報を生成します。<br>
	 *
	 * <pre>
	 * マウスオーバー時に、ツールチップ内に表示する情報です。
	 * </pre>
	 *
	 * @param ginkoKozaBean 銀行口座のBean情報
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
			displayKozaType = DefaultEnum.getVal(KozaType.of(kozaType));

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

	/** ツールチップ?のSetter */
	public void setDisplayGinkoKozaInfo() {
		this.toolTipContents = getDisplayGinkoKozaInfo();
	}

	/** 口座種別Enum */
	public KozaType getKozaTypeEnum() {
		return KozaType.of(this.kozaType);
	}

	/** 銀行口座、支店名、支店番号、口座種別、口座番号、口座名のすべてが空の場合、画面に表示させない */
	public boolean isNoneDisp() {
		return StringUtils.isAllEmpty(ginkoName, shitenName, shitenNo, DefaultEnum.getVal(KozaType.of(kozaType)), kozaNo, kozaName);
	}

	/** 口座の詳細情報を一行表示する */
	public String getKozaInfo() {

		String ginkoName = StringUtils.null2blank(this.ginkoName);
		String shitenName = StringUtils.null2blank(this.shitenName);
		String shitenNo = StringUtils.surroundBrackets(this.shitenNo, false);
		String kozaType = DefaultEnum.getVal(getKozaTypeEnum());
		String kozaNo = StringUtils.null2blank(this.getKozaNo());
		String kozaName = StringUtils.null2blank(this.kozaName);

		return String.join("", ginkoName, shitenName, shitenNo, kozaType, kozaNo, kozaName);
	}

	/**
	 * 口座情報１
	 *
	 * @return
	 */
	public String getDispGinkoKozaInfo1() {
		String kozaInfo1 = CommonConstant.BLANK;

		// 支店番号の入力があれば表示
		if (StringUtils.isNotEmpty(shitenNo)) {
			shitenNo = "(" + shitenNo + ")";
		}
		// 口座情報１
		kozaInfo1 = ginkoName + CommonConstant.SPACE + shitenName + CommonConstant.SPACE + shitenNo;

		return kozaInfo1;
	}

	/**
	 * 口座情報２
	 *
	 * @return
	 */
	public String getDispGinkoKozaInfo2() {
		String kozaInfo2 = CommonConstant.BLANK;

		if (StringUtils.isNotEmpty(kozaType)) {
			kozaType = KozaType.of(kozaType).getVal();
		}
		// 口座情報２
		kozaInfo2 = kozaType + CommonConstant.SPACE + kozaNo + CommonConstant.SPACE + kozaName;

		return kozaInfo2;
	}

	/** 表示用銀行名 */
	public String dispGinkoName() {
		return StringUtils.null2blank(this.ginkoName);
	}

	/** 表示用支店名 */
	public String dispShitenName() {
		return StringUtils.null2blank(this.shitenName);
	}

	/** 表示用支店番号 */
	public String dispShitenNo() {
		return StringUtils.surroundBrackets(this.shitenNo, false);
	}

	/** 表示用口座種別 */
	public String dispKozaType() {
		return StringUtils.null2blank(DefaultEnum.getVal(getKozaTypeEnum()));
	}

	/** 表示用口座番号 */
	public String dispKozaNo() {
		return StringUtils.null2blank(this.getKozaNo());
	}

	/** 表示用口座名 */
	public String dispKozaName() {
		return StringUtils.null2blank(this.kozaName);
	}

}
