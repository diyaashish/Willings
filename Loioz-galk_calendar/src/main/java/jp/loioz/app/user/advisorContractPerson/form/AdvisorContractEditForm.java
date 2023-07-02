package jp.loioz.app.user.advisorContractPerson.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.ContractStatus;
import jp.loioz.common.constant.CommonConstant.ContractType;
import jp.loioz.common.validation.annotation.EnumType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 顧問契約編集フォーム
 */
@Data
public class AdvisorContractEditForm {

	/** 顧問料金（月額）の最大桁数 */
	public static final int MAX_MONTH_CHARGE_INPUT_DIGIT = 7;

	/** 稼働時間（時間/月）の最大桁数 */
	public static final int MAX_MONTH_TIME_INPUT_DIGIT = 4;

	// ▼ 表示用

	/** 契約状況選択リスト */
	List<SelectOptionForm> contractStatusOptionList = new ArrayList<>();

	/** 契約区分選択リスト */
	List<SelectOptionForm> contractTypeOptionList = new ArrayList<>();

	/** 売上計上先 */
	private List<SelectOptionForm> salesOwnerOptions = Collections.emptyList();

	/** 担当弁護士 */
	private List<SelectOptionForm> tantoLawyerOptions = Collections.emptyList();

	/** 担当事務 */
	private List<SelectOptionForm> tantoJimuOptions = Collections.emptyList();

	// ▼ hidden項目

	/** 名簿ID */
	private Long personId;

	/** 顧問契約SEQ */
	private Long advisorContractSeq;

	// ▼ 入力項目

	/** 契約開始日 */
	@LocalDatePattern
	private String contractStartDate;

	/** 契約終了日 */
	@LocalDatePattern
	private String contractEndDate;

	/** 顧問料金（月額） */
	@Numeric
	@MinNumericValue(min = 0)
	@MaxDigit(max = AdvisorContractEditForm.MAX_MONTH_CHARGE_INPUT_DIGIT)
	private String contractMonthCharge;

	/** 稼働時間（時間/月） */
	@Numeric
	@MinNumericValue(min = 0)
	@MaxDigit(max = AdvisorContractEditForm.MAX_MONTH_TIME_INPUT_DIGIT)
	private String contractMonthTime;

	/** 契約状況 */
	@Required
	@EnumType(value = ContractStatus.class)
	private String contractStatus;

	/** 契約内容 */
	@MaxDigit(max = 500)
	private String contractContent;

	/** 契約区分 */
	@Required
	@EnumType(value = ContractType.class)
	private String contractType;

	/** 契約メモ */
	@MaxDigit(max = 100)
	private String contractMemo;

	/** 売上計上先 */
	private List<Tanto> salesOwner = this.setUpTantoList();

	/** 担当弁護士 */
	private List<Tanto> tantoLawyer = this.setUpTantoList();

	/** 担当事務員 */
	private List<Tanto> tantoJimu = this.setUpTantoList();

	/**
	 * 新規登録モーダルかの判定
	 *
	 * @return
	 */
	public boolean isNew() {
		if (this.advisorContractSeq == null) {
			// 編集対象のSEQがないので新規
			return true;
		}
		return false;
	}

	/** 担当者のSetUp */
	private List<Tanto> setUpTantoList() {
		List<Tanto> tantoList = new ArrayList<>();
		for (var i = 0; i < CommonConstant.CONTRACT_TANTO_ADD_LIMIT; i++) {
			tantoList.add(new Tanto());
		}
		return tantoList;
	}

	/** 担当者のアカウント名を取得 */
	public String getAccountName(Long accountSeq) {
		Map<Long, String> seqToMap = new HashMap<>();

		seqToMap.putAll(this.salesOwnerOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));
		seqToMap.putAll(this.tantoLawyerOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));
		seqToMap.putAll(this.tantoJimuOptions.stream().collect(Collectors.toMap(SelectOptionForm::getValueAsLong, SelectOptionForm::getLabel)));

		return seqToMap.get(accountSeq);
	}

	/**
	 * 担当
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Tanto {

		/** 担当者 */
		private Long accountSeq;

		/** 主担当 */
		private boolean mainTanto;

		/**
		 * 空であるか判定する
		 *
		 * @return 空の場合はtrue
		 */
		public boolean isEmpty() {
			return accountSeq == null;
		}
	}

}
