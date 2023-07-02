package jp.loioz.app.user.personManagement.form.personEdit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.personManagement.dto.PersonAddressDto;
import jp.loioz.app.user.personManagement.dto.PersonContactDto;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.ContactCategory;
import jp.loioz.common.constant.CommonConstant.ContactType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.MailingType;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.EmailPattern;
import jp.loioz.common.validation.annotation.Katakana;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.NumericHyphen;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.TelPattern;
import jp.loioz.common.validation.annotation.ZipPattern;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;
import jp.loioz.common.validation.groups.Lawyer;
import jp.loioz.domain.value.PersonAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 名簿編集画面の入力フォームクラス
 */
@Data
public class PersonEditInputForm {

	/**
	 * 名簿基本情報入力フォーム（兼用）
	 */
	@Data
	public static class PersonBasicInputForm {

		// 表示用データのプロパティ

		/** 名簿ID */
		private Long personId;

		/** 顧客ID */
		private Long customerId;

		/** 名簿属性 */
		private PersonAttribute personAttribute;

		/** 性別選択肢リスト */
		List<SelectOptionForm> genderOptionList = new ArrayList<>();

		/** 相談経路選択肢リスト */
		List<SelectOptionForm> sodanRouteOptionList = new ArrayList<>();

		// 入力用データのプロパティ

		/** 個人・法人区分 */
		@Required
		private CustomerType customerType;

		/** 顧客登録日 */
		@Required
		@DateTimeFormat(pattern = "yyyy/MM/dd")
		private LocalDate customerCreatedDate;

		/** 顧客姓・会社名 */
		@Required
		@MaxDigit.List({
				@MaxDigit(max = 24, groups = Kojin.class),
				@MaxDigit(max = 64, groups = Hojin.class)
		})
		private String customerNameSei;

		/** 顧客姓かな・会社名かな */
		@MaxDigit.List({
				@MaxDigit(max = 64, groups = Kojin.class),
				@MaxDigit(max = 128, groups = Hojin.class)
		})
		private String customerNameSeiKana;

		/** 顧客名 個人のみ */
		@MaxDigit(max = 24, groups = Kojin.class)
		private String customerNameMei;

		/** 顧客名かな 個人のみ */
		@MaxDigit(max = 64, groups = Kojin.class)
		private String customerNameMeiKana;

		// 個人顧客付帯情報

		/** 旧姓 */
		@MaxDigit(max = 24, groups = Kojin.class)
		private String oldName;

		/** 旧姓かな */
		@MaxDigit(max = 64, groups = Kojin.class)
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
		@MaxDigit(max = 30, groups = Kojin.class)
		private String country;

		/** 言語 */
		@MaxDigit(max = 30, groups = Kojin.class)
		private String language;

		/** 屋号・通称 */
		@MaxDigit(max = 24, groups = Kojin.class)
		private String yago;

		/** 屋号・通称かな */
		@MaxDigit(max = 64, groups = Kojin.class)
		private String yagoKana;

		/** 職業 */
		@MaxDigit(max = 50, groups = Kojin.class)
		private String job;

		/** 勤務先 */
		@MaxDigit(max = 50, groups = Kojin.class)
		private String workPlace;

		/** 部署 */
		@MaxDigit(max = 50, groups = Kojin.class)
		private String bushoName;

		/** 死亡フラグ */
		private Boolean deathFlg;

		/** 死亡日：年号 */
		private EraType deathEra;

		/** 死亡日：年号紀元 */
		private EraEpoch deathEraEpoch;

		/** 死亡日：年 */
		private Long deathYear;

		/** 死亡日：月 */
		private Long deathMonth;

		/** 死亡年月日：日 */
		private Long deathDay;

		// 法人顧客付帯情報

		/** 代表者 */
		@MaxDigit(max = 24, groups = Hojin.class)
		private String daihyoName;

		/** 代表者かな */
		@MaxDigit(max = 64, groups = Hojin.class)
		private String daihyoNameKana;

		/** 代表者役職 */
		@MaxDigit(max = 24, groups = Hojin.class)
		private String daihyoPositionName;

		/** 担当者 */
		@MaxDigit(max = 24, groups = Hojin.class)
		private String tantoName;

		/** 担当者かな */
		@MaxDigit(max = 64, groups = Hojin.class)
		private String tantoNameKana;

		/** 旧商号 */
		@MaxDigit(max = 64, groups = Hojin.class)
		private String oldHojinName;
		
		/** 特記事項 */
		@MaxDigit(max = 3000)
		private String remarks;

		/** 相談経路ID */
		private Long sodanRoute;

		/** 相談経路備考 */
		@MaxDigit(max = 500, groups = {Kojin.class, Hojin.class})
		private String sodanRemarks;

		// 弁護士付帯情報

		/** 事務所名 */
		@MaxDigit(max = 50, groups = Lawyer.class)
		private String lawyerJimushoName;

		/** 部署 */
		@MaxDigit(max = 50, groups = Lawyer.class)
		private String lawyerBushoName;

		// hidden（保存時などに必要なパラメータ）

		/** 個人・法人区分（更新前の値） */
		private CustomerType customerTypeBeforeUpdate;

	}

	/**
	 * 名簿の住所情報入力フォーム
	 */
	@Data
	public static class PersonAddressInputForm {

		// 表示、クライアント処理制御用のプロパティ

		/** 他の住所データで既に登録地が指定されているかどうか */
		private boolean isAlreadyExistTourokuAddress;

		/** 他の住所データで既に郵送先が指定されているかどうか */
		private boolean isAlreadyExistTransferAddress;

		// 入力用データのプロパティ

		/** 住所情報 */
		@Valid
		private Address address;

		/** 住民票/登記簿の登録地かどうか */
		private boolean isTourokuAddress;

		/** 郵送先住所かどうか */
		private boolean isTransferAddress;

		/** 郵送方法 */
		private MailingType mailingType;

		// hidden（保存時などに必要なパラメータ）

		/** 顧客タイプ（現在DBに登録されている） */
		private CustomerType customerType;

		/** 居住地/所在地かどうか */
		private boolean isBaseAddress;

		/** その他住所（旧住所）Seq */
		private Long oldAddressSeq;

		/** 住民票/登記簿の登録地かどうか（更新前の値） */
		private boolean isTourokuAddressBeforeUpdate;

		/** 郵送先住所かどうか（更新前の値） */
		private boolean isTransferAddressBeforeUpdate;

		/** 新規登録かどうか */
		private boolean isNewRegist;

		/** 顧客タイプ（画面表示時） */
		private CustomerType viewFormCustomerType;

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
		 * 基本住所の表示名を取得する
		 * 
		 * <pre>
		 * ※入力フォームのタイトル表示に使用
		 * </pre>
		 * 
		 * @return
		 */
		public String getBaseAddressTitle() {
			if (customerType == CustomerType.KOJIN) {
				return PersonAddressDto.BASE_ADDRESS_TITLE_KOJIN;
			} else {
				return PersonAddressDto.BASE_ADDRESS_TITLE_HOJIN;
			}
		}

		/**
		 * その他住所の表示名を取得する
		 * 
		 * <pre>
		 * ※入力フォームのタイトル表示に使用
		 * </pre>
		 * 
		 * @return
		 */
		public String getOtherAddressTitle() {
			return PersonAddressDto.OTHER_ADDRESS_TITLE;
		}

		/**
		 * 登録地の表示名を取得する
		 * 
		 * <pre>
		 * ※チェックボックスのラベル表示に使用
		 * </pre>
		 * 
		 * @return
		 */
		public String getTourokuAddressTitle() {
			if (customerType == CustomerType.KOJIN) {
				return PersonAddressDto.TOUROKU_ADDRESS_TITLE_KOJIN;
			} else if (customerType == CustomerType.HOJIN) {
				return PersonAddressDto.TOUROKU_ADDRESS_TITLE_HOJIN;
			} else {
				return null;
			}
		}

		/**
		 * 郵送先住所の表示名を取得する
		 * 
		 * <pre>
		 * ※チェックボックスのラベル表示に使用
		 * </pre>
		 * 
		 * @return
		 */
		public String getTransferAddressTitle() {
			return PersonAddressDto.TRANSFER_ADDRESS_TITLE;
		}
	}

	/**
	 * 電話連絡情報入力フォーム
	 */
	@Data
	public static class PersonAllowTypeInputForm {

		// 入力用データのプロパティ

		/** 電話連絡可否 */
		private String allowType;

		/** 電話連絡備考 */
		@MaxDigit(max = 500)
		private String allowTypeRemarks;

	}

	/**
	 * 連絡先情報入力フォーム
	 */
	@Data
	public static class PersonContactInputForm {

		// 入力用データのプロパティ

		/** 優先フラグ */
		private boolean yusenFlg;

		/** 区分（自宅、勤務先など） */
		private ContactType type;

		/**
		 * 連絡先（電話）
		 * 
		 * <pre>
		 * ※{@link PersonContactInputForm#category}の値が電話の場合に値を設定する。
		 *   それ以外の場合は利用しない。
		 * </pre>
		 */
		@TelPattern
		@MaxDigit(max = 13)
		private String valueTel;

		/**
		 * 連絡先（FAX）
		 * 
		 * <pre>
		 * ※{@link PersonContactInputForm#category}の値がFAXの場合に値を設定する。
		 *   それ以外の場合は利用しない。
		 * </pre>
		 */
		@TelPattern
		@MaxDigit(max = 13)
		private String valueFax;

		/**
		 * 連絡先（メールアドレス）
		 * 
		 * <pre>
		 * ※{@link PersonContactInputForm#category}の値がメールアドレスの場合に値を設定する。
		 *   それ以外の場合は利用しない。
		 * </pre>
		 */
		@EmailPattern
		@MaxDigit(max = 256)
		private String valueMail;

		/**
		 * 連絡先（備考）
		 */
		@MaxDigit(max = 100)
		private String remarks;

		// hidden（保存時などに必要なパラメータ）

		/** 連絡先レコード連番 */
		private Long contactSeq;

		/** 連絡先カテゴリ（電話、FAXなど） */
		private ContactCategory category;

		/** 同じカテゴリの連絡先データがすでに「優先」選択をしているかどうか */
		private boolean alreadySettingYusenFlg;

		/**
		 * 入力フォームのタイトルを取得
		 * 
		 * @return
		 */
		public String getInputFormTitle() {
			// 現状は連絡先カテゴリのタイトルと同じ
			return this.getCategoryTitle();
		}

		/**
		 * 現在の連絡先カテゴリ用の 連絡先区分の選択肢リストを取得する（自宅、勤務先など）
		 * 
		 * @return
		 */
		public List<ContactTypeSelectOption> getContactTypeSelectOptions() {

			ContactType[] contactTypeAry = ContactType.values(category);

			List<ContactTypeSelectOption> selectOptions = Arrays.asList(contactTypeAry).stream()
					.map(elem -> {
						ContactTypeSelectOption option = new ContactTypeSelectOption();
						option.setType(elem);
						option.setTitle(elem.getVal());
						return option;
					})
					.collect(Collectors.toList());

			// デフォルトの選択肢を先頭に追加
			ContactTypeSelectOption defaultOption = new ContactTypeSelectOption();
			defaultOption.setTitle("-");
			selectOptions.add(0, defaultOption);

			return selectOptions;
		}

		/**
		 * 現在の連絡先カテゴリの表示タイトルを取得
		 * 
		 * @return
		 */
		public String getCategoryTitle() {
			switch (category) {
			case TEL:
				return PersonContactDto.TEL_TITLE;
			case FAX:
				return PersonContactDto.FAX_TITLE;
			case EMAIL:
				return PersonContactDto.EMAIL_TITLE;
			default:
				return CommonConstant.BLANK;
			}
		}

		/**
		 * カテゴリに対応した連絡の値を取得する。
		 * 
		 * @return
		 */
		public String getValue() {
			switch (category) {
			case TEL:
				return this.valueTel;
			case FAX:
				return this.valueFax;
			case EMAIL:
				return this.valueMail;
			default:
				return CommonConstant.BLANK;
			}
		}

		/**
		 * 連絡先が空であるか判定する
		 *
		 * @return 空の場合はtrue
		 */
		public boolean isEmpty() {
			return type == null && StringUtils.isEmpty(this.getValue());
		}
	}

	/**
	 * 連絡先区分（自宅、勤務先など）の選択肢
	 */
	@Data
	public static class ContactTypeSelectOption {

		/** 連絡先区分（自宅、勤務先など） */
		private ContactType type;

		/** 表示タイトル */
		private String title;
	}

	/**
	 * 口座情報入力フォーム
	 */
	@Data
	public static class PersonKozaInputForm {

		// 表示、クライアント処理制御用のプロパティ
		// 特になし

		// 入力用データのプロパティ

		/** 銀行名 */
		@MaxDigit(max = 20)
		private String ginkoName;

		/** 支店名 */
		@MaxDigit(max = 20)
		private String shitenName;

		/** 支店番号 */
		@Numeric
		@MaxDigit(max = 6)
		private String shitenNo;

		/** 口座種類 */
		private KozaType kozaType;

		/** 口座番号 */
		@Numeric
		@MaxDigit(max = 9)
		private String kozaNo;

		/** 口座名義 */
		@MaxDigit(max = 30)
		private String kozaName;

		/** 口座名義フリガナ */
		@Katakana
		@MaxDigit(max = 100)
		private String kozaNameKana;

		/** 口座備考 */
		@MaxDigit(max = 500)
		private String kozaRemarks;

		// hidden（保存時などに必要なパラメータ）

		/** 新規登録かどうか */
		private boolean isNewRegist;

		/**
		 * 口座情報が空であるか判定する
		 *
		 * @return 空の場合はtrue
		 */
		public boolean isEmpty() {
			return kozaType == null
					&& StringUtils.isAllEmpty(
							ginkoName, shitenName, shitenNo,
							kozaNo, kozaName, kozaNameKana, kozaRemarks);
		}

		/**
		 * 口座種類の選択肢リストを取得する（普通、当座など）
		 * 
		 * @return
		 */
		public List<KozaTypeSelectOption> getKozaTypeSelectOptions() {

			KozaType[] kozaTypeAry = KozaType.values();

			List<KozaTypeSelectOption> selectOptions = Arrays.asList(kozaTypeAry).stream()
					.map(elem -> {
						KozaTypeSelectOption option = new KozaTypeSelectOption();
						option.setType(elem);
						option.setTitle(elem.getVal());
						return option;
					})
					.collect(Collectors.toList());

			// デフォルトの選択肢を先頭に追加
			KozaTypeSelectOption defaultOption = new KozaTypeSelectOption();
			defaultOption.setTitle("-");
			selectOptions.add(0, defaultOption);

			return selectOptions;
		}
	}

	/**
	 * 口座種類（普通、当座など）の選択肢
	 */
	@Data
	public static class KozaTypeSelectOption {

		/** 口座種類（普通、当座など） */
		private KozaType type;

		/** 表示タイトル */
		private String title;
	}

	/**
	 * 住所
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Address {

		/** 郵便番号 */
		@MaxDigit(max = 8)
		@NumericHyphen
		@ZipPattern
		private String zipCode;

		/** 地域 */
		@MaxDigit(max = 128)
		private String address1;

		/** 番地・建物名 */
		@MaxDigit(max = 128)
		private String address2;

		/** 備考 */
		@MaxDigit(max = 500)
		private String remarks;

		/**
		 * 住所が空であるか判定する
		 *
		 * @return 空の場合はtrue
		 */
		public boolean isEmpty() {
			return StringUtils.isAllEmpty(zipCode, address1, address2, remarks);
		}

		/**
		 * 備考以外が空であるか判定する
		 *
		 * @return 備考以外が空の場合はtrue
		 */
		public boolean isEmptyWithoutRemarks() {
			return StringUtils.isAllEmpty(zipCode, address1, address2);
		}
	}

}
