package jp.loioz.app.user.toiawase.form;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.ToiawaseListDto;
import lombok.Data;

/**
 * 問い合わせ一覧画面用オブジェクト
 */
@Data
public class ToiawaseListViewForm {

	/** 問い合わせ一覧 */
	private List<ToiawaseListDto> toiawaseList = Collections.emptyList();

	/** 検索結果件数 */
	private long count;

	/** ページ */
	private Page<Long> page;
}
