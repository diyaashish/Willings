package jp.loioz.app.common.form;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.ankenManagement.dto.AnkenTantoDispDto;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.dto.AnkenRelatedParentSaibanDto;
import jp.loioz.dto.BunyaDto;
import lombok.Builder;
import lombok.Data;

@Data
public class WrapHeaderForm {

	/** 案件メニューで表示する事件名の最大文字数 */
	private final int MAX_CHAR_COUNT_ANKEN_MENU_JIKEN_NAME = CommonConstant.MAX_CHAR_COUNT_ANKEN_MENU_JIKEN_NAME;

	/** 遷移元種別 */
	private TransitionType transitionType;

	/** 名簿ID */
	private PersonId personId;

	/** 顧客ID */
	private CustomerId customerId;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 顧客名 */
	private String customerName;

	/** 顧客名かな */
	private String customerNameKana;

	/** 顧客-旧姓 */
	private String oldName;

	/** 顧客-登録日 */
	private LocalDate customerCreatedDate;

	/** 顧客種別 */
	private CustomerType customerType;

	/** 故人フラグ */
	private Boolean deathFlg;

	/** 生年月日：年号 */
	private EraType birthEra;

	/** 生年月日：年号紀元 */
	private EraEpoch eraEpoch;

	/** 生年月日：年 */
	private Long birthYear;

	/** 生年月日：月 */
	private Long birthMonth;

	/** 生年月日：日 */
	private Long birthDay;

	/** 性別 */
	private Gender gender;

	/** 死亡年月日：年号 */
	private EraType deathEra;

	/** 死亡年月日：年号紀元 */
	private EraEpoch deathEraEpoch;

	/** 死亡年月日：年 */
	private Long deathYear;

	/** 死亡年月日：月 */
	private Long deathMonth;

	/** 死亡年月日：日 */
	private Long deathDay;

	// 企業・団体

	/** 代表者 */
	private String daihyoName;

	/** 代表者かな */
	private String daihyoNameKana;

	/** 代表者役職 */
	private String daihyoPositionName;

	/** 担当者 */
	private String tantoName;

	/** 担当者かな */
	private String tantoNameKana;

	// 弁護士付帯情報

	/** 事務所名 */
	private String lawyerJimushoName;

	/** 部署 */
	private String lawyerBushoName;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private AnkenName ankenName;

	/** 分野 */
	private BunyaDto bunya;

	/** 案件種別（刑事） */
	private BengoType bengoType;

	/** 刑事分野かどうか */
	private boolean isKeiji;

	/** 表示用「分野-案件名」 */
	private String labelName;

	/** 案件-登録日 */
	private LocalDate ankenCreatedDate;

	/** 案件-区分 */
	private AnkenType ankenType;

	/** 顧客に紐づく案件（未完了） */
	private List<Anken> relatedAnken;

	/** 顧客に紐づく案件（完了、不受任） */
	private List<Anken> relatedAnkenCompleted;

	/** 案件に紐づく顧客情報 */
	private List<Customer> relatedCustomer;

	/** 案件に紐づく相手方情報 */
	private List<Aitegata> relatedAitegata;

	/** 裁判データが存在するか */
	private boolean isExistSaiban;

	/** 裁判SEQ */
	private Long firstSaibanSeq;

	/** 裁判枝番 */
	private Long firstSaibanBranchNo;

	/** 親裁判か判定 */
	private boolean isParentSaiban;

	/** 子裁判 */
	List<AnkenRelatedParentSaibanDto> sideMenuParentSaibanList;

	/** 担当弁護士 */
	private List<AnkenTantoDispDto> tantoLaywer = Collections.emptyList();

	/** 担当事務 */
	private List<AnkenTantoDispDto> tantoJimu = Collections.emptyList();

	/** 旧会計データが存在するか */
	private boolean isExistsOldKaikeiData;

	/** 連携中ストレージ */
	private ExternalService storageConnectedService;

	/** 案件-私選・国選 */
	public String getBengoTypeVal() {
		if (this.bengoType == null) {
			return CommonConstant.BLANK;
		}
		return bengoType.getVal();
	}

	/**
	 * 個人かどうか
	 * 
	 * @return
	 */
	public boolean isKojin() {
		return this.customerType == CommonConstant.CustomerType.KOJIN;
	}

	/**
	 * 法人かどうか
	 * 
	 * @return
	 */
	public boolean isHojin() {
		return this.customerType == CommonConstant.CustomerType.HOJIN;
	}

	/**
	 * 弁護士かどうか
	 * 
	 * @return
	 */
	public boolean isLawyer() {
		return this.customerType == CommonConstant.CustomerType.LAWYER;
	}

	/**
	 * 年齢数を取得
	 * 
	 * @return
	 */
	public String getAgeNumber() {

		// 生年月日が無い場合は年齢を算出できないため空で返す
		LocalDate birthDate = this.makeBirthDate();
		if (birthDate == null) {
			return "";
		}

		// 故人だが死亡日が無い場合は、年齢を算出できないため空で返す
		LocalDate deathDate = makeDeathDate();
		if (deathFlg && deathDate == null) {
			return "";
		}

		// 年齢算出
		Integer age = DateUtils.getAgeNumber(birthDate, deathDate);
		if (age == null) {
			return "";
		}

		return String.valueOf(age);
	}

	/**
	 * 生年月日（西暦形式）文字列を取得（uuuu年 M月 d日）
	 * 
	 * @return
	 */
	public String getBirthdaySeireki() {

		LocalDate birthDate = this.makeBirthDate();
		if (birthDate == null) {
			return "";
		}

		return DateUtils.parseToString(birthDate, DateUtils.DATE_JP_YYYY_MM_DD_ZERO_FILL);
	}

	/**
	 * 生年月日の年（和暦形式）文字列を元号付きで取得（(元号y年)）
	 * 
	 * @return
	 */
	public String getBirthdayWareki() {

		LocalDate birthDate = this.makeBirthDate();
		if (birthDate == null) {
			return "";
		}

		return DateUtils.getWarekiYear(birthDate);
	}

	/**
	 * 生年月日情報を元にLocalDateを生成
	 * 
	 * @return
	 */
	private LocalDate makeBirthDate() {
		boolean anyNull = CommonUtils.anyNull(this.birthEra, this.birthYear, this.birthMonth, this.birthDay);
		if (anyNull) {
			return null;
		}
		LocalDate birthDate = DateUtils.parseToLocalDate(this.birthEra.getCd(), this.birthYear, this.birthMonth, this.birthDay);
		if (birthDate == null) {
			return null;
		}

		return birthDate;
	}

	/**
	 * 死亡年月日情報を元にLocalDateを生成
	 * 
	 * @return
	 */
	private LocalDate makeDeathDate() {
		boolean anyNull = CommonUtils.anyNull(this.deathEra, this.deathYear, this.deathMonth, this.deathDay);
		if (anyNull) {
			return null;
		}
		LocalDate deathDate = DateUtils.parseToLocalDate(this.deathEra.getCd(), this.deathYear, this.deathMonth, this.deathDay);
		if (deathDate == null) {
			return null;
		}

		return deathDate;
	}

	@Data
	@Builder
	public static class Anken {
		/** 案件ID */
		private AnkenId ankenId;
		/** 案件名 */
		private AnkenName ankenName;
		/** 分野 */
		private BunyaDto bunya;
		/** 表示用「分野-案件名」 */
		private String labelName;
		/** 顧客ステータス */
		private String ankenStatus;
	}

	@Data
	@Builder
	public static class Customer {
		/** 顧客ID */
		private CustomerId customerId;
		/** 顧客名 */
		private String customerName;
		/** 顧客ステータス */
		private String ankenStatus;
	}

	@Data
	@Builder
	public static class Aitegata {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 関与者名 */
		private String kanyoshaName;
	}
}
