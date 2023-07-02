package jp.loioz.app.user.gyomuHistory.annitations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 案件軸業務履歴を扱う画面のマーカーアノテーション
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface HasGyomuHistoryByAnken {

}
