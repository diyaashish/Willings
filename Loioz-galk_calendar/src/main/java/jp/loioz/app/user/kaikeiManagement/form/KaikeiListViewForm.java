package jp.loioz.app.user.kaikeiManagement.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.kaikeiManagement.dto.AnkenMeisaiListDto;
import jp.loioz.app.user.kaikeiManagement.dto.TimeChargeDownloadSelectDto;
import jp.loioz.dto.KaikeiKirokuDto;
import jp.loioz.dto.NyushukkinYoteiListDto;
import jp.loioz.dto.SeisanKirokuDto;
import lombok.Data;

/**
 * 会計管理画面のフォームクラス
 */
@Data
public class KaikeiListViewForm {

	/** 分野 */
	private Long bunya;

	/** 遷移元が案件か */
	private boolean isTransitionFromAnken;

	/** 精算書タブ表示 */
	private Boolean isSeisanshoTab = false;

	// --------------------------------------------
	// 案件明細
	// --------------------------------------------
	/** 案件明細情報が存在するかどうか */
	private boolean isExistAnkenMeisai;

	/** 案件明細一覧 */
	private List<AnkenMeisaiListDto> ankenMeisaiListDtos;

	/** 未完了の案件数 */
	private int notCompletedAnkenCount;

	/** 未完了の案件ID 「未完了の案件数」が１の場合に対象案件IDを保持する */
	private long notCompletedFirstAnkenId;

	/** 未完了の顧客ID 「未完了の案件数」が１の場合に対象顧客IDを保持する */
	private long notCompletedFirstCustomerId;

	/** 遷移元案件ID */
	private Long transitionAnkenId;

	/** 遷移元顧客ID */
	private Long transitionCustomerId;

	// --------------------------------------------
	// 会計記録
	// --------------------------------------------
	/** 会計記録情報が存在するかどうか */
	private boolean isExistKaikeiKiroku;

	/** 会計記録一覧 */
	private List<KaikeiKirokuDto> kaikeiKirokuList;

	/** 会計記録SEQ */
	private Long kaikeiKirokuSeq;

	/** 入金額の合計 */
	private String kaikeiKirokuNyukinTotal;

	/** 出金額の合計 */
	private String kaikeiKirokuShukkinTotal;

	// --------------------------------------------
	// 入出金予定
	// --------------------------------------------
	/** 入金予定情報が存在するかどうか */
	private boolean isExistNyushukkinYotei;

	/** 入金予定一覧 */
	private List<NyushukkinYoteiListDto> nyushukkinYoteiList;

	// --------------------------------------------
	// 精算記録
	// --------------------------------------------
	/** 精算記録情報が存在するかどうか */
	private boolean isExistSeisanKiroku;

	/** 精算記録一覧 */
	private List<SeisanKirokuDto> seisanKirokuList;

	/** 案件明細情報にタイムチャージが存在するかどうか */
	/** タイムチャージ出力ボタンを表示するかどうか */
	private boolean isDisplayTimeChargeBtn;

	/** タイムチャージ出力時の対象選択用Dot */
	private List<TimeChargeDownloadSelectDto> timeChargeDownloadSelectDto = Collections.emptyList();
	// ***********************************
	// 会計管理画面では不要だが、
	// 精算書作成画面で使用しているため残す。
	// ***********************************
	/** 消費税の合計 */
	private String kaikeiKirokuTaxTotal;

	/** 源泉徴収の合計 */
	private String kaikeiKirokuGensenTotal;

	/** 差し引き計 */
	private String kaikeiKirokuSashiHikiTotal;

	/** 遷移元判定フラグ */
	private String transitionFlg;

}
