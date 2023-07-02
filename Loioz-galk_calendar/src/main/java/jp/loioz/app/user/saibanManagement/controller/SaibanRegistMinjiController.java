package jp.loioz.app.user.saibanManagement.controller;

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
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.user.saibanManagement.form.SaibanRegistMinjiInputForm;
import jp.loioz.app.user.saibanManagement.service.SaibanRegistMinjiService;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.domain.value.AnkenId;

/**
 * 裁判登録（民事）画面のコントローラークラス
 */
@Controller
@RequestMapping(value = "user/saibanMinjiManagement/{ankenId}/new")
public class SaibanRegistMinjiController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/saibanManagement/saibanRegistMinji";
	
	/** 裁判民事基本情報入力フォームfragmentのパス */
	private static final String SAIBAN_MINJI_REGIST_BASIC_INPUT_FRAGMENT_PATH = "user/saibanManagement/saibanRegistMinjiFragment::saibanRegistMinjiBasicInputFragment";

	/** 裁判民事基本情報の入力用フォームオブジェクト名 */
	private static final String SAIBAN_MINJI_REGIST_BASIC_INPUT_FORM_NAME = "saibanRegistMinjiBasicInputForm";
	
	/** フォームオブジェクト名 */
	private static final String INPUT_FORM_NAME = "inputForm";
	
	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyInputForm";
	
	/** サービスクラス */
	@Autowired
	private SaibanRegistMinjiService service;
	
	/** 共通案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;
	
	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;
	
	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	// =========================================================================
	// public メソッド
	// =========================================================================
	
	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable Long ankenId) {
		
		// 親の入力フォーム
		SaibanRegistMinjiInputForm inputForm = new SaibanRegistMinjiInputForm();
		inputForm.setAnkenId(AnkenId.of(ankenId));
		
		ModelAndView mv = getMyModelAndView(inputForm, MY_VIEW_PATH, INPUT_FORM_NAME);
		
		// 基本情報入力フォームオブジェクトを設定
		SaibanRegistMinjiInputForm.SaibanRegistMinjiBasicInputForm basicInputForm = service.createRegistInputForm(ankenId);
		mv.addObject("saibanRegistMinjiBasicInputForm", basicInputForm);

		return mv;
	}
	
	/**
	 * 裁判（民事）の基本情報の登録
	 * 
	 * @param basicInputForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/registSaibanMinjiBasic", method = RequestMethod.POST)
	public ModelAndView registSaibanMinjiBasic(
			@PathVariable Long ankenId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) SaibanRegistMinjiInputForm.SaibanRegistMinjiBasicInputForm basicInputForm,
			BindingResult result) {

		ModelAndView mv = null;

		// 表示用データの設定
		service.setDisplayData(basicInputForm, ankenId);
		
		// 担当者の相関チェック
		commonAnkenService.validateTanto(basicInputForm.getTantoLawyer(), result, "tantoLawyer", "担当弁護士");
		commonAnkenService.validateTanto(basicInputForm.getTantoJimu(), result, "tantoJimu", "担当事務員");
		
		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 入力フォームにerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(basicInputForm, SAIBAN_MINJI_REGIST_BASIC_INPUT_FRAGMENT_PATH, SAIBAN_MINJI_REGIST_BASIC_INPUT_FORM_NAME, result);
		}
		
		// 画面独自のバリデーションチェック
		// 特になし
		
		// DBバリデーション
		// 特になし

		try {

			// 登録処理
			service.registSaibanMinji(basicInputForm, ankenId);

			// 保存終了後、レスポンスを受けるajax側で裁判編集画面へ遷移するために、saibanSeqとankenIdが必要なので、
			// saibanSeq、ankenId取得のために、画面情報を返却する
			mv = getMyModelAndView(basicInputForm, SAIBAN_MINJI_REGIST_BASIC_INPUT_FRAGMENT_PATH, SAIBAN_MINJI_REGIST_BASIC_INPUT_FORM_NAME);

		} catch (AppException ex) {

			// 想定されるエラー(楽観ロックエラーなど)
			super.setAjaxProcResultFailure(getMessage(ex.getErrorType()));
			return null;

		} catch (Exception ex) {

			// 想定しないシステムエラー
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "裁判情報"));
		return mv;
	}
	
}
