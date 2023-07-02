package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.CustomerKanyoshaPulldownDto;

/**
 * 顧客の共通Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TCustomerCommonDao {

	/**
	 * 案件に紐づく関与者を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<CustomerKanyoshaPulldownDto> selectKanyoshaPulldownByAnkenId(Long ankenId);
	
	/**
	 * 案件に紐づく顧客を取得する
	 *
	 * @param ankenId
	 * @return 顧客一覧
	 */
	@Select
	List<CustomerKanyoshaPulldownDto> selectCustomerByAnkenId(Long ankenId);

	/**
	 * 案件に紐づく顧客、関与者を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	@Select
	List<CustomerKanyoshaPulldownDto> selectCustomerKanyoshaPulldownByAnkenId(Long ankenId);
}