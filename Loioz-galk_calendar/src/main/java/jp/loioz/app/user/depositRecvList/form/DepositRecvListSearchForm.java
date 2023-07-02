package jp.loioz.app.user.depositRecvList.form;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.constant.SortConstant.DepositRecvListSortItem;
import jp.loioz.common.constant.SortConstant.SortOrder;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.condition.DepositRecvListSearchCondition;
import jp.loioz.domain.condition.DepositRecvListSortCondition;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

/**
 * 預り金管理一覧：検索条件フォーム
 */
@Data
public class DepositRecvListSearchForm implements PagerForm {

	/** 検索条件エリアの開閉フラグ */
	private boolean isSearchConditionOpen;

	/** 検索ワード */
	private String searchWord;

	/** 名簿ID */
	private String personId;

	/** 名前 */
	private String personName;

	/** 分野 */
	private String bunyaId;

	/** 分野の一覧情報 */
	private List<BunyaDto> bunyaList;

	/** 案件名 */
	private String ankenName;

	/** 売上計上先 */
	private Long salesOwner;

	/** 担当弁護士 */
	private Long tantoLaywer;

	/** 担当事務 */
	private Long tantoJimu;

	/** 検索条件売上計上先のプルダウン情報 */
	private List<SelectOptionForm> salesOwnerList;

	/** 検索条件担当弁護士のプルダウン情報 */
	private List<SelectOptionForm> tantoLawyerList;

	/** 検索条件担当事務のプルダウン情報 */
	private List<SelectOptionForm> tantoJimuList;

	/** 顧客ステータス */
	private String ankenStatus;

	/** 預り金残高合計 */
	private String totalDepositBalance;

	// -----------------------------------------------
	// ページャー
	// -----------------------------------------------
	/** ページ番号（これから表示するページ） */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	// -----------------------------------------------
	// 一覧ソート情報
	// -----------------------------------------------
	/** 一覧のソートキー */
	private DepositRecvListSortItem depositRecvListSortItem;

	/** 一覧のソート順 */
	private SortOrder depositRecvListSortOrder;

	/**
	 * フォームの初期化
	 */
	public void initForm() {

		// フォームの各値をクリア
		this.clearForm();

		// 検索条件：顧客ステータスに「終了以外」をセットする
		this.ankenStatus = CommonConstant.ANKEN_STATUS_INCOMPLETE_CD;

		// 検索条件：預り金残高合計に「すべて」をセットする
		this.totalDepositBalance = CommonConstant.TotalDepositBalance.All.getCd();
	}

	/**
	 * フォームの各値をクリアします。
	 */
	public void clearForm() {

		// 検索条件エリアは閉じた状態
		this.isSearchConditionOpen = false;

		// 売上計上先プルダウン情報
		this.salesOwnerList = new ArrayList<SelectOptionForm>();

		// 担当弁護士プルダウン情報
		this.tantoLawyerList = new ArrayList<SelectOptionForm>();

		// 担当事務プルダウン情報
		this.tantoJimuList = new ArrayList<SelectOptionForm>();

		// 初期表示の一覧ソート順
		this.setDepositRecvListSortItem(DepositRecvListSortItem.LAST_EDIT_AT);
		this.setDepositRecvListSortOrder(SortOrder.DESC);

		// ページ番号を初期化
		this.defaultPage();

		// 各検索条件初期化
		this.searchWord = "";
		this.personId = "";
		this.personName = "";
		this.bunyaId = "";
		this.ankenName = "";
		this.salesOwner = null;
		this.tantoLaywer = null;
		this.tantoJimu = null;
		this.ankenStatus = "";
		this.totalDepositBalance = "";
	}

	/**
	 * ページ番号を初期化
	 */
	public void defaultPage() {
		setDefaultPage();
	}

	/** 検索条件オブジェクトを作成 */
	public DepositRecvListSearchCondition toDepositRecvListSearchCondition() {

		// 検索条件：顧客ステータス
		List<String> ankenStatusList = null;

		if (CommonConstant.ANKEN_STATUS_INCOMPLETE_CD.equals(this.ankenStatus)) {
			// ステータスが「完了、不受任以外」の場合、ステータスをセットしなおす
			ankenStatusList = List.of(
					AnkenStatus.SODAN.getCd(),
					AnkenStatus.SHINKOCHU.getCd(),
					AnkenStatus.SEISAN_MACHI.getCd(),
					AnkenStatus.KANRYO_MACHI.getCd());
		} else if (CommonConstant.ANKEN_STATUS_ALL_CD.equals(this.ankenStatus)) {
			// ステータスが「すべて」の場合、ステータスをセットしなおす
			ankenStatusList = List.of(
					AnkenStatus.SODAN.getCd(),
					AnkenStatus.SHINKOCHU.getCd(),
					AnkenStatus.SEISAN_MACHI.getCd(),
					AnkenStatus.KANRYO_MACHI.getCd(),
					AnkenStatus.KANRYO.getCd(),
					AnkenStatus.FUJUNIN.getCd());
		} else {
			ankenStatusList = List.of(this.ankenStatus);
		}

		return DepositRecvListSearchCondition.builder()
				.searchWord(StringUtils.removeSpaceCharacter(this.searchWord))
				.personId(this.personId)
				.personName(StringUtils.removeSpaceCharacter(this.personName))
				.bunyaId(this.bunyaId)
				.ankenName(StringUtils.removeSpaceCharacter(this.ankenName))
				.salesOwner(this.salesOwner)
				.tantoLaywer(this.tantoLaywer)
				.tantoJimu(this.tantoJimu)
				.ankenStatusList(ankenStatusList)
				.totalDepositBalance(this.totalDepositBalance)
				.build();
	}

	/** ソート条件オブジェクトを作成 */
	public DepositRecvListSortCondition toDepositRecvListSortCondition() {
		return DepositRecvListSortCondition.builder()
				.sortItem(this.depositRecvListSortItem)
				.sortOrder(this.depositRecvListSortOrder)
				.build();
	}

	/** ページ番号をデフォルトに設定 */
	public void setDefaultPage() {
		this.page = DEFAULT_PAGE;
	}

	/**
	 * 使用している検索条件項目数を取得する
	 * 
	 * @return
	 */
	public int getSearchConditionCount() {
		int count = 0;

		// 名簿IDがあればカウントアップ
		if (!StringUtils.isEmpty(personId)) {
			count++;
		}

		// 名前があればカウントアップ
		if (!StringUtils.isEmpty(personName)) {
			count++;
		}

		// 分野があればカウントアップ
		if (!StringUtils.isEmpty(bunyaId)) {
			count++;
		}

		// 案件名があればカウントアップ
		if (!StringUtils.isEmpty(ankenName)) {
			count++;
		}

		// 売上計上先があればカウントアップ
		if (salesOwner != null) {
			count++;
		}

		// 担当弁護士があればカウントアップ
		if (tantoLaywer != null) {
			count++;
		}

		// 担当事務があればカウントアップ
		if (tantoJimu != null) {
			count++;
		}

		// 顧客ステータスが「すべて」以外はカウントアップ
		if (!CommonConstant.ANKEN_STATUS_ALL_CD.equals(ankenStatus)) {
			count++;
		}

		// 預り金残高合計が「すべて」以外はカウントアップ
		if (!CommonConstant.TotalDepositBalance.All.equalsByCode(totalDepositBalance)) {
			count++;
		}

		return count;
	}

}
