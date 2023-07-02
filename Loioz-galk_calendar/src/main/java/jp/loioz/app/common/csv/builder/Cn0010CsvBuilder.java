package jp.loioz.app.common.csv.builder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jp.loioz.app.common.csv.common.AbstractMakeCsvBuilder;
import jp.loioz.app.common.csv.dto.CsvBuilderDto;
import jp.loioz.app.common.csv.dto.CsvRowDto;
import jp.loioz.app.common.csv.dto.CsvRowItemDto;
import jp.loioz.common.constant.ChohyoConstatnt.OutputCsvTemplate;
import lombok.Setter;

/**
 * 名簿一覧（すべて）CSV出力用クラス
 *
 */
@Setter
public class Cn0010CsvBuilder extends AbstractMakeCsvBuilder<CsvBuilderDto> {

	/** ファイル名 */
	public static final String FILE_NAME = "筆まめ_名簿（すべて）";

	/**
	 * Builder用DTO
	 */
	private CsvBuilderDto csvBuilderDto;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CsvBuilderDto createNewTargetBuilderDto() {
		return new CsvBuilderDto();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CsvRowDto getHeaderRowDto() {

		CsvRowDto headerRow = new CsvRowDto();

		List<CsvRowItemDto> itemList = new ArrayList<>();

		itemList.add(new CsvRowItemDto("フリガナ"));
		itemList.add(new CsvRowItemDto("氏名"));
		itemList.add(new CsvRowItemDto("旧姓等"));
		itemList.add(new CsvRowItemDto("性別"));
		itemList.add(new CsvRowItemDto("誕生日"));
		itemList.add(new CsvRowItemDto("〒"));
		itemList.add(new CsvRowItemDto("住所1"));
		itemList.add(new CsvRowItemDto("住所2"));
		itemList.add(new CsvRowItemDto("TEL"));
		itemList.add(new CsvRowItemDto("FAX"));
		itemList.add(new CsvRowItemDto("e-mail"));
		itemList.add(new CsvRowItemDto("会社名"));
		itemList.add(new CsvRowItemDto("部署名1"));
		itemList.add(new CsvRowItemDto("分類"));
		itemList.add(new CsvRowItemDto("メモ"));

		headerRow.setItemList(itemList);
		return headerRow;
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeCsvFile(HttpServletResponse response) throws IOException, Exception {
		super.makeCsvFile(OutputCsvTemplate.CSV_NAIBU_10, this.csvBuilderDto, response);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 */
	@Override
	public void setCsvData(PrintWriter pw, CsvBuilderDto csvBuilderDto) {
		super.setCsvData(pw, csvBuilderDto);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
