package jp.loioz.app.user.groupManagement.form;

import java.util.ArrayList;
import java.util.List;

import jp.loioz.dto.AccountEditDto;
import lombok.Data;

/**
 * 所属グループアカウント画面のフォームクラス
 */
@Data
public class GroupShozokuForm {

	/* 所属ダイアログ用 */
	private Long tempId;

	/* グループID */
	private Long groupId;

	/* グループ名 */
	private String groupName;

	/* 所属アカウント一覧 */
	private List<AccountEditDto> shozokuAccountList = new ArrayList<AccountEditDto>();

	/* 選択用所属アカウント一覧 */
	private List<AccountEditDto> selectAccountList = new ArrayList<AccountEditDto>();

	/* 所属しているアカウントのSEQリスト */
	private List<Long> accountSeqList = new ArrayList<Long>();
}