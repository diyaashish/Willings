package jp.loioz.dto;

import jp.loioz.common.constant.CommonConstant.HoshuHasuType;
import jp.loioz.common.constant.CommonConstant.TaxHasuType;
import jp.loioz.common.validation.annotation.EnumType;
import lombok.Data;

/**
 * 会計データの設定画面<br>
 * テナント情報（会計設定情報）のDtoクラス
 */
@Data
public class TenantKaikeiEditDto {

	/** 消費税の端数処理の方法 */
	@EnumType(TaxHasuType.class)
	private String taxHasuType;

	/** 報酬の端数処理の方法 */
	@EnumType(HoshuHasuType.class)
	private String HoshuHasuType;

}