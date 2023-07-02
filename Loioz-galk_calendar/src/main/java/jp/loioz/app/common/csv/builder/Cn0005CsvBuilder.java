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
 * 民事裁判一覧CSV出力用クラス
 *
 */
@Setter
public class Cn0005CsvBuilder extends AbstractMakeCsvBuilder<CsvBuilderDto> {

	/** ファイル名 */
	public static final String FILE_NAME = "民事裁判";

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

		itemList.add(new CsvRowItemDto("案件ID"));
		itemList.add(new CsvRowItemDto("分野"));
		itemList.add(new CsvRowItemDto("裁判所"));
		itemList.add(new CsvRowItemDto("事件番号"));
		itemList.add(new CsvRowItemDto("事件名"));
		itemList.add(new CsvRowItemDto("名前（当事者）"));
		itemList.add(new CsvRowItemDto("相手方"));
		itemList.add(new CsvRowItemDto("裁判手続き"));
		itemList.add(new CsvRowItemDto("担当弁護士１"));
		itemList.add(new CsvRowItemDto("担当弁護士２"));
		itemList.add(new CsvRowItemDto("担当弁護士３"));
		itemList.add(new CsvRowItemDto("担当弁護士４"));
		itemList.add(new CsvRowItemDto("担当弁護士５"));
		itemList.add(new CsvRowItemDto("担当弁護士６"));
		itemList.add(new CsvRowItemDto("担当事務１"));
		itemList.add(new CsvRowItemDto("担当事務２"));
		itemList.add(new CsvRowItemDto("担当事務３"));
		itemList.add(new CsvRowItemDto("担当事務４"));
		itemList.add(new CsvRowItemDto("担当事務５"));
		itemList.add(new CsvRowItemDto("担当事務６"));
		itemList.add(new CsvRowItemDto("申立日"));
		itemList.add(new CsvRowItemDto("終了日"));

		headerRow.setItemList(itemList);
		return headerRow;
	}

	/**
	 * {@inheritDoc}
	 */
	public void makeCsvFile(HttpServletResponse response) throws IOException, Exception {
		super.makeCsvFile(OutputCsvTemplate.CSV_NAIBU_5, this.csvBuilderDto, response);
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
