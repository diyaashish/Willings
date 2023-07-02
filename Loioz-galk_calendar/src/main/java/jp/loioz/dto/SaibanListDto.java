package jp.loioz.dto;

import java.time.LocalDate;

import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 裁判一覧情報
 */
@Data
public class SaibanListDto {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private Long bunyaId;

	/** 分野enum代替クラス */
	private BunyaDto bunya;

	/** 裁判所 */
	private String saibanshoNameMei;

	/** 事件SEQ */
	private String jikenSeq;

	/** 事件元号 */
	private String jikenGengo;

	/** 事件年 */
	private String jikenYear;

	/** 事件マーク */
	private String jikenMark;

	/** 事件番号 */
	private String jikenNo;

	/** 事件名 */
	private String jikenName;

	/** 名簿ID */
	private Long personId;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客姓 */
	private String customerNameSei;

	/** 顧客姓（かな） */
	private String customerNameSeiKana;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客名（かな） */
	private String customerNameMeiKana;

	/** 顧客数 */
	private Long numberOfCustomer;

	/** 相手方名 */
	private String aitegataName;

	/** 相手方数 */
	private Long numberOfAitegata;

	/** 裁判ステータス */
	private String saibanStatus;

	/** 検察庁 */
	private String kensatsuchoNameMei;

	/** 担当弁護士 */
	private String tantoLaywerName;

	/** 担当事務 */
	private String tantoJimuName;

	/** 申立日／起訴日 */
	private LocalDate saibanStartDate;

	/** 終了日／判決日 */
	private LocalDate saibanEndDate;

	/** 担当案件フラグ 1:担当 */
	private String tantoAnkenFlg;

	/**
	 * 分野IDから分野名を取得します
	 * 
	 * @return
	 */
	public String getBunyaName() {
		if (this.bunyaId == null) {
			return "";
		}
		return this.bunya.getVal();
	}

	/**
	 * 事件番号を取得します<br>
	 * 令和X年（X）第XXX号
	 * 
	 * @return
	 */
	public String getJikenNumber() {
		return CaseNumber.of(EraType.of(this.jikenGengo), this.jikenYear, this.jikenMark, this.jikenNo).toString();
	}

	/**
	 * -区切りに編集した申立日／起訴日を取得します。
	 * 
	 * @return
	 */
	public String getSaibanStartDateFormat() {
		return DateUtils.parseToString(this.saibanStartDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * -区切りに編集した終了日／判決日を取得します。
	 * 
	 * @return
	 */
	public String getSaibanEndDateFormat() {
		return DateUtils.parseToString(this.saibanEndDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * 裁判ステータスIDから裁判ステータス名を取得します
	 * 
	 * @return
	 */
	public String getSaibanStatusName() {
		if (StringUtils.isEmpty(this.saibanStatus)) {
			return "";
		}
		if ("-".equals(this.saibanStatus)) {
			return "-";
		}
		return SaibanStatus.of(this.saibanStatus).getVal();
	}

}