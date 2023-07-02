package jp.loioz.dao;

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

import jp.loioz.domain.condition.KaikeiListYoteiSearchCondition;
import jp.loioz.dto.NyushukkinYoteiListBean;
import jp.loioz.entity.TNyushukkinYoteiEntity;

/**
 * 入出金予定情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TNyushukkinYoteiDao {

	/**
	 * 入出金予定SEQをキーにして、入出金予定情報を取得します。
	 *
	 * @param nyushukkinYoteiSeq 入出金予定SEQ
	 * @return 入出金予定情報
	 */
	@Select
	TNyushukkinYoteiEntity selectByNyushukkinYoteiSeq(Long nyushukkinYoteiSeq);

	/**
	 * 検索条件を元に、入出金予定情報を取得します。
	 *
	 * @param searchCondition 会計情報の検索条件
	 * @return 入金予定情報リスト
	 */
	@Select
	List<NyushukkinYoteiListBean> selectNyuShukkinYoteiBySearchCondition(KaikeiListYoteiSearchCondition searchCondition);

	/**
	 * 案件IDをキーにして、入出金予定情報を取得します。
	 *
	 * @param ankenId 案件ID
	 * @return 入出金予定情報
	 */
	@Select
	List<TNyushukkinYoteiEntity> selectByAnkenId(Long ankenId);

	/**
	 * 顧客IDをキーにして、入出金予定情報のリストを取得します。
	 *
	 * @param customerId 顧客ID
	 * @return 入出金予定情報
	 */
	@Select
	List<TNyushukkinYoteiEntity> selectByCustomerId(Long customerId);

	/**
	 * 案件IDと顧客IDをキーにして、入出金予定情報のリストを取得します。
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 入出金予定情報のリスト
	 */
	@Select
	List<TNyushukkinYoteiEntity> selectByAnkenIdAndCustomerId(Long ankenId, Long customerId, Boolean nyushukkinFlg);

	/**
	 * 精算SEQをキーにして、入出金予定情報を取得します。
	 *
	 * @param seisanSeq 精算SEQ
	 * @return 入出金予定情報のリスト
	 */
	@Select
	List<TNyushukkinYoteiEntity> selectBySeisanSeq(Long seisanSeq);

	/**
	 * 関与者SEQをキーとして、入出金予定情報を取得する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	List<TNyushukkinYoteiEntity> selectNyushukkinYoteiByKanyoshaSeq(Long kanyoshaSeq);

	/**
	 * 名簿IDをキーとして、紐づく入出金予定を取得します
	 *
	 * @param personId
	 * @return
	 */
	@Select
	List<TNyushukkinYoteiEntity> selectNyushukkinYoteiByPersonId(Long personId);

	/**
	 * 同一案件または一顧客の最新の会計記録情報を取得する。<br>
	 *
	 * <pre>
	 * 入出金項目登録時の判定用
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 会計記録情報
	 */
	@Select
	TNyushukkinYoteiEntity selectPrevByAnkenIdOrCustomerId(Long ankenId, Long customerId);

	/**
	 * 入出金項目IDをキーに、入出金予定情報を取得する。<br>
	 * 
	 * @param nyushukkinKomokuId
	 * @return
	 */
	@Select
	List<TNyushukkinYoteiEntity> selectNyushukkinYoteiByNyushukkinKomokuId(Long nyushukkinKomokuId);

	/**
	 * 入出金項目IDをキーに、入出金予定情報の件数を取得する。<br>
	 * @param nyushukkinKomokuId
	 * @return
	 */
	@Select
	int selectNyushukkinYoteiCountByNyushukkinKomokuId(Long nyushukkinKomokuId);

	/**
	 * 入出金予定データの件数を取得する
	 * @return
	 */
	@Select
	int selectNyushukkinYoteiCount();
	
	/**
	 * 入出金予定データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTNyushukkinYotei();

	/**
	 * 1件の入出金予定情報を登録します。
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TNyushukkinYoteiEntity entity);

	/**
	 * 複数件の入出金予定情報を登録します。
	 *
	 * @param entityList
	 * @return 登録したデータ件数
	 */
	@BatchInsert
	int[] insert(List<TNyushukkinYoteiEntity> entityList);

	/**
	 * 1件の入出金予定情報を更新します。
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TNyushukkinYoteiEntity entity);

	/**
	 * 複数件の入出金予定情報を更新します。
	 *
	 * @param entityList
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TNyushukkinYoteiEntity> entityList);

	/**
	 * 1件の入出金予定情報を削除します。
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TNyushukkinYoteiEntity entity);

	/**
	 * 複数件の入出金予定情報を削除します。
	 *
	 * @param entityList
	 * @return 削除したデータ件数
	 */
	@BatchDelete
	int[] delete(List<TNyushukkinYoteiEntity> entityList);

}
