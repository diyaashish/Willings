package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.dto.GyomuHistoryDto;
import lombok.Data;

@Data
public class GyomuHistoryExcelDto {

	/** 画面表示中の出力日 */
	private String outPutRange;

	/** 出力用顧客ID */
	private String customerId;

	/** 出力用顧客名 */
	private String customerName;

	/** 出力用案件ID */
	private String ankenId;

	/** 出力用案件名 */
	private String ankenName;

	/** 出力用分野名 */
	private String bunyaName;

	/** 遷移軸 */
	private String transitionType;

	/** 業務履歴の一覧表示データ */
	private List<GyomuHistoryDto> gyomuHistoryDtoList = Collections.emptyList();
}
