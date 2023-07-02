package jp.loioz.app.user.kaikeiManagement.form;

import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.domain.condition.KaikeiListMeisaiSearchCondition;
import jp.loioz.domain.condition.KaikeiListYoteiSearchCondition;
import lombok.Data;

/**
 * 会計管理の検索条件フォーム
 */
@Data
public class KaikeiListSearchForm {

	// ----------------------------------
	// 遷移元情報
	// ----------------------------------
	/** 案件ID */
	private Long transitionAnkenId;

	/** 顧客ID */
	private Long transitionCustomerId;

	/** 遷移元判定フラグ（0：案件管理から遷移、1：顧客管理から遷移 */
	private String transitionFlg;

	/** 完了済データ非表示フラグ（案件明細） */
	private String ankenMeisaiHiddenFlg;

	/** 処理済データ非表示フラグ（会計記録） */
	private String kaikeiKirokuHiddenFlg;

	/** 処理済データ非表示フラグ（入出金予定） */
	private String yoteiHiddenFlg;

	/** 精算書タブ表示 */
	private Boolean isSeisanshoTab = false;

	/**
	 * 会計画面_入出金明細の検索条件
	 * 
	 * @return
	 */
	public KaikeiListMeisaiSearchCondition toKaikeiListMeisaiSearchCondition() {
		return KaikeiListMeisaiSearchCondition.builder()
				.transitionAnkenId(this.transitionAnkenId)
				.transitionCustomerId(this.transitionCustomerId)
				.isAnkenView(SystemFlg.FLG_OFF.equalsByCode(this.transitionFlg))
				.isCustomerView(SystemFlg.FLG_ON.equalsByCode(this.transitionFlg))
				.completedExcludeAnkenStatus(AnkenStatus.getCompletedStatusCd())
				.isCompleted(SystemFlg.codeToBoolean(this.kaikeiKirokuHiddenFlg))
				.build();
	}

	/**
	 * 会計画面_入出金予定の検索条件
	 * 
	 * @return
	 */
	public KaikeiListYoteiSearchCondition toKaikeiListYoteiSearchCondition() {
		return KaikeiListYoteiSearchCondition.builder()
				.transitionAnkenId(this.transitionAnkenId)
				.transitionCustomerId(this.transitionCustomerId)
				.isAnkenView(SystemFlg.FLG_OFF.equalsByCode(this.transitionFlg))
				.isCustomerView(SystemFlg.FLG_ON.equalsByCode(this.transitionFlg))
				.completedExcludeAnkenStatus(AnkenStatus.getCompletedStatusCd())
				.isCompleted(SystemFlg.codeToBoolean(this.yoteiHiddenFlg))
				.build();
	}

	/**
	 * 遷移元フラグから遷移元を判定しEnumで返却
	 */
	public TransitionType getTransition() {

		if (SystemFlg.codeToBoolean(this.transitionFlg)) {
			return TransitionType.CUSTOMER;
		} else {
			return TransitionType.ANKEN;
		}
	}

}
