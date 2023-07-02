package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.common.constant.AccountSettingConstant.SettingType;
import jp.loioz.entity.MAccountSettingEntity;

@ConfigAutowireable
@Dao
public interface MAccountSettingDao {

	/**
	 * 対象アカウントの設定情報を全て取得する
	 */
	@Select
	List<MAccountSettingEntity> selectByAccountSeq(Long accountSeq);
	
	/**
	 * 対象アカウントの、対象の設定項目情報を取得する
	 */
	@Select
	MAccountSettingEntity selectByAccountSeqAndSettingType(Long accountSeq, SettingType settingType);
	
	/**
	 * １件のアカウント設定情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MAccountSettingEntity entity);

	/**
	 * １件のアカウント設定情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MAccountSettingEntity entity);
}
