package jp.loioz.entity.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

import jp.loioz.common.utility.SessionUtils;

public class DefaultEntityListener<T extends DefaultEntity> implements EntityListener<T> {

	@Override
	public void preInsert(T entity, PreInsertContext<T> context) {

		LocalDateTime now = LocalDateTime.now();
		Long accountSeq = SessionUtils.getLoginAccountSeq();
		if (accountSeq == null) {
			accountSeq = Long.valueOf(0);
		}

		// 作成日、作成者情報の設定
		setCreateInfo(entity, now, accountSeq);
		// 更新日、更新者情報の設定
		setUpdateInfo(entity, now, accountSeq);
	}

	@Override
	public void preUpdate(T entity, PreUpdateContext<T> context) {

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

			// 削除日、削除者情報の設定
			setDeleteInfo(entity, now, accountSeq);
		}
	}

	@Override
	public void preDelete(T entity, PreDeleteContext<T> context) {
	}

	@Override
	public void postInsert(T entity, PostInsertContext<T> context) {
	}

	@Override
	public void postUpdate(T entity, PostUpdateContext<T> context) {
	}

	@Override
	public void postDelete(T entity, PostDeleteContext<T> context) {
	}

	/**
	 * 作成日時、作成者IDをエンティティに設定
	 *
	 * @param entity
	 * @param now 現在日時
	 * @param accountSeq ログインユーザーのアカウント連番
	 */
	protected void setCreateInfo(T entity, LocalDateTime now, Long accountSeq) {

		Class<?> clazz = entity.getClass();

		try {
			// 作成日
			Method setCreatedAtMethod = clazz.getMethod("setCreatedAt", LocalDateTime.class);
			setCreatedAtMethod.invoke(entity, now);

			// 作成者
			Method setCreatedByMethod = clazz.getMethod("setCreatedBy", Long.class);
			setCreatedByMethod.invoke(entity, accountSeq);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			// TODO 例外をスローする処理を記載する

			e.printStackTrace();
		}

	}

	/**
	 * 更新日時、更新者IDをエンティティに設定
	 *
	 * @param entity
	 * @param now 現在日時
	 * @param accountSeq ログインユーザーのアカウント連番
	 */
	protected void setUpdateInfo(T entity, LocalDateTime now, Long accountSeq) {

		Class<?> clazz = entity.getClass();

		try {
			// 更新日
			Method setUpdatedAtMethod = clazz.getMethod("setUpdatedAt", LocalDateTime.class);
			setUpdatedAtMethod.invoke(entity, now);

			// 作成者
			Method setUpdatedByMethod = clazz.getMethod("setUpdatedBy", Long.class);
			setUpdatedByMethod.invoke(entity, accountSeq);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			// TODO 例外をスローする処理を記載する

			e.printStackTrace();
		}

	}

	/**
	 * 削除日時、削除者IDをエンティティに設定
	 *
	 * @param entity
	 * @param now 現在日時
	 * @param accountSeq ログインユーザーのアカウント連番
	 */
	protected void setDeleteInfo(T entity, LocalDateTime now, Long accountSeq) {

		Class<?> clazz = entity.getClass();

		try {
			// 更新日
			Method setDeletedAtMethod = clazz.getMethod("setDeletedAt", LocalDateTime.class);
			setDeletedAtMethod.invoke(entity, now);

			// 作成者
			Method setDeletedByMethod = clazz.getMethod("setDeletedBy", Long.class);
			setDeletedByMethod.invoke(entity, accountSeq);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

			// TODO 例外をスローする処理を記載する

			e.printStackTrace();
		}
	}
}
