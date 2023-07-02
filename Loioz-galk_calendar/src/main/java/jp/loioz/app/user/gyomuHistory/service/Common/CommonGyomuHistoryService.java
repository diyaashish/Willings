package jp.loioz.app.user.gyomuHistory.service.Common;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.user.gyomuHistory.form.Common.ChangeImportantRequest;
import jp.loioz.app.user.gyomuHistory.form.Common.CommonInputForm;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TGyomuHistoryAnkenDao;
import jp.loioz.dao.TGyomuHistoryCustomerDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TGyomuHistoryAnkenEntity;
import jp.loioz.entity.TGyomuHistoryCustomerEntity;
import jp.loioz.entity.TGyomuHistoryEntity;
import jp.loioz.entity.TSaibanEntity;

/**
 * 業務履歴の共通サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonGyomuHistoryService extends DefaultService {

	/** 共通処理 アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 業務履歴Daoクラス */
	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	/** 業務履歴-案件Dao */
	@Autowired
	private TGyomuHistoryAnkenDao tGyomuHistoryAnkenDao;

	/** 業務履歴-顧客Dao */
	@Autowired
	private TGyomuHistoryCustomerDao tGyomuHistoryCustomerDao;

	/** 案件-顧客Dao */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 裁判Dao */
	@Autowired
	private TSaibanDao tSaibanDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 案件-担当者リストから、条件に該当するデータのアカウントSEQを取得します
	 *
	 * <pre>
	 * 該当データ
	 * → 種別ごとに判定。メイン担当フラグがONの場合、その人が優先
	 *     該当者がいない場合、枝番(BranchNo)が一番若い人
	 * </pre>
	 *
	 *
	 * @param type 担当種別(売上計上先、担当弁護士、担当事務)
	 * @param ankenTantoList 案件IDで絞り込まれていること
	 * @return アカウントSEQ
	 */
	public Long getAnkenTantoshaSeq(TantoType type, List<TAnkenTantoEntity> ankenTantoList) {

		// アカウント種別でフィルタリング
		List<TAnkenTantoEntity> ankenTantoOfType = ankenTantoList.stream()
				.filter(entity -> Objects.equals(entity.getTantoType(), type.getCd()))
				.collect(Collectors.toList());

		// 登録されていない場合はnullを返却
		if (ankenTantoOfType.isEmpty()) {
			return null;
		}

		// メイン担当フラグを持っている人がいるか確認する。
		boolean isMainUser = ankenTantoOfType.stream().anyMatch(entity -> SystemFlg.codeToBoolean(entity.getAnkenMainTantoFlg()));

		// true の場合、メイン担当フラグがONのアカウントSEQをretrun;
		// falseの場合、枝番が一番若いアカウントSEQをreturn
		if (isMainUser) {
			Optional<TAnkenTantoEntity> havingMainFlgDate = ankenTantoOfType.stream()
					.filter(entity -> SystemFlg.codeToBoolean(entity.getAnkenMainTantoFlg()))
					.findFirst();
			if (havingMainFlgDate.isPresent()) {
				return havingMainFlgDate.get().getAccountSeq();
			}
		} else {
			Optional<TAnkenTantoEntity> branchNoIsMinDate = ankenTantoOfType.stream()
					.min(Comparator.comparingLong(TAnkenTantoEntity::getTantoTypeBranchNo));
			if (branchNoIsMinDate.isPresent()) {
				return branchNoIsMinDate.get().getAccountSeq();
			}
		}
		return null;
	}

	/**
	 * 共通処理 共通値の取得
	 *
	 * <pre>
	 * 顧客側、案件側で共通する情報の取得を行います。
	 *
	 * <pre>
	 *
	 * @param gyomuHistorySeq
	 * @return
	 * @throws AppException
	 */
	public CommonInputForm setCommonGyomuHistoryDatas(Long gyomuHistorySeq) throws AppException {

		// アカウント情報の取得
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// 業務履歴情報の取得
		TGyomuHistoryEntity tGyomuHistoryEntity = tGyomuHistoryDao.selectBySeq(gyomuHistorySeq);
		if (tGyomuHistoryEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// 共通Formオブジェクトの作成
		CommonInputForm inputForm = new CommonInputForm();

		// 取得した業務履歴情報をセットする
		inputForm.setGyomuHistorySeq(gyomuHistorySeq);
		inputForm.setTransitionType(tGyomuHistoryEntity.getTransitionType());
		inputForm.setSaibanSeq(tGyomuHistoryEntity.getSaibanSeq());
		inputForm.setSubject(tGyomuHistoryEntity.getSubject());
		inputForm.setMainText(tGyomuHistoryEntity.getMainText());
		inputForm.setImportant(SystemFlg.codeToBoolean(tGyomuHistoryEntity.getImportantFlg()));
		inputForm.setKotei(SystemFlg.codeToBoolean(tGyomuHistoryEntity.getKoteiFlg()));
		inputForm.setSentDengon(SystemFlg.codeToBoolean(tGyomuHistoryEntity.getDengonSentFlg()));
		if (tGyomuHistoryEntity.getSupportedAt() != null) {
			inputForm.setSupportedDate(DateUtils.parseToString(tGyomuHistoryEntity.getSupportedAt().toLocalDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			inputForm.setSupportedTime(DateUtils.parseToString(tGyomuHistoryEntity.getSupportedAt().toLocalTime(), DateUtils.TIME_FORMAT_HHMM));
		}
		inputForm.setVersionNo(tGyomuHistoryEntity.getVersionNo());
		// createrは作成時に必ず登録する想定
		inputForm.setCreaterName(accountNameMap.get(tGyomuHistoryEntity.getCreatedBy()));
		inputForm.setCreateDate(tGyomuHistoryEntity.getCreatedAt());
		// updaterは初期登録時は登録しないため、nullチェックを行う
		if (tGyomuHistoryEntity.getLastEditBy() != null) {
			inputForm.setUpdaterName(accountNameMap.get(tGyomuHistoryEntity.getLastEditBy()));
			inputForm.setUpdateDate(tGyomuHistoryEntity.getLastEditAt());
		}

		return inputForm;
	}

	/**
	 * 共通処理：業務履歴(親テーブル)の登録
	 *
	 * @param inputForm
	 * @return 登録完了後Entityデータ
	 * @throws AppException
	 */
	public TGyomuHistoryEntity insertGyomuHistory(CommonInputForm commonInputForm) throws AppException {

		// 業務履歴情報の作成
		TGyomuHistoryEntity gyomuHistoryEntity = new TGyomuHistoryEntity();

		// 入力情報をセットします
		gyomuHistoryEntity.setTransitionType(commonInputForm.getTransitionType());
		gyomuHistoryEntity.setSaibanSeq(commonInputForm.getSaibanSeq());
		gyomuHistoryEntity.setSubject(commonInputForm.getSubject());
		gyomuHistoryEntity.setMainText(commonInputForm.getMainText());
		gyomuHistoryEntity.setImportantFlg(SystemFlg.booleanToCode(commonInputForm.isImportant()));
		gyomuHistoryEntity.setKoteiFlg(SystemFlg.booleanToCode(commonInputForm.isKotei()));
		gyomuHistoryEntity.setSupportedAt(commonInputForm.getSupportedAt());
		gyomuHistoryEntity.setDengonSentFlg(SystemFlg.FLG_OFF.getCd());

		// 登録処理
		tGyomuHistoryDao.insert(gyomuHistoryEntity);

		// 登録後のEntityを返却
		return gyomuHistoryEntity;
	}

	/**
	 * 共通処理：業務履歴(親テーブル)の更新
	 *
	 * @param commonInputForm
	 * @return 更新完了後Entityデータ
	 * @throws AppException
	 */
	public TGyomuHistoryEntity updateGyomuHistory(CommonInputForm commonInputForm) throws AppException {

		// 業務履歴情報の取得処理
		TGyomuHistoryEntity gyomuHistoryEntity = tGyomuHistoryDao.selectBySeq(commonInputForm.getGyomuHistorySeq());

		// 排他エラーチェック
		if (gyomuHistoryEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力情報をセットします
		gyomuHistoryEntity.setSaibanSeq(commonInputForm.getSaibanSeq());
		gyomuHistoryEntity.setSubject(commonInputForm.getSubject());
		gyomuHistoryEntity.setMainText(commonInputForm.getMainText());
		gyomuHistoryEntity.setImportantFlg(SystemFlg.booleanToCode(commonInputForm.isImportant()));
		gyomuHistoryEntity.setKoteiFlg(SystemFlg.booleanToCode(commonInputForm.isKotei()));
		gyomuHistoryEntity.setSupportedAt(commonInputForm.getSupportedAt());

		// 更新時のみupdater情報をセットします。
		gyomuHistoryEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());
		gyomuHistoryEntity.setLastEditAt(LocalDateTime.now());

		// 更新処理
		tGyomuHistoryDao.update(gyomuHistoryEntity);

		// 更新後のEntityを返却
		return gyomuHistoryEntity;
	}

	/**
	 * 共通処理：業務履歴のステータス更新
	 *
	 * <pre>
	 * 伝言送信した業務履歴のみこっちのupdateを想定
	 * 固定フラグ、重要フラグのみ更新する
	 * </pre>
	 *
	 * @param commonInputForm
	 * @return 更新完了後Entityデータ
	 * @throws AppException
	 */
	public TGyomuHistoryEntity updateGyomuHistoryStatus(CommonInputForm commonInputForm) throws AppException {

		// 業務履歴情報の取得処理
		TGyomuHistoryEntity gyomuHistoryEntity = tGyomuHistoryDao.selectBySeq(commonInputForm.getGyomuHistorySeq());

		// 排他エラーチェック
		if (gyomuHistoryEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 入力情報をセットします
		gyomuHistoryEntity.setImportantFlg(SystemFlg.booleanToCode(commonInputForm.isImportant()));
		gyomuHistoryEntity.setKoteiFlg(SystemFlg.booleanToCode(commonInputForm.isKotei()));

		// 更新処理
		tGyomuHistoryDao.update(gyomuHistoryEntity);

		// 更新後のEntityを返却
		return gyomuHistoryEntity;
	}

	/**
	 * 共通処理：業務履歴(親テーブル)の削除
	 *
	 * @param commonInputForm
	 * @return 削除したEntityデータ
	 * @throws AppException
	 */
	public TGyomuHistoryEntity deleteGyomuHistory(CommonInputForm commonInputForm) throws AppException {

		// 業務履歴情報の取得
		TGyomuHistoryEntity gyomuHistoryEntity = tGyomuHistoryDao.selectBySeq(commonInputForm.getGyomuHistorySeq());

		// 排他エラーチェック
		if (gyomuHistoryEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除処理
		tGyomuHistoryDao.delete(gyomuHistoryEntity);

		// 削除したEntityを返却
		return gyomuHistoryEntity;
	}

	/**
	 * 重要フラグの更新処理
	 *
	 * @param requestData
	 * @throws AppException
	 */
	public void changeImportantFlg(ChangeImportantRequest requestData) throws AppException {

		// 業務履歴情報の取得
		TGyomuHistoryEntity gyomuHistoryEntity = tGyomuHistoryDao.selectBySeq(requestData.getGyomuHistorySeq());

		// 排他エラーチェック
		if (gyomuHistoryEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 重要フラグの設定
		gyomuHistoryEntity.setImportantFlg(CommonUtils.stringBoolanToFlg(requestData.getImportantFlg()));

		// 更新処理
		tGyomuHistoryDao.update(gyomuHistoryEntity);
	}

	/**
	 * 業務履歴-案件の削除
	 *
	 * @param gyomuHistorySeq
	 * @throws AppException
	 */
	public void deleteGyomuHistoryAnken(Long gyomuHistorySeq) throws AppException {

		// 業務履歴-案件情報の取得
		List<TGyomuHistoryAnkenEntity> gyomuHistoryAnkenEntityList = tGyomuHistoryAnkenDao.selectByParentSeq(gyomuHistorySeq);

		// 登録されていない場合、何もしない
		if (gyomuHistoryAnkenEntityList.isEmpty()) {
			return;
		}

		// 削除処理
		tGyomuHistoryAnkenDao.delete(gyomuHistoryAnkenEntityList);

	}

	/**
	 * 業務履歴-顧客の削除
	 *
	 * @param gyomuHistorySeq
	 * @throws AppException
	 */
	public void deleteGyomuHistoryCustomer(Long gyomuHistorySeq) throws AppException {

		// 業務履歴-案件情報の取得
		List<TGyomuHistoryCustomerEntity> gyomuHistoryCustomerEntityList = tGyomuHistoryCustomerDao.selectByParentSeq(gyomuHistorySeq);

		// 登録されていない場合、何もしない
		if (gyomuHistoryCustomerEntityList.isEmpty()) {
			return;
		}

		// 削除処理
		tGyomuHistoryCustomerDao.delete(gyomuHistoryCustomerEntityList);
	}

	/**
	 * 案件-顧客テーブルの整合性チェック
	 *
	 * <pre>
	 * true  : 不整合
	 *
	 * </pre>
	 *
	 * @param customerId
	 * @param ankenIdList
	 * @return 判定結果(boolean)
	 */
	public boolean validReldatingAnkenByCustomerId(Long customerId, List<Long> ankenIdList) {

		List<TAnkenCustomerEntity> tAnkenCustomerEntitiies = tAnkenCustomerDao.selectByCustomerId(customerId);

		// データが一件もない場合
		if (tAnkenCustomerEntitiies.isEmpty()) {
			return true;
		}

		// DB取得、第2引数内の案件IDが一件でも存在しない場合、true
		List<Long> orgAnkenIdList = tAnkenCustomerEntitiies.stream().map(TAnkenCustomerEntity::getAnkenId).collect(Collectors.toList());
		return ankenIdList.stream().anyMatch(id -> !orgAnkenIdList.contains(id));
	}

	/**
	 * 案件-顧客テーブルの整合性チェック
	 *
	 * <pre>
	 * true  : 不整合
	 *
	 * </pre>
	 *
	 * @param ankenId
	 * @param customerIdList
	 * @return 判定結果(boolean)
	 */
	public boolean validReldatingCustomerByAnkenId(Long ankenId, List<Long> customerIdList) {

		List<TAnkenCustomerEntity> tAnkenCustomerEntitiies = tAnkenCustomerDao.selectByAnkenId(ankenId);

		// データが一件もない場合
		if (tAnkenCustomerEntitiies.isEmpty()) {
			return true;
		}

		// DB取得、第2引数内の顧客IDが一件でも存在しない場合、true
		List<Long> orgAnkenIdList = tAnkenCustomerEntitiies.stream().map(TAnkenCustomerEntity::getCustomerId).collect(Collectors.toList());
		return customerIdList.stream().anyMatch(id -> !orgAnkenIdList.contains(id));
	}

	/**
	 * 裁判情報の整合性チェック
	 *
	 * <pre>
	 * true  : 不整合
	 *
	
	 * </pre>
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @return 判定結果(boolean)
	 */
	public boolean validReldatingSaibanByAnkenId(Long saibanSeq, Long ankenId) {

		List<TSaibanEntity> tSaibanEntities = tSaibanDao.selectByAnkenId(ankenId);

		// データが一件もない場合
		if (tSaibanEntities.isEmpty()) {
			return true;
		}

		// 案件に紐づいた裁判か判断する
		List<Long> orgSaibaSeqList = tSaibanEntities.stream().map(TSaibanEntity::getSaibanSeq).collect(Collectors.toList());
		return !orgSaibaSeqList.contains(saibanSeq);
	}

}
