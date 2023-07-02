package jp.loioz.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.CustomerKanyoshaGinkoKozaBean;

/**
 * 会計管理用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface KaikeiManagementDao {

	/**
	 * 名簿の銀行口座情報取得
	 * 
	 * @param customerId 顧客ID
	 * @return 顧客、関与者銀行口座情報Bean
	 */
	@Select
	CustomerKanyoshaGinkoKozaBean selectPersonGinkoKozaDetail(Long customerId);

	/**
	 * 関与者の銀行口座情報取得
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	@Select
	CustomerKanyoshaGinkoKozaBean selectKanyoshaGinkoKozaDetailByKanyoshaSeq(Long kanyoshaSeq);
}