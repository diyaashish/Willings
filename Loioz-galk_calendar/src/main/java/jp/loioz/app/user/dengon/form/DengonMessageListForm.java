package jp.loioz.app.user.dengon.form;

import java.util.List;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.SaibanId;
import jp.loioz.dto.DengonCustomerDto;
import lombok.Data;

/**
 * 伝言編集画面のフォームクラス
 */
@Data
public class DengonMessageListForm {

	/** 伝言SEQ */
	private Long dengonSeq;

	/** 業務履歴SEQ */
	private Long gyomuHistorySeq;

	/** 業務履歴 関連する顧客情報リスト */
	private List<DengonAnkenCustomerForm> relatedCustomerList;

	/** 業務履歴 関連する案件情報リスト */
	private List<DengonAnkenCustomerForm> relatedAnkenList;

	/** 業務履歴削除フラグ */
	private boolean gyomuHistoryDeleted = false;

	/** 業務履歴 裁判SEQ */
	private Long saibanSeq;

	/** 業務履歴 裁判枝番 */
	private Long saibanBranchNo;

	/** 業務履歴登録元 */
	private String transitionType;

	/** タイトル */
	private String title;

	/** 本文 */
	private String body;

	/** 送信者名 */
	private String sendAccountName;

	/*
	 * 受信者SEQリスト
	 * DB上ではアカウントSEQがカンマ区切りで保存されている
	 */
	private String receiveAccountSeqList;

	/** 受信者名リスト */
	private String receiveAccountNameList;

	/** 紐付け顧客 */
	private List<DengonCustomerDto> selectedCustomerList;

	/** 伝言ステータスID */
	private String dengonStatusId;

	/** 下書きフラグ */
	private Boolean draftFlg;

	/** 既読フラグ */
	private Boolean openFlg;

	/** 重要フラグ */
	private Boolean importantFlg;

	/** 遷移先顧客名 */
	private String customerName;

	/** 遷移先案件名 */
	private String ankenName;

	/** 遷移先裁判IDが紐づく案件ID */
	private AnkenId ankenId;

	/** 遷移先裁判ID */
	private SaibanId fwSaibanId;

	/** 遷移先裁判名 */
	private String saibanName;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** 複数送信先有無フラグ */
	private Boolean manyReceiveFlg = false;

	/** メール送信済フラグ */
	private Boolean sendMailFlg = false;

	/** ごみ箱フラグ */
	private Boolean trashedFlg = false;

	/** 送信者ごみ箱フラグ */
	private Boolean sendTrashedFlg = false;
}