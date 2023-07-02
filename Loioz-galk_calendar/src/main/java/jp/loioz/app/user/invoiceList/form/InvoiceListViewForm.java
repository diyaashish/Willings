package jp.loioz.app.user.invoiceList.form;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.user.invoiceList.dto.InvoiceListDto;
import jp.loioz.common.constant.SortConstant.InvoiceListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import lombok.Data;

/**
 * 請求書一覧画面の画面表示フォームクラス
 */
@Data
public class InvoiceListViewForm {

	/** 一覧のソートキー */
	private InvoiceListSortItem invoiceListSortItem;

	/** 一覧のソート順 */
	private SortOrder invoiceListSortOrder;

	/** 一覧データ */
	List<InvoiceListDto> invoiceList = Collections.emptyList();

	/** ページャ情報 */
	Page<InvoiceListDto> page;

}
