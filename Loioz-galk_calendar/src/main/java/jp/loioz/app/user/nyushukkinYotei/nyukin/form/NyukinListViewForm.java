package jp.loioz.app.user.nyushukkinYotei.nyukin.form;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.NyukinListDto;
import lombok.Data;

@Data
public class NyukinListViewForm {

	/** 入金予定の一覧情報 */
	private List<NyukinListDto> nyukinList = Collections.emptyList();

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
