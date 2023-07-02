package jp.loioz.app.user.planSetting.form;

import java.util.List;

import jp.loioz.dto.PlanHistoryDto;
import lombok.Data;

/**
 * プラン履歴画面のフォームクラス
 */
@Data
public class PlanHistoryListForm {

	/** プラン履歴情報 */
	private List<PlanHistoryDto> planHistoryList;
	
}
