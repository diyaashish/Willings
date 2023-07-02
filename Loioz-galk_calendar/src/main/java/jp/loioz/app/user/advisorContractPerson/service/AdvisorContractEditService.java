package jp.loioz.app.user.advisorContractPerson.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonPersonService;
import jp.loioz.app.user.advisorContractPerson.form.AdvisorContractEditForm;
import jp.loioz.app.user.advisorContractPerson.form.AdvisorContractEditForm.Tanto;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.ContractStatus;
import jp.loioz.common.constant.CommonConstant.ContractType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.TAdvisorContractDao;
import jp.loioz.dao.TAdvisorContractTantoDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.TAdvisorContractEntity;
import jp.loioz.entity.TAdvisorContractTantoEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 顧問契約編集画面（名簿）のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AdvisorContractEditService extends DefaultService {

	/** 顧問契約用のDaoクラス */
	@Autowired
	private TAdvisorContractDao tAdvisorContractDao;

	/** 顧問契約担当用のDaoクラス */
	@Autowired
	private TAdvisorContractTantoDao tAdvisorContractTantoDao;

	/** 名簿情報用のDaoクラス */
	@Autowired
	private TPersonDao tPersonDao;
	
	/** アカウント用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 名簿情報関連の共通サービス処理 */
	@Autowired
	private CommonPersonService commonPersonService;

	/** メッセージを取得するサービスクラス */
	@Autowired
	private MessageService messageService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 顧問契約の登録画面表示用のフォームを取得する
	 * 
	 * @param personId
	 * @return
	 */
	public AdvisorContractEditForm getCreateForm(Long personId) {

		AdvisorContractEditForm editForm = new AdvisorContractEditForm();
		editForm.setPersonId(personId);

		// デフォルト値を設定
		ContractType contractType = ContractType.JIMUSHO;
		editForm.setContractType(contractType.getCd());

		// 新規登録では登録されている担当は存在しないので空のリストにする
		List<TAdvisorContractTantoEntity> contractTantoEntityList = new ArrayList<>();

		// 表示用データを設定
		this.setDisplayData(contractType, contractTantoEntityList, editForm);

		return editForm;
	}

	/**
	 * 顧問契約の編集画面用表示用のフォームを取得する
	 * 
	 * @param personId
	 * @param advisorContractSeq
	 * @return
	 */
	public AdvisorContractEditForm getEditForm(Long personId, Long advisorContractSeq) {

		AdvisorContractEditForm editForm = new AdvisorContractEditForm();
		editForm.setAdvisorContractSeq(advisorContractSeq);
		editForm.setPersonId(personId);

		// 編集情報取得
		TAdvisorContractEntity tAdvisorContractEntity = tAdvisorContractDao.selectBySeq(advisorContractSeq);
		if (tAdvisorContractEntity == null) {
			throw new DataNotFoundException("指定の顧問契約情報が存在しません。SEQ：" + advisorContractSeq);
		}

		// 顧問契約タイプ
		ContractType contractType = ContractType.of(tAdvisorContractEntity.getContractType());

		// 登録されている担当情報取得
		List<TAdvisorContractTantoEntity> contractTantoEntityList = tAdvisorContractTantoDao.selectByAdvisorContractSeq(advisorContractSeq);

		// 表示用データを設定
		this.setDisplayData(contractType, contractTantoEntityList, editForm);

		// 編集情報をフォームに設定
		this.setEntityDataToEditForm(tAdvisorContractEntity, contractTantoEntityList, editForm);

		return editForm;
	}

	/**
	 * 顧問契約の編集画面用表示用のフォームを取得する<br>
	 * ※表示用項目のデータのみを設定し、編集項目のデータは未設定状態
	 * 
	 * @param personId
	 * @param advisorContractSeq
	 * @param contractTypeStr
	 * @return
	 */
	public AdvisorContractEditForm getEditFormOnlySetDisplayData(Long personId, Long advisorContractSeq, String contractTypeStr) {

		AdvisorContractEditForm editForm = new AdvisorContractEditForm();
		editForm.setAdvisorContractSeq(advisorContractSeq);

		// 顧問契約タイプ
		ContractType contractType = ContractType.of(contractTypeStr);

		// 登録されている担当情報取得
		List<TAdvisorContractTantoEntity> contractTantoEntityList = tAdvisorContractTantoDao.selectByAdvisorContractSeq(advisorContractSeq);

		// 表示用データを設定
		this.setDisplayData(contractType, contractTantoEntityList, editForm);

		return editForm;
	}

	/**
	 * 表示用項目のデータを設定
	 * 
	 * @param contractType 表示する画面の顧問契約タイプ
	 * @param contractTantoEntityList 対象の顧問契約情報に登録されている担当情報
	 * @param editForm
	 */
	public void setDisplayData(ContractType contractType, List<TAdvisorContractTantoEntity> contractTantoEntityList, AdvisorContractEditForm editForm) {

		// 契約状況の選択肢リスト
		List<SelectOptionForm> contractStatusOptionList = Arrays.stream(ContractStatus.values())
				.map(e -> new SelectOptionForm(e.getCd(), e.getVal()))
				.collect(Collectors.toList());

		editForm.setContractStatusOptionList(contractStatusOptionList);

		// 契約区分の選択肢リスト
		List<SelectOptionForm> contractTypeOptionList = Arrays.stream(ContractType.values())
				.map(e -> new SelectOptionForm(e.getCd(), e.getVal()))
				.collect(Collectors.toList());

		editForm.setContractTypeOptionList(contractTypeOptionList);

		// 担当情報（売上計上先、担当弁護士、担当事務）の選択リストをフォームに設定
		this.setTantoSelectOptionLists(contractType, contractTantoEntityList, editForm);
	}

	/**
	 * 顧問契約情報の新規登録
	 * 
	 * @param editForm
	 * @throws AppException
	 */
	public void regist(AdvisorContractEditForm editForm) throws AppException {

		Long personId = editForm.getPersonId();
		if (personId == null) {
			throw new IllegalArgumentException("名簿IDが送信されてきていません");
		}

		// 編集不可チェック
		boolean canEditStatus = this.canRegistStatus(personId);
		if (!canEditStatus) {
			// 登録か不可能な状態の場合
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		
		TAdvisorContractEntity insertEntity = new TAdvisorContractEntity();
		insertEntity.setPersonId(personId);

		// フォームの値をエンティティに設定
		this.setEditFormDateToEntity(editForm, insertEntity);

		// 顧問契約情報の登録
		this.insertAdvisorContract(insertEntity);
		// 名簿属性のFlgの更新
		commonPersonService.updatePersonAttributeFlgs(personId);

		// 顧問契約-担当情報の登録
		this.updateAdvisorContractTanto(insertEntity.getAdvisorContractSeq(), editForm);
	}

	/**
	 * 顧問契約情報の更新
	 * 
	 * @param editForm
	 * @throws AppException
	 */
	public void update(AdvisorContractEditForm editForm) throws AppException {

		Long personId = editForm.getPersonId();
		if (personId == null) {
			throw new IllegalArgumentException("名簿IDが送信されてきていません");
		}
		
		Long advisorContractSeq = editForm.getAdvisorContractSeq();
		if (advisorContractSeq == null) {
			throw new IllegalArgumentException("顧問契約SEQが送信されてきていません");
		}

		// 更新対象を取得
		TAdvisorContractEntity updateEntity = tAdvisorContractDao.selectBySeq(advisorContractSeq);
		if (updateEntity == null) {
			throw new DataNotFoundException("指定の顧問契約情報が存在しません。SEQ：" + advisorContractSeq);
		}

		if (!personId.equals(updateEntity.getPersonId())) {
			// 送信されてきた名簿IDと、更新対象エンティティの名簿IDが一致しない場合
			throw new IllegalArgumentException("送信されてきた名簿IDと顧問契約SEQの組み合わせが正しくありません。（その組み合わせのデータが存在しません。）");
		}

		// 更新前と更新後（これから更新する）の契約ステータス
		String updateBeforeContractStatus = updateEntity.getContractStatus();
		String updateAfterContractStatus = editForm.getContractStatus();

		// フォームの値をエンティティに設定
		this.setEditFormDateToEntity(editForm, updateEntity);

		// 顧問契約情報の更新
		this.updateAdvisorContract(updateEntity);
		if (!updateBeforeContractStatus.equals(updateAfterContractStatus)) {
			// 契約ステータスが変更されている場合
			// 名簿属性のFlgの更新
			commonPersonService.updatePersonAttributeFlgs(personId);
		}

		// 顧問契約-担当情報の更新
		this.updateAdvisorContractTanto(advisorContractSeq, editForm);

	}

	/**
	 * 顧問契約の削除と、関連データの削除を行う
	 * 
	 * @param advisorContractSeq
	 * @throws AppException
	 */
	public void deleteContract(Long advisorContractSeq) throws AppException {

		// 削除対象を取得（顧問契約）
		TAdvisorContractEntity deleteContractEntity = tAdvisorContractDao.selectBySeq(advisorContractSeq);
		
		// 削除対象を取得（顧問担当）
		List<TAdvisorContractTantoEntity> deleteContractTantoEntity = tAdvisorContractTantoDao.selectByAdvisorContractSeq(advisorContractSeq);

		// 削除（顧問契約）
		this.deleteContract(deleteContractEntity);
		// 名簿属性のFlgの更新
		commonPersonService.updatePersonAttributeFlgs(deleteContractEntity.getPersonId());
		// 削除（顧問担当）
		this.deleteContractTanto(deleteContractTantoEntity);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * エンティティの情報を編集フォームに設定
	 * 
	 * @param contractEntity 対象の顧問契約情報
	 * @param contractTantoEntityList 対象の顧問契約情報に登録されている担当情報
	 * @param editForm
	 */
	private void setEntityDataToEditForm(TAdvisorContractEntity contractEntity, List<TAdvisorContractTantoEntity> contractTantoEntityList, AdvisorContractEditForm editForm) {

		// 契約開始日
		LocalDate contractStartDate = contractEntity.getContractStartDate();
		if (contractStartDate != null) {
			editForm.setContractStartDate(DateUtils.parseToString(contractStartDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		}
		// 契約終了日
		LocalDate contractEndDate = contractEntity.getContractEndDate();
		if (contractEndDate != null) {
			editForm.setContractEndDate(DateUtils.parseToString(contractEndDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		}

		// 顧問料（月額）
		BigDecimal contractMonthCharge = contractEntity.getContractMonthCharge();
		if (contractMonthCharge != null) {
			editForm.setContractMonthCharge(contractMonthCharge.toPlainString());
		}

		// 稼働時間（時間／月）
		Long contractMonthTime = contractEntity.getContractMonthTime();
		if (contractMonthTime != null) {
			editForm.setContractMonthTime(String.valueOf(contractMonthTime));
		}

		// 契約状況
		editForm.setContractStatus(contractEntity.getContractStatus());
		// 契約区分
		editForm.setContractType(contractEntity.getContractType());
		// 契約内容
		editForm.setContractContent(contractEntity.getContractContent());
		// 契約メモ
		editForm.setContractMemo(contractEntity.getContractMemo());

		// 担当情報（売上計上先、担当弁護士、担当事務）を設定
		this.setTantoEntityDataToEditForm(contractTantoEntityList, editForm);
	}

	/**
	 * 担当のエンティティ情報をフォームに設定する
	 * 
	 * @param contractTantoEntityList 対象の顧問契約情報に登録されている担当情報
	 * @param editForm
	 */
	private void setTantoEntityDataToEditForm(List<TAdvisorContractTantoEntity> contractTantoEntityList, AdvisorContractEditForm editForm) {

		// 担当情報を担当種別でグループ化
		Map<String, List<TAdvisorContractTantoEntity>> groupedTanto = contractTantoEntityList.stream()
				.filter(tantoEntity -> StringUtils.isNotEmpty(tantoEntity.getTantoType()))
				.sorted(Comparator.comparing(TAdvisorContractTantoEntity::getTantoTypeBranchNo))
				.collect(Collectors.groupingBy(TAdvisorContractTantoEntity::getTantoType));

		// 売上計上先
		List<Tanto> salesOwnerList = this.createTantoList(groupedTanto.getOrDefault(TantoType.SALES_OWNER.getCd(), Collections.emptyList()));
		editForm.setSalesOwner(salesOwnerList);

		// 担当弁護士
		List<Tanto> tantoLawyerList = this.createTantoList(groupedTanto.getOrDefault(TantoType.LAWYER.getCd(), Collections.emptyList()));
		editForm.setTantoLawyer(tantoLawyerList);

		// 担当事務
		List<Tanto> tantoJimuList = this.createTantoList(groupedTanto.getOrDefault(TantoType.JIMU.getCd(), Collections.emptyList()));
		editForm.setTantoJimu(tantoJimuList);
	}

	/**
	 * 編集フォームの情報をエンティティに設定
	 * 
	 * <pre>
	 * ※エンティティのキー情報となる、advisorContractSeq、personIdの値の設定は行わない
	 * ※型変換が失敗するような不正値のチェックは、Controller側のバリデーションでチェック済みのため、ここでは行わない
	 * </pre>
	 * 
	 * @param editForm
	 * @param entity
	 */
	private void setEditFormDateToEntity(AdvisorContractEditForm editForm, TAdvisorContractEntity entity) {

		// 契約開始日
		String contractStartDateStr = editForm.getContractStartDate();
		if (StringUtils.isEmpty(contractStartDateStr)) {
			entity.setContractStartDate(null);
		} else {
			entity.setContractStartDate(DateUtils.parseToLocalDate(contractStartDateStr, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		}

		// 契約終了日
		String contractEndDateStr = editForm.getContractEndDate();
		if (StringUtils.isEmpty(contractEndDateStr)) {
			entity.setContractEndDate(null);
		} else {
			entity.setContractEndDate(DateUtils.parseToLocalDate(contractEndDateStr, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		}

		// 顧問料（月額）
		String contractMonthChargeStr = editForm.getContractMonthCharge();
		if (StringUtils.isEmpty(contractMonthChargeStr)) {
			entity.setContractMonthCharge(null);
		} else {
			entity.setContractMonthCharge(new BigDecimal(contractMonthChargeStr));
		}

		// 稼働時間（時間／月）
		String contractMonthTime = editForm.getContractMonthTime();
		if (StringUtils.isEmpty(contractMonthTime)) {
			entity.setContractMonthTime(null);
		} else {
			entity.setContractMonthTime(Long.valueOf(contractMonthTime));
		}

		// 契約状況
		entity.setContractStatus(editForm.getContractStatus());
		// 契約区分
		entity.setContractType(editForm.getContractType());
		// 契約内容
		entity.setContractContent(editForm.getContractContent());
		// 契約メモ
		entity.setContractMemo(editForm.getContractMemo());
	}

	/**
	 * 登録が可能な状態かどうかを判定する
	 * 
	 * @param personId
	 * @return true: 可能、false: 不可能
	 */
	private boolean canRegistStatus(Long personId) {
		
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (personEntity == null) {
			// 名簿情報が存在しない場合は不可
			return false;
		}
		
		CustomerType customerType = CustomerType.of(personEntity.getCustomerType());
		if (CustomerType.LAWYER == customerType) {
			// 名簿情報の種別が「弁護士」の場合は不可（弁護士は顧問契約データを作成できないため）
			return false;
		}
		
		return true;
	}
	
	/**
	 * 顧問契約を登録する
	 * 
	 * @param insertEntity
	 * @throws AppException
	 */
	private void insertAdvisorContract(TAdvisorContractEntity insertEntity) throws AppException {

		int insertCount = tAdvisorContractDao.insert(insertEntity);

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 顧問契約を更新する
	 * 
	 * @param updateEntity
	 * @throws AppException
	 */
	private void updateAdvisorContract(TAdvisorContractEntity updateEntity) throws AppException {

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tAdvisorContractDao.update(updateEntity);
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

	/**
	 * 対象の顧問契約情報の担当を更新する
	 * 
	 * @param advisorContractSeq
	 * @param editForm
	 */
	private void updateAdvisorContractTanto(Long advisorContractSeq, AdvisorContractEditForm editForm) {

		// 登録されている担当情報取得
		List<TAdvisorContractTantoEntity> contractTantoEntityList = tAdvisorContractTantoDao.selectByAdvisorContractSeq(advisorContractSeq);

		// 担当情報を担当種別でグループ化
		Map<String, List<TAdvisorContractTantoEntity>> groupedTanto = contractTantoEntityList.stream()
				.filter(tantoEntity -> StringUtils.isNotEmpty(tantoEntity.getTantoType()))
				.sorted(Comparator.comparing(TAdvisorContractTantoEntity::getTantoTypeBranchNo))
				.collect(Collectors.groupingBy(TAdvisorContractTantoEntity::getTantoType));

		// 更新処理
		updateAdvisorContractTanto(advisorContractSeq, editForm.getSalesOwner(),
				groupedTanto.getOrDefault(TantoType.SALES_OWNER.getCd(), Collections.emptyList()),
				TantoType.SALES_OWNER);
		updateAdvisorContractTanto(advisorContractSeq, editForm.getTantoLawyer(),
				groupedTanto.getOrDefault(TantoType.LAWYER.getCd(), Collections.emptyList()),
				TantoType.LAWYER);
		updateAdvisorContractTanto(advisorContractSeq, editForm.getTantoJimu(),
				groupedTanto.getOrDefault(TantoType.JIMU.getCd(), Collections.emptyList()),
				TantoType.JIMU);
	}

	/**
	 * 担当種別毎の顧問契約担当者を更新する
	 *
	 * @param advisorContractSeq 顧問契約SEQ
	 * @param rawInputItemList 画面入力情報
	 * @param entityList DB情報
	 * @param tantoType 担当種別
	 */
	private void updateAdvisorContractTanto(Long advisorContractSeq, List<Tanto> rawInputItemList, List<TAdvisorContractTantoEntity> entityList, TantoType tantoType) {

		// 画面からの入力情報
		List<Tanto> inputItemList = rawInputItemList.stream()
				.filter(item -> !item.isEmpty())
				.distinct()
				.limit(CommonConstant.CONTRACT_TANTO_ADD_LIMIT)
				.collect(Collectors.toList());

		// i番目のデータの更新ルール
		AtomicLong i = new AtomicLong(0);
		BiConsumer<TAdvisorContractTantoEntity, Tanto> entitySetter = (entity, inputItem) -> {
			entity.setAccountSeq(inputItem.getAccountSeq());
			entity.setTantoType(tantoType.getCd());
			entity.setMainTantoFlg(SystemFlg.booleanToCode(inputItem.isMainTanto()));
			entity.setTantoTypeBranchNo(i.getAndIncrement());
		};

		// 更新処理
		LoiozCollectionUtils.zipping(entityList, inputItemList, (dbValue, inputItem) -> {
			if (inputItem != null) {
				if (dbValue != null) {
					// 入力データあり、既存データあり
					// 既存の担当者を入力データで上書き
					TAdvisorContractTantoEntity entity = dbValue;
					entitySetter.accept(entity, inputItem);
					tAdvisorContractTantoDao.update(entity);

				} else {
					// 入力データあり、既存データなし
					// 担当者を新規登録
					TAdvisorContractTantoEntity entity = new TAdvisorContractTantoEntity();
					entity.setAdvisorContractSeq(advisorContractSeq);
					entitySetter.accept(entity, inputItem);
					tAdvisorContractTantoDao.insert(entity);
				}

			} else {
				if (dbValue != null) {
					// 入力データなし、既存データあり
					// 担当者の数が減ったので削除
					tAdvisorContractTantoDao.delete(dbValue);
				} else {
					// 入力データなし、既存データなし
					// 何もしない
				}
			}
		});
	}

	/**
	 * 対象の顧問契約情報を削除する
	 * 
	 * @param deleteEntity
	 * @throws AppException
	 */
	private void deleteContract(TAdvisorContractEntity deleteEntity) throws AppException {

		if (deleteEntity == null) {
			// 削除対象がない場合は処理しない
			return;
		}

		// 件数チェック用
		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tAdvisorContractDao.delete(deleteEntity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}

		if (deleteCount != 1) {
			// 削除件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 対象の顧問契約担当情報を削除する
	 * 
	 * @param deleteEntityList
	 * @throws AppException
	 */
	private void deleteContractTanto(List<TAdvisorContractTantoEntity> deleteEntityList) throws AppException {

		if (deleteEntityList == null || deleteEntityList.isEmpty()) {
			// 削除対象がない場合は処理しない
			return;
		}

		int[] deleteCountArray;

		try {
			// 削除
			deleteCountArray = tAdvisorContractTantoDao.batchDelete(deleteEntityList);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00014, ex);
		}

		if (deleteCountArray.length != deleteEntityList.size()) {
			// 実際の削除件数と削除対象の件数が異なる場合はエラーとする
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00014, null);
		}
	}

	/**
	 * 担当情報（売上計上先、担当弁護士、担当事務）の選択肢リストをフォームに設定する
	 * 
	 * @param contractType 表示する画面の顧問契約タイプ
	 * @param contractTantoEntityList 対象の顧問契約情報に登録されている担当情報
	 * @param editForm
	 */
	private void setTantoSelectOptionLists(ContractType contractType, List<TAdvisorContractTantoEntity> contractTantoEntityList, AdvisorContractEditForm editForm) {

		// アカウント取得
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccount();

		// 売上計上先
		List<SelectOptionForm> salesOwnerOptionList = this.getTantoOptionList(accountEntityList, TantoType.SALES_OWNER, contractTantoEntityList);
		// 担当弁護士
		List<SelectOptionForm> lawyerOptionList = this.getTantoOptionList(accountEntityList, TantoType.LAWYER, contractTantoEntityList);
		// 担当事務
		List<SelectOptionForm> tantoJimuOptionList = this.getTantoOptionList(accountEntityList, TantoType.JIMU, contractTantoEntityList);

		if (contractType == ContractType.JIMUSHO) {
			// 「事務所契約」の画面を表示する場合 -> 売上計上先の選択肢は「売上計上先」を設定
			editForm.setSalesOwnerOptions(salesOwnerOptionList);
		} else {
			// 「事務所契約」以外（個人契約）の画面を表示する場合 -> 売上計上先の選択肢は「弁護士」を設定
			editForm.setSalesOwnerOptions(lawyerOptionList);
		}
		// 担当弁護士の選択肢リスト
		editForm.setTantoLawyerOptions(lawyerOptionList);
		// 担当事務の選択肢リスト
		editForm.setTantoJimuOptions(tantoJimuOptionList);
	}

	/**
	 * 顧問契約：担当者プルダウン
	 *
	 * @param accountEntityList 有効アカウント
	 * @param tantoType 担当種別
	 * @param contractTantoList すでに設定されている顧問契約担当
	 * @return 担当者プルダウン(すでに登録されているデータを含む)
	 */
	private List<SelectOptionForm> getTantoOptionList(List<MAccountEntity> accountEntityList, TantoType tantoType,
			List<TAdvisorContractTantoEntity> contractTantoEntityList) {

		// プルダウン用オブジェクト
		List<SelectOptionForm> sof = new ArrayList<>();

		// フィルター条件
		Predicate<MAccountEntity> filter = entity -> {
			if (TantoType.SALES_OWNER == tantoType) {
				return AccountType.LAWYER.equalsByCode(entity.getAccountType()) &&
						SystemFlg.FLG_ON.equalsByCode(entity.getAccountOwnerFlg());
			} else if (TantoType.LAWYER == tantoType) {
				return AccountType.LAWYER.equalsByCode(entity.getAccountType());
			} else if (TantoType.JIMU == tantoType) {
				return AccountType.JIMU.equalsByCode(entity.getAccountType());
			} else {
				// 想定外のケース
				throw new RuntimeException();
			}
		};

		// 種別ごとのプルダウンを作成
		List<MAccountEntity> typeOfList = accountEntityList.stream().filter(filter).collect(Collectors.toList());
		typeOfList.forEach(entity -> sof.add(new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName())));

		// 登録データが無い場合はここで返却
		if (ListUtils.isEmpty(contractTantoEntityList)) {
			return sof;
		}

		// Mapper条件
		BiFunction<TantoType, List<TAdvisorContractTantoEntity>, List<Long>> typeByIdMapper = (type, entities) -> {
			return entities.stream()
					.filter(e -> type.equalsByCode(e.getTantoType()))
					.map(TAdvisorContractTantoEntity::getAccountSeq)
					.collect(Collectors.toList());
		};

		// 無条件にプルダウンに含めるユーザーをプルダウンに追加
		List<Long> includeAccountSeq = typeByIdMapper.apply(tantoType, contractTantoEntityList);
		List<Long> deletedAccountSeq = new ArrayList<>(
				LoiozCollectionUtils.subtract(includeAccountSeq, typeOfList.stream().map(MAccountEntity::getAccountSeq).collect(Collectors.toList())));
		List<MAccountEntity> deletedAccountEntities = mAccountDao.selectBySeq(deletedAccountSeq);
		deletedAccountEntities.forEach(entity -> sof.add(new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName())));

		return sof;
	}

	/**
	 * 担当のフォーム情報を作成する
	 *
	 * @param entityList 顧問契約担当DB取得値
	 * @return 担当フォーム情報
	 */
	private List<Tanto> createTantoList(List<TAdvisorContractTantoEntity> entityList) {

		// DB取得値をフォーム情報に変換
		List<Tanto> itemList = entityList.stream()
				.map(entity -> Tanto.builder()
						.accountSeq(entity.getAccountSeq())
						.mainTanto(SystemFlg.codeToBoolean(entity.getMainTantoFlg()))
						.build())
				.filter(item -> !item.isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));

		// 担当追加上限まで空データを追加
		for (int i = itemList.size(); i < CommonConstant.CONTRACT_TANTO_ADD_LIMIT; i++) {
			itemList.add(new Tanto());
		}
		return itemList;
	}

	/**
	 * 担当者の相関チェック
	 *
	 * @param tantoList
	 * @param result
	 * @param fieldName
	 * @param fieldMsgName
	 */
	public BindingResult validateTanto(List<Tanto> tantoList, BindingResult result, String fieldName, String fieldMsgName) {
		// key:アカウントSEQ、value:出現回数 でマッピング
		Map<Long, Long> tantoCountMap = tantoList.stream().filter(tanto -> tanto.getAccountSeq() != null)
				.collect(Collectors.groupingBy(Tanto::getAccountSeq, Collectors.counting()));

		int idx = 0;
		for (Tanto tanto : tantoList) {
			if (tanto.getAccountSeq() != null) {
				Long count = tantoCountMap.get(tanto.getAccountSeq());
				if (count > 1) {
					result.rejectValue(fieldName + "[" + idx + "]", null,
							messageService.getMessage(MessageEnum.MSG_E00038, SessionUtils.getLocale(), fieldMsgName));
				}
				idx++;
			}
		}
		return result;
	}
	
	/**
	 * 売上計上先の登録件数チェック
	 * 
	 * @param tantoList
	 * @param result
	 * @param fieldName
	 */
	public void validateSalesOwnerCount(List<Tanto> tantoList, BindingResult result, String fieldName) {

		if (tantoList.stream().filter(e -> Objects.nonNull(e.getAccountSeq())).count() > CommonConstant.ANKEN_SALES_OWNER_ADD_LIMIT) {

			MessageEnum errorMsgEnum = MessageEnum.MSG_E00189;
			String[] errorMsgArgs = {"売上計上先", CommonConstant.ANKEN_SALES_OWNER_ADD_LIMIT + "名"};

			// 売上計上先の登録可能件数が1件のみ
			result.rejectValue(fieldName, errorMsgEnum.getMessageKey(), errorMsgArgs, messageService.getMessage(errorMsgEnum, SessionUtils.getLocale(), errorMsgArgs));
		}
	}
}
