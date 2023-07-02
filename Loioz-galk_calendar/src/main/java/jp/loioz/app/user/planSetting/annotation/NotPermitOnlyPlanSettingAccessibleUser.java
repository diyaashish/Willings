package jp.loioz.app.user.planSetting.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * プラン画面以外アクセス不可な状態のユーザーのアクセスを不可とする
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface NotPermitOnlyPlanSettingAccessibleUser {
}
