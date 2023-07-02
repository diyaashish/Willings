package jp.loioz.app.user.nyushukkinYotei.shukkin.service;

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
import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchRifineType;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinDetailViewForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ajax.ShukkinSaveRequest;
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
 * 出金予定一覧画面 登録エリア用サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ShukkinDetailService extends DefaultService {

	/** 共通アカウントサービスクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** 共通会計サービスクラス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 顧客Daoクラス */
	@Autowired
	private TPersonDao tCustomerDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 会計記録Daoクラス */
	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	/** 入出金予定Daoクラス */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** 精算記録Daoクラス */
	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	@Autowired
	private MNyushukkinKomokuDao mNyushukkinKomokuDao;

	@Autowired
	private KaikeiEditService kaikeiEditService;

	/** ロガー */
	@Autowired
	private Logger logger;

	// *************************************************************
	// publicメソッド
	// *************************************************************
	/**
	 * 出金予定の詳細情報表示用viewを作成する
	 *
	 * @return 画面表示用オブジェクト
	 */
	public ShukkinDetailViewForm createDetailForm() {
		ShukkinDetailViewForm detailForm = new ShukkinDetailViewForm();
		return detailForm;
	}

	/**
	 * 出金予定の詳細情報表示用viewを作成する
	 *
	 * @param nyushukkinYoteiSeq
	 * @return 画面表示用オブジェクト
	 * @throws AppException
	 */
	public ShukkinDetailViewForm createDetailForm(Long nyushukkinYoteiSeq) throws AppException {
		ShukkinDetailViewForm detailForm = createDetailForm();
		this.setDetailData(nyushukkinYoteiSeq, detailForm);
		return detailForm;
	}

	/**
	 * 出金実績を登録します
	 *
	 * @param shukkinDataMap
	 * @throws AppException
	 */
	public void shukkinSave(ShukkinSaveRequest requestForm) throws AppException {

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
		tNyushukkinYoteiEntity.setNyushukkinGaku(requestForm.getParsedDecimalShukkinGaku());

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
		TKaikeiKirokuEntity registedKaikeiEntity = this.kaikeiKirokuEdit(tKaikeiKirokuEntity, tNyushukkinYoteiEntity);

		// 精算に紐付いている出金予定がすべて実績登録済 -> 精算に紐づく会計記録すべてに「精算処理日」を設定
		if (tNyushukkinYoteiEntity.getNyushukkinDate() != null && tNyushukkinYoteiEntity.getNyushukkinGaku() != null) {
			List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeisanSeq(tNyushukkinYoteiEntity.getSeisanSeq());
			// 精算に紐付いていないケースは何もしない
			if (tKaikeiKirokuEntities == null) {
				return;
			}
			tKaikeiKirokuEntities.forEach(entity -> {
				entity.setSeisanDate(LocalDate.now());
				if (!entity.getKaikeiKirokuSeq().equals(registedKaikeiEntity.getKaikeiKirokuSeq())) {
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
	 * 出金予定の詳細情報表示用データを取得・設定する
	 *
	 * @param nyushukkinYoteiSeq
	 * @param detailForm
	 * @throws AppException
	 */
	private void setDetailData(Long nyushukkinYoteiSeq, ShukkinDetailViewForm detailForm) throws AppException {

		// データの取得
		TNyushukkinYoteiEntity tNyushukkinYoteiEntitiy = tNyushukkinYoteiDao.selectByNyushukkinYoteiSeq(nyushukkinYoteiSeq);
		if (tNyushukkinYoteiEntitiy == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 紐づきデータの取得
		TPersonEntity personEntity = tCustomerDao.selectById(tNyushukkinYoteiEntitiy.getCustomerId());
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
		// ユーザーに紐づく情報
		GinkoKozaDto shukkinKoza = commonKaikeiService.getKozaDetail(tNyushukkinYoteiEntitiy.getShukkinSakiKozaSeq());
		// 顧客側に紐づく情報
		GinkoKozaDto shiharaiSakiKoza = null;
		String shiharaiSakiName = new String();
		if (tNyushukkinYoteiEntitiy.getShukkinCustomerId() != null) {
			// 顧客から選択された場合
			TPersonEntity kozaPerson = tCustomerDao.selectById(tNyushukkinYoteiEntitiy.getShukkinCustomerId());
			shiharaiSakiKoza = commonKaikeiService.getCustomerKozaDetail(tNyushukkinYoteiEntitiy.getShukkinCustomerId());
			shiharaiSakiName = PersonName.fromEntity(kozaPerson).getName();
		} else if (tNyushukkinYoteiEntitiy.getShukkinKanyoshaSeq() != null) {
			// 関与者から選択された場合

			// 精算口座取得
			shiharaiSakiKoza = commonKaikeiService.getKanyoshaKozaDetail(tNyushukkinYoteiEntitiy.getShukkinKanyoshaSeq());

			// 関与者情報取得
			KanyoshaBean kozaKanyoshaBean = tKanyoshaDao.selectNyushukkinKanyoshaBeanByKanyoshaSeq(tNyushukkinYoteiEntitiy.getShukkinKanyoshaSeq());
			if (kozaKanyoshaBean != null) {
				// 関与者名
				shiharaiSakiName = kozaKanyoshaBean.getKanyoshaName();
			}

		} else {
			// 自由入力の場合
			shiharaiSakiName = tNyushukkinYoteiEntitiy.getNyukinShiharaisha();
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

		// ■出金口座情報の設定
		detailForm.setShiharaishaName(StringUtils.null2blank(shukkinKoza.getLabelName()));
		detailForm.setShiharaishaGinkoName(StringUtils.null2blank(shukkinKoza.getGinkoName()));
		detailForm.setShiharaishaShitenName(StringUtils.null2blank(shukkinKoza.getShitenName()));
		detailForm.setShiharaishaShitenNo(StringUtils.surroundBrackets(shukkinKoza.getShitenNo(), false));
		detailForm.setShiharaishaKozaType(KozaType.of(shukkinKoza.getKozaType()));
		detailForm.setShiharaishaKozaNo(StringUtils.null2blank(shukkinKoza.getKozaNo()));
		detailForm.setShiharaishaKozaName(StringUtils.null2blank(shukkinKoza.getKozaName()));

		// ■支払先情報の設定
		// 支払先口座が登録済みか判定
		if (shiharaiSakiKoza == null) {
			// 支払先口座が登録されていないため、表示用に設定する
			shiharaiSakiKoza = new GinkoKozaDto();
			detailForm.setNoDataShiharaisakiKoza(true);
		} else {
			detailForm.setNoDataShiharaisakiKoza(false);
		}
		detailForm.setShiharaiSakiName(shiharaiSakiName);
		detailForm.setShiharaiSakiGinkoName(StringUtils.null2blank(shiharaiSakiKoza.getGinkoName()));
		detailForm.setShiharaiSakiShitenName(StringUtils.null2blank(shiharaiSakiKoza.getShitenName()));
		detailForm.setShiharaiSakiShitenNo(StringUtils.surroundBrackets(shiharaiSakiKoza.getShitenNo(), false));
		detailForm.setShiharaiSakiKozaType(KozaType.of(shiharaiSakiKoza.getKozaType()));
		detailForm.setShiharaiSakiKozaNo(StringUtils.null2blank(shiharaiSakiKoza.getKozaNo()));
		detailForm.setShiharaiSakiKozaName(StringUtils.null2blank(shiharaiSakiKoza.getKozaName()));

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
		Function<TNyushukkinYoteiEntity, ShukkinSearchRifineType> refineTypeMapper = data -> {
			if (data.getNyushukkinDate() == null) {
				return ShukkinSearchRifineType.UNPROCESSED;
			} else {
				return ShukkinSearchRifineType.PROCESSED;
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
		detailForm.setShukkinSearchRifineType(refineTypeMapper.apply(tNyushukkinYoteiEntitiy));
		detailForm.setSeisanCompleted(commonKaikeiService.isSeisanComplete(tNyushukkinYoteiEntitiy.getCustomerId(), tNyushukkinYoteiEntitiy.getAnkenId()));
	}

	/**
	 * 出金実績登録時の会計記録登録/更新処理
	 *
	 * <pre>
	 * 既存データが存在する場合 -> 更新
	 * 既存データが存在しない場合 -> 登録
	 * </pre>
	 *
	 * @param tKaikeiKirokuEntity
	 * @param tNyushukkinYoteiEntity
	 */
	private TKaikeiKirokuEntity kaikeiKirokuEdit(TKaikeiKirokuEntity tKaikeiKirokuEntity, TNyushukkinYoteiEntity tNyushukkinYoteiEntity)
			throws AppException {

		// 会計記録のデータを作成
		if (tKaikeiKirokuEntity != null) {
			// 既にデータがある -> 更新
			tKaikeiKirokuEntity.setHasseiDate(tNyushukkinYoteiEntity.getNyushukkinDate());
			tKaikeiKirokuEntity.setShukkinGaku(tNyushukkinYoteiEntity.getNyushukkinGaku());
			tKaikeiKirokuEntity.setLastEditAt(LocalDateTime.now());
			tKaikeiKirokuEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());

			try {
				tKaikeiKirokuDao.update(tKaikeiKirokuEntity);
			} catch (OptimisticLockingFailureException ex) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}

			// 登録結果を返却
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
			insData.setShukkinGaku(tNyushukkinYoteiEntity.getNyushukkinGaku());
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

			// 登録結果を返却
			return insData;
		}
	}

}
