package jp.loioz.common.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ユーティリティリフレクションメソッドUtilクラス。
 */
public class LoiozMethodUtils {

	/**
	 * return org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls)
	 * 
	 * @param cls
	 * @param annotationCls
	 * @return
	 */
	public static List<Method> getMethodsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
		return org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation(cls, annotationCls);
	}
}
