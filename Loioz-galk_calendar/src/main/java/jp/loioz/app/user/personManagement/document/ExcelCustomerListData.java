package jp.loioz.app.user.personManagement.document;

import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AllowType;
import jp.loioz.common.constant.CommonConstant.ContactType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.MailingType;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 名簿一覧エクセル出力用のデータクラス
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class ExcelCustomerListData {

	// 個人・法人区分
	private String customerType;

	/** 個人顧客情報 */
	// 顧客ID
	private String customerId;

	// 顧客名
	private String customerName;

	// 顧客名ふりがな
	private String customerNameKana;

	// 旧姓
	private String oldName;

	// 旧姓ふりがな
	private String oldNameKana;

	// 屋号・通称名
	private String yago;

	// 屋号・通称名ふりがな
	private String yagoKana;

	// 性別
	private String genderType;

	// 生年月日
	private LocalDate birthday;

	// 年齢
	private String age;

	// 国籍
	private String country;

	// 言語
	private String language;

	// 登録日
	private LocalDate customerCreatedDate;

	// 相談経路
	private String sodanRoute;

	// 相談経路備考
	private String sodanRemarks;

	// 追加情報
	private String addInfo;

	// 追加情報備考
	private String addInfoRemarks;

	// 郵便番号
	private String zipCode;

	// 住所
	private String address;

	// 住所備考
	private String addressRemarks;

	// 住民票登録地郵便番号
	private String juminhyoZipCode;

	// 住民票登録地住所
	private String juminhyoAddress;

	// 住民票登録地備考
	private String juminhyoRemarks;

	// 電話番号
	private String telNo;

	// 優先電話番号
	private String yusenTelNo;

	// FAX番号
	private String faxNo;

	// 優先FAX番号
	private String yusenFaxNo;

	// メールアドレス
	private String mailAddress;

	// 優先メールアドレス
	private String yusenMailAddress;

	// 電話連絡可否
	private String contactType;

	// 郵送先郵便番号
	private String transferZipCode;

	// 郵送先住所
	private String transferAddress;

	// 宛名
	private String transferName;

	// 郵送方法
	private String transferType;

	// 郵送先備考
	private String transferRemarks;

	// 銀行名
	private String ginkoName;

	// 支店名
	private String shitenName;

	// 支店番号
	private String shitenNo;

	// 口座種類
	private String kozaType;

	// 口座番号
	private String kozaNo;

	// 口座名義
	private String kozaName;

	// 口座名義かな
	private String kozaNameKana;

	/** 法人顧客情報 */
	// 代表者
	private String daihyoName;

	// 代表者かな
	private String daihyoNameKana;

	// 代表者役職
	private String daihyoPositionName;

	// 担当者
	private String tantoName;

	// 担当者かな
	private String tantoNameKana;

	// 登記郵便番号
	private String tokiZipCode;

	// 登記住所
	private String tokiAddress;

	// 登記住所備考
	private String tokiAddressRemarks;

	// 顧客一覧エクセル用のEnum
	@Getter
	@AllArgsConstructor
	public enum ExcelCustomerList {

		KOJIN(40, "個人"),
		HOJIN(35, "法人"),;

		/** セルの総数 */
		private int cd;

		/** 個人・法人 */
		private String val;
	}

	/**
	 * 1行分のRowを返却する
	 *
	 * @return row
	 */
	public void setRowData(Row row, CellStyle cellStyle) {

		int cellNum;
		if ("0".equals(this.getCustomerType())) {
			// 個人顧客
			cellNum = ExcelCustomerList.KOJIN.getCd();
			for (int i = 0; i < cellNum; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(getKojinCellValue(i));
				cell.setCellStyle(cellStyle);
			}
		} else {
			// 法人顧客
			cellNum = ExcelCustomerList.HOJIN.getCd();
			for (int i = 0; i < cellNum; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(getHojinCellValue(i));
				cell.setCellStyle(cellStyle);
			}
		}
	}

	/**
	 * セルに記述する値を取得する ※個人顧客
	 *
	 * @return value
	 */
	private String getKojinCellValue(int i) {
		DateTimeFormatter f_seireki = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
		DateTimeFormatter f_wareki = DateTimeFormatter.ofPattern("Gy年MM月dd日").withChronology(JapaneseChronology.INSTANCE);

		switch (i) {
		case 0:
			return String.format("%05d", Long.parseLong(customerId));
		case 1:
			return customerName;
		case 2:
			return customerNameKana;
		case 3:
			return oldName;
		case 4:
			return oldNameKana;
		case 5:
			return yago;
		case 6:
			return yagoKana;
		case 7:
			if (genderType == null || Gender.of(genderType) == null) {
				return CommonConstant.BLANK;
			}

			return Gender.of(genderType).getVal();
		case 8:
			if (birthday == null) {
				return CommonConstant.BLANK;
			}

			// 西暦 -> 和暦に変換する
			JapaneseDate d = JapaneseDate.of(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());

			return d.format(f_wareki);
		case 9:
			if (birthday == null) {
				return CommonConstant.BLANK;
			}

			return f_seireki.format(birthday);
		case 10:
			if (birthday == null) {
				return CommonConstant.BLANK;
			}

			return String.valueOf(Period.between(birthday, LocalDate.now()).getYears());
		case 11:
			return country;
		case 12:
			return language;
		case 13:
			if (customerCreatedDate == null) {
				return CommonConstant.BLANK;
			}

			return f_seireki.format(customerCreatedDate);
		case 14:
			return sodanRoute;
		case 15:
			return sodanRemarks;
		case 16:
			return addInfo;
		case 17:
			return addInfoRemarks;
		case 18:
			return zipCode;
		case 19:
			return address;
		case 20:
			return addressRemarks;
		case 21:
			return juminhyoZipCode;
		case 22:
			return juminhyoAddress;
		case 23:
			return juminhyoRemarks;
		case 24:
			return StringUtils.isNotEmpty(yusenTelNo) ? yusenTelNo : telNo;
		case 25:
			return StringUtils.isNotEmpty(yusenFaxNo) ? yusenFaxNo : faxNo;
		case 26:
			return StringUtils.isNotEmpty(yusenMailAddress) ? yusenMailAddress : mailAddress;
		case 27:
			if (contactType == null || ContactType.of(contactType) == null) {
				return CommonConstant.BLANK;
			}

			return AllowType.of(contactType).getVal();
		case 28:
			return transferZipCode;
		case 29:
			return transferAddress;
		case 30:
			return transferName;
		case 31:
			if (transferType == null || MailingType.of(transferType) == null) {
				return CommonConstant.BLANK;
			}

			return MailingType.of(transferType).getVal();
		case 32:
			return transferRemarks;
		case 33:
			return ginkoName;
		case 34:
			return shitenName;
		case 35:
			return shitenNo;
		case 36:
			if (kozaType == null || KozaType.of(kozaType) == null) {
				return CommonConstant.BLANK;
			}

			return KozaType.of(kozaType).getVal();
		case 37:
			return kozaNo;
		case 38:
			return kozaName;
		case 39:
			return kozaNameKana;
		default:
			return CommonConstant.BLANK;
		}
	}

	/**
	 * セルに記述する値を取得する ※法人顧客
	 *
	 * @return value
	 */
	private String getHojinCellValue(int i) {
		DateTimeFormatter f_seireki = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

		switch (i) {
		case 0:
			return String.format("%05d", Long.parseLong(customerId));
		case 1:
			return customerName;
		case 2:
			return customerNameKana;
		case 3:
			return daihyoName;
		case 4:
			return daihyoNameKana;
		case 5:
			return daihyoPositionName;
		case 6:
			return tantoName;
		case 7:
			return tantoNameKana;
		case 8:
			if (customerCreatedDate == null) {
				return CommonConstant.BLANK;
			}

			return f_seireki.format(customerCreatedDate);
		case 9:
			return sodanRoute;
		case 10:
			return sodanRemarks;
		case 11:
			return addInfo;
		case 12:
			return addInfoRemarks;
		case 13:
			return zipCode;
		case 14:
			return address;
		case 15:
			return addressRemarks;
		case 16:
			return tokiZipCode;
		case 17:
			return tokiAddress;
		case 18:
			return tokiAddressRemarks;
		case 19:
			return telNo;
		case 20:
			return faxNo;
		case 21:
			return mailAddress;
		case 22:
			if (contactType == null || ContactType.of(contactType) == null) {
				return CommonConstant.BLANK;
			}

			return ContactType.of(contactType).getVal();
		case 23:
			return transferZipCode;
		case 24:
			return transferAddress;
		case 25:
			return transferName;
		case 26:
			if (transferType == null || MailingType.of(transferType) == null) {
				return CommonConstant.BLANK;
			}

			return MailingType.of(transferType).getVal();
		case 27:
			return transferRemarks;
		case 28:
			return ginkoName;
		case 29:
			return shitenName;
		case 30:
			return shitenNo;
		case 31:
			if (kozaType == null || KozaType.of(kozaType) == null) {
				return CommonConstant.BLANK;
			}

			return KozaType.of(kozaType).getVal();
		case 32:
			return kozaNo;
		case 33:
			return kozaName;
		case 34:
			return kozaNameKana;
		default:
			return CommonConstant.BLANK;
		}
	}
}