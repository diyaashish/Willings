package jp.loioz.common.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

import jp.loioz.common.utility.EditerUtils;

/**
 * テキスト内のEditerタグをHTMLタグに置換して出力するProcessor
 */
public class LoiozEditerProcessor extends AbstractStandardExpressionAttributeTagProcessor {

	public static final String ATTR_NAME = "editer";
	public static final int PRECEDENCE = StandardTextTagProcessor.PRECEDENCE;

	public LoiozEditerProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, PRECEDENCE, true);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {

		final String input = (expressionResult == null ? "" : expressionResult.toString());

		// th:textと同等のエスケープ処理
		String text = HtmlEscape.escapeHtml4Xml(input);

		// thymeleaf表示時にEditer用テキストから表示用テキストに変更
		text = EditerUtils.replaceToPreview(text);

		structureHandler.setBody(text, false);
	}
}
