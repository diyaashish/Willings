package jp.loioz.app.common.excel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Excelの設定クラス
 */
@Data
@Configuration
public class ExcelConfig {

	@Value("${excel.root}")
	private String excelRoot;
}
