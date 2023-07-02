package jp.loioz.app.common.validation.accessDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 顧客 DB整合性チェッククラス
 */
@Component
public class CommonCustomerValidator {

	@Autowired
	private TPersonDao tPersonDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/**
	 * 名簿情報の存在チェック
	 * 
	 * @param personId
	 * @return
	 */
	public boolean existsPerson(Long personId) {

		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (personEntity == null) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 顧客が案件に紐づくかを検証します
	 * 
	 * @param customerId
	 * @param ankenId
	 * @return
	 */
	public boolean isValidRelatingAnken(Long customerId, Long ankenId) {

		// NULLの場合は検証しない
		if (CommonUtils.anyNull(customerId, ankenId)) {
			return true;
		}

		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		if (tAnkenCustomerEntity == null) {
			return false;
		} else {
			return true;
		}

	}
}
