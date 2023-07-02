package jp.loioz.dto;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.common.constant.ChohyoWordConstant;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.OutputConstant;
import lombok.Data;

@Data
public class SaibanTantoAccountDto {

	/** 裁判SEQ */
	private Long saibanSeq;

	/** アカウントSEQ */
	private Long accountSeq;

	/** アカウント名 */
	private String accountName;

	/** アカウント名（姓） */
	private String accountNameSei;

	/** アカウント名（名） */
	private String accountNameMei;

	/** アカウント種別 */
	private String accountType;

	/** 裁判担当フラグ */
	private String tantoType;

	/** 担当種別枝番 */
	private Long tantoTypeBranchNo;

	/** 裁判主担当フラグ */
	private String saibanMainTantoFlg;

	/**
	 * 裁判に紐づく弁護士、事務を表示
	 *
	 * <pre>
	 * 主に帳票の担当者
	 * </pre>
	 *
	 * @return
	 */
	public List<String> getTantoDispList() {
		List<String> list = new ArrayList<String>();

		// 初期値は弁護士として設定
		String dispTantoTypeName = OutputConstant.LAWYER;
		String dispAccountName = accountName;
		String dispTanto = CommonConstant.BLANK;

		if (CommonConstant.TantoType.JIMU.equalsByCode(tantoType)) {
			// 事務員は、名字で表示
			dispAccountName = accountNameSei;
			dispTantoTypeName = OutputConstant.TANTO_JIMU;

		} else {
			// 売上計上先、担当弁護士

			// 主担当の場合は(担当)を表記
			if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(saibanMainTantoFlg)) {
				dispTanto = ChohyoWordConstant.KAKKO_TANTO;
			}
		}

		list.add(dispTantoTypeName);
		list.add(dispAccountName);
		list.add(dispTanto);
		return list;
	}

}
