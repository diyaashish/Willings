package jp.loioz.app.common.mvc.kanyosha.form;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;
import jp.loioz.common.validation.groups.Lawyer;
import jp.loioz.domain.value.PersonAttribute;
import lombok.Data;

/**
 * 共通関与者編集モーダル
 */
@Data
public class KanyoshaEditModalInputForm {

	// 表示用プロパティ
	/** 名簿ID */
	private Long personId;

	/** 属性 */
	private PersonAttribute personAttribute;

	/** 郵便番号 */
	private String zipCode;

	/** 住所1 */
	private String address1;

	/** 住所2 */
	private String address2;

	/** 事務所名 */
	private String jimushoName;

	// 入力用プロパティ
	/** 関与者SEQ */
	private Long kanyoshaSeq;

	/** 顧客種別 */
	@Required.List({
			@Required(groups = Kojin.class),
			@Required(groups = Hojin.class),
			@Required(groups = Lawyer.class),
	})
	private CustomerType customerType;

	/** 顧客姓 */
	@Required.List({
			@Required(groups = Kojin.class),
			@Required(groups = Hojin.class),
			@Required(groups = Lawyer.class),
	})
	@MaxDigit.List({
			@MaxDigit(groups = Kojin.class, max = 24),
			@MaxDigit(groups = Hojin.class, max = 64),
			@MaxDigit(groups = Lawyer.class, max = 24),
	})
	private String customerNameSei;

	/** 顧客名 */
	/** 名 */
	@MaxDigit.List({
			@MaxDigit(groups = Kojin.class, max = 24),
			@MaxDigit(groups = Lawyer.class, max = 24),
	})
	private String customerNameMei;

	/** 顧客せい */
	@MaxDigit.List({
			@MaxDigit(groups = Kojin.class, max = 64),
			@MaxDigit(groups = Hojin.class, max = 128),
			@MaxDigit(groups = Lawyer.class, max = 64),
	})
	private String customerNameSeiKana;

	/** 顧客めい */
	@MaxDigit.List({
			@MaxDigit(groups = Kojin.class, max = 64),
			@MaxDigit(groups = Lawyer.class, max = 64),
	})
	private String customerNameMeiKana;

	/** 案件ID (この案件に対する関与者登録) */
	@Required
	private Long ankenId;

	/** 関係(案件に対する) */
	@MaxDigit(max = 30)
	private String kankei;

	/** 備考(案件に対する) */
	@MaxDigit(max = 3000)
	private String remarks;

}
