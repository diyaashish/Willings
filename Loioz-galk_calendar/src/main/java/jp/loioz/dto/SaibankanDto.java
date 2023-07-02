package jp.loioz.dto;

import org.thymeleaf.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 裁判官
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaibankanDto {

	/** 裁判官名 */
	private String saibankanName;
	
	/**
	 * Dtoが空かどうかを判定
	 */
	public boolean isEmpty() {
		return StringUtils.isEmpty(saibankanName);
	}
}
