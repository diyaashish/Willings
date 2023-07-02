package jp.loioz.app.user.taskManagement.dto;

import java.util.List;

import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.domain.value.AnkenId;
import lombok.Builder;
import lombok.Data;

/**
 * 案件タスク追加情報
 */
@Data
public class TaskAnkenDto {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件名 */
	private String ankenName;

	/** 顧客名 */
	private String customerName;

	/** 顧客数 */
	private Long numberOfCustomer;

	/** タスク数 */
	private Long numberOfTask;

	/** 表示順 */
	private Long dispOrder;

	/*****************************
	 * 案件詳細
	 *****************************/

	/** 案件種別 */
	private AnkenType AnkenType;

	/** 刑事判定 */
	private boolean isKeiji;

	/** 相手方 */
	private List<OpposingParty> opposingPartyList;

	/** 共犯者 */
	private List<Accomplice> accompliceList;

	/** 被害者 */
	private List<Victim> victimList;

	/**
	 * 案件の相手方情報
	 *
	 */
	@Data
	@Builder
	public static class OpposingParty {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 名簿ID */
		private Long personId;

		/** 関与者種別 */
		private String kanyoshaType;

		/** 代理フラグ */
		private String dairiFlg;

		/** 関与者名 */
		private String kanyoshaName;

		/** 代理人関与者SEQ */
		private Long relatedKanyoshaSeq;

		/** 代理人名 */
		private String lawyerName;

	}

	/**
	 * 案件の共犯者情報
	 *
	 */
	@Data
	@Builder
	public static class Accomplice {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 名簿ID */
		private Long personId;

		/** 関与者種別 */
		private String kanyoshaType;

		/** 代理フラグ */
		private String dairiFlg;

		/** 関与者名 */
		private String kanyoshaName;

		/** 代理人関与者SEQ */
		private Long relatedKanyoshaSeq;

		/** 代理人名 */
		private String lawyerName;

	}

	/**
	 * 案件の被害者情報
	 *
	 */
	@Data
	@Builder
	public static class Victim {

		/** 関与者SEQ */
		private Long kanyoshaSeq;

		/** 名簿ID */
		private Long personId;

		/** 関与者種別 */
		private String kanyoshaType;

		/** 代理フラグ */
		private String dairiFlg;

		/** 関与者名 */
		private String kanyoshaName;

		/** 代理人関与者SEQ */
		private Long relatedKanyoshaSeq;

		/** 代理人名 */
		private String lawyerName;
	}

}
