package jp.loioz.common.handlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.constant.plan.PlanConstant.PlanType;
import jp.loioz.common.handlerInterceptor.annotation.PlanFuncRestrictCheck;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.MTenantMgtDao;
import jp.loioz.entity.MTenantMgtEntity;

/**
 * プラン別機能制限（実行制限）を行うHandlerInterceptor
 */
public class PlanFuncRestrictHandlerInterceptor implements HandlerInterceptor {

	/** テナント管理用のDaoクラス */
	@Autowired
	MTenantMgtDao mTenantMgtDao;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		Long tenantSeq = SessionUtils.getTenantSeq();
		Long loginAccountSeq = SessionUtils.getLoginAccountSeq();
		PlanType sessionPlanType = SessionUtils.getPlanType();

		if (CommonUtils.anyNull(tenantSeq, loginAccountSeq)) {
			throw new IllegalStateException("ログインしていないとアクセス不可な処理が未ログイン状態で実行された");
		}
		
		// HandlerMethodではない場合（存在しなURLへのアクセスや静的ファイルへのアクセスの場合）は権限チェックは行わない
		// （HandlerMethodではなく、ResourceHttpRequestHandlerクラスになる。）
		if (!HandlerMethod.class.isInstance(handler)) {
			return true;
		}
		
		MTenantMgtEntity mTenantMgtEntity = mTenantMgtDao.selectBySeq(tenantSeq);
		PlanType tenantPlanType = PlanType.of(mTenantMgtEntity.getPlanType());
		
		// ログイン後に、テナントのプランタイプが変更になったかどうか
		boolean isChangePlanType = sessionPlanType != tenantPlanType;
		if (isChangePlanType) {
			// プランタイプが変更になった場合 -> Handlerメソッドを実行不可とする
			
			// レスポンスにエラー情報を設定
			this.setPlanChangedErrorResponse(request, response);
			
			// Handlerメソッドは実行しない
			return false;
		}
		
		// 実行されたハンドラーのクラス情報、メソッド情報
		HandlerMethod hm = (HandlerMethod) handler;
		Class<?> clazz = hm.getBeanType();
		Method method = hm.getMethod();
		
		// 対象のクラス、メソッドに付与されている指定のアノテーションを取得（付与されていない場合はNULL）
		PlanFuncRestrictCheck classAnnotation = AnnotationUtils.findAnnotation(clazz, PlanFuncRestrictCheck.class);
		PlanFuncRestrictCheck methodAnnotation = AnnotationUtils.findAnnotation(method, PlanFuncRestrictCheck.class);
		
		if (CommonUtils.allNull(classAnnotation, methodAnnotation)) {
			// クラス、メソッドのいずれにもアノテーションが付与されていない（機能制限対象ではない）場合
			// -> 機能制限チェックは行わずHandlerメソッドを実行
			return true;
		}
		
		// 以下、ハンドラーがプランタイプによる、機能制限対象の場合
		
		// 機能制限チェック
		boolean canUseHandlerMethod = this.validCanUseHandlerMethod(classAnnotation, methodAnnotation);
		if (!canUseHandlerMethod) {
			// Handlerメソッド実行不可の状態の場合
			
			// レスポンスにエラー情報を設定
			this.setPlanFuncRestrictErrorResponse(request, response);
			
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
	private boolean validCanUseHandlerMethod(PlanFuncRestrictCheck classAnnotation, PlanFuncRestrictCheck methodAnnotation) {
		
		if (CommonUtils.allNull(classAnnotation, methodAnnotation)) {
			// クラス、メソッドのいずれにもアノテーションが付与されていない（機能制限対象ではない）場合
			// -> Handlerメソッド実行可
			return true;
		}
		
		if (!CommonUtils.anyNull(classAnnotation, methodAnnotation)) {
			// クラス、メソッドの両方にアノテーションが付与されている
			// -> メソッドのアノテーション設定を適用する
			
			PlanFuncRestrict[] funcs = methodAnnotation.funcs();
			boolean canUseHandlerMethod = this.validCanUseHandlerMethod(funcs);
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
			PlanFuncRestrict[] funcs = classAnnotation.funcs();
			boolean canUseHandlerMethod = this.validCanUseHandlerMethod(funcs);
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
			PlanFuncRestrict[] funcs = methodAnnotation.funcs();
			boolean canUseHandlerMethod = this.validCanUseHandlerMethod(funcs);
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
	 * @param validTargetFuncs
	 * @return
	 */
	private boolean validCanUseHandlerMethod(PlanFuncRestrict[] validTargetFuncs) {
		
		for (PlanFuncRestrict func : validTargetFuncs) {
			boolean canUse = SessionUtils.canUsePlanFunc(func);
			if (!canUse) {
				// 利用不可の機能が存在する場合
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * レスポンスに機能制限チェックエラーの情報を設定する
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void setPlanFuncRestrictErrorResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			// ajaxリクエストの場合

			// エラーコードを設定
			response.setStatus(CommonConstant.CUSTOME_PLAN_FUNC_RESTRICT_ERROR_CODE);
		} else {
			// 通常のhttpリクエストの場合 -> プラン機能制限エラー画面へのリダイレクト設定
			response.sendRedirect(UrlConstant.PLAN_FUNC_RESTRICT_ERROR_URL);
		}
	}
	
	/**
	 * レスポンスにプランが変更した旨のエラー情報を設定する
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void setPlanChangedErrorResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			// ajaxリクエストの場合

			// エラーコードを設定
			response.setStatus(CommonConstant.CUSTOME_PLAN_CHANGED_ERROR_CODE);
		} else {
			// 通常のhttpリクエストの場合 -> プランが変更した旨のエラー画面へのリダイレクト設定
			response.sendRedirect(UrlConstant.PLAN_CHANGED_ERROR_URL);
		}
	}
	
}
