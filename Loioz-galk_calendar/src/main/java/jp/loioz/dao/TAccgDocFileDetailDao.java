package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.entity.TAccgDocFileDetailEntity;

/**
 * 会計書類ファイル詳細Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocFileDetailDao {

	/**
	 * 会計書類ファイル詳細SEQをキーとして、会計書類ファイル詳細情報を取得する
	 * 
	 * @param accgDocFileDetailSeq
	 * @return
	 */
	default TAccgDocFileDetailEntity selectAccgDocFileDetailByAccgDocFileDetailSeq(Long accgDocFileDetailSeq) {
		return selectAccgDocFileDetailByAccgDocFileDetailSeq(Arrays.asList(accgDocFileDetailSeq)).stream().findFirst().orElse(null);
	};

	/**
	 * 会計書類ファイル詳細SEQをキーとして、会計書類ファイル詳細情報を取得する
	 * 
	 * @param accgDocFileDetailSeqList
	 * @return
	 */
	@Select
	List<TAccgDocFileDetailEntity> selectAccgDocFileDetailByAccgDocFileDetailSeq(List<Long> accgDocFileDetailSeqList);

	/**
	 * 会計書類ファイルSEQに紐づく会計書類ファイル詳細情報を取得します
	 * 
	 * @param accgDocFileSeq
	 * @return
	 */
	@Select
	List<TAccgDocFileDetailEntity> selectAccgDocFileDetailByAccgDocFileSeq(Long accgDocFileSeq);

	/**
	 * 会計書類SEQに紐づく、会計書類ファイル詳細情報を取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgDocFileDetailEntity> selectAccgDocFileDetailByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく、会計書類ファイル詳細情報を送付済みファイルを除き、取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgDocFileDetailEntity> selectAccgDocFileDetailExcludeSendByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQとファイル種別をキーとして、紐づく会計書類ファイル詳細情報を取得する
	 * 
	 * @param accgDocSeq
	 * @param accgDocFileType
	 * @return
	 */
	@Select
	List<TAccgDocFileDetailEntity> selectAccgDocFileDetailByAccgDocSeqAndAccgDocFileType(Long accgDocSeq, AccgDocFileType accgDocFileType);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgDocFileDetailEntity entity);

	/**
	 * 複数登録
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchInsert
	int[] batchInsert(List<TAccgDocFileDetailEntity> entityList);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgDocFileDetailEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TAccgDocFileDetailEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocFileDetailEntity entity);

	/**
	 * 削除
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocFileDetailEntity> entityList);

	/**
	 * 会計書類ファイル詳細データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocFileDetail();
	
}
