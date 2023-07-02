package jp.loioz.app.common.form.accg;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccgMenu;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.dto.AccgInvoiceStatementListItemDto;
import lombok.Builder;
import lombok.Data;

/**
 * 報酬明細、預り金明細の「報酬合計、預り金残高」の表示フォーム
 */
@Data
public class AccgCaseForm {

	/** 会計管理メニュー */
	private AccgMenu accgMenu;

	/*****************************
	 * 案件情報
	 *****************************/

	/** 案件ID */
	private Long ankenId;

	/** 分野名 */
	private String bunyaName;

	/** 案件名 */
	private String ankenName;

	/** 案件区分 */
	private AnkenType ankenType;

	/** 案件進捗 */
	private String ankenStatus;

	/** 案件進捗（表示名） */
	private String ankenStatusName;

	/** 案件種別（刑事） */
	private BengoType bengoType;

	/** 刑事分野かどうか */
	private boolean isKeiji;

	/** 案件-登録日 */
	private LocalDate ankenCreatedDate;

	/** 複数顧客かどうか */
	private boolean isCustomers;

	/** 名簿ID */
	private Long personId;

	/** 名前 */
	private String personName;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** 種別 */
	private String customerType;

	/** 案件の相手方情報 */
	private List<Aitegata> relatedAitegata;

	/** 案件の売上帰属先 */
	private List<AnkenTanto> salesOwner = Collections.emptyList();

	/** 案件の担当弁護士 */
	private List<AnkenTanto> tantoLaywer = Collections.emptyList();

	/** 案件の担当事務 */
	private List<AnkenTanto> tantoJimu = Collections.emptyList();

	/** 請求書／精算書 */
	private List<AccgInvoiceStatementListItemDto> invoiceStatementList = Collections.emptyList();

	/** 案件-私選・国選 */
	public String getBengoTypeVal() {
		if (this.bengoType == null) {
			return CommonConstant.BLANK;
		}
		return bengoType.getVal();
	}

	/**
	 * 選択中のメニューが報酬管理かどうか
	 * 
	 * @return
	 */
	public boolean isFeeMenu() {
		if (AccgMenu.FEE.equals(accgMenu)) {
			return true;
		}
		return false;
	}

	/**
	 * 選択中のメニューが預り金管理かどうか
	 * 
	 * @return
	 */
	public boolean isDepositMenu() {
		if (AccgMenu.DEPOSIT_RECV.equals(accgMenu)) {
			return true;
		}
		return false;
	}

	/**
	 * 案件に紐づく顧客情報
	 *
	 */
	@Data
	@Builder
	public static class Customer {
		/** 名簿ID */
		private Long personId;

		/** 名簿名 */
		private String personName;

		/** 案件ステータス */
		private AnkenStatus ankenStatus;

		/** 顧客種別 */
		private CustomerType customerType;

		/** 属性 */
		private PersonAttribute personAttribute;
	}

	/**
	 * 案件に紐づく相手方情報
	 *
	 */
	@Data
	@Builder
	public static class Aitegata {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 関与者名 */
		private String kanyoshaName;
	}

	/**
	 * 案件担当情報
	 */
	@Data
	@Builder
	public static class AnkenTanto {

		/** 案件主担当フラグ */
		private boolean isMain;

		/** アカウントSEQ */
		private Long accountSeq;

		/** アカウント名 */
		private String accountName;

		/** 担当種別枝番 */
		private Long tantoTypeBranchNo;

		/** アカウント色 */
		private String accountColor;

		/** アカウント種別 */
		private String accountTypeStr;

	}
}
