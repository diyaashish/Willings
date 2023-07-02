package jp.loioz.app.user.kaikeiManagement.dto;

import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 * タイムチャージ出力の選択用オブジェクト
 */
@Data
public class TimeChargeDownloadSelectDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

}
