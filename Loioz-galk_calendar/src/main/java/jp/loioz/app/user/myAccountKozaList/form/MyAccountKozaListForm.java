package jp.loioz.app.user.myAccountKozaList.form;

import java.util.List;

import jp.loioz.dto.KozaDto;
import lombok.Data;

/**
 * 個人口座の設定画面用のフォームクラス
 */
@Data
public class MyAccountKozaListForm {

	/** 口座情報リスト */
	private List<KozaDto> kozaDtoList;

}