package jp.loioz.app.user.schedule.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 予定を扱う画面のマーカーアノテーション
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface HasSchedule {
}