package jp.loioz.app.user.personManagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.DefaultController;
import jp.loioz.app.user.personManagement.form.personEdit.PersonDeleteForm;
import jp.loioz.app.user.personManagement.service.PersonDeleteService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.exception.AppException;
import jp.loioz.entity.TPersonEntity;

/**
 * 名簿管理画面-名簿削除モーダルのコントローラークラス
 */
@Controller
@RequestMapping("user/personManagement")
public class PersonDeleteController extends DefaultController {

	/** コントローラに対応するviewパス */
	private static final String MY_VIEW_PATH = "user/personManagement/personDeleteModal";

	/** コントローラに対応するviewパス */
	private static final String AJAX_MODAL_PATH = MY_VIEW_PATH + "::personDeleteModal";

	/** 出力フォームオブジェクト名 */
	private static final String VIEW_FORM_NAME = "personDeleteForm";

	/** サービスクラス */
	@Autowired
	private PersonDeleteService service;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * モーダル表示
	 *
	 * @param personId
	 * @return
	 */
	@RequestMapping(value = "/openPersonDeleteModal", method = RequestMethod.POST)
	public ModelAndView index(@RequestParam(name = "personId") Long personId) {

		PersonDeleteForm personDeleteForm = new PersonDeleteForm();
		TPersonEntity personEntity = service.getPersonInfo(personId);
		// 名簿情報が取得できない場合は空のFormでモーダルを表示 モーダル側で排他メッセージを表示する
		if (personEntity == null) {
			return getMyModelAndView(personDeleteForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
		}
		personDeleteForm.setPersonId(personEntity.getPersonId());
		personDeleteForm.setCustomerType(CustomerType.of(personEntity.getCustomerType()));
		personDeleteForm.setCustomerNameSei(personEntity.getCustomerNameSei());
		personDeleteForm.setCustomerNameMei(personEntity.getCustomerNameMei());
		personDeleteForm.setCustomerFlg(personEntity.getCustomerFlg());
		return getMyModelAndView(personDeleteForm, AJAX_MODAL_PATH, VIEW_FORM_NAME);
	}

	/**
	 * 名簿情報削除<br>
	 *
	 * <pre>
	 * 画面右上、「削除」を押下した場合
	 * </pre>
	 *
	 * @param personId
	 * @return 遷移先の画面
	 */
	@ResponseBody
	@RequestMapping(value = "/deletePerson", method = RequestMethod.POST)
	public Map<String, Object> deletePerson(@RequestParam(name = "personId") Long personId) {

		Map<String, Object> response = new HashMap<>();

		try {
			// 名簿情報を削除します。
			List<String> deleteS3ObjectKey = service.deletePerson(personId);

			// 別トランザクションでS3オブジェクトの削除
			service.personDeleteAfterS3FileDelete(deleteS3ObjectKey);

		} catch (AppException ex) {
			response.put("succeeded", false);
			response.put("message", getMessage(ex.getErrorType(), ex.getMessageArgs()));
			return response;
		}

		response.put("succeeded", true);
		response.put("message", getMessage("I00001"));
		return response;
	}

	/**
	 * 名簿情報の削除処理前チェック<br>
	 * 関与者として案件、裁判、当事者・関与者画面で使用されているかチェックします。<br>
	 * 
	 * @param personId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteKanyoshaBeforeCheck", method = RequestMethod.POST)
	public Map<String, Object> deleteKanyoshaBeforeCheck(@RequestParam Long personId) {

		Map<String, Object> response = new HashMap<>();

		// 各種存在チェック
		boolean existsSaibanRelateInfo = service.existsRelateSaiban(personId);
		boolean existsAnkenRelateInfo = service.existsRelateAnken(personId);
		boolean existsKanyoshaRelateInfo = service.existsRelateKanyosha(personId);

		if (existsAnkenRelateInfo || existsSaibanRelateInfo || existsKanyoshaRelateInfo) {
			StringBuilder sb = new StringBuilder("");
			sb.append("以下の画面で利用されていますが削除してもよろしいですか？");
			// どの画面で使用されているかを表示する
			if (existsAnkenRelateInfo) {
				sb.append(CommonConstant.CRLF_CODE + "・案件情報画面");
			}
			if (existsSaibanRelateInfo) {
				sb.append(CommonConstant.CRLF_CODE + "・裁判管理画面");
			}
			if (existsKanyoshaRelateInfo) {
				sb.append(CommonConstant.CRLF_CODE + "・当事者・関与者画面");
			}
			// 作成した確認用メッセージを表示
			response.put("message", sb.toString());
			response.put("needConfirm", true);
		} else {
			response.put("needConfirm", false);
		}

		return response;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

}
