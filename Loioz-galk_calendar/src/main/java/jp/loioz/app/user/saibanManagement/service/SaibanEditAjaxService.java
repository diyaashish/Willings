package jp.loioz.app.user.saibanManagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.saibanManagement.form.ajax.SaibanManagementKeizokuBu;
import jp.loioz.app.user.saibanManagement.form.ajax.SaibanManagementKensatsucho;
import jp.loioz.app.user.saibanManagement.form.ajax.SaibanManagementKensatsucho.Kensatsucho;
import jp.loioz.dao.MSaibanshoBuDao;
import jp.loioz.dao.MSosakikanDao;
import jp.loioz.entity.MSosakikanEntity;

/**
 * 裁判管理画面のAjax用サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanEditAjaxService extends DefaultService {

	@Autowired
	private MSaibanshoBuDao mSaibanshoBuDao;

	@Autowired
	private MSosakikanDao mSosakikanDao;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 係属部を取得する
	 *
	 * @param saibanshoId 裁判所ID
	 * @return 係属部
	 */
	public List<SaibanManagementKeizokuBu> getKeizokuBu(Long saibanshoId) {
		return mSaibanshoBuDao.selectBySaibanshoId(saibanshoId)
				.stream()
				.map(entity -> SaibanManagementKeizokuBu.builder()
						.name(entity.getKeizokuBuName())
						.telNo(entity.getKeizokuBuTelNo())
						.faxNo(entity.getKeizokuBuFaxNo())
						.build())
				.collect(Collectors.toList());
	}

	/**
	 * 検察庁の電話番号とFAX番号を取得します。
	 *
	 * @param kensatsuchoId 検察庁ID(施設ID)
	 * @return 検察庁の電話番号とFAX番号のリスト
	 */
	public SaibanManagementKensatsucho getKensatsuchoTelAndFaxNo(Long kensatsuchoId) {

		SaibanManagementKensatsucho kensatsuchoInfo = new SaibanManagementKensatsucho();
		Kensatsucho kensatsucho = new Kensatsucho();

		MSosakikanEntity entity = mSosakikanDao.selectById(kensatsuchoId);

		if (entity != null) {
			// 電話番号とFAX番号をリストにします。
			kensatsucho.setTelNo(entity.getSosakikanTelNo());
			kensatsucho.setFaxNo(entity.getSosakikanFaxNo());
			kensatsuchoInfo.setKensatsucho(kensatsucho);
		}

		return kensatsuchoInfo;
	}
}
