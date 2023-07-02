package jp.loioz.app.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.ViewType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.service.http.UserAgentService;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.ModelAndViewUtils;
import jp.loioz.common.utility.SessionUtils;
import lombok.Data;

/**
 * コントローラの基底クラス
 */
public abstract class DefaultController {

	/** view側でMessageHolderを参照するための属性名 */
	protected static final String MSG_HOLDER_NAME = "msgHolder";

	/** コントローラーに対するエラー発生時のモーダルviewパス */
	protected static final String AJAX_MODAL_PATH_ERROR = "common/messageArea::JsAlertError";

	/** コントローラーに対するエラー発生時のモーダルviewパス */
	protected static final String AJAX_MODAL_PATH_ERROR_HISTORY_BACK = "common/messageArea::JsAlertErrorHistoryBack";

	/** ロガー */
	@Autowired
	Logger logger;

	/** HttpServletResponse */
	@Autowired
	HttpServletResponse response;

	/** メッセージサービス */
	@Autowired
	MessageService messageService;

	/** UserAgentサービスクラス */
	@Autowired
	private UserAgentService userAgentService;

	/** メッセージを保持するクラス */
	@ModelAttribute(MSG_HOLDER_NAME)
	MessageHolder setUpMessageHolder() {
		return new MessageHolder();
	}

	/**
	 * ModelAndViewを取得
	 *
	 * @param form 画面フォーム
	 * @param viewPath 返却するviewのパス
	 * @param formName view側でフォームオブジェクトを参照するための属性名
	 * @return ModelAndViewオブジェクト
	 */
	public ModelAndView getMyModelAndView(Object form, String viewPath, String formName) {

		// ModelAndViewオブジェクトを取得
		ModelAndView mv = ModelAndViewUtils.getModelAndView(viewPath);
		// フォームオブジェクトを設定
		mv.addObject(formName, form);

		return mv;
	}
	
	/**
	 * ModelAndViewを取得（エラー情報も設定）
	 * 
	 * @param form
	 * @param viewPath
	 * @param formName
	 * @param errors
	 * @return
	 */
	public ModelAndView getMyModelAndViewWithErrors(Object form, String viewPath, String formName, BindingResult errors) {

		// フォーム情報を設定したModelAndView
		ModelAndView mv = this.getMyModelAndView(form, viewPath, formName);
		
		// エラー情報を追加
		mv.addObject(BindingResult.MODEL_KEY_PREFIX + formName, errors);

		return mv;
	}

	/**
	 * ModelAndViewを取得
	 *
	 * @param form 画面フォーム
	 * @param viewPath 返却するviewのパス
	 * @param formName view側でフォームオブジェクトを参照するための属性名
	 * @param msgHolder 画面に返却するメッセージを保持するオブジェクト
	 * @return ModelAndViewオブジェクト
	 */
	public ModelAndView getMyModelAndView(Object form, String viewPath, String formName, MessageHolder msgHolder) {

		// ModelAndViewオブジェクトを取得
		ModelAndView mv = this.getMyModelAndView(form, viewPath, formName);
		// メッセージホルダーを設定
		mv.addObject(MSG_HOLDER_NAME, msgHolder);

		return mv;
	}

	/**
	 * ModelAndViewを取得
	 *
	 * @param msgHolder 画面に返却するメッセージを保持するオブジェクト
	 * @return ModelAndViewオブジェクト
	 */
	public ModelAndView getMyModelAndViewByModalError(MessageHolder msgHolder) {

		// ModelAndViewオブジェクトを取得
		ModelAndView mv = this.getMyModelAndView(msgHolder, AJAX_MODAL_PATH_ERROR, MSG_HOLDER_NAME);

		return mv;
	}

	/**
	 * ModelAndViewを取得
	 *
	 * @param msgHolder 画面に返却するメッセージを保持するオブジェクト
	 * @return ModelAndViewオブジェクト
	 */
	public ModelAndView getMyModelAndViewByModalErrorHistoryBack(MessageHolder msgHolder) {

		// ModelAndViewオブジェクトを取得
		ModelAndView mv = this.getMyModelAndView(msgHolder, AJAX_MODAL_PATH_ERROR_HISTORY_BACK, MSG_HOLDER_NAME);

		return mv;
	}

	/**
	 * リダイレクトを行うModelAndViewを取得
	 *
	 * @return ModelAndViewオブジェクト
	 */
	public ModelAndView getRedirectModelAndView(String redirectPath) {

		// リダイレクト用のModelAndViewオブジェクトを取得
		ModelAndView mv = ModelAndViewUtils.getRedirectModelAndView(redirectPath);

		return mv;
	}

	/**
	 * メッセージリソースからメッセージを取得する
	 *
	 * @param messageEnum
	 * @return メッセージ
	 */
	public String getMessage(MessageEnum messageEnum) {
		return messageService.getMessage(messageEnum, SessionUtils.getLocale());
	}

	/**
	 * メッセージリソースからメッセージを取得する
	 *
	 * @param messageEnum
	 * @param args
	 * @return メッセージ
	 */
	public String getMessage(MessageEnum messageEnum, String... args) {
		return messageService.getMessage(messageEnum.getMessageKey(), SessionUtils.getLocale(), args);
	}

	/**
	 * メッセージリソースからメッセージを取得する
	 *
	 * @param messageKey
	 * @return メッセージ
	 */
	public String getMessage(String messageKey) {
		return messageService.getMessage(messageKey, SessionUtils.getLocale());
	}

	/**
	 * メッセージリソースからメッセージを取得する
	 *
	 * @param messageKey
	 * @param args
	 * @return メッセージ
	 */
	public String getMessage(String messageKey, String... args) {
		return messageService.getMessage(messageKey, SessionUtils.getLocale(), args);
	}

	/**
	 * スマホページを表示するかを判別する
	 * 
	 * @param request
	 * @return
	 */
	public boolean redirectToSPView(HttpServletRequest request) {

		if (!userAgentService.isSmartPhone(request)) {
			return false;
		}

		// スマホ端末でもPCページを表示する導線があるので、UserDetail(セッション)から判別する
		if (ViewType.PC == SessionUtils.getViewType()) {
			return false;
		}

		return true;
	}
	
	/**
	 * HTMLを返却するajax処理の処理結果を設定する（処理成功の場合）
	 */
	public void setAjaxProcResultSuccess() {
		this.setAjaxProcResultSuccess("");
	}
	
	/**
	 * HTMLを返却するajax処理の処理結果とメッセージを設定する（処理成功の場合）
	 * 
	 * @param message
	 */
	public void setAjaxProcResultSuccess(String message) {
		this.setAjaxProcResult(CommonConstant.HEADER_VALUE_OF_AJAX_PROC_RESULT_SUCCESS, message);
	}
	
	/**
	 * HTMLを返却するajax処理の処理結果を設定する（処理失敗の場合）
	 */
	public void setAjaxProcResultFailure() {
		this.setAjaxProcResultFailure("");
	}
	
	/**
	 * HTMLを返却するajax処理の処理結果とメッセージを設定する（処理失敗の場合）
	 * 
	 * @param message
	 */
	public void setAjaxProcResultFailure(String message) {
		this.setAjaxProcResult(CommonConstant.HEADER_VALUE_OF_AJAX_PROC_RESULT_FAILURE, message);
	}

	/**
	 * HTMLを返却するajax処理の処理結果を設定する
	 * 
	 * <pre>
	 * 結果の設定場所はHttpServletResponseのHeader
	 * 
	 * ※このメソッドは、下記の関連事項のメソッドでは対応できないケースでのみ利用すること。
	 *  (レスポンスの結果が2択では対応できない場合)
	 * </pre>
	 * 
	 * @see  DefaultController#setAjaxProcResultSuccess()
	 * @see  DefaultController#setAjaxProcResultFailure()
	 * @see  DefaultController#setAjaxProcResultFailure(String)
	 */
	public void setAjaxProcResult(String result, String message) {
		
		// メッセージはBase64でエンコードしてheaderに設定する
		Charset charset = StandardCharsets.UTF_8;
		String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes(charset));
		
		this.response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT, result);
		this.response.setHeader(CommonConstant.HEADER_NAME_OF_AJAX_PROC_RESULT_MESSAGE, encodedMessage);
	}
	

	/**
	 * HTMLを返却するajax処理中に発生したエラー情報を設定する
	 * 
	 * <pre>
	 * 本メソッドは、予期せぬエラー時にのみ呼び出すこと。
	 * try-catch時にExceptionクラス(継承したExceptionではおこなわない)をキャッチして、
	 * 本メソッドの呼び出しを行うこと。
	 * </pre>
	 * 
	 * @param ex
	 * @return null
	 */
	public ModelAndView ajaxException(Exception ex) {

		// 想定しないシステムエラー
		setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
		logger.error("error", ex);

		return null;
	}

	/**
	 * リダイレクト画面情報のBuilder<br>
	 * RedirectAttributesをラップして、RedirectViewを持ったModelAndViewを作成する。
	 */
	@Data
	public class RedirectViewBuilder {

		private final RedirectAttributes redirectAttributes;
		private String redirectPath;

		/**
		 * コンストラクタ
		 *
		 * @param redirectAttributes
		 * @param redirectPath
		 */
		public RedirectViewBuilder(RedirectAttributes redirectAttributes, String redirectPath) {
			this.redirectAttributes = redirectAttributes;
			this.redirectPath = redirectPath;
		}

		/**
		 * リダイレクトパスを設定する
		 *
		 * @param redirectPath リダイレクトパス
		 * @return このオブジェクト
		 */
		public RedirectViewBuilder redirectPath(String redirectPath) {
			this.redirectPath = redirectPath;
			return this;
		}

		/**
		 * メッセージを設定する
		 *
		 * @param msgHolder メッセージ
		 * @return このオブジェクト
		 */
		public RedirectViewBuilder message(MessageHolder msgHolder) {
			redirectAttributes.addFlashAttribute(MSG_HOLDER_NAME, msgHolder);
			return this;
		}

		/**
		 * リダイレクト先に引き継ぐ属性を追加する
		 *
		 * @param attributeName 属性名
		 * @param attributeValue 属性値
		 * @return このオブジェクト
		 */
		public RedirectViewBuilder addFlashAttribute(String attributeName, @Nullable Object attributeValue) {
			redirectAttributes.addFlashAttribute(attributeName, attributeValue);
			return this;
		}

		/**
		 * フォームを設定する
		 *
		 * @param formName フォーム名
		 * @param form フォーム
		 * @return このオブジェクト
		 */
		public RedirectViewBuilder form(String formName, Object form) {
			return addFlashAttribute(formName, form);
		}

		/**
		 * フォームを設定する
		 *
		 * @param formName フォーム名
		 * @param form フォーム
		 * @param errors バリデーション結果
		 * @return このオブジェクト
		 */
		public RedirectViewBuilder form(String formName, Object form, BindingResult errors) {
			form(formName, form);
			redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + formName, errors);
			return this;
		}

		/**
		 * エラー時の画面を作成する
		 *
		 * @param formName フォーム名
		 * @param form フォーム
		 * @param errors バリデーション結果
		 * @param errorMsg エラーメッセージ
		 * @return 画面情報
		 */
		public ModelAndView buildAsError(String formName, Object form, BindingResult errors, String errorMsg) {
			return this
					.form(formName, form, errors)
					.build(MessageHolder.ofError(errorMsg));
		}

		/**
		 * メッセージを指定して画面を作成する
		 *
		 * @param msgHolder メッセージ
		 * @return 画面情報
		 */
		public ModelAndView build(MessageHolder msgHolder) {
			return this
					.message(msgHolder)
					.build();
		}

		/**
		 * 画面を作成する
		 *
		 * @return 画面情報
		 */
		public ModelAndView build() {
			return ModelAndViewUtils.getRedirectModelAndView(redirectPath);
		}
	}
}
