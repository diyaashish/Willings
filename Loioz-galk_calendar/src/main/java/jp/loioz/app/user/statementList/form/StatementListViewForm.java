package jp.loioz.app.user.statementList.form;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.user.statementList.dto.StatementListDto;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.constant.SortConstant.StatementListSortItem;
import lombok.Data;

/**
 * 精算書一覧画面の画面表示フォームクラス
 */
@Data
public class StatementListViewForm {

	/** 一覧のソートキー */
	private StatementListSortItem statementListSortItem;

	/** 一覧のソート順 */
	private SortOrder statementListSortOrder;

	/** 一覧データ */
	List<StatementListDto> statementList = Collections.emptyList();

	/** ページャ情報 */
	Page<StatementListDto> page;

}
