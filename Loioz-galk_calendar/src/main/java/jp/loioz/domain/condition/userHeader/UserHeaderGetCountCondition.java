package jp.loioz.domain.condition.userHeader;

/**
 * ヘッダー検索：件数取得用パラメータのインタフェース
 */
public interface UserHeaderGetCountCondition {

	/** 件数を取得する場合 */
	public void setGetCount(boolean isGetCount);

	/** 取得上限を自身で設定する場合の件数 */
	public void setLimitCount(Integer limitCount);

	/**
	 * 件数取得処理のセットアップ処理
	 * 
	 * @param limitCount
	 */
	default void setUpCountGetCondition(Integer limitCount) {
		setGetCount(true);
		setLimitCount(limitCount);
	}

}
