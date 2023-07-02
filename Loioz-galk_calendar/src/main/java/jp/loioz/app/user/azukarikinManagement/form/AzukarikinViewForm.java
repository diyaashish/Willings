package jp.loioz.app.user.azukarikinManagement.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.common.form.GroupedTableRowItem;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.form.TableRowItem;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 預り金・未収入金管理画面の画面検索フォームクラス
 */
@Data
public class AzukarikinViewForm {

	/** 売上計上先名 */
	private List<SelectOptionForm> salesOwnerOptionList;

	/** 担当弁護士名 */
	private List<SelectOptionForm> tantoLawyerOptionList;

	/** 担当事務名 */
	private List<SelectOptionForm> tantoJimuOptionList;

	/** 預り金・未収金一覧情報 */
	private List<Customer> rowData = new ArrayList<Customer>();

	/** 顧客検索結果件数 */
	private long customerCount;

	/** 案件検索結果件数 */
	private long ankenCount;

	/** ページ */
	private Page<Long> page;

	/** 検索領域を開くかどうか */
	private boolean isOpenSearchArea;

	/**
	 * 預かり金一覧:顧客情報
	 */
	@Data
	@Builder
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class Customer extends GroupedTableRowItem<Anken> {

		/** 顧客ID */
		private CustomerId customerId;

		/** 顧客名 */
		private String customerName;

		/** 顧客計 */
		private BigDecimal totalCustomerKaikei;

		/** 顧客計（表示用） */
		private String dispTotalCustomerKaikei;

		/** 顧客計がマイナスかどうか。 */
		public boolean isTotalCustomerMinus() {
			return this.totalCustomerKaikei.compareTo(BigDecimal.ZERO) < 0;
		}
	}

	/**
	 * 預り金一覧:案件情報
	 */
	@Data
	@Builder
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class Anken extends GroupedTableRowItem<Kaikei> {

		/** 案件ID */
		private AnkenId ankenId;

		/** 案件名 */
		private String ankenName;

		/** 分野 */
		private BunyaDto bunya;

		/** 案件ステータス */
		private AnkenStatus ankenStatus;

		/** 売上計上先 */
		private String salesOwnerName;

		/** 担当弁護士 */
		private String tantoLawyerName;

		/** 担当事務 */
		private String tantoZimuName;

		/** 報酬計 */
		private BigDecimal hoshu;

		/** 出金計 */
		private BigDecimal totalShukkin;

		/** 入金計 */
		private BigDecimal totalNyukin;

		/** 案件計 */
		private BigDecimal totalAnkenKaikei;

		/** 報酬（表示用） */
		private String dispHoshu;

		/** 出金（表示用） */
		private String dispTotalShukkin;

		/** 入金（表示用） */
		private String dispTotalNyukin;

		/** 案件計（表示用） */
		private String dispTotalAnkenKaikei;

		/** 受任日 */
		private LocalDate juninDate;

		/** 最終入金日 */
		private LocalDate lastNyukinDate;

		/** 顧客計がマイナスかどうか */
		public boolean isTotalAnkenMinus() {
			return this.totalAnkenKaikei.compareTo(BigDecimal.ZERO) < 0;
		}

	}

	/**
	 * 預り金一覧:会計情報
	 */
	@Data
	@Builder
	public static class Kaikei implements TableRowItem {

		/** 会計記録SEQ */
		private Long kaikeiKirokuSeq;

		/** 報酬項目ID */
		private Long hoshuKomokuId;

		/** 入出金種別 */
		// 1:入金 2:出金
		private String nyushukkinType;

		/** 消費税率種別 */
		private String taxRate;

		/** 消費税フラグ(非課税、内税、外税) */
		private String taxFlg;

		/** 源泉徴収フラグ */
		private String gensenchoshuFlg;

		/** 源泉徴収 */
		private BigDecimal gensen;

		/** 報酬 */
		private BigDecimal hoshu;

		/** 出金 */
		private BigDecimal shukkin;

		/** 入金 */
		private BigDecimal nyukin;

		/** 消費税金額 */
		private BigDecimal tax;
	}
}