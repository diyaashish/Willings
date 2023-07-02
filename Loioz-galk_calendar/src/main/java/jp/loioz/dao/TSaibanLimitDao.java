package jp.loioz.dao;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.SaibanKeijiKijitsuBean;
import jp.loioz.bean.SaibanLimitBean;
import jp.loioz.entity.TSaibanLimitEntity;

/**
 * 裁判期日情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanLimitDao {

	/**
	 * 裁判期日SEQをキーにして裁判期日情報を取得する
	 *
	 * @param saibanLimitSeqList 裁判期日SEQ
	 * @return 裁判期日情報
	 */
	default TSaibanLimitEntity selectBySeq(Long saibanLimitSeq) {
		return selectBySeq(Arrays.asList(saibanLimitSeq)).stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * 裁判期日SEQをキーにして裁判期日情報を取得する
	 *
	 * @param saibanLimitSeqList 裁判期日SEQ
	 * @return 裁判期日情報
	 */
	@Select
	List<TSaibanLimitEntity> selectBySeq(List<Long> saibanLimitSeqList);

	/**
	 * 裁判SEQをキーにして裁判期日情報を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判期日情報
	 */
	@Select
	List<TSaibanLimitEntity> selectBySaibanSeq(Long saibanSeq);

	/**
	 * 裁判SEQと回数をキーにして裁判期日情報を取得する
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param count 回数
	 * @return 裁判期日情報
	 */
	@Select
	List<TSaibanLimitEntity> selectBySaibanSeqAndLimitDateCount(Long saibanSeq, Long count);

	/**
	 * 裁判SEQをキーにして裁判期日情報を取得する（親裁判用）
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param isAllTimeKijitsuList true：全ての時間の期日を取得候補とする false：本日以降の期日のみを取得候補とする（本日は含む）
	 * @param now 現在日時
	 * @param accountSEQ アカウントSEQ
	 * @return 裁判期日情報
	 */
	@Select
	List<SaibanLimitBean> selectMinjiKijitsuOfParentSaiban(Long saibanSeq, boolean isAllTimeKijitsuList, LocalDateTime now, Long accountSeq);

	/**
	 * 裁判SEQをキーにして裁判期日情報を全件取得する（子裁判用）<br>
	 * 親裁判の期日情報と重複するものは除外<br>
	 * 公開範囲が設定されている場合、担当者でないものは除外<br>
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param accountSeq アカウントSEQ
	 * @return 裁判期日情報
	 */
	@Select
	List<SaibanLimitBean> selectMinjiKijitsuBySaibanSeq(Long saibanSeq, Long accountSeq);

	/**
	 * 裁判SEQをキーにして裁判期日情報（刑事事件）を取得する
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param isAllTimeKijitsuList true：全ての時間の期日を取得候補とする false：本日以降の期日のみを取得候補とする（本日は含む）
	 * @param now 現在日時
	 * @param accountSeq アカウントSEQ
	 * @return 裁判期日情報
	 */
	@Select
	List<SaibanKeijiKijitsuBean> selectKeijiKijitsuBySaibanSeq(Long saibanSeq, boolean isAllTimeKijitsuList, LocalDateTime now, Long accountSeq);

	/**
	 * 回数の最大値を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 回数
	 */
	@Select
	long getMaxCount(Long saibanSeq);

	/**
	 * 1件の裁判期日情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TSaibanLimitEntity entity);

	/**
	 * 1件の裁判期日情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TSaibanLimitEntity entity);

	/**
	 * 1件の裁判期日情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TSaibanLimitEntity entity);

}
