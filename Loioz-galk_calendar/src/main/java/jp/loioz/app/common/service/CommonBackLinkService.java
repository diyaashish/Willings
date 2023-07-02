package jp.loioz.app.common.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant.ReturnDestinationScreen;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.entity.TAnkenEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 戻るリンク（パンくずに表示のリンク）の共通サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonBackLinkService extends DefaultService {

	/** 案件情報用のDaoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/**
	 * 画面のURIパターンEnum
	 */
	@Getter
	@AllArgsConstructor
	public enum ViewUriPattern {

		// 一覧
		MEIBOLIST("名簿情報一覧", Pattern.compile("^/user/meiboList/.*"), ReturnDestinationScreen.MEIBO_LIST),
		ANKENLIST("案件情報一覧", Pattern.compile("^/user/ankenList/.*"), ReturnDestinationScreen.ANKEN_LIST),

		// 名簿軸
		MEIBO("名簿情報", Pattern.compile("^/user/personManagement/.*"), ReturnDestinationScreen.MEIBO_INFO),
		PERSON_CASE_ACCOUNTING("案件", Pattern.compile("^/user/person/(?<personId>\\d+)/case/.*"), ReturnDestinationScreen.PERSON_CASE_ACCOUNTING),
		ADVISOR_CONTRACT("顧問契約", Pattern.compile("^/user/advisorContractPerson/.*"), ReturnDestinationScreen.ADVISOR_CONTRACT),
		GYOMU_HISTORY_CUSTOMER("業務履歴", Pattern.compile("^/user/gyomuHistory/customer/.*"), ReturnDestinationScreen.GYOMU_HISTORY_CUSTOMER),

		// 案件軸
		ANKEN_COMMON("案件情報", Pattern.compile("^/user/ankenManagement/edit/(?<ankenId>\\d+)/.*"), ReturnDestinationScreen.ANKEN_COMMON),
		ANKEN_MINJI("案件情報", Pattern.compile("^/user/ankenMinjiManagement/(?<ankenId>\\d+)/.*"), ReturnDestinationScreen.ANKEN_MINJI),
		ANKEN_KEIJI("案件情報", Pattern.compile("^/user/ankenKeijiManagement/(?<ankenId>\\d+)/.*"), ReturnDestinationScreen.ANKEN_KEIJI),
		ANKEN_DASHBOARD("案件ダッシュボード", Pattern.compile("^/user/ankenDashboard/.*"), ReturnDestinationScreen.ANKEN_DASHBOARD),
		SAIBAN_COMMON("裁判管理", Pattern.compile("^/user/saibanManagement/.*"), ReturnDestinationScreen.SAIBAN_COMMON),
		SAIBAN_MINJI("裁判管理", Pattern.compile("^/user/saibanMinjiManagement/.*"), ReturnDestinationScreen.SAIBAN_MINJI),
		SAIBAN_KEIJI("裁判管理", Pattern.compile("^/user/saibanKeijiManagement/.*"), ReturnDestinationScreen.SAIBAN_KEIJI),
		KANYOSHA_LIST("当事者・関与者", Pattern.compile("^/user/kanyosha/.*"), ReturnDestinationScreen.KANYOSHA_LIST),
		GYOMU_HISTORY_ANKEN("業務履歴", Pattern.compile("^/user/gyomuHistory/anken/.*"), ReturnDestinationScreen.GYOMU_HISTORY_ANKEN),
		ANKEN_CASE_ACCOUNTING("会計", Pattern.compile("^/user/case/(?<ankenId>\\d+)/accounting/.*"), ReturnDestinationScreen.ANKEN_CASE_ACCOUNTING),

		// URLパラメータの値によって、案件軸か名簿軸かを判断する必要がある画面
		AZUKARIITEM("預り品", Pattern.compile("^/user/azukariItem/.*"), ReturnDestinationScreen.AZUKARIITEM),
		FILE_MANAGEMENT("ファイル", Pattern.compile("^/user/fileManagement/.*"), ReturnDestinationScreen.FILE_MANAGEMENT),
		FILE_TRASHBOX("ファイルゴミ箱", Pattern.compile("^/user/fileTrashBox/.*"), ReturnDestinationScreen.FILE_TRASHBOX),

		// 案件と顧客の両方が必須パラメータの画面（案件軸画面、顧客軸画面、独立した画面の3パターンの表示が可能 ※顧客軸は仕様上存在しない）
		FEE_DETAIL("報酬管理", Pattern.compile("^/user/feeDetail/(?<personId>\\d+)/(?<ankenId>\\d+)/.*"), ReturnDestinationScreen.FEE_DETAIL),
		DEPOSIT_RECV_DETAIL("預り金／実費管理", Pattern.compile("^/user/depositRecvDetail/(?<personId>\\d+)/(?<ankenId>\\d+)/.*"), ReturnDestinationScreen.DEPOSIT_RECV_DETAIL),
		CASE_INVOICE_STATEMENT("請求書／精算書", Pattern.compile("^/user/caseInvoiceStatementList/(?<personId>\\d+)/(?<ankenId>\\d+)/.*"), ReturnDestinationScreen.CASE_INVOICE_STATEMENT),
		INVOICE_DETAIL("請求書", Pattern.compile("^/user/invoiceDetail/(?<invoiceSeq>\\d+)/.*"), ReturnDestinationScreen.INVOICE_DETAIL),
		STATEMENT_DETAIL("精算書", Pattern.compile("^/user/statementDetail/(?<statementSeq>\\d+)/.*"), ReturnDestinationScreen.STATEMENT_DETAIL),
		RECORD_DETAIL("取引実績", Pattern.compile("^/user/recordDetail/(?<accgRecordSeq>\\d+)/.*"), ReturnDestinationScreen.RECORD_DETAIL),

		;

		/** 画面名 */
		private String viewName;

		/** パターン（正規表現） */
		private Pattern uriPattern;

		/** URLのパターンに対応する戻り先画面情報 */
		private ReturnDestinationScreen returnDestinationScreen;

		/**
		 * 引数のパターンを保持するEnumを取得する
		 * 
		 * @param patter
		 * @return 一致するEnumが存在しない場合はNULLを返却
		 */
		public static ViewUriPattern getViewUriPatternEnum(Pattern patter) {

			Optional<ViewUriPattern> viewUriPatternOpt = Stream.of(ViewUriPattern.values())
					.filter(e -> e.getUriPattern().pattern().equals(patter.pattern()))
					.findFirst();

			return viewUriPatternOpt.orElse(null);
		}

	}

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 一覧へ戻るリンクの表示制御を行う値のリセット（初期化）を行う
	 * 
	 * <pre>
	 * 一覧へ戻るリンクのリセットを行うのは、
	 * 「全ての画面から、名簿・案件一覧、名簿軸、案件軸、以外の画面へ遷移したとき」とする。
	 *  (refereの判定は不要で、リクエストが名簿・案件一覧、名簿軸、案件軸、以外のURIの場合)
	 * </pre>
	 * 
	 * @param referer
	 * @param requestUriStr
	 */
	public void resetBackListLinkSetting(String referer, String requestUriStr) {

		// リファラーのURI文字列から、パス情報を取得
		String refererPath = this.getPathFromUri(referer);
		if (StringUtils.isEmpty(refererPath)) {
			// パス情報が空（=リファラーが空）の場合はSession値をリセットする（リンク直アクセスのケースなど）
			this.resetBackListLinkSessionValue();
			return;
		}

		// リクエストのURI文字列から、パス情報を取得
		// （requestUriStrが空になるケースはないので、空チェックは行わない）
		String requestPath = this.getPathFromUri(requestUriStr);

		// 判定の結果、この値がtrueの場合（リクエスト先が「名簿・案件一覧、名簿軸、案件軸、以外」の場合）に初期化処理を行う。それ以外のときはなにもしない。
		boolean requestIsNotListOrMeiboAxisOrAnkenAxis = true;

		//
		// リクエスト先が名簿・案件一覧かのチェック
		//

		// リクエスト先のパスが「名簿・案件一覧」かのチェック
		Matcher requestMeiboOrAnkenListPathMatcher = this.getMeiboOrAnkenListPathMatcher(requestPath);
		if (requestMeiboOrAnkenListPathMatcher != null) {
			// リクエスト先のパスの値が、名簿・案件一覧のものの場合

			requestIsNotListOrMeiboAxisOrAnkenAxis = false;
		}

		//
		// リクエスト先が名簿軸かのチェック
		//

		// リクエスト先のパスが「名簿軸系」かのチェック
		Matcher requestMeiboAxisPathMatcher = this.getMeiboAxisPathMatcher(requestPath);
		if (requestMeiboAxisPathMatcher != null) {
			// リクエスト先のパスが、名簿軸のものの場合

			requestIsNotListOrMeiboAxisOrAnkenAxis = false;
		}

		//
		// リクエスト先が案件軸かのチェック
		//

		// リクエスト先のパスが「案件情報（民事 or 刑事）」かのチェック
		Matcher requestAnkenInfoPathMatcher = this.getAnkenInfoPathMatcher(requestPath);
		if (requestAnkenInfoPathMatcher != null) {
			// リクエスト先の値が、案件情報（民事 or 刑事）のものの場合

			requestIsNotListOrMeiboAxisOrAnkenAxis = false;
		}

		// リクエスト先のパスが「案件軸系」かのチェック
		Matcher requestAnkenAxisPathMatcher = this.getAnkenAxisPathMatcher(requestPath);
		if (requestAnkenAxisPathMatcher != null) {
			// リクエスト先のパスが、案件軸のものの場合

			requestIsNotListOrMeiboAxisOrAnkenAxis = false;
		}

		//
		// リクエスト先のパスが「名簿軸系か案件軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		//

		Matcher requestMeiboOrAnkenAxisPathWithParamsMatcher = this.getMeiboOrAnkenAxisPathWithParamsMatcher(requestPath, requestUriStr);
		if (requestMeiboOrAnkenAxisPathWithParamsMatcher != null) {
			// リクエスト先のURIが、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合
			// （この場合は、URIは名簿軸か、案件軸のものになる）

			requestIsNotListOrMeiboAxisOrAnkenAxis = false;
		}

		//
		// リクエスト先のパスが「会計画面：名簿軸系か案件軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		//

		Matcher requestAccgMngAxisPathWithParamsMatcher = this.getAccgMngAxisPathWithParamsMatcher(requestPath, requestUriStr);
		if (requestAccgMngAxisPathWithParamsMatcher != null) {
			// リクエスト先のURIが、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合
			// （この場合は、URIは案件軸のものになる）

			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(requestAccgMngAxisPathWithParamsMatcher.pattern());

			// リクエスト先のURIが案件軸のものかを判定する -> 会計画面は名簿軸画面を表示しないので案件軸かどうかのチェックのみを行う
			boolean requestIsAnkenAxis = this.checkUriIsAccgAnkenAxisForUrlMustJudgeParameters(requestUriStr, viewUriPattern);

			// 案件軸の場合
			if (requestIsAnkenAxis) {
				requestIsNotListOrMeiboAxisOrAnkenAxis = false;
			}

		}

		if (requestIsNotListOrMeiboAxisOrAnkenAxis) {
			// リクエスト先のURIが「名簿・案件一覧、名簿軸、案件軸、以外」の場合

			// 一覧へ戻るリンクのSession値をリセットする
			this.resetBackListLinkSessionValue();
		}
	}

	/**
	 * 一覧へ戻るリンクの表示制御を行う値の設定を行う
	 * 
	 * <pre>
	 * 一覧へ戻るリンクの設定を行うのは、
	 * 「名簿、案件一覧画面から、名簿軸、案件軸の画面へ遷移したとき」とする。
	 *  (refereが名簿、案件一覧のURIで、リクエストが名簿軸、案件軸URIの場合)
	 * </pre>
	 * 
	 * @param referer
	 * @param requestUriStr
	 */
	public void setBackListLinkSetting(String referer, String requestUriStr) {

		// リファラーのURI文字列から、パス情報を取得
		String refererPath = this.getPathFromUri(referer);
		if (StringUtils.isEmpty(refererPath)) {
			// パス情報が空（=リファラーが空）の場合はSession値の設定処理は行わない（リンク直アクセスのケースなど）
			return;
		}

		// リクエストのURI文字列から、パス情報を取得
		// （requestUriStrが空になるケースはないので、空チェックは行わない）
		String requestPath = this.getPathFromUri(requestUriStr);

		// 判定の結果、この２つの値がtrueの場合（リファラーURIが「名簿 or 案件一覧」で、リクエスト先が「名簿軸 or
		// 案件軸」の場合）に値の設定処理を行う。それ以外のときはなにもしない。
		boolean refererIsMeiboOrAnkenList = false;
		boolean requestIsMeiboOrAnkenAxis = false;
		// refererIsMeiboOrAnkenListがtrueとなったときにマッチした、マッチ情報（戻り先の情報となる）
		Matcher refererIsMeiboOrAnkenListMatcher = null;

		//
		// リファラーが名簿・案件一覧かのチェック
		//

		// リファラーのパスが「名簿・案件一覧」かのチェック
		Matcher refererMeiboOrAnkenListPathMatcher = this.getMeiboOrAnkenListPathMatcher(refererPath);
		if (refererMeiboOrAnkenListPathMatcher != null) {
			// リファラーのパスの値が、名簿・案件一覧のものの場合

			refererIsMeiboOrAnkenList = true;
			refererIsMeiboOrAnkenListMatcher = refererMeiboOrAnkenListPathMatcher;
		}

		//
		// リクエスト先が名簿軸かのチェック
		//

		// リクエスト先のパスが「名簿軸系」かのチェック
		Matcher requestMeiboAxisPathMatcher = this.getMeiboAxisPathMatcher(requestPath);
		if (requestMeiboAxisPathMatcher != null) {
			// リクエスト先のパスが、名簿軸のものの場合

			requestIsMeiboOrAnkenAxis = true;
		}

		//
		// リクエスト先が案件軸かのチェック
		//

		// リクエスト先のパスが「案件情報（民事 or 刑事）」かのチェック
		Matcher requestAnkenInfoPathMatcher = this.getAnkenInfoPathMatcher(requestPath);
		if (requestAnkenInfoPathMatcher != null) {
			// リクエスト先の値が、案件情報（民事 or 刑事）のものの場合

			requestIsMeiboOrAnkenAxis = true;
		}

		// リクエスト先のパスが「案件軸系」かのチェック
		Matcher requestAnkenAxisPathMatcher = this.getAnkenAxisPathMatcher(requestPath);
		if (requestAnkenAxisPathMatcher != null) {
			// リクエスト先のパスが、案件軸のものの場合

			requestIsMeiboOrAnkenAxis = true;
		}

		//
		// リクエスト先のパスが「名簿軸系か案件軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		//

		Matcher requestMeiboOrAnkenAxisPathWithParamsMatcher = this.getMeiboOrAnkenAxisPathWithParamsMatcher(requestPath, requestUriStr);
		if (requestMeiboOrAnkenAxisPathWithParamsMatcher != null) {
			// リクエスト先のURIが、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合
			// （この場合は、URIは名簿軸か、案件軸のものになる）

			requestIsMeiboOrAnkenAxis = true;
		}

		//
		// リクエスト先のパスが「会計画面：名簿軸系か案件軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		//

		Matcher requestAccgMngAxisPathWithParamsMatcher = this.getAccgMngAxisPathWithParamsMatcher(requestPath, requestUriStr);
		if (requestAccgMngAxisPathWithParamsMatcher != null) {
			// リクエスト先のURIが、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合
			// （この場合は、URIは名簿軸か、案件軸のものになる）

			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(requestAccgMngAxisPathWithParamsMatcher.pattern());

			// リクエスト先のURIが案件軸のものかを判定する 会計画面は名簿軸画面を表示しないので案件軸かどうかのチェックのみを行う
			boolean requestIsAnkenAxis = this.checkUriIsAccgAnkenAxisForUrlMustJudgeParameters(requestUriStr, viewUriPattern);

			// 案件軸の場合
			if (requestIsAnkenAxis) {
				requestIsMeiboOrAnkenAxis = true;
			}

		}

		if (refererIsMeiboOrAnkenList && requestIsMeiboOrAnkenAxis) {
			// リファラーURIが「名簿 or 案件一覧」で、リクエスト先が「名簿軸 or 案件軸」の場合

			// 一覧へ戻るリンクのSession値を設定する
			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(refererIsMeiboOrAnkenListMatcher.pattern());
			this.setBackListLinkSessionValue(viewUriPattern.getReturnDestinationScreen());
		}
	}

	/**
	 * 戻るリンクの表示制御を行う値のリセット（初期化）を行う
	 * 
	 * <pre>
	 * 戻るリンクのリセットを行うのは、
	 * 「名簿軸の画面から、名簿軸以外の画面へ遷移したとき」とする。
	 *  (refereが名簿軸のURIで、リクエストが名簿軸以外URIの場合)
	 * </pre>
	 * 
	 * @param referer
	 * @param requestUriStr
	 */
	public void resetBackPrevLinkSetting(String referer, String requestUriStr) {

		// リファラーのURI文字列から、パス情報を取得
		String refererPath = this.getPathFromUri(referer);
		if (StringUtils.isEmpty(refererPath)) {
			// パス情報が空（=リファラーが空）の場合はSession値をリセットする（リンク直アクセスのケースなど）
			this.resetBackPrevLinkSessionValue();
			return;
		}

		// リクエストのURI文字列から、パス情報を取得
		// （requestUriStrが空になるケースはないので、空チェックは行わない）
		String requestPath = this.getPathFromUri(requestUriStr);

		// 判定の結果、この２つの値がtrueの場合（リファラーURIが「名簿軸」で、リクエスト先が「名簿軸以外」の場合）に初期化処理を行う。それ以外のときはなにもしない。
		boolean refererIsMeiboAxis = false;
		boolean requestIsNotMeiboAxis = true;

		//
		// リファラーが名簿軸かのチェック
		//

		// リファラーのパスが「名簿軸系」かのチェック
		Matcher refererMeiboAxisPathMatcher = this.getMeiboAxisPathMatcher(refererPath);
		if (refererMeiboAxisPathMatcher != null) {
			// リファラーの値が、名簿軸のものの場合

			refererIsMeiboAxis = true;
		}

		// リファラーのパスが「名簿軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		Matcher refererMeiboOrAnkenAxisPathWithParamsMatcher = this.getMeiboOrAnkenAxisPathWithParamsMatcher(refererPath, referer);
		if (refererMeiboOrAnkenAxisPathWithParamsMatcher != null) {
			// リファラーの値が、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合

			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(refererMeiboOrAnkenAxisPathWithParamsMatcher.pattern());

			// リファラーURIが案件軸のものかを判定する（案件軸ではない場合は名簿軸）
			boolean refererIsAnkenAxis = this.checkUriIsAnkenAxisForUrlMustJudgeParameters(referer, viewUriPattern);

			if (!refererIsAnkenAxis) {
				// リファラーの値が、名簿軸のものの場合

				refererIsMeiboAxis = true;
			}
		}

		//
		// リクエスト先が「名簿軸以外」かのチェック
		//

		// リクエスト先のパスが「名簿軸以外」ではない（=名簿軸）かのチェック
		Matcher requestMeiboAxisPathMatcher = this.getMeiboAxisPathMatcher(requestPath);
		if (requestMeiboAxisPathMatcher != null) {
			// リクエスト先のパスの値が、名簿軸のものの場合

			requestIsNotMeiboAxis = false;
		}

		// リクエスト先のパスが「名簿軸以外」ではない（=名簿軸）かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		Matcher requestMeiboOrAnkenAxisPathWithParamsMatcher = this.getMeiboOrAnkenAxisPathWithParamsMatcher(requestPath, requestUriStr);
		if (requestMeiboOrAnkenAxisPathWithParamsMatcher != null) {
			// リクエスト先のパスの値が、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合

			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(requestMeiboOrAnkenAxisPathWithParamsMatcher.pattern());

			// リクエスト先のURIが案件軸のものかを判定する（案件軸ではない場合は顧客軸）
			boolean requestIsAnkenAxis = this.checkUriIsAnkenAxisForUrlMustJudgeParameters(requestUriStr, viewUriPattern);

			if (!requestIsAnkenAxis) {
				// リクエスト先のURIの値が、名簿軸のものの場合

				requestIsNotMeiboAxis = false;
			}
		}

		// リクエスト先のパスが「会計管理：名簿軸以外」ではない（=名簿軸）かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		Matcher requestAccgMngAxisPathWithParamsMatcher = this.getAccgMngAxisPathWithParamsMatcher(requestPath, requestUriStr);
		if (requestAccgMngAxisPathWithParamsMatcher != null) {
			// 会計管理に名簿軸は存在しないため
			requestIsNotMeiboAxis = false;
		}

		if (refererIsMeiboAxis && requestIsNotMeiboAxis) {
			// リファラーURIが「名簿軸」で、リクエスト先が「名簿軸以外」の場合

			// 戻るリンクのSession値をリセットする
			this.resetBackPrevLinkSessionValue();
		}
	}

	/**
	 * 戻るリンクの表示制御を行う値の設定を行う
	 * 
	 * <pre>
	 * 戻るリンクの設定を行うのは、
	 * 「案件軸の画面から、名簿軸の画面へ遷移したとき」とする。
	 *  (refereが案件軸のURIで、リクエストが名簿軸URIの場合)
	 * </pre>
	 * 
	 * @param referer
	 * @param requestUriStr
	 */
	public void setBackPrevLinkSetting(String referer, String requestUriStr) {

		// リファラーのURI文字列から、パス情報を取得
		String refererPath = this.getPathFromUri(referer);
		if (StringUtils.isEmpty(refererPath)) {
			// パス情報が空（=リファラーが空）の場合はSession値の設定処理は行わない（リンク直アクセスのケースなど）
			return;
		}

		// リクエストのURI文字列から、パス情報を取得
		// （requestUriStrが空になるケースはないので、空チェックは行わない）
		String requestPath = this.getPathFromUri(requestUriStr);

		// 判定の結果、この２つの値がtrueの場合（リファラーURIが「案件軸」で、リクエスト先が「名簿軸」の場合）にSession値の設定処理を行う。それ以外のときはなにもしない。
		boolean refererIsAnkenAxis = false;
		boolean requestIsMeiboAxis = false;
		// refererIsAnkenAxisがtrueとなったときにマッチした、マッチ情報（戻り先の情報となる）
		Matcher refererIsAnkenAxiMatcher = null;

		//
		// リファラーが案件軸かのチェック
		//

		// リファラーのパスが「案件情報（民事 or 刑事）」かのチェック
		Matcher refererAnkenInfoPathMatcher = this.getAnkenInfoPathMatcher(refererPath);
		if (refererAnkenInfoPathMatcher != null) {
			// リファラーの値が、案件情報（民事 or 刑事）のものの場合

			String ankenId = refererAnkenInfoPathMatcher.group("ankenId");
			boolean isExistAnken = this.isExistAnken(ankenId);
			if (isExistAnken) {
				// ※リファラーのパスが「案件情報（民事 or 刑事）」の場合は、対象の案件が存在する場合のみ、設定をする
				// （案件情報画面での、案件削除処理後の名簿画面遷移の場合に、戻るボタンを表示しないようにするため）
				refererIsAnkenAxis = true;
				refererIsAnkenAxiMatcher = refererAnkenInfoPathMatcher;
			}
		}

		// リファラーのパスが「案件軸系」かのチェック
		Matcher refererAnkenAxisPathMatcher = this.getAnkenAxisPathMatcher(refererPath);
		if (refererAnkenAxisPathMatcher != null) {
			// リファラーのパスが、案件軸のものの場合

			refererIsAnkenAxis = true;
			refererIsAnkenAxiMatcher = refererAnkenAxisPathMatcher;
		}

		// リファラーのパスが「案件軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		Matcher refererMeiboOrAnkenAxisPathWithParamsMatcher = this.getMeiboOrAnkenAxisPathWithParamsMatcher(refererPath, referer);
		if (refererMeiboOrAnkenAxisPathWithParamsMatcher != null) {
			// リファラーのURIが、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合

			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(refererMeiboOrAnkenAxisPathWithParamsMatcher.pattern());

			// リファラーのURIが案件軸のものかを判定する（案件軸ではない場合は名簿軸）
			boolean isAnkenAxis = this.checkUriIsAnkenAxisForUrlMustJudgeParameters(referer, viewUriPattern);

			if (isAnkenAxis) {
				// リファラーのURIの値が、案件軸のものの場合

				refererIsAnkenAxis = true;
				refererIsAnkenAxiMatcher = refererMeiboOrAnkenAxisPathWithParamsMatcher;
			}
		}

		// リファラーのパスが「会計関連画面：案件軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		Matcher refererAccgMngAxisPathWithParamsMatcher = this.getAccgMngAxisPathWithParamsMatcher(refererPath, referer);
		if (refererAccgMngAxisPathWithParamsMatcher != null) {
			// リファラーのURIが、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合

			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(refererAccgMngAxisPathWithParamsMatcher.pattern());

			// リファラーのURIが案件軸のものかを判定する（案件軸ではない場合は名簿軸）
			boolean isAnkenAxis = this.checkUriIsAccgAnkenAxisForUrlMustJudgeParameters(referer, viewUriPattern);

			if (isAnkenAxis) {
				// リファラーのURIの値が、案件軸のものの場合

				refererIsAnkenAxis = true;
				refererIsAnkenAxiMatcher = refererAccgMngAxisPathWithParamsMatcher;
			}
		}

		//
		// リクエスト先が名簿軸かのチェック
		//

		// リクエスト先のパスが「名簿軸系」かのチェック
		Matcher requestMeiboAxisPathMatcher = this.getMeiboAxisPathMatcher(requestPath);
		if (requestMeiboAxisPathMatcher != null) {
			// リクエスト先のパスが、名簿軸のものの場合

			requestIsMeiboAxis = true;
		}

		// リクエスト先のパスが「名簿軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		Matcher requestMeiboOrAnkenAxisPathWithParamsMatcher = this.getMeiboOrAnkenAxisPathWithParamsMatcher(requestPath, requestUriStr);
		if (requestMeiboOrAnkenAxisPathWithParamsMatcher != null) {
			// リクエスト先のURIが、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合

			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(requestMeiboOrAnkenAxisPathWithParamsMatcher.pattern());

			// リクエスト先のURIが案件軸のものかを判定する（案件軸ではない場合は名簿軸）
			boolean isAnkenAxis = this.checkUriIsAnkenAxisForUrlMustJudgeParameters(requestUriStr, viewUriPattern);

			if (!isAnkenAxis) {
				// リクエスト先のURIの値が、名簿軸のものの場合

				requestIsMeiboAxis = true;
			}
		}

		if (refererIsAnkenAxis && requestIsMeiboAxis) {
			// リファラーURIが「案件軸」で、リクエスト先が「名簿軸」の場合

			// 戻るリンクのSession値を設定する
			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(refererIsAnkenAxiMatcher.pattern());
			this.setBackPrevLinkSessionValue(viewUriPattern.getReturnDestinationScreen(), referer);
		}
	}

	/**
	 * 会計管理：案件軸への遷移かをセッションに管理する
	 * 
	 * @param requestUriStr
	 */
	public void setAccgAnkenRefererSetting(String requestUriStr) {

		// リクエストのURI文字列から、パス情報を取得
		// （requestUriStrが空になるケースはないので、空チェックは行わない）
		String requestPath = this.getPathFromUri(requestUriStr);
		boolean requestIsAccgAnkenAxis = false;

		// リクエスト先のパスが「会計管理：名簿軸系」かのチェック（URIのパスではなく、パラメータの値で、案件軸か名簿軸かが分かれるURI用）
		Matcher requestAccgMngAxisPathWithParamsMatcher = this.getAccgMngAxisPathWithParamsMatcher(requestPath, requestUriStr);
		if (requestAccgMngAxisPathWithParamsMatcher != null) {
			// リクエスト先のURIが、パラメータの値で、案件軸か名簿軸かが分かれる画面のものの場合

			ViewUriPattern viewUriPattern = ViewUriPattern.getViewUriPatternEnum(requestAccgMngAxisPathWithParamsMatcher.pattern());

			// リクエスト先のURIが案件軸のものかを判定する
			boolean isAnkenAxis = this.checkUriIsAccgAnkenAxisForUrlMustJudgeParameters(requestUriStr, viewUriPattern);

			if (isAnkenAxis) {
				// リクエスト先のURIの値が、名簿軸のものの場合

				requestIsAccgAnkenAxis = true;
			}
		}

		// 会計管理：案件軸へのリクエストかどうか
		this.setAccgAnkenRefererBoolSessionValue(requestIsAccgAnkenAxis);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * URI文字列から、パス部分の文字列を取得する<br>
	 * （ドメイン、ポート部分や、パラメータ部分は含まないもの）
	 * 
	 * @param uriStr
	 * @return URI文字列かNULLや空の場合はNULLを返却する
	 * @throws IllegalStateException uriStrがURIの形式として不正な場合にスロー
	 */
	private String getPathFromUri(String uriStr) {

		URI uri = null;
		String path = null;
		if (StringUtils.isNotEmpty(uriStr)) {
			try {
				uri = new URI(uriStr);
			} catch (URISyntaxException e) {
				throw new IllegalStateException("uriStrがURIの形式として適切ではありません。uriStr: " + uriStr);
			}
			path = uri.getPath();
		} else {
			// uriStrが空の場合
			return null;
		}

		return path;
	}

	/**
	 * パスが名簿、案件一覧のものの場合、Matcher（マッチ情報）を取得する
	 * 
	 * @param path URIのパス部分の文字列（ドメイン、ポート部分や、パラメータ部分は含まない）
	 * @return パスが名簿、案件一覧のものではない場合はNULLを返却
	 */
	private Matcher getMeiboOrAnkenListPathMatcher(String path) {

		// 名簿一覧
		Matcher meibolistMatcher = ViewUriPattern.MEIBOLIST.getUriPattern().matcher(path);
		// 案件一覧
		Matcher ankenlistMatcher = ViewUriPattern.ANKENLIST.getUriPattern().matcher(path);
		// 上記をリストにまとめる
		List<Matcher> listMatcherList = Arrays.asList(meibolistMatcher, ankenlistMatcher);

		Optional<Matcher> listPathMatcheOpt = listMatcherList.stream()
				.filter(matcher -> matcher.matches())
				.findFirst();
		if (listPathMatcheOpt.isPresent()) {
			// pathの値が、上記のURIパターンにマッチする場合（名簿、案件一覧のものの場合）
			return listPathMatcheOpt.get();
		}

		return null;
	}

	/**
	 * パスが案件情報画面（民事、刑事）のものの場合、Matcher（マッチ情報）を取得する
	 * 
	 * @param path
	 * @return
	 */
	private Matcher getAnkenInfoPathMatcher(String path) {

		// 案件画面（共通）
		Matcher ankenCommonMatcher = ViewUriPattern.ANKEN_COMMON.getUriPattern().matcher(path);
		// 案件画面（民事）
		Matcher ankenMinjiMatcher = ViewUriPattern.ANKEN_MINJI.getUriPattern().matcher(path);
		// 案件画面（刑事）
		Matcher ankenKeijiMatcher = ViewUriPattern.ANKEN_KEIJI.getUriPattern().matcher(path);
		// 上記をリストにまとめる
		List<Matcher> ankenInfoMatcherList = Arrays.asList(ankenCommonMatcher, ankenMinjiMatcher, ankenKeijiMatcher);

		Optional<Matcher> ankenInfoPathMatcheOpt = ankenInfoMatcherList.stream()
				.filter(matcher -> matcher.matches())
				.findFirst();
		if (ankenInfoPathMatcheOpt.isPresent()) {
			// pathの値が、上記のURIパターンにマッチする場合（案件情報（民事、刑事）のものの場合）

			return ankenInfoPathMatcheOpt.get();
		}

		return null;
	}

	/**
	 * パスが案件軸のものの場合、Matcher（マッチ情報）を取得する
	 * 
	 * <pre>
	 * ※下記の画面の判定は含まないので注意
	 * ・案件情報画面（民事、刑事）
	 * ・パスではなく、URLのパラメータによって名簿軸か案件軸かを判定する必要がある画面（会計画面やファイル管理画面など）
	 * </pre>
	 * 
	 * @param path URIのパス部分の文字列（ドメイン、ポート部分や、パラメータ部分は含まない）
	 * @return パスが案件軸のものではない場合はNULLを返却
	 */
	private Matcher getAnkenAxisPathMatcher(String path) {

		// ※案件情報画面（民事、刑事）の判定は、このメソッドには含まない

		// 案件ダッシュボード
		Matcher ankenDashboardMatcher = ViewUriPattern.ANKEN_DASHBOARD.getUriPattern().matcher(path);
		// 裁判画面（共通）
		Matcher saibanCommonMatcher = ViewUriPattern.SAIBAN_COMMON.getUriPattern().matcher(path);
		// 裁判画面（民事）
		Matcher saibanMinjiMatcher = ViewUriPattern.SAIBAN_MINJI.getUriPattern().matcher(path);
		// 裁判画面（刑事）
		Matcher saibanKeijiMatcher = ViewUriPattern.SAIBAN_KEIJI.getUriPattern().matcher(path);
		// 当事者・関与者一覧
		Matcher kanyoshaListMatcher = ViewUriPattern.KANYOSHA_LIST.getUriPattern().matcher(path);
		// 業務履歴（案件）
		Matcher gyomuHistoryAnkenMatcher = ViewUriPattern.GYOMU_HISTORY_ANKEN.getUriPattern().matcher(path);
		// 会計
		Matcher ankenCaseAccountingMatcher = ViewUriPattern.ANKEN_CASE_ACCOUNTING.getUriPattern().matcher(path);

		// 上記をリストにまとめる
		List<Matcher> ankenAxisMatcherList = Arrays.asList(ankenDashboardMatcher, saibanCommonMatcher, saibanMinjiMatcher, saibanKeijiMatcher,
				kanyoshaListMatcher, gyomuHistoryAnkenMatcher, ankenCaseAccountingMatcher);

		Optional<Matcher> ankenAxisPathMatcheOpt = ankenAxisMatcherList.stream()
				.filter(matcher -> matcher.matches())
				.findFirst();
		if (ankenAxisPathMatcheOpt.isPresent()) {
			// pathの値が、上記のURIパターンにマッチする場合（案件軸系（案件情報画面は除く）の画面のものの場合）

			return ankenAxisPathMatcheOpt.get();
		}

		return null;
	}

	/**
	 * パスが名簿軸のものの場合、Matcher（マッチ情報）を取得する
	 * 
	 * <pre>
	 * ※下記の画面の判定は含まないので注意
	 * ・パスではなく、URLのパラメータによって名簿軸か案件軸かを判定する必要がある画面（会計画面やファイル管理画面など）
	 * </pre>
	 * 
	 * @param path URIのパス部分の文字列（ドメイン、ポート部分や、パラメータ部分は含まない）
	 * @return パスが名簿軸のものではない場合はNULLを返却
	 */
	private Matcher getMeiboAxisPathMatcher(String path) {

		// 名簿情報
		Matcher meiboMatcher = ViewUriPattern.MEIBO.getUriPattern().matcher(path);
		// 案件情報
		Matcher personCaseAccountingMatcher = ViewUriPattern.PERSON_CASE_ACCOUNTING.getUriPattern().matcher(path);
		// 顧問契約
		Matcher advisorContractMatcher = ViewUriPattern.ADVISOR_CONTRACT.getUriPattern().matcher(path);
		// 業務履歴（名簿）
		Matcher gyomuHistoryCustomerMatcher = ViewUriPattern.GYOMU_HISTORY_CUSTOMER.getUriPattern().matcher(path);
		// 上記をリストにまとめる
		List<Matcher> meiboAxisMatcherList = Arrays.asList(meiboMatcher, personCaseAccountingMatcher, advisorContractMatcher, gyomuHistoryCustomerMatcher);

		Optional<Matcher> meiboAxisPathMatcheOpt = meiboAxisMatcherList.stream()
				.filter(matcher -> matcher.matches())
				.findFirst();
		if (meiboAxisPathMatcheOpt.isPresent()) {
			// pathの値が、上記のURIパターンにマッチする場合（名簿軸系の画面のものの場合）

			return meiboAxisPathMatcheOpt.get();
		}

		return null;
	}

	/**
	 * Uriのパラメータ情報をもとに、Uriが名簿軸のものか、案件軸のものかを判定し、Matcher（マッチ情報）を取得する
	 * 
	 * <pre>
	 * このメソッドは、
	 * URIのパス情報だけでは、名簿軸か、案件軸かの判定が行えないURIについて、
	 * パラメータ情報をもとに、そのURIが名簿軸か、案件軸かの判定を行う。
	 * （会計画面やファイル管理画面など）
	 * </pre>
	 * 
	 * @param path URIのパス部分の文字列（ドメイン、ポート部分や、パラメータ部分は含まない）
	 * @param uri URI全体の文字列（httpから、パラメータ部分も含む、URI全体の文字列）
	 * @return URIが名簿軸、案件軸のどちらでもない場合はNULLを返却
	 */
	private Matcher getMeiboOrAnkenAxisPathWithParamsMatcher(String path, String uri) {

		// 預り品
		Matcher azukariitemMatcher = ViewUriPattern.AZUKARIITEM.getUriPattern().matcher(path);
		// ファイル管理
		Matcher fileManagementMatcher = ViewUriPattern.FILE_MANAGEMENT.getUriPattern().matcher(path);
		// ファイルゴミ箱
		Matcher fileTrashboxMatcher = ViewUriPattern.FILE_TRASHBOX.getUriPattern().matcher(path);
		// 上記をリストにまとめる
		List<Matcher> paramUrlMatcherList = Arrays.asList(azukariitemMatcher, fileManagementMatcher, fileTrashboxMatcher);

		Optional<Matcher> paramUrlMatcheOpt = paramUrlMatcherList.stream()
				.filter(matcher -> matcher.matches())
				.findFirst();
		if (paramUrlMatcheOpt.isPresent()) {
			// リファラーの値が、上記のURIパターンにマッチする場合

			return paramUrlMatcheOpt.get();
		}

		return null;
	}

	/**
	 * Uriのパラメータ情報をもとに、Uriが会計関連画面の名簿軸のものか、案件軸のもの、独立した画面かを判定し、Matcher（マッチ情報）を取得する
	 * 
	 * <pre>
	 * このメソッドは、
	 * URIのパス情報だけでは、名簿軸か、案件軸かの判定が行えないURIについて、
	 * パラメータ情報をもとに、そのURIが名簿軸か、案件軸かの判定を行う。
	 * ただし、顧客軸でも案件軸でもない場合も考慮する
	 * </pre>
	 * 
	 * @param path
	 * @param uri
	 * @return
	 */
	private Matcher getAccgMngAxisPathWithParamsMatcher(String path, String uri) {

		// 報酬明細
		Matcher feeDetailMatcher = ViewUriPattern.FEE_DETAIL.getUriPattern().matcher(path);
		// 預り金明細
		Matcher depositRecvDetailMatcher = ViewUriPattern.DEPOSIT_RECV_DETAIL.getUriPattern().matcher(path);
		// 請求書／精算書
		Matcher caseInvoiceStatementMatcher = ViewUriPattern.CASE_INVOICE_STATEMENT.getUriPattern().matcher(path);
		// 請求書
		Matcher invoiceDetailMatcher = ViewUriPattern.INVOICE_DETAIL.getUriPattern().matcher(path);
		// 精算書
		Matcher statementDetailMatcher = ViewUriPattern.STATEMENT_DETAIL.getUriPattern().matcher(path);
		// 取引実績
		Matcher recordDetailMatcher = ViewUriPattern.RECORD_DETAIL.getUriPattern().matcher(path);

		// 上記をリストにまとめる
		List<Matcher> paramUrlMatcherList = Arrays.asList(feeDetailMatcher, depositRecvDetailMatcher, caseInvoiceStatementMatcher,
				invoiceDetailMatcher, statementDetailMatcher, recordDetailMatcher);

		Optional<Matcher> paramUrlMatcheOpt = paramUrlMatcherList.stream()
				.filter(matcher -> matcher.matches())
				.findFirst();
		if (paramUrlMatcheOpt.isPresent()) {
			// リファラーの値が、上記のURIパターンにマッチする場合

			return paramUrlMatcheOpt.get();
		}

		return null;
	}

	/**
	 * URIが案件軸のものかどうかを判定する。
	 * 
	 * <pre>
	 * URIのパスではなく、パラメータ値で案件軸か名簿軸かを判断しなければいけないURI用のメソッド。
	 * 
	 * URIが案件軸ではない場合は、名簿軸になる。（どちらでもない場合、もしくは判断が不可能な場合はExceptionをスローする）
	 * </pre>
	 * 
	 * @param uri URI文字列
	 * @param viewUriPattern URIがマッチしたパターンEnum
	 * @return true:URIが案件軸のものの場合、false:URIが名簿軸のものの場合
	 */
	private boolean checkUriIsAnkenAxisForUrlMustJudgeParameters(String uri, ViewUriPattern viewUriPattern) {

		// リファラーからURLパラメータを取得
		UriComponents uriComponents = ServletUriComponentsBuilder.fromUriString(uri).build();
		MultiValueMap<String, String> params = uriComponents.getQueryParams();

		// URIが案件軸かどうか（falseの場合は名簿軸となる）
		boolean uriIsAnkenAxis = false;

		switch (viewUriPattern) {
		case AZUKARIITEM:
			// 預り品

			// 預り品画面は、現状、案件軸しかないため、URIにパラメータが付与されているが、パラメータによる判断は行わない。
			// ※初期表示処理では名簿軸、案件軸判定のパラメータを送信しているが、検索処理ではパラメータを送信してないので、パラメータで判断するように修正する場合は注意すること。

			// 案件軸
			uriIsAnkenAxis = true;

			break;
		case FILE_MANAGEMENT:
			// ファイル管理の場合

			List<String> transitionAnkenIdListFile = params.get("transitionAnkenId");
			List<String> transitionCustomerIdListFile = params.get("transitionCustomerId");

			// ファイル管理の場合、パラメータ名がなく、NULLになる場合がある
			String transitionAnkenIdFileStr = transitionAnkenIdListFile != null ? transitionAnkenIdListFile.get(0) : null;
			String transitionCustomerIdFileStr = transitionCustomerIdListFile != null ? transitionCustomerIdListFile.get(0) : null;

			boolean isAnkenAxisFile = StringUtils.isNotEmpty(transitionAnkenIdFileStr);
			boolean isCustomerAxisFile = StringUtils.isNotEmpty(transitionCustomerIdFileStr);

			if (isAnkenAxisFile && isCustomerAxisFile) {
				throw new IllegalStateException("URIが案件軸のものか、顧客軸のものか判断できない");
			}
			if (!isAnkenAxisFile && !isCustomerAxisFile) {
				throw new IllegalStateException("URIが案件軸のものか、顧客軸のものか判断できない");
			}

			if (isAnkenAxisFile) {
				// 案件軸
				uriIsAnkenAxis = true;
			} else {
				// 名簿軸
				uriIsAnkenAxis = false;
			}
			break;
		case FILE_TRASHBOX:
			// ファイルゴミ箱

			List<String> transitionAnkenIdListFileTrash = params.get("transitionAnkenId");
			List<String> transitionCustomerIdListFileTrash = params.get("transitionCustomerId");

			// ファイル管理の場合、パラメータ名がなく、NULLになる場合がある
			String transitionAnkenIdFileTrashStr = transitionAnkenIdListFileTrash != null ? transitionAnkenIdListFileTrash.get(0) : null;
			String transitionCustomerIdFileTrashStr = transitionCustomerIdListFileTrash != null ? transitionCustomerIdListFileTrash.get(0) : null;

			boolean isAnkenAxisFileTrash = StringUtils.isNotEmpty(transitionAnkenIdFileTrashStr);
			boolean isCustomerAxisFileTrash = StringUtils.isNotEmpty(transitionCustomerIdFileTrashStr);

			if (isAnkenAxisFileTrash && isCustomerAxisFileTrash) {
				throw new IllegalStateException("URIが案件軸のものか、顧客軸のものか判断できない");
			}
			if (!isAnkenAxisFileTrash && !isCustomerAxisFileTrash) {
				throw new IllegalStateException("URIが案件軸のものか、顧客軸のものか判断できない");
			}

			if (isAnkenAxisFileTrash) {
				// 案件軸
				uriIsAnkenAxis = true;
			} else {
				// 名簿軸
				uriIsAnkenAxis = false;
			}
			break;
		default:
			throw new IllegalArgumentException("処理で考慮されていない値が引数に渡されました。viewUriPattern:" + viewUriPattern);
		}

		return uriIsAnkenAxis;
	}

	/**
	 * URIが案件軸のものかどうかを判定する。
	 * 
	 * <pre>
	 * URIのパスではなく、パラメータ値で会計関連画面が案件軸か名簿軸かを判断しなければいけないURI用のメソッド。
	 * 
	 * URIが案件軸ではない場合は、名簿軸になる。（どちらでもない場合、もしくは判断が不可能な場合はExceptionをスローする）
	 * </pre>
	 * 
	 * @param uri URI文字列
	 * @param viewUriPattern URIがマッチしたパターンEnum
	 * @return true:URIが案件軸のものの場合、false:URIが名簿軸のものの場合
	 */
	private boolean checkUriIsAccgAnkenAxisForUrlMustJudgeParameters(String uri, ViewUriPattern viewUriPattern) {

		// リファラーからURLパラメータを取得
		UriComponents uriComponents = ServletUriComponentsBuilder.fromUriString(uri).build();
		MultiValueMap<String, String> params = uriComponents.getQueryParams();

		// URIが案件軸かどうか
		boolean uriIsAnkenAxis = false;

		switch (viewUriPattern) {
		case FEE_DETAIL:
			List<String> isAnkenFeeParam = params.get(UrlConstant.ACCG_REFERER_ANKEN_SIDE_PARAM_NAME);
			uriIsAnkenAxis = isAnkenFeeParam != null ? isAnkenFeeParam.stream().findFirst().map(Boolean::parseBoolean).orElse(false) : false;

			break;
		case DEPOSIT_RECV_DETAIL:
			List<String> isAnkenDepositParam = params.get(UrlConstant.ACCG_REFERER_ANKEN_SIDE_PARAM_NAME);
			uriIsAnkenAxis = isAnkenDepositParam != null ? isAnkenDepositParam.stream().findFirst().map(Boolean::parseBoolean).orElse(false) : false;

			break;
		case CASE_INVOICE_STATEMENT:
			// 請求書/精算書一覧は案件軸のみ表示するため、パラメータでの判別は行わない
			uriIsAnkenAxis = true;

			break;
		case INVOICE_DETAIL:
			List<String> isAnkenInvoiceDetailParam = params.get(UrlConstant.ACCG_REFERER_ANKEN_SIDE_PARAM_NAME);
			uriIsAnkenAxis = isAnkenInvoiceDetailParam != null ? isAnkenInvoiceDetailParam.stream().findFirst().map(Boolean::parseBoolean).orElse(false) : false;

			break;
		case STATEMENT_DETAIL:
			List<String> isAnkenStatementDetailParam = params.get(UrlConstant.ACCG_REFERER_ANKEN_SIDE_PARAM_NAME);
			uriIsAnkenAxis = isAnkenStatementDetailParam != null ? isAnkenStatementDetailParam.stream().findFirst().map(Boolean::parseBoolean).orElse(false) : false;

			break;
		case RECORD_DETAIL:
			List<String> isAnkenRecordDetailParam = params.get(UrlConstant.ACCG_REFERER_ANKEN_SIDE_PARAM_NAME);
			uriIsAnkenAxis = isAnkenRecordDetailParam != null ? isAnkenRecordDetailParam.stream().findFirst().map(Boolean::parseBoolean).orElse(false) : false;

			break;
		default:
			throw new IllegalArgumentException("処理で考慮されていない値が引数に渡されました。viewUriPattern:" + viewUriPattern);
		}

		return uriIsAnkenAxis;
	}

	/**
	 * 「一覧へ戻る」リンクのSession情報をセットする
	 */
	private void setBackListLinkSessionValue(ReturnDestinationScreen screen) {

		// 画面情報
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.RETURN_LIST_SCREEN, screen.getCd());
	}

	/**
	 * 「一覧へ戻る」リンクのSession情報をリセットする
	 */
	private void resetBackListLinkSessionValue() {

		// 画面情報
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.RETURN_LIST_SCREEN, null);
	}

	/**
	 * 「前の画面へ戻る」リンクのSession情報をセットする
	 * 
	 * @param screen
	 * @param backUrl
	 */
	private void setBackPrevLinkSessionValue(ReturnDestinationScreen screen, String backUrl) {

		// 画面情報
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.RETURN_PREV_SCREEN_NAME, screen.getDispVal());
		// URL情報
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.RETURN_PREV_SCREEN_URL, backUrl);
	}

	/**
	 * 「前の画面へ戻る」リンクのSession情報をリセットする
	 */
	private void resetBackPrevLinkSessionValue() {

		// 画面情報
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.RETURN_PREV_SCREEN_NAME, null);
		// URL情報
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.RETURN_PREV_SCREEN_URL, null);
	}

	/**
	 * 会計画面：案件軸からの遷移「報酬管理、預り金管理」
	 * 
	 * @param isAnken
	 */
	private void setAccgAnkenRefererBoolSessionValue(boolean isAnken) {
		// 画面情報
		SessionUtils.setSessionVaule(SessionAttrKeyEnum.ACCG_ANKEN_SCREEN_BOOL, Boolean.toString(isAnken));
	}

	/**
	 * 案件が存在するかを判定
	 * 
	 * @param ankenId
	 * @return true: 存在する、false: 存在しない
	 */
	private boolean isExistAnken(String ankenId) {

		if (StringUtils.isEmpty(ankenId)) {
			return false;
		}

		TAnkenEntity anken = tAnkenDao.selectById(Long.valueOf(ankenId));
		if (anken == null) {
			return false;
		}
		return true;
	}
}
