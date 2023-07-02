package jp.loioz.common.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 非同期処理実行クラス
 */
@Service
public class AsyncExecutor {

	@Autowired
	private AsyncProxy asyncProxy;

	/**
	 * 非同期処理を実行する
	 *
	 * @param threadPoolGroup スレッドプール種別
	 * @param task 処理内容
	 */
	public void execute(ThreadPoolGroup threadPoolGroup, Runnable task) {

		// 現在のスレッド情報を取得
		ThreadLocalContext context = ThreadLocalContextManager.exportContext();

		// 非同期スレッド内で実行する処理
		Runnable enhancedTask = () -> {
			// 元のスレッド情報を非同期スレッドにコピー
			ThreadLocalContextManager.importContext(context);
			try {
				// 処理を実行
				task.run();
			} finally {
				// 非同期スレッドのスレッド情報をクリア
				ThreadLocalContextManager.clearContext();
			}
		};

		// 引数で指定されたスレッドプールを使って非同期処理を実行
		switch (threadPoolGroup) {
		case MAIL_SEND:
			asyncProxy.mailSend(enhancedTask);
			break;

		default:
			asyncProxy.execute(enhancedTask);
			break;
		}
	}

	@Service
	public class AsyncProxy {
		/**
		 * デフォルトのスレッドプールで非同期処理を実行する
		 *
		 * @param task 処理内容
		 */
		@Async
		public void execute(Runnable task) {
			task.run();
		}

		/**
		 * メール送信処理用のスレッドプールで非同期処理を実行する
		 *
		 * @param task 処理内容
		 */
		@Async("mailSendTaskExecutor")
		public void mailSend(Runnable task) {
			task.run();
		}
	}
}
