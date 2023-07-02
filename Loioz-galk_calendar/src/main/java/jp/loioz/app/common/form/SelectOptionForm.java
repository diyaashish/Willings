package jp.loioz.app.common.form;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.utility.LoiozNumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * セレクトボックスのoptionタグを表現するフォームクラス
 */
@Value
@RequiredArgsConstructor
public class SelectOptionForm {

	/** セレクトボックスの選択肢のvalueの値 */
	String value;

	/** セレクトボックスの選択肢の表示名 */
	String label;

	public SelectOptionForm(Number value, String label) {
		this.value = (value != null ? value.toString() : CommonConstant.BLANK);
		this.label = label;
	}

	public static SelectOptionForm fromEnum(DefaultEnum enumItem) {
		if (enumItem == null) {
			return null;
		}
		return new SelectOptionForm(enumItem.getCd(), enumItem.getVal());
	}

	public Long getValueAsLong() {
		return LoiozNumberUtils.parseAsLong(value);
	}
}
