package jp.loioz.dto;

import lombok.Data;

/**
 * 報酬出力（CSV）情報
 */
@Data
public class FeeListCsvDto {

	/** 名簿ID */
	private String personId;

	/** 名前 */
	private String name;

	/** 案件ID */
	private String ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野名 */
	private String bunyaName;

	/** 顧客ステータスCD */
	private String ankenStatus;

	/** 顧客ステータス */
	private String ankenStatusName;

	/** 報酬合計(税込) */
	private String feeTotalAmount;

	/** 未請求（合計） */
	private String feeUnclaimedTotalAmount;

	/** 担当弁護士１ */
	private String tantoLawyerName1;

	/** 担当弁護士２ */
	private String tantoLawyerName2;

	/** 担当弁護士３ */
	private String tantoLawyerName3;

	/** 担当弁護士４ */
	private String tantoLawyerName4;

	/** 担当弁護士５ */
	private String tantoLawyerName5;

	/** 担当弁護士６ */
	private String tantoLawyerName6;

	/** 担当事務１ */
	private String tantoJimuName1;

	/** 担当事務２ */
	private String tantoJimuName2;

	/** 担当事務３ */
	private String tantoJimuName3;

	/** 担当事務４ */
	private String tantoJimuName4;

	/** 担当事務５ */
	private String tantoJimuName5;

	/** 担当事務６ */
	private String tantoJimuName6;

	/** 最終更新日時 */
	private String lastEditAtFormat;
}