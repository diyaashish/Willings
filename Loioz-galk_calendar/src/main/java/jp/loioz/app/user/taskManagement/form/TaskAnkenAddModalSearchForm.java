package jp.loioz.app.user.taskManagement.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.domain.condition.TaskAnkenAddModalSearchCondition;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MAccountEntity;
import lombok.Data;

/**
 * 案件タスク追加モーダルの検索条件フォームクラス
 */
@Data
public class TaskAnkenAddModalSearchForm {

	/** 検索条件 名前 */
	private String name;

	/** 検索条件 案件ID */
	private String ankenId;

	/** 検索条件 分野 */
	private String bunyaId;

	/** 検索条件 案件名 */
	private String ankenName;

	/** 検索条件 担当弁護士 */
	private Long tantoLaywer;

	/** 検索条件 担当事務 */
	private Long tantoJimu;

	/** 検索条件担当弁護士のプルダウン情報 */
	private List<MAccountEntity> tantoLawyerList = Collections.emptyList();

	/** 検索条件担当事務のプルダウン情報 */
	private List<MAccountEntity> tantoJimuList = Collections.emptyList();

	/** 検索条件分野のプルダウン情報 */
	private List<BunyaDto> bunyaList = Collections.emptyList();

	/** 案件登録日From **/
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String ankenCreateDateFrom;

	/** 案件登録日To **/
	@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
	private String ankenCreateDateTo;

	/**
	 * 検索条件オブジェクトを作成
	 * 
	 * @return
	 */
	public TaskAnkenAddModalSearchCondition toTaskAnkenAddModalSearchCondition() {
		return TaskAnkenAddModalSearchCondition.builder()
				.name(StringUtils.removeSpaceCharacter(name))
				.ankenId(StringUtils.isEmpty(ankenId) ? "" : ankenId)
				.bunyaId(bunyaId)
				.ankenName(StringUtils.removeSpaceCharacter(ankenName))
				.tantoLaywer(tantoLaywer)
				.tantoJimu(tantoJimu)
				.ankenCreateDateFrom(ankenCreateDateFrom)
				.ankenCreateDateTo(ankenCreateDateTo)
				.build();
	}

}