package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.SeisanKirokuBean;
import jp.loioz.entity.TSeisanKirokuEntity;

/**
 * 精算記録情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSeisanKirokuDao {

	/**
	 * 精算記録SEQをキーにして、精算記録情報を取得します。
	 * 
	 * @param seisanSeq 精算SEQ
	 * @return 精算記録情報
	 */
	@Select
	TSeisanKirokuEntity selectBySeisanSeq(Long seisanSeq);

	/**
	 * 顧客IDと案件IDをキーにして、精算記録情報のリストを取得します。
	 * 
	 * @param ankenIds 案件IDのリスト
	 * @param customerId 顧客ID
	 * @return 精算記録情報のリスト
	 */
	@Select
	List<SeisanKirokuBean> selectByAnkenIdsAndCustomerId(List<Long> ankenIds, Long customerId);

	/**
	 * 顧客IDと案件IDをキーにして、精算記録情報のリストを取得します。
	 * 
	 * @param customerIds 顧客IDのリスト
	 * @param ankenId 案件ID
	 * @return 精算記録情報のリスト
	 */
	@Select
	List<SeisanKirokuBean> selectByCustomerIdsAndAnkenId(List<Long> customerIds, Long ankenId);

	/**
	 * 関与者SEQをキーとして、精算記録を取得する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	List<TSeisanKirokuEntity> selectSeisanKirokuByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 名簿IDをキーとして、紐づく精算記録を取得します<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TSeisanKirokuEntity> selectSeisanKirokuByPersonId(Long personId);

	/**
	 * 精算記録データを全て削除します。
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTSisanKiroku();

	/**
	 * 1件の精算記録情報を登録します。
	 * 
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TSeisanKirokuEntity entity);

	/**
	 * 1件の精算記録情報を更新します。
	 * 
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TSeisanKirokuEntity entity);

}
