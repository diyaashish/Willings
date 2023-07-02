package jp.loioz.app.user.kaikeiManagement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0005ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0006ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0007ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0008ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.HoshuMeisaiExcelDto;
import jp.loioz.app.common.excel.dto.NyushukkinMeisaiExcelDto;
import jp.loioz.app.common.excel.dto.ShiharaiKeikakuExcelDto;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.user.kaikeiManagement.form.ExcelHoshuMeisaiData;
import jp.loioz.app.user.kaikeiManagement.form.ExcelNyushukkinMeisaiData;
import jp.loioz.app.user.kaikeiManagement.form.KaikeiListViewForm;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.GensenTarget;
import jp.loioz.common.constant.CommonConstant.LawyerHoshu;
import jp.loioz.common.constant.CommonConstant.TaxFlg;
import jp.loioz.common.constant.CommonConstant.TimeChargeTimeShitei;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dao.TKaikeiKirokuDao;
import jp.loioz.dao.TNyushukkinYoteiDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSeisanKirokuDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.domain.value.SeisanId;
import jp.loioz.dto.ExcelShiharaiKeikakuListDto;
import jp.loioz.dto.ExcelTimeChargeDto;
import jp.loioz.dto.HoshuMeisaiBean;
import jp.loioz.dto.KaikeiKirokuDto;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TGinkoKozaEntity;
import jp.loioz.entity.TKaikeiKirokuEntity;
import jp.loioz.entity.TNyushukkinYoteiEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSeisanKirokuEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class KaikeiDocSevice extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** 会計管理の共通サービス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 帳票の共通サービス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 名簿情報用のDaoクラス **/
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件情報用のDaoクラス **/
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 会計記録のDaoクラス **/
	@Autowired
	private TKaikeiKirokuDao tKaikeiKirokuDao;

	/** 精算Daoクラス */
	@Autowired
	private TSeisanKirokuDao tSeisanKirokuDao;

	/** 入出金予定Daoクラス */
	@Autowired
	private TNyushukkinYoteiDao tNyushukkinYoteiDao;

	/** 銀行口座Daoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// *******************************************************************************
	// 入出金明細
	// *******************************************************************************
	/**
	 * 入出金明細の帳票出力処理
	 *
	 * @param response
	 * @param viewForm
	 */
	public void excelNyushukkinMeisai(HttpServletResponse response, KaikeiListViewForm nyushukkinMeisaiData, TransitionType transitionType)
			throws Exception {

		// ■1.Builderを定義
		En0006ExcelBuilder en0006ExcelBuilder = new En0006ExcelBuilder();
		en0006ExcelBuilder.setConfig(excelConfig);

		// ■2.DTOを定義
		NyushukkinMeisaiExcelDto nyushukkinMeisaiExcelDto = en0006ExcelBuilder.createNewTargetBuilderDto();

		// ■3.DTOに設定するデータを取得と設定
		nyushukkinMeisaiExcelDto.setTransitonType(transitionType);
		nyushukkinMeisaiExcelDto.setExcelNyushukkinMeisaiData(convertToExcelNyushukkinMeisaiData(nyushukkinMeisaiData.getKaikeiKirokuList()));
		nyushukkinMeisaiExcelDto.setTotalNyukingaku(nyushukkinMeisaiData.getKaikeiKirokuNyukinTotal());
		nyushukkinMeisaiExcelDto.setTotalShukkingaku(nyushukkinMeisaiData.getKaikeiKirokuShukkinTotal());
		nyushukkinMeisaiExcelDto.setSashihiki(nyushukkinMeisaiData.getKaikeiKirokuSashiHikiTotal());

		// 遷移元が案件の場合か、顧客の場合かによって変わる
		if (TransitionType.ANKEN.equals(transitionType)) {
			TAnkenEntity ankenEntity = tAnkenDao.selectById(nyushukkinMeisaiData.getTransitionAnkenId());
			nyushukkinMeisaiExcelDto.setAnkenId(AnkenId.of(ankenEntity.getAnkenId()));
			nyushukkinMeisaiExcelDto.setAnkenName(ankenEntity.getAnkenName());
			nyushukkinMeisaiExcelDto.setBunya(commonBunyaService.getBunya(ankenEntity.getBunyaId()));
		} else if (TransitionType.CUSTOMER.equals(transitionType)) {
			TPersonEntity personEntity = tPersonDao.selectById(nyushukkinMeisaiData.getTransitionCustomerId());
			nyushukkinMeisaiExcelDto.setCustomerId(CustomerId.of(personEntity.getCustomerId()));
			nyushukkinMeisaiExcelDto.setCustomerName(PersonName.fromEntity(personEntity).getName());
		} else {
			logger.error("想定外のパターン");
			throw new RuntimeException();
		}

		// Builderクラスに設定
		en0006ExcelBuilder.setNyushukkinMeisaiExcelDto(nyushukkinMeisaiExcelDto);

		try {
			// Excelファイルの出力処理
			en0006ExcelBuilder.makeExcelFile(response);
		} catch (IOException e) {
			logger.error("帳票出力時のエラー", e);
			throw new AppException(MessageEnum.MSG_E00034, e);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new AppException(MessageEnum.MSG_E00034, e);
		}

	}

	/**
	 * DTOをDataに変換 List<KaikeiKirokuDto> -> List<ExcelNyushukkinMeisaiData>
	 *
	 * @param KaikeiKirokuDto
	 * @return
	 */
	private List<ExcelNyushukkinMeisaiData> convertToExcelNyushukkinMeisaiData(List<KaikeiKirokuDto> kaikeiKirokuList) {
		// 変換条件
		Function<KaikeiKirokuDto, ExcelNyushukkinMeisaiData> mapper = dto -> {

			ExcelNyushukkinMeisaiData data = new ExcelNyushukkinMeisaiData();

			data.setHasseiDate(dto.getHasseiDate());

			if (dto.getNyukinGaku() != null) {
				data.setNyukin(dto.getNyukinGaku().toString());
			}

			if (dto.getShukkinGaku() != null) {
				data.setShukkin(dto.getShukkinGaku().toString());
			}

			data.setNyushukkinType(dto.getNyushukkinType());
			data.setCustomerId(CustomerId.of(dto.getCustomerId()));
			data.setCustomerName(dto.getCustomerName());
			data.setAnkenId(AnkenId.of((dto.getAnkenId())));
			data.setAnkenBunya(dto.getBunya());
			data.setAnkenName(dto.getAnkenName());
			data.setTekiyo(dto.getTekiyo());
			data.setSeisanDate(dto.getSeisanDate());
			data.setSeisanId(new SeisanId(dto.getCustomerId(), dto.getSeisanId()));

			return data;
		};

		// 加工してreturn
		return Optional.ofNullable(kaikeiKirokuList).orElse(kaikeiKirokuList = Collections.emptyList()).stream().map(mapper)
				.collect(Collectors.toList());
	}

	/**
	 * タイムチャージ情報取得
	 *
	 * @param customerId
	 * @return
	 */
	public ExcelTimeChargeDto getTimechargeKaikeikiroku(Long customerId, Long ankenId) {

		List<TKaikeiKirokuEntity> kaikeiList = tKaikeiKirokuDao.selectByAnkenIdAndCustomerIdAndHoshuKomoku(ankenId, customerId, LawyerHoshu.TIME_CHARGE.getCd(), true);
		ExcelTimeChargeDto timeChargeData = commonChohyoService.getTimeChargeData(kaikeiList, customerId);
		return timeChargeData;
	}

	/**
	 * タイムチャージExcel出力処理
	 *
	 * @param response
	 * @param createForm
	 * @param viewForm
	 * @throws Exception
	 */
	public void createTimeChargeExcelFile(HttpServletResponse response, Long customerId, Long ankenId) throws Exception {
		// ■1.Builderを定義
		En0008ExcelBuilder en0008ExcelBuilder = new En0008ExcelBuilder();
		en0008ExcelBuilder.setConfig(excelConfig);

		// ■2.DTOを定義
		ExcelTimeChargeDto excelTimeChargeDtoList = this.getTimechargeKaikeikiroku(customerId, ankenId);

		// ■3.DTOに設定するデータを取得と設定
		en0008ExcelBuilder.setTimeChargeData(excelTimeChargeDtoList);

		try {
			// Excelファイルの出力処理
			en0008ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new AppException(MessageEnum.MSG_E00034, e);
		}
	}

	/**
	 * 報酬明細の出力処理
	 *
	 * @param response
	 * @param hoshuMeisaiData
	 * @param transitionType
	 * @throws AppException
	 */
	public void excelHoshuMeisa(HttpServletResponse response, TransitionType transitionType, Long transitionAnkenId, Long transitionCustomerId, boolean seisanCompDispFlg, Long ankenId, Long customerId) throws AppException {

		// ■1.Builderを定義
		En0005ExcelBuilder en0005ExcelBuilder = new En0005ExcelBuilder();
		en0005ExcelBuilder.setConfig(excelConfig);

		// ■2.DTOを定義
		HoshuMeisaiExcelDto hoshuMeisaiExcelDto = en0005ExcelBuilder.createNewTargetBuilderDto();

		// ■3.DTOに設定するデータを取得と設定
		this.setHoshuMeisaiExcelDto(hoshuMeisaiExcelDto, transitionType, transitionAnkenId, transitionCustomerId, seisanCompDispFlg, ankenId, customerId);

		// Builderクラスに設定
		en0005ExcelBuilder.setHoshuMeisaiExcelDto(hoshuMeisaiExcelDto);

		try {
			// Excelファイルの出力処理
			en0005ExcelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			throw new AppException(MessageEnum.MSG_E00034, e);
		}
	}

	/**
	 * 支払い計画帳票出力（支払い計画モーダルから出力）
	 *
	 * @param response
	 * @param seisanSeq
	 * @throws AppException
	 */
	public void excelShiharaiKeikaku(HttpServletResponse response, Long seisanSeq) throws AppException {

		// ***************************************************************
		// ■1.Builderを定義
		// ***************************************************************
		En0007ExcelBuilder en0007ExcelBuilder = new En0007ExcelBuilder();
		en0007ExcelBuilder.setConfig(excelConfig);

		// ***************************************************************
		// ■2.DTOを定義
		// ***************************************************************
		ShiharaiKeikakuExcelDto shiharaiKeikakuExcelDto = en0007ExcelBuilder.createNewTargetBuilderDto();

		// ***************************************************************
		// ■3.DTOに設定するデータを取得と設定
		// ***************************************************************

		// 必要なデータを取得
		TSeisanKirokuEntity tSeisanKirokuEntity = tSeisanKirokuDao.selectBySeisanSeq(seisanSeq);
		Long seikyuCustomerId = tSeisanKirokuEntity.getSeikyuCustomerId();
		Long seikyuKanyoshaSeq = tSeisanKirokuEntity.getSeikyuKanyoshaSeq();
		// 名前表示
		String name = commonChohyoService.getAtesakiName(seikyuCustomerId, seikyuKanyoshaSeq);
		shiharaiKeikakuExcelDto.setName(name);

		// Entity -> Dto
		List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities = tNyushukkinYoteiDao.selectBySeisanSeq(seisanSeq);
		List<ExcelShiharaiKeikakuListDto> shiharaiKeikakuListData = this.convertToShiharaiKeikakuListData(tNyushukkinYoteiEntities,
				tSeisanKirokuEntity.getSeisanGaku());

		// 出力データに設定
		TGinkoKozaEntity tGinkoKozaEntity = tGinkoKozaDao.selectByGinkoAccountSeq(tSeisanKirokuEntity.getSeikyuKozaSeq());
		shiharaiKeikakuExcelDto.setGinkoName(tGinkoKozaEntity.getGinkoName());
		shiharaiKeikakuExcelDto.setShitenName(tGinkoKozaEntity.getShitenName());
		shiharaiKeikakuExcelDto.setShitenNo(tGinkoKozaEntity.getShitenNo());
		shiharaiKeikakuExcelDto.setKozaType(tGinkoKozaEntity.getKozaType());
		shiharaiKeikakuExcelDto.setKozaNo(tGinkoKozaEntity.getKozaNo());
		shiharaiKeikakuExcelDto.setKozaName(tGinkoKozaEntity.getKozaName());
		shiharaiKeikakuExcelDto.setShiharaiYoteiList(shiharaiKeikakuListData);

		// Builderクラスに設定
		en0007ExcelBuilder.setShiharaiKeikakuExcelDto(shiharaiKeikakuExcelDto);

		// ***************************************************************
		// ■4.出力処理
		// ***************************************************************

		try {
			// Excelファイルの出力処理
			en0007ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			throw new AppException(MessageEnum.MSG_E00034, e);

		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * Excel出力用データに加工します
	 *
	 * @param tNyushukkinYoteiEntities 一覧に出力する元データ
	 * @param seikyuGaku 請求額
	 * @return 加工した一覧出力用データ
	 */
	private List<ExcelShiharaiKeikakuListDto> convertToShiharaiKeikakuListData(List<TNyushukkinYoteiEntity> tNyushukkinYoteiEntities,
			BigDecimal seikyuGaku) {

		// 初期化
		List<ExcelShiharaiKeikakuListDto> shiharaiYoteiList = new ArrayList<>();
		BigDecimal zankin = seikyuGaku;

		for (TNyushukkinYoteiEntity entity : tNyushukkinYoteiEntities) {
			ExcelShiharaiKeikakuListDto dto = new ExcelShiharaiKeikakuListDto();
			dto.setNyushukkinYoteiSeq(entity.getNyushukkinYoteiSeq());
			dto.setNyushukkinYoteiDate(entity.getNyushukkinYoteiDate());
			dto.setKingaku(entity.getNyushukkinYoteiGaku());
			dto.setDispKingaku(commonKaikeiService.toDispAmountLabel(entity.getNyushukkinYoteiGaku()));
			zankin = zankin.subtract(entity.getNyushukkinYoteiGaku());
			dto.setDispZankin(commonKaikeiService.toDispAmountLabel(zankin));
			shiharaiYoteiList.add(dto);
		}
		return shiharaiYoteiList;

	}

	/**
	 * 報酬明細ExcelDtoにデータを設定する。
	 * 
	 * @param hoshuMeisaiExcelDto
	 * @param transitionType
	 * @param transitionAnkenId
	 * @param transitionCustomerId
	 * @param seisanCompDispFlg
	 * @param ankenId
	 * @param customerId
	 */
	private void setHoshuMeisaiExcelDto(HoshuMeisaiExcelDto hoshuMeisaiExcelDto, TransitionType transitionType, Long transitionAnkenId, Long transitionCustomerId, boolean seisanCompDispFlg, Long ankenId, Long customerId) {

		// 報酬明細Beanの定義
		List<HoshuMeisaiBean> hoshuMeisaiBeanList = Collections.emptyList();

		// 遷移元が案件か案件によって表示する内容は異なる。
		if (TransitionType.ANKEN == transitionType) {

			hoshuMeisaiExcelDto.setTransitonType(TransitionType.ANKEN);

			// 案件情報を取得・設定
			TAnkenEntity ankenEntity = tAnkenDao.selectById(transitionAnkenId);
			hoshuMeisaiExcelDto.setAnkenId(AnkenId.of(ankenEntity.getAnkenId()));
			hoshuMeisaiExcelDto.setAnkenName(ankenEntity.getAnkenName());

			if (customerId != null || seisanCompDispFlg) {
				// Bean情報の取得(すべて)
				hoshuMeisaiBeanList = tKaikeiKirokuDao.selectHoshuMeisaiByAnkenId(transitionAnkenId, customerId);
			} else {
				// Bean情報の取得(精算が未完了のもの）
				hoshuMeisaiBeanList = tKaikeiKirokuDao.selectNotCompByAnkenId(transitionAnkenId, customerId, AnkenStatus.getCompletedStatusCd());
			}

		} else if (TransitionType.CUSTOMER == transitionType) {

			hoshuMeisaiExcelDto.setTransitonType(TransitionType.CUSTOMER);

			// 顧客情報を取得・設定
			TPersonEntity personEntity = tPersonDao.selectById(transitionCustomerId);
			hoshuMeisaiExcelDto.setCustomerId(CustomerId.of(personEntity.getCustomerId()));
			hoshuMeisaiExcelDto.setCustomerName(PersonName.fromEntity(personEntity).getName());

			if (ankenId != null || seisanCompDispFlg) {
				// Bean情報の取得(すべて)
				hoshuMeisaiBeanList = tKaikeiKirokuDao.selectHoshuMeisaiByCustomerId(transitionCustomerId, ankenId);
			} else {
				// Bean情報の取得(精算が未完了のもの）
				hoshuMeisaiBeanList = tKaikeiKirokuDao.selectNotCompByCustomerId(transitionCustomerId, ankenId, AnkenStatus.getCompletedStatusCd());
			}

		} else {
			// 想定外のパラメータ
			throw new RuntimeException("");
		}

		List<ExcelHoshuMeisaiData> hoshuMeisaiDataList = this.covert2ExcelHoshuMeisaiData(hoshuMeisaiBeanList);
		BigDecimal hoshuGakuTotal = hoshuMeisaiDataList.stream().filter(e -> !BigDecimal.ZERO.equals(e.getHoshuGaku())).map(ExcelHoshuMeisaiData::getHoshuGaku).reduce((accm, value) -> accm.add(value)).orElse(new BigDecimal(0));
		BigDecimal taxGakuTotal = hoshuMeisaiDataList.stream().filter(e -> !BigDecimal.ZERO.equals(e.getTaxGaku())).map(ExcelHoshuMeisaiData::getTaxGaku).reduce((accm, value) -> accm.add(value)).orElse(new BigDecimal(0));
		BigDecimal gensenGakuTotal = hoshuMeisaiDataList.stream().filter(e -> !BigDecimal.ZERO.equals(e.getGensenGaku())).map(ExcelHoshuMeisaiData::getGensenGaku).reduce((accm, value) -> accm.add(value)).orElse(new BigDecimal(0));
		BigDecimal hoshuTotal = hoshuMeisaiDataList.stream().filter(e -> !BigDecimal.ZERO.equals(e.getTotal())).map(ExcelHoshuMeisaiData::getTotal).reduce((accm, value) -> accm.add(value)).orElse(new BigDecimal(0));

		// 報酬明細一覧をセット
		hoshuMeisaiExcelDto.setExcelHoshuMeisaiData(hoshuMeisaiDataList);

		// 報酬明細の合計金額をセット
		hoshuMeisaiExcelDto.setHoshuGakuTotal(hoshuGakuTotal);
		hoshuMeisaiExcelDto.setDispHoshuGakuTotal(hoshuGakuTotal != null ? hoshuGakuTotal.toString() : "0");
		hoshuMeisaiExcelDto.setTaxGakuTotal(taxGakuTotal);
		hoshuMeisaiExcelDto.setDispTaxGakuTotal(taxGakuTotal != null ? taxGakuTotal.toString() : "0");
		hoshuMeisaiExcelDto.setGensenGakuTotal(gensenGakuTotal);
		hoshuMeisaiExcelDto.setDispGensenGakuTotal(gensenGakuTotal != null ? gensenGakuTotal.negate().toString() : "0");// 源泉徴収の表示はマイナス
		hoshuMeisaiExcelDto.setHoshuTotal(hoshuTotal);
		hoshuMeisaiExcelDto.setDispHoshuTotal(hoshuTotal != null ? hoshuTotal.toString() : "0");

	}

	/**
	 * 報酬明細BeanをExcelHoshuMeisaiDataに変換
	 * 
	 * @param hoshuMeisaiBeanList
	 * @return
	 */
	private List<ExcelHoshuMeisaiData> covert2ExcelHoshuMeisaiData(List<HoshuMeisaiBean> hoshuMeisaiBeanList) {

		if (CollectionUtils.isEmpty(hoshuMeisaiBeanList)) {
			return Collections.emptyList();
		}

		// 変換条件
		Function<HoshuMeisaiBean, ExcelHoshuMeisaiData> mapper = bean -> {

			ExcelHoshuMeisaiData excelHoshuMeisaiData = new ExcelHoshuMeisaiData();

			// 顧客の設定
			if (bean.getCustomerId() != null) {
				excelHoshuMeisaiData.setCustomerId(CustomerId.of(bean.getCustomerId()));
				excelHoshuMeisaiData.setCustomerName(bean.getCustomerName());
			}
			// 案件の設定
			if (bean.getAnkenId() != null) {
				excelHoshuMeisaiData.setAnkenId(AnkenId.of(bean.getAnkenId()));
				excelHoshuMeisaiData.setAnkenName(bean.getAnkenName());
			}
			// 精算IDの設定
			if (bean.getSeisanSeq() != null) {
				SeisanId seisanId = new SeisanId(bean.getCustomerId(), bean.getSeisanId());
				excelHoshuMeisaiData.setSeisanId(seisanId.toString());
			} else {
				excelHoshuMeisaiData.setSeisanId("");
			}
			// 精算処理日の設定
			if (bean.getSeisanDate() != null) {
				excelHoshuMeisaiData.setSeisanDate(DateUtils.parseToString(bean.getSeisanDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			} else {
				excelHoshuMeisaiData.setSeisanDate("");
			}

			BigDecimal hoshuGaku = bean.getKingaku() == null ? new BigDecimal(0) : bean.getKingaku();
			BigDecimal taxGaku = bean.getTaxGaku() == null ? new BigDecimal(0) : bean.getTaxGaku();
			BigDecimal gensenGaku = bean.getGensenchoshuGaku() == null ? new BigDecimal(0) : bean.getGensenchoshuGaku();

			// 税込の場合は、金額から税額を減算
			if (TaxFlg.INTERNAL_TAX.equalsByCode(bean.getTaxFlg())) {
				hoshuGaku = hoshuGaku.subtract(taxGaku);
			}

			// 合計を算出(税額考慮した報酬額 - 源泉徴収額)
			BigDecimal total = hoshuGaku.add(taxGaku);

			// 源泉対象の場合のみ、源泉額を減算
			if (GensenTarget.TAISHOU.equalsByCode(bean.getGensenchoshuFlg())) {
				total = total.subtract(gensenGaku);
			}

			// 発生日
			if (LawyerHoshu.TIME_CHARGE.equalsByCode(bean.getHoshuKomokuId()) && TimeChargeTimeShitei.START_END_TIME.equalsByCode(bean.getTimeChargeTimeShitei())) {
				// タイムチャージで、開始・終了日時を指定している場合
				// 発生日には開始日を設定する
				LocalDate timeChargeStartDate = DateUtils.convertLocalDate(bean.getTimeChargeStartTime());
				excelHoshuMeisaiData.setTimeChargeStartTime(bean.getTimeChargeStartTime());
				excelHoshuMeisaiData.setHasseiDateDate(timeChargeStartDate);
				excelHoshuMeisaiData.setHasseiDate(DateUtils.parseToString(timeChargeStartDate, DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			} else {
				// それ以外の場合
				excelHoshuMeisaiData.setHasseiDateDate(bean.getHasseiDate());
				excelHoshuMeisaiData.setHasseiDate(DateUtils.parseToString(bean.getHasseiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			}

			excelHoshuMeisaiData.setHoshuKomoku(LawyerHoshu.of(bean.getHoshuKomokuId()));
			excelHoshuMeisaiData.setHoshuGaku(hoshuGaku);
			excelHoshuMeisaiData.setDispHoshuGaku(hoshuGaku != null ? hoshuGaku.toString() : "0");
			excelHoshuMeisaiData.setTaxGaku(taxGaku);
			excelHoshuMeisaiData.setDispTaxGaku(taxGaku != null ? taxGaku.toString() : "0");
			excelHoshuMeisaiData.setGensenGaku(gensenGaku);
			excelHoshuMeisaiData.setDispGensenGaku(gensenGaku != null ? gensenGaku.negate().toString() : "0"); // 源泉徴収の表示はマイナス
			excelHoshuMeisaiData.setTotal(total);
			excelHoshuMeisaiData.setDispTotal(total != null ? total.toString() : "0");
			excelHoshuMeisaiData.setTekiyo(bean.getTekiyo());

			return excelHoshuMeisaiData;
		};

		List<ExcelHoshuMeisaiData> excelHoshuMeisaiDataList = hoshuMeisaiBeanList.stream().map(mapper).collect(Collectors.toList());

		// データを発生日、開始日時でソート
		List<ExcelHoshuMeisaiData> sotedList = excelHoshuMeisaiDataList.stream()
				.sorted(Comparator.comparing(ExcelHoshuMeisaiData::getHasseiDateDate, Comparator.nullsFirst(LocalDate::compareTo))
						.thenComparing(Comparator.comparing(ExcelHoshuMeisaiData::getTimeChargeStartTime, Comparator.nullsFirst(LocalDateTime::compareTo))))
				.collect(Collectors.toList());

		return sotedList;
	}

}
