package jp.loioz.bean;

import java.time.LocalDate;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.domain.value.PersonName;
import lombok.Data;

/**
 * 案件-顧客(名簿)Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class AnkenPersonRelationBean {

	/** 名簿ID */
	private Long personId;

	/** 顧客ID */
	private Long customerId;

	/** 顧客姓 */
	private String customerNameSei;

	/** 顧客せい */
	private String customerNameSeiKana;

	/** 顧客名 */
	private String customerNameMei;

	/** 顧客めい */
	private String customerNameMeiKana;

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 顧客種別 */
	private String customerType;

	/** 顧客登録日 */
	private LocalDate customerCreatedDate;

	/** 案件ID */
	private Long ankenId;

	/** 案件名 */
	private String ankenName;

	/** 案件種別 */
	private String ankenType;

	/** 分野 */
	private Long bunyaId;

	/** 案件ステータス */
	private String ankenStatus;

	/** 名前オブジェクトを返却 */
	public PersonName toPersonName() {
		return new PersonName(this.customerNameSei, this.customerNameMei, this.customerNameSeiKana, this.customerNameMeiKana);
	}

}
