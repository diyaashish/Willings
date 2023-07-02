package jp.loioz.app.user.kaikeiManagement.form;

import java.util.List;

import javax.validation.Valid;

import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.dto.AnkenMeisaiDto;
import jp.loioz.dto.KaikeiManagementRelationDto;
import lombok.Data;

/**
 * 案件明細のフォームクラス
 */
@Data
public class AnkenMeisaiEditForm {

	/** 更新フラグ */
	private boolean updateFlg = false;

	/** 更新不可フラグ */
	private boolean disabledFlg = false;

	/** 精算済の旨のメッセージ表示判定（完了状態の場合はそちらを優先するためfalseになる） */
	private boolean isShowSeisanCompletedMsg = false;

	/** 案件ステータスが完了である旨のメッセージ表示判定 */
	private boolean isShowAnkenCompletedMsg = false;
	
	/** 精算書で利用されている旨のメッセージ表示判定 */
	private boolean isShowUsedSeisanshoMsg = false;

	/** 遷移元判定フラグ */
	private String transitionFlg;

	/** 遷移元案件ID */
	private Long transitionAnkenId;

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	/** 会計管理の関連情報リスト */
	private List<KaikeiManagementRelationDto> kaikeiManagementList;

	/** 案件明細Dto */
	@Valid
	private AnkenMeisaiDto ankenMeisaiDto = new AnkenMeisaiDto();

	/** 源泉徴収の計算式・金額のリスト */
	private List<String> kingakuList;

	/** 源泉徴収項目を表示するかどうか */
	private boolean isDisplayGensenchoshu = false;

	/**
	 * 遷移元の判定がFLG(String)で行われているので判定用Enumで返却する
	 */
	public TransitionType getTransitionType() {
		if (SystemFlg.codeToBoolean(this.transitionFlg)) {
			return TransitionType.CUSTOMER;
		} else {
			return TransitionType.ANKEN;
		}
	}
}
