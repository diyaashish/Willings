package jp.loioz.app.user.personManagement.form.personEdit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.personManagement.dto.PersonAddressDto;
import jp.loioz.app.user.personManagement.dto.PersonContactDto;
import jp.loioz.app.user.personManagement.dto.PersonKozaDto;
import jp.loioz.app.user.personManagement.dto.PersonScheduleListDto;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AddressTypeHojin;
import jp.loioz.common.constant.CommonConstant.AddressTypeKojin;
import jp.loioz.common.constant.CommonConstant.AllowType;
import jp.loioz.common.constant.CommonConstant.ContactCategory;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.dto.AnkenDto;
import lombok.Data;

/**
 * 名簿編集画面の画面表示フォームクラス
 */
@Data
public class PersonEditViewForm {

	/** 名簿の基本情報表示フォーム */
	private PersonBasicViewForm personBasicViewForm;

	/** 名簿の住所情報表示フォーム */
	private PersonAddressViewForm personAddressViewForm;

	/** 名簿の連絡先情報表示フォーム */
	private PersonContactViewForm personContactViewForm;

	/** 名簿の口座情報表示フォーム */
	private PersonKozaViewForm personKozaViewForm;

	/** 名簿の案件情報表示フォーム */
	private CustomerAnkenViewForm customerAnkenViewForm;

	/** 名簿の関連する案件情報表示フォーム */
	private RelatedAnkenViewForm relatedAnkenViewForm;

	/** 名簿の予定情報表示フォーム */
	private PersonScheduleListViewForm personScheduleListViewForm;

	/**
	 * 名簿の基本情報表示フォーム
	 */
	@Data
	public static class PersonBasicViewForm {

		/** 名簿ID */
		private PersonId personId;

		/** 顧客ID */
		private CustomerId customerId;

		/** 名簿属性 */
		private PersonAttribute personAttribute;

		/** 顧客区分 */
		private CustomerType customerType;

		/** 顧客登録日 */
		private LocalDate customerCreatedDate;

		/** 顧客姓・会社名 */
		private String customerNameSei;

		/** 顧客姓かな・会社名かな */
		private String customerNameSeiKana;

		/** 顧客名 個人のみ必須 */
		private String customerNameMei;

		/** 顧客名かな */
		private String customerNameMeiKana;

		/** 顧客かどうか（委任契約書のダウンロード可否判定） */
		private boolean customerFlg;

		// 個人顧客付帯情報

		/** 旧姓 */
		private String oldName;

		/** 旧姓かな */
		private String oldNameKana;

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

		/** 国籍 */
		private String country;

		/** 言語 */
		private String language;

		/** 屋号・通称 */
		private String yago;

		/** 屋号・通称かな */
		private String yagoKana;

		/** 職業 */
		private String job;

		/** 勤務先 */
		private String workPlace;

		/** 部署 */
		private String bushoName;

		/** 故人フラグ */
		private Boolean deathFlg;

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

		// 法人顧客付帯情報

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

		/** 旧商号 */
		private String oldHojinName;
		
		/** 特記事項 */
		private String remarks;

		/** 相談経路 */
		private String sodanRouteText;

		/** 相談経路備考 */
		private String sodanRemarks;

		// 弁護士付帯情報

		/** 事務所名 */
		private String lawyerJimushoName;

		/** 部署 */
		private String lawyerBushoName;

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
		 * 死亡年月日（西暦形式）文字列を取得（uuuu年 M月 d日）
		 * 
		 * @return
		 */
		public String getDeathDateSeireki() {

			LocalDate deathDate = this.makeDeathDate();
			if (deathDate == null) {
				return "";
			}

			return DateUtils.parseToString(deathDate, DateUtils.DATE_JP_YYYY_MM_DD_ZERO_FILL);
		}

		/**
		 * 死亡年月日の年（和暦形式）文字列を元号付きで取得（(元号y年)）
		 * 
		 * @return
		 */
		public String getDeathDateWareki() {

			LocalDate deathDate = this.makeDeathDate();
			if (deathDate == null) {
				return "";
			}

			return DateUtils.getWarekiYear(deathDate);
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

	}

	/**
	 * 名簿の住所情報表示フォーム
	 */
	@Data
	public static class PersonAddressViewForm {

		/** 顧客タイプ */
		private CustomerType customerType;

		/** 個人の住所タイプリスト */
		private List<AddressTypeKojin> addressTypeKojinList = new ArrayList<>();

		/** 法人の住所タイプリスト */
		private List<AddressTypeHojin> addressTypeHojinList = new ArrayList<>();

		/** 住所表示テーブルデータリスト */
		private List<PersonAddressDto> personAddressRowDtoList = new ArrayList<>();

		/**
		 * 住所追加の選択肢を取得する
		 * 
		 * @return
		 */
		public List<AddAddressSelectOption> getAddAddressSelectOptions() {

			List<AddAddressSelectOption> selectOptions = new ArrayList<>();

			// 居住地／所在地を追加するための選択肢

			// 居住地／所在地が既に登録されているかどうか
			boolean baseAddressAlreadyRegist = personAddressRowDtoList.stream()
					.anyMatch(dto -> dto.isBaseAddress());
			if (!baseAddressAlreadyRegist) {
				// まだ登録されていない場合は居住地／所在地を追加するための選択肢を追加
				AddAddressSelectOption baseAddressOption = new AddAddressSelectOption();
				baseAddressOption.setBaseAddressOption(true);
				if (this.customerType == CustomerType.KOJIN) {
					baseAddressOption.setTitle(PersonAddressDto.BASE_ADDRESS_TITLE_KOJIN);
				} else if (this.customerType == CustomerType.HOJIN) {
					baseAddressOption.setTitle(PersonAddressDto.BASE_ADDRESS_TITLE_HOJIN);
				} else {
					baseAddressOption.setTitle(PersonAddressDto.BASE_ADDRESS_TITLE_LAWYER);
				}
				selectOptions.add(baseAddressOption);
			}

			// その他住所を追加するための選択肢

			// 居住地／所在地以外の住所データの数
			long otherAddress = personAddressRowDtoList.stream()
					.filter(dto -> !dto.isBaseAddress())
					.count();
			if (otherAddress < CommonConstant.OTHER_ADDRESS_ADD_LIMIT) {
				// 居住地／所在地以外の住所データの数が上限値以下の場合
				// -> その他の住所を追加するための選択肢を追加
				AddAddressSelectOption otherAddressOption = new AddAddressSelectOption();
				otherAddressOption.setBaseAddressOption(false);
				otherAddressOption.setTitle(PersonAddressDto.OTHER_ADDRESS_TITLE + "（旧住所）");
				selectOptions.add(otherAddressOption);
			}

			return selectOptions;
		}

		/**
		 * 住所追加時の選択肢表示クラス
		 */
		@Data
		public static class AddAddressSelectOption {

			/** 居住地/所在地かどうか */
			private boolean isBaseAddressOption;

			/** 表示タイトル */
			private String title;
		}
	}

	/**
	 * 名簿の連絡先情報表示フォーム
	 */
	@Data
	public static class PersonContactViewForm {

		/** 連絡先一覧 */
		private List<PersonContactDto> contactList = new ArrayList<>();

		// 画面に表示するデータを作るのに必要な値（この値自体は画面に表示しない）

		/** 電話連絡先が追加登録可能かどうかの判定値 */
		private boolean canAddTelContact;
		/** FAX連絡先が追加登録可能かどうかの判定値 */
		private boolean canAddFaxContact;
		/** メールアドレス連絡先が追加登録可能かどうかの判定値 */
		private boolean canAddMailContact;

		/** 連絡可否 */
		private AllowType allowType;

		/** 連絡可否備考 */
		private String allowTypeRemarks;

		/**
		 * 連絡先追加の選択肢を取得する
		 * 
		 * @return
		 */
		public List<AddContactSelectOption> getAddContacSelectOptions() {

			List<AddContactSelectOption> selectOptions = new ArrayList<>();

			if (this.canAddTelContact) {
				selectOptions.add(this.getAddContacSelectOption(ContactCategory.TEL, PersonContactDto.TEL_TITLE));
			}
			if (this.canAddFaxContact) {
				selectOptions.add(this.getAddContacSelectOption(ContactCategory.FAX, PersonContactDto.FAX_TITLE));
			}
			if (this.canAddMailContact) {
				selectOptions.add(this.getAddContacSelectOption(ContactCategory.EMAIL, PersonContactDto.EMAIL_TITLE));
			}

			return selectOptions;
		}

		/**
		 * 指定のカテゴリの連絡先追加の選択肢を取得する
		 * 
		 * @param category
		 * @param displayTitle
		 * @return
		 */
		private AddContactSelectOption getAddContacSelectOption(ContactCategory category, String displayTitle) {

			AddContactSelectOption option = new AddContactSelectOption();
			option.setCategory(category);
			option.setTitle(displayTitle);
			return option;
		}

		/**
		 * 連絡先追加時の選択肢表示クラス
		 */
		@Data
		public static class AddContactSelectOption {

			/** 連絡先のカテゴリ（電話、FAXなど） */
			private ContactCategory category;

			/** 表示タイトル */
			private String title;
		}
	}

	/**
	 * 名簿の口座情報表示フォーム
	 */
	@Data
	public static class PersonKozaViewForm {

		/** 口座情報 */
		private PersonKozaDto koza;

	}

	/**
	 * 名簿の案件情報表示フォーム
	 */
	@Data
	public static class CustomerAnkenViewForm {

		/** 顧客ID */
		private CustomerId customerId;

		/** 案件リスト */
		private List<AnkenDto> ankenList;

		/** 全ての案件を表示するかどうかのチェックボックスの値 */
		private boolean isAllAnkenList;
	}

	/**
	 * 関連する案件情報表示フォーム
	 */
	@Data
	public static class RelatedAnkenViewForm {

		/** 顧客ID */
		private CustomerId customerId;

		/** 案件リスト */
		private List<AnkenDto> ankenList;

		/** 案件数（全ステータスの案件件数） */
		private int ankenCount;

		/** 全ての案件を表示するかどうかのチェックボックスの値 */
		private boolean isAllAnkenList;
	}

	/**
	 * 名簿のスケジュール一覧情報表示フォーム
	 */
	@Data
	public static class PersonScheduleListViewForm {

		/** 名簿ID */
		private Long personId;

		/** 顧客ID */
		private CustomerId customerId;

		/** 顧客名 */
		private String customerName;

		/** 過去の予定を表示するチェック */
		private boolean isAllTimeScheduleList;

		/** 予定一覧：名簿 */
		private List<PersonScheduleListDto> scheduleList = Collections.emptyList();

	}

}