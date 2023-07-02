package jp.loioz.app.user.ankenManagement.form;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.dto.AnkenEditCustomerListDto;
import jp.loioz.dto.AnkenSosakikanDto;
import lombok.Data;

/**
 * 案件管理（刑事）画面
 *
 * <pre>
 * 送付書選択（捜査機関含む）
 * </pre>
 */
@Data
public class AnkenSouhushoForm {

	/** 案件ID */
	private AnkenId ankenId;

	/** 顧客一覧 */
	private List<AnkenEditCustomerListDto> customerList = new ArrayList<AnkenEditCustomerListDto>();

	/** 捜査機関情報 */
	private List<AnkenSosakikanDto> ankenSosakikan = new ArrayList<AnkenSosakikanDto>();

}
