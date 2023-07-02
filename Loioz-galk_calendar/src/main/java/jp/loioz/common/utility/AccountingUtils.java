package jp.loioz.common.utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import jp.loioz.common.constant.AccgConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgNoDFmt;
import jp.loioz.common.constant.CommonConstant.AccgNoMFmt;
import jp.loioz.common.constant.CommonConstant.AccgNoYFmt;
import jp.loioz.common.constant.CommonConstant.SystemFlg;

/**
 * 会計用のUtilクラス
 */
public class AccountingUtils {

	/**
	 * 実金額を表示用金額に変換する（カンマあり）
	 *
	 * @param rawAmount 実金額
	 * @return 表示用金額
	 */
	public static String toDispAmountLabel(BigDecimal rawAmount) {

		if (rawAmount == null) {
			return null;
		}

		DecimalFormat format = new DecimalFormat(AccgConstant.AMOUNT_LABEL_FORMAT);
		String dispAmount = format.format(rawAmount);

		return dispAmount;
	}

	/**
	 * マスタ設定した件名の形式にする
	 * 
	 * @param ankenName
	 * @param subjectPrefix
	 * @param subjectSuffix
	 * @return
	 */
	public static String formatAccgDocSubject(String ankenName, String subjectPrefix, String subjectSuffix) {
		// 件名
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.null2blank(subjectPrefix));
		sb.append(StringUtils.null2blank(ankenName));
		sb.append(StringUtils.null2blank(subjectSuffix));
		return sb.toString();
	}

	/**
	 * 適格請求書発行事業者登録番号の形式で返す
	 * 
	 * @param invoiceRegistrationNo
	 * @return
	 */
	public static String formatInvoiceRegistrationNo(String invoiceRegistrationNo) {
		String val = CommonConstant.BLANK;
		if (StringUtils.isNotEmpty(invoiceRegistrationNo)) {
			val = "T" + invoiceRegistrationNo;
		}
		return val;
	}

	/**
	 * 会計書類-請求番号 or 精算番号の形式で返す
	 * 
	 * @param numberingLastNo
	 * @param prefix
	 * @param yFmt
	 * @param mFmt
	 * @param dFmt
	 * @param delimiter
	 * @param zeroPadFlg
	 * @param zeroPadDigits
	 * @return
	 */
	public static String formatNumberingLastNo(Long numberingLastNo, String prefix, String yFmt, String mFmt, String dFmt,
			String delimiter, String zeroPadFlg, String zeroPadDigits) {

		// 返却する値
		StringBuilder sb = new StringBuilder();
		// 現在日
		LocalDate now = LocalDate.now();
		// 接頭辞
		sb.append(StringUtils.null2blank(prefix));

		// 請求番号-年フォーマット
		String yVal = CommonConstant.BLANK;
		switch (AccgNoYFmt.of(yFmt)) {
		case UNSPECIFIED:
			// 何もしない
			break;
		case YY:
			yVal = DateUtils.parseToString(now, DateUtils.DATE_YY);
			break;
		case YYYY:
			yVal = DateUtils.parseToString(now, DateUtils.DATE_YYYY);
			break;
		case JP_ERA:
			yVal = DateUtils.getWarekiYearAlphabet(now);
			break;
		default:
			// 何もしない
			break;
		}
		sb.append(yVal);

		// 請求番号-月フォーマット
		String mVal = CommonConstant.BLANK;
		switch (AccgNoMFmt.of(mFmt)) {
		case UNSPECIFIED:
			// 何もしない
			break;
		case MM:
			mVal = DateUtils.parseToString(now, DateUtils.DATE_MM);
			break;
		default:
			// 何もしない
			break;
		}
		sb.append(mVal);

		// 請求番号-日フォーマット
		String dVal = CommonConstant.BLANK;
		switch (AccgNoDFmt.of(dFmt)) {
		case UNSPECIFIED:
			// 何もしない
			break;
		case DD:
			dVal = DateUtils.parseToString(now, DateUtils.DATE_DD);
			break;
		default:
			// 何もしない
			break;
		}

		sb.append(dVal);

		// 請求番号-区切り文字
		sb.append(StringUtils.null2blank(delimiter));

		// 請求番号-連番
		String numberingLastNoStr = numberingLastNo.toString();
		// 連番のゼロ埋め
		if (SystemFlg.FLG_ON.equalsByCode(zeroPadFlg)) {
			Integer zeroPadDigitsInt = LoiozNumberUtils.parseAsInteger(zeroPadDigits);
			if (zeroPadDigitsInt != null) {
				// 連番ゼロ埋め桁数分のゼロ埋め
				numberingLastNoStr = StringUtils.leftPad(numberingLastNoStr, zeroPadDigitsInt.intValue(), CommonConstant.ZERO);
			}
		}
		sb.append(numberingLastNoStr);

		return sb.toString();
	}

	/**
	 * 金額の合計値を計算します。
	 * 
	 * @param amount
	 * @return
	 */
	public static BigDecimal calcTotal(BigDecimal... amount) {
		List<BigDecimal> amountList = Arrays.asList(amount);
		return AccountingUtils.calcTotal(amountList);
	}

	/**
	 * 金額の合計値を計算します。
	 *
	 * @param kingakuList 金額リスト
	 * @return 金額の合計値
	 */
	public static BigDecimal calcTotal(List<BigDecimal> kingakuList) {

		if (CollectionUtils.isEmpty(kingakuList)) {
			// 空の場合は、その時点で返却
			return BigDecimal.ZERO;
		}

		BigDecimal total = kingakuList.stream()
				.filter(Objects::nonNull) // NULLを除去
				.reduce(BigDecimal.ZERO, BigDecimal::add); // 合計計算処理
		return total;
	}

	/**
	 * 金額の対象からの減算をします。
	 * 
	 * @param 減算対象
	 * @param amount
	 * @return
	 */
	public static BigDecimal calcSubtract(BigDecimal target, BigDecimal... amount) {
		List<BigDecimal> amountList = Arrays.asList(amount);
		return AccountingUtils.calcSubtract(target, amountList);
	}

	/**
	 * 金額の対象からの減算をします。
	 *
	 * @param 減算対象
	 * @param kingakuList 金額リスト
	 * @return 金額の合計値
	 */
	public static BigDecimal calcSubtract(BigDecimal target, List<BigDecimal> kingakuList) {

		if (CollectionUtils.isEmpty(kingakuList) || target == null) {
			// 空の場合はそのまま返却
			return target;
		}

		BigDecimal total = kingakuList.stream()
				.filter(Objects::nonNull) // NULLを除去
				.reduce(BigDecimal.ZERO, BigDecimal::add); // 合計

		BigDecimal bd = target.subtract(total);
		return bd;
	}

	/**
	 * 金額がnullの場合0を返します
	 *
	 * @param val
	 * @return
	 */
	public static BigDecimal blank2Zero(BigDecimal val) {
		if (val == null) {
			// 空の場合は、その時点で返却
			return BigDecimal.ZERO;
		} else {
			return val;
		}
	}

}