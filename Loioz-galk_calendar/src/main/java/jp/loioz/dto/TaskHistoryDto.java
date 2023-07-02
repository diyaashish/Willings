package jp.loioz.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.common.constant.CommonConstant.AnkenReletedUpdateKbn;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TaskCheckItemUpdateKbn;
import jp.loioz.common.constant.CommonConstant.TaskHistoryType;
import jp.loioz.common.constant.CommonConstant.TaskStatus;
import lombok.Data;

@Data
public class TaskHistoryDto {

	/** タスク履歴SEQ */
	private Long taskHistorySeq;

	/** タスクSEQ */
	private Long taskSeq;

	/** タスク履歴種別 */
	private TaskHistoryType taskHistoryType;

	/** 件名更新フラグ */
	private SystemFlg titleUpdateFlg;

	/** 本文更新フラグ */
	private SystemFlg contentUpdateFlg;

	/** 作業者更新フラグ */
	private SystemFlg workerUpdateFlg;

	/** 期日To */
	private SystemFlg limitDtUpdateFlg;

	/** タスクステータス */
	private SystemFlg statusUpdateFlg;

	/** 更新後タスクステータス */
	private TaskStatus updatedStatus;;

	/** 案件更新フラグ */
	private SystemFlg ankenReletedUpdateFlg;

	/** 案件更新区分 */
	private AnkenReletedUpdateKbn ankenReletedUpdateKbn;

	/** チェック項目更新フラグ */
	private SystemFlg checkItemUpdateFlg;

	/** チェック項目更新区分 */
	private TaskCheckItemUpdateKbn checkItemUpdateKbn;

	/** チェック項目名 */
	private String checkItemName;

	/** コメント（表示用） */
	private String commentForDisplay;

	/** コメント（入力用） */
	private String comment;

	/** 登録日 */
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
	private LocalDateTime createdAt;

	/** 更新日 */
	private LocalDateTime updatedAt;

	/** 登録アカウントSEQ */
	private Long createdBy;

	/** 登録者名 */
	private String accountName;

	/** バージョンNo */
	private Long versionNo;

	/** 表示用プロパティ */
	private boolean isMyComment;

}
