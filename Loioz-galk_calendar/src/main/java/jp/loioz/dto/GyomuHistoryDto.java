package jp.loioz.dto;

import java.time.LocalDateTime;
import java.util.List;

import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import lombok.Data;

/**
 * 業務履歴の帳票出力用のDtoクラス
 *
 * @author ichimura
 *
 */
@Data
public class GyomuHistoryDto {
	/** 業務履歴SEQ */
	private Long gyomuHistorySeq;

	/** 遷移元種別 */
	private TransitionType transitionType;

	/** 件名 */
	private String subject;

	/** 本文 */
	private String mainText;

	/** 登録者 */
	private String createrName;
	
	/** 対応日時 */
	private LocalDateTime supportedDateTime;

	/** 顧客ID */
	private CustomerId customerId;

	/** 顧客名 */
	private String customerName;

	/** 案件ID(業務履歴出力用) */
	private AnkenId ankenId;

	/** 案件ID(業務履歴出力用) */
	private String ankenName;

	/** 分野名（業務履歴出力用） */
	private String bunyaName;

	/** 案件のリスト（案件情報用） */
	private List<AnkenDtoForGyomuHistory> ankenDtoList;

	/** 裁判ブランチNo */
	private String saibanBranchNo;

	/** 事件名 */
	private String jikenName;
	
	/** 事件番号 */
	private CaseNumber caseNumber;

}
