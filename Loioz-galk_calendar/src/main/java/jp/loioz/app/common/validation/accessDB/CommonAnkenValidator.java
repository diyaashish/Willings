package jp.loioz.app.common.validation.accessDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MSosakikanDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.entity.MSosakikanEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TKanyoshaEntity;

/**
 * 案件のDB整合性バリデータークラス
 */
@Component
public class CommonAnkenValidator {

	@Autowired
	private TAnkenDao tAnkenDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 捜査機関Daoクラス */
	@Autowired
	private MSosakikanDao mSosakikanDao;

	/**
	 * 案件の存在チェック
	 * 
	 * @param ankenId
	 * @return
	 */
	public boolean existsAnken(Long ankenId) {

		TAnkenEntity anken = tAnkenDao.selectByAnkenId(ankenId);
		if (anken == null) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 案件と関与者が紐付いているかを検証します
	 * 
	 * @param ankenId
	 * @param kanyoshaSeq
	 * @return
	 */
	public boolean isValidRelatingKanyosha(Long ankenId, Long kanyoshaSeq) {

		// NULL の場合は検証しない
		if (CommonUtils.anyNull(ankenId, kanyoshaSeq)) {
			return true;
		}

		// データの取得
		TKanyoshaEntity tKanyoshaEntity = tKanyoshaDao.selectAnkenKanyoshaByParams(kanyoshaSeq, ankenId);

		if (tKanyoshaEntity == null) {
			// データの取得ができなかった場合は、エラー
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 捜査機関の存在チェック
	 * 
	 * @param sosakikanIdID
	 * @return 検証結果
	 */
	public boolean isSosakikanExistsValid(Long sosakikanId) {

		// IDがnull -> 検証しない
		if (sosakikanId == null) {
			return true;
		}

		MSosakikanEntity entity = mSosakikanDao.selectById(sosakikanId);
		if (entity == null) {
			// 無効なID
			return false;
		} else {
			return true;
		}
	}

}
