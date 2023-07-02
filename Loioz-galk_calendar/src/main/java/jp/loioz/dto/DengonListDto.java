package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SaibanId;
import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class DengonListDto {

	/** 伝言SEQ */
	private Long dengonSeq;

	/** 業務履歴 案件ID */
	private AnkenId ankenId;

	/** 業務履歴 顧客ID */
	private CustomerId customerId;

	/** 業務履歴 裁判SEQ */
	private Long saibanSeq;

	/** 業務履歴 裁判枝番 */
	private Long saibanBranchNo;

	/** タイトル */
	private String title;

	/** 送信者名 */
	private String sendAccountName;

	/*
	 * 受信者SEQリスト
	 * DB上ではアカウントSEQがカンマ区切りで保存されている
	 */
	private String receiveAccountSeqList;

	/** 受信者名リスト */
	private String receiveAccountNameList;

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

	/** 遷移先裁判ID */
	private SaibanId fwSaibanId;

	/** 遷移先裁判名 */
	private String saibanName;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;
}