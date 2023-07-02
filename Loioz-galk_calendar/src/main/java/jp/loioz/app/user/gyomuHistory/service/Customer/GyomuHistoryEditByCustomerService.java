package jp.loioz.app.user.gyomuHistory.service.Customer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.user.gyomuHistory.form.Common.CommonInputForm;
import jp.loioz.app.user.gyomuHistory.form.Customer.GyomuHistoryEditByCustomerInputForm;
import jp.loioz.app.user.gyomuHistory.form.Customer.GyomuHistoryEditByCustomerViewForm;
import jp.loioz.app.user.gyomuHistory.form.Customer.GyomuHistoryEditByCustomerViewForm.Anken;
import jp.loioz.app.user.gyomuHistory.service.Common.CommonGyomuHistoryService;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TGyomuHistoryAnkenDao;
import jp.loioz.dao.TGyomuHistoryCustomerDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.TGyomuHistoryAnkenEntity;
import jp.loioz.entity.TGyomuHistoryCustomerEntity;
import jp.loioz.entity.TGyomuHistoryEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class GyomuHistoryEditByCustomerService extends DefaultService {

	/** 業務履歴 共通処理 */
	@Autowired
	private CommonGyomuHistoryService commonGyomuHistoryService;

	/** 分野 共通処理 */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 業務履歴-案件Dao */
	@Autowired
	private TGyomuHistoryAnkenDao tGyomuHistoryAnkenDao;

	/** 業務履歴-顧客Dao */
	@Autowired
	private TGyomuHistoryCustomerDao tGyomuHistoryCustomerDao;

	/** 案件-顧客用のDaoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;
	
	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * viewの作成
	 * 
	 * <pre>
	 * 業務履歴 顧客側の登録・編集モーダル表示用viewを作成します。
	 * ※ ModelAttribute
	 * </pre>
	 * 
	 * @param customerId 遷移元顧客ID
	 * @return モーダルのviewForm
	 */
	public GyomuHistoryEditByCustomerViewForm createViewForm(Long customerId) {
		// viewの作成
		GyomuHistoryEditByCustomerViewForm viewForm = new GyomuHistoryEditByCustomerViewForm();
		
		// 顧客IDに紐づく案件情報を取得します。
		List<AnkenCustomerRelationBean> ankenCustomerBean = tAnkenCustomerDao.selectRelation(null, customerId);
		// 未完了の案件リスト
		List<AnkenCustomerRelationBean> ankenCustomerIncompleteBean = ankenCustomerBean.stream().filter(bean -> (!AnkenStatus.KANRYO.equalsByCode(bean.getAnkenStatus()) && !AnkenStatus.FUJUNIN.equalsByCode(bean.getAnkenStatus()))).collect(Collectors.toList());
		// 完了、不受任の案件リスト
		List<AnkenCustomerRelationBean> ankenCustomerCompletedBean = ankenCustomerBean.stream().filter(bean -> AnkenStatus.KANRYO.equalsByCode(bean.getAnkenStatus()) || AnkenStatus.FUJUNIN.equalsByCode(bean.getAnkenStatus())).collect(Collectors.toList());
		//分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// 表示用案件情報の作成
		
		// 未完了の案件
		List<Anken> ankenInfo = ankenCustomerIncompleteBean.stream()
				.map(bean -> {
					return Anken.builder()
							.ankenId(AnkenId.of(bean.getAnkenId()))
							.bunya(bunyaMap.get(bean.getBunyaId()))
							.ankenName(StringUtils.isEmpty(bean.getAnkenName()) ? "(案件名未入力)" : bean.getAnkenName())
							.labelName(commonBunyaService.bunyaAndAnkenName(bean.getAnkenName(), bean.getBunyaId(), bunyaMap))
							.build();
				})
				.collect(Collectors.toList());
		// 完了、不受任の案件
		List<Anken> ankenInfoCompleted = ankenCustomerCompletedBean.stream()
				.map(bean -> {
					return Anken.builder()
							.ankenId(AnkenId.of(bean.getAnkenId()))
							.bunya(bunyaMap.get(bean.getBunyaId()))
							.ankenName(StringUtils.isEmpty(bean.getAnkenName()) ? "(案件名未入力)" : bean.getAnkenName())
							.labelName(commonBunyaService.bunyaAndAnkenName(bean.getAnkenName(), bean.getBunyaId(), bunyaMap))
							.build();
				})
				.collect(Collectors.toList());
		
		viewForm.setAnkenInfo(ankenInfo);
		viewForm.setAnkenInfoCompleted(ankenInfoCompleted);
		
		return viewForm;
	}

	/**
	 * 入力フォームの初期値を設定します。
	 * 
	 * @param viewForm
	 * @return
	 */
	public GyomuHistoryEditByCustomerInputForm createInputForm(Long customerId) {

		// 現在日付を取得
		LocalDateTime now = LocalDateTime.now();
		// 遷移元情報と初期値をセットします。
		GyomuHistoryEditByCustomerInputForm inputForm = new GyomuHistoryEditByCustomerInputForm();
		inputForm.setCustomerId(customerId);
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
	public void setGyomuHistoryEditDatas(Long gyomuHistorySeq, Long customerId, GyomuHistoryEditByCustomerInputForm inputForm) throws AppException {

		// 業務履歴情報の取得(共通部分)
		CommonInputForm commonInputForm = commonGyomuHistoryService.setCommonGyomuHistoryDatas(gyomuHistorySeq);
		BeanUtils.copyProperties(commonInputForm, inputForm);

		// 顧客側のみ、紐づく案件情報を取得
		List<TGyomuHistoryAnkenEntity> tGyomuHistoryAnkenEntities = tGyomuHistoryAnkenDao.selectByParentSeq(gyomuHistorySeq);

		// 業務履歴-案件情報が取得できた場合、
		// 取得したデータの案件IDをリスト化して、Formにセットする。
		if (!tGyomuHistoryAnkenEntities.isEmpty()) {
			List<Long> ankenIdList = tGyomuHistoryAnkenEntities.stream()
					.map(TGyomuHistoryAnkenEntity::getAnkenId)
					.collect(Collectors.toList());
			inputForm.setAnkenIdList(ankenIdList);
		} else {
			inputForm.setAnkenIdList(Collections.emptyList());
		}

		// 顧客軸側のため、遷移元顧客IDをFormにセットする(1件)
		inputForm.setCustomerId(customerId);
	}

	/**
	 * 業務履歴の新規登録処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void createGyomuHistory(
			GyomuHistoryEditByCustomerInputForm inputForm) throws AppException {

		// 整合性チェック 案件IDに紐づく顧客かどうか
		if (!inputForm.getAnkenIdList().isEmpty()
				&& commonGyomuHistoryService.validReldatingAnkenByCustomerId(inputForm.getCustomerId(), inputForm.getAnkenIdList())) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.error(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 共通Formオブジェクトの作成
		CommonInputForm commonInputForm = new CommonInputForm();
		// 入力値を共通Formオブジェクトにコピー
		BeanUtils.copyProperties(inputForm, commonInputForm);
		// 新規登録時に、必ず遷移元種別をいれる
		commonInputForm.setTransitionType(TransitionType.CUSTOMER.getCd());

		// 共通：登録処理
		// 業務履歴(親テーブルのみ)の登録処理を行います
		// ※ 戻り値は、登録後Entityデータ
		TGyomuHistoryEntity insertedEntity = commonGyomuHistoryService.insertGyomuHistory(commonInputForm);

		// 顧客側 子テーブルの登録
		insertGyomuHistoryCustomer(insertedEntity.getGyomuHistorySeq(), inputForm.getCustomerId());
		insertGyomuHistoryAnken(insertedEntity.getGyomuHistorySeq(), inputForm.getAnkenIdList());

	}

	/**
	 * 業務履歴の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateGyomuHistory(
			GyomuHistoryEditByCustomerInputForm inputForm) throws AppException {

		// 整合性チェック 案件IDに紐づく顧客かどうか
		if (!inputForm.getAnkenIdList().isEmpty()
				&& commonGyomuHistoryService.validReldatingAnkenByCustomerId(inputForm.getCustomerId(), inputForm.getAnkenIdList())) {
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
		commonGyomuHistoryService.deleteGyomuHistoryAnken(updatedEntity.getGyomuHistorySeq());
		insertGyomuHistoryAnken(updatedEntity.getGyomuHistorySeq(), inputForm.getAnkenIdList());

	}

	/**
	 * 業務履歴の更新処理
	 * 
	 * @param inputForm
	 * @throws AppException
	 */
	public void updateGyomuHistoryStatus(
			GyomuHistoryEditByCustomerInputForm inputForm) throws AppException {

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
			GyomuHistoryEditByCustomerInputForm inputForm) throws AppException {

		// 共通Formオブジェクトの作成
		CommonInputForm commonInputForm = new CommonInputForm();
		// 入力値を共通Formオブジェクトにコピー
		BeanUtils.copyProperties(inputForm, commonInputForm);

		// 共通：削除処理
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
	 * 業務履歴-顧客の登録処理
	 * 
	 * <pre>
	 * 顧客軸の為、1件のみ想定
	 * </pre>
	 * 
	 * @param gyomuHistorySeq
	 * @param customerId
	 * @throws AppException
	 */
	private void insertGyomuHistoryCustomer(
			Long gyomuHistorySeq,
			Long customerId) {

		// 業務履歴-顧客情報の設定
		TGyomuHistoryCustomerEntity gyomuHistoryCustomerEntity = new TGyomuHistoryCustomerEntity();
		gyomuHistoryCustomerEntity.setGyomuHistorySeq(gyomuHistorySeq);
		gyomuHistoryCustomerEntity.setCustomerId(customerId);

		// 登録処理
		tGyomuHistoryCustomerDao.insert(gyomuHistoryCustomerEntity);

	}

	/**
	 * 業務履歴-案件の登録
	 * 
	 * <pre>
	 * 顧客軸のため、案件は複数を想定
	 * </pre>
	 * 
	 * @param gyomuHistorySeq
	 * @param ankenIdList
	 * @throws AppException
	 */
	private void insertGyomuHistoryAnken(
			Long gyomuHistorySeq,
			List<Long> ankenIdList) {

		// 誰にも紐づけない場合は、何もしない
		if (ankenIdList.isEmpty()) {
			return;
		}

		// 業務履歴-案件のリストエンティティを作成
		List<TGyomuHistoryAnkenEntity> gyomuHistoryAnkenEntityList = new ArrayList<>();
		ankenIdList.forEach(ankenId -> {
			TGyomuHistoryAnkenEntity gyomuHistoryAnkenEntity = new TGyomuHistoryAnkenEntity();
			gyomuHistoryAnkenEntity.setGyomuHistorySeq(gyomuHistorySeq);
			gyomuHistoryAnkenEntity.setAnkenId(ankenId);
			gyomuHistoryAnkenEntityList.add(gyomuHistoryAnkenEntity);
		});

		// 登録処理
		tGyomuHistoryAnkenDao.insert(gyomuHistoryAnkenEntityList);

	}
}
