package jp.loioz.app.user.ankenManagement.form;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.ankenManagement.dto.SosakikanSelectOptionDto;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.ShobunType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.LocalDateTimePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.annotation.TelPattern;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

/**
 * 案件管理画面（刑事）の入力フォームクラス
 */
@Data
public class AnkenEditKeijiInputForm {

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

		/** 私選・国選 */
		private String lawyerSelectType;

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

		/** 完了済 */
		private boolean isCompleted;

		/** 案件ステータス */
		private AnkenStatus ankenStatus;

		// 入力用項目

		/** 受任日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String juninDate;

		/** 事件処理完了日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String jikenshoriKanryoDate;

		/** 精算完了日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String seisanKanryoDate;

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

	/**
	 * 案件顧客-事件 情報入力用オブジェクト
	 */
	@Data
	public static class AnkenCustomerJikenListInputForm {

		// 表示用項目

		/** 処分 */
		private List<SelectOptionForm> shobunTypeOptions = Collections.emptyList();

		/** 選択顧客が完了済 */
		private boolean isCompleted;

		// 入力用項目

		/** 事件SEQ */
		private Long jikenSeq;

		/** 選択している案件ID */
		@Required
		private Long targetAnkenId;

		/** 選択している顧客ID */
		@Required
		private Long targetCustomerId;

		/** 事件名 */
		@MaxDigit(max = 100)
		private String jikenName;

		/** 逮捕日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String taihoDate;

		/** 勾留請求日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String koryuSeikyuDate;

		/** 満期日① */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String koryuExpirationDate;

		/** 満期日② */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String koryuExtendedExpirationDate;

		/** 処分種別 */
		@EnumType(value = ShobunType.class)
		private String shobunType;

		/** 処分日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String shobunDate;

	}

	/**
	 * 案件顧客-接見 情報入力用オブジェクト
	 */
	@Data
	public static class AnkenCustomerSekkenListInputForm {

		/** 接見SEQ */
		private Long sekkenSeq;

		/** 選択している案件ID */
		@Required
		private Long targetAnkenId;

		/** 選択している顧客ID */
		@Required
		private Long targetCustomerId;

		/** 接見開始日 */
		@LocalDateTimePattern(format = DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM)
		private String sekkenStartAt;

		/** 接見終了日 */
		@LocalDateTimePattern(format = DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM)
		private String sekkenEndAt;

		/** 場所 */
		@MaxDigit(max = 100)
		private String place;

		/** 備考 */
		@MaxDigit(max = 5000)
		private String remarks;

		/** 選択顧客が完了済 */
		private boolean isCompleted;

		/** 接見開始日：LocalDateTime */
		public LocalDateTime getParsedSekkenStartAt() {
			try {
				return DateUtils.parseToLocalDateTime(this.sekkenStartAt, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM);
			} catch (Exception e) {
				return null;
			}
		}

		/** 接見終了日：LocalDateTime */
		public LocalDateTime getParsedSekkenEndAt() {
			try {
				return DateUtils.parseToLocalDateTime(this.sekkenEndAt, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM);
			} catch (Exception e) {
				return null;
			}
		}

	}

	/**
	 * 案件顧客-在監 情報入力用オブジェクト
	 */
	@Data
	public static class AnkenCustomerZaikanListInputForm {

		// 表示用プロパティ
		/** 捜査機関datalistオプション */
		List<SosakikanSelectOptionDto> sosakikanSelectOptionDto = Collections.emptyList();

		// 入力用プロパティ
		/** 勾留SEQ */
		private Long koryuSeq;

		/** 選択している案件ID */
		@Required
		private Long targetAnkenId;

		/** 選択している顧客ID */
		@Required
		private Long targetCustomerId;

		/** 在監/移送日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String zaikanDate;

		/** 保釈/釈放/移送日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String hoshakuDate;

		/** 捜査機関ID */
		private Long sosakikanId;

		/** 在監場所 */
		@MaxDigit(max = 100)
		private String zaikanPlace;

		/** 電話番号 */
		@TelPattern
		private String telNo;

		/** 備考 */
		@MaxDigit(max = 5000)
		private String remarks;

		/** 選択顧客が完了済 */
		private boolean isCompleted;

		/** 在監日：LocalDate */
		public LocalDate getParsedZaikanDate() {
			try {
				return DateUtils.parseToLocalDate(this.zaikanDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}

		/** 保釈日：LocalDate */
		public LocalDate getParsedHoshakuDate() {
			try {
				return DateUtils.parseToLocalDate(this.hoshakuDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * 捜査機関 入力用オブジェクト
	 */
	@Data
	public static class AnkenSosakikanListInputForm {

		// 表示用プロパティ

		/** 捜査機関選択用 */
		private List<SosakikanSelectOptionDto> sosakikanSelectOptionDto = Collections.emptyList();

		// 入力用プロパティ

		/** 捜査機関SEQ */
		private Long sosakikanSeq;

		/** 案件ID */
		@Required
		private Long ankenId;

		/** 施設ID */
		private Long sosakikanId;

		/** 名称 */
		@MaxDigit(max = 100)
		private String sosakikanName;

		/** 担当部 */
		@MaxDigit(max = 100)
		private String sosakikanTantoBu;

		/** 電話番号 */
		@TelPattern
		@MaxDigit(max = 20)
		private String sosakikanTelNo;

		/** 内線番号 */
		@MaxDigit(max = 20)
		private String sosakikanExtensionNo;

		/** FAX番号 */
		@TelPattern
		@MaxDigit(max = 20)
		private String sosakikanFaxNo;

		/** 号室 */
		@MaxDigit(max = 10)
		private String sosakikanRoomNo;

		/** 担当者①氏名 */
		@MaxDigit(max = 128)
		private String tantosha1Name;

		/** 担当者②氏名 */
		@MaxDigit(max = 128)
		private String tantosha2Name;

		/** 備考 */
		@MaxDigit(max = 3000)
		private String remarks;

	}

}
