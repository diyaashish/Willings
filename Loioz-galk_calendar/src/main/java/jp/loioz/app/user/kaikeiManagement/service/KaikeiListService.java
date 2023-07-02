package jp.loioz.app.user.kaikeiManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListInputForm;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSeisanKirokuEntity;

/**
 * 会計管理画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KaikeiListService extends DefaultService {

	/** 共通：案件サービスクラス */
	@Autowired
	private CommonAnkenService commonAnkenService;

	/** 精算記録のDaoクラス */
	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	/** 案件Daoクラス **/
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 名簿Daoクラス **/
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 案件ステータス入力フォームを作成する
	 * 
	 * @param ankenId
	 * @param customerId
	 * @param transitionFlg
	 * @return
	 */
	public KaikeiListInputForm.AnkenStatusInputForm createAnkenStatusInputForm(Long ankenId, Long customerId, String transitionFlg) throws AppException {
		KaikeiListInputForm.AnkenStatusInputForm inputForm = new KaikeiListInputForm.AnkenStatusInputForm();

		// 画面表示のキーとなるパラメータ（表示用プロパティの設定より前に設定する）
		inputForm.setAnkenId(ankenId);
		inputForm.setCustomerId(customerId);
		inputForm.setTransitionFlg(transitionFlg);
		
		// 表示用プロパティを設定
		this.setDisplayProperties(inputForm);

		// 入力項目の設定
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, customerId);
		if (tAnkenCustomerEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		inputForm.setSeisanKanryoDate(DateUtils.parseToString(tAnkenCustomerEntity.getKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setCompleted(SystemFlg.codeToBoolean(tAnkenCustomerEntity.getKanryoFlg()));
		inputForm.setVersionNo(tAnkenCustomerEntity.getVersionNo()); // バージョンNoはここだけ厳密に判断する

		return inputForm;
	}

	/**
	 * 案件ステータス入力フォームの表示用プロパティを設定する
	 * 
	 * @param inputForm
	 */
	public void setDisplayProperties(KaikeiListInputForm.AnkenStatusInputForm inputForm) throws AppException {
		
		Long ankenId = inputForm.getAnkenId();
		Long customerId = inputForm.getCustomerId();
		boolean isTransitionAnken = inputForm.isTransitionAnken();
		
		if (isTransitionAnken) {
			// 案件軸の画面の場合
		
			// 操作対象の顧客名
			TPersonEntity tPersonEntity = this.getTPersonEntity(customerId);
			PersonName personName = PersonName.fromEntity(tPersonEntity);
			inputForm.setTargetCustomerName(personName.getName());
			
		} else {
			// 顧客軸の画面の場合
			
			// 操作対象の案件名
			TAnkenEntity ankenEntity = this.getTAnkenEntity(ankenId);
			inputForm.setTargetAnkenName(ankenEntity.getAnkenName());
		}
	}

	/**
	 * 案件ステータスの更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveAnkenStatus(KaikeiListInputForm.AnkenStatusInputForm inputForm) throws AppException {

		// 案件顧客情報を取得
		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(inputForm.getAnkenId(), inputForm.getCustomerId());

		// 取得できない場合、バージョンNoに差がある場合
		if (tAnkenCustomerEntity == null || !Objects.equal(tAnkenCustomerEntity.getVersionNo(), inputForm.getVersionNo())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 案件顧客情報に日付と完了チェックをセットする
		tAnkenCustomerEntity.setKanryoDate(DateUtils.parseToLocalDate(inputForm.getSeisanKanryoDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		tAnkenCustomerEntity.setKanryoFlg(SystemFlg.booleanToCode(inputForm.isCompleted()));

		// 設定した時の案件ステータスを取得・設定する
		String ankenStatusCd = commonAnkenService.getCurrentAnkenStatus(tAnkenCustomerEntity);
		tAnkenCustomerEntity.setAnkenStatus(ankenStatusCd);

		try {
			// 案件顧客情報の更新
			tAnkenCustomerDao.update(tAnkenCustomerEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	// ****************************************************************
	// 精算記録
	// ****************************************************************
	/**
	 * 摘要を更新します。
	 *
	 * @param seisanSeq 精算SEQ
	 * @param tekiyo 摘要
	 * @throws AppException
	 */
	public void updateSeisanTekiyo(Long seisanSeq, String tekiyo) throws AppException {

		// 更新対象の情報を取得します。
		TSeisanKirokuEntity seisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);

		// 摘要を設定します。
		seisanKirokuEntity.setTekiyo(tekiyo);

		try {
			// 更新します。
			tSeisanKirokuDao.update(seisanKirokuEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);
		}
	}

	// ****************************************************************
	// パラメータのバリデーション
	// ****************************************************************

	/**
	 * IDが正しいか（存在する名簿のものか）を検証する。<br>
	 * 対応するデータが存在しない場合はDataNotFoundExceptionをスロー
	 * 
	 * @param customerId
	 * @return
	 */
	public void validCustomerId(Long customerId) {

		this.getTPersonEntity(customerId);
	}

	/**
	 * IDが正しいか（存在する案件のものか）を検証する。<br>
	 * 対応するデータが存在しない場合はDataNotFoundExceptionをスロー
	 * 
	 * @param ankenId
	 */
	public void validAnkenId(Long ankenId) {

		this.getTAnkenEntity(ankenId);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * IDから名簿エンティティを取得する
	 * 
	 * @param customerId
	 * @return
	 * @throws IDに一致するデータが存在しない場合はDataNotFoundExceptionをスロー
	 */
	private TPersonEntity getTPersonEntity(Long customerId) {
		
		// 名簿情報を取得
		TPersonEntity entity = tPersonDao.selectById(customerId);

		// 顧客情報が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("名簿情報が存在しません。[customerId=" + customerId + "]");
		}
		
		return entity;
	}
	
	/**
	 * IDから案件エンティティを取得する
	 * 
	 * @param ankenId
	 * @return
	 * @throws IDに一致するデータが存在しない場合はDataNotFoundExceptionをスロー
	 */
	private TAnkenEntity getTAnkenEntity(Long ankenId) {

		// 案件情報を取得
		TAnkenEntity anken = tAnkenDao.selectById(ankenId);

		// 案件情報が存在しない場合
		if (anken == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}
		
		return anken;
	}
}
