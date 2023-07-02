package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.MBunyaEntity;

/**
 * 分野マスタ用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MBunyaDao {

	
	/**
	 * 分野マスタの全件取得。
	 *
	 * @return 
	 */
	@Select
	List<MBunyaEntity> selectAll();

	/**
	 * 分野IDを指定して分野情報を1件取得します。
	 *
	 * @return 
	 */
	@Select
	MBunyaEntity selectById(Long bunyaId);

	/**
	 * 分野情報の表示順（有効データのみ）の最大値を1件取得します。
	 *
	 * @return 
	 */
	@Select
	long maxDisp();

	/**
	 * 分野名を指定して分野情報を取得します。
	 * 
	 * @return 
	 */
	@Select
	List<MBunyaEntity> selectByName(String bunyaName);
	
	
	/**
	 * 対象の分野IDを除く分野名を指定して分野情報を取得します。
	 * 
	 * @return 
	 */
	@Select
	List<MBunyaEntity> selectByNotIdAndName(Long bunyaId, String bunyaName);
	
	/**
	 * １件の分野情報を登録します。
	 *
	 * @param MBunyaEntity 分野マスタを管理するEntity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MBunyaEntity entity);

	/**
	 * １件の分野情報を更新します。
	 *
	 * @param MBunyaEntity 分野マスタを管理するEntity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MBunyaEntity entity);

	/**
	 * 複数の分野情報を更新します。
	 *
	 * @param MBunyaEntity 分野マスタを管理するEntity
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<MBunyaEntity> entities);
}
