package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TDengonAccountStatusEntity;

/**
 * 伝言ステータス用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TDengonAccountStatusDao {

	/**
	 * キー項目から現在のステータス情報を取得
	 *
	 * @param accountSeq アカウントSeq
	 * @param dengonSeq 伝言Seq
	 * @return 伝言アカウントステータス情報
	 */
	@Select
	TDengonAccountStatusEntity selectByKey(Long accountSeq, Long dengonSeq);

	/**
	 * キー項目から複数の現在のステータス情報を取得
	 *
	 * @param accountSeq アカウントSeq
	 * @param dengonSeqList 伝言Seqリスト
	 * @return 伝言アカウントステータス情報
	 */
	@Select
	List<TDengonAccountStatusEntity> selectListByKey(Long accountSeq, List<Long> dengonSeqList);

	/**
	 * カスタムフォルダに登録されている伝言のアカウントステータス情報を取得する
	 *
	 * @param accountSe
	 * @param dengonFolderSeq
	 * @return 伝言アカウントステータス情報リスト
	 */
	@Select
	List<TDengonAccountStatusEntity> selectListByDengonFolderSeq(Long accountSeq, Long dengonFolderSeq);

	/**
	 * 複数のカスタムフォルダに登録されている伝言のアカウントステータス情報を取得する
	 * 
	 * @param loginAccountSeq
	 * @param dengonFolderSeqList
	 * @return 伝言アカウントステータス情報リスト
	 */
	@Select
	List<TDengonAccountStatusEntity> selectListByDengonFolderSeqList(Long loginAccountSeq, List<Long> dengonFolderSeqList);

	/**
	 * 複数件のステータス情報の登録
	 *
	 * @param entitys
	 * @return 登録したデータ件数
	 */
	@BatchInsert
	int[] insert(List<TDengonAccountStatusEntity> entitys);

	/**
	 * 1件のステータス情報の更新
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TDengonAccountStatusEntity entity);

	/**
	 * 複数件のステータス情報の更新
	 *
	 * @param entitys
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TDengonAccountStatusEntity> entitys);
}