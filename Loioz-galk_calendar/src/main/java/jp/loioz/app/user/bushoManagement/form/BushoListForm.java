package jp.loioz.app.user.bushoManagement.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.BushoListDto;
import lombok.Data;

/**
 * 部門画面の画面フォームクラス
 */
@Data
public class BushoListForm {

	/** 部門情報リスト */
	private List<BushoListDto> bushoList;

	/** ページ */
	private Page<BushoListDto> page;

	/** 検索用の部門ID */
	private Long bushoId;
}