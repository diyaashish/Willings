package jp.loioz.entity;

import java.time.LocalDateTime;

import jp.loioz.entity.common.DefaultEntityListener;

/**
 *
 */
public class TMailAddressChangeVerificationEntityListener extends DefaultEntityListener<TMailAddressChangeVerificationEntity> {

	@Override
	protected void setCreateInfo(TMailAddressChangeVerificationEntity entity, LocalDateTime now, Long accountSeq) {
		entity.setCreatedAt(now);
	}

	@Override
	protected void setUpdateInfo(TMailAddressChangeVerificationEntity entity, LocalDateTime now, Long accountSeq) {
		entity.setUpdatedAt(now);
	}

	@Override
	protected void setDeleteInfo(TMailAddressChangeVerificationEntity entity, LocalDateTime now, Long accountSeq) {
		// 論理削除カラムを持たないため、何もしない
	}
}