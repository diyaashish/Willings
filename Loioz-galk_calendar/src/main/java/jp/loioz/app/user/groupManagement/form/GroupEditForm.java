package jp.loioz.app.user.groupManagement.form;

import javax.validation.Valid;

import jp.loioz.dto.GroupEditDto;
import lombok.Data;

/**
 * グループ編集画面のFormクラス
 */
@Data
public class GroupEditForm {

	/** 部署情報 */
	@Valid
	private GroupEditDto groupEditData = new GroupEditDto();

	/**
	 * 新規登録モード判定
	 *
	 * @return
	 */
	public boolean isNew() {
		boolean newFlg = false;
		if (groupEditData.getGroupId() == null) {
			newFlg = true;
		}
		return newFlg;
	}
}