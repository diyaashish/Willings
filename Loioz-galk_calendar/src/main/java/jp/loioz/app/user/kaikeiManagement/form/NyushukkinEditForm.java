package jp.loioz.app.user.kaikeiManagement.form;

import java.util.List;

import javax.validation.Valid;

import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.dto.KaikeiKirokuDto;
import jp.loioz.dto.KaikeiManagementRelationDto;
import jp.loioz.dto.NyushukkinKomokuListDto;
import lombok.Data;

/**
 * 入出金のフォームクラス
 */
@Data
public class NyushukkinEditForm {

	/** 更新フラグ */
	private boolean updateFlg = false;

	/** 更新不可フラグ */
	private boolean disabledFlg = false;

	/** 削除可能かのフラグ */
	private boolean isAbleDeleteFlg = false;

	/** 入出金変更不可フラグ */
	private boolean nyushukkinDisabledFlg = false;
	
	/** 精算済の旨のメッセージ表示判定（完了状態の場合はそちらを優先するためfalseになる） */
	private boolean isShowSeisanCompletedMsg = false;
	
	/** 案件ステータスが完了である旨のメッセージ表示判定 */
	private boolean isShowAnkenCompletedMsg = false;

	/** 精算書で利用されている旨のメッセージ表示判定 */
	private boolean isShowUsedSeisanshoMsg = false;
	
	/** 他案件からの振替入金である旨のメッセージ表示判定 */
	private boolean isShowNyukinPoolMsg = false;
	
	/** 他案件への振替出金である旨のメッセージ表示判定 */
	private boolean isShowShukkinPoolMsg = false;
	
	/** 遷移元判定フラグ */
	private String transitionFlg;

	/** 遷移元案件ID */
	private Long transitionAnkenId;

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	/** 課税かどうか */
	private boolean isTaxation;

	/** 入金項目リスト */
	private List<NyushukkinKomokuListDto> nyukinKomokuList;

	/** 出金項目リスト */
	private List<NyushukkinKomokuListDto> shukkinKomokuList;

	/** 会計管理の関連情報リスト */
	private List<KaikeiManagementRelationDto> kaikeiManagementList;

	/** 会計記録Dto */
	@Valid
	private KaikeiKirokuDto kaikeiKirokuDto = new KaikeiKirokuDto();

	/**
	 * 遷移元の判定がFLG(String)で行われているので判定用Enumで返却する
	 *
	 * <br>
	 * TODO タイミングをみて、TransitionFlg(String)をTransitionType(Enum)に変更する
	 */
	public TransitionType getTransitionType() {
		if (SystemFlg.codeToBoolean(this.transitionFlg)) {
			return TransitionType.CUSTOMER;
		} else {
			return TransitionType.ANKEN;
		}
	}
}
