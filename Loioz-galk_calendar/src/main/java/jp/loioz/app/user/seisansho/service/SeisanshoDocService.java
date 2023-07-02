package jp.loioz.app.user.seisansho.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0009ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.SeisanshoExcelSetDto;
import jp.loioz.app.common.form.Keys;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.user.seisansho.form.SeisanshoEditForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CalType;
import jp.loioz.common.constant.CommonConstant.GensenTarget;
import jp.loioz.common.constant.CommonConstant.Hasu;
import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SeikyuType;
import jp.loioz.common.constant.CommonConstant.SeisanKirokuKubun;
import jp.loioz.common.constant.CommonConstant.SeisanShiharaiMonthDay;
import jp.loioz.common.constant.CommonConstant.SeisanStatus;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TaxRate;
import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TKanyoshaDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSeisanshoDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenTantoAccountDto;
import jp.loioz.dto.ExcelJippiMeisaiDto;
import jp.loioz.dto.ExcelJippiMeisaiShukkinListBean;
import jp.loioz.dto.ExcelJippiMeisaiShukkinListDto;
import jp.loioz.dto.ExcelLawyerHoshuDto;
import jp.loioz.dto.ExcelSeikyushoDto;
import jp.loioz.dto.ExcelSeisanshoDto;
import jp.loioz.dto.ExcelSeisanshoSetDto;
import jp.loioz.dto.ExcelShiharaiKeikakuDto;
import jp.loioz.dto.ExcelShiharaiKeikakuListDto;
import jp.loioz.dto.ExcelSofushoDto;
import jp.loioz.dto.ExcelTimeChargeDto;
import jp.loioz.dto.ExcelTimeChargeListDto;
import jp.loioz.dto.KanyoshaBean;
import jp.loioz.dto.SeisanshoCreateDto;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TGinkoKozaEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 精算書出力のサービスクラス
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SeisanshoDocService extends DefaultService {

	/** 会計系の共通サービス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 帳票の共通サービス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** テナント用のDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 名簿Daoクラス **/
	@Autowired
	private TPersonDao tPersonDao;

	/** 銀行口座Daoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 会計Daoクラス */
	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	/** 精算書Daoクラス */
	@Autowired
	private TSeisanshoDao tSeisanshoDao;

	/** 関与者Daoクラス */
	@Autowired
	private TKanyoshaDao tKanyoshaDao;

	/** 入出金予定Daoクラス */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;
	/**
	 * 設定クラス
	 */
	@Autowired
	private ExcelConfig config;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 精算、仮精算時のExcel出力用データを作成します
	 *
	 * @param seisanshoEditForm
	 * @param seisanKubun
	 * @param seisanZeroFlag
	 * @return
	 * @throws AppException
	 */
	public ExcelSeisanshoSetDto createSeisanDataAll(SeisanshoEditForm seisanshoEditForm, String seisanKubun, boolean seisanZeroFlag)
			throws AppException {

		// ■精算書データ一式
		ExcelSeisanshoSetDto excelSeisanshoSetDto = new ExcelSeisanshoSetDto();

		// ■送付書データを作成
		ExcelSofushoDto sofushoData = this.createSofushoData(seisanshoEditForm, seisanKubun, seisanZeroFlag);
		excelSeisanshoSetDto.setSofushoData(sofushoData);

		// ■支払計画データ
		ExcelShiharaiKeikakuDto shiharaiKeikakuData = new ExcelShiharaiKeikakuDto();

		if (SeisanKirokuKubun.SEIKYU.equalsByCode(seisanKubun)) {
			// 請求の場合

			// ■請求書データを取得
			ExcelSeikyushoDto seikyushoData = this.createSeikyushoData(seisanshoEditForm);
			excelSeisanshoSetDto.setSeikyushoData(seikyushoData);

			// 精算時、支払い計画シートに出力するデータ
			if (SeisanStatus.SEISAN.equalsByCode(seisanshoEditForm.getSeisanshoCreateDto().getSeisanStatus())) {
				// 精算の場合

				// 「精算」の場合は登録済の支払計画を取得
				shiharaiKeikakuData = this.getShiharaiKeikakuData(seisanshoEditForm, seikyushoData.getSeikyuGaku());

			} else {
				// 仮精算の場合

				// 支払計画データを作成
				shiharaiKeikakuData = this.createShiharaiKeikakuData(seisanshoEditForm, seikyushoData.getSeikyuGaku());

			}

		} else if (SeisanKirokuKubun.SEISAN.equalsByCode(seisanKubun)) {
			// 精算の場合

			// ■精算書データ作成
			ExcelSeisanshoDto seisanshoData = this.createSeisanshoData(seisanshoEditForm, sofushoData.getCustomerId().longValue());
			excelSeisanshoSetDto.setSeisanshoData(seisanshoData);

		} else {
			// 請求 or 精算以外は例外
			throw new RuntimeException();

		}
		// 支払い計画データを設定
		excelSeisanshoSetDto.setShiharaiKeikakuData(shiharaiKeikakuData);

		// ■タイムチャージデータを生成
		ExcelTimeChargeDto timeChargeData = this.createKariTimeChargeData(seisanshoEditForm);
		excelSeisanshoSetDto.setTimeChargeData(timeChargeData);

		// ■入出金明細
		ExcelJippiMeisaiDto jippiMeisaiData = this.createKariJippiMeisaiData(seisanshoEditForm);
		excelSeisanshoSetDto.setJippiMeisaiData(jippiMeisaiData);

		return excelSeisanshoSetDto;
	}

	/**
	 * 精算書Excel出力処理
	 *
	 * @param response
	 * @param seisanData
	 * @param seisanKubun
	 * @param seisanZeroFlag
	 * @throws Exception
	 */
	public void createExcelFile(HttpServletResponse response, ExcelSeisanshoSetDto seisanData, String seisanKubun, boolean seisanZeroFlag) {

		// ■1.Builderを定義
		En0009ExcelBuilder en0009ExcelBuilder = new En0009ExcelBuilder();
		en0009ExcelBuilder.setConfig(config);

		// ■2.DTOを定義
		SeisanshoExcelSetDto seisanshoExcelSetDto = en0009ExcelBuilder.createNewTargetBuilderDto();

		// ■3.DTOに設定するデータを取得と設定

		// 精算0フラグを設定
		seisanshoExcelSetDto.setSeisanZeroFlag(seisanZeroFlag);

		// 出力データを設定
		seisanshoExcelSetDto.setSofushoData(seisanData.getSofushoData());
		seisanshoExcelSetDto.setSeisanshoData(seisanData.getSeisanshoData());
		seisanshoExcelSetDto.setSeikyushoData(seisanData.getSeikyushoData());
		seisanshoExcelSetDto.setShiharaiKeikakuData(seisanData.getShiharaiKeikakuData());
		seisanshoExcelSetDto.setJippiMeisaiData(seisanData.getJippiMeisaiData());
		seisanshoExcelSetDto.setTimeChargeData(seisanData.getTimeChargeData());

		// 出力フラグを初期化
		boolean isCreateSeisanSheet = false;
		boolean isCreateSeikyuSheet = false;
		boolean isCreateSeikyuBunkatsuSheet = false;
		boolean isCreateJippiMesaiSheet = false;
		boolean isCreateShiharaiKeikakuSheet = false;
		boolean isCreateTimeChargeSheet = false;

		String fileName = null;
		if (SeisanKirokuKubun.SEIKYU.equalsByCode(seisanKubun)) {
			// 請求書の場合

			fileName = "請求書";
			if (SeikyuType.IKKATSU.equalsByCode(seisanData.getSeikyushoData().getSeikyuType())) {
				// 一括
				isCreateSeikyuSheet = true;

			} else if (SeikyuType.BUNKATSU.equalsByCode(seisanData.getSeikyushoData().getSeikyuType())) {
				// 分割
				isCreateSeikyuBunkatsuSheet = true;

			}

		} else if (SeisanKirokuKubun.SEISAN.equalsByCode(seisanKubun)) {
			// 精算書の場合

			fileName = "精算書";
			isCreateSeisanSheet = true;

		} else {
			// 想定しないケース
			throw new RuntimeException();
		}

		// 実費明細の作成
		isCreateJippiMesaiSheet = true;

		if (seisanData.getShiharaiKeikakuData().getCreateFlg()) {
			// 支払い計画の作成
			isCreateShiharaiKeikakuSheet = true;
		}

		// タイムチャージがあれば、作成する
		if (seisanData.getTimeChargeData().getCreateFlg()) {
			isCreateTimeChargeSheet = true;
		}
		// 送付書を作成する(作成したシート情報を使うため最後に作成する)
		if (!seisanData.getSofushoData().getCreateFlg()) {
			// 仮精算の場合、タイトルに仮を付与
			fileName = "仮" + fileName;
		}

		// 出力フラグを設定
		seisanshoExcelSetDto.setOutPutSohushoSheet(seisanData.getSofushoData().getCreateFlg());
		seisanshoExcelSetDto.setOutPutSeisanshoSheet(isCreateSeisanSheet);
		seisanshoExcelSetDto.setOutPutSeikyushoIkkatsuSheet(isCreateSeikyuSheet);
		seisanshoExcelSetDto.setOutPutSeikyushoBunkatsuSheet(isCreateSeikyuBunkatsuSheet);
		seisanshoExcelSetDto.setOutPutJippiMesaiSheet(isCreateJippiMesaiSheet);
		seisanshoExcelSetDto.setOutPutShiharaiKeikakuSheet(isCreateShiharaiKeikakuSheet);
		seisanshoExcelSetDto.setOutPutTimeChargeSheet(isCreateTimeChargeSheet);

		// 出力Dtoをbuilderに設定
		en0009ExcelBuilder.setSeisanshoExcelSetDto(seisanshoExcelSetDto);

		try {

			// ファイル名を変更
			en0009ExcelBuilder.setFileName(fileName);

			// Excelファイルの出力処理
			en0009ExcelBuilder.makeExcelFile(response);

		} catch (Exception ex) {
			// 精算書（請求書）の出力失敗
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName, ex);

		}

	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 送付書データの作成（シート1）
	 *
	 * @param seisanshoEditForm
	 * @param seisanKubun
	 * @param seisanZeroFlg
	 * @return
	 * @throws AppException
	 */
	private ExcelSofushoDto createSofushoData(SeisanshoEditForm seisanshoEditForm, String seisanKubun, boolean seisanZeroFlg)
			throws AppException {

		// 送付書のデータ
		ExcelSofushoDto sofushoData = new ExcelSofushoDto();

		// 精算対象の顧客を取得
		Long seisanCustomerId = seisanshoEditForm.getSeisanCustomerId();
		TPersonEntity personEntity = tPersonDao.selectById(seisanCustomerId);

		// 精算の場合作成フラグをtrueにセット
		if (SeisanStatus.SEISAN.equalsByCode(seisanshoEditForm.getSeisanshoCreateDto().getSeisanStatus())) {
			// 精算の場合
			sofushoData.setCreateFlg(true);

		} else {
			// 仮精算の場合
			sofushoData.setCreateFlg(false);

		}

		// 顧客情報の設定
		CustomerId customerId = CustomerId.of(seisanCustomerId);
		sofushoData.setCustomerId(customerId);
		sofushoData.setCustomerName(PersonName.fromEntity(personEntity).getName());

		// 案件IDの設定
		Long seisanAnkenId = seisanshoEditForm.getSeisanAnkenId();
		AnkenId ankenId = AnkenId.of(seisanAnkenId);
		sofushoData.setAnkenId(ankenId);

		// 案件情報の設定
		TAnkenEntity ankenEntity = tAnkenDao.selectById(seisanAnkenId);
		sofushoData.setAnkenBunya(commonBunyaService.getBunya(ankenEntity.getBunyaId()).getVal());
		sofushoData.setAnkenName(ankenEntity.getAnkenName());

		// 帳票に出力する弁護士と事務を取得
		List<AnkenTantoAccountDto> dispAnkenTanto = commonChohyoService.dispAnkenTantoBengoshiMainJimu(seisanAnkenId);
		sofushoData.setAnkenTanto(dispAnkenTanto);

		// 事務所情報の取得
		MTenantEntity tenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		sofushoData.setTenantZipCode(tenantEntity.getTenantZipCd());
		sofushoData.setTenantAddress1(tenantEntity.getTenantAddress1());
		sofushoData.setTenantAddress2(tenantEntity.getTenantAddress2());
		sofushoData.setTenantName(tenantEntity.getTenantName());
		sofushoData.setTenantTelNo(tenantEntity.getTenantTelNo());
		sofushoData.setTenantFaxNo(tenantEntity.getTenantFaxNo());

		// 出力する顧客情報
		if (seisanZeroFlg) {
			// 0円精算の場合
			// 宛先名を取得
			List<String> nameList = commonChohyoService.getAtesakiNameList(seisanCustomerId, null);
			if (LoiozCollectionUtils.isNotEmpty(nameList)) {
				// 送付情報を顧客情報から設定
				sofushoData.setName1(nameList.get(0));
				if (nameList.size() > 1) {
					// 宛先名が2つ場合（法人かつ代表者担当者が存在する場合）
					sofushoData.setName2(nameList.get(1));
				}
			}
			// 顧客情報を設定
			sofushoData.setId(seisanCustomerId.toString());
			sofushoData.setAddress1(personEntity.getAddress1());
			sofushoData.setAddress2(personEntity.getAddress2());
			sofushoData.setZipCode(personEntity.getZipCode());

		} else {
			// 0円精算以外の場合

			// 精算書作成画面データ
			SeisanshoCreateDto seisanshoCreateDto = seisanshoEditForm.getSeisanshoCreateDto();

			// 初期化
			Long kanyoshaSeq = null;
			String address1 = null;
			String address2 = null;
			String zipCode = null;

			// 事務所側の口座情報の設定
			TGinkoKozaEntity jimushoKozaEntity = new TGinkoKozaEntity();

			// 請求の場合は支払者情報を元にデータを取得と精算の場合は出金情報を元にデータを取得
			if (SeisanKirokuKubun.SEIKYU.equalsByCode(seisanKubun)) {
				// 請求の場合

				// 関与者が請求先の場合があるため、データ取得
				kanyoshaSeq = seisanshoCreateDto.getShiharaiKanyoshaSeq();

				// 請求口座 情報を元にデータを取得
				jimushoKozaEntity = tGinkoKozaDao.selectByGinkoAccountSeq(seisanshoCreateDto.getSeikyuKozaSeq());

			} else if (SeisanKirokuKubun.SEISAN.equalsByCode(seisanKubun)) {
				// 精算の場合

				// 関与者に精算するの場合があるため、データ取得
				kanyoshaSeq = seisanshoCreateDto.getHenkinKanyoshaSeq();

				// 出金口座 情報を元にデータを取得
				jimushoKozaEntity = tGinkoKozaDao.selectByGinkoAccountSeq(seisanshoCreateDto.getHenkinKozaSeq());

			} else {
				// 想定しないケース
				throw new RuntimeException();
			}
			// ■請求書、支払い計画で使用する事務所の銀行口座情報を設定
			sofushoData.setJimushoKozaGinkoName(jimushoKozaEntity.getGinkoName());
			sofushoData.setJimushoKozaName(jimushoKozaEntity.getKozaName());
			sofushoData.setJimushokozaNo(jimushoKozaEntity.getKozaNo());
			sofushoData.setJimushoKozashitenName(jimushoKozaEntity.getShitenName());
			sofushoData.setJimushoKozashitenNo(jimushoKozaEntity.getShitenNo());
			sofushoData.setJimushoKozaType(jimushoKozaEntity.getKozaType());

			// ■宛先情報を取得
			Long searchCustomerId = null;
			if (kanyoshaSeq == null) {
				searchCustomerId = seisanCustomerId;
			}
			// 宛先名を取得
			List<String> nameList = commonChohyoService.getAtesakiNameList(searchCustomerId, kanyoshaSeq);
			if (LoiozCollectionUtils.isNotEmpty(nameList)) {
				// 送付情報を顧客情報から設定
				sofushoData.setName1(nameList.get(0));
				if (nameList.size() > 1) {
					// 宛先名が2つ場合（法人かつ代表者担当者が存在する場合）
					sofushoData.setName2(nameList.get(1));
				}
			}

			// 宛先住所の取得
			Map<String, String> atesakiMap = commonChohyoService.getAtesakiMap(searchCustomerId, kanyoshaSeq);
			address1 = atesakiMap.get(CommonChohyoService.TXT_ADDRESS1_KEY);
			address2 = atesakiMap.get(CommonChohyoService.TXT_ADDRESS2_KEY);
			zipCode = atesakiMap.get(CommonChohyoService.TXT_ZIPCODE_KEY);

			// 送付書の宛先情報を設定
			sofushoData.setId(StringUtils.null2blank(seisanCustomerId));
			sofushoData.setAddress1(StringUtils.null2blank(address1));
			sofushoData.setAddress2(StringUtils.null2blank(address2));
			sofushoData.setZipCode(StringUtils.null2blank(zipCode));

		}
		return sofushoData;
	}

	/**
	 * 精算書シート出力用データの取得
	 *
	 * @param createForm
	 * @param customerId
	 * @return ExcelSeisanshoDto seisanshoData
	 */
	private ExcelSeisanshoDto createSeisanshoData(SeisanshoEditForm createForm, Long customerId) {

		ExcelSeisanshoDto seisanshoData = new ExcelSeisanshoDto();

		// DBからデータの取得
		MTenantEntity tenantInfo = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		List<TKaikeiKirokuEntity> kaikeiList = tKaikeiKirokuDao.selectBySeqList(createForm.getKaikeiSeqList());

		// 入金額計の計算
		BigDecimal nyukinTotal = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.NYUKIN_TOTAL.getCd()), tenantInfo, kaikeiList);
		seisanshoData.setNyukinTotal(nyukinTotal);
		seisanshoData.setDispNyukinTotal(commonKaikeiService.toDispAmountLabel(nyukinTotal));

		// 出金額計の計算(報酬以外)
		BigDecimal shukkinTotal = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.SHUKKIN_TOTAL_EXCEPT_HOSHU.getCd()), tenantInfo, kaikeiList);
		seisanshoData.setShukkinTotal(shukkinTotal);
		seisanshoData.setDispShukkinTotal(commonKaikeiService.toDispAmountLabel(shukkinTotal));

		// 消費税計の計算
		BigDecimal taxTotal = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.TAX_TOTAL.getCd()), tenantInfo, kaikeiList);
		BigDecimal taxTotalEight = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.TAX_TOTAL_EIGHT.getCd()), tenantInfo, kaikeiList);
		BigDecimal taxTotalTen = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.TAX_TOTAL_TEN.getCd()), tenantInfo, kaikeiList);
		seisanshoData.setTaxTotal(taxTotal);
		seisanshoData.setTaxTotalEight(taxTotalEight);
		seisanshoData.setTaxTotalTen(taxTotalTen);
		seisanshoData.setDispTaxTotal(commonKaikeiService.toDispAmountLabel(taxTotal));
		seisanshoData.setDispTaxTotalEight(commonKaikeiService.toDispAmountLabel(taxTotalEight));
		seisanshoData.setDispTaxTotalTen(commonKaikeiService.toDispAmountLabel(taxTotalTen));

		// 弁護士報酬ごとの情報を設定
		List<ExcelLawyerHoshuDto> hoshuMeisaiDtoList = this.convert2ExcelLawyerHoshuList(kaikeiList);
		seisanshoData.setHoshuMeisaiDtoList(hoshuMeisaiDtoList);

		// 報酬計の計算
		List<BigDecimal> hoshuList = hoshuMeisaiDtoList.stream().map(ExcelLawyerHoshuDto::getTotalKingaku).collect(Collectors.toList());
		BigDecimal hoshuTotal = commonKaikeiService.calcTotal(hoshuList);

		// 源泉徴収合計の計算
		BigDecimal gensenTotal = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.GENSEN_TOTAL.getCd()), tenantInfo, kaikeiList);
		seisanshoData.setGensenTotal(gensenTotal);
		seisanshoData.setDispGensenTotal(commonKaikeiService.toDispAmountLabel(gensenTotal));

		// 小計の計算
		BigDecimal shokei = hoshuTotal.add(taxTotal).subtract(gensenTotal);
		seisanshoData.setShokei(shokei);
		seisanshoData.setDispshokei(commonKaikeiService.toDispAmountLabel(shokei));

		// 差し引き金額
		BigDecimal sashihikiTotal = nyukinTotal.subtract(shokei).subtract(shukkinTotal);
		seisanshoData.setSaishihikiTotal(sashihikiTotal);
		seisanshoData.setDispSashihikiTotal(commonKaikeiService.toDispAmountLabel(sashihikiTotal));

		// 返金額を設定
		seisanshoData.setHenkinGaku(sashihikiTotal);
		seisanshoData.setDispHenkinGaku(commonKaikeiService.toDispAmountLabel(sashihikiTotal));

		// 返金口座の設定
		String ginkoName = null;
		String kozaName = null;
		String kozaNo = null;
		String kozaType = null;
		String shitenName = null;
		String shitenNo = null;

		// 返金先タイプによって、口座情報を取得する
		String henkinSakiType = createForm.getSeisanshoCreateDto().getHenkinSakiType();
		if (TargetType.CUSTOMER.equalsByCode(henkinSakiType)) {
			// 0：顧客から選択
			TPersonEntity personEntity = tPersonDao.selectById(createForm.getSeisanshoCreateDto().getHenkinCustomerId());
			if (personEntity != null) {
				// 顧客の精算口座
				ginkoName = personEntity.getGinkoName();
				kozaName = personEntity.getKozaName();
				kozaNo = personEntity.getKozaNo();
				kozaType = personEntity.getKozaType();
				shitenName = personEntity.getShitenName();
				shitenNo = personEntity.getShitenNo();
			}

		} else if (TargetType.KANYOSHA.equalsByCode(henkinSakiType)) {
			// 関与者から選択
			KanyoshaBean kanyoshaBean = tKanyoshaDao.selectNyushukkinKanyoshaBeanByKanyoshaSeq(createForm.getSeisanshoCreateDto().getHenkinKanyoshaSeq());

			if (kanyoshaBean != null) {

				// 口座情報
				ginkoName = kanyoshaBean.getGinkoName();
				kozaName = kanyoshaBean.getKozaName();
				kozaNo = kanyoshaBean.getKozaNo();
				kozaType = kanyoshaBean.getKozaType();
				shitenName = kanyoshaBean.getShitenName();
				shitenNo = kanyoshaBean.getShitenNo();
			}

		} else {
			// 未選択はすべて空文字とする
		}
		seisanshoData.setGinkoName(StringUtils.null2blank(ginkoName));
		seisanshoData.setKozaName(StringUtils.null2blank(kozaName));
		seisanshoData.setKozaNo(StringUtils.null2blank(kozaNo));
		seisanshoData.setKozaType(StringUtils.null2blank(kozaType));
		seisanshoData.setShitenName(StringUtils.null2blank(shitenName));
		seisanshoData.setShitenNo(StringUtils.null2blank(shitenNo));

		return seisanshoData;
	}

	/**
	 * 精算時、出力用請求書データの作成
	 *
	 * @param createForm
	 * @return ExcelSeikyushoDto seikyushoData
	 */
	private ExcelSeikyushoDto createSeikyushoData(SeisanshoEditForm createForm) {

		ExcelSeikyushoDto seikyushoData = new ExcelSeikyushoDto();

		// DBからデータの取得
		MTenantEntity tenantInfo = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		List<TKaikeiKirokuEntity> kaikeiList = tKaikeiKirokuDao.selectBySeqList(createForm.getKaikeiSeqList());

		// 入金額計の計算
		BigDecimal nyukinTotal = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.NYUKIN_TOTAL.getCd()), tenantInfo, kaikeiList);
		seikyushoData.setNyukinTotal(nyukinTotal);
		seikyushoData.setDispNyukinTotal(commonKaikeiService.toDispAmountLabel(nyukinTotal));

		// 出金額計の計算(報酬以外)
		BigDecimal shukkinTotal = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.SHUKKIN_TOTAL_EXCEPT_HOSHU.getCd()), tenantInfo, kaikeiList);
		seikyushoData.setShukkinTotal(shukkinTotal);
		seikyushoData.setDispShukkinTotal(commonKaikeiService.toDispAmountLabel(shukkinTotal));

		// 消費税計の計算
		BigDecimal taxTotal = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.TAX_TOTAL.getCd()), tenantInfo, kaikeiList);
		BigDecimal taxTotalEight = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.TAX_TOTAL_EIGHT.getCd()), tenantInfo, kaikeiList);
		BigDecimal taxTotalTen = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.TAX_TOTAL_TEN.getCd()), tenantInfo, kaikeiList);

		seikyushoData.setTaxTotal(taxTotal);
		seikyushoData.setTaxTotalEight(taxTotalEight);
		seikyushoData.setTaxTotalTen(taxTotalTen);
		seikyushoData.setDispTaxTotal(commonKaikeiService.toDispAmountLabel(taxTotal));
		seikyushoData.setDispTaxTotalEight(commonKaikeiService.toDispAmountLabel(taxTotalEight));
		seikyushoData.setDispTaxTotalTen(commonKaikeiService.toDispAmountLabel(taxTotalTen));

		// 弁護士報酬ごとの情報を設定
		List<ExcelLawyerHoshuDto> hoshuMeisaiDtoList = this.convert2ExcelLawyerHoshuList(kaikeiList);
		seikyushoData.setHoshuMeisaiDtoList(hoshuMeisaiDtoList);

		// 報酬計の計算
		List<BigDecimal> hoshuList = hoshuMeisaiDtoList.stream().map(ExcelLawyerHoshuDto::getTotalKingaku).collect(Collectors.toList());
		BigDecimal hoshuTotal = commonKaikeiService.calcTotal(hoshuList);

		// 源泉徴収合計の計算
		BigDecimal gensenTotal = calcTotalByKaikeiKirokuList(Integer.parseInt(CalType.GENSEN_TOTAL.getCd()), tenantInfo, kaikeiList);
		seikyushoData.setGensenTotal(gensenTotal);
		seikyushoData.setDispGensenTotal(commonKaikeiService.toDispAmountLabel(gensenTotal));

		// 小計の計算
		BigDecimal shokei = hoshuTotal.add(taxTotal).subtract(gensenTotal);
		seikyushoData.setShokei(shokei);
		seikyushoData.setDispshokei(commonKaikeiService.toDispAmountLabel(shokei));

		// 差し引き金額
		BigDecimal sashihikiTotal = nyukinTotal.subtract(shokei).subtract(shukkinTotal);
		seikyushoData.setSaishihikiTotal(sashihikiTotal);
		seikyushoData.setDispSashihikiTotal(commonKaikeiService.toDispAmountLabel(sashihikiTotal));

		// 請求金額
		BigDecimal seikyuTotal = sashihikiTotal.multiply(new BigDecimal(-1));
		seikyushoData.setSeikyuGaku(seikyuTotal);
		seikyushoData.setDispSeikyuGaku(commonKaikeiService.toDispAmountLabel(seikyuTotal));

		seikyushoData.setSeikyuType(createForm.getSeisanshoCreateDto().getSeikyuType());

		return seikyushoData;

	}

	/**
	 * 仮精算時、支払い計画書データの作成
	 *
	 * @param createForm
	 * @param seikyuGaku
	 * @return ExcelShiharaiKeikakuDto shiharaiKeikakuData
	 * @throws AppException
	 */
	private ExcelShiharaiKeikakuDto createShiharaiKeikakuData(SeisanshoEditForm createForm, BigDecimal seikyuGaku) throws AppException {

		ExcelShiharaiKeikakuDto shiharaiKeikakuData = new ExcelShiharaiKeikakuDto();
		shiharaiKeikakuData.setCreateFlg(true);
		List<ExcelShiharaiKeikakuListDto> shiharaiYoteiList = new ArrayList<ExcelShiharaiKeikakuListDto>();
		// *********************************************
		// 請求の場合
		// *********************************************
		String seikyuType = createForm.getSeisanshoCreateDto().getSeikyuType();
		if (SeikyuType.IKKATSU.equalsByCode(seikyuType)) {
			// -----------------------------------------
			// 一括支払い時
			// -----------------------------------------
			ExcelShiharaiKeikakuListDto dto = new ExcelShiharaiKeikakuListDto();
			dto.setNyushukkinYoteiSeq(Long.parseLong("1"));
			dto.setNyushukkinYoteiDate(
					DateUtils.parseToLocalDate(createForm.getSeisanshoCreateDto().getPaymentYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			dto.setKingaku(seikyuGaku);
			dto.setDispKingaku(commonKaikeiService.toDispAmountLabel(seikyuGaku));
			dto.setZankin(BigDecimal.ZERO);
			dto.setDispZankin("0");
			shiharaiYoteiList.add(dto);

		} else if (SeikyuType.BUNKATSU.equalsByCode(seikyuType)) {
			// -----------------------------------------
			// 分割支払い時
			// -----------------------------------------

			// 入出金予定情報を生成します。
			shiharaiYoteiList = this.createNyushukkinYoteiListForBunkatsu(createForm, seikyuGaku);

		}
		shiharaiKeikakuData.setShiharaiYoteiList(shiharaiYoteiList);
		return shiharaiKeikakuData;

	}

	/**
	 * 精算時、支払い計画書データの取得、作成
	 *
	 * @param createForm
	 * @param seikyuGaku
	 * @return
	 * @throws AppException
	 */
	private ExcelShiharaiKeikakuDto getShiharaiKeikakuData(SeisanshoEditForm createForm, BigDecimal seikyuGaku) throws AppException {

		// 支払い計画データを生成
		ExcelShiharaiKeikakuDto shiharaiKeikakuData = new ExcelShiharaiKeikakuDto();
		shiharaiKeikakuData.setCreateFlg(true);

		// 入出金予定を取得
		List<TNyushukkinYoteiEntity> nyushukkinYoteiList = tNyushukkinYoteiDao.selectBySeisanSeq(createForm.getSeisanshoCreateDto().getSeisanSeq());

		List<ExcelShiharaiKeikakuListDto> shiharaiYoteiList = new ArrayList<ExcelShiharaiKeikakuListDto>();
		BigDecimal seikyuZanbun = seikyuGaku;

		for (TNyushukkinYoteiEntity entity : nyushukkinYoteiList) {
			ExcelShiharaiKeikakuListDto dto = new ExcelShiharaiKeikakuListDto();
			dto.setNyushukkinYoteiSeq(entity.getNyushukkinYoteiSeq());
			dto.setNyushukkinYoteiDate(entity.getNyushukkinYoteiDate());
			dto.setKingaku(entity.getNyushukkinYoteiGaku());
			dto.setDispKingaku(commonKaikeiService.toDispAmountLabel(entity.getNyushukkinYoteiGaku()));
			seikyuZanbun = seikyuZanbun.subtract(entity.getNyushukkinYoteiGaku());
			dto.setDispZankin(commonKaikeiService.toDispAmountLabel(seikyuZanbun));
			shiharaiYoteiList.add(dto);
		}
		shiharaiKeikakuData.setShiharaiYoteiList(shiharaiYoteiList);
		return shiharaiKeikakuData;

	}

	/**
	 * 仮精算時、出力用実費明細データの作成
	 *
	 * @param createForm
	 * @return ExcelJippiMeisaiDto jippiMeisaiData
	 */
	private ExcelJippiMeisaiDto createKariJippiMeisaiData(SeisanshoEditForm createForm) {

		ExcelJippiMeisaiDto jippiMeisaiData = new ExcelJippiMeisaiDto();

		// DBからデータの取得
		MTenantEntity tenantInfo = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		List<ExcelJippiMeisaiShukkinListBean> jippiMeisaiShukkinListBean = tSeisanshoDao
				.selectExcelJippiMeisaiShukkinList(createForm.getKaikeiSeqList());
		// 出金項目の一覧を設定
		List<ExcelJippiMeisaiShukkinListDto> shukkinList = convertExcelJippiMeisaiShukkinListDto(jippiMeisaiShukkinListBean, tenantInfo);
		jippiMeisaiData.setShukkinList(shukkinList);

		// 出金額の計算
		List<BigDecimal> shukkinListForTotal = jippiMeisaiData.getShukkinList().stream()
				.map(ExcelJippiMeisaiShukkinListDto::getShukkinGaku)
				.collect(Collectors.toList());
		BigDecimal shukkinTotal = commonKaikeiService.calcTotal(shukkinListForTotal);
		jippiMeisaiData.setShukkinTotal(shukkinTotal);
		jippiMeisaiData.setDispShukkinTotal(commonKaikeiService.toDispAmountLabel(shukkinTotal));

		// 消費税の計算
		List<BigDecimal> taxList = shukkinList.stream()
				.map(ExcelJippiMeisaiShukkinListDto::getTax)
				.collect(Collectors.toList());
		BigDecimal taxTotal = commonKaikeiService.calcTotal(taxList);
		jippiMeisaiData.setTaxTotal(taxTotal);
		jippiMeisaiData.setDispTaxTotal(commonKaikeiService.toDispAmountLabel(taxTotal));

		return jippiMeisaiData;
	}

	/**
	 * タイムチャージデータを取得
	 *
	 * @param createForm
	 * @return ExcelTimeChargeDto timeChargeData
	 */
	public ExcelTimeChargeDto createKariTimeChargeData(SeisanshoEditForm createForm) {
		
		// 会計記録を取得
		List<TKaikeiKirokuEntity> kaikeiList = tKaikeiKirokuDao.selectBySeqListOrderByHasseiDate(createForm.getKaikeiSeqList(), true);
		
		// タイムチャージデータを取得
		ExcelTimeChargeDto timeChargeData = commonChohyoService.getTimeChargeData(kaikeiList, createForm.getSeisanCustomerId());
		return timeChargeData;
	}

	/**
	 * 取得した会計一覧から各種合計金額を算出する。
	 *
	 * @param type
	 * @param tenantInfo
	 * @param kaikeiList
	 * @return BigDecimal 一覧から計算した金額
	 */
	private BigDecimal calcTotalByKaikeiKirokuList(int type, MTenantEntity tenantInfo, List<TKaikeiKirokuEntity> kaikeiList) {

		// 初期化
		BigDecimal total = new BigDecimal(0);

		switch (type) {
		// 入金額計の計算
		case 1:
			List<BigDecimal> nyukinGakuList = kaikeiList.stream()
					.filter(entity -> {
						return NyushukkinType.NYUKIN.equalsByCode(entity.getNyushukkinType());
					})
					.map(TKaikeiKirokuEntity::getNyukinGaku)
					.collect(Collectors.toList());
			total = commonKaikeiService.calcTotal(nyukinGakuList);
			break;

		// 出金額計の計算(報酬を除く税込み)
		case 2:
			List<BigDecimal> shukkinGakuList = kaikeiList.stream()
					.filter(entity -> {
						return NyushukkinType.SHUKKIN.equalsByCode(entity.getNyushukkinType()) && StringUtils.isEmpty(entity.getHoshuKomokuId());
					})
					.map(TKaikeiKirokuEntity::getShukkinGaku)
					.collect(Collectors.toList());
			total = commonKaikeiService.calcTotal(shukkinGakuList);
			break;

		// 出金額計の計算(すべて ※報酬を含む)
		case 3:
			List<BigDecimal> shukkinTotalList = kaikeiList.stream()
					.filter(entity -> {
						return NyushukkinType.SHUKKIN.equalsByCode(entity.getNyushukkinType());
					})
					.map(TKaikeiKirokuEntity::getShukkinGaku)
					.collect(Collectors.toList());
			total = commonKaikeiService.calcTotal(shukkinTotalList);
			break;

		// 消費税計の計算
		case 4:
			List<BigDecimal> taxList = new ArrayList<BigDecimal>();
			kaikeiList.forEach(list -> {
				if (!StringUtils.isEmpty(list.getHoshuKomokuId())) {
					if (NyushukkinType.SHUKKIN.equalsByCode(list.getNyushukkinType())) {
						taxList.add(list.getTaxGaku());
					}
				}
			});
			total = commonKaikeiService.calcTotal(taxList);
			break;

		case 5:
			// 源泉徴収合計
			List<BigDecimal> gensenchoshuList = new ArrayList<BigDecimal>();
			kaikeiList.forEach(list -> {
				if (list.getGensenchoshuFlg() != null && GensenTarget.TAISHOU.getCd().equals(list.getGensenchoshuFlg())) {
					gensenchoshuList.add(list.getGensenchoshuGaku());
				}
			});
			total = commonKaikeiService.calcTotal(gensenchoshuList);
			break;

		// 消費税8%計の計算
		case 6:
			List<BigDecimal> taxEightList = new ArrayList<BigDecimal>();
			kaikeiList.forEach(list -> {
				if (!StringUtils.isEmpty(list.getHoshuKomokuId())) {
					if (NyushukkinType.SHUKKIN
							.equalsByCode(list.getNyushukkinType())
							&& CommonConstant.TaxRate.EIGHT_PERCENT.getCd().equals(list.getTaxRate())) {
						taxEightList.add(list.getTaxGaku());
					}
				}
			});
			total = commonKaikeiService.calcTotal(taxEightList);
			break;

		// 消費税10%計の計算
		case 7:
			List<BigDecimal> taxTenList = new ArrayList<BigDecimal>();
			kaikeiList.forEach(list -> {
				if (!StringUtils.isEmpty(list.getHoshuKomokuId())) {
					if (NyushukkinType.SHUKKIN.equalsByCode(list.getNyushukkinType())
							&& CommonConstant.TaxRate.TEN_PERCENT.getCd().equals(list.getTaxRate())) {
						taxTenList.add(list.getTaxGaku());
					}
				}
			});
			total = commonKaikeiService.calcTotal(taxTenList);
			break;

		// デフォルト
		default:
			break;

		}
		return total;
	}

	/**
	 * TKaikeiKirokuEntityからExcelLawyerHoshuDtoを作成する
	 *
	 * @param kaikeiList
	 * @return
	 */
	private List<ExcelLawyerHoshuDto> convert2ExcelLawyerHoshuList(List<TKaikeiKirokuEntity> kaikeiList) {

		List<ExcelLawyerHoshuDto> dtoList = new ArrayList<ExcelLawyerHoshuDto>();

		// 報酬項目のみ抽出
		List<TKaikeiKirokuEntity> hoshuKomokuList = kaikeiList.stream()
				.filter(entity -> NyushukkinType.SHUKKIN.equalsByCode(entity.getNyushukkinType()) && !StringUtils.isEmpty(entity.getHoshuKomokuId()))
				.collect(Collectors.toList());

		// 報酬項目と税率をキーとする、会計情報のGroupingMapperを作成
		Function<TKaikeiKirokuEntity, Keys<LawyerHoshu, TaxRate>> mapper = e -> {
			LawyerHoshu lawyerHoshu = LawyerHoshu.of(e.getHoshuKomokuId());
			TaxRate taxRate = TaxRate.of(e.getTaxRate());
			return new Keys<LawyerHoshu, TaxRate>(lawyerHoshu, taxRate);
		};

		// 報酬項目と税率をキーとする、会計情報のMapを作成
		Map<Keys<LawyerHoshu, TaxRate>, List<TKaikeiKirokuEntity>> twoKeysMap = hoshuKomokuList.stream().collect(Collectors.groupingBy(mapper));

		// 表示用データリストを作成
		for (Keys<LawyerHoshu, TaxRate> key : twoKeysMap.keySet()) {
			List<TKaikeiKirokuEntity> value = twoKeysMap.get(key);

			// 各報酬、税率ごとの合計額を算出
			BigDecimal totalKingaku = value.stream().map(e -> {
				BigDecimal hoshu = e.getShukkinGaku();
				BigDecimal tax = e.getTaxGaku();

				if (TaxFlg.INTERNAL_TAX.equalsByCode(e.getTaxFlg())) {
					// 内税の場合、出金額から税額を引く
					hoshu = hoshu.subtract(tax);
				}
				return hoshu;
			}).reduce((r, e) -> r.add(e)).orElse(new BigDecimal(0));

			ExcelLawyerHoshuDto excelLawyerHoshuDto = new ExcelLawyerHoshuDto();
			excelLawyerHoshuDto.setLawyerHoshu(key.getKey1());
			excelLawyerHoshuDto.setTaxRate(key.getKey2().getCd());
			excelLawyerHoshuDto.setTaxRateDisp(commonKaikeiService.changeTaxRateDisp(key.getKey2().getCd()));
			excelLawyerHoshuDto.setTotalKingaku(totalKingaku);
			dtoList.add(excelLawyerHoshuDto);
		}

		// 1.報酬,2.税率（数値）でソートする
		Collections.sort(dtoList, Comparator.comparing(ExcelLawyerHoshuDto::getLawyerHoshu).thenComparing(ExcelLawyerHoshuDto::getTaxRateDisp));
		return dtoList;
	}

	/**
	 * タイムチャージデータをDtoに変換
	 *
	 * @param kaikeiList
	 * @return List<ExcelTimeChargeListDto>
	 */
	public List<ExcelTimeChargeListDto> convertExcelTimeChargeList(List<TKaikeiKirokuEntity> kaikeiList) {

		List<ExcelTimeChargeListDto> list = new ArrayList<>();
		kaikeiList.forEach(entity -> {
			// タイムチャージ項目のみ
			if (LawyerHoshu.TIME_CHARGE.equalsByCode(entity.getHoshuKomokuId())) {
				ExcelTimeChargeListDto dto = new ExcelTimeChargeListDto();
				// 日付
				if (TimeChargeTimeShitei.START_END_TIME.equalsByCode(entity.getTimeChargeTimeShitei())) {
					// 開始・終了時間を指定している場合
					// 発生日には開始日を設定する
					dto.setHasseiDate(DateUtils.convertLocalDate(entity.getTimeChargeStartTime()));
				} else {
					dto.setHasseiDate(entity.getHasseiDate());
				}
				// 活動
				dto.setKatsudo(entity.getTekiyo());
				// タイムチャージ開始日時
				dto.setTimeChargeStartDateTime(entity.getTimeChargeStartTime());
				// タイムチャージ開始時間
				dto.setTimeChargeStartTime(
						DateUtils.parseToString(entity.getTimeChargeStartTime(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
				// タイムチャージ終了時間
				dto.setTimeChargeEndTime(DateUtils.parseToString(entity.getTimeChargeEndTime(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
				// 時間(分)
				dto.setTime(entity.getTimeChargeTime());
				// 単価
				dto.setTimeChargeTanka(entity.getTimeChargeTanka());
				dto.setDispTimeChargeTanka(commonKaikeiService.toDispAmountLabel(entity.getTimeChargeTanka()));
				// 小計
				if (!TaxFlg.FOREIGN_TAX.equalsByCode(entity.getTaxFlg())) {
					// タイムチャージが税抜の場合は想定しない。
					logger.error("タイムチャージが税抜以外で登録されています。TKaikeiKirokuEntity.kaikeiKirokuSeq=『 " + entity.getKaikeiKirokuSeq() + " 』");
					throw new RuntimeException("想定外のデータパターン");
				}
				dto.setShukkinGaku(entity.getShukkinGaku());
				dto.setDispShukkinGaku(commonKaikeiService.toDispAmountLabel(entity.getShukkinGaku()));

				list.add(dto);
			}
		});
		
		if (list.isEmpty()) {
			return list;
		}
		
		// データを発生日、開始日時でソート 
		List<ExcelTimeChargeListDto> sotedList = list.stream()
			.sorted(Comparator.comparing(ExcelTimeChargeListDto::getHasseiDate, Comparator.nullsFirst(LocalDate::compareTo))
					.thenComparing(Comparator.comparing(ExcelTimeChargeListDto::getTimeChargeStartDateTime, Comparator.nullsFirst(LocalDateTime::compareTo))))
			.collect(Collectors.toList());
		
		return sotedList;
	}

	/**
	 * 入出金予定情報のリストを生成
	 *
	 * @param createForm
	 * @param seikyuGaku
	 * @return List<ExcelShiharaiKeikakuListDto>
	 * @throws AppException
	 */
	private List<ExcelShiharaiKeikakuListDto> createNyushukkinYoteiListForBunkatsu(SeisanshoEditForm createForm, BigDecimal seikyuGaku)
			throws AppException {

		List<ExcelShiharaiKeikakuListDto> nyushukkinYoteiList = new ArrayList<ExcelShiharaiKeikakuListDto>();

		// 入力した情報を取得します。
		SeisanshoCreateDto inputCreateDto = createForm.getSeisanshoCreateDto();

		// --------------------------------------------
		// String -> BigDecimal
		// --------------------------------------------
		DecimalFormat decimalFormat = new DecimalFormat();
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

		// --------------------------------------------
		// 精算額 ÷ 月々の支払額 = 分割回数
		// --------------------------------------------
		BigDecimal count = commonKaikeiService.calcBunkatsuKaisu(seikyuGaku, monthShiharaiGaku);
		int bunkatsuCount = count.intValue();

		// --------------------------------------------
		// 端数
		// --------------------------------------------
		BigDecimal totalNyukinGaku = monthShiharaiGaku.multiply(count);
		BigDecimal hasu = seikyuGaku.subtract(totalNyukinGaku);

		// --------------------------------------------
		// 入出金予定情報を作成します。
		// --------------------------------------------
		// 支払開始日
		String shiharaiStartYear = inputCreateDto.getShiharaiStartYear();
		String shiharaiStartMonth = inputCreateDto.getShiharaiStartMonth();
		String monthShiharaiDate = CommonConstant.BLANK;
		if (Optional.ofNullable(inputCreateDto.getLastDay()).orElse(SeisanShiharaiMonthDay.LASTDAY.getCd())
				.equals(SeisanShiharaiMonthDay.DESIGNATEDDAY.getCd())) {
			monthShiharaiDate = inputCreateDto.getPaymentDate();
		} else {
			monthShiharaiDate = DateUtils.END_OF_MONTH;
		}
		String newShiharaiStartMonth = CommonConstant.BLANK;
		String newMonthShiharaiDate = CommonConstant.BLANK;

		int monthNum = Integer.parseInt(shiharaiStartMonth);
		int dateNum = Integer.parseInt(monthShiharaiDate);

		if (monthNum < 10) {
			newShiharaiStartMonth = "0" + shiharaiStartMonth;
		} else {
			newShiharaiStartMonth = shiharaiStartMonth;
		}

		if (dateNum < 10) {
			newMonthShiharaiDate = "0" + monthShiharaiDate;
		} else {
			newMonthShiharaiDate = monthShiharaiDate;
		}

		// 開始年月・毎月の支払日から、支払開始日を生成します。
		String shiharaiDate = shiharaiStartYear + "/" + newShiharaiStartMonth + "/" + newMonthShiharaiDate;
		LocalDate shiharaiStartDate = DateUtils.parseToLocalDate(shiharaiDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED);

		BigDecimal zankin = seikyuGaku;
		for (int i = 0; i < bunkatsuCount; i++) {
			ExcelShiharaiKeikakuListDto dto = new ExcelShiharaiKeikakuListDto();
			LocalDate nyushukkinYoteiDate = null;
			BigDecimal nyushukkinYoteiGaku = new BigDecimal(0);

			// 入出金予定日
			if (i == 0) {
				nyushukkinYoteiDate = shiharaiStartDate;
			} else {
				if (monthShiharaiDate.equals(DateUtils.END_OF_MONTH)) {
					nyushukkinYoteiDate = shiharaiStartDate.plusMonths(i).with(TemporalAdjusters.lastDayOfMonth());
				} else {
					nyushukkinYoteiDate = shiharaiStartDate.plusMonths(i);
				}
			}

			if (hasu.compareTo(BigDecimal.ZERO) > 0) {
				// 端数がある場合

				if (Hasu.FIRST.getCd().equals(inputCreateDto.getHasu())) {
					// 初回
					if (i == 0) {
						// 初回の入金予定額に端数を足します。
						nyushukkinYoteiGaku = monthShiharaiGaku.add(hasu);
					} else {
						// 初回以外は、月々の支払額を予定額にします。
						nyushukkinYoteiGaku = monthShiharaiGaku;
					}

				} else {
					// 最終
					if (i == bunkatsuCount - 1) {
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
			// 残金の計算
			zankin = zankin.subtract(nyushukkinYoteiGaku);

			// 各情報を設定します。
			dto.setNyushukkinYoteiSeq(Long.parseLong("1"));
			dto.setNyushukkinYoteiDate(nyushukkinYoteiDate);
			dto.setKingaku(nyushukkinYoteiGaku);
			dto.setDispKingaku(commonKaikeiService.toDispAmountLabel(nyushukkinYoteiGaku));
			dto.setZankin(zankin);
			dto.setDispZankin(commonKaikeiService.toDispAmountLabel(zankin));

			// リストに追加します。
			nyushukkinYoteiList.add(dto);
		}

		return nyushukkinYoteiList;
	}

	/**
	 * 実費明細データをDtoに変換
	 *
	 * @param jippiMeisaiShukkinListBean
	 * @param tenantInfo
	 * @return ExcelJippiMeisaiShukkinListDto
	 */
	private List<ExcelJippiMeisaiShukkinListDto> convertExcelJippiMeisaiShukkinListDto(List<ExcelJippiMeisaiShukkinListBean> jippiMeisaiShukkinListBean, MTenantEntity tenantInfo) {

		List<ExcelJippiMeisaiShukkinListDto> list = new ArrayList<>();

		// Dtoに変換
		jippiMeisaiShukkinListBean.forEach(bean -> {
			ExcelJippiMeisaiShukkinListDto dto = new ExcelJippiMeisaiShukkinListDto();
			dto.setHasseiDate(bean.getHasseiDate());

			// ※外税、内税に関わらず、税額の設定は行わない
			dto.setTax(null);
			dto.setDispTax(CommonConstant.BLANK);
			dto.setShukkinGaku(bean.getShukkinGaku());
			dto.setDispShukkinGaku(commonKaikeiService.toDispAmountLabel(bean.getShukkinGaku()));

			dto.setTaxType(DefaultEnum.getEnum(TaxFlg.class, bean.getTaxFlg()).getVal());
			dto.setShukkinKomokuName(bean.getKomokuName());
			dto.setTekiyo(bean.getTekiyo());
			list.add(dto);
		});
		return list;
	}

}
