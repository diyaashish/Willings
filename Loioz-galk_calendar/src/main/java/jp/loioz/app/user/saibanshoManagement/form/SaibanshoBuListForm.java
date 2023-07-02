package jp.loioz.app.user.saibanshoManagement.form;

import java.util.List;

import jp.loioz.dto.SaibanshoBuListDto;
import lombok.Data;

@Data
public class SaibanshoBuListForm {

	/** 裁判所ID */
	private Long saibanshoId;

	/** 都道府県 */
	private String todofukenCd;

	/** 裁判所部リストのDto */
	private List<SaibanshoBuListDto> saibanshoBuList;
}