package jp.loioz.app.user.planSetting.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.api.oauth.service.CommonOAuthService;
import jp.loioz.app.common.form.LoginHeaderForm;
import jp.loioz.app.common.form.userHeader.HeaderSearchListSearchForm;
import jp.loioz.app.common.form.userHeader.HeaderSearchListViewForm;
import jp.loioz.app.common.service.CommonGinkoKozaService;
import jp.loioz.app.common.service.LoginHeaderService;
import jp.loioz.app.user.planSetting.annotation.NotPermitOnlyPlanSettingAccessibleUser;
import jp.loioz.app.user.planSetting.dto.PlanSettingSessionInfoDto;
import jp.loioz.app.user.planSetting.exception.PlanSettingAuthException;
import jp.loioz.app.user.planSetting.form.PlanSettingCancelConfirmForm;
import jp.loioz.app.user.planSetting.form.PlanSettingForm;
import jp.loioz.app.user.planSetting.form.PlanSettingSaveConfirmForm;
import jp.loioz.app.user.planSetting.service.PlanSettingService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.MessageLevel;
import jp.loioz.common.constant.CookieConstant;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.ViewPathAndAttrConstant;
import jp.loioz.common.constant.plan.PlanConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanStatus;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.constant.plan.PlanConstant.StoragePrice;
import jp.loioz.common.dataSource.SchemaContextHolder;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.service.http.CookieService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.HttpUtils;
import jp.loioz.common.utility.LoiozMethodUtils;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.domain.UriService;
import jp.loioz.dto.PaymentCardDto;
import jp.loioz.dto.PlanInfo;

/**
 * プラン設定画面のコントローラークラス
 */
@Controller
@RequestMapping(UrlConstant.PLANSETTING_URL)
public class PlanSettingController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/planSetting/planSetting";
	/** viewのformで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "planSettingForm";
	
	/** 保存内容確認fragmentのパス */
	private static final String SAVE_CONFIRM_FRAGMENT_PATH = "user/planSetting/planSettingFragment::planSaveConfirmFragment";
	/** 保存内容確認fragment用のフォームオブジェクト名 */
	private static final String SAVE_CONFIRM_FORM_NAME = "planSettingSaveConfirmForm";
	
	/** 解約確認fragmentのパス */
	private static final String CANCEL_CONFIRM_FRAGMENT_PATH = "user/planSetting/planSettingFragment::planCancelConfirmFragment";
	/** 解約確認fragment用のフォームオブジェクト名 */
	private static final String CANCEL_CONFIRM_FORM_NAME = "planSettingCancelConfirmForm";

	/** ヘッダー検索フラグメントのパス */
	private final static String USER_HEADER_SEARCH_FRAGMENT_PATH = ViewPathAndAttrConstant.USER_HEADER_SEARCH_FRAGMENT_PATH;
	/** ヘッダー検索フラグメントの画面オブジェクト */
	private final static String USER_HEADER_SEARCH_FRAGMENT_VIEW_FORM = ViewPathAndAttrConstant.USER_HEADER_SEARCH_FRAGMENT_VIEW_FORM;
	
	/** バリデーションメッセージ（プランタイプ必須入力） */
	private static final String VALID_MSG_PLANTYPE_IS_REQUIRED = "ご利用プランは選択必須です";
	/** バリデーションメッセージ（ライセンス数必須入力） */
	private static final String VALID_MSG_LICENSE_IS_REQUIRED = "ライセンス数は入力必須です";
	/** バリデーションメッセージ（ストレージ量必須入力） */
	private static final String VALID_MSG_STORAGE_IS_REQUIRED = "ストレージ量は入力必須です";
	/** バリデーションメッセージ（プランタイプの不正値チェック） */
	private static final String VALID_MSG_PLANTYPE_IS_INVALID = "ご利用プランが正しくありません";
	/** バリデーションメッセージ（ストレージ量の不正値チェック） */
	private static final String VALID_MSG_STORAGE_IS_INVALID = "ストレージ量が正しくありません";

	/** バリデーター */
	private static final Validator javaxValidator = Validation.buildDefaultValidatorFactory().getValidator();
	/** Springバリデーター */
	private static final SpringValidatorAdapter validator = new SpringValidatorAdapter(javaxValidator);
	
	/** プラン画面以外アクセス不可なユーザーが、アクセスできないこのコントローラ内のメソッドのパスのリスト */
	public static final List<String> NOT_PERMIT_ACCESS_PATH_LIST_FOR_ONLY_PLAN_SETTING_ACCESSIBLE_USER = new ArrayList<>() {
		private static final long serialVersionUID = 1L;
		{
			List<String> notPermitAccessPathList = new ArrayList<>();
			
			// このコントローラ内でNotPermitDisabledPlanStatusUserが付与されているメソッドを取得
			List<Method> notPermitAccessMethodListOnlyPlanSettingAccessibleUser = LoiozMethodUtils.getMethodsListWithAnnotation(PlanSettingController.class, NotPermitOnlyPlanSettingAccessibleUser.class);
			
			// メソッドのRequestMappingのアノテーションのvalue属性の値を取得し、Contorllerのパスと合わせて、パス文字列を作成しListにまとめる
			for (Method method : notPermitAccessMethodListOnlyPlanSettingAccessibleUser) {
				RequestMapping requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
				Stream.of(requestMappingAnnotation.value())
					.map(path -> "/" + UrlConstant.PLANSETTING_URL + "/" + path)
					.forEach(notPermitAccessPathList::add);
			}
			this.addAll(notPermitAccessPathList);
		};
	};

	/** 口座情報関連の共通サービス処理 */
	@Autowired
	private CommonGinkoKozaService commonGinkoKozaService;
	
	/** Cookieを扱うサービスクラス */
	@Autowired
	private CookieService cookieService;

	/** URIを扱うサービスクラス */	
	@Autowired
	private UriService uriService;
	
	/** ログインヘッダー用のサービスクラス */
	@Autowired
	private LoginHeaderService headerService;
	
	/** プラン設定サービス */
	@Autowired
	private PlanSettingService service;
	
	/** アクセス認証情報の共通サービスクラス */
	@Autowired
	private CommonOAuthService commonOAuthService;
	
	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// Controller独自の例外ハンドラー
	// =========================================================================
	/**
	 * このController内でthrowされたPlanSettingAuthExceptionをキャッチしハンドリングする。<br>
	 * ajaxかhttpリクエストかを判定し、認証エラー画面を表示するための適切なレスポンスをresponseに設定する。
	 * 
	 * @param ex
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@ExceptionHandler(PlanSettingAuthException.class)
	public void handleCustomException(PlanSettingAuthException ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// リクエストからサブドメイン取得
		String cookieSubDomain = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, request);
		
		if (StringUtils.isEmpty(cookieSubDomain)) {
			// cookieにサブドメインがない -> 操作しているブラウザでは未ログイン状態の場合
			
			// テナントのサブドメインはわからないので、プラン画面のサブドメインでURLを構築する
			final String PLAN_DOMAIN = uriService.getTenantDomain(PlanConstant.PLAN_SETTING_SUB_DOMAIN);
			String planUriUntilContextPath = ServletUriComponentsBuilder.fromCurrentContextPath()
					.host(PLAN_DOMAIN)
					.toUriString();
			
			String planSettingAuthErrorUrl = planUriUntilContextPath + "/" + UrlConstant.PLANSETTING_GATEWAY_URL + "/" + PlanConstant.PLAN_SETTING_AUTH_ERROR_PATH;
			response.sendRedirect(planSettingAuthErrorUrl);
			return;
		}
		
		final String TENANT_DOMAIN = uriService.getTenantDomain(cookieSubDomain);
		String tenantUriUntilContextPath = ServletUriComponentsBuilder.fromCurrentContextPath()
				.host(TENANT_DOMAIN)
				.toUriString();
		
		boolean isAjaxRequest = HttpUtils.isAjax(request);
		
		if (isAjaxRequest) {
			// ajaxのリクエストの場合
			
			response.setStatus(PlanConstant.CUSTOM_PLAN_SETTING_AUTH_ERROR_CODE);
			response.setHeader(CommonConstant.HEADER_NAME_OF_URI_UNTIL_CONTEXT_PATH, tenantUriUntilContextPath);
			return;
			
		} else {
			
			String planSettingAuthErrorUrl = tenantUriUntilContextPath + "/" + UrlConstant.PLANSETTING_GATEWAY_URL + "/" + PlanConstant.PLAN_SETTING_AUTH_ERROR_PATH;
			response.sendRedirect(planSettingAuthErrorUrl);
		}
	}
	
	// =========================================================================
	// ModelAttribute
	// =========================================================================
	
	/**
	 * プラン画面用のコントローラ（本コントローラ）のメソッドにアクセスする前に、
	 * 不正アクセスチェックや権限チェックを行い、アクセス可能であればSession情報に相当するSessionDtoをmodelに設定する共通処理。
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@ModelAttribute
	PlanSettingSessionInfoDto setUpCommonData(HttpServletRequest request, Model model) {
		
		// リクエストから認証キー取得
		String cookieAuthKey = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_PLAN_SETTING_ACCESS_AUTH_KEY, request);
		// リクエストからサブドメイン取得
		String cookieSubDomain = cookieService.getCookieValue(CookieConstant.COOKIE_NAME_OF_SUBDOMAIN, request);
		
		if (StringUtils.isEmpty(cookieSubDomain)) {
			// cookieにサブドメインがない -> 操作しているブラウザでは未ログイン状態の場合
			// 認証エラーとする
			throw new PlanSettingAuthException();
		}
		
		// 認証キーからSessionDto取得
		PlanSettingSessionInfoDto sessionDto = service.getSessionInfoByAuthKey(cookieAuthKey, cookieSubDomain, NOT_PERMIT_ACCESS_PATH_LIST_FOR_ONLY_PLAN_SETTING_ACCESSIBLE_USER);
		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
			
			// テナントデータをDtoに追加設定
			service.setTenantInfoToDto(sessionDto);
			
			// ヘッダー表示で必要なformをmodelに設定
			// ※プラン画面（このControllerクラス）はログインSessionが存在しない状態でアクセスするため、UserHeaderControllerAdviceでのセットアップ処理が行われない。
			//   そのためUserHeaderControllerAdviceで行われるセットアップ処理の中で、プラン画面で必要なものはここで独自にセットアップを行う。
			LoginHeaderForm loginHeaderForm = headerService.createLoginHeaderCountForm(sessionDto.getAccountSeq());
			model.addAttribute("loginHeaderForm", loginHeaderForm);
			
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
		
		return sessionDto;
	}
	
	/** リダイレクト先の初期化 */
	@ModelAttribute
	RedirectViewBuilder setUpRedirectViewBuilder(RedirectAttributes redirectAttributes) {
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(), controller -> controller.index(null, null));
		return new RedirectViewBuilder(redirectAttributes, redirectPath);
	}
	
	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 事務所の口座が登録されているかを判定する。
	 * 
	 * <pre>
	 * プラン画面のサイドメニュー（systemSettingMenuForPlan.html）の表示時にHTMLの中で呼び出される。
	 * サイドメニューの表示のみで利用しており、プラン画面の機能自体には影響しないメソッドだが、
	 * プラン画面同様、ログインSessionがない状態で実行する必要があるため、このController内に定義している。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "isRegistOfficeKoza", method = RequestMethod.GET)
	public Map<String, Object> isRegistOfficeKoza(PlanSettingSessionInfoDto sessionDto) {
		Map<String, Object> response = new HashMap<>();

		Long tenantSeq = sessionDto.getTenantSeq();
		boolean isRegist = false;
		
		try {
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
			
			/* ログインアカウントの連番 */
			Long accountSeq = sessionDto.getAccountSeq();
			
			/* 事務所に紐づく銀行口座の登録有無を判定 */
			isRegist = commonGinkoKozaService.isRegistOfficeGinkoKoza(accountSeq);
			
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
		
		response.put("isRegist", isRegist);
		return response;
	}

	/**
	 * ヘッダー検索フラグメントの取得
	 * 
	 * @param sessionDto
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "getUserHeaderSearchFragment", method = RequestMethod.GET)
	public ModelAndView getUserHeaderSearchFragment(PlanSettingSessionInfoDto sessionDto, HeaderSearchListSearchForm searchForm) {
		
		ModelAndView mv = null;
		HeaderSearchListViewForm viewForm = null;
		Long tenantSeq = sessionDto.getTenantSeq();

		try {
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
			
			// 画面オブジェクトを作成する
			viewForm = headerService.getSearchResultHeaderSearchListViewForm(searchForm);
			
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}

		// 画面ビューの作成
		mv = getMyModelAndView(viewForm, USER_HEADER_SEARCH_FRAGMENT_PATH, USER_HEADER_SEARCH_FRAGMENT_VIEW_FORM);
		if (mv == null) {
			this.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return null;
		}

		// 正常終了
		String requestUuid = searchForm.getRequestUuid();
		this.setAjaxProcResultSuccess(requestUuid);
		return mv;
	} 
	
	/**
	 * GoogleルートフォルダURLを取得する
	 * 
	 * @param sessionDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getGoogleRootFolder", method = RequestMethod.GET)
	public Map<String, Object> getGoogleRootFolderUrl(PlanSettingSessionInfoDto sessionDto) {

		Map<String, Object> response = new HashMap<>();
		Long tenantSeq = sessionDto.getTenantSeq();
		Long accountSeq = sessionDto.getAccountSeq();

		try {
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);

			boolean isDeleted = false;
			try {
				commonOAuthService.refresh(ExternalService.GOOGLE, accountSeq);
				isDeleted = service.isDeletedGoogleRootFolder(accountSeq);

			} catch (AppException e) {
				// エラー内容
				response.put("successed", false);
				
				MessageEnum messageEnum = e.getErrorType();
				String message = null;
				if (messageEnum == MessageEnum.MSG_E00141) {
					// このエラーの場合、メッセージにURLが含まれるため、ここでテナント側のコンテキストパスを設定する
					message = getMessage(messageEnum, sessionDto.getTenantUrlContextPath() + "/" + UrlConstant.MY_EXTERNAL_SETTING_URL + "/");
				} else {
					message = getMessage(messageEnum, e.getMessageArgs());
				}
				
				response.put("message", message);
				return response;
			}

			if (isDeleted) {
				// 削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			String rootFoloderUrl = service.getGoogleRootFolderUrl();
			response.put("successed", true);
			response.put("url", rootFoloderUrl);
			return response;

		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
	}

	/**
	 * BoxルートフォルダURLを取得する
	 * 
	 * @param sessionDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getBoxRootFolder", method = RequestMethod.GET)
	public Map<String, Object> getBoxRootFolderUrl(PlanSettingSessionInfoDto sessionDto) {

		Map<String, Object> response = new HashMap<>();
		Long tenantSeq = sessionDto.getTenantSeq();
		Long accountSeq = sessionDto.getAccountSeq();

		try {
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);

			boolean isDeleted = false;
			try {
				commonOAuthService.refresh(ExternalService.BOX, accountSeq);
				isDeleted = service.isDeletedBoxRootFolder(accountSeq);

			} catch (AppException e) {
				// エラー内容
				response.put("successed", false);
				
				MessageEnum messageEnum = e.getErrorType();
				String message = null;
				if (messageEnum == MessageEnum.MSG_E00141) {
					// このエラーの場合、メッセージにURLが含まれるため、ここでテナント側のコンテキストパスを設定する
					message = getMessage(messageEnum, sessionDto.getTenantUrlContextPath() + "/" + UrlConstant.MY_EXTERNAL_SETTING_URL + "/");
				} else {
					message = getMessage(messageEnum, e.getMessageArgs());
				}
				
				response.put("message", message);
				return response;
			}

			if (isDeleted) {
				// 削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			String rootFoloderUrl = service.getBoxRootFolderUrl();
			response.put("successed", true);
			response.put("url", rootFoloderUrl);
			return response;

		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
	}

	/**
	 * DropboxルートフォルダURLを取得する
	 * 
	 * @param sessionDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getDropboxRootFolder", method = RequestMethod.GET)
	public Map<String, Object> getDropboxRootFolderUrl(PlanSettingSessionInfoDto sessionDto) {

		Map<String, Object> response = new HashMap<>();
		Long tenantSeq = sessionDto.getTenantSeq();
		Long accountSeq = sessionDto.getAccountSeq();

		try {
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);

			boolean isNotFound = false;
			try {
				commonOAuthService.refresh(ExternalService.DROPBOX, accountSeq);
				isNotFound = service.isNotFoundDropboxRootFolder(accountSeq);

			} catch (AppException e) {
				// エラー内容
				response.put("successed", false);
				
				MessageEnum messageEnum = e.getErrorType();
				String message = null;
				if (messageEnum == MessageEnum.MSG_E00141) {
					// このエラーの場合、メッセージにURLが含まれるため、ここでテナント側のコンテキストパスを設定する
					message = getMessage(messageEnum, sessionDto.getTenantUrlContextPath() + "/" + UrlConstant.MY_EXTERNAL_SETTING_URL + "/");
				} else {
					message = getMessage(messageEnum, e.getMessageArgs());
				}
				
				response.put("message", message);
				return response;
			}

			if (isNotFound) {
				// 削除されている場合
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00142));
				return response;
			}

			String rootFoloderUrl = service.getDropboxRootFolderUrl(accountSeq);
			response.put("successed", true);
			response.put("url", rootFoloderUrl);
			return response;

		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 * 
	 * @param sessionDto
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(PlanSettingSessionInfoDto sessionDto, Model model) {
		
		Long tenantSeq = sessionDto.getTenantSeq();
		ModelAndView mv = null;
		
		try {
		
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
			
			PlanSettingForm viewForm = new PlanSettingForm();
			viewForm.setSessionDto(sessionDto);
			
			// 契約ステータス等、現在の契約情報を設定
			PlanInfo nowPlanInfo = service.getNowPlanInfo(sessionDto);
			viewForm.setNowPlanInfo(nowPlanInfo);
			
			// 1ライセンスの料金(税抜)（画面に表示）
			viewForm.setOneLicenseCharge(String.valueOf(nowPlanInfo.getOneLicenseCharge()));
			
			// 現在が無料期間中かどうか
			boolean nowIsDuringTheFreePeriod = service.nowIsDuringTheFreePeriod(sessionDto);
			viewForm.setDuringTheFreePeriod(nowIsDuringTheFreePeriod);
			// 無料トライアル（無料期間）の期日
			LocalDateTime freeTrialExpiredAt = service.getFreeTrialExpiredAt(sessionDto);
			viewForm.setFreeTrialExpiredAt(freeTrialExpiredAt);
			
			// 現在支払いに使用しているカード情報
			PaymentCardDto cardDto = service.getNowCardInfo();
			viewForm.setNowCardInfo(cardDto);
			
			// 現在利用中（有効状態としている）ライセンス数
			Long nowUsingLicenseCount = service.getNowUsingLicenseCount();
			viewForm.setUsingLicenseCount(nowUsingLicenseCount);
			
			// 連携中の外部ストレージサービス（未連携の場合はNULL）
			ExternalService externalServiceEnum = service.getConnectStorageService();
			viewForm.setConnectedExternalStorageService(externalServiceEnum);
			
			// メッセージ
			MessageHolder msgHolder = this.getIndexMessage(sessionDto, model, nowPlanInfo.getPlanStatus(), nowPlanInfo.getExpiredAt());
			
			// 遷移先パス、フォームの設定
			mv = this.getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME, msgHolder);
			
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
		
		return mv;
	}

	/**
	 * 保存／変更処理後のメッセージ表示をともなう初期表示画面へのリダイレクト
	 *
	 * @param form フォーム情報
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "redirectIndexWithMessage", method = RequestMethod.GET)
	public ModelAndView redirectIndexWithMessage(@RequestParam("level") String levelCd, @RequestParam("message") String message,
			RedirectViewBuilder redirectViewBuilder) {

		MessageLevel level = DefaultEnum.getEnum(MessageLevel.class, levelCd);
		return redirectViewBuilder.build(MessageHolder.ofLevel(level, StringUtils.lineBreakStr2Code(message)));
	}

	/**
	 * 保存内容確認フラグメントを取得する
	 * 
	 * @param sessionDto
	 * @param confirmForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "getPlanSaveConfirmFragment", method = RequestMethod.GET)
	public ModelAndView getPlanSaveConfirmFragment(PlanSettingSessionInfoDto sessionDto,
			@Validated PlanSettingSaveConfirmForm confirmForm, BindingResult result) {
		
		Long tenantSeq = sessionDto.getTenantSeq();
		ModelAndView mv = null;

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合（送信前に制御をしているため、不正なことをしない限りはこのケースにはならない）
			super.setAjaxProcResultFailure(getMessage(MessageEnum.VARIDATE_MSG_E00001));
			return null;
		}
		
		// 入力項目のバリデーションチェック
		if (this.inputFormValidatedForGetPlanSaveConfirmFragment(confirmForm)) {
			super.setAjaxProcResultFailure(getMessage(MessageEnum.VARIDATE_MSG_E00001));
			return null;
		}
		
		try {

			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
			
			// 以下の処理では、DBにはアクセスしないが念のため
			
			// 金額情報
			PlanInfo planInfo = service.getPlanChargeInfo(confirmForm.getPlanTypeAfter(), Long.valueOf(confirmForm.getLicenseVal()), Long.valueOf(confirmForm.getStorageVal()));
			confirmForm.setOneLicenseCharge(planInfo.getOneLicenseCharge());
			confirmForm.setLicenseCharge(planInfo.getLicenseCharge());
			confirmForm.setStorageCharge(planInfo.getStorageCharge());
			confirmForm.setSumCharge(planInfo.getSumCharge());
			confirmForm.setTaxAmountOfSumCharge(planInfo.getTaxAmountOfSumCharge());
			
			// 会計機能が利用不可になるか
			boolean isChangeUnavailableKaikei = service.isChangeUnavailableKaikei(sessionDto, PlanType.of(confirmForm.getPlanTypeAfter()));
			confirmForm.setChangeUnavailableKaikei(isChangeUnavailableKaikei);
			
			// 旧会計のデータが存在するか
			boolean isExistsOldKaikeiData = service.isExistsOldKaikeiData();
			confirmForm.setExistsOldKaikeiData(isExistsOldKaikeiData);
			
			// session情報
			confirmForm.setSessionDto(sessionDto);
			
			// 画面取得
			mv = getMyModelAndView(confirmForm, SAVE_CONFIRM_FRAGMENT_PATH, SAVE_CONFIRM_FORM_NAME);

		} catch (Exception ex) {
			// 上記のメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// メソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}
	
	/**
	 * 解約確認フラグメントを取得する
	 * 
	 * @param confirmForm
	 * @return
	 */
	@RequestMapping(value = "getPlanCancelConfirmFragment", method = RequestMethod.GET)
	public ModelAndView getPlanCancelConfirmFragment(PlanSettingCancelConfirmForm confirmForm) {
		
		ModelAndView mv = null;
		
		try {
			
			// 画面取得
			mv = getMyModelAndView(confirmForm, CANCEL_CONFIRM_FRAGMENT_PATH, CANCEL_CONFIRM_FORM_NAME);

		} catch (Exception ex) {
			// 上記のメソッドはAppExceptionをthrowしないので、Exceptionのみcatchしているが、
			// メソッドがAppExceptionをthrowする場合は、AppExceptionのcatchも行い、適切なエラーメッセージを返却すること。

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}
	
	/**
	 * 無料期間中のプラン内容の変更
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param result
	 * @return
	 * @throws AppException
	 */
	@NotPermitOnlyPlanSettingAccessibleUser
	@ResponseBody
	@RequestMapping(value = "freePlanUpdate", method = RequestMethod.POST)
	public Map<String, Object> freePlanUpdate(PlanSettingSessionInfoDto sessionDto,
			@Validated @ModelAttribute(VIEW_FORM_NAME) PlanSettingForm viewForm, BindingResult result) throws AppException {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// ※登録、更新処理で行っているフォームの値のエスケープ処理は行わない（この処理の場合、自由入力の項目が入力項目に存在しないため）

		// 入力項目のバリデーションチェック
		if (this.inputFormValidatedForFreePlanUpdate(viewForm, response)) {
			// エラーが存在する場合
			return response;
		}

		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			// DBアクセスチェック
			PlanInfo nowPlanInfo = service.getNowPlanInfo(sessionDto);
			if (this.accessDBValidatedForFreePlanUpdate(viewForm, nowPlanInfo, response)) {
				// エラーが発生した場合
				return response;
			}

			try {

				service.freePlanUpdate(sessionDto, viewForm);
				response.put("succeeded", true);
				response.put("message", getMessage(MessageEnum.MSG_I00002.getMessageKey()));

			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
				return response;
			}
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}

		return response;
	}

	/**
	 * 無料期間中の課金情報の登録
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param result
	 * @return
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "planRegistDuringFreeTrial", method = RequestMethod.POST)
	public Map<String, Object> planRegistDuringFreeTrial(PlanSettingSessionInfoDto sessionDto,
			@Validated @ModelAttribute(VIEW_FORM_NAME) PlanSettingForm viewForm, BindingResult result) throws AppException {

		Map<String, Object> response = new HashMap<>();
		
		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// フォームの値のエスケープ
		this.sanitizeAjaxFormValue(viewForm);

		// 入力項目のバリデーションチェック
		if (this.inputFormValidatedForPlanRegistDuringFreeTrial(viewForm, response)) {
			// エラーが存在する場合
			return response;
		}

		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			// DBアクセスチェック
			if (this.accessDBValidatedForPlanRegistDuringFreeTrial(sessionDto, viewForm, response)) {
				// 整合性エラーが発生した場合
				return response;
			}

			try {

				service.planRegistForDuringFreeTrial(sessionDto, viewForm);
				response.put("succeeded", true);
				response.put("message", getMessage(MessageEnum.MSG_I00002.getMessageKey()));
				
			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00012.getMessageKey()));
				return response;
			}

			try {
				// プランの登録処理とは別トランザクションで行う

				// プラン登録メールの送信
				service.sendRegistPlanMail2User(sessionDto);// ユーザーに送信
				service.sendRegistPlanMail2SystemManager(sessionDto); // システム管理に送信

			} catch (Exception e) {
				// メール送信で例外が発生しても、プラン登録処理自体は成功しているので、特になにも行わない
				logger.warn("有料プラン登録完了メールの送信に失敗しました。");
			}
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}

		return response;
	}
	
	/**
	 * 無料期間中の課金情報の変更
	 *
	 * <pre>
	 *  無料期間中のプラン変更では、現在のプランを解約し、変更後のプランを新規登録する。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param result
	 * @return
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "planUpdateDuringFreeTrial", method = RequestMethod.POST)
	public Map<String, Object> planUpdateDuringFreeTrial(PlanSettingSessionInfoDto sessionDto,
			@Validated @ModelAttribute(VIEW_FORM_NAME) PlanSettingForm viewForm, BindingResult result) throws AppException {

		Map<String, Object> response = new HashMap<>();

		// ※無料期間外のプラン変更（planUpdateRegist）では、プラン変更可能日のチェック（月末日ではないか）を行うが、
		//   無料期間中のプラン変更では、変更可能日のチェックは行わない。
		//    -> 無料期間中の場合は当月末にはまだ課金は発生せず、月末日でのプラン金額の変更が問題とはならないため。

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// フォームの値のエスケープ
		this.sanitizeAjaxFormValue(viewForm);

		// 入力項目のバリデーションチェック
		if (this.inputFormValidatedForPlanUpdateDuringFreeTrial(viewForm, response)) {
			// エラーが存在する場合
			return response;
		}
		
		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			// DBアクセスチェック
			if (this.accessDBValidatedForPlanUpdateDuringFreeTrial(sessionDto, viewForm, response)) {
				// 整合性エラーが発生した場合
				return response;
			}

			try {
				
				// 現在の有料プランの登録を削除（無料トライアル状態に戻す）
				service.planDeleteDuringFreeTrial(sessionDto);

			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("isDeleteFailed", true);
				response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
				return response;
			}

			try {

				// 無料トライアル状態でのプランの新規登録
				service.planRegistForDuringFreeTrial(sessionDto, viewForm);
				response.put("succeeded", true);
				response.put("message", getMessage(MessageEnum.MSG_I00002.getMessageKey()));
				
			} catch (Exception ex) {

				logger.error("【テナントSEQ：" + tenantSeq + "】"
						+ "対象顧客が無料期間中のプラン変更を行い、処理が途中で失敗したため、"
						+ "有料プラン登録状態から、無料トライアル状態に戻りました。"
						+ "ユーザーが再度、有料プランの登録を行わない場合、通常の無料トライアルを利用するユーザーと同じ状態になります。");
				
				response.put("succeeded", false);
				response.put("isRegistFailed", true);
				
				response.put("message", getMessage(MessageEnum.MSG_E00072.getMessageKey()));
				return response;
			}
			
			try {
				// プランの変更処理とは別トランザクションで行う

				// プラン変更メールの送信
				service.sendUpdatePlanMail2User(sessionDto); // ユーザーに送信
				service.sendUpdatePlanMail2SystemManager(sessionDto); // システム管理に送信

			} catch (Exception e) {
				// メール送信で例外が発生しても、プラン変更処理自体は成功しているので、特になにも行わない
				logger.warn("プラン変更完了メールの送信に失敗しました。");
			}
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}

		return response;
	}
	
	/**
	 * 課金情報の登録
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param result
	 * @return
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "regist", method = RequestMethod.POST)
	public Map<String, Object> regist(PlanSettingSessionInfoDto sessionDto,
			@Validated @ModelAttribute(VIEW_FORM_NAME) PlanSettingForm viewForm, BindingResult result) throws AppException {

		Map<String, Object> response = new HashMap<>();
		
		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// フォームの値のエスケープ
		this.sanitizeAjaxFormValue(viewForm);

		// 入力項目のバリデーションチェック
		if (this.inputFormValidatedForPlanRegist(viewForm, response)) {
			// エラーが存在する場合
			return response;
		}

		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			// DBアクセスチェック
			if (this.accessDBValidatedForPlanRegist(sessionDto, viewForm, response)) {
				// 整合性エラーが発生した場合
				return response;
			}

			try {

				service.planRegistForStartingUsePlanAfterFreeTrial(sessionDto, viewForm);
				response.put("succeeded", true);
				response.put("message", getMessage(MessageEnum.MSG_I00002.getMessageKey()));
				
			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00012.getMessageKey()));
				return response;
			}

			try {
				// プランの登録処理とは別トランザクションで行う

				// プラン登録メールの送信
				service.sendRegistPlanMail2User(sessionDto);// ユーザーに送信
				service.sendRegistPlanMail2SystemManager(sessionDto); // システム管理に送信

			} catch (Exception e) {
				// メール送信で例外が発生しても、プラン登録処理自体は成功しているので、特になにも行わない
				logger.warn("有料プラン登録完了メールの送信に失敗しました。");
			}
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}

		return response;
	}

	/**
	 * 課金情報の変更と登録処理
	 *
	 * <pre>
	 *  現在のプランを停止し、変更後状態のプランを新規登録する。
	 * </pre>
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param result
	 * @return
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "planUpdateRegist", method = RequestMethod.POST)
	public Map<String, Object> planUpdateRegist(PlanSettingSessionInfoDto sessionDto,
			@Validated @ModelAttribute(VIEW_FORM_NAME) PlanSettingForm viewForm, BindingResult result) throws AppException {

		Map<String, Object> response = new HashMap<>();

		// プラン変更可能日チェック
		if (service.nowIsMonthLastDate()) {
			// 現在日が月末の決済日である場合はプラン変更は不可とする
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00085.getMessageKey()));
			return response;
		}

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// フォームの値のエスケープ
		this.sanitizeAjaxFormValue(viewForm);

		// 入力項目のバリデーションチェック
		if (this.inputFormValidatedForPlanUpdate(viewForm, response)) {
			// エラーが存在する場合
			return response;
		}

		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			PlanInfo nowPlanInfo = service.getNowPlanInfo(sessionDto);

			// DBアクセスチェック
			if (this.accessDBValidatedForPlanUpdate(sessionDto, viewForm, nowPlanInfo, response)) {
				// 整合性エラーが発生した場合
				return response;
			}

			// 現在のプランステータス
			PlanStatus nowPlanStatus = nowPlanInfo.getPlanStatus();

			try {

				// 現行プランの停止
				service.planStopAndChangingStatus(sessionDto, viewForm, nowPlanStatus);

			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("isUpdateFailed", true);
				response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
				return response;
			}

			try {

				// 登録処理を行う
				service.planRegistAfterStopAndChangingStatus(sessionDto, viewForm, nowPlanStatus);

			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("isRegistFailed", true);

				if (nowPlanStatus == PlanStatus.CANCELED) {
					// 解約からの再契約処理の場合のエラー（プランの停止は完了したが、プランの登録が失敗した場合）

					logger.error("【テナントSEQ：" + tenantSeq + "】"
							+ "対象顧客が解約期間中でのプランの再契約を行い、現在のプランの自動課金の停止には成功したが、変更プランの自動課金の登録に失敗しました。"
							+ "当月中にユーザーが再度プランの再契約操作を行わない場合、当月末の課金が行われなくなってしまいます。"
							+ "対象ユーザーのテナント情報テーブル、プランの履歴テーブルの登録データより、本ログの出力後にユーザーが当月のプランの再契約処理を完了しているかを判断し、"
							+ "再契約処理を完了させていない場合は、ロボットペイメントの管理システムへログインし、停止された自動課金の停止を解除してください。"
							+ "（ユーザーが再契約処理を完了させていない場合であれば、どのタイミングで停止の解除を行っても大丈夫です。ユーザーが再契約処理を行う際に、停止が解除されていれば、再度停止処理が実行されるため、二重課金が発生することなどはありません。"
							+ "停止を解除する自動課金の番号は、契約ステータスを「解約」ステータスへ更新した際のプラン履歴情報に登録されている自動課金番号になります。"
							+ "停止の解除をする際は、停止された自動課金が当月末にあと1度だけ課金を実施する課金情報であることを確認してください。）");

					response.put("message", StringUtils.lineBreakCode2Str(getMessage(MessageEnum.MSG_E00057.getMessageKey())));

				} else {
					// プラン変更処理の場合（上記の登録処理は、必ず「解約」か「変更中」のステータスで処理が実行されるため、ここにくるケースではステータスは「変更中」となっている）の登録処理に失敗した場合

					logger.error("【テナントSEQ：" + tenantSeq + "】"
							+ "現在のプランの自動課金の停止と変更中ステータスへの更新には成功したが、変更プランの自動課金の登録に失敗したため、プラン変更処理の完了（有効ステータスへの変更）ができませんでした。"
							+ "当月中にユーザーが再度プランの再契約操作を行わない場合、当月末の課金が行われなくなってしまいます。"
							+ "対象ユーザーのテナント情報テーブル、プランの履歴テーブルの登録データより、本ログの出力後にユーザーが当月のプランの再契約処理を完了しているかを判断し、"
							+ "再契約処理を完了させていない場合は、ロボットペイメントの管理システムへログインし、停止された自動課金の停止を解除し、課金停止回数に「1」設定したうえで、"
							+ "金額（お試し金額、金額の両方）を、契約ステータスを「変更中」ステータスへ更新した際のプラン履歴情報に登録されている「charge_this_month（当月金額）」の値に変更してください。（税金額も適切なものに変更する）"
							+ "（ユーザーが再契約処理を完了させていない場合であれば、どのタイミングで停止の解除を行っても大丈夫です。ユーザーが再契約処理を行う際に、停止が解除されていれば、再度停止処理が実行されるため、二重課金が発生することなどはありません。"
							+ "停止を解除する自動課金の番号は、契約ステータスを「変更中」ステータスへ更新した際のプラン履歴情報に登録されている自動課金番号になります。）");

					response.put("message", StringUtils.lineBreakCode2Str(getMessage(MessageEnum.MSG_E00054.getMessageKey())));
				}

				return response;
			}

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00002.getMessageKey()));

			try {
				// プランの変更処理とは別トランザクションで行う

				// プラン変更メールの送信
				service.sendUpdatePlanMail2User(sessionDto); // ユーザーに送信
				service.sendUpdatePlanMail2SystemManager(sessionDto); // システム管理に送信

			} catch (Exception e) {
				// メール送信で例外が発生しても、プラン変更処理自体は成功しているので、特になにも行わない
				logger.warn("プラン変更完了メールの送信に失敗しました。");
			}
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}

		return response;
	}

	/**
	 * 無料期間中の解約（即停止）
	 * 
	 * <pre>
	 * 無料期間中に解約を行うと、通常の無料トライアルの状態に戻るかたちとする。
	 * ロボットペイメントの課金を即停止し、契約ステータスやプラン履歴なども無料トライアルを行っていたときの状態に戻す。
	 * （申し込みを行い、無料トライアルを開始したときと同じ状態。）
	 * </pre>
	 * 
	 * @param sessionDto
	 * @return
	 * @throws AppException
	 */
	@NotPermitOnlyPlanSettingAccessibleUser
	@ResponseBody
	@RequestMapping(value = "planDeleteDuringFreeTrial", method = RequestMethod.POST)
	public Map<String, Object> planDeleteDuringFreeTrial(PlanSettingSessionInfoDto sessionDto) throws AppException {
		
		Map<String, Object> response = new HashMap<>();
		
		// ※無料期間外の解約（planCancel）では、プラン変更可能日のチェック（月末日ではないか）を行うが、
		//   無料期間中の解約では、変更可能日のチェックは行わない。
		//    -> 無料期間中の場合は当月末にはまだ課金は発生せず、また、自動課金の停止予約ではなく、自動課金の即停止を行うため月末に実施されても問題とはならない。
		
		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			// DBアクセスチェック
			if (this.accessDBValidatedForPlanDeleteDuringFreeTrial(sessionDto, response)) {
				// 整合性エラーが発生した場合
				return response;
			}
			
			try {

				service.planDeleteDuringFreeTrial(sessionDto);
				response.put("succeeded", true);
				response.put("message", getMessage(MessageEnum.MSG_I00123.getMessageKey()));

			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00049.getMessageKey()));
				return response;
			}
			
			try {
				// プランの解約（削除）処理とは別トランザクションで行う

				// 解約完了メールの送信
				service.sendCancelPlanMail2User(sessionDto); // ユーザーに送信
				service.sendCancelPlanMail2SystemManager(sessionDto); // システム管理に送信

			} catch (Exception e) {
				// メール送信で例外が発生しても、解約処理自体は成功しているので、特になにも行わない
				logger.warn("解約完了メールの送信に失敗しました。");
			}
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
		
		return response;
	}
	
	/**
	 * 課金の解約（停止予約）
	 * 
	 * @param sessionDto
	 * @return
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "planCancel", method = RequestMethod.POST)
	public Map<String, Object> planCancel(PlanSettingSessionInfoDto sessionDto) throws AppException {

		Map<String, Object> response = new HashMap<>();

		// プラン変更可能日チェック
		if (service.nowIsMonthLastDate()) {
			// 現在日が月末の決済日である場合はプラン変更は不可とする
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00085.getMessageKey()));
			return response;
		}

		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			// DBアクセスチェック
			if (this.accessDBValidatedForPlanCancel(sessionDto, response)) {
				// 整合性エラーが発生した場合
				return response;
			}

			try {

				service.planCancel(sessionDto);
				response.put("succeeded", true);
				String lastDateOfThisMonth = DateUtils.parseToString(DateUtils.getLastDateOfThisMonth(LocalDate.now()),
						DateUtils.DATE_JP_YYYY_MM_DD_ZERO_FILL);
				response.put("message", getMessage(MessageEnum.MSG_I00119.getMessageKey(), lastDateOfThisMonth));

			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00049.getMessageKey()));
				return response;
			}

			try {
				// プランの解約処理とは別トランザクションで行う

				// 解約完了メールの送信
				service.sendCancelPlanMail2User(sessionDto); // ユーザーに送信
				service.sendCancelPlanMail2SystemManager(sessionDto); // システム管理に送信

			} catch (Exception e) {
				// メール送信で例外が発生しても、解約処理自体は成功しているので、特になにも行わない
				logger.warn("解約完了メールの送信に失敗しました。");
			}
		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}
		
		return response;
	}

	/**
	 * カード情報の変更
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param result
	 * @return
	 * @throws AppException
	 */
	@NotPermitOnlyPlanSettingAccessibleUser
	@ResponseBody
	@RequestMapping(value = "cardChange", method = RequestMethod.POST)
	public Map<String, Object> cardChange(PlanSettingSessionInfoDto sessionDto,
			@Validated @ModelAttribute(VIEW_FORM_NAME) PlanSettingForm viewForm, BindingResult result)
			throws AppException {

		Map<String, Object> response = new HashMap<>();

		// 入力チェック
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", result.getFieldErrors());
			return response;
		}

		// フォームの値のエスケープ
		this.sanitizeAjaxFormValue(viewForm);

		Long tenantSeq = sessionDto.getTenantSeq();
		
		try {
			
			// テナントDBを接続先とする
			SchemaContextHolder.setTenantSeq(tenantSeq);
		
			// DBアクセスチェック
			if (this.accessDBValidatedForCardUpdate(sessionDto, response)) {
				// 整合性エラーが発生した場合
				return response;
			}

			try {

				service.cardUpdate(sessionDto, viewForm);
				response.put("succeeded", true);
				response.put("message", getMessage(MessageEnum.MSG_I00033, "カード情報"));

			} catch (Exception ex) {

				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00010.getMessageKey()));
				return response;
			}

		} finally {
			// テナントDBの接続解除
			SchemaContextHolder.clear();
		}

		return response;
	}

	/**
	 * プラン情報の取得
	 *
	 * @param planTypeId プランタイプID
	 * @param licenseCount ライセンス数
	 * @param storageCount ストレージ容量
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "planCharge", method = RequestMethod.GET)
	public Map<String, Object> planCharge(@RequestParam("planTypeId") String planTypeId, @RequestParam("licenseCount") String licenseCount, @RequestParam("storageCount") String storageCount) {

		Map<String, Object> response = new HashMap<>();

		// 入力項目のバリデーションチェック
		if (this.inputParamValidatedForGetPlanCharge(planTypeId, licenseCount, storageCount, response)) {
			// エラーが存在する場合
			return response;
		}

		//
		// ※以下の処理ではテナントDBへのアクセスは行わないため、DB切り替えの処理は行わない
		//
		
		// 返却値
		// ライセンス数、ストレージ容量、ライセンス料、ストレージ料、合計料金
		PlanInfo planChargeInfo = service.getPlanChargeInfo(planTypeId, Long.valueOf(licenseCount), Long.valueOf(storageCount));

		response.put("planInfo", planChargeInfo);
		response.put("succeeded", true);
		response.put("message", getMessage(MessageEnum.MSG_I00002.getMessageKey()));

		return response;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================
	/**
	 * Indexメソッドの返却画面に表示するメッセージを取得
	 * 
	 * @param sessionDto
	 * @param model
	 * @param planStatus
	 * @param expiredAt
	 * @return
	 */
	private MessageHolder getIndexMessage(PlanSettingSessionInfoDto sessionDto, Model model, PlanStatus planStatus, LocalDateTime expiredAt) {

		MessageHolder retunrMsgHolder = null;

		// リダイレクト時のメッセージを取得
		MessageHolder redirectMsgHolder = this.getRedirectMessage(model);

		if (redirectMsgHolder.hasAnyMessage()) {
			// リダイレクトメッセージが存在する場合
			retunrMsgHolder = redirectMsgHolder;
		} else {
			// 初期表示メッセージを取得
			retunrMsgHolder = this.getInitDisplayMessage(sessionDto, planStatus, expiredAt);
		}

		return retunrMsgHolder;
	}

	/**
	 * リダイレクト時に設定したメッセージを取得する ※メッセージが設定されていない場合はnullを返却する
	 *
	 * @param model
	 * @return
	 */
	private MessageHolder getRedirectMessage(Model model) {

		MessageHolder msgHolder = null;
		Object obj = model.asMap().get(DefaultController.MSG_HOLDER_NAME);
		if (obj != null) {
			msgHolder = (MessageHolder) model.asMap().get(DefaultController.MSG_HOLDER_NAME);
		}

		return msgHolder;
	}

	/**
	 * 初期表示時の表示メッセージを取得
	 * 
	 * @param sessionDto
	 * @param nowPlanStatus
	 * @param expiredAt
	 * @return
	 */
	private MessageHolder getInitDisplayMessage(PlanSettingSessionInfoDto sessionDto, PlanStatus nowPlanStatus, LocalDateTime expiredAt) {

		String message = null;
		MessageLevel level = null;

		boolean isOnlyPlanSettingAccessible = sessionDto.isOnlyPlanSettingAccessible();

		if (!isOnlyPlanSettingAccessible) {
			// プラン画面のみのアクセス制限がかかっていない場合（通常アクセスが可能な場合）
			// （右記以外のステータスの場合 （無料プランの利用期限が切れた場合 or 解約後の利用期限が切れた場合 or プラン変更中の利用期限が切れた場合））

			String expiredDateStr = null;

			switch (nowPlanStatus) {
			case CANCELED:
				expiredDateStr = DateUtils.parseToString(expiredAt, DateUtils.DATE_JP_YYYY_MM_DD_ZERO_FILL);
				message = getMessage(MessageEnum.MSG_W00006.getMessageKey(), expiredDateStr);
				level = MessageLevel.WARN;
				break;
			case CHANGING:
				expiredDateStr = DateUtils.parseToString(expiredAt, DateUtils.DATE_JP_YYYY_MM_DD_ZERO_FILL);
				message = getMessage(MessageEnum.MSG_W00007.getMessageKey(), expiredDateStr);
				level = MessageLevel.WARN;
				break;
			default:
				break;
			}

		} else {

			switch (nowPlanStatus) {
			case FREE:
				message = getMessage(MessageEnum.MSG_W00003.getMessageKey());
				level = MessageLevel.WARN;
				break;
			case CANCELED:
				message = getMessage(MessageEnum.MSG_W00004.getMessageKey());
				level = MessageLevel.WARN;
				break;
			case CHANGING:
				message = getMessage(MessageEnum.MSG_W00005.getMessageKey());
				level = MessageLevel.WARN;
				break;
			default:
				break;
			}
		}

		if (StringUtils.isEmpty(message) || level == null) {
			return null;
		}

		return MessageHolder.ofLevel(level, message);
	}

	/**
	 * Formの値についてサニタイズ処理を行う。
	 *
	 * <pre>
	 *  Formの値がajaxで送信された場合はthymeleafのエスケープ処理が行われないため、独自に実施。
	 * </pre>
	 * 
	 * @param viewForm
	 */
	private void sanitizeAjaxFormValue(PlanSettingForm viewForm) {

		// カード名義人（名、性）
		String cardFirstNameForSave = StringUtils.escapeHtml(viewForm.getCardFirstNameForSave());
		String cardLastNameForSave = StringUtils.escapeHtml(viewForm.getCardLastNameForSave());

		viewForm.setCardFirstNameForSave(cardFirstNameForSave);
		viewForm.setCardLastNameForSave(cardLastNameForSave);
	}

	//////////////////// ▼ 入力値バリデーション ▼ ////////////////////
	
	/**
	 * 画面独自のバリデーションチェック（保存内容確認モーダル表示時）
	 * 
	 * @param confirmForm
	 * @return
	 */
	private boolean inputFormValidatedForGetPlanSaveConfirmFragment(PlanSettingSaveConfirmForm confirmForm) {
		boolean error = false;
		
		String planTypeBeforeStr = confirmForm.getPlanTypeBefore();
		String planTypeAfterStr = confirmForm.getPlanTypeAfter();
		
		// プランタイプの妥当性チェック
		PlanType planTypeBefore = PlanType.of(planTypeBeforeStr);
		PlanType planTypeAfter = PlanType.of(planTypeAfterStr);
		
		if (planTypeBefore == null || planTypeAfter == null) {
			error = true;
			return error; 
		}
		
		return error;
	}
	
	/**
	 * 画面独自のバリデーションチェック（無料プラン内容の変更時）
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean inputFormValidatedForFreePlanUpdate(PlanSettingForm viewForm, Map<String, Object> response) {
		boolean error = false;

		// 無料期間中のプラン登録の場合と同じチェック
		error = this.inputFormValidatedForPlanRegistDuringFreeTrial(viewForm, response);

		return error;
	}

	/**
	 * 画面独自のバリデーションチェック（無料期間中のプラン登録時）
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean inputFormValidatedForPlanRegistDuringFreeTrial(PlanSettingForm viewForm, Map<String, Object> response) {
		boolean error = false;
		
		// プラン登録の場合と同じチェック
		error = this.inputFormValidatedForPlanRegist(viewForm, response);
		
		if (error) {
			// エラーがすでにあればここでチェック終了
			return error;
		}
		
		List<FieldError> errorList = new ArrayList<>();
		
		// ストレージ量の数値の妥当性チェック（存在しない値の場合のチェックは上記のプラン登録のバリデーションで行うため、無料期間用の値かどうかのチェックのみ行う）
		String storageCapacityForSave = viewForm.getStorageCapacityForSave();
		int storageCapacityForSaveInt = Integer.valueOf(storageCapacityForSave);
		StoragePrice storagePrice = StoragePrice.valueOf(storageCapacityForSaveInt);
		if (storagePrice.getQuantity() != PlanConstant.FREE_PLAN_STORAGE_CAPACITY) {
			FieldError fieldError = new FieldError("storageCapacityForSave", "storageCapacityForSave", VALID_MSG_STORAGE_IS_INVALID);
			errorList.add(fieldError);
		}
		
		if (errorList.size() > 0) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", errorList);

			return error = true;
		}
		
		return error;
	}
	
	/**
	 * 画面独自のバリデーションチェック（無料期間中のプラン更新時）
	 * 
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean inputFormValidatedForPlanUpdateDuringFreeTrial(PlanSettingForm viewForm, Map<String, Object> response) {
		boolean error = false;
		
		// 無料期間中のプラン登録の場合と同じチェック
		error = this.inputFormValidatedForPlanRegistDuringFreeTrial(viewForm, response);
		
		return error;
	}
	
	/**
	 * 画面独自のバリデーションチェック（プラン登録時）
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean inputFormValidatedForPlanRegist(PlanSettingForm viewForm, Map<String, Object> response) {
		boolean error = false;

		List<FieldError> errorList = new ArrayList<>();

		// 必須入力チェック
		
		String planTypeIdForSave = viewForm.getPlanTypeIdForSave();
		if (StringUtils.isEmpty(planTypeIdForSave)) {
			FieldError fieldError = new FieldError("planTypeIdForSave", "planTypeIdForSave", VALID_MSG_PLANTYPE_IS_REQUIRED);
			errorList.add(fieldError);
		}
		String licenseNumForSave = viewForm.getLicenseNumForSave();
		if (StringUtils.isEmpty(licenseNumForSave)) {
			FieldError fieldError = new FieldError("licenseNumForSave", "licenseNumForSave", VALID_MSG_LICENSE_IS_REQUIRED);
			errorList.add(fieldError);
		}
		String storageCapacityForSave = viewForm.getStorageCapacityForSave();
		if (StringUtils.isEmpty(storageCapacityForSave)) {
			FieldError fieldError = new FieldError("storageCapacityForSave", "storageCapacityForSave", VALID_MSG_STORAGE_IS_REQUIRED);
			errorList.add(fieldError);
		}

		// 必須項目のチェックでNGでの場合はここでチェック終了
		if (errorList.size() > 0) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", errorList);

			return error = true;
		}

		// プランタイプの妥当性チェック
		PlanType planType = PlanType.of(planTypeIdForSave);
		if (planType == null) {
			FieldError fieldError = new FieldError("planTypeIdForSave", "planTypeIdForSave", VALID_MSG_PLANTYPE_IS_INVALID);
			errorList.add(fieldError);
		}
		
		// ストレージ量の数値の妥当性チェック
		int storageCapacityForSaveInt = Integer.valueOf(storageCapacityForSave);
		StoragePrice storagePrice = StoragePrice.valueOf(storageCapacityForSaveInt);
		if (storagePrice == null) {
			FieldError fieldError = new FieldError("storageCapacityForSave", "storageCapacityForSave", VALID_MSG_STORAGE_IS_INVALID);
			errorList.add(fieldError);
		}

		if (errorList.size() > 0) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", errorList);

			return error = true;
		}

		return error;
	}

	/**
	 * 画面独自のバリデーションチェック（プラン更新時）
	 *
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean inputFormValidatedForPlanUpdate(PlanSettingForm viewForm, Map<String, Object> response) {
		boolean error = false;

		// プラン登録の場合と同じチェック
		error = this.inputFormValidatedForPlanRegist(viewForm, response);

		return error;
	}

	/**
	 * 画面独自のバリデーションチェック（プラン情報取得時）
	 *
	 * @param planTypeId
	 * @param licenseCount
	 * @param storageCount
	 * @param response
	 * @return
	 */
	private boolean inputParamValidatedForGetPlanCharge(String planTypeId, String licenseCount, String storageCount, Map<String, Object> response) {
		boolean error = false;

		PlanSettingForm viewForm = new PlanSettingForm();

		viewForm.setPlanTypeIdForGetPlanCharge(planTypeId);
		viewForm.setLicenseNumForGetPlanCharge(licenseCount);
		viewForm.setStorageCapacityForGetPlanCharge(storageCount);

		Errors errors = null;

		// バリデーション
		errors = new BeanPropertyBindingResult(viewForm, viewForm.getClass().getName());
		validator.validate(viewForm, errors);

		// 入力チェック
		if (errors.hasErrors()) {
			// バリデーションエラーが存在する場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", errors.getFieldErrors());

			return error = true;
		}

		// 必須入力チェック
		List<FieldError> errorList = new ArrayList<>();

		if (StringUtils.isEmpty(planTypeId)) {
			FieldError fieldError = new FieldError("planTypeIdForGetPlanCharge", "planTypeIdForGetPlanCharge", VALID_MSG_PLANTYPE_IS_REQUIRED);
			errorList.add(fieldError);
		}
		if (StringUtils.isEmpty(licenseCount)) {
			FieldError fieldError = new FieldError("licenseNumForGetPlanCharge", "licenseNumForGetPlanCharge", VALID_MSG_LICENSE_IS_REQUIRED);
			errorList.add(fieldError);
		}
		if (StringUtils.isEmpty(storageCount)) {
			FieldError fieldError = new FieldError("storageCapacityForGetPlanCharge", "storageCapacityForGetPlanCharge", VALID_MSG_STORAGE_IS_REQUIRED);
			errorList.add(fieldError);
		}

		// 必須項目のチェックでNGでの場合はここでチェック終了
		if (errorList.size() > 0) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", errorList);

			return error = true;
		}

		// プランタイプの妥当性チェック
		PlanType planType = PlanType.of(planTypeId);
		if (planType == null) {
			FieldError fieldError = new FieldError("planTypeIdForGetPlanCharge", "planTypeIdForGetPlanCharge", VALID_MSG_PLANTYPE_IS_INVALID);
			errorList.add(fieldError);
		}
		
		// ストレージ量の数値の妥当性チェック
		int storageCountInt = Integer.valueOf(storageCount);
		StoragePrice storagePrice = StoragePrice.valueOf(storageCountInt);
		if (storagePrice == null) {
			FieldError fieldError = new FieldError("storageCapacityForGetPlanCharge", "storageCapacityForGetPlanCharge", VALID_MSG_STORAGE_IS_INVALID);
			errorList.add(fieldError);
		}

		if (errorList.size() > 0) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00001.getMessageKey()));
			response.put("errors", errorList);

			return error = true;
		}

		return error;
	}

	//////////////////// ▲ 入力値バリデーション ▲ ////////////////////
	
	//////////////////// ▼ DB値バリデーション ▼ ////////////////////
	
	/**
	 * DBアクセスを伴うチェック(無料プラン内容の変更時)
	 *
	 * @param viewForm
	 * @param nowPlanInfo
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForFreePlanUpdate(PlanSettingForm viewForm, PlanInfo nowPlanInfo, Map<String, Object> response) {
		boolean error = false;

		// 無料プラン内容の更新が行える状態かどうかをチェック
		if (!service.isFreePlanUpdatableStatus(nowPlanInfo)) {
			// 無料プラン内容の更新が行えない状態の場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00026.getMessageKey()));
			error = true;
			return error;
		}

		Long registLicenseNum = Long.valueOf(viewForm.getLicenseNumForSave());
		Long storageCapacity = Long.valueOf(viewForm.getStorageCapacityForSave());

		// 更新可能なライセンス数かどうかをチェック
		if (!service.isSaveableLicenseCount(registLicenseNum)) {
			// 更新不可なライセンス数
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00052.getMessageKey()));
			error = true;
			return error;
		}

		PlanType planType = PlanType.of(viewForm.getPlanTypeIdForSave());
		
		// プランの金額上限チェック
		if (!service.isNotPlanChargeExceedsLimit(planType, registLicenseNum, storageCapacity)) {
			// プラン金額が上限値を超える場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00078.getMessageKey()));
			error = true;
			return error;
		}

		return error;
	}
	
	/**
	 * DBアクセスを伴うチェック(無料期間中のプラン登録時)
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForPlanRegistDuringFreeTrial(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm, Map<String, Object> response) {
		boolean error = false;
		
		PlanStatus nowPlanStatus = service.getNowPlanStatus(sessionDto);
		if (nowPlanStatus != PlanStatus.FREE) {
			// 現在のステータスが「無料」でなければ、無料期間中のプラン登録は行えないのでエラーとする。
			// ※無料期間中では、「無料」以外のステータスは「有効」（既にプランが登録されている状態）しかありえない。
			//  （解約を行った場合も、解約ではなく「無料」ステータスに戻すようにし、プラン変更の場合も、停止→登録というステップを行わず、更新を行うためステータスは有効から変わらない。）
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00026.getMessageKey()));
			error = true;
			return error;
		}
		
		// 無料期間中ではないプラン登録の場合（無料期間切れ、解約期間切れの場合）は、ユーザーがアカウント管理画面へ行けないため
		// プログラム側で各アカウントを「無効」状態に変更するが、
		// 無料期間中の場合は、ユーザーはアカウント管理画面へ行くことが可能（自分で各アカウントを「無効」状態に変更し、ライセンスの利用数を調整可能）なため、
		// 設定されたライセンス数が現在の利用数より少なくても、プログラム側での対応（アカウントの「無効」化処理）は行わず、プラン更新のケースと同様バリデーションエラーとする。
		
		// 更新可能なライセンス数かどうかをチェック
		Long registLicenseNum = Long.valueOf(viewForm.getLicenseNumForSave());
		if (!service.isSaveableLicenseCount(registLicenseNum)) {
			// 更新不可なライセンス数
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00052.getMessageKey()));
			error = true;
			return error;
		}
		
		// あとはプラン登録の場合と同じ
		return this.accessDBValidatedForPlanRegist(sessionDto, viewForm, response);
	}
	
	/**
	 * DBアクセスを伴うチェック(無料期間中のプラン更新時)
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForPlanUpdateDuringFreeTrial(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm, Map<String, Object> response) {
		boolean error = false;
		
		// 現在のプランのステータス
		PlanInfo nowPlanInfo = service.getNowPlanInfo(sessionDto);
		PlanStatus nowPlanStatus = nowPlanInfo.getPlanStatus();
		// 現在が無料期間中かどうか
		boolean nowIsDuringTheFreePeriod = service.nowIsDuringTheFreePeriod(sessionDto);
		
		// 無料期間中のプラン更新が行える状態かどうかをチェック
		if (!service.isUpdatableStatusDuringFreeTrial(sessionDto, nowPlanStatus, nowIsDuringTheFreePeriod)) {
			// 更新が行えない状態の場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00089.getMessageKey()));
			error = true;
			return error;
		}

		Long changeLicenseNum = Long.valueOf(viewForm.getLicenseNumForSave());
		Long storageCapacity = Long.valueOf(viewForm.getStorageCapacityForSave());

		// 変更可能なライセンス数かどうかをチェック
		if (!service.isSaveableLicenseCount(changeLicenseNum)) {
			// 変更不可なライセンス数
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00052.getMessageKey()));
			error = true;
			return error;
		}

		PlanType planType = PlanType.of(viewForm.getPlanTypeIdForSave());
		
		// プランの金額上限チェック
		if (!service.isNotPlanChargeExceedsLimit(planType, changeLicenseNum, storageCapacity)) {
			// プラン金額が上限値を超える場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00074.getMessageKey()));
			error = true;
			return error;
		}
		
		// ストレージ量のチェックは行わない
		// -> 無料期間中はデフォルトの最も少ないストレージ量から設定の変更はできないため、
		//    現在登録されているストレージ量が、選択されたストレージ量の設定値を超える状況は発生しないため。
		
		return error;
	}
	
	/**
	 * DBアクセスを伴うチェック(プラン登録時)
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForPlanRegist(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm, Map<String, Object> response) {
		boolean error = false;

		// プラン登録が行える状態かどうかをチェック
		if (!service.isRegistableStatus(sessionDto)) {
			// プランの登録が行えない状態の場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00026.getMessageKey()));
			error = true;
			return error;
		}

		PlanType planType = PlanType.of(viewForm.getPlanTypeIdForSave());
		Long registLicenseNum = Long.valueOf(viewForm.getLicenseNumForSave());
		Long storageCapacity = Long.valueOf(viewForm.getStorageCapacityForSave());

		// プランの金額上限チェック
		if (!service.isNotPlanChargeExceedsLimit(planType, registLicenseNum, storageCapacity)) {
			// プラン金額が上限値を超える場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00074.getMessageKey()));
			error = true;
			return error;
		}

		// ストレージの妥当性チェック
		ExternalService externalService = service.getConnectStorageService();
		if (externalService != null) {
			// 外部ストレージサービス連携中の場合 -> 無料の15GB以外の場合はエラーとする
			StoragePrice storagePrice = StoragePrice.valueOf(storageCapacity.intValue());
			if (storagePrice.getQuantity() != PlanConstant.FREE_PLAN_STORAGE_CAPACITY) {
				response.put("succeeded", false);
				response.put("message", VALID_MSG_STORAGE_IS_INVALID);
				error = true;
				return error;
			}
		}
		
		PlanStatus nowPlanStatus = service.getNowPlanStatus(sessionDto);
		if (nowPlanStatus == PlanStatus.CANCELED) {
			// 「解約」ステータスからのプラン登録の場合（解約後の有効期限切れからのプラン登録のケース）
			// ※無料期間中は一番低い容量しか利用できないため、無料期間後の初回のプラン登録の場合はストレージ容量のチェックは行わない
			
			// ストレージ容量チェック
			if (!service.isSaveableStorageCapacity(storageCapacity)) {
				// 保存しようとしているストレージ量が既に利用しているストレージ量より少ない値の場合は登録不可とする
				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00079.getMessageKey()));
				error = true;
				return error;
			}
		}

		return error;
	}

	/**
	 * DBアクセスを伴うチェック（プラン更新時）
	 * 
	 * @param sessionDto
	 * @param viewForm
	 * @param nowPlanInfo
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForPlanUpdate(PlanSettingSessionInfoDto sessionDto, PlanSettingForm viewForm, PlanInfo nowPlanInfo, Map<String, Object> response) {
		boolean error = false;

		// プラン更新を行える状態かチェック
		if (!service.isUpdatableStatus(sessionDto, nowPlanInfo)) {
			// 更新が行えない状態の場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00047.getMessageKey()));
			error = true;
			return error;
		}

		// プランの登録／変更回数上限チェック
		if (!service.isNotPlanChangeCountExceedsLimit()) {
			// プラン登録／変更回数が上限値を超える場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00073.getMessageKey()));
			error = true;
			return error;
		}

		Long changeLicenseNum = Long.valueOf(viewForm.getLicenseNumForSave());
		Long storageCapacity = Long.valueOf(viewForm.getStorageCapacityForSave());

		// 変更可能なライセンス数かどうかをチェック
		if (!service.isSaveableLicenseCount(changeLicenseNum)) {
			// 変更不可なライセンス数
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00052.getMessageKey()));
			error = true;
			return error;
		}

		PlanType planType = PlanType.of(viewForm.getPlanTypeIdForSave());
		
		// プランの金額上限チェック
		if (!service.isNotPlanChargeExceedsLimit(planType, changeLicenseNum, storageCapacity)) {
			// プラン金額が上限値を超える場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00074.getMessageKey()));
			error = true;
			return error;
		}

		PlanStatus nowPlanStatus = nowPlanInfo.getPlanStatus();
		if (nowPlanStatus == PlanStatus.CHANGING) {
			// ステータスが「変更中」の場合のみチェック

			if (!service.isSameLicenseAndStorageAsCurrentRegist(sessionDto, planType, changeLicenseNum, storageCapacity)) {
				// 送信パラメータの値が現在の登録値と同じ値ではない場合（不正な値の場合）はエラーとする
				// ※プランの変更処理が中途半端な状態で終了（エラー）となってしまった場合に「変更中」ステータスとなるが、
				// 変更をしようとしたプランタイプ、ライセンス数、ストレージ量はDBに登録されている状態となるため、同じ値でしか変更中でのプラン変更を行うことは不可とする。
				// （実際の課金料金と利用内容に差が生まれないようにするため）

				// 不正なリクエストを意図して送信しない限りは起き得ない状況のため、エラーメッセージは簡易的なものとする。
				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00013.getMessageKey()));
				error = true;
				return error;
			}
		} else {
			// ステータスが「変更中」ではない場合（通常のプランの更新処理のケース）
			// ※「変更中」の場合、変更ステータスへ切り替わった際の更新処理において、ここのバリデーションを通過しているので再度実施はしない。
			// (ありえないケースではあるが、万が一バリデーションを行って判定NGとなっても、変更中の場合は送信パラメータを画面で変更できないので判定処理は行わない)

			// ストレージの妥当性チェック
			ExternalService externalService = service.getConnectStorageService();
			if (externalService != null) {
				// 外部ストレージサービス連携中の場合 -> 無料の15GB以外の場合はエラーとする
				StoragePrice storagePrice = StoragePrice.valueOf(storageCapacity.intValue());
				if (storagePrice.getQuantity() != PlanConstant.FREE_PLAN_STORAGE_CAPACITY) {
					response.put("succeeded", false);
					response.put("message", VALID_MSG_STORAGE_IS_INVALID);
					error = true;
					return error;
				}
			}
			
			// ストレージ容量チェック
			if (!service.isSaveableStorageCapacity(storageCapacity)) {
				// 保存しようとしているストレージ量が既に利用しているストレージ量より少ない値の場合は登録不可とする
				response.put("succeeded", false);
				response.put("message", getMessage(MessageEnum.MSG_E00053.getMessageKey()));
				error = true;
				return error;
			}
		}

		return error;
	}

	/**
	 * DBアクセスを伴うチェック（無料期間中のプラン削除時）
	 * 
	 * @param sessionDto
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForPlanDeleteDuringFreeTrial(PlanSettingSessionInfoDto sessionDto, Map<String, Object> response) {
		boolean error = false;

		// 現在のプラン情報
		PlanInfo nowPlanInfo = service.getNowPlanInfo(sessionDto);
		// 現在が無料期間中かどうか
		boolean nowIsDuringTheFreePeriod = service.nowIsDuringTheFreePeriod(sessionDto);
		
		// プランの削除処理が可能かをチェック
		if (!service.isExecutableStatusForPlanDeleteDuringFreeTrial(nowPlanInfo, nowIsDuringTheFreePeriod)) {
			// プランの削除処理不可
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00048.getMessageKey()));
			error = true;
			return error;
		}

		return error;
	}
	
	/**
	 * DBアクセスを伴うチェック（プラン解約時）
	 * 
	 * @param sessionDto
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForPlanCancel(PlanSettingSessionInfoDto sessionDto, Map<String, Object> response) {
		boolean error = false;

		// 解約処理が可能かをチェック
		if (!service.isExecutableStatusForCancel(sessionDto)) {
			// 解約処理不可
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00048.getMessageKey()));
			error = true;
			return error;
		}

		return error;
	}

	/**
	 * DBアクセスを伴うチェック（カード情報変更時）
	 * 
	 * @param sessionDto
	 * @param response
	 * @return
	 */
	private boolean accessDBValidatedForCardUpdate(PlanSettingSessionInfoDto sessionDto, Map<String, Object> response) {
		boolean error = false;

		// 有効なプランが登録されているかをチェック
		if (!service.isAlreadRegist(sessionDto) || service.isExpiredStatus(sessionDto)) {
			// 登録されていない、もしくは有効期限切れ（有効な状態のプランが登録されていない）状態の場合
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00047.getMessageKey()));
			error = true;
			return error;
		}

		return error;
	}
	
	//////////////////// ▲ DB値バリデーション ▲ ////////////////////

}
