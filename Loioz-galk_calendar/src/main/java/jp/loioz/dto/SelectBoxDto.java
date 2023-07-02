package jp.loioz.dto;

public class SelectBoxDto {

	/** マスタテーブルのID */
	private Long Id;

	/** マスタテーブルの名前 */
	private String Name;

	/**
	 * Returns the bunyaId.
	 *
	 * @return the bunyaId
	 */
	public Long getId() {
		return Id;
	}

	/**
	 * Sets the bunyaId.
	 *
	 * @param bunyaId the bunyaId
	 */
	public void setId(Long Id) {
		this.Id = Id;
	}

	/**
	 * Returns the bunyaName.
	 *
	 * @return the bunyaName
	 */
	public String getName() {
		return Name;
	}

	/**
	 * Sets the bunyaName.
	 *
	 * @param bunyaName the bunyaName
	 */
	public void setName(String Name) {
		this.Name = Name;
	}

}
