package jp.loioz.common.aspect.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * サブドメイン付きのアクセスが必須
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RequireSubDomain {
}
