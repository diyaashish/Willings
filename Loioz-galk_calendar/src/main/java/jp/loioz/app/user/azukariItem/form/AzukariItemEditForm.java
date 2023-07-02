package jp.loioz.app.user.azukariItem.form;

import java.util.List;

import javax.validation.Valid;

import jp.loioz.dto.AzukariItemEditDto;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import lombok.Data;

/**
 * 預り品編集画面のフォームクラス
 */
@Data
public class AzukariItemEditForm {

	/** 預り品Dto */
	@Valid
	private AzukariItemEditDto azukariItemDto = new AzukariItemEditDto();

	/** 関与者一覧 */
	private List<CustomerKanyoshaPulldownDto> kanyoshaList;

	/** 顧客一覧 */
	private List<CustomerKanyoshaPulldownDto> customerList;

	/** 案件ID */
	private Long ankenId;
}