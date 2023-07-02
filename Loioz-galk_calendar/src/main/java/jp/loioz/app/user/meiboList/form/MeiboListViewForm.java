package jp.loioz.app.user.meiboList.form;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;

import jp.loioz.app.user.meiboList.dto.AllMeiboListDto;
import jp.loioz.app.user.meiboList.dto.BengoshiMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerAllMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerHojinMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerKojinMeiboListDto;
import jp.loioz.app.user.meiboList.dto.AdvisorMeiboListDto;
import jp.loioz.bean.meiboList.AllMeiboListBean;
import jp.loioz.common.constant.meiboList.MeiboListConstant;
import lombok.Data;

/**
 * 顧客一覧画面の画面表示フォームクラス
 */
@Data
public class MeiboListViewForm {

	/** 名簿メニュフォーム */
	private MeiboMenuViewForm meiboMenuViewForm;

	/** 名簿検索フォーム */
	private MeiboSearchViewForm meiboSearchViewForm;

	/** 名簿一覧：すべての一覧フォーム */
	private AllMeiboListViewForm allMeiboListViewForm;

	/** 名簿一覧：顧客の一覧フォーム */
	private CustomerAllMeiboListViewForm customerAllMeiboListViewForm;

	/** 名簿一覧：個人顧客の一覧フォーム */
	private CustomerKojinMeiboListViewForm customerKojinMeiboListViewForm;

	/** 名簿一覧：法人顧客の一覧フォーム */
	private CustomerHojinMeiboListViewForm customerHojinMeiboListViewForm;

	/** 名簿一覧：顧問の一覧フォーム */
	private AdvisorMeiboListViewForm advisorMeiboListViewForm;

	/** 名簿一覧：弁護士の一覧フォーム */
	private BengoshiMeiboListViewForm bengoshiMeiboListViewForm;

	/**
	 * 名簿メニュー画面表示オブジェクト
	 */
	@Data
	public static class MeiboMenuViewForm {
	}

	/**
	 * 名簿検索画面表示オブジェクト
	 */
	@Data
	public static class MeiboSearchViewForm {
	}

	/**
	 * 「名簿一覧：すべて」の画面表示用オブジェクト
	 */
	@Data
	public static class AllMeiboListViewForm {

		/** すべての一覧情報 */
		List<AllMeiboListDto> allMeiboList = Collections.emptyList();

		/** 「すべて」のページャ情報 */
		Page<AllMeiboListBean> allMeiboPage;

		/** 名簿メニュー */
		final String SELECT_MEIBO_MENU = MeiboListConstant.MeiboMenu.ALL.getCd();
	}

	/**
	 * 「名簿一覧：顧客」の画面表示用オブジェクト
	 */
	@Data
	public static class CustomerAllMeiboListViewForm {

		/** 顧客の一覧情報 */
		List<CustomerAllMeiboListDto> customerAllMeiboListDto = Collections.emptyList();

		/** 「顧客」のページャ情報 */
		Page<Long> customerAllMeiboPage;

		/** 名簿メニュー */
		final String SELECT_MEIBO_MENU = MeiboListConstant.MeiboMenu.CUSTOMER_ALL.getCd();
	}

	/**
	 * 「名簿一覧：個人顧客」の画面表示用オブジェクト
	 */
	@Data
	public static class CustomerKojinMeiboListViewForm {

		/** 個人顧客の一覧情報 */
		List<CustomerKojinMeiboListDto> customerKojinMeiboListDto = Collections.emptyList();

		/** 「個人顧客」のページャ情報 */
		Page<Long> customerKojinMeiboPage;

		/** 名簿メニュー */
		final String SELECT_MEIBO_MENU = MeiboListConstant.MeiboMenu.CUSTOMER_KOJIN.getCd();

	}

	/**
	 * 「名簿一覧：法人顧客」の画面表示用オブジェクト
	 */
	@Data
	public static class CustomerHojinMeiboListViewForm {

		/** 法人顧客の一覧情報 */
		List<CustomerHojinMeiboListDto> customerHojinMeiboListDto = Collections.emptyList();

		/** 「法人顧客」のページャ情報 */
		Page<Long> customerHojinMeiboPage;

		/** 名簿メニュー */
		final String SELECT_MEIBO_MENU = MeiboListConstant.MeiboMenu.CUSTOMER_HOJIN.getCd();
	}

	/**
	 * 「名簿一覧：顧問」の画面表示用オブジェクト
	 */
	@Data
	public static class AdvisorMeiboListViewForm {

		/** 顧問の一覧情報 */
		List<AdvisorMeiboListDto> advisorMeiboListDto = Collections.emptyList();

		/** 「顧問」のページャ情報 */
		Page<Long> advisorMeiboPage;

		/** 名簿メニュー */
		final String SELECT_MEIBO_MENU = MeiboListConstant.MeiboMenu.ADVISOR.getCd();
	}

	/**
	 * 「名簿一覧：弁護士」の画面表示用オブジェクト
	 */
	@Data
	public static class BengoshiMeiboListViewForm {

		/** 弁護士の一覧情報 */
		List<BengoshiMeiboListDto> bengoshiMeiboListDto = Collections.emptyList();

		/** 「弁護士」のページャ情報 */
		Page<Long> bengoshiMeiboPage;

		/** 名簿メニュー */
		final String SELECT_MEIBO_MENU = MeiboListConstant.MeiboMenu.BENGOSHI.getCd();
	}

}
