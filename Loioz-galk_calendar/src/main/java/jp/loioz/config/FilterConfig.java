package jp.loioz.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jp.loioz.common.filter.LogPrepareFilter;
import jp.loioz.common.filter.ThreadPrepareFilter;

/**
 * フィルター処理の設定
 */
@Configuration
public class FilterConfig {

	/**
	 * スレッドの準備フィルター
	 */
	@Bean
	public FilterRegistrationBean<ThreadPrepareFilter> threadPrepareFilter() {
		FilterRegistrationBean<ThreadPrepareFilter> bean = new FilterRegistrationBean<ThreadPrepareFilter>(new ThreadPrepareFilter());
		bean.addUrlPatterns("/*");
		// Spring Securityのフィルター処理より先に実行する
		bean.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1);
		return bean;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// ※ Spring Securityのフィルター処理が実行される。
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * ログ出力の準備フィルター
	 */
	@Bean
	public FilterRegistrationBean<LogPrepareFilter> logPrepareFilter() {
		FilterRegistrationBean<LogPrepareFilter> bean = new FilterRegistrationBean<LogPrepareFilter>(new LogPrepareFilter());
		bean.addUrlPatterns("/*");
		bean.setOrder(1);
		return bean;
	}

}
