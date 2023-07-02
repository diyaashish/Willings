package jp.loioz.app.common.mvc.accgAccountSelect.form;

import java.util.List;

import jp.loioz.dto.GinkoKozaDto;
import lombok.Data;

/**
 * 口座選択モーダルフォームクラス
 */
@Data
public class AccgInvoiceStatementAccountSelectForm {

	/** 口座リスト */
	private List<GinkoKozaDto> bankAccountList;

}
