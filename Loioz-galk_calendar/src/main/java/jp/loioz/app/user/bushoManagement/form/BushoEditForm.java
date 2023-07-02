package jp.loioz.app.user.bushoManagement.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.dto.BushoEditDto;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部門編集画面のフォームクラス
 */
@Data
public class BushoEditForm {

	/** 部署情報 */
	@Valid
	private BushoEditDto bushoEditData = new BushoEditDto();

	/** 所属アカウント一覧 */
	private List<Account> shozokuAccountList = new ArrayList<>();

	/** 選択用所属アカウント一覧 */
	private List<Account> selectAccountList = new ArrayList<>();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (bushoEditData.getBushoId() == null) {
			newFlg = true;
		}
		return newFlg;
	}

	/**
	 * アカウント
	 */
	@Data
	@NoArgsConstructor
	public static class Account {

		/** アカウントSEQ */
		private Long accountSeq;

		/** アカウント名 */
		private String accountName;

		/** アカウント種別 */
		private AccountType accountType;
	}

}