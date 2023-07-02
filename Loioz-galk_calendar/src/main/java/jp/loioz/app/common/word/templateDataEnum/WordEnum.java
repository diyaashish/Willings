package jp.loioz.app.common.word.templateDataEnum;

import java.util.Collections;
import java.util.List;

/**
 * Word出力用Enumの基底Enum
 */
public interface WordEnum {

	/** wordのテンプレートに記載しているKey文字列を取得 */
	String getKey();

	/** Key文字列と対応する値文字列を取得 */
	String getValue();

	default List<String> getListValue() {
		return Collections.emptyList();
	}
}
