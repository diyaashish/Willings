package jp.loioz.dto;

public class VarificationDto {

	/** 認証キー */
	String verificationKey;

	/** アカウントID */
	String accountId;


	/**
	 * Returns the verificationKey.
	 *
	 * @return the verificationKey
	 */
	public String getVerificationKey() {
		return verificationKey;
	}

	/**
	 * Sets the verificationKey.
	 *
	 * @param verificationKey the verificationKey
	 */
	public void setVerificationKey(String verificationKey) {
		this.verificationKey = verificationKey;
	}

	/**
	 * Returns the accountId.
	 *
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * Sets the accountId.
	 *
	 * @param accountId the accountId
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}