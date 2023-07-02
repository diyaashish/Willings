package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 案件の顧客情報（名簿ID、名前、案件ステータス）Dto
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class PersonInfoForAnkenDto {

	/** 案件ID */
	Long ankenId;

	/** 名簿ID */
	Long personId;

	/** 顧客フラグ */
	String customerFlg;

	/** 顧問フラグ */
	String advisorFlg;

	/** 顧客名 */
	String customerName;

	/** 顧客種別 */
	String customerType;

	/** 案件ステータス */
	String ankenStatus;

	/**
	 * 表示用の名簿IDを取得します
	 * 
	 * @return
	 */
	public String getPersonIdDisp() {
		return CustomerId.of(personId).toString();
	}

	/**
	 * 表示用の案件ステータス名を取得します
	 * 
	 * @return
	 */
	public String getAnkenStatusDisp() {
		return AnkenStatus.of(ankenStatus).getVal();
	}
}
