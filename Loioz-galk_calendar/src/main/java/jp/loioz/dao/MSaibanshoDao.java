package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.app.user.saibanshoManagement.form.SaibanshoSearchForm;
import jp.loioz.entity.MSaibanshoEntity;

@ConfigAutowireable
@Dao
public interface MSaibanshoDao {

	/**
	 * 裁判所データの取得
	 * ※ 論理削除を除く
	 *
	 * @param
	 * @return
	 */
	@Select
	List<MSaibanshoEntity> selectEnabled();

	/**
	 * 裁判所IDをキーにして裁判所情報を取得する
	 *
	 * @param saibanshoId 裁判所ID
	 * @return 裁判所情報
	 */
	@Select
	MSaibanshoEntity selectById(Long saibanshoId);

	/**
	 * 裁判所IDをキーにして裁判所情報を取得する(削除済も含める)
	 *
	 * @param saibanshoId 裁判所ID
	 * @return 裁判所情報
	 */
	@Select
	MSaibanshoEntity selectByIdIncludeDeleted(Long saibanshoId);

	/**
	 * 裁判所情報検索
	 *
	 * @param searchForm
	 * @param options
	 * @return
	 */
	@Select
	List<MSaibanshoEntity> selectConditions(SaibanshoSearchForm searchForm, SelectOptions options);

	/**
	 * 登録処理
	 *
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(MSaibanshoEntity entity);

	/**
	 * 更新処理
	 *
	 * @param entity
	 * @return
	 */
	@Update
	int update(MSaibanshoEntity entity);
}
