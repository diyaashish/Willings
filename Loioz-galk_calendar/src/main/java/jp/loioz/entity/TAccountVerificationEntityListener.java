package jp.loioz.entity;

import java.time.LocalDateTime;

import jp.loioz.entity.common.DefaultEntityListener;

/**
 *
 */
public class TAccountVerificationEntityListener extends DefaultEntityListener<TAccountVerificationEntity> {

	@Override
	protected void setCreateInfo(TAccountVerificationEntity entity, LocalDateTime now, Long accountSeq) {
		entity.setCreatedAt(now);
	}

	@Override
	protected void setUpdateInfo(TAccountVerificationEntity entity, LocalDateTime now, Long accountSeq) {
		entity.setUpdatedAt(now);
	}

	@Override
	protected void setDeleteInfo(TAccountVerificationEntity entity, LocalDateTime now, Long accountSeq) {
		// 論理削除カラムを持たないため、何もしない
	}
}