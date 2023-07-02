package jp.loioz.app.user.azukarikinManagement.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.loioz.app.common.form.PagerForm;
import jp.loioz.app.user.azukarikinManagement.Enums.CustomerTotalSearchType;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.PageSize;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.domain.condition.AzukarikinSearchCondition;
import jp.loioz.domain.condition.AzukarikinSearchCondition.AnkenStatusSeachCondition;
import jp.loioz.domain.condition.AzukarikinSearchCondition.TantoSearchCondition;
import lombok.Data;

/**
 * 預り金・未収入金画面の画面表示フォームクラス
 */
@Data
public class AzukarikinSearchForm implements PagerForm {

	/** 顧客計種別 */
	private String totalSearchType;

	/** 0円を非表示 */
	private boolean excludeZero;

	/** 最終入金日From */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String lastNyukinDateFrom;

	/** 最終入金日To */
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String lastNyukinDateTo;

	/** 売上計上先アカウント */
	private List<Long> salesOwnerSeq = Collections.emptyList();

	/** 担当弁護士アカウント */
	private List<Long> tantoLawyerSeq = Collections.emptyList();

	/** 担当事務員アカウント */
	private List<Long> tantoJimuSeq = Collections.emptyList();

	/** 案件ステータス */
	private List<String> ankenStatus = Collections.emptyList();

	/** ページ番号 */
	private Integer page = DEFAULT_PAGE;

	/** 表示件数 */
	private PageSize pageSize = DEFAULT_SIZE;

	/** 詳細検索の開閉(初期遷移時はOFF) */
	private String detailSearchFlg = CommonConstant.SystemFlg.FLG_OFF.getCd();

	/**
	 * 検索フォームの初期値設定
	 */
	public void initForm() {
		this.totalSearchType = CustomerTotalSearchType.ALL.getCd();
		this.excludeZero = false;
		this.lastNyukinDateFrom = null;
		this.lastNyukinDateTo = null;
		this.salesOwnerSeq = Collections.emptyList();
		this.tantoLawyerSeq = Collections.emptyList();
		this.tantoJimuSeq = Collections.emptyList();
		this.ankenStatus = Collections.emptyList();
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
	 * 売上計上先アカウント連番を設定する<br>
	 * nullの要素は無視される
	 * 
	 * @param salesOwnerSeq 売上計上先アカウント連番
	 */
	public void setSalesOwnerSeq(List<Long> salesOwnerSeq) {
		this.salesOwnerSeq = LoiozCollectionUtils.select(salesOwnerSeq, Objects::nonNull, new ArrayList<>());
	}

	/**
	 * 担当弁護士アカウント連番を設定する<br>
	 * nullの要素は無視される
	 *
	 * @param tantoLawyerSeq 担当弁護士アカウント連番
	 */
	public void setTantoLawyerSeq(List<Long> tantoLawyerSeq) {
		this.tantoLawyerSeq = LoiozCollectionUtils.select(tantoLawyerSeq, Objects::nonNull, new ArrayList<>());
	}

	/**
	 * 担当事務員アカウント連番を設定する<br>
	 * nullの要素は無視される
	 *
	 * @param tantoJimuSeq 担当事務員アカウント連番
	 */
	public void setTantoJimuSeq(List<Long> tantoJimuSeq) {
		this.tantoJimuSeq = LoiozCollectionUtils.select(tantoJimuSeq, Objects::nonNull, new ArrayList<>());
	}

	/**
	 * チェックされた、案件ステータスを設定する
	 * 
	 * @param ankenStatus
	 */
	public void setAnkenStatus(List<String> ankenStatus) {
		this.ankenStatus = LoiozCollectionUtils.select(ankenStatus, Objects::nonNull, new ArrayList<>());
	}

	/** 最終入金日From LocalDate変換 */
	public LocalDate getParsedLastNyukinDateFrom() {
		try {
			return DateUtils.parseToLocalDate(this.lastNyukinDateFrom, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		} catch (Exception e) {
			return null;
		}
	}

	/** 最終入金日To LocalDate変換 */
	public LocalDate getParsedLastNyukinDateTo() {
		try {
			return DateUtils.parseToLocalDate(this.lastNyukinDateTo, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 預り金・未収金一覧検索条件に変換する
	 * 
	 * @return 預り金・未収金検索条件
	 */
	public AzukarikinSearchCondition toAzukarikinSearchCondition() {
		List<String> ankenStatusList = new ArrayList<>(ankenStatus);
		if (ankenStatusList.stream().map(String::valueOf).anyMatch(AnkenStatus.SODAN::equalsByCode)) {
			ankenStatusList.add(AnkenStatus.MENDAN_YOTEI.getCd());
		}

		return AzukarikinSearchCondition.builder()
				.totalSearchType(CustomerTotalSearchType.of(this.totalSearchType))
				.excludeZero(this.excludeZero)
				.lastNyukinDateFrom(getParsedLastNyukinDateFrom())
				.lastNyukinDateTo(getParsedLastNyukinDateTo())
				.tantosha(TantoSearchCondition.builder()
						.salesOwnerSeqList(this.salesOwnerSeq)
						.tantoLawyerSeqList(this.tantoLawyerSeq)
						.tantoJimuSeqList(this.tantoJimuSeq)
						.build())
				.status(AnkenStatusSeachCondition.builder()
						.ankenStatus(ankenStatusList)
						.build())
				.build();
	}

}