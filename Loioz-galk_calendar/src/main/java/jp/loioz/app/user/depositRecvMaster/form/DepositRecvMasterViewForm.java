package jp.loioz.app.user.depositRecvMaster.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.user.depositRecvMaster.dto.DepositRecvMasterListItemDto;
import lombok.Data;

/**
 * 預り金項目 画面のフォームクラス
 */
@Data
public class DepositRecvMasterViewForm {

	/** 預かり項目一覧フラグメントView */
	private DepositRecvMasterListViewForm depositRecvMasterListViewForm;

	/**
	 * 預かり項目一覧フラグメントViewForm
	 */
	@Data
	public static class DepositRecvMasterListViewForm {

		/** 預り金項目リストのDto */
		private List<DepositRecvMasterListItemDto> depositRecvMasterListItemDtoList;

		/** 項目のソートが可能かどうか */
		private boolean canSort;

		/** ページ */
		private Page<Long> page;
	}

}