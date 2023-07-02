package jp.loioz.app.user.gyomuHistory.form.Anken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.loioz.app.user.gyomuHistory.form.Common.CommonInputForm;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GyomuHistoryEditByAnkenInputForm extends CommonInputForm implements Serializable {

	/**
	 * シリアライズ処理用
	 * 
	 * <pre>
	 * オブジェクトをSessionに保存する為 以下、参考資料
	 * https://qiita.com/esgr_dxx/items/346b0e08ffeece9df1de
	 * </pre>
	 **/
	private static final long serialVersionUID = 1L;

	/** 案件ID */
	@Required
	private Long ankenId;

	/** 紐づける顧客ID */
	private List<Long> customerIdList = new ArrayList<>();

}
