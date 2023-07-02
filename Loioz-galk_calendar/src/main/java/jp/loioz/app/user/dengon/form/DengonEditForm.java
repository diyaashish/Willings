package jp.loioz.app.user.dengon.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import jp.loioz.dto.DengonCustomerDto;
import jp.loioz.dto.DengonEditDto;
import lombok.Data;

/**
 * 伝言編集画面のフォームクラス
 */
@Data
public class DengonEditForm {

	/** 伝言編集用のDto */
	@Valid
	private DengonEditDto dto = new DengonEditDto();

	/** 選択された部署ID */
	@Valid
	private Long bushoId;

	/** 送信者アカウント情報 */
	private Account sentAccountInfo = new Account();

	/** 宛先アカウント一覧 */
	private List<Account> receiveAccountList = new ArrayList<>();

	/** 宛先に選択されたアカウントのSEQリスト */
	private List<Long> accountSeqList = new ArrayList<Long>();

	/** 紐づけられた顧客情報 */
	private List<DengonCustomerDto> selectedCustomerList = new ArrayList<>();

	/** 紐付ける顧客ID */
	private List<Long> customerIdList = new ArrayList<Long>();

	/** 関連する顧客情報リスト */
	private List<DengonAnkenCustomerForm> relatedCustomerList;

	/** 関連する案件情報リスト */
	private List<DengonAnkenCustomerForm> relatedAnkenList;

	/** メール送信フラグ */
	private boolean mailFlg = false;

	/** 業務履歴送信済フラグ */
	private boolean sentFromGyoumuHistoryFlg = false;

	/** 下書きボタン非表示フラグ */
	private boolean UnDisplayDraftFlg = false;

	/**
	 * 伝言編集アカウント
	 */
	@Data
	public static class Account {

		/** アカウントSEQ */
		private Long accountSeq;

		/** アカウント種別 */
		private String accountType;

		/** アカウント名 */
		private String accountName;

		/** アカウント名(かな) */
		private String accountNameKana;

		/** 所属部署 */
		private List<String> bushoShozoku = Collections.emptyList();

	}

}