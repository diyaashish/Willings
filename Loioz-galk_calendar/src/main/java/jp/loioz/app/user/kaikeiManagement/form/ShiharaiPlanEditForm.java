package jp.loioz.app.user.kaikeiManagement.form;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.validation.annotation.LocalDatePattern;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.MaxNumericValue;
import jp.loioz.common.validation.annotation.MinNumericValue;
import jp.loioz.common.validation.annotation.Numeric;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.dto.ShiharaiPlanDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支払い計画のフォームクラス
 */
@Data
public class ShiharaiPlanEditForm {

	/** 支払計画Dto */
	@Valid
	private ShiharaiPlanDto shiharaiPlanDto = new ShiharaiPlanDto();

	/** 支払計画の一覧表示用に格納 */
	private List<ShiharaiPlanYotei> shiharaiPlanYoteiList = Collections.emptyList();

	/** 残金を回収不能として処理する */
	private boolean uncollectible = false;

	/** 精算完了かどうか */
	private boolean isSeisanCompleted = false;
	
	/** 精算済の旨のメッセージ表示判定（完了状態の場合はそちらを優先するためfalseになる） */
	private boolean isShowSeisanCompletedMsg = false;

	/** 案件ステータスが完了である旨のメッセージ表示判定 */
	private boolean isShowAnkenCompletedMsg = false;
	
	/** 支払い計画が完了である旨のメッセージ表示判定 */
	private boolean isShowPlanCompletedMsg = false;

	/** 支払計画編集 request格納用 (key : nyushukkinYorteiSeq) */
	@Valid
	private Map<Long, ShiharaiPlanYotei> shiharaiPlanYoteiEdit = new HashMap<>();

	/** 支払計画追加 request格納用 (key : index) */
	@Valid
	private Map<Long, ShiharaiPlanYotei> shiharaiPlanYoteiNewAdd = new HashMap<>();

	/** 表示するデータ内に実績登録済が存在するか判定 */
	public boolean exsitsJissekiData() {
		return this.shiharaiPlanYoteiList.stream().anyMatch(ShiharaiPlanYotei::isCompleted);
	}

	/**
	 * 支払計画の編集情報クラス
	 */
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ShiharaiPlanYotei {

		/** 支払計画追加連番番号 */
		private Long newAddIndex;

		/** 入出金予定SEQ */
		private Long nyushukkinYoteiSeq;

		/** 入出金予定日 */
		@Required
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String nyushukkinYoteiDate;

		/** 入出金予定額 */
		@Required
		@Numeric
		@MaxNumericValue(max = 999999999)
		@MinNumericValue(min = 1)
		private String nyushukkinYoteiGaku;

		/** 入出金予定額 表示用 */
		private String dispNyushukkinYoteiGaku;

		/** 入出金日 */
		@LocalDatePattern(format = DateUtils.DATE_FORMAT_SLASH_DELIMITED)
		private String nyushukkinDate;

		/** 入出金額 */
		@Numeric
		@MaxNumericValue(max = 999999999)
		@MinNumericValue(min = 1)
		private String nyushukkinGaku;

		/** 入出金予定額 表示用 */
		private String dispNyushukkinGaku;

		/** 摘要 */
		@MaxDigit(max = 10000)
		private String tekiyo;

		/** disable判定用 実績が登録されたものは編集不可 */
		private boolean isCompleted;

		/** 新規追加データか識別する */
		public boolean isNewAdd() {
			/** 保存時requestData格納先を指定 */
			return this.nyushukkinYoteiSeq == null;
		}

		/** 更新時requestData格納先を指定 */
		public String responseTarget() {
			if (this.nyushukkinYoteiSeq != null) {
				return "shiharaiPlanYoteiEdit[" + this.nyushukkinYoteiSeq + "]"; // 支払計画編集 Response用
			} else {
				return "shiharaiPlanYoteiNewAdd[" + this.newAddIndex + "]";// 支払計画追加 Response用
			}
		}

		/** Stirng -> BigDecimal */
		public BigDecimal decimalNyushukkinYoteiGaku() {
			return LoiozNumberUtils.parseAsBigDecimal(this.nyushukkinYoteiGaku);
		}

		/** Stirng -> BigDecimal */
		public BigDecimal decimalNyushukkinGaku() {
			return LoiozNumberUtils.parseAsBigDecimal(this.nyushukkinGaku);
		}
	}
}