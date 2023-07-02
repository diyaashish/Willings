package jp.loioz.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jp.loioz.common.dataSource.SchemaContextHolder;

/**
 * スレッドの準備フィルター
 */
public class ThreadPrepareFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// ※SchemaContextHolderが保持するスレッド上の変数に企業連番が残っていると、DB接続時に他の企業のDBへ接続できる状態となってしまうため,
		// そのようなことが発生しないように、リクエストの開始時と終了時に、スレッドに保持する変数を必ずクリアするようにする。

		// リクエストの開始時にスレッド情報をクリアする
		SchemaContextHolder.clear();

		try {
			// 後続の処理につなげる
			chain.doFilter(request, response);
		} finally {
			// リクエストの終了時にスレッド情報をクリアする
			SchemaContextHolder.clear();
		}

	}

}
