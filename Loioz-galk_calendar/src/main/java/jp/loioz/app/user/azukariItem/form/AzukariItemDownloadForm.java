package jp.loioz.app.user.azukariItem.form;

import java.util.List;

import lombok.Data;

/**
 * 預り品ダウンロードのフォームクラス
 */
@Data
public class AzukariItemDownloadForm {

	/** ダウンロード対象預かり品SEQリスト */
	private List<Long> azukariSeqList;

	/** 案件ID */
	private Long ankenId;

}