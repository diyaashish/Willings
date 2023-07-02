package jp.loioz.app.user.groupManagement.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.GroupListDto;
import lombok.Data;

/**
 * グループ一覧画面のFormクラス
 */
@Data
public class GroupListForm {

	/** グループ情報リスト */
	private List<GroupListDto> groupList;

	/** ページ */
	private Page<GroupListDto> page;

	/** 検索結果件数 */
	private Long count;

}