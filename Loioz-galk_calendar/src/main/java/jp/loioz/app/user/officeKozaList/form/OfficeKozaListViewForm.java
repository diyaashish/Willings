package jp.loioz.app.user.officeKozaList.form;

import java.util.List;

import jp.loioz.dto.KozaDto;
import lombok.Data;

/**
 * 事務所口座の設定画面用のフォームクラス
 */
@Data
public class OfficeKozaListViewForm {

	/** 口座情報リスト */
	private List<KozaDto> kozaDtoList;

}