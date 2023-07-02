package jp.loioz.entity;

import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

import jp.loioz.entity.common.DefaultEntityListener;

/**
 * 問い合わせ
 */
public class TToiawaseEntityListener extends DefaultEntityListener<TToiawaseEntity> {

	@Override
	public void preInsert(TToiawaseEntity entity, PreInsertContext<TToiawaseEntity> context) {
		// なにもしない
	}

	@Override
	public void preUpdate(TToiawaseEntity entity, PreUpdateContext<TToiawaseEntity> context) {
		// なにもしない
	}

}