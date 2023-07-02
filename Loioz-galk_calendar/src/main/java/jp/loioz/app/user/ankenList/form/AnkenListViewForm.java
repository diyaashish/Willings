package jp.loioz.app.user.ankenList.form;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.common.constant.SortConstant.AnkenListSortItem;
import jp.loioz.common.constant.SortConstant.SaibanListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.dto.AnkenListDto;
import jp.loioz.dto.SaibanListDto;
import lombok.Data;

/**
 * 案件一覧画面の画面表示フォームクラス
 */
@Data
public class AnkenListViewForm {

	/** 案件系の表示フォーム */
	private AnkenKeiListViewForm ankenKeiListViewForm;

	/** 裁判系の表示フォーム */
	private SaibanKeiListViewForm saibanKeiListViewForm;

	/**
	 * 案件一覧情報
	 */
	@Data
	public static class AnkenKeiListViewForm {

		/** 案件一覧情報 */
		private List<AnkenListDto> ankenList = new ArrayList<AnkenListDto>();

		/** 案件一覧のソートキー */
		private AnkenListSortItem ankenListSortKey;

		/** 案件一覧のソート順 */
		private SortOrder ankenListSortOrder;

		/** ページ */
		private Page<AnkenListDto> page;
	}

	/**
	 * 裁判一覧情報
	 */
	@Data
	public static class SaibanKeiListViewForm {

		/** 裁判一覧情報 */
		private List<SaibanListDto> saibanList = new ArrayList<SaibanListDto>();

		/** 裁判一覧のソートキー */
		private SaibanListSortItem saibanListSortKey;

		/** 裁判一覧のソート順 */
		private SortOrder saibanListSortOrder;

		/** ページ */
		private Page<SaibanListDto> page;
	}
}
