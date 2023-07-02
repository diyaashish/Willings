package jp.loioz.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.InfoListForAccountDto;
import jp.loioz.entity.TInfoReadHistoryEntity;

/**
 * お知らせ既読履歴用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TInfoReadHistoryDao {

	/**
	 * 既読していないお知らせ情報を取得する
	 *
	 * @param accountSeq
	 * @param now
	 * @return お知らせ情報のリスト（未既読）
	 */
	@Select
	List<InfoListForAccountDto> selectUnread(Long accountSeq, LocalDateTime now);

	/**
	 * 登録済のお知らせ情報件数取得する
	 *
	 * @param accountSeq
	 * @param infoSeq
	 * @return お知らせ情報のリスト
	 */
	@Select
	int selectRegistCount(Long accountSeq, Long infoSeq);

	/**
	 * １件のお知らせ既読履歴情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TInfoReadHistoryEntity entity);

}
