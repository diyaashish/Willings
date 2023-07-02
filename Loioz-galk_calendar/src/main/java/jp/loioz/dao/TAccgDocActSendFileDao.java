package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TAccgDocActSendFileEntity;

/**
 * 会計書類-対応-送付-ファイルDao
 */
@ConfigAutowireable
@Dao
public interface TAccgDocActSendFileDao {

	/**
	 * SEQをキーとして、会計書類-対応-送付-ファイル情報を取得する
	 * 
	 * @param accgDocActSendFileSeq
	 * @return
	 */
	default TAccgDocActSendFileEntity selectAccgDocActSendFileByAccgDocActSendFileSeq(Long accgDocActSendFileSeq) {
		return selectAccgDocActSendFileByAccgDocActSendFileSeqList(Arrays.asList(accgDocActSendFileSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * SEQをキーとして、会計書類-対応-送付-ファイル情報を取得する
	 * 
	 * @param accgDocActSendFileSeqList
	 * @return
	 */
	@Select
	List<TAccgDocActSendFileEntity> selectAccgDocActSendFileByAccgDocActSendFileSeqList(List<Long> accgDocActSendFileSeqList);

	/**
	 * 会計書類-対応-送付SEQをキーとして、会計書類-対応-送付-ファイル情報を取得する
	 * 
	 * @param accgDocActSendSeq
	 * @return
	 */
	default List<TAccgDocActSendFileEntity> selectAccgDocActSendFileByAccgDocActSendSeq(Long accgDocActSendSeq) {
		return selectAccgDocActSendFileByAccgDocActSendSeq(Arrays.asList(accgDocActSendSeq));
	}

	/**
	 * 会計書類対応-送付-ファイルデータを取得します。<br>
	 * 
	 * @param accgDocActSendSeqList
	 * @return
	 */
	@Select
	List<TAccgDocActSendFileEntity> selectAccgDocActSendFileByAccgDocActSendSeq(List<Long> accgDocActSendSeqList);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgDocActSendFileEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgDocActSendFileEntity entity);

	/**
	 * 複数件の会計書類-対応-送付-ファイルを登録する
	 * 
	 * @param entity
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgDocActSendFileEntity> entities);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocActSendFileEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocActSendFileEntity> entityList);
	
	/**
	 * 会計書類-対応-送付-ファイルデータを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocActSendFile();

}
