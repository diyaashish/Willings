package jp.loioz.entity;

import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 外部サービス認証
 */
@Entity(listener = TAuthTokenEntityListener.class)
@Table(name = "t_auth_token")
public class TAuthTokenEntity extends DefaultEntity {

    /** 外部サービス認証SEQ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_token_seq")
    Long authTokenSeq;

    /** アカウントSEQ */
    @Column(name = "account_seq")
    Long accountSeq;

    /** 外部サービスID */
    @Column(name = "external_service_id")
    String externalServiceId;

    /** アクセストークン */
    @Column(name = "access_token")
    String accessToken;

    /** リフレッシュトークン */
    @Column(name = "refresh_token")
    String refreshToken;

    /** 登録日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** 登録アカウントSEQ */
    @Column(name = "created_by")
    Long createdBy;

    /** 更新日時 */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 更新アカウントSEQ */
    @Column(name = "updated_by")
    Long updatedBy;

    /** バージョンNo */
    @Version
    @Column(name = "version_no")
    Long versionNo;

    /** 
     * Returns the authTokenSeq.
     * 
     * @return the authTokenSeq
     */
    public Long getAuthTokenSeq() {
        return authTokenSeq;
    }

    /** 
     * Sets the authTokenSeq.
     * 
     * @param authTokenSeq the authTokenSeq
     */
    public void setAuthTokenSeq(Long authTokenSeq) {
        this.authTokenSeq = authTokenSeq;
    }

    /** 
     * Returns the accountSeq.
     * 
     * @return the accountSeq
     */
    public Long getAccountSeq() {
        return accountSeq;
    }

    /** 
     * Sets the accountSeq.
     * 
     * @param accountSeq the accountSeq
     */
    public void setAccountSeq(Long accountSeq) {
        this.accountSeq = accountSeq;
    }

    /** 
     * Returns the externalServiceId.
     * 
     * @return the externalServiceId
     */
    public String getExternalServiceId() {
        return externalServiceId;
    }

    /** 
     * Sets the externalServiceId.
     * 
     * @param externalServiceId the externalServiceId
     */
    public void setExternalServiceId(String externalServiceId) {
        this.externalServiceId = externalServiceId;
    }

    /** 
     * Returns the accessToken.
     * 
     * @return the accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /** 
     * Sets the accessToken.
     * 
     * @param accessToken the accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /** 
     * Returns the refreshToken.
     * 
     * @return the refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /** 
     * Sets the refreshToken.
     * 
     * @param refreshToken the refreshToken
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /** 
     * Returns the createdAt.
     * 
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** 
     * Sets the createdAt.
     * 
     * @param createdAt the createdAt
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** 
     * Returns the createdBy.
     * 
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /** 
     * Sets the createdBy.
     * 
     * @param createdBy the createdBy
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /** 
     * Returns the updatedAt.
     * 
     * @return the updatedAt
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /** 
     * Sets the updatedAt.
     * 
     * @param updatedAt the updatedAt
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** 
     * Returns the updatedBy.
     * 
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /** 
     * Sets the updatedBy.
     * 
     * @param updatedBy the updatedBy
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /** 
     * Returns the versionNo.
     * 
     * @return the versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /** 
     * Sets the versionNo.
     * 
     * @param versionNo the versionNo
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }
}