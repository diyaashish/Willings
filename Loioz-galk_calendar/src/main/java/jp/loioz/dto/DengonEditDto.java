package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 伝言編集用のDtoクラス
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class DengonEditDto {

	/* 伝言SEQ */
	private Long dengonSeq;

	/*
	 * 受信者SEQリスト
	 * DB上ではアカウントSEQがカンマ区切りで保存されている
	 */
	private String receiveAccountSeqList;

	/* 送信者SEQ */
	private Long sendAccountSeq;

	/* 業務履歴SEQ */
	private Long gyomuhistorySeq;

	/* 遷移先顧客名 */
	private CustomerId customerId;

	/* 遷移先顧客名 */
	private String customerName;

	/* 遷移先案件ID */
	private AnkenId ankenId;

	/* 遷移先案件名 */
	private String ankenName;

	/* 遷移先案件分野ID */
	private Long bunyaId;

	/* 遷移先裁判ID */
	private Long saibanSeq;

	/* 裁判枝番 */
	private Long saibanBranchNo;

	/* 遷移先裁判ID */
	private String saibanName;

	/* 件名 */
	@MaxDigit(max = 50)
	private String title;

	/* 本文 */
	@MaxDigit(max = 10000)
	private String body;

	/* 下書きフラグ */
	private String draftFlg;
}