
package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryListByAnkenSearchForm;
import jp.loioz.app.user.gyomuHistory.form.Customer.GyomuHistoryListByCustomerSearchForm;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.dto.GyomuHistoryHeaderDto;
import jp.loioz.dto.GyomuHistoryListBean;
import jp.loioz.entity.TGyomuHistoryEntity;

/**
 * 業務履歴のDaoクラス
 *
 */
@ConfigAutowireable
@Dao
public interface TGyomuHistoryDao {

	/**
	 * 業務履歴の取得（1件）
	 *
	 * @param gyomuHistorySeq
	 * @return
	 */
	@Select
	TGyomuHistoryEntity selectBySeq(Long gyomuHistorySeq);

	/**
	 * 業務履歴の取得（複数件）
	 *
	 * @param gyomuHistorySeq
	 * @return
	 */
	@Select
	List<TGyomuHistoryEntity> selectEntityBySeqList(List<Long> gyomuHistorySeqList);

	/**
	 * ヘッダー情報の取得
	 *
	 * 遷移元が案件の場合
	 *
	 * @param ankenId
	 * @return
	 */
	@Select
	List<GyomuHistoryHeaderDto> selectAnkenHeader(Long ankenId);

	/**
	 * ヘッダー情報の取得
	 *
	 * 遷移元が顧客画面の場合
	 *
	 * @param customerId
	 * @return
	 */
	@Select
	List<GyomuHistoryHeaderDto> selectCustomerHeader(Long customerId);

	/**
	 * 検索条件から業務履歴 案件側の一覧情報に表示するSEQを取得します。
	 *
	 * <pre>
	 * 一覧情報を2段階取得する(1段階目)
	 * → 1対NのN側を他のテーブルと結合し、情報を取得するため
	 *     ページャ情報の取得と、表示するデータの取得を分ける
	 *
	 * 取得されるSEQは一覧で表示する件数と同じ(default 50件)
	 * </pre>
	 *
	 * @param searchForm
	 * @return 一覧に表示する業務履歴のSEQリスト
	 */
	@Select
	List<Long> selectAnkenGyomuHistorySeqListBySearchConditions(GyomuHistoryListByAnkenSearchForm searchForm, SelectOptions options);

	/**
	 * 検索条件から、業務履歴 案件側の一覧情報を取得します。
	 *
	 * <pre>
	 * 一覧情報を2段階取得する(2段階目)
	 * → 1対NのN側を他のテーブルと結合し、情報を取得するため
	 *     ページャ情報の取得と、表示するデータの取得を分ける
	 *
	 * 1段階目取得したSEQリストを検索条件に含める
	 * </pre>
	 *
	 * @param searchForm
	 * @param seqList
	 * @return 業務履歴 案件軸の一覧情報
	 */
	@Select
	List<GyomuHistoryListBean> selectAnkenGyomuHistoryBeansBySearchConditions(GyomuHistoryListByAnkenSearchForm searchForm, List<Long> seqList);

	/**
	 * 検索条件から業務履歴 顧客側の一覧情報に表示するSEQを取得します。
	 *
	 * <pre>
	 * 一覧情報を2段階取得する(1段階目)
	 * → 1対NのN側を他のテーブルと結合し、情報を取得するため
	 *     ページャ情報の取得と、表示するデータの取得を分ける
	 *
	 * 取得されるSEQは一覧で表示する件数と同じ(default 50件)
	 * </pre>
	 *
	 * @param searchForm
	 * @return 一覧に表示する業務履歴のSEQリスト
	 */
	@Select
	List<Long> selectCustomerGyomuHistorySeqListBySearchConditions(GyomuHistoryListByCustomerSearchForm searchForm, SelectOptions options);

	/**
	 * 検索条件から、業務履歴 顧客側の一覧情報を取得します。
	 *
	 * <pre>
	 * 一覧情報を2段階取得する(2段階目)
	 * → 1対NのN側を他のテーブルと結合し、情報を取得するため
	 *     ページャ情報の取得と、表示するデータの取得を分ける
	 *
	 * 1段階目取得したSEQリストを検索条件に含める
	 * </pre>
	 *
	 * @param searchForm
	 * @param seqList
	 * @return 業務履歴 顧客軸の一覧情報
	 */
	@Select
	List<GyomuHistoryListBean> selectCustomerGyomuHistoryBeansBySearchConditions(GyomuHistoryListByCustomerSearchForm searchForm, List<Long> seqList);

	/**
	 * IDをもとに業務履歴情報を取得
	 *
	 * @param gyomuHistorySeq
	 * @return
	 */
	@Select
	List<GyomuHistoryListBean> selectBySeqList(List<Long> seqList);

	/**
	 * 業務履歴連番から案件情報取得
	 */
	@Select
	List<AnkenCustomerRelationBean> selectAnkenBeanBySeq(Long seq);

	/**
	 * 業務履歴連番から顧客情報取得(個人顧客用)
	 */
	@Select
	List<AnkenCustomerRelationBean> selectCustomerBeanBySeq(Long seq);

	/**
	 * 裁判SEQをキーとして、業務履歴の取得を行う
	 * 
	 * @param saibanSeq
	 * @return
	 */
	@Select
	List<TGyomuHistoryEntity> selectBySaibanSeq(Long saibanSeq);

	/**
	 * 業務履歴を登録（1件）
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TGyomuHistoryEntity entity);

	/**
	 * 業務履歴を更新（1件）
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TGyomuHistoryEntity entity);

	/**
	 * 業務履歴を更新（複数）
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TGyomuHistoryEntity> entity);

	/**
	 * 業務履歴を削除（1件）
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TGyomuHistoryEntity entity);

	/**
	 * 複数削除（物理）
	 *
	 * @param entity
	 * @return
	 */
	@BatchDelete
	int[] delete(List<TGyomuHistoryEntity> entity);

}
