package jp.loioz.app.user.kanyosha.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.kanyosha.dto.KanyoshaListDto;
import lombok.Data;

/**
 * 関与者一覧画面のFormクラス
 */
@Data
public class KanyoshaListForm {

	/** 案件ID */
	private Long ankenId;

	/** 関与者/当事者一覧の一覧情報 */
	private List<KanyoshaListDto> kanyoshaDtoList = Collections.emptyList();

}