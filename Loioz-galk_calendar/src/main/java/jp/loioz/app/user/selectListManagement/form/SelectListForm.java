package jp.loioz.app.user.selectListManagement.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.SelectListDto;
import lombok.Data;

/**
 * 選択肢設定画面のフォームクラス
 */
@Data
public class SelectListForm {

	/** 選択肢情報リスト */
	private List<SelectListDto> selectList;

	/** ページ */
	private Page<SelectListDto> page;

}
