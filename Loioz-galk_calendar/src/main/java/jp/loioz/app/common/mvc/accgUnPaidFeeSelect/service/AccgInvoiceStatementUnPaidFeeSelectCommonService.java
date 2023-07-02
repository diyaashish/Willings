package jp.loioz.app.common.mvc.accgUnPaidFeeSelect.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.mvc.accgUnPaidFeeSelect.dto.AccgInvoiceStatementUnPaidFeeSelectDto;
import jp.loioz.bean.FeeDetailListBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.utility.AccountingUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TFeeDao;

/**
 * 未精算報酬選択モーダルサービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccgInvoiceStatementUnPaidFeeSelectCommonService extends DefaultService {

	/** 報酬Daoクラス */
	@Autowired
	private TFeeDao tFeeDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * <pre>
	 * 案件ID、名簿IDに紐づく未精算報酬の中から
	 * どの請求書／精算書でも使用されていない報酬と
	 * 会計書類SEQの請求書／精算書で使用されている報酬を取得する。
	 * </pre>
	 * 
	 * @param ankenId
	 * @param personId
	 * @param accgDocSeq
	 * @param excludeFeeSeqList
	 * @return
	 */
	public List<AccgInvoiceStatementUnPaidFeeSelectDto> getUnPaidFeeList(Long ankenId, Long personId,
			Long accgDocSeq, List<Long> excludeFeeSeqList) {
		List<FeeDetailListBean> unPaidFeeBeanList = tFeeDao.selectFeeNotUsedInAnotherAccgDocByParams(ankenId, personId, accgDocSeq);
		List<AccgInvoiceStatementUnPaidFeeSelectDto> unPaidFeeDtoList = this.convertBeanList2Dto(unPaidFeeBeanList,
				excludeFeeSeqList);
		return unPaidFeeDtoList;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================


	/**
	 * List<FeeDetailListBean>型からList<FeeDetailListDto>型に変換します<br>
	 * 
	 * @param feeDetailListBeanList
	 * @param excludeFeeSeqList
	 * @return
	 */
	private List<AccgInvoiceStatementUnPaidFeeSelectDto> convertBeanList2Dto(List<FeeDetailListBean> feeDetailListBeanList, List<Long> excludeFeeSeqList) {
		// Bean型をDto型に変換する
		List<AccgInvoiceStatementUnPaidFeeSelectDto> dtoList = new ArrayList<>();
		feeDetailListBeanList.forEach(bean -> {
			if (!excludeFeeSeqList.contains(bean.getFeeSeq())) {
				dtoList.add(convertFeeDetailBean2Dto(bean));
			}
		});
		return dtoList;
	}

	/**
	 * FeeDetailListBean型からAccgInvoiceStatementUnPaidFeeSelectDto型に変換します<br>
	 * 
	 * @param feeDetailListBean
	 * @return
	 */
	private AccgInvoiceStatementUnPaidFeeSelectDto convertFeeDetailBean2Dto(FeeDetailListBean feeDetailListBean) {

		AccgInvoiceStatementUnPaidFeeSelectDto dto = new AccgInvoiceStatementUnPaidFeeSelectDto();

		dto.setAnkenId(feeDetailListBean.getAnkenId());
		dto.setFeeDate(DateUtils.parseToString(feeDetailListBean.getFeeDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
		dto.setFeeItemName(feeDetailListBean.getFeeItemName());
		dto.setFeePaymentStatus(feeDetailListBean.getFeePaymentStatus());
		dto.setFeeSeq(feeDetailListBean.getFeeSeq());
		dto.setInvoiceNo(feeDetailListBean.getInvoiceNo());
		dto.setInvoicePaymentStatus(feeDetailListBean.getInvoicePaymentStatus());
		dto.setPersonId(feeDetailListBean.getPersonId());
		dto.setStatementNo(feeDetailListBean.getStatementNo());
		dto.setStatementRefundStatus(feeDetailListBean.getStatementRefundStatus());
		dto.setSumText(feeDetailListBean.getSumText());
		dto.setFeeMemo(feeDetailListBean.getFeeMemo());
		dto.setFeeAmount(AccountingUtils.toDispAmountLabel(feeDetailListBean.getFeeAmount()));
		dto.setTaxAmount(AccountingUtils.toDispAmountLabel(feeDetailListBean.getTaxAmount()));
		dto.setWithholdingAmount(AccountingUtils.toDispAmountLabel(feeDetailListBean.getWithholdingAmount()));
		dto.setFeeAmountTaxIn(AccountingUtils.toDispAmountLabel(feeDetailListBean.getFeeAmountTaxIn()));
		dto.setAfterWithholdingTax(AccountingUtils.toDispAmountLabel(feeDetailListBean.getAfterWithholdingTax()));
		// タイムチャージ報酬
		dto.setTimeCharge(CommonConstant.SystemFlg.FLG_ON.equalsByCode(feeDetailListBean.getFeeTimeChargeFlg()));
		dto.setWorkTimeMinute(feeDetailListBean.getWorkTimeMinute());
		dto.setHourPrice(AccountingUtils.toDispAmountLabel(feeDetailListBean.getHourPrice()));
		return dto;
	}

}