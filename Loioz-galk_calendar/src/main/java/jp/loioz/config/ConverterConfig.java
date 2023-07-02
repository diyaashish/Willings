package jp.loioz.config;

import java.lang.reflect.Method;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.domain.value.Id;

/**
 * 変換ルールの設定
 */
@Configuration
public class ConverterConfig implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverterFactory(new CodeToEnumConverterFactory());
		registry.addConverter(new EnumToCodeConverter());
		registry.addConverterFactory(new StringToIdConverterFactory());
	}

	/**
	 * Stringのコード値をEnumに変換するConverterのFactory
	 */
	private class CodeToEnumConverterFactory implements ConverterFactory<String, DefaultEnum> {

		@Override
		@SuppressWarnings({"unchecked", "rawtypes"})
		public <T extends DefaultEnum> Converter<String, T> getConverter(Class<T> targetType) {
			return new CodeToEnumConverter(targetType);
		}

		/**
		 * Stringのコード値をEnumに変換するConverter
		 */
		private class CodeToEnumConverter<T extends DefaultEnum> implements Converter<String, T> {

			private final Class<T> enumType;

			public CodeToEnumConverter(Class<T> enumType) {
				this.enumType = enumType;
			}

			@Override
			public T convert(String source) {
				return DefaultEnum.getEnum(enumType, source);
			}
		}
	}

	/**
	 * Enumをコード値に変換するConverter
	 */
	private class EnumToCodeConverter implements Converter<DefaultEnum, String> {
		@Override
		public String convert(DefaultEnum source) {
			return source.getCd();
		}
	}

	/**
	 * StringからIDに変換するConverterのFactory
	 */
	private class StringToIdConverterFactory implements ConverterFactory<String, Id<?>> {

		@Override
		public <T extends Id<?>> Converter<String, T> getConverter(Class<T> targetType) {
			return new StringToIdConverter<>(targetType);
		}

		/**
		 * StringからIDに変換するConverter
		 */
		private class StringToIdConverter<T extends Id<?>> implements Converter<String, T> {

			private final Class<T> type;

			public StringToIdConverter(Class<T> type) {
				this.type = type;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T convert(String source) {
				Method factoryMethod = ReflectionUtils.findMethod(type, "of", String.class);
				if (factoryMethod == null) {
					throw new IllegalStateException("Method not found: " + type.getName() + ".of(String)");
				}
				return (T) ReflectionUtils.invokeMethod(factoryMethod, null, source);
			}
		}
	}
}
