package jp.loioz.domain.value;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TPersonEntity;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * 名前Domain
 */
@Value
@RequiredArgsConstructor
public class PersonName {

	/** 姓 */
	String sei;

	/** 名 */
	String mei;

	/** 姓(かな) */
	String seiKana;

	/** 名(かな) */
	String meiKana;

	/**
	 * AccountEntityから名前Domainを作成
	 *
	 * @param accountEntity
	 * @return アカウント名
	 */
	public static PersonName fromEntity(MAccountEntity accountEntity) {
		return new PersonName(
				accountEntity.getAccountNameSei(),
				accountEntity.getAccountNameMei(),
				accountEntity.getAccountNameSeiKana(),
				accountEntity.getAccountNameMeiKana());
	}

	/**
	 * TPersonEntityから名前Domainを作成
	 *
	 * @param tPersonEntity
	 * @return 顧客名
	 */
	public static PersonName fromEntity(TPersonEntity tPersonEntity) {
		return new PersonName(
				tPersonEntity.getCustomerNameSei(),
				tPersonEntity.getCustomerNameMei(),
				tPersonEntity.getCustomerNameSeiKana(),
				tPersonEntity.getCustomerNameMeiKana());
	}

	/**
	 * /* 姓名の取得
	 *
	 * @return
	 */
	public String getName() {
		String name = String.format(
				"%s%s%s",
				StringUtils.null2blank(this.getSei()),
				CommonConstant.SPACE,
				StringUtils.null2blank(this.getMei()));
		return StringUtils.trim(name);
	}

	/**
	 * /* 姓名の取得（全角スペース）
	 *
	 * @return
	 */
	public String getNameFullSpace() {
		String name = String.format(
				"%s%s%s",
				StringUtils.null2blank(this.getSei()),
				CommonConstant.FULL_SPACE,
				StringUtils.null2blank(this.getMei()));
		return StringUtils.trim(name);
	}

	/**
	 * 姓名(かな)の取得
	 *
	 * @return
	 */
	public String getNameKana() {
		String nameKana = String.format(
				"%s%s%s",
				StringUtils.null2blank(this.getSeiKana()),
				CommonConstant.SPACE,
				StringUtils.null2blank(this.getMeiKana()));
		return StringUtils.trim(nameKana);
	}

	/**
	 * 姓名(かな)の取得
	 *
	 * @return
	 */
	public String getNameKanaFullSpace() {
		String nameKana = String.format(
				"%s%s%s",
				StringUtils.null2blank(this.getSeiKana()),
				CommonConstant.FULL_SPACE,
				StringUtils.null2blank(this.getMeiKana()));
		return StringUtils.trim(nameKana);
	}

}
