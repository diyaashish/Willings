package jp.loioz.app.user.planSetting.form;

import lombok.Data;

/**
 * プラン設定画面の解約確認フォームクラス
 */
@Data
public class PlanSettingCancelConfirmForm {
	
	/** トライアル中かどうか */
	private boolean isDuringFreeTrial;
	
	/** 確認後に実行するアクション処理のボタンのID */
	private String actionButtonId;
	
}
