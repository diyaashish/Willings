package jp.loioz.app.user.info.form;

import org.seasar.doma.Entity;
import lombok.Data;

@Entity
@Data
public class InfoListForAccountForm {

	/** お知らせSEQ */
	private Long infoSeq;

	/** お知らせ区分 */
	private String infoType;

	/** お知らせタイトル */
	private String infoTitle;

	/** お知らせ本文 */
	private String infoBody;

	/** お知らせ更新日時 */
	private String updatedAt;

	/** アカウントSEQ */
	private Long accountSeq;

}
