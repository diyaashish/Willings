package jp.loioz.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * ダウンロード一覧用Bean
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class DownloadListBean {

	/** 発行日 */
	private LocalDate issueDate;

	/** 会計書類対応送付ファイルSEQ */
	private Long accgDocActSendFileSeq;

	/** 最終ダウンロード日時 */
	private LocalDateTime downloadLastAt;

	/** 会計書類ファイル種別 */
	private String accgDocFileType;

	/** 拡張子 */
	private String fileExtension;

	/** 会計書類SEQ */
	private Long accgDocSeq;
}
