package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AccgDocAndRecordBean;
import jp.loioz.entity.TAccgRecordEntity;

/**
 * 取引実績Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgRecordDao {

	/**
	 * SEQをキーとして、取引実績情報を取得する
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	@Select
	TAccgRecordEntity selectAccgRecordEntityByAccgRecordSeq(Long accgRecordSeq);

	/**
	 * 会計書類SEQをキーとして、取引実績情報を取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	TAccgRecordEntity selectAccgRecordEntityByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQをキーとして、会計書類＋取引実績情報を取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	AccgDocAndRecordBean selectAccgDocByAccgDocSeq(Long accgDocSeq);

	/**
	 * 登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAccgRecordEntity entity);

	/**
	 * 更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TAccgRecordEntity entity);

	/**
	 * 削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TAccgRecordEntity entity);

	/**
	 * 取引実績データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgRecord();
	
}
