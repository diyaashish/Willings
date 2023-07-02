package jp.loioz.app.user.ankenManagement.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenKanyoshaViewDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenTantoDispDto;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

/**
 * 案件管理画面（民事）の画面表示フォームクラス
 */
@Data
public class AnkenEditMinjiViewForm {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件の基本情報表示フォーム */
	private AnkenBasicViewForm ankenBasicViewForm;

	/** 案件顧客情報一覧 */
	private AnkenCustomerListViewForm ankenCustomerListViewForm;

	/** 案件相手方一覧情報 */
	private AnkenAitegataListViewForm ankenAitegataListViewForm;

	/**
	 * 案件基本情報の表示用フォームオブジェクト名
	 */
	@Data
	public static class AnkenBasicViewForm {

		/** 案件ID */
		private AnkenId ankenId;

		/** 案件種別 */
		private AnkenType ankenType;

		/** 案件名 */
		private String ankenName;

		/** 分野 */
		private BunyaDto bunya;

		/** 案件作成日 */
		private String ankenCreatedDate;

		/** 売上計上先 */
		private List<AnkenTantoDispDto> salesOwner = Collections.emptyList();

		/** 担当弁護士 */
		private List<AnkenTantoDispDto> tantoLaywer = Collections.emptyList();

		/** 担当事務 */
		private List<AnkenTantoDispDto> tantoJimu = Collections.emptyList();

		/** 事案概要 */
		private String jianSummary;

		/** 裁判件数 */
		private int saibanCount;
	}

	/**
	 * 案件-顧客情報一覧表示用オブジェクト
	 */
	@Data
	public static class AnkenCustomerListViewForm {

		/** 案件ID */
		private AnkenId ankenId;

		/** 顧客情報 */
		List<AnkenCustomerDto> ankenCustomerDto = Collections.emptyList();

	}

	/**
	 * 案件関与者情報
	 */
	@Data
	public static class AnkenAitegataListViewForm {

		/** 案件 相手方 */
		private List<AnkenKanyoshaViewDto> ankenKanyoshaViewDtoList;

	}

}
