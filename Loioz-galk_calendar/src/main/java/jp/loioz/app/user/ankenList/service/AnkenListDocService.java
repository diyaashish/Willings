package jp.loioz.app.user.ankenList.service;

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
import jp.loioz.app.common.csv.builder.Cn0001CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0005CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0006CsvBuilder;
import jp.loioz.app.common.csv.dto.CsvBuilderDto;
import jp.loioz.app.common.csv.dto.CsvRowDto;
import jp.loioz.app.common.csv.dto.CsvRowItemDto;
import jp.loioz.app.common.excel.builder.naibu.En0010ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0015ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0016ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.AnkenListExcelDto;
import jp.loioz.app.common.excel.dto.AnkenListExcelDto.ExcelAnkenListRowData;
import jp.loioz.app.common.excel.dto.KeijiSaibanListExcelDto;
import jp.loioz.app.common.excel.dto.KeijiSaibanListExcelDto.ExcelKeijiSaibanListRowData;
import jp.loioz.app.common.excel.dto.MinjiSaibanListExcelDto;
import jp.loioz.app.common.excel.dto.MinjiSaibanListExcelDto.ExcelMinjiSaibanListRowData;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.chohyo.CommonChohyoService;
import jp.loioz.app.user.ankenList.form.AnkenListSearchForm;
import jp.loioz.bean.AnkenAitegataCntBean;
import jp.loioz.bean.AnkenListBean;
import jp.loioz.bean.AnkenTantoFlgBean;
import jp.loioz.bean.AnkenTantoLawyerJimuBean;
import jp.loioz.bean.SaibanAitegataBean;
import jp.loioz.bean.SaibanListBean;
import jp.loioz.bean.SaibanSeqCustomerBean;
import jp.loioz.bean.SaibanTantoFlgBean;
import jp.loioz.bean.SaibanTantoLawyerJimuBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenListMenu;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.AnkenListDao;
import jp.loioz.dao.TAnkenTantoDao;
import jp.loioz.dao.TSaibanTantoDao;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.dto.AnkenListCsvDto;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.SaibanListCsvDto;

/**
 * 案件一覧画面のExcel出力サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AnkenListDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** 帳票の共通サービス */
	@Autowired
	private CommonChohyoService commonChohyoService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 案件一覧共通サービスクラス */
	@Autowired
	private AnkenListCommonService ankenListCommonService;

	/** 案件一覧Daoクラス */
	@Autowired
	private AnkenListDao ankenListDao;

	/** 案件担当Daoクラス */
	@Autowired
	private TAnkenTantoDao tAnkenTantoDao;

	/** 裁判担当Daoクラス */
	@Autowired
	private TSaibanTantoDao tSaibanTantoDao;

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
	 * 案件一覧情報をCSVで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputAnkenListCsv(HttpServletResponse response, AnkenListSearchForm searchForm) throws Exception {

		// ■1.Builderを定義
		Cn0001CsvBuilder cn0001CsvBuilder = new Cn0001CsvBuilder();
		cn0001CsvBuilder.setMessageService(messageService);
		cn0001CsvBuilder.setFileName(Cn0001CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0001CsvBuilder.validDataCountOver(getAnkenListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義、ヘッダー行作成
		CsvBuilderDto csvBuilderDto = cn0001CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerRowDto = cn0001CsvBuilder.getHeaderRowDto();
		// ヘッダー行
		csvBuilderDto.setHeaderRowDto(headerRowDto);

		// ■4.データをDTOに設定

		// データ取得
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		List<AnkenListCsvDto> ankenList = getAnkenListCsvData(searchForm);
		// 案件一覧データ設定
		for (AnkenListCsvDto dto : ankenList) {
			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();
			// 案件ID
			CsvRowItemDto ankenIdItem = new CsvRowItemDto(dto.getAnkenId() == null ? "" : dto.getAnkenId().asLong().toString());
			dataRowItemList.add(ankenIdItem);
			// 分野
			CsvRowItemDto bunyaItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getBunyaName()) ? "" : dto.getBunyaName());
			dataRowItemList.add(bunyaItem);
			// 案件名
			CsvRowItemDto ankenNameItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getAnkenName()) ? "(案件名未入力)" : dto.getAnkenName());
			dataRowItemList.add(ankenNameItem);
			// 顧客名
			CsvRowItemDto customerNameItem = new CsvRowItemDto(dto.getCustomerNameSei() == null ? "" : dto.getCustomerNameSei() + CommonConstant.SPACE + dto.getCustomerNameMei());
			dataRowItemList.add(customerNameItem);
			// 相手方
			CsvRowItemDto aitegataItem = new CsvRowItemDto(
					StringUtils.isEmpty(dto.getAitegataName()) ? "" : dto.getAitegataName() + commonChohyoService.formatOthersCount(dto.getNumberOfAitegata()));
			dataRowItemList.add(aitegataItem);
			// 顧客ステータス
			CsvRowItemDto ankenStatusItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getAnkenStatusName()) ? "" : dto.getAnkenStatusName());
			dataRowItemList.add(ankenStatusItem);
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
			// 受任日
			CsvRowItemDto jyuninDateItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getJuninDateFormat()) ? "" : dto.getJuninDateFormat());
			dataRowItemList.add(jyuninDateItem);
			// 登録日
			CsvRowItemDto ankenCreateDateItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getAnkenCreatedDateFormat()) ? "" : dto.getAnkenCreatedDateFormat());
			dataRowItemList.add(ankenCreateDateItem);

			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
		cn0001CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0001CsvBuilder.makeCsvFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 案件一覧情報をExcelで出力します。全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputAnkenListExcel(HttpServletResponse response, AnkenListSearchForm searchForm) throws Exception {

		// ■1.Builderを定義
		En0010ExcelBuilder en0010excelBuilder = new En0010ExcelBuilder();
		en0010excelBuilder.setConfig(excelConfig);
		en0010excelBuilder.setMessageService(messageService);
		en0010excelBuilder.setFileName(En0010ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0010excelBuilder.validDataCountOver(getAnkenListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		AnkenListExcelDto ankenListExcelDto = en0010excelBuilder.createNewTargetBuilderDto();

		// ■4.DTOに設定するデータ取得と設定
		ankenListExcelDto.setSelectedAnkenListMenu(searchForm.getSelectedAnkenListMenu());
		ankenListExcelDto.setAnkenListDtoList(this.getAnkenListExcelData(searchForm));
		ankenListExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));

		en0010excelBuilder.setAnkenListExcelDto(ankenListExcelDto);
		try {
			// Excelファイルの出力処理
			en0010excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 民事裁判一覧情報をCSVで出力します。検索画面の全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputMinjiSaibanListCsv(HttpServletResponse response, AnkenListSearchForm searchForm) throws Exception {

		// ■1.Builderを定義
		Cn0005CsvBuilder cn0005CsvBuilder = new Cn0005CsvBuilder();
		cn0005CsvBuilder.setMessageService(messageService);
		cn0005CsvBuilder.setFileName(Cn0005CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0005CsvBuilder.validDataCountOver(getSaibanListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義、ヘッダーを作成
		CsvBuilderDto csvBuilderDto = cn0005CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerRowDto = cn0005CsvBuilder.getHeaderRowDto();
		csvBuilderDto.setHeaderRowDto(headerRowDto);


		// ■4.データをDTOに設定
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();

		// 裁判一覧データ取得
		List<SaibanListCsvDto> saibanList = getSaibanListCsvData(searchForm);
		for (SaibanListCsvDto dto : saibanList) {
			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();
			// 案件ID
			CsvRowItemDto ankenIdItem = new CsvRowItemDto(dto.getAnkenId() == null ? "" : dto.getAnkenId().asLong().toString());
			dataRowItemList.add(ankenIdItem);
			// 分野
			CsvRowItemDto bunyaItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getBunyaName()) ? "" : dto.getBunyaName());
			dataRowItemList.add(bunyaItem);
			// 裁判所
			CsvRowItemDto saibanshoItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getSaibanshoNameMei()) ? "" : dto.getSaibanshoNameMei());
			dataRowItemList.add(saibanshoItem);
			// 事件番号
			CsvRowItemDto jikenNoItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getJikenNumber()) ? "" : dto.getJikenNumber());
			dataRowItemList.add(jikenNoItem);
			// 事件名
			CsvRowItemDto jikenNameItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getJikenName()) ? "" : dto.getJikenName());
			dataRowItemList.add(jikenNameItem);
			// 名前（当事者）
			CsvRowItemDto customerNameItem = new CsvRowItemDto(
					dto.getCustomerNameSei() == null ? "" : dto.getCustomerNameSei() + CommonConstant.SPACE + dto.getCustomerNameMei() + commonChohyoService.formatOthersCount(dto.getNumberOfCustomer()));
			dataRowItemList.add(customerNameItem);
			// 相手方
			CsvRowItemDto aitegataItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getAitegataName()) ? "" : dto.getAitegataName() + commonChohyoService.formatOthersCount(dto.getNumberOfAitegata()));
			dataRowItemList.add(aitegataItem);
			// 裁判手続き
			CsvRowItemDto saibanStatusItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getSaibanStatusName()) ? "" : dto.getSaibanStatusName());
			dataRowItemList.add(saibanStatusItem);
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
			// 申立日／起訴日
			CsvRowItemDto saibanStartDateItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getSaibanStartDateFormat()) ? "" : dto.getSaibanStartDateFormat());
			dataRowItemList.add(saibanStartDateItem);
			// 終了日／判決日
			CsvRowItemDto saibanEndDateItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getSaibanEndDateFormat()) ? "" : dto.getSaibanEndDateFormat());
			dataRowItemList.add(saibanEndDateItem);

			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
		cn0005CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0005CsvBuilder.makeCsvFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 刑事裁判一覧情報をCSVで出力します。検索画面の全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputKeijiSaibanListCsv(HttpServletResponse response, AnkenListSearchForm searchForm) throws Exception {

		// ■1.Builderを定義
		Cn0006CsvBuilder cn0006CsvBuilder = new Cn0006CsvBuilder();
		cn0006CsvBuilder.setMessageService(messageService);
		cn0006CsvBuilder.setFileName(Cn0006CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0006CsvBuilder.validDataCountOver(getSaibanListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義、ヘッダー作成
		CsvBuilderDto csvBuilderDto = cn0006CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerRowDto = cn0006CsvBuilder.getHeaderRowDto();
		csvBuilderDto.setHeaderRowDto(headerRowDto);

		// ■4.データをDTOに設定
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();

		// 裁判一覧データ取得
		List<SaibanListCsvDto> saibanList = getSaibanListCsvData(searchForm);
		for (SaibanListCsvDto dto : saibanList) {
			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();
			// 案件ID
			CsvRowItemDto ankenIdItem = new CsvRowItemDto(dto.getAnkenId() == null ? "" : dto.getAnkenId().asLong().toString());
			dataRowItemList.add(ankenIdItem);
			// 分野
			CsvRowItemDto bunyaItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getBunyaName()) ? "" : dto.getBunyaName());
			dataRowItemList.add(bunyaItem);
			// 裁判所
			CsvRowItemDto saibanshoItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getSaibanshoNameMei()) ? "" : dto.getSaibanshoNameMei());
			dataRowItemList.add(saibanshoItem);
			// 事件番号
			CsvRowItemDto jikenNoItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getJikenNumber()) ? "" : dto.getJikenNumber());
			dataRowItemList.add(jikenNoItem);
			// 事件名
			CsvRowItemDto jikenNameItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getJikenName()) ? "" : dto.getJikenName());
			dataRowItemList.add(jikenNameItem);
			// 名前（被告人）
			CsvRowItemDto customerNameItem = new CsvRowItemDto(dto.getCustomerNameSei() == null ? "" : dto.getCustomerNameSei() + CommonConstant.SPACE + dto.getCustomerNameMei()
					+ commonChohyoService.formatOthersCount(dto.getNumberOfCustomer()));
			dataRowItemList.add(customerNameItem);
			// 検察庁
			CsvRowItemDto kensatsuchoNameItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getKensatsuchoNameMei()) ? "" : dto.getKensatsuchoNameMei());
			dataRowItemList.add(kensatsuchoNameItem);
			// 裁判手続き
			CsvRowItemDto saibanStatusItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getSaibanStatusName()) ? "" : dto.getSaibanStatusName());
			dataRowItemList.add(saibanStatusItem);
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
			// 申立日／起訴日
			CsvRowItemDto saibanStartDateItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getSaibanStartDateFormat()) ? "" : dto.getSaibanStartDateFormat());
			dataRowItemList.add(saibanStartDateItem);
			// 終了日／判決日
			CsvRowItemDto saibanEndDateItem = new CsvRowItemDto(StringUtils.isEmpty(dto.getSaibanEndDateFormat()) ? "" : dto.getSaibanEndDateFormat());
			dataRowItemList.add(saibanEndDateItem);

			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
		cn0006CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0006CsvBuilder.makeCsvFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 民事裁判一覧情報をExcelで出力します。検索画面の全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputMinjiSaibanListExcel(HttpServletResponse response, AnkenListSearchForm searchForm) throws Exception {

		// ■1.Builderを定義
		En0015ExcelBuilder en0015excelBuilder = new En0015ExcelBuilder();
		en0015excelBuilder.setConfig(excelConfig);
		en0015excelBuilder.setMessageService(messageService);
		en0015excelBuilder.setFileName(En0015ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0015excelBuilder.validDataCountOver(getSaibanListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		MinjiSaibanListExcelDto saibanListExcelDto = en0015excelBuilder.createNewTargetBuilderDto();

		// ■4.DTOに設定するデータ取得と設定
		saibanListExcelDto.setSelectedAnkenListMenu(searchForm.getSelectedAnkenListMenu());
		saibanListExcelDto.setSaibanListDtoList(getMinjiSaibanListExcelData(searchForm));
		saibanListExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));

		en0015excelBuilder.setSaibanListExcelDto(saibanListExcelDto);
		try {
			// Excelファイルの出力処理
			en0015excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 刑事裁判一覧情報をExcelで出力します。検索画面の全ページング分全て出力。
	 * 
	 * @param response
	 * @param searchForm 検索条件フォーム
	 * @throws Exception
	 */
	public void outputKeijiSaibanListExcel(HttpServletResponse response, AnkenListSearchForm searchForm) throws Exception {

		// ■1.Builderを定義
		En0016ExcelBuilder en0016excelBuilder = new En0016ExcelBuilder();
		en0016excelBuilder.setConfig(excelConfig);
		en0016excelBuilder.setMessageService(messageService);
		en0016excelBuilder.setFileName(En0016ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0016excelBuilder.validDataCountOver(getSaibanListDataCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		KeijiSaibanListExcelDto saibanListExcelDto = en0016excelBuilder.createNewTargetBuilderDto();

		// ■4.DTOに設定するデータ取得と設定
		saibanListExcelDto.setSelectedAnkenListMenu(searchForm.getSelectedAnkenListMenu());
		saibanListExcelDto.setSaibanListDtoList(getKeijiSaibanListExcelData(searchForm));
		saibanListExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));

		en0016excelBuilder.setSaibanListExcelDto(saibanListExcelDto);
		try {
			// Excelファイルの出力処理
			en0016excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 案件一覧データ数を取得する。
	 * 
	 * @param searchForm
	 */
	private int getAnkenListDataCount(AnkenListSearchForm searchForm) {

		// 案件件数を取得
		if (!AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 顧問取引先の案件以外の案件

			// 案件データ件数取得
			if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
				// 通常の検索の場合
				return ankenListDao.selectAnkenListBySearchConditionsCount(
						ankenListCommonService.createAnkenSearchCondition(searchForm),
						ankenListCommonService.createAnkenSortCondition(searchForm));
			} else {
				// クイック検索の場合（「すべて」メニューの場合のみこのケースがあり得る）
				return ankenListDao.selectAnkenListByQuickSearchCount(
						StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()),
						ankenListCommonService.createAnkenSearchCondition(searchForm).getAnkenStatusList(),
						ankenListCommonService.createAnkenSortCondition(searchForm));
			}
		} else {
			
			// 顧問取引先の案件数取得
			return ankenListDao.selectAdvisorAnkenListBySearchConditionsCount(
					ankenListCommonService.createAnkenSearchCondition(searchForm),
					ankenListCommonService.createAnkenSortCondition(searchForm));
		}

	}

	/**
	 * CSV出力用に案件一覧データを取得する。<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<AnkenListCsvDto> getAnkenListCsvData(AnkenListSearchForm searchForm) {
		List<AnkenListBean> ankenListBeanList = null;

		// 一覧表示のソートキーとソート順をviewFormにセット
		if (!AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 顧問取引先の案件以外の案件

			// 案件一覧データ取得
			if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
				// 通常の検索の場合
				ankenListBeanList = ankenListDao.selectAnkenListBySearchConditions(
						ankenListCommonService.createAnkenSearchCondition(searchForm),
						ankenListCommonService.createAnkenSortCondition(searchForm));
			} else {
				// クイック検索の場合（「すべて」メニューの場合のみこのケースがあり得る）
				ankenListBeanList = ankenListDao.selectAnkenListByQuickSearch(
						StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()),
						ankenListCommonService.createAnkenSearchCondition(searchForm).getAnkenStatusList(),
						ankenListCommonService.createAnkenSortCondition(searchForm));
			}
		} else {
			// 顧問取引先の案件

			// 案件一覧データ取得
			ankenListBeanList = ankenListDao.selectAdvisorAnkenListBySearchConditions(
					ankenListCommonService.createAnkenSearchCondition(searchForm),
					ankenListCommonService.createAnkenSortCondition(searchForm));
		}

		// 案件IDに関する担当弁護士、担当事務を取得
		this.setAnkenListTantoLaywerJimu(ankenListBeanList);

		// 案件IDに関する相手方を取得
		this.setAnkenListAitegata(ankenListBeanList);

		// 案件IDに関する担当フラグを取得（ログインユーザが担当する案件かどうか）
		this.setAnkenListTantoAnkenFlg(ankenListBeanList);

		return this.convertAnkenBeanListForDtoList(ankenListBeanList);
	}

	/**
	 * Excel出力用に案件一覧データを取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<ExcelAnkenListRowData> getAnkenListExcelData(AnkenListSearchForm searchForm) {

		List<AnkenListBean> ankenListBeanList = null;
		// 一覧表示のソートキーとソート順をviewFormにセット
		if (!AnkenListMenu.ADVISOR_ANKEN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			// 顧問取引先の案件以外の案件

			// 案件一覧データ取得
			if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
				// 通常の検索の場合
				ankenListBeanList = ankenListDao.selectAnkenListBySearchConditions(
						ankenListCommonService.createAnkenSearchCondition(searchForm),
						ankenListCommonService.createAnkenSortCondition(searchForm));
			} else {
				// クイック検索の場合（「すべて」メニューの場合のみこのケースがあり得る）
				ankenListBeanList = ankenListDao.selectAnkenListByQuickSearch(
						StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()),
						ankenListCommonService.createAnkenSearchCondition(searchForm).getAnkenStatusList(),
						ankenListCommonService.createAnkenSortCondition(searchForm));
			}
		} else {
			// 顧問取引先の案件

			// 案件一覧データ取得
			ankenListBeanList = ankenListDao.selectAdvisorAnkenListBySearchConditions(
					ankenListCommonService.createAnkenSearchCondition(searchForm),
					ankenListCommonService.createAnkenSortCondition(searchForm));
		}

		// 案件IDに関する担当弁護士、担当事務を取得
		this.setAnkenListTantoLaywerJimu(ankenListBeanList);

		// 案件IDに関する相手方を取得
		this.setAnkenListAitegata(ankenListBeanList);

		// 案件IDに関する担当フラグを取得（ログインユーザが担当する案件かどうか）
		this.setAnkenListTantoAnkenFlg(ankenListBeanList);

		return convertAnkenBeanListForExcelDataList(ankenListBeanList);
	}

	/**
	 * 裁判一覧データ数を取得する。
	 * 
	 * @param searchForm
	 */
	private int getSaibanListDataCount(AnkenListSearchForm searchForm) {

		// 裁判データ件数取得
		if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			return ankenListDao.selectMinjiSaibanListBySearchConditionsCount(
					ankenListCommonService.createSaibanSearchCondition(searchForm),
					ankenListCommonService.createSaibanSortCondition(searchForm));
		} else {
			return ankenListDao.selectKeijiSaibanListBySearchConditionsCount(
					ankenListCommonService.createSaibanSearchCondition(searchForm),
					ankenListCommonService.createSaibanSortCondition(searchForm));
		}
	}

	/**
	 * CSV出力用裁判一覧データを取得する。<br>
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<SaibanListCsvDto> getSaibanListCsvData(AnkenListSearchForm searchForm) {

		// 裁判一覧データ取得
		List<SaibanListBean> saibanListBeanList;
		if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			saibanListBeanList = ankenListDao.selectMinjiSaibanListBySearchConditions(
					ankenListCommonService.createSaibanSearchCondition(searchForm),
					ankenListCommonService.createSaibanSortCondition(searchForm));
		} else {
			saibanListBeanList = ankenListDao.selectKeijiSaibanListBySearchConditions(
					ankenListCommonService.createSaibanSearchCondition(searchForm),
					ankenListCommonService.createSaibanSortCondition(searchForm));
		}

		// 裁判に関する担当弁護士、担当事務を取得
		this.setSaibanListTantoLaywerJimu(saibanListBeanList);

		// 裁判に関する相手方を取得（民事裁判の場合）
		if (AnkenListMenu.MINJI_SAIBAN_LIST.equalsByCode(searchForm.getSelectedAnkenListMenu())) {
			this.setSaibanListAitegata(saibanListBeanList);
		}

		// 裁判に関する担当フラグを取得（ログインユーザが担当する案件かどうか）
		this.setSaibanListTantoSaibanFlg(saibanListBeanList);

		// 裁判に関する顧客情報を取得
		this.setSaibanListCustomer(saibanListBeanList);

		return this.convertSaibanBeanListForDtoList(saibanListBeanList);
	}

	/**
	 * EXCEL出力用民事裁判一覧データを取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<ExcelMinjiSaibanListRowData> getMinjiSaibanListExcelData(AnkenListSearchForm searchForm) {

		// 裁判一覧データ取得
		List<SaibanListBean> saibanListBeanList = ankenListDao.selectMinjiSaibanListBySearchConditions(
				ankenListCommonService.createSaibanSearchCondition(searchForm),
				ankenListCommonService.createSaibanSortCondition(searchForm));

		// 裁判に関する担当弁護士、担当事務を取得
		this.setSaibanListTantoLaywerJimu(saibanListBeanList);

		// 裁判に関する相手方を取得（民事裁判の場合）
		this.setSaibanListAitegata(saibanListBeanList);

		// 裁判に関する担当フラグを取得（ログインユーザが担当する案件かどうか）
		this.setSaibanListTantoSaibanFlg(saibanListBeanList);

		// 裁判に関する顧客情報を取得
		this.setSaibanListCustomer(saibanListBeanList);

		return convertMinjiSaibanBeanListForExcelDataList(saibanListBeanList);

	}

	/**
	 * EXCEL出力用刑事裁判一覧データを取得する。
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<ExcelKeijiSaibanListRowData> getKeijiSaibanListExcelData(AnkenListSearchForm searchForm) {

		// 裁判一覧データ取得
		List<SaibanListBean> saibanListBeanList = ankenListDao.selectKeijiSaibanListBySearchConditions(
				ankenListCommonService.createSaibanSearchCondition(searchForm),
				ankenListCommonService.createSaibanSortCondition(searchForm));

		// 裁判に関する担当弁護士、担当事務を取得
		this.setSaibanListTantoLaywerJimu(saibanListBeanList);

		// 裁判に関する担当フラグを取得（ログインユーザが担当する案件かどうか）
		this.setSaibanListTantoSaibanFlg(saibanListBeanList);

		// 裁判に関する顧客情報を取得
		this.setSaibanListCustomer(saibanListBeanList);

		return convertKeijiSaibanBeanListForExcelDataList(saibanListBeanList);

	}

	/**
	 * list<AnkenList>型からlist<ExcelAnkenListRowData>型に変換します
	 * 
	 * @param AnkenListBeanList
	 * @return
	 */
	private List<ExcelAnkenListRowData> convertAnkenBeanListForExcelDataList(List<AnkenListBean> ankenListBeanList) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<ExcelAnkenListRowData> excelAnkenDataList = new ArrayList<ExcelAnkenListRowData>();
		ankenListBeanList.forEach(bean -> {
			excelAnkenDataList.add(convertAnkenBeanForExcelData(bean, bunyaMap));
		});

		return excelAnkenDataList;
	}

	/**
	 * AnkenListBean型からExcelAnkenListRowData型に変換します<br>
	 * 
	 * @param rowData
	 * @param bunyaMap
	 * @return
	 */
	private ExcelAnkenListRowData convertAnkenBeanForExcelData(AnkenListBean rowData, Map<Long, BunyaDto> bunyaMap) {

		ExcelAnkenListRowData excelAnkenListRowData = new ExcelAnkenListRowData();

		// 案件ID
		if (rowData.getAnkenId() != null && !rowData.getAnkenId().asLong().equals(Long.valueOf(0))) {
			excelAnkenListRowData.setAnkenId(rowData.getAnkenId().asLong().toString());
		}

		// 分野
		if (rowData.getBunyaId() != null) {
			excelAnkenListRowData.setBunyaName(bunyaMap.get(rowData.getBunyaId()).getBunyaName());
		}

		// 案件名
		if (rowData.getAnkenId() != null && !rowData.getAnkenId().asLong().equals(Long.valueOf(0))) {
			excelAnkenListRowData.setAnkenName((rowData.getAnkenName() == null || StringUtils.isEmpty(rowData.getAnkenName())) ? "(案件名未入力)" : rowData.getAnkenName());
		}

		// 担当弁護士
		if (!StringUtils.isEmpty(rowData.getTantoLaywerName())) {
			List<String> lawyerTantoNameList = Arrays.asList(rowData.getTantoLaywerName().split(","));
			try {
				excelAnkenListRowData.setTantoLawyerName1(lawyerTantoNameList.get(0).trim());
				excelAnkenListRowData.setTantoLawyerName2(lawyerTantoNameList.get(1).trim());
				excelAnkenListRowData.setTantoLawyerName3(lawyerTantoNameList.get(2).trim());
				excelAnkenListRowData.setTantoLawyerName4(lawyerTantoNameList.get(3).trim());
				excelAnkenListRowData.setTantoLawyerName5(lawyerTantoNameList.get(4).trim());
				excelAnkenListRowData.setTantoLawyerName6(lawyerTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当弁護士が6人に満たない場合。問題無いので処理を継続。
			}
		}

		// 担当事務
		if (!StringUtils.isEmpty(rowData.getTantoJimuName())) {
			List<String> jimuTantoNameList = Arrays.asList(rowData.getTantoJimuName().split(","));
			try {
				excelAnkenListRowData.setTantoJimuName1(jimuTantoNameList.get(0).trim());
				excelAnkenListRowData.setTantoJimuName2(jimuTantoNameList.get(1).trim());
				excelAnkenListRowData.setTantoJimuName3(jimuTantoNameList.get(2).trim());
				excelAnkenListRowData.setTantoJimuName4(jimuTantoNameList.get(3).trim());
				excelAnkenListRowData.setTantoJimuName5(jimuTantoNameList.get(4).trim());
				excelAnkenListRowData.setTantoJimuName6(jimuTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当事務員が6人に満たない場合。問題無いので処理を継続。
			}
		}

		// 顧客名
		if (!StringUtils.isEmpty(rowData.getCustomerNameSei())) {
			excelAnkenListRowData.setCustomerName(rowData.getCustomerNameSei() + CommonConstant.SPACE + rowData.getCustomerNameMei());
		}

		// 相手方
		if (!StringUtils.isEmpty(rowData.getAitegataName())) {
			excelAnkenListRowData.setAitegataName(rowData.getAitegataName());
		}

		// 相手方数
		if (rowData.getNumberOfAitegata() != null && rowData.getNumberOfAitegata() > 1) {
			excelAnkenListRowData.setNumberOfAitegata(commonChohyoService.formatOthersCount(rowData.getNumberOfAitegata()));
		}

		// 進捗
		if (!StringUtils.isEmpty(rowData.getAnkenStatus())) {
			if (CommonConstant.HYPHEN.equals(rowData.getAnkenStatus())) {
				excelAnkenListRowData.setAnkenStatusName(CommonConstant.HYPHEN);
			} else {
				excelAnkenListRowData.setAnkenStatusName(AnkenStatus.of(rowData.getAnkenStatus()).getVal());
			}
		}

		// 受任日
		if (rowData.getJuninDate() != null) {
			excelAnkenListRowData.setJuninDateFormat(DateUtils.parseToString(rowData.getJuninDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		}

		// 登録日
		if (rowData.getAnkenCreatedDate() != null) {
			excelAnkenListRowData.setAnkenCreatedDateFormat(DateUtils.parseToString(rowData.getAnkenCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		}

		return excelAnkenListRowData;
	}

	/**
	 * 民事裁判データのlist<SaibanList>型からlist<ExcelSaibanListRowData>型に変換します
	 * 
	 * @param AnkenListBeanList
	 * @return
	 */
	private List<ExcelMinjiSaibanListRowData> convertMinjiSaibanBeanListForExcelDataList(List<SaibanListBean> saibanListBeanList) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<ExcelMinjiSaibanListRowData> excelSaibanDataList = new ArrayList<ExcelMinjiSaibanListRowData>();
		saibanListBeanList.forEach(bean -> {
			excelSaibanDataList.add(convertMinjiSaibanBeanForExcelData(bean, bunyaMap));
		});

		return excelSaibanDataList;
	}

	/**
	 * SaibanListBean型からExcelMinjiSaibanListRowData型に変換します<br>
	 * 
	 * @param rowData
	 * @param bunyaMap
	 * @return
	 */
	private ExcelMinjiSaibanListRowData convertMinjiSaibanBeanForExcelData(SaibanListBean rowData, Map<Long, BunyaDto> bunyaMap) {

		ExcelMinjiSaibanListRowData excelSaibanListRowData = new ExcelMinjiSaibanListRowData();

		// 案件ID
		if (rowData.getAnkenId() != null && !rowData.getAnkenId().asLong().equals(Long.valueOf(0))) {
			excelSaibanListRowData.setAnkenId(rowData.getAnkenId().asLong().toString());
		}

		// 分野
		if (rowData.getBunyaId() != null) {
			excelSaibanListRowData.setBunyaName(bunyaMap.get(rowData.getBunyaId()).getBunyaName());
		}

		// 裁判所
		if (!StringUtils.isEmpty(rowData.getSaibanshoNameMei())) {
			excelSaibanListRowData.setSaibanshoNameMei(rowData.getSaibanshoNameMei());
		}

		// 事件番号
		if (!StringUtils.isEmpty(rowData.getJikenYear()) || !StringUtils.isEmpty(rowData.getJikenMark()) || !StringUtils.isEmpty(rowData.getJikenNo())) {
			excelSaibanListRowData.setJikenNumber(CaseNumber.of(EraType.of(rowData.getJikenGengo()), rowData.getJikenYear(), rowData.getJikenMark(), rowData.getJikenNo()).toString());
		}

		// 事件名
		if (!StringUtils.isEmpty(rowData.getJikenName())) {
			excelSaibanListRowData.setJikenName(rowData.getJikenName());
		}

		// 担当弁護士１
		if (!StringUtils.isEmpty(rowData.getTantoLaywerName())) {
			List<String> lawyerTantoNameList = Arrays.asList(rowData.getTantoLaywerName().split(","));
			try {
				excelSaibanListRowData.setTantoLawyerName1(lawyerTantoNameList.get(0).trim());
				excelSaibanListRowData.setTantoLawyerName2(lawyerTantoNameList.get(1).trim());
				excelSaibanListRowData.setTantoLawyerName3(lawyerTantoNameList.get(2).trim());
				excelSaibanListRowData.setTantoLawyerName4(lawyerTantoNameList.get(3).trim());
				excelSaibanListRowData.setTantoLawyerName5(lawyerTantoNameList.get(4).trim());
				excelSaibanListRowData.setTantoLawyerName6(lawyerTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当弁護士が6人に満たない場合。問題無いので処理を継続。
			}
		}

		// 担当事務
		if (!StringUtils.isEmpty(rowData.getTantoJimuName())) {
			List<String> jimuTantoNameList = Arrays.asList(rowData.getTantoJimuName().split(","));
			try {
				excelSaibanListRowData.setTantoJimuName1(jimuTantoNameList.get(0).trim());
				excelSaibanListRowData.setTantoJimuName2(jimuTantoNameList.get(1).trim());
				excelSaibanListRowData.setTantoJimuName3(jimuTantoNameList.get(2).trim());
				excelSaibanListRowData.setTantoJimuName4(jimuTantoNameList.get(3).trim());
				excelSaibanListRowData.setTantoJimuName5(jimuTantoNameList.get(4).trim());
				excelSaibanListRowData.setTantoJimuName6(jimuTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当事務員が6人に満たない場合。問題無いので処理を継続。
			}
		}

		// 名前（当事者）
		if (!StringUtils.isEmpty(rowData.getCustomerNameSei())) {
			excelSaibanListRowData.setCustomerName(rowData.getCustomerNameSei() + CommonConstant.SPACE + rowData.getCustomerNameMei());
		}

		// 顧客数
		if (rowData.getNumberOfCustomer() != null && rowData.getNumberOfCustomer() > 1) {
			excelSaibanListRowData.setNumberOfCustomer(commonChohyoService.formatOthersCount(rowData.getNumberOfCustomer()));
		}

		// 相手方
		if (!StringUtils.isEmpty(rowData.getAitegataName())) {
			excelSaibanListRowData.setAitegataName(rowData.getAitegataName());
		}

		// 相手方数
		if (rowData.getNumberOfAitegata() != null && rowData.getNumberOfAitegata() > 1) {
			excelSaibanListRowData.setNumberOfAitegata(commonChohyoService.formatOthersCount(rowData.getNumberOfAitegata()));
		}

		// 裁判手続き
		if (!StringUtils.isEmpty(rowData.getSaibanStatus())) {
			if (CommonConstant.HYPHEN.equals(rowData.getSaibanStatus())) {
				excelSaibanListRowData.setSaibanStatusName(CommonConstant.HYPHEN);
			} else {
				excelSaibanListRowData.setSaibanStatusName(SaibanStatus.of(rowData.getSaibanStatus()).getVal());
			}
		}

		// 申立日
		if (rowData.getSaibanStartDate() != null) {
			excelSaibanListRowData.setSaibanStartDateFormat(DateUtils.parseToString(rowData.getSaibanStartDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		}

		// 終了日
		if (rowData.getSaibanEndDate() != null) {
			excelSaibanListRowData.setSaibanEndDateFormat(DateUtils.parseToString(rowData.getSaibanEndDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		}

		return excelSaibanListRowData;
	}

	/**
	 * 刑事裁判データのlist<SaibanList>型からlist<ExcelSaibanListRowData>型に変換します
	 * 
	 * @param AnkenListBeanList
	 * @return
	 */
	private List<ExcelKeijiSaibanListRowData> convertKeijiSaibanBeanListForExcelDataList(List<SaibanListBean> saibanListBeanList) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<ExcelKeijiSaibanListRowData> excelSaibanDataList = new ArrayList<ExcelKeijiSaibanListRowData>();
		saibanListBeanList.forEach(bean -> {
			excelSaibanDataList.add(convertKeijiSaibanBeanForExcelData(bean, bunyaMap));
		});

		return excelSaibanDataList;
	}

	/**
	 * SaibanListBean型からExcelKeijiSaibanListRowData型に変換します<br>
	 * 
	 * @param rowData
	 * @param bunyaMap
	 * @return
	 */
	private ExcelKeijiSaibanListRowData convertKeijiSaibanBeanForExcelData(SaibanListBean rowData, Map<Long, BunyaDto> bunyaMap) {

		ExcelKeijiSaibanListRowData excelSaibanListRowData = new ExcelKeijiSaibanListRowData();

		// 案件ID
		if (rowData.getAnkenId() != null && !rowData.getAnkenId().asLong().equals(Long.valueOf(0))) {
			excelSaibanListRowData.setAnkenId(rowData.getAnkenId().asLong().toString());
		}

		// 分野
		if (rowData.getBunyaId() != null) {
			excelSaibanListRowData.setBunyaName(bunyaMap.get(rowData.getBunyaId()).getBunyaName());
		}

		// 裁判所
		if (!StringUtils.isEmpty(rowData.getSaibanshoNameMei())) {
			excelSaibanListRowData.setSaibanshoNameMei(rowData.getSaibanshoNameMei());
		}

		// 事件番号
		if (!StringUtils.isEmpty(rowData.getJikenYear()) || !StringUtils.isEmpty(rowData.getJikenMark()) || !StringUtils.isEmpty(rowData.getJikenNo())) {
			excelSaibanListRowData.setJikenNumber(CaseNumber.of(EraType.of(rowData.getJikenGengo()), rowData.getJikenYear(), rowData.getJikenMark(), rowData.getJikenNo()).toString());
		}

		// 事件名
		if (!StringUtils.isEmpty(rowData.getJikenName())) {
			excelSaibanListRowData.setJikenName(rowData.getJikenName());
		}

		// 担当弁護士
		if (!StringUtils.isEmpty(rowData.getTantoLaywerName())) {
			List<String> lawyerTantoNameList = Arrays.asList(rowData.getTantoLaywerName().split(","));
			try {
				excelSaibanListRowData.setTantoLawyerName1(lawyerTantoNameList.get(0).trim());
				excelSaibanListRowData.setTantoLawyerName2(lawyerTantoNameList.get(1).trim());
				excelSaibanListRowData.setTantoLawyerName3(lawyerTantoNameList.get(2).trim());
				excelSaibanListRowData.setTantoLawyerName4(lawyerTantoNameList.get(3).trim());
				excelSaibanListRowData.setTantoLawyerName5(lawyerTantoNameList.get(4).trim());
				excelSaibanListRowData.setTantoLawyerName6(lawyerTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当弁護士が6人に満たない場合。問題無いので処理を継続。
			}
		}

		// 担当事務
		if (!StringUtils.isEmpty(rowData.getTantoJimuName())) {
			List<String> jimuTantoNameList = Arrays.asList(rowData.getTantoJimuName().split(","));
			try {
				excelSaibanListRowData.setTantoJimuName1(jimuTantoNameList.get(0).trim());
				excelSaibanListRowData.setTantoJimuName2(jimuTantoNameList.get(1).trim());
				excelSaibanListRowData.setTantoJimuName3(jimuTantoNameList.get(2).trim());
				excelSaibanListRowData.setTantoJimuName4(jimuTantoNameList.get(3).trim());
				excelSaibanListRowData.setTantoJimuName5(jimuTantoNameList.get(4).trim());
				excelSaibanListRowData.setTantoJimuName6(jimuTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当事務員が6人に満たない場合。問題無いので処理を継続。
			}
		}

		// 名前（被告人）
		if (!StringUtils.isEmpty(rowData.getCustomerNameSei())) {
			excelSaibanListRowData.setCustomerName(rowData.getCustomerNameSei() + CommonConstant.SPACE + rowData.getCustomerNameMei());
		}

		// 顧客数
		if (rowData.getNumberOfCustomer() != null && rowData.getNumberOfCustomer() > 1) {
			excelSaibanListRowData.setNumberOfCustomer(commonChohyoService.formatOthersCount(rowData.getNumberOfCustomer()));
		}

		// 検察庁
		if (!StringUtils.isEmpty(rowData.getKensatsuchoNameMei())) {
			excelSaibanListRowData.setKensatsuchoNameMei(rowData.getKensatsuchoNameMei());
		}

		// 裁判手続き
		if (!StringUtils.isEmpty(rowData.getSaibanStatus())) {
			if (CommonConstant.HYPHEN.equals(rowData.getSaibanStatus())) {
				excelSaibanListRowData.setSaibanStatusName(CommonConstant.HYPHEN);
			} else {
				excelSaibanListRowData.setSaibanStatusName(SaibanStatus.of(rowData.getSaibanStatus()).getVal());
			}
		}

		// 起訴日／上訴日
		if (rowData.getSaibanStartDate() != null) {
			excelSaibanListRowData.setSaibanStartDateFormat(DateUtils.parseToString(rowData.getSaibanStartDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		}

		// 判決日
		if (rowData.getSaibanEndDate() != null) {
			excelSaibanListRowData.setSaibanEndDateFormat(DateUtils.parseToString(rowData.getSaibanEndDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		}

		return excelSaibanListRowData;
	}

	/**
	 * AnkenList<Bean>型からAnkenList<Dto>型に変換します
	 * 
	 * @param AnkenListBeanList
	 * @return
	 */
	private List<AnkenListCsvDto> convertAnkenBeanListForDtoList(List<AnkenListBean> AnkenListBeanList) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<AnkenListCsvDto> ankenListCsvDtoList = new ArrayList<AnkenListCsvDto>();
		AnkenListBeanList.forEach(bean -> {
			ankenListCsvDtoList.add(convertAnkenBeanForDto(bean, bunyaMap));
		});
		return ankenListCsvDtoList;
	}

	/**
	 * AnkenListBean型からAnkenListCsvDto型に変換します<br>
	 * 
	 * @param ankenListBean
	 * @param bunyaMap
	 * @param lawyerColumMaxSize
	 * @param jimuColumMaxSize
	 * @return
	 */
	private AnkenListCsvDto convertAnkenBeanForDto(AnkenListBean ankenListBean, Map<Long, BunyaDto> bunyaMap) {

		AnkenListCsvDto ankenListCsvDto = new AnkenListCsvDto();

		ankenListCsvDto.setAitegataName(ankenListBean.getAitegataName());
		ankenListCsvDto.setAnkenCreatedDate(ankenListBean.getAnkenCreatedDate());
		ankenListCsvDto.setAnkenId(ankenListBean.getAnkenId());
		ankenListCsvDto.setAnkenName(ankenListBean.getAnkenName());
		ankenListCsvDto.setAnkenStatus(ankenListBean.getAnkenStatus());
		ankenListCsvDto.setBunyaId(ankenListBean.getBunyaId());
		ankenListCsvDto.setBunya(bunyaMap.get(ankenListBean.getBunyaId()));
		ankenListCsvDto.setCustomerCreatedDate(ankenListBean.getCustomerCreatedDate());
		ankenListCsvDto.setPersonId(ankenListBean.getPersonId());
		ankenListCsvDto.setCustomerId(ankenListBean.getCustomerId());
		ankenListCsvDto.setCustomerNameMei(ankenListBean.getCustomerNameMei());
		ankenListCsvDto.setCustomerNameMeiKana(ankenListBean.getCustomerNameMeiKana());
		ankenListCsvDto.setCustomerNameSei(ankenListBean.getCustomerNameSei());
		ankenListCsvDto.setCustomerNameSeiKana(ankenListBean.getCustomerNameSeiKana());
		ankenListCsvDto.setJuninDate(ankenListBean.getJuninDate());
		ankenListCsvDto.setNumberOfAitegata(ankenListBean.getNumberOfAitegata());
		ankenListCsvDto.setTantoAnkenFlg(ankenListBean.getTantoAnkenFlg());

		// 担当弁護士
		if (StringUtils.isNotEmpty(ankenListBean.getTantoLaywerName())) {
			List<String> lawyerTantoNameList = Arrays.asList(ankenListBean.getTantoLaywerName().split(","));
			try {
				ankenListCsvDto.setTantoLawyerName1(lawyerTantoNameList.get(0).trim());
				ankenListCsvDto.setTantoLawyerName2(lawyerTantoNameList.get(1).trim());
				ankenListCsvDto.setTantoLawyerName3(lawyerTantoNameList.get(2).trim());
				ankenListCsvDto.setTantoLawyerName4(lawyerTantoNameList.get(3).trim());
				ankenListCsvDto.setTantoLawyerName5(lawyerTantoNameList.get(4).trim());
				ankenListCsvDto.setTantoLawyerName6(lawyerTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当弁護士が6人に満たない場合。問題無いので処理を継続。
			}
		}

		// 担当事務
		if (StringUtils.isNotEmpty(ankenListBean.getTantoJimuName())) {
			List<String> jimuTantoNameList = Arrays.asList(ankenListBean.getTantoJimuName().split(","));
			try {
				ankenListCsvDto.setTantoJimuName1(jimuTantoNameList.get(0).trim());
				ankenListCsvDto.setTantoJimuName2(jimuTantoNameList.get(1).trim());
				ankenListCsvDto.setTantoJimuName3(jimuTantoNameList.get(2).trim());
				ankenListCsvDto.setTantoJimuName4(jimuTantoNameList.get(3).trim());
				ankenListCsvDto.setTantoJimuName5(jimuTantoNameList.get(4).trim());
				ankenListCsvDto.setTantoJimuName6(jimuTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当事務員が6人に満たない場合。問題無いので処理を継続。
			}
		}

		return ankenListCsvDto;
	}
	/**
	 * SaibanList<Bean>型からSaibanListCsv<Dto>型に変換します
	 * 
	 * @param AnkenListBeanList
	 * @return
	 */
	private List<SaibanListCsvDto> convertSaibanBeanListForDtoList(List<SaibanListBean> SaibanListBeanList) {

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// Bean型をDto型に変換する
		List<SaibanListCsvDto> saibanListCsvDtoList = new ArrayList<SaibanListCsvDto>();
		SaibanListBeanList.forEach(bean -> {
			saibanListCsvDtoList.add(convertSaibanBeanForDto(bean, bunyaMap));
		});
		return saibanListCsvDtoList;
	}

	/**
	 * SaibanListBean型からSaibanListDto型に変換します<br>
	 * 
	 * @param ankenListBean
	 * @return
	 */
	private SaibanListCsvDto convertSaibanBeanForDto(SaibanListBean saibanListBean, Map<Long, BunyaDto> bunyaMap) {

		SaibanListCsvDto saibanListCsvDto = new SaibanListCsvDto();

		saibanListCsvDto.setSaibanSeq(saibanListBean.getSaibanSeq());
		saibanListCsvDto.setAnkenId(saibanListBean.getAnkenId());
		saibanListCsvDto.setAnkenName(saibanListBean.getAnkenName());
		saibanListCsvDto.setBunyaId(saibanListBean.getBunyaId());
		saibanListCsvDto.setBunya(bunyaMap.get(saibanListBean.getBunyaId()));
		saibanListCsvDto.setSaibanshoNameMei(saibanListBean.getSaibanshoNameMei());
		saibanListCsvDto.setJikenSeq(saibanListBean.getJikenSeq());
		saibanListCsvDto.setJikenGengo(saibanListBean.getJikenGengo());
		saibanListCsvDto.setJikenYear(saibanListBean.getJikenYear());
		saibanListCsvDto.setJikenMark(saibanListBean.getJikenMark());
		saibanListCsvDto.setJikenNo(saibanListBean.getJikenNo());
		saibanListCsvDto.setJikenName(saibanListBean.getJikenName());
		saibanListCsvDto.setPersonId(saibanListBean.getPersonId());
		saibanListCsvDto.setCustomerId(saibanListBean.getCustomerId());
		saibanListCsvDto.setCustomerNameSei(saibanListBean.getCustomerNameSei());
		saibanListCsvDto.setCustomerNameSeiKana(saibanListBean.getCustomerNameSeiKana());
		saibanListCsvDto.setCustomerNameMei(saibanListBean.getCustomerNameMei());
		saibanListCsvDto.setCustomerNameMeiKana(saibanListBean.getCustomerNameMeiKana());
		saibanListCsvDto.setNumberOfCustomer(saibanListBean.getNumberOfCustomer());
		saibanListCsvDto.setAitegataName(saibanListBean.getAitegataName());
		saibanListCsvDto.setNumberOfAitegata(saibanListBean.getNumberOfAitegata());
		saibanListCsvDto.setSaibanStatus(saibanListBean.getSaibanStatus());
		saibanListCsvDto.setKensatsuchoNameMei(saibanListBean.getKensatsuchoNameMei());

		// 担当弁護士
		if (StringUtils.isNotEmpty(saibanListBean.getTantoLaywerName())) {
			List<String> lawyerTantoNameList = Arrays.asList(saibanListBean.getTantoLaywerName().split(","));
			try {
				saibanListCsvDto.setTantoLawyerName1(lawyerTantoNameList.get(0).trim());
				saibanListCsvDto.setTantoLawyerName2(lawyerTantoNameList.get(1).trim());
				saibanListCsvDto.setTantoLawyerName3(lawyerTantoNameList.get(2).trim());
				saibanListCsvDto.setTantoLawyerName4(lawyerTantoNameList.get(3).trim());
				saibanListCsvDto.setTantoLawyerName5(lawyerTantoNameList.get(4).trim());
				saibanListCsvDto.setTantoLawyerName6(lawyerTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当弁護士が6人に満たない場合。問題無いので処理を継続。
			}
		}

		// 担当事務
		if (StringUtils.isNotEmpty(saibanListBean.getTantoJimuName())) {
			List<String> jimuTantoNameList = Arrays.asList(saibanListBean.getTantoJimuName().split(","));
			try {
				saibanListCsvDto.setTantoJimuName1(jimuTantoNameList.get(0).trim());
				saibanListCsvDto.setTantoJimuName2(jimuTantoNameList.get(1).trim());
				saibanListCsvDto.setTantoJimuName3(jimuTantoNameList.get(2).trim());
				saibanListCsvDto.setTantoJimuName4(jimuTantoNameList.get(3).trim());
				saibanListCsvDto.setTantoJimuName5(jimuTantoNameList.get(4).trim());
				saibanListCsvDto.setTantoJimuName6(jimuTantoNameList.get(5).trim());
			} catch (ArrayIndexOutOfBoundsException e) {
				// 担当事務員が6人に満たない場合。問題無いので処理を継続。
			}
		}

		saibanListCsvDto.setSaibanStartDate(saibanListBean.getSaibanStartDate());
		saibanListCsvDto.setSaibanEndDate(saibanListBean.getSaibanEndDate());
		saibanListCsvDto.setTantoAnkenFlg(saibanListBean.getTantoSaibanFlg());

		return saibanListCsvDto;
	}

	/**
	 * 案件に関する担当弁護士、担当事務をセットします。
	 * 
	 * @param ankenList
	 */
	private void setAnkenListTantoLaywerJimu(List<AnkenListBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenTantoLawyerJimuBean> tantoLaywerJimuBeanList = tAnkenTantoDao.selectAnkenTantoLaywerJimuById(ankenIdList);
		for (AnkenListBean ankenListBean : ankenList) {
			// 担当弁護士の担当者名
			Optional<AnkenTantoLawyerJimuBean> lawyerOptBean = tantoLaywerJimuBeanList.stream().filter(tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(ankenListBean.getAnkenId().asLong()) && TantoType.LAWYER.equalsByCode(tantoLaywerJimuBean.getTantoType())).findFirst();
			if (lawyerOptBean.isPresent()) {
				ankenListBean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			// 担当事務の担当者名
			Optional<AnkenTantoLawyerJimuBean> jimuOptBean = tantoLaywerJimuBeanList.stream().filter(tantoLaywerJimuBean -> tantoLaywerJimuBean.getAnkenId().asLong().equals(ankenListBean.getAnkenId().asLong()) && TantoType.JIMU.equalsByCode(tantoLaywerJimuBean.getTantoType())).findFirst();
			if (jimuOptBean.isPresent()) {
				ankenListBean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * 案件に関する相手方情報をセットします。
	 * 
	 * @param ankenList
	 */
	private void setAnkenListAitegata(List<AnkenListBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenAitegataCntBean> aitegataBeanList = ankenListDao.selectAnkenAitegataByAnkenId(ankenIdList);
		for (AnkenListBean ankenListBean : ankenList) {
			Optional<AnkenAitegataCntBean> optBean = aitegataBeanList.stream().filter(aitegataBean -> aitegataBean.getAnkenId().asLong().equals(ankenListBean.getAnkenId().asLong())).findFirst();
			if (optBean.isPresent()) {
				ankenListBean.setAitegataName(optBean.get().getAitegataName());
				ankenListBean.setNumberOfAitegata(optBean.get().getNumberOfAitegata());
			}
		}
	}

	/**
	 * ログインユーザが案件の担当かフラグをセットします。
	 * 
	 * @param ankenList
	 */
	private void setAnkenListTantoAnkenFlg(List<AnkenListBean> ankenList) {
		if (LoiozCollectionUtils.isEmpty(ankenList)) {
			return;
		}

		List<Long> ankenIdList = ankenList.stream().map(bean -> bean.getAnkenId().asLong()).collect(Collectors.toList());
		List<AnkenTantoFlgBean> ankenTantoFlgBeanList = ankenListDao.selectTantoAnkenFlgById(SessionUtils.getLoginAccountSeq(), ankenIdList);
		for (AnkenListBean ankenListBean : ankenList) {
			Optional<AnkenTantoFlgBean> optBean = ankenTantoFlgBeanList.stream().filter(ankenTantoFlgBean -> ankenTantoFlgBean.getAnkenId().asLong().equals(ankenListBean.getAnkenId().asLong())).findFirst();
			if (optBean.isPresent()) {
				ankenListBean.setTantoAnkenFlg(optBean.get().getTantoAnkenFlg());
			}
		}
	}

	/**
	 * 裁判に関する担当弁護士、担当事務をセットします。
	 * 
	 * @param ankenList
	 */
	public void setSaibanListTantoLaywerJimu(List<SaibanListBean> saibanList) {
		if (LoiozCollectionUtils.isEmpty(saibanList)) {
			return;
		}

		List<Long> saibanSeqList = saibanList.stream().map(bean -> bean.getSaibanSeq()).collect(Collectors.toList());
		List<SaibanTantoLawyerJimuBean> tantoLawyerJimuBeanList = tSaibanTantoDao.selectSaibanTantoLaywerJimuBySeq(saibanSeqList);
		for (SaibanListBean saibanListBean : saibanList) {
			Optional<SaibanTantoLawyerJimuBean> lawyerOptBean = tantoLawyerJimuBeanList.stream().filter(tantoLawyerJimuBean -> tantoLawyerJimuBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq()) && TantoType.LAWYER.equalsByCode(tantoLawyerJimuBean.getTantoType())).findFirst();
			if (lawyerOptBean.isPresent()) {
				saibanListBean.setTantoLaywerName(lawyerOptBean.get().getTantoName());
			}
			Optional<SaibanTantoLawyerJimuBean> jimuOptBean = tantoLawyerJimuBeanList.stream().filter(tantoLawyerJimuBean -> tantoLawyerJimuBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq()) && TantoType.JIMU.equalsByCode(tantoLawyerJimuBean.getTantoType())).findFirst();
			if (jimuOptBean.isPresent()) {
				saibanListBean.setTantoJimuName(jimuOptBean.get().getTantoName());
			}
		}
	}

	/**
	 * 裁判に関する相手方情報をセットします。
	 * 
	 * @param ankenList
	 */
	private void setSaibanListAitegata(List<SaibanListBean> saibanList) {
		if (LoiozCollectionUtils.isEmpty(saibanList)) {
			return;
		}

		List<Long> saibanSeqList = saibanList.stream().map(bean -> bean.getSaibanSeq()).collect(Collectors.toList());
		List<SaibanAitegataBean> aitegataBeanList = ankenListDao.selectSaibanAitegataBySaibanSeq(saibanSeqList);
		for (SaibanListBean saibanListBean : saibanList) {
			Optional<SaibanAitegataBean> optBean = aitegataBeanList.stream().filter(aitegataBean -> aitegataBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq())).findFirst();
			if (optBean.isPresent()) {
				saibanListBean.setAitegataName(optBean.get().getAitegataName());
				saibanListBean.setNumberOfAitegata(optBean.get().getNumberOfAitegata());
			}
		}
	}

	/**
	 * ログインユーザが裁判の担当かフラグをセットします。
	 * 
	 * @param ankenList
	 */
	private void setSaibanListTantoSaibanFlg(List<SaibanListBean> saibanList) {
		if (LoiozCollectionUtils.isEmpty(saibanList)) {
			return;
		}

		List<Long> saibanSeqList = saibanList.stream().map(bean -> bean.getSaibanSeq()).collect(Collectors.toList());
		List<SaibanTantoFlgBean> saibanTantoFlgBeanList = ankenListDao.selectTantoSaibanFlgBySeq(SessionUtils.getLoginAccountSeq(), saibanSeqList);
		for (SaibanListBean saibanListBean : saibanList) {
			Optional<SaibanTantoFlgBean> optBean = saibanTantoFlgBeanList.stream().filter(ankenTantoFlgBean -> ankenTantoFlgBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq())).findFirst();
			if (optBean.isPresent()) {
				saibanListBean.setTantoSaibanFlg(optBean.get().getTantoSaibanFlg());
			}
		}
	}

	/**
	 * 裁判に関する顧客情報(1名分）と紐づけられている顧客数をセットします。<br>
	 * 顧客の中で筆頭が設定してあれば筆頭の顧客情報をセットします。<br>
	 * 筆頭が設定していなければ最初に登録された顧客情報をセットします。
	 * 
	 * @param ankenList
	 */
	private void setSaibanListCustomer(List<SaibanListBean> saibanList) {
		if (LoiozCollectionUtils.isEmpty(saibanList)) {
			return;
		}

		List<Long> saibanSeqList = saibanList.stream().map(bean -> bean.getSaibanSeq()).collect(Collectors.toList());
		List<SaibanSeqCustomerBean> customerBeanList = ankenListDao.selectSaibanCustomerBySeq(saibanSeqList);
		for (SaibanListBean saibanListBean : saibanList) {
			Optional<SaibanSeqCustomerBean> optBean = customerBeanList.stream().filter(
					aitegataBean -> aitegataBean.getSaibanSeq().equals(saibanListBean.getSaibanSeq())).findFirst();
			if (optBean.isPresent()) {
				saibanListBean.setPersonId(optBean.get().getPersonId());
				saibanListBean.setCustomerId(optBean.get().getCustomerId());
				saibanListBean.setCustomerNameMei(optBean.get().getCustomerNameMei());
				saibanListBean.setCustomerNameMeiKana(optBean.get().getCustomerNameMeiKana());
				saibanListBean.setCustomerNameSei(optBean.get().getCustomerNameSei());
				saibanListBean.setCustomerNameSeiKana(optBean.get().getCustomerNameSeiKana());
				saibanListBean.setNumberOfCustomer(optBean.get().getNumberOfCustomer());
			}
		}
	}
}