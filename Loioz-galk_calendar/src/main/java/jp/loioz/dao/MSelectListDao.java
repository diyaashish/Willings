package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.app.user.selectListManagement.form.SelectSearchForm;
import jp.loioz.entity.MSelectListEntity;

/**
 * 選択肢マスタ用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MSelectListDao {

	/**
	 * 利用停止含め、登録されている全データを取得
	 * 
	 * @return
	 */
	@Select
	List<MSelectListEntity> selectAll();

	/**
	 * 選択肢SEQを取得します。
	 *
	 * @param searchCondition 選択肢検索条件
	 * @param options オプション
	 * @return 選択肢SEQ
	 */
	@Select
	List<MSelectListEntity> selectConditions(
			SelectSearchForm searchForm,
			SelectOptions options);

	/**
	 * 選択肢SEQをキーにして、選択肢情報を取得します。
	 *
	 * @param selectSeq 選択肢SEQ
	 * @return 選択肢情報
	 */
	@Select
	MSelectListEntity selectSelectListBySeq(Long selectSeq);

	/**
	 * 有効な選択肢情報を全て取得する
	 *
	 * @return 選択肢情報
	 */
	@Select
	List<MSelectListEntity> selectEnabled();

	/**
	 * 選択肢種別をキーとして、有効な選択肢情報を全て取得する
	 *
	 * @return 選択肢情報
	 */
	@Select
	List<MSelectListEntity> selectEnabledFilterByType(String type);

	/**
	 * 有効な選択肢情報を取得する(削除前に設定済の相談ルート、追加情報を含む)
	 *
	 * @return 選択肢情報
	 */
	@Select
	List<MSelectListEntity> selectEnabledIncludeDeleted(Long sodanRoute, Long addInfo);

	/**
	 * 選択肢の表示順最大値を取得
	 * 
	 * @return
	 */
	@Select
	long maxDisp();

	/**
	 * １件の選択肢情報を登録します。
	 *
	 * @param MSelectListEntity 選択肢マスタを管理するEntity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MSelectListEntity entity);

	/**
	 * １件の選択肢情報を更新します。
	 *
	 * @param MSelectListEntity 選択肢マスタを管理するEntity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MSelectListEntity entity);

	/**
	 * 複数の選択肢情報を更新します。
	 *
	 * @param MSelectListEntity 選択肢マスタを管理するEntity
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<MSelectListEntity> entities);
}
