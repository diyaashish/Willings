package jp.loioz.app.user.gyomuHistory.service.Anken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryEditByAnkenInputForm;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryEditByAnkenViewForm;
import jp.loioz.app.user.gyomuHistory.form.Anken.GyomuHistoryEditByAnkenViewForm.Customer;
import jp.loioz.app.user.gyomuHistory.form.Common.CommonInputForm;
import jp.loioz.app.user.gyomuHistory.service.Common.CommonGyomuHistoryService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TGyomuHistoryAnkenDao;
import jp.loioz.dao.TGyomuHistoryCustomerDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.SaibanRelateJikenBean;
import jp.loioz.entity.TGyomuHistoryAnkenEntity;
import jp.loioz.entity.TGyomuHistoryCustomerEntity;
import jp.loioz.entity.TGyomuHistoryEntity;
import jp.loioz.entity.TPersonEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class GyomuHistoryEditByAnkenService extends DefaultService {

	/** 業務履歴 共通ロジック */
	@Autowired
	private CommonGyomuHistoryService commonGyomuHistoryService;

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;

	/** 裁判Daoクラス */
	@Autowired
	private TSaibanDao tSaibanDao;

	/** 業務履歴-顧客Daoクラス */
	@Autowired
	private TGyomuHistoryCustomerDao tGyomuHistoryCustomerDao;

	/** 業務履歴-顧客Daoクラス */
	@Autowired
	private TGyomuHistoryAnkenDao tGyomuHistoryAnkenDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	/**
	 * viewFormオブジェクトの作成処理
	 * 
	 * <pre>
	 * 業務履歴 案件側の登録・編集モーダル
	 * ※ ModelAttribute
	 * </pre>
	 * 
	 * 
	 * @param ankenId
	 * @return
	 */
	public GyomuHistoryEditByAnkenViewForm createViewForm(Long ankenId) {

		// viewFormオブジェクトの作成
		GyomuHistoryEditByAnkenViewForm viewForm = new GyomuHistoryEditByAnkenViewForm();

		// 案件に紐づく顧客情報を取得します。
		List<TPersonEntity> personEntityList = tPersonDao.selectByAnkenId(ankenId);

		// 表示用顧客情報を作成
		List<Customer> customerInfo = personEntityList.stream()
				.map(entity -> {
					return Customer.builder()
							.customerId(entity.getCustomerId())
							.customerName(PersonName.fromEntity(entity).getName())
							.build();
				})
				.collect(Collectors.toList());
		viewForm.setCustomerInfo(customerInfo);

		// 案件に紐づく裁判情報を取得します。
		List<SaibanRelateJikenBean> jikenInfoBeans = tSaibanDao.selectParentJikenByAnkenId(ankenId);

		// 裁判情報をセレクトボックスとして作成します
		List<SelectOptionForm> saibanSelectBox = jikenInfoBeans.stream()
				.map(bean -> {
					StringBuilder label = new StringBuilder();
					label.append(CommonConstant.SPACE);
					label.append(CaseNumber.of(
							EraType.of(bean.getJikenGengo()),
							bean.getJikenYear(),
							bean.getJikenMark(),
							bean.getJikenNo()));
					label.append(CommonConstant.SPACE);
					label.append(StringUtils.isEmpty(bean.getJikenName()) ? "(事件名未入力)" : bean.getJikenName());
					return new SelectOptionForm(bean.getSaibanSeq(), label.toString());
				})
				.collect(Collectors.toList());
		viewForm.setSaibanSelectBox(saibanSelectBox);

		return viewForm;
	}

	/**
	 * 入力フォームの初期値を設定します。
	 * 
	 * @param viewForm
	 * @return
	 */
	public GyomuHistoryEditByAnkenInputForm createInputForm(Long ankenId) {

		// 現在日付を取得
		LocalDateTime now = LocalDateTime.now();
		// 遷移元情報と初期値をセットします。
		GyomuHistoryEditByAnkenInputForm inputForm = new GyomuHistoryEditByAnkenInputForm();
		inputForm.setAnkenId(ankenId);
		inputForm.setSupportedDate(DateUtils.parseToString(now.toLocalDate(), DateUtils.DATE_FORMAT_SLASH_DELIMITED));
		inputForm.setSupportedTime(DateUtils.parseToString(now.toLocalTime(), DateUtils.TIME_FORMAT_HHMM));

		return inputForm;
	}

	/**
	 * 業務履歴情報を取得し、セットします。
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void setGyomuHistoryEditDatas(Long gyomuHistorySeq, Long transitionAnkenId, GyomuHistoryEditByAnkenInputForm inputForm) throws AppException {

		// 業務履歴情報の取得(共通部分)
		CommonInputForm commonInputForm = commonGyomuHistoryService.setCommonGyomuHistoryDatas(gyomuHistorySeq);
		BeanUtils.copyProperties(commonInputForm, inputForm);

		// 案件側は、紐づく顧客情報を取得
		List<TGyomuHistoryCustomerEntity> tGyomuHistoryCustomerEntities = tGyomuHistoryCustomerDao.selectByParentSeq(gyomuHistorySeq);

		// 業務履歴-顧客情報が取得できた場合、
		// 取得したデータの顧客IDをリスト化して、Formにセットする。
		if (!tGyomuHistoryCustomerEntities.isEmpty()) {
			List<Long> customerIdList = tGyomuHistoryCustomerEntities.stream()
					.map(TGyomuHistoryCustomerEntity::getCustomerId)
					.collect(Collectors.toList());
			inputForm.setCustomerIdList(customerIdList);
		} else {
			inputForm.setCustomerIdList(Collections.emptyList());
		}

		// 案件軸側のため、遷移元案件IDをFormにセットする(1件)
		inputForm.setAnkenId(transitionAnkenId);
	}

	/**
	 * 業務履歴の新規登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void createGyomuHistory(
			GyomuHistoryEditByAnkenInputForm inputForm) throws AppException {

		// 整合性チェック 案件IDに紐づく顧客かどうか
		if (!inputForm.getCustomerIdList().isEmpty()
				&& commonGyomuHistoryService.validReldatingCustomerByAnkenId(inputForm.getAnkenId(), inputForm.getCustomerIdList())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 整合性チェック 案件IDに紐づく裁判SEQかどうか
		if (Objects.nonNull(inputForm.getSaibanSeq())
				&& commonGyomuHistoryService.validReldatingSaibanByAnkenId(inputForm.getSaibanSeq(), inputForm.getAnkenId())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 共通Formオブジェクトの作成
		CommonInputForm commonInputForm = new CommonInputForm();
		// 入力値を共通Formオブジェクトにコピー
		BeanUtils.copyProperties(inputForm, commonInputForm);
		// 新規登録時には必ず遷移元種別を入れる
		commonInputForm.setTransitionType(TransitionType.ANKEN.getCd());

		// 共通：登録処理
		// 業務履歴(親テーブルのみ)の登録処理を行います
		// ※ 戻り値は、登録後Entityデータ
		TGyomuHistoryEntity insertedEntity = commonGyomuHistoryService.insertGyomuHistory(commonInputForm);

		// 子テーブルの登録処理
		insertGyomuHistoryAnken(insertedEntity.getGyomuHistorySeq(), inputForm.getAnkenId());
		insertGyomuHistoryCustomer(insertedEntity.getGyomuHistorySeq(), inputForm.getCustomerIdList());
	}

	/**
	 * 業務履歴の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateGyomuHistory(GyomuHistoryEditByAnkenInputForm inputForm) throws AppException {

		// 整合性チェック 案件IDに紐づく顧客かどうか
		if (!inputForm.getCustomerIdList().isEmpty()
				&& commonGyomuHistoryService.validReldatingCustomerByAnkenId(inputForm.getAnkenId(), inputForm.getCustomerIdList())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 整合性チェック 案件IDに紐づく裁判SEQかどうか
		if (Objects.nonNull(inputForm.getSaibanSeq())
				&& commonGyomuHistoryService.validReldatingSaibanByAnkenId(inputForm.getSaibanSeq(), inputForm.getAnkenId())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 共通Formオブジェクトの作成
		CommonInputForm commonInputForm = new CommonInputForm();
		// 入力値を共通Formオブジェクトにコピー
		BeanUtils.copyProperties(inputForm, commonInputForm);

		// 共通：更新処理
		// ※ 戻り値は、更新後Entityデータ
		TGyomuHistoryEntity updatedEntity = commonGyomuHistoryService.updateGyomuHistory(commonInputForm);

		// 削除登録処理
		commonGyomuHistoryService.deleteGyomuHistoryCustomer(updatedEntity.getGyomuHistorySeq());
		insertGyomuHistoryCustomer(updatedEntity.getGyomuHistorySeq(), inputForm.getCustomerIdList());

	}

	/**
	 * 業務履歴の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateGyomuHistoryStatus(
			GyomuHistoryEditByAnkenInputForm inputForm) throws AppException {

		// 共通Formオブジェクトの作成
		CommonInputForm commonInputForm = new CommonInputForm();
		// 入力値を共通Formオブジェクトにコピー
		BeanUtils.copyProperties(inputForm, commonInputForm);

		// ステータスのみ更新する
		commonGyomuHistoryService.updateGyomuHistoryStatus(commonInputForm);
	}

	/**
	 * 業務履歴の削除処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void deleteGyomuHistory(
			GyomuHistoryEditByAnkenInputForm inputForm) throws AppException {

		// 共通Formオブジェクトの作成
		CommonInputForm commonInputForm = new CommonInputForm();
		// 入力値を共通Formオブジェクトにコピー
		BeanUtils.copyProperties(inputForm, commonInputForm);

		// 共通：親テーブル削除処理
		// ※ 戻り値は、削除したEntityデータ
		TGyomuHistoryEntity deletedEntity = commonGyomuHistoryService.deleteGyomuHistory(commonInputForm);

		// 子テーブルの削除処理
		commonGyomuHistoryService.deleteGyomuHistoryAnken(deletedEntity.getGyomuHistorySeq());
		commonGyomuHistoryService.deleteGyomuHistoryCustomer(deletedEntity.getGyomuHistorySeq());
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 業務履歴-案件の登録
	 * 
	 * <pre>
	 * 案件軸の為、1件のみ想定
	 * </pre>
	 * 
	 * @param gyoumuHistorySeq
	 * @param ankenId
	 * @throws AppException
	 */
	private void insertGyomuHistoryAnken(
			Long gyoumuHistorySeq,
			Long ankenId) {

		// 業務履歴-案件情報の設定
		TGyomuHistoryAnkenEntity gyomuHistoryAnkenEntity = new TGyomuHistoryAnkenEntity();
		gyomuHistoryAnkenEntity.setGyomuHistorySeq(gyoumuHistorySeq);
		gyomuHistoryAnkenEntity.setAnkenId(ankenId);

		// 登録処理(1件)
		tGyomuHistoryAnkenDao.insert(gyomuHistoryAnkenEntity);
	}

	/**
	 * 業務履歴-顧客の登録
	 * 
	 * <pre>
	 * 案件軸の為、複数件を想定
	 * </pre>
	 * 
	 * @param gyoumuHistorySeq
	 * @param ankenId
	 * @throws AppException
	 */
	private void insertGyomuHistoryCustomer(
			Long gyomuHistorySeq,
			List<Long> customerIdList) {

		// // 誰にも紐づけない場合は、何もしない
		if (customerIdList.isEmpty()) {
			return;
		}

		// 紐づける業務履歴-案件情報を作成
		List<TGyomuHistoryCustomerEntity> gyomuHistoryCustomerEntities = new ArrayList<>();
		customerIdList.forEach(customerId -> {
			TGyomuHistoryCustomerEntity entity = new TGyomuHistoryCustomerEntity();
			entity.setGyomuHistorySeq(gyomuHistorySeq);
			entity.setCustomerId(customerId);
			gyomuHistoryCustomerEntities.add(entity);
		});

		// 登録処理
		tGyomuHistoryCustomerDao.insert(gyomuHistoryCustomerEntities);
	}

}
