package jp.loioz.app.user.nyushukkinYotei.shukkin.form;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.ShukkinListDto;
import lombok.Data;

@Data
public class ShukkinListViewForm {

	/** 出金予定一覧情報 */
	private List<ShukkinListDto> shukkinList = Collections.emptyList();

	/** 担当弁護士セレクトボックス */
	private List<SelectOptionForm> tantoLawyerOptionList = Collections.emptyList();

	/** 担当弁護士セレクトボックス */
	private List<SelectOptionForm> tantoJimuOptionList = Collections.emptyList();

	/** 口座選択用アカウントのセレクトボックス */
	private List<GinkoKozaDto> tenantKozaList = Collections.emptyList();

	/** 口座選択用アカウントのセレクトボックス */
	private Map<Long, List<GinkoKozaDto>> accountKozaList = new HashMap<>();

	/** 検索領域を開くかどうか */
	private boolean isOpenSearchArea;

	/** 検索結果件数 */
	private long count;

	/** ページ */
	private Page<Long> page;

}
