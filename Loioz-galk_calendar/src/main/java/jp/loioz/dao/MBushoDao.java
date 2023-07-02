package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.app.user.bushoManagement.form.BushoSearchForm;
import jp.loioz.bean.BushoListUserBean;
import jp.loioz.entity.MBushoEntity;

/**
 * 部署データ用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MBushoDao {

	/**
	 * 1件の部署情報を取得する
	 *
	 * @param bushoId 部署ID
	 * @return 部署情報
	 */
	default MBushoEntity selectById(Long bushoId) {
		return selectById(Arrays.asList(bushoId)).stream().findFirst().orElse(null);
	}

	/**
	 * 部署IDを指定して部署情報を取得する
	 *
	 * @param bushoIdList 部署ID
	 * @return 部署情報
	 */
	@Select
	List<MBushoEntity> selectById(List<Long> bushoIdList);

	/**
	 * 部署のリストを取得する
	 *
	 * @param searchForm
	 * @param options
	 * @return
	 */
	@Select
	List<BushoListUserBean> selectConditions(BushoSearchForm searchForm, SelectOptions options);

	/**
	 * 全ての部署情報を取得する
	 *
	 * @return 部署情報
	 */
	@Select
	List<MBushoEntity> selectAll();

	/**
	 * 表示順の最大を取得する ※ 有効なデータのみ対象とする
	 *
	 * @return 最大dispOrder
	 */
	@Select
	long maxDisp();

	/**
	 * 1件の部署データ情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MBushoEntity entity);

	/**
	 * 1件の部署データ情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MBushoEntity entity);

	/**
	 * 複数件の部署情報を更新する
	 *
	 * @param mBushoEntity
	 * @return 登録したデータ件数
	 */
	@BatchUpdate
	int[] update(List<MBushoEntity> mBushoEntity);
}