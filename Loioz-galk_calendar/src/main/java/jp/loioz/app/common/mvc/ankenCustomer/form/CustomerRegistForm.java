package jp.loioz.app.common.mvc.ankenCustomer.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.common.mvc.ankenCustomer.dto.PersonSearchResultDto;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;
import jp.loioz.domain.condition.AnkenCustomerSearchCondition;
import lombok.Data;

/**
 * 顧客登録モーダルのオブジェクト
 */
public class CustomerRegistForm {

	/**
	 * 案件-顧客登録モーダルの入力フォームオブジェクト
	 */
	@Data
	public static class RegistFragmentInputForm {

		/** 案件ID */
		@Required
		private Long ankenId;

		/** 顧客種別 */
		@Required
		private CustomerType customerType;

		/** 顧客姓 */
		@Required.List({
				@Required(groups = Kojin.class),
				@Required(groups = Hojin.class),
		})
		@MaxDigit.List({
				@MaxDigit(groups = Kojin.class, max = 24),
				@MaxDigit(groups = Hojin.class, max = 64),
		})
		private String customerNameSei;

		/** 顧客名 */
		/** 名 */
		@MaxDigit.List({
				@MaxDigit(groups = Kojin.class, max = 24),
		})
		private String customerNameMei;

		/** 顧客せい */
		@MaxDigit.List({
				@MaxDigit(groups = Kojin.class, max = 64),
				@MaxDigit(groups = Hojin.class, max = 128),
		})
		private String customerNameSeiKana;

		/** 顧客めい */
		@MaxDigit.List({
				@MaxDigit(groups = Kojin.class, max = 64),
		})
		private String customerNameMeiKana;

	}

	/**
	 * 顧客登録モーダル_顧客検索フラグメントの検索フォームオブジェクト
	 */
	@Data
	public static class SearchFragmentSearchForm {

		/** 検索結果領域を開くかどうか */
		private boolean isOpenSearchResult = false;

		/** 案件ID */
		private Long ankenId;

		/** 名簿ID */
		private String personId;

		/** 名前 */
		private String customerName;

		/**
		 * 検索条件に変換する
		 * 
		 * @return 検索条件
		 */
		public AnkenCustomerSearchCondition toAnkenCustomerSearchCondition() {

			String searchName = null;

			if (!StringUtils.isEmpty(this.customerName)) {
				// 名前のスペースを除去します。
				// (半角スペースと全角スペースを考慮します。)
				searchName = this.customerName.replaceAll("[　]|[ ]", "");
			}

			return AnkenCustomerSearchCondition.builder()
					.ankenId(this.ankenId)
					.personId(LoiozNumberUtils.parseAsLong(this.personId))
					.name(searchName)
					.build();
		}

	}

	/**
	 * 顧客登録モーダル_顧客検索フラグメントの画面表示用オブジェクト
	 */
	@Data
	public static class SearchFragmentViewForm {

		/** 案件ID */
		private Long ankenId;

		/** 表示件数を超えたフラグ */
		private boolean overViewCntFlg;

		/** 検索結果 */
		private List<PersonSearchResultDto> searchResultList = Collections.emptyList();

	}

}
