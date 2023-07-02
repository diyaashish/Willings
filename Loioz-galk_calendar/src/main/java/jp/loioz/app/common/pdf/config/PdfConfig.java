package jp.loioz.app.common.pdf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * PDFの設定クラス
 */
@Data
@Configuration
public class PdfConfig {

	/** PDFテンプレートファイルパス */
	@Value("${pdf.root}")
	private String pdfRoot;

	/** PDFのテスト出力パス */
	@Value("${test.output}")
	private String pdfTestOutputPath;

}
