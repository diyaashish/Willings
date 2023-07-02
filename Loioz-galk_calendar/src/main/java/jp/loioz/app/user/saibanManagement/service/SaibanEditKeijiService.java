package jp.loioz.app.user.saibanManagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKanyoshaService;
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.app.common.service.CommonScheduleService;
import jp.loioz.app.common.service.CommonSelectBoxService;
import jp.loioz.app.common.validation.accessDB.CommonKanyoshaValidator;
import jp.loioz.app.user.saibanManagement.dto.SaibanCustomerDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKanyoshaViewDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKensatsukanDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKijitsuDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanTantoDto;
import jp.loioz.app.user.saibanManagement.form.SaibanCommonInputForm;
import jp.loioz.app.user.saibanManagement.form.SaibanEditKeijiInputForm;
import jp.loioz.app.user.saibanManagement.form.SaibanEditKeijiViewForm;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.bean.AnkenSaibanKeijiBean;
import jp.loioz.bean.SaibanCustomerBean;
import jp.loioz.bean.SaibanKeijiBasicBean;
import jp.loioz.bean.SaibanKeijiKijitsuBean;
import jp.loioz.bean.SaibanKensatsukanBean;
import jp.loioz.bean.SaibanRelatedKanyoshaBean;
import jp.loioz.bean.SaibanTantoBean;
import jp.loioz.bean.SaibanTsuikisoJikenBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TojishaHyoki;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MSosakikanDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TSaibanAddKeijiDao;
import jp.loioz.dao.TSaibanCustomerDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TSaibanRelatedKanyoshaDao;
import jp.loioz.dao.TSaibanTantoDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.SaibanTsuikisoJikenDto;
import jp.loioz.dto.SaibankanDto;
import jp.loioz.entity.MSosakikanEntity;
import jp.loioz.entity.TSaibanAddKeijiEntity;
import jp.loioz.entity.TSaibanCustomerEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;
import jp.loioz.entity.TSaibanLimitEntity;
import jp.loioz.entity.TSaibanRelatedKanyoshaEntity;

/**
 * 裁判管理（刑事）画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanEditKeijiService extends DefaultService {

	/** 裁判管理系画面の共通サービス */
	@Autowired
	private SaibanCommonService saibanCommonService;

	/** 裁判 共通サービス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 共通プルダウンサービス */
	@Autowired
	private CommonSelectBoxService commonSelectBoxService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 共通予定サービス */
	@Autowired
	private CommonScheduleService commonScheduleService;

	/** 関与者の共通サービス */
	@Autowired
	private CommonKanyoshaService commonkanyoshaService;

	/** 関与者の共通バリデートクラス */
	@Autowired
	private CommonKanyoshaValidator commonkanyoshaValidator;

	@Autowired
	private TSaibanDao tSaibanDao;

	@Autowired
	private TSaibanAddKeijiDao tSaibanAddKeijiDao;

	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	@Autowired
	private TSaibanTantoDao tSaibanTantoDao;

	@Autowired
	private TSaibanCustomerDao tSaibanCustomerDao;

	@Autowired
	private TSaibanRelatedKanyoshaDao tSaibanRelatedKanyoshaDao;

	@Autowired
	private TSaibanLimitDao tSaibanLimitDao;

	@Autowired
	private TAnkenDao tAnkenDao;

	@Autowired
	private MSosakikanDao mSosakikanDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する
	 *
	 * @param saibanSeq
	 * @param ankenId
	 * @return
	 */
	public SaibanEditKeijiViewForm createViewForm(Long saibanSeq, Long ankenId) {

		SaibanEditKeijiViewForm form = new SaibanEditKeijiViewForm();

		// 表示している裁判SEQ
		form.setSelectedSaibanSeq(saibanSeq);
		// 案件情報を設定
		form.setAnkenId(AnkenId.of(ankenId));

		return form;
	}

	/**
	 * 裁判の基本情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public SaibanEditKeijiViewForm.SaibanBasicViewForm createSaibanBasicViewForm(Long saibanSeq) {

		SaibanEditKeijiViewForm.SaibanBasicViewForm viewForm = new SaibanEditKeijiViewForm.SaibanBasicViewForm();

		// 表示フォームは、基本的には入力フォームで保存されたデータを表示する形式になるので、入力フォームの取得処理を活用する。

		// 裁判の基本情報入力フォーム
		SaibanEditKeijiInputForm.SaibanBasicInputForm inputForm = this.createSaibanBasicInputForm(saibanSeq);

		// 表示フォームに値を設定

		// 案件情報
		viewForm.setAnkenId(inputForm.getAnkenId());
		viewForm.setAnkenType(AnkenType.of(inputForm.getAnkenType()));
		viewForm.setBunyaName(inputForm.getBunyaName());
		viewForm.setLawyerSelectType(inputForm.getLawyerSelectType());

		// 裁判情報
		viewForm.setSaibanSeq(saibanSeq);
		viewForm.setSaibanshoId(inputForm.getSaibanshoId());
		viewForm.setSaibanStatus(inputForm.getSaibanStatus());
		viewForm.setSaibanshoName(inputForm.getSaibanshoName());
		viewForm.setKeizokuBuName(inputForm.getKeizokuBuName());
		viewForm.setKeizokuKakariName(inputForm.getKeizokuKakariName());
		viewForm.setKeizokuBuTelNo(inputForm.getKeizokuBuTelNo());
		viewForm.setKeizokuBuFaxNo(inputForm.getKeizokuBuFaxNo());
		viewForm.setTantoShoki(inputForm.getTantoShoki());
		viewForm.setSaibanStartDate(inputForm.getSaibanStartDate());
		viewForm.setSaibanEndDate(inputForm.getSaibanEndDate());
		viewForm.setSaibanResult(inputForm.getSaibanResult());

		// 裁判官リスト
		List<SaibankanDto> saibankanDtoList = saibanCommonService.convertSaibankanInputFormToDto(inputForm.getSaibankanInputFormList());
		viewForm.setSaibankanDtoList(saibankanDtoList);

		// 裁判担当を取得
		List<SaibanTantoBean> saibanTantoBeanList = tSaibanTantoDao.selectBySeqJoinAccount(inputForm.getSaibanSeq());
		// 担当弁護士
		List<SaibanTantoDto> tantoBeangoshiList = saibanTantoBeanList.stream()
				.filter(bean -> TantoType.LAWYER.equalsByCode(bean.getTantoType()))
				.map(bean -> SaibanTantoDto.builder()
						.accountSeq(bean.getAccountSeq())
						.tantoName(CommonUtils.nameFormat(bean.getAccountNameSei(), bean.getAccountNameMei()))
						.mainTantoFlg(bean.getSaibanMainTantoFlg())
						.accountColor(bean.getAccountColor())
						.build())
				.collect(Collectors.toList());
		viewForm.setTantoBengoshiList(tantoBeangoshiList);

		// 担当事務
		List<SaibanTantoDto> tantoJimuList = saibanTantoBeanList.stream()
				.filter(bean -> TantoType.JIMU.equalsByCode(bean.getTantoType()))
				.map(bean -> SaibanTantoDto.builder()
						.accountSeq(bean.getAccountSeq())
						.tantoName(CommonUtils.nameFormat(bean.getAccountNameSei(), bean.getAccountNameMei()))
						.mainTantoFlg(bean.getSaibanMainTantoFlg())
						.accountColor(bean.getAccountColor())
						.build())
				.collect(Collectors.toList());
		viewForm.setTantoJimuList(tantoJimuList);

		// 事件情報

		// 事件Noのフォーマット
		CaseNumber JikenNo = CaseNumber.of(inputForm.getJikenGengo(), inputForm.getJikenYear(), inputForm.getJikenMark(), inputForm.getJikenNo());
		viewForm.setCaseNumber(JikenNo);
		viewForm.setJikenName(inputForm.getJikenName());

		// 追起訴事件の取得・設定
		List<SaibanTsuikisoJikenDto> saibanTsuikisoJikenDtoList = this.getTsuikisoSaibanList(saibanSeq, inputForm.getJikenSeq());
		viewForm.setSaibanTsuikisoJikenDtoList(saibanTsuikisoJikenDtoList);

		// 裁判情報を取得
		TSaibanEntity entity = commonSaibanService.getSaibanEntity(saibanSeq);
		// 裁判の登録件数を取得
		Long saibanCnt = tSaibanDao.selectCountKeijiByAnkenId(entity.getAnkenId());

		// 20件未満は追加登録が可能
		boolean isAddSaiban = false;
		if (saibanCnt.intValue() < CommonConstant.SAIBAN_ADD_LIMIT) {
			// 裁判追加OK
			isAddSaiban = true;
		}
		// 裁判の追加可否を設定
		viewForm.setAddSaiban(isAddSaiban);

		return viewForm;
	}

	/**
	 * 裁判の基本情報入力フォームを取得する
	 * 
	 * @param saibanId
	 * @return SaibanEditKeijiInputForm.SaibanBasicInputForm
	 */
	public SaibanEditKeijiInputForm.SaibanBasicInputForm createSaibanBasicInputForm(Long saibanSeq) {

		// 初期設定済みの入力フォームを取得

		SaibanEditKeijiInputForm.SaibanBasicInputForm inputForm = this.getInitializedSaibanBasicInputForm(saibanSeq);
		// 裁判情報を取得
		// ※フォーム初期化時にデータの存在チェックは行っているので、ここでは行わない
		SaibanKeijiBasicBean saiban = tSaibanDao.selectBySeqParentOnlyKeiji(saibanSeq);

		// 裁判所マスタ名
		inputForm.setSaibanshoMstName(saiban.getSaibanshoMstName());

		String viewSaibanshoName = saiban.getSaibanshoMstName();
		if (StringUtils.isEmpty(viewSaibanshoName)) {
			viewSaibanshoName = saiban.getSaibanshoName();
		}
		// 裁判所
		inputForm.setSaibanshoName(viewSaibanshoName);
		// 裁判所ID
		inputForm.setSaibanshoId(saiban.getSaibanshoId());
		// 係属部
		inputForm.setKeizokuBuName(saiban.getKeizokuBuName());
		// 係属係
		inputForm.setKeizokuKakariName(saiban.getKeizokuKakariName());
		// 係属部電話番号
		inputForm.setKeizokuBuTelNo(saiban.getKeizokuBuTelNo());
		// 係属部FAX番号
		inputForm.setKeizokuBuFaxNo(saiban.getKeizokuBuFaxNo());
		// 担当書記官
		inputForm.setTantoShoki(saiban.getTantoShoki());
		// 申立日
		inputForm.setSaibanStartDate(saiban.getSaibanStartDate());
		// 終了
		inputForm.setSaibanEndDate(saiban.getSaibanEndDate());
		// 結果
		inputForm.setSaibanResult(saiban.getSaibanResult());

		// 事件情報
		// 事件SEQ
		inputForm.setJikenSeq(saiban.getJikenSeq());
		// 事件元号
		inputForm.setJikenGengo(EraType.of(saiban.getJikenGengo()));
		// 事件番号年
		inputForm.setJikenYear(saiban.getJikenYear());
		// 事件番号符号
		inputForm.setJikenMark(saiban.getJikenMark());
		// 事件番号
		inputForm.setJikenNo(saiban.getJikenNo());
		// 事件名
		inputForm.setJikenName(saiban.getJikenName());

		// 裁判官を設定
		List<SaibankanDto> saibankanDtoList = commonSaibanService.getSaibankanList(saibanSeq);
		if (CollectionUtils.isEmpty(saibankanDtoList)) {
			// 登録データがない場合は初期入力項目用のDtoを追加する
			SaibankanDto dto = new SaibankanDto();
			saibankanDtoList.add(dto);
		}
		List<SaibanCommonInputForm.SaibankanInputForm> saibankanInputFormList = saibanCommonService.convertSaibankanDtoToInputForm(saibankanDtoList);
		inputForm.setSaibankanInputFormList(saibankanInputFormList);

		// 裁判担当者を設定
		Map<TantoType, List<AnkenTantoSelectInputForm>> tantoListMap = commonSaibanService.getTantoListMapForEdit(saibanSeq);
		inputForm.setTantoLawyer(tantoListMap.get(TantoType.LAWYER));
		inputForm.setTantoJimu(tantoListMap.get(TantoType.JIMU));

		return inputForm;
	}

	/**
	 * 初期設定済みの裁判の基本情報入力フォームを取得
	 * 
	 * @param saibanId
	 * @return
	 */
	public SaibanEditKeijiInputForm.SaibanBasicInputForm getInitializedSaibanBasicInputForm(Long saibanSeq) {

		SaibanEditKeijiInputForm.SaibanBasicInputForm inputForm = new SaibanEditKeijiInputForm.SaibanBasicInputForm();

		// 表示用データの設定
		this.setDisplayData(inputForm, saibanSeq);

		return inputForm;
	}

	/**
	 * 入力フォームに表示用のデータを設定する。
	 * 
	 * <pre>
	 * リクエストパラメータや、画面の入力項目に表示する値ではない、
	 * 表示処理のみで利用する（HTML、もしくはHTMLから呼ばれるFormのメソッドでのみ参照される）データをinputフォームに設定する。
	 * 
	 * つまり、このメソッドで設定すプロパティがinputフォームから削除された場合、
	 * 影響があるのはHTMLとFormのみとならなければならなず、そうならないような箇所で
	 * このメソッドで設定するプロパティを参照してはいけない。
	 * </pre>
	 * 
	 * @param inputForm
	 * @param saibanId
	 */
	public void setDisplayData(SaibanEditKeijiInputForm.SaibanBasicInputForm inputForm, Long saibanSeq) {

		// 案件情報を取得
		AnkenSaibanKeijiBean ankenSaibanKeijiBean = tAnkenDao.selectKeijiBySaibanSeq(saibanSeq);
		// 裁判情報が存在しない場合
		if (ankenSaibanKeijiBean == null) {
			throw new DataNotFoundException("裁判情報が存在しません。[saibanSeq=" + saibanSeq + "]");
		}

		// 案件ID
		inputForm.setAnkenId(AnkenId.of(ankenSaibanKeijiBean.getAnkenId()));
		// 案件区分
		inputForm.setAnkenType(ankenSaibanKeijiBean.getAnkenType());
		// 分野名
		inputForm.setBunyaName(commonBunyaService.getBunya(ankenSaibanKeijiBean.getBunyaId()).getVal());
		// 私選・国選
		inputForm.setLawyerSelectType(ankenSaibanKeijiBean.getLawyerSelectType());

		// 裁判情報を取得
		TSaibanEntity saiban = this.getSaibanEntity(saibanSeq);
		// 裁判SEQ
		inputForm.setSaibanSeq(saiban.getSaibanSeq());
		// 裁判ステータス
		inputForm.setSaibanStatus(SaibanStatus.calc(saiban.getSaibanStartDate(), saiban.getSaibanEndDate()));

		// 事件元号プルダウン
		List<SelectOptionForm> jikenGengoOptionList = commonSelectBoxService.getSaibanEraType();
		inputForm.setJikenGengoOptionList(jikenGengoOptionList);

		// 裁判所マスタプルダウン
		List<SelectOptionForm> saibanshoOptionList = commonSelectBoxService.getSaibanshoSelectBox();
		inputForm.setSaibanshoOptionList(saibanshoOptionList);

		// 担当（担当弁護士、担当事務）の選択肢
		Map<TantoType, List<SelectOptionForm>> tantoSelectOptListMap = commonSaibanService.getTantoSelectOptListMapForEdit(saibanSeq, ankenSaibanKeijiBean.getAnkenId());
		inputForm.setAnkenTantoBengoshiOptionList(tantoSelectOptListMap.get(TantoType.LAWYER));
		inputForm.setAnkenTantoJimuOptionList(tantoSelectOptListMap.get(TantoType.JIMU));
	}

	/**
	 * 検察官情報入力入力フォームに表示用のデータを設定する。
	 * 
	 * @param inputForm
	 */
	public void setDisplayData(SaibanEditKeijiInputForm.SaibanKensatsukanInputForm inputForm) {
		List<MSosakikanEntity> sosakikanEntityList = mSosakikanDao.selectKensatsu();
		List<SelectOptionForm> kensatsuchoOptionList = sosakikanEntityList.stream()
				.map(entity -> new SelectOptionForm(entity.getSosakikanId(), entity.getSosakikanName()))
				.collect(Collectors.toList());
		inputForm.setKensatsuchoOptions(kensatsuchoOptionList);
	}

	/**
	 * 裁判の基本情報の保存
	 * 
	 * @param saibanId
	 * @param basicInputForm
	 * @throws AppException
	 */
	public void saveSaibanBasic(Long saibanSeq, SaibanEditKeijiInputForm.SaibanBasicInputForm basicInputForm) throws AppException {

		// 裁判の基本情報を更新
		this.updateSaibanBasic(saibanSeq, basicInputForm);

		// 裁判の事件情報を更新
		this.updateSaibanJiken(saibanSeq, basicInputForm);

		// 裁判の裁判官情報を更新
		saibanCommonService.updateSaibanSaibankan(saibanSeq, basicInputForm.getSaibankanInputFormList());

		// 裁判の担当者情報を更新
		this.updateSaibanTanto(saibanSeq, basicInputForm);
	}

	/**
	 * 裁判の期日情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @param isAllTimeKijitsuList true：全ての時間の期日を取得候補とする
	 * false：本日以降の期日、過去直近1件の期日を取得候補とする（本日は含む）
	 * @return
	 */
	public SaibanEditKeijiViewForm.SaibanKijitsuViewForm createSaibanKijitsuViewForm(Long saibanSeq, boolean isAllTimeKijitsuList) {

		SaibanEditKeijiViewForm.SaibanKijitsuViewForm viewForm = new SaibanEditKeijiViewForm.SaibanKijitsuViewForm();

		// 全ての時間の期日を取得するかどうか
		viewForm.setAllTimeKijitsuList(isAllTimeKijitsuList);

		// 裁判期日を取得
		List<SaibanKeijiKijitsuBean> saibanLimitBeanList = this.getSaibanLimitBeanList(saibanSeq, isAllTimeKijitsuList);
		// 表示用に加工
		List<SaibanKijitsuDto> saibanKijitsuDtoList = saibanLimitBeanList.stream()
				.map(bean -> SaibanKijitsuDto.builder()
						.saibanSeq(bean.getSaibanSeq())
						.saibanLimitSeq(bean.getSaibanLimitSeq())
						.scheduleSeq(bean.getScheduleSeq())
						.limitDateCount(bean.getLimitDateCount())
						.subject(bean.getSubject())
						.limitAt(bean.getLimitAt())
						.dateTo(bean.getDataTo())
						.timeTo(bean.getTimeTo())
						.roomId(bean.getRoomId())
						.placeName(bean.getRoomId() != null ? bean.getRoomName() : bean.getPlace())
						.shutteiType(bean.getShutteiType())
						.content(bean.getContent())
						.result(bean.getResult())
						.build())
				.collect(Collectors.toList());
		viewForm.setSaibanKijitsuDtoList(saibanKijitsuDtoList);

		// 次回の期日回数を採番
		long currentMaxSaibanLimitCount = tSaibanLimitDao.getMaxCount(saibanSeq);
		viewForm.setNextSaibanKijitsuCount(currentMaxSaibanLimitCount + 1);

		return viewForm;
	}

	/**
	 * 裁判刑事期日の予定詳細データを取得
	 * 
	 * @param saibanSeq
	 * @param isAllTimeKijitsuList
	 * @return
	 */
	public Map<Long, ScheduleDetail> getSaibanKijitsuDetails(Long saibanSeq, boolean isAllTimeKijitsuList) {

		// 裁判期日を取得
		List<SaibanKeijiKijitsuBean> saibanLimitBeanList = this.getSaibanLimitBeanList(saibanSeq, isAllTimeKijitsuList);

		Set<Long> scheduleSeqSet = saibanLimitBeanList.stream().map(SaibanKeijiKijitsuBean::getScheduleSeq).collect(Collectors.toSet());
		return commonScheduleService.getSchedulePKOne(new ArrayList<>(scheduleSeqSet));
	}

	/**
	 * 裁判期日Beanの取得処理
	 * 
	 * @param saibanSeq
	 * @param isAllTimeKijitsuList
	 * @return
	 */
	private List<SaibanKeijiKijitsuBean> getSaibanLimitBeanList(Long saibanSeq, boolean isAllTimeKijitsuList) {
		return tSaibanLimitDao.selectKeijiKijitsuBySaibanSeq(saibanSeq, isAllTimeKijitsuList, LocalDateTime.now(), SessionUtils.getLoginAccountSeq());
	}

	/**
	 * 裁判 期日結果入力フォームを作成する
	 * 
	 * @param saibanLimitSeq
	 * @return
	 */
	public SaibanEditKeijiInputForm.SaibanKijitsuResultInputForm createSaibanKijitsuResultInputForm(Long saibanLimitSeq) {

		SaibanEditKeijiInputForm.SaibanKijitsuResultInputForm kijitsuResultInputForm = new SaibanEditKeijiInputForm.SaibanKijitsuResultInputForm();

		// 裁判期日SEQ
		kijitsuResultInputForm.setSaibanLimitSeq(saibanLimitSeq);

		// 裁判期日結果
		TSaibanLimitEntity entity = tSaibanLimitDao.selectBySeq(saibanLimitSeq);
		kijitsuResultInputForm.setKijitsuResult(entity.getResult());

		return kijitsuResultInputForm;
	}

	/**
	 * 裁判 期日結果の保存
	 * 
	 * @param kijitsuResultInputForm
	 * @throws AppException
	 */
	public void saveSaibanKijitsuResult(SaibanEditKeijiInputForm.SaibanKijitsuResultInputForm kijitsuResultInputForm)
			throws AppException {

		// 更新対象の期日
		TSaibanLimitEntity updateEntity = tSaibanLimitDao.selectBySeq(kijitsuResultInputForm.getSaibanLimitSeq());
		if (updateEntity == null) {
			// 更新対象が存在しない場合は楽観ロックとする（保存ボタンを押す前に対象データが削除された場合など）
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		updateEntity.setResult(kijitsuResultInputForm.getKijitsuResult());

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tSaibanLimitDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 裁判 顧客情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public SaibanEditKeijiViewForm.SaibanCustomerViewForm createSaibanCustomerViewForm(Long saibanSeq) {

		SaibanEditKeijiViewForm.SaibanCustomerViewForm viewForm = new SaibanEditKeijiViewForm.SaibanCustomerViewForm();

		// 裁判顧客を取得
		List<SaibanCustomerBean> saibanCustomerBean = tSaibanCustomerDao.selectBySeq(saibanSeq);

		// 表示用に加工
		List<SaibanCustomerDto> saibanCustomerDtoList = saibanCustomerBean.stream().map(
				bean -> SaibanCustomerDto.builder()
						.saibanSeq(bean.getSaibanSeq())
						.personId(PersonId.of(bean.getPersonId()))
						.customerId(CustomerId.of(bean.getCustomerId()))
						.customerType(CustomerType.of(bean.getCustomerType()))
						.personAttribute(PersonAttribute.of(bean.getCustomerFlg(), bean.getAdvisorFlg(), bean.getCustomerType()))
						.saibanTojishaHyoki(bean.getSaibanTojishaHyoki())
						.mainFlg(CommonConstant.SystemFlg.codeToBoolean(bean.getMainFlg()))
						.customerName(new PersonName(
								bean.getCustomerNameSei(), bean.getCustomerNameMei(),
								null, null).getName())
						.build())
				.collect(Collectors.toList());
		viewForm.setSaibanCustomerDtoList(saibanCustomerDtoList);

		return viewForm;
	}

	/**
	 * 裁判 顧客追加入力フォームを作成する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public SaibanEditKeijiInputForm.SaibanCustomerAddInputForm createSaibanCustomerAddInputForm(Long saibanSeq, Long ankenId) {

		SaibanEditKeijiInputForm.SaibanCustomerAddInputForm customerAddInputForm = new SaibanEditKeijiInputForm.SaibanCustomerAddInputForm();

		// 裁判に紐づく顧客一覧を取得
		List<SaibanCustomerBean> saibanCustomerBean = tSaibanCustomerDao.selectBySeq(saibanSeq);
		List<Long> saibanCustomerIdList = saibanCustomerBean.stream()
				.map(bean -> bean.getCustomerId())
				.collect(Collectors.toList());

		// 案件に紐づく顧客一覧を取得
		List<AnkenCustomerRelationBean> ankenCustomerBean = tAnkenCustomerDao.selectRelation(ankenId, null);

		// 案件に紐づく顧客のうち、裁判顧客になっていない顧客のみに絞り込む
		List<AnkenCustomerRelationBean> ankenCustomerNotIncludeSaibanCustomerBeanList = ankenCustomerBean.stream()
				.filter(bean -> !saibanCustomerIdList.contains(bean.getCustomerId()))
				.collect(Collectors.toList());

		// 追加候補顧客の選択肢を生成
		List<SaibanEditKeijiInputForm.AddOption> addCustomerCandidateList = ankenCustomerNotIncludeSaibanCustomerBeanList.stream()
				.map(bean -> {
					SaibanEditKeijiInputForm.AddOption addOption = new SaibanEditKeijiInputForm.AddOption();
					addOption.setSeq(bean.getCustomerId());
					addOption.setToAdd(false);
					addOption.setTitle(bean.getCustomerName());
					return addOption;
				})
				.collect(Collectors.toList());

		// フォームに設定
		customerAddInputForm.setAddCustomerCandidateList(addCustomerCandidateList);

		return customerAddInputForm;
	}

	/**
	 * 裁判の顧客追加の登録
	 * 
	 * @param saibanSeq
	 * @param ankenId
	 * @param customerAddInputForm
	 * @throws AppException
	 */
	public void registSaibanCustomerAdd(Long saibanSeq, Long ankenId, SaibanEditKeijiInputForm.SaibanCustomerAddInputForm customerAddInputForm)
			throws AppException {

		// 追加を行う顧客のIDリストをフォームから取得
		List<SaibanEditKeijiInputForm.AddOption> addCustomerCandidateList = customerAddInputForm.getAddCustomerCandidateList();
		List<Long> addCustomerIdList = addCustomerCandidateList.stream()
				.filter(candidate -> candidate.isToAdd())
				.map(candidate -> candidate.getSeq())
				.collect(Collectors.toList());

		// 登録前バリデーション（楽観ロックエラーチェック）
		this.registSaibanCustomerValidation(ankenId, saibanSeq, addCustomerIdList);

		// 登録処理
		commonSaibanService.registSaibanCustomer(saibanSeq, addCustomerIdList);
	}

	/**
	 * 裁判 顧客削除
	 * 
	 * @param saibanSeq
	 * @param customerId
	 * @throws AppException
	 */
	public void removeSaibanCustomer(Long saibanSeq, Long customerId) throws AppException {

		commonSaibanService.deleteSaibanCustomer(saibanSeq, customerId);
	}

	/**
	 * 裁判 共犯者 情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public SaibanEditKeijiViewForm.SaibanOtherViewForm createSaibanOtherViewForm(Long saibanSeq) {

		SaibanEditKeijiViewForm.SaibanOtherViewForm viewForm = new SaibanEditKeijiViewForm.SaibanOtherViewForm();

		// 共犯者情報の取得
		List<SaibanRelatedKanyoshaBean> saibanRelatedKanyoshaBeanList = commonSaibanService.getSaibanRelatedKanyoshaBeanList(saibanSeq, KanyoshaType.KYOHANSHA, SystemFlg.FLG_OFF);

		// 共犯者代理人情報の取得
		List<SaibanRelatedKanyoshaBean> saibanRelatedKanyoshaDairininBeanList = commonSaibanService.getSaibanRelatedKanyoshaBeanList(saibanSeq, KanyoshaType.KYOHANSHA, SystemFlg.FLG_ON);

		// 画面表示用に加工
		List<SaibanKanyoshaViewDto> saibanKyohanshaDtoList = commonSaibanService.generateKanyoshaViewDto(saibanRelatedKanyoshaBeanList, saibanRelatedKanyoshaDairininBeanList);
		viewForm.setSaibanKyohanshaDtoList(saibanKyohanshaDtoList);

		return viewForm;
	}

	/**
	 * 当事者の筆頭を更新する
	 * 
	 * <pre>
	 * 引数で受けるcustomerId、kanyoshaSeqのデータについて、
	 * 引数のisHittouがtrueの場合にのみ「筆頭」状態に更新し、それ以外の当事者データを「筆頭」ではない状態に更新する。
	 * （引数のisHittouがfalseの場合は、全ての当事者データが「筆頭」ではない状態で更新される。）
	 * </pre>
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param customerId 更新対象の顧客
	 * @param kanyoshaSeq 更新対象の関与者
	 * @param toHittou 「筆頭」とするかどうか。false場合は「筆頭」としない。
	 */
	public void saveTojishaHittou(Long saibanSeq, Long customerId, Long kanyoshaSeq, boolean toHittou) throws AppException {

		// 裁判顧客を取得
		List<TSaibanCustomerEntity> tSaibanCustomerEntity = tSaibanCustomerDao.selectBySaibanSeq(saibanSeq);

		// 顧客の筆頭更新の場合 該当顧客が存在しない場合エラーとする
		if (customerId != null && tSaibanCustomerEntity.stream().noneMatch(e -> Objects.equals(e.getCustomerId(), customerId))) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 筆頭を初期化し、顧客IDがパラメータとして送信されていたら、その顧客を筆頭にする
		tSaibanCustomerEntity.forEach(e -> {
			e.setMainFlg(SystemFlg.FLG_OFF.getCd());
			// 対象の顧客を筆頭にする
			if (toHittou && e.getCustomerId().equals(customerId)) {
				e.setMainFlg(SystemFlg.FLG_ON.getCd());
			}
		});
		// 筆頭を更新する
		tSaibanCustomerDao.update(tSaibanCustomerEntity);

		// 関与者（共犯者）を取得
		List<TSaibanRelatedKanyoshaEntity> tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectNotDairininBySeq(saibanSeq,
				KanyoshaType.KYOHANSHA.getCd());

		// 顧客の筆頭更新の場合 該当顧客が存在しない場合エラーとする
		if (kanyoshaSeq != null && tSaibanRelatedKanyoshaEntity.stream().noneMatch(e -> Objects.equals(e.getKanyoshaSeq(), kanyoshaSeq))) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 筆頭を初期化し、顧客IDがパラメータとして送信されていたら、その顧客を筆頭にする
		tSaibanRelatedKanyoshaEntity.forEach(e -> {
			e.setMainFlg(SystemFlg.FLG_OFF.getCd());
			// 対象の関与者を筆頭にする
			if (toHittou && e.getKanyoshaSeq().equals(kanyoshaSeq)) {
				e.setMainFlg(SystemFlg.FLG_ON.getCd());
			}
		});
		// 筆頭を更新する
		tSaibanRelatedKanyoshaDao.update(tSaibanRelatedKanyoshaEntity);
	}

	/**
	 * 公判担当検察官 表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @return saibanKensatsukanViewForm
	 */
	public SaibanEditKeijiViewForm.SaibanKensatsukanViewForm createSaibanKensatsukanViewForm(Long saibanSeq) {

		SaibanEditKeijiViewForm.SaibanKensatsukanViewForm viewForm = new SaibanEditKeijiViewForm.SaibanKensatsukanViewForm();

		// 公判担当検察官を取得
		SaibanKensatsukanBean bean = tSaibanAddKeijiDao.selectJoinSosakikanBySeq(saibanSeq);

		// 画面表示用に加工
		SaibanKensatsukanDto saibanKensatsukanDto = convert2Dto(bean);

		viewForm.setSaibanKensatsukanDto(saibanKensatsukanDto);

		return viewForm;
	}

	/**
	 * Dto -> Bean
	 * 
	 * @param bean
	 * @return
	 */
	private SaibanKensatsukanDto convert2Dto(SaibanKensatsukanBean bean) {
		SaibanKensatsukanDto saibanKensatsukanDto = new SaibanKensatsukanDto();
		saibanKensatsukanDto.setSaibanSeq(bean.getSaibanSeq());
		saibanKensatsukanDto.setMainJikenSeq(bean.getMainJikenSeq());
		saibanKensatsukanDto.setSosakikanId(bean.getSosakikanId());
		saibanKensatsukanDto.setKensatsuchoName(bean.getKensatsuchoName());
		saibanKensatsukanDto.setKensatsuchoTantoBuName(bean.getKensatsuchoTantoBuName());
		saibanKensatsukanDto.setKensatsukanName(bean.getKensatsukanName());
		saibanKensatsukanDto.setKensatsukanNameKana(bean.getKensatsukanNameKana());
		saibanKensatsukanDto.setJimukanName(bean.getJimukanName());
		saibanKensatsukanDto.setJimukanNameKana(bean.getJimukanNameKana());
		saibanKensatsukanDto.setKensatsuTelNo(bean.getKensatsuTelNo());
		saibanKensatsukanDto.setKensatsuExtensionNo(bean.getKensatsuExtensionNo());
		saibanKensatsukanDto.setKensatsuFaxNo(bean.getKensatsuFaxNo());
		saibanKensatsukanDto.setKensatsuRoomNo(bean.getKensatsuRoomNo());
		saibanKensatsukanDto.setKensatsuRemarks(bean.getKensatsuRemarks());
		return saibanKensatsukanDto;
	}

	/**
	 * 公判担当検察官 表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @return saibanKensatsukanInputForm
	 */
	public SaibanEditKeijiInputForm.SaibanKensatsukanInputForm createSaibanKensatsukanInputForm(Long saibanSeq) {

		SaibanEditKeijiInputForm.SaibanKensatsukanInputForm inputForm = new SaibanEditKeijiInputForm.SaibanKensatsukanInputForm();

		// 公判担当検察官を取得
		TSaibanAddKeijiEntity tSaibanAddKeijiEntity = tSaibanAddKeijiDao.selectBySaibanSeq(saibanSeq);

		if (tSaibanAddKeijiEntity != null) {

			inputForm.setKensatsuchoTantoBuName(tSaibanAddKeijiEntity.getKensatsuchoTantoBuName());
			inputForm.setKensatsuTelNo(tSaibanAddKeijiEntity.getKensatsuTelNo());
			inputForm.setKensatsuExtensionNo(tSaibanAddKeijiEntity.getKensatsuExtensionNo());
			inputForm.setKensatsuFaxNo(tSaibanAddKeijiEntity.getKensatsuFaxNo());
			inputForm.setKensatsuRoomNo(tSaibanAddKeijiEntity.getKensatsuRoomNo());
			inputForm.setKensatsukanName(tSaibanAddKeijiEntity.getKensatsukanName());
			inputForm.setJimukanName(tSaibanAddKeijiEntity.getJimukanName());
			inputForm.setKensatsuRemarks(tSaibanAddKeijiEntity.getKensatsuRemarks());

			Long sosakikanId = tSaibanAddKeijiEntity.getSosakikanId();
			if (sosakikanId != null) {

				// マスタに登録されている検察庁が設定されている場合
				MSosakikanEntity mSosakikanEntity = mSosakikanDao.selectById(tSaibanAddKeijiEntity.getSosakikanId());
				inputForm.setSosakikanId(tSaibanAddKeijiEntity.getSosakikanId());
				inputForm.setKensatsuchoName(mSosakikanEntity.getSosakikanName());

			} else {

				// 検察庁が自由入力されている場合
				inputForm.setKensatsuchoName(tSaibanAddKeijiEntity.getKensatsuchoName());

			}
		}

		// 検察庁
		this.setDisplayData(inputForm);

		return inputForm;
	}

	/**
	 * 公判担当検察官の保存
	 * 
	 * @param saibanSeq
	 * @param inputForm
	 * @throws AppException
	 */
	public void saveKensatsukan(Long saibanSeq, SaibanEditKeijiInputForm.SaibanKensatsukanInputForm inputForm) throws AppException {

		// 既にDBに登録されている情報
		TSaibanAddKeijiEntity saibanAddKeijiEntity = tSaibanAddKeijiDao.selectBySaibanSeq(saibanSeq);

		// ----------------------------------------------------------
		// 登録・更新処理
		// ----------------------------------------------------------
		if (saibanAddKeijiEntity == null) {
			// 登録
			TSaibanAddKeijiEntity newEntity = new TSaibanAddKeijiEntity();
			newEntity.setSaibanSeq(saibanSeq);
			this.setSaibanAddKeijiEntity(newEntity, inputForm);
			tSaibanAddKeijiDao.insert(newEntity);

		} else {
			// 件数チェック用
			int updateCount = 0;

			try {
				// 更新
				this.setSaibanAddKeijiEntity(saibanAddKeijiEntity, inputForm);
				updateCount = tSaibanAddKeijiDao.update(saibanAddKeijiEntity);

			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());
				throw new AppException(MessageEnum.MSG_E00025, ex);
			}

			if (updateCount != 1) {
				// 更新件数エラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
				throw new AppException(MessageEnum.MSG_E00013, null);
			}

		}

	}

	/**
	 * 削除処理<br>
	 *
	 * @param saibanSeq
	 * @throws AppException
	 */
	public Long deleteSaiban(Long saibanSeq) throws AppException {

		// 裁判の関連情報を削除
		commonSaibanService.deleteKeijiSaibanRelated(saibanSeq);

		// 裁判情報を削除
		TSaibanEntity entity = tSaibanDao.selectBySeq(saibanSeq);
		// 削除する案件ID
		Long ankenId = entity.getAnkenId();
		tSaibanDao.delete(entity);

		return ankenId;
	}

	/**
	 * 裁判 顧客の当事者表記の更新
	 * 
	 * @param saibanSeq
	 * @param customerId
	 * @param tojishaHyoki
	 * @throws AppException
	 */
	public void saveCustomerTojishaHyoki(Long saibanSeq, Long customerId, TojishaHyoki tojishaHyoki) throws AppException {

		// 更新
		this.updateSaibanCustomerTojishaHyoki(saibanSeq, customerId, tojishaHyoki);

	}

	/**
	 * 共犯者の当事者表記の更新
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param kanyoshaSeq 関与者SEQ
	 * @param tojishaHyoki 当事者表記
	 * @throws AppException
	 */
	public void saveKanyoshaTojishaHyoki(Long saibanSeq, Long kanyoshaSeq, TojishaHyoki tojishaHyoki) throws AppException {

		// 更新
		this.updateSaibanRelatedKanyoshaTojishaHyoki(saibanSeq, kanyoshaSeq, tojishaHyoki);

	}

	/**
	 * 共犯者の削除
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param kanyoshaSeq 関与者SEQ
	 * @throws AppException
	 */
	public void removeSaibanKanyosha(Long saibanSeq, Long kanyoshaSeq) throws AppException {

		// 削除する前に代理人情報を取得
		TSaibanRelatedKanyoshaEntity kanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (kanyoshaEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		Long dairininKanyoshaSeq = kanyoshaEntity.getRelatedKanyoshaSeq();

		// 削除
		this.deleteSaibanKanyosha(saibanSeq, kanyoshaSeq);

		if (dairininKanyoshaSeq != null) {
			// 削除対象の関与者に代理人が設定されていた場合、
			// その代理人について、どの関与者からも代理人として利用されなくなった場合は関与者-関係者情報を削除
			commonSaibanService.deleteSaibanRelatedKanyoshaDairininIfNotUsed(saibanSeq, dairininKanyoshaSeq);
		}
	}

	/**
	 * 共犯者の弁護人を外す
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param kanyoshaSeq 関与者SEQ
	 * @throws AppException
	 */
	public void removeSaibanRelatedKanyosha(Long saibanSeq, Long kanyoshaSeq) throws AppException {

		// 外す弁護人（代理人）の関与者IDを事前に取得
		TSaibanRelatedKanyoshaEntity kanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (kanyoshaEntity == null || kanyoshaEntity.getRelatedKanyoshaSeq() == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		Long dairininKanyoshaSeq = kanyoshaEntity.getRelatedKanyoshaSeq();

		// 削除 弁護人（代理人）のIDをnullで更新する
		this.updateRelatedKanyosha(saibanSeq, kanyoshaSeq, null);

		// 外した弁護人（代理人）について、どの関与者からも弁護人（代理人）として利用されなくなった場合は関与者-関係者情報を削除
		commonSaibanService.deleteSaibanRelatedKanyoshaDairininIfNotUsed(saibanSeq, dairininKanyoshaSeq);
	}

	/**
	 * 共犯者を案件（当事者・関与者）から外す
	 * 
	 * @param kanyoshaSeq 関与者SEQ
	 * @throws AppException
	 */
	public Map<String, Object> deleteCommonKanyoshaFromAnkenBeforeCheck(Long kanyoshaSeq) throws AppException {

		Map<String, Object> response = commonkanyoshaService.deleteCommonKanyoshaBeforeCheck(kanyoshaSeq);

		return response;
	}

	/**
	 * 共犯者を案件（当事者・関与者）から外す
	 * 
	 * @param kanyoshaSeq 関与者SEQ
	 * @throws AppException
	 */
	public void deleteCommonKanyoshaFromAnken(Long kanyoshaSeq) throws AppException {
		commonkanyoshaService.deleteCommonKanyoshaInfo(kanyoshaSeq);
	}

	/**
	 * 関与者共通処理で関与者削除可否のチェックを実施する
	 * 
	 * @param kanyoshaSeq
	 * @return
	 */
	public boolean canDeleteKanyosha(Long kanyoshaSeq) {
		return commonkanyoshaValidator.canDeleteKanyosha(kanyoshaSeq);
	}

	// =========================================================================
	// privateメソッド
	// =========================================================================

	/**
	 * 裁判Entityを取得
	 * 
	 * @param saibanSeq
	 * @return
	 */
	private TSaibanEntity getSaibanEntity(Long saibanSeq) {

		TSaibanEntity entity = tSaibanDao.selectBySeq(saibanSeq);

		// 裁判情報が存在しない場合
		if (entity == null) {
			throw new DataNotFoundException("裁判情報が存在しません。[saibanSeq=" + saibanSeq + "]");
		}

		return entity;
	}

	/**
	 * 追起訴裁判を取得
	 * 
	 * @param saibanSeq
	 * @param jikenSeq
	 * @return
	 */
	private List<SaibanTsuikisoJikenDto> getTsuikisoSaibanList(Long saibanSeq, Long jikenSeq) {

		// 追起訴裁判を取得
		List<SaibanTsuikisoJikenBean> saibanTsuikisoJikenBeanList = tSaibanJikenDao.selectTsuikisoBySaibanSeq(saibanSeq, jikenSeq);

		// 表示用に加工
		List<SaibanTsuikisoJikenDto> saibanTsuikisoJikenDtoList = saibanTsuikisoJikenBeanList.stream()
				.map(bean -> SaibanTsuikisoJikenDto.builder()
						.saibanSeq(bean.getSaibanSeq())
						.jikenSeq(bean.getJikenSeq())
						.jikenName(bean.getJikenName())
						.jikenNo(CaseNumber.of(EraType.of(bean.getJikenGengo()), bean.getJikenYear(), bean.getJikenMark(), bean.getJikenNo()))
						.build())
				.collect(Collectors.toList());

		return saibanTsuikisoJikenDtoList;
	}

	/**
	 * 画面で入力した情報を、Entityに反映する
	 *
	 * @param entity 刑事裁判付帯情報のEntity
	 * @param inputForm 画面で入力した情報
	 */
	private void setSaibanAddKeijiEntity(TSaibanAddKeijiEntity entity, SaibanEditKeijiInputForm.SaibanKensatsukanInputForm inputForm) {

		entity.setKensatsuchoTantoBuName(inputForm.getKensatsuchoTantoBuName());
		entity.setKensatsuTelNo(inputForm.getKensatsuTelNo());
		entity.setKensatsuExtensionNo(inputForm.getKensatsuExtensionNo());
		entity.setKensatsuFaxNo(inputForm.getKensatsuFaxNo());
		entity.setKensatsuRoomNo(inputForm.getKensatsuRoomNo());
		entity.setKensatsukanName(inputForm.getKensatsukanName());
		entity.setJimukanName(inputForm.getJimukanName());
		entity.setKensatsuRemarks(inputForm.getKensatsuRemarks());

		if (inputForm.getSosakikanId() != null) {
			// マスタに登録されている検察庁が選択されている場合
			entity.setSosakikanId(inputForm.getSosakikanId());
			entity.setKensatsuchoName(null);

		} else {
			// マスタに登録されていない検察庁の場合

			entity.setSosakikanId(null);
			if (inputForm.getKensatsuchoName() != null) {
				// 検察庁が自由入力されている場合
				entity.setKensatsuchoName(inputForm.getKensatsuchoName());
			} else {
				// 自由入力
				entity.setKensatsuchoName(null);
			}
		}

		// 本起訴事件のSEQを設定する
		Long jikenSeq = inputForm.getMainJikenSeq();
		if (jikenSeq != null) {
			TSaibanJikenEntity jikenEntity = tSaibanJikenDao.selectByJikenSeq(jikenSeq);
			if (jikenEntity != null) {
				// 本起訴事件情報が存在する
				entity.setMainJikenSeq(jikenSeq);
			} else {
				// 削除されている
				entity.setMainJikenSeq(null);
			}
		}
	}

	/**
	 * 裁判の基本情報を更新する
	 * 
	 * @param saibanId
	 * @param basicInputForm
	 */
	private void updateSaibanBasic(Long saibanSeq, SaibanEditKeijiInputForm.SaibanBasicInputForm basicInputForm) {

		// 裁判情報
		TSaibanEntity entity = tSaibanDao.selectBySeq(saibanSeq);

		// 裁判ステータス
		entity.setSaibanStatus(SaibanStatus.calc(basicInputForm.getSaibanStartDate(), basicInputForm.getSaibanEndDate()).getCd());
		// 開始日
		entity.setSaibanStartDate(basicInputForm.getSaibanStartDate());
		// 終了日
		entity.setSaibanEndDate(basicInputForm.getSaibanEndDate());
		// 裁判結果
		entity.setSaibanResult(basicInputForm.getSaibanResult());

		// 裁判所情報
		entity.setSaibanshoId(basicInputForm.getSaibanshoId());
		entity.setSaibanshoName(basicInputForm.getSaibanshoName());
		entity.setKeizokuBuName(basicInputForm.getKeizokuBuName());
		entity.setKeizokuKakariName(basicInputForm.getKeizokuKakariName());
		entity.setKeizokuBuTelNo(basicInputForm.getKeizokuBuTelNo());
		entity.setKeizokuBuFaxNo(basicInputForm.getKeizokuBuFaxNo());
		entity.setTantoShoki(basicInputForm.getTantoShoki());

		// 裁判所IDが入力されている場合は、自由入力項目「裁判所名」はnullをセット
		if (Objects.nonNull(entity.getSaibanshoId())) {
			entity.setSaibanshoName(null);
		}

		tSaibanDao.update(entity);
	}

	/**
	 * 裁判の事件情報を更新する
	 * 
	 * @param saibanSeq
	 * @param basicInputForm
	 */
	private void updateSaibanJiken(Long saibanSeq, SaibanEditKeijiInputForm.SaibanBasicInputForm basicInputForm) {

		// 裁判事件情報
		TSaibanJikenEntity entity = tSaibanJikenDao.selectByJikenSeq(basicInputForm.getJikenSeq());

		boolean insert;
		if (entity == null) {
			// 登録
			entity = new TSaibanJikenEntity();
			entity.setSaibanSeq(saibanSeq);
			insert = true;
		} else {
			// 更新
			insert = false;
		}

		entity.setJikenGengo(DefaultEnum.getCd(basicInputForm.getJikenGengo()));
		entity.setJikenYear(basicInputForm.getJikenYear());
		entity.setJikenMark(basicInputForm.getJikenMark());
		entity.setJikenNo(basicInputForm.getJikenNo());
		entity.setJikenName(basicInputForm.getJikenName());

		if (insert) {
			tSaibanJikenDao.insert(entity);
		} else {
			tSaibanJikenDao.update(entity);
		}
	}

	/**
	 * 裁判-担当者を更新する
	 * 
	 * @param saibanSeq
	 * @param basicInputForm
	 */
	private void updateSaibanTanto(Long saibanSeq, SaibanEditKeijiInputForm.SaibanBasicInputForm basicInputForm) {

		List<AnkenTantoSelectInputForm> tantoLawyerInputItemList = basicInputForm.getTantoLawyer();
		List<AnkenTantoSelectInputForm> tantoJimuInputItemList = basicInputForm.getTantoJimu();

		commonSaibanService.updateSaibanTanto(saibanSeq, tantoLawyerInputItemList, tantoJimuInputItemList);
	}

	/**
	 * 裁判顧客の追加登録のバリデーションチェック<br>
	 * 異常がある場合はAppExceptionを楽観ロックエラーとしてスローする。
	 * 
	 * @param ankenId
	 * @param saibanSeq
	 * @param addCustomerIdList
	 * @throws AppException
	 */
	private void registSaibanCustomerValidation(Long ankenId, Long saibanSeq, List<Long> addCustomerIdList) throws AppException {

		// 案件に紐づく顧客一覧を取得
		List<AnkenCustomerRelationBean> ankenCustomerBean = tAnkenCustomerDao.selectRelation(ankenId, null);
		List<Long> ankenCustomerIdList = ankenCustomerBean.stream()
				.map(bean -> bean.getCustomerId())
				.collect(Collectors.toList());

		// 裁判に紐づく顧客一覧を取得
		List<SaibanCustomerBean> saibanCustomerBean = tSaibanCustomerDao.selectBySeq(saibanSeq);
		List<Long> saibanCustomerIdList = saibanCustomerBean.stream()
				.map(bean -> bean.getCustomerId())
				.collect(Collectors.toList());

		// 裁判顧客の全てが、案件顧客となっているか
		boolean allSaibanCustomerIsAnkenCustomer = ankenCustomerIdList.containsAll(saibanCustomerIdList);
		if (!allSaibanCustomerIsAnkenCustomer) {
			// 案件顧客に存在しない裁判顧客がいる -> 楽観ロックエラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 追加顧客の全てが、案件顧客となっているか
		boolean allAddCustomerIsAnkenCustomer = ankenCustomerIdList.containsAll(addCustomerIdList);
		if (!allAddCustomerIsAnkenCustomer) {
			// 案件顧客に存在しない追加顧客がいる -> 楽観ロックエラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 追加顧客の中で、既に裁判顧客になっている顧客がいないか
		List<Long> alreadySaibanCustomerId = addCustomerIdList.stream()
				.filter(addCustomerId -> saibanCustomerIdList.contains(addCustomerId))
				.collect(Collectors.toList());
		if (!alreadySaibanCustomerId.isEmpty()) {
			// 追加顧客のいずれかが、既に裁判顧客として登録されている -> 楽観ロックエラー
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
	}

	/**
	 * 裁判 顧客の当事者表記の更新
	 * 
	 * @param saibanSeq
	 * @param customerId
	 * @param tojishaHyoki
	 * @throws AppException
	 */
	private void updateSaibanCustomerTojishaHyoki(Long saibanSeq, Long customerId, TojishaHyoki tojishaHyoki) throws AppException {

		// 更新対象取得
		TSaibanCustomerEntity updateEntity = tSaibanCustomerDao.selectBySaibanSeqAndCustomerId(saibanSeq, customerId);

		// 値を設定
		String tojishaHyokiCd = tojishaHyoki != null ? tojishaHyoki.getCd() : null;
		updateEntity.setSaibanTojishaHyoki(tojishaHyokiCd);

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tSaibanCustomerDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 裁判 共犯者の当事者表記の更新
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param kanyoshaSeq 関与者SEQ
	 * @param tojishaHyoki 当事者表記
	 * @throws AppException
	 */
	private void updateSaibanRelatedKanyoshaTojishaHyoki(Long saibanSeq, Long kanyoshaSeq, TojishaHyoki tojishaHyoki) throws AppException {

		// 更新対象取得
		TSaibanRelatedKanyoshaEntity updateEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (updateEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 値を設定
		String tojishaHyokiCd = tojishaHyoki != null ? tojishaHyoki.getCd() : null;
		updateEntity.setSaibanTojishaHyoki(tojishaHyokiCd);

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tSaibanRelatedKanyoshaDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 共犯者を削除する
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param kanyoshaSeq 関与者SEQ
	 * @throws AppException
	 */
	private void deleteSaibanKanyosha(Long saibanSeq, Long kanyoshaSeq) throws AppException {

		TSaibanRelatedKanyoshaEntity entity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (entity == null) {
			// 既に削除されている -> 楽観ロックエラーとせず、何もしない（削除しようとした裁判顧客が既に削除されていたので削除処理終了とする）
			return;
		}

		// 件数チェック用
		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tSaibanRelatedKanyoshaDao.delete(entity);
		} catch (OptimisticLockingFailureException | DomaNullPointerException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (deleteCount != 1) {
			// 削除件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_DELETED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

	/**
	 * 関与者IDに紐づく弁護人を更新する
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param kanyoshaSeq 関与者SEQ
	 * @param relatedKanyoshaSeq 代理人SEQ
	 * @throws AppException
	 */
	private void updateRelatedKanyosha(Long saibanSeq, Long kanyoshaSeq, Long relatedKanyoshaSeq) throws AppException {

		// 更新対象取得
		TSaibanRelatedKanyoshaEntity updateEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (updateEntity == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 値を設定
		updateEntity.setRelatedKanyoshaSeq(relatedKanyoshaSeq);

		// 件数チェック用
		int updateCount = 0;

		try {
			// 処理実行
			updateCount = tSaibanRelatedKanyoshaDao.update(updateEntity);
		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());
			throw new AppException(MessageEnum.MSG_E00025, ex);
		}

		if (updateCount != 1) {
			// 更新件数エラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);
			throw new AppException(MessageEnum.MSG_E00013, null);
		}
	}

}