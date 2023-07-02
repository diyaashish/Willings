package jp.loioz.app.user.saibanManagement.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.TelPattern;
import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 * 裁判管理画面の入力フォームクラス
 */
@Data
public class SaibanEditKeijiInputForm {

	/**
	 * 裁判の基本情報入力フォーム
	 */
	@Data
	public static class SaibanBasicInputForm {

		// 表示用データのプロパティ

		/** 案件-区分 */
		private String ankenType;

		/** 案件-分野名 */
		private String bunyaName;

		/** 案件-私選・国選 */
		private String lawyerSelectType;

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 案件ID */
		private AnkenId ankenId;

		/** 裁判名（裁判所マスタの裁判所名） */
		private String saibanshoMstName;

		/** 裁判ステータス */
		private SaibanStatus saibanStatus;

		/** 事件番号-年号選択肢リスト */
		private List<SelectOptionForm> jikenGengoOptionList = new ArrayList<>();

		/** 案件情報-担当弁護士 */
		private List<SelectOptionForm> ankenTantoBengoshiOptionList = new ArrayList<>();

		/** 案件情報-担当事務 */
		private List<SelectOptionForm> ankenTantoJimuOptionList = new ArrayList<>();

		/** 裁判所マスタリスト */
		private List<SelectOptionForm> saibanshoOptionList = new ArrayList<>();

		/** 事件SEQ */
		private Long jikenSeq;

		// 入力用データのプロパティ

		/** 裁判所 */
		@MaxDigit(max = 100)
		private String saibanshoName;

		/** 裁判所ID */
		private Long saibanshoId;

		/** 係属部 */
		@MaxDigit(max = 100)
		private String keizokuBuName;

		/** 係属係 */
		@MaxDigit(max = 100)
		private String keizokuKakariName;

		/** 係属部電話番号 */
		@TelPattern
		@MaxDigit(max = 13)
		private String keizokuBuTelNo;

		/** 係属部FAX番号 */
		@TelPattern
		@MaxDigit(max = 13)
		private String keizokuBuFaxNo;

		/** 裁判官リスト */
		@Valid
		private List<SaibanCommonInputForm.SaibankanInputForm> saibankanInputFormList = new ArrayList<>();

		/** 担当書記官 */
		@MaxDigit(max = 128)
		private String tantoShoki;

		/** 事件元号 */
		@Required
		private EraType jikenGengo;

		/** 事件番号年 */
		@MaxDigit(max = 2)
		private String jikenYear;

		/** 事件番号符号 */
		@MaxDigit(max = 2)
		private String jikenMark;

		/** 事件番号 */
		@MaxDigit(max = 7)
		private String jikenNo;

		/** 事件名 */
		@MaxDigit(max = 100)
		private String jikenName;

		/** 申立日 */
		@DateTimeFormat(pattern = "yyyy/MM/dd")
		private LocalDate saibanStartDate;

		/** 終了日 */
		@DateTimeFormat(pattern = "yyyy/MM/dd")
		private LocalDate saibanEndDate;

		/** 結果 */
		@MaxDigit(max = 10000)
		private String saibanResult;

		/** 担当弁護士 */
		private List<AnkenTantoSelectInputForm> tantoLawyer = new ArrayList<>();

		/** 担当事務員 */
		private List<AnkenTantoSelectInputForm> tantoJimu = new ArrayList<>();

		/**
		 * 担当弁護士、担当事務の選択肢から、対象アカウントSEQのデータの表示名を取得する
		 * 
		 * @param accountSeq
		 * @return
		 */
		public String getAccountName(Long accountSeq) {
			Map<Long, String> seqToMap = new HashMap<>();

			seqToMap.putAll(this.ankenTantoBengoshiOptionList.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));
			seqToMap.putAll(this.ankenTantoJimuOptionList.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));

			return seqToMap.get(accountSeq);
		}
	}

	/**
	 * 裁判の期日結果入力フォーム
	 */
	@Data
	public static class SaibanKijitsuResultInputForm {

		/** 裁判期日SEQ */
		Long saibanLimitSeq;

		/** 期日結果 */
		@MaxDigit(max = 5000)
		String kijitsuResult;
	}

	/**
	 * 裁判の顧客追加入力フォーム
	 */
	@Data
	public static class SaibanCustomerAddInputForm {

		/** 追加顧客の候補リスト */
		List<AddOption> addCustomerCandidateList = new ArrayList<>();
	}

	/**
	 * 追加候補の選択肢
	 */
	@Data
	public static class AddOption {

		/** データを一意に判定する値 */
		private Long seq;

		/** 追加するかどうか */
		private boolean isToAdd;

		/** 表示タイトル */
		private String title;
	}

	/**
	 * 公判担当検察官 入力フォーム
	 */
	@Data
	public static class SaibanKensatsukanInputForm {

		/** 裁判SEQ */
		private Long saibanSeq;

		/** 本起訴事件SEQ */
		private Long mainJikenSeq;

		/** 捜査機関ID */
		private Long sosakikanId;

		/** 検察庁名 */
		@MaxDigit(max = 100)
		private String kensatsuchoName;

		/** 担当部 */
		@MaxDigit(max = 100)
		private String kensatsuchoTantoBuName;

		/** 検察官名 */
		@MaxDigit(max = 100)
		private String kensatsukanName;

		/** 検察官名かな */
		private String kensatsukanNameKana;

		/** 事務官名 */
		@MaxDigit(max = 100)
		private String jimukanName;

		/** 事務官名かな */
		private String jimukanNameKana;

		/** 検察電話番号 */
		@TelPattern
		private String kensatsuTelNo;

		/** 検察内線番号 */
		@MaxDigit(max = 5)
		@Numeric
		private String kensatsuExtensionNo;

		/** 検察FAX番号 */
		@TelPattern
		private String kensatsuFaxNo;

		/** 検察号室 */
		@MaxDigit(max = 10)
		private String kensatsuRoomNo;

		/** 検察備考 */
		@MaxDigit(max = 100)
		private String kensatsuRemarks;

		/** 検察庁選択リスト */
		private List<SelectOptionForm> kensatsuchoOptions = Collections.emptyList();

	}

}