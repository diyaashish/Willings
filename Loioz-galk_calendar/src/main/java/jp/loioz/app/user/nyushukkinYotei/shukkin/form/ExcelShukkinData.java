package jp.loioz.app.user.nyushukkinYotei.shukkin.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExcelShukkinData {

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

	/** 出金口座 */
	private String shukkinKoza;

	/** 支払先 */
	private String shiharaiSaki;

	/** 支払期日 */
	private String shiharaiLimitDate;

	/** 精算額 */
	private String seisanGaku;

	/** 精算ID */
	private String seisanId;

	/** 状態 */
	private String jotai;

}
