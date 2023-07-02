package jp.loioz.app.user.saibanManagement.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.saibanManagement.constant.SaibanManagementConstant;
import jp.loioz.app.user.saibanManagement.dto.SaibanCustomerDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKanyoshaViewDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKijitsuDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanTantoDto;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.HeigoHansoType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.SaibanId;
import jp.loioz.dto.SaibanTreeJikenDto;
import jp.loioz.dto.SaibankanDto;
import lombok.Builder;
import lombok.Data;

/**
 * 裁判管理（民事）画面の画面表示フォームクラス
 */
@Data
public class SaibanEditMinjiViewForm {

	/** 案件ID */
	private AnkenId ankenId;

	/** 編集対象の裁判 */
	private Long selectedSaibanSeq;

	/** 裁判 基本情報表示フォーム */
	private SaibanBasicViewForm saibanBasicViewForm;

	/** 裁判 オプション情報 表示フォーム */
	private SaibanOptionViewForm saibanOptionViewForm;

	/** 裁判 タブ情報 表示フォーム */
	private SaibanTabViewForm saibanTabViewForm;

	/** 裁判 期日情報 表示フォーム */
	private SaibanKijitsuViewForm saibanKijitsuViewForm;

	/** 裁判 顧客情報 表示フォーム */
	private SaibanCustomerViewForm saibanCustomerViewForm;

	/** 裁判 その他当事者情報 表示フォーム */
	private SaibanOtherViewForm saibanOtherViewForm;

	/** 裁判 相手方情報 表示フォーム */
	private SaibanAitegataViewForm saibanAitegataViewForm;

	/**
	 * 裁判の基本情報表示フォーム
	 */
	@Data
	public static class SaibanBasicViewForm {

		/****************************
		 * 案件情報
		 ****************************/

		/** 案件ID */
		private AnkenId ankenId;

		/** 案件区分 */
		private AnkenType ankenType;

		/** 案件-分野名 */
		private String bunyaName;

		/****************************
		 * 裁判情報
		 ****************************/

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 裁判所名 */
		private String saibanshoName;

		/** 事件番号 */
		private CaseNumber caseNumber;

		/** 本訴フラグ */
		private String honsoFlg;

		/** 基本事件フラグ */
		private String kihonFlg;

		/** 事件名 */
		private String jikenName;

		/** 裁判ステータス */
		private SaibanStatus saibanStatus;

		/** 併合・反訴種別 */
		private String connectType;

		/** 裁判所ID */
		private Long saibanshoId;

		/** 係属部 */
		private String keizokuBuName;

		/** 係属係 */
		private String keizokuKakariName;

		/** 係属部電話番号 */
		private String keizokuBuTelNo;

		/** 係属部FAX番号 */
		private String keizokuBuFaxNo;

		/** 裁判官リスト */
		private List<SaibankanDto> saibankanDtoList = new ArrayList<>();

		/** 担当書記官 */
		private String tantoShoki;

		/** 開始日 */
		private LocalDate saibanStartDate;

		/** 終了日 */
		private LocalDate saibanEndDate;

		/** 結果 */
		private String saibanResult;

		/** 事件元号 */
		private EraType jikenGengo;

		/** 事件番号年 */
		private String jikenYear;

		/** 事件番号符号 */
		private String jikenMark;

		/** 事件番号 */
		private String jikenNo;

		/** 担当弁護士 */
		private List<SaibanTantoDto> tantoBengoshiList;

		/** 担当事務 */
		private List<SaibanTantoDto> tantoJimuList;

		/** 裁判追加 */
		private boolean addSaiban;

	}

	/**
	 * 裁判のオプション情報表示フォーム
	 */
	@Data
	public static class SaibanOptionViewForm {

		/** 併合裁判追加判定 */
		private boolean addHeigoSaiban;

		/** 反訴裁判追加判定 */
		private boolean addHansoSaiban;

		/** 被併合・反訴候補 */
		private List<Saiban> childSaibanCandidates = Collections.emptyList();

	}

	/**
	 * タブ 情報表示フォーム
	 */
	@Data
	public static class SaibanTabViewForm {

		/** 裁判タブで表示する最大文字数 */
		private final int INIT_SHOW_MAX_CHAR_COUNT_SAIBAN_TAB_NAME = SaibanManagementConstant.INIT_SHOW_MAX_CHAR_COUNT_SAIBAN_TAB_NAME;

		/** 親裁判SEQ */
		private Long parentSaibanSeq;

		/** 子-裁判 */
		private List<SaibanTreeJikenDto> childSaibanList;

		/** 基本事件フラグ */
		private Boolean kihonFlg;

		/** 本訴フラグ */
		private Boolean honsoFlg;

		/** 選択タブ裁判SEQ */
		private Long targetSaibanSeq;

		/** 分離/取下ボタンフラグ */
		private Boolean bunriTorisageButtonFlg;

	}

	/**
	 * 期日 情報表示フォーム
	 */
	@Data
	public static class SaibanKijitsuViewForm {

		/** 初期表示状態での期日手続きの最大表示文字数 */
		private final int INIT_SHOW_MAX_CHAR_COUNT_KIJITSU_CONTENT = SaibanManagementConstant.INIT_SHOW_MAX_CHAR_COUNT_KIJITSU_CONTENT;

		/** 初期表示状態での期日結果の最大表示文字数 */
		private final int INIT_SHOW_MAX_CHAR_COUNT_KIJITSU_RESULT = SaibanManagementConstant.INIT_SHOW_MAX_CHAR_COUNT_KIJITSU_RESULT;

		/** 全ての期間の期日を表示するかどうかのチェックボックスの値 */
		private boolean isAllTimeKijitsuList;

		/** 子裁判かどうか */
		private boolean isChildSaiban;

		/** 次回期日回数 */
		private Long nextSaibanKijitsuCount;

		/** 裁判期日情報 */
		private List<SaibanKijitsuDto> saibanKijitsuDtoList;

		/** 表示のみ判定フラグ */
		private Boolean displayOnlyFlg;

	}

	/**
	 * 顧客 情報表示フォーム
	 */
	@Data
	public static class SaibanCustomerViewForm {

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 裁判 顧客情報 */
		private List<SaibanCustomerDto> saibanCustomerDtoList;

		/** 表示のみ判定フラグ */
		private Boolean displayOnlyFlg;

	}

	/**
	 * その他当事者 情報表示フォーム
	 */
	@Data
	public static class SaibanOtherViewForm {

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 裁判 共同訴訟人 */
		private List<SaibanKanyoshaViewDto> saibanKanyoshaOtherDtoList = Collections.emptyList();

		/** 表示のみ判定フラグ */
		private boolean displayOnlyFlg;

	}

	/**
	 * 相手方 情報表示フォーム
	 */
	@Data
	public static class SaibanAitegataViewForm {

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 裁判 相手方 */
		private List<SaibanKanyoshaViewDto> saibanKanyoshaAitegataDtoList = Collections.emptyList();

		/** 表示のみ判定フラグ */
		private boolean displayOnlyFlg;

	}

	/**
	 * 裁判
	 */
	@Data
	@Builder
	public static class Saiban {

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 裁判ID */
		private SaibanId saibanId;

		/** 事件番号 */
		private CaseNumber caseNumber;

		/** 事件名 */
		private String jikenName;

		/** ステータス */
		private SaibanStatus status;

		/** 親裁判SEQ */
		private Long parentSeq;

		/** 併合・反訴種別 */
		private String connectType;

		/** 本訴フラグ */
		private boolean isHonso;

		/** 基本事件フラグ */
		private boolean isKihon;

		/** タブ表示名 */
		private String tabName;

		/** 基本事件・本訴 */
		private boolean isMainSaiban;

		/** 子裁判 */
		private List<Saiban> child;

		/** 子裁判が空かどうか */
		public boolean nonChild() {
			return LoiozCollectionUtils.isEmpty(this.child);
		}

		/** 被併合事件かどうか */
		public boolean hiHeigo() {
			return this.connectType != null && HeigoHansoType.HEIGO.equalsByCode(this.connectType);
		}

		/** 反訴かどうか */
		public boolean hanso() {
			return this.connectType != null && HeigoHansoType.HANSO.equalsByCode(this.connectType);
		}

		/** 子裁判かどうか */
		public boolean isChild() {
			return this.parentSeq != null;
		}

	}

}