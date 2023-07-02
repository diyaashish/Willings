package jp.loioz.common.utility;

/**
 * Loioz全般のUtilクラス。
 */
public class LoiozUtils {

	/**
	 * HTMLのタイトルタグの値を取得する。<br>
	 * 表示形式は下記となる。
	 * 
	 * <pre>
	 * 画面名 | loioz | 事務所名
	 * </pre>
	 * 
	 * @param viewName 画面名
	 * @param tenantName 事務所名
	 * @return
	 */
	public static String getHtmlTitle(String viewName, String tenantName) {
		String htmlTitle = "loioz";
		if (StringUtils.isNotEmpty(viewName)) {
			htmlTitle = viewName + " | " + htmlTitle;
		}
		if (StringUtils.isNotEmpty(tenantName)) {
			htmlTitle = htmlTitle + " | " + tenantName;
		}
		
		return htmlTitle;
	}
}
