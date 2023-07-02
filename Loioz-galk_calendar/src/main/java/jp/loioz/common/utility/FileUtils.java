package jp.loioz.common.utility;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import jp.loioz.common.constant.CommonConstant.FileExtension;

/**
 * ファイル関連のUtilsクラス
 * 
 * @author hoshima
 *
 */
public class FileUtils {

	/**
	 * Blob型をbyte[]に変換する
	 * 
	 * @param blob
	 * @return
	 */
	public static byte[] toByteArray(Blob blob) {
		if (blob == null) {
			return null;
		}

		try {
			int blobLength = (int) blob.length();
			return blob.getBytes(1, blobLength);
		} catch (SQLException e) {
			return null;
		}
	}

	/**
	 * byte[]をBlob型に変換する
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Blob toBlob(MultipartFile multipartFile) {
		if (multipartFile == null || multipartFile.isEmpty()) {
			return null;
		}

		// 変換処理
		try {
			return new SerialBlob(multipartFile.getBytes());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ファイル拡張子を取得する
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getExtension(String fileName) {
		return Optional.ofNullable(fileName)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(fileName.lastIndexOf(".") + 1)).orElse("");
	}

	/**
	 * 画像データをHTML表示用文字列に変換する
	 * HTML Imageタグ srcプロパティに当メソッドの文字列を設定することを想定している
	 * 
	 * @return
	 */
	public static String toHtmlImgSrc(byte[] image, String extension) {

		String base64Image = Base64Utils.encodeToString(image);

		// htmlでsrcプロパティに設定する文字列形式
		String formatter = "data:%s;base64,%s";
		String mimeType = getImageMineType(extension);
		return String.format(formatter, mimeType, base64Image);
	}

	/**
	 * HTML画像表示用のMineTypeを取得
	 * 
	 * @return
	 */
	private static String getImageMineType(String extension) {
		FileExtension fileExtension = FileExtension.ofExtension(extension);
		switch (fileExtension) {
		case JPEG:
		case JPEG_U:
			return "image/jpeg";
		case JPG:
		case JPG_U:
			return "image/jpeg";
		case PNG:
		case PNG_U:
			return "image/png";
		case GIF:
		case GIF_U:
			return "image/gif";
		default:
			throw new RuntimeException("拡張子が未対応");
		}
	}

}
