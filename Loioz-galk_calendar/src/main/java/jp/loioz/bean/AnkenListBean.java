package jp.loioz.bean;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 案件一覧情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenListBean {

	/** レコード件数 */
	private Integer count;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private Long bunyaId;

	/** 案件登録日 */
	private LocalDate ankenCreatedDate;

	/** 担当弁護士 */
	private String tantoLaywerName;

	/** 担当事務 */
	private String tantoJimuName;

	/** 担当案件フラグ 1:担当 */
	private String tantoAnkenFlg;

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

	/** 案件ステータス */
	private String ankenStatus;

	/** 顧客登録日 */
	private LocalDate customerCreatedDate;

	/** 受任日 */
	private LocalDate juninDate;

	/** 相手方名 */
	private String aitegataName;

	/** 相手方数 */
	private Long numberOfAitegata;
}