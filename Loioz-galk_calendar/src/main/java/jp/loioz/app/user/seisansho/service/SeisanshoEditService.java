package jp.loioz.app.user.seisansho.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.validation.accessDB.CommonAnkenValidator;
import jp.loioz.app.common.validation.accessDB.CommonCustomerValidator;
import jp.loioz.app.common.validation.accessDB.CommonKaikeiValidator;
import jp.loioz.app.user.seisansho.form.SeisanshoEditForm;
import jp.loioz.app.user.seisansho.form.SeisanshoViewForm;
import jp.loioz.app.user.seisansho.form.SeisanshoViewForm.Anken;
import jp.loioz.app.user.seisansho.form.SeisanshoViewForm.MonthShiharaiDate;
import jp.loioz.app.user.seisansho.form.SeisanshoViewForm.ShiharaiStartYear;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.GensenChoshu;
import jp.loioz.common.constant.CommonConstant.GensenTarget;
import jp.loioz.common.constant.CommonConstant.Hasu;
import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.common.constant.CommonConstant.NyushukkinKomokuType;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.PoolType;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.SeisanKirokuKubun;
import jp.loioz.common.constant.CommonConstant.SeisanShiharaiMonthDay;
import jp.loioz.common.constant.CommonConstant.SeisanStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.common.utility.data.LoiozDecimalFormat;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TCustomerCommonDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.dao.TSeisanshoDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.domain.value.SeisanId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.KaikeiKirokuBean;
import jp.loioz.dto.SeisanshoCreateDto;
import jp.loioz.dto.SeisanshoDto;
import jp.loioz.dto.SeisanshoPoolAnkenBean;
import jp.loioz.entity.MNyushukkinKomokuEntity;
import jp.loioz.entity.TAnkenCustomerEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSeisanKirokuEntity;

/**
 * 精算書作成画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SeisanshoEditService extends DefaultService {

	/** 会計系の共通サービス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 共通：顧客 DBアクセスのバリデータークラス */
	@Autowired
	private CommonCustomerValidator commonCustomerValidator;

	/** 共通：案件 DBアクセスのバリデータークラス */
	@Autowired
	private CommonAnkenValidator commonAnkenValidator;

	/** 共通：会計系 DBアクセスのバリデータークラス */
	@Autowired
	private CommonKaikeiValidator commonKaikeiValidator;

	/** 案件明細・会計記録のDaoクラス */
	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	/** 入出金予定のDaoクラス */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件顧客Dao */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/** 精算書作成のDaoクラス */
	@Autowired
	private TSeisanshoDao tSeisanshoDao;

	/** 精算記録のDaoクラス */
	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	/** 入出金項目マスタのDaoクラス */
	@Autowired
	private MNyushukkinKomokuDao mNyushukkinDao;

	/** 顧客井共通のDaoクラス */
	@Autowired
	private TCustomerCommonDao tCustomerCommonDao;

	/** 月末の定義を31日 */
	private static final String END_OF_MONTH = "31";

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 精算書作成画面の表示情報を作成します。
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void createViewForm(SeisanshoViewForm viewForm) throws AppException {

		// 遷移元の情報を設定
		String transitionType = viewForm.getTransitionType();
		if (TransitionType.CUSTOMER.equalsByCode(transitionType)) {
			// 1：顧客管理から遷移
			viewForm.setTransitionCustomerId(viewForm.getSeisanCustomerId().asLong());
			// 遷移元を設定
			viewForm.setTransitionFlg(SystemFlg.FLG_OFF.getCd());
		} else if (TransitionType.ANKEN.equalsByCode(transitionType)) {
			// 2：案件管理から遷移
			viewForm.setTransitionAnkenId(viewForm.getSeisanAnkenId().asLong());
			// 遷移元を設定
			viewForm.setTransitionFlg(SystemFlg.FLG_ON.getCd());
		}

		Long seisanCustomerId = null;
		Long seisanAnkenId = null;
		List<GinkoKozaDto> ginkoKozaListForTenantAndAccount = null;

		Long seisanSeq = viewForm.getSeisanSeq();
		if (seisanSeq != null) {
			// 編集の場合

			// 画面項目取得
			this.editViewForm(viewForm);
			seisanCustomerId = viewForm.getSeisanCustomerId().asLong();
			seisanAnkenId = viewForm.getSeisanAnkenId().asLong();

		} else {
			// 新規登録の場合

			// 会計管理画面のパラメータを設定
			seisanCustomerId = viewForm.getSeisanCustomerId().asLong();
			seisanAnkenId = viewForm.getSeisanAnkenId().asLong();

			// 顧客の口座詳細情報取得
			GinkoKozaDto ginkoKozaDto = commonKaikeiService.getCustomerKozaDetail(seisanCustomerId);
			if (ginkoKozaDto != null) {

				// 口座情報１
				viewForm.setKozaInfo1(ginkoKozaDto.getDispGinkoKozaInfo1());
				// 口座情報２
				viewForm.setKozaInfo2(ginkoKozaDto.getDispGinkoKozaInfo2());

				// Dtoに設定します。
				SeisanshoCreateDto createDto = new SeisanshoCreateDto();
				viewForm.setSeisanshoCreateDto(createDto);
				// 新規登録のため
				viewForm.setSeisanComplete(false);
			}

			// 事務所・売上計上先弁護士の口座情報
			ginkoKozaListForTenantAndAccount = commonKaikeiService.getTenantAccountGinkoKozaList(seisanAnkenId);
			viewForm.setTenantAccountGinkoKozaList(ginkoKozaListForTenantAndAccount);

			// 会計記録情報を取得します。
			List<KaikeiKirokuBean> kaikeiKirokuBeanList = tSeisanshoDao.selectByCustomerIdAnkenId(seisanCustomerId, seisanAnkenId);
			List<SeisanshoDto> seisanshoDtoList = this.kaikeiKirokuBean2Dto(kaikeiKirokuBeanList);

			// 会計記録情報を設定します。
			viewForm.setSeisanHoshuList(this.dispTargetKaikeiKirokuList(seisanshoDtoList, true));
			viewForm.setSeisanNyushukkinList(this.dispTargetKaikeiKirokuList(seisanshoDtoList, false));

			List<SeisanshoDto> filteredSeisanshoDtoList = seisanshoDtoList.stream()
					.filter(seisanshoDto -> {
						// 精算書が発行されている会計記録は合計金額計算からは除外
						return seisanshoDto.getSeisanId() == null;
					}).collect(Collectors.toList());

			// 画面表示用の合計金額を計算・設定します。
			this.calcTotalForKaikeiKiroku(viewForm, filteredSeisanshoDtoList);

			// Dtoに設定
			SeisanshoCreateDto createDto = new SeisanshoCreateDto();
			// 初期表示

			viewForm.setSeisanshoCreateDto(createDto);

		}

		// 顧客名を取得
		TPersonEntity personEntity = tPersonDao.selectById(seisanCustomerId);
		viewForm.setCustomerName(PersonName.fromEntity(personEntity).getName());

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// 案件名取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(seisanAnkenId);
		viewForm.setAnkenName(tAnkenEntity.getAnkenName());
		viewForm.setBunyaName(bunyaMap.get(tAnkenEntity.getBunyaId()).getVal());
		viewForm.setBunyaId(tAnkenEntity.getBunyaId());

		/* 案件に紐づく関与者一覧を取得する */
		List<CustomerKanyoshaPulldownDto> kanyoshaList = tCustomerCommonDao.selectKanyoshaPulldownByAnkenId(seisanAnkenId);
		viewForm.setKanyoshaList(kanyoshaList);

		// viewFormに格納する案件情報変換条件
		Function<TAnkenEntity, Anken> mapper = entity -> {
			return Anken.builder()
					.ankenId(entity.getAnkenId())
					.displayAnkenId(AnkenId.of(entity.getAnkenId()))
					.bunya(bunyaMap.get(entity.getBunyaId()).getVal())
					.ankenType(AnkenType.of(entity.getAnkenType()).getVal())
					.ankenName(entity.getAnkenName())
					.build();
		};

		// 案件情報、プール可能案件を取得・設定
		List<TAnkenCustomerEntity> tAnkenCutomerEntities = tAnkenCustomerDao.selectByCustomerId(seisanCustomerId);

		Long filterSeisanAnkenId = seisanAnkenId;

		// プール可能案件条件
		Predicate<TAnkenCustomerEntity> statusFilter = e -> !AnkenStatus.isSeisanComp(e.getAnkenStatus());
		Predicate<TAnkenCustomerEntity> targetAnkenFilter = e -> !Objects.equals(e.getAnkenId(), filterSeisanAnkenId);

		// プール可能案件IDを抽出
		List<Long> ankenIdList = tAnkenCutomerEntities.stream()
				.filter(targetAnkenFilter)
				.filter(statusFilter)
				.map(TAnkenCustomerEntity::getAnkenId)
				.collect(Collectors.toList());

		// プール可能案件を取得・加工・設定
		List<TAnkenEntity> tAnkenEntities = tAnkenDao.selectById(ankenIdList);
		List<Anken> canPoolAnkenList = tAnkenEntities.stream().map(mapper).collect(Collectors.toList());
		viewForm.setCanPoolAnkenList(canPoolAnkenList);

		// 毎月の支払日リスト
		List<MonthShiharaiDate> monthShiharaiDateList = this.getMonthShihraiDateList();
		viewForm.setMonthShiharaiDateList(monthShiharaiDateList);

		// 支払開始年リスト
		List<ShiharaiStartYear> shiharaiStartYearList = this.getShiharaiStartYear();
		viewForm.setShiharaiStartYearList(shiharaiStartYearList);

		// 未設定の初期値を設定
		defaultDtoSetting(viewForm);
	}

	/**
	 * 未設定の項目に初期値を設定
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	private void defaultDtoSetting(SeisanshoViewForm viewForm) throws AppException {
		SeisanshoCreateDto createDto = viewForm.getSeisanshoCreateDto();
		// 当年を初期値に設定
		LocalDate now = LocalDate.now();

		// 入金予定日（年）
		String shiharaiStartYear = createDto.getShiharaiStartYear();
		if (shiharaiStartYear == null) {
			createDto.setShiharaiStartYear(String.valueOf(now.getYear()));
		}
		// 入金予定日（月）
		String shiharaiStartMonth = createDto.getShiharaiStartMonth();
		if (shiharaiStartMonth == null) {
			// 当月を初期値に設定
			createDto.setShiharaiStartMonth(String.valueOf(now.getMonthValue()));
		}
	}

	/**
	 * 入力値のDB整合性チェック
	 * 
	 * @param editForm
	 * @return
	 */
	public boolean accessDBValidated(SeisanshoEditForm editForm) {

		// 顧客と案件の関連があるか確認する
		Long seisanCustomerId = editForm.getSeisanCustomerId();
		if (!commonCustomerValidator.isValidRelatingAnken(seisanCustomerId, editForm.getSeisanAnkenId())) {
			return true;
		}

		// 会計記録が、顧客IDと案件IDに紐づくデータか確認する
		if (!commonKaikeiValidator.isValidRelatingKaikeiKiroku(editForm.getKaikeiSeqList(), seisanCustomerId,
				editForm.getSeisanAnkenId())) {
			return true;
		}

		// 精算書作成時の作成条件
		SeisanshoCreateDto dto = editForm.getSeisanshoCreateDto();

		if (SeisanKirokuKubun.SEISAN.equalsByCode(dto.getSeisanKubun())) {
			// 精算の時(金額が0以上の時)

			if (editForm.isSeisanZero()) {
				// 精算額がゼロの場合、falseを返却(入力項目がないため)
				return false;
			}

			// プールの時
			if (dto.isPoolFlg()) {
				// プール先案件のDB整合性チェック
				if (!commonCustomerValidator.isValidRelatingAnken(seisanCustomerId, dto.getPoolAnkenId())) {
					return true;
				}

				// 完了済案件にプールしているかのチェック
				if (!commonKaikeiValidator.isValidCanKaikeiPool(seisanCustomerId, editForm.getSeisanAnkenId(),
						dto.getPoolAnkenId())) {
					return true;
				}
			}

			// 返金先口座 DB整合性チェック
			if (TargetType.CUSTOMER.equalsByCode(dto.getHenkinSakiType())) {
				// 返金先に指定した顧客が案件に紐づくか検証
				if (!commonCustomerValidator.isValidRelatingAnken(dto.getHenkinCustomerId(), editForm.getSeisanAnkenId())) {
					return true;
				}
			} else if (TargetType.KANYOSHA.equalsByCode(dto.getHenkinSakiType())) {
				// 返金先に指定した関与者が案件に紐づくか検証
				if (!commonAnkenValidator.isValidRelatingKanyosha(editForm.getSeisanAnkenId(), dto.getHenkinKanyoshaSeq())) {
					return true;
				}
			} else {
				// なにもしない
			}

			TSeisanKirokuEntity seisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(dto.getSeisanSeq());
			if (seisanKirokuEntity != null) {
				// 編集の場合

				// 返金者口座のDB整合性チェック
				Long henkinKozaSeq = seisanKirokuEntity.getHenkinKozaSeq();
				boolean sameAsNowSavedKozaSeq = (henkinKozaSeq != null && henkinKozaSeq.equals(dto.getHenkinKozaSeq()));
				if (sameAsNowSavedKozaSeq) {
					// 現在保存されている値の更新の場合はチェックを行わない（そのまま保存可能とする）
				} else {
					if (!commonKaikeiValidator.isValidRelatingUsersKozaByAnken(editForm.getSeisanAnkenId(), dto.getHenkinKozaSeq())) {
						return true;
					}
				}

			} else {
				// 新規登録の場合

				// 返金者口座のDB整合性チェック
				if (!commonKaikeiValidator.isValidRelatingUsersKozaByAnken(editForm.getSeisanAnkenId(), dto.getHenkinKozaSeq())) {
					return true;
				}
			}

		} else if (SeisanKirokuKubun.SEIKYU.equalsByCode(dto.getSeisanKubun())) {
			// 請求の時 (金額がマイナスのとき)

			// 支払者 DB整合性チェック
			if (TargetType.CUSTOMER.equalsByCode(dto.getShiharaishaType())) {
				// 支払者に指定した顧客が案件に紐づくか検証
				if (!commonCustomerValidator.isValidRelatingAnken(dto.getShiharaiCustomerId(), editForm.getSeisanAnkenId())) {
					return true;
				}
			} else if (TargetType.KANYOSHA.equalsByCode(dto.getShiharaishaType())) {
				// 支払者に指定した関与者が案件に紐づくか検証
				if (!commonAnkenValidator.isValidRelatingKanyosha(editForm.getSeisanAnkenId(), dto.getShiharaiKanyoshaSeq())) {
					return true;
				}
			} else {
				// なにもしない
			}

			TSeisanKirokuEntity seisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(dto.getSeisanSeq());
			if (seisanKirokuEntity != null) {
				// 編集の場合

				// 請求口座のDB整合性チェック
				Long seikyuKozaSeq = seisanKirokuEntity.getSeikyuKozaSeq();
				boolean sameAsNowSavedKozaSeq = (seikyuKozaSeq != null && seikyuKozaSeq.equals(dto.getSeikyuKozaSeq()));
				if (sameAsNowSavedKozaSeq) {
					// 現在保存されている値の更新の場合はチェックを行わない（そのまま保存可能とする）
				} else {
					if (!commonKaikeiValidator.isValidRelatingUsersKozaByAnken(editForm.getSeisanAnkenId(), dto.getSeikyuKozaSeq())) {
						return true;
					}
				}

			} else {
				// 新規登録の場合

				// 請求口座のDB整合性チェック
				if (!commonKaikeiValidator.isValidRelatingUsersKozaByAnken(editForm.getSeisanAnkenId(), dto.getSeikyuKozaSeq())) {
					return true;
				}
			}

		} else {
			// 想定外のケース
			throw new RuntimeException();
		}

		// ここまできたら不整合がないのでfalseで返却
		return false;
	}

	/**
	 * 精算記録-登録
	 *
	 * @param editForm
	 * @throws AppException
	 */
	public void registSeisanKiroku(SeisanshoEditForm editForm) throws AppException {

		// 選択した報酬項目、入出金項目を取得
		List<Long> kaikeiSeqList = editForm.getKaikeiSeqList();
		// 選択した報酬項目、入出金項目を計算
		SeisanshoViewForm viewForm = this.recalculation(kaikeiSeqList);

		// 精算記録情報を登録します。
		Long seisanSeq = this.insertSeisankirokuData(editForm, viewForm);
		editForm.getSeisanshoCreateDto().setSeisanSeq(seisanSeq);

		// 会計記録情報を更新します。
		this.updateKaikeikiroku(seisanSeq, kaikeiSeqList, editForm.isSeisanZero());

		// 入出金予定情報を登録します。
		this.deleteInsertNyushukkinYotei(viewForm, editForm);
	}

	/**
	 * 精算記録 プールの保存処理
	 * ※ プール精算 -> プール精算は未考慮
	 * 
	 * @param editForm
	 * @throws AppException
	 */
	public void saveSeisanKirokuForPool(SeisanshoEditForm editForm) throws AppException {

		List<Long> kaikeiSeqList = editForm.getKaikeiSeqList();

		// 精算金額の合計を算出する -> 精算額の算出ロジックの作りがいまいちだが影響範囲や修正が多くなるため一旦このまま
		SeisanshoViewForm viewForm = this.recalculation(kaikeiSeqList);
		BigDecimal seisanTotal = viewForm.getSeisangakuTotal();

		SeisanshoCreateDto seisanshoCreateDto = editForm.getSeisanshoCreateDto();
		Long seisanSeq = seisanshoCreateDto.getSeisanSeq();

		// 整合性チェック
		if (commonKaikeiService.isSeisanComplete(editForm.getSeisanCustomerId(), editForm.getSeisanAnkenId())
				|| commonKaikeiService.isSeisanComplete(editForm.getSeisanCustomerId(), seisanshoCreateDto.getPoolAnkenId())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 精算の編集の場合のみ
		if (seisanSeq != null) {
			// 精算記録に紐づく作成済みの入出金を削除する
			List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);

			// 精算記録に紐づく実費から精算情報を外す
			List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeisanSeq(seisanSeq);

			// 精算記録との紐づけデータカラムをNULLにする処理
			Consumer<TKaikeiKirokuEntity> setNullForSeisanSeq = e -> e.setSeisanSeq(null);
			Consumer<TKaikeiKirokuEntity> setNullForSeisanDate = e -> e.setSeisanDate(null);

			List<TKaikeiKirokuEntity> updateKaikeiKirokuEntities = tKaikeiKirokuEntities.stream().peek(setNullForSeisanSeq.andThen(setNullForSeisanDate)).collect(Collectors.toList());

			// 削除と更新
			tNyushukkinYoteiDao.delete(tNyushukkinYoteiEntities);
			tKaikeiKirokuDao.update(updateKaikeiKirokuEntities);

			// 精算記録の更新
			this.updatePoolSeisanEntity(seisanSeq, seisanTotal);
		}

		if (seisanSeq == null) {
			// 精算記録の発行
			seisanSeq = insertPoolSeisanEntity(editForm.getSeisanCustomerId(), editForm.getSeisanAnkenId(), seisanTotal);
		}

		// 該当の精算記録に日付を登録
		this.updatePoolSeisanKaikeiKiroku(seisanSeq, kaikeiSeqList);

		// 精算記録に対すプールデータの作成
		this.insertPoolSeisanKaikeiKiroku(seisanSeq, editForm.getSeisanCustomerId(), editForm.getSeisanAnkenId(), seisanshoCreateDto.getPoolAnkenId(), seisanTotal);
	}

	/**
	 * プールによる精算記録データを作成する
	 * 
	 * @param customerId
	 * @param ankenId
	 * @param poolAmount
	 * @return
	 * @throws AppException
	 */
	private Long insertPoolSeisanEntity(Long customerId, Long ankenId, BigDecimal poolAmount) throws AppException {
		TSeisanKirokuEntity tSeisanKirokuEntity = new TSeisanKirokuEntity();

		// 現在の枝番の最大値を取得
		Long currentMaxBranchNumber = tSeisanshoDao.getMaxBranchNo(customerId);

		tSeisanKirokuEntity.setSeisanId(currentMaxBranchNumber + 1);
		tSeisanKirokuEntity.setSeisanStatus(SeisanStatus.SEISAN.getCd());
		tSeisanKirokuEntity.setCustomerId(customerId);
		tSeisanKirokuEntity.setAnkenId(ankenId);
		tSeisanKirokuEntity.setSeisanKubun(SeisanKirokuKubun.SEISAN.getCd());
		tSeisanKirokuEntity.setSeikyuType(SeikyuType.IKKATSU.getCd());
		tSeisanKirokuEntity.setSeisanGaku(poolAmount);
		tSeisanKirokuEntity.setPoolFlg(SystemFlg.FLG_ON.getCd());

		try {
			// 精算記録の登録処理
			tSeisanKirokuDao.insert(tSeisanKirokuEntity);

			return tSeisanKirokuEntity.getSeisanSeq();
		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * 精算記録をプール精算へ更新する
	 * 
	 * @param seisanSeq
	 * @param poolAmount
	 */
	private void updatePoolSeisanEntity(Long seisanSeq, BigDecimal poolAmount) throws AppException {

		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		if (tSeisanKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		tSeisanKirokuEntity.setSeisanStatus(SeisanStatus.SEISAN.getCd());
		tSeisanKirokuEntity.setSeisanKubun(SeisanKirokuKubun.SEISAN.getCd());
		tSeisanKirokuEntity.setSeikyuType(SeikyuType.IKKATSU.getCd());
		tSeisanKirokuEntity.setMonthShiharaiGaku(null);
		tSeisanKirokuEntity.setMonthShiharaiDate(null);
		tSeisanKirokuEntity.setShiharaiStartDt(null);
		tSeisanKirokuEntity.setShiharaiDt(null);
		tSeisanKirokuEntity.setHasu(null);
		tSeisanKirokuEntity.setSeikyuShiharaishaType(null);
		tSeisanKirokuEntity.setSeikyuCustomerId(null);
		tSeisanKirokuEntity.setSeikyuKanyoshaSeq(null);
		tSeisanKirokuEntity.setSeikyuKozaSeq(null);
		tSeisanKirokuEntity.setShiharaiLimitDt(null);
		tSeisanKirokuEntity.setHenkinKozaSeq(null);
		tSeisanKirokuEntity.setHenkinSakiType(null);
		tSeisanKirokuEntity.setHenkinCustomerId(null);
		tSeisanKirokuEntity.setHenkinKanyoshaSeq(null);
		tSeisanKirokuEntity.setSeisanGaku(poolAmount);
		tSeisanKirokuEntity.setTekiyo(null);
		tSeisanKirokuEntity.setPoolFlg(SystemFlg.FLG_ON.getCd());
		tSeisanKirokuEntity.setLastEditAt(LocalDateTime.now());
		tSeisanKirokuEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());

		try {
			// 精算記録の更新
			tSeisanKirokuDao.update(tSeisanKirokuEntity);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * プール精算の精算情報を会計記録に保存する
	 * 
	 * @param seisanSeq
	 * @param kaikeiKirokuSeqList
	 * @throws AppException
	 */
	private void updatePoolSeisanKaikeiKiroku(Long seisanSeq, List<Long> kaikeiKirokuSeqList) throws AppException {

		if (CollectionUtils.isEmpty(kaikeiKirokuSeqList)) {
			// プールする会計情報がない場合は想定外
			throw new RuntimeException("引数に指定した 「kaikeiKirokuSeqList」 が空は想定外です");
		}

		// 会計データの取得
		List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeqList(kaikeiKirokuSeqList);

		// 精算SEQの有効チェック
		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		if (tSeisanKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// データと取得件数の整合性チェック
		if (CollectionUtils.isEmpty(tKaikeiKirokuEntities) || !Objects.equals(tKaikeiKirokuEntities.size(), kaikeiKirokuSeqList.size())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		Predicate<TKaikeiKirokuEntity> seisanDateRegistedCheck = e -> e.getSeisanDate() != null;
		Predicate<TKaikeiKirokuEntity> seisanSeqRegistedCheck = e -> e.getSeisanSeq() != null;
		if (tKaikeiKirokuEntities.stream().anyMatch(seisanDateRegistedCheck.or(seisanSeqRegistedCheck))) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 精算情報を会計情報に設定
		LocalDate today = LocalDate.now();
		Consumer<TKaikeiKirokuEntity> setSeisanDateAction = e -> e.setSeisanDate(today);
		Consumer<TKaikeiKirokuEntity> setSeisanSeqAction = e -> e.setSeisanSeq(seisanSeq);

		List<TKaikeiKirokuEntity> updateKaikeiKirokuEntities = tKaikeiKirokuEntities.stream().peek(setSeisanSeqAction.andThen(setSeisanDateAction)).collect(Collectors.toList());

		try {
			// 会計記録に精算情報を登録する
			tKaikeiKirokuDao.update(updateKaikeiKirokuEntities);

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}
	}

	/**
	 * プール精算の実績を登録する
	 * 
	 * @param seisanSeq 精算記録SEQ
	 * @param customerId 精算顧客ID
	 * @param ankenId 精算案件ID(プール元の案件ID)
	 * @param targetAnkenId プール先の案件ID
	 * @param poolAmount プールする金額
	 * @throws AppException
	 */
	private void insertPoolSeisanKaikeiKiroku(Long seisanSeq, Long customerId, Long ankenId, Long targetAnkenId, BigDecimal poolAmount) throws AppException {

		final String nyuykinTekiyoFormat = "案件ID：%s からの振替";
		final String shukkinTekiyoFormat = "案件ID：%s への振替";

		// 現在日付の取得
		LocalDate today = LocalDate.now();

		// 入金項目情報を取得
		MNyushukkinKomokuEntity nyukinKomokuEntity = mNyushukkinDao.selectById(Long.valueOf(NyushukkinKomokuType.NYUKIN_POOL.getCd()));
		MNyushukkinKomokuEntity shukkinKomokuEntity = mNyushukkinDao.selectById(Long.valueOf(NyushukkinKomokuType.SHUKKIN_POOL.getCd()));

		// 出金データの作成(プール元)
		TKaikeiKirokuEntity poolSourceData = new TKaikeiKirokuEntity();
		poolSourceData.setCustomerId(customerId);
		poolSourceData.setAnkenId(ankenId);
		poolSourceData.setHasseiDate(today);
		poolSourceData.setNyushukkinKomokuId(shukkinKomokuEntity.getNyushukkinKomokuId());
		poolSourceData.setTaxFlg(shukkinKomokuEntity.getTaxFlg());
		poolSourceData.setNyushukkinType(NyushukkinType.SHUKKIN.getCd());
		poolSourceData.setShukkinGaku(poolAmount);
		poolSourceData.setTekiyo(String.format(shukkinTekiyoFormat, AnkenId.of(targetAnkenId)));
		poolSourceData.setSeisanDate(today);
		poolSourceData.setSeisanSeq(seisanSeq);
		poolSourceData.setPoolType(PoolType.POOL_MOTO.getCd());
		poolSourceData.setPoolMotoAnkenId(ankenId);
		poolSourceData.setPoolSakiAnkenId(targetAnkenId);
		poolSourceData.setUncollectableFlg(SystemFlg.FLG_OFF.getCd());// プールよる出金は回収不能フラグOFF

		// 入金データの作成(プール先)
		TKaikeiKirokuEntity poolTargetData = new TKaikeiKirokuEntity();
		poolTargetData.setCustomerId(customerId);
		poolTargetData.setAnkenId(targetAnkenId);
		poolTargetData.setHasseiDate(today);
		poolTargetData.setNyushukkinKomokuId(nyukinKomokuEntity.getNyushukkinKomokuId());
		poolTargetData.setTaxFlg(nyukinKomokuEntity.getTaxFlg());
		poolTargetData.setNyushukkinType(NyushukkinType.NYUKIN.getCd());
		poolTargetData.setNyukinGaku(poolAmount);
		poolTargetData.setTekiyo(String.format(nyuykinTekiyoFormat, AnkenId.of(ankenId)));
		poolTargetData.setPoolType(PoolType.POOL_SAKI.getCd());
		poolTargetData.setPoolMotoAnkenId(ankenId);
		poolTargetData.setPoolSakiAnkenId(targetAnkenId);
		poolTargetData.setUncollectableFlg(SystemFlg.FLG_OFF.getCd());// プールよる入金は回収不能フラグOFF

		try {
			// 登録処理
			tKaikeiKirokuDao.insert(Arrays.asList(poolSourceData, poolTargetData));

		} catch (OptimisticLockingFailureException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

	}

	/**
	 * 精算記録の更新処理
	 *
	 * @param viewForm
	 * @param editForm
	 * @throws AppException
	 */
	public void updateSeisanKiroku(SeisanshoViewForm viewForm, SeisanshoEditForm editForm) throws AppException {

		// 精算記録情報の更新
		this.updateSeisanKirokuData(viewForm, editForm);

		// 会計記録情報の更新
		this.updateKaikeikiroku(editForm.getSeisanshoCreateDto().getSeisanSeq(), editForm.getKaikeiSeqList(), editForm.isSeisanZero());

		// 入出金予定情報の削除・登録
		this.deleteInsertNyushukkinYotei(viewForm, editForm);

	}

	/**
	 * 精算記録を削除
	 *
	 * @param seisanSeq 精算SEQ
	 * @throws AppException
	 */

	public void deleteSeisanKiroku(Long seisanSeq) throws AppException {

		// 削除（更新）対象の精算記録情報を取得
		TSeisanKirokuEntity deleteSeisansho = tSeisanshoDao.selectBySeisanSeq(seisanSeq);

		// 排他エラーチェックをします。
		if (deleteSeisansho == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);

		}

		Long customerId = deleteSeisansho.getCustomerId();
		Long ankenId = deleteSeisansho.getAnkenId();

		// 排他チェック（削除ボタンの表示条件と同じ判定）
		boolean isAbledEditSeisansho = this.isAbledEditSeisansho(deleteSeisansho);
		boolean isSeisanComplete = commonKaikeiService.isSeisanComplete(customerId, ankenId);
		if (!isAbledEditSeisansho || isSeisanComplete) {
			// 編集可能ではない、または、完了状態の場合 -> 削除不可
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除（更新）対象の会計記録情報を取得
		List<TKaikeiKirokuEntity> kaikeikirokuList = tSeisanshoDao.selectKaikeiKirokuByIds(customerId, seisanSeq);

		// 更新用のentityを生成
		List<TKaikeiKirokuEntity> updateKaikeikirokuList = new ArrayList<TKaikeiKirokuEntity>();
		List<TKaikeiKirokuEntity> deleteKaikeikirokuList = new ArrayList<TKaikeiKirokuEntity>();
		for (TKaikeiKirokuEntity tKaikeiKirokuEntity : kaikeikirokuList) {
			if (ankenId.equals(tKaikeiKirokuEntity.getPoolMotoAnkenId())) {
				deleteKaikeikirokuList.add(tKaikeiKirokuEntity);
			} else {
				if (GensenChoshu.DO.getCd().equals(tKaikeiKirokuEntity.getGensenchoshuFlg())) {
					tKaikeiKirokuEntity.setGensenchoshuGaku(commonKaikeiService.calcGensenChoshu(tKaikeiKirokuEntity.getShukkinGaku()));
				}
				tKaikeiKirokuEntity.setSeisanSeq(null);
				tKaikeiKirokuEntity.setSeisanDate(null);
				updateKaikeikirokuList.add(tKaikeiKirokuEntity);
			}
		}

		// 削除（更新）対象の入出金予定情報を取得
		List<TNyushukkinYoteiEntity> deleteNyushukkinYoteiList = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);

		try {
			// 会計記録情報の更新処理を行います。
			for (TKaikeiKirokuEntity entity : updateKaikeikirokuList) {
				tKaikeiKirokuDao.update(entity);
			}
			// 会計記録情報の削除処理を行います。
			for (TKaikeiKirokuEntity entity : deleteKaikeikirokuList) {
				tKaikeiKirokuDao.delete(entity);
			}
			// 入出金予定情報の削除処理を行います。
			for (TNyushukkinYoteiEntity entity : deleteNyushukkinYoteiList) {
				tNyushukkinYoteiDao.delete(entity);
			}
			// 更新パターンを論理削除に設定します。
			// (利用停止 = 論理削除)
			deleteSeisansho.setFlgDelete();
			tSeisanshoDao.update(deleteSeisansho);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, ex);

		}

	}

	/**
	 * 精算記録情報を、画面表示用に加工します。
	 *
	 * @param seisanSeq 精算SEQ
	 * @return 会計記録のSEQのList
	 */
	public List<Long> getKaikeikirokuByseisanSeq(Long seisanSeq) {

		List<KaikeiKirokuBean> kaikeiKirokuBeanList = new ArrayList<KaikeiKirokuBean>();
		List<Long> kaikeiSeq = new ArrayList<Long>();
		// ---------------------------------------------
		// 入出金予定情報
		// ---------------------------------------------
		List<Long> yoteiSeqList = new ArrayList<Long>();
		List<TNyushukkinYoteiEntity> yoteiEntityList = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);
		yoteiEntityList.forEach(entity -> {
			yoteiSeqList.add(entity.getNyushukkinYoteiSeq());
		});

		kaikeiKirokuBeanList = tSeisanshoDao.selectKaikeiKirokuBeanBySeisanSeq(seisanSeq);
		kaikeiKirokuBeanList.forEach(list -> {
			if (!yoteiSeqList.contains(list.getNyushukkinYoteiSeq())) {
				kaikeiSeq.add(list.getKaikeiKirokuSeq());
			}
		});

		return kaikeiSeq;
	}

	/**
	 * 会計記録から合計金額を計算
	 *
	 * @param kaikeiKirokuSeqList
	 * @return
	 * @throws AppException
	 */
	public SeisanshoViewForm recalculation(List<Long> kaikeiKirokuSeqList) throws AppException {

		SeisanshoViewForm viewForm = new SeisanshoViewForm();

		if (CollectionUtils.isEmpty(kaikeiKirokuSeqList)) {
			// 選択されている報酬項目・入出金項目がない場合は計算しない
			return viewForm;
		}

		// 会計記録情報を取得
		List<KaikeiKirokuBean> kaikeiKirokuBeanList = tSeisanshoDao.selectByKaikeiKirokuSeq(kaikeiKirokuSeqList);
		List<SeisanshoDto> seisanshoDtoList = this.kaikeiKirokuBean2Dto(kaikeiKirokuBeanList);

		// 画面表示用の合計金額を計算・設定します。
		this.calcTotalForKaikeiKiroku(viewForm, seisanshoDtoList);

		return viewForm;
	}

	/**
	 * 精算記録情報を、画面表示用に加工します。
	 *
	 * @param seisanSeq 精算SEQ
	 * @return 会計記録のSEQのList
	 */
	public SeisanshoEditForm getSeisansho(Long seisanSeq) {

		SeisanshoEditForm editForm = new SeisanshoEditForm();
		SeisanshoCreateDto createDto = new SeisanshoCreateDto();
		TSeisanKirokuEntity entity = tSeisanshoDao.selectBySeisanSeq(seisanSeq);

		editForm.setSeisanAnkenId(entity.getAnkenId());
		editForm.setSeisanCustomerId(entity.getCustomerId());
		createDto.setSeisanSeq(seisanSeq);
		createDto.setSeisanStatus(entity.getSeisanStatus());
		createDto.setSeisanKubun(entity.getSeisanKubun());
		createDto.setHasu(entity.getHasu());
		if (entity.getMonthShiharaiGaku() != null) {
			createDto.setMonthlyPay(entity.getMonthShiharaiGaku().toString());
		} else {
			createDto.setMonthlyPay("");
		}
		createDto.setSeikyuType(entity.getSeikyuType());
		createDto.setSeikyuKozaSeq(entity.getSeikyuKozaSeq());
		createDto.setHenkinKozaSeq(entity.getHenkinKozaSeq());

		// 一括請求の場合
		if (SeikyuType.IKKATSU.equalsByCode(entity.getSeikyuType())) {
			if (entity.getShiharaiDt() != null) {
				String paymentYoteiDate = DateUtils.parseToString(entity.getShiharaiDt(), DateUtils.DATE_FORMAT_SLASH_DELIMITED);
				createDto.setPaymentYoteiDate(paymentYoteiDate);
			}
		}

		// 分割請求の場合
		if (SeikyuType.BUNKATSU.equalsByCode(entity.getSeikyuType())) {
			int startYear = entity.getShiharaiStartDt().getYear();
			int startMonth = entity.getShiharaiStartDt().getMonthValue();
			createDto.setShiharaiStartYear(String.valueOf(startYear));
			createDto.setShiharaiStartMonth(String.valueOf(startMonth));
			if (END_OF_MONTH.equals(entity.getMonthShiharaiDate())) {
				// 月末
				createDto.setLastDay(SeisanShiharaiMonthDay.LASTDAY.getCd());
			} else {
				// 日付指定
				createDto.setLastDay(SeisanShiharaiMonthDay.DESIGNATEDDAY.getCd());
				createDto.setPaymentDate(entity.getMonthShiharaiDate());
			}
		}

		// 支払者
		String shiharaishaType = entity.getSeikyuShiharaishaType();
		createDto.setShiharaishaType(shiharaishaType);

		if (TargetType.CUSTOMER.equalsByCode(shiharaishaType)) {
			// 支払者種別が顧客の場合DBの請求支払者を顧客IDにセット
			createDto.setShiharaiCustomerId(entity.getSeikyuCustomerId());

		} else if (TargetType.KANYOSHA.equalsByCode(shiharaishaType)) {
			// 支払者種別が関与者の場合DBの請求支払者を関与者SEQにセット
			createDto.setShiharaiKanyoshaSeq(entity.getSeikyuKanyoshaSeq());

		}

		// 返金先
		String henkinSakiType = entity.getHenkinSakiType();
		createDto.setHenkinSakiType(henkinSakiType);

		if (TargetType.CUSTOMER.equalsByCode(henkinSakiType)) {
			// 返金先種別が顧客の場合は、返金先に顧客IDを設定する。
			createDto.setHenkinCustomerId(entity.getHenkinCustomerId());

		} else if (TargetType.KANYOSHA.equalsByCode(henkinSakiType)) {
			// 返金先種別が関与者の場合は、返金先に関与者SEQを設定する。
			createDto.setHenkinKanyoshaSeq(entity.getHenkinKanyoshaSeq());

		}

		editForm.setSeisanshoCreateDto(createDto);

		return editForm;
	}

	/**
	 * 編集用
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	private SeisanshoViewForm editViewForm(SeisanshoViewForm viewForm) throws AppException {

		// 精算記録取得
		Long seisanSeq = viewForm.getSeisanSeq();

		TSeisanKirokuEntity seisanKirokuEntity = tSeisanshoDao.selectBySeisanSeq(seisanSeq);
		// 排他エラーチェック
		if (seisanKirokuEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 精算書データの案件IDを設定
		Long seisanAnkenId = seisanKirokuEntity.getAnkenId();
		viewForm.setSeisanAnkenId(AnkenId.of(seisanAnkenId));

		// 精算書データの顧客IDを設定
		Long seisanCustomerId = seisanKirokuEntity.getCustomerId();
		viewForm.setSeisanCustomerId(CustomerId.of(seisanCustomerId));

		// Dtoに変換
		SeisanshoCreateDto createDto = this.tSeisanKirokuEntity2Dto(seisanKirokuEntity);

		// 事務所・売上計上先弁護士の口座情報
		List<GinkoKozaDto> ginkoKozaListForTenantAndAccount = commonKaikeiService.getTenantAccountGinkoKozaList(seisanAnkenId);
		viewForm.setTenantAccountGinkoKozaList(ginkoKozaListForTenantAndAccount);

		// 削除したデータの対策
		if (SeisanKirokuKubun.SEIKYU.equalsByCode(seisanKirokuEntity.getSeisanKubun())) {
			// 請求口座が登録されていないときは行わない(精算額が0円の時、もしくはプール時)
			if (createDto.getSeikyuKozaSeq() != null) {
				commonKaikeiService.margeRelateKozaDto(ginkoKozaListForTenantAndAccount, createDto.getSeikyuKozaSeq());
			}
		} else if (SeisanKirokuKubun.SEISAN.equalsByCode(seisanKirokuEntity.getSeisanKubun())) {
			// 返金口座が登録されていないときは行わない(精算額が0円の時、もしくはプール時)
			if (createDto.getHenkinKozaSeq() != null) {
				commonKaikeiService.margeRelateKozaDto(ginkoKozaListForTenantAndAccount, createDto.getHenkinKozaSeq());
			}
		} else {
			// なにもしない
		}

		// 編集可能かどうか判定
		viewForm.setAbledEdit(this.isAbledEditSeisansho(seisanKirokuEntity));
		// 精算完了（精算完了日か、完了チェックのどちらかが設定されている）か
		viewForm.setSeisanComplete(commonKaikeiService.isSeisanComplete(seisanCustomerId, seisanAnkenId));

		TAnkenCustomerEntity tAnkenCustomerEntity = tAnkenCustomerDao.selectByAnkenIdAndCustomerId(seisanAnkenId, seisanCustomerId);
		String ankenStatus = tAnkenCustomerEntity.getAnkenStatus();
		// ステータス（精算完了日、完了チェック）による編集不可の説明メッセージの表示を判定する
		if (viewForm.isSeisanComplete()) {
			// 精算完了状態（精算完了日か完了チェックに入力がある場合の状態）の場合のみメッセージを表示する

			// 編集不可メッセージ表示フラグの設定
			Consumer<Boolean> setShowAnkenCompletedMsg = bool -> viewForm.setShowAnkenCompletedMsg(bool);
			Consumer<Boolean> setShowSeisanCompletedMsg = bool -> viewForm.setShowSeisanCompletedMsg(bool);
			commonKaikeiService.setShowCantEditMsgFlg(tAnkenCustomerEntity.getKanryoDate(), ankenStatus, setShowAnkenCompletedMsg, setShowSeisanCompletedMsg);
	}
		
		// Dtoに設定します。
		viewForm.setSeisanshoCreateDto(createDto);

		// ---------------------------------------------
		// 入出金予定情報
		// ---------------------------------------------
		List<Long> yoteiSeqList = new ArrayList<Long>();
		List<TNyushukkinYoteiEntity> yoteiEntityList = tNyushukkinYoteiDao.selectBySeisanSeq(seisanKirokuEntity.getSeisanSeq());

		for (TNyushukkinYoteiEntity yoteiEntity : yoteiEntityList) {
			yoteiSeqList.add(yoteiEntity.getNyushukkinYoteiSeq());
		}

		// ---------------------------------------------
		// 会計記録情報
		// ---------------------------------------------
		List<KaikeiKirokuBean> newKaikeiKirokuBeanList = new ArrayList<KaikeiKirokuBean>();
		List<KaikeiKirokuBean> kaikeiKirokuBeanListForCalc = new ArrayList<KaikeiKirokuBean>();

		List<KaikeiKirokuBean> kaikeiKirokuBeanList = tSeisanshoDao.selectEditBySearchCondition(seisanAnkenId, seisanCustomerId);
		for (KaikeiKirokuBean bean : kaikeiKirokuBeanList) {
			if (!yoteiSeqList.contains(bean.getNyushukkinYoteiSeq())) {
				// 精算SEQに紐づく入出金予定SEQに、会計記録情報が紐づいている場合
				// ＝ 実績なので省きます。
				newKaikeiKirokuBeanList.add(bean);

				if (seisanSeq.equals(bean.getSeisanSeq())) {
					// 精算SEQに紐づいている情報のみ、精算合計を計算します。
					kaikeiKirokuBeanListForCalc.add(bean);
				}
			}
		}

		// 精算書の状態を取得
		boolean isAbledEdit = viewForm.isAbledEdit();
		boolean isSeisanComplete = viewForm.isSeisanComplete();

		// フォームに設定します。
		List<SeisanshoDto> seisanshoDtoList = this.kaikeiKirokuBean2Dto(newKaikeiKirokuBeanList, isAbledEdit, isSeisanComplete, seisanSeq);
		List<SeisanshoDto> calcSeisanshoDtoList = new ArrayList<SeisanshoDto>();
		// １.報酬項目の選択を設定
		viewForm.setSeisanHoshuList(this.dispTargetKaikeiKirokuList(seisanshoDtoList, true, seisanSeq));
		// ２.入出金項目の選択を設定
		viewForm.setSeisanNyushukkinList(this.dispTargetKaikeiKirokuList(seisanshoDtoList, false, seisanSeq));

		for (SeisanshoDto seisansho : seisanshoDtoList) {
			if (seisanSeq.equals(seisansho.getSeisanSeq())) {
				// 精算SEQに紐づいている情報のみ、精算合計を計算します。
				calcSeisanshoDtoList.add(seisansho);
			}
		}

		// 画面表示用の合計金額を計算・設定します。
		this.calcTotalForKaikeiKiroku(viewForm, calcSeisanshoDtoList);

		return viewForm;
	}

	/**
	 * TSeisanKirokuEntity -> SeisanshoCreateDto の設定処理
	 *
	 * @param seisanKirokuEntity 精算記録情報Entity
	 * @return 精算書作成Dto
	 */
	private SeisanshoCreateDto tSeisanKirokuEntity2Dto(TSeisanKirokuEntity seisanKirokuEntity) {

		SeisanshoCreateDto createDto = new SeisanshoCreateDto();

		// Entityの値をDtoに設定します。
		createDto.setSeikyuType(seisanKirokuEntity.getSeikyuType());
		createDto.setPaymentFirstDay(DateUtils.parseToString(seisanKirokuEntity.getShiharaiStartDt(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		createDto.setSeisanStatus(seisanKirokuEntity.getSeisanStatus());
		createDto.setHasu(seisanKirokuEntity.getHasu());
		createDto.setPaymentLimitDt(DateUtils.parseToString(seisanKirokuEntity.getShiharaiLimitDt(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		createDto.setPaymentYoteiDate(DateUtils.parseToString(seisanKirokuEntity.getShiharaiDt(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		if (END_OF_MONTH.equals(seisanKirokuEntity.getMonthShiharaiDate())) {
			// 31日を指定した場合は月末とみなす
			createDto.setLastDay(SeisanShiharaiMonthDay.LASTDAY.getCd());
		} else if (seisanKirokuEntity.getMonthShiharaiDate() != null) {
			// 日付指定
			createDto.setLastDay(SeisanShiharaiMonthDay.DESIGNATEDDAY.getCd());
			createDto.setPaymentDate(seisanKirokuEntity.getMonthShiharaiDate());
		}

		// 別案件への振替
		if (SystemFlg.codeToBoolean(seisanKirokuEntity.getPoolFlg())) {
			// プールフラグON
			createDto.setPoolFlg(true);
			SeisanshoPoolAnkenBean bean = tSeisanshoDao.selectSeisanshoPoolAnkenDetail(seisanKirokuEntity.getSeisanSeq());
			createDto.setDisplayPoolAnkenId(AnkenId.of(bean.getAnkenId()));
			createDto.setPoolAnkenInfo(commonBunyaService.bunyaAndAnkenName(bean.getAnkenName(), bean.getBunyaId()));
		}

		if (SeisanKirokuKubun.SEIKYU.equalsByCode(seisanKirokuEntity.getSeisanKubun())) {
			// 請求の場合
			String seikyuShiharaishaType = seisanKirokuEntity.getSeikyuShiharaishaType();

			if (TargetType.CUSTOMER.equalsByCode(seikyuShiharaishaType)) {
				// 支払者種別が顧客の場合DBの請求支払者を顧客IDにセット
				createDto.setShiharaiCustomerId(seisanKirokuEntity.getSeikyuCustomerId());

			} else if (TargetType.KANYOSHA.equalsByCode(seikyuShiharaishaType)) {
				// 支払者種別が関与者の場合DBの請求支払者を関与者SEQにセット
				createDto.setShiharaiKanyoshaSeq(seisanKirokuEntity.getSeikyuKanyoshaSeq());

			}
			createDto.setShiharaishaType(seisanKirokuEntity.getSeikyuShiharaishaType());
			createDto.setSeikyuKozaSeq(seisanKirokuEntity.getSeikyuKozaSeq());

			if (SeikyuType.BUNKATSU.equalsByCode(createDto.getSeikyuType())) {
				// 分割の場合

				// 支払開始年を取得します。
				int startYear = seisanKirokuEntity.getShiharaiStartDt().getYear();
				String shiharaiStartYearStr = String.valueOf(startYear);
				createDto.setShiharaiStartYear(shiharaiStartYearStr);

				// 支払開始月を取得します。
				int startMonth = seisanKirokuEntity.getShiharaiStartDt().getMonthValue();
				String shiharaiStartMonthStr = String.valueOf(startMonth);
				createDto.setShiharaiStartMonth(shiharaiStartMonthStr);

				// 月々の支払額
				// 金額を BigDecimal -> String に変換します。
				createDto.setMonthlyPay(commonKaikeiService.toDispAmountLabel(seisanKirokuEntity.getMonthShiharaiGaku()));
			}

		} else if (SeisanKirokuKubun.SEISAN.equalsByCode(seisanKirokuEntity.getSeisanKubun())) {
			// 精算（返金）の場合

			// 精算先 口座
			createDto.setHenkinKozaSeq(seisanKirokuEntity.getHenkinKozaSeq());

			// 精算先タイプ
			String henkinSakiType = seisanKirokuEntity.getHenkinSakiType();
			createDto.setHenkinSakiType(henkinSakiType);

			if (TargetType.CUSTOMER.equalsByCode(henkinSakiType)) {
				// 顧客の場合

				// 返金先種別が顧客の場合は、返金先に顧客IDを設定する。
				createDto.setHenkinCustomerId(seisanKirokuEntity.getHenkinCustomerId());

				// 顧客の口座詳細情報取得
				GinkoKozaDto ginkoKozaDto = commonKaikeiService.getCustomerKozaDetail(seisanKirokuEntity.getHenkinCustomerId());
				if (ginkoKozaDto != null) {
					// 口座情報１
					createDto.setShiharaiKozaInfo1(ginkoKozaDto.getDispGinkoKozaInfo1());
					// 口座情報２
					createDto.setShiharaiKozaInfo2(ginkoKozaDto.getDispGinkoKozaInfo2());
				}

			} else if (TargetType.KANYOSHA.equalsByCode(henkinSakiType)) {
				// 関与者の場合

				// 返金先種別が関与者の場合は、返金先に関与者SEQを設定する。
				createDto.setHenkinKanyoshaSeq(seisanKirokuEntity.getHenkinKanyoshaSeq());

				// 関与者の口座詳細情報取得
				GinkoKozaDto ginkoKozaDto = commonKaikeiService.getKanyoshaKozaDetail(seisanKirokuEntity.getHenkinKanyoshaSeq());
				if (ginkoKozaDto != null) {
					// 口座情報１
					createDto.setShiharaiKozaInfo1(ginkoKozaDto.getDispGinkoKozaInfo1());
					// 口座情報２
					createDto.setShiharaiKozaInfo2(ginkoKozaDto.getDispGinkoKozaInfo2());
				}

			}
		}

		return createDto;
	}

	/**
	 * 新規登録時<br>
	 * 会計記録リスト取得
	 *
	 * @param kaikeiKirokuBeanList
	 * @return
	 */
	private List<SeisanshoDto> kaikeiKirokuBean2Dto(List<KaikeiKirokuBean> kaikeiKirokuBeanList) {
		return kaikeiKirokuBean2Dto(kaikeiKirokuBeanList, true, false, null);
	}

	/**
	 * 編集時<br>
	 * 会計記録リスト取得<br>
	 * List<KaikeiKirokuBean> -> List<KaikeiKirokuDto>
	 *
	 * @param kaikeiKirokuBeanList
	 * @param isAbledEdit
	 * @param isSeisanComplete
	 * @param seisanSeq
	 * @return
	 */
	private List<SeisanshoDto> kaikeiKirokuBean2Dto(List<KaikeiKirokuBean> kaikeiKirokuBeanList, boolean isAbledEdit, boolean isSeisanComplete,
			Long seisanSeq) {

		List<SeisanshoDto> seisanshoDtoList = new ArrayList<SeisanshoDto>();

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		kaikeiKirokuBeanList.forEach(kaikeiKirokuBean -> {
			SeisanshoDto seisanshoDto = new SeisanshoDto();

			// Beanの値をDtoにコピーします。
			BeanUtils.copyProperties(kaikeiKirokuBean, seisanshoDto);

			// 編集か判定
			if (seisanSeq != null) {
				// 編集の場合

				// 各項目の精算SEQと詳細の精算SEQを比較
				if (seisanSeq.equals(seisanshoDto.getSeisanSeq())) {
					// 対象の精算と一致した場合

					// チェック：あり
					seisanshoDto.setDefaultSeisanCheck(true);
					// チェック切替：できる
					seisanshoDto.setDisabledSeisanCheck(false);

				} else {
					// 対象の精算と一致しない場合

					// チェック：なし
					seisanshoDto.setDefaultSeisanCheck(false);

					// 未精算のものを判定
					if (seisanshoDto.getSeisanSeq() == null) {
						// 未精算の場合

						// チェック切替：できる
						seisanshoDto.setDisabledSeisanCheck(false);
					} else {
						// 精算済の場合

						// チェック切替：できない
						seisanshoDto.setDisabledSeisanCheck(true);
					}
				}

				// 精算完了もしくは案件完了状態の場合はすべてチェック不可
				if (isSeisanComplete || !isAbledEdit) {
					seisanshoDto.setDisabledSeisanCheck(true);
				}

			} else {
				// 新規登録の場合

				// チェック：あり
				seisanshoDto.setDefaultSeisanCheck(true);
				// チェック切替：できる
				seisanshoDto.setDisabledSeisanCheck(false);
			}

			// 入出金タイプの設定
			seisanshoDto.setDisplaNyushukkinType(NyushukkinType.of(kaikeiKirokuBean.getNyushukkinType()).getVal());

			// 発生日
			if (LawyerHoshu.TIME_CHARGE.equalsByCode(kaikeiKirokuBean.getHoshuKomokuId()) && TimeChargeTimeShitei.START_END_TIME.equalsByCode(kaikeiKirokuBean.getTimeChargeTimeShitei())) {
				// タイムチャージで、開始・終了日時を指定している場合
				// 発生日には開始日を設定する
				LocalDate timeChargeStartDate = DateUtils.convertLocalDate(kaikeiKirokuBean.getTimeChargeStartTime());
				seisanshoDto.setTimeChargeStartTime(kaikeiKirokuBean.getTimeChargeStartTime());
				seisanshoDto.setHasseiDateDate(timeChargeStartDate);
				seisanshoDto.setHasseiDate(DateUtils.parseToString(timeChargeStartDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			} else {
				// それ以外の場合
				seisanshoDto.setHasseiDateDate(kaikeiKirokuBean.getHasseiDate());
				seisanshoDto.setHasseiDate(DateUtils.parseToString(kaikeiKirokuBean.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			}

			// 案件IDの設定
			seisanshoDto.setDisplayAnkenId(AnkenId.of(kaikeiKirokuBean.getAnkenId()));

			// 顧客IDの設定
			seisanshoDto.setDisplayCustomerId(CustomerId.of(kaikeiKirokuBean.getCustomerId()));

			if (kaikeiKirokuBean.getSeisanId() != null) {
				// 精算ID
				seisanshoDto.setDisplaySeisanId(new SeisanId(kaikeiKirokuBean.getCustomerId(), kaikeiKirokuBean.getSeisanId()));
			}
			// 分野の変換
			seisanshoDto.setBunya(bunyaMap.get(kaikeiKirokuBean.getBunyaId()).getVal());
			// 表示用金額への変換（カンマあり）
			seisanshoDto.setDisplayNyukinGaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuBean.getNyukinGaku()));

			// 項目名の設定
			if (kaikeiKirokuBean.getHoshuKomokuId() != null) {
				// 弁護士報酬項目の場合
				seisanshoDto.setItem(LawyerHoshu.of(kaikeiKirokuBean.getHoshuKomokuId()).getVal());
			}
			if (kaikeiKirokuBean.getNyushukkinKomokuId() != null) {
				// 入出金項目の場合
				seisanshoDto.setItem(kaikeiKirokuBean.getKomokuName());
				seisanshoDto.setTaxFlg(kaikeiKirokuBean.getNyushukkinType());
			}

			// 消費税の設定
			if (kaikeiKirokuBean.getHoshuKomokuId() != null) {
				// １．報酬項目 の場合
				BigDecimal tax = kaikeiKirokuBean.getTaxGaku() != null ? kaikeiKirokuBean.getTaxGaku() : new BigDecimal(0);
				BigDecimal hoshu = kaikeiKirokuBean.getShukkinGaku() != null ? kaikeiKirokuBean.getShukkinGaku() : new BigDecimal(0);

				if (TaxFlg.INTERNAL_TAX.equalsByCode(kaikeiKirokuBean.getTaxFlg())) {
					// 税込の場合、報酬から引く
					hoshu = hoshu.subtract(tax);
				}
				seisanshoDto.setTax(tax);
				seisanshoDto.setTaxIncludedShukkingaku(hoshu);
				seisanshoDto.setDisplayTax(commonKaikeiService.toDispAmountLabel(tax));
				seisanshoDto.setDisplayShukkinGaku(commonKaikeiService.toDispAmountLabel(hoshu));

			} else {
				// ２．入出金項目
				seisanshoDto.setDisplayTax("");
				seisanshoDto.setTaxIncludedShukkingaku(kaikeiKirokuBean.getShukkinGaku());
				seisanshoDto.setDisplayShukkinGaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuBean.getShukkinGaku()));

			}
			// Dtoに設定します。
			// 源泉徴収額
			BigDecimal gensenGaku = kaikeiKirokuBean.getGensenchoshuGaku() != null ? kaikeiKirokuBean.getGensenchoshuGaku() : new BigDecimal(0);
			seisanshoDto.setDispGensenchoshuGaku(commonKaikeiService.toDispAmountLabel(gensenGaku.negate()));// 源泉徴収はマイナスにする
			seisanshoDto.setDisplayNyukinGaku(commonKaikeiService.toDispAmountLabel(kaikeiKirokuBean.getNyukinGaku()));
			seisanshoDto.setTaxIncludedNyukingaku(kaikeiKirokuBean.getNyukinGaku());
			seisanshoDtoList.add(seisanshoDto);
		});

		return seisanshoDtoList;
	}

	/**
	 * SeisanshoCreateDto -> TSeisanKirokuEntity の設定処理<br>
	 *
	 * <pre>
	 * 請求時用です。
	 * </pre>
	 *
	 * @param editForm
	 * @param seisanKirokuEntity
	 */
	private void seisanshoDto2EntityForSeikyu(SeisanshoEditForm editForm, TSeisanKirokuEntity seisanKirokuEntity) {

		// 請求・精算記録の画面入力情報
		SeisanshoCreateDto createDto = editForm.getSeisanshoCreateDto();

		// 請求方法
		seisanKirokuEntity.setSeikyuType(createDto.getSeikyuType().toString());
		// 端数の指定
		seisanKirokuEntity.setHasu(createDto.getHasu());

		// 月々の支払金額
		if (SeikyuType.BUNKATSU.equalsByCode(createDto.getSeikyuType())) {
			// *****************************
			// 分割の場合
			// *****************************
			LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
			decimalFormat.setParseBigDecimal(true);
			BigDecimal monthlyPay = new BigDecimal(0);

			try {
				// 月額
				monthlyPay = (BigDecimal) decimalFormat.parse(createDto.getMonthlyPay().toString());
				seisanKirokuEntity.setMonthShiharaiGaku(monthlyPay);

			} catch (ParseException e) {
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.error(classAndMethodName + "：" + createDto.getMonthlyPay().toString() + "が正しく変換できませんでした。");
			}

			// 支払開始年月・毎月の支払日 ⇒ 支払開始年月日として設定

			// 支払い開始月
			String shiharaiStartMonth = createDto.getShiharaiStartMonth();
			// 支払い開始月の0埋め
			String shiharaiStartDtMonth = StringUtils.leftPad(shiharaiStartMonth, 2, CommonConstant.ZERO);

			// 支払い開始日
			String monthShiharaiDate = null;
			if (SeisanShiharaiMonthDay.DESIGNATEDDAY.equalsByCode(createDto.getLastDay())) {
				// 指定日付
				monthShiharaiDate = createDto.getPaymentDate();
			} else {
				// 月末
				monthShiharaiDate = END_OF_MONTH;
			}
			// 指定日付を設定
			seisanKirokuEntity.setMonthShiharaiDate(monthShiharaiDate);
			// 支払い開始日の0埋め
			String shiharaiStartDtDay = StringUtils.leftPad(monthShiharaiDate, 2, CommonConstant.ZERO);

			// 支払い開始年
			String shiharaiStartDtYear = createDto.getShiharaiStartYear();

			// 支払い開始：年月日
			String shiharaiDate = shiharaiStartDtYear + "/" + shiharaiStartDtMonth + "/" + shiharaiStartDtDay;
			seisanKirokuEntity.setShiharaiStartDt(DateUtils.parseToLocalDate(shiharaiDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			// 精算-入金予定日
			seisanKirokuEntity.setShiharaiDt(null);

		} else if (SeikyuType.IKKATSU.equalsByCode(createDto.getSeikyuType())) {
			// *****************************
			// 一括の場合
			// *****************************
			// 支払日
			seisanKirokuEntity.setShiharaiDt(
					DateUtils.parseToLocalDate(createDto.getPaymentYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			// 精算分割項目
			seisanKirokuEntity.setMonthShiharaiGaku(null);
			seisanKirokuEntity.setMonthShiharaiDate(null);
			seisanKirokuEntity.setShiharaiStartDt(null);
			seisanKirokuEntity.setHasu(null);

		}

		// 支払者
		String shiharaishaType = createDto.getShiharaishaType();
		seisanKirokuEntity.setSeikyuShiharaishaType(shiharaishaType);

		if (TargetType.CUSTOMER.equalsByCode(shiharaishaType)) {
			// 支払者種別が顧客の場合DBの請求支払者に顧客IDにセット
			seisanKirokuEntity.setSeikyuCustomerId(editForm.getSeisanCustomerId());
			seisanKirokuEntity.setSeikyuKanyoshaSeq(null);

		} else if (TargetType.KANYOSHA.equalsByCode(shiharaishaType)) {
			// 支払者種別が関与者の場合DBの請求支払者に関与者IDにセット
			seisanKirokuEntity.setSeikyuKanyoshaSeq(createDto.getShiharaiKanyoshaSeq());
			seisanKirokuEntity.setSeikyuCustomerId(null);

		}

		// 口座指定
		seisanKirokuEntity.setSeikyuKozaSeq(createDto.getSeikyuKozaSeq());

		// 返金時に設定される情報をリセットします。
		seisanKirokuEntity.setPoolFlg(null);
		// 支払い期日
		seisanKirokuEntity.setShiharaiLimitDt(null);
		// 請求-支払者種別
		seisanKirokuEntity.setHenkinSakiType(null);
		// 請求-返金口座SEQ
		seisanKirokuEntity.setHenkinKozaSeq(null);
		// 請求-支払者顧客ID
		seisanKirokuEntity.setHenkinCustomerId(null);
		// 請求-支払者関与者ID
		seisanKirokuEntity.setHenkinKanyoshaSeq(null);
	}

	/**
	 * SeisanshoCreateDto -> TSeisanKirokuEntity の設定処理<br>
	 *
	 * <pre>
	 * 精算（返金）用
	 * </pre>
	 *
	 * @param editForm
	 * @param seisanKirokuEntity
	 */
	private void seisanshoDto2EntityForSeisan(SeisanshoEditForm editForm, TSeisanKirokuEntity seisanKirokuEntity) {

		// 請求・精算記録の画面入力情報
		SeisanshoCreateDto createDto = editForm.getSeisanshoCreateDto();

		// 預り金プールフラグ
		if (createDto.isPoolFlg()) {
			seisanKirokuEntity.setPoolFlg(SystemFlg.FLG_ON.getCd());
		}

		// 精算-支払期日
		seisanKirokuEntity.setShiharaiLimitDt(
				DateUtils.parseToLocalDate(createDto.getPaymentLimitDt(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

		// 精算-返金先
		String henkinSakiType = createDto.getHenkinSakiType();
		seisanKirokuEntity.setHenkinSakiType(henkinSakiType);
		seisanKirokuEntity.setHenkinKozaSeq(createDto.getHenkinKozaSeq());

		if (TargetType.CUSTOMER.equalsByCode(henkinSakiType)) {
			// 顧客から選択する場合は、返金先に顧客IDを設定する。
			seisanKirokuEntity.setHenkinCustomerId(editForm.getSeisanCustomerId());
			seisanKirokuEntity.setHenkinKanyoshaSeq(null);

		} else if (TargetType.KANYOSHA.equalsByCode(henkinSakiType)) {
			// 関与者から選択する場合は、返金先に関与者IDを設定する。
			seisanKirokuEntity.setHenkinKanyoshaSeq(createDto.getHenkinKanyoshaSeq());
			seisanKirokuEntity.setHenkinCustomerId(null);

		}

		// 返金の場合は請求タイプを一括に設定する
		seisanKirokuEntity.setSeikyuType(SeikyuType.IKKATSU.getCd());

		// 精算項目を未設定

		// 請求-月々の支払い額
		seisanKirokuEntity.setMonthShiharaiGaku(null);
		// 請求-月々の支払日
		seisanKirokuEntity.setMonthShiharaiDate(null);
		// 請求-月々の支払開始日
		seisanKirokuEntity.setShiharaiStartDt(null);
		// 請求-入金予定日
		seisanKirokuEntity.setShiharaiDt(null);
		// 請求-端数
		seisanKirokuEntity.setHasu(null);
		// 請求-支払者種別
		seisanKirokuEntity.setSeikyuShiharaishaType(null);
		// 請求-支払者顧客ID
		seisanKirokuEntity.setSeikyuCustomerId(null);
		// 請求-支払者関与者ID
		seisanKirokuEntity.setSeikyuKanyoshaSeq(null);
		// 請求-口座SEQ
		seisanKirokuEntity.setSeikyuKozaSeq(null);

	}

	/**
	 * 画面に表示する会計記録情報を特定します。
	 *
	 * @param seisanshoDtoList 精算書作成画面に表示する会計記録情報のDtoリスト
	 * @param isHoshu true:報酬情報、false:入出金情報
	 * @param seisanSeq
	 * @return isHoshuがtrue:報酬情報のDtoリスト、isHoshuがfalse:入出金情報のDtoリスト
	 */
	private List<SeisanshoDto> dispTargetKaikeiKirokuList(List<SeisanshoDto> seisanshoDtoList, boolean isHoshu, Long seisanSeq) {

		List<SeisanshoDto> displayTargetList = this.dispTargetKaikeiKirokuList(seisanshoDtoList, isHoshu);

		List<SeisanshoDto> targetSeisanSeqList = null;
		List<SeisanshoDto> notTargetSeisanSeqList = null;

		if (isHoshu) {
			// 報酬データの場合

			targetSeisanSeqList = displayTargetList.stream()
					.filter(entity -> seisanSeq.equals(entity.getSeisanSeq()))
					.sorted(Comparator.comparing(SeisanshoDto::getHasseiDateDate, Comparator.nullsFirst(LocalDate::compareTo))
							.thenComparing(Comparator.comparing(SeisanshoDto::getTimeChargeStartTime, Comparator.nullsFirst(LocalDateTime::compareTo))))
					.collect(Collectors.toList());

			notTargetSeisanSeqList = displayTargetList.stream()
					.filter(entity -> !seisanSeq.equals(entity.getSeisanSeq()))
					.sorted(Comparator.comparing(SeisanshoDto::getHasseiDateDate, Comparator.nullsFirst(LocalDate::compareTo))
							.thenComparing(Comparator.comparing(SeisanshoDto::getTimeChargeStartTime, Comparator.nullsFirst(LocalDateTime::compareTo))))
					.collect(Collectors.toList());

		} else {
			// 入出金データの場合

			targetSeisanSeqList = displayTargetList.stream()
					.filter(entity -> seisanSeq.equals(entity.getSeisanSeq()))
					.sorted(Comparator.comparing(SeisanshoDto::getHasseiDate)
							.thenComparing(Comparator.comparing(SeisanshoDto::getKaikeiKirokuSeq)))
					.collect(Collectors.toList());

			notTargetSeisanSeqList = displayTargetList.stream()
					.filter(entity -> !seisanSeq.equals(entity.getSeisanSeq()))
					.sorted(Comparator.comparing(SeisanshoDto::getHasseiDate)
							.thenComparing(Comparator.comparing(SeisanshoDto::getKaikeiKirokuSeq)))
					.collect(Collectors.toList());
		}

		List<SeisanshoDto> outputTargetList = new ArrayList<SeisanshoDto>();
		outputTargetList.addAll(targetSeisanSeqList);
		outputTargetList.addAll(notTargetSeisanSeqList);
		return outputTargetList;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 月々の支払日リストを取得します。
	 *
	 * @return
	 */
	private List<MonthShiharaiDate> getMonthShihraiDateList() {

		List<MonthShiharaiDate> monthShiharaiDateList = new ArrayList<MonthShiharaiDate>();

		for (int i = 1; i <= CommonConstant.MAX_MONTH_SHIHARAI_DATE; i++) {
			String shiharaiDate = String.valueOf(i);
			MonthShiharaiDate monthShiharaiDate = new SeisanshoViewForm.MonthShiharaiDate();
			monthShiharaiDate.setCd(shiharaiDate);
			monthShiharaiDate.setVal(shiharaiDate);

			monthShiharaiDateList.add(monthShiharaiDate);
		}

		return monthShiharaiDateList;
	}

	/**
	 * 支払期間のリストを取得します。
	 *
	 * @return
	 */
	private List<ShiharaiStartYear> getShiharaiStartYear() {

		List<ShiharaiStartYear> shiharaiStartYearList = new ArrayList<ShiharaiStartYear>();

		// 支払期間の設定
		LocalDate now = LocalDate.now();
		int currentYear = now.getYear();

		// 過去
		int fromYear = currentYear - CommonConstant.FROM_SHIHARAI_KIKAN_YEAR;
		// 未来
		int toYear = currentYear + CommonConstant.TO_SHIHARAI_KIKAN_YEAR;
		for (int i = fromYear; i <= toYear; i++) {
			String shiharaiYear = String.valueOf(i);
			ShiharaiStartYear shiharaiStartYear = new SeisanshoViewForm.ShiharaiStartYear();
			shiharaiStartYear.setCd(shiharaiYear);
			shiharaiStartYear.setVal(shiharaiYear);

			shiharaiStartYearList.add(shiharaiStartYear);
		}

		return shiharaiStartYearList;
	}

	/**
	 * 精算記録を登録
	 *
	 * @param editForm
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	private Long insertSeisankirokuData(SeisanshoEditForm editForm, SeisanshoViewForm viewForm) throws AppException {

		// 精算記録情報
		TSeisanKirokuEntity seisanKirokuEntity = new TSeisanKirokuEntity();

		// 現在の精算IDの枝番の最大値を取得
		Long currentEdaNo = tSeisanshoDao.getMaxBranchNo(editForm.getSeisanCustomerId());
		seisanKirokuEntity.setSeisanId(currentEdaNo + 1);
		seisanKirokuEntity.setCustomerId(editForm.getSeisanCustomerId());
		seisanKirokuEntity.setAnkenId(editForm.getSeisanAnkenId());
		seisanKirokuEntity.setSeisanStatus(editForm.getSeisanshoCreateDto().getSeisanStatus());

		// 精算額を計算します。
		BigDecimal seisanGaku = viewForm.getSeisangakuTotal();

		// 請求 or 精算（返金）の判定
		if (seisanGaku.compareTo(BigDecimal.ZERO) == 0) {
			// 0円精算
			seisanKirokuEntity.setSeisanGaku(seisanGaku);
			seisanKirokuEntity.setSeisanKubun(SeisanKirokuKubun.SEISAN.getCd());

		} else if (seisanGaku.compareTo(BigDecimal.ZERO) < 0) {
			// 請求の場合
			this.seisanshoDto2EntityForSeikyu(editForm, seisanKirokuEntity);
			seisanKirokuEntity.setSeisanGaku(seisanGaku.negate());
			seisanKirokuEntity.setSeisanKubun(SeisanKirokuKubun.SEIKYU.getCd());

		} else {
			// 精算（返金）の場合
			this.seisanshoDto2EntityForSeisan(editForm, seisanKirokuEntity);
			seisanKirokuEntity.setSeisanGaku(seisanGaku);
			seisanKirokuEntity.setSeisanKubun(SeisanKirokuKubun.SEISAN.getCd());

		}

		try {
			// 登録処理を行います。
			tSeisanshoDao.insert(seisanKirokuEntity);

		} catch (OptimisticLockingFailureException e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + "：" + viewForm.getDispSeisangakuTotal() + "が正しく変換できませんでした。");
			throw new AppException(MessageEnum.MSG_E00025, e);

		}

		return seisanKirokuEntity.getSeisanSeq();
	}

	/**
	 * 精算記録情報を更新します。
	 *
	 * @param viewForm 精算書作成画面の表示用フォーム
	 * @param editForm 精算書作成画面の編集用フォーム
	 * @throws AppException
	 */
	private void updateSeisanKirokuData(SeisanshoViewForm viewForm, SeisanshoEditForm editForm) throws AppException {

		// 請求・精算記録の画面入力情報
		SeisanshoCreateDto createDto = editForm.getSeisanshoCreateDto();

		// 更新対象の精算記録情報
		TSeisanKirokuEntity seisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(createDto.getSeisanSeq());
		// 精算記録情報を設定
		seisanKirokuEntity.setSeisanStatus(createDto.getSeisanStatus());
		seisanKirokuEntity.setCustomerId(editForm.getSeisanCustomerId());
		seisanKirokuEntity.setAnkenId(editForm.getSeisanAnkenId());

		// 精算額を計算します。
		BigDecimal seisanGaku = viewForm.getSeisangakuTotal();
		if (seisanGaku.compareTo(BigDecimal.ZERO) == 0) {
			// 0円精算
			TSeisanKirokuEntity newSeisanKirokuEntity = new TSeisanKirokuEntity();
			newSeisanKirokuEntity.setSeisanSeq(seisanKirokuEntity.getSeisanSeq());
			newSeisanKirokuEntity.setSeisanId(seisanKirokuEntity.getSeisanId());
			newSeisanKirokuEntity.setCustomerId(editForm.getSeisanCustomerId());
			newSeisanKirokuEntity.setAnkenId(editForm.getSeisanAnkenId());
			newSeisanKirokuEntity.setSeisanStatus(createDto.getSeisanStatus());
			newSeisanKirokuEntity.setSeisanKubun(SeisanKirokuKubun.SEISAN.getCd());
			newSeisanKirokuEntity.setSeisanGaku(seisanGaku);
			newSeisanKirokuEntity.setCreatedAt(seisanKirokuEntity.getCreatedAt());
			newSeisanKirokuEntity.setCreatedBy(seisanKirokuEntity.getCreatedBy());
			newSeisanKirokuEntity.setVersionNo(seisanKirokuEntity.getVersionNo());
			// 更新対象を上書き
			seisanKirokuEntity = newSeisanKirokuEntity;

		} else if (seisanGaku.compareTo(BigDecimal.ZERO) < 0) {
			// 請求の場合
			this.seisanshoDto2EntityForSeikyu(editForm, seisanKirokuEntity);
			seisanKirokuEntity.setSeisanGaku(seisanGaku.negate());
			seisanKirokuEntity.setSeisanKubun(SeisanKirokuKubun.SEIKYU.getCd());

		} else {
			// 精算（返金）の場合
			this.seisanshoDto2EntityForSeisan(editForm, seisanKirokuEntity);
			seisanKirokuEntity.setSeisanGaku(seisanGaku);
			seisanKirokuEntity.setSeisanKubun(SeisanKirokuKubun.SEISAN.getCd());

		}
		// 更新情報
		seisanKirokuEntity.setLastEditAt(LocalDateTime.now());
		seisanKirokuEntity.setLastEditBy(SessionUtils.getLoginAccountSeq());

		try {
			// 更新処理を行います。
			tSeisanshoDao.update(seisanKirokuEntity);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00012, ex);

		}
	}

	/**
	 * 会計記録情報を更新する。<br>
	 *
	 * <pre>
	 * 精算書作成時に使用する。
	 * </pre>
	 *
	 * @param seisanSeq
	 * @param kaikeiSeqList
	 * @param isSeisanZero
	 * @throws AppException
	 */
	private void updateKaikeikiroku(Long seisanSeq, List<Long> checkKaikeiSeqList, boolean isSeisanZero) throws AppException {

		// 既に紐付けがある会計記録
		List<TKaikeiKirokuEntity> tKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeisanSeq(seisanSeq);
		// 紐付け会計記録SEQ
		List<Long> himozukeKaikeiKirokuSeqList = tKaikeiKirokuEntities.stream()
				.map(TKaikeiKirokuEntity::getKaikeiKirokuSeq)
				.collect(Collectors.toList());

		// 更新対象
		List<Long> targetKaikeiKirokuSeqList = Stream.concat(himozukeKaikeiKirokuSeqList.stream(), checkKaikeiSeqList.stream())
				.distinct()
				.collect(Collectors.toList());

		// チェックがある会計記録
		List<TKaikeiKirokuEntity> targetKaikeiKirokuEntities = tKaikeiKirokuDao.selectBySeqList(targetKaikeiKirokuSeqList);
		if (CollectionUtils.isEmpty(targetKaikeiKirokuEntities)) {
			// 会計記録がない場合 -> すでに削除されている
			logger.error(ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 精算記録に紐付ける
		targetKaikeiKirokuEntities.forEach(entity -> {

			// チェックした会計記録を選別
			boolean himozukeFlg = false;
			Long kaikeiKirokuSeq = entity.getKaikeiKirokuSeq();
			for (Long seq : checkKaikeiSeqList) {
				if (kaikeiKirokuSeq.equals(seq)) {
					himozukeFlg = true;
					break;
				}
			}
			if (himozukeFlg) {
				// 紐付け
				entity.setSeisanSeq(seisanSeq);
				// 精算時の差引計が0だった場合、精算処理日を登録する
				if (isSeisanZero) {
					entity.setSeisanDate(LocalDate.now());
				}

			} else {
				// 紐付けからはずす
				entity.setSeisanSeq(null);
			}
		});

		// 更新処理
		tKaikeiKirokuDao.update(targetKaikeiKirokuEntities);

	}

	/**
	 * 入出金予定情報を登録します。
	 *
	 * @param viewForm
	 * @param editForm
	 * @throws AppException
	 */
	private void deleteInsertNyushukkinYotei(SeisanshoViewForm viewForm, SeisanshoEditForm editForm) throws AppException {

		// 入力値を取得
		SeisanshoCreateDto createDto = editForm.getSeisanshoCreateDto();
		Long seisanSeq = createDto.getSeisanSeq();

		// 削除対象の入出金予定情報を取得します。
		List<TNyushukkinYoteiEntity> deleteTargetList = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);

		try {
			// 入出金予定情報を削除
			tNyushukkinYoteiDao.delete(deleteTargetList);

		} catch (OptimisticLockingFailureException e) {
			// ロガー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, e);
			throw new AppException(MessageEnum.MSG_E00025, e);

		}

		// 入出金予定情報を登録

		// 0円精算の場合は、入出金予定を作成せずに返却
		if (editForm.isSeisanZero()) {
			return;
		}

		// 登録用のListを作成
		List<TNyushukkinYoteiEntity> insertYoteiList = new ArrayList<>();

		if (SeikyuType.IKKATSU.equalsByCode(createDto.getSeikyuType())) {
			// 一括の場合 -> 一括時の予定データを作成して登録用Listにadd
			TNyushukkinYoteiEntity nyushukkinYoteiEntity = this.createNyushukkinYoteiForIkkatsu(seisanSeq, viewForm, editForm);
			if (nyushukkinYoteiEntity != null) {
				insertYoteiList.add(nyushukkinYoteiEntity);
			}
		} else if (SeikyuType.BUNKATSU.equalsByCode(createDto.getSeikyuType())) {
			// 分割の場合 -> 分割時の予定データを作成して登録用Listにadd
			List<TNyushukkinYoteiEntity> nyushukkinYoteiList = this.createNyushukkinYoteiForBunkatsu(seisanSeq, viewForm, editForm);
			if (!CollectionUtils.isEmpty(nyushukkinYoteiList)) {
				insertYoteiList.addAll(nyushukkinYoteiList);
			}
		} else {
			// 請求時は↑上記の「精算の場合」一括支払いとして、入金予定を登録する
			// なにもしない
		}

		// 登録するデータがない もしくは仮精算の場合は何もせずに返却
		// 予定データ作成時にエラーが存在する可能性があるので、
		// 仮精算の場合もデータの作成処理までは行う必要がある
		if (CollectionUtils.isEmpty(insertYoteiList) || SeisanStatus.KARI_SEISAN.equalsByCode(createDto.getSeisanStatus())) {
			return;
		}

		// 登録処理
		try {
			tNyushukkinYoteiDao.insert(insertYoteiList);
		} catch (OptimisticLockingFailureException e) {
			// ロガー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, e);
			throw new AppException(MessageEnum.MSG_E00025, e);
		}

	}

	/**
	 * 入出金予定情報を生成します。<br>
	 * 一括支払い
	 *
	 * @param seisanSeq
	 * @param viewForm
	 * @param editForm
	 * @return
	 * @throws AppException
	 */
	private TNyushukkinYoteiEntity createNyushukkinYoteiForIkkatsu(Long seisanSeq, SeisanshoViewForm viewForm, SeisanshoEditForm editForm)
			throws AppException {

		TNyushukkinYoteiEntity nyushukkinYoteiEntity = new TNyushukkinYoteiEntity();
		SeisanshoCreateDto inputCreateDto = editForm.getSeisanshoCreateDto();

		LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
		decimalFormat.setParseBigDecimal(true);
		BigDecimal seisanGaku = new BigDecimal(0);

		// 各情報を設定します。
		nyushukkinYoteiEntity.setSeisanSeq(seisanSeq);
		nyushukkinYoteiEntity.setCustomerId(editForm.getSeisanCustomerId());
		nyushukkinYoteiEntity.setAnkenId(editForm.getSeisanAnkenId());

		try {
			// 精算額
			seisanGaku = (BigDecimal) decimalFormat.parse(viewForm.getDispSeisangakuTotal());

		} catch (ParseException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + "：" + viewForm.getDispSeisangakuTotal() + "が正しく変換できませんでした。");
			throw new AppException(MessageEnum.MSG_E00030, ex);

		}

		if (seisanGaku.compareTo(BigDecimal.ZERO) > 0) {
			// 精算額がプラス = 返金の場合
			nyushukkinYoteiEntity.setNyushukkinType(NyushukkinType.SHUKKIN.getCd());
			nyushukkinYoteiEntity.setNyushukkinKomokuId(Long.valueOf(NyushukkinKomokuType.HENKIN.getCd()));
			nyushukkinYoteiEntity.setNyushukkinYoteiGaku(seisanGaku);
			nyushukkinYoteiEntity.setShukkinSakiKozaSeq(inputCreateDto.getHenkinKozaSeq());
			nyushukkinYoteiEntity.setNyushukkinYoteiDate(
					DateUtils.parseToLocalDate(inputCreateDto.getPaymentLimitDt(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			// 返金先
			String henkinSakiType = inputCreateDto.getHenkinSakiType();
			nyushukkinYoteiEntity.setShukkinShiharaiSakiType(henkinSakiType);

			if (TargetType.CUSTOMER.equalsByCode(henkinSakiType)) {
				// 顧客から選択する場合は、顧客IDを設定する。
				nyushukkinYoteiEntity.setShukkinCustomerId(editForm.getSeisanCustomerId());

			} else if (TargetType.KANYOSHA.equalsByCode(henkinSakiType)) {
				// 関与者から選択する場合は、関与者IDを設定する。
				nyushukkinYoteiEntity.setShukkinKanyoshaSeq(inputCreateDto.getHenkinKanyoshaSeq());

			}

		} else {
			// 精算額がマイナス = 請求の場合
			nyushukkinYoteiEntity.setNyushukkinType(NyushukkinType.NYUKIN.getCd());
			nyushukkinYoteiEntity.setNyushukkinKomokuId(Long.valueOf(NyushukkinKomokuType.SEIKYU.getCd()));
			nyushukkinYoteiEntity.setNyushukkinYoteiGaku(seisanGaku.negate());
			nyushukkinYoteiEntity.setNyukinSakiKozaSeq(inputCreateDto.getSeikyuKozaSeq());
			nyushukkinYoteiEntity.setNyushukkinYoteiDate(
					DateUtils.parseToLocalDate(inputCreateDto.getPaymentYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));

			// 支払者
			String shiharaishaType = inputCreateDto.getShiharaishaType();
			nyushukkinYoteiEntity.setNyukinShiharaishaType(shiharaishaType);

			if (TargetType.CUSTOMER.equalsByCode(shiharaishaType)) {
				// 顧客から選択する場合は、顧客IDを設定する。
				nyushukkinYoteiEntity.setNyukinCustomerId(editForm.getSeisanCustomerId());

			} else if (TargetType.KANYOSHA.equalsByCode(shiharaishaType)) {
				// 関与者から選択する場合は、関与者IDを設定する。
				nyushukkinYoteiEntity.setNyukinKanyoshaSeq(inputCreateDto.getShiharaiKanyoshaSeq());

			}
		}

		return nyushukkinYoteiEntity;
	}

	/**
	 * 入出金予定情報のリストを生成します。<br>
	 *
	 * <pre>
	 * 分割支払い時用です。
	 * </pre>
	 *
	 * @param seisanSeq 精算SEQ
	 * @param viewForm 精算書作成画面情報のフォーム
	 * @param editForm 精算書作成画面の編集用フォーム
	 * @return 入出金予定情報のリスト
	 * @throws AppException
	 */
	private List<TNyushukkinYoteiEntity> createNyushukkinYoteiForBunkatsu(Long seisanSeq, SeisanshoViewForm viewForm,
			SeisanshoEditForm editForm) throws AppException {

		// 入力した情報を取得します。
		SeisanshoCreateDto inputCreateDto = editForm.getSeisanshoCreateDto();

		// --------------------------------------------
		// String -> BigDecimal
		// --------------------------------------------
		LoiozDecimalFormat decimalFormat = new LoiozDecimalFormat();
		decimalFormat.setParseBigDecimal(true);
		BigDecimal monthShiharaiGaku = new BigDecimal(0);

		try {
			// 月々の支払額
			monthShiharaiGaku = (BigDecimal) decimalFormat.parse(inputCreateDto.getMonthlyPay());

		} catch (ParseException ex) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + "：" + inputCreateDto.getMonthlyPay() + "が正しく変換できませんでした。");
			throw new AppException(MessageEnum.MSG_E00030, ex);

		}

		/* 支払開始年月・毎月の支払日 ⇒ 支払開始年月日として設定 */
		// 支払い開始月
		String shiharaiStartMonth = inputCreateDto.getShiharaiStartMonth();
		// 支払い開始月の0埋め
		String shiharaiStartDtMonth = StringUtils.leftPad(shiharaiStartMonth, 2, CommonConstant.ZERO);

		// 支払い開始日
		String monthShiharaiDate = null;
		if (SeisanShiharaiMonthDay.DESIGNATEDDAY.equalsByCode(inputCreateDto.getLastDay())) {
			// 指定日付
			monthShiharaiDate = inputCreateDto.getPaymentDate();
		} else {
			// 月末
			monthShiharaiDate = END_OF_MONTH;
		}
		// 支払い開始日の0埋め
		String shiharaiStartDtDay = StringUtils.leftPad(monthShiharaiDate, 2, CommonConstant.ZERO);
		// 支払い開始年
		String shiharaiStartDtYear = inputCreateDto.getShiharaiStartYear();

		// 支払い開始：年月日
		String shiharaiDate = shiharaiStartDtYear + "/" + shiharaiStartDtMonth + "/" + shiharaiStartDtDay;
		LocalDate shiharaiStartDate = DateUtils.parseToLocalDate(shiharaiDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);

		// --------------------------------------------
		// 精算額 ÷ 月々の支払額 = 分割回数
		// --------------------------------------------
		// 精算額はマイナスなので正の数に変換
		BigDecimal seisanTotal = viewForm.getSeisangakuTotal().negate();
		BigDecimal bunkatsuCount = commonKaikeiService.calcBunkatsuKaisu(seisanTotal, monthShiharaiGaku);

		// --------------------------------------------
		// 端数
		// --------------------------------------------
		BigDecimal totalNyukinGaku = monthShiharaiGaku.multiply(bunkatsuCount);
		BigDecimal hasu = seisanTotal.subtract(totalNyukinGaku);

		// 入金予定を設定
		List<TNyushukkinYoteiEntity> nyushukkinYoteiList = new ArrayList<TNyushukkinYoteiEntity>();
		for (int i = 0; i < bunkatsuCount.intValue(); i++) {
			// 入金予定
			TNyushukkinYoteiEntity nyushukkinYoteiEntity = new TNyushukkinYoteiEntity();

			LocalDate nyushukkinYoteiDate = null;
			BigDecimal nyushukkinYoteiGaku = new BigDecimal(0);

			// 情報を設定します。
			nyushukkinYoteiEntity.setSeisanSeq(seisanSeq);
			nyushukkinYoteiEntity.setCustomerId(editForm.getSeisanCustomerId());
			nyushukkinYoteiEntity.setAnkenId(editForm.getSeisanAnkenId());
			nyushukkinYoteiEntity.setNyushukkinType(NyushukkinType.NYUKIN.getCd());

			// 入金予定日の設定
			if (i == 0) {
				// 初回
				nyushukkinYoteiDate = shiharaiStartDate;
			} else {
				// 2回目以降
				if (END_OF_MONTH.equals(monthShiharaiDate)) {
					// 月末
					nyushukkinYoteiDate = shiharaiStartDate.plusMonths(i).with(TemporalAdjusters.lastDayOfMonth());
				} else {
					// 指定日付
					nyushukkinYoteiDate = shiharaiStartDate.plusMonths(i);
				}
			}

			// 入金予定額の設定
			if (hasu.compareTo(BigDecimal.ZERO) > 0) {
				// 端数がある場合

				if (Hasu.FIRST.equalsByCode(inputCreateDto.getHasu())) {
					// 端数が初回の場合
					if (i == 0) {
						// 初回の入金予定額に端数を足します。
						nyushukkinYoteiGaku = monthShiharaiGaku.add(hasu);
					} else {
						// 初回以外は、月々の支払額を予定額にします。
						nyushukkinYoteiGaku = monthShiharaiGaku;
					}

				} else {
					// 最終
					if (i == bunkatsuCount.intValue() - 1) {
						// 最終回の入金予定額に端数を足します。
						nyushukkinYoteiGaku = monthShiharaiGaku.add(hasu);
					} else {
						// 最終回以外は、月々の支払額を入出金予定額にします。
						nyushukkinYoteiGaku = monthShiharaiGaku;
					}

				}

			} else {
				// 端数がない場合
				nyushukkinYoteiGaku = monthShiharaiGaku;
			}

			// 各情報を設定します。
			nyushukkinYoteiEntity.setNyushukkinKomokuId(Long.valueOf(NyushukkinKomokuType.SEIKYU.getCd()));
			nyushukkinYoteiEntity.setNyushukkinYoteiDate(nyushukkinYoteiDate);
			nyushukkinYoteiEntity.setNyushukkinYoteiGaku(nyushukkinYoteiGaku);
			nyushukkinYoteiEntity.setNyukinSakiKozaSeq(inputCreateDto.getSeikyuKozaSeq());

			// 支払者
			String shiharaishaType = inputCreateDto.getShiharaishaType();
			nyushukkinYoteiEntity.setNyukinShiharaishaType(shiharaishaType);

			if (TargetType.CUSTOMER.equalsByCode(shiharaishaType)) {
				// 顧客から選択する場合は、顧客IDを設定する。
				nyushukkinYoteiEntity.setNyukinCustomerId(editForm.getSeisanCustomerId());

			} else if (TargetType.KANYOSHA.equalsByCode(shiharaishaType)) {
				// 関与者から選択する場合は、関与者IDを設定する。
				nyushukkinYoteiEntity.setNyukinKanyoshaSeq(inputCreateDto.getShiharaiKanyoshaSeq());

			}

			// リストに追加します。
			nyushukkinYoteiList.add(nyushukkinYoteiEntity);
		}

		return nyushukkinYoteiList;
	}

	/**
	 * 各項目の合計金額を計算します。<br>
	 *
	 * <pre>
	 * 精算書作成用です。
	 * </pre>
	 *
	 * @param viewForm
	 * @param seisanshoDtoList
	 */
	private void calcTotalForKaikeiKiroku(SeisanshoViewForm viewForm, List<SeisanshoDto> seisanshoDtoList) {

		List<BigDecimal> nyukinList = new ArrayList<BigDecimal>();
		List<BigDecimal> shukkinList = new ArrayList<BigDecimal>();
		List<BigDecimal> shukkinIncludeTaxList = new ArrayList<BigDecimal>();
		List<BigDecimal> hoshuList = new ArrayList<BigDecimal>();
		List<BigDecimal> hoshuTaxList = new ArrayList<BigDecimal>();
		List<BigDecimal> hoshuIncludeTaxList = new ArrayList<BigDecimal>();
		List<BigDecimal> hoshuTax8perList = new ArrayList<BigDecimal>();
		List<BigDecimal> hoshuTax10perList = new ArrayList<BigDecimal>();
		List<BigDecimal> taxList = new ArrayList<BigDecimal>();
		List<BigDecimal> gensenList = new ArrayList<BigDecimal>();

		// --------------------------------------------------------
		// 入金・出金／報酬・源泉徴収
		// ------------------------------ --------------------------
		for (SeisanshoDto seisanshoDto : seisanshoDtoList) {
			if (NyushukkinType.NYUKIN.equalsByCode(seisanshoDto.getNyushukkinType())) {
				// 入金の場合
				nyukinList.add(seisanshoDto.getTaxIncludedNyukingaku());

			} else if (NyushukkinType.SHUKKIN.equalsByCode(seisanshoDto.getNyushukkinType()) && seisanshoDto.getHoshuKomokuId() == null) {
				// 出金の場合
				shukkinList.add(seisanshoDto.getTaxIncludedShukkingaku());
				shukkinIncludeTaxList.add(seisanshoDto.getTaxIncludedShukkingaku());

			} else if (NyushukkinType.SHUKKIN.equalsByCode(seisanshoDto.getNyushukkinType()) && seisanshoDto.getHoshuKomokuId() != null) {
				// 報酬の場合
				hoshuList.add(seisanshoDto.getTaxIncludedShukkingaku());
				hoshuIncludeTaxList.add(seisanshoDto.getTaxIncludedShukkingaku());
				if (seisanshoDto.getTax() != null) {
					hoshuTaxList.add(seisanshoDto.getTax());
					hoshuIncludeTaxList.add(seisanshoDto.getTax());
					if (TaxRate.EIGHT_PERCENT.equalsByCode(seisanshoDto.getTaxRate())) {
						// 8%の場合
						hoshuTax8perList.add(seisanshoDto.getTax());
					} else if (TaxRate.TEN_PERCENT.equalsByCode(seisanshoDto.getTaxRate())) {
						// 10%の場合
						hoshuTax10perList.add(seisanshoDto.getTax());
					} else {
						// それ以外の場合はなにもしない(免税)
					}
				}

				if (GensenTarget.TAISHOU.equalsByCode(seisanshoDto.getGensenchoshuFlg()) && seisanshoDto.getGensenchoshuGaku() != null) {
					// 源泉徴収対象の場合

					// 源泉徴収額
					gensenList.add(seisanshoDto.getGensenchoshuGaku());
				}
			}

			// 消費税
			if (seisanshoDto.getTax() != null) {
				taxList.add(seisanshoDto.getTax());
			}
		}

		// --------------------------------------------------------
		// 各項目の合計金額計算
		// --------------------------------------------------------
		BigDecimal nyukinTotal = commonKaikeiService.calcTotal(nyukinList);
		BigDecimal taxTotal = commonKaikeiService.calcTotal(taxList);
		BigDecimal shukkinIncludeTaxTotal = commonKaikeiService.calcTotal(shukkinIncludeTaxList);
		BigDecimal hoshuTotal = commonKaikeiService.calcTotal(hoshuList);
		BigDecimal hoshuIncludeTaxTotal = commonKaikeiService.calcTotal(hoshuIncludeTaxList);
		BigDecimal hoshuTaxTotal = commonKaikeiService.calcTotal(hoshuTaxList);
		BigDecimal hoshuTax8perTotal = commonKaikeiService.calcTotal(hoshuTax8perList);
		BigDecimal hoshuTax10perTotal = commonKaikeiService.calcTotal(hoshuTax10perList);
		BigDecimal gensenTotal = commonKaikeiService.calcTotal(gensenList);
		// --------------------------------------------------------
		// 報酬小計
		// 報酬計 + 消費税計 - 源泉徴収計
		// --------------------------------------------------------
		BigDecimal hoshuShokei = hoshuIncludeTaxTotal.subtract(gensenTotal);

		// --------------------------------------------------------
		// 差し引き計の計算
		// 入金計 - (出金計(税込み) + 報酬計(税込み) - 源泉徴収計)
		// --------------------------------------------------------

		BigDecimal calcTotal = shukkinIncludeTaxTotal.add(hoshuIncludeTaxTotal).subtract(gensenTotal);

		// 差し引き計を計算します。
		BigDecimal sashiHikiTotal = nyukinTotal.subtract(calcTotal);

		// --------------------------------------------------------
		// viewFormに設定します。
		// --------------------------------------------------------
		viewForm.setSeisangakuTotal(sashiHikiTotal);
		viewForm.setDispSeisangakuTotal(commonKaikeiService.toDispAmountLabel(sashiHikiTotal));
		viewForm.setKaikeiKirokuNyukinTotal(commonKaikeiService.toDispAmountLabel(nyukinTotal));
		viewForm.setKaikeiKirokuShukkinTotal(commonKaikeiService.toDispAmountLabel(shukkinIncludeTaxTotal));
		viewForm.setKaikeiKirokuHoshuTotal(commonKaikeiService.toDispAmountLabel(hoshuTotal));
		viewForm.setKaikeiKirokuHoshuShoukei(commonKaikeiService.toDispAmountLabel(hoshuShokei));
		viewForm.setKaikeiKirokuHoshuTaxTotal(commonKaikeiService.toDispAmountLabel(hoshuTaxTotal));
		viewForm.setKaikeiKirokuHoshuTax8perTotal(commonKaikeiService.toDispAmountLabel(hoshuTax8perTotal));
		viewForm.setKaikeiKirokuHoshuTax10perTotal(commonKaikeiService.toDispAmountLabel(hoshuTax10perTotal));
		viewForm.setKaikeiKirokuGensenTotal(commonKaikeiService.toDispAmountLabel(gensenTotal.negate())); // 源泉徴収はマイナスにする
		viewForm.setKaikeiKirokuTaxTotal(commonKaikeiService.toDispAmountLabel(taxTotal));
		viewForm.setKaikeiKirokuSashiHikiTotal(commonKaikeiService.toDispAmountLabel(sashiHikiTotal));
	}

	/**
	 * 画面に表示する会計記録情報を特定します。
	 *
	 * @param seisanshoDtoList 精算書作成画面に表示する会計記録情報のDtoリスト
	 * @param isHoshu true:報酬情報、false:入出金情報
	 * @return isHoshuがtrue:報酬情報のDtoリスト、isHoshuがfalse:入出金情報のDtoリスト
	 */
	private List<SeisanshoDto> dispTargetKaikeiKirokuList(List<SeisanshoDto> seisanshoDtoList, boolean isHoshu) {

		List<SeisanshoDto> displayTargetList = new ArrayList<SeisanshoDto>();

		seisanshoDtoList.forEach(dto -> {
			if (isHoshu) {
				// 報酬情報を特定する場合
				if (dto.getHoshuKomokuId() != null && dto.getNyushukkinKomokuId() == null) {
					displayTargetList.add(dto);
				}

			} else {
				// 入出金情報を特定したい場合
				if (dto.getHoshuKomokuId() == null && dto.getNyushukkinKomokuId() != null) {
					displayTargetList.add(dto);
				}
			}
		});

		return displayTargetList;
	}

	/**
	 * 精算書の編集が可能かどうか判定します。
	 *
	 * @param seisanKirokuEntity 精算記録情報のEntity
	 * @return true:編集可能、false:編集不可
	 */
	private boolean isAbledEditSeisansho(TSeisanKirokuEntity seisanKirokuEntity) {

		// 精算SEQ
		Long seisanSeq = seisanKirokuEntity.getSeisanSeq();
		if (SeisanKirokuKubun.SEIKYU.equalsByCode(seisanKirokuEntity.getSeisanKubun())) {
			// -------------------------------------------------
			// 請求の場合
			// 回収額を計算します。
			// -------------------------------------------------
			// 精算SEQに紐づく会計記録情報を取得
			List<TKaikeiKirokuEntity> targetKaikeiKirokuEntityList = tKaikeiKirokuDao.selectBySeisanSeqList(Arrays.asList(seisanSeq));
			List<BigDecimal> kaishuGakuList = new ArrayList<BigDecimal>();
			BigDecimal kaishuGaku = new BigDecimal(0);

			// 回収額(実際に入金された金額)を計算
			if (!CollectionUtils.isEmpty(targetKaikeiKirokuEntityList)) {
				for (TKaikeiKirokuEntity entity : targetKaikeiKirokuEntityList) {
					kaishuGakuList.add(entity.getNyukinGaku());
				}
			}

			kaishuGaku = commonKaikeiService.calcTotal(kaishuGakuList);

			if (kaishuGaku.compareTo(BigDecimal.ZERO) != 0) {
				// 1円でも回収実績がある場合は編集不可
				return false;
			}

		} else {
			// -------------------------------------------------
			// 返金の場合
			// 返金済かどうか判定します。
			// -------------------------------------------------
			// 精算SEQに紐づく入出金予定情報を取得します。
			List<TNyushukkinYoteiEntity> yoteiEntityList = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);

			for (TNyushukkinYoteiEntity yotei : yoteiEntityList) {
				if (yotei.getNyushukkinDate() != null || yotei.getNyushukkinGaku() != null) {
					// 返金実績がある場合
					return false;
				}
			}

			if (SystemFlg.FLG_ON.getCd().equals(seisanKirokuEntity.getPoolFlg())) {
				// 預かり金としてプールする場合
				List<TKaikeiKirokuEntity> poolKaikeiKirokuList = tKaikeiKirokuDao.selectPoolBySeisanSeq(seisanSeq);
				if (!CollectionUtils.isEmpty(poolKaikeiKirokuList)) {
					return false;
				}
			}
		}

		return true;
	}

}