package jp.loioz.app.user.saibanshoManagement.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.SaibanshoListDto;
import lombok.Data;

@Data
public class SaibanshoListForm {

	/** 裁判所部リストのDto */
	private List<SaibanshoListDto> saibanshoList;

	/** ページ */
	private Page<SaibanshoListDto> page;
}
