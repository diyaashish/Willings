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
 * 名簿一覧（個人顧客）CSV出力用クラス
 *
 */
@Setter
public class Cn0003CsvBuilder extends AbstractMakeCsvBuilder<CsvBuilderDto> {

	/** ファイル名 */
	public static final String FILE_NAME = "顧客名簿（個人）";

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

		itemList.add(new CsvRowItemDto("名簿ID"));
		itemList.add(new CsvRowItemDto("名前"));
		itemList.add(new CsvRowItemDto("郵便番号"));
		itemList.add(new CsvRowItemDto("住所"));
		itemList.add(new CsvRowItemDto("電話番号"));
		itemList.add(new CsvRowItemDto("メールアドレス"));
		itemList.add(new CsvRowItemDto("登録日"));
		itemList.add(new CsvRowItemDto("特記事項"));

		headerRow.setItemList(itemList);
		return headerRow;
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeCsvFile(HttpServletResponse response) throws IOException, Exception {
		super.makeCsvFile(OutputCsvTemplate.CSV_NAIBU_3, this.csvBuilderDto, response);
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
