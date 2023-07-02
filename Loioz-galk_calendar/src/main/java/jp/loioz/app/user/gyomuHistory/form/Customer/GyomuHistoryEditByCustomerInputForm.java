package jp.loioz.app.user.gyomuHistory.form.Customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.loioz.app.user.gyomuHistory.form.Common.CommonInputForm;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 顧客IDと案件ID以外はCommonInputFormに定義
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GyomuHistoryEditByCustomerInputForm extends CommonInputForm implements Serializable {

	/**
	 * シリアライズ処理用
	 * 
	 * <pre>
	 * オブジェクトをSessionに保存する為. 以下、参考にした資料
	 * https://qiita.com/esgr_dxx/items/346b0e08ffeece9df1de
	 * </pre>
	 **/
	private static final long serialVersionUID = 1L;

	/** 顧客ID */
	@Required
	private Long customerId;

	/** 紐づける案件ID(1:Nのため複数) */
	private List<Long> ankenIdList = new ArrayList<>();

}
