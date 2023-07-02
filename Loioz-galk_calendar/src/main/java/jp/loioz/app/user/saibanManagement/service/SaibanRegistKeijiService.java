package jp.loioz.app.user.saibanManagement.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.app.common.service.CommonSelectBoxService;
import jp.loioz.app.user.saibanManagement.form.SaibanCommonInputForm;
import jp.loioz.app.user.saibanManagement.form.SaibanRegistKeijiInputForm;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TSaibanAddKeijiDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.dto.SaibankanDto;
import jp.loioz.entity.TSaibanAddKeijiEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;

/**
 * 裁判管理（刑事）登録画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanRegistKeijiService extends DefaultService {

	/** 裁判管理系画面の共通サービス */
	@Autowired
	private SaibanCommonService saibanCommonService;

	/** 共通プルダウンサービス */
	@Autowired
	private CommonSelectBoxService commonSelectBoxService;

	/** 共通 裁判サービス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 案件情報関連の共通サービス処理 */
	@Autowired
	private CommonAnkenService commonAnkenService;

	@Autowired
	private TSaibanDao tSaibanDao;

	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	@Autowired
	private TSaibanAddKeijiDao tSaibanAddKeijiDao;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 裁判（刑事）の登録フォームを作成する
	 * 
	 * @param ankenId
	 * @return
	 */
	public SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm createRegistInputForm(Long ankenId) {

		SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm inputForm = this.getInitializedSaibanRegistKeijiBasicInputForm(ankenId);

		// フォームの初期値の設定

		LocalDate now = LocalDate.now();

		// 事件元号
		EraType eraType = DateUtils.getWarekiEraType(now);
		inputForm.setJikenGengo(eraType);

		// 事件番号年
		long warekiYear = DateUtils.getWarekiYearLong(now);
		inputForm.setJikenYear(String.valueOf(warekiYear));

		// 裁判官（空の入力枠を1つ設定）
		List<SaibankanDto> saibankanDtoList = Arrays.asList(new SaibankanDto());
		List<SaibanCommonInputForm.SaibankanInputForm> saibankanInputFormList = saibanCommonService.convertSaibankanDtoToInputForm(saibankanDtoList);
		inputForm.setSaibankanInputFormList(saibankanInputFormList);

		Map<TantoType, List<AnkenTantoSelectInputForm>> tantoListMap = commonSaibanService.getTantoListMapForRegist(ankenId);
		// 担当弁護士
		inputForm.setTantoLawyer(tantoListMap.get(TantoType.LAWYER));
		// 担当事務
		inputForm.setTantoJimu(tantoListMap.get(TantoType.JIMU));

		return inputForm;
	}

	/**
	 * 入力フォームに表示用のデータを設定する。
	 * 
	 * <pre>
	 * リクエストパラメータや、画面の入力項目に表示する値ではない、
	 * 表示処理のみで利用する（HTML、もしくはHTMLから呼ばれるFormのメソッドでのみ参照される）データをinputフォームに設定する。
	 * 
	 * つまり、このメソッドで設定すプロパティがinputフォームから削除された場合、
	 * 影響があるのはHTMLとFormのみとならなければならなず、そうならないような箇所で
	 * このメソッドで設定するプロパティを参照してはいけない。
	 * </pre>
	 * 
	 * @param inputForm
	 * @param ankenId
	 */
	public void setDisplayData(SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm inputForm, Long ankenId) {

		// 案件ID
		inputForm.setAnkenId(ankenId);

		// 事件元号プルダウン
		List<SelectOptionForm> jikenGengoOptionList = commonSelectBoxService.getSaibanEraType();
		inputForm.setJikenGengoOptionList(jikenGengoOptionList);

		// 裁判所マスタプルダウン
		List<SelectOptionForm> saibanshoOptionList = commonSelectBoxService.getSaibanshoSelectBox();
		inputForm.setSaibanshoOptionList(saibanshoOptionList);

		// 担当（担当弁護士、担当事務）の選択肢
		Map<TantoType, List<SelectOptionForm>> tantoSelectOptListMap = commonSaibanService.getTantoSelectOptListMapForRegist(ankenId);
		inputForm.setAnkenTantoBengoshiOptionList(tantoSelectOptListMap.get(TantoType.LAWYER));
		inputForm.setAnkenTantoJimuOptionList(tantoSelectOptListMap.get(TantoType.JIMU));
	}

	/**
	 * 裁判（刑事）の情報を登録する
	 * 
	 * @param basicInputForm
	 * @param ankenId
	 * @throws AppException
	 */
	public void registSaibanKeiji(SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm basicInputForm, Long ankenId) throws AppException {

		// 案件の分野のタイプが刑事になっているかのチェック（画面表示後に案件の分野が変更された場合の楽観ロックチェック）
		boolean isKeiji = commonAnkenService.isKeiji(ankenId);
		if (!isKeiji) {
			// 刑事ではない -> 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		////////////////////////////////////////////////
		// 基本情報の登録
		////////////////////////////////////////////////

		// 裁判情報の登録
		TSaibanEntity saibanEntity = this.insertSaibanBasic(basicInputForm, ankenId);
		Long saibanSeq = saibanEntity.getSaibanSeq();

		// 裁判の事件情報の登録
		TSaibanJikenEntity saibanJikenEntity = this.insertSaibanJiken(basicInputForm, saibanSeq);
		Long jikenSeq = saibanJikenEntity.getJikenSeq();

		// 刑事裁判の付帯情報の登録
		this.insertSaibanAddKeiji(basicInputForm, saibanSeq, jikenSeq);

		// 裁判の裁判官情報の登録
		saibanCommonService.insertSaibanSaibankan(saibanSeq, basicInputForm.getSaibankanInputFormList());

		// 裁判の担当者情報を更新
		this.insertSaibanTanto(saibanSeq, basicInputForm);

		////////////////////////////////////////////////
		// 入力する情報以外の初期値データの登録
		////////////////////////////////////////////////

		// 裁判の顧客情報の登録
		commonSaibanService.registInitSaibanCustomer(saibanSeq, ankenId);

		// 裁判の共犯者情報の登録
		commonSaibanService.registInitSaibanRelatedKanyosha(saibanSeq, ankenId, KanyoshaType.KYOHANSHA);

		// フォームに登録された裁判SEQを設定
		basicInputForm.setSaibanSeq(saibanSeq);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 初期設定済みの裁判（刑事）の登録フォームを取得
	 * 
	 * @param ankenId
	 * @return
	 */
	private SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm getInitializedSaibanRegistKeijiBasicInputForm(Long ankenId) {

		SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm inputForm = new SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm, ankenId);

		return inputForm;
	}

	/**
	 * 裁判情報を登録
	 * 
	 * @param basicInputForm
	 * @param ankenId
	 * @return
	 * @throws AppException
	 */
	private TSaibanEntity insertSaibanBasic(SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm basicInputForm, Long ankenId) throws AppException {

		// 現在の枝番の最大値を取得
		long currentMaxBranchNumber = tSaibanDao.getMaxBranchNo(ankenId);

		TSaibanEntity insertEntity = new TSaibanEntity();

		insertEntity.setAnkenId(ankenId);
		insertEntity.setSaibanBranchNo(currentMaxBranchNumber + 1);
		insertEntity.setSaibanStatus(SaibanStatus.calc(basicInputForm.getSaibanStartDate(), basicInputForm.getSaibanEndDate()).getCd());
		insertEntity.setSaibanStartDate(basicInputForm.getSaibanStartDate());
		insertEntity.setSaibanEndDate(basicInputForm.getSaibanEndDate());
		insertEntity.setSaibanshoId(basicInputForm.getSaibanshoId());
		insertEntity.setSaibanshoName(basicInputForm.getSaibanshoName());
		insertEntity.setKeizokuBuName(basicInputForm.getKeizokuBuName());
		insertEntity.setKeizokuKakariName(basicInputForm.getKeizokuKakariName());
		insertEntity.setKeizokuBuTelNo(basicInputForm.getKeizokuBuTelNo());
		insertEntity.setKeizokuBuFaxNo(basicInputForm.getKeizokuBuFaxNo());
		insertEntity.setTantoShoki(basicInputForm.getTantoShoki());
		insertEntity.setSaibanResult(basicInputForm.getSaibanResult());

		// 裁判所IDが入力されている場合は、自由入力項目「裁判所名」はnullをセット
		if (Objects.nonNull(insertEntity.getSaibanshoId())) {
			insertEntity.setSaibanshoName(null);
		}

		int insertCount = 0;

		try {
			// 裁判登録
			insertCount = tSaibanDao.insert(insertEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return insertEntity;
	}

	/**
	 * 裁判の事件情報の登録
	 * 
	 * @param basicInputForm
	 * @param saibanSeq
	 * @return
	 * @throws AppException
	 */
	private TSaibanJikenEntity insertSaibanJiken(SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm basicInputForm, Long saibanSeq) throws AppException {

		TSaibanJikenEntity insertEntity = new TSaibanJikenEntity();

		insertEntity.setSaibanSeq(saibanSeq);
		insertEntity.setJikenGengo(DefaultEnum.getCd(basicInputForm.getJikenGengo()));
		insertEntity.setJikenYear(basicInputForm.getJikenYear());
		insertEntity.setJikenMark(basicInputForm.getJikenMark());
		insertEntity.setJikenNo(basicInputForm.getJikenNo());
		insertEntity.setJikenName(basicInputForm.getJikenName());

		int insertCount = 0;

		try {
			// 事件登録
			insertCount = tSaibanJikenDao.insert(insertEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}

		return insertEntity;
	}

	/**
	 * 刑事裁判の付帯情報の登録
	 * 
	 * @param basicInputForm
	 * @param saibanSeq
	 * @param mainJikenSeq
	 * @throws AppException
	 */
	private void insertSaibanAddKeiji(SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm basicInputForm, Long saibanSeq, Long mainJikenSeq) throws AppException {

		TSaibanAddKeijiEntity insertEntity = new TSaibanAddKeijiEntity();

		insertEntity.setSaibanSeq(saibanSeq);
		insertEntity.setMainJikenSeq(mainJikenSeq);

		int insertCount = 0;

		try {
			// 刑事裁判付帯情報登録
			insertCount = tSaibanAddKeijiDao.insert(insertEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, ex);
		}

		if (insertCount != 1) {
			// 登録処理に失敗した場合
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_INSERTED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00012, null);
		}
	}

	/**
	 * 裁判-担当者を登録する
	 * 
	 * @param saibanSeq
	 * @param basicInputForm
	 */
	private void insertSaibanTanto(Long saibanSeq, SaibanRegistKeijiInputForm.SaibanRegistKeijiBasicInputForm basicInputForm) {

		List<AnkenTantoSelectInputForm> tantoLawyerInputItemList = basicInputForm.getTantoLawyer();
		List<AnkenTantoSelectInputForm> tantoJimuInputItemList = basicInputForm.getTantoJimu();

		commonSaibanService.insertSaibanTanto(saibanSeq, tantoLawyerInputItemList, tantoJimuInputItemList);
	}

}
