package jp.loioz.app.user.depositRecvDetail.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.common.form.accg.AccgCaseForm;
import jp.loioz.app.common.form.accg.AccgCasePersonForm;
import jp.loioz.app.common.form.accg.AccgCaseSummaryForm;
import jp.loioz.app.user.depositRecvDetail.dto.DepositRecvDetailListDto;
import jp.loioz.common.constant.SortConstant.DepositRecvDetailSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import lombok.Data;

/**
 * 預り金明細画面の画面表示フォームクラス
 */
@Data
public class DepositRecvDetailViewForm {

	/** 会計管理-案件共通 */
	private AccgCaseForm accgCaseForm;

	/** 会計管理-案件共通-報酬、預り金 */
	private AccgCaseSummaryForm accgCaseSummaryForm;

	/** 会計管理-共通サイド-対象の顧客 */
	private AccgCasePersonForm accgCasePersonForm;

	/** 預り金明細一覧 */
	List<DepositRecvDetailListDto> depositRecvDetailList = Collections.emptyList();

	/** 一覧のソートキー */
	private DepositRecvDetailSortItem depositRecvDetailSortItem;

	/** 一覧のソート順 */
	private SortOrder depositRecvDetailSortOrder;
}
