package jp.loioz.app.common.mvc.accgDocRegist.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.mvc.accgDocRegist.form.AccgDocRegistModalInputForm;
import jp.loioz.app.common.service.CommonAccgAmountService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AccgInvoiceStatementAmountDto;
import jp.loioz.dto.PersonDto;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 請求書・精算書作成サービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccgDocRegistCommonService extends DefaultService {

	/** 共通会計サービス */
	@Autowired
	CommonAccgService commonAccgService;

	/** 会計管理の金額を扱う共通サービス */
	@Autowired
	private CommonAccgAmountService commonAccgAmountService;

	/** 名簿情報Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 案件情報Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	/**
	 * 請求書・精算書作成モーダルの入力用オブジェクトを作成
	 * 
	 * @param ankenId
	 * @param customerType
	 * @return
	 */
	public AccgDocRegistModalInputForm createAccgDocRegistModalInputForm() {
		AccgDocRegistModalInputForm inputForm = new AccgDocRegistModalInputForm();
		return inputForm;
	}

	/**
	 * 顧客候補リストを取得します
	 * 
	 * @param searchWord
	 * @return
	 */
	public List<SelectOptionForm> getCustomerList(String searchWord) {

		List<TPersonEntity> personEntity = tPersonDao.selectPersonOfDealByParams(StringUtils.removeSpaceCharacter(searchWord), CommonConstant.ACCG_DOC_REGIST_CUSTOMER_LIST_LIMIT);
		if (ListUtils.isEmpty(personEntity)) {
			return Collections.emptyList();
		}

		return personEntity.stream().map(entity -> {
			SelectOptionForm form = new SelectOptionForm(entity.getCustomerId(), PersonName.fromEntity(entity).getName()
					+ "（" + CustomerId.of(entity.getCustomerId()).toString() + "）");
			return form;
		}).collect(Collectors.toList());
	}

	/**
	 * 名簿情報を取得します<br>
	 * 
	 * @param personId
	 * @return
	 * @throws AppException
	 */
	public PersonDto getPerson(Long personId) throws AppException {
		TPersonEntity entity = tPersonDao.selectPersonByPersonId(personId);
		if (entity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		PersonDto dto = new PersonDto();
		dto.setPersonId(PersonId.of(entity.getPersonId()));
		dto.setPersonName(PersonName.fromEntity(entity).getName());

		return dto;
	}

	/**
	 * 案件リストを取得します
	 * 
	 * @param personId
	 * @return
	 */
	public List<SelectOptionForm> getAnkenList(Long personId) {

		List<TAnkenEntity> ankenEntityList = tAnkenDao.selectByCustomerId(personId);
		if (ListUtils.isEmpty(ankenEntityList)) {
			return Collections.emptyList();
		}

		return ankenEntityList.stream().map(entity -> {
			SelectOptionForm form = new SelectOptionForm(entity.getAnkenId(),
					StringUtils.isEmpty(entity.getAnkenName()) ? "(案件名未入力)" + "（" + AnkenId.of(entity.getAnkenId()).toString() + "）" : entity.getAnkenName() + "（" + AnkenId.of(entity.getAnkenId()).toString() + "）");
			return form;
		}).collect(Collectors.toList());
	}

	/**
	 * 請求書を作成します
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	public Long registInvoice(Long personId, Long ankenId) throws AppException {

		// DB整合性チェック
		this.registAccgDocDbValidate(personId, ankenId);

		// 請求書作成
		Long invoiceSeq = commonAccgService.registInvoice(personId, ankenId);

		return invoiceSeq;
	}

	/**
	 * 請求書を作成し、報酬や預り金を紐づけます。
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeSeqList
	 * @param depositRecvSeqList
	 * @return
	 * @throws AppException
	 */
	public Long registInvoice(Long personId, Long ankenId, List<Long> feeSeqList, List<Long> depositRecvSeqList) throws AppException {

		// 請求書・精算書登録のDB整合性チェック
		this.registAccgDocDbValidate(personId, ankenId);

		// 報酬データのステータスが「未請求」かチェック
		this.checkFeePaymentStatusIsUnclaimed(feeSeqList);

		// 請求書作成（請求項目のデータも登録）
		Long invoiceSeq = commonAccgService.registInvoice(personId, ankenId, feeSeqList, depositRecvSeqList);

		// 請求項目情報取得
		Long accgDocSeq = commonAccgService.getAccgDocSeqByInvoiceSeq(invoiceSeq);
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// ※ 上記のDtoの取得では、Dtoの値はDBの値を元に設定している。
		// 　 この時点では、消費税額のテーブルと、源泉徴収額のテーブルのデータはDBに登録しておらず、DBにデータがない状態
		// 　 （Dtoに設定されている消費税額、源泉徴収額は正しくない状態）のため、
		// 　 下記で、Dtoに設定されている金額データを元に、消費税額、源泉徴収額を計算し、Dtoに再設定する。
		
		// Dtoの値をもとに消費税額、源泉徴収額を再計算し、
		// 請求項目-消費税、請求項目-源泉徴収税のデータを更新
		// ※メソッドの中で、Dtoに設定されている消費税額、源泉徴収税の値が、更新値で変更される
		commonAccgService.recalcAndUpdateAccgInvoiceTaxAndWithholding(accgDocSeq, accgInvoiceStatementAmountDto);
		
		// 請求書の請求額変更
		commonAccgService.updateTAccgInvoiceAmount(accgInvoiceStatementAmountDto, accgDocSeq);
		
		return invoiceSeq;
	}

	/**
	 * 精算書を作成します
	 * 
	 * @param personId
	 * @param ankenId
	 * @return
	 */
	public Long registStatement(Long personId, Long ankenId) throws AppException {

		// DB整合性チェック
		this.registAccgDocDbValidate(personId, ankenId);

		// 精算書作成
		Long statementSeq = commonAccgService.registStatement(personId, ankenId);

		return statementSeq;
	}

	/**
	 * 精算書を作成し、報酬や預り金を紐づけます。
	 * 
	 * @param personId
	 * @param ankenId
	 * @param feeSeqList
	 * @param depositRecvSeqList
	 * @return
	 * @throws AppException
	 */
	public Long registStatement(Long personId, Long ankenId, List<Long> feeSeqList, List<Long> depositRecvSeqList) throws AppException {

		// 請求書・精算書登録のDB整合性チェック
		this.registAccgDocDbValidate(personId, ankenId);

		// 報酬データのステータスが「未請求」かチェック
		this.checkFeePaymentStatusIsUnclaimed(feeSeqList);

		// 精算書作成
		Long statementSeq = commonAccgService.registStatement(personId, ankenId, feeSeqList, depositRecvSeqList);

		// 請求項目情報取得
		Long accgDocSeq = commonAccgService.getAccgDocSeqByStatementSeq(statementSeq);
		AccgInvoiceStatementAmountDto accgInvoiceStatementAmountDto = commonAccgAmountService.getAccgInvoiceStatementAmountDto(accgDocSeq);

		// ※ 上記のDtoの取得では、Dtoの値はDBの値を元に設定している。
		// 　 この時点では、消費税額のテーブルと、源泉徴収額のテーブルのデータはDBに登録しておらず、DBにデータがない状態
		// 　 （Dtoに設定されている消費税額、源泉徴収額は正しくない状態）のため、
		// 　 下記で、Dtoに設定されている金額データを元に、消費税額、源泉徴収額を計算し、Dtoに再設定する。
		
		// Dtoの値をもとに消費税額、源泉徴収額を再計算し、
		// 請求項目-消費税、請求項目-源泉徴収税のデータを更新
		// ※メソッドの中で、Dtoに設定されている消費税額、源泉徴収税の値が、更新値で変更される
		commonAccgService.recalcAndUpdateAccgInvoiceTaxAndWithholding(accgDocSeq, accgInvoiceStatementAmountDto);

		// 精算書の精算額の変更
		commonAccgService.updateTAccgStatementAmount(accgInvoiceStatementAmountDto, accgDocSeq);
		
		return statementSeq;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 請求書・精算書登録のDB整合性バリデーション
	 * 
	 * @param personId
	 * @param ankenId
	 * @throws AppException
	 */
	private void registAccgDocDbValidate(Long personId, Long ankenId) throws AppException {

		// 対象名簿が「弁護士」になっている場合は顧客登録は不可（登録候補の検索時にも表示されない）
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (personEntity == null || CustomerType.LAWYER == CustomerType.of(personEntity.getCustomerType())) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 選択した案件の存在チェック
		TAnkenEntity tAnkenEntity = tAnkenDao.selectByAnkenId(ankenId);
		if (tAnkenEntity == null) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

	/**
	 * 報酬ステータスが全て「未請求」かチェックします<br>
	 * 
	 * @param feeSeqList
	 * @throws AppException
	 */
	private void checkFeePaymentStatusIsUnclaimed(List<Long> feeSeqList) throws AppException {
		boolean isFeePaymentStatusOfAllUnclaimed = commonAccgService.checkFeePaymentStatusIsUnclaimed(feeSeqList);
		
		if (!isFeePaymentStatusOfAllUnclaimed) {
			// 楽観エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}
}
