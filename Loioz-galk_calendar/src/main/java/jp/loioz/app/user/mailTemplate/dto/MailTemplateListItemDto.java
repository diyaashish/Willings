package jp.loioz.app.user.mailTemplate.dto;

import jp.loioz.common.constant.CommonConstant.MailTemplateType;
import lombok.Data;

/**
 * メールテンプレート
 */
@Data
public class MailTemplateListItemDto {

	/** メールテンプレートSEQ */
	private Long mailTemplateSeq;

	/** メールテンプレート種別 */
	private MailTemplateType mailTemplateType;

	/** タイトル */
	private String mailTemplateTitle;

	/** 既定使用フラグ */
	private boolean isDefaultUse;
}
