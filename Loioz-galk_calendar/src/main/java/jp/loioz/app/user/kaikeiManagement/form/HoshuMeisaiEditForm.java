package jp.loioz.app.user.kaikeiManagement.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.kaikeiManagement.dto.HoshuMeisaiListDto;
import lombok.Data;

/**
 * 報酬明細のフォームクラス
 */
@Data
public class HoshuMeisaiEditForm {

	/** 精算済を表示するフラグ */
	private boolean seisanCompDispFlg = true;

	/** 遷移元判定フラグ */
	private String transitionFlg;

	/** 遷移元案件ID */
	private Long transitionAnkenId;

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	/** 行選択された案件ID */
	private Long selectedAnkenId;

	/** 行選択された案件ID */
	private Long selectedCustomerId;

	/** 未完了の案件数 */
	private int notCompletedAnkenCount;

	/** 報酬明細一覧 */
	private List<HoshuMeisaiListDto> hoshuMeisaiDtoList = Collections.emptyList();

}
