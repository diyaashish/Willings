package jp.loioz.app.common.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.springframework.lang.Nullable;

import jp.loioz.common.utility.StringUtils;

/**
 * 空文字列をnullに変換するエディタークラス
 */
public class EmptyToNullEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(@Nullable String text) {
		setValue(StringUtils.stripToNull(text));
	}
}
