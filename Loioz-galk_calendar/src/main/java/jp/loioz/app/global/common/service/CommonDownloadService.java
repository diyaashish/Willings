package jp.loioz.app.global.common.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.global.common.exception.GlobalAuthException;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.dao.TAccgDocDownloadDao;
import jp.loioz.entity.TAccgDocDownloadEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class CommonDownloadService extends DefaultService {

	/** テナントDB：ファイルダウンロードDao */
	@Autowired
	private TAccgDocDownloadDao tAccgDocDownloadDao;

	/**
	 * ダウンロード情報の取得
	 * 
	 * @param downloadUrlKey
	 * @return
	 */
	public TAccgDocDownloadEntity getAccgDocDownloadEntity(String downloadUrlKey) throws GlobalAuthException {
		TAccgDocDownloadEntity tAccgDocDownloadEntity = tAccgDocDownloadDao.selectAccgDocDownloadByDownloadUrlKey(downloadUrlKey);
		if (tAccgDocDownloadEntity == null) {
			// ここではloggerを表示しない
			throw new GlobalAuthException(MessageEnum.MSG_E00175, "キー情報からデータの取得ができませんでした。");
		}
		return tAccgDocDownloadEntity;
	}
	
	/**
	 * 現在日が、Entityが保持するダウンロード期限内かどうかを検証する。<br>
	 * 検証NGの場合にはGlobalAuthExceptionをスローする。
	 * 
	 * @param tAccgDocDownloadEntity ダウンロード情報Entity
	 * @param now 現在日
	 */
	public void validDownloadLimit(TAccgDocDownloadEntity tAccgDocDownloadEntity, LocalDate now) {
		
		LocalDate downloadLimit = tAccgDocDownloadEntity.getDownloadLimitDate();
		
		boolean isExpired = now.isAfter(downloadLimit);
		if (isExpired) {
			throw new GlobalAuthException(MessageEnum.MSG_E00193, "ダウンロードの有効期限が切れている。");
		}
	}

}
