package jp.loioz.app.user.roomManagement.form;

import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.dto.RoomListDto;
import lombok.Data;

/**
 * 会議室一覧画面のフォームクラス
 */
@Data
public class RoomListForm {

	/** 会議室情報リスト */
	private List<RoomListDto> roomList;

	/** ページ */
	private Page<RoomListDto> page;
}