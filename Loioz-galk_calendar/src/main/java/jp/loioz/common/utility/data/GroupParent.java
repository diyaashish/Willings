package jp.loioz.common.utility.data;

import java.util.List;

/**
 * 一対多の親子関係を持つデータ
 *
 * @param <T> 子要素クラス
 */
public interface GroupParent<T> {

	/**
	 * 子要素を取得する
	 *
	 * @return 子要素
	 */
	List<T> getChildren();

	/**
	 * 子要素を設定する
	 *
	 * @param children 子要素
	 */
	void setChildren(List<T> children);

}
