package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.Data;

/**
 * 預り金・未収入金管理-検索用のDtoクラス
 */
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Data
public class AzukarikinSearchDto {

	/** 顧客ID */
	private Long customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID */
	private Long ankenId;

	/** 分野ID */
	private Long bunyaId;

	/** 案件名 */
	private String ankenName;

	/** 案件ステータス */
	private String ankenStatus;

	/** 売上計上先 */
	private String uriageKizokuSaki;

	/** 担当弁護士 */
	private String tantoLawyer;

	/** 担当事務 */
	private String tantoJimu;

	/** 源泉徴収 */
	private Long gensenChosyu;

	/** 入金 */
	private Long nyukinGaku;

	/** 出金 */
	private Long shukkinGaku;

	/** 報酬 */
	private String hosyuGaku;

	/** 消費税 */
	private String tax;

	/** 源泉徴収 */
	private Long gensenchoshuGaku;

	/** 業歴最終更新日 */
	private String gyomuHistory;

	/** 最終入金日 */
	private String nyushukkin;
}