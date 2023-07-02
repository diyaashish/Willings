package jp.loioz.bean;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 *  裁判一覧情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SaibanListBean {

	/** レコード件数 */
	private Integer count;

	/** 裁判SEQ */
	private Long saibanSeq;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private Long bunyaId;

	/** 裁判所 */
	private String saibanshoNameMei;

	/** 事件SEQ */
	private String jikenSeq;

	/** 事件元号 */
	private String jikenGengo;

	/** 事件年 */
	private String jikenYear;

	/** 事件マーク */
	private String jikenMark;

	/** 事件番号 */
	private String jikenNo;

	/** 事件名 */
	private String jikenName;

	/** 名簿ID */
	private Long personId;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客姓 */
	private String customerNameSei;

	/** 顧客姓（かな） */
	private String customerNameSeiKana;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客名（かな） */
	private String customerNameMeiKana;

	/** 顧客数 */
	private Long numberOfCustomer;

	/** 相手方名 */
	private String aitegataName;

	/** 相手方数 */
	private Long numberOfAitegata;

	/** 裁判ステータス */
	private String saibanStatus;

	/** 検察庁 */
	private String kensatsuchoNameMei;

	/** 担当弁護士 */
	private String tantoLaywerName;

	/** 担当事務 */
	private String tantoJimuName;

	/** 申立日／起訴日 */
	private LocalDate saibanStartDate;

	/** 終了日／判決日 */
	private LocalDate saibanEndDate;

	/** 担当裁判フラグ 1:担当 */
	private String tantoSaibanFlg;
}