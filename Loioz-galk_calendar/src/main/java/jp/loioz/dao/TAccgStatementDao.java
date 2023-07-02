package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.bean.StatementDetailBean;
import jp.loioz.bean.StatementListBean;
import jp.loioz.domain.condition.StatementListSearchCondition;
import jp.loioz.domain.condition.StatementListSortCondition;
import jp.loioz.entity.TAccgStatementEntity;

/**
 * 精算書Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgStatementDao {

	/**
	 * 会計書類SEQに紐づく精算書データを取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	TAccgStatementEntity selectStatementByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく精算書データを取得します
	 * 
	 * @param accgDocSeqList
	 * @return
	 */
	@Select
	List<TAccgStatementEntity> selectStatementByAccgDocSeqList(List<Long> accgDocSeqList);

	/**
	 * 精算書SEQに紐づく精算書データを取得します
	 * 
	 * @param statementSeq
	 * @return
	 */
	@Select
	TAccgStatementEntity selectStatementByStatementSeq(Long statementSeq);

	/**
	 * 精算書画面の一覧データを１ページ分取得します。
	 * 
	 * @param statementListSearchCondition
	 * @param statementListSortCondition
	 * @param options
	 * @return
	 */
	@Select
	List<StatementListBean> selectStatementListBySearchConditions(StatementListSearchCondition statementListSearchCondition,
			StatementListSortCondition statementListSortCondition, SelectOptions options);

	/**
	 * 精算書詳細情報を取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	StatementDetailBean selectStatementDetailByAccgDocSeq(Long accgDocSeq);

	/**
	 * 名簿IDをキーとして、精算書情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TAccgStatementEntity> selectStatementByRefundToPersonId(Long personId);

	/**
	 * 名簿IDをキーとして、精算書情報の件数を取得する
	 * 
	 * @param personId
	 * @return
	 */
	default int selectStatementCountByRefundToPersonId(Long personId) {
		return selectStatementByRefundToPersonId(personId).size();
	}

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgStatementEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgStatementEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgStatementEntity entity);

	/**
	 * 精算書データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgStatement();
	
}
