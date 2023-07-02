package jp.loioz.domain.file;

import lombok.Data;

/**
 * ディレクトリ情報を保持するクラス
 */
@Data
public class Directory {

	/** ディレクトリ名 */
	private String dirName;

	/** ディレクトリパス */
	private String dirPath;

	// =========================================================================
	// コンストラクタ
	// =========================================================================
	public Directory() {
	}

	public Directory(String dirPath) {
		this.dirPath = dirPath;
	}
}