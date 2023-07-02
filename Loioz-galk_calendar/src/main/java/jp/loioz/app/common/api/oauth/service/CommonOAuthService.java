package jp.loioz.app.common.api.oauth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.api.box.form.BoxConnectDto;
import jp.loioz.app.common.api.box.service.CommonBoxApiService;
import jp.loioz.app.common.api.dropbox.form.DropboxConnectDto;
import jp.loioz.app.common.api.dropbox.service.CommonDropboxApiService;
import jp.loioz.app.common.api.google.form.GoogleConnectDto;
import jp.loioz.app.common.api.google.service.CommonGoogleApiService;
import jp.loioz.common.constant.CommonConstant.ExternalService;
import jp.loioz.common.constant.CommonConstant.ExternalServiceType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.UrlConstant;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TAuthTokenDao;
import jp.loioz.dao.TConnectedExternalServiceDao;
import jp.loioz.entity.TAuthTokenEntity;
import jp.loioz.entity.TConnectedExternalServiceEntity;

/**
 * アクセス認証情報の共通サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonOAuthService extends DefaultService {

	/** 共通サービス：Google */
	@Autowired
	private CommonGoogleApiService commonGoogleApiService;

	/** 共通サービス：Box */
	@Autowired
	private CommonBoxApiService commonBoxApiService;

	/** 共通サービス：Dropbox */
	@Autowired
	private CommonDropboxApiService commonDropboxApiService;

	/** 認証情報Dao */
	@Autowired
	private TAuthTokenDao tAuthTokenDao;

	/** 外部サービス連携情報Dao */
	@Autowired
	private TConnectedExternalServiceDao tConnectedExternalServiceDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 外部ストレージサービスを利用しているかどうか
	 * 
	 * @return
	 */
	public boolean useExternalStorage() {
		List<ExternalService> storage = this.getConnectedExternalService(ExternalServiceType.STORAGE);
		if (CollectionUtils.isEmpty(storage)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * DBからアクセストークンを取得する
	 * 
	 * @return
	 */
	public List<TAuthTokenEntity> getMyConnectedExternalService() {
		return getConnectedExternalService(SessionUtils.getLoginAccountSeq());
	}

	/**
	 * DBからアクセストークンを取得する
	 * 
	 * @return
	 */
	public List<TAuthTokenEntity> getConnectedExternalService(Long accountSeq) {
		return tAuthTokenDao.selectByAccountSeq(accountSeq);
	}

	/**
	 * DBからアクセストークンを取得する
	 * 
	 * @param externalService
	 * @return
	 */
	public TAuthTokenEntity getMyAuthToken(ExternalService externalService) {
		return this.getAuthToken(SessionUtils.getLoginAccountSeq(), externalService);
	}

	/**
	 * DBからアクセストークンを取得する
	 * 
	 * <p>
	 * ※ 注意<br>
	 * 引数に指定すれば、他人のアクセストークンを操作することになる。
	 * 基本的には使用せず、SessionUtilsからアカウントIDが取得出来ないケースなどのみで利用すること。
	 * 
	 * @param externalService
	 * @param accountSeq
	 * @return
	 */
	public TAuthTokenEntity getAuthToken(ExternalService externalService, Long accountSeq) {
		return this.getAuthToken(accountSeq, externalService);
	}

	/**
	 * アクセストークン情報の取得処理
	 * 
	 * @param accountSeq
	 * @param externalService
	 * @return
	 */
	private TAuthTokenEntity getAuthToken(Long accountSeq, ExternalService externalService) {
		return tAuthTokenDao.selectByAccountSeqAndExternalServiceId(accountSeq, externalService.getCd());
	}

	/**
	 * DBからアクセストークンを取得する
	 * 
	 * @param externalService
	 * @return
	 */
	public List<TAuthTokenEntity> getExternalServiceAuthToken(ExternalService externalService) {
		return tAuthTokenDao.selectByExternalServiceId(externalService.getCd());
	}

	/**
	 * 接続中のサービスを取得する
	 * 
	 * @return
	 */
	public List<ExternalService> getCanConnectExternalServiceList() {
		List<TConnectedExternalServiceEntity> tConnectedExternalServiceEntities = tConnectedExternalServiceDao.selectAll();
		Set<ExternalService> canConncetedExternalService = tConnectedExternalServiceEntities.stream()
				.map(e -> ExternalService.of(e.getExternalServiceId()))
				.collect(Collectors.toSet());
		return new ArrayList<>(canConncetedExternalService);
	}

	/**
	 * 接続中のサービスを取得する
	 * 
	 * @param externalServiceType
	 * @return
	 */
	public List<ExternalService> getConnectedExternalService(ExternalServiceType externalServiceType) {
		List<TConnectedExternalServiceEntity> tConnectedExternalServiceEntities = tConnectedExternalServiceDao.selectByServiceType(externalServiceType.getCd());
		Set<ExternalService> conncetedExternalService = tConnectedExternalServiceEntities.stream()
				.map(e -> ExternalService.of(e.getExternalServiceId()))
				.collect(Collectors.toSet());
		return new ArrayList<>(conncetedExternalService);
	}

	/**
	 * Entityにアクセストークンのリフレッシュ
	 * 
	 * @param entity
	 * @throws AppException
	 */
	public void refresh(ExternalService externalService) throws AppException {

		TAuthTokenEntity tAuthTokenEntity = tAuthTokenDao.selectByAccountSeqAndExternalServiceId(SessionUtils.getLoginAccountSeq(), externalService.getCd());
		if (tAuthTokenEntity == null) {
			throw new AppException(MessageEnum.MSG_E00141, null, "/" + UrlConstant.MY_EXTERNAL_SETTING_URL + "/");
		}

		// 共通リフレッシュ処理
		this.refreshFn(externalService, tAuthTokenEntity);
	}

	/**
	 * アクセストークンのリフレッシュ処理
	 * 
	 * <p>
	 * ※ 注意<br>
	 * 引数に指定すれば、他人のアクセストークンを操作することになる。
	 * 基本的には使用せず、SessionUtilsからアカウントIDが取得出来ないケースなどのみで利用すること。
	 * </p>
	 * 
	 * @param externalService
	 * @param accountSeq
	 * @throws AppException
	 */
	public void refresh(ExternalService externalService, Long accountSeq) throws AppException {
		TAuthTokenEntity tAuthTokenEntity = tAuthTokenDao.selectByAccountSeqAndExternalServiceId(accountSeq, externalService.getCd());
		if (tAuthTokenEntity == null) {
			// プラン画面でこのメソッドを呼ぶ場合、メッセージに含まれるURLを、テナント側のコンテキストパスで設定する必要がある。
			// 上記のようにメソッドの呼び出し側で設定するべきURLの値が変わってくるため、ここでの設定は行わない。
			throw new AppException(MessageEnum.MSG_E00141, null);
		}

		// 共通リフレッシュ処理
		this.refreshFn(externalService, tAuthTokenEntity);
	}

	/**
	 * リフレッシュ処理
	 * 
	 * @param externalService
	 * @param tAuthTokenEntity
	 * @throws AppException
	 */
	private void refreshFn(ExternalService externalService, TAuthTokenEntity tAuthTokenEntity) throws AppException {

		boolean needRefresh = false;
		switch (externalService) {
		case GOOGLE:
			GoogleConnectDto googleConnectDto = commonGoogleApiService.refreshToken(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
			if (googleConnectDto.isRefreshed()) {
				needRefresh = true;
				tAuthTokenEntity.setAccessToken(googleConnectDto.getAccessToken());
				tAuthTokenEntity.setRefreshToken(googleConnectDto.getRefreshToken());
			}
			break;
		case BOX:
			BoxConnectDto boxConnectDto = commonBoxApiService.refresh(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
			if (boxConnectDto.isRefreshed()) {
				needRefresh = true;
				tAuthTokenEntity.setAccessToken(boxConnectDto.getAccessToken());
				tAuthTokenEntity.setRefreshToken(boxConnectDto.getRefreshToken());
			}
			break;
		case DROPBOX:
			DropboxConnectDto dropboxConnectDto = commonDropboxApiService.refreshToken(tAuthTokenEntity.getAccessToken(), tAuthTokenEntity.getRefreshToken());
			if (dropboxConnectDto.isRefreshed()) {
				needRefresh = true;
				tAuthTokenEntity.setAccessToken(dropboxConnectDto.getAccessToken());
				tAuthTokenEntity.setRefreshToken(dropboxConnectDto.getRefreshToken());
			}
			break;
		default:
		}

		try {
			if (needRefresh) {
				this.update(tAuthTokenEntity);
			}
		} catch (OptimisticLockingFailureException e) {
			logger.warn("楽観的排他エラー", e);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * アクセス認証情報の登録処理
	 * 
	 * @param entity
	 * @return
	 */
	public int insert(TAuthTokenEntity entity) {

		// 一人のユーザーは一つのサービスに１つまでしかトークンを保存出来ないようにする
		TAuthTokenEntity deleteAuthTokenEntity = tAuthTokenDao.selectByAccountSeqAndExternalServiceId(entity.getAccountSeq(), entity.getExternalServiceId());
		if (deleteAuthTokenEntity != null) {
			this.delete(deleteAuthTokenEntity);
		}

		return tAuthTokenDao.insert(entity);
	}

	/**
	 * アクセス認証情報の更新処理(リフレッシュ時のみ利用すること)
	 * 
	 * @param entity
	 * @return
	 */
	public int update(TAuthTokenEntity entity) {
		return tAuthTokenDao.update(entity);
	}

	/**
	 * アクセス認証情報の削除処理
	 * 
	 * @param entity
	 * @return
	 */
	public int delete(TAuthTokenEntity entity) {

		int count = tAuthTokenDao.delete(entity);

		ExternalService service = ExternalService.of(entity.getExternalServiceId());
		try {
			switch (service) {
			case GOOGLE:
				commonGoogleApiService.revokeToken(entity.getAccessToken());
				break;
			case BOX:
				// リフレッシュトークンを失効する(アクセストークンの有効期限は60分なので、無視しても問題ないと判断)
				commonBoxApiService.revokeRefreshToken(entity.getRefreshToken());
				break;
			case DROPBOX:
				commonDropboxApiService.revokeToken(entity.getAccessToken());
				break;
			default:
			}
		} catch (AppException e) {
			// すでに無効になっているトークンの削除もここで行うので、握りつぶす
		}

		return count;
	}

	/**
	 * アクセス認証情報の削除処理
	 * 
	 * ※複数件の削除はトークンの失効処理を行わない(APIの処理数/秒の対応が困難なため)
	 * 
	 * @param entity
	 * @return
	 */
	public int[] delete(List<TAuthTokenEntity> entity) {
		return tAuthTokenDao.delete(entity);
	}

}
