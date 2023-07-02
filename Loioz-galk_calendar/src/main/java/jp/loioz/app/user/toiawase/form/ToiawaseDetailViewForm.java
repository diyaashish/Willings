package jp.loioz.app.user.toiawase.form;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import jp.loioz.common.constant.CommonConstant.SystemType;
import jp.loioz.common.constant.CommonConstant.ToiawaseStatus;
import jp.loioz.common.constant.CommonConstant.ToiawaseType;
import jp.loioz.common.utility.DateUtils;
import lombok.Data;

/**
 * 問い合わせ詳細画面表示用オブジェクト
 */
@Data
public class ToiawaseDetailViewForm {

	/** テナント名 */
	private String tenantName;

	/** 問い合わせSEQ */
	private Long toiawaseSeq;

	/** 件名 */
	private String subject;

	/** 問い合わせ種別 */
	private ToiawaseType toiawaseType;

	/** 問い合わせステータス */
	private ToiawaseStatus toiawaseStatus;

	/** 最終更新日時 */
	private String lastUpdatedAt;

	/** 問い合わせ詳細 */
	private List<Detail> toiawaseDetails = Collections.emptyList();

	/** 解決済かどうか */
	public boolean isCompleted() {
		return ToiawaseStatus.COMPLETED == this.toiawaseStatus;
	}

	/**
	 * 画面表示用：詳細情報
	 */
	@Data
	public static class Detail {

		/** 問い合わせ詳細 */
		private Long toiawaseDetailSeq;

		/** 問い合わせ詳細本文 */
		private String body;

		/** 登録元種別 ※本値は画面に表示しないこと(thymeleafの条件でのみ使用) */
		private String registType;

		/** テナント既読フラグ */
		private boolean tenantRead;

		/** ユーザー側：作成日時 */
		private LocalDateTime createdAt;

		/** システム管理側：公開日時 (更新日時) */
		private LocalDateTime openDateAt;

		/** 問い合わせ添付SEQ */
		private List<Attachment> attachmentList = Collections.emptyList();

		/** システム管理者が作成したかどうか */
		public boolean isAdminCreate() {
			return SystemType.ADMIN.equalsByCode(this.registType);
		}

		/** テナントユーザーが作成したかどうか */
		public boolean isUserCreate() {
			return SystemType.USER.equalsByCode(this.registType);
		}

		/** 作成日時 */
		public String getDispCreatedAt() {
			if (this.isAdminCreate()) {
				// システム管理側：作成日時
				return DateUtils.parseToString(this.openDateAt, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM);
			} else if (this.isUserCreate()) {
				// ユーザー側：作成日時
				return DateUtils.parseToString(this.createdAt, DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM);
			} else {
				throw new RuntimeException("登録元が設定されていないケースは存在しない");
			}
		}

		/** 表示順ソートキー */
		public LocalDateTime getSortKey() {
			if (this.isAdminCreate()) {
				// システム管理側：作成日時
				return this.openDateAt;
			} else if (this.isUserCreate()) {
				// ユーザー側：作成日時
				return this.createdAt;
			} else {
				throw new RuntimeException("登録元が設定されていないケースは存在しない");
			}
		}

	}

	/**
	 * 添付ファイル
	 */
	@Data
	public static class Attachment {

		/** 問い合わせ-添付SEQ */
		private Long toiawaseAttachmentSeq;

		/** 問い合わせ詳細 */
		private Long toiawaseDetailSeq;

		/** ファイル名 */
		private String fileName;

		/** 拡張子 */
		private String fileExtension;
	}

}
