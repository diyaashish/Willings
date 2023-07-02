package jp.loioz.app.user.ankenManagement.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

/**
 * 案件管理画面（民事）の入力フォームクラス
 */
@Data
public class AnkenEditMinjiInputForm {

	/**
	 * 案件基本情報の入力用フォームオブジェクト名
	 */
	@Data
	public static class AnkenBasicInputForm {

		/** Ajax通信結果の分岐時に使用するプロパティ */
		public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_REDIRECT = "100";

		// 表示用プロパティ
		/** 案件ID */
		private Long ankenId;

		/** 受任区分の選択肢 */
		private List<SelectOptionForm> ankenTypeOptions = Collections.emptyList();

		/** 売上計上先 */
		private List<SelectOptionForm> salesOwnerOptions = Collections.emptyList();

		/** 担当弁護士 */
		private List<SelectOptionForm> tantoLawyerOptions = Collections.emptyList();

		/** 担当事務 */
		private List<SelectOptionForm> tantoJimuOptions = Collections.emptyList();

		// 入力用プロパティ
		/** 区分 */
		private AnkenType ankenType;

		/** 案件登録日 */
		@Required
		@DateTimeFormat(pattern = "yyyy/MM/dd")
		private LocalDate ankenCreatedDate;

		/** 案件名 */
		@MaxDigit(max = 150)
		private String ankenName;

		/** 分野 */
		@Required
		private Long bunya;

		/** 分野選択肢 */
		private List<BunyaDto> bunyaList;

		/** 売上計上先 */
		private List<AnkenTantoSelectInputForm> salesOwner = this.setUpTantoList();

		/** 担当弁護士 */
		private List<AnkenTantoSelectInputForm> tantoLawyer = this.setUpTantoList();

		/** 担当事務員 */
		private List<AnkenTantoSelectInputForm> tantoJimu = this.setUpTantoList();

		/** 事案概要・方針 */
		@MaxDigit(max = 5000)
		private String jianSummary;

		/** 担当者のSetUp */
		private List<AnkenTantoSelectInputForm> setUpTantoList() {
			List<AnkenTantoSelectInputForm> tantoList = new ArrayList<>();
			for (var i = 0; i < CommonConstant.ANKEN_TANTO_ADD_LIMIT; i++) {
				tantoList.add(new AnkenTantoSelectInputForm());
			}
			return tantoList;
		}

		public String getAccountName(Long accountSeq) {
			Map<Long, String> seqToMap = new HashMap<>();

			seqToMap.putAll(this.salesOwnerOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));
			seqToMap.putAll(this.tantoLawyerOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));
			seqToMap.putAll(this.tantoJimuOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));

			return seqToMap.get(accountSeq);
		}

	}

	/**
	 * 案件顧客情報入力用オブジェクト
	 */
	@Data
	public static class AnkenCustomerInputForm {

		// 表示用項目
		/** 案件ID */
		private AnkenId ankenId;

		/** 顧客ID */
		private CustomerId customerId;

		/** 案件ステータス */
		private AnkenStatus ankenStatus;

		// 入力用項目

		/** 受任日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String juninDate;

		/** 事件処理完了日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String jikenshoriKanryoDate;

		/** 事件処理完了日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String seisanKanryoDate;

		/** 完了済 */
		private boolean isCompleted;

		/** 案件-顧客バージョンNo */
		@Required
		private Long versionNo;

		/** 受任日：LocalDate型 */
		public LocalDate getJuninDateLocalDate() {
			try {
				return DateUtils.parseToLocalDate(this.juninDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 事件処理完了日：LocalDate型 */
		public LocalDate getJikenshoriKanryoDateLocalDate() {
			try {
				return DateUtils.parseToLocalDate(this.jikenshoriKanryoDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * 案件ステータスが相談中か不受任か返す
		 * 
		 * @return
		 */
		public boolean isSodanFujyunin() {
			boolean rc = false;
			if (CommonConstant.AnkenStatus.SODAN.equals(ankenStatus) || CommonConstant.AnkenStatus.FUJUNIN.equals(ankenStatus)) {
				rc = true;
			}
			return rc;
		}

	}

}
