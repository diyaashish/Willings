package jp.loioz.app.user.funcOldKaikeiSetting.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.user.funcOldKaikeiSetting.dto.FuncOldKaikeiSettingListItemDto;
import lombok.Data;

/**
 * 入出金項目 画面のフォームクラス
 */
@Data
public class FuncOldKaikeiSettingViewForm {

	/** 入出金項目一覧フラグメントView */
	private FuncOldKaikeiSettingListViewForm funcOldKaikeiSettingListViewForm;

	/**
	 * 入出金項目一覧フラグメントViewForm
	 */
	@Data
	public static class FuncOldKaikeiSettingListViewForm {

		/** 入出金項目リストのDto */
		private List<FuncOldKaikeiSettingListItemDto> funcOldKaikeiSettingListItemDtoList;

		/** 項目のソートが可能かどうか */
		private boolean canSort;

		/** ページ */
		private Page<Long> page;
	}

}