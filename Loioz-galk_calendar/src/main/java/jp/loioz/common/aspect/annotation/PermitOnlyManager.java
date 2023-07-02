package jp.loioz.common.aspect.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * システム管理者以外による処理の実行を禁止する
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface PermitOnlyManager {

}
