package jp.loioz.app.user.nyushukkinYotei.shukkin.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0002ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.ShukkinYoteiExcelDto;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchYoteiDateType;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ExcelShukkinData;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinListSearchForm;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.ShukkinListDto;

@Service
@Transactional(rollbackFor = Exception.class)
public class ShukkinDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 出金一覧の帳票出力処理
	 *
	 * @param response
	 * @param nyukinList
	 * @throws Exception
	 */
	public void excelShukkinList(HttpServletResponse response, List<ShukkinListDto> shukkinList, ShukkinListSearchForm searchForm) {

		// ■1.Builderを定義
		En0002ExcelBuilder en0002ExcelBuilder = new En0002ExcelBuilder();
		en0002ExcelBuilder.setConfig(excelConfig);

		// ■2.DTOを定義
		ShukkinYoteiExcelDto shukkinYoteiExcelDto = en0002ExcelBuilder.createNewTargetBuilderDto();

		// ■3.DTOに設定するデータを取得と設定
		List<ExcelShukkinData> excelShukkinData = this.convert2OutPutData(shukkinList);
		shukkinYoteiExcelDto.setOutPutRange("出金予定一覧" + this.outPutRange(searchForm));
		shukkinYoteiExcelDto.setExcelShukkinData(excelShukkinData);

		// Builderクラスに設定
		en0002ExcelBuilder.setShukkinYoteiExcelDto(shukkinYoteiExcelDto);

		try {
			// Excelファイルの出力処理
			en0002ExcelBuilder.makeExcelFile(response);

		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 出力する日付の範囲を文字列で取得する
	 *
	 * @param searchForm
	 * @return
	 */
	private String outPutRange(ShukkinListSearchForm searchForm) {

		// uuuu年MM月
		StringBuilder sb = new StringBuilder(DateUtils.parseToString(searchForm.getYearMonth(), DateUtils.DATE_JP_YYYY_MM));
		if (ShukkinSearchYoteiDateType.SPECIFICATION == searchForm.getShukkinSearchYoteiDateType()) {
			// uuuu年MM月dd～dd日
			sb.append(searchForm.getSearchDaysFrom());
			sb.append("日～");
			sb.append(searchForm.getSearchDaysTo());
			sb.append("日");
		}

		return StringUtils.surroundBrackets(sb.toString(), true);
	}

	/**
	 * 画面表示DtoをExcel出力用に変換する
	 *
	 * @param nyukinList
	 * @return 出力用データ
	 */
	private List<ExcelShukkinData> convert2OutPutData(List<ShukkinListDto> shukkinList) {
		return shukkinList.stream().map(data -> {
			return ExcelShukkinData.builder()
					.customerId(Long.toString(data.getCustomerId().asLong()))
					.customerName(data.getCustomerName())
					.ankenId(Long.toString(data.getAnkenId().asLong()))
					.ankenName(data.getAnkenName())
					.bunya(commonBunyaService.getBunyaName(data.getBunya()))
					.dispTantoLawer(data.getDispTantoLawer())
					.dispTantoJimu(data.getDispTantoJimu())
					.shukkinKoza(data.getShiharaishaName())
					.shiharaiSaki(data.getShiharaiSakiName())
					.shiharaiLimitDate(DateUtils.parseToString(data.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
					.seisanGaku(data.getSeisanGaku().toString())
					.seisanId(data.getSeisanId().toString())
					.jotai(DefaultEnum.getVal(data.getShukkinSearchRifineType()))
					.build();
		}).collect(Collectors.toList());
	}
}
