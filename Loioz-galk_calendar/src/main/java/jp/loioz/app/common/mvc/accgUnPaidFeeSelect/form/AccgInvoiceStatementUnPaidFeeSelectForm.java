package jp.loioz.app.common.mvc.accgUnPaidFeeSelect.form;

import java.util.List;

import jp.loioz.app.common.mvc.accgUnPaidFeeSelect.dto.AccgInvoiceStatementUnPaidFeeSelectDto;
import lombok.Data;

/**
 * 未精算報酬選択モーダルフォームクラス
 */
@Data
public class AccgInvoiceStatementUnPaidFeeSelectForm {

	/** 未精算報酬リスト */
	private List<AccgInvoiceStatementUnPaidFeeSelectDto> unPaidFeeList;

}
