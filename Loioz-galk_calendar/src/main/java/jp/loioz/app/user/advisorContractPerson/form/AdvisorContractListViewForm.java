package jp.loioz.app.user.advisorContractPerson.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.common.form.WrapHeaderForm;
import jp.loioz.common.constant.CommonConstant.ContractStatus;
import jp.loioz.common.constant.CommonConstant.ContractType;
import jp.loioz.entity.TAdvisorContractEntity;
import lombok.Data;

/**
 * 顧問契約一覧画面（名簿）表示フォーム
 */
@Data
public class AdvisorContractListViewForm {
	
	/** 画面ヘッダー */
	private WrapHeaderForm wrapHeader;

	/** 名簿ID */
	private Long personId;
	
	/** 顧問契約一覧リスト */
	private List<AdvisorContractListRowDto> advisorContractRowDtoList = new ArrayList<>();
	
	/** ページ */
	private Page<TAdvisorContractEntity> page;
	
	/**
	 * 顧問契約一覧（名簿）の行データDto
	 */
	@Data
	public static class AdvisorContractListRowDto {
		
		/** 顧問契約SEQ */
		private Long advisorContractSeq;
		
		/** 契約開始日 */
		private LocalDate contractStartDate;
		
		/** 契約終了日 */
		private LocalDate contractEndDate;
		
		/** 顧問料金（月額） */
		private Long contractMonthCharge;
		
		/** 稼働時間（時間/月） */
		private Long contractMonthTime;
		
		/** 契約状況 */
		private ContractStatus contractStatus;
		
		/** 契約内容 */
		private String contractContent;
		
		/** 契約区分 */
		private ContractType contractType;
		
		/** 契約メモ */
		private String contractMemo;
		
		/** 売上計上先情報一覧 */
		private List<AdvisorContractTantoInfoDto> salesOwnerList;
		
		/** 担当弁護士情報一覧 */
		private List<AdvisorContractTantoInfoDto> tantoLawyerList;

		/** 担当事務情報一覧 */
		private List<AdvisorContractTantoInfoDto> tantoJimuList;
		
		@Data
		public static class AdvisorContractTantoInfoDto{
			/** 顧問契約SEQ */
			private Long advisorContractSeq;
			
			/** 担当タイプ */
			private String tantoType;
			
			/** 担当タイプ枝番 */
			private String tantoTypeBranchNo;
			
			/** メイン担当フラグ */
			private boolean mainTantoFlg;

			/** 担当者名 */
			private String tantoName;
		}

		/**
		 * 「契約状況」の表示用文字列を取得
		 * 
		 * @return
		 */
		public String getContractStatusForDisp() {
			if (contractStatus == null) {
				return "";
			}
			return contractStatus.getVal();
		}

		/**
		 * 「契約区分」の表示用文字列を取得
		 * 
		 * @return
		 */
		public String getContractTypeForDisp() {
			if (contractType == null) {
				return "";
			}
			
			String contractTypeForDisp = "";
			
			if (contractType == ContractType.JIMUSHO) {
				contractTypeForDisp = "事務所契約";
			} else if (contractType == ContractType.KOJIN) {
				contractTypeForDisp = "個人契約";
			} else {
				contractTypeForDisp = contractType.getVal();
			}
			
			return contractTypeForDisp;
		}
	}
}
