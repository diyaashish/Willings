package jp.loioz.domain.value;

import java.util.Objects;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.entity.TAnkenEntity;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AnkenName {

	/** 案件名 */
	private String ankenName;

	/** 分野ID */
	private Long bunyaId;

	/**
	 * 引数から案件名を作成
	 * 
	 * @param ankenName
	 * @param bunyaId
	 * @return
	 */
	public static AnkenName of(String ankenName, Long bunyaId) {
		return new AnkenName(ankenName, bunyaId);
	}

	/**
	 * エンティティから案件名を設定する
	 * 
	 * @param entity
	 * @return
	 */
	public static AnkenName fromEntity(TAnkenEntity entity) {
		if (Objects.isNull(entity)) {
			return null;
		}
		return new AnkenName(entity.getAnkenName(), entity.getBunyaId());
	}

	@Override
	public String toString() {
		if (StringUtils.isEmpty(this.ankenName)) {
			return "";
		} else {
			return ankenName;
		}
	}

}
