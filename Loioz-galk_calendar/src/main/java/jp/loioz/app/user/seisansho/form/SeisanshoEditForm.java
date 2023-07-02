package jp.loioz.app.user.seisansho.form;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import jp.loioz.common.validation.annotation.Required;
import jp.loioz.dto.SeisanshoCreateDto;
import lombok.Data;

/**
 * 精算書作成画面：再計算のリクエスト情報
 */
@Data
public class SeisanshoEditForm {

	/** kaikeikirokuSeq_map */
	private List<Long> kaikeiSeqList;

	/** 顧客ID */
	@Required
	private Long seisanCustomerId;

	/** 案件ID */
	@Required
	private Long seisanAnkenId;

	/** 支払先のアカウント種別 */
	private String kozaAccountType;

	/** 精算額が0円かどうか */
	private boolean isSeisanZero = false;

	/** 精算額 */
	private BigDecimal seisanGakuTotal;

	/** 精算設定情報 */
	@Valid
	private SeisanshoCreateDto seisanshoCreateDto = new SeisanshoCreateDto();
}
