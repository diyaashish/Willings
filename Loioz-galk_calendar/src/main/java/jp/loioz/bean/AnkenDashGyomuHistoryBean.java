package jp.loioz.bean;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件ダッシュボード：業務履歴情報取得用オブジェクト
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenDashGyomuHistoryBean {

	/** 業務履歴SEQ */
	private Long gyomuHistorySeq;

	/** 遷移元種別 */
	private String transitionType;

	/** 件名 */
	private String subject;

	/** 本文 */
	private String mainText;

	/** 重要フラグ */
	private String importantFlg;

	/** 伝言送信済フラグ */
	private String dengonSentFlg;

	/** 固定 */
	private String koteiFlg;

	/** 対応日時 */
	private LocalDateTime supportedAt;

	/** 作成日時 */
	private LocalDateTime createdAt;

	/** 作成者SEQ */
	private Long createdBy;

	/** 更新日時 */
	private LocalDateTime updatedAt;

	/** 案件ID */
	private Long ankenId;

	/** 案件IDに紐づく分野ID */
	private Long bunyaId;

	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 裁判SEQに紐づく枝番 */
	private String saibanBranchNo;
}
