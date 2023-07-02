package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AccgSalesTotalBean;
import jp.loioz.entity.TSalesEntity;

/**
 * 売上用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSalesDao {

	/**
	 * 売上を取得する
	 * 
	 * @param salesSeq
	 * @return
	 */
	default TSalesEntity selectBySeq(Long salesSeq) {
		return selectBySeq(Arrays.asList(salesSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 売上を取得する<br>
	 *
	 * @param salesSeqList
	 * @return
	 */
	@Select
	List<TSalesEntity> selectBySeq(List<Long> salesSeqList);

	/**
	 * 名簿IDと案件IDをキーとして、売上情報を取得する
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	@Select
	TSalesEntity selectSalesEntityByPersonIdAndAnkenId(Long personId, Long ankenId);

	/**
	 * 名簿IDと案件IDをキーとして、売上情報の合計を取得する
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	@Select
	AccgSalesTotalBean selectSalesTotalByPersonIdAndAnkenId(Long personId, Long ankenId);

	/**
	 * 名簿IDをキーとして、売上情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TSalesEntity> selectSalesByPersonId(Long personId);

	/**
	 * 名簿IDをキーとして、売上情報の件数を取得する
	 * 
	 * @param personId
	 * @return
	 */
	default int selectSalesCountByPersonId(Long personId) {
		return selectSalesByPersonId(personId).size();
	}

	/**
	 * 案件IDをキーとして、売上情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TSalesEntity> selectSalesByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、売上情報の件数を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	default int selectSalesCountByAnkenId(Long ankenId) {
		return selectSalesByAnkenId(ankenId).size();
	};

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TSalesEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TSalesEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TSalesEntity entity);

	/**
	 * 売上データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTSales();

}
