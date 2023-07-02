package jp.loioz.app.common.mvc.kanyosha.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.common.mvc.kanyosha.dto.PersonSearchResultDto;
import lombok.Data;

/**
 * 関与者共通検索フォームオブジェクト
 */
@Data
public class KanyoshaSearchModalViewForm {

	/** 案件ID */
	private Long ankenId;

	/** 表示件数を超えたフラグ */
	private boolean overViewCntFlg;

	/** 検索結果（すでに案件に関与者として紐づいているデータは取得されない） */
	List<PersonSearchResultDto> meiboList = Collections.emptyList();

}
