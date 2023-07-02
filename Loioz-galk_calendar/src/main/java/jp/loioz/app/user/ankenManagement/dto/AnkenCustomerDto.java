package jp.loioz.app.user.ankenManagement.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.KoryuStatus;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import lombok.Data;

/**
 * 案件顧客情報一覧
 */
@Data
public class AnkenCustomerDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 名簿ID */
	private Long personId;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 顧客タイプ */
	private CustomerType customerType;

	/** 名簿属性 */
	private PersonAttribute personAttribute;

	/** ステータス */
	private AnkenStatus status;

	/** 受任日 */
	private String juninDate;

	/** 事件処理完了日 */
	private String jikenshoriKanryoDate;

	/** 精算完了日 */
	private String seisanKanryoDate;

	/** 完了フラグ */
	private boolean isCompleted;

	/** 刑事区分 */

	/** 勾留ステータス */
	private KoryuStatus koryuStatus;

	/** 顧客-事件情報 */
	List<AnkenCustomerJikenDto> ankenCustomerJikenDtoList = Collections.emptyList();

	/** 顧客-接見情報 */
	List<AnkenCustomerSekkenDto> ankenCustomerSekkenDtoList = Collections.emptyList();

	/** 顧客-在監情報 */
	List<AnkenCustomerZaikanDto> ankenCustomerZaikanDtoList = Collections.emptyList();

	/**
	 * 案件ステータスが相談中か不受任か返す
	 * 
	 * @return
	 */
	public boolean isSodanFujyunin() {
		boolean rc = false;
		if (CommonConstant.AnkenStatus.SODAN.equals(status) || CommonConstant.AnkenStatus.FUJUNIN.equals(status)) {
			rc = true;
		}
		return rc;
	}

}
