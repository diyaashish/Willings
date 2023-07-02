package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.AccountKengen;
import jp.loioz.common.constant.CommonConstant.AccountStatus;
import jp.loioz.common.constant.CommonConstant.AccountType;
import lombok.Data;

@Data
public class AccountListDto {

	/** アカウント連番 */
	private Long accountSeq;

	/** アカウントID */
	private String accountId;

	/** アカウント名 */
	private String accountName;

	/** アカウント名（カナ） */
	private String accountNameKana;

	/** アカウント種別 */
	private AccountType accountType;

	/** アカウントステータス */
	private AccountStatus accountStatus;

	/** メールアドレス */
	private String accountMailAddress;

	/** アカウント権限 */
	private AccountKengen accountKengen;

	/** オーナーフラグ */
	private boolean accountOwnerFlg;

	/** アカウントカラー */
	private String accountColor;

	/** アカウント利用停止日時-年月 */
	private String accountLimitDateYearMonth;

	/** 作成日時 */
	private String createdAt;

	/** 更新日時 */
	private String updatedAt;

	/** バージョンNo */
	private Long versionNo;
}