package jp.loioz.app.user.depositRecvDetail.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.propertyEditor.EmptyToNullEditor;
import jp.loioz.app.user.depositRecvDetail.dto.DepositRecvDetailListDto;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailInputForm;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailSearchForm;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailViewForm;
import jp.loioz.app.user.depositRecvDetail.service.DepositRecvDetailService;
import jp.loioz.common.constant.CommonConstant.AccgDocType;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.log.Logger;

/**
 * 「預り金明細」画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0002})
@Controller
@RequestMapping(value = "user/depositRecvDetail/{personId}/{ankenId}")
@SessionAttributes(DepositRecvDetailController.SEARCH_FORM_NAME)
public class DepositRecvDetailController extends DefaultController {

	/** 入力フォームオブジェクト名(初期化なし) */
	private static final String EMPTY_INPUT_FORM_NAME = "emptyDepositRecvDetailInputForm";

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/depositRecvDetail/depositRecvDetail";

	/** 預り金明細の表示フラグメントviewのパス */
	private static final String DEPOSIT_RECV_DETAIL_FRAGMENT_VIEW_PATH = "user/depositRecvDetail/depositRecvDetailFragment::depositRecvDetailFragment";

	/** 預り金明細の登録フラグメントinputのパス */
	private static final String DEPOSIT_RECV_DETAIL_FRAGMENT_INPUT_PATH = "user/depositRecvDetail/depositRecvDetailFragment::depositRecvDetailInputRowFragment";

	/** 預り金明細の1行編集フラグメントinputのパス */
	private static final String DEPOSIT_RECV_DETAIL_FRAGMENT_EDIT_ROW_PATH = "user/depositRecvDetail/depositRecvDetailFragment::depositRecvDetailEditRowFragment";

	/** 預り金明細の1行表示フラグメントinputのパス */
	private static final String DEPOSIT_RECV_DETAIL_FRAGMENT_VIEW_ROW_PATH = "user/depositRecvDetail/depositRecvDetailFragment::depositRecvDetailViewRowFragment";

	/** 項目候補値のデータリストFragmentパス */
	private static final String DEPOSIT_RECV_ITEM_LIST_FRAGMENT_PATH = "user/depositRecvDetail/depositRecvDetailFragment::depositRecvItemFragment";

	/** 検索条件のフォームオブジェクト名 */
	public static final String SEARCH_FORM_NAME = "depositRecvDetailSearchForm";

	/** 表示で使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** 登録で使用するフォームオブジェクト名 */
	private static final String INPUT_FORM_NAME = "inputForm";

	/** 編集で使用するフォームオブジェクト名 */
	private static final String EDIT_FORM_NAME = "editForm";

	/** 明細閉じる処理後の1行表示で使用するフォームオブジェクト名 */
	private static final String VIEW_ROW_NAME = "depositRecvDetail";

	/** 項目の候補値データオブジェクト名 */
	private static final String DEPOSIT_RECV_ITEM_LIST_NAME = "depositRecvItemList";

	/** 預り金明細のサービスクラス */
	@Autowired
	private DepositRecvDetailService service;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/** 未入力項目をnullにする */
	@InitBinder(EMPTY_INPUT_FORM_NAME)
	public void emptyToNullInitBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new EmptyToNullEditor());
	}

	/** フォームクラスの初期化 */
	@ModelAttribute(SEARCH_FORM_NAME)
	DepositRecvDetailSearchForm setUpForm() {
		return new DepositRecvDetailSearchForm();
	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 預り金明細画面初期表示
	 * 
	 * @param ankenId
	 * @param personId
	 * @param searchForm
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@ModelAttribute(SEARCH_FORM_NAME) DepositRecvDetailSearchForm searchForm) {

		ModelAndView mv = null;

		// 検索条件、ソート条件を初期化する
		searchForm.initForm();
		searchForm.setAnkenId(ankenId);
		searchForm.setPersonId(personId);

		// 案件ID、名簿IDに紐づく預り金明細情報を取得する
		DepositRecvDetailViewForm viewForm = service.createViewForm();
		// 案件＋会計情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchDepositRecvDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, MY_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金明細一覧ソート処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param searchForm
	 * @param httpMethod
	 * @return
	 */
	@RequestMapping(value = "/sort", method = RequestMethod.GET)
	public ModelAndView sort(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@ModelAttribute(SEARCH_FORM_NAME) DepositRecvDetailSearchForm searchForm, HttpMethod httpMethod) {

		ModelAndView mv = null;

		// セッションが切れている場合は処理終了
		if (!existSession(httpMethod, searchForm)) {
			return null;
		}

		// 預り金明細表示用フォームを作成する
		DepositRecvDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchDepositRecvDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, DEPOSIT_RECV_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金入力エリア表示処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	@RequestMapping(value = "/getDepositRecvDetailInputRow", method = RequestMethod.GET)
	public ModelAndView getDepositRecvDetailInputRow(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId) {

		ModelAndView mv = null;

		// 預り金明細入力用フォームを作成する
		DepositRecvDetailInputForm inputForm = service.createInputForm();
		inputForm.setDepositType(DepositType.SHUKKIN.getCd());

		mv = getMyModelAndView(inputForm, DEPOSIT_RECV_DETAIL_FRAGMENT_INPUT_PATH, INPUT_FORM_NAME);
		if (mv == null) {
			// 画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金登録処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param depositRecvDetailInputForm
	 * @param result
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/registDepositRecv", method = RequestMethod.POST)
	public ModelAndView registDepositRecv(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) DepositRecvDetailInputForm depositRecvDetailInputForm,
			BindingResult result, @ModelAttribute(SEARCH_FORM_NAME) DepositRecvDetailSearchForm searchForm) {

		ModelAndView mv = null;

		if (result.hasErrors()) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(depositRecvDetailInputForm, DEPOSIT_RECV_DETAIL_FRAGMENT_INPUT_PATH, INPUT_FORM_NAME, result);
		}

		// Enum存在確認
		if (checkExistEnum(depositRecvDetailInputForm, result)) {
			// バリデーションエラーが存在する場合
			// 処理失敗とする
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
			// 自分自身の画面にerror情報を設定した画面を返却
			return getMyModelAndViewWithErrors(depositRecvDetailInputForm, DEPOSIT_RECV_DETAIL_FRAGMENT_INPUT_PATH, INPUT_FORM_NAME, result);
		}

		try {
			// 登録処理
			service.registDepositRecv(depositRecvDetailInputForm, searchForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		DepositRecvDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchDepositRecvDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, DEPOSIT_RECV_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00020, "預り金／実費"));
		return mv;
	}

	/**
	 * 預り金編集処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param depositRecvDetailInputForm
	 * @param result
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/editDepositRecv", method = RequestMethod.POST)
	public ModelAndView editDepositRecv(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId,
			@Validated @ModelAttribute(EMPTY_INPUT_FORM_NAME) DepositRecvDetailInputForm depositRecvDetailInputForm,
			BindingResult result, @ModelAttribute(SEARCH_FORM_NAME) DepositRecvDetailSearchForm searchForm) {

		ModelAndView mv = null;

		try {
			// 相関バリデーション
			service.validateDepositRecvDetailInputForm(depositRecvDetailInputForm, result);
			
			if (result.hasErrors()) {
				// バリデーションエラーが存在する場合
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				mv = getMyModelAndViewWithErrors(depositRecvDetailInputForm, DEPOSIT_RECV_DETAIL_FRAGMENT_EDIT_ROW_PATH, EDIT_FORM_NAME, result);
				return mv;
			}
			
			// Enum存在確認
			if (checkExistEnum(depositRecvDetailInputForm, result)) {
				// バリデーションエラーが存在する場合
				// 処理失敗とする
				super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00001));
				// 自分自身の画面にerror情報を設定した画面を返却
				mv = getMyModelAndViewWithErrors(depositRecvDetailInputForm, DEPOSIT_RECV_DETAIL_FRAGMENT_EDIT_ROW_PATH, EDIT_FORM_NAME, result);
				return mv;
			}
			
			// 編集処理
			service.editDepositRecv(depositRecvDetailInputForm, searchForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00012));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		DepositRecvDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchDepositRecvDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, DEPOSIT_RECV_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00033, "預り金／実費"));
		return mv;
	}

	/**
	 * 預り金変更前のチェック処理<br>
	 * 請求書、精算書から作成された預り金か確認します。<br>
	 * 
	 * @param invoiceSeq
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "/checkOfBeforeEditDepositRecv", method = RequestMethod.GET)
	public Map<String, Object> checkOfBeforeEditDepositRecv(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("depositRecvSeq") Long depositRecvSeq,
			@ModelAttribute(SEARCH_FORM_NAME) DepositRecvDetailSearchForm searchForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 請求書／精算書から作成された預り金かチェック
			Map<String, Object> depositCreatorCheckResult = service.checkIfDepositCreatedFromAccgDoc(depositRecvSeq);

			boolean isAccgDocCreated = (boolean) depositCreatorCheckResult.get("isAccgDocCreated");
			if (isAccgDocCreated) {
				// 請求書／精算書から作成された預り金の場合 -> 編集不可
				AccgDocType docType = (AccgDocType) depositCreatorCheckResult.get("accgDocType");
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00187, docType.getVal(), "編集"));
				return response;
			}

			// 紐づく請求書／精算書が未発行の場合 -> 編集可能
			response.put("successed", true);
			return response;

		} catch (AppException e) {
			response.put("successed", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			response.put("successed", false);
			response.put("message", getMessage(MessageEnum.MSG_E00025));
			return response;
		}
	}

	/**
	 * 預り金編集用フォーム表示
	 * 
	 * @param personId
	 * @param ankenId
	 * @param depositRecvSeq
	 * @param isChecked
	 * @return
	 */
	@RequestMapping(value = "/openEditDepositRecvFragment", method = RequestMethod.GET)
	public ModelAndView openEditDepositRecvFragment(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("depositRecvSeq") Long depositRecvSeq,
			@RequestParam("isChecked") boolean isChecked) {

		ModelAndView mv = null;

		DepositRecvDetailInputForm inputForm = service.createInputForm();

		// 画面の行チェックボックスチェック状態をセット
		inputForm.setChecked(isChecked);
		try {
			// 預り金データを入力用フォームにセット
			service.setDepositRecv(depositRecvSeq, inputForm);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);
			return null;
		}

		mv = getMyModelAndView(inputForm, DEPOSIT_RECV_DETAIL_FRAGMENT_EDIT_ROW_PATH, EDIT_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金編集用フォーム閉じる処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param depositRecvSeq
	 * @param isChecked
	 * @return
	 */
	@RequestMapping(value = "/closeEditDepositRecvFragment", method = RequestMethod.GET)
	public ModelAndView closeEditDepositRecvFragment(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("depositRecvSeq") Long depositRecvSeq,
			@RequestParam("isChecked") boolean isChecked) {

		ModelAndView mv = null;

		DepositRecvDetailListDto dto = new DepositRecvDetailListDto();
		try {
			dto = service.getDepositRecv(depositRecvSeq);
			dto.setChecked(isChecked);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00091));
			logger.error("error", ex);

			return null;
		}

		mv = getMyModelAndView(dto, DEPOSIT_RECV_DETAIL_FRAGMENT_VIEW_ROW_PATH, VIEW_ROW_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess();
		return mv;
	}

	/**
	 * 預り金削除前のチェック処理<br>
	 * 紐づく請求書、精算書が発行ステータスが下書きか確認します。<br>
	 * 
	 * @param invoiceSeq
	 * @param accgDocSeq
	 * @param ankenId
	 * @param personId
	 * @throws AppException
	 */
	@ResponseBody
	@RequestMapping(value = "/checkOfBeforeDeleteDepositRecv", method = RequestMethod.GET)
	public Map<String, Object> checkOfBeforeDeleteDepositRecv(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("depositRecvSeq") Long depositRecvSeq,
			@ModelAttribute(SEARCH_FORM_NAME) DepositRecvDetailSearchForm searchForm) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 紐づく請求書、精算書の発行ステータスチェック
			Map<String, Object> checkResult = service.checkIssueStatusIssued(depositRecvSeq);

			boolean isIssued = (boolean) checkResult.get("isIssued");
			if (isIssued) {
				// 発行済みの場合 -> 削除不可
				AccgDocType docType = (AccgDocType) checkResult.get("accgDocType");
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_W00016, docType.getVal(), "削除"));
				return response;
			}

			// 請求書／精算書から作成された預り金かチェック
			Map<String, Object> depositCreatorCheckResult = service.checkIfDepositCreatedFromAccgDoc(depositRecvSeq);

			boolean isAccgDocCreated = (boolean) depositCreatorCheckResult.get("isAccgDocCreated");
			if (isAccgDocCreated) {
				// 請求書／精算書から作成された預り金の場合 -> 編集不可
				AccgDocType docType = (AccgDocType) depositCreatorCheckResult.get("accgDocType");
				response.put("successed", false);
				response.put("message", getMessage(MessageEnum.MSG_E00187, docType.getVal(), "削除"));
				return response;
			}

			// 未発行の場合 -> 削除可能
			response.put("successed", true);
			return response;

		} catch (AppException e) {
			response.put("successed", false);
			response.put("message", getMessage(e.getErrorType(), e.getMessageArgs()));
			return response;
		} catch (DataNotFoundException ex) {
			// 楽観ロックエラー
			response.put("successed", false);
			response.put("message", getMessage(MessageEnum.MSG_E00025));
			return response;
		}
	}

	/**
	 * 預り金削除処理
	 * 
	 * @param personId
	 * @param ankenId
	 * @param depositRecvSeq
	 * @param searchForm
	 * @return
	 */
	@RequestMapping(value = "/deleteDepositRecv", method = RequestMethod.POST)
	public ModelAndView deleteDepositRecv(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("depositRecvSeq") Long depositRecvSeq,
			@ModelAttribute(SEARCH_FORM_NAME) DepositRecvDetailSearchForm searchForm) {

		ModelAndView mv = null;

		try {
			// 削除処理
			service.deleteDepositAndDataRelatedToDeposit(depositRecvSeq, personId, ankenId);
		} catch (AppException e) {
			super.setAjaxProcResultFailure(getMessage(e.getErrorType(), e.getMessageArgs()));
			return null;
		} catch (Exception ex) {
			// 処理失敗
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00014));
			logger.error("error", ex);

			return null;
		}

		// 保存成功後は、表示画面を返却する
		DepositRecvDetailViewForm viewForm = service.createViewForm();
		// ヘッダー情報
		service.getAccgData(viewForm, searchForm);
		// 一覧情報
		service.searchDepositRecvDetail(viewForm, searchForm);

		mv = getMyModelAndView(viewForm, DEPOSIT_RECV_DETAIL_FRAGMENT_VIEW_PATH, VIEW_FORM_NAME);
		if (mv == null) {
			// 保存は成功したが、画面の取得に失敗した場合
			super.setAjaxProcResultFailure(getMessage(MessageEnum.MSG_E00092));
			return mv;
		}

		super.setAjaxProcResultSuccess(getMessage(MessageEnum.MSG_I00028, "預り金／実費"));
		return mv;
	}

	/**
	 * 項目の候補を取得
	 * 
	 * @param personId
	 * @param ankenId
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	@RequestMapping(value = "/searchDepositRecvDataList", method = RequestMethod.GET)
	public ModelAndView searchDepositRecvDataList(@PathVariable(name = "personId") Long personId,
			@PathVariable(name = "ankenId") Long ankenId, @RequestParam("searchWord") String searchWord,
			@RequestParam("depositType") String depositType) {

		ModelAndView mv = null;

		List<SelectOptionForm> depositRecvItemList = service.searchDepositRecvDataList(searchWord, depositType);
		mv = getMyModelAndView(depositRecvItemList, DEPOSIT_RECV_ITEM_LIST_FRAGMENT_PATH, DEPOSIT_RECV_ITEM_LIST_NAME);

		super.setAjaxProcResultSuccess();
		return mv;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 預り金登録項目「項目種別」の選択値がEnumに定義されている値か確認します。<br>
	 * Enumに定義されていない値だった場合はエラーとして true を返します。
	 * 
	 * @param depositRecvDetailInputForm
	 * @param result
	 * @return
	 */
	private boolean checkExistEnum(DepositRecvDetailInputForm depositRecvDetailInputForm, BindingResult result) {

		boolean errFlg = false;

		// 項目種別
		if (!DepositType.isExist(depositRecvDetailInputForm.getDepositType())) {
			errFlg = true;
			result.rejectValue("depositType", null, getMessage(MessageEnum.VARIDATE_MSG_E00001));
		}

		return errFlg;
	}

	/**
	 * セッション情報が有るか確認する。<br>
	 * 
	 * <pre>
	 * 複数タブで操作している場合、いずれかのタブで再ログインをし、Session（ブラウザが保持するCookie）が変わった場合、
	 * 開いていたタスク画面で再操作をしようとすると、Sessionが変わっているため、SearchFormの中の値がNULLになっている状態が発生する。
	 * そのまま後続処理を行うとNullPointerExceptionなどエラーが発生するため、Sessionが変わった（SearchFormの中の値がNULL）などの場合は
	 * 後続処理を行わないようにする。
	 * 
	 * POSTリクエストの場合、上記のSessionが変わった状態でリクエストすると、
	 * フレームワーク側のCSRFトークンチェックでNGとなり、Controllerの処理までこないため、ここではPOSTの処理は考慮しない。
	 * </pre>
	 * 
	 * @param httpMethod
	 * @param searchForm
	 * @return
	 */
	private boolean existSession(HttpMethod httpMethod, DepositRecvDetailSearchForm searchForm) {

		// POSTの場合はセッション有無を確認しない
		if (httpMethod == null || httpMethod == HttpMethod.POST) {
			return true;
		}

		// セッションにフォームが有るか確認する
		if (searchForm == null || searchForm.getDepositRecvDetailSortItem() == null) {
			super.setAjaxProcResultFailure(MessageEnum.MSG_E00161.getMessageKey());
			return false;
		} else {
			return true;
		}
	}

}
