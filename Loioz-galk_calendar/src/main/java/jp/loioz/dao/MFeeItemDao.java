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

import jp.loioz.entity.MFeeItemEntity;

/**
 * 報酬項目マスタ用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MFeeItemDao {

	/**
	 * すべての報酬項目を取得
	 * 
	 * @return
	 */
	@Select
	List<MFeeItemEntity> selectAll();

	/**
	 * すべての報酬項目を、表示順に従い、昇順で取得する
	 * 
	 * @return
	 */
	default List<MFeeItemEntity> selectAllOrderByDispOrder() {
		return selectAll().stream().sorted(Comparator.comparing(MFeeItemEntity::getDispOrder)).collect(Collectors.toList());
	}

	/**
	 * SEQをキーとして、有効な報酬項目を取得
	 * 
	 * @param feeItemSeq
	 * @return
	 */
	default MFeeItemEntity selectBySeq(Long feeItemSeq) {
		return selectBySeq(Arrays.asList(feeItemSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * SEQをキーとして、有効な報酬項目をすべて取得
	 * 
	 * @param feeItemSeqList
	 * @return
	 */
	@Select
	List<MFeeItemEntity> selectBySeq(List<Long> feeItemSeqList);

	/**
	 * 検索ワードをキーとして、有効な報酬項目を取得
	 * 
	 * @param searchWord
	 * @return
	 */
	@Select
	List<MFeeItemEntity> selectEnabledFeeItemBySearchWord(String searchWord);

	/**
	 * 有効表示順の最大値を取得する
	 * 
	 * @return
	 */
	@Select
	long maxDisp();

	/**
	 * １件の報酬項目情報を登録
	 *
	 * @param MBunyaEntity 報酬項目マスタを管理するEntity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MFeeItemEntity entity);

	/**
	 * １件の報酬項目情報を更新
	 *
	 * @param MBunyaEntity 報酬項目マスタを管理するEntity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MFeeItemEntity entity);

	/**
	 * 複数の報酬項目情報を更新
	 *
	 * @param MBunyaEntity 報酬項目マスタを管理するEntity
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<MFeeItemEntity> entities);

	/**
	 * １件の報酬項目を削除
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(MFeeItemEntity entity);

}
