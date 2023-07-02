package jp.loioz.app.common.api.google.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.api.services.drive.DriveScopes;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Google関連のConstants
 */
public class GoogleConstants {

	/** Googleのトークン削除用URL */
	public static final String GOOGLE_AUTH_ROVOKE_BASE = "https://oauth2.googleapis.com/revoke";
	/** GoogleAuthGatewayRedirect ※Google側のシステムにも登録しているため、値は変えないこと */
	public static final String GOOGLE_AUTH_GATEWAY_REDIRECT_PATH = "googleAuthGatewayRedirect";

	/** GoogleDriveアプリのフォルダURL */
	public static final String GOOGLE_DRIVE_FOLDER_URL_BASE = "https://drive.google.com/drive/folders/%s";

	/**
	 * GoogleのOAuth認証時にredirect先を決定するEnum
	 */
	@Getter
	@AllArgsConstructor
	public enum GoogleAuthRequestCode implements DefaultEnum {

		/** ファイル管理設定画面からのGoogleを選択した時 */
		FILE_SETTING_CONNECT("1", "GoogleDrive(ファイル管理設定)"),
		/** アカウント画面から連携を行う時 */
		USER_SETTING_CONNECT("2", "ユーザー連携");

		/** コード */
		private String cd;

		/** 名称 */
		private String val;

		/**
		 * コード値からEnumを取得
		 * 
		 * @param cd
		 * @return
		 */
		public static GoogleAuthRequestCode of(String cd) {
			return DefaultEnum.getEnum(GoogleAuthRequestCode.class, cd);
		}
	}

	/**
	 * loioz内で利用するGoogleAPIのスコープ
	 */
	@Getter
	@AllArgsConstructor
	public enum GoogleAPIScope {

		/** Driveスコープ ※参照{@link DriveScopes#DRIVE DriveScopes} */
		DRIVE(DriveScopes.DRIVE),;

		/** スコープ */
		private String scope;
	}

	/**
	 * GoogleDriveのMineType
	 * https://developers.google.com/drive/api/v3/mime-types
	 * 
	 * ※SDK内に管理するクラスが見つかれば削除
	 */
	@Getter
	@AllArgsConstructor
	public enum DriveMineType {

		FOLDER("application/vnd.google-apps.folder"),;

		/** MineType */
		private String minetype;
	}

	/**
	 * GoogleDriveファイル(フォルダ)情報のFiled名
	 * 
	 * @see <a href="https://developers.google.com/drive/api/v3/reference/files">公式ドキュメント</a>.
	 */
	@Getter
	@AllArgsConstructor
	public enum DriveFileField {

		KIND("kind"),
		ID("id"),
		NAME("name"),
		MIME_TYPE("mimeType"),
		TRASHED("trashed"),
		PARENTS("parents"),
		CREATED_TIME("createdTime"),
		OWNERS("owners"),
		SHARED("shared"),
		OWNED_BY_ME("ownedByMe");

		/** フィールド名 */
		private String fieldName;

		/**
		 * 全てのfield名をList形式で取得する
		 * 
		 * @return
		 */
		public static List<String> getAll() {
			return Arrays.asList(DriveFileField.values()).stream().map(DriveFileField::getFieldName).collect(Collectors.toList());
		}

		/**
		 * すべてのfield名をカンマ区切り文字列で取得する
		 * 
		 * @return
		 */
		public static String getAllStr() {
			return StringUtils.list2Comma(getAll());
		}
	}

	@Getter
	@AllArgsConstructor
	public enum OAuthAPIError {

		INVALID_REQUEST(400, "invalid_request", null),
		INVALID_CLIENT(400, "invalid_client", null),
		INVALID_GRANT(400, "invalid_grant", null),
		UNAUTHORIZED_CLIENT(400, "unauthorized_client", null),
		UNSUPPORTED_GRANT_TYPE(400, "unsupported_grant_type", null),
		INVALID_SCOPE(400, "invalid_scope", null),

		// 明確なドキュメントがなかったが発生した
		UNAUTHORIZED(401, "Unauthorized", MessageEnum.MSG_E00130),

		// 明確なドキュメントがなかったがおそらく発生する
		BACKEND_ERROR(500, "server_error", MessageEnum.MSG_E00136), // 500系エラー
		BAD_GATEWAY(502, "", MessageEnum.MSG_E00136),
		UNAVAILABLE(503, "", MessageEnum.MSG_E00136),
		GATEWAY_TIMEOUT(504, "", MessageEnum.MSG_E00136),
		;

		/** statusCode */
		private int statusCode;

		/** エラー内容 */
		private String error;

		/** メッセージEnum */
		private MessageEnum messageEnum;

		/**
		 * OAuthAPIErrorを取得する
		 * 
		 * @param statusCode
		 * @param error
		 * @return
		 */
		private static OAuthAPIError getEnum(int statusCode, String error) {
			if (statusCode <= 500 && statusCode > 600) {
				// 500系はerrorなしで判別可能
				return Stream.of(OAuthAPIError.values()).filter(e -> e.getStatusCode() == statusCode).findFirst().orElseThrow();
			}

			// それ以外はerrorで判別
			return Stream.of(OAuthAPIError.values()).filter(e -> e.error.equals(error)).findFirst().orElse(null);
		}

		/**
		 * OAuthAPIエラーを判別する
		 * 
		 * @param statusCode
		 * @param error
		 * @return
		 */
		public static MessageEnum getAppErrorMsgEnum(int statusCode, String error) {

			if (statusCode <= 500 && statusCode > 600) {
				// エラーコードが 500系の場合
				return MessageEnum.MSG_E00117;
			}

			if (StringUtils.isEmpty(error)) {
				return null;
			}

			OAuthAPIError oAuthAPIError = getEnum(statusCode, error);
			return oAuthAPIError != null ? oAuthAPIError.getMessageEnum() : null;
		}

	}

	/**
	 * GoogleDriveAPIで発生するエラー
	 */
	@Getter
	@AllArgsConstructor
	public enum DriveAPIError {

		BAD_REQUEST(400, "badRequest", null),
		INVALID_CREDENTIALS(401, "authError", MessageEnum.MSG_E00130),
		RATE_LIMIT_EXCEEDED(403, "rateLimitExceeded", MessageEnum.MSG_E00135), // 短時間でのAPIリクエスト制限を超えた
		APP_NOT_AUTHORIZED_TO_FILE(403, "appNotAuthorizedToFile", MessageEnum.MSG_E00131), // ファイルの所有者がアプリによるアクセスを制限している？
		INSUFFICIENT_FILE_PERMISSIONS(403, "insufficientFilePermissions", MessageEnum.MSG_E00132), // ファイル・フォルダに対する権限がない
		INSUFFICIENT_PARENT_PERMISSIONS(403, "insufficientParentPermissions", MessageEnum.MSG_E00132), // ファイル・フォルダに対する権限がない
		DOMAIN_POLICY(403, "domainPolicy", MessageEnum.MSG_E00133), // ドメインポリシーによるエラー？
		FILE_NOT_FOUND(404, "notFound", MessageEnum.MSG_E00134), // ファイルが共有されていない、完全に削除されている
		TOO_MANY_REQUESTS(429, "rateLimitExceeded", MessageEnum.MSG_E00135), // リクエストが多い場合
		BACKEND_ERROR(500, "backendError", MessageEnum.MSG_E00136), // 500系エラー
		BAD_GATEWAY(502, "", MessageEnum.MSG_E00136),
		UNAVAILABLE(503, "", MessageEnum.MSG_E00136),
		GATEWAY_TIMEOUT(504, "", MessageEnum.MSG_E00136),
		;

		/** statusCode */
		private int statusCode;

		/** エラー内容 */
		private String reason;

		/** メッセージEnum */
		private MessageEnum messageEnum;

		/**
		 * DriveAPIErrorEnumを取得する
		 *
		 * @param statusCode
		 * @param reason
		 * @return
		 */
		private static DriveAPIError getEnum(int statusCode, String reason) {
			if (statusCode <= 500 && statusCode > 600) {
				// 500系ならstatusCodeのみで判別できる(reasonが不明なので、そこだけで判別)
				return Stream.of(DriveAPIError.values()).filter(e -> e.getStatusCode() == statusCode).findFirst().orElseThrow();
			}

			// statusCodeとreasonでエラーを判別 (判別出来ない場合はNULL)
			return Stream.of(DriveAPIError.values()).filter(e -> e.getStatusCode() == statusCode && e.reason.equals(reason)).findFirst().orElse(null);
		}

		/**
		 * DriveAPIエラーを判別する
		 * 
		 * @param statusCode
		 * @param reason
		 * @return
		 */
		public static MessageEnum getAppErrorMsgEnum(int statusCode, String reason) {

			if (statusCode <= 500 && statusCode > 600) {
				// エラーコードが 500系の場合
				return MessageEnum.MSG_E00117;
			}

			if (StringUtils.isEmpty(reason)) {
				return null;
			}

			DriveAPIError driveAPIError = getEnum(statusCode, reason);
			return driveAPIError != null ? driveAPIError.getMessageEnum() : null;
		}

	}

}
