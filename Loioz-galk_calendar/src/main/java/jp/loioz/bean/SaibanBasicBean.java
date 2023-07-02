package jp.loioz.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;

import lombok.Data;

@Data
@Entity
public class SaibanBasicBean {

	/** 裁判SEQ */
	@Column(name = "saiban_seq")
	private Long saibanSeq;

	/** 裁判枝番 */
	@Column(name = "saiban_branch_no")
	private String saibanBranchNo;

	/** 裁判進捗 */
	@Column(name = "saiban_status")
	private String saibanStatus;

	/** 開始日 */
	@Column(name = "saiban_start_date")
	private LocalDate saibanStartDate;

	/** 終了日 */
	@Column(name = "saiban_end_date")
	private LocalDate saibanEndDate;

	/** 裁判所ID */
	@Column(name = "saibansho_id")
	private Long saibanshoId;

	/** 裁判所名（裁判所マスタの裁判所名） */
	@Column(name = "saibansho_mst_name")
	private String saibanshoMstName;

	/** 裁判所名 */
	@Column(name = "saibansho_name")
	private String saibanshoName;

	/** 係属部 */
	@Column(name = "keizoku_bu_name")
	private String keizokuBuName;

	/** 係属部電話番号 */
	@Column(name = "keizoku_bu_tel_no")
	private String keizokuBuTelNo;

	/** 係属部FAX番号 */
	@Column(name = "keizoku_bu_fax_no")
	private String keizokuBuFaxNo;

	/** 係属係 */
	@Column(name = "keizoku_kakari_name")
	private String keizokuKakariName;

	/** 担当書記官 */
	@Column(name = "tanto_shoki")
	private String tantoShoki;

	/** 結果 */
	@Column(name = "saiban_result")
	private String saibanResult;

	/** 事件-元号 */
	@Column(name = "jiken_gengo")
	private String jikenGengo;

	/** 事件-年度 */
	@Column(name = "jiken_year")
	private String jikenYear;

	/** 事件-符号 */
	@Column(name = "jiken_mark")
	private String jikenMark;

	/** 事件-No */
	@Column(name = "jiken_no")
	private String jikenNo;

	/** 事件-事件名 */
	@Column(name = "jiken_name")
	private String jikenName;

	/** 事件ツリーSEQ */
	@Column(name = "saiban_tree_seq")
	private Long saibanTreeSeq;

	/** 親SEQ */
	@Column(name = "parent_seq")
	private Long parentSeq;

	/** 併合反訴種別 */
	@Column(name = "connect_type")
	private String connectType;

	/** 本訴フラグ */
	@Column(name = "honso_flg")
	private String honsoFlg;

	/** 基本事件フラグ */
	@Column(name = "kihon_flg")
	private String kihonFlg;

	/** 登録日時 */
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	/** 登録者 */
	@Column(name = "created_by")
	private Long createdBy;

	/** 更新日時 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/** 更新者 */
	@Column(name = "updated_by")
	private Long updatedBy;

}
