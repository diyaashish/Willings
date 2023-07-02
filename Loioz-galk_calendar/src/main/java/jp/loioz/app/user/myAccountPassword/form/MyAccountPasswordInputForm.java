package jp.loioz.app.user.myAccountPassword.form;

import javax.validation.Valid;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.DigitRange;
import jp.loioz.common.validation.annotation.Password;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.dto.PasswordDto;
import lombok.Data;

/**
 * パスワード変更 画面のフォームクラス
 */
@Data
public class MyAccountPasswordInputForm {

	/**
	 * アカウントパスワード入力フォームオブジェクト
	 */
	@Data
	public static class AccountPassWordInputForm {

		// 表示用プロパティ
		// 現状なし

		// 入力用プロパティ

		/** 現在のパスワード */
		@Required
		@DigitRange(min = 8, max = 72)
		@Password
		private String oldPassword;

		/** 新しいパスワード */
		@Valid
		private PasswordDto password = new PasswordDto();

		/**
		 * 入力がされたか判定します
		 * 
		 * @return true:ない、false:ある
		 */
		public boolean isAllEmpty() {

			return StringUtils.isAllEmpty(
					this.oldPassword, // 現在のパスワード
					this.password.getPassword(), // 新しいパスワード
					this.password.getConfirm() // 新しいパスワード(確認用)
			);
		}

		/**
		 * すべて入力されている -> パスワードを変更する
		 * 
		 * @return true:変更、false:変更しない
		 */
		public boolean changePassWord() {

			return !StringUtils.isAnyEmpty(
					this.oldPassword, // 現在のパスワード
					this.password.getPassword(), // 新しいパスワード
					this.password.getConfirm() // 新しいパスワード(確認用)
			);
		}
	}

	/** パスワード変更フラグ */
	private String passWordEditFlg;

	/** 現在のパスワード */
	@Required
	@DigitRange(min = 8, max = 72)
	@Password
	private String oldPassword;

	/** 新しいパスワード */
	@Valid
	private PasswordDto password = new PasswordDto();

	/**
	 * 入力がされたか判定します
	 * 
	 * @return true:ない、false:ある
	 */
	public boolean isAllEmpty() {

		return StringUtils.isAllEmpty(
				this.oldPassword, // 現在のパスワード
				this.password.getPassword(), // 新しいパスワード
				this.password.getConfirm() // 新しいパスワード(確認用)
		);
	}

	/**
	 * すべて入力されている -> パスワードを変更する
	 * 
	 * @return true:変更、false:変更しない
	 */
	public boolean changePassWord() {

		return !StringUtils.isAnyEmpty(
				this.oldPassword, // 現在のパスワード
				this.password.getPassword(), // 新しいパスワード
				this.password.getConfirm() // 新しいパスワード(確認用)
		);
	}

}
