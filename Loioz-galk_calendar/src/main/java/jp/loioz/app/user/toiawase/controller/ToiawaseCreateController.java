package jp.loioz.app.user.toiawase.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.toiawase.form.ToiawaseCreateInputForm;
import jp.loioz.app.user.toiawase.form.ToiawaseCreateViewForm;
import jp.loioz.app.user.toiawase.service.ToiawaseCreateService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 問い合わせ新規作成Controller
 */
@Controller
@RequestMapping("user/toiawase")
public class ToiawaseCreateController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/toiawase/toiawaseCreate";
	/** 画面描写で使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";
	/** 入力フォームオブジェクト */
	private static final String INPUT_FORM_NAME = "inputForm";
	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";

	/** 問い合わせ新規作成サービス */
	@Autowired
	private ToiawaseCreateService service;

	/** 入力フォームのセットアップ処理 */
	@ModelAttribute(INPUT_FORM_NAME)
	ToiawaseCreateInputForm setUpInputForm() {
		return new ToiawaseCreateInputForm();
	}

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	/** リダイレクト先の初期化 */
	@ModelAttribute
	RedirectViewBuilder setUpRedirectViewBuilder(RedirectAttributes redirectAttributes) {
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(),
				controller -> controller.create());
		return new RedirectViewBuilder(redirectAttributes, redirectPath);
	}

	/**
	 * 問い合わせ作成画面
	 *
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {

		// viewFormの作成
		ToiawaseCreateViewForm viewForm = service.createVewForm();

		// 画面返却
		return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 新規登録処理
	 *
	 * @param inputForm
	 * @param result
	 * @param redirectViewBuilder
	 * @return
	 */
	@RequestMapping(value = "/regist", method = RequestMethod.POST)
	public ModelAndView regist(
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) ToiawaseCreateInputForm inputForm,
			BindingResult result,
			RedirectViewBuilder redirectViewBuilder) {

		// ファイルアップロードバリデーション
		this.fileUploadValidate(inputForm, result);

		// 入力エラー
		if (result.hasErrors()) {
			return redirectViewBuilder.buildAsError(INPUT_FORM_NAME, inputForm, result, getMessage(MessageEnum.MSG_E00001));
		}

		try {
			// 登録処理
			Long toiawaseSeq = service.regist(inputForm);

			String redirectPath = ModelAndViewUtils.getRedirectPath(ToiawaseDetailController.class, controller -> controller.detail(toiawaseSeq));

			// 詳細画面にリダイレクト
			return redirectViewBuilder.redirectPath(redirectPath).build(MessageHolder.ofInfo(getMessage(MessageEnum.MSG_I00005)));

		} catch (AppException e) {
			return redirectViewBuilder.buildAsError(INPUT_FORM_NAME, inputForm, result, getMessage(e.getErrorType()));
		}
	}

	/**
	 * ファイルに対するバリデーション
	 * 
	 * @param inputForm
	 * @param result
	 */
	private void fileUploadValidate(ToiawaseCreateInputForm inputForm, BindingResult result) {

		String fieldName = "uploadFile";
		List<MultipartFile> uploadFileList = inputForm.getUploadFile();

		// アップロードファイルがなければ、なにもしない
		if (ListUtils.isEmpty(inputForm.getUploadFile())) {
			return;
		}

		try {
			// 合計ファイルサイズを算出
			long sumFileSize = uploadFileList.stream().mapToLong(MultipartFile::getSize).sum();

			if (CommonConstant.TOIAWASE_FILE_UPLOAD_MAX_STRAGE.longValue() < sumFileSize) {
				result.rejectValue(fieldName, null, getMessage(MessageEnum.MSG_E00087, "10MB"));
			}
		} catch (Exception e) {
			// ファイルサイズの数値変換・計算で発生されるエラーなので
			result.rejectValue(fieldName, null, getMessage(MessageEnum.MSG_E00087, "10MB"));
		}

	}

}
