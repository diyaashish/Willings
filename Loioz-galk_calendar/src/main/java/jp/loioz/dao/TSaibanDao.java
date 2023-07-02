package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AnkenRelatedParentSaibanBean;
import jp.loioz.bean.AnkenRelatedSaibanKeijiBean;
import jp.loioz.bean.SaibanAnkenBean;
import jp.loioz.bean.SaibanBasicBean;
import jp.loioz.bean.SaibanKeijiBasicBean;
import jp.loioz.bean.SaibanTojishaBean;
import jp.loioz.domain.value.SaibanId;
import jp.loioz.dto.KanyoshaTojishaDto;
import jp.loioz.dto.SaibanRelateJikenBean;
import jp.loioz.entity.TSaibanEntity;

/**
 * 裁判情報用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TSaibanDao {

	/**
	 * 裁判SEQをキーにして裁判情報を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判情報
	 */
	default TSaibanEntity selectBySeq(Long saibanSeq) {
		return selectBySeq(Arrays.asList(saibanSeq)).stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * 裁判SEQをキーにして裁判情報を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判情報
	 */
	@Select
	List<TSaibanEntity> selectBySeq(List<Long> saibanSeqList);

	/**
	 * 裁判SEQをキーとして、キーとした裁判の子裁判を取得する
	 *
	 * @param parentSaibanSeq
	 * @return
	 */
	@Select
	List<TSaibanEntity> selectByParentSaibanSeq(Long parentSaibanSeq);

	/**
	 * 裁判IDをキーにして裁判情報を取得する
	 *
	 * @param saibanId 裁判ID
	 * @return 裁判情報
	 */
	@Select
	TSaibanEntity selectBySaibanId(SaibanId saibanId);

	/**
	 * 裁判SEQをキーにして裁判情報（民事）を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判情報
	 */
	@Select
	SaibanBasicBean selectBySeqParentOnly(Long saibanSeq);

	/**
	 * 裁判SEQをキーにして裁判情報（刑事）を取得する
	 *
	 * @param saibanSeq 裁判SEQ
	 * @return 裁判情報
	 */
	@Select
	SaibanKeijiBasicBean selectBySeqParentOnlyKeiji(Long saibanSeq);

	/**
	 * 案件IDをキーにして裁判情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 裁判情報
	 */
	@Select
	List<TSaibanEntity> selectByAnkenId(Long ankenId);

	/**
	 * 裁判情報とその裁判に紐づく親事件情報を取得します。
	 */
	@Select
	List<AnkenRelatedParentSaibanBean> selectParentSaibanMinjiByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、裁判情報（刑事）とその裁判に紐づく親事件情報を取得します。<br>
	 * ※追起訴の子事件は含まない
	 */
	@Select
	List<AnkenRelatedSaibanKeijiBean> selectSaibanKeijiByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーとして、裁判情報とその裁判に紐づく親事件情報を取得します。<br>
	 * 業歴で使用
	 */
	@Select
	List<SaibanRelateJikenBean> selectParentJikenByAnkenId(Long ankenId);

	/**
	 * 案件IDリストをキーにして裁判情報を取得する
	 *
	 * @param ankenIdList 案件IDリスト
	 * @return 裁判情報
	 */
	@Select
	List<TSaibanEntity> selectByAnkenIdList(List<Long> ankenIdList);

	/**
	 * 案件IDと顧客IDをキーにして、裁判情報を取得する
	 *
	 * @param ankenId 案件ID
	 * @param customerId 顧客ID
	 * @return 裁判情報
	 */
	@Select
	List<TSaibanEntity> selectByAnkenIdAndCustomerId(Long ankenId, Long customerId);

	/**
	 * 裁判SEQをキーとして、紐づく当事者を取得します(帳票)
	 *
	 * @return
	 */
	@Select
	List<KanyoshaTojishaDto> selectTojishaForSaiban(Long saibanSeq);

	/**
	 * 複数の裁判SEQをキーとして、裁判当事者Beanリストを取得する
	 * 
	 * @param saibanSeqList
	 * @return 裁判当事者Beanリスト
	 */
	@Select
	List<SaibanTojishaBean> selectSaibanTojishaBeanBySaibanSeqList(List<Long> saibanSeqList);

	/**
	 * 枝番の最大値を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 枝番
	 */
	@Select
	long getMaxBranchNo(Long ankenId);

	/**
	 * 案件IDをキーにして裁判件数を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 裁判件数
	 */
	@Select
	long selectCountMinjiByAnkenId(Long ankenId);

	/**
	 * 案件IDをキーにして裁判件数（刑事）を取得する
	 *
	 * @param ankenId 案件ID
	 * @return 裁判件数
	 */
	@Select
	long selectCountKeijiByAnkenId(Long ankenId);

	/**
	 * 裁判連番をキーにして被告名を取得する(筆頭が先頭に来るように取得)
	 *
	 * @param saibanSeq 裁判連番
	 * @return 被告名
	 */
	@Select
	List<String> selectHikokuName(Long saibanSeq);

	/**
	 * 裁判SEQと案件分野を取得する
	 *
	 * @param ankenId
	 * @param branchNumber
	 * @return
	 */
	@Select
	SaibanAnkenBean selectByAnkenIdAndBranchNo(Long ankenId, Long branchNo);

	/**
	 * 1件の裁判情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TSaibanEntity entity);

	/**
	 * 1件の裁判情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TSaibanEntity entity);

	/**
	 * 1件の裁判情報を削除する
	 *
	 * @param entity
	 * @return 削除したデータ件数
	 */
	@Delete
	int delete(TSaibanEntity entity);

}