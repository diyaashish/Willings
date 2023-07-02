package jp.loioz.app.user.dengon.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.loioz.app.common.form.SelectOptionForm;
import lombok.Data;

/**
 * メッセージ作成：宛先ポッパー用Formオブジェクト
 */
@Data
public class DengonAtesakiListPopForm {

	/** 部署 */
	private List<SelectOptionForm> bushoList = Collections.emptyList();

	/** 案件担当アカウントSEQ */
	private Map<Long, List<Long>> ankenIdToTantoAccountMap = Collections.emptyMap();

	public List<Long> ankenTantoAccountSeq() {

		Set<Long> ankenTantoAccountSeqSet = new HashSet<>();
		this.ankenIdToTantoAccountMap.values().stream().forEach(ankenTantoAccountSeqSet::addAll);

		return new ArrayList<>(ankenTantoAccountSeqSet);
	}
}
