package jp.loioz.entity;

import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

import jp.loioz.entity.common.DefaultEntityListener;

/**
 * 問い合わせ-詳細
 */
public class TToiawaseDetailEntityListener extends DefaultEntityListener<TToiawaseDetailEntity> {

	@Override
	public void preInsert(TToiawaseDetailEntity entity, PreInsertContext<TToiawaseDetailEntity> context) {
		// なにもしない
	}

	@Override
	public void preUpdate(TToiawaseDetailEntity entity, PreUpdateContext<TToiawaseDetailEntity> context) {
		// なにもしない
	}

}