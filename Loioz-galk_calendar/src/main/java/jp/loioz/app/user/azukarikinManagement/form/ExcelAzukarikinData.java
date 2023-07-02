package jp.loioz.app.user.azukarikinManagement.form;

import java.time.LocalDate;

import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

@Data
public class ExcelAzukarikinData {

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 顧客計（表示用） */
	private String dispTotalCustomerKaikei;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private BunyaDto bunya;

	/** 案件ステータス */
	private AnkenStatus ankenStatus;

	/** 売上計上先 */
	private String salesOwnerName;

	/** 担当弁護士 */
	private String tantoLawyerName;

	/** 担当事務 */
	private String tantoZimuName;

	/** 入金（表示用） */
	private String dispTotalNyukin;

	/** 報酬（表示用） */
	private String dispHoshu;

	/** 消費税（表示用） */
	private String dispTotalTax;

	/** 源泉徴収（表示用） */
	private String dispTotalGensen;

	/** 出金（表示用） */
	private String dispTotalShukkin;

	/** 案件計（表示用） */
	private String dispTotalAnkenKaikei;

	/** 受任日 */
	private LocalDate juninDate;

	/** 最終入金日 */
	private LocalDate lastNyukinDate;

	/**
	 * Cell番号に対応する値を取得します
	 * 
	 * @param cellNum
	 * @return
	 */
	public String getCellValue(int cellNum) {

		switch (cellNum) {
		case 0:
			return this.customerId.toString();
		case 1:
			return this.customerName;
		case 2:
			return this.dispTotalCustomerKaikei;
		case 3:
			return this.ankenId.toString();
		case 4:
			return StringUtils.isEmpty(this.ankenName) ? "(案件名未入力)" : this.ankenName.toString();
		case 5:
			return this.ankenStatus.getVal();
		case 6:
			return this.salesOwnerName;
		case 7:
			return this.tantoLawyerName;
		case 8:
			return this.tantoZimuName;
		case 9:
			return this.dispTotalAnkenKaikei;
		case 10:
			return this.dispTotalNyukin;
		case 11:
			return this.dispHoshu;
		case 12:
			return this.dispTotalShukkin;
		case 13:
			return DateUtils.parseToString(this.juninDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		case 14:
			return DateUtils.parseToString(this.lastNyukinDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		default:
			return "";
		}

	}

}
