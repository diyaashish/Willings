package jp.loioz.app.common.pdf.data;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PDF出力受け渡し用オブジェクトインタフェース
 */
public interface PdfData {

	/** PDF出力のパラメータ変換 */
	default Map<String, Object> convertPdfParams() {

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {
		};
		Map<String, Object> params = mapper.convertValue(this, type);
		return params;
	};

}
