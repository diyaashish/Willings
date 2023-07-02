package jp.loioz.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jp.loioz.entity.common.DefaultEntity;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

/**
 * 個人名簿付帯情報
 */
@Entity(listener = TPersonAddKojinEntityListener.class)
@Table(name = "t_person_add_kojin")
public class TPersonAddKojinEntity extends DefaultEntity {

    /** 名簿ID */
    @Id
    @Column(name = "person_id")
    Long personId;

    /** 旧姓 */
    @Column(name = "old_name")
    String oldName;

    /** 旧姓かな */
    @Column(name = "old_name_kana")
    String oldNameKana;

    /** 屋号・通称 */
    @Column(name = "yago")
    String yago;

    /** 屋号・通称かな */
    @Column(name = "yago_kana")
    String yagoKana;

    /** 性別 */
    @Column(name = "gender_type")
    String genderType;

    /** 生年月日 */
    @Column(name = "birthday")
    LocalDate birthday;

    /** 生年月日表示区分 */
    @Column(name = "birthday_display_type")
    String birthdayDisplayType;

    /** 国籍 */
    @Column(name = "country")
    String country;

    /** 言語 */
    @Column(name = "language")
    String language;

    /** 住民票登録地住所区分 */
    @Column(name = "juminhyo_address_type")
    String juminhyoAddressType;

    /** 住民票登録地郵便番号 */
    @Column(name = "juminhyo_zip_code")
    String juminhyoZipCode;

    /** 住民票登録地地域 */
    @Column(name = "juminhyo_address1")
    String juminhyoAddress1;

    /** 住民票登録地番地・建物名 */
    @Column(name = "juminhyo_address2")
    String juminhyoAddress2;

    /** 住民票登録地備考 */
    @Column(name = "juminhyo_remarks")
    String juminhyoRemarks;

    /** 職業 */
    @Column(name = "job")
    String job;

    /** 勤務先 */
    @Column(name = "work_place")
    String workPlace;

    /** 部署名 */
    @Column(name = "busho_name")
    String bushoName;

    /** 死亡日 */
    @Column(name = "death_date")
    LocalDate deathDate;

    /** 死亡日表示区分 */
    @Column(name = "death_date_display_type")
    String deathDateDisplayType;

    /** 死亡フラグ */
    @Column(name = "death_flg")
    String deathFlg;

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
     * Returns the personId.
     * 
     * @return the personId
     */
    public Long getPersonId() {
        return personId;
    }

    /** 
     * Sets the personId.
     * 
     * @param personId the personId
     */
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    /** 
     * Returns the oldName.
     * 
     * @return the oldName
     */
    public String getOldName() {
        return oldName;
    }

    /** 
     * Sets the oldName.
     * 
     * @param oldName the oldName
     */
    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    /** 
     * Returns the oldNameKana.
     * 
     * @return the oldNameKana
     */
    public String getOldNameKana() {
        return oldNameKana;
    }

    /** 
     * Sets the oldNameKana.
     * 
     * @param oldNameKana the oldNameKana
     */
    public void setOldNameKana(String oldNameKana) {
        this.oldNameKana = oldNameKana;
    }

    /** 
     * Returns the yago.
     * 
     * @return the yago
     */
    public String getYago() {
        return yago;
    }

    /** 
     * Sets the yago.
     * 
     * @param yago the yago
     */
    public void setYago(String yago) {
        this.yago = yago;
    }

    /** 
     * Returns the yagoKana.
     * 
     * @return the yagoKana
     */
    public String getYagoKana() {
        return yagoKana;
    }

    /** 
     * Sets the yagoKana.
     * 
     * @param yagoKana the yagoKana
     */
    public void setYagoKana(String yagoKana) {
        this.yagoKana = yagoKana;
    }

    /** 
     * Returns the genderType.
     * 
     * @return the genderType
     */
    public String getGenderType() {
        return genderType;
    }

    /** 
     * Sets the genderType.
     * 
     * @param genderType the genderType
     */
    public void setGenderType(String genderType) {
        this.genderType = genderType;
    }

    /** 
     * Returns the birthday.
     * 
     * @return the birthday
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    /** 
     * Sets the birthday.
     * 
     * @param birthday the birthday
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    /** 
     * Returns the birthdayDisplayType.
     * 
     * @return the birthdayDisplayType
     */
    public String getBirthdayDisplayType() {
        return birthdayDisplayType;
    }

    /** 
     * Sets the birthdayDisplayType.
     * 
     * @param birthdayDisplayType the birthdayDisplayType
     */
    public void setBirthdayDisplayType(String birthdayDisplayType) {
        this.birthdayDisplayType = birthdayDisplayType;
    }

    /** 
     * Returns the country.
     * 
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /** 
     * Sets the country.
     * 
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /** 
     * Returns the language.
     * 
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /** 
     * Sets the language.
     * 
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /** 
     * Returns the juminhyoAddressType.
     * 
     * @return the juminhyoAddressType
     */
    public String getJuminhyoAddressType() {
        return juminhyoAddressType;
    }

    /** 
     * Sets the juminhyoAddressType.
     * 
     * @param juminhyoAddressType the juminhyoAddressType
     */
    public void setJuminhyoAddressType(String juminhyoAddressType) {
        this.juminhyoAddressType = juminhyoAddressType;
    }

    /** 
     * Returns the juminhyoZipCode.
     * 
     * @return the juminhyoZipCode
     */
    public String getJuminhyoZipCode() {
        return juminhyoZipCode;
    }

    /** 
     * Sets the juminhyoZipCode.
     * 
     * @param juminhyoZipCode the juminhyoZipCode
     */
    public void setJuminhyoZipCode(String juminhyoZipCode) {
        this.juminhyoZipCode = juminhyoZipCode;
    }

    /** 
     * Returns the juminhyoAddress1.
     * 
     * @return the juminhyoAddress1
     */
    public String getJuminhyoAddress1() {
        return juminhyoAddress1;
    }

    /** 
     * Sets the juminhyoAddress1.
     * 
     * @param juminhyoAddress1 the juminhyoAddress1
     */
    public void setJuminhyoAddress1(String juminhyoAddress1) {
        this.juminhyoAddress1 = juminhyoAddress1;
    }

    /** 
     * Returns the juminhyoAddress2.
     * 
     * @return the juminhyoAddress2
     */
    public String getJuminhyoAddress2() {
        return juminhyoAddress2;
    }

    /** 
     * Sets the juminhyoAddress2.
     * 
     * @param juminhyoAddress2 the juminhyoAddress2
     */
    public void setJuminhyoAddress2(String juminhyoAddress2) {
        this.juminhyoAddress2 = juminhyoAddress2;
    }

    /** 
     * Returns the juminhyoRemarks.
     * 
     * @return the juminhyoRemarks
     */
    public String getJuminhyoRemarks() {
        return juminhyoRemarks;
    }

    /** 
     * Sets the juminhyoRemarks.
     * 
     * @param juminhyoRemarks the juminhyoRemarks
     */
    public void setJuminhyoRemarks(String juminhyoRemarks) {
        this.juminhyoRemarks = juminhyoRemarks;
    }

    /** 
     * Returns the job.
     * 
     * @return the job
     */
    public String getJob() {
        return job;
    }

    /** 
     * Sets the job.
     * 
     * @param job the job
     */
    public void setJob(String job) {
        this.job = job;
    }

    /** 
     * Returns the workPlace.
     * 
     * @return the workPlace
     */
    public String getWorkPlace() {
        return workPlace;
    }

    /** 
     * Sets the workPlace.
     * 
     * @param workPlace the workPlace
     */
    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    /** 
     * Returns the bushoName.
     * 
     * @return the bushoName
     */
    public String getBushoName() {
        return bushoName;
    }

    /** 
     * Sets the bushoName.
     * 
     * @param bushoName the bushoName
     */
    public void setBushoName(String bushoName) {
        this.bushoName = bushoName;
    }

    /** 
     * Returns the deathDate.
     * 
     * @return the deathDate
     */
    public LocalDate getDeathDate() {
        return deathDate;
    }

    /** 
     * Sets the deathDate.
     * 
     * @param deathDate the deathDate
     */
    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    /** 
     * Returns the deathDateDisplayType.
     * 
     * @return the deathDateDisplayType
     */
    public String getDeathDateDisplayType() {
        return deathDateDisplayType;
    }

    /** 
     * Sets the deathDateDisplayType.
     * 
     * @param deathDateDisplayType the deathDateDisplayType
     */
    public void setDeathDateDisplayType(String deathDateDisplayType) {
        this.deathDateDisplayType = deathDateDisplayType;
    }

    /** 
     * Returns the deathFlg.
     * 
     * @return the deathFlg
     */
    public String getDeathFlg() {
        return deathFlg;
    }

    /** 
     * Sets the deathFlg.
     * 
     * @param deathFlg the deathFlg
     */
    public void setDeathFlg(String deathFlg) {
        this.deathFlg = deathFlg;
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