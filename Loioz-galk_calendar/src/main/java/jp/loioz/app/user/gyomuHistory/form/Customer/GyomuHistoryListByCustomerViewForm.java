package jp.loioz.app.user.gyomuHistory.form.Customer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.common.form.GroupedTableRowItem;
import jp.loioz.app.common.form.TableRowItem;
import jp.loioz.app.common.form.WrapHeaderForm;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.AnkenName;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.GyomuHistoryHeaderDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class GyomuHistoryListByCustomerViewForm {

	/** 画面ヘッダー */
	private WrapHeaderForm wrapHeader;

	/** 業務履歴ヘッダー情報 */
	private List<GyomuHistoryHeaderDto> Header = new ArrayList<>();

	/** 業務履歴一覧情報 */
	private List<GyomuHistory> gyomuHistoryList = new ArrayList<>();

	/** 検索結果件数 */
	private Long count;

	/** ページ */
	private Page<Long> page;

	@Data
	@Builder
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class GyomuHistory extends GroupedTableRowItem<Anken> {

		/** 業務履歴SEQ */
		private Long gyomuHistorySeq;

		/** 遷移元種別 */
		private TransitionType transitionType;

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

		// 登録元顧客情報
		/** 顧客ID */
		private CustomerId customerId;

		/** 顧客名 */
		private String customerName;

	}

	@Data
	@Builder
	public static class Anken implements TableRowItem {

		/** 案件ID */
		private AnkenId ankenId;

		/** 案件名 */
		private AnkenName ankenName;

		/** 分野ID */
		private BunyaDto bunya;

		/** 表示用「分野-案件名」 */
		private String labelName;
	}
}
