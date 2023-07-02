package jp.loioz.common.aspect.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.loioz.common.constant.CommonConstant.AccountKengen;

/**
 * 特定のアカウント権限以外による処理の実行を禁止する
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface PermitAccountKengen {

	AccountKengen[] value() default {};

}
