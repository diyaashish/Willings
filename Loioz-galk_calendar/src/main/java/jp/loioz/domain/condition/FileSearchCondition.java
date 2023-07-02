package jp.loioz.domain.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 全体ファイル管理検索画面の検索条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchCondition extends SearchCondition {

	/** ファイル名 */
	private String fileName;

	/** アップロードユーザー */
	private String uploadUser;

	/** ファイルタイプ */
	private String fileType;

	/** ファイル区分 */
	private String fileKubun;

}
