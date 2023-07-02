package jp.loioz.app.user.officeSetting.form;

import org.springframework.web.multipart.MultipartFile;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.constant.CommonConstant.TenantType;
import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MultipartFileEnabledExtension;
import jp.loioz.common.validation.annotation.MultipartFileMaxSize;
import jp.loioz.common.validation.annotation.MultipartFileRequired;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.TelPattern;
import jp.loioz.common.validation.annotation.ZipPattern;
import lombok.Data;

/**
 * 事務所情報設定入力フォーム
 */
@Data
public class OfficeSettingInputForm {

	/**
	 * 事務所情報入力フォームオブジェクト
	 */
	@Data
	public static class OfficeInfoSettingInputForm {

		// 表示用パラメータ
		// 現状なし

		// 入力用プロパティ
		/** テナント名 */
		@Required
		@MaxDigit(max = 64, item = "事務所名")
		private String tenantName;

		/** 個人・法人区分 */
		@Required
		@EnumType(TenantType.class)
		private String tenantType;

		/** 郵便番号 */
		@ZipPattern(item = "郵便番号")
		private String tenantZipCd;

		/** 住所１ */
		@MaxDigit(max = 256, item = "住所１")
		private String tenantAddress1;

		/** 住所２ */
		@MaxDigit(max = 128, item = "住所２")
		private String tenantAddress2;

		/** 電話番号 */
		@Required
		@DigitRange(min = 10, max = 13, item = "電話番号")
		@TelPattern(item = "電話番号")
		private String tenantTelNo;

		/** FAX番号 */
		@DigitRange(min = 10, max = 13, item = "FAX番号")
		@TelPattern(item = "FAX番号")
		private String tenantFaxNo;

		/** 適格請求書発行事業者番号 */
		@Numeric
		@MaxDigit(max = 13)
		private String tenantInvoiceRegistrationNo;
		
		/** 事務所印：表示有無 */
		private boolean isTenantStampPrintEnabled;
	}

	/**
	 * 事務所印入力フォームオブジェクト
	 */
	@Data
	public static class OfficeStampSettingInputForm {

		// 表示データ
		/** 画像が登録済みかどうか */
		private boolean existsImage;

		/** 事務所職印 base64 Imageタグのソース */
		private String base64ImageSrc;

		// 入力用プロパティ
		/**
		 * 画像データ<br>
		 * ※エラーメッセージが固定値のため、ファイル上限のサイズを変更した場合は、文言も変更する
		 */
		@MultipartFileRequired
		@MultipartFileEnabledExtension(enabledExtension = {
				FileExtension.JPEG, FileExtension.JPG, FileExtension.PNG, FileExtension.GIF,
				FileExtension.JPEG_U, FileExtension.JPG_U, FileExtension.PNG_U, FileExtension.GIF_U
		})
		@MultipartFileMaxSize(max = CommonConstant.OFFICE_STAMP_MAX_SIZE, message = "ファイルサイズの上限は2MBまでです。")
		private MultipartFile multiPartFile;
	}

}
