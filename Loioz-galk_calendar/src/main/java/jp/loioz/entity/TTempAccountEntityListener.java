package jp.loioz.entity;

import java.time.LocalDateTime;

import jp.loioz.entity.common.DefaultEntityListener;

/**
 *
 */
public class TTempAccountEntityListener extends DefaultEntityListener<TTempAccountEntity> {

	@Override
	protected void setCreateInfo(TTempAccountEntity entity, LocalDateTime now, Long accountSeq) {
		entity.setCreatedAt(now);
	}

	@Override
	protected void setUpdateInfo(TTempAccountEntity entity, LocalDateTime now, Long accountSeq) {
		entity.setUpdatedAt(now);
	}

	@Override
	protected void setDeleteInfo(TTempAccountEntity entity, LocalDateTime now, Long accountSeq) {
		// 論理削除カラムを持たないため、何もしない
	}
}