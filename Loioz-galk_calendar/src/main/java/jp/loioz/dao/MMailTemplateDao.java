package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.common.constant.CommonConstant.MailTemplateType;
import jp.loioz.entity.MMailTemplateEntity;

/**
 * MMailTemplateDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MMailTemplateDao {

	/**
	 * 総件数を取得する
	 * 
	 * @return
	 */
	default long getTotalSize() {
		return selectAll().stream().count();
	}

	/**
	 * すべてのメールテンプレート情報を取得する
	 * 
	 * @return
	 */
	@Select
	List<MMailTemplateEntity> selectAll();

	/**
	 * 1件のメールテンプレート情報を取得する
	 * 
	 * @param mailTemplateSeq
	 * @return
	 */
	default MMailTemplateEntity selectBySeq(Long mailTemplateSeq) {
		return selectBySeq(Arrays.asList(mailTemplateSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 複数件のメールテンプレート情報を取得する
	 * 
	 * @return
	 */
	@Select
	List<MMailTemplateEntity> selectBySeq(List<Long> mailTemplateSeqList);

	/**
	 * テンプレート種別をキーとして、メールテンプレート情報を取得する
	 * 
	 * @param MailTemplateTypeのcd値
	 * @return
	 */
	@Select
	List<MMailTemplateEntity> selectByTemplateType(String mailTemplateType);

	/**
	 * テンプレート種別をキーとして、既定利用のメールテンプレートを取得する
	 * 
	 * @param mailTemplateType
	 * @return
	 */
	@Select
	MMailTemplateEntity selectDefaultUseMailTemplateByMailTemplateType(MailTemplateType mailTemplateType);

	/**
	 * 登録処理
	 *
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(MMailTemplateEntity entity);

	/**
	 * 更新処理
	 *
	 * @param entity
	 * @return
	 */
	@Update
	int update(MMailTemplateEntity entity);

	/**
	 * 複数データの更新処理
	 * 
	 * @param entity
	 * @return
	 */
	@BatchUpdate
	int[] update(List<MMailTemplateEntity> entity);

	/**
	 * 削除処理
	 * 
	 * @param entity
	 * @return
	 */
	@Delete
	int delete(MMailTemplateEntity entity);

}
