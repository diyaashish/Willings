package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AccgRecordDetailBean;
import jp.loioz.domain.condition.RecordDetailSearchCondition;
import jp.loioz.entity.TAccgRecordDetailEntity;

/**
 * 取引実績明細Dao
 */
@ConfigAutowireable
@Dao
public interface TAccgRecordDetailDao {

	/**
	 * 取引実績詳細SEQをキーとして、取引実績詳細情報を取得する
	 * 
	 * @param accgRecordDetailSeq
	 * @return
	 */
	default TAccgRecordDetailEntity selectAccgRecordDetailEntityByAccgRecordDetailSeq(Long accgRecordDetailSeq) {
		return selectAccgRecordDetailEntityByAccgRecordDetailSeqList(Arrays.asList(accgRecordDetailSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 取引実績詳細SEQ配列をキーとして、取引実績詳細情報を取得する
	 * 
	 * @param accgRecordDetailSeqList
	 * @return
	 */
	@Select
	List<TAccgRecordDetailEntity> selectAccgRecordDetailEntityByAccgRecordDetailSeqList(List<Long> accgRecordDetailSeqList);

	/**
	 * 取引実績SEQをキーとして、紐づく取引実績詳細情報を取得する
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	@Select
	List<TAccgRecordDetailEntity> selectAccgRecordDetailEntityByAccgRecordSeq(Long accgRecordSeq);

	/**
	 * 会計書類SEQをキーとして、紐づく取引実績情報をすべて取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgRecordDetailEntity> selectAccgRecordDetailEntityByAccgDocSeq(Long accgDocSeq);

	/**
	 * 売上SEQをキーとして、紐づく取引実績情報をすべて取得する
	 * ※ 売上SEQは、案件と名簿に対して一意であるため、複数の会計書類に紐付く全データ取得となる
	 * 
	 * @param salesSeq
	 * @return
	 */
	@Select
	List<TAccgRecordDetailEntity> selectAccgRecordDetailEntityBySalesSeq(Long salesSeq);

	/**
	 * 取引実績画面：一覧取得
	 * 
	 * @return
	 */
	@Select
	List<AccgRecordDetailBean> selectAccgRecordDetailBeanByRecordDetailSearchConditions(RecordDetailSearchCondition conditions);

	/**
	 * 取引実績SEQをキーとして、紐づく取引実績詳細-過入金情報Beanを取得する
	 * 
	 * @param accgRecordSeq
	 * @return
	 */
	@Select
	List<AccgRecordDetailBean> selectAccgRecordDetailBeanByAccgRecordSeq(Long accgRecordSeq);

	/**
	 * 取引実績を登録する
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAccgRecordDetailEntity entity);

	/**
	 * 取引実績を更新する
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TAccgRecordDetailEntity entity);

	/**
	 * 取引実績を削除する
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TAccgRecordDetailEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return 削除結果
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgRecordDetailEntity> entityList);

	/**
	 * 取引実績明細データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgRecordDetail();
	
}
