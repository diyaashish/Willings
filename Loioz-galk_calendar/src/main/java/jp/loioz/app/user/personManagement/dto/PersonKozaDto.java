package jp.loioz.app.user.personManagement.dto;

import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 口座情報Dto
 */
@Data
public class PersonKozaDto {

	/** 銀行名 */
	private String ginkoName;

	/** 支店名 */
	private String shitenName;

	/** 支店番号 */
	private String shitenNo;

	/** 口座種類 */
	private KozaType kozaType;

	/** 口座番号 */
	private String kozaNo;

	/** 口座名義 */
	private String kozaName;

	/** 口座名義フリガナ */
	private String kozaNameKana;

	/** 口座備考 */
	private String kozaRemarks;

	/**
	 * 口座情報が空であるか判定する
	 *
	 * @return 空の場合はtrue
	 */
	public boolean isEmpty() {
		return kozaType == null
				&& StringUtils.isAllEmpty(
						ginkoName, shitenName, shitenNo,
						kozaNo, kozaName, kozaNameKana, kozaRemarks);
	}

}
