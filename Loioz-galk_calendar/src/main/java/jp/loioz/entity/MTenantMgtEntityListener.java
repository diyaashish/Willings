package jp.loioz.entity;

import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

import jp.loioz.entity.common.DefaultEntityListener;

/**
 *
 */
public class MTenantMgtEntityListener extends DefaultEntityListener<MTenantMgtEntity> {

	@Override
	public void preInsert(MTenantMgtEntity entity, PreInsertContext<MTenantMgtEntity> context) {
		// 何もしない
	}

	@Override
	public void preUpdate(MTenantMgtEntity entity, PreUpdateContext<MTenantMgtEntity> context) {
		// 何もしない
	}
}