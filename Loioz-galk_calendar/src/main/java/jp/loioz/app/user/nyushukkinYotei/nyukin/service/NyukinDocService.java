package jp.loioz.app.user.nyushukkinYotei.nyukin.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0003ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.NyukinYoteiExcelDto;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.nyushukkinYotei.nyukin.enums.NyukinSearchYoteiDateType;
import jp.loioz.app.user.nyushukkinYotei.nyukin.form.ExcelNyukinData;
import jp.loioz.app.user.nyushukkinYotei.nyukin.form.NyukinListSearchForm;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dto.NyukinListDto;

@Service
@Transactional(rollbackFor = Exception.class)
public class NyukinDocService extends DefaultService {

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
	 * 入金一覧の帳票出力処理
	 *
	 * @param response
	 * @param nyukinList
	 * @throws Exception
	 */
	public void excelNyukinList(HttpServletResponse response, List<NyukinListDto> nyukinList, NyukinListSearchForm searchForm) {

		// ■1.Builderを定義
		En0003ExcelBuilder en0003ExcelBuilder = new En0003ExcelBuilder();
		en0003ExcelBuilder.setConfig(excelConfig);

		// ■2.DTOを定義
		NyukinYoteiExcelDto nyukinYoteiExcelDto = en0003ExcelBuilder.createNewTargetBuilderDto();

		// ■3.DTOに設定するデータを取得と設定
		List<ExcelNyukinData> excelNyukinData = this.convert2OutPutData(nyukinList);
		nyukinYoteiExcelDto.setOutPutRange("入金予定一覧" + this.outPutRange(searchForm));
		nyukinYoteiExcelDto.setExcelNyukinData(excelNyukinData);

		// Builderクラスに設定
		en0003ExcelBuilder.setNyushukkinYoteiExcelDto(nyukinYoteiExcelDto);

		try {
			// Excelファイルの出力処理
			en0003ExcelBuilder.makeExcelFile(response);
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
	private String outPutRange(NyukinListSearchForm searchForm) {

		// uuuu年MM月
		StringBuilder sb = new StringBuilder(DateUtils.parseToString(searchForm.getYearMonth(), DateUtils.DATE_JP_YYYY_MM));
		if (NyukinSearchYoteiDateType.SPECIFICATION == searchForm.getNyukinSearchYoteiDateType()) {
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
	private List<ExcelNyukinData> convert2OutPutData(List<NyukinListDto> nyukinList) {
		return nyukinList.stream().map(data -> {
			return ExcelNyukinData.builder()
					.customerId(Long.toString(data.getCustomerId().asLong()))
					.customerName(data.getCustomerName())
					.ankenId(Long.toString(data.getAnkenId().asLong()))
					.ankenName(data.getAnkenName())
					.bunya(commonBunyaService.getBunyaName(data.getBunya()))
					.dispTantoLawer(data.getDispTantoLawer())
					.dispTantoJimu(data.getDispTantoJimu())
					.seisanSaki(data.getShiharaishaName())
					.nyukinKoza(data.getShiharaiSakiName())
					.nyukinYoteiDate(DateUtils.parseToString(data.getNyushukkinYoteiDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED))
					.nyukinYoteiGaku(data.getNyushukkinYoteiGaku().toString())
					.zankin(data.getZankin().toString())
					.seisanId(data.getSeisanId().toString())
					.jotai(DefaultEnum.getVal(data.getNyukinSearchRifineType()))
					.build();
		}).collect(Collectors.toList());
	}

}
