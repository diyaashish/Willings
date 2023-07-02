package jp.loioz.app.user.info.form;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.dto.InfoListForAccountDto;
import lombok.Data;

/**
 * 予定表画面のお知らせ表示フォームクラス
 */
@Data
public class InfoViewForm {

	/** お知らせ一覧を表示するか否か */ // デフォルトはfalse
	private boolean infoOpenFlg = false;

	/** お知らせ一覧 */
	private List<InfoListForAccountDto> infoList = new ArrayList<>();
}
