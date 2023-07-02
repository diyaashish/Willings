package jp.loioz.app.common.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 複合主キー情報のMapKeyオブジェクト<br>
 *
 * @param <K1>第一引数
 * @param <K2>第二引数
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Keys<K1, K2> {

	public K1 key1;
	public K2 key2;

}
