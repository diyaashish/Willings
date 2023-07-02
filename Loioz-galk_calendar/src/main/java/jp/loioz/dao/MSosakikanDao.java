package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.app.user.sosakikanManagement.form.SosakikanSearchForm;
import jp.loioz.entity.MSosakikanEntity;

/**
 * 捜査機関用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MSosakikanDao {

	/** 有効な捜査機関情報の取得 */
	@Select
	List<MSosakikanEntity> selectEnabled();

	/**
	 * 捜査機関情報の取得（全件）
	 *
	 * @return
	 */
	@Select
	List<MSosakikanEntity> selectAll();

	/**
	 * 捜査機関情報の取得（１件）
	 *
	 * @param id
	 * @return
	 */
	@Select
	MSosakikanEntity selectById(Long id);

	/**
	 * 削除されていない捜査機関情報の取得（１件）
	 *
	 * @param id
	 * @return
	 */
	@Select
	MSosakikanEntity selectNotDeletedById(Long id);

	/**
	 * 捜査機関情報の取得<br>
	 *
	 * <pre>
	 * 裁判(刑事弁護)画面 用です。
	 * 捜査機関区分が「検察庁」の捜査機関情報を取得します。
	 * </pre>
	 *
	 * @return 捜査機関情報リスト
	 */
	@Select
	List<MSosakikanEntity> selectKensatsu();

	/**
	 * 捜査機関情報の検索
	 *
	 * @param searchForm
	 * @param options
	 * @return
	 */
	@Select
	List<MSosakikanEntity> selectConditions(SosakikanSearchForm searchForm, SelectOptions options);

	/**
	 * 登録（１件）
	 *
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(MSosakikanEntity entity);

	/**
	 * 更新（1件）
	 *
	 * @param entity
	 * @return
	 */
	@Update
	int update(MSosakikanEntity entity);

}
