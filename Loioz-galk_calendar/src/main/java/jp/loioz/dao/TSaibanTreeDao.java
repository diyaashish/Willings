package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.SaibanTreeJikenBean;
import jp.loioz.entity.TSaibanTreeEntity;

@ConfigAutowireable
@Dao
public interface TSaibanTreeDao {

	/**
	 * 裁判SEQをキーとして、裁判ツリー情報を取得します。
	 * 
	 * @param saibanSeq
	 * @return
	 */
	@Select
	TSaibanTreeEntity selectBySaibanSeq(Long saibanSeq);

	/**
	 * 裁判SEQをキーとして、子供の裁判情報を取得します。
	 * 
	 * @param parentSaibanSeq
	 * @return
	 */
	@Select
	List<TSaibanTreeEntity> selectChildBySaibanSeq(Long parentSaibanSeq);

	/**
	 * 親裁判SEQをキーとして、子裁判情報（事件情報含む）を取得します。
	 * 
	 * @param saibanSeq
	 * @return
	 */
	@Select
	List<SaibanTreeJikenBean> selectChildAndJikenBySaibanSeq(Long saibanSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TSaibanTreeEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return
	 */
	@Update
	int update(TSaibanTreeEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(TSaibanTreeEntity entity);

}
