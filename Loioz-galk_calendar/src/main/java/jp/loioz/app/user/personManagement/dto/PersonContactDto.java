package jp.loioz.app.user.personManagement.dto;

import jp.loioz.common.constant.CommonConstant.ContactCategory;
import jp.loioz.common.constant.CommonConstant.ContactType;
import jp.loioz.common.utility.StringUtils;
import lombok.Builder;
import lombok.Data;

/**
 * 連絡先情報Dto
 */
@Data
@Builder
public class PersonContactDto {

	/** 電話の表示名 */
	public static final String TEL_TITLE = ContactCategory.TEL.getVal();
	/** FAXの表示名 */
	public static final String FAX_TITLE = ContactCategory.FAX.getVal();
	/** メールアドレスの表示名 */
	public static final String EMAIL_TITLE = ContactCategory.EMAIL.getVal();
	
	/** 連絡先テーブルのレコード連番 */
	private Long contactSeq;
	
	/** カテゴリ（電話、FAXなど） */
	private ContactCategory category;
	
	/** 区分（自宅、勤務先など） */
	private ContactType type;

	/** 優先フラグ */
	private boolean yusenFlg;

	/** 連絡先 */
	private String value;

	/** 備考 */
	private String remarks;

	/**
	 * 連絡先が空であるか判定する
	 *
	 * @return 空の場合はtrue
	 */
	public boolean isEmpty() {
		return type == null && StringUtils.isEmpty(value);
	}
}
