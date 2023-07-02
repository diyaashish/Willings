package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AnkenMeisaiBean;
import jp.loioz.domain.condition.KaikeiListMeisaiSearchCondition;
import jp.loioz.dto.HoshuMeisaiBean;
import jp.loioz.dto.KaikeiKirokuBean;
import jp.loioz.entity.TKaikeiKirokuEntity;

/**
 * 会計記録(案件明細)情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TKaikeiKirokuDao {

	// *************************************
	// 案件明細情報 固有
	// *************************************

	/**
	 * 顧客IDをキーにして、案件明細情報のリストを取得します。
	 *
	 * @param customerId 顧客ID
	 * @return 案件明細情報のリスト
	 */
	@Select
	List<AnkenMeisaiBean> selectAnkenMeisaiByCustomerId(Long customerId);

	/**
	 * 案件IDをキーにして、案件明細情報のリストを取得します。
	 *
	 * @param ankenId 案件ID
	 * @return 案件明細情報のリスト
	 */
	@Select
	List<AnkenMeisaiBean> selectAnkenMeisaiByAnkenId(Long ankenId);

	/**
	 * 案件IDと顧客IDをキーにして、会計記録情報のリストを取得します。
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 会計記録情報のリスト
	 */
	@Select
	List<TKaikeiKirokuEntity> selectByAnkenIdAndCustomerId(Long ankenId, Long customerId, Boolean seisanFlg);

	/**
	 * 会計記録SEQをキーにして、会計記録情報を取得します。
	 *
	 * @param kaikeiKirokuSeq 会計記録SEQ
	 * @return 会計記録情報
	 */
	@Select
	TKaikeiKirokuEntity selectByKaikeiKirokuSeq(Long kaikeiKirokuSeq);

	/**
	 * 会計記録SEQ(List)をキーにして、会計記録情報を取得します。
	 *
	 * @param kaikeiSeqList
	 * @param orderByHasseiDate
	 * @return 会計記録情報(List)
	 */
	@Select
	List<TKaikeiKirokuEntity> selectBySeqListOrderByHasseiDate(List<Long> kaikeiSeqList, boolean orderByHasseiDate);

	/**
	 * 会計記録SEQ(List)をキーにして、会計記録情報を取得します。
	 *
	 * @param kaikeiSeqList
	 * @return 会計記録情報(List)
	 */
	default List<TKaikeiKirokuEntity> selectBySeqList(List<Long> kaikeiSeqList) {
		return selectBySeqListOrderByHasseiDate(kaikeiSeqList, false);
	}

	/**
	 * 案件ID、顧客ID、報酬項目IDをキーにして、会計記録情報を取得します。<br>
	 *
	 * <pre>
	 * 直近のタイムチャージ情報を取得する時用です。
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @param hoshuKomokuId 報酬項目ID
	 * @param orderItem2
	 * @return 会計記録情報
	 */
	@Select
	List<TKaikeiKirokuEntity> selectByAnkenIdAndCustomerIdAndHoshuKomoku(Long ankenId, Long customerId, String timeCharge, boolean orderByHasseiDate);

	/**
	 * 同一案件かつ同一顧客の最新の会計記録情報を取得する。<br>
	 *
	 * <pre>
	 * 報酬登録時の判定用
	 * </pre>
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 会計記録情報
	 */
	@Select
	TKaikeiKirokuEntity selectPrevByAnkenIdAndCustomerId(Long ankenId, Long customerId);

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
	TKaikeiKirokuEntity selectPrevByAnkenIdOrCustomerId(Long ankenId, Long customerId);

	// *************************************
	// 報酬明細 固有
	// *************************************
	/**
	 * 案件IDをキーにして、会計記録情報を取得します。
	 *
	 * @param ankenId 案件ID
	 * @return 会計記録情報のリスト
	 */
	@Select
	List<HoshuMeisaiBean> selectHoshuMeisaiByAnkenId(Long ankenId, Long customerId);

	/**
	 * 顧客IDをキーにして、会計記録情報を取得します。
	 *
	 * @param customerId 顧客ID
	 * @return 会計記録情報のリスト
	 */
	@Select
	List<HoshuMeisaiBean> selectHoshuMeisaiByCustomerId(Long customerId, Long ankenId);

	/**
	 * 案件IDをキーにして、精算済以外の会計記録情報を取得します。
	 *
	 * @param ankenId 案件ID
	 * @return 会計記録情報のリスト
	 */
	@Select
	List<HoshuMeisaiBean> selectNotCompByAnkenId(Long ankenId, Long customerId, List<String> completedExcludeAnkenStatusCd);

	/**
	 * 顧客IDをキーにして、精算済以外の会計記録情報を取得します。
	 *
	 * @param customerId 顧客ID
	 * @return 会計記録情報のリスト
	 */
	@Select
	List<HoshuMeisaiBean> selectNotCompByCustomerId(Long customerId, Long ankenId, List<String> completedExcludeAnkenStatusCd);

	// *************************************
	// 会計記録情報 固有
	// *************************************
	/**
	 * 検索条件を元に、会計記録情報を取得します。
	 *
	 * @param searchCondition 会計情報の検索条件
	 * @return 会計記録情報
	 */
	@Select
	List<KaikeiKirokuBean> selectBySearchCondition(KaikeiListMeisaiSearchCondition searchCondition);

	/**
	 * 会計記録SEQをキーにして、会計記録情報を取得します。<br>
	 *
	 * <pre>
	 * 更新対象の入出金情報を特定する際に使用します。
	 * </pre>
	 *
	 * @param kaikeiKirokuSeq 会計記録SEQ
	 * @return 会計記録情報
	 */
	@Select
	KaikeiKirokuBean selectNyushukkinByKaikeiKirokuSeq(Long kaikeiKirokuSeq);

	/**
	 * 入出金項目IDをキーにして、会計記録情報を取得します。<br>
	 * 
	 * @param nyushukkinKomokuId 入出金項目ID
	 * @return
	 */
	@Select
	List<TKaikeiKirokuEntity> selectKaikeiKirokuByNyushukkinKomokuId(Long nyushukkinKomokuId);

	/**
	 * 入出金項目IDをキーにして、会計記録情報の件数を取得します。<br>
	 * 
	 * @param nyushukkinKomokuId 入出金項目ID
	 * @return
	 */
	@Select
	int selectKaikeiKirokuCountByNyushukkinKomokuId(Long nyushukkinKomokuId);

	// *************************************
	// 入出金予定 固有
	// *************************************
	/**
	 * 入出金予定SEQをキーにして、会計記録情報を取得します。<br>
	 *
	 * <pre>
	 * 入出金予定情報の更新時に使用します。
	 * </pre>
	 *
	 * @param nyushukkinYoteiSeq 入出金予定SEQ
	 * @return 会計記録情報
	 */
	@Select
	TKaikeiKirokuEntity selectByNyushukkinYoteiSeq(Long nyushukkinYoteiSeq);

	/**
	 * 入出金予定SEQリストをキーにして、会計記録情報リストを取得します。
	 *
	 * @param nyushukkinYoteiSeqList 入出金予定SEQリスト
	 * @return 会計記録情報リスト
	 */
	@Select
	List<TKaikeiKirokuEntity> selectByNyushukkinYoteiSeqList(List<Long> nyushukkinYoteiSeqList);

	/**
	 * 顧客ID,案件ID,報酬項目を指定してを件数を取得します。<br>
	 *
	 * <pre>
	 * 精算書作成時に報酬項目がタイムチャージ対象のデータが存在するかを判定する際に使用します。
	 * </pre>
	 *
	 * @param customerId 顧客ID
	 * @param ankenId 顧客ID
	 * @param hoshuKomokuId 報酬項目ID
	 * @return データカウント
	 */
	@Select
	List<TKaikeiKirokuEntity> selectByHoshuKomoku(Long customerId, Long ankenId, String hoshuKomokuId);

	// *************************************
	// 精算記録、支払計画
	// *************************************
	/**
	 * 精算SEQをキーにして、会計記録情報を取得します。
	 *
	 * @param seisanSeq 精算SEQ
	 * @return 会計記録情報のリスト
	 */
	@Select
	List<TKaikeiKirokuEntity> selectBySeisanSeq(Long seisanSeq);

	/**
	 * 精算SEQリストをキーにして、会計記録情報を取得します。
	 *
	 * @param seisanSeqList 精算SEQリスト
	 * @return 会計記録情報のリスト
	 */
	@Select
	List<TKaikeiKirokuEntity> selectBySeisanSeqList(List<Long> seisanSeqList);

	// *************************************
	// 精算書作成・編集
	// *************************************
	/**
	 * 精算SEQをキーにして、プール処理した会計記録情報を取得します。
	 *
	 * @param seisanSeq 精算SEQ
	 * @return 会計記録情報
	 */
	@Select
	List<TKaikeiKirokuEntity> selectPoolBySeisanSeq(Long seisanSeq);

	// *************************************
	// 共通
	// *************************************

	/**
	 * 案件IDと顧客IDをキーとして、※未精算データの件数を取得します。
	 *
	 * <pre>
	 * ※ 未精算データ <br>
	 * → 精算処理されていない会計記録データの件数 + 精算により作成された実績は登録されていない入出金予定データ件数
	 * </pre>
	 *
	 * @param ankenId
	 * @param customerId
	 * @return
	 */
	@Select
	int countMiseisanByAnkenIdAndCustomerId(Long ankenId, Long customerId);

	/**
	 * 案件Idをキーとして、会計記録情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TKaikeiKirokuEntity> selectByAnkenId(Long ankenId);

	/**
	 * 顧客Idをキーとして、会計記録情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<TKaikeiKirokuEntity> selectByCustomerId(Long customerId);

	/**
	 * 会計記録情報の件数を取得する
	 * @return
	 */
	@Select
	int selectKaikeiKirokuCount();
	
	/**
	 * 会計記録データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTKaikeiKiroku();

	/**
	 * 1件の会計記録情報を登録します。
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TKaikeiKirokuEntity entity);

	/**
	 * 複数件の会計記録情報を登録します。
	 *
	 * @param entityList
	 * @return 登録したデータ件数
	 */
	@BatchInsert
	int[] insert(List<TKaikeiKirokuEntity> entityList);

	/**
	 * 1件の会計記録情報を更新します。
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TKaikeiKirokuEntity entity);

	/**
	 * 複数件の会計記録情報を更新します。
	 *
	 * @param entityList
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TKaikeiKirokuEntity> entityList);

	/**
	 * 1件の会計記録情報を削除します。
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TKaikeiKirokuEntity entity);
}
