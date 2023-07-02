package jp.loioz.app.user.ankenManagement.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.app.user.ankenManagement.form.AnkenDeleteForm;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.constant.plan.PlanConstant.PlanFuncRestrict;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.service.file.FileStorageService;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
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
import jp.loioz.dao.TFileConfigurationManagementDao;
import jp.loioz.dao.TFileDetailInfoManagementDao;
import jp.loioz.dao.TFolderPermissionInfoManagementDao;
import jp.loioz.dao.TGyomuHistoryAnkenDao;
import jp.loioz.dao.TGyomuHistoryCustomerDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TKeijiAnkenCustomerDao;
import jp.loioz.dao.TRootFolderRelatedInfoManagementDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.dao.TTaskAnkenDao;
import jp.loioz.dao.TTaskDao;
import jp.loioz.dao.TTaskHistoryDao;
import jp.loioz.dao.TTaskWorkerDao;
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
import jp.loioz.entity.TFileConfigurationManagementEntity;
import jp.loioz.entity.TFileDetailInfoManagementEntity;
import jp.loioz.entity.TFolderPermissionInfoManagementEntity;
import jp.loioz.entity.TGyomuHistoryAnkenEntity;
import jp.loioz.entity.TGyomuHistoryCustomerEntity;
import jp.loioz.entity.TGyomuHistoryEntity;
import jp.loioz.entity.TKanyoshaEntity;
import jp.loioz.entity.TKeijiAnkenCustomerEntity;
import jp.loioz.entity.TRootFolderRelatedInfoManagementEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TScheduleEntity;
import jp.loioz.entity.TTaskAnkenEntity;
import jp.loioz.entity.TTaskEntity;
import jp.loioz.entity.TTaskHistoryEntity;
import jp.loioz.entity.TTaskWorkerEntity;

/**
 * 案件管理画面 案件削除サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenDeleteService extends DefaultService {

	// **************************************************
	// 共通サービスクラス
	// **************************************************

	/** 共通ファイルストレージサービスクラス */
	@Autowired
	private FileStorageService fileStorageService;

	/** 共通会計サービスクラス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 共通顧客サービスクラス */
	@Autowired
	private CommonPersonService commonCustomerService;

	/** 共通旧会計サービスクラス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	// **************************************************
	// Daoクラス
	// **************************************************

	@Autowired
	private TAnkenDao tAnkenDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	@Autowired
	private TAnkenAzukariItemDao tAnkenAzukariItemDao;

	@Autowired
	private TAnkenAddKeijiDao tAnkenAddKeijiDao;

	@Autowired
	private TKeijiAnkenCustomerDao tKeijiAnkenCustomerDao;

	@Autowired
	private TAnkenJikenDao tAnkenJikenDao;

	@Autowired
	private TAnkenKoryuDao tAnkenKoryuDao;

	@Autowired
	private TAnkenSekkenDao tAnkenSekkenDao;

	@Autowired
	private TAnkenSosakikanDao tAnkenSosakikanDao;

	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	@Autowired
	private TGyomuHistoryAnkenDao tGyomuHistoryAnkenDao;

	@Autowired
	private TGyomuHistoryCustomerDao tGyomuHistoryCustomerDao;

	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	@Autowired
	private TAnkenRelatedKanyoshaDao tAnkenRelatedKanyoshaDao;

	@Autowired
	private TTaskDao tTaskDao;

	@Autowired
	private TTaskHistoryDao tTaskHistoryDao;

	@Autowired
	private TTaskWorkerDao tTaskWorkerDao;

	@Autowired
	private TTaskAnkenDao tTaskAnkenDao;

	@Autowired
	private TSaibanDao tSaibanDao;

	@Autowired
	private TScheduleDao tScheduleDao;

	@Autowired
	private TRootFolderRelatedInfoManagementDao tRootFolderRelatedInfoManagementDao;

	@Autowired
	private TFileConfigurationManagementDao tFileConfigurationManagementDao;

	@Autowired
	private TFileDetailInfoManagementDao tFileDetailInfoManagementDao;

	@Autowired
	private TFolderPermissionInfoManagementDao tFolderPermissionInfoManagementDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 案件に紐づく顧客情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public List<Long> getRelatedCustomerId(Long ankenId) {
		return this.getRelatedCustomerInfo(ankenId).stream().map(TAnkenCustomerEntity::getCustomerId).sorted().collect(Collectors.toList());
	}

	/**
	 * 画面ViewFormを作成します。
	 * 
	 * @param ankenId
	 * @return
	 */
	public AnkenDeleteForm createViewForm(Long ankenId) {
		AnkenDeleteForm viewForm = new AnkenDeleteForm();
		TAnkenEntity ankenInfo = this.getAnkenInfo(ankenId);
		viewForm.setAnkenId(ankenInfo.getAnkenId());
		return viewForm;
	}

	/**
	 * 案件削除チェック
	 * 
	 * @param ankenId
	 * @throws AppException 削除不可の場合
	 */
	public void deleteCheck(Long ankenId) throws AppException {

		// *****************************************************************************
		// 仕様 案件を削除、紐づく顧客は削除しない。
		//
		// ★★★ 削除不可の条件 ★★★
		// ① 案件IDに紐づいたスケジュール情報が存在する。
		// ② 会計データが存在する（スタータープランのときはチェックをしない）。
		// ③ 裁判データが存在する。
		// ④ 日付データが存在する。(受任日、事件処理完了日、精算完了日、完了フラグON)
		// *****************************************************************************

		// ① 案件IDに紐づいたスケジュール情報が存在する。
		List<TScheduleEntity> shceduleData = tScheduleDao.selectByAnkenId(ankenId);
		if (!CollectionUtils.isEmpty(shceduleData)) {
			throw new AppException(MessageEnum.MSG_E00086, null, "案件IDに紐づいたスケジュール情報が存在する");
		}

		// ② 会計データが存在する（スタータープランは会計データが全て削除され会計管理画面も使用できないためチェックをしない）。
		if (SessionUtils.canUsePlanFunc(PlanFuncRestrict.PF0002)) {
			if (commonKaikeiService.isExistsOldKaikeiDataOnAnken(ankenId) || commonAccgService.existsAccgDataByAnkenId(ankenId)) {
				throw new AppException(MessageEnum.MSG_E00086, null, "会計データが存在する");
			}
		}

		// ③ 裁判データが存在する。
		List<TSaibanEntity> saibanData = tSaibanDao.selectByAnkenId(ankenId);
		if (!CollectionUtils.isEmpty(saibanData)) {
			throw new AppException(MessageEnum.MSG_E00086, null, "裁判データが存在する");
		}

		// 紐付いている顧客情報を取得する
		// ④ 日付データが存在する。(受任日、事件処理完了日、精算完了日、完了フラグON)
		List<TAnkenCustomerEntity> customerInfo = this.getRelatedCustomerInfo(ankenId);
		for (TAnkenCustomerEntity entity : customerInfo) {
			// 初回面談日は予定の存在チェックで確認済。 受任日、事件完了日、精算完了日、完了チェックのいずれかに登録されている場合は、削除不可
			if (entity.getJuninDate() != null || entity.getJikenKanryoDate() != null || entity.getKanryoDate() != null || SystemFlg.codeToBoolean(entity.getKanryoFlg())) {
				throw new AppException(MessageEnum.MSG_E00086, null, "受任日、事件処理完了日、精算完了日のいずれかが設定されている、もしくは案件が完了済の");
			}
		}

	}

	/**
	 * 案件削除モーダルから案件を削除する
	 * 
	 * @param ankenId
	 * @throws AppException
	 */
	public void deleteAnken(Long ankenId) throws AppException {

		// *****************************************************************************
		// 仕様 案件を削除、紐づく顧客は削除しない。
		//
		// ★★★ 削除不可の条件 ★★★
		// ① 案件IDに紐づいたスケジュール情報が存在する。
		// ② 会計データが存在する。
		// ③ 裁判データが存在する。
		// ④ 日付データが存在する。(受任日、事件処理完了日、精算完了日、完了フラグON)
		// *****************************************************************************

		// 顧客情報を取得
		List<TAnkenCustomerEntity> ankenCustomerInfo = this.getRelatedCustomerInfo(ankenId);
		tAnkenCustomerDao.delete(ankenCustomerInfo);

		List<Long> customerIdList = ankenCustomerInfo.stream()
			.map(e -> e.getCustomerId())
			.collect(Collectors.toList());
		
		// 名簿属性のFlgの更新
		this.updatePersonAttributeFlgs(customerIdList);
		
		// 案件情報の削除
		this.ankenInfoDelete(ankenId);

		// 案件刑事関連の削除
		this.ankenKeijiInfoDelete(ankenId);

		// 案件IDの紐付けが可能な機能を持つデータ(該当案件と紐付かなくても成り立つデータ)
		this.ankenGyomuHistoryDelete(ankenId);
		this.ankenKanyoshaDelete(ankenId);
		this.taskDelete(ankenId);
		this.taskAnkenDelete(ankenId);

		// 案件ファイル情報の削除
		List<String> deleteS3Keys = this.ankenFileDelete(ankenId);

		if (!CollectionUtils.isEmpty(deleteS3Keys)) {
			// S3からファイルを削除
			fileStorageService.deleteFile(deleteS3Keys);
		}

	}

	/**
	 * 対象名簿の属性Flg情報を更新する
	 * 
	 * @param personIdList
	 * @throws AppException
	 */
	private void updatePersonAttributeFlgs(List<Long> personIdList) throws AppException {
		for (Long personId : personIdList) {
			commonCustomerService.updatePersonAttributeFlgs(personId);
		}
	}
	
	/**
	 * 案件削除処理内の基本情報の削除
	 * 
	 * @param ankenId
	 */
	private void ankenInfoDelete(Long ankenId) {

		TAnkenEntity ankenInfo = this.getAnkenInfo(ankenId);
		List<TAnkenTantoEntity> ankenTantoInfo = tAnkenTantoDao.selectByAnkenId(ankenId);
		List<TAnkenAzukariItemEntity> ankenAzukariInfo = tAnkenAzukariItemDao.selectByAnkenId(ankenId);

		// 削除処理
		tAnkenDao.delete(ankenInfo);
		if (!CollectionUtils.isEmpty(ankenTantoInfo)) {
			tAnkenTantoDao.batchDelete(ankenTantoInfo);
		}
		if (!CollectionUtils.isEmpty(ankenAzukariInfo)) {
			tAnkenAzukariItemDao.delete(ankenAzukariInfo);
		}
	}

	/**
	 * 案件削除処理内の刑事情報の削除
	 * 
	 * @param ankenId
	 */
	private void ankenKeijiInfoDelete(Long ankenId) {

		// 刑事情報の削除処理
		TAnkenAddKeijiEntity ankenKeijiInfo = tAnkenAddKeijiDao.selectByAnkenId(ankenId);
		List<TKeijiAnkenCustomerEntity> keijiAnkenCustomerInfo = tKeijiAnkenCustomerDao.selectByAnkenId(ankenId);
		List<TAnkenJikenEntity> ankenJikenInfo = tAnkenJikenDao.selectByAnkenId(ankenId);
		List<TAnkenKoryuEntity> ankenKoryuInfo = tAnkenKoryuDao.selectByAnkenId(ankenId);
		List<TAnkenSekkenEntity> ankenSekkenInfo = tAnkenSekkenDao.selectByAnkenId(ankenId);
		List<TAnkenSosakikanEntity> ankenSosakikanInfo = tAnkenSosakikanDao.selectByAnkenId(ankenId);

		if (ankenKeijiInfo != null) {
			tAnkenAddKeijiDao.delete(ankenKeijiInfo);
		}
		if (!CollectionUtils.isEmpty(keijiAnkenCustomerInfo)) {
			tKeijiAnkenCustomerDao.batchDelete(keijiAnkenCustomerInfo);
		}
		if (!CollectionUtils.isEmpty(ankenJikenInfo)) {
			tAnkenJikenDao.delete(ankenJikenInfo);
		}
		if (!CollectionUtils.isEmpty(ankenKoryuInfo)) {
			tAnkenKoryuDao.delete(ankenKoryuInfo);
		}
		if (!CollectionUtils.isEmpty(ankenSekkenInfo)) {
			tAnkenSekkenDao.delete(ankenSekkenInfo);
		}
		if (!CollectionUtils.isEmpty(ankenSosakikanInfo)) {
			tAnkenSosakikanDao.delete(ankenSosakikanInfo);
		}
	}

	/**
	 * 案件削除処理内の業務履歴削除
	 * ※ 案件に紐付いた業務履歴情報をすべて削除する
	 * 
	 * @param ankenId
	 */
	private void ankenGyomuHistoryDelete(Long ankenId) {

		// 業務履歴-案件情報を取得
		List<TGyomuHistoryAnkenEntity> gyomuHistoryAnken = tGyomuHistoryAnkenDao.selectByAnkenId(ankenId);

		// 紐づく業務履歴がない場合は、なにもしない
		if (CollectionUtils.isEmpty(gyomuHistoryAnken)) {
			return;
		}

		// 案件に紐づく業務履歴情報を取得
		List<Long> gyomuHistorySeq = gyomuHistoryAnken.stream().map(TGyomuHistoryAnkenEntity::getGyomuHistorySeq).collect(Collectors.toList());

		List<TGyomuHistoryAnkenEntity> tGyomuHistoryAnkenEntities = tGyomuHistoryAnkenDao.selectByParentSeq(gyomuHistorySeq);
		Map<Long, List<TGyomuHistoryAnkenEntity>> seqToMap = tGyomuHistoryAnkenEntities.stream().collect(Collectors.groupingBy(TGyomuHistoryAnkenEntity::getGyomuHistorySeq));

		List<Long> deleteGyomuHistorySeq = new ArrayList<>();
		for (Map.Entry<Long, List<TGyomuHistoryAnkenEntity>> entry : seqToMap.entrySet()) {
			if (entry.getValue().stream().count() <= 1) {
				// 業務履歴が案件に一見のみしか紐付いていない場合は削除する
				deleteGyomuHistorySeq.add(entry.getKey());
			}
		}

		// 業務履歴-案件情報は削除する
		tGyomuHistoryAnkenDao.delete(gyomuHistoryAnken);

		if (!CollectionUtils.isEmpty(deleteGyomuHistorySeq)) {
			// 削除する業務履歴情報を取得する
			List<TGyomuHistoryEntity> gyomuHistoryInfo = tGyomuHistoryDao.selectEntityBySeqList(deleteGyomuHistorySeq);
			List<TGyomuHistoryCustomerEntity> gyomuHistoryCustomer = tGyomuHistoryCustomerDao.selectByParentSeq(deleteGyomuHistorySeq);

			// 削除
			tGyomuHistoryDao.delete(gyomuHistoryInfo);
			tGyomuHistoryCustomerDao.delete(gyomuHistoryCustomer);
		}

	}

	/**
	 * 案件削除処理内の関与者削除
	 * 
	 * @param ankenId
	 */
	private void ankenKanyoshaDelete(Long ankenId) {

		// 案件-関与者関係者
		List<TAnkenRelatedKanyoshaEntity> ankenRelatedKanyoshaInfo = tAnkenRelatedKanyoshaDao.selectByAnkenId(ankenId);
		// 関与者
		List<TKanyoshaEntity> tKanyoshaEntityList = tKanyoshaDao.selectKanyoshaListByAnkenId(ankenId);
		
		if (!CollectionUtils.isEmpty(ankenRelatedKanyoshaInfo)) {
			tAnkenRelatedKanyoshaDao.delete(ankenRelatedKanyoshaInfo);
		}
		if (!CollectionUtils.isEmpty(tKanyoshaEntityList)) {
			tKanyoshaDao.batchDelete(tKanyoshaEntityList);
		}
	}

	/**
	 * 案件削除処理内でのタスク削除処理
	 * 
	 * @param ankenId
	 */
	private void taskDelete(Long ankenId) {

		// タスク情報を取得
		List<TTaskEntity> taskInfo = tTaskDao.selectByAnkenId(ankenId);

		List<Long> taskSeqList = taskInfo.stream().map(TTaskEntity::getTaskSeq).collect(Collectors.toList());

		// タスクの関連情報を取得
		List<TTaskHistoryEntity> taskHistoryInfo = tTaskHistoryDao.selectByTaskSeqList(taskSeqList);
		List<TTaskWorkerEntity> taskWorkerInfo = tTaskWorkerDao.selectByTaskSeqList(taskSeqList);

		// 削除処理
		if (!CollectionUtils.isEmpty(taskInfo)) {
			tTaskDao.batchDelete(taskInfo);
		}
		if (!CollectionUtils.isEmpty(taskHistoryInfo)) {
			tTaskHistoryDao.delete(taskHistoryInfo);
		}
		if (!CollectionUtils.isEmpty(taskWorkerInfo)) {
			tTaskWorkerDao.delete(taskWorkerInfo);
		}

	}

	/**
	 * 案件削除処理内でのタスク案件（タスク管理サイドメニュー）削除処理
	 * 
	 * @param ankenId
	 */
	private void taskAnkenDelete(Long ankenId) {

		// タスク情報を取得
		List<TTaskAnkenEntity> taskAnkenList = tTaskAnkenDao.selectTaskAnkenByAccountSeqAnkenId(null, ankenId);
		if (CollectionUtils.isEmpty(taskAnkenList)) {
			// 対象データが無い場合は処理終了
			return;
		}

		// 案件タスク情報の削除
		tTaskAnkenDao.delete(taskAnkenList);
	}

	/**
	 * 案件削除時のファイル管理データ削除処理(S3オブジェクトは削除しない)
	 * 
	 * @param ankenId
	 * @return 削除したレコードのS3オブジェクトキーのリスト
	 */
	private List<String> ankenFileDelete(Long ankenId) {

		List<String> deleteS3Keys = new ArrayList<>();

		// 削除するルートフォルダ関連情報管理を取得
		TRootFolderRelatedInfoManagementEntity tRootFolderRelatedInfoManagementEntity = tRootFolderRelatedInfoManagementDao.selectByAnkenId(ankenId);
		if (tRootFolderRelatedInfoManagementEntity == null) {
			// ファイル管理情報がなければそのまま返却
			return deleteS3Keys;
		}

		// ルートフォルダ関連情報管理のデータが存在する場合のみ削除データの取得を行う。
		// ※データ移行などで、t_ankenは存在するが、ファイル管理系のデータが存在しない案件が存在するため（データ移行後、一度も対象案件のファイル管理画面へアクセスしていない案件）

		// 削除するファイル構成管理を取得
		List<TFileConfigurationManagementEntity> tFileConfigurationManagementEntityList = tFileConfigurationManagementDao.selectByRootFolderRelatedInfoManagementId(tRootFolderRelatedInfoManagementEntity.getRootFolderRelatedInfoManagementId());

		// 削除するファイル構成管理を取得
		List<Long> fileDetailInfoMngIdList = tFileConfigurationManagementEntityList.stream().map(TFileConfigurationManagementEntity::getFileDetailInfoManagementId).collect(Collectors.toList());
		List<TFileDetailInfoManagementEntity> tFileDetailInfoManagementEntityList = tFileDetailInfoManagementDao.selectByFileDetailInfoManagementIdList(fileDetailInfoMngIdList);

		// 削除するフォルダー権限管理を取得
		List<Long> fileConfMngSeqList = tFileConfigurationManagementEntityList.stream().map(TFileConfigurationManagementEntity::getFileConfigurationManagementId).collect(Collectors.toList());
		List<TFolderPermissionInfoManagementEntity> tFolderPermissionInfoManagementEntityList = tFolderPermissionInfoManagementDao.selectByfileConfigurationManagementList(fileConfMngSeqList);

		// 削除するS3キーを取得
		deleteS3Keys = tFileDetailInfoManagementEntityList.stream().map(TFileDetailInfoManagementEntity::getS3ObjectKey).filter(StringUtils::isNotEmpty).collect(Collectors.toList());

		// ルートフォルダの削除処理
		tRootFolderRelatedInfoManagementDao.delete(tRootFolderRelatedInfoManagementEntity);
		if (!CollectionUtils.isEmpty(tFileConfigurationManagementEntityList)) {
			tFileConfigurationManagementDao.batchDelete(tFileConfigurationManagementEntityList);
		}
		if (!CollectionUtils.isEmpty(tFileDetailInfoManagementEntityList)) {
			tFileDetailInfoManagementDao.batchDelete(tFileDetailInfoManagementEntityList);
		}
		if (!CollectionUtils.isEmpty(tFolderPermissionInfoManagementEntityList)) {
			tFolderPermissionInfoManagementDao.batchDelete(tFolderPermissionInfoManagementEntityList);
		}

		return deleteS3Keys;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件情報の取得
	 * 
	 * @param ankenId
	 * @return
	 */
	private TAnkenEntity getAnkenInfo(Long ankenId) {
		TAnkenEntity ankenInfo = tAnkenDao.selectByAnkenId(ankenId);
		if (ankenInfo == null) {
			throw new DataNotFoundException("案件情報が存在しません。[ankenId=" + ankenId + "]");
		}
		return ankenInfo;
	}

	/**
	 * 案件-顧客情報を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	private List<TAnkenCustomerEntity> getRelatedCustomerInfo(Long ankenId) {
		return tAnkenCustomerDao.selectByAnkenId(ankenId);
	}

}
