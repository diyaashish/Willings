package jp.loioz.app.user.saibanManagement.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.saibanManagement.constant.SaibanManagementConstant;
import jp.loioz.app.user.saibanManagement.dto.SaibanCustomerDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKanyoshaViewDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKensatsukanDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKijitsuDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanTantoDto;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.dto.SaibanTsuikisoJikenDto;
import jp.loioz.dto.SaibankanDto;
import lombok.Data;

/**
 * 裁判管理（刑事）画面の画面表示フォームクラス
 */
@Data
public class SaibanEditKeijiViewForm {

	/** 案件ID */
	private AnkenId ankenId;

	/** 編集対象の裁判 */
	private Long selectedSaibanSeq;

	/** 新規裁判登録モード */
	private boolean isCreate;

	/** 裁判 基本情報表示フォーム */
	private SaibanBasicViewForm saibanBasicViewForm;

	/** 裁判 期日情報 表示フォーム */
	private SaibanKijitsuViewForm saibanKijitsuViewForm;

	/** 裁判 顧客情報 表示フォーム */
	private SaibanCustomerViewForm saibanCustomerViewForm;

	/** 裁判 共犯者 表示フォーム */
	private SaibanOtherViewForm saibanOtherViewForm;

	/** 裁判 検察官 表示フォーム */
	private SaibanKensatsukanViewForm saibanKensatsukanViewForm;

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

		/** 案件-私選・国選 */
		private String lawyerSelectType;

		/** 案件-私選・国選 */
		public String getBengoTypeVal() {
			BengoType bengoType = BengoType.of(this.lawyerSelectType);
			if (bengoType == null) {
				return "";
			}
			return bengoType.getVal();
		}

		/****************************
		 * 裁判情報
		 ****************************/

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 裁判所名 */
		private String saibanshoName;

		/** 事件番号 */
		private CaseNumber caseNumber;

		/** 事件名 */
		private String jikenName;

		/** 裁判ステータス */
		private SaibanStatus saibanStatus;

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

		/** 事件SEQ */
		private Long jikenSeq;

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

		/** 追起訴 */
		private List<SaibanTsuikisoJikenDto> saibanTsuikisoJikenDtoList;

		/** 裁判追加 */
		private boolean addSaiban;

	}

	/**
	 * 裁判の期日 情報表示フォーム
	 */
	@Data
	public static class SaibanKijitsuViewForm {

		/** 初期表示状態での期日手続きの最大表示文字数 */
		private final int INIT_SHOW_MAX_CHAR_COUNT_KIJITSU_CONTENT = SaibanManagementConstant.INIT_SHOW_MAX_CHAR_COUNT_KIJITSU_CONTENT;

		/** 初期表示状態での期日結果の最大表示文字数 */
		private final int INIT_SHOW_MAX_CHAR_COUNT_KIJITSU_RESULT = SaibanManagementConstant.INIT_SHOW_MAX_CHAR_COUNT_KIJITSU_RESULT;

		/** 全ての期間の期日を表示するかどうかのチェックボックスの値 */
		private boolean isAllTimeKijitsuList;

		/** 次回期日回数 */
		private Long nextSaibanKijitsuCount;

		/** 裁判期日情報 */
		private List<SaibanKijitsuDto> saibanKijitsuDtoList;

	}

	/**
	 * 顧客情報表示フォーム
	 */
	@Data
	public static class SaibanCustomerViewForm {

		/** 裁判 顧客 情報 */
		private List<SaibanCustomerDto> saibanCustomerDtoList;

	}

	/**
	 * 共犯者 情報表示フォーム
	 */
	@Data
	public static class SaibanOtherViewForm {

		/** 裁判 その他当事者 */
		private List<SaibanKanyoshaViewDto> saibanKyohanshaDtoList = Collections.emptyList();

	}

	/**
	 * 公判担当検察官 情報表示フォーム
	 */
	@Data
	public static class SaibanKensatsukanViewForm {

		/** 公判担当検察官 */
		private SaibanKensatsukanDto saibanKensatsukanDto;

	}

}