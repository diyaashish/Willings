package jp.loioz.app.common.form;

/**
 * テーブルレイアウトの行データ
 */
public interface TableRowItem {

	/**
	 * このデータを表示するために必要な行数を取得する
	 *
	 * @return 行数
	 */
	public default long getRowSize() {
		return 1;
	}
}
