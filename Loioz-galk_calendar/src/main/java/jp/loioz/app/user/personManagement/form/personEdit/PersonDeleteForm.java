package jp.loioz.app.user.personManagement.form.personEdit;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import lombok.Data;

/**
 * 名簿削除画面の入力フォームクラス
 */
@Data
public class PersonDeleteForm {

	/** 名簿ID */
	private Long personId;

	/** 個人、企業・団体、弁護士区分 */
	private CustomerType customerType;

	/** 顧客名 姓（会社名） */
	private String customerNameSei;

	/** 顧客名 名 */
	private String customerNameMei;

	/** 顧客フラグ */
	private String customerFlg;
}
