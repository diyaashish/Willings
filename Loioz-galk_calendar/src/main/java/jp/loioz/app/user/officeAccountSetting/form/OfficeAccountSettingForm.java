package jp.loioz.app.user.officeAccountSetting.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.AccountListDto;
import lombok.Data;

/**
 * アカウント設定画面のフォームクラス
 */
@Data
public class OfficeAccountSettingForm {

	/** 利用可能ライセンス数 */
	private Long useableLicenseCount;

	/** 利用中ライセンス数 */
	private Long usingLicenseCount;

	/** アカウント情報リスト */
	private List<AccountListDto> accountList;

	/** ページング */
	private Page<AccountListDto> page;

	/** 検索結果件数 */
	private Long count;
}