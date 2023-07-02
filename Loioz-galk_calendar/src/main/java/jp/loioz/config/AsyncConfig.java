package jp.loioz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 非同期処理の設定
 */
@Configuration
@EnableAsync
public class AsyncConfig {

	/**
	 * デフォルトのTaskExecutor
	 *
	 * @return TaskExecutor
	 */
	@Bean
	@ConfigurationProperties("async.thread-pool.default")
	@Primary
	public TaskExecutor defaultTaskExecutor() {
		return new ThreadPoolTaskExecutor();
	}

	/**
	 * メール送信処理用のTaskExecutor
	 *
	 * @return TaskExecutor
	 */
	@Bean("mailSendTaskExecutor")
	@ConfigurationProperties("async.thread-pool.mail-send")
	public TaskExecutor mailSendTaskExecutor() {
		return new ThreadPoolTaskExecutor();
	}
}
