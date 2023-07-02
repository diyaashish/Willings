package jp.loioz.app.common.word.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Wordの設定クラス
 */
@Data
@Configuration
public class WordConfig {

	@Value("${word.root}")
	private String wordRoot;
}
