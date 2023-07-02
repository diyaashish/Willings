package jp.loioz.app.common.mvc.accgDocRegist.form;

import java.util.List;

import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 請求書・精算書作成モーダル
 */
@Data
public class AccgDocRegistModalInputForm {

	/** 名簿ID */
	@Required
	private Long personId;

	/** 案件ID */
	@Required
	private Long ankenId;

	/** 報酬SEQリスト */
	private List<Long> feeSeqList;

	/** 預り金SEQリスト */
	private List<Long> depositRecvSeqList;
}
