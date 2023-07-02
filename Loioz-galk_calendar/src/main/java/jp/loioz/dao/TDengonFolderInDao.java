package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TDengonFolderInEntity;

/**
 * 伝言-カスタムフォルダー中身用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TDengonFolderInDao {

	/**
	 * 1件のカスタムフォルダー中身Entityを取得する
	 *
	 * @param dengonSeq
	 * @param accountSeq
	 * @return カスタムフォルダー中身Entity
	 */
	@Select
	TDengonFolderInEntity selectByKey(Long dengonSeq, Long accountSeq);

	/**
	 * 複数件のカスタムフォルダー中身Entityリストを取得する
	 *
	 * @param dengonFolderSeq
	 * @return カスタムフォルダー中身Entityリスト
	 */
	@Select
	List<TDengonFolderInEntity> selectListByDengonFolderSeq(Long dengonFolderSeq);

	/**
	 * 複数件のカスタムフォルダー中身Entityリストを取得する
	 *
	 * @param dengonSeqList
	 * @param accountSeq
	 * @return カスタムフォルダー中身Entityリスト
	 */
	@Select
	List<TDengonFolderInEntity> selectListByDengonSeqList(List<Long> dengonSeqList, Long accountSeq);

	/**
	 * 複数件のカスタムフォルダー中身情報を登録する
	 *
	 * @param entitys
	 * @return 登録件数
	 */
	@BatchInsert
	int[] insert(List<TDengonFolderInEntity> entitys);

	/**
	 * 1件のカスタムフォルダー中身情報を削除する
	 *
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TDengonFolderInEntity entity);

	/**
	 * 複数件のカスタムフォルダー中身情報を削除する
	 *
	 * @param entitys
	 * @return 削除件数
	 */
	@BatchDelete
	int[] delete(List<TDengonFolderInEntity> entitys);
}