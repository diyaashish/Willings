package jp.loioz.app.common.csv.common;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import jp.loioz.app.common.csv.dto.CsvBuilderDto;
import jp.loioz.app.common.csv.dto.CsvRowDto;
import jp.loioz.common.constant.ChohyoConstatnt.OutputCsvTemplate;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.OutputConstant;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 画面の一覧出力処理（CSV出力）用抽象クラス<br>
 * 一覧からのCSV出力処理は、当クラスを継承して作成する。
 */

@Data
public abstract class AbstractMakeCsvBuilder<T extends CsvBuilderDto> {

	/**
	 * 生成するCSVファイル定義
	 */
	private OutputCsvTemplate outputCsvTemplate;

	/**
	 * CSVファイル名<br>
	 *
	 * ※ファイル名を指定しない場合は、ChohyoConstantで定義したファイルとなる
	 */
	private String fileName;

	/**
	 * ヘッダー行の出力を行わないかどうか
	 * false: 行う、 true:行わない
	 */
	private boolean noOutputHeaderRow = false;

	/**
	 * データをダブルクォーテーションで囲わずに出力するかどうか
	 * false: 囲う、 true:囲わない
	 */
	private boolean noWrapDataDoubleQuot = false;

	/**
	 * メッセージサービス
	 */
	private MessageService messageService;

	/**
	 * 対応するBuilderのDTOを生成する
	 *
	 * @return 対応するBuilderのDTO
	 */
	public abstract T createNewTargetBuilderDto();

	/**
	 * CSVのヘッダー行データを取得する
	 * 
	 * @return CsvRowDto
	 */
	public abstract CsvRowDto getHeaderRowDto();

	/**
	 * ダウンロード対象のデータ件数が適切か判定する<br>
	 * 
	 * @param dataCount
	 * @param response
	 * @return true:ダウンロード可能 false:ダウンロード不可
	 */
	public boolean validDataCountOver(int dataCount, HttpServletResponse response) {

		if (dataCount > OutputConstant.MAX_CSV_OUTPUT_DATA) {
			String message = messageService.getMessage(MessageEnum.MSG_E00158, Locale.JAPANESE, String.valueOf(OutputConstant.MAX_CSV_OUTPUT_DATA));

			// ダウンロード可能件数を超えている場合はエラーメッセージをセットする
			Charset charset = StandardCharsets.UTF_8;
			String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes(charset));

			response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT_MESSAGE, encodedMessage);
			return false;
		}
		return true;
	}

	/**
	 * ダウンロードするCSVファイルを作成
	 * 
	 * @param outputCsvTemplate
	 * @param csvBuilderDto
	 * @param response
	 * @throws Exception
	 */
	protected void makeCsvFile(OutputCsvTemplate outputCsvTemplate, T csvBuilderDto, HttpServletResponse response) throws Exception {

		boolean isColumnCountMatch = csvBuilderDto.validAllRowColumnCountMatch(this.noOutputHeaderRow);
		if (!isColumnCountMatch) {
			throw new IllegalStateException("出力するデータの列数が統一されていません");
		}

		this.outputCsvTemplate = outputCsvTemplate;

		// CSVファイル作成
		this.makeDownloadableState(response);

		try (PrintWriter pw = response.getWriter()) {

			// データセット
			this.setCsvData(pw, csvBuilderDto);

		}
	}

	/**
	 * ダウンロードするCSVファイルの名前と文字コードを指定する
	 * 
	 * @param response
	 * @throws Exception
	 */
	protected void makeDownloadableState(HttpServletResponse response) throws Exception {

		// 出力日の取得
		String date = DateUtils.parseToString(LocalDateTime.now(),
				DateUtils.DATE_YYYY + DateUtils.DATE_MM + DateUtils.DATE_DD + DateUtils.TIME_FORMAT_HH + DateUtils.TIME_FORMAT_MM
						+ DateUtils.TIME_FORMAT_SS);

		// ファイル名
		String fineName = this.fileName;
		if (StringUtils.isEmpty(fineName)) {
			fineName = this.outputCsvTemplate.getVal();
		}
		String encodeFileName = fineName + CommonConstant.UNDER_BAR + date + this.outputCsvTemplate.getFileType();

		// 文字コードと出力するCSVファイル名を設定
		response.setContentType("text/csv" + ";charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(encodeFileName, StandardCharsets.UTF_8.name()));
	}

	/**
	 * CSVファイルに出力データを設定する
	 * 
	 * @param pw
	 * @param csvBuilderDto
	 */
	protected void setCsvData(PrintWriter pw, T csvBuilderDto) {

		if (!this.noOutputHeaderRow) {
			// ヘッダー行の書き込みを行う
			CsvRowDto headerRowDto = csvBuilderDto.getHeaderRowDto();
			String headerStr = this.buildCsvRowStr(headerRowDto, true);
			pw.write(headerStr);
		}

		// データ行の書き込みを行う
		List<CsvRowDto> dataRowDtoList = csvBuilderDto.getDataRowDtoList();

		if (dataRowDtoList.isEmpty()) {
			return;
		}

		// 最後の行データを取得（リストからも除去）
		CsvRowDto lastRowDto = dataRowDtoList.remove(dataRowDtoList.size() - 1);

		// 最後の行データ以外のデータの書き込み
		for (CsvRowDto rowDto : dataRowDtoList) {
			String rowStr = this.buildCsvRowStr(rowDto, true);
			pw.write(rowStr);
		}

		// 最後の行データの書き込み
		String lastRowStr = this.buildCsvRowStr(lastRowDto, false);
		pw.write(lastRowStr);
	}

	/**
	 * CsvRowDtoが保持するデータから、<br>
	 * CSVファイルの1行分の書き込み文字列を組み立てる。
	 * 
	 * @param rowDto
	 * @param withLineBreak 改行文字を付与するかどうか true:付与する
	 * @return
	 */
	protected String buildCsvRowStr(CsvRowDto rowDto, boolean withLineBreak) {

		List<String> rowItemValList = rowDto.getItemValList(this.noWrapDataDoubleQuot);
		String headerStr = StringUtils.join(",", rowItemValList);

		if (withLineBreak) {
			headerStr = headerStr + "\r\n";
		}

		return headerStr;
	}

}
