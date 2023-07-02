package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.bean.userHeader.UserHeaderSearchAnkenBean;
import jp.loioz.bean.userHeader.UserHeaderSearchPersonBean;
import jp.loioz.bean.userHeader.UserHeaderSearchSaibanBean;
import jp.loioz.domain.condition.userHeader.UserHeaderAnkenSearchCondition;
import jp.loioz.domain.condition.userHeader.UserHeaderPersonSearchCondition;
import jp.loioz.domain.condition.userHeader.UserHeaderSaibanSearchCondition;

/**
 * ヘッダー表示データ取得用Daoクラス
 */
@ConfigAutowireable
@Dao
public interface UserHeaderDao {

	/**
	 * ヘッダー検索：名簿一覧の検索結果件数を取得する
	 * 
	 * <pre>
	 * UserHeaderPersonSearchCondition内のisGetCountがtrueである必要がある
	 * </pre>
	 * 
	 * @param searchConditions
	 * @return
	 */
	default Long getPersonListCountBySearchConditions(UserHeaderPersonSearchCondition searchConditions) {
		if (!searchConditions.isGetCount()) {
			throw new RuntimeException();
		}
		List<Long> result = selectPersonIdBySearchConditions(searchConditions);
		return result.stream().findFirst().orElse(0L);
	}

	/**
	 * ページャ情報を指定しないヘッダー検索：名簿一覧
	 * <pre>
	 * 基本的に当メソッドをサービスクラスからは呼び出しを行わない
	 * 利用する場合
	 * {@link  jp.loioz.dao.UserHeaderDao#getPersonListCountBySearchConditions(UserHeaderPersonSearchCondition) getPersonListCountBySearchConditions(UserHeaderPersonSearchCondition) } などを利用する
	 * </pre>
	 * 
	 * @param searchConditions
	 * @return
	 */
	@Select
	List<Long> selectPersonIdBySearchConditions(UserHeaderPersonSearchCondition searchConditions);

	/**
	 * ヘッダー検索：名簿一覧の検索(ID)
	 * 
	 * @param searchConditions
	 * @param options
	 * @return
	 */
	@Select
	List<Long> selectPersonIdBySearchConditions(UserHeaderPersonSearchCondition searchConditions, SelectOptions options);

	/**
	 * ヘッダー検索：名簿一覧
	 * 
	 * @param personIdList
	 * @return
	 */
	@Select
	List<UserHeaderSearchPersonBean> selectPersonBeanByPersonIdList(List<Long> personIdList);

	/**
	 * ヘッダー検索：案件一覧の検索結果件数を取得する
	 * 
	 * <pre>
	 * UserHeaderAnkenSearchCondition内のisGetCountがtrueである必要がある
	 * </pre>
	 * 
	 * @param searchConditions
	 * @return
	 */
	default Long getAnkenListCountBySearchConditions(UserHeaderAnkenSearchCondition searchConditions) {
		if (!searchConditions.isGetCount()) {
			throw new RuntimeException();
		}
		List<Long> result = selectAnkenIdBySearchConditions(searchConditions);
		return result.stream().findFirst().orElse(0L);
	}

	/**
	 * ページャ情報を指定しないヘッダー検索：案件一覧
	 * <pre>
	 * 基本的に当メソッドをサービスクラスからは呼び出しを行わない
	 * 利用する場合
	 * {@link  jp.loioz.dao.UserHeaderDao#getAnkenListCountBySearchConditions(UserHeaderAnkenSearchCondition) getAnkenListCountBySearchConditions(UserHeaderAnkenSearchCondition) } などを利用する
	 * </pre>
	 * 
	 * @param searchConditions
	 * @return
	 */
	@Select
	List<Long> selectAnkenIdBySearchConditions(UserHeaderAnkenSearchCondition searchConditions);

	/**
	 * ヘッダー検索：案件一覧の検索(ID)
	 * 
	 * @param searchConditions
	 * @param options
	 * @return
	 */
	@Select
	List<Long> selectAnkenIdBySearchConditions(UserHeaderAnkenSearchCondition searchConditions, SelectOptions options);

	/**
	 * ヘッダー検索：案件一覧
	 * 
	 * @param ankenIdList
	 * @return
	 */
	@Select
	List<UserHeaderSearchAnkenBean> selectAnkenBeanByAnkenIdList(List<Long> ankenIdList);

	/**
	 * ヘッダー検索：裁判一覧の検索結果件数を取得する
	 * 
	 * <pre>
	 * UserHeaderSaibanSearchCondition内のisGetCountがtrueである必要がある
	 * </pre>
	 * 
	 * @param searchConditions
	 * @return
	 */
	default Long getSaibanListCountBySearchConditions(UserHeaderSaibanSearchCondition searchConditions) {
		if (!searchConditions.isGetCount()) {
			throw new RuntimeException();
		}
		List<Long> result = selectSaibanSeqBySearchConditions(searchConditions);
		return result.stream().findFirst().orElse(0L);
	}

	/**
	 * ページャ情報を指定しないヘッダー検索：裁判一覧
	 * <pre>
	 * 基本的に当メソッドをサービスクラスからは呼び出しを行わない
	 * 利用する場合
	 * {@link  jp.loioz.dao.UserHeaderDao#getSaibanListCountBySearchConditions(UserHeaderSaibanSearchCondition) getSaibanListCountBySearchConditions(UserHeaderSaibanSearchCondition) } などを利用する
	 * </pre>
	 * 
	 * @param searchConditions
	 * @return
	 */
	@Select
	List<Long> selectSaibanSeqBySearchConditions(UserHeaderSaibanSearchCondition searchConditions);

	/**
	 * ヘッダー検索：裁判一覧の検索(SEQ)
	 * 
	 * @param searchConditions
	 * @param options
	 * @return
	 */
	@Select
	List<Long> selectSaibanSeqBySearchConditions(UserHeaderSaibanSearchCondition searchConditions, SelectOptions options);

	/**
	 * ヘッダー検索：裁判一覧
	 * 
	 * @param saibanSeqList
	 * @return
	 */
	@Select
	List<UserHeaderSearchSaibanBean> selectSaibanBeanBySaibanSeqList(List<Long> saibanSeqList);

}
