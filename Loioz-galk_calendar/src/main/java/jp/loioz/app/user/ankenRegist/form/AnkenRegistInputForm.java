package jp.loioz.app.user.ankenRegist.form;

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
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.RegistCustomerType;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;
import jp.loioz.dto.BunyaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 案件新規登録画面のFormクラス
 */
@Data
public class AnkenRegistInputForm {

	/**
	 * 顧客
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Customer {

		/** 登録区分 */
		private RegistCustomerType registCustomerType;

		/** 個人・法人区分 */
		private CustomerType customerType;

		/** 顧客ID */
		private Long customerId;

		/** 顧客姓 */
		private String customerNameSei;

		/** 顧客姓かな */
		private String customerNameSeiKana;

		/** 顧客名 個人のみ */
		private String customerNameMei;

		/** 顧客名かな 個人のみ */
		private String customerNameMeiKana;

		/** 会社名 */
		private String companyName;

		/** 会社名かな */
		private String companyNameKana;
	}

	/** 案件の基本情報入力フォーム */
	@Valid
	private AnkenRegistBasicInputForm ankenRegistBasicInputForm;

	/**
	 * 案件の基本情報登録フォーム
	 */
	@Data
	public static class AnkenRegistBasicInputForm {

		/** Ajax通信結果の分岐時に使用するプロパティ */
		public static final String HEADER_VALUE_OF_AJAX_PROC_RESULT_REDIRECT = "100";

		/**
		 * 売上計上先、担当弁護士、担当事務の中でaccountSeqに該当するアカウント名を取得します
		 * 
		 * @param accountSeq
		 * @return
		 */
		public String getAccountName(Long accountSeq) {
			Map<Long, String> seqToMap = new HashMap<>();

			seqToMap.putAll(this.salesOwnerOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));
			seqToMap.putAll(this.tantoLawyerOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));
			seqToMap.putAll(this.tantoJimuOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));

			return seqToMap.get(accountSeq);
		}

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
		private String bunya;

		/** 分野一覧 */
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

		/** 選択済顧客情報 */
		@Required
		private List<Customer> customerList;

		/** 登録区分 */
		private RegistCustomerType registCustomerType;

		/** 個人・法人区分 */
		@Required
		private CustomerType customerType;

		/** 顧客ID */
		private Long customerId;

		/** 顧客姓 */
		@Required(groups = Kojin.class)
		@MaxDigit(max = 64, groups = Hojin.class)
		private String customerNameSei;

		/** 顧客姓かな */
		@MaxDigit(max = 64, groups = Kojin.class)
		private String customerNameSeiKana;

		/** 顧客名 */
		@MaxDigit(max = 24, groups = Kojin.class)
		private String customerNameMei;

		/** 顧客名かな */
		@MaxDigit(max = 64, groups = Kojin.class)
		private String customerNameMeiKana;

		/** 会社名 */
		@Required(groups = Hojin.class)
		@MaxDigit(max = 64, groups = Hojin.class)
		private String companyName;

		/** 会社名かな */
		@MaxDigit(max = 128, groups = Hojin.class)
		private String companyNameKana;
	}

}