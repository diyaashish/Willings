package jp.loioz.app.user.saibanManagement.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.TelPattern;
import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 * 裁判登録（民事）画面の入力フォームクラス
 */
@Data
public class SaibanRegistMinjiInputForm {

	/** 案件ID */
	private AnkenId ankenId;

	/**
	 * 裁判の基本情報入力フォーム
	 */
	@Data
	public static class SaibanRegistMinjiBasicInputForm {

		// 表示用データのプロパティ

		/** 案件ID */
		private Long ankenId;

		/** 事件番号-年号選択肢リスト */
		List<SelectOptionForm> jikenGengoOptionList = new ArrayList<>();

		/** 案件情報-担当弁護士 */
		List<SelectOptionForm> ankenTantoBengoshiOptionList = new ArrayList<>();

		/** 案件情報-担当事務 */
		List<SelectOptionForm> ankenTantoJimuOptionList = new ArrayList<>();

		/** 裁判所マスタリスト */
		List<SelectOptionForm> saibanshoOptionList = new ArrayList<>();

		// 入力用データのプロパティ

		/** 事件名 */
		@MaxDigit(max = 100)
		private String jikenName;

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

		// hidden（保存時などに必要なパラメータ）

		/** 裁判SEQ（登録処理完了後、この値を設定して、画面へ返却するためのプロパティ） */
		private Long saibanSeq;
	}
}
