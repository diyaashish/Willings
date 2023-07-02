package jp.loioz.app.user.personManagement.dto;

import java.util.Arrays;

import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 住所情報Dto
 */
@Data
public class PersonAddressDto {

	/** 基本住所の表示名（個人） */
	public static final String BASE_ADDRESS_TITLE_KOJIN = "居住地";
	/** 基本住所の表示名（企業・団体） */
	public static final String BASE_ADDRESS_TITLE_HOJIN = "所在地";
	/** 基本住所の表示名（弁護士） */
	public static final String BASE_ADDRESS_TITLE_LAWYER = "所在地";
	/** 登録住所の表示名（個人） */
	public static final String TOUROKU_ADDRESS_TITLE_KOJIN = "住民票登録地";
	/** 登録住所の表示名（企業・団体） */
	public static final String TOUROKU_ADDRESS_TITLE_HOJIN = "登記簿登録地";
	/** 郵送先住所の表示名 */
	public static final String TRANSFER_ADDRESS_TITLE = "郵送先住所";
	/** その他住所の表示名 */
	public static final String OTHER_ADDRESS_TITLE = "その他住所";
	/** その他住所の簡易表示名 */
	public static final String OTHER_ADDRESS_SIMPLE_TITLE = "その他";

	/** 顧客タイプ */
	private CustomerType customerType;

	/** 居住地/所在地かどうか */
	private boolean isBaseAddress;

	/** 住民票/登記簿の登録地かどうか */
	private boolean isTourokuAddress;

	/** 郵送先住所かどうか */
	private boolean isTransferAddress;

	/** その他住所（旧住所）Seq */
	private Long oldAddressSeq;

	/** 郵便番号 */
	private String zipCode;

	/** 住所１ */
	private String address1;

	/** 住所２ */
	private String address2;

	/** 備考 */
	private String remarks;

	/** 郵送方法 */
	private String transferTypeName;

	// 表示では利用せず、更新時に利用するプロパティ

	/** 住民票/登記簿の登録地かどうか（更新前の値） */
	private boolean isTourokuAddressBeforeUpdate;

	/** 郵送先住所かどうか（更新前の値） */
	private boolean isTransferAddressBeforeUpdate;

	/**
	 * 住所が空であるか判定する
	 *
	 * @return 空の場合はtrue
	 */
	public boolean isEmpty() {
		return StringUtils.isAllEmpty(zipCode, address1, address2, remarks);
	}

	/**
	 * 住所一覧の住所タイトル部分（住所タイプ表示）を取得する
	 * 
	 * @return
	 */
	public String getAddressTitle() {

		StringBuilder addressType = new StringBuilder();
		if (this.customerType == CustomerType.KOJIN) {
			// 個人の場合
			if (this.isBaseAddress) {
				addressType.append(BASE_ADDRESS_TITLE_KOJIN);
			} else {
				addressType.append(OTHER_ADDRESS_SIMPLE_TITLE);
			}

			if (this.isTourokuAddress) {
				addressType.append("／");
				addressType.append(TOUROKU_ADDRESS_TITLE_KOJIN);
			}
		} else if (this.customerType == CustomerType.HOJIN) {
			// 企業・団体の場合
			if (this.isBaseAddress) {
				addressType.append(BASE_ADDRESS_TITLE_HOJIN);
			} else {
				addressType.append(OTHER_ADDRESS_SIMPLE_TITLE);
			}

			if (isTourokuAddress) {
				addressType.append("／");
				addressType.append(TOUROKU_ADDRESS_TITLE_HOJIN);
			}
		} else {
			// 弁護士の場合
			if (this.isBaseAddress) {
				addressType.append(BASE_ADDRESS_TITLE_LAWYER);
			} else {
				addressType.append(OTHER_ADDRESS_SIMPLE_TITLE);
			}
		}

		if (isTransferAddress) {
			addressType.append("／");
			addressType.append(TRANSFER_ADDRESS_TITLE);
		}

		return addressType.toString();
	}

	/**
	 * 住所一覧の住所タイトル部分（住所タイプ表示）を取得する
	 * 
	 * <pre>
	 * 居住地 or その他住所
	 * </pre>
	 * 
	 * @return
	 */
	public String getBaseAddressOnly() {

		StringBuilder addressType = new StringBuilder();
		if (this.customerType == CustomerType.KOJIN) {
			// 個人の場合
			if (this.isBaseAddress) {
				addressType.append(BASE_ADDRESS_TITLE_KOJIN);
			} else {
				addressType.append(OTHER_ADDRESS_SIMPLE_TITLE);
			}
		} else if (this.customerType == CustomerType.HOJIN) {
			// 企業・団体の場合
			if (this.isBaseAddress) {
				addressType.append(BASE_ADDRESS_TITLE_HOJIN);
			} else {
				addressType.append(OTHER_ADDRESS_SIMPLE_TITLE);
			}
		} else {
			// 弁護士の場合
			if (this.isBaseAddress) {
				addressType.append(BASE_ADDRESS_TITLE_LAWYER);
			} else {
				addressType.append(OTHER_ADDRESS_SIMPLE_TITLE);
			}
		}
		return addressType.toString();
	}

	/**
	 * 住所一覧の住所タイトル部分（住所タイプ表示）を取得する
	 * 
	 * <pre>
	 * 住民票登録地 or 登記簿登録地
	 * </pre>
	 * 
	 * @return
	 */
	public String getTourokuAddressOnly() {

		StringBuilder addressType = new StringBuilder();
		if (this.customerType == CustomerType.KOJIN) {
			// 個人の場合
			if (this.isTourokuAddress) {
				addressType.append(TOUROKU_ADDRESS_TITLE_KOJIN);
			}
		} else {
			// 企業・団体の場合
			if (isTourokuAddress) {
				addressType.append(TOUROKU_ADDRESS_TITLE_HOJIN);
			}
		}

		return addressType.toString();
	}

	/**
	 * 住所一覧の住所タイトル部分（住所タイプ表示）を取得する
	 * 
	 * <pre>
	 * 郵送先住所
	 * </pre>
	 * 
	 * @return
	 */
	public String getTransferAddressOnly() {

		StringBuilder addressType = new StringBuilder();

		if (isTransferAddress) {
			addressType.append(TRANSFER_ADDRESS_TITLE);
		}

		return addressType.toString();
	}

	/**
	 * 表示用の郵便番号を取得
	 * 
	 * @return
	 */
	public String getZipCodeForDisp() {
		return StringUtils.convertToDispZipCode(this.zipCode);
	}

	/**
	 * 住所情報を取得（それぞれの住所情報を結合した状態）
	 * 
	 * @return
	 */
	public String getAddressFull() {
		return String.join("",
				Arrays.asList(
						StringUtils.null2blank(this.address1),
						StringUtils.null2blank(this.address2)));
	}

	/**
	 * 同じ住所データかどうかを判定する
	 * 
	 * @param dto
	 * @return
	 */
	public boolean isSameAddressData(PersonAddressDto dto) {
		if (dto == null) {
			return false;
		}

		boolean isSameAddressData = StringUtils.null2blank(this.zipCode).equals(StringUtils.null2blank(dto.zipCode))
				&& StringUtils.null2blank(this.address1).equals(StringUtils.null2blank(dto.address1))
				&& StringUtils.null2blank(this.address2).equals(StringUtils.null2blank(dto.address2))
				&& StringUtils.null2blank(this.remarks).equals(StringUtils.null2blank(dto.remarks));

		return isSameAddressData;
	}

}
