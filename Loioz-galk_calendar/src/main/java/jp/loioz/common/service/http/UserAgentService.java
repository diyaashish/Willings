package jp.loioz.common.service.http;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import jp.loioz.common.constant.CommonConstant.DeviceType;
import lombok.NonNull;

/**
 * ユーザーエージェントを扱うサービスクラス
 */
@Service
public class UserAgentService {

	/** ユーザーエージェントで判別するため文字列 */
	private static final String IPHONE = "iPhone";
	private static final String IPOD = "iPod";
	private static final String ANDROID = "Android";
	private static final String IPAD = "iPad";
	private static final String MOBILE = "Mobile";

	/**
	 * リクエストした端末がスマホかを判定する
	 * 
	 * @param request
	 * @return
	 */
	public DeviceType getDevice(@NonNull HttpServletRequest request) {
		String userAgent = this.getUserAgent(request);

		if (userAgent.contains(IPHONE) || userAgent.contains(IPOD)
				|| (userAgent.contains(ANDROID) && userAgent.contains(MOBILE))) {
			// スマホ
			return DeviceType.MOBILE;
		} else if (userAgent.contains(IPAD) || userAgent.contains(ANDROID)) {
			// タブレット (iPadとスマホ以外のAndroid端末)
			return DeviceType.TABLET;
		} else {
			// PC
			return DeviceType.PC;
		}
	}

	/**
	 * リクエストした端末がスマホかを判定する
	 * 
	 * @param request
	 * @return
	 */
	public boolean isSmartPhone(@NonNull HttpServletRequest request) {
		return getDevice(request) == DeviceType.MOBILE;
	}

	/**
	 * リクエストした端末がタブレットかを判定する
	 * 
	 * @param request
	 * @return
	 */
	public boolean isTablet(@NonNull HttpServletRequest request) {
		return getDevice(request) == DeviceType.TABLET;
	}

	/**
	 * HttpRequestからユーザーエージェントを取得する
	 * 
	 * @param request
	 * @return
	 */
	private String getUserAgent(@NonNull HttpServletRequest request) {
		return request.getHeader(HttpHeaders.USER_AGENT);
	}

}
