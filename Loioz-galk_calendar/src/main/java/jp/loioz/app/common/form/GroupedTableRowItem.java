package jp.loioz.app.common.form;

import java.util.List;

import org.springframework.util.CollectionUtils;

import jp.loioz.common.utility.data.GroupParent;
import lombok.Data;

/**
 * 縦方向にセル結合されたテーブルレイアウトの行データ
 *
 * @param <T> 下位グループクラス
 */
@Data
public class GroupedTableRowItem<T extends TableRowItem> implements TableRowItem, GroupParent<T> {

	/** 下位グループ情報 */
	private List<T> children;

	@Override
	public long getRowSize() {
		if (CollectionUtils.isEmpty(children)) {
			// 下位グループ情報が存在しない場合は1行
			return 1;
		}
		// 下位グループ情報の行数の合計
		return children.stream()
				.mapToLong(TableRowItem::getRowSize)
				.sum();
	}
}
