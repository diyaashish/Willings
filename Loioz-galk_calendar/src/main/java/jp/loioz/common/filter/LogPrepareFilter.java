package jp.loioz.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.MDC;

import jp.loioz.common.utility.SessionUtils;

/**
 * ログ出力の準備フィルター
 */
public class LogPrepareFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			HttpSession session = ((HttpServletRequest) request).getSession();

			if (session != null) {

				// セッションIDをSLF4jのMDCに登録する（ログ情報にSessionIdを出力するため）
				MDC.put("sessionId", session.getId());
			}
		}

		try {

			Long tenantSeq = SessionUtils.getTenantSeq();

			// セッションIDをSLF4jのMDCに登録する（ログ情報にtenantSeqを出力するため）
			MDC.put("tenantSeq", tenantSeq.toString());

		} catch (Exception e) {
			// セッションから情報を取得できない場合
			// なにもしない
		}

		try {
			// 後続の処理につなげる
			chain.doFilter(request, response);
		} finally {
			MDC.remove("sessionId");
			MDC.remove("tenantSeq");
		}
	}

}
