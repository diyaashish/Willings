package jp.loioz.domain.value;

import java.util.Objects;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.PersonAttributeCd;
import jp.loioz.entity.TPersonEntity;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PersonAttribute {

	/** 顧客フラグ */
	private String customerFlg;

	/** 顧問フラグ */
	private String advisorFlg;

	/** 個人・法人・弁護士区分 */
	private String customerType;

	/**
	 * 引数から名簿属性を作成
	 * 
	 * @param customerFlg
	 * @param advisorFlg
	 * @param customerType
	 * @return
	 */
	public static PersonAttribute of(String customerFlg, String advisorFlg, String customerType) {
		return new PersonAttribute(customerFlg, advisorFlg, customerType);
	}

	/**
	 * エンティティから名簿属性を設定する
	 * 
	 * @param entity
	 * @return
	 */
	public static PersonAttribute fromEntity(TPersonEntity entity) {
		if (Objects.isNull(entity)) {
			return null;
		}
		return new PersonAttribute(entity.getCustomerFlg(), entity.getAdvisorFlg(), entity.getCustomerType());
	}

	/**
	 * 属性を取得
	 * 
	 * @return
	 */
	public PersonAttributeCd getCd() {
		// 顧客（個人／企業・団体）、顧問以外の場合
		if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(this.customerFlg) &&
				CommonConstant.SystemFlg.FLG_OFF.equalsByCode(this.advisorFlg) &&
				(CommonConstant.CustomerType.HOJIN.equalsByCode(this.customerType) ||
						CommonConstant.CustomerType.KOJIN.equalsByCode(this.customerType))) {
			// 顧客
			return CommonConstant.PersonAttributeCd.CUSTOMER;
		} else if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(this.customerFlg) &&
				CommonConstant.SystemFlg.FLG_ON.equalsByCode(this.advisorFlg) &&
				(CommonConstant.CustomerType.HOJIN.equalsByCode(this.customerType) ||
						CommonConstant.CustomerType.KOJIN.equalsByCode(this.customerType))) {
			// 顧問
			return CommonConstant.PersonAttributeCd.ADVISOR;
		} else if (CommonConstant.CustomerType.LAWYER.equalsByCode(this.customerType)) {
			// 弁護士
			return CommonConstant.PersonAttributeCd.LAWYER;
		} else {
			// その他
			return CommonConstant.PersonAttributeCd.OTHER;
		}
	}

	/**
	 * 属性名を取得
	 * 
	 * @return
	 */
	public String getName() {
		// 顧客（個人／企業・団体）、顧問以外の場合
		if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(this.customerFlg) &&
				CommonConstant.SystemFlg.FLG_OFF.equalsByCode(this.advisorFlg) &&
				(CommonConstant.CustomerType.HOJIN.equalsByCode(this.customerType) ||
						CommonConstant.CustomerType.KOJIN.equalsByCode(this.customerType))) {
			// 顧客
			return CommonConstant.PersonAttributeCd.CUSTOMER.getVal();
		} else if (CommonConstant.SystemFlg.FLG_ON.equalsByCode(this.customerFlg) &&
				CommonConstant.SystemFlg.FLG_ON.equalsByCode(this.advisorFlg) &&
				(CommonConstant.CustomerType.HOJIN.equalsByCode(this.customerType) ||
						CommonConstant.CustomerType.KOJIN.equalsByCode(this.customerType))) {
			// 顧問
			return CommonConstant.PersonAttributeCd.ADVISOR.getVal();
		} else if (CommonConstant.CustomerType.LAWYER.equalsByCode(this.customerType)) {
			// 弁護士
			return CommonConstant.PersonAttributeCd.LAWYER.getVal();
		} else {
			// その他
			return CommonConstant.PersonAttributeCd.OTHER.getVal();
		}
	}

}
