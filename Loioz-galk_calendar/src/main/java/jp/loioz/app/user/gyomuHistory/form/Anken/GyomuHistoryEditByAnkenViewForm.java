package jp.loioz.app.user.gyomuHistory.form.Anken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import jp.loioz.app.common.form.SelectOptionForm;
import lombok.Builder;
import lombok.Data;

@Data
public class GyomuHistoryEditByAnkenViewForm {

	/** 業務履歴SEQ */
	private Long gyomuHistorySeq;

	/** 遷移元案件ID */
	private Long ankenId;

	/** 入力フォーム情報 */
	@Valid
	private GyomuHistoryEditByAnkenInputForm inputForm;

	/** 遷移元案件IDに紐づく顧客情報 */
	private List<Customer> customerInfo = new ArrayList<>();

	/** 遷移元案件IDに紐づく裁判情報 */
	private List<SelectOptionForm> saibanSelectBox = new ArrayList<>();

	/**
	 * 新規登録モード判定
	 * 
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (Objects.isNull(this.gyomuHistorySeq)) {
			newFlg = true;
		}
		return newFlg;
	}

	@Data
	@Builder
	public static class Customer {

		/** 顧客ID */
		private Long customerId;

		/** 顧客名 */
		private String customerName;
	}

}
