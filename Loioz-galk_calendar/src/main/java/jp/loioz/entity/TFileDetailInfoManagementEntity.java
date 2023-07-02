package jp.loioz.entity;

import java.math.BigInteger;
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
 * ファイル詳細情報管理
 */
@Entity(listener = TFileDetailInfoManagementEntityListener.class)
@Table(name = "t_file_detail_info_management")
public class TFileDetailInfoManagementEntity extends DefaultEntity {

	/** ファイル詳細情報管理ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_detail_info_management_id")
	Long fileDetailInfoManagementId;

	/** ファイル名 */
	@Column(name = "file_name")
	String fileName;

	/** 拡張子 */
	@Column(name = "file_extension")
	String fileExtension;

	/** ファイルタイプ */
	@Column(name = "file_type")
	String fileType;

	/** フォルダパス */
	@Column(name = "folder_path")
	String folderPath;

	/** ファイルサイズ */
	@Column(name = "file_size")
	BigInteger fileSize;

	/** S3オブジェクトキー */
	@Column(name = "s3_object_key")
	String s3ObjectKey;

	/** ゴミ箱フラグ */
	@Column(name = "trash_box_flg")
	String trashBoxFlg;

	/** 削除操作アカウント連番 */
	@Column(name = "deleted_operation_account_seq")
	Long deletedOperationAccountSeq;

	/** ファイル作成日時 */
	@Column(name = "file_created_datetime")
	LocalDateTime fileCreatedDatetime;

	/** アップロード日時 */
	@Column(name = "upload_datetime")
	LocalDateTime uploadDatetime;

	/** アップロードユーザー */
	@Column(name = "upload_user")
	String uploadUser;

	/** 登録日 */
	@Column(name = "created_at")
	LocalDateTime createdAt;

	/** 登録アカウントSEQ */
	@Column(name = "created_by")
	Long createdBy;

	/** 更新日 */
	@Column(name = "updated_at")
	LocalDateTime updatedAt;

	/** 更新アカウントSEQ */
	@Column(name = "updated_by")
	Long updatedBy;

	/** 削除日 */
	@Column(name = "deleted_at")
	LocalDateTime deletedAt;

	/** 削除アカウントSEQ */
	@Column(name = "deleted_by")
	Long deletedBy;

	/** バージョンNo */
	@Version
	@Column(name = "version_no")
	Long versionNo;

	/**
	 * Returns the fileDetailInfoManagementId.
	 * 
	 * @return the fileDetailInfoManagementId
	 */
	public Long getFileDetailInfoManagementId() {
		return fileDetailInfoManagementId;
	}

	/**
	 * Sets the fileDetailInfoManagementId.
	 * 
	 * @param fileDetailInfoManagementId the fileDetailInfoManagementId
	 */
	public void setFileDetailInfoManagementId(Long fileDetailInfoManagementId) {
		this.fileDetailInfoManagementId = fileDetailInfoManagementId;
	}

	/**
	 * Returns the fileName.
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the fileName.
	 * 
	 * @param fileName the fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the fileExtension.
	 * 
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * Sets the fileExtension.
	 * 
	 * @param fileExtension the fileExtension
	 */
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * Returns the fileType.
	 * 
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * Sets the fileType.
	 * 
	 * @param fileType the fileType
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * Returns the folderPath.
	 * 
	 * @return the folderPath
	 */
	public String getFolderPath() {
		return folderPath;
	}

	/**
	 * Sets the folderPath.
	 * 
	 * @param folderPath the folderPath
	 */
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	/**
	 * Returns the fileSize.
	 * 
	 * @return the fileSize
	 */
	public BigInteger getFileSize() {
		return fileSize;
	}

	/**
	 * Sets the fileSize.
	 * 
	 * @param fileSize the fileSize
	 */
	public void setFileSize(BigInteger fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * Returns the s3ObjectKey.
	 * 
	 * @return the s3ObjectKey
	 */
	public String getS3ObjectKey() {
		return s3ObjectKey;
	}

	/**
	 * Sets the s3ObjectKey.
	 * 
	 * @param s3ObjectKey the s3ObjectKey
	 */
	public void setS3ObjectKey(String s3ObjectKey) {
		this.s3ObjectKey = s3ObjectKey;
	}

	/**
	 * Returns the trashBoxFlg.
	 * 
	 * @return the trashBoxFlg
	 */
	public String getTrashBoxFlg() {
		return trashBoxFlg;
	}

	/**
	 * Sets the trashBoxFlg.
	 * 
	 * @param trashBoxFlg the trashBoxFlg
	 */
	public void setTrashBoxFlg(String trashBoxFlg) {
		this.trashBoxFlg = trashBoxFlg;
	}

	/**
	 * Returns the deletedOperationAccountSeq.
	 * 
	 * @return the deletedOperationAccountSeq
	 */
	public Long getDeletedOperationAccountSeq() {
		return deletedOperationAccountSeq;
	}

	/**
	 * Sets the deletedOperationAccountSeq.
	 * 
	 * @param deletedOperationAccountSeq the deletedOperationAccountSeq
	 */
	public void setDeletedOperationAccountSeq(Long deletedOperationAccountSeq) {
		this.deletedOperationAccountSeq = deletedOperationAccountSeq;
	}

	/**
	 * Returns the fileCreatedDatetime.
	 * 
	 * @return the fileCreatedDatetime
	 */
	public LocalDateTime getFileCreatedDatetime() {
		return fileCreatedDatetime;
	}

	/**
	 * Sets the fileCreatedDatetime.
	 * 
	 * @param fileCreatedDatetime the fileCreatedDatetime
	 */
	public void setFileCreatedDatetime(LocalDateTime fileCreatedDatetime) {
		this.fileCreatedDatetime = fileCreatedDatetime;
	}

	/**
	 * Returns the uploadDatetime.
	 * 
	 * @return the uploadDatetime
	 */
	public LocalDateTime getUploadDatetime() {
		return uploadDatetime;
	}

	/**
	 * Sets the uploadDatetime.
	 * 
	 * @param uploadDatetime the uploadDatetime
	 */
	public void setUploadDatetime(LocalDateTime uploadDatetime) {
		this.uploadDatetime = uploadDatetime;
	}

	/**
	 * Returns the uploadUser.
	 * 
	 * @return the uploadUser
	 */
	public String getUploadUser() {
		return uploadUser;
	}

	/**
	 * Sets the uploadUser.
	 * 
	 * @param uploadUser the uploadUser
	 */
	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
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
	 * Returns the deletedAt.
	 * 
	 * @return the deletedAt
	 */
	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	/**
	 * Sets the deletedAt.
	 * 
	 * @param deletedAt the deletedAt
	 */
	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	/**
	 * Returns the deletedBy.
	 * 
	 * @return the deletedBy
	 */
	public Long getDeletedBy() {
		return deletedBy;
	}

	/**
	 * Sets the deletedBy.
	 * 
	 * @param deletedBy the deletedBy
	 */
	public void setDeletedBy(Long deletedBy) {
		this.deletedBy = deletedBy;
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