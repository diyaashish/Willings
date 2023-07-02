package jp.loioz.app.user.sosakikanManagement.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.SosakikanListDto;
import lombok.Data;

@Data
public class SosakikanListForm {

	/** 捜査機関情報リスト */
	private List<SosakikanListDto> sosakikanList;

	/** ページ */
	private Page<SosakikanListDto> page;

	/** 検索結果件数 */
	private Long count;
}
