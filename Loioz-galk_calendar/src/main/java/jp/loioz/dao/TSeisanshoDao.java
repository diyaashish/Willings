package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.ExcelJippiMeisaiShukkinListBean;
import jp.loioz.dto.KaikeiKirokuBean;
import jp.loioz.dto.SeisanshoPoolAnkenBean;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TSeisanKirokuEntity;

/**
 * 精算書作成情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSeisanshoDao {

	/**
	 * 精算SEQをキーにして、案件情報を取得します。
	 * 
	 * @param seisanSeq 精算SEQ
	 * @return 案件情報
	 */
	@Select
	TSeisanKirokuEntity selectBySeisanSeq(Long seisanSeq);

	/**
	 * 検索条件を元に会計記録情報を取得します。
	 * 
	 * @param seisanCustomerId
	 * @param seisanAnkenId
	 * @return
	 */
	@Select
	List<KaikeiKirokuBean> selectByCustomerIdAnkenId(Long seisanCustomerId, Long seisanAnkenId);

	/**
	 * 検索条件を元に会計記録情報を取得します。<br>
	 * 
	 * <pre>
	 * 精算書編集時用です。
	 * </pre>
	 * 
	 * @param searchForm 検索条件
	 * @return 会計記録情報
	 */
	@Select
	List<KaikeiKirokuBean> selectEditBySearchCondition(Long ankenId, Long customerId);

	/**
	 * 会計記録SEQを元に会計記録情報を取得します。
	 * 
	 * @param searchForm 検索条件
	 * @return 会計記録情報
	 */
	@Select
	List<KaikeiKirokuBean> selectByKaikeiKirokuSeq(List<Long> kaikeiKirokuSeq);

	/**
	 * 精算IDと案件IDを元に会計記録情報を取得します。
	 * 
	 * @param searchForm 検索条件
	 * @return 会計記録情報
	 */
	@Select
	List<TKaikeiKirokuEntity> selectKaikeiKirokuByIds(Long customerId, Long seisanSeq);

	/**
	 * 精算SEQを元に会計記録情報を取得します。
	 * 
	 * @param searchForm 検索条件
	 * @return 会計記録情報
	 */
	@Select
	List<KaikeiKirokuBean> selectKaikeiKirokuBeanBySeisanSeq(Long seisanSeq);

	/**
	 * 精算書帳票出力
	 * 
	 * <pre>
	 * 実費明細シート内の一覧情報を取得します
	 * </pre>
	 * 
	 * @param kaikeiSeqList
	 * @return ExcelJippiMeisaiShukkinListBean
	 */
	@Select
	List<ExcelJippiMeisaiShukkinListBean> selectExcelJippiMeisaiShukkinList(List<Long> kaikeiSeqList);

	/**
	 * 他案件へプールした精算書のプール先案件情報取得
	 * 
	 * <pre>
	 * 他案件へプールした精算書のプール先案件情報取得します。
	 * </pre>
	 * 
	 * @param seisanSeq
	 * @return SeisanshoPoolAnkenBean
	 */
	@Select
	SeisanshoPoolAnkenBean selectSeisanshoPoolAnkenDetail(Long seisanSeq);

	/**
	 * 枝番の最大値を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 枝番
	 */
	@Select
	long getMaxBranchNo(Long customerId);

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
