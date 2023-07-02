package jp.loioz.app.common.csv.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * CSVファイル出力処理（CsvBuilder）で利用するDto
 */
@Data
public class CsvBuilderDto {

	/** ヘッダー行データ（1行分） */
	private CsvRowDto headerRowDto = new CsvRowDto();
	
	/** データ行データ（複数行分） */
	private List<CsvRowDto> dataRowDtoList = new ArrayList<>();
	
	/**
	 * 各データ行の列数が一致しているかチェック
	 * 
	 * @param noOutputHeaderRow ヘッダー行を出力に含めないかどうか true:含めない
	 * @return true：すべて一致、 false:一致しないものがある
	 */
	public boolean validAllRowColumnCountMatch(boolean noOutputHeaderRow) {
		
		if (dataRowDtoList.isEmpty()) {
			// データ行がない場合（出力はヘッダー行のみの場合）は検証OKとする
			return true;
		}
		
		// 列数の基準行
		CsvRowDto baseRowDto;
		if (!noOutputHeaderRow) {
			// ヘッダー行を出力に含める場合 -> ヘッダー行
			baseRowDto = headerRowDto;
		} else {
			// 含めない場合 -> データ行の1行目
			baseRowDto = dataRowDtoList.get(0);
		}
		
		// 列数の基準値（基準行の列数）
		final int COL_COUNT = baseRowDto.getItemList().size();
		
		// すべての行で列数が一致するかどうか
		boolean isColCountMath = dataRowDtoList.stream()
			.allMatch(row -> row.getItemList().size() == COL_COUNT);

		return isColCountMath;
	}
}
