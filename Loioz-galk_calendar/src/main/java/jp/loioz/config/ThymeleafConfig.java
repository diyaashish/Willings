package jp.loioz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.connect_group.thymeleaf_extras.ThymeleafExtrasDialect;

import jp.loioz.common.thymeleaf.MyThymeleafDialect;

/**
 * Thymeleafの設定
 */
@Configuration
public class ThymeleafConfig {

	/**
	 * 独自Dialectの設定
	 *
	 * @return Dialect
	 */
	@Bean
	public MyThymeleafDialect myThymeleafDialect() {
		return new MyThymeleafDialect();
	}

	/**
	 * ThymeleafExtrasDialectの設定
	 *
	 * @return Dialect
	 */
	@Bean
	public ThymeleafExtrasDialect thymeleafExtrasDialect() {
		return new ThymeleafExtrasDialect();
	}
}
