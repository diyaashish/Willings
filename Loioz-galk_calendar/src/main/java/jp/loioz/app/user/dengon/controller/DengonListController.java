package jp.loioz.app.user.dengon.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.dengon.form.DengonListForm;
import jp.loioz.app.user.dengon.form.DengonMessageListForm;
import jp.loioz.app.user.dengon.service.DengonListService;
import jp.loioz.common.constant.CommonConstant.MailBoxType;
import jp.loioz.common.constant.CommonConstant.MidokuKidokuCd;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.message.MessageHolder;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.DengonFolderDto;

/**
 * メッセージ画面のコントローラークラス
 */
@PlanFuncRestrictCheck(funcs = {PlanFuncRestrict.PF0001})
@Controller
@RequestMapping(value = "user/dengon")
public class DengonListController extends DefaultController {

	/** コントローラと対応するviewのパス */
	private static final String MY_VIEW_PATH = "user/dengon/dengonList";

	/** メッセージ一覧エリアと対応するviewのフラグメントパス */
	private static final String MY_VIEW_DENGON_LIST_PATH = MY_VIEW_PATH + "::dengonList";

	/** メニューのカスタムフォルダーエリアと対応するviewのフラグメントパス */
	private static final String MY_VIEW_MENU_FOLDER_PATH = MY_VIEW_PATH + "::folderTree";

	/** メニューのごみ箱カスタムフォルダーエリアと対応するviewのフラグメントパス */
	private static final String MY_VIEW_MENU_TRASHED_FOLDER_PATH = MY_VIEW_PATH + "::trashedFolderTree";

	/** メッセージ詳細表示と対応するviewのフラグメントパス */
	private static final String MY_VIEW_DETAIL_PATH = MY_VIEW_PATH + "::dengonDetail";

	/** viewで使用するフォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "viewForm";

	/** サービスクラス */
	@Autowired
	private DengonListService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 初期表示
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {

		// メッセージ画面
		DengonListForm form = service.setInitData();

		return getMyModelAndView(form, MY_VIEW_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 選択したフォルダのメッセージ一覧を取得する
	 *
	 * @return 表示するメッセージ一覧
	 */
	@RequestMapping(value = "/selectMailBox", method = RequestMethod.POST)
	public ModelAndView selectMailBox(
			@RequestParam(name = "dengonFolderSeq") Long dengonFolderSeq,
			@RequestParam(name = "mailBoxType") String mailBoxType,
			@RequestParam(name = "page") Integer page) {

		List<DengonMessageListForm> dengonList = service.searchDengonList(mailBoxType, dengonFolderSeq, page);

		return getMyModelAndView(dengonList, MY_VIEW_DENGON_LIST_PATH, "dengonList");
	}

	/**
	 * 検索ワードを含むメッセージ一覧を取得する
	 *
	 * @param searchMailText
	 * @param mailBoxType
	 * @param dengonFolderSeq
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public ModelAndView search(
			@RequestParam(name = "searchMailText") String searchMailText,
			@RequestParam(name = "mailBoxType") String mailBoxType,
			@RequestParam(name = "dengonFolderSeq", required = false) Long dengonFolderSeq,
			@RequestParam(name = "page") Integer page) {

		List<DengonMessageListForm> dengonList = service.selectDengonSearchList(searchMailText, mailBoxType, dengonFolderSeq, page);

		return getMyModelAndView(dengonList, MY_VIEW_DENGON_LIST_PATH, "dengonList");
	}

	/**
	 * リストで選択されたメッセージの詳細情報を取得する
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public ModelAndView searchDetail(
			@RequestParam(name = "dengonSeq") Long dengonSeq,
			@RequestParam(name = "mailBoxType") String mailBoxType,
			@RequestParam(name = "sendFlg") String sendFlg,
			MessageHolder msgHolder) {

		DengonMessageListForm messageForm = new DengonMessageListForm();

		try {

			/* 詳細表示のためのデータ取得 */
			messageForm = service.searchDetail(dengonSeq, mailBoxType, sendFlg);

			return this.getMyModelAndView(messageForm, MY_VIEW_DETAIL_PATH, "dto");

		} catch (AppException ex) {
			/* エラー情報を返却する */
			String errorMsg = super.getMessage(ex.getMessageKey());
			msgHolder.setErrorMsg(errorMsg);

			return this.getMyModelAndView(messageForm, MY_VIEW_DETAIL_PATH, "dto", msgHolder);
		}
	}

	/**
	 * フィルターでの一覧表示（未読/要返信/重要）
	 */
	@RequestMapping(value = "/filter", method = RequestMethod.POST)
	public ModelAndView filter(
			@RequestParam(name = "filterType") String filterType,
			@RequestParam(name = "mailBoxType") String mailBoxType,
			@RequestParam(name = "searchMailText") String searchMailText,
			@RequestParam(name = "dengonFolderSeq", required = false) Long dengonFolderSeq,
			@RequestParam(name = "page") Integer page) {

		List<DengonMessageListForm> dengonList = service.selectDengonFilterList(filterType, searchMailText, dengonFolderSeq, page);

		return getMyModelAndView(dengonList, MY_VIEW_DENGON_LIST_PATH, "dengonList");
	}

	/**
	 * メニューのフォルダー一覧を取得/更新する
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/refreshFolderMenu", method = RequestMethod.POST)
	public String refreshFolderMenu(Model model) {

		/* 詳細表示のためのデータ取得 */
		List<DengonFolderDto> customeFolderList = service.selectCustomeFolderAsSelectOptions(null, null);
		int draftCount = service.selectDraftCount();
		boolean creatableFolder = service.creatableFolder();

		model.addAttribute("customeFolderList", customeFolderList);
		model.addAttribute("draftCount", draftCount);
		model.addAttribute("creatableFolder", creatableFolder);

		return MY_VIEW_MENU_FOLDER_PATH;
	}

	/**
	 * メニューのごみ箱フォルダー一覧を取得/更新する
	 *
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/refreshTrashedFolderMenu", method = RequestMethod.POST)
	public String refreshTrashedFolderMenu(Model model) {

		/* 詳細表示のためのデータ取得 */
		List<DengonFolderDto> customeFolderList = service.selectCustomeFolderAsSelectOptions(null, true);
		model.addAttribute("customeFolderList", customeFolderList);

		return MY_VIEW_MENU_TRASHED_FOLDER_PATH;
	}

	/**
	 * 既読にする/未読にする選択時の処理
	 *
	 * @param dengonSeqList
	 * @param openFlg
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/changeOpen", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> changeOpenFlg(
			@RequestParam(name = "dengonSeqList") String dengonSeqList,
			@RequestParam(name = "openFlg") String openFlg) {

		Map<String, Object> response = new HashMap<>();

		try {

			// 既読・未読処理
			service.changeOpenFlg(dengonSeqList, openFlg);
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00026, MidokuKidokuCd.of(openFlg).getVal() + "にしました"));

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));

		}

		return response;
	}

	/**
	 * リストで選択されたメッセージの重要フラグを変更する
	 *
	 * @param dengonSeq
	 * @return
	 */
	@RequestMapping(value = "/updateImportant", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateImportantFlg(@RequestParam(name = "dengonSeq") Long dengonSeq) {

		Map<String, Object> response = new HashMap<>();

		try {

			// 重要に更新
			String importantFlg = service.updateImportantFlg(dengonSeq);
			response.put("succeeded", true);
			response.put("importantFlg", importantFlg);
			response.put("message", getMessage(MessageEnum.MSG_I00026, "重要にしました"));

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));

		}

		return response;
	}

	/**
	 * メッセージステータスを「要返信」に変更する
	 *
	 * @param dengonSeq
	 * @return
	 */
	@RequestMapping(value = "/updateYohenshin", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateYohenshin(@RequestParam(name = "dengonSeq") Long dengonSeq) {

		Map<String, Object> response = new HashMap<>();

		try {

			// 要返信
			service.updateYohenshin(dengonSeq);

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00026, "要返信にしました"));

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));

		}

		return response;
	}

	/**
	 * メッセージをごみ箱に入れる/取り出す
	 *
	 * @param dengonSeq
	 * @param receiveDengonSeqList
	 * @param sendDengonSeqList
	 * @param mailBoxType
	 * @return
	 */
	@RequestMapping(value = "/trashedDengon", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> trashedDengon(
			@RequestParam(name = "dengonSeq", required = false) Long dengonSeq,
			@RequestParam(name = "receiveDengonSeqList", required = false) String receiveDengonSeqList,
			@RequestParam(name = "sendDengonSeqList", required = false) String sendDengonSeqList,
			@RequestParam(name = "mailBoxType") String mailBoxType) {

		Map<String, Object> response = new HashMap<>();

		try {

			// ごみ箱移行処理
			if (dengonSeq != null) {

				if (MailBoxType.SEND.equalsByCode(mailBoxType)) {
					// 送信の場合
					service.trashedSendDengon(dengonSeq);

				} else {
					// 送信以外
					service.trashedReceiveDengon(dengonSeq);

				}

			} else {
				// 複数の削除処理

				if (StringUtils.isNotEmpty(receiveDengonSeqList)) {
					// 受信メッセージの場合
					service.trashedReceiveDengonList(receiveDengonSeqList);
				}
				if (StringUtils.isNotEmpty(sendDengonSeqList)) {
					// 送信メッセージの場合
					service.trashedSendDengonList(sendDengonSeqList);
				}
			}

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00026, "ゴミ箱に入れました"));

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));

		}

		return response;
	}

	/**
	 * ごみ箱のメッセージを削除（論理）する
	 *
	 * @param dengonSeq
	 * @param receiveDengonSeqList
	 * @param sendDengonSeqList
	 * @param mailBoxType
	 * @return
	 */
	@RequestMapping(value = "/deletedDengon", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deletedDengon(
			@RequestParam(name = "dengonSeq", required = false) Long dengonSeq,
			@RequestParam(name = "receiveDengonSeqList", required = false) String receiveDengonSeqList,
			@RequestParam(name = "sendDengonSeqList", required = false) String sendDengonSeqList,
			@RequestParam(name = "mailBoxType") String mailBoxType) {

		Map<String, Object> response = new HashMap<>();

		try {

			// 削除処理
			if (dengonSeq != null) {
				// 単独削除の場合

				if (MailBoxType.SEND.equalsByCode(mailBoxType)) {
					// 送信メッセージの場合
					service.deletedSendDengon(dengonSeq);

				} else {
					// 受信メッセージの場合
					service.deletedReceiveDengon(dengonSeq);

				}

			} else {
				// 複数削除の場合

				if (StringUtils.isNotEmpty(receiveDengonSeqList)) {
					// 受信メッセージの場合
					service.deletedReceiveDengonList(receiveDengonSeqList);
				}
				if (StringUtils.isNotEmpty(sendDengonSeqList)) {
					// 送信メッセージの場合
					service.deletedSendDengonList(sendDengonSeqList);
				}
			}

			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00026, "削除しました"));

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));

		}

		return response;
	}

	/**
	 * 下書きを論理削除する
	 *
	 * @param dengonSeq
	 * @return 遷移先の画面
	 */
	@RequestMapping(value = "/deletedDraft", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deletedDraft(@RequestParam(name = "dengonSeq") Long dengonSeq,
			@RequestParam(name = "sendDengonSeqList", required = false) String strSendDengonSeqList) {

		Map<String, Object> response = new HashMap<>();

		try {

			// 削除処理
			if (dengonSeq != null) {
				// 単独削除の場
				service.deletedDraft(dengonSeq);
			} else {
				// 複数の削除処理
				if (StringUtils.isNotEmpty(strSendDengonSeqList)) {
					List<Long> dengonSeqList = StringUtils.toListLong(strSendDengonSeqList);
					service.deletedDraftList(dengonSeqList);
				}
			}
			response.put("succeeded", true);
			response.put("message", getMessage(MessageEnum.MSG_I00027, "削除しました"));

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(MessageEnum.MSG_E00013));

		}

		return response;
	}

	/**
	 * 未読メッセージを開いた場合に未読件数を取得する
	 *
	 * @return unreadCount
	 */
	@RequestMapping(value = "/unreadCount", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> selectUnreadCount() {

		Map<String, Object> response = new HashMap<>();
		response.put("header", service.selectUnreadCount().toString());
		response.put("receive", service.selectUnreadCountWhereRecieveBox());
		return response;
	}

	/**
	 * 下書き件数を取得する
	 *
	 * @return draftCount
	 */
	@RequestMapping(value = "/draftCount", method = RequestMethod.POST)
	@ResponseBody
	public Integer selectDraftCount() {
		return service.selectDraftCount();
	}
}