package jp.loioz.app.common.pdf.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.pdf.config.PdfConfig;
import jp.loioz.app.common.pdf.data.PdfData;
import jp.loioz.common.constant.ChohyoConstatnt.OutputPdfFonts;
import jp.loioz.common.constant.ChohyoConstatnt.OutputPdfTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.FileExtension;
import jp.loioz.common.service.pdf.PdfService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.FileContentsDto;
import lombok.Data;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.SimpleFontFace;
import net.sf.jasperreports.engine.fonts.SimpleFontFamily;

/**
 * PDF出力用抽象クラス
 */
@Data
public abstract class AbstractMakePdfBuilder<T extends PdfData> {

	/** 利用フォント定数 */
	private static final List<OutputPdfFonts> OUTPUT_FONTS = Arrays.asList(OutputPdfFonts.BIZ_UDP_MINCHO, OutputPdfFonts.IPA_EX_MINCHO);

	/**
	 * 設定クラス
	 */
	private PdfConfig config;

	/**
	 * PdfBoxService
	 */
	private PdfService pdfService;

	/**
	 * 生成するPdfファイル定義
	 */
	private OutputPdfTemplate formTemplate;

	/**
	 * PDF出力用データ
	 */
	private T pdfData;

	/**
	 * PDFファイル名<br>
	 *
	 * ※ファイル名を指定しない場合は、ChohyoConstantで定義したファイルとなる
	 */
	private String fileName;

	/**
	 * PDFを作成し、HttpResponseに書き込みを行う
	 *
	 * @param formTemplate
	 * @param response
	 * @throws Exception
	 */
	protected void makePdfFile(OutputPdfTemplate formTemplate, HttpServletResponse response) throws Exception {

		// PDFのbyte[]を作成
		FileContentsDto pdfByteContents = makePdfContentsDto(formTemplate);

		// ダウンロード設定（HttpResponseへの書き込み処理）
		this.makeDownloadableState(pdfByteContents, response);
	}

	/**
	 * PDFファイルを作成し、バイトデータを作成する
	 *
	 * @param formTemplate
	 * @return
	 * @throws Exception
	 */
	protected FileContentsDto makePdfContentsDto(OutputPdfTemplate formTemplate) throws Exception {

		// テンプレートの設定
		this.formTemplate = formTemplate;

		// テンプレートXMLPathの取得
		String inputFilePath = this.getInputFilePath();

		try (InputStream is = new FileInputStream(inputFilePath);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();) {

			// 出力情報の作成
			Map<String, Object> parameterMap = pdfData.convertPdfParams();
			SimpleJasperReportsContext jasperReportsContext = this.createJasperReportContext(OUTPUT_FONTS);

			// 出力用インスタンスの作成
			JasperReport report = JasperCompileManager.compileReport(is);
			JasperPrint jasperPrint = JasperFillManager.getInstance(jasperReportsContext).fill(report, parameterMap, new JREmptyDataSource());

			JasperExportManager.getInstance(jasperReportsContext).exportToPdfStream(jasperPrint, baos);
			byte[] byteArray = baos.toByteArray();

			// ファイルオブジェクトを作成
			FileContentsDto pdfBytesContents = new FileContentsDto(byteArray, FileExtension.PDF.getVal());

			// ページャ情報の書き込み
			pdfService.writePdfPeger(pdfBytesContents);

			return pdfBytesContents;
		}

	}

	/**
	 * テンプレートファイルのパスを取得する
	 * 
	 * @return
	 */
	private String getInputFilePath() {

		// ファイル名取得
		String fileName = this.formTemplate.getCd();
		// ファイルパス
		String dirPath = "";
		// ファイル拡張子
		String extension = this.formTemplate.getTempFileType();
		// ファイルパス生成
		String pdfRoot = this.config.getPdfRoot();
		String inputFilePath = pdfRoot + dirPath + fileName + extension;

		return inputFilePath;
	}

	/**
	 * jasperReportのコンテキストを作成する
	 *
	 * @param fonts 使用するフォントを設定
	 * @return
	 * @throws Exception
	 */
	private SimpleJasperReportsContext createJasperReportContext(List<OutputPdfFonts> fonts) throws Exception {
		SimpleJasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();

		if (CollectionUtils.isEmpty(fonts)) {
			return jasperReportsContext;
		}

		// ファイルパス生成
		final String pdfRoot = this.config.getPdfRoot();

		// 使用するフォントをjasperReportContextに追加する
		List<SimpleFontFamily> fontFamilys = new ArrayList<>();
		for (OutputPdfFonts font : fonts) {
			SimpleFontFamily fontFamily = new SimpleFontFamily(jasperReportsContext);
			fontFamily.setName(font.getVal());
			fontFamily.setPdfEmbedded(true);
			fontFamily.setPdfEncoding("Identity-H"); // フォント埋め込み時のに設定するエンコーディング設定

			// ファイルパス
			String dirPath = font.getDir();
			// ファイル名取得
			String fileName = font.getFileName();
			// ファイル拡張子
			String extension = font.getFileType();

			SimpleFontFace regular = new SimpleFontFace(jasperReportsContext);
			regular.setTtf(pdfRoot + dirPath + fileName + extension);
			fontFamily.setNormalFace(regular);
			fontFamilys.add(fontFamily);
		}

		jasperReportsContext.setExtensions(FontFamily.class, fontFamilys);
		return jasperReportsContext;
	}

	/**
	 * 作成したPdfファイルをダウンロード可能な状態にする
	 *
	 * @param jasperPrint
	 * @param jasperReportsContext
	 * @param response
	 * @throws Exception
	 */
	private void makeDownloadableState(FileContentsDto pdfByteContents, HttpServletResponse response) throws Exception {

		// 出力日の取得
		String date = DateUtils.parseToString(LocalDateTime.now(),
				DateUtils.DATE_YYYY + DateUtils.DATE_MM + DateUtils.DATE_DD + DateUtils.TIME_FORMAT_HH + DateUtils.TIME_FORMAT_MM
						+ DateUtils.TIME_FORMAT_SS);

		String fineName = this.fileName;
		if (StringUtils.isEmpty(fineName)) {
			fineName = this.formTemplate.getVal();
		}
		String encodeFileName = fineName + CommonConstant.UNDER_BAR + date + this.formTemplate.getOutputFileType();

		// ファイルダウンロードの準備
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename*=" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()));

		// HttpReponseのOutputStreamに書き込み処理を行う
		try (InputStream is = new ByteArrayInputStream(pdfByteContents.getByteArray());) {
			IOUtils.copy(is, response.getOutputStream());
		}
	}

	// ================================================================
	// 以下、帳票テスト出力用メソッド
	// PDFテンプレートの作成・修正時に画面操作を行わずに出力処理を行うために作成
	// ================================================================

	/**
	 * PDF出力テスト用
	 * 
	 * @param formTemplate
	 * @throws Exception
	 */
	protected void makePdfFileTest(OutputPdfTemplate formTemplate) throws Exception {

		// PDFのbyte[]を作成
		FileContentsDto pdfByteContents = makePdfContentsDto(formTemplate);

		// 出力用ファイル名
		String date = DateUtils.parseToString(LocalDateTime.now(),
				DateUtils.DATE_YYYY + DateUtils.DATE_MM + DateUtils.DATE_DD + DateUtils.TIME_FORMAT_HH + DateUtils.TIME_FORMAT_MM
						+ DateUtils.TIME_FORMAT_SS);

		String fineName = this.fileName;
		if (StringUtils.isEmpty(fineName)) {
			fineName = this.formTemplate.getVal();
		}
		String outputFileName = fineName + CommonConstant.UNDER_BAR + date + this.formTemplate.getOutputFileType();

		File dir = new File(this.config.getPdfTestOutputPath());
		if (!dir.exists()) {
			dir.mkdir();
		}

		// ファイルの書き込み処理
		try (InputStream is = new ByteArrayInputStream(pdfByteContents.getByteArray());
				OutputStream os = new FileOutputStream(this.config.getPdfTestOutputPath() + "/" + outputFileName);) {
			IOUtils.copy(is, os);
		}

	}

}
