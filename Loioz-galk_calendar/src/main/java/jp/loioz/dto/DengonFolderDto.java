package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * カスタムフォルダツリー表示用のDtoクラス
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class DengonFolderDto {

	/* カスタムフォルダSEQ */
	private Long dengonFolderSeq;

	/* カスタムフォルダ名 */
	@Required
	@MaxDigit(max = 20)
	private String dengonFolderName;

	/* 親カスタムフォルダSEQ */
	private Long parentDengonFolderSeq;

	/* ごみ箱フラグ */
	private boolean trashedFlg;

}