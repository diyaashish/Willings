package jp.loioz.domain.verification;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;

/**
 * 認証サービスクラス
 */
public abstract class VerificationService<T> extends DefaultService {

	/** ロガー */
	@Autowired
	private Logger log;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 認証キーを生成する
	 *
	 * @return 認証キー
	 */
	public String createVerificationKey() {
		do {
			// 認証キーを生成
			String key = StringUtils.randomAlphanumeric(64);

			// 発行済みのキーと重複していないか確認
			if (!keyExists(key)) {
				return key;
			}
		} while (true);
	}

	/**
	 * 認証情報を取得する
	 *
	 * @param key 認証キー
	 * @return 認証情報
	 */
	public abstract T getVerification(String key);

	/**
	 * 認証情報のバリデーション
	 *
	 * @param entity 認証情報
	 * @return 認証エラーの場合はエラー種別、エラーがない場合はnull
	 */
	public VerificationErrorType validate(T entity) {
		if (entity == null) {
			// 認証情報が取得できない
			return VerificationErrorType.NOT_EXISTS;
		}

		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(getLimitDate(entity))) {
			// 認証キーが有効期限切れ
			return VerificationErrorType.EXPIRED;

		} else if (SystemFlg.FLG_ON.equalsByCode(getCompleteFlg(entity))) {
			// 認証完了済み
			return VerificationErrorType.COMPLETED;
		}
		// エラーなし
		return null;
	}

	/**
	 * 認証情報から有効期限を取得する
	 *
	 * @param entity 認証情報
	 * @return 有効期限
	 */
	protected abstract LocalDateTime getLimitDate(T entity);

	/**
	 * 認証情報から完了フラグを取得する
	 *
	 * @param entity 認証情報
	 * @return 完了フラグ
	 */
	protected abstract String getCompleteFlg(T entity);

	/**
	 * 認証情報のステータスを完了にする
	 *
	 * @param key 認証キー
	 * @throws AppException 更新に失敗した場合
	 */
	@Transactional(rollbackFor = AppException.class)
	public void updateToComplete(String key) throws AppException {
		int updateCount = updateToCompleteExe(key);
		if (updateCount != 1) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			log.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 認証情報のステータスを完了にする
	 *
	 * @param key 認証キー
	 * @return 更新件数
	 */
	protected abstract int updateToCompleteExe(String key);

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 認証キーが既に発行されているか確認する
	 *
	 * @param key 認証キー
	 * @return 入力の認証キーが発行済み認証キーと重複する場合はtrue
	 */
	private boolean keyExists(String key) {
		return getVerification(key) != null;
	}
}
