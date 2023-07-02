package jp.loioz.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import jp.loioz.entity.common.DefaultEntityListener;

/**
 * 
 */
public class TPlanSettingSessionEntityListener extends DefaultEntityListener<TPlanSettingSessionEntity> {
	
	/**
	 * 作成日時をエンティティに設定<br>
	 * ※このEntityは作成者IDは設定しない
	 *
	 * @param entity
	 * @param now 現在日時
	 * @param accountSeq ログインユーザーのアカウント連番
	 */
	@Override
	protected void setCreateInfo(TPlanSettingSessionEntity entity, LocalDateTime now, Long accountSeq) {

		Class<?> clazz = entity.getClass();

		try {
			// 作成日
			Method setCreatedAtMethod = clazz.getMethod("setCreatedAt", LocalDateTime.class);
			setCreatedAtMethod.invoke(entity, now);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 更新日時をエンティティに設定<br>
	 * ※このEntityは更新者IDは設定しない
	 *
	 * @param entity
	 * @param now 現在日時
	 * @param accountSeq ログインユーザーのアカウント連番
	 */
	@Override
	protected void setUpdateInfo(TPlanSettingSessionEntity entity, LocalDateTime now, Long accountSeq) {

		Class<?> clazz = entity.getClass();

		try {
			// 更新日
			Method setUpdatedAtMethod = clazz.getMethod("setUpdatedAt", LocalDateTime.class);
			setUpdatedAtMethod.invoke(entity, now);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

	}
}