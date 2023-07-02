package jp.loioz.common.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jp.loioz.app.common.controller.AppErrorController;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.exception.PermissionException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.HttpUtils;
import jp.loioz.common.utility.ModelAndViewUtils;

/**
 * コントローラーで発生した例外のハンドラークラス
 */
@ControllerAdvice
@Component
public class AppExceptionHandler {

	@Autowired
	private AppErrorController appErrorController;

	@Autowired
	private HttpServletRequest request;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;
	
	/**
	 * 権限エラーのハンドリングを行う
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(PermissionException.class)
	public ModelAndView permissionExceptionHandler(PermissionException e) {

		// 権限エラー画面にリダイレクトする
		return ModelAndViewUtils.getRedirectModelAndView(AppErrorController.class,
				controller -> controller.permissionError());
	}

	/**
	 * URLのパラメータが不正な場合のハンドリングを行う
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(TypeMismatchException.class)
	public ModelAndView typeMismatchExceptionHandler(TypeMismatchException e) {
		// ajax通信の場合
		if (HttpUtils.isAjax(request)) {
			
			// システムエラー画面を表示する場合は、エラーログを出力
			logger.error("error", e);
			
			// システムエラー画面にリダイレクトする
			return ModelAndViewUtils.getRedirectModelAndView(AppErrorController.class,
					controller -> controller.systemError());
		}
		// URLが間違っているので、404エラー画面を表示する
		return appErrorController.error404();
	}

	/**
	 * 画面表示に必要なデータが見つからない場合のハンドリングを行う
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(DataNotFoundException.class)
	public ModelAndView dataNotFoundExceptionHandler(DataNotFoundException e) {
		// ajax通信の場合
		if (HttpUtils.isAjax(request)) {
			
			// DataNotFoundExceptionではwarnログを出力する
			logger.warn("warn", e);
			
			// システムエラー画面にリダイレクトする（ajaxではリダイレクトされないので、エラーハンドリングはajax側のfailの関数による）
			return ModelAndViewUtils.getRedirectModelAndView(AppErrorController.class,
					controller -> controller.systemError());
		}

		// データが存在しないので、404エラー画面を表示する
		return appErrorController.error404();
	}

	/**
	 * 想定外のエラーのハンドリングを行う
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ModelAndView unexpectedExceptionHandler(Exception e) {
		
		boolean isCauseClientAbortException = this.findCauseExceptionRecursive(ClientAbortException.class, e, 0);
		if (isCauseClientAbortException) {
			// ClientAbortExceptionが例外原因の場合 -> ユーザーの操作の仕方(リクエスト後、完了を待たずに他画面に遷移するなど)によって発生するため、
			//                                         ERRORログではなく、WARNログの出力のみとする。
			// ※ClientAbortExceptionが発生するタイミングは、Contorllerのメソッドが完了した後になり、
			//   通常、エラーログの出力を行う｛jp.loioz.common.aspect.LogAspect.java｝ではClientAbortExceptionをキャッチできないため、特別ここでハンドリングを行う。
			logger.warn("warn", e);
			return null;
		}
		
		// システムエラー画面を表示する場合は、エラーログを出力
		logger.error("error", e);
		
		// システムエラー画面にリダイレクトする
		return ModelAndViewUtils.getRedirectModelAndView(AppErrorController.class,
				controller -> controller.systemError());
	}
	
	/**
	 * 指定の例外クラスが、対象の例外情報に含まれているか、再帰的に調べる（より深いcauseについても調べる）
	 * 
	 * @param clazz 例外クラス （このクラスの例外が原因かを調べる）
	 * @param cause 例外情報インスタンス （再帰的に調べる対象）
	 * @param loopCount 再帰処理のループカウント。外部からこのメソッドを呼ぶときは「0」を渡すこと。（発生はしないと思うが、無限ループを避けるため、再帰処理が10回まで来たら処理を終了するために利用。）
	 * @return true: 例外クラスが例外情報に存在する場合（例外クラスが原因となっている状態の場合）、 false: 例外クラスが例外情報に存在しない場合
	 */
	private boolean findCauseExceptionRecursive(Class<?> clazz, Throwable cause, int loopCount) {
		
		loopCount = loopCount + 1;
		
		if (loopCount > 10) {
			// 再帰処理が10回より多くなったら、「例外クラスは見つからなかった（例外クラスが原因ではなかった）」として、処理を終了する。
			return false;
		}
		
		if (cause == null) {
			// 例外（cause）がnullの場合 -> false
			return false;
		} else if (clazz.isInstance(cause)) {
			// 例外（cause）の型が引数のclazzの場合 -> true
			return true;
		} else {
			// 上記以外の場合 -> 例外（cause）の原因となった例外（cause.getCause()）を引数に再帰呼び出し
			return findCauseExceptionRecursive(clazz, cause.getCause(), loopCount);
		}
	}

}
