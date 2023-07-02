package jp.loioz.config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import jp.loioz.common.utility.DateUtils;

/**
 * JSONの設定
 */
@Configuration
public class JsonConfig {

	private static final String DATE_FORMAT = DateUtils.DATE_FORMAT_SLASH_DELIMITED;

	private static final String DATE_TIME_FORMAT = DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED;

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> {
			// 日付時刻のフォーマット設定
			builder.simpleDateFormat(DATE_TIME_FORMAT);
			builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
			builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
		};
	}

	/**
	 * LocalDateをキーに持つMap用のシリアライザ
	 */
	public static class LocalDateKeySerializer extends JsonSerializer<LocalDate> {

		private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

		@Override
		public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeFieldName(FORMATTER.format(value));
		}
	}
}