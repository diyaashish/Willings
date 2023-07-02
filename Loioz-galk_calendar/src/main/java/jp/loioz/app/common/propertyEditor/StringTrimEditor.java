package jp.loioz.app.common.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.springframework.lang.Nullable;

/**
 * String型のプロパティに対してトリム処理を行うエディタークラス
 */
public class StringTrimEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(@Nullable String text) {

		if (text == null) {
			setValue(null);
			return;
		}

		char[] charList = text.toCharArray();
		int length = text.length();
		int startIndex = 0;
		while (startIndex < length && (charList[startIndex] <= ' ' || charList[startIndex] == '　')) {
			startIndex++;
		}
		while (startIndex < length && (charList[length - 1] <= ' ' || charList[length - 1] == '　')) {
			length--;
		}
		if (startIndex == 0 && length == text.length()) {
			setValue(text);
			return;
		}

		setValue(text.substring(startIndex, length));
	}

	@Override
	public String getAsText() {
		Object value = getValue();
		return (value != null ? value.toString() : "");
	}
}
