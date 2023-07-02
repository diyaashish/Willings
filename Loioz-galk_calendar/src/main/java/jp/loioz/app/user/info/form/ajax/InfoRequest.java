package jp.loioz.app.user.info.form.ajax;

import lombok.Data;

/**
 * お知らせを取得するAPIのリクエスト情報
 */
@Data
public class InfoRequest {

	/** アカウントSEQ */
	private long accountSeq;

	/** お知らせSEQ */
	private long infoMgtSeq;

}
