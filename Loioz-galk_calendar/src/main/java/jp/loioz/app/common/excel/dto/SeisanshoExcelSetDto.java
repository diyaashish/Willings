package jp.loioz.app.common.excel.dto;

import java.util.Collections;
import java.util.List;

import jp.loioz.app.common.excel.builder.naibu.En0009ExcelBuilder.SeisanshoSet;
import jp.loioz.dto.ExcelJippiMeisaiDto;
import jp.loioz.dto.ExcelSeikyushoDto;
import jp.loioz.dto.ExcelSeisanshoDto;
import jp.loioz.dto.ExcelShiharaiKeikakuDto;
import jp.loioz.dto.ExcelSofushoDto;
import jp.loioz.dto.ExcelTimeChargeDto;
import lombok.Data;

@Data
public class SeisanshoExcelSetDto {

	/** 送付書出力用データ */
	private ExcelSofushoDto sofushoData;

	/** 精算書出力用データ */
	private ExcelSeisanshoDto seisanshoData;

	/** 請求書出力用データ */
	private ExcelSeikyushoDto seikyushoData;

	/** 実費明細出力用データ */
	private ExcelJippiMeisaiDto jippiMeisaiData;

	/** 支払い計画出力用データ */
	private ExcelShiharaiKeikakuDto shiharaiKeikakuData;

	/** タイムチャージ出力用データ */
	private ExcelTimeChargeDto timeChargeData;

	/** 各種シートの出力をするかどうかを判定する 出力する場合がtrue */
	private boolean isOutPutSohushoSheet = false;
	private boolean isOutPutSeisanshoSheet = false;
	private boolean isOutPutSeikyushoIkkatsuSheet = false;
	private boolean isOutPutSeikyushoBunkatsuSheet = false;
	private boolean isOutPutJippiMesaiSheet = false;
	private boolean isOutPutShiharaiKeikakuSheet = false;
	private boolean isOutPutTimeChargeSheet = false;

	// 各出力用データのフォームオブジェクトに格納する。

	boolean seisanZeroFlag = false;

	/**
	 * 送付書に記載する出力シートの取得
	 *
	 * @return
	 */
	public List<SeisanshoSet> getSheetList() {

		List<SeisanshoSet> sheetList = Collections.emptyList();
		if (this.isOutPutSeisanshoSheet) {
			sheetList.add(SeisanshoSet.SEISANSHO);
		}

		if (this.isOutPutSeikyushoIkkatsuSheet()) {
			sheetList.add(SeisanshoSet.SEIKYUSHO_IKKATSU);
		}

		if (this.isOutPutSeikyushoBunkatsuSheet()) {
			sheetList.add(SeisanshoSet.SEIKYUSHO_BUNKATSU);
		}

		if (this.isOutPutJippiMesaiSheet) {
			sheetList.add(SeisanshoSet.JIPPIMEISAI);
		}

		if (this.isOutPutShiharaiKeikakuSheet) {
			sheetList.add(SeisanshoSet.SHIHARAIKEIKAKU);
		}

		if (this.isOutPutTimeChargeSheet) {
			sheetList.add(SeisanshoSet.TIMECHARGE);
		}
		return sheetList;
	}

}
