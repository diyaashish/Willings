package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 案件IDごとのタスク数
 *
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class NumberOfTaskPerAnkenIdBean {

	/** 案件ID */
	private Long ankenId;

	/** タスク数 */
	private Long numberOfTask;

}
