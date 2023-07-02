package jp.loioz.common.thymeleaf.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;

/**
 * テキスト内の[A]をaタグに置換して出力するProcessor
 */
public class TextToLinkProcessor extends AbstractStandardExpressionAttributeTagProcessor {

	public static final String ATTR_NAME = "txt2link";
	public static final int PRECEDENCE = StandardTextTagProcessor.PRECEDENCE;

	public TextToLinkProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, PRECEDENCE, true);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {

		final String input = (expressionResult == null ? "" : expressionResult.toString());

		// th:textと同等のエスケープ処理
		String text = HtmlEscape.escapeHtml4Xml(input);

		// [A]タグを<a>タグに置換する
		text = replaceLinkTag(text);

		structureHandler.setBody(text, false);
	}

	/**
	 * オリジナルリンクタグをHTMLタグに置換する処理
	 * 
	 * @param before
	 * @return
	 */
	private String replaceLinkTag(String before) {

		String after = before;

		// [A]...[/A]の正規表現
		Pattern linkTag = Pattern.compile("\\[A+\\](.*?)\\[\\/A+\\]");
		Matcher matcher = linkTag.matcher(before);

		// 複数考慮のため、Map化
		Map<String, String> replaceMap = new HashMap<>();
		while (matcher.find()) {
			String originTag = matcher.group();
			// key：オリジナルタグ、 value：htmlタグ
			replaceMap.put(originTag, replaceHtmlATag(originTag));
		}

		// 変換したタグ情報を全体の文字列から置換
		for (String origin : replaceMap.keySet()) {
			// mapのキーをvalueに置換する ※同じ文字列のリンクが存在してもすべて変更するので問題はない
			after = StringUtils.replace(after, origin, replaceMap.get(origin));
		}

		return after;
	}

	/**
	 * オリジナルリンクタグをHTMLタグに置換する処理
	 * 
	 * @param originTag
	 * @return
	 */
	private String replaceHtmlATag(String originTag) {

		// [A]タグ内の[PATH]を取得
		Pattern hrefPtn = Pattern.compile("\\[PATH+\\](.*?)\\[\\/PATH+\\]");
		Matcher matcher = hrefPtn.matcher(originTag);

		StringBuilder htmlTag = new StringBuilder();
		htmlTag.append("<a target=\"_blank\" rel=\"noopener\"");
		if (matcher.find()) {
			String originPath = matcher.group();
			htmlTag.append(CommonConstant.SPACE + "href=\"" + originPath.replaceAll("\\[PATH+\\]", "").replaceAll("\\[/PATH+\\]", "") + "\"");
			originTag = StringUtils.replace(originTag, originPath, "");
		} else {
			// [PATH]がない場合、aタグとして成り立たないのでエラー
			throw new RuntimeException();
		}

		htmlTag.append(">");
		htmlTag.append(originTag.replaceAll("\\[A+\\]", "").replaceAll("\\[/A+\\]", ""));
		htmlTag.append("</a>");

		return htmlTag.toString();
	}

}
