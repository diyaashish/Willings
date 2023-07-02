package jp.loioz.app.user.feeList.form;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.user.feeList.dto.FeeListDto;
import jp.loioz.common.constant.SortConstant.FeeListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import lombok.Data;

/**
 * 報酬一覧画面の画面表示フォームクラス
 */
@Data
public class FeeListViewForm {

	/** 報酬一覧のソートキー */
	private FeeListSortItem feeListSortItem;

	/** 報酬一覧のソート順 */
	private SortOrder feeListSortOrder;

	/** 報酬一覧 */
	List<FeeListDto> feeList = Collections.emptyList();

	/** 「すべて」のページャ情報 */
	Page<FeeListDto> page;

}
