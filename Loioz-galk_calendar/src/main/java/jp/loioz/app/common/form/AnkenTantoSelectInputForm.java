package jp.loioz.app.common.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 案件担当者の選択入力用フォームオブジェクト
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnkenTantoSelectInputForm {

	/** 担当者 */
	private Long accountSeq;

	/** 主担当 */
	private boolean mainTanto;

	/**
	 * 空であるか判定する
	 *
	 * @return 空の場合はtrue
	 */
	public boolean isEmpty() {
		return accountSeq == null;
	}

}
