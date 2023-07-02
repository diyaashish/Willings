package jp.loioz.entity;

import java.time.LocalDateTime;

import org.seasar.doma.jdbc.entity.PreUpdateContext;

import jp.loioz.common.utility.SessionUtils;
import jp.loioz.entity.common.DefaultEntityListener;

/**
 * 
 */
public class TLoginRecordEntityListener extends DefaultEntityListener<TLoginRecordEntity> {

	@Override
	public void preUpdate(TLoginRecordEntity entity, PreUpdateContext<TLoginRecordEntity> context) {

		LocalDateTime now = LocalDateTime.now();
		Long accountSeq = SessionUtils.getLoginAccountSeq();
		if (accountSeq == null) {
			accountSeq = Long.valueOf(0);
		}

		if (entity.getUpdatePattern()) {
			// 更新パターンが更新の場合

			// 更新日、更新者情報の設定
			setUpdateInfo(entity, now, accountSeq);

		} else {
			// 更新パターンが論理削除の場合

			// TLoginRecordEntityは論理削除用のカラムを持たないため、論理削除用の設定処理は行わない
		}
	}
}