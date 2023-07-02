package jp.loioz.dto;

import java.math.BigDecimal;

import org.seasar.doma.Table;

import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import lombok.Data;

@Data
@Table(name = "t_kaikei_kiroku")
public class KaikeiKirokuDto {

	// ********************************
	// DBと対となる情報
	// ********************************
	// t_kaikei_kiroku
	/** 会計記録SEQ */
	private Long kaikeiKirokuSeq;

	/** 顧客ID */
	private Long customerId;

	/** 案件ID */
	private Long ankenId;

	/** 発生日 */
	@LocalDatePattern
	private String hasseiDate;

	/** 入出金項目ID */
	private Long nyushukkinKomokuId;

	/** 入出金タイプ */
	private String nyushukkinType;

	/** 入金額 */
	private BigDecimal nyukinGaku;

	/** 出金額 */
	private BigDecimal shukkinGaku;

	/** 摘要 */
	@MaxDigit(max = 10000)
	private String tekiyo;

	/** 精算SEQ */
	private Long seisanSeq;

	/** 精算ID */
	private Long seisanId;

	/** 精算処理日 */
	private String seisanDate;

	// t_nyushukkin_yotei
	/** 入出金予定SEQ */
	private Long nyushukkinYoteiSeq;

	/** 入出金予定の精算SEQ */
	private Long yoteiSeisanSeq;

	// m_nyushukkin_komoku
	/** 項目名 */
	private String komokuName;

	/** 課税フラグ */
	private String taxFlg;

	/** 税額 */
	private String taxGaku;

	// t_anken
	/** 案件名 */
	private String ankenName;

	/** 分野 */
	private String bunya;

	/** 案件名(表示用) */
	private String dispAnkenName;

	// t_person
	/** 顧客名 */
	private String customerName;

	/** 登録者 */
	private String creator;
	// ********************************
	// 画面表示用
	// ********************************
	/** 項目 */
	private String item;

	/** 案件ID(表示用) */
	private AnkenId displayAnkenId;

	/** 顧客ID(表示用) */
	private CustomerId displayCustomerId;

	/** 精算ID(表示用) */
	private SeisanId displaySeisanId;

	/** 入金額(表示用) */
	private String displayNyukinGaku;

	/** 出金額(表示用) */
	private String displayShukkinGaku;

	/** 金額(表示用) */
	private String kingaku;

	/** 入金項目ID */
	private Long nyukinKomokuId;

	/** 出金項目ID */
	private Long shukkinKomokuId;

	/** 課税/非課税(表示用) */
	private String taxText;

	/** 報酬項目ID */
	private String hoshuKomokuId;

	/** 消費税率 */
	private String taxRate;

	/** 源泉徴収フラグ */
	private String gensenchoshuFlg;

	/** 源泉徴収額 */
	private String gensenchoshuGaku;

	/** 消費税額 */
	private BigDecimal tax;

	/** 消費税額(表示用) */
	private String displayTax;

	/** プールした(された)かどうか */
	private boolean isPool;

	/** 登録日時 */
	private String createdAt;

	/** 登録アカウントSEQ */
	private Long createdBy;

	// m_account
	/** アカウント名 */
	private String accountName;

	/** 最終編集日時 */
	private String lastEditAt;

	/** 最終編集アカウントSEQ */
	private Long lastEditBy;

	/** 表示用登録者名、登録日時 */
	private String dispCreatedByNameDateTime;

	/** 表示用更新者名、更新日時 */
	private String dispLastEditByNameDateTime;

	/** 精算済みかどうか （精算処理日が登録されているか、案件ステータスが精算完了になっている場合にtrue） */
	private boolean isCompleted;
}