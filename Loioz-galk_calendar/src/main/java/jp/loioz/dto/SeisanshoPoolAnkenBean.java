package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 精算書作成画面：再計算のリクエスト情報
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class SeisanshoPoolAnkenBean{

	/** 案件ID */
	private Long ankenId;
	
	/** 案件名 */
	private String ankenName;
	
	/** 分野ID */
	private Long bunyaId;
}
