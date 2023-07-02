package jp.loioz.app.user.officeAccountSetting.form;

import javax.validation.Valid;

import jp.loioz.dto.AccountEditDto;
import lombok.Data;

/**
 * アカウント編集画面のフォームクラス
 */
@Data
public class OfficeAccountEditForm {

	/** アカウント管理Dto */
	@Valid
	private AccountEditDto accountEditData = new AccountEditDto();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (accountEditData.getAccountSeq() == null) {
			newFlg = true;
		}
		return newFlg;
	}

}