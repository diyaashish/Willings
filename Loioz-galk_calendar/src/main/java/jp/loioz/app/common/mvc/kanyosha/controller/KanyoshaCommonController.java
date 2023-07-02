package jp.loioz.app.common.mvc.kanyosha.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.mvc.kanyosha.form.KanyoshaEditModalInputForm;
import jp.loioz.app.common.mvc.kanyosha.form.KanyoshaSearchModalSearchForm;
import jp.loioz.app.common.mvc.kanyosha.form.KanyoshaSearchModalViewForm;
import jp.loioz.app.common.mvc.kanyosha.service.KanyoshaCommonService;
import jp.loioz.app.common.validation.accessDB.CommonKanyoshaValidator;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.validation.groups.Hojin;
import jp.loioz.common.validation.groups.Kojin;
import jp.loioz.common.validation.groups.Lawyer;

/**
 * 関与者の共通コントローラー
 */
@Controller
@RequestMapping(value = "common/mvc/kanyosha")
public class KanyoshaCommonController extends DefaultController {

	/** 共通関与者編集モーダルのコントローラーパス */
	private static final String KANYOSHA_EDIT_MODAL_PATH = "common/mvc/kanyosha/kanyoshaEditModal";
	/** 共通関与者編集モーダルのフラグメントパス */
	private static final String KANYOSHA_EDIT_MODAL_FRAGMENT_PATH = KANYOSHA_EDIT_MODAL_PATH + "::kanyoshaEditModalFragment";
	/** 共通関与者編集モーダルのフラグメントフォームオブジェクト名 */
	private static final String KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME = "kanyoshaEditModalInputForm";

	/** 共通関与者検索モーダルのフラグメントパス */
	private static final String KANYOSHA_SEARCH_MODAL_FRAGMENT_PATH = KANYOSHA_EDIT_MODAL_PATH + "::kanyoshaSearchModalFragment";
	/** 共通関与者検索モーダルのフラグメントフォームオブジェクト名 */
	private static final String KANYOSHA_SEARCH_MODAL_VIEW_FORM_NAME = "kanyoshaSearchModalViewForm";
	/** 共通関与者検索モーダルのフラグメントフォームオブジェクト名 */
	private static final String KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME = "kanyoshaSearchModalSearchForm";

	/** 共通関与者モーダル：タイトル */
	public static final String KANYOSHA_TITLE = "当事者・関与者";

	@Autowired
	private KanyoshaCommonService kanyoshaCommonService;

	@Autowired
	private CommonKanyoshaValidator commonKanyoshaValidator;

	// Commonなのでindexメソッドはなし

	/**
	 * 関与者編集モーダルの新規作成用
	 * 
	 * @param ankenId
	 * @param customerType
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaEditModalFragment/create", method = RequestMethod.GET)
	public ModelAndView getKanyoshaCreateModalFragment(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "initCustomerType", required = false) CustomerType customerType) {

		ModelAndView mv = null;
		try {
			KanyoshaEditModalInputForm inputForm = kanyoshaCommonService.createKanyoshaEditInputForm(ankenId, customerType);
			mv = getMyModelAndView(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME);

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 関与者編集モーダルの編集用
	 * 
	 * @param kanyoshaSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaEditModalFragment/{kanyoshaSeq}", method = RequestMethod.GET)
	public ModelAndView getKanyoshaEditModalFragment(@PathVariable(name = "kanyoshaSeq") Long kanyoshaSeq) {

		ModelAndView mv = null;

		try {
			KanyoshaEditModalInputForm inputForm = kanyoshaCommonService.createKanyoshaEditInputForm(kanyoshaSeq);
			mv = getMyModelAndView(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME);

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 関与者検索モーダル
	 * 
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragment(@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {

		ModelAndView mv = null;

		KanyoshaSearchModalViewForm viewForm = kanyoshaCommonService.createKanyoshaSearchModalViewForm(searchForm);
		mv = getMyModelAndView(viewForm, KANYOSHA_SEARCH_MODAL_FRAGMENT_PATH, KANYOSHA_SEARCH_MODAL_VIEW_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 個人の場合、新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyosha", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojin(@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyosha(inputForm, result);
	}

	/**
	 * 法人の場合、新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyosha", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojin(@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyosha(inputForm, result);
	}

	/**
	 * 弁護士の場合、新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyosha", params = "lawyer", method = RequestMethod.POST)
	public ModelAndView registKanyoshaLawyer(@Validated({Default.class, Lawyer.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyosha(inputForm, result);
	}

	/**
	 * 関与者モーダル：共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyosha(KanyoshaEditModalInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;

		try {
			// 表示用プロパティを設定する
			kanyoshaCommonService.setDispProperties(inputForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return null;
		}

		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfo(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, KANYOSHA_TITLE));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 共通関与者、更新処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateKanyosha", method = RequestMethod.POST)
	public ModelAndView updateKanyosha(@Validated({Default.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;
		try {
			// 表示用プロパティを設定する
			kanyoshaCommonService.setDispProperties(inputForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return null;
		}

		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.updateKanyoshaInfo(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, KANYOSHA_TITLE));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 削除処理前チェック
	 * 
	 * @param kanyoshaSeq
	 * @param ankenId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteKanyoshaBeforeCheck", method = RequestMethod.POST)
	public Map<String, Object> deleteKanyoshaBeforeCheck(@RequestParam Long kanyoshaSeq) {

		Map<String, Object> response = new HashMap<>();
		try {
			response = kanyoshaCommonService.deleteKanyoshaBeforeCheck(kanyoshaSeq);
		} catch (AppException ex) {
			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {
			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return null;
		}

		return response;
	}

	/**
	 * 関与者の削除処理
	 *
	 * @param kanyoshaSeq
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/deleteKanyosha", method = RequestMethod.POST)
	public ModelAndView deleteKanyosha(@RequestParam Long kanyoshaSeq) {

		ModelAndView mv = null;

		Map<String, String> errorMessage = new HashMap<>();
		// DB整合姓チェック
		if (!this.accessDBValidForDelete(kanyoshaSeq, errorMessage)) {
			super.setAjaxProcResultFailure(errorMessage.get("message"));
			return null;
		}

		try {
			// 関与者の登録処理
			kanyoshaCommonService.deleteKanyoshaInfo(kanyoshaSeq);

		} catch (AppException e) {
			// エラー内容を返却
			super.setAjaxProcResultFailure(getMessage(e.getErrorType()));
			return null;
		}

		// 正常に削除できたケースは、HTMLがないのでnull
		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, KANYOSHA_TITLE));
		return mv;
	}

	/**
	 * 削除時のDB整合バリデート
	 * 
	 * @param form
	 * @param response
	 * @return
	 */
	private boolean accessDBValidForDelete(Long kanyoshaSeq, Map<String, String> errorMap) {

		// 削除が不可能な場合、ここで返却
		if (!commonKanyoshaValidator.canDeleteKanyosha(kanyoshaSeq)) {
			errorMap.put("message", getMessage(MessageEnum.MSG_E00086, "会計情報が登録されている。もしくは預かり元として登録されている"));
			return false;
		}

		return true;
	}

	/**
	 * 関与者検索モーダルの検索結果から登録
	 * 
	 * @param ankenId
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaSearchResult", method = RequestMethod.POST)
	public ModelAndView registKanyoshaSearchResult(@RequestParam(name = "ankenId") Long ankenId, @RequestParam(name = "personId") Long personId) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResult(ankenId, personId);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00023, KANYOSHA_TITLE));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// **********************************************************************************
	// 案件画面 「名簿から検索する」
	// **********************************************************************************

	/**
	 * 関与者検索モーダル：案件相手方
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/anken/aitegata", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentAnkenAitegata(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.AITEGATA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_OFF.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.LAWYER.getCd()));
		return getKanyoshaSearchModalFragmentAnkenRelatedKanyosha(searchForm, KanyoshaType.AITEGATA, SystemFlg.FLG_OFF);
	}

	/**
	 * 関与者検索モーダル：案件相手方代理人
	 *
	 * @param searchForm
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/anken/aitegataDairinin", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentAnkenAitegataDairinin(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.AITEGATA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_ON.getCd());
		return getKanyoshaSearchModalFragmentAnkenRelatedKanyosha(searchForm, KanyoshaType.AITEGATA, SystemFlg.FLG_ON);
	}

	/**
	 * 関与者検索モーダル：案件共犯者
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/anken/kyohansha", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentAnkenKyohansha(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.KYOHANSHA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_OFF.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.LAWYER.getCd()));
		return getKanyoshaSearchModalFragmentAnkenRelatedKanyosha(searchForm, KanyoshaType.KYOHANSHA, SystemFlg.FLG_OFF);
	}

	/**
	 * 関与者検索モーダル：案件共犯者弁護人
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/anken/kyohanshaBengonin", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentAnkenKyohanshaBengonin(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.KYOHANSHA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_ON.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.KOJIN.getCd(), CustomerType.HOJIN.getCd()));
		return getKanyoshaSearchModalFragmentAnkenRelatedKanyosha(searchForm, KanyoshaType.KYOHANSHA, SystemFlg.FLG_ON);
	}

	/**
	 * 関与者検索モーダル：案件被害者
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/anken/higaisha", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentAnkenHigaisha(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.HIGAISHA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_OFF.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.LAWYER.getCd()));
		return getKanyoshaSearchModalFragmentAnkenRelatedKanyosha(searchForm, KanyoshaType.HIGAISHA, SystemFlg.FLG_OFF);
	}

	/**
	 * 関与者検索モーダル：案件被害者
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/anken/higaishaDairinin", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentAnkenHigaishaDairinin(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.HIGAISHA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_ON.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.KOJIN.getCd(), CustomerType.HOJIN.getCd()));
		return getKanyoshaSearchModalFragmentAnkenRelatedKanyosha(searchForm, KanyoshaType.HIGAISHA, SystemFlg.FLG_ON);
	}

	/**
	 * 案件画面-当事者・関与者を選択する：モーダル表示
	 * 
	 * @param searchForm
	 * @param kanyoshaType
	 * @param dairiFlg
	 * @return
	 */
	private ModelAndView getKanyoshaSearchModalFragmentAnkenRelatedKanyosha(KanyoshaSearchModalSearchForm searchForm, KanyoshaType kanyoshaType, SystemFlg dairiFlg) {

		ModelAndView mv = null;
		if (searchForm.getSaibanSeq() != null) {
			// 想定外のパラメータが渡されている場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return mv;
		}

		KanyoshaSearchModalViewForm viewForm = kanyoshaCommonService.createKanyoshaSearchModalViewForm(searchForm);
		mv = getMyModelAndView(viewForm, KANYOSHA_SEARCH_MODAL_FRAGMENT_PATH, KANYOSHA_SEARCH_MODAL_VIEW_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// **********************************************************************************
	// 案件画面 相手方/相手方代理人の登録
	// **********************************************************************************

	/**
	 * 個人の場合、案件相手方の新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenAitegata", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojinForAnkenAitegata(@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyoshaForAnkenAitegata(inputForm, result);
	}

	/**
	 * 法人の場合、案件相手方の新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenAitegata", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojinForAnkenAitegata(@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyoshaForAnkenAitegata(inputForm, result);
	}

	// 弁護士はなし

	/**
	 * 関与者モーダル：案件相手方の共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyoshaForAnkenAitegata(KanyoshaEditModalInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForAnkenAitegata(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "相手方"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 個人の場合、案件相手方代理人の新規登録処理
	 * ※案件相手方代理人は個人も許容する
	 * 
	 * @param aitegataKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenAitegataDairinin", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojinForAnkenAitegataDairinin(
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq,
			@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return registKanyoshaForAnkenAitegataDairinin(aitegataKanyoshaSeq, inputForm, result);
	}

	/**
	 * 法人の場合、案件相手方代理人の新規登録処理
	 * ※案件相手方代理人は法人も許容する
	 * 
	 * @param aitegataKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenAitegataDairinin", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojinForAnkenAitegataDairinin(
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq,
			@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return registKanyoshaForAnkenAitegataDairinin(aitegataKanyoshaSeq, inputForm, result);
	}

	/**
	 * 弁護士の場合、案件相手方代理人の新規登録処理
	 * 
	 * @param aitegataKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenAitegataDairinin", params = "lawyer", method = RequestMethod.POST)
	public ModelAndView registKanyoshaLawyerForAnkenAitegataDairinin(
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq,
			@Validated({Default.class, Lawyer.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return registKanyoshaForAnkenAitegataDairinin(aitegataKanyoshaSeq, inputForm, result);
	}

	/**
	 * 関与者モーダル：案件相手方代理人の共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyoshaForAnkenAitegataDairinin(Long aitegataKanyoshaSeq, KanyoshaEditModalInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForAnkenAitegataDairinin(inputForm, aitegataKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "相手方代理人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-相手方代理人の登録処理
	 * 
	 * @param ankenId
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForAnkenAitegata", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForAnkenAitegata(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForAnkenAitegata(ankenId, parsonId, kanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "相手方"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-相手方代理人の登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForAnkenAitegataDairinin", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForAnkenAitegataDairinin(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq,
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForAnkenAitegataDairinin(ankenId, parsonId, kanyoshaSeq, aitegataKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "相手方代理人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// **********************************************************************************
	// 案件画面 共犯者/共犯者弁護人の登録
	// **********************************************************************************

	/**
	 * 個人の場合、案件-共犯者の新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenKyohansha", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojinForAnkenKyohansha(@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyoshaForAnkenKyohansha(inputForm, result);
	}

	/**
	 * 法人の場合、案件-共犯者の新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenKyohansha", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojinForAnkenKyohansha(@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyoshaForAnkenKyohansha(inputForm, result);
	}

	/**
	 * 関与者モーダル：案件共犯者の共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyoshaForAnkenKyohansha(KanyoshaEditModalInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForAnkenKyohansha(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "共犯者"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// 個人・法人は代理人にならない
	/**
	 * 弁護士の場合、案件-共犯者弁護人の新規登録処理
	 *
	 * @param kyohanshaKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenKyohanshaBengonin", params = "lawyer", method = RequestMethod.POST)
	public ModelAndView registKanyoshaLawyerForAnkenKyohanshaBengonin(
			@RequestParam(name = "kyohanshaKanyoshaSeq") Long kyohanshaKanyoshaSeq,
			@Validated({Default.class, Lawyer.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForAnkenKyohanshaBengonin(inputForm, kyohanshaKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "共犯者弁護人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-共犯者の登録処理
	 *
	 * @param ankenId
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForAnkenKyohansha", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForAnkenKyohansha(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForAnkenKyohansha(ankenId, parsonId, kanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "共犯者"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-共犯者弁護人の登録処理
	 *
	 * @param ankenId
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @param kyohanshaKanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForAnkenKyohanshaBengonin", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForAnkenKyohanshaBengonin(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq,
			@RequestParam(name = "kyohanshaKanyoshaSeq") Long kyohanshaKanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForAnkenKyohanshaBengonin(ankenId, parsonId, kanyoshaSeq, kyohanshaKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "共犯者弁護人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// **********************************************************************************
	// 案件画面 被害者/被害者代理人の登録
	// **********************************************************************************

	/**
	 * 個人の場合、案件-被害者の新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenHigaisha", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojinForAnkenHigaisha(@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyoshaForAnkenHigaisha(inputForm, result);
	}

	/**
	 * 法人の場合、案件-被害者の新規登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenHigaisha", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojinForAnkenHigaisha(@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return this.registKanyoshaForAnkenHigaisha(inputForm, result);
	}

	/**
	 * 関与者モーダル：案件-被害者の共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyoshaForAnkenHigaisha(KanyoshaEditModalInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForAnkenHigaisha(inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "被害者"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// 個人・法人は代理人にならない
	/**
	 * 弁護士の場合、案件-被害者代理人の新規登録処理
	 *
	 * @param higaishaKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForAnkenHigaishaDairinin", params = "lawyer", method = RequestMethod.POST)
	public ModelAndView registKanyoshaLawyerForAnkenHigaishaDairinin(
			@RequestParam(name = "higaishaKanyoshaSeq") Long higaishaKanyoshaSeq,
			@Validated({Default.class, Lawyer.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForAnkenHigaishaDairinin(inputForm, higaishaKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "被害者代理人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-被害者の登録処理
	 * 
	 * @param ankenId
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForAnkenHigaisha", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForAnkenHigaisha(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForAnkenHigaisha(ankenId, parsonId, kanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "被害者"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-被害者代理人の登録処理
	 *
	 * @param ankenId
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @param higaishaKanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForAnkenHigaishaDairinin", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForAnkenHigaishaDairinin(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq,
			@RequestParam(name = "higaishaKanyoshaSeq") Long higaishaKanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForAnkenHigaishaDairinin(ankenId, parsonId, kanyoshaSeq, higaishaKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "被害者代理人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// **********************************************************************************
	// 裁判画面 「名簿から検索する」
	// **********************************************************************************

	/**
	 * 関与者検索モーダル：裁判その他当事者
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/saiban/other", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentSaibanOther(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.KYODOSOSHONIN.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_OFF.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.LAWYER.getCd()));
		return getKanyoshaSearchModalFragmentSaibanRelatedKanyosha(searchForm, KanyoshaType.HIGAISHA, SystemFlg.FLG_OFF);
	}

	/**
	 * 関与者検索モーダル：裁判その他当事者代理人
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/saiban/otherDairinin", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentSaibanOtherDairinin(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.KYODOSOSHONIN.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_ON.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.KOJIN.getCd(), CustomerType.HOJIN.getCd()));
		return getKanyoshaSearchModalFragmentSaibanRelatedKanyosha(searchForm, KanyoshaType.HIGAISHA, SystemFlg.FLG_ON);
	}

	/**
	 * 関与者検索モーダル：裁判相手方
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/saiban/aitegata", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentSaibanAitegata(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.AITEGATA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_OFF.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.LAWYER.getCd()));
		return getKanyoshaSearchModalFragmentSaibanRelatedKanyosha(searchForm, KanyoshaType.AITEGATA, SystemFlg.FLG_OFF);
	}

	/**
	 * 関与者検索モーダル：裁判相手方代理人
	 *
	 * @param searchForm
	 * @param saibanId
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/saiban/aitegataDairinin", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentSaibanAitegataDairinin(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.AITEGATA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_ON.getCd());
		return getKanyoshaSearchModalFragmentSaibanRelatedKanyosha(searchForm, KanyoshaType.AITEGATA, SystemFlg.FLG_ON);
	}

	/**
	 * 関与者検索モーダル：裁判共犯者
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/saiban/kyohansha", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentSaibanKyohansha(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.KYOHANSHA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_OFF.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.LAWYER.getCd()));
		return getKanyoshaSearchModalFragmentSaibanRelatedKanyosha(searchForm, KanyoshaType.KYOHANSHA, SystemFlg.FLG_OFF);
	}

	/**
	 * 関与者検索モーダル：裁判共犯者弁護人
	 *
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/getKanyoshaSearchModalFragment/saiban/kyohanshaBengonin", method = RequestMethod.GET)
	public ModelAndView getKanyoshaSearchModalFragmentSaibanKyohanshaBengonin(
			@ModelAttribute(KANYOSHA_SEARCH_MODAL_SEARCH_FORM_NAME) KanyoshaSearchModalSearchForm searchForm) {
		searchForm.setKanyoshaTypeCd(KanyoshaType.KYOHANSHA.getCd());
		searchForm.setDairiFlg(SystemFlg.FLG_ON.getCd());
		searchForm.setDisabledCustomerType(Arrays.asList(CustomerType.KOJIN.getCd(), CustomerType.HOJIN.getCd()));
		return getKanyoshaSearchModalFragmentSaibanRelatedKanyosha(searchForm, KanyoshaType.KYOHANSHA, SystemFlg.FLG_ON);
	}

	/**
	 * 裁判画面-当事者・関与者を選択する：モーダル表示
	 * 
	 * @param searchForm
	 * @param kanyoshaType
	 * @param dairiFlg
	 * @return
	 */
	private ModelAndView getKanyoshaSearchModalFragmentSaibanRelatedKanyosha(KanyoshaSearchModalSearchForm searchForm, KanyoshaType kanyoshaType, SystemFlg dairiFlg) {

		ModelAndView mv = null;
		if (searchForm.getSaibanSeq() == null) {
			// 想定しているパラメータが送られて来ない場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			return mv;
		}

		KanyoshaSearchModalViewForm viewForm = kanyoshaCommonService.createKanyoshaSearchModalViewForm(searchForm);
		mv = getMyModelAndView(viewForm, KANYOSHA_SEARCH_MODAL_FRAGMENT_PATH, KANYOSHA_SEARCH_MODAL_VIEW_FORM_NAME);

		if (mv == null) {
			// 正常終了したが、画面情報の取得が失敗している場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// **********************************************************************************
	// 裁判画面 その他当事者/その他当事者代理人の登録
	// **********************************************************************************

	/**
	 * 個人の場合、裁判その他当事者の新規登録処理
	 *
	 * @param saibanSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanOther", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojinForSaibanOther(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm,
			BindingResult result) {
		return this.registKanyoshaForSaibanOther(saibanSeq, inputForm, result);
	}

	/**
	 * 法人の場合、裁判その他当事者の新規登録処理
	 *
	 * @param saibanSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanOther", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojinForSaibanOther(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm,
			BindingResult result) {
		return this.registKanyoshaForSaibanOther(saibanSeq, inputForm, result);
	}

	// 弁護士はなし

	/**
	 * 関与者モーダル：裁判その他当事者の共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyoshaForSaibanOther(Long saibanSeq, KanyoshaEditModalInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForSaibanOther(saibanSeq, inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "その他当事者"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// 個人・法人は代理人にならない
	/**
	 * 弁護士の場合、裁判その他当事者代理人の新規登録処理
	 *
	 * @param saibanSeq
	 * @param otherKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanOtherDairinin", params = "lawyer", method = RequestMethod.POST)
	public ModelAndView registKanyoshaLawyerForSaibanOtherDairinin(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "otherKanyoshaSeq") Long otherKanyoshaSeq,
			@Validated({Default.class, Lawyer.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForSaibanOtherDairinin(saibanSeq, otherKanyoshaSeq, inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "その他当事者代理人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-その他当事者代理人の登録処理
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForSaibanOther", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForSaibanOther(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForSaibanOther(ankenId, saibanSeq, parsonId, kanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "その他当事者"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-その他当事者代理人の登録処理
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @param otherKanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForSaibanOtherDairinin", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForSaibanOtherDairinin(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq,
			@RequestParam(name = "otherKanyoshaSeq") Long otherKanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForSaibanOtherDairinin(ankenId, saibanSeq, parsonId, kanyoshaSeq, otherKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "その他当事者代理人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// **********************************************************************************
	// 裁判画面 相手方/相手方代理人の登録
	// **********************************************************************************

	/**
	 * 個人の場合、裁判相手方の新規登録処理
	 *
	 * @param saibanSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanAitegata", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojinForSaibanAitegata(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm,
			BindingResult result) {
		return this.registKanyoshaForSaibanAitegata(saibanSeq, inputForm, result);
	}

	/**
	 * 法人の場合、裁判相手方の新規登録処理
	 *
	 * @param saibanSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanAitegata", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojinForSaibanAitegata(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm,
			BindingResult result) {
		return this.registKanyoshaForSaibanAitegata(saibanSeq, inputForm, result);
	}

	// 弁護士はなし

	/**
	 * 関与者モーダル：裁判相手方の共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyoshaForSaibanAitegata(Long saibanSeq, KanyoshaEditModalInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForSaibanAitegata(saibanSeq, inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "相手方"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// 個人・法人は代理人にならない
	/**
	 * 個人の場合、裁判相手方代理人の新規登録処理
	 * ※ 相手方代理人は、個人を許容する
	 *
	 * @param saibanSeq
	 * @param aitegataKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanAitegataDairinin", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojinForSaibanAitegataDairinin(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq,
			@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return registKanyoshaForSaibanAitegataDairinin(saibanSeq, aitegataKanyoshaSeq, inputForm, result);
	}

	/**
	 * 法人の場合、裁判相手方代理人の新規登録処理
	 * ※ 相手方代理人は、法人を許容する
	 *
	 * @param saibanSeq
	 * @param aitegataKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanAitegataDairinin", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojinForSaibanAitegataDairinin(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq,
			@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return registKanyoshaForSaibanAitegataDairinin(saibanSeq, aitegataKanyoshaSeq, inputForm, result);
	}

	/**
	 * 弁護士の場合、裁判相手方代理人の新規登録処理
	 *
	 * @param saibanSeq
	 * @param aitegataKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanAitegataDairinin", params = "lawyer", method = RequestMethod.POST)
	public ModelAndView registKanyoshaLawyerForSaibanAitegataDairinin(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq,
			@Validated({Default.class, Lawyer.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {
		return registKanyoshaForSaibanAitegataDairinin(saibanSeq, aitegataKanyoshaSeq, inputForm, result);
	}

	/**
	 * 関与者モーダル：裁判相手方代理人の共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyoshaForSaibanAitegataDairinin(Long saibanSeq, Long aitegataKanyoshaSeq, KanyoshaEditModalInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForSaibanAitegataDairinin(saibanSeq, aitegataKanyoshaSeq, inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "相手方代理人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-相手方代理人の登録処理
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForSaibanAitegata", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForSaibanAitegata(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForSaibanAitegata(ankenId, saibanSeq, parsonId, kanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "相手方"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-相手方代理人の登録処理
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @param aitegataKanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForSaibanAitegataDairinin", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForSaibanAitegataDairinin(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq,
			@RequestParam(name = "aitegataKanyoshaSeq") Long aitegataKanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForSaibanAitegataDairinin(ankenId, saibanSeq, parsonId, kanyoshaSeq, aitegataKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "相手方代理人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// **********************************************************************************
	// 裁判画面 共犯者/共犯者弁護人の登録
	// **********************************************************************************
	/**
	 * 個人の場合、裁判共犯者の新規登録処理
	 *
	 * @param saibanSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanKyohansha", params = "kojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaKojinForSaibanKyohansha(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@Validated({Default.class, Kojin.class}) KanyoshaEditModalInputForm inputForm,
			BindingResult result) {
		return this.registKanyoshaForSaibanKyohansha(saibanSeq, inputForm, result);
	}

	/**
	 * 法人の場合、裁判共犯者の新規登録処理
	 *
	 * @param saibanSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanKyohansha", params = "hojin", method = RequestMethod.POST)
	public ModelAndView registKanyoshaHojinForSaibanKyohansha(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@Validated({Default.class, Hojin.class}) KanyoshaEditModalInputForm inputForm,
			BindingResult result) {
		return this.registKanyoshaForSaibanKyohansha(saibanSeq, inputForm, result);
	}

	// 弁護士はなし

	/**
	 * 関与者モーダル：裁判共犯者の共通登録処理
	 * 
	 * @param inputForm
	 * @param result
	 * @return
	 */
	private ModelAndView registKanyoshaForSaibanKyohansha(Long saibanSeq, KanyoshaEditModalInputForm inputForm, BindingResult result) {
		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForSaibanKyohansha(saibanSeq, inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "共犯者"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	// 個人・法人は弁護人にならない
	/**
	 * 弁護士の場合、裁判共犯者弁護人の新規登録処理
	 *
	 * @param saibanSeq
	 * @param kyohanshaKanyoshaSeq
	 * @param inputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registKanyoshaForSaibanKyohanshaBengonin", params = "lawyer", method = RequestMethod.POST)
	public ModelAndView registKanyoshaLawyerForSaibanKyohanshaBengonin(
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "kyohanshaKanyoshaSeq") Long kyohanshaKanyoshaSeq,
			@Validated({Default.class, Lawyer.class}) KanyoshaEditModalInputForm inputForm, BindingResult result) {

		ModelAndView mv = null;
		if (result.hasErrors()) {
			mv = getMyModelAndViewWithErrors(inputForm, KANYOSHA_EDIT_MODAL_FRAGMENT_PATH, KANYOSHA_EDIT_MODAL_INPUT_FORM_NAME, result);
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			return mv;
		}

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaInfoForSaibanKyohanshaBengonin(saibanSeq, kyohanshaKanyoshaSeq, inputForm);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "共犯者弁護人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-共犯者弁護人の登録処理
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForSaibanKyohansha", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForSaibanKyohansha(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForSaibanKyohansha(ankenId, saibanSeq, parsonId, kanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "共犯者"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}

	/**
	 * 検索結果 -> 案件-共犯者弁護人の登録処理
	 *
	 * @param ankenId
	 * @param saibanSeq
	 * @param parsonId
	 * @param kanyoshaSeq
	 * @param kyohanshaKanyoshaSeq
	 * @return
	 */
	@RequestMapping(value = "/searchResultRegistKanyoshaForSaibanKyohanshaBengonin", method = RequestMethod.POST)
	public ModelAndView searchResultRegistKanyoshaForSaibanKyohanshaBengonin(
			@RequestParam(name = "ankenId") Long ankenId,
			@RequestParam(name = "saibanSeq") Long saibanSeq,
			@RequestParam(name = "personId") Long parsonId,
			@RequestParam(name = "kanyoshaSeq", required = false) Long kanyoshaSeq,
			@RequestParam(name = "kyohanshaKanyoshaSeq") Long kyohanshaKanyoshaSeq) {

		try {
			// 登録処理
			kanyoshaCommonService.registKanyoshaSearchResultForSaibanKyohanshaBengonin(ankenId, saibanSeq, parsonId, kanyoshaSeq, kyohanshaKanyoshaSeq);
			super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "共犯者弁護人"));
			return null;

		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		}
	}
}
