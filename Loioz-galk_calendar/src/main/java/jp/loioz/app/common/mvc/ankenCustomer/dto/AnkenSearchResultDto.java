package jp.loioz.app.common.mvc.ankenCustomer.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.value.AnkenId;
import lombok.Data;

/**
 * 案件検索結果Dto
 */
@Data
public class AnkenSearchResultDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 分野名 */
	private String bunya;

	/** 案件名 */
	private String ankenName;

	/** 顧客名 */
	private String customerName;

	/** 旧姓 */
	private String oldName;

	/** 旧姓かな */
	private String oldNameKana;

	/** 相手方 */
	private List<Aitegata> aitegataList = Collections.emptyList();

	/** 表示用の相手方名を取得する */
	public String getAitegataDispName() {

		List<String> dispAitegataNameList = this.aitegataList.stream().map(e -> {
			String dispName = e.getAitegataName();
			if (StringUtils.isNotEmpty(e.getOldName())) {
				dispName += StringUtils.surroundBrackets(e.getOldName(), true);
			}
			return dispName;
		}).collect(Collectors.toList());

		return StringUtils.list2CommaSP(dispAitegataNameList);
	}

	/**
	 * Dto内で扱う相手方情報
	 */
	@Data
	public static class Aitegata {

		/** 相手方名 */
		private String aitegataName;

		/** 相手方名かな */
		private String aitegataNameKana;

		/** 旧姓 */
		private String oldName;

		/** 旧姓かな */
		private String oldNameKana;
	}

}
