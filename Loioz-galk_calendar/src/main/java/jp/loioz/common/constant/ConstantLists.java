package jp.loioz.common.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.NoArgsConstructor;

/**
 * 定数List
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ConstantLists {

	/**
	 * コントローラクラスの@RequestMappingを取得します
	 * 
	 */
	public static final List<String> CONTROLLER_MAPPING_PATH = new ArrayList<>() {
		private static final long serialVersionUID = 1L;
		{
			ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);// スキャニングする際にDefaltでFilterをかけるかかけないか

			// スキャニングするクラスは@Controllerが付与されたクラスのみ
			scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

			// ControllerクラスのPackage
			Set<BeanDefinition> beanSet = scanner.findCandidateComponents("jp.loioz.app.user.**");

			Set<String> mappingPathList = new HashSet<>();
			for (BeanDefinition def : beanSet) {
				try {
					Class<?> clazz = Class.forName(def.getBeanClassName());
					// @RequestMappingのvalueを取得
					Optional.ofNullable(clazz).map(m -> m.getAnnotation(RequestMapping.class)).ifPresent(a -> Arrays.stream(a.value()).forEach(mappingPathList::add));
				} catch (ClassNotFoundException e) {
					// 何もしない
				}
			}
			// 重複削除した物をadd
			this.addAll(mappingPathList);
		};

	};

	/**
	 * システム内のRequestMappingPathをすべて取得します
	 * 
	 */
	public static final List<String> ALL_MAPPING_PATH = new ArrayList<>() {
		private static final long serialVersionUID = 1L;
		{
			ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);// スキャニングする際にDefaltでFilterをかけるかかけないか

			// スキャニングするクラスは@Controllerが付与されたクラスのみ
			scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

			// ControllerクラスのPackage
			Set<BeanDefinition> beanSet = scanner.findCandidateComponents("jp.loioz.app.user.**");

			Set<String> mappingPathList = new HashSet<>();
			for (BeanDefinition def : beanSet) {
				try {
					Class<?> clazz = Class.forName(def.getBeanClassName());
					// コントローラーclassの@RequestMappingのvalueを取得
					List<String> classPaths = new ArrayList<>();
					Optional.ofNullable(clazz).map(m -> m.getAnnotation(RequestMapping.class)).ifPresent(a -> Arrays.stream(a.value()).forEach(classPaths::add));

					List<String> methodPaths = new ArrayList<>();
					// 該当コントローラーのメソッド@RequestMappingのvalueを取得
					Stream.of(clazz.getMethods()).map(m -> m.getAnnotation(RequestMapping.class)).forEach(stream -> {
						Optional.ofNullable(stream).ifPresent(a -> Arrays.stream(a.value()).forEach(methodPaths::add));
					});

					classPaths.forEach(cp -> {
						methodPaths.forEach(mp -> {
							// @RequestMappingの書き方がコントローラーごとにスラッシュの付け方が統一されていないため正しいPathになっていないものがある
							// 例... @RequestMapping(value="/user"),@RequestMapping(value="user")
							mappingPathList.add(cp + mp);
						});
					});

				} catch (ClassNotFoundException e) {
					// 何もしない
				}
			}
			// 重複削除した物をadd
			this.addAll(mappingPathList);
		};

	};
}
