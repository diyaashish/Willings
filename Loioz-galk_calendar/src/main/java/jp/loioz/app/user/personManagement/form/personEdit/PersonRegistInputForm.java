package jp.loioz.app.user.personManagement.form.personEdit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraEpoch;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.validation.annotation.MaxDigit;
import jp.loioz.common.validation.annotation.Required;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;
import lombok.Data;

@Data
public class PersonRegistInputForm {
	
	/** 登録名簿の基本情報入力フォーム */
	private PersonRegistBasicInputForm personRegistBasicInputForm;
	
	/**
	 * 登録名簿の基本情報登録フォーム
	 */
	@Data
	public static class PersonRegistBasicInputForm {
		
		/** 性別選択肢リスト */
		List<SelectOptionForm> genderOptionList = new ArrayList<>();
		
		/** 相談経路選択肢リスト */
		List<SelectOptionForm> sodanRouteOptionList = new ArrayList<>();
		
		// 入力用データのプロパティ
		
		/** 個人・法人・弁護士区分 */
		@Required
		private CustomerType customerType;

		/** 顧客登録日 */
		@Required
		@DateTimeFormat(pattern = "yyyy/MM/dd")
		private LocalDate customerCreatedDate;

		/** 顧客姓・会社名 */
		@Required
		@MaxDigit.List({
			@MaxDigit(max = 24, groups = Kojin.class),
			@MaxDigit(max = 64, groups = Hojin.class)
		})
		private String customerNameSei;

		/** 顧客姓かな・会社名かな */
		@MaxDigit.List({
			@MaxDigit(max = 64, groups = Kojin.class),
			@MaxDigit(max = 128, groups = Hojin.class)
		})
		private String customerNameSeiKana;

		/** 顧客名 個人のみ */
		@MaxDigit(max = 24, groups = Kojin.class)
		private String customerNameMei;

		/** 顧客名かな 個人のみ */
		@MaxDigit(max = 64, groups = Kojin.class)
		private String customerNameMeiKana;

		// 個人顧客付帯情報
		
		/** 旧姓 */
		@MaxDigit(max = 24, groups = Kojin.class)
		private String oldName;

		/** 旧姓かな */
		@MaxDigit(max = 64, groups = Kojin.class)
		private String oldNameKana;

		/** 生年月日：年号 */
		private EraType birthEra;

		/** 生年月日：年号紀元 */
		private EraEpoch eraEpoch;

		/** 生年月日：年 */
		private Long birthYear;

		/** 生年月日：月 */
		private Long birthMonth;

		/** 生年月日：日 */
		private Long birthDay;

		/** 性別 */
		private Gender gender;

		/** 国籍 */
		@MaxDigit(max = 30, groups = Kojin.class)
		private String country;

		/** 言語 */
		@MaxDigit(max = 30, groups = Kojin.class)
		private String language;

		/** 屋号・通称 */
		@MaxDigit(max = 24, groups = Kojin.class)
		private String yago;

		/** 屋号・通称かな */
		@MaxDigit(max = 64, groups = Kojin.class)
		private String yagoKana;
		
		// 法人顧客付帯情報

		/** 代表者 */
		@MaxDigit(max = 24, groups = Hojin.class)
		private String daihyoName;

		/** 代表者かな */
		@MaxDigit(max = 64, groups = Hojin.class)
		private String daihyoNameKana;

		/** 代表者役職 */
		@MaxDigit(max = 24, groups = Hojin.class)
		private String daihyoPositionName;

		/** 担当者 */
		@MaxDigit(max = 24, groups = Hojin.class)
		private String tantoName;

		/** 担当者かな */
		@MaxDigit(max = 64, groups = Hojin.class)
		private String tantoNameKana;
		
		/** 旧商号 */
		@MaxDigit(max = 64, groups = Hojin.class)
		private String oldHojinName;
		
		/** 特記事項 */
		@MaxDigit(max = 3000)
		private String remarks;

		/** 相談経路ID */
		private Long sodanRoute;

		/** 相談経路備考 */
		@MaxDigit(max = 500)
		private String sodanRemarks;

		/** 職業 */
		@MaxDigit(max = 50)
		private String job;

		/** 勤務先 */
		@MaxDigit(max = 50)
		private String workPlace;

		/** 部署 */
		@MaxDigit(max = 50)
		private String bushoName;

		/** 事務所 */
		@MaxDigit(max = 50)
		private String jimushoName;

		/** 死亡フラグ */
		private Boolean deathFlg;

		/** 死亡日：年号 */
		private EraType deathEra;

		/** 死亡日：年号紀元 */
		private EraEpoch deathEraEpoch;

		/** 死亡日：年 */
		private Long deathYear;

		/** 死亡日：月 */
		private Long deathMonth;

		/** 死亡日：日 */
		private Long deathDay;

		// hidden（保存時などに必要なパラメータ）
		
		/** 顧客ID */
		private Long customerId;

		/** 名簿ID（登録処理完了後、この値を設定して、画面へ返却するためのプロパティ） */
		private Long personId;
	}

}