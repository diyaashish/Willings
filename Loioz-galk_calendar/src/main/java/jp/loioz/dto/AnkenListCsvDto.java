package jp.loioz.dto;

import java.time.LocalDate;

import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 案件一覧出力（CSV）情報
 */
@Data
public class AnkenListCsvDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private Long bunyaId;

	/** 分野enum代替クラス */
	private BunyaDto bunya;

	/** 案件登録日 */
	private LocalDate ankenCreatedDate;

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

	/** 担当案件フラグ 1:担当 */
	private String tantoAnkenFlg;

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

	/** 案件ステータス */
	private String ankenStatus;

	/** 顧客登録日 */
	private LocalDate customerCreatedDate;

	/** 受任日 */
	private LocalDate juninDate;

	/** 相手方名 */
	private String aitegataName;

	/** 相手方数 */
	private Long numberOfAitegata;

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
	 * -区切りに編集した案件登録日を取得します。
	 * 
	 * @return
	 */
	public String getAnkenCreatedDateFormat() {
		return DateUtils.parseToString(this.ankenCreatedDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * -区切りに編集した顧客登録日を取得します。
	 * 
	 * @return
	 */
	public String getCustomerCreatedDateFormat() {
		return DateUtils.parseToString(this.customerCreatedDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * -区切りに編集した受任日を取得します。
	 * 
	 * @return
	 */
	public String getJuninDateFormat() {
		return DateUtils.parseToString(this.juninDate, DateUtils.DATE_FORMAT_HYPHEN_DELIMITED);
	}

	/**
	 * 案件ステータスIDから案件ステータス名を取得します
	 * 
	 * @return
	 */
	public String getAnkenStatusName() {
		if (StringUtils.isEmpty(this.ankenStatus)) {
			return "";
		}
		if ("-".equals(this.ankenStatus)) {
			return "-";
		}
		return AnkenStatus.of(this.ankenStatus).getVal();
	}

}