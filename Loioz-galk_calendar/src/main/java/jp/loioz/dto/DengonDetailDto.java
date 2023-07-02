package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 伝言詳細表示用のDtoクラス
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class DengonDetailDto {

	/** 送信メールフラグ */
	private Boolean sendMailFlg;

	/** 送信者複数人フラグ */
	private Boolean manyReceiveFlg;

	/** 伝言SEQ */
	private Long dengonSeq;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判枝番 */
	private Long saibanBranchNo;

	/** 裁判事件名 */
	private String jikenName;

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

	/** 伝言ステータスID */
	private String dengonStatusId;

	/** 下書きフラグ */
	private Boolean draftFlg;

	/** 重要フラグ */
	private Boolean importantFlg;

	/** 既読フラグ */
	private Boolean openFlg;

	/** ごみ箱フラグ */
	private Boolean trashedFlg;

	/** 送信者ごみ箱フラグ */
	private Boolean sendTrashedFlg;

	/** 送信日時 */
	private String createdAt;
}