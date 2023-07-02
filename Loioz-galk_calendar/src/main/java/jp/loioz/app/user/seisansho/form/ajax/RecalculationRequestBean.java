package jp.loioz.app.user.seisansho.form.ajax;

import lombok.Data;

/**
 * 精算書作成画面：再計算のリクエスト情報
 */
@Data
public class RecalculationRequestBean {

	/** kaikeikirokuSeq_map */
	private String map;

		public String getMap() {
		return map;
	}
	public void setMap(String map) {
		this.map = map;
	}
}
