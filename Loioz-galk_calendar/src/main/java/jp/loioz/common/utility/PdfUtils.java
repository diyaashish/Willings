package jp.loioz.common.utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.unbescape.html.HtmlEscape;

import jp.loioz.common.constant.CommonConstant;

/**
 * PDFUtility
 */
public class PdfUtils {

	/** PDF出力時の金額フォーマット */
	private static final DecimalFormat PDF_DECIMAL_FORMAT = new DecimalFormat("#,###.##");

	/** 円マーク */
	private static final String YEN_MARK = "¥";

	/**
	 * PDF出力パラメータのサニタイズ処理
	 * JasperReport側で表示オブジェクトのMarkup属性を「HTML」にする場合に使用する
	 * 
	 * @param str
	 * @return
	 */
	public static String sanitizeStr(String str) {
		return HtmlEscape.escapeHtml4Xml(str);
	}

	/**
	 * PDF出力パラメータの均等割当処理
	 * JasperReport側で表示オブジェクトを均等割当で表示する際に使用する
	 * 
	 * @param str
	 * @return
	 */
	public static String justifiedStr(String str) {

		String outputStr = StringUtils.defaultString(str);

		List<String> charList = new ArrayList<>();
		for (int i = 0; i < outputStr.length(); i = outputStr.offsetByCodePoints(i, 1)) {
			String charStr = Character.toString(outputStr.codePointAt(i));

			// サニタイズ処理
			charStr = HtmlEscape.escapeHtml4Xml(charStr);

			// 配列に追加
			charList.add(charStr);
		}

		// 自動割当処理
		charList.removeIf(c -> c.matches(" |　"));
		outputStr = StringUtils.list2Str(charList);

		return outputStr;
	}

	/**
	 * 改行コードをJasperReportの判定用に変換する
	 * 
	 * @param str
	 * @return
	 */
	public static String newLineToBreak(String str) {

		if (StringUtils.isEmpty(str)) {
			return "";
		}

		String text = sanitizeStr(str);

		final Pattern NEWLINE_PATTERN = Pattern.compile("(\r\n|\n)");
		final String JASPER_BR_TAG = "<br/>";

		text = NEWLINE_PATTERN.matcher(text).replaceAll(JASPER_BR_TAG);

		return text;
	}

	/**
	 * プレフィックスを追加した文字列作成
	 * 
	 * @param prefix 追加するプレフィックス
	 * @param target 付与される文字列
	 * @return
	 */
	public static String addPrefix(String prefix, String target) {
		if (StringUtils.isEmpty(target)) {
			return "";
		}
		return prefix + target;
	}

	/**
	 * 表示用金額に変換する(999,999,999)
	 * 
	 * @param amount
	 * @return
	 */
	public static String toDispAmount(BigDecimal amount) {

		if (amount == null) {
			return "";
		}
		return PDF_DECIMAL_FORMAT.format(amount);
	}

	/**
	 * 表示用金額に変換する(¥ 999,999,999)
	 * 
	 * @param amount
	 * @return
	 */
	public static String toDispTotalAmount(BigDecimal amount) {

		if (amount == null) {
			return "";
		}
		return addPrefix(YEN_MARK + CommonConstant.SPACE, toDispAmount(amount));
	}

	/**
	 * 配列に空データを追加する
	 * 
	 * @param <E> 一覧オブジェクト
	 * @param list 一覧データ
	 * @param emptyDataSupplier 空データを作成するサプライヤー
	 * @param listSize 処理完了後に配列格納件数
	 */
	public static <E> void appendEmptyListItem(List<E> list, Supplier<E> emptyDataSupplier, Integer listSize) {

		// 空データの作成
		E item = emptyDataSupplier.get();

		int currentSize = list.size();
		for (var i = currentSize; i < listSize; i++) {
			list.add(item);
		}
	}

}
