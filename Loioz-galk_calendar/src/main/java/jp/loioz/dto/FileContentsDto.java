package jp.loioz.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * バイトファイルオブジェクトDto
 */
@Data
public class FileContentsDto {

	/** バイト配列 */
	private byte[] byteArray;

	/** 拡張子 */
	private String extension;

	/**
	 * コンストラクタ
	 * 
	 * @param byteArray
	 * @param extension
	 */
	public FileContentsDto(byte[] byteArray, String extension) {

		Pattern p = Pattern.compile("\\.");
		Matcher m = p.matcher(extension);

		if (m.find()) {
			throw new RuntimeException("拡張子にドットは含めないでください");
		}

		this.byteArray = byteArray;
		this.extension = extension;
	}

	/**
	 * extensionのsetter 拡張子のパターンチェックを行うためlombock
	 * 
	 * @param extension
	 */
	public void setExtension(String extension) {

		Pattern p = Pattern.compile("\\.");
		Matcher m = p.matcher(extension);

		if (m.find()) {
			throw new RuntimeException("拡張子にドットは含めないでください");
		}

		this.extension = extension;
	}

	/**
	 * 拡張子を"."+拡張子で取得する
	 * 
	 * @return "." + extension
	 */
	public String getExtensionIncludeDots() {

		String extension = this.extension;

		if (!StringUtils.isEmpty(extension)) {
			extension = "." + extension;
		}
		return extension;
	}

}
