package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.GinkoKozaBean;
import jp.loioz.entity.TGinkoKozaEntity;

/**
 * 銀行口座用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TGinkoKozaDao {

	/**
	 * テナント以外の口座情報をすべて取得します
	 *
	 * @return ユーザの口座情報
	 */
	@Select
	List<TGinkoKozaEntity> selectAccountKoza();

	/**
	 * 1件の銀行口座情報を取得する
	 *
	 * @param ginkoAccountSeq
	 * @return 銀行口座情報
	 */
	@Select
	TGinkoKozaEntity selectByGinkoAccountSeq(Long ginkoAccountSeq);

	/**
	 * テナント情報から削除されていない事務所用口座情報を複数件取得する
	 *
	 * @param tenantSeq
	 * @return 銀行口座情報
	 */
	@Select
	List<TGinkoKozaEntity> selectKozaListByTenantSeq(Long tenantSeq);

	/**
	 * 指定したアカウントSEQの口座情報を複数件取得する
	 *
	 * @param accountSeq
	 * @return 銀行口座情報
	 */
	@Select
	List<TGinkoKozaEntity> selectKozaListByAccountSeq(Long accountSeq);

	/**
	 * 指定したアカウントSEQの既定口座情報を取得する
	 * 
	 * @param accountSeq
	 * @return
	 */
	@Select
	TGinkoKozaEntity selectDefaultSalesAccountKozaByAccountSeq(Long accountSeq);

	/**
	 * 指定したアカウントSEQのBranchNoの最大値を取得します。
	 *
	 * @param accountSeq
	 * @return アカウント口座のBranchNoの最大値
	 */
	@Select
	long selectMaxAccountBranchNo(Long accountSeq);

	/**
	 * 事務所口座のBranchNoの最大値を取得します。
	 *
	 * @param tenantSeq
	 * @return テナント口座のBranchNoの最大値
	 */
	@Select
	long selectMaxTenantBranchNo(Long tenantSeq);

	/**
	 * 指定したSEQの口座情報を取得する
	 *
	 * @param accountSeqList
	 * @return 銀行口座情報
	 */
	@Select
	List<TGinkoKozaEntity> selectKozaListByAccountSeqList(List<Long> accountSeqList);

	/**
	 * 銀行口座SEQをキーとして、口座情報を取得します
	 *
	 * @param ginkoAccountSeq
	 * @return
	 */
	@Select
	GinkoKozaBean selectBeanBySeq(Long ginkoAccountSeq);

	/**
	 * 事務所口座の登録口座件数を取得します。
	 *
	 * @param accountSeq
	 * @return テナント口座の登録件数
	 */
	@Select
	int countKozaByTenantSeq(Long accountSeq);

	/**
	 * １件の銀行口座情報を登録する
	 *
	 * @param TGinkoKozaEntity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TGinkoKozaEntity entity);

	/**
	 * １件の銀行口座情報を更新する
	 *
	 * @param TGinkoKozaEntity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TGinkoKozaEntity entity);

	/**
	 * 複数件の銀行口座情報を更新する
	 *
	 * @param TGinkoKozaEntity
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TGinkoKozaEntity> entities);

}