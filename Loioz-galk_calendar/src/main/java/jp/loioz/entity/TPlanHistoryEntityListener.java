package jp.loioz.entity;

import org.seasar.doma.jdbc.entity.PreInsertContext;

import jp.loioz.entity.common.DefaultEntityListener;

/**
 * 
 */
public class TPlanHistoryEntityListener extends DefaultEntityListener<TPlanHistoryEntity> {

	@Override
	public void preInsert(TPlanHistoryEntity entity, PreInsertContext<TPlanHistoryEntity> context) {

		super.preInsert(entity, context);
		
		// 履歴登録日
		entity.setHistoryCreatedAt(entity.getCreatedAt());
		// 履歴登録アカウント
		entity.setHistoryCreatedBy(entity.getCreatedBy());
	}
}