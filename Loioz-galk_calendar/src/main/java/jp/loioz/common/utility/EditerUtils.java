package jp.loioz.common.utility;

import java.util.regex.Pattern;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.EditerTag;

/**
 * エディタUtilクラス
 * 
 * <pre>
 * ■エディタ文字列の表示方法
 * 
 * 置換処理せずに呼び出すだけでOK
 * １: Thymeleafでmyth:editerを使用する 
 * 
 * 以下は、すでに置換した文字列をThymeleafで表示する場合
 * ２: 対象要素に「previewTargetObj」のclassを付与する
 * ３: 対象画面でcommon.jsのsetUpPreview(obj)を呼ぶ
 * </pre>
 * 
 */
public class EditerUtils {

	/** コンテンツパネル用開始タグ */
	private static final String CONTENT_PANEL_START = "<div class=\"content__panel prev-cp-padding\">";

	/** コンテンツパネル用終了タグ */
	private static final String CONTENT_PANEL_END = "</div>";

	/** brタグ */
	private static final String BR_TAG = "<br>";

	// =========================================================
	// publicメソッド
	// =========================================================

	/**
	 * 掲示板本文のプレビュー表示用編集
	 *
	 * @param フォーム情報
	 */
	public static String replaceToPreview(String content) {

		String replaceContent = new String(content);
		// "<",">"を文字参照に変換
		replaceContent = replaceInequality(replaceContent);
		// オリジナルエディタタグ -> HTMLタグ に変換
		replaceContent = replaceToHtmlTag(replaceContent);
		// 改行コードをbrタグに変換
		replaceContent = replaceTobr(replaceContent);

		StringBuilder preview = new StringBuilder("");
		preview.append(CONTENT_PANEL_START);
		preview.append(replaceContent);
		preview.append(CONTENT_PANEL_END);
		return preview.toString();
	}

	/**
	 * 小なりを文字実体参照に変換する
	 *
	 * @param content
	 * @return 変換結果
	 */
	public static String replaceLT(String content) {

		// 空 or NULL はなにもしない
		if (StringUtils.isEmpty(content)) {
			return CommonConstant.BLANK;
		}

		return StringUtils.replace(content, "<", "&lt;");
	}

	/**
	 * 大なりを文字実体参照に変換する
	 *
	 * @param content
	 * @return 変換結果
	 */
	public static String replaceGT(String content) {

		// 空 or NULL はなにもしない
		if (StringUtils.isEmpty(content)) {
			return CommonConstant.BLANK;
		}

		return StringUtils.replace(content, ">", "&gt;");
	}

	/**
	 * 大なり小なりを文字実体参照に変換する
	 *
	 * @param content
	 * @return 変換結果
	 */
	public static String replaceInequality(String content) {

		// 空 or NULL はなにもしない
		if (StringUtils.isEmpty(content)) {
			return CommonConstant.BLANK;
		}

		String replaceInequality = new String(content);
		replaceInequality = replaceLT(replaceInequality);
		replaceInequality = replaceGT(replaceInequality);

		return replaceInequality;
	}

	// =========================================================
	// privateメソッド
	// =========================================================

	/**
	 * 本文を表示用に編集する
	 *
	 * @param content
	 * @return dispComment
	 */
	private static String replaceTobr(String content) {
		if (StringUtils.isEmpty(content)) {
			return content;
		}
		return Pattern.compile("(\r\n|\n)").matcher(content).replaceAll(BR_TAG);
	}

	/**
	 * 画面で入力されたタグをHTMLタグに置き換える
	 *
	 * @param orginalContent
	 * @return
	 */
	private static String replaceToHtmlTag(String content) {

		if (StringUtils.isEmpty(content)) {
			// 置換対象文字列がnullの場合、処理を終了する
			return CommonConstant.BLANK;
		}

		String replaceContent = content;
		for (EditerTag tag : EditerTag.values()) {
			replaceContent = StringUtils.replace(replaceContent, tag.getEditerTag(), tag.getHtmlTag());
		}

		return replaceContent;
	}

}
