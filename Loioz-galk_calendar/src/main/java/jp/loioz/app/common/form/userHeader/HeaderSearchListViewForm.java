package jp.loioz.app.common.form.userHeader;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;

/**
 * ヘッダー検索一覧画面フォームオブジェクト
 */
@Data
public class HeaderSearchListViewForm {

	/** 名簿件数 */
	private long personCount;

	/** 名簿件数カウンタ上限超えているかどうか */
	private boolean isPersonCounterLimitOver;

	/** 案件件数 */
	private long ankenCount;

	/** 案件件数カウンタ上限超えているかどうか */
	private boolean isAnkenCounterLimitOver;

	/** 裁判件数 */
	private long saibanCount;

	/** 裁判件数カウンタ上限超えているかどうか */
	private boolean isSaibanCounterLimitOver;

	/** ヘッダー検索名簿一覧フラグメントViewForm */
	private HeaderSearchPersonListFragmentViewForm headerSearchPersonListFragmentViewForm;

	/** ヘッダー検索案件一覧フラグメントViewForm */
	private HeaderSearchAnkenListFragmentViewForm headerSearchAnkenListFragmentViewForm;

	/** ヘッダー検索裁判一覧フラグメントViewForm */
	private HeaderSearchSaibanListFragmentViewForm headerSearchSaibanListFragmentViewForm;

	/**
	 * ヘッダー検索結果名簿一覧フラグメントView
	 */
	@Data
	public static class HeaderSearchPersonListFragmentViewForm {

		/** 名簿一覧 */
		private List<PersonListDto> personList = Collections.emptyList();

		/** 名簿一覧のページャ情報 */
		private Page<Long> pager;

	}

	/**
	 * ヘッダー検索結果案件一覧フラグメントViewオブジェクト
	 */
	@Data
	public static class HeaderSearchAnkenListFragmentViewForm {

		/** 案件一覧 */
		private List<AnkenListDto> ankenList = Collections.emptyList();

		/** 案件一覧のページャ情報 */
		private Page<Long> pager;

	}

	/**
	 * ヘッダー検索結果裁判一覧フラグメントView
	 */
	@Data
	public static class HeaderSearchSaibanListFragmentViewForm {

		/** 裁判一覧 */
		private List<SaibanListDto> saibanList = Collections.emptyList();

		/** 裁判一覧のページャ情報 */
		private Page<Long> pager;

	}

}
