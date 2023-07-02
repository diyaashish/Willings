package jp.loioz.app.user.myExternalSetting.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.box.sdk.BoxAPIConnection;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.box.service.CommonBoxApiService;
import jp.loioz.app.common.api.dropbox.form.DropboxConnectDto;
import jp.loioz.app.common.api.dropbox.service.CommonDropboxApiService;
import jp.loioz.app.common.api.google.form.GoogleConnectDto;
import jp.loioz.app.common.api.google.service.CommonGoogleApiService;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.user.myExternalSetting.form.MyExternalSettingViewForm;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dto.ConnectedExternalServiceDto;
import jp.loioz.entity.TAuthTokenEntity;

/**
 * 外部サービス接続画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MyExternalSettingService extends DefaultService {

	@Autowired
	private CommonGoogleApiService commonGoogleApiService;

	@Autowired
	private CommonBoxApiService commonBoxApiService;

	@Autowired
	private CommonDropboxApiService commonDropboxApiService;

	@Autowired
	private CommonOAuthService commonOAuthService;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面ViewFormの作成処理
	 * 
	 * @return
	 */
	public MyExternalSettingViewForm createViewForm() {
		return new MyExternalSettingViewForm();
	}

	/**
	 * 接続中外部サービス接続情報用オブジェクトを作成する
	 * 
	 * @return
	 */
	public MyExternalSettingViewForm.AccountExternalConnectViewForm createAccountExternalConnectViewForm() {

		MyExternalSettingViewForm.AccountExternalConnectViewForm viewForm = new MyExternalSettingViewForm.AccountExternalConnectViewForm();

		// テナントが接続している外部サービスを取得する
		List<ExternalService> canConnectExternalServiceList = commonOAuthService.getCanConnectExternalServiceList();

		// ユーザーが連携している(DBに登録している)アクセストークン情報をすべて取得する
		List<TAuthTokenEntity> tAuthTokenEntities = commonOAuthService.getMyConnectedExternalService();
		Map<ExternalService, ConnectedExternalServiceDto> connectedExternalServiceMap = tAuthTokenEntities.stream().map(e -> {
			ConnectedExternalServiceDto connectedExternalServiceDto = new ConnectedExternalServiceDto();
			connectedExternalServiceDto.setConnectedExternalServiceSeq(e.getAuthTokenSeq());
			connectedExternalServiceDto.setExternalService(ExternalService.of(e.getExternalServiceId()));
			connectedExternalServiceDto.setConnectedStartDate(DateUtils.parseToString(e.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
			return connectedExternalServiceDto;
		}).collect(Collectors.toMap(ConnectedExternalServiceDto::getExternalService, Function.identity()));

		viewForm.setConnectedExternalServiceMap(connectedExternalServiceMap);
		viewForm.setCanConnectExternalServiceList(canConnectExternalServiceList);

		return viewForm;
	}

	/**
	 * アカウント画面 Google連携処理
	 * 
	 * @param code
	 * @throws AppException
	 */
	public void accountGoogleAuthConnect(String code) throws AppException {

		// 接続処理 ※このときにアクセストークンが発行される
		GoogleConnectDto connectDto = commonGoogleApiService.requestAccessToken(code);

		try {
			// トークン情報の登録処理
			TAuthTokenEntity insertAuthTokenEntity = new TAuthTokenEntity();
			insertAuthTokenEntity.setAccountSeq(SessionUtils.getLoginAccountSeq());
			insertAuthTokenEntity.setAccessToken(connectDto.getAccessToken());
			insertAuthTokenEntity.setRefreshToken(connectDto.getRefreshToken());
			insertAuthTokenEntity.setExternalServiceId(ExternalService.GOOGLE.getCd());
			commonOAuthService.insert(insertAuthTokenEntity);

		} catch (Exception e) {
			// 手動でトークンを失効させる。
			try {
				// 作成したトークンのリフレッシュトークンを無効化する
				commonGoogleApiService.revokeToken(connectDto.getAccessToken());

			} catch (Exception ex) {
				// ここで発生したエラーは対処出来ないので握りつぶす
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}
	}

	/**
	 * アカウント画面 Box連携処理
	 * 
	 * @param code
	 */
	public void accountBoxAuthConnect(String code) throws AppException {

		// 接続処理 ※このときにアクセストークンが発行される
		BoxAPIConnection api = commonBoxApiService.requestAccessToken(code);

		try {
			// トークン情報の登録処理
			TAuthTokenEntity insertAuthTokenEntity = new TAuthTokenEntity();
			insertAuthTokenEntity.setAccountSeq(SessionUtils.getLoginAccountSeq());
			insertAuthTokenEntity.setAccessToken(api.getAccessToken());
			insertAuthTokenEntity.setRefreshToken(api.getRefreshToken());
			insertAuthTokenEntity.setExternalServiceId(ExternalService.BOX.getCd());
			commonOAuthService.insert(insertAuthTokenEntity);

		} catch (Exception e) {
			// 手動でトークンを失効させる。
			try {
				// 作成したトークンのリフレッシュトークンを無効化する
				commonBoxApiService.revokeRefreshToken(api.getRefreshToken());

			} catch (Exception ex) {
				// ここで発生したエラーは対処出来ないので握りつぶす
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}
	}

	/**
	 * Dropboxの認証
	 * 
	 * @param code
	 * @throws AppException
	 */
	public void accountDropboxAuthConnect(String code) throws AppException {

		DropboxConnectDto dropboxConnectDto = commonDropboxApiService.requestAccessToken(code);

		try {
			// トークン情報の登録処理
			TAuthTokenEntity insertAuthTokenEntity = new TAuthTokenEntity();
			insertAuthTokenEntity.setAccountSeq(SessionUtils.getLoginAccountSeq());
			insertAuthTokenEntity.setAccessToken(dropboxConnectDto.getAccessToken());
			insertAuthTokenEntity.setRefreshToken(dropboxConnectDto.getRefreshToken());
			insertAuthTokenEntity.setExternalServiceId(ExternalService.DROPBOX.getCd());
			commonOAuthService.insert(insertAuthTokenEntity);

		} catch (Exception e) {
			// 手動でトークンを失効させる。
			try {
				// 作成したトークンのリフレッシュトークンを無効化する
				commonDropboxApiService.revokeToken(dropboxConnectDto.getAccessToken());

			} catch (Exception ex) {
				// ここで発生したエラーは対処出来ないので握りつぶす
				logger.warn("エラーハンドリング内でのAPI処理が失敗しました。", ex);
			}
			throw e;
		}

	}

	// =========================================================================
	// public メソッド
	// =========================================================================

}
