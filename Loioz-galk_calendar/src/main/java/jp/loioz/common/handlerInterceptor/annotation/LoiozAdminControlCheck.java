package jp.loioz.common.handlerInterceptor.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;

/**
 * ロイオズ管理者制御による実行制限をするためのアノテーション
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface LoiozAdminControlCheck {
	
	/** 制限対象の管理者制御項目 */
	LoiozAdminControl[] items() default {};
}
