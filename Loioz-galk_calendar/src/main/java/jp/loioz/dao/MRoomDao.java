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

import jp.loioz.app.user.roomManagement.form.RoomSearchForm;
import jp.loioz.entity.MRoomEntity;

/**
 * 会議室用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MRoomDao {

	/**
	 * 全ての会議室情報を取得する
	 *
	 * @return 会議室情報
	 */
	@Select
	List<MRoomEntity> selectAll();

	/**
	 * 有効な全ての会議室情報を取得する
	 *
	 * @return 会議室情報
	 */
	@Select
	List<MRoomEntity> selectEnabled();

	/**
	 * 1件の会議室情報を取得する
	 *
	 * @param roomId 会議室ID
	 * @return 会議室情報
	 */
	default MRoomEntity selectById(Long roomId) {
		return selectById(Arrays.asList(roomId)).stream().findFirst().orElse(null);
	}

	/**
	 * 会議室IDを指定して会議室情報を取得する
	 *
	 * @param roomIdList 会議室ID
	 * @return 会議室情報
	 */
	@Select
	List<MRoomEntity> selectById(List<Long> roomIdList);

	/**
	 * 会議室のリストを取得する
	 *
	 * @param searchForm
	 * @param options
	 * @return
	 */
	@Select
	List<MRoomEntity> selectConditions(RoomSearchForm searchForm, SelectOptions options);

	/**
	 * 会議室の表示順最大値を取得
	 * 
	 * @return
	 */
	@Select
	long maxDisp();

	/**
	 * 1件の会議室情報を登録する
	 *
	 * @param mRoomEntity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MRoomEntity mRoomEntity);

	/**
	 * 1件の会議室情報を更新する
	 *
	 * @param mRoomEntity
	 * @return 登録したデータ件数
	 */
	@Update
	int update(MRoomEntity mRoomEntity);

	/**
	 * 複数件の会議室情報を更新する
	 *
	 * @param mRoomEntityList
	 * @return 登録したデータ件数
	 */
	@BatchUpdate
	int[] update(List<MRoomEntity> mRoomEntityList);
}