package jp.loioz.app.user.bunyaManagement.form;

import java.util.List;

import jp.loioz.dto.BunyaListDto;
import lombok.Data;

/**
 * 分野の設定画面のフォームクラス
 */
@Data
public class BunyaListForm {

	/** 分野情報リスト */
	private List<BunyaListDto> bunyaList;

}
