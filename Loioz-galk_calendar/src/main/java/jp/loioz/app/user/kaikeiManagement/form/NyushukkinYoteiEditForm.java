package jp.loioz.app.user.kaikeiManagement.form;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.KaikeiManagementRelationDto;
import jp.loioz.dto.NyushukkinYoteiEditDto;
import lombok.Data;

/**
 * 入出金予定のフォームクラス
 */
@Data
public class NyushukkinYoteiEditForm {

	/** 画面入力情報 */
	@Valid
	private NyushukkinYoteiEditDto nyushukkinYoteiEditDto = new NyushukkinYoteiEditDto();

	/** 更新フラグ */
	private boolean updateFlg = false;

	/** 更新不可フラグ */
	private boolean disabledFlg = false;

	/** 精算によって作成された予定かを判断する */
	private boolean seisanFlg = false;

	/** 精算済の旨のメッセージ表示判定（完了状態の場合はそちらを優先するためfalseになる） */
	private boolean isShowSeisanCompletedMsg = false;

	/** 案件ステータスが完了である旨のメッセージ表示判定 */
	private boolean isShowAnkenCompletedMsg = false;
	
	/** 予定が処理済みの旨のメッセージ表示判定 */
	private boolean isShowYoteiCompletedMsg = false;

	/** 遷移元案件ID */
	private Long transitionAnkenId;

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	/** 入金項目リスト */
	private List<SelectOptionForm> nyukinKomokuList = Collections.emptyList();

	/** 出金項目リスト */
	private List<SelectOptionForm> shukkinKomokuList = Collections.emptyList();

	/** 会計管理の関連情報リスト(顧客と案件情報) */
	private List<KaikeiManagementRelationDto> kaikeiManagementList = Collections.emptyList();

	/** 事務所・担当弁護士の口座リスト */
	private List<GinkoKozaDto> tenantAccountGinkoKozaList = Collections.emptyList();

	/** 支払者：選択用関与者リスト */
	private List<CustomerKanyoshaPulldownDto> kanyoshaList = Collections.emptyList();

	/**
	 * 入金先口座の表示名を取得します。
	 * 
	 * @return 入金先口座の表示名
	 */
	public String getNyukinSakiKozaName() {

		for (GinkoKozaDto dto : tenantAccountGinkoKozaList) {

			if (dto.getGinkoAccountSeq().equals(nyushukkinYoteiEditDto.getNyukinSakiKozaSeq())) {
				return dto.getListDisplayName();
			}
		}
		return null;
	}

}
