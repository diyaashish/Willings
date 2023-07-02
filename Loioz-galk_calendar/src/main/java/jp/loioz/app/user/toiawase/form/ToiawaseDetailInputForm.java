package jp.loioz.app.user.toiawase.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

/**
 * 問い合わせ詳細入力フォーム
 */
@Data
public class ToiawaseDetailInputForm {

	/** 問い合わせSEQ */
	@Required
	private Long toiawaseSeq;

	/** 本文 */
	@Required
	@MaxDigit(max = 10000)
	private String body;

	/** 添付ファイル */
	private List<MultipartFile> uploadFile;

}
