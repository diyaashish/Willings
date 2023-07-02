package jp.loioz.app.user.feeMaster.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.feeMaster.dto.FeeMasterListItemDto;
import lombok.Data;

/**
 * 報酬項目の設定画面の画面表示フォームクラス
 */
@Data
public class FeeMasterViewForm {

	/** 報酬項目一覧フラグメントビューフォーム */
	private FeeMasterListViewForm feeMasterListViewForm;

	/**
	 * 報酬項目一覧フラグメントビューフォーム
	 */
	@Data
	public static class FeeMasterListViewForm {

		/** 報酬項目一覧 */
		List<FeeMasterListItemDto> feeMasterList = Collections.emptyList();

	}

}
