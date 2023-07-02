package jp.loioz.app.user.ankenManagement.dto;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.ShobunType;
import jp.loioz.common.constant.DefaultEnum;
import lombok.Builder;
import lombok.Data;

/**
 * 案件顧客-事件情報一覧
 */
@Builder
@Data
public class AnkenCustomerJikenDto {

	/** 事件SEQ */
	private Long jikenSeq;

	/** 案件ID */
	private Long ankenId;

	/** 顧客ID */
	private Long customerId;

	/** 事件名 */
	private String jikenName;

	/** 逮捕日 */
	private String taihoDate;

	/** 勾留請求日 */
	private String koryuSeikyuDate;

	/** 満期日① */
	private String koryuExpirationDate;

	/** 満期日② */
	private String koryuExtendedExpirationDate;

	/** 処分種別 */
	private String shobunType;

	/** 処分日 */
	private String shobunDate;

	/** 備考 */
	private String remarks;

	/**
	 * 処分ラベルを返す
	 * 
	 * @return
	 */
	public String getShobunLabel() {
		if (this.shobunType == null) {
			return CommonConstant.BLANK;
		}
		return DefaultEnum.getVal(ShobunType.of(this.shobunType));
	}

}
