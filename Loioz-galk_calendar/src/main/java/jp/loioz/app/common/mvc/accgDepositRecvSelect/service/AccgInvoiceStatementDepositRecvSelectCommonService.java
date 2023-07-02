package jp.loioz.app.common.mvc.accgDepositRecvSelect.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.mvc.accgDepositRecvSelect.dto.AccgInvoiceStatementDepositRecvSelectDto;
import jp.loioz.app.common.mvc.accgDepositRecvSelect.form.AccgInvoiceStatementDepositRecvSelectInputForm;
import jp.loioz.app.common.mvc.accgDepositRecvSelect.form.AccgInvoiceStatementDepositRecvSelectSearchForm;
import jp.loioz.app.common.mvc.accgDepositRecvSelect.form.AccgInvoiceStatementDepositRecvSelectViewForm;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.user.depositRecvDetail.form.DepositRecvDetailInputForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.DepositRecvCreatedType;
import jp.loioz.common.constant.CommonConstant.DepositType;
import jp.loioz.common.constant.CommonConstant.ExpenseInvoiceFlg;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozNumberUtils;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.entity.TDepositRecvEntity;

/**
 * 預り金選択モーダルサービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccgInvoiceStatementDepositRecvSelectCommonService extends DefaultService {

	/** 会計管理共通サービス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 預り金Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 預り金選択モーダル表示用Formを作成します
	 * 
	 * @param searchForm
	 * @return
	 */
	public AccgInvoiceStatementDepositRecvSelectViewForm createAccgInvoiceStatementDepositRecvSelectModal(
			AccgInvoiceStatementDepositRecvSelectSearchForm searchForm) {
		Long ankenId = searchForm.getAnkenId();
		Long personId = searchForm.getPersonId();
		Long accgDocSeq = searchForm.getAccgDocSeq();
		String strDepositType = searchForm.getDepositType();
		
		// 案件ID、名簿ID、預り金種別に紐づく預り金データを全て取得
		List<TDepositRecvEntity> depositList = tDepositRecvDao.selectDepositRecvByParams(ankenId, personId, strDepositType);
		
		List<TDepositRecvEntity> canUseDepositList = depositList.stream()
			.filter(depositRecvEntity -> {
				// 請求書／精算書で利用可能（チェック可能）な預り金データのみに絞り込む
				
				// 請求書／精算書で利用可能（チェック可能）かどうか
				boolean isCanUseDepositRecv = false;
				
				// 作成タイプ
				DepositRecvCreatedType createdType = DepositRecvCreatedType.of(depositRecvEntity.getCreatedType());
				// 事務所負担フラグ
				SystemFlg tenantBearFlg = SystemFlg.of(depositRecvEntity.getTenantBearFlg());
				// 会計書類使用先
				Long usingAccgDocSeq = depositRecvEntity.getUsingAccgDocSeq();
				// 入金/出金タイプ
				DepositType depositType = DepositType.of(depositRecvEntity.getDepositType());
				// 実費入金フラグ
				ExpenseInvoiceFlg expenseInvoiceFlg = ExpenseInvoiceFlg.of(depositRecvEntity.getExpenseInvoiceFlg());
				// 入出金完了フラグ
				SystemFlg depositCompleteFlg = SystemFlg.of(depositRecvEntity.getDepositCompleteFlg());
				
				// 対象の預り金データが、対象の請求書／精算書で利用可能（チェック可能）かどうかを判定する
				// ※対象の請求書／精算書で既に利用されている預り金も「利用可能」と判定される
				isCanUseDepositRecv = commonAccgService.isCanUseDepositRecvOnTargetAccgDocSeq(createdType,
					tenantBearFlg, usingAccgDocSeq, depositType, expenseInvoiceFlg, depositCompleteFlg, accgDocSeq);
				
				return isCanUseDepositRecv;
			})
			.collect(Collectors.toList());
		
		// 案件ID、名簿IDに紐づく請求書や精算書で使用されていない事務所負担でない預り金データを取得
		List<Long> excludeDepositRecvSeqList = searchForm.getExcludeDepositRecvSeqList();
		List<AccgInvoiceStatementDepositRecvSelectDto> dtoList = this.convertEntityList2Dto(canUseDepositList,
				excludeDepositRecvSeqList);

		AccgInvoiceStatementDepositRecvSelectViewForm form = new AccgInvoiceStatementDepositRecvSelectViewForm();
		int numberOfRegistDeposit = tDepositRecvDao.selectDepositRecvByParams(ankenId, personId, null).size();
		form.setNumberOfRegistDeposit(numberOfRegistDeposit);
		form.setDepositRecvList(dtoList);
		form.setAnkenId(ankenId);
		form.setPersonId(personId);
		form.setDepositType(strDepositType);

		return form;
	}

	/**
	 * 預り金入力用フォームを作成する
	 * 
	 * @return
	 */
	public DepositRecvDetailInputForm createInputForm() {
		DepositRecvDetailInputForm inputForm = new DepositRecvDetailInputForm();
		return inputForm;
	}

	/**
	 * 項目の候補データを取得します
	 * 
	 * @param searchWord
	 * @param depositType
	 * @return
	 */
	public List<SelectOptionForm> searchDepositRecvDataList(String searchWord, String depositType) {
		return commonAccgService.searchDepositRecvDataList(searchWord, depositType);
	}

	/**
	 * 預り金データを登録します
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void registDepositRecv(AccgInvoiceStatementDepositRecvSelectInputForm inputForm) throws AppException {

		Long ankenId = inputForm.getAnkenId();
		Long personId = inputForm.getPersonId();
		String depositType = null;

		// 既に預り金の登録上限に達している場合
		List<TDepositRecvEntity> list = tDepositRecvDao.selectDepositRecvByParams(ankenId, personId, depositType);
		if (!list.isEmpty() && list.size() >= CommonConstant.DEPOSIT_RECV_ADD_REGIST_LIMIT) {
			// 預り金の登録上限に達しているためエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00174, null, "預り金／実費",
					String.valueOf(CommonConstant.DEPOSIT_RECV_ADD_REGIST_LIMIT));
		}

		// 預り金の登録
		TDepositRecvEntity entity = new TDepositRecvEntity();
		setDepositRecvDetailInputFormToEntity(inputForm, entity);

		int insertCount = 0;

		try {
			
			// 登録
			insertCount = tDepositRecvDao.insert(entity);
			
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
		
		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================


	/**
	 * List<TDepositRecvEntity>型からList<AccgInvoiceStatementDepositRecvSelectDto>型に変換します<br>
	 * 
	 * @param tDepositRecvEntityList
	 * @param excludeDepositRecvSeqList
	 * @return
	 */
	private List<AccgInvoiceStatementDepositRecvSelectDto> convertEntityList2Dto(List<TDepositRecvEntity> tDepositRecvEntityList, List<Long> excludeDepositRecvSeqList) {
		// Entity型をDto型に変換する
		List<AccgInvoiceStatementDepositRecvSelectDto> dtoList = new ArrayList<>();
		tDepositRecvEntityList.forEach(entity -> {
			if (!excludeDepositRecvSeqList.contains(entity.getDepositRecvSeq())) {
				dtoList.add(convertDepositRecvEntity2Dto(entity));
			}
		});
		return dtoList;
	}

	/**
	 * TDepositRecvEntity型からAccgInvoiceStatementDepositRecvSelectDto型に変換します<br>
	 * 
	 * @param tDepositRecvEntity
	 * @return
	 */
	private AccgInvoiceStatementDepositRecvSelectDto convertDepositRecvEntity2Dto(TDepositRecvEntity tDepositRecvEntity) {

		AccgInvoiceStatementDepositRecvSelectDto dto = new AccgInvoiceStatementDepositRecvSelectDto();

		dto.setAnkenId(tDepositRecvEntity.getAnkenId());
		dto.setDepositAmount(AccountingUtils.toDispAmountLabel(tDepositRecvEntity.getDepositAmount()));
		dto.setDepositCompleteFlg(tDepositRecvEntity.getDepositCompleteFlg());
		dto.setDepositDate(DateUtils.parseToString(tDepositRecvEntity.getDepositDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		dto.setDepositItemName(tDepositRecvEntity.getDepositItemName());
		dto.setDepositRecvSeq(tDepositRecvEntity.getDepositRecvSeq());
		dto.setDepositType(tDepositRecvEntity.getDepositType());
		dto.setMemo(tDepositRecvEntity.getDepositRecvMemo());
		dto.setPersonId(tDepositRecvEntity.getPersonId());
		dto.setSumText(tDepositRecvEntity.getSumText());
		dto.setTenantBearFlg(tDepositRecvEntity.getTenantBearFlg());
		dto.setWithdrawalAmount(AccountingUtils.toDispAmountLabel(tDepositRecvEntity.getWithdrawalAmount()));

		return dto;
	}

	/**
	 * 預り金入力フォームの情報を預り金エンティティに設定します
	 * 
	 * @param inputForm
	 * @param entity
	 */
	private void setDepositRecvDetailInputFormToEntity(AccgInvoiceStatementDepositRecvSelectInputForm inputForm, TDepositRecvEntity entity) {

		entity.setPersonId(inputForm.getPersonId());
		entity.setAnkenId(inputForm.getAnkenId());
		entity.setDepositDate(DateUtils.parseToLocalDate(inputForm.getDepositDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		entity.setDepositItemName(inputForm.getDepositItemName());
		entity.setDepositType(inputForm.getDepositType());
		entity.setSumText(inputForm.getSumText());
		entity.setDepositCompleteFlg(SystemFlg.FLG_ON.getCd());

		// 入金額、出金額
		if (DepositType.NYUKIN.equalsByCode(inputForm.getDepositType())) {
			entity.setDepositAmount(LoiozNumberUtils.parseAsBigDecimal(inputForm.getAmountOfMoney()));
			entity.setWithdrawalAmount(null);
		} else {
			entity.setDepositAmount(null);
			entity.setWithdrawalAmount(LoiozNumberUtils.parseAsBigDecimal(inputForm.getAmountOfMoney()));
		}

		// 事務所負担フラグ
		if (inputForm.isTenantBearFlg()) {
			entity.setTenantBearFlg(SystemFlg.FLG_ON.getCd());
		} else {
			if (DepositType.NYUKIN.equalsByCode(inputForm.getDepositType())) {
				// 入金の場合は「事務所負担フラグ」をnullにする
				entity.setTenantBearFlg(null);
			} else {
				entity.setTenantBearFlg(SystemFlg.FLG_OFF.getCd());
			}
		}

		entity.setDepositRecvMemo(inputForm.getDepositRecvMemo());
		entity.setCreatedType(DepositRecvCreatedType.USER_CREATED.getCd());
		entity.setExpenseInvoiceFlg(SystemFlg.FLG_OFF.getCd());
	}

}