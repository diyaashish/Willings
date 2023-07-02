package jp.loioz.common.aspect.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * プランの契約状態が無効なユーザーにも実行を許可する。<br>
 * （ExecutionRestrictionAspect#nonPermitExcuteDisabledServicePlanStatusUserMethodで使用するアノテーション）
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface PermitAlsoDisabledPlanStatusUser {
}
