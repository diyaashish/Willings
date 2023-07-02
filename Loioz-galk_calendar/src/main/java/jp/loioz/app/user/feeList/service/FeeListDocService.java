package jp.loioz.app.user.feeList.service;

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
import jp.loioz.app.common.csv.builder.Cn0016CsvBuilder;
import jp.loioz.app.common.csv.dto.CsvBuilderDto;
import jp.loioz.app.common.csv.dto.CsvRowDto;
import jp.loioz.app.common.csv.dto.CsvRowItemDto;
import jp.loioz.app.common.excel.builder.naibu.En0022ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.FeeListExcelDto;
import jp.loioz.app.common.excel.dto.FeeListExcelDto.ExcelFeeRowData;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.feeList.form.FeeListSearchForm;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.FeeListBean;
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
import jp.loioz.dao.TFeeDao;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.FeeListCsvDto;

/**
 * 報酬画面のExcel出力サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeListDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 報酬用のDaoクラス */
	@Autowired
	private TFeeDao tFeeDao;

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
	 * 報酬情報をCSVで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputFeeListCsv(HttpServletResponse response, FeeListSearchForm searchForm) throws Exception {
		
		// ■1.Builderを定義
		Cn0016CsvBuilder cn0016CsvBuilder = new Cn0016CsvBuilder();
		cn0016CsvBuilder.setMessageService(messageService);
		cn0016CsvBuilder.setFileName(Cn0016CsvBuilder.FILE_NAME);
		
		// ■2.ダウンロードデータ件数チェック
		if (!cn0016CsvBuilder.validDataCountOver(getFeeListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}
		
		// ■3.DTOを定義、ヘッダー行作成
		CsvBuilderDto csvBuilderDto = cn0016CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerRowDto = cn0016CsvBuilder.getHeaderRowDto();
		// ヘッダー行
		csvBuilderDto.setHeaderRowDto(headerRowDto);
		
		// ■4.データをDTOに設定
		
		// データ取得
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		List<FeeListCsvDto> feeList = getFeeListCsvData(searchForm);
		// 報酬データ設定
		for (FeeListCsvDto dto : feeList) {
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
			
			// 報酬合計(税込み)
			CsvRowItemDto feeTotalAmountItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getFeeTotalAmount()) ? "" : dto.getFeeTotalAmount());
			dataRowItemList.add(feeTotalAmountItem);
			
			// 未請求(合計)
			CsvRowItemDto feeUnclaimedTotalAmountItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getFeeUnclaimedTotalAmount()) ? "" : dto.getFeeUnclaimedTotalAmount());
			dataRowItemList.add(feeUnclaimedTotalAmountItem);
			
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
		cn0016CsvBuilder.setCsvBuilderDto(csvBuilderDto);
		
		try {
			// CSVファイルの出力処理
			cn0016CsvBuilder.makeCsvFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 報酬情報をExcelで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputFeeListExcel(HttpServletResponse response, FeeListSearchForm searchForm) throws Exception {
		
		// ■1.Builderを定義
		En0022ExcelBuilder en0022excelBuilder = new En0022ExcelBuilder();
		en0022excelBuilder.setConfig(excelConfig);
		en0022excelBuilder.setMessageService(messageService);
		en0022excelBuilder.setFileName(En0022ExcelBuilder.FILE_NAME);
		
		// ■2.ダウンロードデータ件数チェック
		if (!en0022excelBuilder.validDataCountOver(getFeeListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}
		
		// ■3.DTOを定義
		FeeListExcelDto feeListExcelDto = en0022excelBuilder.createNewTargetBuilderDto();
		
		// ■4.DTOに設定するデータ取得と設定
		feeListExcelDto.setExcelfeeRowDataList(this.getFeeListExcelData(searchForm));
		feeListExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));
		
		en0022excelBuilder.setFeeListExcelDto(feeListExcelDto);
		try {
			// Excelファイルの出力処理
			en0022excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 報酬データ数を取得する。
	 * 
	 * @param searchForm
	 */
	private int getFeeListDataCount(FeeListSearchForm searchForm) {
		
		// 報酬件数を取得
		int feeCount = tFeeDao.selectByListSearchConditionsCount(searchForm.toFeeListSearchCondition(), searchForm.toFeeListSortCondition());
		
		return feeCount;
	}

	/**
	 * CSV出力用に報酬データを取得する。<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<FeeListCsvDto> getFeeListCsvData(FeeListSearchForm searchForm) {
		
		// 報酬データ取得
		List<FeeListBean> feeListBean = tFeeDao.selectByListSearchConditions(searchForm.toFeeListSearchCondition(), searchForm.toFeeListSortCondition());
		
		// 報酬情報に担当弁護士、担当事務をセット
		this.setFeeListTantoLaywerJimu(feeListBean);
		
		// DTO変換
		List<FeeListCsvDto> feeCsvList = this.convertBean2CsvDtoList(feeListBean);
		
		return feeCsvList;
	}

	/**
	 * List<FeeListBean>型からList<FeeListCsvDto>型に変換します
	 * 
	 * @param feeListBean
	 * @return
	 */
	private List<FeeListCsvDto> convertBean2CsvDtoList(List<FeeListBean> feeListBean) {
		
		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));
		
		// Bean型をDto型に変換する
		List<FeeListCsvDto> dtoList = new ArrayList<FeeListCsvDto>();
		feeListBean.forEach(bean -> {
			dtoList.add(convertFeeBean2CsvDto(bean, bunyaMap));
		});
		return dtoList;
	}

	/**
	 * FeeListBean型からFeeListCsvDto型に変換します<br>
	 * 
	 * @param rowData
	 * @param bunyaMap
	 * @return
	 */
	private FeeListCsvDto convertFeeBean2CsvDto(FeeListBean rowData, Map<Long, BunyaDto> bunyaMap) {
		
		FeeListCsvDto csvFeeRowData = new FeeListCsvDto();
		
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
		
		// 報酬(税込)
		if (rowData.getFeeTotalAmount() != null) {
			csvFeeRowData.setFeeTotalAmount(AccountingUtils.toDispAmountLabel(rowData.getFeeTotalAmount()));
		}
		
		// 未請求
		if (rowData.getFeeUnclaimedTotalAmount() != null) {
			csvFeeRowData.setFeeUnclaimedTotalAmount(AccountingUtils.toDispAmountLabel(rowData.getFeeUnclaimedTotalAmount()));
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
	 * Excel出力用に報酬データを取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<ExcelFeeRowData> getFeeListExcelData(FeeListSearchForm searchForm) {
		
		// 報酬データ取得
		List<FeeListBean> feeListBean = tFeeDao.selectByListSearchConditions(searchForm.toFeeListSearchCondition(), searchForm.toFeeListSortCondition());
		
		// 報酬情報に担当弁護士、担当事務をセット
		this.setFeeListTantoLaywerJimu(feeListBean);
		
		// Dto変換
		List<ExcelFeeRowData> feeExcelList = this.convertBean2ExcelDtoList(feeListBean);
		
		return feeExcelList;
	}

	/**
	 * List<FeeListBean>型からList<ExcelFeeRowData>型に変換します
	 * 
	 * @param feeListBean
	 * @return
	 */
	private List<ExcelFeeRowData> convertBean2ExcelDtoList(List<FeeListBean> feeListBean) {
		
		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));
		
		// Bean型をDto型に変換する
		List<ExcelFeeRowData> dtoList = new ArrayList<ExcelFeeRowData>();
		feeListBean.forEach(bean -> {
			dtoList.add(convertFeeBean2ExcelDto(bean, bunyaMap));
		});
		return dtoList;
	}

	/**
	 * FeeListBean型からExcelFeeRowData型に変換します<br>
	 * 
	 * @param rowData
	 * @param bunyaMap
	 * @return
	 */
	private ExcelFeeRowData convertFeeBean2ExcelDto(FeeListBean rowData, Map<Long, BunyaDto> bunyaMap) {
		
		ExcelFeeRowData excelFeeRowData = new ExcelFeeRowData();
		
		// 名簿ID
		if (rowData.getPersonId() != null && !rowData.getPersonId().equals(Long.valueOf(0))) {
			excelFeeRowData.setPersonId(rowData.getPersonId().toString());
		}
		
		// 名前
		if (!StringUtils.isEmpty(rowData.getPersonNameSei())) {
			excelFeeRowData.setName(rowData.getPersonNameSei() + CommonConstant.SPACE + rowData.getPersonNameMei());
		}
		
		// 案件ID
		if (rowData.getAnkenId() != null && !rowData.getAnkenId().equals(Long.valueOf(0))) {
			excelFeeRowData.setAnkenId(rowData.getAnkenId().toString());
		}
		
		// 分野名
		if (rowData.getBunyaId() != null) {
			excelFeeRowData.setBunyaName(bunyaMap.get(rowData.getBunyaId()).getBunyaName());
		}
		
		// 案件名
		excelFeeRowData.setAnkenName(StringUtils.isEmpty(rowData.getAnkenName()) ? "(案件名未入力)" : rowData.getAnkenName());
		
		// ステータス名
		if (!StringUtils.isEmpty(rowData.getAnkenStatus())) {
			excelFeeRowData.setAnkenStatusName(CommonConstant.AnkenStatus.of(rowData.getAnkenStatus()).getVal());
		}
		
		// 報酬(税込)
		if (rowData.getFeeTotalAmount() != null) {
			excelFeeRowData.setFeeTotalAmount(rowData.getFeeTotalAmount());
		}
		
		// 未請求
		if (rowData.getFeeUnclaimedTotalAmount() != null) {
			excelFeeRowData.setFeeUnclaimedTotalAmount(rowData.getFeeUnclaimedTotalAmount());
		}
		
		// 担当弁護士
		if (!StringUtils.isEmpty(rowData.getTantoLaywerName())) {
			List<String> lawyerTantoNameList = Arrays.asList(rowData.getTantoLaywerName().split(","));
			try {
				excelFeeRowData.setTantoLawyerName1(lawyerTantoNameList.get(0).trim());
				excelFeeRowData.setTantoLawyerName2(lawyerTantoNameList.get(1).trim());
				excelFeeRowData.setTantoLawyerName3(lawyerTantoNameList.get(2).trim());
				excelFeeRowData.setTantoLawyerName4(lawyerTantoNameList.get(3).trim());
				excelFeeRowData.setTantoLawyerName5(lawyerTantoNameList.get(4).trim());
				excelFeeRowData.setTantoLawyerName6(lawyerTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当弁護士が6人に満たない場合。問題無いので処理を継続。
			}
		}
		
		// 担当事務
		if (!StringUtils.isEmpty(rowData.getTantoJimuName())) {
			List<String> jimuTantoNameList = Arrays.asList(rowData.getTantoJimuName().split(","));
			try {
				excelFeeRowData.setTantoJimuName1(jimuTantoNameList.get(0).trim());
				excelFeeRowData.setTantoJimuName2(jimuTantoNameList.get(1).trim());
				excelFeeRowData.setTantoJimuName3(jimuTantoNameList.get(2).trim());
				excelFeeRowData.setTantoJimuName4(jimuTantoNameList.get(3).trim());
				excelFeeRowData.setTantoJimuName5(jimuTantoNameList.get(4).trim());
				excelFeeRowData.setTantoJimuName6(jimuTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当事務員が6人に満たない場合。問題無いので処理を継続。
			}
		}
		
		// 最終更新日時
		if (rowData.getLastEditAt() != null) {
			excelFeeRowData.setLastEditAtFormat(DateUtils.parseToString(rowData.getLastEditAt(), DateUtils.HYPHEN_YYYY_MM_DD_HHMM));
		}
		
		return excelFeeRowData;
	}

	/**
	 * 報酬情報に担当弁護士、担当事務をセットします。<br>
	 * 
	 * @param feeList
	 */
	private void setFeeListTantoLaywerJimu(List<FeeListBean> feeList) {

		if (LoiozCollectionUtils.isEmpty(feeList)) {
			return;
		}

		List<Long> ankenIdList = feeList.stream().map(bean -> bean.getAnkenId()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (FeeListBean feeListBean : feeList) {
			// 担当弁護士の担当者名
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(feeListBean.getAnkenId())
							&& TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (lawyerOptBean.isPresent()) {
				feeListBean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream().filter(
					tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(feeListBean.getAnkenId())
							&& TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType()))
					.findFirst();
			if (jimuOptBean.isPresent()) {
				feeListBean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}
}