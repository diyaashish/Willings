package jp.loioz.app.user.depositRecvList.form;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.user.depositRecvList.dto.DepositRecvListDto;
import jp.loioz.common.constant.SortConstant.DepositRecvListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import lombok.Data;

/**
 * 預り金管理一覧画面の画面表示フォームクラス
 */
@Data
public class DepositRecvListViewForm {

	/** 一覧のソートキー */
	private DepositRecvListSortItem depositRecvListSortItem;

	/** 一覧のソート順 */
	private SortOrder depositRecvListSortOrder;

	/** 一覧データ */
	List<DepositRecvListDto> depositRecvList = Collections.emptyList();

	/** ページャ情報 */
	Page<DepositRecvListDto> page;

}
