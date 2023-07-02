package jp.loioz.app.common.mvc.anken.popover.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.dto.AnkenDto;
import lombok.Data;

/**
 * 案件情報ポップオーバー画面オブジェクト
 */
@Data
public class AnkenPopoverViewForm {

	/** 案件リスト */
	private List<AnkenDto> ankenList;

	/** 名簿ID */
	private Long personId;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 名簿名 */
	private String personName;

	/** 一覧のページャ情報 */
	private Page<Long> pager;

}
