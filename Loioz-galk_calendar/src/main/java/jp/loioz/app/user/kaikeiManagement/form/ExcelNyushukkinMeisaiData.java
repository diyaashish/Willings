package jp.loioz.app.user.kaikeiManagement.form;

import org.springframework.beans.factory.annotation.Autowired;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

/**
 * 入出金明細一覧Excel出力データ
 */
@Data
public class ExcelNyushukkinMeisaiData {

	public static final int COLUMN_NUM = 11;

	/** 入出金項目Daoクラス */
	@Autowired
	private MNyushukkinKomokuDao mNyushukkinKomokuDao;

	/** 発生日 */
	private String hasseiDate;

	/** 入金額 */
	private String nyukin;

	/** 出金額 */
	private String shukkin;

	/** 入出金項目名 */
	private String nyushukkinType;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件分野 */
	private String ankenBunya;

	/** 案件名 */
	private String ankenName;

	/** 摘要 */
	private String tekiyo;

	/** 登録者 */
	private String createrName;

	/** 精算処理日 */
	private String seisanDate;

	/** 精算ID */
	private SeisanId seisanId;

	/**
	 * セルに設定する値の取得
	 *
	 * @param cellNo
	 * @param transitionType
	 * @return
	 */
	public String getCellValue(int cellNo, TransitionType transitionType) {
		switch (cellNo) {
		case 0:
			return hasseiDate;
		case 1:
			return nyukin;
		case 2:
			return shukkin;
		case 3:
			return nyushukkinType;
		case 4:
			if (TransitionType.CUSTOMER.equals(transitionType)) {
				return Long.toString(ankenId.asLong());
			} else if (TransitionType.ANKEN.equals(transitionType)) {
				return Long.toString(customerId.asLong());
			} else {
				// 想定外の場合 基本ここに来ない想定
				return CommonConstant.BLANK;
			}
		case 5:
			if (TransitionType.CUSTOMER.equals(transitionType)) {
				return StringUtils.isEmpty(ankenName) ? "(案件名未入力)" : ankenName;
			} else if (TransitionType.ANKEN.equals(transitionType)) {
				return customerName.toString();
			} else {
				// 想定外の場合 基本ここに来ない想定
				return CommonConstant.BLANK;
			}
		case 6:
			return tekiyo;
		case 7:
			return seisanDate;
		case 8:
			return seisanId.toString();
		default:
			return CommonConstant.BLANK;
		}
	}

}