package jp.loioz.common.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * Beanの完全修飾クラス名をBean名として使用するようにするクラス。<br>
 *
 * ※Springのデフォルトでは、DIなどの際、Beanの名前は単純なクラス名のみが使用されるため、別パッケージでも同じクラス名がある場合、
 *  DIの際にクラスの重複が発生していしまう。そのため、別パッケージに同じクラス名が存在しても問題ないように、SpringがBeanを判断する名前をクラス名ではなく、
 *  完全修飾クラス名に変更する。
 */
public class FQCNGenerator extends AnnotationBeanNameGenerator {

	@Override
	protected String buildDefaultBeanName(BeanDefinition definition) {
		return definition.getBeanClassName();
	}
}
