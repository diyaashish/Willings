package jp.loioz.bean;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class AccgDocFileBean {

	// 会計書類ファイル

	/** 会計書類ファイルSEQ */
	private Long accgDocFileSeq;

	/** 会計書類SEQ */
	private Long accgDocSeq;

	/** 会計書類ファイル種別 */
	private String accgDocFileType;

	/** 拡張子（PDF、PNG） */
	private String fileExtension;

	/** 再作成実行待ちフラグ */
	private String recreateStandbyFlg;

	// 会計書類ファイル-詳細

	/** 会計書類ファイル詳細SEQ */
	private Long accgDocFileDetailSeq;

	/** ファイル枝番 */
	private Long fileBranchNo;

	/** S3オブジェクトキー */
	private String s3ObjectKey;

	// 会計書類-対応-送付-ファイル

	/** 会計書類対応SEQ このレコードの有無により、送付済みかどうかを判断する(複数の場合あり) */
	private Long accgDocActSendFileSeq;

}
