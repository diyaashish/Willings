package jp.loioz.app.user.azukarikinManagement.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0004ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.AzukarikinListExcelDto;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm.Anken;
import jp.loioz.app.user.azukarikinManagement.form.AzukarikinViewForm.Customer;
import jp.loioz.app.user.azukarikinManagement.form.ExcelAzukarikinData;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.domain.value.CustomerId;

@Service
@Transactional(rollbackFor = Exception.class)
public class AzukarikinDocumentService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/**
	 * 預り金・未収金一覧の帳票出力処理
	 *
	 * @param response
	 * @param form
	 * @throws AppException
	 */
	public void excelAzukarikinList(HttpServletResponse response, AzukarikinViewForm form) throws AppException {

		// ■1.Builderを定義
		En0004ExcelBuilder en0004ExcelBuilder = new En0004ExcelBuilder();
		en0004ExcelBuilder.setConfig(excelConfig);

		// ■2.DTOを定義
		AzukarikinListExcelDto azukarikinListExcelDto = en0004ExcelBuilder.createNewTargetBuilderDto();

		// ■3.DTOに設定するデータを取得と設定
		List<ExcelAzukarikinData> azukariListData = convertToOutPutData(form);
		azukarikinListExcelDto.setExcelAzukarikinData(azukariListData);

		en0004ExcelBuilder.setAzukarikinListExcelDto(azukarikinListExcelDto);

		try {
			// Excelファイルの出力処理
			en0004ExcelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			throw new AppException(MessageEnum.MSG_E00034, e);
		}

	}

	/**
	 * 親画面での表示用データから帳票出力用に加工します
	 *
	 * @param form
	 * @return 帳票出力用データ
	 */
	private List<ExcelAzukarikinData> convertToOutPutData(AzukarikinViewForm form) {

		// 多次元配列構造になっているため、一つのリストになるように加工
		List<ExcelAzukarikinData> azukarikinListData = new ArrayList<>();
		for (Customer customerData : form.getRowData()) {

			// 顧客で必要になるデータを保持しておく
			CustomerId customerId = customerData.getCustomerId();
			String customerName = customerData.getCustomerName();
			String dispTotalCustomerKaikei = customerData.getDispTotalCustomerKaikei();

			for (Anken ankenData : customerData.getChildren()) {

				// 出力用データに変換します
				ExcelAzukarikinData azukarikinData = new ExcelAzukarikinData();
				azukarikinData.setCustomerId(customerId);
				azukarikinData.setCustomerName(customerName);
				azukarikinData.setDispTotalCustomerKaikei(CommonUtils.convertMoney2Num(dispTotalCustomerKaikei));
				azukarikinData.setAnkenId(ankenData.getAnkenId());
				azukarikinData.setAnkenName(ankenData.getAnkenName());
				azukarikinData.setBunya(ankenData.getBunya());
				azukarikinData.setAnkenStatus(ankenData.getAnkenStatus());
				azukarikinData.setSalesOwnerName(ankenData.getSalesOwnerName());
				azukarikinData.setTantoLawyerName(ankenData.getTantoLawyerName());
				azukarikinData.setTantoZimuName(ankenData.getTantoZimuName());
				azukarikinData.setDispTotalNyukin(CommonUtils.convertMoney2Num(ankenData.getDispTotalNyukin()));
				azukarikinData.setDispHoshu(CommonUtils.convertMoney2Num(ankenData.getDispHoshu()));
				azukarikinData.setDispTotalShukkin(CommonUtils.convertMoney2Num(ankenData.getDispTotalShukkin()));
				azukarikinData.setDispTotalAnkenKaikei(CommonUtils.convertMoney2Num(ankenData.getDispTotalAnkenKaikei()));
				azukarikinData.setJuninDate(ankenData.getJuninDate());
				azukarikinData.setLastNyukinDate(ankenData.getLastNyukinDate());

				// 一覧のデータにaddする
				azukarikinListData.add(azukarikinData);
			}
		}

		return azukarikinListData;
	}

}
