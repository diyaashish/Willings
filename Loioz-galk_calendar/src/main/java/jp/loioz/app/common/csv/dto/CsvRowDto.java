package jp.loioz.app.common.csv.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * CSVファイルのデータ1行分を保持するDto
 */
@Data
public class CsvRowDto {

	/** 1行に表示するデータのリスト */
	private List<CsvRowItemDto> itemList = new ArrayList<>();
	
	/**
	 * 1行データの各値を{@literal List<String>}にして取得
	 * 
	 * @param noWrapDataDoubleQuot データをダブルクォーテーションで囲わないかどうか true:囲わない false:囲う
	 * @return
	 */
	public List<String> getItemValList(boolean noWrapDataDoubleQuot) {
		List<String> itemValList = itemList.stream()
			.map(item -> item.getVal())
			.collect(Collectors.toList());
		
		if (noWrapDataDoubleQuot) {
			return itemValList;
		}
		
		List<String> itemValListWithDoubleQuot = itemValList.stream()
			.map(itemVal -> StringUtils.surroundDoubleCoat(itemVal))
			.collect(Collectors.toList());
		
		return itemValListWithDoubleQuot;
	}
}
