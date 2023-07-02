package jp.loioz.dto;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

@Data
public class ExcelSofushoDto {

	/** 作成有無フラグ */
	private Boolean createFlg = false;

	/** 画面表示ID */
	private String id;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 案件分野 */
	private String ankenBunya;

	/** お客様名1 **/
	private String name1;

	/** お客様名2 **/
	private String name2;

	/** お客様郵便番号 */
	private String zipCode;

	/** お客様住所１ */
	private String Address1;

	/** お客様住所２ */
	private String Address2;

	/** 事務所郵便番号 */
	private String tenantZipCode;

	/** 事務所住所１ */
	private String tenantAddress1;

	/** 事務所住所２ */
	private String tenantAddress2;

	/** 事務所名 */
	private String tenantName;

	/** 事務所電話番号 */
	private String tenantTelNo;

	/** 事務所FAX番号 */
	private String tenantFaxNo;

	/** 弁護士リスト */
	private List<String> tantoBengoshiList;

	/** 事務所側口座名 */
	private String jimushoKozaName;

	/** 事務所側銀行名 */
	private String jimushoKozaGinkoName;

	/** 事務所側支店名 */
	private String jimushoKozashitenName;

	/** 事務所側支店番号 */
	private String jimushoKozashitenNo;

	/** 事務所側口座種類 */
	private String jimushoKozaType;

	/** 事務所側口座番号 */
	private String jimushokozaNo;

	/** 案件担当（弁護士、事務） */
	private List<AnkenTantoAccountDto> ankenTanto = new ArrayList<AnkenTantoAccountDto>();

	/** 作成シートリスト */
	// {sheet番号 , <'シート名'>}
	private List<String[]> statusList;

}
