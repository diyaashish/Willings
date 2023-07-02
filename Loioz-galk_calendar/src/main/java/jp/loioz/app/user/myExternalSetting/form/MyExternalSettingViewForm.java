package jp.loioz.app.user.myExternalSetting.form;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.dto.ConnectedExternalServiceDto;
import lombok.Data;

/**
 * 外部サービス接続画面のビュークラス
 */
@Data
public class MyExternalSettingViewForm {

	/** アカウント外部連携情報の表示用 */
	private AccountExternalConnectViewForm accountExternalConnectViewForm;

	/**
	 * アカウント外部連携情報の表示用オブジェクト
	 */
	@Data
	public static class AccountExternalConnectViewForm {

		/** 利用可能な外部連携サービス ※連携可能なサービスは制御できるようにする */
		private List<ExternalService> canConnectExternalServiceList = Collections.emptyList();

		/** 連携している外部サービスMap */
		private Map<ExternalService, ConnectedExternalServiceDto> connectedExternalServiceMap = Collections.emptyMap();

	}

}
