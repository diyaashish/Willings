package jp.loioz.app.user.toiawase.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.toiawase.form.ToiawaseDetailInputForm;
import jp.loioz.app.user.toiawase.form.ToiawaseDetailViewForm;
import jp.loioz.app.user.toiawase.service.ToiawaseDetailService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * 問い合わせ詳細コントローラークラス
 */
@Controller
@RequestMapping("user/toiawase/{toiawaseSeq}")
public class ToiawaseDetailController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/toiawase/toiawaseDetail";
	/** 画面描写で使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";
	/** 入力フォームオブジェクト */
	private static final String INPUT_FORM_NAME = "inputForm";
	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";

	@Autowired
	private ToiawaseDetailService service;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** 入力フォームのセットアップ処理 */
	@ModelAttribute(INPUT_FORM_NAME)
	ToiawaseDetailInputForm setUpInputForm(@PathVariable Long toiawaseSeq) {
		ToiawaseDetailInputForm inputForm = new ToiawaseDetailInputForm();
		return inputForm;
	}

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	/** リダイレクト先の初期化 */
	@ModelAttribute
	RedirectViewBuilder setUpRedirectViewBuilder(RedirectAttributes redirectAttributes, @PathVariable Long toiawaseSeq) {
		String redirectPath = ModelAndViewUtils.getRedirectPath(this.getClass(),
				controller -> controller.detail(toiawaseSeq));
		return new RedirectViewBuilder(redirectAttributes, redirectPath);
	}

	/**
	 * 問い合わせ詳細画面
	 *
	 * @param toiawaseSeq
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView detail(@PathVariable Long toiawaseSeq) {

		try {
			// viewFormの作成
			ToiawaseDetailViewForm viewForm = service.createVewForm(toiawaseSeq);
			return getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);

		} catch (DataNotFoundException e) {
			// NotFound
			return ModelAndViewUtils.getCommonErrorPage();
		}

	}

	/**
	 * 問い合わせ詳細の追加処理
	 *
	 * @param toiawaseSeq
	 * @param inputForm
	 * @param result
	 * @param redirectViewBuilder
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView add(
			@PathVariable Long toiawaseSeq,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) ToiawaseDetailInputForm inputForm,
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
			service.addDetail(inputForm);

			// 画面返却
			return redirectViewBuilder.build(MessageHolder.ofInfo(getMessage(MessageEnum.MSG_I00006)));

		} catch (AppException e) {
			return redirectViewBuilder.buildAsError(INPUT_FORM_NAME, inputForm, result, getMessage(e.getErrorType()));
		}

	}

	/**
	 * 問い合わせ内容を解決済みに変更
	 *
	 * @param toiawaseSeq
	 * @param redirectViewBuilder
	 * @return
	 */
	@RequestMapping(value = "/completed", method = RequestMethod.POST)
	public ModelAndView completed(@PathVariable Long toiawaseSeq, RedirectViewBuilder redirectViewBuilder) {

		ModelAndView mv = null;
		
		try {
			// 登録処理
			service.completed(toiawaseSeq);

			// 返却画面取得
			mv = redirectViewBuilder.build(MessageHolder.ofInfo(
					getMessage(MessageEnum.MSG_I00032)));

		} catch (AppException e) {
			return redirectViewBuilder.build(MessageHolder.ofError(
					getMessage(e.getErrorType())));
		}
		
		try {
			// 登録処理とは別トランザクションで行う
			
			// システム管理者への通知メール送信
			service.sendToiawaseCompletedMail2Admin(toiawaseSeq);
		} catch (Exception e) {
			logger.warn("問い合わせ解決済み通知のメール送信に失敗しました。");
		}
		
		return mv;
	}

	/**
	 * 問い合わせ詳細の既読フラグを更新する ※ 詳細画面レンダリングが完了したタイミングでJSにより発火
	 *
	 * @param toiawaseSeq
	 */
	@ResponseBody
	@RequestMapping(value = "/detailOpen", method = RequestMethod.POST)
	public void detailOpen(@PathVariable Long toiawaseSeq) {

		try {
			// 既読フラグの更新処理
			service.detailOpen(toiawaseSeq);

		} catch (Exception e) {
			// 既読フラグの更新が失敗してもロジック上影響はないので、何もしない
		}

	}

	/**
	 * ファイルに対するバリデーション
	 *
	 * @param inputForm
	 * @param result
	 */
	private void fileUploadValidate(ToiawaseDetailInputForm inputForm, BindingResult result) {

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
