package jp.loioz.app.common.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import jp.loioz.app.common.propertyEditor.StringTrimEditor;

/**
 * 全てのコントローラのセットアップ処理を行うクラス
 */
@ControllerAdvice
public class ControllerSetup {

	@InitBinder
	public void initBinder (WebDataBinder binder)
	{
		// Strginのフィールドの値に対してトリム処理を行うよう設定
		binder.registerCustomEditor(String.class, new StringTrimEditor());
	}
}
