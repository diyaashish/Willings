package jp.loioz.common.thymeleaf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import jp.loioz.common.thymeleaf.expression.Formats;
import jp.loioz.common.thymeleaf.processor.LoiozEditerProcessor;
import jp.loioz.common.thymeleaf.processor.NewlineToBreakProcessor;
import jp.loioz.common.thymeleaf.processor.TextToLinkProcessor;

/**
 * Thymeleafの独自Dialect
 */
public class MyThymeleafDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

	public static final String NAME = "Custom Dialect";
	public static final String PREFIX = "myth";

	public static final String FORMATS_EXPRESSION_OBJECT_NAME = "formats";

	private IExpressionObjectFactory expressionObjectFactory = null;

	public MyThymeleafDialect() {
		super(NAME, PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		final Set<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(new NewlineToBreakProcessor(dialectPrefix));
		processors.add(new LoiozEditerProcessor(dialectPrefix));
		processors.add(new TextToLinkProcessor(dialectPrefix));
		processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
		return processors;
	}

	@Override
	public IExpressionObjectFactory getExpressionObjectFactory() {
		if (this.expressionObjectFactory == null) {
			this.expressionObjectFactory = new IExpressionObjectFactory() {
				@Override
				public Set<String> getAllExpressionObjectNames() {
					return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
							FORMATS_EXPRESSION_OBJECT_NAME)));
				}

				@Override
				public Object buildObject(IExpressionContext context, String expressionObjectName) {
					if (FORMATS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
						return new Formats();
					}
					return null;
				}

				@Override
				public boolean isCacheable(String expressionObjectName) {
					return true;
				}
			};
		}

		return this.expressionObjectFactory;
	}
}
