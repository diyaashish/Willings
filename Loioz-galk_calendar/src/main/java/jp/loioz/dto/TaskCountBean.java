package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * タスク管理：サイドメニューで使用するメニューごとのタスク件数、未読件数Bean。
 *
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class TaskCountBean {

	/** カウント */
	private Integer count;

	/** ソート項目 */
	private Integer sortOrder;
}