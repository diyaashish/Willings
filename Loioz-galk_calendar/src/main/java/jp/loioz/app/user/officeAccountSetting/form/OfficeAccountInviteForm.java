package jp.loioz.app.user.officeAccountSetting.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.AccountMailDto;
import lombok.Data;

/**
 * 招待メール画面のフォームクラス
 */
@Data
public class OfficeAccountInviteForm {
	
	/** 利用可能ライセンス数 */
	private Long useableLicenseCount;

	/** 利用中ライセンス数 */
	private Long usingLicenseCount;
	
	/** 送信ボタン可能フラグ */
	private boolean mailSendButtonFlg;
	
	/** 削除対象キー */
	private String verificationkey;
	
	/** 招待メール設定情報 */
	@Valid
	private List<AccountMailDto> mailAddressList = new ArrayList<>();

	
	/**
	 * 空のデータを除外したmailAddressListを取得する<br>
	 * ※招待中（画面ではラベル表示としている）のメールアドレスも返却するリストに含める
	 * 
	 * @return
	 */
	public List<AccountMailDto> getMailAddressListExcludeEmpty() {
		return mailAddressList.stream()
			.filter(e -> StringUtils.isNotEmpty(e.getMailAddress()))
			.collect(Collectors.toList());
	}

	/**
	 * 入力可能状態のメールアドレスについて、空のデータを除外したmailAddressListを取得する<br>
	 * ※招待中（画面ではラベル表示としている）のメールアドレスは返却するリストには含めない
	 * 
	 * @return
	 */
	public List<AccountMailDto> getInsertMailAddressListExcludeEmpty() {
		return mailAddressList.stream()
			.filter(e -> e.isInsertFlg())
			.filter(e -> StringUtils.isNotEmpty(e.getMailAddress()))
			.collect(Collectors.toList());
	}
}