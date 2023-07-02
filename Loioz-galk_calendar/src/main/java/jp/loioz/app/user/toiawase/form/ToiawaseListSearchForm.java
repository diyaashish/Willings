package jp.loioz.app.user.toiawase.form;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.constant.CommonConstant.ToiawaseStatus;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.domain.condition.ToiawaseListSearchCondition;
import lombok.Data;

/**
 * 問い合わせ一覧検索用オブジェクト
 */
@Data
public class ToiawaseListSearchForm implements PagerForm {

	/** 件名・本文の文字列 */
	private String text;

	/** 問い合わせステータス */
	private String toiawaseStatusCd;

	/** ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	/** 検索用オブジェクトの初期化処理 */
	public void initForm() {
		this.text = "";
		this.toiawaseStatusCd = null;
		this.page = DEFAULT_PAGE;
		this.pageSize = DEFAULT_SIZE;
	}

	/** ページングを初期化 */
	public void setDefaultPage() {
		this.page = DEFAULT_PAGE;
		this.pageSize = DEFAULT_SIZE;
	}

	/**
	 * 検索用オブジェクトに変換する
	 * 
	 * @return
	 */
	public ToiawaseListSearchCondition toToiawaseListSearchCondition() {
		return ToiawaseListSearchCondition.builder()
				.text(this.text)
				.toiawaseStatus(DefaultEnum.getCd(ToiawaseStatus.of(this.toiawaseStatusCd)))
				.build();
	}

}
