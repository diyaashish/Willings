package jp.loioz.app.common;

/**
 * サービスの基底クラス
 */
public abstract class DefaultService {

	public static final String NOT_INSERTED_EXPECTED_COUNT_DATA = "想定した件数のデータが登録されませんでした。";
	public static final String NOT_UPDATED_EXPECTED_COUNT_DATA = "想定した件数のデータが更新されませんでした。";
	public static final String NOT_DELETED_EXPECTED_COUNT_DATA = "想定した件数のデータが削除されませんでした。";
	public static final String ALREADY_UPDATED_TO_OTHER_USER = "既に他のユーザーに更新されています。";
	public static final String ALREADY_NO_DATA = "データが存在しませんでした。";

}
