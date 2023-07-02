package jp.loioz.dto;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

/**
 * 最小値と最大値を保持するDto
 */
@Entity
@Data
public class MinMaxDto {

	@Column(name = "min")
	Integer min;

	@Column(name = "max")
	Integer max;

}
