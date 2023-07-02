package jp.loioz.app.common.api.box.constants;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.utility.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class BoxConstants {

	/** BoxのURL */
	public static final String BOX_APP_BASE_URL = "https://app.box.com/folder/";

	/** Box ※Box側のシステムにも登録しているため、値は変えないこと */
	public static final String BOX_AUTH_GATEWAY_REDIRECT_PATH = "boxAuthGatewayRedirect";

	@Getter
	@AllArgsConstructor
	public enum BoxAuthRequestCode implements DefaultEnum {

		/** ファイル管理設定画面からのBoxを選択した時 */
		SYSTEM_CONNECT("1", "システム連携"),
		/** アカウント画面から連携を行う時 */
		USER_CONNECT("2", "ユーザー連携");

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
		public static BoxAuthRequestCode of(String cd) {
			return DefaultEnum.getEnum(BoxAuthRequestCode.class, cd);
		}
	}

	/**
	 * ロイオズで発生しうるBox側のエラー
	 * 
	 * コラボレーションや、アップロードは行わないので、
	 * 
	 * 参考元
	 * ・https://ja.developer.box.com/guides/api-calls/permissions-and-errors/common-errors/
	 * 
	 */
	@Getter
	@AllArgsConstructor
	public enum BoxAPIErrorResponse {

		// エラーコード400
		/** APIリクエストでの必須パラメータが不足している */
		BAD_REQUEST(400, "bad_request", null),
		/** 空のフォルダの削除処理時に、オプションが設定されていない場合 */
		FOLDER_NOT_EMPTY(400, "folder_not_empty", null),
		/** 基本的にはアクセストークン周りの期限切れ ※その他でも発生するっぽいが、エラーメッセージが変わるだけで判別できないっぽい */
		INVALID_GRANT(400, "invalid_grant", MessageEnum.MSG_E00105),
		/** 不正なアクセストークン (失効処理で確認) */
		INVALID_TOKEN(400, "invalid_token", MessageEnum.MSG_E00105),
		/** 指定された制限値を超えた数値のリクエスト */
		INVALID_LIMIT(400, "invalid_limit", null),
		/** 指定されたオフセット値に有効な数値ではない場合 TODO どうやれば発生する？ */
		INVALID_OFFSET(400, "invalid_offset", null),
		/** ファイル名、フォルダ名に無効な項目名 */
		ITEM_NAME_INVALID(400, "item_name_invalid", null),
		/** ファイル名、フォルダ名の桁数上限を超える場合(255文字) */
		ITEM_NAME_TOO_LONG(400, "item_name_too_long", null),
		/** Boxアカウントのパスワード変更が必要な場合 */
		PASSWORD_RESET_REQUIRED(400, "password_reset_required", MessageEnum.MSG_E00107),
		/** 同期したファイル・フォルダの移動エラー ※フォルダの移動や削除は行わないため、おそらく不要 */
		SYNC_ITEM_MOVE_FAILURE(400, "sync_item_move_failure", null),
		/**
		 * ビジネスプランの場合、カスタム利用規約みたいなのを設定できるらしく、同意していないユーザーは操作が出来ない。この利用規約をユーザーが同意していない場合
		 * https://support.box.com/hc/en-us/articles/360044192733-Using-Custom-Terms-Of-Service
		 */
		TERMS_OF_SERVICE_REQUIRED(400, "terms_of_service_required", MessageEnum.MSG_E00108),

		// エラーコード401
		/** 承認されていないトークンの利用時 */
		UNAUTHORIZED(401, "unauthorized", MessageEnum.MSG_E00105),

		// エラーコード403
		/** 権限以上のAPIリクエストを行った場合 */
		ACCESS_DENIED_INSUFFICIENT_PERMISSIONS(403, "access_denied_insufficient_permissions", MessageEnum.MSG_E00110),
		/** ロックされた項目へのアクセス */
		ACCESS_DENIED_ITEM_LOCKED(403, "access_denied_item_locked", MessageEnum.MSG_E00110),
		/** 未承認の場所からアクセスエラー TODO おそらBoxEnterpriseのアプリトークンを管理者の許可にチェックがついているが、承認されないケース */
		ACCESS_FROM_LOCATION_BLOCKED(403, "access_from_location_blocked", MessageEnum.MSG_E00111),
		/** Box Shieldポリシーによるアクセス拒否 */
		FORBIDDEN_BY_POLICY(403, "forbidden_by_policy", MessageEnum.MSG_E00109),
		/** ストレージサイズの上限に達しました */
		STORAGE_LIMIT_EXCEEDED(403, "storage_limit_exceeded", MessageEnum.MSG_E00112),
		/** メールの認証が未完了 */
		USER_EMAIL_CONFIRMATION_REQUIRED(403, "user_email_confirmation_required", MessageEnum.MSG_E00106),

		// エラーコード404
		/** リソースが見つかりません */
		NOT_FOUND(404, "not_found", MessageEnum.MSG_E00113),
		/** 対象がゴミ箱に存在するため、変更できません */
		TRASHED(404, "trashed", MessageEnum.MSG_E00113),

		// エラーコード409
		/** 作成するリソースがすでに存在する場合 */
		CONFLICT(409, "conflict", MessageEnum.MSG_E00114),
		/** 同じ名前のリソースがすでに存在する場合 */
		ITEM_NAME_IN_USE(409, "item_name_in_use", MessageEnum.MSG_E00114),
		/** 重複する2つのリクエストが行われた場合 */
		NAME_TEMPORARILY_RESERVED(409, "name_temporarily_reserved", MessageEnum.MSG_E00115),
		/** 他の進行中の操作によって操作がブロックされた場合 */
		OPERATION_BLOCKED_TEMPORARY(409, "operation_blocked_temporary", MessageEnum.MSG_E00115),

		// エラーコード412
		/** リソースが変更されている場合 */
		PRECONDITION_FAILED(412, "precondition_failed", MessageEnum.MSG_E00115),
		/** リソースが変更されている場合 */
		SYNC_STATE_PRECONDITION_FAILED(412, "sync_state_precondition_failed", MessageEnum.MSG_E00115),

		// エラーコード429
		/**
		 * APIのリクエスト上限を超えた場合
		 * ・https://ja.developer.box.com/guides/api-calls/permissions-and-errors/rate-limits/
		 */
		RATE_LIMIT_EXCEEDED(429, "rate_limit_exceeded", MessageEnum.MSG_E00116),

		// エラーコード500
		/** Boxサーバーの内部エラー */
		INTERNAL_SERVER_ERROR(500, "internal_server_error", MessageEnum.MSG_E00117),

		// エラーコード502
		/** Boxサーバー通信エラー */
		BAD_GATEWAY(502, "bad_gateway", MessageEnum.MSG_E00117),

		// エラーコード503
		/** 現在利用不可能な場合のエラー */
		UNAVAILABLE(503, "unavailable", MessageEnum.MSG_E00117),
		;

		/** エラーコード */
		private int status;

		/** responseBodyのerrorパラメータ */
		private String code;

		/** メッセージEnum */
		private MessageEnum msgEnum;

		/**
		 * エラーパラメータからEnum値を取得する
		 * 
		 * @param error
		 * @return
		 */
		public static BoxAPIErrorResponse getEnum(String error) {
			return Arrays.asList(BoxAPIErrorResponse.values()).stream().filter(e -> e.getCode().equals(error)).findFirst().orElse(null);
		}

		/**
		 * BoxAPIExceptionのresponseCodeとresponseBodyからエラーメッセージのEnumを取得する。
		 * 
		 * @param errorCode
		 * @param responseBody
		 * @return
		 */
		public static MessageEnum getAppErrorMsgEnum(int errorCode, String responseBody) {

			if (errorCode <= 500 && errorCode > 600) {
				// エラーコードが 500系の場合
				return MessageEnum.MSG_E00117;
			}

			if (errorCode == 401) {
				return MessageEnum.MSG_E00105;
			}

			String code = "";
			String error = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode node = mapper.readValue(responseBody, JsonNode.class);
				code = node.get("code") != null ? node.get("code").asText() : "";
				error = node.get("error") != null ? node.get("error").asText() : "";
			} catch (IOException e) {
				return null;
			}

			if (StringUtils.isAllEmpty(code, error)) {
				return null;
			}

			String errorId = code.length() > error.length() ? code : error;
			BoxAPIErrorResponse boxAPIErrorResponse = BoxAPIErrorResponse.getEnum(errorId);
			return boxAPIErrorResponse != null ? boxAPIErrorResponse.getMsgEnum() : null;
		}

	}

}
