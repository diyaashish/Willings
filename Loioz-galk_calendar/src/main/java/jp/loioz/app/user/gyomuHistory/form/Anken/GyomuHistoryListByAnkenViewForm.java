package jp.loioz.app.user.gyomuHistory.form.Anken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.common.form.GroupedTableRowItem;
import jp.loioz.app.common.form.TableRowItem;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.GyomuHistoryHeaderDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class GyomuHistoryListByAnkenViewForm {

	/** 画面ヘッダー */

	/** 業務履歴ヘッダー情報 */
	private List<GyomuHistoryHeaderDto> Header = new ArrayList<>();

	/** 業務履歴一覧情報 */
	private List<GyomuHistory> gyomuHistoryList = new ArrayList<>();

	/** 検索結果件数 */
	private Long count;

	/** ページ */
	private Page<Long> page;

	/** 分野 */
	private Long bunya;

	@Data
	@Builder
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class GyomuHistory extends GroupedTableRowItem<Customer> {

		/** 業務履歴SEQ */
		private Long gyomuHistorySeq;

		/** 遷移元種別 */
		private TransitionType transitionType;

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 件名 */
		private String subject;

		/** 本文 */
		private String mainText;

		/** 重要フラグ */
		private boolean isImportant;

		/** 固定フラグ */
		private boolean isKotei;

		/** 伝言送信済フラグ */
		private boolean isSentDengon;

		/** 登録者名 */
		private String createrName;

		/** 対応日時 */
		private LocalDateTime supportedAt;

		// 案件軸のため、1件を想定
		/** 案件ID */
		private AnkenId ankenId;

		/** 案件名 */
		private AnkenName ankenName;

		/** 分野 */
		private BunyaDto bunya;

		/** 表示名 「分野-案件名」 */
		private String labelName;

		// 裁判情報
		/** 裁判SEQ枝番 */
		private String saibanBranchNo;

		/** 事件名 */
		private String jikenName;

		/** 事件No */
		private CaseNumber caseNumber;

	}

	@Data
	@Builder
	public static class Customer implements TableRowItem {

		/** 顧客ID */
		private CustomerId customerId;

		/** 顧客名 */
		private String customerName;
	}

}
