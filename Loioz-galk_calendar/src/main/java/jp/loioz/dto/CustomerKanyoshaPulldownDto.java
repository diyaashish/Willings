package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 預り品に紐づく情報を管理するDtoクラス
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class CustomerKanyoshaPulldownDto {

	// 名前
	private String name;

	// ID
	private Long id;
}