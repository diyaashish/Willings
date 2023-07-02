package jp.loioz.common.handlerInterceptor.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;

/**
 * プラン別機能制限（実行制限）をするためのアノテーション
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface PlanFuncRestrictCheck {
	
	/** 制限候補とする機能 */
	PlanFuncRestrict[] funcs() default {};
}
