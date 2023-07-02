package jp.loioz.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import jp.loioz.common.constant.FileStorageConstant.GaibuChohyoCategory;
import jp.loioz.common.utility.StringUtils;
import lombok.Data;

/**
 * 帳票一覧用のDto
 */
@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class ChohyoListDto {

	// 帳票SEQ
	private Integer chohyoSeq;

	// ファイル名
	private String fileName;

	/**
	 * ファイル名から拡張子を除いたものを取得する
	 */
	public String getFileName() {
		if (StringUtils.isNotEmpty(this.fileName)) {
			if (fileName.lastIndexOf(".") == -1) {
				return fileName;
			} else {
				return fileName.substring(0, fileName.lastIndexOf("."));
			}
		} else {
			return null;
		}
	}

	/**
	 * ファイル名から拡張子を取得する
	 */
	public String getFileType() {
		return StringUtils.isNotEmpty(this.fileName) ? fileName.substring(fileName.lastIndexOf(".") + 1) : null;
	}

	/**
	 * 帳票SEQからカテゴリーパスリストを取得する
	 */
	public List<String> getCategoryPath() {
		try {
			List<String>categoryPathList = Arrays.stream(GaibuChohyoCategory.values()).filter(e -> Objects.equals(e.getChohyoSeq(), this.chohyoSeq)).map(e -> e.getCategory().getName()).collect(Collectors.toList());
			return categoryPathList;
		} catch (NullPointerException e) {
			return null;
		}
	}
}