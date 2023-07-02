package jp.loioz.app.user.depositRecvList.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.csv.builder.Cn0017CsvBuilder;
import jp.loioz.app.common.csv.dto.CsvBuilderDto;
import jp.loioz.app.common.csv.dto.CsvRowDto;
import jp.loioz.app.common.csv.dto.CsvRowItemDto;
import jp.loioz.app.common.excel.builder.naibu.En0023ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.DepositRecvListExcelDto;
import jp.loioz.app.common.excel.dto.DepositRecvListExcelDto.ExcelDepositRecvRowData;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.depositRecvList.form.DepositRecvListSearchForm;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.DepositRecvListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TDepositRecvDao;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.DepositRecvListCsvDto;

/**
 * 預り金／実費画面のExcel出力サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositRecvListDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 預り金Daoクラス */
	@Autowired
	private TDepositRecvDao tDepositRecvDao;

	/** 案件担当者情報用のDaoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** メッセージサービス */
	@Autowired
	MessageService messageService;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 預り金／実費情報をCSVで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputDepositRecvListCsv(HttpServletResponse response, DepositRecvListSearchForm searchForm) throws Exception {
		
		// ■1.Builderを定義
		Cn0017CsvBuilder cn0017CsvBuilder = new Cn0017CsvBuilder();
		cn0017CsvBuilder.setMessageService(messageService);
		cn0017CsvBuilder.setFileName(Cn0017CsvBuilder.FILE_NAME);
		
		// ■2.ダウンロードデータ件数チェック
		if (!cn0017CsvBuilder.validDataCountOver(this.getDepositRecvListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}
		
		// ■3.DTOを定義、ヘッダー行作成
		CsvBuilderDto csvBuilderDto = cn0017CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerRowDto = cn0017CsvBuilder.getHeaderRowDto();
		// ヘッダー行
		csvBuilderDto.setHeaderRowDto(headerRowDto);
		
		// ■4.データをDTOに設定
		
		// データ取得
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		List<DepositRecvListCsvDto> depositRecvList = this.getDepositRecvListCsvData(searchForm);
		// 預り金／実費データ設定
		for (DepositRecvListCsvDto dto : depositRecvList) {
			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();
			
			// 名簿ID
			CsvRowItemDto personIdItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getPersonId()) ? "" : dto.getPersonId());
			dataRowItemList.add(personIdItem);
			
			// 名前
			CsvRowItemDto nameItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getName()) ? "" : dto.getName());
			dataRowItemList.add(nameItem);
			
			// 案件ID
			CsvRowItemDto ankenIdItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getAnkenId()) ? "" : dto.getAnkenId());
			dataRowItemList.add(ankenIdItem);
			
			// 分野名
			CsvRowItemDto bunyaNameItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getBunyaName()) ? "" : dto.getBunyaName());
			dataRowItemList.add(bunyaNameItem);
			
			// 案件名
			CsvRowItemDto ankenNameItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getAnkenName()) ? "(案件名未入力)" : dto.getAnkenName());
			dataRowItemList.add(ankenNameItem);
			
			// ステータス名
			CsvRowItemDto statusNameItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getAnkenStatusName()) ? "" : dto.getAnkenStatusName());
			dataRowItemList.add(statusNameItem);
			
			// 入金額
			CsvRowItemDto nyukinTotalAmountItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getNyukinTotal()) ? "" : dto.getNyukinTotal());
			dataRowItemList.add(nyukinTotalAmountItem);
			
			// 出金額
			CsvRowItemDto shukkinTotalAmountItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getShukkinTotal()) ? "" : dto.getShukkinTotal());
			dataRowItemList.add(shukkinTotalAmountItem);
			
			// 預り金残高
			CsvRowItemDto zandakaTotalAmountItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getZandakaTotal()) ? "" : dto.getZandakaTotal());
			dataRowItemList.add(zandakaTotalAmountItem);
			
			// 担当弁護士１
			CsvRowItemDto tantoLawyerName1 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoLawyerName1()) ? "" : dto.getTantoLawyerName1());
			dataRowItemList.add(tantoLawyerName1);
			
			// 担当弁護士２
			CsvRowItemDto tantoLawyerName2 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoLawyerName2()) ? "" : dto.getTantoLawyerName2());
			dataRowItemList.add(tantoLawyerName2);
			
			// 担当弁護士３
			CsvRowItemDto tantoLawyerName3 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoLawyerName3()) ? "" : dto.getTantoLawyerName3());
			dataRowItemList.add(tantoLawyerName3);
			
			// 担当弁護士４
			CsvRowItemDto tantoLawyerName4 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoLawyerName4()) ? "" : dto.getTantoLawyerName4());
			dataRowItemList.add(tantoLawyerName4);
			
			// 担当弁護士５
			CsvRowItemDto tantoLawyerName5 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoLawyerName5()) ? "" : dto.getTantoLawyerName5());
			dataRowItemList.add(tantoLawyerName5);
			
			// 担当弁護士６
			CsvRowItemDto tantoLawyerName6 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoLawyerName6()) ? "" : dto.getTantoLawyerName6());
			dataRowItemList.add(tantoLawyerName6);
			
			// 担当事務１
			CsvRowItemDto tantoJimuName1 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoJimuName1()) ? "" : dto.getTantoJimuName1());
			dataRowItemList.add(tantoJimuName1);
			
			// 担当事務２
			CsvRowItemDto tantoJimuName2 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoJimuName2()) ? "" : dto.getTantoJimuName2());
			dataRowItemList.add(tantoJimuName2);
			
			// 担当事務３
			CsvRowItemDto tantoJimuName3 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoJimuName3()) ? "" : dto.getTantoJimuName3());
			dataRowItemList.add(tantoJimuName3);
			
			// 担当事務４
			CsvRowItemDto tantoJimuName4 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoJimuName4()) ? "" : dto.getTantoJimuName4());
			dataRowItemList.add(tantoJimuName4);
			
			// 担当事務５
			CsvRowItemDto tantoJimuName5 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoJimuName5()) ? "" : dto.getTantoJimuName5());
			dataRowItemList.add(tantoJimuName5);
			
			// 担当事務６
			CsvRowItemDto tantoJimuName6 = new CsvRowItemDto(StringUtils.isEmpty(dto.getTantoJimuName6()) ? "" : dto.getTantoJimuName6());
			dataRowItemList.add(tantoJimuName6);
			
			// 最終更新日時
			CsvRowItemDto lastEditAtItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getLastEditAtFormat()) ? "" : dto.getLastEditAtFormat());
			dataRowItemList.add(lastEditAtItem);
			
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}
		
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
		cn0017CsvBuilder.setCsvBuilderDto(csvBuilderDto);
		
		try {
			// CSVファイルの出力処理
			cn0017CsvBuilder.makeCsvFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 預り金／実費情報をExcelで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputDepositRecvListExcel(HttpServletResponse response, DepositRecvListSearchForm searchForm) throws Exception {
		
		// ■1.Builderを定義
		En0023ExcelBuilder en0023excelBuilder = new En0023ExcelBuilder();
		en0023excelBuilder.setConfig(excelConfig);
		en0023excelBuilder.setMessageService(messageService);
		en0023excelBuilder.setFileName(En0023ExcelBuilder.FILE_NAME);
		
		// ■2.ダウンロードデータ件数チェック
		if (!en0023excelBuilder.validDataCountOver(this.getDepositRecvListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}
		
		// ■3.DTOを定義
		DepositRecvListExcelDto depositRecvListExcelDto = en0023excelBuilder.createNewTargetBuilderDto();
		
		// ■4.DTOに設定するデータ取得と設定
		depositRecvListExcelDto.setExcelDepositRecvRowDataList(this.getDepositRecvListExcelData(searchForm));
		depositRecvListExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));
		
		en0023excelBuilder.setDepositRecvListExcelDto(depositRecvListExcelDto);
		try {
			// Excelファイルの出力処理
			en0023excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 預り金／実費データ数を取得する。
	 * 
	 * @param searchForm
	 */
	private int getDepositRecvListDataCount(DepositRecvListSearchForm searchForm) {
		
		// 報酬件数を取得
		int depositRecvCount = tDepositRecvDao.selectByListSearchConditionsCount(searchForm.toDepositRecvListSearchCondition(), searchForm.toDepositRecvListSortCondition());
		
		return depositRecvCount;
	}

	/**
	 * CSV出力用に預り金／実費データを取得する。<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<DepositRecvListCsvDto> getDepositRecvListCsvData(DepositRecvListSearchForm searchForm) {
		
		// 預り金／実費データ取得
		List<DepositRecvListBean> depositRecvListBean = tDepositRecvDao.selectByListSearchConditions(searchForm.toDepositRecvListSearchCondition(), searchForm.toDepositRecvListSortCondition());
		
		// 預り金／実費情報に担当弁護士、担当事務をセット
		this.setDepositRecvListTantoLaywerJimu(depositRecvListBean);
		
		// DTO変換
		List<DepositRecvListCsvDto> depositRecvCsvList = this.convertBean2CsvDtoList(depositRecvListBean);
		
		return depositRecvCsvList;
	}

	/**
	 * List<DepositRecvListBean>型からList<DepositRecvListCsvDto>型に変換します
	 * 
	 * @param depositRecvListBean
	 * @return
	 */
	private List<DepositRecvListCsvDto> convertBean2CsvDtoList(List<DepositRecvListBean> depositRecvListBean) {
		
		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));
		
		// Bean型をDto型に変換する
		List<DepositRecvListCsvDto> dtoList = new ArrayList<DepositRecvListCsvDto>();
		depositRecvListBean.forEach(bean -> {
			dtoList.add(convertFeeBean2CsvDto(bean, bunyaMap));
		});
		return dtoList;
	}

	/**
	 * DepositRecvListBean型からDepositRecvListCsvDto型に変換します<br>
	 * 
	 * @param rowData
	 * @param bunyaMap
	 * @return
	 */
	private DepositRecvListCsvDto convertFeeBean2CsvDto(DepositRecvListBean rowData, Map<Long, BunyaDto> bunyaMap) {
		
		DepositRecvListCsvDto csvFeeRowData = new DepositRecvListCsvDto();
		
		// 名簿ID
		if (rowData.getPersonId() != null && !rowData.getPersonId().equals(Long.valueOf(0))) {
			csvFeeRowData.setPersonId(rowData.getPersonId().toString());
		}
		
		// 名前
		if (!StringUtils.isEmpty(rowData.getPersonNameSei())) {
			csvFeeRowData.setName(rowData.getPersonNameSei() + CommonConstant.SPACE + rowData.getPersonNameMei());
		}
		
		// 案件ID
		if (rowData.getAnkenId() != null && !rowData.getAnkenId().equals(Long.valueOf(0))) {
			csvFeeRowData.setAnkenId(rowData.getAnkenId().toString());
		}
		
		// 分野名
		if (rowData.getBunyaId() != null) {
			csvFeeRowData.setBunyaName(bunyaMap.get(rowData.getBunyaId()).getBunyaName());
		}
		
		// 案件名
		if (!StringUtils.isEmpty(rowData.getAnkenName())) {
			csvFeeRowData.setAnkenName(rowData.getAnkenName());
		}
		
		// ステータス名
		if (!StringUtils.isEmpty(rowData.getAnkenStatus())) {
			csvFeeRowData.setAnkenStatusName(CommonConstant.AnkenStatus.of(rowData.getAnkenStatus()).getVal());
		}
		
		// 入金額
		if (rowData.getNyukinTotal() != null) {
			csvFeeRowData.setNyukinTotal(AccountingUtils.toDispAmountLabel(rowData.getNyukinTotal()));
		}
		
		// 出金額
		if (rowData.getShukkinTotal() != null) {
			csvFeeRowData.setShukkinTotal(AccountingUtils.toDispAmountLabel(rowData.getShukkinTotal()));
		}
		
		// 預り金残高
		if (rowData.getZandakaTotal() != null) {
			csvFeeRowData.setZandakaTotal(AccountingUtils.toDispAmountLabel(rowData.getZandakaTotal()));
		}
		
		// 担当弁護士
		if (!StringUtils.isEmpty(rowData.getTantoLaywerName())) {
			List<String> lawyerTantoNameList = Arrays.asList(rowData.getTantoLaywerName().split(","));
			try {
				csvFeeRowData.setTantoLawyerName1(lawyerTantoNameList.get(0).trim());
				csvFeeRowData.setTantoLawyerName2(lawyerTantoNameList.get(1).trim());
				csvFeeRowData.setTantoLawyerName3(lawyerTantoNameList.get(2).trim());
				csvFeeRowData.setTantoLawyerName4(lawyerTantoNameList.get(3).trim());
				csvFeeRowData.setTantoLawyerName5(lawyerTantoNameList.get(4).trim());
				csvFeeRowData.setTantoLawyerName6(lawyerTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当弁護士が6人に満たない場合。問題無いので処理を継続。
			}
		}
		
		// 担当事務
		if (!StringUtils.isEmpty(rowData.getTantoJimuName())) {
			List<String> jimuTantoNameList = Arrays.asList(rowData.getTantoJimuName().split(","));
			try {
				csvFeeRowData.setTantoJimuName1(jimuTantoNameList.get(0).trim());
				csvFeeRowData.setTantoJimuName2(jimuTantoNameList.get(1).trim());
				csvFeeRowData.setTantoJimuName3(jimuTantoNameList.get(2).trim());
				csvFeeRowData.setTantoJimuName4(jimuTantoNameList.get(3).trim());
				csvFeeRowData.setTantoJimuName5(jimuTantoNameList.get(4).trim());
				csvFeeRowData.setTantoJimuName6(jimuTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当事務員が6人に満たない場合。問題無いので処理を継続。
			}
		}
		
		// 最終更新日時
		if (rowData.getLastEditAt() != null) {
			csvFeeRowData.setLastEditAtFormat(DateUtils.parseToString(rowData.getLastEditAt(), DateUtils.HYPHEN_YYYY_MM_DD_HHMM));
		}
		
		return csvFeeRowData;
	}

	/**
	 * Excel出力用に預り金／実費データを取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<ExcelDepositRecvRowData> getDepositRecvListExcelData(DepositRecvListSearchForm searchForm) {
		
		// 預り金／実費データ取得
		List<DepositRecvListBean> depositRecvListBean = tDepositRecvDao.selectByListSearchConditions(searchForm.toDepositRecvListSearchCondition(), searchForm.toDepositRecvListSortCondition());
		
		// 預り金／実費データに担当弁護士、担当事務をセット
		this.setDepositRecvListTantoLaywerJimu(depositRecvListBean);
		
		// Dto変換
		List<ExcelDepositRecvRowData> depositRecvExcelList = this.convertBean2ExcelDtoList(depositRecvListBean);
		
		return depositRecvExcelList;
	}

	/**
	 * List<FeeListBean>型からList<ExcelFeeRowData>型に変換します
	 * 
	 * @param feeListBean
	 * @return
	 */
	private List<ExcelDepositRecvRowData> convertBean2ExcelDtoList(List<DepositRecvListBean> depositRecvListBean) {
		
		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));
		
		// Bean型をDto型に変換する
		List<ExcelDepositRecvRowData> dtoList = new ArrayList<ExcelDepositRecvRowData>();
		depositRecvListBean.forEach(bean -> {
			dtoList.add(convertFeeBean2ExcelDto(bean, bunyaMap));
		});
		return dtoList;
	}

	/**
	 * DepositRecvListBean型からExcelDepositRecvRowData型に変換します<br>
	 * 
	 * @param rowData
	 * @param bunyaMap
	 * @return
	 */
	private ExcelDepositRecvRowData convertFeeBean2ExcelDto(DepositRecvListBean rowData, Map<Long, BunyaDto> bunyaMap) {
		
		ExcelDepositRecvRowData excelRowData = new ExcelDepositRecvRowData();
		
		// 名簿ID
		if (rowData.getPersonId() != null && !rowData.getPersonId().equals(Long.valueOf(0))) {
			excelRowData.setPersonId(rowData.getPersonId().toString());
		}
		
		// 名前
		if (!StringUtils.isEmpty(rowData.getPersonNameSei())) {
			excelRowData.setName(rowData.getPersonNameSei() + CommonConstant.SPACE + rowData.getPersonNameMei());
		}
		
		// 案件ID
		if (rowData.getAnkenId() != null && !rowData.getAnkenId().equals(Long.valueOf(0))) {
			excelRowData.setAnkenId(rowData.getAnkenId().toString());
		}
		
		// 分野名
		if (rowData.getBunyaId() != null) {
			excelRowData.setBunyaName(bunyaMap.get(rowData.getBunyaId()).getBunyaName());
		}
		
		// 案件名
		excelRowData.setAnkenName(StringUtils.isEmpty(rowData.getAnkenName()) ? "(案件名未入力)" : rowData.getAnkenName());
		
		// ステータス名
		if (!StringUtils.isEmpty(rowData.getAnkenStatus())) {
			excelRowData.setAnkenStatusName(CommonConstant.AnkenStatus.of(rowData.getAnkenStatus()).getVal());
		}
		
		// 入金額
		if (rowData.getNyukinTotal() != null) {
			excelRowData.setNyukinTotal(rowData.getNyukinTotal());
		}
		
		// 出金額
		if (rowData.getShukkinTotal() != null) {
			excelRowData.setShukkinTotal(rowData.getShukkinTotal());
		}
		
		// 預り金残高
		if (rowData.getZandakaTotal() != null) {
			excelRowData.setZandakaTotal(rowData.getZandakaTotal());
		}
		
		// 担当弁護士
		if (!StringUtils.isEmpty(rowData.getTantoLaywerName())) {
			List<String> lawyerTantoNameList = Arrays.asList(rowData.getTantoLaywerName().split(","));
			try {
				excelRowData.setTantoLawyerName1(lawyerTantoNameList.get(0).trim());
				excelRowData.setTantoLawyerName2(lawyerTantoNameList.get(1).trim());
				excelRowData.setTantoLawyerName3(lawyerTantoNameList.get(2).trim());
				excelRowData.setTantoLawyerName4(lawyerTantoNameList.get(3).trim());
				excelRowData.setTantoLawyerName5(lawyerTantoNameList.get(4).trim());
				excelRowData.setTantoLawyerName6(lawyerTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当弁護士が6人に満たない場合。問題無いので処理を継続。
			}
		}
		
		// 担当事務
		if (!StringUtils.isEmpty(rowData.getTantoJimuName())) {
			List<String> jimuTantoNameList = Arrays.asList(rowData.getTantoJimuName().split(","));
			try {
				excelRowData.setTantoJimuName1(jimuTantoNameList.get(0).trim());
				excelRowData.setTantoJimuName2(jimuTantoNameList.get(1).trim());
				excelRowData.setTantoJimuName3(jimuTantoNameList.get(2).trim());
				excelRowData.setTantoJimuName4(jimuTantoNameList.get(3).trim());
				excelRowData.setTantoJimuName5(jimuTantoNameList.get(4).trim());
				excelRowData.setTantoJimuName6(jimuTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当事務員が6人に満たない場合。問題無いので処理を継続。
			}
		}
		
		// 最終更新日時
		if (rowData.getLastEditAt() != null) {
			excelRowData.setLastEditAtFormat(DateUtils.parseToString(rowData.getLastEditAt(), DateUtils.HYPHEN_YYYY_MM_DD_HHMM));
		}
		
		return excelRowData;
	}

	/**
	 * 預り金／実費情報に担当弁護士、担当事務をセットします。<br>
	 * 
	 * @param depositRecvList
	 */
	private void setDepositRecvListTantoLaywerJimu(List<DepositRecvListBean> depositRecvList) {
		
		if (LoiozCollectionUtils.isEmpty(depositRecvList)) {
			return;
		}
		
		List<Long> ankenIdList = depositRecvList.stream().map(bean -> bean.getAnkenId()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (DepositRecvListBean bean : depositRecvList) {
			// 担当弁護士の担当者名
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(bean.getAnkenId())
							&& TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (lawyerOptBean.isPresent()) {
				bean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(bean.getAnkenId())
							&& TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (jimuOptBean.isPresent()) {
				bean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}
}