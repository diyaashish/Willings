package jp.loioz.app.user.ankenManagement.form;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerJikenDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerSekkenDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenCustomerZaikanDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenKanyoshaViewDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenSosakikanDto;
import jp.loioz.app.user.ankenManagement.dto.AnkenTantoDispDto;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.BengoType;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.dto.BunyaDto;
import lombok.Data;

/**
 * 案件管理（刑事）画面の画面表示フォームクラス
 */
@Data
public class AnkenEditKeijiViewForm {

	/** 案件ID */
	private AnkenId ankenId;

	/** 案件の基本情報表示フォーム */
	private AnkenBasicViewForm ankenBasicViewForm;

	/** 顧客情報一覧 */
	private AnkenCustomerListViewForm ankenCustomerListViewForm;

	/** 捜査機関一覧 */
	private AnkenSosakikanListViewForm ankenSosakikanListViewForm;

	/** 共犯者一覧情報 */
	private AnkenKyohanshaListViewForm ankenKyohanshaListViewForm;

	/** 被害者一覧情報 */
	private AnkenHigaishaListViewForm ankenHigaishaListViewForm;

	/**
	 * 案件基本情報の表示用フォームオブジェクト名
	 */
	@Data
	public static class AnkenBasicViewForm {

		/** 案件ID */
		private AnkenId ankenId;

		/** 案件種別 */
		private AnkenType ankenType;

		/** 案件種別 */
		private String lawyerSelectType;

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

		/** 案件-私選・国選 */
		public String getBengoTypeVal() {
			BengoType bengoType = BengoType.of(this.lawyerSelectType);
			if (bengoType == null) {
				return "";
			}
			return bengoType.getVal();
		}

		/** 裁判件数 */
		private int saibanCount;

	}

	/**
	 * 案件-顧客情報一覧表示用オブジェクト
	 */
	@Data
	public static class AnkenCustomerListViewForm {

		// 新規予定登録で使用する案件情報プロパティ
		/** 案件ID */
		private AnkenId ankenId;

		/** 顧客情報 */
		List<AnkenCustomerDto> ankenCustomerDto = Collections.emptyList();

	}

	/**
	 * 案件顧客-事件情報一覧表示用オブジェクト
	 */
	@Data
	public static class AnkenCustomerJikenListViewForm {

		/** 選択顧客が完了済 */
		private boolean isCompleted;

		/** 顧客-事件情報 */
		List<AnkenCustomerJikenDto> ankenCustomerJikenDtoList = Collections.emptyList();

	}

	/**
	 * 案件顧客-接見情報一覧表示用オブジェクト
	 */
	@Data
	public static class AnkenCustomerSekkenListViewForm {

		/** 選択顧客が完了済 */
		private boolean isCompleted;

		/** 顧客-接見情報 */
		List<AnkenCustomerSekkenDto> ankenCustomerSekkenDtoList = Collections.emptyList();

	}

	/**
	 * 案件顧客-在監情報一覧表示用オブジェクト
	 */
	@Data
	public static class AnkenCustomerZaikanListViewForm {

		/** 選択顧客が完了済 */
		private boolean isCompleted;

		/** 顧客-在監場所情報 */
		List<AnkenCustomerZaikanDto> ankenCustomerZaikanDtoList = Collections.emptyList();
	}

	/**
	 * 捜査機関の表示フォーム
	 */
	@Data
	public static class AnkenSosakikanListViewForm {

		/** 捜査機関 */
		private List<AnkenSosakikanDto> ankenSosakikanDtoList;

	}

	/**
	 * 共犯者（関与者）情報
	 */
	@Data
	public static class AnkenKyohanshaListViewForm {

		/** 共犯者 */
		private List<AnkenKanyoshaViewDto> ankenKanyoshaViewDtoList;
	}

	/**
	 * 被害者（関与者）情報
	 */
	@Data
	public static class AnkenHigaishaListViewForm {

		/** 被害者 */
		private List<AnkenKanyoshaViewDto> ankenKanyoshaViewDtoList;

	}

}
