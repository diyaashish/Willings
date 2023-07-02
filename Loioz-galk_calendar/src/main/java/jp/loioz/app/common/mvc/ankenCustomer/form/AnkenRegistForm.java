package jp.loioz.app.common.mvc.ankenCustomer.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.common.mvc.ankenCustomer.dto.AnkenSearchResultDto;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.domain.condition.CustomerAnkenSearchCondition;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

/**
 * 案件登録フォームオブジェクト
 */
@Data
public class AnkenRegistForm {

	/**
	 * 顧客-案件登録モーダルの入力フォームオブジェクト
	 */
	@Data
	public static class RegistFragmentInputForm {

		// 表示用
		/** 分野ID選択肢 */
		private List<BunyaDto> bunyaIdSelectOption = Collections.emptyList();

		/** 名簿ID */
		@Required
		private Long personId;

		/** 分野ID */
		@Required
		private Long bunyaId;

	}

	/**
	 * 顧客登録モーダル_顧客検索フラグメントの検索フォームオブジェクト
	 */
	@Data
	public static class SearchFragmentSearchForm {

		/** 検索結果領域を開くかどうか */
		private boolean isOpenSearchResult = false;

		/** 名簿ID */
		private Long personId;

		/** 名前 */
		private String name;

		/** 分野ID */
		private Long bunyaId;

		/** 検索用案件ID */
		private String ankenId;

		/** 案件名 */
		private String ankenName;

		/**
		 * 検索条件に変換する
		 * 
		 * @return 検索条件
		 */
		public CustomerAnkenSearchCondition toCustomerAnkenSearchCondition() {

			String searchName = "";
			String searchAnkenName = "";

			if (!StringUtils.isEmpty(this.name)) {
				// 名前のスペースを除去します。
				// (半角スペースと全角スペースを考慮します。)
				searchName = this.name.replaceAll("[　]|[ ]", "");
			}

			if (!StringUtils.isEmpty(this.ankenName)) {
				// 名前のスペースを除去します。
				// (半角スペースと全角スペースを考慮します。)
				searchAnkenName = this.ankenName.replaceAll("[　]|[ ]", "");
			}

			return CustomerAnkenSearchCondition.builder()
					.personId(this.personId)
					.name(searchName)
					.bunya(this.bunyaId)
					.ankenId(LoiozNumberUtils.parseAsLong(this.ankenId))
					.ankenName(searchAnkenName)
					.build();
		}

	}

	/**
	 * 顧客登録モーダル_顧客検索フラグメントの画面表示用オブジェクト
	 */
	@Data
	public static class SearchFragmentViewForm {

		// 表示用
		/** 分野ID選択肢 */
		private List<BunyaDto> bunyaIdSelectOption = Collections.emptyList();

		/** 名簿ID */
		private Long personId;

		/** 表示件数を超えたフラグ */
		private boolean overViewCntFlg;

		/** 検索結果 */
		private List<AnkenSearchResultDto> searchResultList = Collections.emptyList();

	}

}
