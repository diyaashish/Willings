package jp.loioz.app.user.personManagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TAccgDocDao;
import jp.loioz.dao.TAccgInvoiceDao;
import jp.loioz.dao.TAccgStatementDao;
import jp.loioz.dao.TAdvisorContractDao;
import jp.loioz.dao.TAnkenAddKeijiDao;
import jp.loioz.dao.TAnkenAzukariItemDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenJikenDao;
import jp.loioz.dao.TAnkenKoryuDao;
import jp.loioz.dao.TAnkenRelatedKanyoshaDao;
import jp.loioz.dao.TAnkenSekkenDao;
import jp.loioz.dao.TAnkenSosakikanDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.dao.TFeeDao;
import jp.loioz.dao.TFileConfigurationManagementDao;
import jp.loioz.dao.TFileDetailInfoManagementDao;
import jp.loioz.dao.TFolderPermissionInfoManagementDao;
import jp.loioz.dao.TGyomuHistoryAnkenDao;
import jp.loioz.dao.TGyomuHistoryCustomerDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TKeijiAnkenCustomerDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TOldAddressDao;
import jp.loioz.dao.TPersonAddHojinDao;
import jp.loioz.dao.TPersonAddKojinDao;
import jp.loioz.dao.TPersonAddLawyerDao;
import jp.loioz.dao.TPersonContactDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TRootFolderRelatedInfoManagementDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanRelatedKanyoshaDao;
import jp.loioz.dao.TSalesDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.dao.TTaskDao;
import jp.loioz.dao.TTaskHistoryDao;
import jp.loioz.dao.TTaskWorkerDao;
import jp.loioz.dao.TUncollectibleDao;
import jp.loioz.entity.TAccgDocEntity;
import jp.loioz.entity.TAdvisorContractEntity;
import jp.loioz.entity.TAnkenAddKeijiEntity;
import jp.loioz.entity.TAnkenAzukariItemEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TAnkenJikenEntity;
import jp.loioz.entity.TAnkenKoryuEntity;
import jp.loioz.entity.TAnkenRelatedKanyoshaEntity;
import jp.loioz.entity.TAnkenSekkenEntity;
import jp.loioz.entity.TAnkenSosakikanEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TDepositRecvEntity;
import jp.loioz.entity.TFeeEntity;
import jp.loioz.entity.TFileConfigurationManagementEntity;
import jp.loioz.entity.TFileDetailInfoManagementEntity;
import jp.loioz.entity.TFolderPermissionInfoManagementEntity;
import jp.loioz.entity.TGyomuHistoryAnkenEntity;
import jp.loioz.entity.TGyomuHistoryCustomerEntity;
import jp.loioz.entity.TGyomuHistoryEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TKeijiAnkenCustomerEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TOldAddressEntity;
import jp.loioz.entity.TPersonAddHojinEntity;
import jp.loioz.entity.TPersonAddKojinEntity;
import jp.loioz.entity.TPersonAddLawyerEntity;
import jp.loioz.entity.TPersonContactEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TRootFolderRelatedInfoManagementEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanRelatedKanyoshaEntity;
import jp.loioz.entity.TSalesEntity;
import jp.loioz.entity.TScheduleEntity;
import jp.loioz.entity.TSeisanKirokuEntity;
import jp.loioz.entity.TTaskEntity;
import jp.loioz.entity.TTaskHistoryEntity;
import jp.loioz.entity.TTaskWorkerEntity;
import jp.loioz.entity.TUncollectibleEntity;

/**
 * 名簿削除画面のserviceクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PersonDeleteService extends DefaultService {

	@Autowired
	private TPersonDao tPersonDao;

	@Autowired
	private TPersonContactDao tPersonContactDao;

	@Autowired
	private TPersonAddKojinDao tPersonAddKojinDao;

	@Autowired
	private TPersonAddHojinDao tPersonAddHojinDao;

	@Autowired
	private TOldAddressDao tOldAddressDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private TSaibanDao tSaibanDao;

	@Autowired
	private TScheduleDao tScheduleDao;

	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	@Autowired
	private TGyomuHistoryCustomerDao tGyomuHistoryCustomerDao;

	@Autowired
	private TGyomuHistoryAnkenDao tGyomuHistoryAnkenDao;

	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	@Autowired
	private TRootFolderRelatedInfoManagementDao tRootFolderRelatedInfoManagementDao;

	@Autowired
	private TFileConfigurationManagementDao tFileConfigurationManagementDao;

	@Autowired
	private TFileDetailInfoManagementDao tFileDetailInfoManagementDao;

	@Autowired
	private TFolderPermissionInfoManagementDao tFolderPermissionInfoManagementDao;

	@Autowired
	private TAnkenDao tAnkenDao;

	@Autowired
	private TAnkenAddKeijiDao tAnkenAddKeijiDao;

	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	@Autowired
	private TAnkenAzukariItemDao tAnkenAzukariItemDao;

	@Autowired
	private TAnkenJikenDao tAnkenJikenDao;

	@Autowired
	private TAnkenKoryuDao tAnkenKoryuDao;

	@Autowired
	private TAnkenSekkenDao tAnkenSekkenDao;

	@Autowired
	private TAnkenSosakikanDao tAnkenSosakikanDao;

	@Autowired
	private TKeijiAnkenCustomerDao tKeijiAnkenCustomerDao;

	@Autowired
	private TTaskDao tTaskDao;

	@Autowired
	private TTaskHistoryDao tTaskHistoryDao;

	@Autowired
	private TTaskWorkerDao tTaskWorkerDao;

	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	@Autowired
	private TAnkenRelatedKanyoshaDao tAnkenRelatedKanyoshaDao;

	@Autowired
	private TSaibanRelatedKanyoshaDao tSaibanRelatedKanyoshaDao;

	@Autowired
	private TPersonAddLawyerDao tPersonAddLawyerDao;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	@Autowired
	private TAdvisorContractDao tAdvisorContractDao;

	/** 報酬Daoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	/** 預り金Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** 売上情報Daoクラス */
	@Autowired
	private TSalesDao tSalesDao;

	/** 回収不能金情報Daoクラス */
	@Autowired
	private TUncollectibleDao tUncollectibleDao;

	/** 会計書類情報Daoクラス */
	@Autowired
	private TAccgDocDao tAccgDocDao;

	/** 請求書Daoクラス */
	@Autowired
	private TAccgInvoiceDao tAccgInvoiceDao;

	/** 精算書Daoクラス */
	@Autowired
	private TAccgStatementDao tAccgStatementDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 名簿IDに紐づいた名簿情報を取得します。
	 * 
	 * @param personId 名簿ID
	 */
	public TPersonEntity getPersonInfo(Long personId) {
		// 名簿情報を取得
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		return personEntity;
	}

	/**
	 * 名簿情報、案件情報、関与者情報を削除します。
	 * 
	 * @param personId 名簿ID
	 */
	public List<String> deletePerson(Long personId) throws AppException {

		// 名簿に紐づく案件IDを取得する
		List<Long> ankenIdList = tAnkenCustomerDao.selectAnkenIdsByCustomerId(personId);// customerId == personId

		// 削除可能かチェックを行う(不可の場合はAppExceptionがスローされる)
		this.deletePersonChecked(personId, ankenIdList);

		// 削除したS3ファイル
		Set<String> deleteS3ObjectKey = new HashSet<>();

		// 名簿情報削除
		List<String> customerDeleteS3List = this.deleteCustomer(personId);

		deleteS3ObjectKey.addAll(customerDeleteS3List);

		// 案件情報削除
		for (Long ankenId : ankenIdList) {
			List<String> ankenDeleteS3List = this.deleteAnken(ankenId, personId);
			deleteS3ObjectKey.addAll(ankenDeleteS3List);
		}

		// 関与者情報削除
		this.deleteKanyosha(personId);

		return new ArrayList<>(deleteS3ObjectKey);
	}

	/**
	 * 名簿情報削除後のS3オブジェクト削除
	 * 
	 * @param s3ObjectKey
	 */
	public void personDeleteAfterS3FileDelete(List<String> s3ObjectKey) {

		try {
			// ■AmazonS3のストレージから該当ファイルを削除
			if (!CollectionUtils.isEmpty(s3ObjectKey)) {
				fileStorageService.deleteFile(s3ObjectKey);
			}
		} catch (Exception ex) {
			// S3オブジェクトの削除APIで発生したエラーは、握りつぶす
			// ロガーには表示
			logger.error("顧客削除処理後のS3ファイル削除が失敗しました。", ex);
		}
	}

	/**
	 * 選択した名簿情報が、削除可能かどうかを判定する
	 * 
	 * @param personId 削除対象名簿ID
	 * @param personRelateAnkenIdList 削除対象名簿に紐づく案件ID
	 * @throws AppException
	 */
	private void deletePersonChecked(Long personId, List<Long> personRelateAnkenIdList) throws AppException {

		// *****************************************************************************
		// ★★★ 削除不可の条件 ★★★
		// ① 案件に削除対象顧客しか紐づいていない場合。(案件：顧客 = １：１ の場合) の判定
		// ② 案件IDに紐づいたスケジュール情報が存在する。
		// ③ 日付データが存在する。(初回面談日、受任日、事件処理完了日、精算完了日、完了フラグON)
		// ④ 会計データが存在する（顧客）。
		// ⑤ 預り品データが存在する（顧客）。
		// ⑥ 裁判データが存在する。
		// ⑦ 顧客IDに紐付いたスケジュール情報が存在する。 （必然的に初回面談が登録されているケースは削除できないので、③の条件から初回面談は外す） ※仕様変更による追記 （UI改善-1st）
		// ⑧ 顧問契約データの契約開始日が存在する
		// ⑨ 会計データが存在する（関与者）。
		// ⑩ 預り品データが存在する（関与者）。
		//
		// *****************************************************************************

		// エラーメッセージのEnumは固定
		MessageEnum msgEnum = MessageEnum.MSG_E00086;

		// =========================================================================
		// ⑦顧客IDに紐づいたスケジュール情報が存在する
		List<TScheduleEntity> tScheduleEntityListForCustomer = tScheduleDao.selectByCustomerId(personId);
		if (tScheduleEntityListForCustomer.size() > 0) {
			throw new AppException(msgEnum, null, "顧客の予定情報が登録されている");
		}
		// ⑦顧客IDに紐づいたスケジュール情報が存在する END
		// =========================================================================

		// =========================================================================
		// ⑧顧問契約データが存在するか
		List<TAdvisorContractEntity> advisorContractEntityList = tAdvisorContractDao.selectContractListPageByPersonId(personId);
		if (LoiozCollectionUtils.isNotEmpty(advisorContractEntityList)) {
			// 顧問契約が登録されている。
			throw new AppException(msgEnum, null, "顧問契約が登録されている");
		}
		// ⑧顧問契約データが存在するか END
		// =========================================================================

		for (Long personRelateAnkenId : personRelateAnkenIdList) {

			// =========================================================================
			// ① 案件に削除対象顧客しか紐づいていない場合。(案件：顧客 = １：１ の場合) の判定
			List<TAnkenCustomerEntity> ankenCustomerList = tAnkenCustomerDao.selectByAnkenId(personRelateAnkenId);
			if (1 < ankenCustomerList.size()) {
				// 複数の案件に紐づいている場合 -> 削除不可
				throw new AppException(msgEnum, null, "案件に削除者以外の顧客が設定されている");
			}
			// ① 案件に削除対象顧客しか紐づいていない場合。(案件：顧客 = １：１ の場合) の判定 END
			// =========================================================================

			// =========================================================================
			// ②案件IDに紐づいたスケジュール情報が存在する
			List<TScheduleEntity> tScheduleEntityListForAnken = tScheduleDao.selectByAnkenId(personRelateAnkenId);
			if (tScheduleEntityListForAnken.size() > 0) {
				throw new AppException(msgEnum, null, "案件の予定情報が登録されている");
			}
			// ②案件IDに紐づいたスケジュール情報が存在する END
			// =========================================================================

			// ===================================================================
			// ③ 日付データが存在する。(受任日、事件処理完了日、精算完了日、完了フラグON)
			TAnkenCustomerEntity ankenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(personRelateAnkenId, personId);

			if (SystemFlg.codeToBoolean(ankenCustomerEntity.getKanryoFlg())) {
				// 完了フラグがONの場合
				throw new AppException(msgEnum, null, "案件が完了している");
			}

			LocalDate juninDate = ankenCustomerEntity.getJuninDate();
			LocalDate jikenCompDate = ankenCustomerEntity.getJikenKanryoDate();
			LocalDate seisanCompDate = ankenCustomerEntity.getKanryoDate();
			if (juninDate != null || jikenCompDate != null || seisanCompDate != null) {
				// 受任日、事件処理完了日、精算完了日のいずれかが設定されている
				throw new AppException(msgEnum, null, "削除者の顧客情報に、受任日、事件処理完了日、精算完了日が設定されている");
			}
			// ③ 日付データが存在する END
			// ==================================================================

			// ==================================================================
			// ④ 会計データが存在する（スタータープランは会計データが全て削除され会計管理画面も使用できないためチェックをしない）。
			if (SessionUtils.canUsePlanFunc(PlanFuncRestrict.PF0002)) {
				List<TKaikeiKirokuEntity> kaikeiKirokuList = tKaikeiKirokuDao.selectByAnkenIdAndCustomerId(personRelateAnkenId,
						personId, false);
				List<TNyushukkinYoteiEntity> nyushukkinYoteiList = tNyushukkinYoteiDao.selectByAnkenIdAndCustomerId(personRelateAnkenId,
						personId, false);
				if (kaikeiKirokuList.size() > 0 || nyushukkinYoteiList.size() > 0) {
					// 会計データが存在する場合は、削除できません。
					throw new AppException(msgEnum, null, "案件に会計データが登録されている");
				}

				// 新会計 名簿関連データの件数取得
				List<TFeeEntity> tFeeEntities = tFeeDao.selectFeeEntityByParams(personRelateAnkenId, personId);
				List<TDepositRecvEntity> tDepositRecvEntities = tDepositRecvDao.selectDepositRecvByParams(personRelateAnkenId, personId, null);
				List<TAccgDocEntity> tAccgDocEntities = tAccgDocDao.selectAccgDocByParams(personRelateAnkenId, personId);
				TSalesEntity tSalesEntity = tSalesDao.selectSalesEntityByPersonIdAndAnkenId(personId, personRelateAnkenId);
				TUncollectibleEntity tUncollectibleEntity = tUncollectibleDao.selectUncollectibleByPersonIdAndAnkenId(personId, personRelateAnkenId);

				if (!CollectionUtils.isEmpty(tFeeEntities) || !CollectionUtils.isEmpty(tDepositRecvEntities) || !CollectionUtils.isEmpty(tAccgDocEntities)
						|| tSalesEntity != null || tUncollectibleEntity != null) {
					// 新会計データが存在する場合は、削除できません。
					throw new AppException(msgEnum, null, "案件に会計データが登録されている");
				}
			}
			// ④ 会計データが存在する END
			// ==================================================================

			// ==================================================================
			// ⑤ 預り品データが存在する
			List<TAnkenAzukariItemEntity> azukariItemList = tAnkenAzukariItemDao.selectByAnkenIdAndCustomerId(personRelateAnkenId, personId);
			if (azukariItemList.size() > 0) {
				// 預かり品データが存在する場合は、削除できません。
				throw new AppException(msgEnum, null, "案件に預かり品が登録されている");
			}
			// ⑤ 預り品データが存在する END
			// ==================================================================

			// ==================================================================
			// ⑥ 裁判データが存在する
			List<TSaibanEntity> saibanList = tSaibanDao.selectByAnkenIdAndCustomerId(personRelateAnkenId, personId);
			if (saibanList.size() > 0) {
				// 裁判データが存在する場合は、削除できません。
				throw new AppException(msgEnum, null, "案件に裁判情報が登録されている");
			}
			// ⑥ 裁判データが存在する END
			// ==================================================================

		}

		// ==================================================================
		// ⑨ 会計データが存在する（関与者）
		// 関与者に紐づく会計データを取得（スタータープランは会計データが全て削除され会計管理画面も使用できないためチェックをしない）。
		if (SessionUtils.canUsePlanFunc(PlanFuncRestrict.PF0002)) {
			List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectNyushukkinYoteiByPersonId(personId);
			List<TSeisanKirokuEntity> tSeisanKirokuEntities = tSeisanKirokuDao.selectSeisanKirokuByPersonId(personId);
			if (LoiozCollectionUtils.isNotEmpty(tNyushukkinYoteiEntities) || LoiozCollectionUtils.isNotEmpty(tSeisanKirokuEntities)) {
				// 会計データが存在する場合は、削除できません。
				throw new AppException(msgEnum, null, "関与案件に会計情報が登録されている");
			}

			// 新会計 請求書/精算書で利用されている場合
			final int invoiceCount = tAccgInvoiceDao.selectInvoiceCountByBillToPersonId(personId);
			final int statementCount = tAccgStatementDao.selectStatementCountByRefundToPersonId(personId);
			if (IntStream.of(invoiceCount, statementCount).anyMatch(count -> count > 0)) {
				// 会計データが存在する場合は、削除できません。
				throw new AppException(msgEnum, null, "関与案件に会計情報が登録されている");
			}

		}
		// ⑨ 会計データが存在する（関与者）END
		// ==================================================================

		// ==================================================================
		// ⑩ 預り品データが存在する（関与者）
		// 関与者に紐づく預り品データを取得
		List<TAnkenAzukariItemEntity> tAnkenAzukariItemEntities = tAnkenAzukariItemDao.selectAnkenAzukariItemByPersonId(personId);
		if (LoiozCollectionUtils.isNotEmpty(tAnkenAzukariItemEntities)) {
			// 預かり品データが存在する場合は、削除できません。
			throw new AppException(msgEnum, null, "関与案件に預かり品が登録されている");
		}
		// ⑩ 預り品データが存在する（関与者） END
		// ==================================================================

	}

	/**
	 * 案件-関与者関連情報(相手方として登録されているか)が存在するかどうか
	 *
	 * @param personId
	 * @return
	 */
	public boolean existsRelateAnken(Long personId) {
		List<TAnkenRelatedKanyoshaEntity> entityList = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByPersonId(personId);
		if (LoiozCollectionUtils.isNotEmpty(entityList)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 裁判-関与者関連情報(相手方として登録されているかなど)が存在するかどうか
	 *
	 * @param personId
	 * @return
	 */
	public boolean existsRelateSaiban(Long personId) {
		List<TSaibanRelatedKanyoshaEntity> tSaibanRelatedKanyoshaEntities = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaByPersonId(personId);
		if (LoiozCollectionUtils.isNotEmpty(tSaibanRelatedKanyoshaEntities)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 預かり品の預り元か返却先に該当関与者が設定されたデータが存在するかどうか
	 *
	 * @param personId
	 * @return
	 */
	public boolean existsRelateAzukariItem(Long personId) {
		List<TAnkenAzukariItemEntity> tAnkenAzukariItemEntities = tAnkenAzukariItemDao.selectAnkenAzukariItemByPersonId(personId);
		if (LoiozCollectionUtils.isNotEmpty(tAnkenAzukariItemEntities)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 関与者情報（当事者・関与者画面で登録されているなど）が存在するかどうか
	 * 
	 * @param personId
	 * @return
	 */
	public boolean existsRelateKanyosha(Long personId) {
		List<TKanyoshaEntity> tKanyoshaEntityList = tKanyoshaDao.selectKanyoshaListByPersonId(personId);
		if (LoiozCollectionUtils.isNotEmpty(tKanyoshaEntityList)) {
			return true;
		} else {
			return false;
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 名簿情報を削除します。
	 * 
	 * @param personId 名簿ID
	 */
	private List<String> deleteCustomer(Long personId) throws AppException {

		try {
			// 削除する名簿情報を取得する。
			TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);

			// 削除する名簿住所を取得する。
			List<TPersonContactEntity> tPersonContactEntityList = tPersonContactDao.selectPersonContactByPersonId(personId);

			// 削除する顧客軸の業務履歴を取得する。
			List<TGyomuHistoryCustomerEntity> tGyomuHistoryCustomerEntityList = tGyomuHistoryCustomerDao
					.selectByCustomerId(personId);

			// 削除対象の業務履歴顧客から業務履歴SEQを取得する
			List<Long> gyomuHistorySeqList = tGyomuHistoryCustomerEntityList.stream().map(TGyomuHistoryCustomerEntity::getGyomuHistorySeq).collect(Collectors.toList());

			// 削除する業務履歴を取得する。
			List<TGyomuHistoryEntity> tGyomuHistoryEntityList = tGyomuHistoryDao.selectEntityBySeqList(gyomuHistorySeqList);

			// 削除する個人名簿付帯情報を取得する。
			TPersonAddKojinEntity tPersonAddKojinEntity = tPersonAddKojinDao.selectPersonAddKojinByPersonId(personId);

			// 削除する法人名簿付帯情報を取得する。
			TPersonAddHojinEntity tPersonAddHojinEntity = tPersonAddHojinDao.selectPersonAddHojinByPersonId(personId);

			// 削除する弁護士付帯情報を取得する。
			List<TPersonAddLawyerEntity> tPersonAddLawyerEntityList = tPersonAddLawyerDao.selectPersonAddLawyerByPersonId(List.of(personId));

			// 削除する顧問契約情報を取得する。
			List<TAdvisorContractEntity> tAdvisorContractEntityEntityList = tAdvisorContractDao.selectContractListPageByPersonId(personId);

			// 削除するルートフォルダ関連情報管理を取得する。
			TRootFolderRelatedInfoManagementEntity tRootFolderRelatedInfoManagementEntity = tRootFolderRelatedInfoManagementDao
					.selectByCustomerId(personId);

			List<TFileConfigurationManagementEntity> tFileConfigurationManagementEntityList = new ArrayList<>();
			List<TFileDetailInfoManagementEntity> tFileDetailInfoManagementEntityList = new ArrayList<>();
			List<TFolderPermissionInfoManagementEntity> tFolderPermissionInfoManagementEntityList = new ArrayList<>();

			if (tRootFolderRelatedInfoManagementEntity != null) {
				// ルートフォルダ関連情報管理のデータが存在する場合のみ削除データの取得を行う。
				// ※データ移行などで、t_personは存在するが、ファイル管理系のデータが存在しない顧客が存在するため（データ移行後、一度も対象顧客のファイル管理画面へアクセスしていない顧客）

				// 削除するファイル構成管理を取得する。
				tFileConfigurationManagementEntityList = tFileConfigurationManagementDao
						.selectByRootFolderRelatedInfoManagementId(tRootFolderRelatedInfoManagementEntity.getRootFolderRelatedInfoManagementId());

				// 削除するファイル構成管理リストを取得する。
				List<Long> fileConfigurationManagementList = new ArrayList<Long>();

				// 削除するファイル構成詳細リストを取得する。
				List<Long> fileDetailInfoManagementIdList = new ArrayList<Long>();

				// 削除対象のファイル構成管理ID、ファイル詳細情報管理IDを取得する。
				tFileConfigurationManagementEntityList.stream().forEach(fileConfigurationManagementEntity -> {
					fileConfigurationManagementList.add(fileConfigurationManagementEntity.getFileConfigurationManagementId());
					fileDetailInfoManagementIdList.add(fileConfigurationManagementEntity.getFileDetailInfoManagementId());
				});

				// 削除するファイル構成管理を情報取得する。
				tFileDetailInfoManagementEntityList = tFileDetailInfoManagementDao
						.selectByFileDetailInfoManagementIdList(fileDetailInfoManagementIdList);

				// 削除するフォルダー権限管理情報を取得する。
				tFolderPermissionInfoManagementEntityList = tFolderPermissionInfoManagementDao
						.selectByfileConfigurationManagementList(fileConfigurationManagementList);
			}

			// 旧住所情報を取得する。
			List<TOldAddressEntity> tOldAddressEntityList = tOldAddressDao.selectOldAddressByPersonId(personId);

			if (personEntity != null) {
				// 顧客情報削除
				tPersonDao.delete(personEntity);
			}
			if (tPersonContactEntityList != null && !tPersonContactEntityList.isEmpty()) {
				// 顧客連絡先削除
				tPersonContactDao.delete(tPersonContactEntityList);
			}
			if (tPersonAddKojinEntity != null) {
				// 個人顧客付帯情報削除
				tPersonAddKojinDao.delete(tPersonAddKojinEntity);
			}
			if (tPersonAddHojinEntity != null) {
				// 法人顧客付帯情報削除
				tPersonAddHojinDao.delete(tPersonAddHojinEntity);
			}
			if (LoiozCollectionUtils.isNotEmpty(tPersonAddLawyerEntityList)) {
				TPersonAddLawyerEntity tPersonAddLawyerEntity = tPersonAddLawyerEntityList.stream().findFirst().get();
				// 弁護士付帯情報削除
				tPersonAddLawyerDao.delete(tPersonAddLawyerEntity);
			}
			if (LoiozCollectionUtils.isNotEmpty(tAdvisorContractEntityEntityList)) {
				// 顧問契約情報削除
				tAdvisorContractDao.delete(tAdvisorContractEntityEntityList);
			}
			if (tGyomuHistoryEntityList != null && !tGyomuHistoryEntityList.isEmpty()) {
				// 業務履歴削除
				tGyomuHistoryDao.delete(tGyomuHistoryEntityList);
			}
			if (tGyomuHistoryCustomerEntityList != null && !tGyomuHistoryCustomerEntityList.isEmpty()) {
				// 業務履歴顧客削除
				tGyomuHistoryCustomerDao.delete(tGyomuHistoryCustomerEntityList);
			}
			if (tRootFolderRelatedInfoManagementEntity != null) {
				// ルートフォルダ関連情報管理削除
				tRootFolderRelatedInfoManagementDao.delete(tRootFolderRelatedInfoManagementEntity);
			}
			if (tFileConfigurationManagementEntityList != null && !tFileConfigurationManagementEntityList.isEmpty()) {
				// ファイル構成管理情報削除
				tFileConfigurationManagementDao.batchDelete(tFileConfigurationManagementEntityList);
			}
			List<String> s3KeyList = new ArrayList<>();

			if (tFileDetailInfoManagementEntityList != null && !tFileDetailInfoManagementEntityList.isEmpty()) {

				for (TFileDetailInfoManagementEntity entity : tFileDetailInfoManagementEntityList) {
					if (entity.getS3ObjectKey() != null && !entity.getS3ObjectKey().isEmpty()) {
						s3KeyList.add(entity.getS3ObjectKey());
					}
				}
				// ファイル構成詳細管理情報削除
				tFileDetailInfoManagementDao.batchDelete(tFileDetailInfoManagementEntityList);
			}
			if (tFolderPermissionInfoManagementEntityList != null && !tFolderPermissionInfoManagementEntityList.isEmpty()) {
				// ファイル構成詳細管理情報削除
				tFolderPermissionInfoManagementDao.batchDelete(tFolderPermissionInfoManagementEntityList);
			}
			if (tOldAddressEntityList != null && !tOldAddressEntityList.isEmpty()) {
				// 旧住所情報削除
				tOldAddressDao.batchDelete(tOldAddressEntityList);
			}
			return s3KeyList;

		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}
	}

	/**
	 * 案件情報を削除します。
	 * 
	 * @param ankenId 案件ID
	 * @param personId 名簿ID
	 */
	private List<String> deleteAnken(Long ankenId, Long personId) throws AppException {

		try {
			// 案件IDと顧客IDに紐づく 案件-顧客情報を取得します。
			TAnkenCustomerEntity ankenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(ankenId, personId);

			// 削除する案件情報を取得します。
			TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);

			// 削除する案件刑事情報を取得します。
			TAnkenAddKeijiEntity tAnkenAddKeijiEntity = tAnkenAddKeijiDao.selectByAnkenId(ankenId);

			// 削除する刑事案件顧客を取得します。
			List<TKeijiAnkenCustomerEntity> tKeijiAnkenCustomerEntityList = tKeijiAnkenCustomerDao.selectByAnkenId(ankenId);

			// 削除する案件担当情報を取得します。
			List<TAnkenTantoEntity> tAnkenTantoEntityList = tAnkenTantoDao.selectByAnkenId(ankenId);

			// 削除する案件軸の業務履歴を取得
			List<TGyomuHistoryAnkenEntity> tGyomuHistoryAnkenEntityList = tGyomuHistoryAnkenDao.selectByAnkenId(ankenId);

			// 削除する預かり品情報を取得
			List<TAnkenAzukariItemEntity> tAnkenAzukariItemEntityList = tAnkenAzukariItemDao.selectByAnkenId(ankenId);

			// 削除対象の業務履歴顧客から業務履歴SEQを取得
			List<Long> gyomuHistorySeqList = tGyomuHistoryAnkenEntityList.stream().map(TGyomuHistoryAnkenEntity::getGyomuHistorySeq).collect(Collectors.toList());

			// 削除する業務履歴を取得
			List<TGyomuHistoryEntity> tGyomuHistoryEntityList = tGyomuHistoryDao.selectEntityBySeqList(gyomuHistorySeqList);

			// 削除する案件事件情報を取得
			List<TAnkenJikenEntity> tAnkenJikenEntityList = tAnkenJikenDao.selectByAnkenId(ankenId);

			// 削除する案件拘留情報を取得
			List<TAnkenKoryuEntity> tAnkenKoryuEntityList = tAnkenKoryuDao.selectByAnkenId(ankenId);

			// 削除する案件接見情報を取得
			List<TAnkenSekkenEntity> tAnkenSekkenEntityList = tAnkenSekkenDao.selectByAnkenId(ankenId);

			// 削除する案件捜査機関情報を取得
			List<TAnkenSosakikanEntity> tAnkenSosakikanEntityList = tAnkenSosakikanDao.selectByAnkenId(ankenId);

			// 削除する関与者情報を取得
			List<TKanyoshaEntity> tKanyoshaEntityList = tKanyoshaDao.selectKanyoshaListByAnkenId(ankenId);

			// 削除する関与者関係者情報を取得
			List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntityList = tAnkenRelatedKanyoshaDao
					.selectByAnkenId(ankenId);

			// 削除するルートフォルダ関連情報管理を取得
			TRootFolderRelatedInfoManagementEntity tRootFolderRelatedInfoManagementEntity = tRootFolderRelatedInfoManagementDao
					.selectByAnkenId(ankenId);

			List<TFileConfigurationManagementEntity> tFileConfigurationManagementEntityList = new ArrayList<>();
			List<TFileDetailInfoManagementEntity> tFileDetailInfoManagementEntityList = new ArrayList<>();
			List<TFolderPermissionInfoManagementEntity> tFolderPermissionInfoManagementEntityList = new ArrayList<>();

			if (tRootFolderRelatedInfoManagementEntity != null) {
				// ルートフォルダ関連情報管理のデータが存在する場合のみ削除データの取得を行う。
				// ※データ移行などで、t_ankenは存在するが、ファイル管理系のデータが存在しない案件が存在するため（データ移行後、一度も対象案件のファイル管理画面へアクセスしていない案件）

				// 削除するファイル構成管理を取得
				tFileConfigurationManagementEntityList = tFileConfigurationManagementDao
						.selectByRootFolderRelatedInfoManagementId(tRootFolderRelatedInfoManagementEntity.getRootFolderRelatedInfoManagementId());

				// 削除するファイル構成管理リスト
				List<Long> fileConfigurationManagementList = new ArrayList<Long>();

				// 削除するファイル構成詳細リスト
				List<Long> fileDetailInfoManagementIdList = new ArrayList<Long>();

				// 削除対象のファイル構成管理ID、ファイル詳細情報管理IDを取得
				tFileConfigurationManagementEntityList.stream().forEach(fileConfigurationManagementEntity -> {
					fileConfigurationManagementList.add(fileConfigurationManagementEntity.getFileConfigurationManagementId());
					fileDetailInfoManagementIdList.add(fileConfigurationManagementEntity.getFileDetailInfoManagementId());
				});

				// 削除するファイル構成管理を取得
				tFileDetailInfoManagementEntityList = tFileDetailInfoManagementDao
						.selectByFileDetailInfoManagementIdList(fileDetailInfoManagementIdList);

				// 削除するフォルダー権限管理を取得
				tFolderPermissionInfoManagementEntityList = tFolderPermissionInfoManagementDao
						.selectByfileConfigurationManagementList(fileConfigurationManagementList);
			}

			// 削除するタスク管理を取得
			List<TTaskEntity> tTaskEntityList = tTaskDao.selectByAnkenId(ankenId);

			// 削除対象のタスク管理からタスクSEQを取得
			List<Long> taskSeqList = tTaskEntityList.stream().map(TTaskEntity::getTaskSeq).collect(Collectors.toList());

			// 削除するタスク履歴リストを取得
			List<TTaskHistoryEntity> tTaskHistoryEntityList = tTaskHistoryDao.selectByTaskSeqList(taskSeqList);

			// 削除するタスク作業者リストを取得
			List<TTaskWorkerEntity> tTaskWorkerEntityList = tTaskWorkerDao.selectByTaskSeqList(taskSeqList);

			if (ankenCustomerEntity != null) {
				// 案件と顧客の紐づけを解除します。
				tAnkenCustomerDao.delete(ankenCustomerEntity);
			}
			if (tAnkenEntity != null) {
				// 案件情報を削除します。
				tAnkenDao.delete(tAnkenEntity);
			}
			if (tAnkenAddKeijiEntity != null) {
				// 案件刑事付帯情報を削除します。
				tAnkenAddKeijiDao.delete(tAnkenAddKeijiEntity);
			}
			if (tKeijiAnkenCustomerEntityList != null && !tKeijiAnkenCustomerEntityList.isEmpty()) {
				tKeijiAnkenCustomerDao.batchDelete(tKeijiAnkenCustomerEntityList);
			}
			if (tAnkenTantoEntityList != null && !tAnkenTantoEntityList.isEmpty()) {
				// 案件担当者情報を削除します。
				tAnkenTantoDao.batchDelete(tAnkenTantoEntityList);
			}
			if (tGyomuHistoryEntityList != null && !tGyomuHistoryEntityList.isEmpty()) {
				// 業務履歴を削除します。
				tGyomuHistoryDao.delete(tGyomuHistoryEntityList);
			}
			if (tGyomuHistoryAnkenEntityList != null && !tGyomuHistoryAnkenEntityList.isEmpty()) {
				// 業務履歴案件を削除します。
				tGyomuHistoryAnkenDao.delete(tGyomuHistoryAnkenEntityList);
			}
			if (tAnkenAzukariItemEntityList != null && !tAnkenAzukariItemEntityList.isEmpty()) {
				// 預かり品情報を削除します。
				tAnkenAzukariItemDao.delete(tAnkenAzukariItemEntityList);
			}
			if (tAnkenJikenEntityList != null && !tAnkenJikenEntityList.isEmpty()) {
				// 案件事件情報を削除します。
				tAnkenJikenDao.delete(tAnkenJikenEntityList);
			}
			if (tAnkenKoryuEntityList != null && !tAnkenKoryuEntityList.isEmpty()) {
				// 案件拘留情報を削除します。
				tAnkenKoryuDao.delete(tAnkenKoryuEntityList);
			}
			if (tAnkenSekkenEntityList != null && !tAnkenSekkenEntityList.isEmpty()) {
				// 案件接見情報を削除します。
				tAnkenSekkenDao.delete(tAnkenSekkenEntityList);
			}
			if (tAnkenSosakikanEntityList != null && !tAnkenSosakikanEntityList.isEmpty()) {
				// 案件捜査機関情報を削除します。
				tAnkenSosakikanDao.delete(tAnkenSosakikanEntityList);
			}
			if (tAnkenRelatedKanyoshaEntityList != null && !tAnkenRelatedKanyoshaEntityList.isEmpty()) {
				// 案件関与者-関係者を削除します。
				tAnkenRelatedKanyoshaDao.delete(tAnkenRelatedKanyoshaEntityList);
			}
			if (tKanyoshaEntityList != null && !tKanyoshaEntityList.isEmpty()) {
				// 関与者情報を削除します。
				tKanyoshaDao.batchDelete(tKanyoshaEntityList);
			}
			if (tTaskEntityList != null && !tTaskEntityList.isEmpty()) {
				// タスク情報を削除します。
				tTaskDao.batchDelete(tTaskEntityList);
			}
			if (tTaskHistoryEntityList != null && !tTaskHistoryEntityList.isEmpty()) {
				// タスク履歴情報削除
				tTaskHistoryDao.delete(tTaskHistoryEntityList);
			}
			if (tTaskWorkerEntityList != null && !tTaskWorkerEntityList.isEmpty()) {
				// タスク作業者情報削除
				tTaskWorkerDao.delete(tTaskWorkerEntityList);
			}
			if (tRootFolderRelatedInfoManagementEntity != null) {
				// ルートフォルダ関連情報管理削除
				tRootFolderRelatedInfoManagementDao.delete(tRootFolderRelatedInfoManagementEntity);
			}
			if (tFileConfigurationManagementEntityList != null && !tFileConfigurationManagementEntityList.isEmpty()) {
				// ファイル構成管理情報削除
				tFileConfigurationManagementDao.batchDelete(tFileConfigurationManagementEntityList);
			}
			List<String> s3KeyList = new ArrayList<>();
			if (tFileDetailInfoManagementEntityList != null && !tFileDetailInfoManagementEntityList.isEmpty()) {

				for (TFileDetailInfoManagementEntity entity : tFileDetailInfoManagementEntityList) {
					if (entity.getS3ObjectKey() != null && !entity.getS3ObjectKey().isEmpty()) {
						s3KeyList.add(entity.getS3ObjectKey());
					}
				}
				// ファイル構成詳細管理情報削除
				tFileDetailInfoManagementDao.batchDelete(tFileDetailInfoManagementEntityList);
			}
			if (tFolderPermissionInfoManagementEntityList != null && !tFolderPermissionInfoManagementEntityList.isEmpty()) {
				// ファイル構成詳細管理情報削除
				tFolderPermissionInfoManagementDao.batchDelete(tFolderPermissionInfoManagementEntityList);
			}
			return s3KeyList;

		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 関与者情報の削除処理
	 *
	 * @param personId
	 * @throws AppException
	 */
	private void deleteKanyosha(Long personId) throws AppException {

		try {
			// 案件-関与者関係者情報の削除
			this.deleteAnkenRelatedKanyosha(personId);

			// 裁判-関与者関係者情報の削除
			this.deleteSaibanRelatedKanyosha(personId);

			// 関与者情報の削除
			List<TKanyoshaEntity> kanyoshaList = tKanyoshaDao.selectKanyoshaListByPersonId(personId);
			if (LoiozCollectionUtils.isNotEmpty(kanyoshaList)) {
				tKanyoshaDao.batchDelete(kanyoshaList);
			}

		} catch (OptimisticLockingFailureException e) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 案件-関与者関係者の削除処理
	 * 
	 * @param personId
	 */
	private void deleteAnkenRelatedKanyosha(Long personId) {

		// 削除する関与者のEntity
		List<TAnkenRelatedKanyoshaEntity> deleteAnkenRelatedKanyoshaList = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByPersonId(personId);
		if (CollectionUtils.isEmpty(deleteAnkenRelatedKanyoshaList)) {
			// 削除データが存在しない場合、何もしない
			return;
		}

		// 代理人の場合
		deleteAnkenRelatedKanyoshaList.stream().filter(entity -> SystemFlg.codeToBoolean(entity.getDairiFlg()))
				.forEach(entity -> {
					// 該当の関与者を代理人として設定しているデータを取得
					List<TAnkenRelatedKanyoshaEntity> tAnkenRelatedKanyoshaEntities = tAnkenRelatedKanyoshaDao.selectKanyoshaDairininByParams(entity.getAnkenId(), entity.getKanyoshaSeq());

					// ひも付き関与者カラムをnullにして更新
					List<TAnkenRelatedKanyoshaEntity> ankenUpdateEntities = tAnkenRelatedKanyoshaEntities.stream()
							.peek(e -> e.setRelatedKanyoshaSeq(null))
							.collect(Collectors.toList());

					// データの削除
					tAnkenRelatedKanyoshaDao.delete(entity);

					// 紐付いていたデータを更新
					tAnkenRelatedKanyoshaDao.update(ankenUpdateEntities);
				});

		// 相手方、共犯者、被害者の場合
		deleteAnkenRelatedKanyoshaList.stream().filter(entity -> !SystemFlg.codeToBoolean(entity.getDairiFlg()))
				.forEach(deleteAnkenRelatedKanyoshaEntity -> {

					// 該当の関与者が代理人を設定している場合は、代理人のデータを削除
					if (deleteAnkenRelatedKanyoshaEntity.getRelatedKanyoshaSeq() != null) {
						// 該当の関与者の代理人が代理人として登録しているデータを取得
						TAnkenRelatedKanyoshaEntity deleteDairninEntity = tAnkenRelatedKanyoshaDao.selectAnkenRelatedKanyoshaByParams(deleteAnkenRelatedKanyoshaEntity.getAnkenId(), deleteAnkenRelatedKanyoshaEntity.getRelatedKanyoshaSeq());

						// 該当データと紐付いていた代理人のデータを削除
						if (deleteDairninEntity != null) {
							tAnkenRelatedKanyoshaDao.delete(deleteDairninEntity);
						}
					}

					// データの削除
					tAnkenRelatedKanyoshaDao.delete(deleteAnkenRelatedKanyoshaEntity);
				});
	}

	/**
	 * 裁判-関与者関係者の削除処理
	 * 
	 * @param personId
	 */
	private void deleteSaibanRelatedKanyosha(Long personId) {

		// 案件に紐づく裁判-関与者関係者情報を取得する
		List<TSaibanRelatedKanyoshaEntity> deleteSaibanRelatedKanyoshaList = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaByPersonId(personId);

		if (CollectionUtils.isEmpty(deleteSaibanRelatedKanyoshaList)) {
			// 削除データが存在しない場合、何もしない
			return;
		}

		// 代理人の場合
		deleteSaibanRelatedKanyoshaList.stream().filter(entity -> SystemFlg.codeToBoolean(entity.getDairiFlg()))
				.forEach(deleteSaibanRelatedKanyoshaEntity -> {

					// 該当の関与者を代理人として設定しているデータを取得
					List<TSaibanRelatedKanyoshaEntity> tSaibanRelatedKanyoshaList = tSaibanRelatedKanyoshaDao.selectKanyoshaDairininByParams(deleteSaibanRelatedKanyoshaEntity.getSaibanSeq(), deleteSaibanRelatedKanyoshaEntity.getKanyoshaSeq());

					// ひも付き関与者カラムをnullにして更新
					List<TSaibanRelatedKanyoshaEntity> saibanUpdateEntities = tSaibanRelatedKanyoshaList.stream()
							.peek(e -> e.setRelatedKanyoshaSeq(null))
							.collect(Collectors.toList());

					// データの削除
					tSaibanRelatedKanyoshaDao.delete(deleteSaibanRelatedKanyoshaEntity);

					// 紐付いていたデータを更新
					tSaibanRelatedKanyoshaDao.update(saibanUpdateEntities);
				});

		// 相手方、共犯者、被害者の場合
		deleteSaibanRelatedKanyoshaList.stream().filter(entity -> !SystemFlg.codeToBoolean(entity.getDairiFlg()))
				.forEach(deleteSaibanRelatedKanyoshaEntity -> {

					// 該当の関与者が代理人を設定している場合は、代理人のデータを削除
					if (deleteSaibanRelatedKanyoshaEntity.getRelatedKanyoshaSeq() != null) {
						// 該当の関与者の代理人が代理人として登録しているデータを取得
						TSaibanRelatedKanyoshaEntity deleteDairninEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(deleteSaibanRelatedKanyoshaEntity.getSaibanSeq(), deleteSaibanRelatedKanyoshaEntity.getRelatedKanyoshaSeq());

						// 該当データと紐付いていた代理人のデータを削除
						if (deleteDairninEntity != null) {
							tSaibanRelatedKanyoshaDao.delete(deleteDairninEntity);
						}
					}

					// データの削除
					tSaibanRelatedKanyoshaDao.delete(deleteSaibanRelatedKanyoshaEntity);
				});
	}

}