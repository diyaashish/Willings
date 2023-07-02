package jp.loioz.app.user.tenantRegist.form;

import javax.validation.Valid;

import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * アカウント情報詳細入力画面(オーナー登録)のフォームクラス
 */
@Data
public class TenantRegistForm {

	// hidden
	/** 認証キー */
	@Required
	String key;

	// ドメイン情報 (画面１)
	/** ドメイン情報 */
	@Valid
	SubDomainSettingForm domain = new SubDomainSettingForm();

	// テナント情報 (画面２)
	/** テナント情報 */
	@Valid
	TenantSettingForm tenant = new TenantSettingForm();

	// ユーザー情報 (画面3)
	/** ユーザー情報 */
	@Valid
	UserForm user = new UserForm();

}
