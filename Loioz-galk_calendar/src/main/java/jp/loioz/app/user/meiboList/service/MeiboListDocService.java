package jp.loioz.app.user.meiboList.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.csv.builder.Cn0002CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0003CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0004CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0007CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0008CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0009CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0010CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0011CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0012CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0013CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0014CsvBuilder;
import jp.loioz.app.common.csv.builder.Cn0015CsvBuilder;
import jp.loioz.app.common.csv.dto.CsvBuilderDto;
import jp.loioz.app.common.csv.dto.CsvRowDto;
import jp.loioz.app.common.csv.dto.CsvRowItemDto;
import jp.loioz.app.common.excel.builder.naibu.En0012ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0013ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0014ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0017ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0018ExcelBuilder;
import jp.loioz.app.common.excel.builder.naibu.En0019ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.AdvisorMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.AdvisorMeiboListExcelDto.ExcelAdvisorMeiboListRowData;
import jp.loioz.app.common.excel.dto.AllMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.AllMeiboListExcelDto.ExcelAllMeiboListRowData;
import jp.loioz.app.common.excel.dto.BengoshiMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.BengoshiMeiboListExcelDto.ExcelBengoshiMeiboListRowData;
import jp.loioz.app.common.excel.dto.CustomerAllMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.CustomerAllMeiboListExcelDto.ExcelCustomerAllMeiboListRowData;
import jp.loioz.app.common.excel.dto.CustomerHojinMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.CustomerHojinMeiboListExcelDto.ExcelCustomerHojinMeiboListRowData;
import jp.loioz.app.common.excel.dto.CustomerKojinMeiboListExcelDto;
import jp.loioz.app.common.excel.dto.CustomerKojinMeiboListExcelDto.ExcelCustomerKojinMeiboListRowData;
import jp.loioz.app.user.meiboList.dto.AdvisorMeiboListDto;
import jp.loioz.app.user.meiboList.dto.AllMeiboListDto;
import jp.loioz.app.user.meiboList.dto.BengoshiMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerAllMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerHojinMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerKojinMeiboListDto;
import jp.loioz.app.user.meiboList.dto.FudemameCsvDto;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm.AllMeiboListSearchForm;
import jp.loioz.bean.meiboList.AdvisorMeiboListBean;
import jp.loioz.bean.meiboList.AllMeiboListBean;
import jp.loioz.bean.meiboList.BengoshiMeiboListBean;
import jp.loioz.bean.meiboList.CustomerAllMeiboListBean;
import jp.loioz.bean.meiboList.CustomerHojinMeiboListBean;
import jp.loioz.bean.meiboList.CustomerKojinMeiboListBean;
import jp.loioz.bean.meiboList.FudemameAdvisorMeiboListBean;
import jp.loioz.bean.meiboList.FudemameAllMeiboListBean;
import jp.loioz.bean.meiboList.FudemameBengoshiMeiboListBean;
import jp.loioz.bean.meiboList.FudemameCustomerAllMeiboListBean;
import jp.loioz.bean.meiboList.FudemameCustomerHojinMeiboListBean;
import jp.loioz.bean.meiboList.FudemameCustomerKojinMeiboListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.Gender;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.log.Logger;
import jp.loioz.common.service.message.MessageService;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MeiboListDao;
import jp.loioz.domain.condition.meiboList.AdvisorMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.AdvisorMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.AllMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.AllMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.BengoshiMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.BengoshiMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerAllMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerAllMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerHojinMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerHojinMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerKojinMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerKojinMeiboListSortCondition;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonName;

/**
 * 名簿一覧出力のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MeiboListDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** メッセージサービス */
	@Autowired
	private MessageService messageService;

	/** メッセージ一覧Daoクラス */
	@Autowired
	private MeiboListDao meiboListDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 名簿一覧：すべてのCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputAllMeiboListCsv(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0002CsvBuilder cn0002CsvBuilder = new Cn0002CsvBuilder();
		cn0002CsvBuilder.setMessageService(messageService);
		cn0002CsvBuilder.setFileName(Cn0002CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0002CsvBuilder.validDataCountOver(this.getAllMeiboListCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0002CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headeCsvRowDto = cn0002CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headeCsvRowDto);

		List<AllMeiboListDto> allMeiboListDtoList = this.getAllMeiboListCsvData(searchForm);
		this.setAllMeiboCsvBuilderDto(csvBuilderDto, allMeiboListDtoList);
		cn0002CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0002CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：すべてのExcel出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputAllMeiboListExcel(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		En0012ExcelBuilder en0012ExcelBuilder = new En0012ExcelBuilder();
		en0012ExcelBuilder.setConfig(excelConfig);
		en0012ExcelBuilder.setMessageService(messageService);
		en0012ExcelBuilder.setFileName(En0012ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0012ExcelBuilder.validDataCountOver(this.getAllMeiboListCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		AllMeiboListExcelDto allMeiboListExcelDto = new AllMeiboListExcelDto();
		allMeiboListExcelDto.setAllMeiboListDtoList(this.getAllMeiboListExcelData(searchForm));

		en0012ExcelBuilder.setAllMeiboListExcelDto(allMeiboListExcelDto);

		// ■4.DTOに設定するデータ取得と設定
		try {
			// Excelファイルの出力処理
			en0012ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：すべてのCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputAllMeiboListFudemame(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0010CsvBuilder cn0010CsvBuilder = new Cn0010CsvBuilder();
		cn0010CsvBuilder.setMessageService(messageService);
		cn0010CsvBuilder.setFileName(Cn0010CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0010CsvBuilder.validDataCountOver(this.getAllMeiboFudemameListCount(searchForm), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0010CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headeCsvRowDto = cn0010CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headeCsvRowDto);

		List<FudemameCsvDto> fudemameCsvDtoList = this.getAllMeiboListFudemameData(searchForm);
		this.setAllMeiboFudemameBuilderDto(csvBuilderDto, fudemameCsvDtoList);
		cn0010CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0010CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：顧客のCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerAllMeiboListCsv(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0008CsvBuilder cn0008CsvBuilder = new Cn0008CsvBuilder();
		cn0008CsvBuilder.setMessageService(messageService);
		cn0008CsvBuilder.setFileName(Cn0008CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0008CsvBuilder.validDataCountOver(this.getCustomerAllMeiboListCount(searchForm.getCustomerAllMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0008CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0008CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<CustomerAllMeiboListDto> customerAllMeiboListDtoList = this.getCustomerAllMeiboListCsvData(searchForm.getCustomerAllMeiboListSearchForm());
		this.setCustomerAllMeiboCsvBuilderDto(csvBuilderDto, customerAllMeiboListDtoList);
		cn0008CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0008CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：顧客のExcel出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerAllMeiboListExcel(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		En0018ExcelBuilder en0018ExcelBuilder = new En0018ExcelBuilder();
		en0018ExcelBuilder.setConfig(excelConfig);
		en0018ExcelBuilder.setMessageService(messageService);
		en0018ExcelBuilder.setFileName(En0018ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0018ExcelBuilder.validDataCountOver(this.getCustomerAllMeiboListCount(searchForm.getCustomerAllMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CustomerAllMeiboListExcelDto customerAllMeiboListExcelDto = new CustomerAllMeiboListExcelDto();
		customerAllMeiboListExcelDto.setCustomerAllMeiboListDtoList(this.getCustomerAllMeiboListExcelData(searchForm.getCustomerAllMeiboListSearchForm()));

		en0018ExcelBuilder.setCustomerAllMeiboListExcelDto(customerAllMeiboListExcelDto);

		// ■4.DTOに設定するデータ取得と設定
		try {
			// Excelファイルの出力処理
			en0018ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：顧客の筆まめCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerAllMeiboListFudemame(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0014CsvBuilder cn0014CsvBuilder = new Cn0014CsvBuilder();
		cn0014CsvBuilder.setMessageService(messageService);
		cn0014CsvBuilder.setFileName(Cn0014CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0014CsvBuilder.validDataCountOver(this.getCustomerAllMeiboFudemameListCount(searchForm.getCustomerAllMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0014CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0014CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<FudemameCsvDto> fudemameCsvDtoList = this.getCustomerAllMeiboListFudemameData(searchForm.getCustomerAllMeiboListSearchForm());
		this.setCustomerAllMeiboFudemameBuilderDto(csvBuilderDto, fudemameCsvDtoList);
		cn0014CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0014CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：個人顧客のCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerKojinMeiboListCsv(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0003CsvBuilder cn0003CsvBuilder = new Cn0003CsvBuilder();
		cn0003CsvBuilder.setMessageService(messageService);
		cn0003CsvBuilder.setFileName(Cn0003CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0003CsvBuilder.validDataCountOver(this.getCustomerKojinMeiboListCount(searchForm.getCustomerKojinMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0003CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0003CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<CustomerKojinMeiboListDto> customerKojinMeiboListDtoList = this.getCustomerKojinMeiboListCsvData(searchForm.getCustomerKojinMeiboListSearchForm());
		this.setCustomerKojinMeiboCsvBuilderDto(csvBuilderDto, customerKojinMeiboListDtoList);
		cn0003CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0003CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：個人顧客のExcel出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerKojinMeiboListExcel(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		En0013ExcelBuilder en0013ExcelBuilder = new En0013ExcelBuilder();
		en0013ExcelBuilder.setConfig(excelConfig);
		en0013ExcelBuilder.setMessageService(messageService);
		en0013ExcelBuilder.setFileName(En0013ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0013ExcelBuilder.validDataCountOver(this.getCustomerKojinMeiboListCount(searchForm.getCustomerKojinMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CustomerKojinMeiboListExcelDto customerKojinMeiboListExcelDto = new CustomerKojinMeiboListExcelDto();
		customerKojinMeiboListExcelDto.setCustomerKojinMeiboListDtoList(this.getCustomerKojinMeiboListExcelData(searchForm.getCustomerKojinMeiboListSearchForm()));
		en0013ExcelBuilder.setCustomerKojinMeiboListExcelDto(customerKojinMeiboListExcelDto);

		// ■4.DTOに設定するデータ取得と設定
		try {
			// Excelファイルの出力処理
			en0013ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：個人顧客の筆まめCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerKojinMeiboListFudemame(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0011CsvBuilder cn0011CsvBuilder = new Cn0011CsvBuilder();
		cn0011CsvBuilder.setMessageService(messageService);
		cn0011CsvBuilder.setFileName(Cn0011CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0011CsvBuilder.validDataCountOver(this.getCustomerKojinMeiboFudemameListCount(searchForm.getCustomerKojinMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0011CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0011CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<FudemameCsvDto> fudemameCsvDto = this.getCustomerKojinMeiboListFudemameData(searchForm.getCustomerKojinMeiboListSearchForm());
		this.setCustomerKojinMeiboFudemameBuilderDto(csvBuilderDto, fudemameCsvDto);
		cn0011CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0011CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：法人顧客のCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerHojinMeiboListCsv(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0004CsvBuilder cn0004CsvBuilder = new Cn0004CsvBuilder();
		cn0004CsvBuilder.setMessageService(messageService);
		cn0004CsvBuilder.setFileName(Cn0004CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0004CsvBuilder.validDataCountOver(this.getCustomerHojinMeiboListCount(searchForm.getCustomerHojinMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0004CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0004CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<CustomerHojinMeiboListDto> customerHojinMeiboListDtoList = this.getCustomerHojinMeiboListCsvData(searchForm.getCustomerHojinMeiboListSearchForm());
		this.setCustomerHojinMeiboCsvBuilderDto(csvBuilderDto, customerHojinMeiboListDtoList);
		cn0004CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0004CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：法人顧客のExcel出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerHojinMeiboListExcel(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		En0014ExcelBuilder en0014ExcelBuilder = new En0014ExcelBuilder();
		en0014ExcelBuilder.setConfig(excelConfig);
		en0014ExcelBuilder.setMessageService(messageService);
		en0014ExcelBuilder.setFileName(En0014ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0014ExcelBuilder.validDataCountOver(this.getCustomerHojinMeiboListCount(searchForm.getCustomerHojinMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CustomerHojinMeiboListExcelDto customerHojinMeiboListExcelDto = new CustomerHojinMeiboListExcelDto();
		customerHojinMeiboListExcelDto.setCustomerHojinMeiboListDtoList(this.getCustomerHojinMeiboListExcelData(searchForm.getCustomerHojinMeiboListSearchForm()));

		en0014ExcelBuilder.setCustomerHojinMeiboListExcelDto(customerHojinMeiboListExcelDto);

		// ■4.DTOに設定するデータ取得と設定
		try {
			// Excelファイルの出力処理
			en0014ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：法人顧客の筆まめCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputCustomerHojinMeiboListFudemame(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0012CsvBuilder cn0012CsvBuilder = new Cn0012CsvBuilder();
		cn0012CsvBuilder.setMessageService(messageService);
		cn0012CsvBuilder.setFileName(Cn0012CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0012CsvBuilder.validDataCountOver(this.getCustomerHojinMeiboFudemameListCount(searchForm.getCustomerHojinMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0012CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0012CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<FudemameCsvDto> fudemameCsvDtoList = this.getCustomerHojinMeiboListFudemameData(searchForm.getCustomerHojinMeiboListSearchForm());
		this.setCustomerHojinMeiboFudemameBuilderDto(csvBuilderDto, fudemameCsvDtoList);
		cn0012CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0012CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：顧問のCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputAdvisorMeiboListCsv(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0009CsvBuilder cn0009CsvBuilder = new Cn0009CsvBuilder();
		cn0009CsvBuilder.setMessageService(messageService);
		cn0009CsvBuilder.setFileName(Cn0009CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0009CsvBuilder.validDataCountOver(this.getAdvisorMeiboListCount(searchForm.getAdvisorMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0009CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0009CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<AdvisorMeiboListDto> advisorMeiboListDtoList = this.getAdvisorMeiboListCsvData(searchForm.getAdvisorMeiboListSearchForm());
		this.setAdvisorMeiboCsvBuilderDto(csvBuilderDto, advisorMeiboListDtoList);
		cn0009CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0009CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：顧問のExcel出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputAdvisorMeiboListExcel(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		En0019ExcelBuilder en0019ExcelBuilder = new En0019ExcelBuilder();
		en0019ExcelBuilder.setConfig(excelConfig);
		en0019ExcelBuilder.setMessageService(messageService);
		en0019ExcelBuilder.setFileName(En0019ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0019ExcelBuilder.validDataCountOver(this.getAdvisorMeiboListCount(searchForm.getAdvisorMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		AdvisorMeiboListExcelDto advisorMeiboListExcelDto = new AdvisorMeiboListExcelDto();
		advisorMeiboListExcelDto.setAdvisorMeiboListDtoList(this.getAdvisorMeiboListExcelData(searchForm.getAdvisorMeiboListSearchForm()));

		en0019ExcelBuilder.setAdvisorMeiboListExcelDto(advisorMeiboListExcelDto);

		// ■4.DTOに設定するデータ取得と設定
		try {
			// Excelファイルの出力処理
			en0019ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：顧問の筆まめ出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputAdvisorMeiboListFudemame(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0015CsvBuilder cn0015CsvBuilder = new Cn0015CsvBuilder();
		cn0015CsvBuilder.setMessageService(messageService);
		cn0015CsvBuilder.setFileName(Cn0015CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0015CsvBuilder.validDataCountOver(this.getAdvisorMeiboFudemameListCount(searchForm.getAdvisorMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0015CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0015CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<FudemameCsvDto> fudemameCsvDto = this.getAdvisorMeiboListFudemameData(searchForm.getAdvisorMeiboListSearchForm());
		this.setAdvisorMeiboFudemameBuilderDto(csvBuilderDto, fudemameCsvDto);
		cn0015CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0015CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：弁護士のCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputBengoshiMeiboListCsv(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0007CsvBuilder cn0007CsvBuilder = new Cn0007CsvBuilder();
		cn0007CsvBuilder.setMessageService(messageService);
		cn0007CsvBuilder.setFileName(Cn0007CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0007CsvBuilder.validDataCountOver(this.getBengoshiMeiboListCount(searchForm.getBengoshiMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0007CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0007CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<BengoshiMeiboListDto> bengoshiMeiboListDtoList = this.getBengoshiMeiboListCsvData(searchForm.getBengoshiMeiboListSearchForm());
		this.setBengoshiMeiboCsvBuilderDto(csvBuilderDto, bengoshiMeiboListDtoList);
		cn0007CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0007CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：弁護士のExcel出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputBengoshiMeiboListExcel(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		En0017ExcelBuilder en0017ExcelBuilder = new En0017ExcelBuilder();
		en0017ExcelBuilder.setConfig(excelConfig);
		en0017ExcelBuilder.setMessageService(messageService);
		en0017ExcelBuilder.setFileName(En0017ExcelBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!en0017ExcelBuilder.validDataCountOver(this.getBengoshiMeiboListCount(searchForm.getBengoshiMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		BengoshiMeiboListExcelDto bengoshiMeiboListExcelDto = new BengoshiMeiboListExcelDto();
		List<ExcelBengoshiMeiboListRowData> bengoshiMeiboListDtoList = this.getBengoshiMeiboListExcelData(searchForm.getBengoshiMeiboListSearchForm());
		bengoshiMeiboListExcelDto.setBengoshiMeiboListDtoList(bengoshiMeiboListDtoList);

		en0017ExcelBuilder.setBengoshiMeiboListExcelDto(bengoshiMeiboListExcelDto);

		// ■4.DTOに設定するデータ取得と設定
		try {
			// Excelファイルの出力処理
			en0017ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 名簿一覧：弁護士の筆まめCsv出力処理
	 *
	 * @param response
	 * @param searchForm
	 */
	public void outputBengoshiMeiboListFudemame(HttpServletResponse response, MeiboListSearchForm searchForm) {

		// ■1.Builderを定義
		Cn0013CsvBuilder cn0013CsvBuilder = new Cn0013CsvBuilder();
		cn0013CsvBuilder.setMessageService(messageService);
		cn0013CsvBuilder.setFileName(Cn0013CsvBuilder.FILE_NAME);

		// ■2.ダウンロードデータ件数チェック
		if (!cn0013CsvBuilder.validDataCountOver(this.getBengoshiMeiboFudemameListCount(searchForm.getBengoshiMeiboListSearchForm()), response)) {
			// ダウンロード可能件数より多い場合は処理終了
			return;
		}

		// ■3.DTOを定義
		CsvBuilderDto csvBuilderDto = cn0013CsvBuilder.createNewTargetBuilderDto();
		CsvRowDto headerCsvRowDto = cn0013CsvBuilder.getHeaderRowDto();

		// ■4.DTOに設定するデータ取得と設定
		csvBuilderDto.setHeaderRowDto(headerCsvRowDto);

		List<FudemameCsvDto> fudemameCsvDtoList = this.getBengoshiMeiboListFudemameData(searchForm.getBengoshiMeiboListSearchForm());
		this.setBengoshiMeiboFudemameBuilderDto(csvBuilderDto, fudemameCsvDtoList);
		cn0013CsvBuilder.setCsvBuilderDto(csvBuilderDto);

		try {
			// CSVファイルの出力処理
			cn0013CsvBuilder.makeCsvFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 名簿一覧：すべての出力する件数を取得する
	 * 
	 * @param allMeiboListSearchForm
	 * @return
	 */
	private int getAllMeiboListCount(MeiboListSearchForm searchForm) {

		AllMeiboListSearchForm allMeiboListSearchForm = searchForm.getAllMeiboListSearchForm();
		AllMeiboListSearchCondition searchConditions = allMeiboListSearchForm.toAllMeiboListSearchCondition();
		AllMeiboListSortCondition sortConditions = allMeiboListSearchForm.toAllMeiboListSortCondition();

		// 検索処理
		if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
			// 通常の検索の場合
			return meiboListDao.selectAllMeiboByConditionsCount(searchConditions, sortConditions);
		} else {
			// クイック検索の場合
			return meiboListDao.selectAllMeiboByQuickSearchCount(StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()), sortConditions);
		}
	}

	/**
	 * CSV出力用に名簿一覧：すべての画面表示情報を取得する
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<AllMeiboListDto> getAllMeiboListCsvData(MeiboListSearchForm searchForm) {

		// 検索条件
		AllMeiboListSearchForm allMeiboSearchForm = searchForm.getAllMeiboListSearchForm();
		AllMeiboListSearchCondition searchConditions = allMeiboSearchForm.toAllMeiboListSearchCondition();
		AllMeiboListSortCondition sortConditions = allMeiboSearchForm.toAllMeiboListSortCondition();

		// 検索処理
		List<AllMeiboListBean> allMeiboListBeanList = null;
		if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
			// 通常の検索の場合
			allMeiboListBeanList = meiboListDao.selectAllMeiboByConditions(searchConditions, sortConditions);
		} else {
			// クイック検索の場合
			allMeiboListBeanList = meiboListDao.selectAllMeiboByQuickSearch(StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()), sortConditions);
		}

		// convert処理
		List<AllMeiboListDto> allMeiboListDtoList = this.convertAllBean2Dto(allMeiboListBeanList);

		return allMeiboListDtoList;
	}

	/**
	 * Excel出力用に名簿一覧：すべての画面表示情報を取得する
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<ExcelAllMeiboListRowData> getAllMeiboListExcelData(MeiboListSearchForm searchForm) {

		// 検索条件
		AllMeiboListSearchForm allMeiboSearchForm = searchForm.getAllMeiboListSearchForm();
		AllMeiboListSearchCondition searchConditions = allMeiboSearchForm.toAllMeiboListSearchCondition();
		AllMeiboListSortCondition sortConditions = allMeiboSearchForm.toAllMeiboListSortCondition();

		// 検索処理
		List<AllMeiboListBean> allMeiboListBeanList = null;
		if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
			// 通常の検索の場合
			allMeiboListBeanList = meiboListDao.selectAllMeiboByConditions(searchConditions, sortConditions);
		} else {
			// クイック検索の場合
			allMeiboListBeanList = meiboListDao.selectAllMeiboByQuickSearch(StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()), sortConditions);
		}

		// convert処理
		return this.convertAllBeanForExcelData(allMeiboListBeanList);
	}

	/**
	 * 名簿一覧：すべてのデータExcel用<br>
	 * List<AllMeiboListBean>型からList<ExcelAllMeiboListRowData>型に変換します
	 * 
	 * @param allMeiboListBeanList
	 * @return
	 */
	private List<ExcelAllMeiboListRowData> convertAllBeanForExcelData(List<AllMeiboListBean> allMeiboListBeanList) {
		return allMeiboListBeanList.stream().map(bean -> {
			ExcelAllMeiboListRowData allMeiboListDto = new ExcelAllMeiboListRowData();

			allMeiboListDto.setPersonId(bean.getPersonId());
			PersonName name = new PersonName(bean.getNameSei(), bean.getNameMei(), bean.getNameSeiKana(), bean.getNameMeiKana());
			allMeiboListDto.setName(name.getName());
			PersonAttribute personAttribute = new PersonAttribute(bean.getCustomerFlg(), bean.getAdvisorFlg(), bean.getCustomerType());
			allMeiboListDto.setPersonAttribute(personAttribute);
			allMeiboListDto.setCustomerFlg(bean.getCustomerFlg());
			allMeiboListDto.setCustomerType(CustomerType.of(bean.getCustomerType()));
			allMeiboListDto.setZipCode(bean.getZipCode());
			allMeiboListDto.setAddress1(bean.getAddress1());
			allMeiboListDto.setAddress2(bean.getAddress2());
			allMeiboListDto.setTelNo(bean.getTelNo());
			allMeiboListDto.setMailAddress(bean.getMailAddress());
			allMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));

			if (SystemFlg.FLG_ON.equalsByCode(bean.getExistsAnkenCustomer())) {
				// 案件顧客として登録されている
				allMeiboListDto.setExistsAnkenCustomer(true);
			} else {
				// 案件顧客として登録されていない
				allMeiboListDto.setExistsAnkenCustomer(false);
			}

			if (SystemFlg.FLG_ON.equalsByCode(bean.getExistsKanyosha())) {
				// 関与者として登録されている
				allMeiboListDto.setExistsKanyosha(true);
			} else {
				// 関与者として登録されていない
				allMeiboListDto.setExistsKanyosha(false);
			}

			return allMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 名簿一覧：すべての出力する筆まめCSV件数を取得する
	 * 
	 * @param allMeiboListSearchForm
	 * @return
	 */
	private int getAllMeiboFudemameListCount(MeiboListSearchForm searchForm) {

		AllMeiboListSearchForm allMeiboSearchForm = searchForm.getAllMeiboListSearchForm();
		AllMeiboListSearchCondition searchConditions = allMeiboSearchForm.toAllMeiboListSearchCondition();
		AllMeiboListSortCondition sortConditions = allMeiboSearchForm.toAllMeiboListSortCondition();

		// 検索処理
		List<FudemameAllMeiboListBean> fudemameAllMeiboListBean = null;
		if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
			// 通常の検索の場合
			fudemameAllMeiboListBean = meiboListDao.selectAllMeiboFudemameBeanByConditions(searchConditions, sortConditions, true);
		} else {
			// クイック検索の場合
			fudemameAllMeiboListBean = meiboListDao.selectAllMeiboFudemameBeanByQuickSearch(StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()), sortConditions, true);
		}

		if (CollectionUtils.isEmpty(fudemameAllMeiboListBean)) {
			return Integer.valueOf(0);
		}

		return fudemameAllMeiboListBean.stream().findFirst().map(FudemameAllMeiboListBean::getCount).orElse(Integer.valueOf(0));
	}

	/**
	 * 筆まめCSV出力用に名簿一覧：すべての画面表示情報を取得する
	 * 
	 * @param searchForm
	 * @return
	 */
	private List<FudemameCsvDto> getAllMeiboListFudemameData(MeiboListSearchForm searchForm) {

		// 検索条件
		AllMeiboListSearchForm allMeiboSearchForm = searchForm.getAllMeiboListSearchForm();
		AllMeiboListSearchCondition searchConditions = allMeiboSearchForm.toAllMeiboListSearchCondition();
		AllMeiboListSortCondition sortConditions = allMeiboSearchForm.toAllMeiboListSortCondition();

		// 検索処理
		List<FudemameAllMeiboListBean> fudemameAllMeiboListBean = null;
		if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
			// 通常の検索の場合
			fudemameAllMeiboListBean = meiboListDao.selectAllMeiboFudemameBeanByConditions(searchConditions, sortConditions, false);
		} else {
			// クイック検索の場合
			fudemameAllMeiboListBean = meiboListDao.selectAllMeiboFudemameBeanByQuickSearch(StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()), sortConditions, false);
		}

		// convert処理
		List<FudemameCsvDto> fudemameCsvDto = fudemameAllMeiboListBean.stream().map(e -> {
			FudemameCsvDto dto = new FudemameCsvDto();
			PersonName name = new PersonName(e.getNameSei(), e.getNameMei(), e.getNameSeiKana(), e.getNameMeiKana());
			dto.setNameKana(name.getNameKana());
			dto.setName(name.getName());
			dto.setOldName(e.getOldName());
			String gender = "";
			if (Gender.MALE.equalsByCode(e.getGenderType())) {
				gender = "男";
			} else if (Gender.FEMALE.equalsByCode(e.getGenderType())) {
				gender = "女";
			} else {
				// その他はインストールできないのでなにもしない
			}
			dto.setGender(gender);
			dto.setBirthday(DateUtils.parseToString(e.getBirthday(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			dto.setZipCode(e.getZipCode());
			dto.setAddress1(e.getAddress1());
			dto.setAddress2(e.getAddress2());
			dto.setTelNo(e.getTelNo());
			dto.setFaxNo(e.getFaxNo());
			dto.setMailAddress(e.getMailAddress());
			dto.setWorkPlace(e.getWorkPlace());
			dto.setBushoName(e.getBushoName());
			PersonAttribute attr = new PersonAttribute(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType());
			dto.setBunrui(attr.getName());
			dto.setMemo(e.getRemarks());
			return dto;
		}).collect(Collectors.toList());

		return fudemameCsvDto;
	}

	/**
	 * 名簿一覧：すべてのCSVデータを設定する
	 * 
	 * @param csvBuilderDto
	 * @param allMeiboListDtoList
	 */
	private void setAllMeiboCsvBuilderDto(CsvBuilderDto csvBuilderDto, List<AllMeiboListDto> allMeiboListDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (AllMeiboListDto allMeiboListDto : allMeiboListDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// 名簿ID
			String personId = CommonConstant.BLANK;
			if (allMeiboListDto.getPersonId() != null) {
				personId = String.valueOf(allMeiboListDto.getPersonId());
			}
			CsvRowItemDto personIdItem = new CsvRowItemDto(personId);
			dataRowItemList.add(personIdItem);

			// 属性
			String attribute = allMeiboListDto.getPersonAttribute().getName();
			CsvRowItemDto attributeItem = new CsvRowItemDto(attribute);
			dataRowItemList.add(attributeItem);

			// 名前
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(allMeiboListDto.getName())) {
				name = allMeiboListDto.getName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(allMeiboListDto.getZipCode())) {
				zipCode = allMeiboListDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所
			String address = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(allMeiboListDto.getAddress1()) || StringUtils.isNotEmpty(allMeiboListDto.getAddress2())) {
				address = StringUtils.null2blank(allMeiboListDto.getAddress1()) + StringUtils.null2blank(allMeiboListDto.getAddress2());
			}
			CsvRowItemDto addressItem = new CsvRowItemDto(address);
			dataRowItemList.add(addressItem);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(allMeiboListDto.getTelNo())) {
				telNo = allMeiboListDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(allMeiboListDto.getMailAddress())) {
				mailAddress = allMeiboListDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 案件
			String anken = CommonConstant.BLANK;
			if (allMeiboListDto.isExistsAnkenCustomer() || allMeiboListDto.isExistsKanyosha()) {
				anken = "案件あり";
			} 
			CsvRowItemDto ankenItem = new CsvRowItemDto(anken);
			dataRowItemList.add(ankenItem);

			// 登録日
			String customerCreateDate = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(allMeiboListDto.getCustomerCreatedDate())) {
				customerCreateDate = allMeiboListDto.getCustomerCreatedDate();
			}
			CsvRowItemDto customerCreateDateItem = new CsvRowItemDto(customerCreateDate);
			dataRowItemList.add(customerCreateDateItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：すべての筆まめCSVデータを設定する
	 * 
	 * @param csvBuilderDto
	 * @param allMeiboListDtoList
	 */
	private void setAllMeiboFudemameBuilderDto(CsvBuilderDto csvBuilderDto, List<FudemameCsvDto> fudemameCsvDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (FudemameCsvDto fudemameCsvDto : fudemameCsvDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// フリガナ
			String nameKana = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getNameKana())) {
				nameKana = fudemameCsvDto.getNameKana();
			}
			CsvRowItemDto nameKanaItem = new CsvRowItemDto(nameKana);
			dataRowItemList.add(nameKanaItem);

			// 氏名
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getName())) {
				name = fudemameCsvDto.getName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 旧姓
			String oldName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getOldName())) {
				oldName = fudemameCsvDto.getOldName();
			}
			CsvRowItemDto oldNameItem = new CsvRowItemDto(oldName);
			dataRowItemList.add(oldNameItem);

			// 性別
			String gender = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getGender())) {
				gender = fudemameCsvDto.getGender();
			}
			CsvRowItemDto genderItem = new CsvRowItemDto(gender);
			dataRowItemList.add(genderItem);

			// 誕生日
			String birthday = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBirthday())) {
				birthday = fudemameCsvDto.getBirthday();
			}
			CsvRowItemDto birthdayItem = new CsvRowItemDto(birthday);
			dataRowItemList.add(birthdayItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getZipCode())) {
				zipCode = fudemameCsvDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所1
			String address1 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress1())) {
				address1 = fudemameCsvDto.getAddress1();
			}
			CsvRowItemDto address1Item = new CsvRowItemDto(address1);
			dataRowItemList.add(address1Item);

			// 住所2
			String address2 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress2())) {
				address2 = fudemameCsvDto.getAddress2();
			}
			CsvRowItemDto address2Item = new CsvRowItemDto(address2);
			dataRowItemList.add(address2Item);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getTelNo())) {
				telNo = fudemameCsvDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// Fax番号
			String faxNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getFaxNo())) {
				faxNo = fudemameCsvDto.getFaxNo();
			}
			CsvRowItemDto faxNoItem = new CsvRowItemDto(faxNo);
			dataRowItemList.add(faxNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMailAddress())) {
				mailAddress = fudemameCsvDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 会社名
			String workPlace = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getWorkPlace())) {
				workPlace = fudemameCsvDto.getWorkPlace();
			}
			CsvRowItemDto workPlaceItem = new CsvRowItemDto(workPlace);
			dataRowItemList.add(workPlaceItem);

			// 部署名
			String bushoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBushoName())) {
				bushoName = fudemameCsvDto.getBushoName();
			}
			CsvRowItemDto bushoNameItem = new CsvRowItemDto(bushoName);
			dataRowItemList.add(bushoNameItem);

			// 分類
			String bunrui = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBunrui())) {
				bunrui = fudemameCsvDto.getBunrui();
			}
			CsvRowItemDto bunruiItem = new CsvRowItemDto(bunrui);
			dataRowItemList.add(bunruiItem);

			// メモ
			String memo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMemo())) {
				memo = fudemameCsvDto.getMemo();
			}
			CsvRowItemDto memoItem = new CsvRowItemDto(memo);
			dataRowItemList.add(memoItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：個人顧客の出力する件数を取得する
	 *
	 * @param customerKojinMeiboListSearchForm
	 * @return
	 */
	private int getCustomerKojinMeiboListCount(MeiboListSearchForm.CustomerKojinMeiboListSearchForm customerKojinMeiboListSearchForm) {

		CustomerKojinMeiboListSearchCondition searchConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSearchCondition();
		CustomerKojinMeiboListSortCondition sortConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSortCondition();

		// 件数取得
		return meiboListDao.selectCustomerKojinMeiboByConditionsCount(searchConditions, sortConditions);
	}

	/**
	 * CSV出力用に名簿一覧：個人顧客の画面表示情報を取得する
	 * 
	 * @param customerKojinMeiboListSearchForm
	 * @return
	 */
	private List<CustomerKojinMeiboListDto> getCustomerKojinMeiboListCsvData(MeiboListSearchForm.CustomerKojinMeiboListSearchForm customerKojinMeiboListSearchForm) {

		CustomerKojinMeiboListSearchCondition searchConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSearchCondition();
		CustomerKojinMeiboListSortCondition sortConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSortCondition();

		// 検索処理
		List<CustomerKojinMeiboListBean> customerKojinMeiboBeanList = meiboListDao.selectCustomerKojinMeiboByConditions(searchConditions, sortConditions);

		// convert処理
		List<CustomerKojinMeiboListDto> customerKojinMeiboListDtoList = this.convertKojinBean2Dto(customerKojinMeiboBeanList);

		return customerKojinMeiboListDtoList;
	}

	/**
	 * EXCEL出力用に名簿一覧：個人顧客の画面表示情報を取得する
	 * 
	 * @param customerKojinMeiboListSearchForm
	 * @return
	 */
	private List<ExcelCustomerKojinMeiboListRowData> getCustomerKojinMeiboListExcelData(MeiboListSearchForm.CustomerKojinMeiboListSearchForm customerKojinMeiboListSearchForm) {

		CustomerKojinMeiboListSearchCondition searchConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSearchCondition();
		CustomerKojinMeiboListSortCondition sortConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSortCondition();

		// 検索処理
		List<CustomerKojinMeiboListBean> customerKojinMeiboBeanList = meiboListDao.selectCustomerKojinMeiboByConditions(searchConditions, sortConditions);

		// convert処理
		return this.convertKojinBeanForExcelData(customerKojinMeiboBeanList);
	}

	/**
	 * 名簿一覧：顧客個人のデータExcel用<br>
	 * List<CustomerAllMeiboListBean>型からList<ExcelCustomerAllMeiboListRowData>型に変換します
	 * 
	 * @param customerAllMeiboListBeanList
	 * @return
	 */
	private List<ExcelCustomerKojinMeiboListRowData> convertKojinBeanForExcelData(List<CustomerKojinMeiboListBean> customerKojinMeiboListBeanList) {

		return customerKojinMeiboListBeanList.stream().map(bean -> {

			ExcelCustomerKojinMeiboListRowData excelCustomerKojinMeiboListRowData = new ExcelCustomerKojinMeiboListRowData();

			excelCustomerKojinMeiboListRowData.setPersonId(bean.getPersonId());
			excelCustomerKojinMeiboListRowData.setCustomerName(bean.getCustomerNameSei() + " " + StringUtils.null2blank(bean.getCustomerNameMei()));
			excelCustomerKojinMeiboListRowData.setZipCode(bean.getZipCode());
			excelCustomerKojinMeiboListRowData.setAddress1(bean.getAddress1());
			excelCustomerKojinMeiboListRowData.setAddress2(bean.getAddress2());
			excelCustomerKojinMeiboListRowData.setTelNo(bean.getTelNo());
			excelCustomerKojinMeiboListRowData.setMailAddress(bean.getMailAddress());
			excelCustomerKojinMeiboListRowData.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			excelCustomerKojinMeiboListRowData.setRemarks(bean.getRemarks());

			return excelCustomerKojinMeiboListRowData;
		}).collect(Collectors.toList());
	}

	/**
	 * 名簿一覧：個人顧客の出力する件数を取得する
	 *
	 * @param customerKojinMeiboListSearchForm
	 * @return
	 */
	private int getCustomerKojinMeiboFudemameListCount(MeiboListSearchForm.CustomerKojinMeiboListSearchForm customerKojinMeiboListSearchForm) {

		CustomerKojinMeiboListSearchCondition searchConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSearchCondition();
		CustomerKojinMeiboListSortCondition sortConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSortCondition();

		// 件数取得
		List<FudemameCustomerKojinMeiboListBean> fudemameCustomerKojinMeiboListBeanList = meiboListDao.selectCustomerKojinMeiboFudemameBeanByConditions(searchConditions, sortConditions, true);
		if (CollectionUtils.isEmpty(fudemameCustomerKojinMeiboListBeanList)) {
			return Integer.valueOf(0);
		}
		return fudemameCustomerKojinMeiboListBeanList.stream().findFirst().map(FudemameCustomerKojinMeiboListBean::getCount).orElse(Integer.valueOf(0));
	}

	/**
	 * CSV出力用に名簿一覧：個人顧客の画面表示情報を取得する
	 * 
	 * @param customerKojinMeiboListSearchForm
	 * @return
	 */
	private List<FudemameCsvDto> getCustomerKojinMeiboListFudemameData(MeiboListSearchForm.CustomerKojinMeiboListSearchForm customerKojinMeiboListSearchForm) {

		CustomerKojinMeiboListSearchCondition searchConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSearchCondition();
		CustomerKojinMeiboListSortCondition sortConditions = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSortCondition();

		// 検索処理
		List<FudemameCustomerKojinMeiboListBean> fudemameCustomerKojinMeiboListBeanList = meiboListDao.selectCustomerKojinMeiboFudemameBeanByConditions(searchConditions, sortConditions, false);

		// convert処理
		List<FudemameCsvDto> fudemameCsvDto = fudemameCustomerKojinMeiboListBeanList.stream().map(e -> {
			FudemameCsvDto dto = new FudemameCsvDto();
			PersonName name = new PersonName(e.getCustomerNameSei(), e.getCustomerNameMei(), e.getCustomerNameSeiKana(), e.getCustomerNameMeiKana());
			dto.setNameKana(name.getNameKana());
			dto.setName(name.getName());
			dto.setOldName(e.getOldName());
			String gender = "";
			if (Gender.MALE.equalsByCode(e.getGenderType())) {
				gender = "男";
			} else if (Gender.FEMALE.equalsByCode(e.getGenderType())) {
				gender = "女";
			} else {
				// その他はインストールできないのでなにもしない
			}
			dto.setGender(gender);
			dto.setBirthday(DateUtils.parseToString(e.getBirthday(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			dto.setZipCode(e.getZipCode());
			dto.setAddress1(e.getAddress1());
			dto.setAddress2(e.getAddress2());
			dto.setTelNo(e.getTelNo());
			dto.setFaxNo(e.getFaxNo());
			dto.setMailAddress(e.getMailAddress());
			dto.setWorkPlace(e.getWorkPlace());
			dto.setBushoName(e.getBushoName());
			PersonAttribute attr = new PersonAttribute(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType());
			dto.setBunrui(attr.getName());
			dto.setMemo(e.getRemarks());
			return dto;
		}).collect(Collectors.toList());

		return fudemameCsvDto;
	}

	/**
	 * 名簿一覧：個人顧客のCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param customerKojinMeiboListDtoList
	 */
	private void setCustomerKojinMeiboCsvBuilderDto(CsvBuilderDto csvBuilderDto, List<CustomerKojinMeiboListDto> customerKojinMeiboListDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (CustomerKojinMeiboListDto customerKojinMeiboListDto : customerKojinMeiboListDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// 名簿ID
			String personId = CommonConstant.BLANK;
			if (customerKojinMeiboListDto.getPersonId() != null) {
				personId = customerKojinMeiboListDto.getPersonId().toString();
			}
			CsvRowItemDto personIdItem = new CsvRowItemDto(personId);
			dataRowItemList.add(personIdItem);

			// 名前
			String customerName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerKojinMeiboListDto.getCustomerName())) {
				customerName = customerKojinMeiboListDto.getCustomerName();
			}
			CsvRowItemDto customerNameItem = new CsvRowItemDto(customerName);
			dataRowItemList.add(customerNameItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerKojinMeiboListDto.getZipCode())) {
				zipCode = customerKojinMeiboListDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所
			String address = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerKojinMeiboListDto.getAddress1()) || StringUtils.isNotEmpty(customerKojinMeiboListDto.getAddress2())) {
				address = StringUtils.null2blank(customerKojinMeiboListDto.getAddress1()) + StringUtils.null2blank(customerKojinMeiboListDto.getAddress2());
			}
			CsvRowItemDto addressItem = new CsvRowItemDto(address);
			dataRowItemList.add(addressItem);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerKojinMeiboListDto.getTelNo())) {
				telNo = customerKojinMeiboListDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerKojinMeiboListDto.getMailAddress())) {
				mailAddress = customerKojinMeiboListDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 登録日
			String customerCreateDate = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerKojinMeiboListDto.getCustomerCreatedDate())) {
				customerCreateDate = customerKojinMeiboListDto.getCustomerCreatedDate();
			}
			CsvRowItemDto customerCreateDateItem = new CsvRowItemDto(customerCreateDate);
			dataRowItemList.add(customerCreateDateItem);

			// 特記事項
			String remarks = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerKojinMeiboListDto.getRemarks())) {
				remarks = customerKojinMeiboListDto.getRemarks();
			}
			CsvRowItemDto remarksItem = new CsvRowItemDto(remarks);
			dataRowItemList.add(remarksItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：個人顧客の筆まめCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param fudemameCsvDtoList
	 */
	private void setCustomerKojinMeiboFudemameBuilderDto(CsvBuilderDto csvBuilderDto, List<FudemameCsvDto> fudemameCsvDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (FudemameCsvDto fudemameCsvDto : fudemameCsvDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// フリガナ
			String nameKana = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getNameKana())) {
				nameKana = fudemameCsvDto.getNameKana();
			}
			CsvRowItemDto nameKanaItem = new CsvRowItemDto(nameKana);
			dataRowItemList.add(nameKanaItem);

			// 氏名
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getName())) {
				name = fudemameCsvDto.getName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 旧姓
			String oldName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getOldName())) {
				oldName = fudemameCsvDto.getOldName();
			}
			CsvRowItemDto oldNameItem = new CsvRowItemDto(oldName);
			dataRowItemList.add(oldNameItem);

			// 性別
			String gender = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getGender())) {
				gender = fudemameCsvDto.getGender();
			}
			CsvRowItemDto genderItem = new CsvRowItemDto(gender);
			dataRowItemList.add(genderItem);

			// 誕生日
			String birthday = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBirthday())) {
				birthday = fudemameCsvDto.getBirthday();
			}
			CsvRowItemDto birthdayItem = new CsvRowItemDto(birthday);
			dataRowItemList.add(birthdayItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getZipCode())) {
				zipCode = fudemameCsvDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所1
			String address1 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress1())) {
				address1 = fudemameCsvDto.getAddress1();
			}
			CsvRowItemDto address1Item = new CsvRowItemDto(address1);
			dataRowItemList.add(address1Item);

			// 住所2
			String address2 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress2())) {
				address2 = fudemameCsvDto.getAddress2();
			}
			CsvRowItemDto address2Item = new CsvRowItemDto(address2);
			dataRowItemList.add(address2Item);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getTelNo())) {
				telNo = fudemameCsvDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// Fax番号
			String faxNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getFaxNo())) {
				faxNo = fudemameCsvDto.getFaxNo();
			}
			CsvRowItemDto faxNoItem = new CsvRowItemDto(faxNo);
			dataRowItemList.add(faxNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMailAddress())) {
				mailAddress = fudemameCsvDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 会社名
			String workPlace = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getWorkPlace())) {
				workPlace = fudemameCsvDto.getWorkPlace();
			}
			CsvRowItemDto workPlaceItem = new CsvRowItemDto(workPlace);
			dataRowItemList.add(workPlaceItem);

			// 部署名
			String bushoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBushoName())) {
				bushoName = fudemameCsvDto.getBushoName();
			}
			CsvRowItemDto bushoNameItem = new CsvRowItemDto(bushoName);
			dataRowItemList.add(bushoNameItem);

			// 分類
			String bunrui = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBunrui())) {
				bunrui = fudemameCsvDto.getBunrui();
			}
			CsvRowItemDto bunruiItem = new CsvRowItemDto(bunrui);
			dataRowItemList.add(bunruiItem);

			// メモ
			String memo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMemo())) {
				memo = fudemameCsvDto.getMemo();
			}
			CsvRowItemDto memoItem = new CsvRowItemDto(memo);
			dataRowItemList.add(memoItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：法人顧客の出力する件数を取得する
	 * 
	 * @param customerHojinMeiboListSearchForm
	 * @return
	 */
	private int getCustomerHojinMeiboListCount(MeiboListSearchForm.CustomerHojinMeiboListSearchForm customerHojinMeiboListSearchForm) {

		CustomerHojinMeiboListSearchCondition searchConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSearchCondition();
		CustomerHojinMeiboListSortCondition sortConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSortCondition();

		// 件数取得
		return meiboListDao.selectCustomerHojinMeiboByConditionsCount(searchConditions, sortConditions);
	}

	/**
	 * CSV出力用に名簿一覧：法人顧客の画面表示情報を取得する
	 * 
	 * @param customerHojinMeiboListSearchForm
	 * @return
	 */
	private List<CustomerHojinMeiboListDto> getCustomerHojinMeiboListCsvData(MeiboListSearchForm.CustomerHojinMeiboListSearchForm customerHojinMeiboListSearchForm) {

		CustomerHojinMeiboListSearchCondition searchConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSearchCondition();
		CustomerHojinMeiboListSortCondition sortConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSortCondition();

		// 検索処理
		List<CustomerHojinMeiboListBean> customerHojinMeiboBeanList = meiboListDao.selectCustomerHojinMeiboByConditions(searchConditions, sortConditions);

		// convert処理
		List<CustomerHojinMeiboListDto> customerHojinMeiboListDtoList = this.convertHojinBean2Dto(customerHojinMeiboBeanList);

		return customerHojinMeiboListDtoList;
	}

	/**
	 * EXCEL出力用に名簿一覧：法人データを取得する
	 * 
	 * @param customerAllMeiboListSearchForm
	 * @return
	 */
	private List<ExcelCustomerHojinMeiboListRowData> getCustomerHojinMeiboListExcelData(MeiboListSearchForm.CustomerHojinMeiboListSearchForm customerHojinMeiboListSearchForm) {

		CustomerHojinMeiboListSearchCondition searchConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSearchCondition();
		CustomerHojinMeiboListSortCondition sortConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSortCondition();

		// 検索処理
		List<CustomerHojinMeiboListBean> meiboBeanList = meiboListDao.selectCustomerHojinMeiboByConditions(searchConditions, sortConditions);

		// convert
		return this.convertCustomerHojinBeanForExcelData(meiboBeanList);
	}

	/**
	 * 名簿一覧：法人データExcel用<br>
	 * List<CustomerHojinMeiboListBean>型からList<ExcelCustomerHojinMeiboListRowData>型に変換します
	 * 
	 * @param customerHojinMeiboListBeanList
	 * @return
	 */
	private List<ExcelCustomerHojinMeiboListRowData> convertCustomerHojinBeanForExcelData(List<CustomerHojinMeiboListBean> customerHojinMeiboListBeanList) {

		return customerHojinMeiboListBeanList.stream().map(bean -> {

			ExcelCustomerHojinMeiboListRowData excelCustomerHojinMeiboListRowData = new ExcelCustomerHojinMeiboListRowData();

			excelCustomerHojinMeiboListRowData.setPersonId(bean.getPersonId());
			excelCustomerHojinMeiboListRowData.setCustomerName(bean.getCustomerNameSei());
			// 代表名は必須チェックがついていないので、（かな）だけ入力している場合は（かな）のほうを表示する
			if (StringUtils.isNotEmpty(bean.getDaihyoName())) {
				excelCustomerHojinMeiboListRowData.setDaihyoName(bean.getDaihyoName());
			} else {
				excelCustomerHojinMeiboListRowData.setDaihyoName(bean.getDaihyoNameKana());
			}
			excelCustomerHojinMeiboListRowData.setDaihyoPositionName(bean.getDaihyoPositionName());
			excelCustomerHojinMeiboListRowData.setTantoName(bean.getTantoName());
			excelCustomerHojinMeiboListRowData.setZipCode(bean.getZipCode());
			excelCustomerHojinMeiboListRowData.setAddress1(bean.getAddress1());
			excelCustomerHojinMeiboListRowData.setAddress2(bean.getAddress2());
			excelCustomerHojinMeiboListRowData.setTelNo(bean.getTelNo());
			excelCustomerHojinMeiboListRowData.setMailAddress(bean.getMailAddress());
			excelCustomerHojinMeiboListRowData.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			excelCustomerHojinMeiboListRowData.setRemarks(bean.getRemarks());

			return excelCustomerHojinMeiboListRowData;
		}).collect(Collectors.toList());
	}

	/**
	 * 名簿一覧：法人顧客の筆まめCsv出力する件数を取得する
	 * 
	 * @param customerHojinMeiboListSearchForm
	 * @return
	 */
	private int getCustomerHojinMeiboFudemameListCount(MeiboListSearchForm.CustomerHojinMeiboListSearchForm customerHojinMeiboListSearchForm) {

		CustomerHojinMeiboListSearchCondition searchConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSearchCondition();
		CustomerHojinMeiboListSortCondition sortConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSortCondition();

		// 件数取得
		List<FudemameCustomerHojinMeiboListBean> fudemameCustomerHojinMeiboListBeanList = meiboListDao.selectCustomerHojinMeiboFudemameBeanByConditions(searchConditions, sortConditions, true);
		if (CollectionUtils.isEmpty(fudemameCustomerHojinMeiboListBeanList)) {
			return Integer.valueOf(0);
		}
		return fudemameCustomerHojinMeiboListBeanList.stream().findFirst().map(FudemameCustomerHojinMeiboListBean::getCount).orElse(Integer.valueOf(0));
	}

	/**
	 * 筆まめCSV出力用に名簿一覧：法人顧客の画面表示情報を取得する
	 * 
	 * @param customerHojinMeiboListSearchForm
	 * @return
	 */
	private List<FudemameCsvDto> getCustomerHojinMeiboListFudemameData(MeiboListSearchForm.CustomerHojinMeiboListSearchForm customerHojinMeiboListSearchForm) {

		CustomerHojinMeiboListSearchCondition searchConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSearchCondition();
		CustomerHojinMeiboListSortCondition sortConditions = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSortCondition();

		// 検索処理
		List<FudemameCustomerHojinMeiboListBean> fudemameCustomerHojinMeiboListBean = meiboListDao.selectCustomerHojinMeiboFudemameBeanByConditions(searchConditions, sortConditions, false);

		// convert処理
		List<FudemameCsvDto> fudemameCsvDto = fudemameCustomerHojinMeiboListBean.stream().map(e -> {
			FudemameCsvDto dto = new FudemameCsvDto();
			PersonName name = new PersonName(e.getCustomerNameSei(), "", e.getCustomerNameSeiKana(), "");
			dto.setNameKana(name.getNameKana());
			dto.setName(name.getName());
			dto.setOldName("");
			dto.setGender("");
			dto.setBirthday("");
			dto.setZipCode(e.getZipCode());
			dto.setAddress1(e.getAddress1());
			dto.setAddress2(e.getAddress2());
			dto.setTelNo(e.getTelNo());
			dto.setFaxNo(e.getFaxNo());
			dto.setMailAddress(e.getMailAddress());
			dto.setWorkPlace("");
			dto.setBushoName("");
			PersonAttribute attr = new PersonAttribute(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType());
			dto.setBunrui(attr.getName());
			dto.setMemo(e.getRemarks());
			return dto;
		}).collect(Collectors.toList());

		return fudemameCsvDto;
	}

	/**
	 * 名簿一覧：法人顧客のCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param customerHojinMeiboListDtoList
	 */
	private void setCustomerHojinMeiboCsvBuilderDto(CsvBuilderDto csvBuilderDto, List<CustomerHojinMeiboListDto> customerHojinMeiboListDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (CustomerHojinMeiboListDto customerHojinMeiboListDto : customerHojinMeiboListDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// 名簿ID
			String personId = CommonConstant.BLANK;
			if (customerHojinMeiboListDto.getPersonId() != null) {
				personId = customerHojinMeiboListDto.getPersonId().toString();
			}
			CsvRowItemDto personIdItem = new CsvRowItemDto(personId);
			dataRowItemList.add(personIdItem);

			// 名前
			String customerName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getCustomerName())) {
				customerName = customerHojinMeiboListDto.getCustomerName();
			}
			CsvRowItemDto customerNameItem = new CsvRowItemDto(customerName);
			dataRowItemList.add(customerNameItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getZipCode())) {
				zipCode = customerHojinMeiboListDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所
			String address = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getAddress1()) || StringUtils.isNotEmpty(customerHojinMeiboListDto.getAddress2())) {
				address = StringUtils.null2blank(customerHojinMeiboListDto.getAddress1()) + StringUtils.null2blank(customerHojinMeiboListDto.getAddress2());
			}
			CsvRowItemDto addressItem = new CsvRowItemDto(address);
			dataRowItemList.add(addressItem);

			// 役職
			String daihyoPosition = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getDaihyoPositionName())) {
				daihyoPosition = customerHojinMeiboListDto.getDaihyoPositionName();
			}
			CsvRowItemDto daihyoPositionItem = new CsvRowItemDto(daihyoPosition);
			dataRowItemList.add(daihyoPositionItem);

			// 代表者
			String daihyoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getDaihyoName())) {
				daihyoName = customerHojinMeiboListDto.getDaihyoName();
			}
			CsvRowItemDto daihyoNameItem = new CsvRowItemDto(daihyoName);
			dataRowItemList.add(daihyoNameItem);

			// 担当者
			String tantoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getTantoName())) {
				tantoName = customerHojinMeiboListDto.getTantoName();
			}
			CsvRowItemDto tantoNameItem = new CsvRowItemDto(tantoName);
			dataRowItemList.add(tantoNameItem);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getTelNo())) {
				telNo = customerHojinMeiboListDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getMailAddress())) {
				mailAddress = customerHojinMeiboListDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 登録日
			String customerCreateDate = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getCustomerCreatedDate())) {
				customerCreateDate = customerHojinMeiboListDto.getCustomerCreatedDate();
			}
			CsvRowItemDto customerCreateDateItem = new CsvRowItemDto(customerCreateDate);
			dataRowItemList.add(customerCreateDateItem);

			// 特記事項
			String remarks = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerHojinMeiboListDto.getRemarks())) {
				remarks = customerHojinMeiboListDto.getRemarks();
			}
			CsvRowItemDto remarksItem = new CsvRowItemDto(remarks);
			dataRowItemList.add(remarksItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：法人顧客の筆まめCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param fudemameCsvDtoList
	 */
	private void setCustomerHojinMeiboFudemameBuilderDto(CsvBuilderDto csvBuilderDto, List<FudemameCsvDto> fudemameCsvDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (FudemameCsvDto fudemameCsvDto : fudemameCsvDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// フリガナ
			String nameKana = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getNameKana())) {
				nameKana = fudemameCsvDto.getNameKana();
			}
			CsvRowItemDto nameKanaItem = new CsvRowItemDto(nameKana);
			dataRowItemList.add(nameKanaItem);

			// 氏名
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getName())) {
				name = fudemameCsvDto.getName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 旧姓
			String oldName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getOldName())) {
				oldName = fudemameCsvDto.getOldName();
			}
			CsvRowItemDto oldNameItem = new CsvRowItemDto(oldName);
			dataRowItemList.add(oldNameItem);

			// 性別
			String gender = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getGender())) {
				gender = fudemameCsvDto.getGender();
			}
			CsvRowItemDto genderItem = new CsvRowItemDto(gender);
			dataRowItemList.add(genderItem);

			// 誕生日
			String birthday = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBirthday())) {
				birthday = fudemameCsvDto.getBirthday();
			}
			CsvRowItemDto birthdayItem = new CsvRowItemDto(birthday);
			dataRowItemList.add(birthdayItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getZipCode())) {
				zipCode = fudemameCsvDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所1
			String address1 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress1())) {
				address1 = fudemameCsvDto.getAddress1();
			}
			CsvRowItemDto address1Item = new CsvRowItemDto(address1);
			dataRowItemList.add(address1Item);

			// 住所2
			String address2 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress2())) {
				address2 = fudemameCsvDto.getAddress2();
			}
			CsvRowItemDto address2Item = new CsvRowItemDto(address2);
			dataRowItemList.add(address2Item);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getTelNo())) {
				telNo = fudemameCsvDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// Fax番号
			String faxNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getFaxNo())) {
				faxNo = fudemameCsvDto.getFaxNo();
			}
			CsvRowItemDto faxNoItem = new CsvRowItemDto(faxNo);
			dataRowItemList.add(faxNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMailAddress())) {
				mailAddress = fudemameCsvDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 会社名
			String workPlace = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getWorkPlace())) {
				workPlace = fudemameCsvDto.getWorkPlace();
			}
			CsvRowItemDto workPlaceItem = new CsvRowItemDto(workPlace);
			dataRowItemList.add(workPlaceItem);

			// 部署名
			String bushoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBushoName())) {
				bushoName = fudemameCsvDto.getBushoName();
			}
			CsvRowItemDto bushoNameItem = new CsvRowItemDto(bushoName);
			dataRowItemList.add(bushoNameItem);

			// 分類
			String bunrui = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBunrui())) {
				bunrui = fudemameCsvDto.getBunrui();
			}
			CsvRowItemDto bunruiItem = new CsvRowItemDto(bunrui);
			dataRowItemList.add(bunruiItem);

			// メモ
			String memo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMemo())) {
				memo = fudemameCsvDto.getMemo();
			}
			CsvRowItemDto memoItem = new CsvRowItemDto(memo);
			dataRowItemList.add(memoItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：弁護士の出力する件数を取得する
	 * 
	 * @param bengoshiMeiboListSearchForm
	 * @return
	 */
	private int getBengoshiMeiboListCount(MeiboListSearchForm.BengoshiMeiboListSearchForm bengoshiMeiboListSearchForm) {

		BengoshiMeiboListSearchCondition searchConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSearchCondition();
		BengoshiMeiboListSortCondition sortConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSortCondition();

		// 件数取得
		return meiboListDao.selectBengoshiMeiboByConditionsCount(searchConditions, sortConditions);
	}

	/**
	 * CSV出力用に名簿一覧：弁護士データを取得する
	 * 
	 * @param bengoshiMeiboListSearchForm
	 * @return
	 */
	private List<BengoshiMeiboListDto> getBengoshiMeiboListCsvData(MeiboListSearchForm.BengoshiMeiboListSearchForm bengoshiMeiboListSearchForm) {

		BengoshiMeiboListSearchCondition searchConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSearchCondition();
		BengoshiMeiboListSortCondition sortConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSortCondition();

		// 検索処理
		List<BengoshiMeiboListBean> meiboBeanList = meiboListDao.selectBengoshiMeiboByConditions(searchConditions, sortConditions);

		// convert
		return this.convertBengoshiBean2Dto(meiboBeanList);
	}

	/**
	 * EXCEL出力用に名簿一覧：弁護士データを取得する
	 * 
	 * @param bengoshiMeiboListSearchForm
	 * @return
	 */
	private List<ExcelBengoshiMeiboListRowData> getBengoshiMeiboListExcelData(MeiboListSearchForm.BengoshiMeiboListSearchForm bengoshiMeiboListSearchForm) {

		BengoshiMeiboListSearchCondition searchConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSearchCondition();
		BengoshiMeiboListSortCondition sortConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSortCondition();

		// 検索処理
		List<BengoshiMeiboListBean> meiboBeanList = meiboListDao.selectBengoshiMeiboByConditions(searchConditions, sortConditions);

		// convert
		return this.convertBengoshiBeanForExcelData(meiboBeanList);
	}

	/**
	 * 名簿一覧：弁護士データExcel用<br>
	 * List<BengoshiMeiboListBean>型からList<ExcelBengoshiMeiboListRowData>型に変換します
	 * 
	 * @param bengoshiMeiboListBeanList
	 * @return
	 */
	private List<ExcelBengoshiMeiboListRowData> convertBengoshiBeanForExcelData(List<BengoshiMeiboListBean> bengoshiMeiboListBeanList) {

		return bengoshiMeiboListBeanList.stream().map(bean -> {

			ExcelBengoshiMeiboListRowData excelBengoshiMeiboListRowData = new ExcelBengoshiMeiboListRowData();

			excelBengoshiMeiboListRowData.setPersonId(bean.getPersonId());
			excelBengoshiMeiboListRowData.setBengoshiName(bean.getBengoshiNameSei() + " " + StringUtils.null2blank(bean.getBengoshiNameMei()));
			excelBengoshiMeiboListRowData.setJimushoName(bean.getJimushoName());
			excelBengoshiMeiboListRowData.setZipCode(bean.getZipCode());
			excelBengoshiMeiboListRowData.setAddress1(bean.getAddress1());
			excelBengoshiMeiboListRowData.setAddress2(bean.getAddress2());
			excelBengoshiMeiboListRowData.setTelNo(bean.getTelNo());
			excelBengoshiMeiboListRowData.setMailAddress(bean.getMailAddress());
			excelBengoshiMeiboListRowData.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			excelBengoshiMeiboListRowData.setRemarks(bean.getRemarks());

			return excelBengoshiMeiboListRowData;
		}).collect(Collectors.toList());
	}

	/**
	 * 名簿一覧：弁護士の出力する件数を取得する
	 * 
	 * @param bengoshiMeiboListSearchForm
	 * @return
	 */
	private int getBengoshiMeiboFudemameListCount(MeiboListSearchForm.BengoshiMeiboListSearchForm bengoshiMeiboListSearchForm) {

		BengoshiMeiboListSearchCondition searchConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSearchCondition();
		BengoshiMeiboListSortCondition sortConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSortCondition();

		// 件数取得
		List<FudemameBengoshiMeiboListBean> fudemameBengoshiMeiboListBean = meiboListDao.selectBengoshiMeiboFudemameBeanByConditions(searchConditions, sortConditions, true);
		if (CollectionUtils.isEmpty(fudemameBengoshiMeiboListBean)) {
			return Integer.valueOf(0);
		}
		return fudemameBengoshiMeiboListBean.stream().findFirst().map(FudemameBengoshiMeiboListBean::getCount).orElse(Integer.valueOf(0));
	}

	/**
	 * CSV出力用に名簿一覧：弁護士データを取得する
	 * 
	 * @param bengoshiMeiboListSearchForm
	 * @return
	 */
	private List<FudemameCsvDto> getBengoshiMeiboListFudemameData(MeiboListSearchForm.BengoshiMeiboListSearchForm bengoshiMeiboListSearchForm) {

		BengoshiMeiboListSearchCondition searchConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSearchCondition();
		BengoshiMeiboListSortCondition sortConditions = bengoshiMeiboListSearchForm.toBengoshiMeiboListSortCondition();

		// 検索処理
		List<FudemameBengoshiMeiboListBean> fudemameBengoshiMeiboListBean = meiboListDao.selectBengoshiMeiboFudemameBeanByConditions(searchConditions, sortConditions, false);

		// convert
		List<FudemameCsvDto> fudemameCsvDtoList = fudemameBengoshiMeiboListBean.stream().map(e -> {
			FudemameCsvDto dto = new FudemameCsvDto();
			PersonName name = new PersonName(e.getBengoshiNameSei(), e.getBengoshiNameMei(), e.getBengoshiNameSeiKana(), e.getBengoshiNameMeiKana());
			dto.setNameKana(name.getNameKana());
			dto.setName(name.getName());
			dto.setOldName("");
			dto.setGender("");
			dto.setBirthday("");
			dto.setZipCode(e.getZipCode());
			dto.setAddress1(e.getAddress1());
			dto.setAddress2(e.getAddress2());
			dto.setTelNo(e.getTelNo());
			dto.setFaxNo(e.getFaxNo());
			dto.setMailAddress(e.getMailAddress());
			dto.setWorkPlace(e.getJimushoName());
			dto.setBushoName(e.getBushoName());
			PersonAttribute attr = new PersonAttribute(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType());
			dto.setBunrui(attr.getName());
			dto.setMemo(e.getRemarks());
			return dto;
		}).collect(Collectors.toList());
		return fudemameCsvDtoList;
	}

	/**
	 * 名簿一覧：弁護士のCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param bengoshiMeiboListDtoList
	 */
	private void setBengoshiMeiboCsvBuilderDto(CsvBuilderDto csvBuilderDto, List<BengoshiMeiboListDto> bengoshiMeiboListDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (BengoshiMeiboListDto bengoshiMeiboListDto : bengoshiMeiboListDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// 名簿ID
			String personId = CommonConstant.BLANK;
			if (bengoshiMeiboListDto.getPersonId() != null) {
				personId = bengoshiMeiboListDto.getPersonId().toString();
			}
			CsvRowItemDto personIdItem = new CsvRowItemDto(personId);
			dataRowItemList.add(personIdItem);

			// 名前
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(bengoshiMeiboListDto.getBengoshiName())) {
				name = bengoshiMeiboListDto.getBengoshiName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 事務所名
			String jimushoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(bengoshiMeiboListDto.getJimushoName())) {
				jimushoName = bengoshiMeiboListDto.getJimushoName();
			}
			CsvRowItemDto jimushoNameItem = new CsvRowItemDto(jimushoName);
			dataRowItemList.add(jimushoNameItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(bengoshiMeiboListDto.getZipCode())) {
				zipCode = bengoshiMeiboListDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所
			String address = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(bengoshiMeiboListDto.getAddress1()) || StringUtils.isNotEmpty(bengoshiMeiboListDto.getAddress2())) {
				address = StringUtils.null2blank(bengoshiMeiboListDto.getAddress1()) + StringUtils.null2blank(bengoshiMeiboListDto.getAddress2());
			}
			CsvRowItemDto addressItem = new CsvRowItemDto(address);
			dataRowItemList.add(addressItem);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(bengoshiMeiboListDto.getTelNo())) {
				telNo = bengoshiMeiboListDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(bengoshiMeiboListDto.getMailAddress())) {
				mailAddress = bengoshiMeiboListDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 登録日
			String createDate = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(bengoshiMeiboListDto.getCustomerCreatedDate())) {
				createDate = bengoshiMeiboListDto.getCustomerCreatedDate();
			}
			CsvRowItemDto createDateItem = new CsvRowItemDto(createDate);
			dataRowItemList.add(createDateItem);

			// 特記事項
			String remarks = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(bengoshiMeiboListDto.getRemarks())) {
				remarks = bengoshiMeiboListDto.getRemarks();
			}
			CsvRowItemDto remarksItem = new CsvRowItemDto(remarks);
			dataRowItemList.add(remarksItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：弁護士の筆まめCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param fudemameCsvDtoList
	 */
	private void setBengoshiMeiboFudemameBuilderDto(CsvBuilderDto csvBuilderDto, List<FudemameCsvDto> fudemameCsvDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (FudemameCsvDto fudemameCsvDto : fudemameCsvDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// フリガナ
			String nameKana = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getNameKana())) {
				nameKana = fudemameCsvDto.getNameKana();
			}
			CsvRowItemDto nameKanaItem = new CsvRowItemDto(nameKana);
			dataRowItemList.add(nameKanaItem);

			// 氏名
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getName())) {
				name = fudemameCsvDto.getName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 旧姓
			String oldName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getOldName())) {
				oldName = fudemameCsvDto.getOldName();
			}
			CsvRowItemDto oldNameItem = new CsvRowItemDto(oldName);
			dataRowItemList.add(oldNameItem);

			// 性別
			String gender = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getGender())) {
				gender = fudemameCsvDto.getGender();
			}
			CsvRowItemDto genderItem = new CsvRowItemDto(gender);
			dataRowItemList.add(genderItem);

			// 誕生日
			String birthday = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBirthday())) {
				birthday = fudemameCsvDto.getBirthday();
			}
			CsvRowItemDto birthdayItem = new CsvRowItemDto(birthday);
			dataRowItemList.add(birthdayItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getZipCode())) {
				zipCode = fudemameCsvDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所1
			String address1 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress1())) {
				address1 = fudemameCsvDto.getAddress1();
			}
			CsvRowItemDto address1Item = new CsvRowItemDto(address1);
			dataRowItemList.add(address1Item);

			// 住所2
			String address2 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress2())) {
				address2 = fudemameCsvDto.getAddress2();
			}
			CsvRowItemDto address2Item = new CsvRowItemDto(address2);
			dataRowItemList.add(address2Item);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getTelNo())) {
				telNo = fudemameCsvDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// Fax番号
			String faxNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getFaxNo())) {
				faxNo = fudemameCsvDto.getFaxNo();
			}
			CsvRowItemDto faxNoItem = new CsvRowItemDto(faxNo);
			dataRowItemList.add(faxNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMailAddress())) {
				mailAddress = fudemameCsvDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 会社名
			String workPlace = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getWorkPlace())) {
				workPlace = fudemameCsvDto.getWorkPlace();
			}
			CsvRowItemDto workPlaceItem = new CsvRowItemDto(workPlace);
			dataRowItemList.add(workPlaceItem);

			// 部署名
			String bushoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBushoName())) {
				bushoName = fudemameCsvDto.getBushoName();
			}
			CsvRowItemDto bushoNameItem = new CsvRowItemDto(bushoName);
			dataRowItemList.add(bushoNameItem);

			// 分類
			String bunrui = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBunrui())) {
				bunrui = fudemameCsvDto.getBunrui();
			}
			CsvRowItemDto bunruiItem = new CsvRowItemDto(bunrui);
			dataRowItemList.add(bunruiItem);

			// メモ
			String memo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMemo())) {
				memo = fudemameCsvDto.getMemo();
			}
			CsvRowItemDto memoItem = new CsvRowItemDto(memo);
			dataRowItemList.add(memoItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：顧客の出力する件数を取得する
	 * 
	 * @param customerAllMeiboListSearchForm
	 * @return
	 */
	private int getCustomerAllMeiboListCount(MeiboListSearchForm.CustomerAllMeiboListSearchForm customerAllMeiboListSearchForm) {

		CustomerAllMeiboListSearchCondition searchConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSearchCondition();
		CustomerAllMeiboListSortCondition sortConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSortCondition();

		// 件数取得
		return meiboListDao.selectCustomerAllMeiboByConditionsCount(searchConditions, sortConditions);
	}

	/**
	 * CSV出力用に名簿一覧：顧客データを取得する
	 * 
	 * @param customerAllMeiboListSearchForm
	 * @return
	 */
	private List<CustomerAllMeiboListDto> getCustomerAllMeiboListCsvData(MeiboListSearchForm.CustomerAllMeiboListSearchForm customerAllMeiboListSearchForm) {

		CustomerAllMeiboListSearchCondition searchConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSearchCondition();
		CustomerAllMeiboListSortCondition sortConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSortCondition();

		// 検索処理
		List<CustomerAllMeiboListBean> meiboBeanList = meiboListDao.selectCustomerAllMeiboByConditions(searchConditions, sortConditions);

		// convert
		return this.convertCustomerAllBean2Dto(meiboBeanList);
	}

	/**
	 * EXCEL出力用に名簿一覧：顧客データを取得する
	 * 
	 * @param customerAllMeiboListSearchForm
	 * @return
	 */
	private List<ExcelCustomerAllMeiboListRowData> getCustomerAllMeiboListExcelData(MeiboListSearchForm.CustomerAllMeiboListSearchForm customerAllMeiboListSearchForm) {

		CustomerAllMeiboListSearchCondition searchConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSearchCondition();
		CustomerAllMeiboListSortCondition sortConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSortCondition();

		// 検索処理
		List<CustomerAllMeiboListBean> meiboBeanList = meiboListDao.selectCustomerAllMeiboByConditions(searchConditions, sortConditions);

		// convert
		return this.convertCustomerAllBeanForExcelData(meiboBeanList);
	}

	/**
	 * 名簿一覧：顧客データExcel用<br>
	 * List<CustomerAllMeiboListBean>型からList<ExcelCustomerAllMeiboListRowData>型に変換します
	 * 
	 * @param customerAllMeiboListBeanList
	 * @return
	 */
	private List<ExcelCustomerAllMeiboListRowData> convertCustomerAllBeanForExcelData(List<CustomerAllMeiboListBean> customerAllMeiboListBeanList) {

		return customerAllMeiboListBeanList.stream().map(bean -> {

			ExcelCustomerAllMeiboListRowData excelCustomerAllMeiboListRowData = new ExcelCustomerAllMeiboListRowData();

			excelCustomerAllMeiboListRowData.setPersonId(bean.getPersonId());
			excelCustomerAllMeiboListRowData.setCustomerName(bean.getCustomerNameSei() + " " + StringUtils.null2blank(bean.getCustomerNameMei()));
			excelCustomerAllMeiboListRowData.setZipCode(bean.getZipCode());
			excelCustomerAllMeiboListRowData.setAddress1(bean.getAddress1());
			excelCustomerAllMeiboListRowData.setAddress2(bean.getAddress2());
			excelCustomerAllMeiboListRowData.setTelNo(bean.getTelNo());
			excelCustomerAllMeiboListRowData.setMailAddress(bean.getMailAddress());
			excelCustomerAllMeiboListRowData.setCustomerCreateDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			excelCustomerAllMeiboListRowData.setRemarks(bean.getRemarks());

			return excelCustomerAllMeiboListRowData;
		}).collect(Collectors.toList());
	}

	/**
	 * 名簿一覧：顧客の筆まめCSV出力データ件数を取得する
	 * 
	 * @param customerAllMeiboListSearchForm
	 * @return
	 */
	private int getCustomerAllMeiboFudemameListCount(MeiboListSearchForm.CustomerAllMeiboListSearchForm customerAllMeiboListSearchForm) {

		CustomerAllMeiboListSearchCondition searchConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSearchCondition();
		CustomerAllMeiboListSortCondition sortConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSortCondition();

		// 件数取得
		List<FudemameCustomerAllMeiboListBean> fudemameCustomerAllMeiboListBeanList = meiboListDao.selectCustomerAllMeiboFudemameBeanByConditions(searchConditions, sortConditions, true);

		if (CollectionUtils.isEmpty(fudemameCustomerAllMeiboListBeanList)) {
			return Integer.valueOf(0);
		}

		return fudemameCustomerAllMeiboListBeanList.stream().findFirst().map(FudemameCustomerAllMeiboListBean::getCount).orElse(Integer.valueOf(0));
	}

	/**
	 * 筆まめCSV出力用に名簿一覧：顧客データを取得する
	 *
	 * @param customerAllMeiboListSearchForm
	 * @return
	 */
	private List<FudemameCsvDto> getCustomerAllMeiboListFudemameData(MeiboListSearchForm.CustomerAllMeiboListSearchForm customerAllMeiboListSearchForm) {

		CustomerAllMeiboListSearchCondition searchConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSearchCondition();
		CustomerAllMeiboListSortCondition sortConditions = customerAllMeiboListSearchForm.toCustomerAllMeiboListSortCondition();

		// 検索処理
		List<FudemameCustomerAllMeiboListBean> fudemameCustomerAllMeiboListBeanList = meiboListDao.selectCustomerAllMeiboFudemameBeanByConditions(searchConditions, sortConditions, false);

		// convert処理
		List<FudemameCsvDto> fudemameCsvDto = fudemameCustomerAllMeiboListBeanList.stream().map(e -> {
			FudemameCsvDto dto = new FudemameCsvDto();
			PersonName name = new PersonName(e.getCustomerNameSei(), e.getCustomerNameMei(), e.getCustomerNameSeiKana(), e.getCustomerNameMeiKana());
			dto.setNameKana(name.getNameKana());
			dto.setName(name.getName());
			dto.setOldName(e.getOldName());
			String gender = "";
			if (Gender.MALE.equalsByCode(e.getGenderType())) {
				gender = "男";
			} else if (Gender.FEMALE.equalsByCode(e.getGenderType())) {
				gender = "女";
			} else {
				// その他はインストールできないのでなにもしない
			}
			dto.setGender(gender);
			dto.setBirthday(DateUtils.parseToString(e.getBirthday(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			dto.setZipCode(e.getZipCode());
			dto.setAddress1(e.getAddress1());
			dto.setAddress2(e.getAddress2());
			dto.setTelNo(e.getTelNo());
			dto.setFaxNo(e.getFaxNo());
			dto.setMailAddress(e.getMailAddress());
			dto.setWorkPlace(e.getWorkPlace());
			dto.setBushoName(e.getBushoName());
			PersonAttribute attr = new PersonAttribute(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType());
			dto.setBunrui(attr.getName());
			dto.setMemo(e.getRemarks());
			return dto;
		}).collect(Collectors.toList());

		return fudemameCsvDto;
	}

	/**
	 * 名簿一覧：顧客のCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param customerAllMeiboListDtoList
	 */
	private void setCustomerAllMeiboCsvBuilderDto(CsvBuilderDto csvBuilderDto, List<CustomerAllMeiboListDto> customerAllMeiboListDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (CustomerAllMeiboListDto customerAllMeiboListDto : customerAllMeiboListDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// 名簿ID
			String personId = CommonConstant.BLANK;
			if (customerAllMeiboListDto.getPersonId() != null) {
				personId = customerAllMeiboListDto.getPersonId().toString();
			}
			CsvRowItemDto personIdItem = new CsvRowItemDto(personId);
			dataRowItemList.add(personIdItem);

			// 名前
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerAllMeiboListDto.getCustomerName())) {
				name = customerAllMeiboListDto.getCustomerName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerAllMeiboListDto.getZipCode())) {
				zipCode = customerAllMeiboListDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所
			String address = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerAllMeiboListDto.getAddress1()) || StringUtils.isNotEmpty(customerAllMeiboListDto.getAddress2())) {
				address = StringUtils.null2blank(customerAllMeiboListDto.getAddress1()) + StringUtils.null2blank(customerAllMeiboListDto.getAddress2());
			}
			CsvRowItemDto addressItem = new CsvRowItemDto(address);
			dataRowItemList.add(addressItem);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerAllMeiboListDto.getTelNo())) {
				telNo = customerAllMeiboListDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerAllMeiboListDto.getMailAddress())) {
				mailAddress = customerAllMeiboListDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 登録日
			String createDate = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerAllMeiboListDto.getCustomerCreatedDate())) {
				createDate = customerAllMeiboListDto.getCustomerCreatedDate();
			}
			CsvRowItemDto createDateItem = new CsvRowItemDto(createDate);
			dataRowItemList.add(createDateItem);

			// 特記事項
			String remarks = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(customerAllMeiboListDto.getRemarks())) {
				remarks = customerAllMeiboListDto.getRemarks();
			}
			CsvRowItemDto remarksItem = new CsvRowItemDto(remarks);
			dataRowItemList.add(remarksItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：顧客の筆まめCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param fudemameCsvDtoList
	 */
	private void setCustomerAllMeiboFudemameBuilderDto(CsvBuilderDto csvBuilderDto, List<FudemameCsvDto> fudemameCsvDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (FudemameCsvDto fudemameCsvDto : fudemameCsvDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// フリガナ
			String nameKana = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getNameKana())) {
				nameKana = fudemameCsvDto.getNameKana();
			}
			CsvRowItemDto nameKanaItem = new CsvRowItemDto(nameKana);
			dataRowItemList.add(nameKanaItem);

			// 氏名
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getName())) {
				name = fudemameCsvDto.getName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 旧姓
			String oldName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getOldName())) {
				oldName = fudemameCsvDto.getOldName();
			}
			CsvRowItemDto oldNameItem = new CsvRowItemDto(oldName);
			dataRowItemList.add(oldNameItem);

			// 性別
			String gender = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getGender())) {
				gender = fudemameCsvDto.getGender();
			}
			CsvRowItemDto genderItem = new CsvRowItemDto(gender);
			dataRowItemList.add(genderItem);

			// 誕生日
			String birthday = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBirthday())) {
				birthday = fudemameCsvDto.getBirthday();
			}
			CsvRowItemDto birthdayItem = new CsvRowItemDto(birthday);
			dataRowItemList.add(birthdayItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getZipCode())) {
				zipCode = fudemameCsvDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所1
			String address1 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress1())) {
				address1 = fudemameCsvDto.getAddress1();
			}
			CsvRowItemDto address1Item = new CsvRowItemDto(address1);
			dataRowItemList.add(address1Item);

			// 住所2
			String address2 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress2())) {
				address2 = fudemameCsvDto.getAddress2();
			}
			CsvRowItemDto address2Item = new CsvRowItemDto(address2);
			dataRowItemList.add(address2Item);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getTelNo())) {
				telNo = fudemameCsvDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// Fax番号
			String faxNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getFaxNo())) {
				faxNo = fudemameCsvDto.getFaxNo();
			}
			CsvRowItemDto faxNoItem = new CsvRowItemDto(faxNo);
			dataRowItemList.add(faxNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMailAddress())) {
				mailAddress = fudemameCsvDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 会社名
			String workPlace = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getWorkPlace())) {
				workPlace = fudemameCsvDto.getWorkPlace();
			}
			CsvRowItemDto workPlaceItem = new CsvRowItemDto(workPlace);
			dataRowItemList.add(workPlaceItem);

			// 部署名
			String bushoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBushoName())) {
				bushoName = fudemameCsvDto.getBushoName();
			}
			CsvRowItemDto bushoNameItem = new CsvRowItemDto(bushoName);
			dataRowItemList.add(bushoNameItem);

			// 分類
			String bunrui = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBunrui())) {
				bunrui = fudemameCsvDto.getBunrui();
			}
			CsvRowItemDto bunruiItem = new CsvRowItemDto(bunrui);
			dataRowItemList.add(bunruiItem);

			// メモ
			String memo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMemo())) {
				memo = fudemameCsvDto.getMemo();
			}
			CsvRowItemDto memoItem = new CsvRowItemDto(memo);
			dataRowItemList.add(memoItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：顧問の出力する件数を取得する
	 * 
	 * @param advisorMeiboListSearchForm
	 * @return
	 */
	private int getAdvisorMeiboListCount(MeiboListSearchForm.AdvisorMeiboListSearchForm advisorMeiboListSearchForm) {

		AdvisorMeiboListSearchCondition searchConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSearchCondition();
		AdvisorMeiboListSortCondition sortConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSortCondition();

		// 件数取得
		return meiboListDao.selectAdvisorMeiboByConditionsCount(searchConditions, sortConditions);
	}

	/**
	 * CSV出力用に名簿一覧：顧問データを取得する
	 * 
	 * @param advisorMeiboListSearchForm
	 * @return
	 */
	private List<AdvisorMeiboListDto> getAdvisorMeiboListCsvData(MeiboListSearchForm.AdvisorMeiboListSearchForm advisorMeiboListSearchForm) {

		AdvisorMeiboListSearchCondition searchConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSearchCondition();
		AdvisorMeiboListSortCondition sortConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSortCondition();

		// 検索処理
		List<AdvisorMeiboListBean> meiboBeanList = meiboListDao.selectAdvisorMeiboByConditions(searchConditions, sortConditions);

		// convert
		return this.convertAdvisorBean2Dto(meiboBeanList);
	}

	/**
	 * EXCEL出力用に名簿一覧：顧問データを取得する
	 * 
	 * @param advisorMeiboListSearchForm
	 * @return
	 */
	private List<ExcelAdvisorMeiboListRowData> getAdvisorMeiboListExcelData(MeiboListSearchForm.AdvisorMeiboListSearchForm advisorMeiboListSearchForm) {

		AdvisorMeiboListSearchCondition searchConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSearchCondition();
		AdvisorMeiboListSortCondition sortConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSortCondition();

		// 検索処理
		List<AdvisorMeiboListBean> meiboBeanList = meiboListDao.selectAdvisorMeiboByConditions(searchConditions, sortConditions);

		// convert
		return this.convertAdvisorBeanForExcelData(meiboBeanList);
	}

	/**
	 * 名簿一覧：顧問データExcel用<br>
	 * List<AdvisorMeiboListBean>型からList<ExcelAdvisorMeiboListRowData>型に変換します
	 * 
	 * @param advisorMeiboListBeanList
	 * @return
	 */
	private List<ExcelAdvisorMeiboListRowData> convertAdvisorBeanForExcelData(List<AdvisorMeiboListBean> advisorMeiboListBeanList) {

		return advisorMeiboListBeanList.stream().map(bean -> {

			ExcelAdvisorMeiboListRowData excelAdvisorMeiboListRowData = new ExcelAdvisorMeiboListRowData();

			excelAdvisorMeiboListRowData.setPersonId(bean.getPersonId());
			excelAdvisorMeiboListRowData.setCustomerName(bean.getCustomerNameSei() + " " + StringUtils.null2blank(bean.getCustomerNameMei()));
			excelAdvisorMeiboListRowData.setZipCode(bean.getZipCode());
			excelAdvisorMeiboListRowData.setAddress1(bean.getAddress1());
			excelAdvisorMeiboListRowData.setAddress2(bean.getAddress2());
			excelAdvisorMeiboListRowData.setTelNo(bean.getTelNo());
			excelAdvisorMeiboListRowData.setMailAddress(bean.getMailAddress());
			excelAdvisorMeiboListRowData.setCustomerCreateDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			excelAdvisorMeiboListRowData.setRemarks(bean.getRemarks());

			return excelAdvisorMeiboListRowData;
		}).collect(Collectors.toList());
	}

	/**
	 * 名簿一覧：顧問の筆まめCsv出力する件数を取得する
	 * 
	 * @param advisorMeiboListSearchForm
	 * @return
	 */
	private int getAdvisorMeiboFudemameListCount(MeiboListSearchForm.AdvisorMeiboListSearchForm advisorMeiboListSearchForm) {

		AdvisorMeiboListSearchCondition searchConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSearchCondition();
		AdvisorMeiboListSortCondition sortConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSortCondition();

		// 件数取得
		List<FudemameAdvisorMeiboListBean> fudemameAdvisorMeiboListBean = meiboListDao.selectAdvisorMeiboFudemameBeanByConditions(searchConditions, sortConditions, true);
		if (CollectionUtils.isEmpty(fudemameAdvisorMeiboListBean)) {
			return Integer.valueOf(0);
		}
		return fudemameAdvisorMeiboListBean.stream().findFirst().map(FudemameAdvisorMeiboListBean::getCount).orElse(Integer.valueOf(0));
	}

	/**
	 * CSV出力用に名簿一覧：顧問データを取得する
	 * 
	 * @param advisorMeiboListSearchForm
	 * @return
	 */
	private List<FudemameCsvDto> getAdvisorMeiboListFudemameData(MeiboListSearchForm.AdvisorMeiboListSearchForm advisorMeiboListSearchForm) {

		AdvisorMeiboListSearchCondition searchConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSearchCondition();
		AdvisorMeiboListSortCondition sortConditions = advisorMeiboListSearchForm.toAdvisorMeiboListSortCondition();

		// 検索処理
		List<FudemameAdvisorMeiboListBean> fudemameAdvisorMeiboListBean = meiboListDao.selectAdvisorMeiboFudemameBeanByConditions(searchConditions, sortConditions, false);

		List<FudemameCsvDto> fudemameCsvDto = fudemameAdvisorMeiboListBean.stream().map(e -> {
			FudemameCsvDto dto = new FudemameCsvDto();
			PersonName name = new PersonName(e.getCustomerNameSei(), e.getCustomerNameMei(), e.getCustomerNameSeiKana(), e.getCustomerNameMeiKana());
			dto.setNameKana(name.getNameKana());
			dto.setName(name.getName());
			dto.setOldName(e.getOldName());
			String gender = "";
			if (Gender.MALE.equalsByCode(e.getGenderType())) {
				gender = "男";
			} else if (Gender.FEMALE.equalsByCode(e.getGenderType())) {
				gender = "女";
			} else {
				// その他はインストールできないのでなにもしない
			}
			dto.setGender(gender);
			dto.setBirthday(DateUtils.parseToString(e.getBirthday(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
			dto.setZipCode(e.getZipCode());
			dto.setAddress1(e.getAddress1());
			dto.setAddress2(e.getAddress2());
			dto.setTelNo(e.getTelNo());
			dto.setFaxNo(e.getFaxNo());
			dto.setMailAddress(e.getMailAddress());
			dto.setWorkPlace(e.getWorkPlace());
			dto.setBushoName(e.getBushoName());
			PersonAttribute attr = new PersonAttribute(e.getCustomerFlg(), e.getAdvisorFlg(), e.getCustomerType());
			dto.setBunrui(attr.getName());
			dto.setMemo(e.getRemarks());
			return dto;
		}).collect(Collectors.toList());

		// convert
		return fudemameCsvDto;
	}

	/**
	 * 名簿一覧：顧問のCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param advisorMeiboListDtoList
	 */
	private void setAdvisorMeiboCsvBuilderDto(CsvBuilderDto csvBuilderDto, List<AdvisorMeiboListDto> advisorMeiboListDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (AdvisorMeiboListDto advisorMeiboListDto : advisorMeiboListDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// 名簿ID
			String personId = CommonConstant.BLANK;
			if (advisorMeiboListDto.getPersonId() != null) {
				personId = advisorMeiboListDto.getPersonId().toString();
			}
			CsvRowItemDto personIdItem = new CsvRowItemDto(personId);
			dataRowItemList.add(personIdItem);

			// 名前
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(advisorMeiboListDto.getCustomerName())) {
				name = advisorMeiboListDto.getCustomerName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(advisorMeiboListDto.getZipCode())) {
				zipCode = advisorMeiboListDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所
			String address = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(advisorMeiboListDto.getAddress1()) || StringUtils.isNotEmpty(advisorMeiboListDto.getAddress2())) {
				address = StringUtils.null2blank(advisorMeiboListDto.getAddress1()) + StringUtils.null2blank(advisorMeiboListDto.getAddress2());
			}
			CsvRowItemDto addressItem = new CsvRowItemDto(address);
			dataRowItemList.add(addressItem);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(advisorMeiboListDto.getTelNo())) {
				telNo = advisorMeiboListDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(advisorMeiboListDto.getMailAddress())) {
				mailAddress = advisorMeiboListDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 登録日
			String createDate = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(advisorMeiboListDto.getCustomerCreatedDate())) {
				createDate = advisorMeiboListDto.getCustomerCreatedDate();
			}
			CsvRowItemDto createDateItem = new CsvRowItemDto(createDate);
			dataRowItemList.add(createDateItem);

			// 特記事項
			String remarks = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(advisorMeiboListDto.getRemarks())) {
				remarks = advisorMeiboListDto.getRemarks();
			}
			CsvRowItemDto remarksItem = new CsvRowItemDto(remarks);
			dataRowItemList.add(remarksItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 名簿一覧：顧問の筆まめCSVデータを設定する
	 *
	 * @param csvBuilderDto
	 * @param fudemameCsvDtoList
	 */
	private void setAdvisorMeiboFudemameBuilderDto(CsvBuilderDto csvBuilderDto, List<FudemameCsvDto> fudemameCsvDtoList) {

		// データの追加
		List<CsvRowDto> dataRowDtoList = new ArrayList<>();
		for (FudemameCsvDto fudemameCsvDto : fudemameCsvDtoList) {

			// データ格納用Dto、リスト作成
			CsvRowDto dataRowDto = new CsvRowDto();
			List<CsvRowItemDto> dataRowItemList = new ArrayList<>();

			// フリガナ
			String nameKana = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getNameKana())) {
				nameKana = fudemameCsvDto.getNameKana();
			}
			CsvRowItemDto nameKanaItem = new CsvRowItemDto(nameKana);
			dataRowItemList.add(nameKanaItem);

			// 氏名
			String name = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getName())) {
				name = fudemameCsvDto.getName();
			}
			CsvRowItemDto nameItem = new CsvRowItemDto(name);
			dataRowItemList.add(nameItem);

			// 旧姓
			String oldName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getOldName())) {
				oldName = fudemameCsvDto.getOldName();
			}
			CsvRowItemDto oldNameItem = new CsvRowItemDto(oldName);
			dataRowItemList.add(oldNameItem);

			// 性別
			String gender = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getGender())) {
				gender = fudemameCsvDto.getGender();
			}
			CsvRowItemDto genderItem = new CsvRowItemDto(gender);
			dataRowItemList.add(genderItem);

			// 誕生日
			String birthday = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBirthday())) {
				birthday = fudemameCsvDto.getBirthday();
			}
			CsvRowItemDto birthdayItem = new CsvRowItemDto(birthday);
			dataRowItemList.add(birthdayItem);

			// 郵便番号
			String zipCode = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getZipCode())) {
				zipCode = fudemameCsvDto.getZipCode();
			}
			CsvRowItemDto zipCodeItem = new CsvRowItemDto(zipCode);
			dataRowItemList.add(zipCodeItem);

			// 住所1
			String address1 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress1())) {
				address1 = fudemameCsvDto.getAddress1();
			}
			CsvRowItemDto address1Item = new CsvRowItemDto(address1);
			dataRowItemList.add(address1Item);

			// 住所2
			String address2 = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getAddress2())) {
				address2 = fudemameCsvDto.getAddress2();
			}
			CsvRowItemDto address2Item = new CsvRowItemDto(address2);
			dataRowItemList.add(address2Item);

			// 電話番号
			String telNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getTelNo())) {
				telNo = fudemameCsvDto.getTelNo();
			}
			CsvRowItemDto telNoItem = new CsvRowItemDto(telNo);
			dataRowItemList.add(telNoItem);

			// Fax番号
			String faxNo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getFaxNo())) {
				faxNo = fudemameCsvDto.getFaxNo();
			}
			CsvRowItemDto faxNoItem = new CsvRowItemDto(faxNo);
			dataRowItemList.add(faxNoItem);

			// メールアドレス
			String mailAddress = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMailAddress())) {
				mailAddress = fudemameCsvDto.getMailAddress();
			}
			CsvRowItemDto mailAddressItem = new CsvRowItemDto(mailAddress);
			dataRowItemList.add(mailAddressItem);

			// 会社名
			String workPlace = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getWorkPlace())) {
				workPlace = fudemameCsvDto.getWorkPlace();
			}
			CsvRowItemDto workPlaceItem = new CsvRowItemDto(workPlace);
			dataRowItemList.add(workPlaceItem);

			// 部署名
			String bushoName = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBushoName())) {
				bushoName = fudemameCsvDto.getBushoName();
			}
			CsvRowItemDto bushoNameItem = new CsvRowItemDto(bushoName);
			dataRowItemList.add(bushoNameItem);

			// 分類
			String bunrui = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getBunrui())) {
				bunrui = fudemameCsvDto.getBunrui();
			}
			CsvRowItemDto bunruiItem = new CsvRowItemDto(bunrui);
			dataRowItemList.add(bunruiItem);

			// メモ
			String memo = CommonConstant.BLANK;
			if (StringUtils.isNotEmpty(fudemameCsvDto.getMemo())) {
				memo = fudemameCsvDto.getMemo();
			}
			CsvRowItemDto memoItem = new CsvRowItemDto(memo);
			dataRowItemList.add(memoItem);

			// CSVデータ行を追加
			dataRowDto.setItemList(dataRowItemList);
			dataRowDtoList.add(dataRowDto);
		}

		// CSVデータ
		csvBuilderDto.setDataRowDtoList(dataRowDtoList);
	}

	/**
	 * 「すべて」のconvert処理
	 * 
	 * @param allMeiboListBeanList
	 * @return
	 */
	private List<AllMeiboListDto> convertAllBean2Dto(List<AllMeiboListBean> allMeiboListBeanList) {
		return allMeiboListBeanList.stream().map(bean -> {
			AllMeiboListDto allMeiboListDto = new AllMeiboListDto();

			allMeiboListDto.setPersonId(bean.getPersonId());
			PersonName name = new PersonName(bean.getNameSei(), bean.getNameMei(), bean.getNameSeiKana(), bean.getNameMeiKana());
			allMeiboListDto.setName(name.getName());
			// 名簿属性を生成
			PersonAttribute personAttribute = new PersonAttribute(bean.getCustomerFlg(), bean.getAdvisorFlg(), bean.getCustomerType());
			allMeiboListDto.setPersonAttribute(personAttribute);
			allMeiboListDto.setCustomerFlg(bean.getCustomerFlg());
			allMeiboListDto.setZipCode(bean.getZipCode());
			allMeiboListDto.setAddress1(bean.getAddress1());
			allMeiboListDto.setAddress2(bean.getAddress2());
			allMeiboListDto.setCustomerType(CustomerType.of(bean.getCustomerType()));
			allMeiboListDto.setTelNo(bean.getTelNo());
			allMeiboListDto.setFaxNo(bean.getFaxNo());
			allMeiboListDto.setMailAddress(bean.getMailAddress());
			allMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));

			if (SystemFlg.FLG_ON.equalsByCode(bean.getExistsAnkenCustomer())) {
				// 案件顧客として登録されている
				allMeiboListDto.setExistsAnkenCustomer(true);
			} else {
				// 案件顧客として登録されていない
				allMeiboListDto.setExistsAnkenCustomer(false);
			}

			if (SystemFlg.FLG_ON.equalsByCode(bean.getExistsKanyosha())) {
				// 関与者として登録されている
				allMeiboListDto.setExistsKanyosha(true);
			} else {
				// 関与者として登録されていない
				allMeiboListDto.setExistsKanyosha(false);
			}

			return allMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「顧客名簿」のconvert処理
	 * 
	 * @param customerAllMeiboListBeanList
	 * @return
	 */
	private List<CustomerAllMeiboListDto> convertCustomerAllBean2Dto(List<CustomerAllMeiboListBean> customerAllMeiboListBeanList) {

		return customerAllMeiboListBeanList.stream().map(bean -> {

			CustomerAllMeiboListDto customerAllMeiboListDto = new CustomerAllMeiboListDto();

			customerAllMeiboListDto.setPersonId(bean.getPersonId());
			customerAllMeiboListDto.setCustomerName(bean.getCustomerNameSei() + " " + StringUtils.null2blank(bean.getCustomerNameMei()));
			customerAllMeiboListDto.setZipCode(bean.getZipCode());
			customerAllMeiboListDto.setAddress1(bean.getAddress1());
			customerAllMeiboListDto.setAddress2(bean.getAddress2());
			customerAllMeiboListDto.setTelNo(bean.getTelNo());
			customerAllMeiboListDto.setMailAddress(bean.getMailAddress());
			customerAllMeiboListDto.setAnkenTotalCnt(bean.getAnkenTotalCnt());
			customerAllMeiboListDto.setAnkenProgressCnt(bean.getAnkenProgressCnt());
			customerAllMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			customerAllMeiboListDto.setRemarks(bean.getRemarks());

			return customerAllMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「顧客名簿 個人」のconvert処理
	 * 
	 * @param customerKojinMeiboListBeanList
	 * @return
	 */
	private List<CustomerKojinMeiboListDto> convertKojinBean2Dto(List<CustomerKojinMeiboListBean> customerKojinMeiboListBeanList) {

		return customerKojinMeiboListBeanList.stream().map(bean -> {
			CustomerKojinMeiboListDto customerKojinMeiboListDto = new CustomerKojinMeiboListDto();

			customerKojinMeiboListDto.setPersonId(bean.getPersonId());
			PersonName name = new PersonName(bean.getCustomerNameSei(), bean.getCustomerNameMei(), bean.getCustomerNameSeiKana(), bean.getCustomerNameMeiKana());
			customerKojinMeiboListDto.setCustomerName(name.getName());
			customerKojinMeiboListDto.setZipCode(bean.getZipCode());
			customerKojinMeiboListDto.setAddress1(bean.getAddress1());
			customerKojinMeiboListDto.setAddress2(bean.getAddress2());
			customerKojinMeiboListDto.setTelNo(bean.getTelNo());
			customerKojinMeiboListDto.setMailAddress(bean.getMailAddress());
			customerKojinMeiboListDto.setAnkenTotalCnt(bean.getAnkenTotalCnt());
			customerKojinMeiboListDto.setAnkenProgressCnt(bean.getAnkenProgressCnt());
			customerKojinMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			customerKojinMeiboListDto.setRemarks(bean.getRemarks());

			return customerKojinMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「顧客名簿 企業・団体」のconvert処理
	 * 
	 * @param customerHojinMeiboListBeanList
	 * @return
	 */
	private List<CustomerHojinMeiboListDto> convertHojinBean2Dto(List<CustomerHojinMeiboListBean> customerHojinMeiboListBeanList) {

		return customerHojinMeiboListBeanList.stream().map(bean -> {

			CustomerHojinMeiboListDto customerHojinMeiboListDto = new CustomerHojinMeiboListDto();

			customerHojinMeiboListDto.setPersonId(bean.getPersonId());
			customerHojinMeiboListDto.setCustomerName(bean.getCustomerNameSei());
			// 代表名は必須チェックがついていないので、（かな）だけ入力している場合は（かな）のほうを表示する
			if (StringUtils.isNotEmpty(bean.getDaihyoName())) {
				customerHojinMeiboListDto.setDaihyoName(bean.getDaihyoName());
			} else {
				customerHojinMeiboListDto.setDaihyoName(bean.getDaihyoNameKana());
			}
			customerHojinMeiboListDto.setDaihyoPositionName(bean.getDaihyoPositionName());
			customerHojinMeiboListDto.setTantoName(bean.getTantoName());
			customerHojinMeiboListDto.setZipCode(bean.getZipCode());
			customerHojinMeiboListDto.setAddress1(bean.getAddress1());
			customerHojinMeiboListDto.setAddress2(bean.getAddress2());
			customerHojinMeiboListDto.setTelNo(bean.getTelNo());
			customerHojinMeiboListDto.setFaxNo(bean.getFaxNo());
			customerHojinMeiboListDto.setMailAddress(bean.getMailAddress());
			customerHojinMeiboListDto.setAnkenTotalCnt(bean.getAnkenTotalCnt());
			customerHojinMeiboListDto.setAnkenProgressCnt(bean.getAnkenProgressCnt());
			customerHojinMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			customerHojinMeiboListDto.setRemarks(bean.getRemarks());

			return customerHojinMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「顧客名簿 顧問取引先」のconvert処理
	 * 
	 * @param advisorMeiboListBeanList
	 * @return
	 */
	private List<AdvisorMeiboListDto> convertAdvisorBean2Dto(List<AdvisorMeiboListBean> advisorMeiboListBeanList) {

		return advisorMeiboListBeanList.stream().map(bean -> {

			AdvisorMeiboListDto advisorMeiboListDto = new AdvisorMeiboListDto();

			advisorMeiboListDto.setPersonId(bean.getPersonId());
			advisorMeiboListDto.setCustomerType(bean.getCustomerType());
			advisorMeiboListDto.setCustomerName(bean.getCustomerNameSei() + " " + StringUtils.null2blank(bean.getCustomerNameMei()));
			advisorMeiboListDto.setZipCode(bean.getZipCode());
			advisorMeiboListDto.setAddress1(bean.getAddress1());
			advisorMeiboListDto.setAddress2(bean.getAddress2());
			advisorMeiboListDto.setTelNo(bean.getTelNo());
			advisorMeiboListDto.setMailAddress(bean.getMailAddress());
			advisorMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			advisorMeiboListDto.setRemarks(bean.getRemarks());

			return advisorMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「弁護士名簿」のconvert処理
	 * 
	 * @param bengoshiMeiboListBeanList
	 * @return
	 */
	private List<BengoshiMeiboListDto> convertBengoshiBean2Dto(List<BengoshiMeiboListBean> bengoshiMeiboListBeanList) {

		return bengoshiMeiboListBeanList.stream().map(bean -> {

			BengoshiMeiboListDto bengoshiMeiboListDto = new BengoshiMeiboListDto();

			bengoshiMeiboListDto.setPersonId(bean.getPersonId());
			bengoshiMeiboListDto.setJimushoName(bean.getJimushoName());
			if (StringUtils.isNotEmpty(bean.getBushoName())) {
				bengoshiMeiboListDto.setBushoYakushokuName(bean.getBushoName());
			}
			bengoshiMeiboListDto.setBengoshiName(bean.getBengoshiNameSei() + " " + StringUtils.null2blank(bean.getBengoshiNameMei()));
			bengoshiMeiboListDto.setZipCode(bean.getZipCode());
			bengoshiMeiboListDto.setAddress1(bean.getAddress1());
			bengoshiMeiboListDto.setAddress2(bean.getAddress2());
			bengoshiMeiboListDto.setAddressRemarks(bean.getAddressRemarks());
			bengoshiMeiboListDto.setTelNo(bean.getTelNo());
			bengoshiMeiboListDto.setFaxNo(bean.getFaxNo());
			bengoshiMeiboListDto.setMailAddress(bean.getMailAddress());
			bengoshiMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			bengoshiMeiboListDto.setRemarks(bean.getRemarks());

			return bengoshiMeiboListDto;
		}).collect(Collectors.toList());
	}

}