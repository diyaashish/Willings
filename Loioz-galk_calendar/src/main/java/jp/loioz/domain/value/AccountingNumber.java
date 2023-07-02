package jp.loioz.domain.value;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgNoDFmt;
import jp.loioz.common.constant.CommonConstant.AccgNoMFmt;
import jp.loioz.common.constant.CommonConstant.AccgNoNumberingType;
import jp.loioz.common.constant.CommonConstant.AccgNoYFmt;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.entity.MInvoiceSettingEntity;
import jp.loioz.entity.MStatementSettingEntity;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

/**
 * 請求書 / 精算書番号クラス
 */
@Value
@AllArgsConstructor
public class AccountingNumber {

	/** プレフィックス */
	private String prefix;

	/** 年フォーマット */
	private AccgNoYFmt accgNoYFmt;

	/** 月フォーマット */
	private AccgNoMFmt accgNoMFmt;

	/** 日フォーマット */
	private AccgNoDFmt accgNoDFmt;

	/** 区切り文字 */
	private String delimiter;

	/** ナンバリング種別 */
	private AccgNoNumberingType accgNoNumberingType;

	/** ゼロ埋め有無 */
	private boolean isInvoiceNoZeroPadEnabled;

	/** ゼロ埋め桁数 */
	private String zeroPadDigits;

	/**
	 * 日付フォーマットを作成
	 * 
	 * @return
	 */
	public DateTimeFormatter getDateFormat() {

		var format = "";

		// 年
		switch (this.accgNoYFmt) {
		case UNSPECIFIED:
			break;
		case YYYY:
			format += DateUtils.DATE_YYYY;
			break;
		case YY:
			format += DateUtils.DATE_YY;
			break;
		case JP_ERA:
			format += "GGGGGy";
			break;
		default:
			break;
		}

		// 月
		switch (this.accgNoMFmt) {
		case UNSPECIFIED:
			break;
		case MM:
			format += DateUtils.DATE_MM;
			break;
		default:
			break;
		}

		// 日
		switch (this.accgNoDFmt) {
		case UNSPECIFIED:
			break;
		case DD:
			format += DateUtils.DATE_DD;
			break;
		default:
			break;
		}

		Locale locale = new Locale("ja", "JP", "JP");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format).withLocale(locale);
		return dtf;
	}

	/**
	 * 請求書/精算書番号の表示文字列を取得
	 * 
	 * @param localDate
	 * @param seq 請求書連番
	 * @return
	 */
	public String getAccgNoView(@NonNull LocalDate localDate, @NonNull Number seq) {

		// 日付の文字列変換
		var dateString = "";
		if (AccgNoYFmt.JP_ERA == this.accgNoYFmt) {
			Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			JapaneseDate jpDate = JapaneseDate.from(zdt);
			dateString = jpDate.format(getDateFormat());

		} else {
			// 日付をStringに変換
			dateString = localDate.format(getDateFormat());
		}

		var seqStr = "";
		if (this.isInvoiceNoZeroPadEnabled) {
			Integer zeroPadDigits = LoiozNumberUtils.parseAsInteger(this.zeroPadDigits);
			seqStr = StringUtils.leftPad(LoiozNumberUtils.parseAsString(seq), Optional.ofNullable(zeroPadDigits).orElse(1), CommonConstant.ZERO);
		} else {
			seqStr = LoiozNumberUtils.parseAsString(seq);
		}

		return StringUtils.defaultString(this.prefix) + dateString + StringUtils.defaultString(this.delimiter) + seqStr;
	}

	/**
	 * 請求書設定Entittyから作成
	 * 
	 * @param entity
	 * @return
	 */
	public static AccountingNumber fromEntity(MInvoiceSettingEntity entity) {
		return new AccountingNumber(
				entity.getInvoiceNoPrefix(),
				AccgNoYFmt.of(entity.getInvoiceNoYFmt()),
				AccgNoMFmt.of(entity.getInvoiceNoMFmt()),
				AccgNoDFmt.of(entity.getInvoiceNoDFmt()),
				entity.getInvoiceNoDelimiter(),
				AccgNoNumberingType.of(entity.getInvoiceNoNumberingType()),
				SystemFlg.codeToBoolean(entity.getInvoiceNoZeroPadFlg()),
				entity.getInvoiceNoZeroPadDigits());
	}

	/**
	 * 精算書設定Entittyから作成
	 * 
	 * @param entity
	 * @return
	 */
	public static AccountingNumber fromEntity(MStatementSettingEntity entity) {
		return new AccountingNumber(
				entity.getStatementNoPrefix(),
				AccgNoYFmt.of(entity.getStatementNoYFmt()),
				AccgNoMFmt.of(entity.getStatementNoMFmt()),
				AccgNoDFmt.of(entity.getStatementNoDFmt()),
				entity.getStatementNoDelimiter(),
				AccgNoNumberingType.of(entity.getStatementNoNumberingType()),
				SystemFlg.codeToBoolean(entity.getStatementNoZeroPadFlg()),
				entity.getStatementNoZeroPadDigits());
	}

}
