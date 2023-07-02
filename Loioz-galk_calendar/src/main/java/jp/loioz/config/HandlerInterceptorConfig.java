package jp.loioz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.handlerInterceptor.LoiozAdminControlHandlerInterceptor;
import jp.loioz.common.handlerInterceptor.PlanFuncRestrictHandlerInterceptor;
import jp.loioz.common.handlerInterceptor.PreventMultiLoginHandlerInterceptor;
import jp.loioz.common.handlerInterceptor.SettingBackLinkSessionValueHandlerInterceptor;
import jp.loioz.common.utility.LoiozArrayUtils;

/**
 * HandlerInterceptorクラスの設定
 */
@Configuration
public class HandlerInterceptorConfig implements WebMvcConfigurer {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 二重ログイン防止のハンドラーインターセプターを設定
		registry.addInterceptor(preventMultiLoginHandlerInterceptor())
			.addPathPatterns("/**") // 適用対象のパス(パターン)を指定
			.excludePathPatterns(LoiozArrayUtils.addAll(
				WebSecurityConfig.URL_PATTERNS_IGNOR_SECURITY_CHECK,
				WebSecurityConfig.URL_PATTERNS_ACCESSIBLE_NO_LOGIN
			)); // 除外するパス(パターン)を指定
		
		// 「戻る」リンク初期化のハンドラーインターセプターを設定
		registry.addInterceptor(settingBackLinkSessionValueHandlerInterceptor())
			.addPathPatterns("/**") // 適用対象のパス(パターン)を指定
			.excludePathPatterns(LoiozArrayUtils.addAll(
					WebSecurityConfig.URL_PATTERNS_IGNOR_SECURITY_CHECK,
					WebSecurityConfig.URL_PATTERNS_ACCESSIBLE_NO_LOGIN
			)); // 除外するパス(パターン)を指定除外するパス(パターン)を指定
		
		// プラン別機能制限HandlerInterceptor
		registry.addInterceptor(planFuncRestrictHandlerInterceptor())
			.addPathPatterns("/**") // 適用対象のパス(パターン)を指定
			.excludePathPatterns(LoiozArrayUtils.addAll(
					LoiozArrayUtils.addAll(
					WebSecurityConfig.URL_PATTERNS_IGNOR_SECURITY_CHECK,
					WebSecurityConfig.URL_PATTERNS_ACCESSIBLE_NO_LOGIN
				),
				UrlConstant.PLAN_CHANGED_ERROR_URL,
				UrlConstant.PLAN_FUNC_RESTRICT_ERROR_URL,
				"/" + UrlConstant.PLANSETTING_TENANT_ACCESS_URL + "/**"
			)); // 除外するパス(パターン)を指定
				// ※プラン権限変更のエラー画面へのアクセスと、プラン画面でのプラン変更後のSession情報の変更処理へのアクセスは除外する
				//  (除外しないとInterceptorで行う、プラン変更が発生した場合の処理に引っ掛かり行いたい処理が実行できないため)
		
		// ロイオズ管理者制御による実行制限HandlerInterceptor
		registry.addInterceptor(loiozAdminControlHandlerInterceptor())
			.addPathPatterns("/**") // 適用対象のパス(パターン)を指定
			.excludePathPatterns(LoiozArrayUtils.addAll(
				WebSecurityConfig.URL_PATTERNS_IGNOR_SECURITY_CHECK,
				WebSecurityConfig.URL_PATTERNS_ACCESSIBLE_NO_LOGIN
			)); // 除外するパス(パターン)を指定除外するパス(パターン)を指定
	}
	
	/**
	 * Bean定義
	 * @return PreventMultiLoginHandlerInterceptor 二重ログイン防止処理のHandlerInterceptor
	 */
	@Bean
	PreventMultiLoginHandlerInterceptor preventMultiLoginHandlerInterceptor() {
		return new PreventMultiLoginHandlerInterceptor();
	}
	
	/**
	 * Bean定義
	 * @return SettingBackLinkSessionValueHandlerInterceptor
	 */
	@Bean
	SettingBackLinkSessionValueHandlerInterceptor settingBackLinkSessionValueHandlerInterceptor() {
		return new SettingBackLinkSessionValueHandlerInterceptor();
	}
	
	/**
	 * Bean定義
	 * @return PlanFuncRestrictHandlerInterceptor プラン別機能制限（実行制限）を行うHandlerInterceptor
	 */
	@Bean
	PlanFuncRestrictHandlerInterceptor planFuncRestrictHandlerInterceptor() {
		return new PlanFuncRestrictHandlerInterceptor();
	}
	
	/**
	 * Bean定義
	 * @return LoiozAdminControlHandlerInterceptor ロイオズ管理者制御による実行制限を行うHandlerInterceptor
	 */
	@Bean
	LoiozAdminControlHandlerInterceptor loiozAdminControlHandlerInterceptor() {
		return new LoiozAdminControlHandlerInterceptor();
	}
	
}
