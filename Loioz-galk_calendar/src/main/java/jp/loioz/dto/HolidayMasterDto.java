package jp.loioz.dto;

import org.seasar.doma.Entity;
import org.seasar.doma.jdbc.entity.NamingType;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import lombok.Data;

@Data
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class HolidayMasterDto {

	/** 祝日マスタ連番 */
	private Long holidaySeq;

	/** 祝日 */
	@Required
	@LocalDatePattern
	private String holidayDate;

	/** 祝日名 */
	@Required
	@MaxDigit(max = 20, item = "祝日名")
	private String holidayName;

	/** システム管理者_作成者ID */
	private Long sysCreatedBy;

	/** システム管理者更新日時 */
	private String sysUpdatedAt;

	/** システム管理者更新者ID */
	private Long sysUpdatedBy;

	/** バージョンNo */
	private Long versionNo;
}
