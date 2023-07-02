package jp.loioz.app.user.saibanManagement.dto;

import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 刑事裁判付帯情報のDto
 */
@Data
public class SaibanKensatsukanDto {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 本起訴事件SEQ */
	private Long mainJikenSeq;

	/** 捜査機関ID */
	private Long sosakikanId;

	/** 検察庁名 */
	private String kensatsuchoName;

	/** 担当部 */
	private String kensatsuchoTantoBuName;

	/** 検察官名 */
	private String kensatsukanName;

	/** 検察官名かな */
	private String kensatsukanNameKana;

	/** 事務官名 */
	private String jimukanName;

	/** 事務官名かな */
	private String jimukanNameKana;

	/** 検察電話番号 */
	private String kensatsuTelNo;

	/** 検察内線番号 */
	private String kensatsuExtensionNo;

	/** 検察FAX番号 */
	private String kensatsuFaxNo;

	/** 検察号室 */
	private String kensatsuRoomNo;

	/** 検察備考 */
	private String kensatsuRemarks;

	/** 公判担当者が登録済みか判定する */
	public boolean isRegist() {

		if (StringUtils.isAllEmpty(
				kensatsuchoName, kensatsuchoTantoBuName, kensatsuTelNo, kensatsuExtensionNo,
				kensatsuFaxNo, kensatsuRoomNo, kensatsukanName, jimukanName, kensatsuRemarks)
				&& (sosakikanId == null)) {
			return false;
		} else {
			return true;
		}
	}

}
