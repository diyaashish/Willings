package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.entity.TToiawaseAttachmentEntity;

/**
 * 問い合わせ-添付Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TToiawaseAttachmentDao {

	/**
	 * 問い合わせ-添付SEQに紐づく情報を取得します
	 * 
	 * @param toiawaseAttachmentSeq
	 * @return
	 */
	default TToiawaseAttachmentEntity selectBySeq(Long toiawaseAttachmentSeq) {
		return selectBySeq(Arrays.asList(toiawaseAttachmentSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 問い合わせ-添付の情報を取得します
	 * 
	 * @param toiawaseAttachmentSeq
	 * @return
	 */
	@Select
	List<TToiawaseAttachmentEntity> selectBySeq(List<Long> toiawaseAttachmentSeq);

	/**
	 * 問い合わせSEQに紐づく情報を取得します
	 * 
	 * @param toiawaseSeq
	 * @return
	 */
	@Select
	List<TToiawaseAttachmentEntity> selectByToiawaseSeq(Long toiawaseSeq);

	/**
	 * 問い合わせ - 添付情報を１件登録します。
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TToiawaseAttachmentEntity entity);

}
