package jp.loioz.app.user.azukariItem.form;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.AzukariItemListDto;
import lombok.Data;

/**
 * 預り品一覧画面の画面表示フォームクラス
 */
@Data
public class AzukariItemViewForm {

	/** 預り品Dtoリスト */
	private List<AzukariItemListDto> azukariItemDtoList = new ArrayList<AzukariItemListDto>();

	/** 案件ID */
	private Long ankenId;

	/** 検索結果件数 */
	private long count;

	/** ページ */
	private Page<Long> page;
}