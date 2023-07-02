package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 帳票カテゴリ用のDto
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class ChohyoCategoryDto {

}