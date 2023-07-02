package jp.loioz.common.thymeleaf.processor;

import java.util.regex.Pattern;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

/**
 * テキスト内の改行コードをbrタグに置換して出力するProcessor
 */
public class NewlineToBreakProcessor extends AbstractStandardExpressionAttributeTagProcessor {

	public static final String ATTR_NAME = "nl2br";
	public static final int PRECEDENCE = StandardTextTagProcessor.PRECEDENCE;

	private static final Pattern NEWLINE_PATTERN = Pattern.compile("(\r\n|\n)");
	private static final String BR_TAG = "<br>";

	public NewlineToBreakProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, PRECEDENCE, true);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
		String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {

		final String input = (expressionResult == null ? "" : expressionResult.toString());

		// th:textと同等のエスケープ処理
		String text = HtmlEscape.escapeHtml4Xml(input);

		// 改行コードを<br>タグに置換
		text = NEWLINE_PATTERN.matcher(text).replaceAll(BR_TAG);

		structureHandler.setBody(text, false);
	}
}
