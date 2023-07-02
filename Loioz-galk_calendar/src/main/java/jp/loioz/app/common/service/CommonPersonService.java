package jp.loioz.app.common.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.TAdvisorContractDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TPersonContactDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TPersonContactEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 名簿情報関連の共通サービス処理
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonPersonService extends DefaultService {

	/** 案件顧客 */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 顧問契約用のDaoクラス */
	@Autowired
	private TAdvisorContractDao tAdvisorContractDao;

	/** 名簿情報用のDaoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 名簿-連絡先情報のDaoクラス */
	@Autowired
	private TPersonContactDao tPersonContactDao;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 案件に紐づく顧客を追加上限チェック
	 *
	 * @param ankenId
	 * @return
	 */
	public boolean isCustomerAdd(Long ankenId) {
		boolean error = false;
		// 登録上限チェック
		List<TAnkenCustomerEntity> tAnkenEntityList = tAnkenCustomerDao.selectByAnkenId(ankenId);
		if (tAnkenEntityList.size() >= CommonConstant.CUSTOMER_ADD_LIMIT) {
			error = true;
		}
		return error;
	}

	/**
	 * 名簿IDから優先電話番号を取得する
	 * 
	 * @param personId
	 * @return
	 */
	public String getYusenTelNo(Long personId) {
		return getYusenTelNo(tPersonContactDao.selectPersonContactByPersonId(personId));
	}

	/**
	 * 名簿-連絡情報から、優先電話番号を取得する
	 * 
	 * @param tPersonContactEntities 名簿-連絡先Entity
	 * @return 優先電話番号
	 */
	public String getYusenTelNo(List<TPersonContactEntity> tPersonContactEntities) {
		if (CollectionUtils.isEmpty(tPersonContactEntities)) {
			return "";
		}
		Optional<TPersonContactEntity> yusenTelEntity = tPersonContactEntities.stream()
				.filter(entity -> Objects.nonNull(entity.getTelNo()) && SystemFlg.codeToBoolean(entity.getYusenTelFlg()))
				.findFirst();
		return yusenTelEntity.orElse(tPersonContactEntities.stream().filter(entity -> Objects.nonNull(entity.getTelNo())).findFirst().orElse(new TPersonContactEntity())).getTelNo();
	}

	/**
	 * 名簿IDから優先Fax番号を取得する
	 * 
	 * @param personId
	 * @return
	 */
	public String getYusenFaxNo(Long personId) {
		return getYusenFaxNo(tPersonContactDao.selectPersonContactByPersonId(personId));
	}

	/**
	 * 名簿-連絡情報から、優先Fax番号を取得する
	 * 
	 * @param tPersonContactEntities 名簿-連絡先Entity
	 * @return 優先Fax番号
	 */
	public String getYusenFaxNo(List<TPersonContactEntity> tPersonContactEntities) {
		if (CollectionUtils.isEmpty(tPersonContactEntities)) {
			return "";
		}
		Optional<TPersonContactEntity> yusenFaxEntity = tPersonContactEntities.stream()
				.filter(entity -> Objects.nonNull(entity.getFaxNo()) && SystemFlg.codeToBoolean(entity.getYusenFaxFlg()))
				.findFirst();
		return yusenFaxEntity.orElse(tPersonContactEntities.stream().filter(entity -> Objects.nonNull(entity.getFaxNo())).findFirst().orElse(new TPersonContactEntity())).getFaxNo();
	}

	/**
	 * 名簿IDから優先メールアドレスを取得する
	 * 
	 * @param personId
	 * @return
	 */
	public String getYusenMailAddress(Long personId) {
		return getYusenMailAddress(tPersonContactDao.selectPersonContactByPersonId(personId));
	}

	/**
	 * 名簿-連絡情報から、優先メールアドレスを取得する
	 * 
	 * @param tPersonContactEntities 名簿-連絡先Entity
	 * @return 優先メールアドレス
	 */
	public String getYusenMailAddress(List<TPersonContactEntity> tPersonContactEntities) {
		if (CollectionUtils.isEmpty(tPersonContactEntities)) {
			return "";
		}
		Optional<TPersonContactEntity> yusenMailAddressEntity = tPersonContactEntities.stream()
				.filter(entity -> Objects.nonNull(entity.getMailAddress()) && SystemFlg.codeToBoolean(entity.getYusenMailAddressFlg()))
				.findFirst();
		return yusenMailAddressEntity.orElse(tPersonContactEntities.stream().filter(entity -> Objects.nonNull(entity.getMailAddress())).findFirst().orElse(new TPersonContactEntity())).getMailAddress();
	}

	/**
	 * 名簿の属性情報Flgの状態を最新状態に更新する
	 * 
	 * <pre>
	 * t_anken_customerのinsert,delete、
	 * t_advisor_contractのinsert,deleteとステータスの更新を行った場合に、このメソッドを実行する。
	 * （名簿の属性情報Flgの状態が変わる（変える必要がある）ケースが現状では上記のため。）
	 * </pre>
	 * 
	 * <pre>
	 * 名簿の属性情報Flgは現在は下記の2つ
	 * ・t_person.customer_flg
	 * ・t_person.advisor_flg
	 * 
	 * このメソッドは、上記のFlgの値を、現在の登録されているデータの状態をもとに更新する。
	 * 更新の判定に利用されるデータと、更新ルールは下記。
	 * ・t_anken_customer　　対象の名簿のレコードが存在する場合はcustomer_flg=1に更新
	 * ・t_advisor_contract　対象の名簿のレコードが存在し、かつステータス（enum ContractStatus の値）が「新規」か「進行中」の場合はadvisor_flg=1,customer_flg=1に更新
	 * 
	 * ※advisor_flg=1になるとき（対象の名簿に有効な顧問契約が存在する場合）、その名簿は「顧客」としても扱うため、customer_flg=1の更新も行う
	 * ※flgを「1」にする条件のいずれにも一致しない場合は「0」で更新する（対象名簿を、「顧客」や「顧問」の状態ではなくす）
	 * </pre>
	 * 
	 * @param personId
	 * @return
	 * @throws AppException
	 */
	public void updatePersonAttributeFlgs(Long personId) throws AppException {

		// 対象名簿が「顧問」状態かどうか
		boolean isAdvisor = this.isAdvisor(personId);
		String advisorFlg = SystemFlg.booleanToCode(isAdvisor);

		// 対象名簿が「顧客」状態かどうか
		boolean isCustomer = this.isCustomer(personId, isAdvisor);
		String customerFlg = SystemFlg.booleanToCode(isCustomer);

		// 現在登録されている属性情報Flg
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		String beforeUpdateAdvisorFlg = personEntity.getAdvisorFlg();
		String beforeUpdateCustomerFlg = personEntity.getCustomerFlg();

		if (!advisorFlg.equals(beforeUpdateAdvisorFlg) || !customerFlg.equals(beforeUpdateCustomerFlg)) {
			// 現在t_personに登録されている値と、現在のデータ状態での正しい属性情報Flgの値が異なっている場合
			// -> Flgの値を設定して更新

			personEntity.setAdvisorFlg(advisorFlg);
			personEntity.setCustomerFlg(customerFlg);

			this.updatePerson(personEntity);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 対象の名簿が「顧問」状態の名簿か判定
	 * 
	 * @param personId
	 * @return
	 */
	private boolean isAdvisor(Long personId) {

		long activeContractCount = tAdvisorContractDao.selectActiveContractCountByPersonId(personId);
		if (activeContractCount > 0) {
			// アクティブな顧問契約が1件以上ある場合
			return true;
		}

		return false;
	}

	/**
	 * 対象の名簿が「顧客」状態の名簿か判定
	 * 
	 * @param personId
	 * @return
	 */
	private boolean isCustomer(Long personId, boolean isAdvisor) {

		if (isAdvisor) {
			// 対象名簿が「顧問」の場合は、その時点で「顧客」として扱う
			return true;
		}

		long ankenCountAsCustomer = tAnkenCustomerDao.selectCountByCustomerId(personId);
		if (ankenCountAsCustomer > 0) {
			// 「顧客」として登録されている案件が1件以上ある場合
			return true;
		}

		// アクティブな顧問契約も、「顧客」として登録されている案件もない場合 -> false（対象名簿は「顧客」ではない）
		return false;
	}

	/**
	 * 名簿情報を更新する
	 * 
	 * @param updateEntity
	 * @throws AppException
	 */
	private void updatePerson(TPersonEntity updateEntity) throws AppException {

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tPersonDao.update(updateEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}
}
