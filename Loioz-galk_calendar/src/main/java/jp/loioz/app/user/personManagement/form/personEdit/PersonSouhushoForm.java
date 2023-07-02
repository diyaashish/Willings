package jp.loioz.app.user.personManagement.form.personEdit;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.domain.value.CustomerId;
import jp.loioz.dto.AnkenDto;
import lombok.Data;

/**
 * 名簿情報：送付書選択表示フォームクラス
 */
@Data
public class PersonSouhushoForm {

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名* */
	private String customerName;

	/** 案件ID */
	private List<AnkenDto> ankenList = new ArrayList<AnkenDto>();

}
