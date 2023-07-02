package jp.loioz.app.global.download.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.global.download.dto.DownloadFileDto;
import lombok.Data;

/**
 * ダウンロード画面のフォームオブジェクト
 */
@Data
public class DownloadViewForm {

	/** テナント認証キー(5桁 + テナントSEQ) */
	private String tenantAuthKey;

	/** 認証キー */
	private String downloadViewUrlKey;

	/** 事務所名 */
	private String tenantName;

	/** ダウンロード期限 */
	private String downloadLimitDt;

	/** ダウンロードファイル一覧 */
	private DownloadListViewForm downloadListViewForm;

	/**
	 * ダウンロード一覧
	 */
	@Data
	public static class DownloadListViewForm {

		/** ダウンロード期限切れ */
		private boolean isDownloadExpired;

		/** ダウンロード一覧情報 */
		private List<DownloadFileDto> downloadFileList = Collections.emptyList();

	}

}
