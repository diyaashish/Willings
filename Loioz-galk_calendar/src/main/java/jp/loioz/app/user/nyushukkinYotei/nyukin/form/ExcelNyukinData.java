package jp.loioz.app.user.nyushukkinYotei.nyukin.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExcelNyukinData {

	/** 顧客ID */
	private String customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private String ankenId;

	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private String bunya;

	/** 担当弁護士 */
	private String dispTantoLawer;

	/** 担当事務 */
	private String dispTantoJimu;

	/** 精算先 */
	private String seisanSaki;

	/** 入金口座 */
	private String nyukinKoza;

	/** 入金予定日 */
	private String nyukinYoteiDate;

	/** 入金予定額 */
	private String nyukinYoteiGaku;

	/** 残金 */
	private String zankin;

	/** 精算ID */
	private String seisanId;

	/** 状態 */
	private String jotai;

}
