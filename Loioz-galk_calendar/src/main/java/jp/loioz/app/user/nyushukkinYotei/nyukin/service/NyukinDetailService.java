package jp.loioz.app.user.nyushukkinYotei.nyukin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.user.kaikeiManagement.service.KaikeiEditService;
import jp.loioz.app.user.nyushukkinYotei.nyukin.enums.NyukinSearchRifineType;
import jp.loioz.app.user.nyushukkinYotei.nyukin.form.NyukinDetailViewForm;
import jp.loioz.app.user.nyushukkinYotei.nyukin.form.ajax.NyukinSaveRequest;
import jp.loioz.common.constant.CommonConstant.KozaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.domain.value.SeisanId;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.KanyoshaBean;
import jp.loioz.entity.MNyushukkinKomokuEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TAnkenTantoEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSeisanKirokuEntity;

/**
 * 入金予定一覧画面 登録エリア用サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class NyukinDetailService extends DefaultService {

	/** 共通アカウントServiceクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 金額の計算用 共通Serviceクラス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 名簿情報Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件情報Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件-担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 入出金予定Daoクラス */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** 会計記録Daoクラス */
	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	/** 精算記録Daoクラス */
	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	@Autowired
	private MNyushukkinKomokuDao mNyushukkinKomokuDao;

	@Autowired
	private KaikeiEditService kaikeiEditService;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// *************************************************************
	// publicメソッド
	// *************************************************************

	/**
	 * 入金予定の詳細情報表示用viewを作成する
	 *
	 * @return 画面表示用オブジェクト
	 */
	public NyukinDetailViewForm createDetailForm() {
		NyukinDetailViewForm detailForm = new NyukinDetailViewForm();
		return detailForm;
	}

	/**
	 * 入金予定の詳細情報表示用viewを作成する
	 *
	 * @param nyushukkinYoteiSeq
	 * @return 画面表示用オブジェクト
	 * @throws AppException
	 */
	public NyukinDetailViewForm createDetailForm(Long nyushukkinYoteiSeq) throws AppException {
		NyukinDetailViewForm detailForm = createDetailForm();
		this.setDetailData(nyushukkinYoteiSeq, detailForm);
		return detailForm;
	}

	/**
	 * 入金実績の登録処理
	 *
	 * @param nyukinDataMap
	 * @throws AppException
	 */
	public void nyukinSave(NyukinSaveRequest requestForm) throws AppException {

		Long nyushukkinYoteiSeq = requestForm.getNyushukkinYoteiSeq();

		// データの取得
		TNyushukkinYoteiEntity tNyushukkinYoteiEntity = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);

		// 排他エラーチェック
		if (tNyushukkinYoteiEntity == null || !Objects.equals(requestForm.getVersionNo(), tNyushukkinYoteiEntity.getVersionNo())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 実績を設定
		tNyushukkinYoteiEntity.setNyushukkinDate(requestForm.getNyushukkinLocalDate());
		tNyushukkinYoteiEntity.setNyushukkinGaku(requestForm.getParsedDecimalNyukinGaku());

		try {
			// 登録処理
			tNyushukkinYoteiDao.update(tNyushukkinYoteiEntity);
		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		// 登録した情報をもとに会計記録テーブルの登録/更新処理
		TKaikeiKirokuEntity tKaikeiKirokuEntity = tKaikeiKirokuDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		TKaikeiKirokuEntity registedKaikeiKirokuEntity = this.kaikeiKirokuEdit(tKaikeiKirokuEntity, tNyushukkinYoteiEntity);

		// 精算に紐付いている入金予定がすべて実績登録済 -> 精算に紐づく会計記録すべてに「精算処理日」を設定
		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectBySeisanSeq(tNyushukkinYoteiEntity.getSeisanSeq());
		if (tNyushukkinYoteiEntities.stream().allMatch(entity -> entity.getNyushukkinGaku() != null)) {
			List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeisanSeq(tNyushukkinYoteiEntity.getSeisanSeq());
			if (tKaikeiKirokuEntities == null) {
				return;
			}

			tKaikeiKirokuEntities.forEach(entity -> {
				entity.setSeisanDate(LocalDate.now());
				if (!entity.getKaikeiKirokuSeq().equals(registedKaikeiKirokuEntity.getKaikeiKirokuSeq())) {
					entity.setLastEditAt(LocalDateTime.now());
					entity.setLastEditBy(SessionUtils.getLoginAccountSeq());
				}
			});

			try {
				// 登録処理
				tKaikeiKirokuDao.update(tKaikeiKirokuEntities);
			} catch (OptimisticLockingFailureException ex) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
		}
	}

	// *************************************************************
	// privateメソッド
	// *************************************************************

	/**
	 * 入金予定の詳細情報表示用データを取得する
	 *
	 * @param nyushukkinYoteiSeq
	 * @param detailForm
	 * @throws AppException
	 */
	private void setDetailData(Long nyushukkinYoteiSeq, NyukinDetailViewForm detailForm) throws AppException {

		// データの取得
		TNyushukkinYoteiEntity tNyushukkinYoteiEntitiy = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		if (tNyushukkinYoteiEntitiy == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 紐づきデータの取得
		TPersonEntity personEntity = tPersonDao.selectById(tNyushukkinYoteiEntitiy.getCustomerId());
		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(tNyushukkinYoteiEntitiy.getAnkenId());
		List<TAnkenTantoEntity> tAnkenTantoEntities = tAnkenTantoDao.selectByAnkenId(tNyushukkinYoteiEntitiy.getAnkenId());
		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(tNyushukkinYoteiEntitiy.getSeisanSeq());

		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();
		CustomerId customerId = CustomerId.of(personEntity.getCustomerId());
		AnkenId ankenId = AnkenId.of(tAnkenEntity.getAnkenId());

		// ■担当者情報の取得
		List<String> tantoLawyer = tAnkenTantoEntities.stream()
				.filter(entity -> TantoType.LAWYER.equalsByCode(entity.getTantoType()))
				.map(entity -> accountNameMap.get(entity.getAccountSeq()))
				.collect(Collectors.toList());
		List<String> tantoJimu = tAnkenTantoEntities.stream()
				.filter(entity -> TantoType.JIMU.equalsByCode(entity.getTantoType()))
				.map(entity -> accountNameMap.get(entity.getAccountSeq()))
				.collect(Collectors.toList());

		// ■口座情報の取得

		// ユーザー情報
		GinkoKozaDto hikidashiKoza = commonKaikeiService.getKozaDetail(tNyushukkinYoteiEntitiy.getNyukinSakiKozaSeq());

		// 顧客情報
		GinkoKozaDto seisanSakiKoza = null;
		String shiharaishaName = new String();
		if (tNyushukkinYoteiEntitiy.getNyukinCustomerId() != null) {
			// 顧客から選択された場合
			TPersonEntity kozaPerson = tPersonDao.selectById(tNyushukkinYoteiEntitiy.getNyukinCustomerId());
			seisanSakiKoza = commonKaikeiService.getCustomerKozaDetail(tNyushukkinYoteiEntitiy.getNyukinCustomerId());
			shiharaishaName = PersonName.fromEntity(kozaPerson).getName();

		} else if (tNyushukkinYoteiEntitiy.getNyukinKanyoshaSeq() != null) {
			// 関与者から選択された場合

			// 精算口座取得
			seisanSakiKoza = commonKaikeiService.getKanyoshaKozaDetail(tNyushukkinYoteiEntitiy.getNyukinKanyoshaSeq());

			// 関与者情報取得
			KanyoshaBean kozaKanyoshaBean = tKanyoshaDao.selectNyushukkinKanyoshaBeanByKanyoshaSeq(tNyushukkinYoteiEntitiy.getNyukinKanyoshaSeq());
			if (kozaKanyoshaBean != null) {
				shiharaishaName = kozaKanyoshaBean.getKanyoshaName();
			}

		} else {
			// 自由入力の場合
			shiharaishaName = tNyushukkinYoteiEntitiy.getNyukinShiharaisha();

		}

		SeisanId seisanId = SeisanId.fromEntity(tSeisanKirokuEntity);

		// ■顧客情報の設定
		detailForm.setCustomerId(customerId);
		detailForm.setCustomerName(PersonName.fromEntity(personEntity).getName());

		// ■案件情報の設定
		detailForm.setAnkenId(ankenId);
		detailForm.setAnkenName(tAnkenEntity.getAnkenName());
		detailForm.setTantoLawyer(tantoLawyer);
		detailForm.setTantoJimu(tantoJimu);

		// ■精算先情報の設定
		detailForm.setShiharaishaName(shiharaishaName);

		// 精算口座が登録済みか判定
		if (seisanSakiKoza == null) {
			// 精算口座が登録されていないため、表示用に設定する
			seisanSakiKoza = new GinkoKozaDto();
			detailForm.setNoDatashiharaisakiKoza(true);
		} else {
			detailForm.setNoDatashiharaisakiKoza(false);
		}
		detailForm.setShiharaishaGinkoName(StringUtils.null2blank(seisanSakiKoza.getGinkoName()));
		detailForm.setShiharaishaShitenName(StringUtils.null2blank(seisanSakiKoza.getShitenName()));
		detailForm.setShiharaishaShitenNo(StringUtils.surroundBrackets(seisanSakiKoza.getShitenNo(), false));
		detailForm.setShiharaishaKozaType(KozaType.of(seisanSakiKoza.getKozaType()));
		detailForm.setShiharaishaKozaNo(StringUtils.null2blank(seisanSakiKoza.getKozaNo()));
		detailForm.setShiharaishaKozaName(StringUtils.null2blank(seisanSakiKoza.getKozaName()));

		// ■引き出し口座情報の設定
		detailForm.setShiharaiSakiName(StringUtils.null2blank(hikidashiKoza.getLabelName()));
		detailForm.setShiharaiSakiGinkoName(StringUtils.null2blank(hikidashiKoza.getGinkoName()));
		detailForm.setShiharaiSakiShitenName(StringUtils.null2blank(hikidashiKoza.getShitenName()));
		detailForm.setShiharaiSakiShitenNo(StringUtils.surroundBrackets(hikidashiKoza.getShitenNo(), false));
		detailForm.setShiharaiSakiKozaType(KozaType.of(hikidashiKoza.getKozaType()));
		detailForm.setShiharaiSakiKozaNo(StringUtils.null2blank(hikidashiKoza.getKozaNo()));
		detailForm.setShiharaiSakiKozaName(StringUtils.null2blank(hikidashiKoza.getKozaName()));

		// ■入金情報の設定
		detailForm.setNyushukkinYoteiSeq(tNyushukkinYoteiEntitiy.getNyushukkinYoteiSeq());
		detailForm.setNyushukkinYoteiDate(
				DateUtils.parseToString(tNyushukkinYoteiEntitiy.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		detailForm.setNyushukkinYoteiGaku(tNyushukkinYoteiEntitiy.getNyushukkinYoteiGaku());
		detailForm.setDispNyushukkinYoteiGaku(commonKaikeiService.toDispAmountLabel(tNyushukkinYoteiEntitiy.getNyushukkinYoteiGaku()));
		detailForm.setNyushukkinDate(DateUtils.parseToString(tNyushukkinYoteiEntitiy.getNyushukkinDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		detailForm.setNyushukkinGaku(tNyushukkinYoteiEntitiy.getNyushukkinGaku());
		detailForm.setDispNyushukkinGaku(commonKaikeiService.toDispAmountLabel(tNyushukkinYoteiEntitiy.getNyushukkinGaku()));
		detailForm.setSeisanSeq(Optional.ofNullable(tSeisanKirokuEntity).map(TSeisanKirokuEntity::getSeisanSeq).orElse(null));
		detailForm.setSeisanId(seisanId);
		detailForm.setVersionNo(tNyushukkinYoteiEntitiy.getVersionNo());

		// 未処理・処理済の判定Mapper
		Function<TNyushukkinYoteiEntity, NyukinSearchRifineType> refineTypeMapper = data -> {
			if (data.getNyushukkinDate() == null) {
				return NyukinSearchRifineType.UNPROCESSED;
			} else {
				return NyukinSearchRifineType.PROCESSED;
			}
		};

		// 期限を超えたかの判定Mapper
		Function<TNyushukkinYoteiEntity, Boolean> limitOverMapper = data -> {
			LocalDate shukkinYoteiDate = data.getNyushukkinYoteiDate();
			LocalDate shukkinDate = data.getNyushukkinDate();
			// 予定日が現在日を超えている && 出金日が登録されていない -> true
			return (shukkinYoteiDate.isBefore(LocalDate.now()) && shukkinDate == null);
		};

		// ■その他ステータスの設定
		detailForm.setLimitOver(limitOverMapper.apply(tNyushukkinYoteiEntitiy));
		detailForm.setNyukinSearchRifineType(refineTypeMapper.apply(tNyushukkinYoteiEntitiy));
		detailForm.setSeisanCompleted(commonKaikeiService.isSeisanComplete(tNyushukkinYoteiEntitiy.getCustomerId(), tNyushukkinYoteiEntitiy.getAnkenId()));
	}

	/**
	 * 入金実績登録時の会計記録登録/更新処理
	 *
	 * <pre>
	 * 既存データが存在する場合 -> 更新
	 * 既存データが存在しない場合 -> 登録
	 * </pre>
	 *
	 * @param tKaikeiKirokuEntity
	 * @param tNyushukkinYoteiEntity
	 * @throws AppException
	 */
	private TKaikeiKirokuEntity kaikeiKirokuEdit(TKaikeiKirokuEntity tKaikeiKirokuEntity, TNyushukkinYoteiEntity tNyushukkinYoteiEntity)
			throws AppException {

		// 会計記録のデータを作成
		if (tKaikeiKirokuEntity != null) {
			// 既にデータがある -> 更新
			tKaikeiKirokuEntity.setHasseiDate(tNyushukkinYoteiEntity.getNyushukkinDate());
			tKaikeiKirokuEntity.setNyukinGaku(tNyushukkinYoteiEntity.getNyushukkinGaku());
			tKaikeiKirokuEntity.setLastEditAt(LocalDateTime.now());
			tKaikeiKirokuEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());

			try {
				tKaikeiKirokuDao.update(tKaikeiKirokuEntity);
			} catch (OptimisticLockingFailureException ex) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}
			return tKaikeiKirokuEntity;

		} else {
			// まだ登録されていない場合 -> 登録
			TKaikeiKirokuEntity insData = new TKaikeiKirokuEntity();
			insData.setNyushukkinYoteiSeq(tNyushukkinYoteiEntity.getNyushukkinYoteiSeq());
			insData.setCustomerId(tNyushukkinYoteiEntity.getCustomerId());
			insData.setAnkenId(tNyushukkinYoteiEntity.getAnkenId());
			insData.setHasseiDate(tNyushukkinYoteiEntity.getNyushukkinDate());
			insData.setNyushukkinKomokuId(tNyushukkinYoteiEntity.getNyushukkinKomokuId());
			insData.setNyushukkinType(tNyushukkinYoteiEntity.getNyushukkinType());
			insData.setNyukinGaku(tNyushukkinYoteiEntity.getNyushukkinGaku());
			insData.setUncollectableFlg(SystemFlg.FLG_OFF.getCd());// 回収不能は登録できない
			insData.setSeisanSeq(tNyushukkinYoteiEntity.getSeisanSeq());

			MNyushukkinKomokuEntity nyushukkinKomokuEntity = mNyushukkinKomokuDao.selectById(tNyushukkinYoteiEntity.getNyushukkinKomokuId());
			if (nyushukkinKomokuEntity != null) {

				insData.setTaxFlg(nyushukkinKomokuEntity.getTaxFlg());

				if (!TaxFlg.FREE_TAX.equalsByCode(nyushukkinKomokuEntity.getTaxFlg())) {
					// 課税の場合は消費税率を特定します。
					String taxRate = kaikeiEditService.getTaxRate(tNyushukkinYoteiEntity.getAnkenId(), tNyushukkinYoteiEntity.getCustomerId(), tNyushukkinYoteiEntity.getNyushukkinDate());
					insData.setTaxRate(taxRate);

					// ※外税、内税に関わらず、税額の設定は行わない
				}
			}

			try {
				tKaikeiKirokuDao.insert(insData);
			} catch (OptimisticLockingFailureException ex) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}

			// 登録データを返却
			return insData;
		}
	}

}
