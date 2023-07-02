package jp.loioz.common.handlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.LoiozAdminControlConstant.LoiozAdminControl;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.handlerInterceptor.annotation.LoiozAdminControlCheck;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;

/**
 * ロイオズ管理者制御による実行制限を行うHandlerInterceptor
 */
public class LoiozAdminControlHandlerInterceptor implements HandlerInterceptor {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		Long tenantSeq = SessionUtils.getTenantSeq();
		Long loginAccountSeq = SessionUtils.getLoginAccountSeq();

		if (CommonUtils.anyNull(tenantSeq, loginAccountSeq)) {
			throw new IllegalStateException("ログインしていないとアクセス不可な処理が未ログイン状態で実行された");
		}
		
		// HandlerMethodではない場合（存在しないURLへのアクセスや静的ファイルへのアクセスの場合）は権限チェックは行わない
		// （HandlerMethodではなく、ResourceHttpRequestHandlerクラスになる。）
		if (!HandlerMethod.class.isInstance(handler)) {
			return true;
		}
		
		// 実行されたハンドラーのクラス情報
		HandlerMethod hm = (HandlerMethod) handler;
		Class<?> clazz = hm.getBeanType();
		Method method = hm.getMethod();
		
		// 対象のクラス、メソッドに付与されている指定のアノテーションを取得（付与されていない場合はNULL）
		LoiozAdminControlCheck classAnnotation = AnnotationUtils.findAnnotation(clazz, LoiozAdminControlCheck.class);
		LoiozAdminControlCheck methodAnnotation = AnnotationUtils.findAnnotation(method, LoiozAdminControlCheck.class);
		
		if (CommonUtils.allNull(classAnnotation, methodAnnotation)) {
			// クラス、メソッドのいずれにもアノテーションが付与されていない（機能制限対象ではない）場合
			// -> 機能制限チェックは行わずHandlerメソッドを実行
			return true;
		}
		
		// 以下、ハンドラーがロイオズ管理者制御による、制限対象の場合
		
		// 制限チェック
		boolean canUseHandlerMethod = this.validCanUseHandlerMethod(classAnnotation, methodAnnotation);
		if (!canUseHandlerMethod) {
			// Handlerメソッド実行不可の状態の場合
			
			// レスポンスにエラー情報を設定
			 this.setLoiozAdminControlErrorResponse(request, response);
			
			// Handlerメソッドは実行しない
			return false;
		}
		
		// Handlerメソッドを実行する
		return true;
	}
	
	/**
	 * 引数のアノテーション情報をもとに、Handlerメソッドを実行可能かどうかを検証する
	 * 
	 * @param classAnnotation
	 * @param methodAnnotation
	 * @return
	 */
	private boolean validCanUseHandlerMethod(LoiozAdminControlCheck classAnnotation, LoiozAdminControlCheck methodAnnotation) {
		
		if (CommonUtils.allNull(classAnnotation, methodAnnotation)) {
			// クラス、メソッドのいずれにもアノテーションが付与されていない（制限対象ではない）場合
			// -> Handlerメソッド実行可
			return true;
		}
		
		if (!CommonUtils.anyNull(classAnnotation, methodAnnotation)) {
			// クラス、メソッドの両方にアノテーションが付与されている
			// -> メソッドのアノテーション設定を適用する
			
			LoiozAdminControl[] items = methodAnnotation.items();
			boolean canUseHandlerMethod = this.validCanUseHandlerMethod(items);
			if (!canUseHandlerMethod) {
				// 実行不可の場合
				// Handlerメソッド実行不可
				return false;
			} else {
				// 実行可の場合
				// Handlerメソッド実行可
				return true;
			}
		}
		
		if (classAnnotation != null) {
			// クラスにアノテーションが付与されている場合
			// -> メソッドにはアノテーションが付与されていない
			
			// クラスのアノテーション設定を適用する
			LoiozAdminControl[] items = classAnnotation.items();
			boolean canUseHandlerMethod = this.validCanUseHandlerMethod(items);
			if (!canUseHandlerMethod) {
				// 実行不可の場合
				// Handlerメソッド実行不可
				return false;
			} else {
				// Handlerメソッド実行可
				return true;
			}
		} else {
			// クラスにアノテーションが付与されていない場合
			// -> メソッドにはアノテーションが付与されている
			
			// メソッドのアノテーション設定を適用する
			LoiozAdminControl[] items = methodAnnotation.items();
			boolean canUseHandlerMethod = this.validCanUseHandlerMethod(items);
			if (!canUseHandlerMethod) {
				// 実行不可の場合
				// Handlerメソッド実行不可
				return false;
			} else {
				// Handlerメソッド実行可
				return true;
			}
		}
	}
	
	/**
	 * 引数で指定された機能について、利用可能かを検証する（セッション情報にアクセス）
	 * 
	 * @param validTargetItems 検証項目
	 * @return 検証項目の中に、ひとつでも「利用不可」の項目があればfalseを返却。
	 */
	private boolean validCanUseHandlerMethod(LoiozAdminControl[] validTargetItems) {
		
		for (LoiozAdminControl item : validTargetItems) {
			
			// 対象の制御項目の設定値
			String controlValue = SessionUtils.getLoiozAdminControlValue(item);
			
			switch (item) {
				case OLD_KAIKEI_OPEN:
					// 旧会計機能の利用許可項目のケース
					boolean notCanUse = SystemFlg.FLG_OFF.equalsByCode(controlValue);
					if (notCanUse) {
						// 利用不可
						return false;
					}
					break;
				default:
					throw new RuntimeException("switch文の分岐パターンで考慮されていない値で実行されました。");
			}
		}
		
		// 全ての検証項目で、「利用不可」が存在しない - > 利用可能
		return true;
	}
	
	/**
	 * レスポンスに制限チェックエラーの情報を設定する
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void setLoiozAdminControlErrorResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			// ajaxリクエストの場合

			// エラーコードを設定
			response.setStatus(CommonConstant.CUSTOME_LOIOZ_ADMIN_CONTROL_ERROR_CODE);
		} else {
			// 通常のhttpリクエストの場合 -> 権限エラー画面へのリダイレクト設定
			response.sendRedirect(UrlConstant.PERMISSION_ERROR_URL);
		}
	}
	
}
