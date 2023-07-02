package jp.loioz.app.user.gyomuHistory.form.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.dto.BunyaDto;
import lombok.Builder;
import lombok.Data;

@Data
public class GyomuHistoryEditByCustomerViewForm {

	/** 業務履歴SEQ */
	private Long gyomuHistorySeq;

	/** 遷移元顧客ID */
	private Long customerId;

	/** 入力フォーム */
	@Valid
	private GyomuHistoryEditByCustomerInputForm inputForm;

	/** 顧客に紐づく案件情報（未完了） */
	List<Anken> ankenInfo = new ArrayList<>();

	/** 顧客に紐づく案件情報（完了、不受任） */
	List<Anken> ankenInfoCompleted = new ArrayList<>();
	
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
	public static class Anken {

		/** 案件ID */
		private AnkenId ankenId;

		/** 分野 */
		private BunyaDto bunya;

		/** 案件名 */
		private String ankenName;

		/** 表示用分野-案件名 */
		private String labelName;

	}

}
