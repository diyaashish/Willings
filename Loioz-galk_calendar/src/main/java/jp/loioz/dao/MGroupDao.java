package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.app.user.groupManagement.form.GroupSearchForm;
import jp.loioz.entity.MGroupEntity;

/**
 * グループ用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MGroupDao {

	/**
	 * 全てのグループ情報を取得する
	 *
	 * @return グループ情報
	 */
	@Select
	List<MGroupEntity> selectAll();

	/**
	 * 1件のグループ情報を取得する
	 *
	 * @param groupId グループID
	 * @return グループ情報
	 */
	default MGroupEntity selectById(Long groupId) {
		return selectById(Arrays.asList(groupId)).stream().findFirst().orElse(null);
	}

	/**
	 * グループIDを指定してグループ情報を取得する
	 *
	 * @param groupIdList グループID
	 * @return グループ情報
	 */
	@Select
	List<MGroupEntity> selectById(List<Long> groupIdList);

	/**
	 * グループのリストを取得する
	 *
	 * @param searchForm
	 * @param options
	 * @return
	 */
	@Select
	List<MGroupEntity> selectConditions(GroupSearchForm searchForm, SelectOptions options);

	/**
	 * グループIDリストを取得する
	 *
	 * @param groupId
	 * @return
	 */
	@Select
	List<MGroupEntity> selectByDispOrder(Long groupId, Long dispOrder);

	/**
	 * １件のグループ情報を登録する
	 *
	 * @param MGroupEntity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MGroupEntity mGroupEntity);

	/**
	 * １件のグループ情報を登録する
	 *
	 * @param MGroupEntity
	 * @return 登録したデータ件数
	 */
	@Update
	int update(MGroupEntity mGroupEntity);
}