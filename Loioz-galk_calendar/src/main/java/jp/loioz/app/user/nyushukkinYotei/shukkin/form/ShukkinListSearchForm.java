package jp.loioz.app.user.nyushukkinYotei.shukkin.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchRifineType;
import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchYoteiDateType;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.KozaRelateType;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.domain.condition.ShukkinListSearchCondition;
import jp.loioz.domain.condition.ShukkinListSearchCondition.AnkenTantoSearchCondition;
import lombok.Data;

@Data
public class ShukkinListSearchForm implements PagerForm {

	/** 顧客ID */
	private String customerId;

	/** 顧客名 */
	private String customerName;

	/** 担当弁護士アカウント */
	private List<Long> tantoLawyerSeqList = Collections.emptyList();

	/** 担当事務員アカウント */
	private List<Long> tantoJimuSeqList = Collections.emptyList();;

	/** 出金予定日絞り込み種別 */
	private ShukkinSearchYoteiDateType shukkinSearchYoteiDateType = ShukkinSearchYoteiDateType.ALL;

	/** 絞り込み日付From */
	private int searchDaysFrom = Integer.parseInt(DateUtils.START_OF_MONTH);

	/** 絞り込み日付To */
	private int searchDaysTo = Integer.parseInt(DateUtils.START_OF_MONTH);;

	/** 表示する月を判断 **/
	private LocalDate yearMonth = LocalDate.now();

	/** 口座紐づき種別 */
	private KozaRelateType kozaRelateType;

	/** 出金口座 */
	private Long kozaSeq;

	/** 出金絞り込み種別 */
	private ShukkinSearchRifineType shukkinSearchRifineType;

	/** 詳細検索の開閉(初期遷移時はOFF) */
	private String detailSearchFlg = CommonConstant.SystemFlg.FLG_OFF.getCd();

	/** 選択中の入出金予定SEQを保持する */
	private Long nyushukkinYoteiSeq;

	// ページャー
	/** ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	/**
	 * 担当弁護士アカウント連番を設定する<br>
	 * nullの要素は無視される
	 *
	 * @param tantoLawyerSeq 担当弁護士アカウント連番
	 */
	public void setTantoLawyerSeq(List<Long> tantoLawyerSeqList) {
		this.tantoLawyerSeqList = LoiozCollectionUtils.select(tantoLawyerSeqList, Objects::nonNull, new ArrayList<>());
	}

	/**
	 * 担当事務員アカウント連番を設定する<br>
	 * nullの要素は無視される
	 *
	 * @param tantoJimuSeq 担当事務員アカウント連番
	 */
	public void setTantoJimuSeq(List<Long> tantoJimuSeqList) {
		this.tantoJimuSeqList = LoiozCollectionUtils.select(tantoJimuSeqList, Objects::nonNull, new ArrayList<>());
	}

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.customerId = null;
		this.customerName = null;
		this.tantoLawyerSeqList = Collections.emptyList();
		this.tantoJimuSeqList = Collections.emptyList();
		this.shukkinSearchYoteiDateType = ShukkinSearchYoteiDateType.ALL;
		this.searchDaysFrom = Integer.parseInt(DateUtils.START_OF_MONTH);
		this.searchDaysTo = Integer.parseInt(DateUtils.START_OF_MONTH);
		this.yearMonth = LocalDate.now();
		this.kozaRelateType = null;
		this.kozaSeq = null;
		this.nyushukkinYoteiSeq = null;
		this.shukkinSearchRifineType = ShukkinSearchRifineType.ALL;
		this.page = DEFAULT_PAGE;
		this.pageSize = DEFAULT_SIZE;
	}

	/**
	 * 最初のページ番号に設定
	 */
	public void setDefaultPage() {
		this.page = DEFAULT_PAGE;
	}

	/**
	 * 選択中の予定SEQを破棄します
	 */
	public void discardSelected() {
		this.nyushukkinYoteiSeq = null;
	}

	/**
	 * 月切り替え時、日付指定選択が最終日の場合を切り替え後の月末に自動設定する
	 */
	public void settingLastDaysOfMonth() {

		// DaysFrom
		if (this.searchDaysFrom > this.yearMonth.lengthOfMonth()) {
			this.searchDaysFrom = this.yearMonth.lengthOfMonth();
		}

		// DaysTo
		if (this.searchDaysTo > this.yearMonth.lengthOfMonth()) {
			this.searchDaysTo = this.yearMonth.lengthOfMonth();
		}

	}

	/**
	 * 出金予定一覧検索条件に変換する
	 * 
	 * @return 出金予定一覧検索条件
	 */
	public ShukkinListSearchCondition toShukkinListSearchCondition() {

		// 検索用に加工
		LocalDate searchDateFrom = LocalDate.of(this.yearMonth.getYear(), this.yearMonth.getMonthValue(), this.searchDaysFrom);
		LocalDate searchDateTo = LocalDate.of(this.yearMonth.getYear(), this.yearMonth.getMonthValue(), this.searchDaysTo);

		return ShukkinListSearchCondition.builder()
				.customerId(LoiozNumberUtils.parseAsLong(this.customerId))
				.customerName(this.customerName)
				.shukkinSearchYoteiDateType(this.shukkinSearchYoteiDateType)
				.searchDateFrom(searchDateFrom)
				.searchDateTo(searchDateTo)
				.yearMonth(this.yearMonth)
				.kozaSeq(this.kozaSeq)
				.shukkinSearchRifineType(this.shukkinSearchRifineType)
				.tantosha(AnkenTantoSearchCondition.builder()
						.tantoLawyerSeqList(tantoLawyerSeqList)
						.tantoJimuSeqList(tantoJimuSeqList)
						.build())
				.build();
	}

}
