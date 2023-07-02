package jp.loioz.dao;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.domain.condition.DepositRecvMasterListSearchCondition;
import jp.loioz.entity.MDepositItemEntity;

/**
 * 預り金項目Daoクラス
 */
@ConfigAutowireable
@Dao
public interface MDepositItemDao {

	/**
	 * すべての預り金項目を取得
	 * 
	 * @return
	 */
	@Select
	List<MDepositItemEntity> selectAll();

	/**
	 * すべての預り金項目を表示順カラムに基づき、昇順で取得する
	 * 
	 * @return
	 */
	default List<MDepositItemEntity> selectAllOrderByDispOrder() {
		return selectAll().stream().sorted(Comparator.comparing(MDepositItemEntity::getDispOrder)).collect(Collectors.toList());
	}

	/**
	 * SEQをキーとして、預り金項目を取得
	 *
	 * @param depositItemSeq
	 * @return
	 */
	default MDepositItemEntity selectById(Long depositItemSeq) {
		return selectById(Arrays.asList(depositItemSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * SEQをキーとして、預り金項目をすべて取得
	 *
	 * @param depositItemSeqList
	 * @return
	 */
	@Select
	List<MDepositItemEntity> selectById(List<Long> depositItemSeqList);

	/**
	 * 検索ワードに項目名が一致する有効な預り金項目を取得
	 * 
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	@Select
	List<MDepositItemEntity> selectEnabledDepositItemBySearchWord(String searchWord, String depositType);

	/**
	 * 表示順の最大値を取得する
	 * 
	 * @return
	 */
	@Select
	long maxDisp();

	/**
	 * 預り金項目マスタ画面：一覧検索
	 *
	 * @param searchForm
	 * @param options
	 * @return
	 */
	@Select
	List<MDepositItemEntity> selectDepositRecvMasterListByConditions(DepositRecvMasterListSearchCondition conditions, SelectOptions options);

	/**
	 * 1件の預り金項目マスタデータを登録する
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(MDepositItemEntity entity);

	/**
	 * 1件の預り金項目マスタデータを更新する
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(MDepositItemEntity entity);

	/**
	 * 1件の預り金項目マスタデータを更新する
	 * 
	 * @param entity
	 * @return
	 */
	@BatchUpdate
	int[] update(List<MDepositItemEntity> entities);

	/**
	 * 1件の預り金項目マスタデータを削除する
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(MDepositItemEntity entity);

}
