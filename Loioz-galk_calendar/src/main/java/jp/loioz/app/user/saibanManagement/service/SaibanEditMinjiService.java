package jp.loioz.app.user.saibanManagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.seasar.doma.DomaNullPointerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.AnkenTantoSelectInputForm;
import jp.loioz.app.common.form.Keys;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKanyoshaService;
import jp.loioz.app.common.service.CommonSaibanService;
import jp.loioz.app.common.service.CommonScheduleService;
import jp.loioz.app.common.service.CommonSelectBoxService;
import jp.loioz.app.user.saibanManagement.dto.SaibanCustomerDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKanyoshaViewDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanKijitsuDto;
import jp.loioz.app.user.saibanManagement.dto.SaibanTantoDto;
import jp.loioz.app.user.saibanManagement.form.SaibanCommonInputForm;
import jp.loioz.app.user.saibanManagement.form.SaibanEditMinjiInputForm;
import jp.loioz.app.user.saibanManagement.form.SaibanEditMinjiViewForm;
import jp.loioz.app.user.saibanManagement.form.SaibanEditMinjiViewForm.Saiban;
import jp.loioz.app.user.saibanManagement.form.SaibanScheduleInputForm;
import jp.loioz.app.user.schedule.form.ScheduleDetail;
import jp.loioz.app.user.schedule.form.ScheduleInputForm;
import jp.loioz.app.user.schedule.service.ScheduleCommonService;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.bean.SaibanBasicBean;
import jp.loioz.bean.SaibanCustomerBean;
import jp.loioz.bean.SaibanLimitBean;
import jp.loioz.bean.SaibanRelatedKanyoshaBean;
import jp.loioz.bean.SaibanTantoBean;
import jp.loioz.bean.SaibanTreeJikenBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.HeigoHansoType;
import jp.loioz.common.constant.CommonConstant.KanyoshaType;
import jp.loioz.common.constant.CommonConstant.SaibanStatus;
import jp.loioz.common.constant.CommonConstant.ShutteiType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TantoType;
import jp.loioz.common.constant.CommonConstant.TojishaHyoki;
import jp.loioz.common.constant.ConstantMaps;
import jp.loioz.common.constant.DefaultEnum;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dao.SaibanManagementDao;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TSaibanCustomerDao;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.dao.TSaibanLimitDao;
import jp.loioz.dao.TSaibanLimitRelationDao;
import jp.loioz.dao.TSaibanRelatedKanyoshaDao;
import jp.loioz.dao.TSaibanTantoDao;
import jp.loioz.dao.TSaibanTreeDao;
import jp.loioz.dao.TScheduleAccountDao;
import jp.loioz.dao.TScheduleDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.domain.value.SaibanId;
import jp.loioz.dto.SaibanEditSaibanDto;
import jp.loioz.dto.SaibanTreeJikenDto;
import jp.loioz.dto.SaibankanDto;
import jp.loioz.entity.MRoomEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TSaibanCustomerEntity;
import jp.loioz.entity.TSaibanEntity;
import jp.loioz.entity.TSaibanJikenEntity;
import jp.loioz.entity.TSaibanLimitEntity;
import jp.loioz.entity.TSaibanLimitRelationEntity;
import jp.loioz.entity.TSaibanRelatedKanyoshaEntity;
import jp.loioz.entity.TSaibanTreeEntity;
import jp.loioz.entity.TScheduleAccountEntity;
import jp.loioz.entity.TScheduleEntity;

/**
 * 裁判管理（民事）画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanEditMinjiService extends DefaultService {

	/** 裁判管理系画面の共通サービス */
	@Autowired
	private SaibanCommonService saibanCommonService;

	/** 共通 予定サービス */
	@Autowired
	private ScheduleCommonService scheduleCommonService;

	/** 共通 裁判サービス */
	@Autowired
	private CommonSaibanService commonSaibanService;

	/** 共通 プルダウンサービス */
	@Autowired
	private CommonSelectBoxService commonSelectBoxService;

	/** 共通 分野サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 共通予定サービス */
	@Autowired
	private CommonScheduleService commonScheduleService;

	@Autowired
	private TSaibanDao tSaibanDao;

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
	private TSaibanLimitRelationDao tSaibanLimitRelationDao;

	@Autowired
	private TAnkenDao tAnkenDao;

	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	@Autowired
	private TScheduleDao tScheduleDao;

	@Autowired
	private TScheduleAccountDao tScheduleAccountDao;

	@Autowired
	private MRoomDao mRoomDao;

	@Autowired
	private TSaibanTreeDao tSaibanTreeDao;

	@Autowired
	private SaibanManagementDao saibanManagementDao;

	@Autowired
	private CommonKanyoshaService commonkanyoshaService;

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
	public SaibanEditMinjiViewForm createViewForm(Long saibanSeq, Long ankenId) {

		SaibanEditMinjiViewForm form = new SaibanEditMinjiViewForm();

		// 表示している裁判SEQ
		form.setSelectedSaibanSeq(saibanSeq);
		// 案件IDを設定
		form.setAnkenId(AnkenId.of(ankenId));

		return form;
	}

	/**
	 * 裁判の基本情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public SaibanEditMinjiViewForm.SaibanBasicViewForm createSaibanBasicViewForm(Long saibanSeq) {

		SaibanEditMinjiViewForm.SaibanBasicViewForm viewForm = new SaibanEditMinjiViewForm.SaibanBasicViewForm();

		// 表示フォームは、基本的には入力フォームで保存されたデータを表示する形式になるので、入力フォームの取得処理を活用する。

		// 裁判の基本情報入力フォーム
		SaibanEditMinjiInputForm.SaibanBasicInputForm inputForm = this.createSaibanBasicInputForm(saibanSeq);

		// 表示フォームに値を設定

		// 案件情報
		viewForm.setAnkenId(inputForm.getAnkenId());
		viewForm.setAnkenType(AnkenType.of(inputForm.getAnkenType()));
		viewForm.setBunyaName(inputForm.getBunyaName());

		// 裁判情報
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
		viewForm.setHonsoFlg(inputForm.getHonsoFlg());
		viewForm.setKihonFlg(inputForm.getKihonFlg());

		// 裁判の登録件数を取得
		TSaibanEntity entity = commonSaibanService.getSaibanEntity(saibanSeq);
		// 裁判件数を取得
		Long ankenId = entity.getAnkenId();
		Long saibanCnt = tSaibanDao.selectCountMinjiByAnkenId(ankenId);

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
	 * @return SaibanEditMinjiInputForm.SaibanBasicInputForm
	 */
	public SaibanEditMinjiInputForm.SaibanBasicInputForm createSaibanBasicInputForm(Long saibanSeq) {

		// 初期設定済みの入力フォームを取得

		SaibanEditMinjiInputForm.SaibanBasicInputForm inputForm = this.getInitializedSaibanBasicInputForm(saibanSeq);
		// 裁判情報を取得
		SaibanBasicBean saiban = tSaibanDao.selectBySeqParentOnly(saibanSeq);
		// 裁判情報が存在しない、もしくは、親（基本、本訴）裁判ではない場合
		if (saiban == null) {
			throw new DataNotFoundException("裁判情報が存在しないか、親（基本、本訴）裁判ではありません。[saibanSeq=" + saibanSeq + "]");
		}

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

		// 裁判ツリー
		// 基本事件フラグ
		inputForm.setKihonFlg(saiban.getKihonFlg());
		// 反訴フラグ
		inputForm.setHonsoFlg(saiban.getHonsoFlg());

		// 裁判官を設定
		List<SaibankanDto> saibankanDtoList = commonSaibanService.getSaibankanList(saibanSeq);
		if (LoiozCollectionUtils.isEmpty(saibankanDtoList)) {
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
	public SaibanEditMinjiInputForm.SaibanBasicInputForm getInitializedSaibanBasicInputForm(Long saibanSeq) {

		SaibanEditMinjiInputForm.SaibanBasicInputForm inputForm = new SaibanEditMinjiInputForm.SaibanBasicInputForm();

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
	public void setDisplayData(SaibanEditMinjiInputForm.SaibanBasicInputForm inputForm, Long saibanSeq) {

		// 案件情報を取得
		TAnkenEntity anken = tAnkenDao.selectBySaibanSeq(saibanSeq);
		// 裁判情報が存在しない場合
		if (anken == null) {
			throw new DataNotFoundException("裁判情報が存在しません。[saibanSeq=" + saibanSeq + "]");
		}

		// 案件ID
		inputForm.setAnkenId(AnkenId.of(anken.getAnkenId()));
		// 案件区分
		inputForm.setAnkenType(anken.getAnkenType());
		// 分野名
		inputForm.setBunyaName(commonBunyaService.getBunya(anken.getBunyaId()).getVal());

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
		Map<TantoType, List<SelectOptionForm>> tantoSelectOptListMap = commonSaibanService.getTantoSelectOptListMapForEdit(saibanSeq, anken.getAnkenId());
		inputForm.setAnkenTantoBengoshiOptionList(tantoSelectOptListMap.get(TantoType.LAWYER));
		inputForm.setAnkenTantoJimuOptionList(tantoSelectOptListMap.get(TantoType.JIMU));
	}

	/**
	 * 裁判の基本情報の保存
	 * 
	 * @param saibanId
	 * @param basicInputForm
	 * @throws AppException
	 */
	public void saveSaibanBasic(Long saibanSeq, SaibanEditMinjiInputForm.SaibanBasicInputForm basicInputForm) throws AppException {

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
	 * 裁判のオプション情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @return
	 */
	public SaibanEditMinjiViewForm.SaibanOptionViewForm createSaibanOptionViewForm(Long saibanSeq) {

		SaibanEditMinjiViewForm.SaibanOptionViewForm viewForm = new SaibanEditMinjiViewForm.SaibanOptionViewForm();

		// 裁判情報を取得
		TSaibanEntity entity = commonSaibanService.getSaibanEntity(saibanSeq);
		// 裁判の案件IDを取得
		Long ankenId = entity.getAnkenId();

		// 併合裁判追加可能か、反訴裁判追加可能かを判定
		this.addHeigoHansoFlg(saibanSeq, ankenId, entity, viewForm);

		return viewForm;
	}

	/**
	 * 裁判のタブ情報表示フォームを取得する
	 * 
	 * @param saibanSeq 併合・反訴事件を取得するためのキーとなる裁判SEQ
	 * @param targetSaibanSeq 選択した裁判タブの裁判SEQ
	 * @param bunriTorisageButtonFlg 分離/取下ボタン表示判定
	 * @return
	 */
	public SaibanEditMinjiViewForm.SaibanTabViewForm createSaibanTabViewForm(Long saibanSeq, Long targetSaibanSeq, Boolean bunriTorisageButtonFlg) {

		SaibanEditMinjiViewForm.SaibanTabViewForm viewForm = new SaibanEditMinjiViewForm.SaibanTabViewForm();

		// 親裁判SEQの設定
		viewForm.setParentSaibanSeq(saibanSeq);

		// 併合・反訴事件の取得
		List<SaibanTreeJikenBean> saibanTreeJikenBean = tSaibanTreeDao.selectChildAndJikenBySaibanSeq(saibanSeq);
		List<SaibanTreeJikenDto> childSaiban = this.treeJikenBean2Dto(saibanTreeJikenBean);
		viewForm.setChildSaibanList(childSaiban);

		// 基本情報が基本事件、本訴事件かの判定
		boolean existsHeigo = saibanTreeJikenBean.stream().anyMatch(bean -> HeigoHansoType.HEIGO.equalsByCode(bean.getConnectType()));
		boolean existsHanso = saibanTreeJikenBean.stream().anyMatch(bean -> HeigoHansoType.HANSO.equalsByCode(bean.getConnectType()));
		viewForm.setKihonFlg(existsHeigo);
		viewForm.setHonsoFlg(existsHanso);

		// 選択したタブの裁判SEQ
		viewForm.setTargetSaibanSeq(targetSaibanSeq);

		// 分離/取下ボタン表示判定
		viewForm.setBunriTorisageButtonFlg(bunriTorisageButtonFlg);

		return viewForm;
	}

	/**
	 * 裁判の期日情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @param isAllTimeKijitsuList true：全ての時間の期日を取得候補とする
	 * false：本日以降の期日のみを取得候補とする（本日は含む）
	 * @param isDisplayOnly true:期日情報を画面で変更可 false:期日情報を画面で変更不可
	 * @param isChildSaiban 期日データ取得判定フラグ（基本・本訴事件、被併合・反訴事件で取得方法を変える）
	 * @return
	 */
	public SaibanEditMinjiViewForm.SaibanKijitsuViewForm createSaibanKijitsuViewForm(Long saibanSeq, boolean isAllTimeKijitsuList, boolean isDisplayOnly, boolean isChildSaiban) {

		SaibanEditMinjiViewForm.SaibanKijitsuViewForm viewForm = new SaibanEditMinjiViewForm.SaibanKijitsuViewForm();

		// 期日情報の編集可否設定
		viewForm.setDisplayOnlyFlg(isDisplayOnly);

		// 全ての時間の期日を取得するかどうか
		viewForm.setAllTimeKijitsuList(isAllTimeKijitsuList);

		// 子裁判かどうか
		viewForm.setChildSaiban(isChildSaiban);

		// 裁判期日を取得
		List<SaibanLimitBean> saibanLimitBeanList = this.getSaibanLimitBeanList(saibanSeq, isAllTimeKijitsuList, isChildSaiban);

		// 表示用に加工
		List<SaibanKijitsuDto> saibanKijitsuDtoList = saibanLimitBeanList.stream()
				.map(bean -> SaibanKijitsuDto.builder()
						.saibanSeq(bean.getSaibanSeq())
						.saibanLimitSeq(bean.getSaibanLimitSeq())
						.scheduleSeq(bean.getScheduleSeq())
						.limitDateCount(bean.getLimitDateCount())
						.dateTo(bean.getDataTo())
						.timeTo(bean.getTimeTo())
						.subject(bean.getSubject())
						.limitAt(bean.getLimitAt())
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
	 * 裁判民事期日の予定詳細データを取得
	 * 
	 * @param saibanSeq
	 * @param isAllTimeKijitsuList
	 * @param isChildSaiban
	 * @return
	 */
	public Map<Long, ScheduleDetail> getSaibanKijitsuDetails(Long saibanSeq, boolean isAllTimeKijitsuList, boolean isChildSaiban) {

		// 裁判期日を取得
		List<SaibanLimitBean> saibanLimitBeanList = this.getSaibanLimitBeanList(saibanSeq, isAllTimeKijitsuList, isChildSaiban);

		Set<Long> scheduleSeqList = saibanLimitBeanList.stream().map(SaibanLimitBean::getScheduleSeq).collect(Collectors.toSet());
		return commonScheduleService.getSchedulePKOne(new ArrayList<>(scheduleSeqList));
	}

	/**
	 * 裁判期日Beanを取得
	 * 
	 * @param saibanSeq
	 * @param isAllTimeKijitsuList
	 * @param isChildSaiban
	 * @return
	 */
	private List<SaibanLimitBean> getSaibanLimitBeanList(Long saibanSeq, boolean isAllTimeKijitsuList, boolean isChildSaiban) {

		// 裁判期日を取得
		List<SaibanLimitBean> saibanLimitBeanList;
		if (isChildSaiban) {
			saibanLimitBeanList = tSaibanLimitDao.selectMinjiKijitsuBySaibanSeq(saibanSeq, SessionUtils.getLoginAccountSeq());
		} else {
			saibanLimitBeanList = tSaibanLimitDao.selectMinjiKijitsuOfParentSaiban(saibanSeq, isAllTimeKijitsuList, LocalDateTime.now(), SessionUtils.getLoginAccountSeq());
		}

		return saibanLimitBeanList;
	}

	/**
	 * 裁判 期日結果入力フォームを作成する
	 * 
	 * @param saibanLimitSeq
	 * @return
	 */
	public SaibanEditMinjiInputForm.SaibanKijitsuResultInputForm createSaibanKijitsuResultInputForm(Long saibanLimitSeq) {

		SaibanEditMinjiInputForm.SaibanKijitsuResultInputForm kijitsuResultInputForm = new SaibanEditMinjiInputForm.SaibanKijitsuResultInputForm();

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
	public void saveSaibanKijitsuResult(SaibanEditMinjiInputForm.SaibanKijitsuResultInputForm kijitsuResultInputForm)
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
	 * @param isDisplayOnly
	 * @return
	 */
	public SaibanEditMinjiViewForm.SaibanCustomerViewForm createSaibanCustomerViewForm(Long saibanSeq, boolean isDisplayOnly) {

		SaibanEditMinjiViewForm.SaibanCustomerViewForm viewForm = new SaibanEditMinjiViewForm.SaibanCustomerViewForm();

		// 裁判SEQ
		viewForm.setSaibanSeq(saibanSeq);

		// 顧客情報の編集可否設定
		viewForm.setDisplayOnlyFlg(isDisplayOnly);

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
	public SaibanEditMinjiInputForm.SaibanCustomerAddInputForm createSaibanCustomerAddInputForm(Long saibanSeq, Long ankenId) {

		SaibanEditMinjiInputForm.SaibanCustomerAddInputForm customerAddInputForm = new SaibanEditMinjiInputForm.SaibanCustomerAddInputForm();

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
		List<SaibanEditMinjiInputForm.AddOption> addCustomerCandidateList = ankenCustomerNotIncludeSaibanCustomerBeanList.stream()
				.map(bean -> {
					SaibanEditMinjiInputForm.AddOption addOption = new SaibanEditMinjiInputForm.AddOption();
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
	public void registSaibanCustomerAdd(Long saibanSeq, Long ankenId, SaibanEditMinjiInputForm.SaibanCustomerAddInputForm customerAddInputForm)
			throws AppException {

		// 追加を行う顧客のIDリストをフォームから取得
		List<SaibanEditMinjiInputForm.AddOption> addCustomerCandidateList = customerAddInputForm.getAddCustomerCandidateList();
		List<Long> addCustomerIdList = addCustomerCandidateList.stream()
				.filter(candidate -> candidate.isToAdd())
				.map(candidate -> candidate.getSeq())
				.collect(Collectors.toList());

		// 登録前バリデーション（楽観ロックエラーチェック）
		this.registSaibanCustomerAddValidation(ankenId, saibanSeq, addCustomerIdList);

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

		this.deleteSaibanCustomer(saibanSeq, customerId);
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

		// 顧客の筆頭更新だが、更新対象の顧客が存在しない場合
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

		// その他当事者・共同訴訟人（代理人を含まない）を取得
		List<TSaibanRelatedKanyoshaEntity> tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectNotDairininBySeq(saibanSeq,
				KanyoshaType.KYODOSOSHONIN.getCd());

		// 関与者の筆頭更新だが、更新対象の関与者が存在しない場合
		if (kanyoshaSeq != null && tSaibanRelatedKanyoshaEntity.stream().noneMatch(e -> Objects.equals(e.getKanyoshaSeq(), kanyoshaSeq))) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 筆頭を初期化し、関与者IDがパラメータとして送信されていたら、その関与者を筆頭にする
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
	 * その他当事者情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @param isDisplayOnly
	 * @return
	 */
	public SaibanEditMinjiViewForm.SaibanOtherViewForm createSaibanOtherViewForm(Long saibanSeq, boolean isDisplayOnly) {

		SaibanEditMinjiViewForm.SaibanOtherViewForm viewForm = new SaibanEditMinjiViewForm.SaibanOtherViewForm();

		// 裁判SEQ
		viewForm.setSaibanSeq(saibanSeq);

		// その他当事者情報の編集可否設定
		viewForm.setDisplayOnlyFlg(isDisplayOnly);

		// 共同訴訟人情報を取得
		List<SaibanRelatedKanyoshaBean> saibanRelatedKanyoshaBeanList = commonSaibanService.getSaibanRelatedKanyoshaBeanList(saibanSeq, KanyoshaType.KYODOSOSHONIN, SystemFlg.FLG_OFF);

		// 共同訴訟人の代理人情報を取得
		List<SaibanRelatedKanyoshaBean> saibanRelatedKanyoshaDairininBeanList = commonSaibanService.getSaibanRelatedKanyoshaBeanList(saibanSeq, KanyoshaType.KYODOSOSHONIN, SystemFlg.FLG_ON);

		// 画面表示用に加工
		List<SaibanKanyoshaViewDto> saibanKanyoshaViewDtoList = commonSaibanService.generateKanyoshaViewDto(saibanRelatedKanyoshaBeanList, saibanRelatedKanyoshaDairininBeanList);
		viewForm.setSaibanKanyoshaOtherDtoList(saibanKanyoshaViewDtoList);

		return viewForm;
	}

	/**
	 * 関与者の当事者表記の更新
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
	 * 関与者の削除
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
	 * 関与者の代理人を外す
	 * 
	 * @param saibanSeq 裁判SEQ
	 * @param kanyoshaSeq 関与者SEQ
	 * @throws AppException
	 */
	public void removeSaibanRelatedKanyosha(Long saibanSeq, Long kanyoshaSeq) throws AppException {

		// 外す代理人の関与者IDを事前に取得
		TSaibanRelatedKanyoshaEntity kanyoshaEntity = tSaibanRelatedKanyoshaDao.selectSaibanRelatedKanyoshaEntityByParams(saibanSeq, kanyoshaSeq);
		if (kanyoshaEntity == null || kanyoshaEntity.getRelatedKanyoshaSeq() == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		Long dairininKanyoshaSeq = kanyoshaEntity.getRelatedKanyoshaSeq();

		// 削除 代理人のIDをnullで更新する
		this.updateRelatedKanyosha(saibanSeq, kanyoshaSeq, null);

		// 外した代理人について、どの関与者からも代理人として利用されなくなった場合は関与者-関係者情報を削除
		commonSaibanService.deleteSaibanRelatedKanyoshaDairininIfNotUsed(saibanSeq, dairininKanyoshaSeq);
	}

	/**
	 * 裁判 相手方情報表示フォームを取得する
	 * 
	 * @param saibanSeq
	 * @param isDisplayOnly
	 * @return
	 */
	public SaibanEditMinjiViewForm.SaibanAitegataViewForm createSaibanAitegataViewForm(Long saibanSeq, boolean isDisplayOnly) {

		SaibanEditMinjiViewForm.SaibanAitegataViewForm viewForm = new SaibanEditMinjiViewForm.SaibanAitegataViewForm();

		// 裁判SEQ
		viewForm.setSaibanSeq(saibanSeq);

		// 相手方情報の編集可否設定
		viewForm.setDisplayOnlyFlg(isDisplayOnly);

		// 相手方情報を取得
		List<SaibanRelatedKanyoshaBean> saibanRelatedKanyoshaBean = commonSaibanService.getSaibanRelatedKanyoshaBeanList(saibanSeq, KanyoshaType.AITEGATA, SystemFlg.FLG_OFF);

		// 相手方代理人情報を取得
		List<SaibanRelatedKanyoshaBean> saibanRelatedKanyoshaDairininBean = commonSaibanService.getSaibanRelatedKanyoshaBeanList(saibanSeq, KanyoshaType.AITEGATA, SystemFlg.FLG_ON);

		// 画面表示情報に加工
		List<SaibanKanyoshaViewDto> saibanAitegataDtoList = commonSaibanService.generateKanyoshaViewDto(saibanRelatedKanyoshaBean, saibanRelatedKanyoshaDairininBean);
		viewForm.setSaibanKanyoshaAitegataDtoList(saibanAitegataDtoList);

		return viewForm;
	}

	/**
	 * 相手方の筆頭を更新する
	 * 
	 * @param saibanSeq
	 * @param kanyoshaSeq
	 * @param toHittou
	 */
	public void saveAitegataHittou(Long saibanSeq, Long kanyoshaSeq, boolean toHittou) throws AppException {

		// 相手方（代理人を含まない）を取得
		List<TSaibanRelatedKanyoshaEntity> tSaibanRelatedKanyoshaEntity = tSaibanRelatedKanyoshaDao.selectNotDairininBySeq(saibanSeq,
				KanyoshaType.AITEGATA.getCd());

		// 相手方の筆頭更新だが、更新対象の相手方が存在しない場合
		if (kanyoshaSeq != null && tSaibanRelatedKanyoshaEntity.stream().noneMatch(e -> Objects.equals(e.getKanyoshaSeq(), kanyoshaSeq))) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 筆頭を初期化し、顧客IDがパラメータとして送信されていて更新フラグがtrueであれば、その顧客を筆頭にする
		tSaibanRelatedKanyoshaEntity.forEach(e -> {
			e.setMainFlg(SystemFlg.FLG_OFF.getCd());
			// 対象の関与者を筆頭にする
			if (e.getKanyoshaSeq().equals(kanyoshaSeq) && toHittou) {
				e.setMainFlg(SystemFlg.FLG_ON.getCd());
			}
		});
		// 筆頭を更新する
		tSaibanRelatedKanyoshaDao.update(tSaibanRelatedKanyoshaEntity);

	}

	/**
	 * 裁判一覧情報に子裁判を設定する処理
	 *
	 * <pre>
	 * ツリー構造のため、自己メソッドループ処理
	 * </pre>
	 *
	 *
	 * @param saibanList
	 * @param saibanMap
	 */
	public void createTreeMap(List<Saiban> saibanList, Map<Long, Saiban> saibanMap) {

		for (Saiban child : saibanList) {
			saibanMap.put(child.getSaibanSeq(), child);
			if (child.nonChild()) {
				continue;
			}
			createTreeMap(child.getChild(), saibanMap);
		}
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 裁判の基本情報を更新する
	 * 
	 * @param saibanId
	 * @param basicInputForm
	 */
	private void updateSaibanBasic(Long saibanSeq, SaibanEditMinjiInputForm.SaibanBasicInputForm basicInputForm) {

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
	 * 子裁判を表示用に加工
	 * 
	 * @param saibanTreeJikenBean
	 * @return
	 */
	private List<SaibanTreeJikenDto> treeJikenBean2Dto(List<SaibanTreeJikenBean> saibanTreeJikenBean) {
		List<SaibanTreeJikenDto> saibanChild = saibanTreeJikenBean.stream()
				.map(bean -> SaibanTreeJikenDto.builder()
						.saibanTreeSeq(bean.getSaibanTreeSeq())
						.saibanSeq(bean.getSaibanSeq())
						.connectType(bean.getConnectType())
						.jikenSeq(bean.getJikenSeq())
						.jikenNo(CaseNumber.of(EraType.of(bean.getJikenGengo()), bean.getJikenYear(), bean.getJikenMark(), bean.getJikenNo()))
						.jikenName(bean.getJikenName())
						.build())
				.collect(Collectors.toList());
		return saibanChild;
	}

	/**
	 * 裁判の事件情報を更新する
	 * 
	 * @param saibanSeq
	 * @param basicInputForm
	 */
	private void updateSaibanJiken(Long saibanSeq, SaibanEditMinjiInputForm.SaibanBasicInputForm basicInputForm) {

		// 裁判事件情報
		TSaibanJikenEntity entity = tSaibanJikenDao.selectSingleBySaibanSeq(saibanSeq);

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
	private void updateSaibanTanto(Long saibanSeq, SaibanEditMinjiInputForm.SaibanBasicInputForm basicInputForm) {

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
	private void registSaibanCustomerAddValidation(Long ankenId, Long saibanSeq, List<Long> addCustomerIdList) throws AppException {

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
	 * 裁判顧客を削除する
	 * 
	 * @param saibanSeq
	 * @param customerId
	 * @throws AppException
	 */
	private void deleteSaibanCustomer(Long saibanSeq, Long customerId) throws AppException {

		TSaibanCustomerEntity entity = tSaibanCustomerDao.selectBySaibanSeqAndCustomerId(saibanSeq, customerId);
		if (entity == null) {
			// 既に削除されている -> 楽観ロックエラーとせず、何もしない（削除しようとした裁判顧客が既に削除されていたので削除処理終了とする）
			return;
		}

		// 件数チェック用
		int deleteCount = 0;

		try {
			// 削除
			deleteCount = tSaibanCustomerDao.delete(entity);
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
	 * 裁判 その他当事者の当事者表記の更新
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
	 * 裁判関与者関係者を削除する
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
	 * 関与者IDに紐づく代理人を更新する
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

	/**
	 * 裁判Entityを取得
	 * 
	 * @param saibanId
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
	 * 裁判期日登録処理
	 *
	 * @param ankenId 案件ID
	 * @param branchNumber 枝番
	 * @param inputForm フォーム入力値
	 * @throws AppException
	 */
	public void createSaibanLimitSchedule(Long ankenId, Long branchNumber, SaibanScheduleInputForm inputForm) throws AppException {

		SaibanId saibanId = new SaibanId(ankenId, branchNumber);

		TSaibanEntity entity = tSaibanDao.selectBySaibanId(saibanId);

		if (entity == null) {
			// 裁判情報が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		Long saibanSeq = entity.getSaibanSeq();

		SaibanScheduleInputForm form = new SaibanScheduleInputForm();
		BeanUtils.copyProperties(inputForm, form);
		form.setAnkenId(ankenId);
		form.setSaibanLimit(true);
		form.setSaibanSeq(saibanSeq);

		// 裁判期日を登録
		TSaibanLimitEntity saibanLimitEntity = new TSaibanLimitEntity();
		populateScheduleInputFormToSaibanLimitEntity(form).accept(saibanLimitEntity);
		saibanLimitEntity.setLimitDateCount(form.getSaibanLimitCount());
		tSaibanLimitDao.insert(saibanLimitEntity);

		Long saibanLimitSeq = saibanLimitEntity.getSaibanLimitSeq();

		// 裁判と紐づけます
		TSaibanLimitRelationEntity relation = new TSaibanLimitRelationEntity();
		relation.setSaibanSeq(saibanSeq);
		relation.setSaibanLimitSeq(saibanLimitSeq);
		tSaibanLimitRelationDao.insert(relation);

		// 子裁判にも紐づけます
		List<TSaibanEntity> childSaiban = tSaibanDao.selectByParentSaibanSeq(saibanSeq);
		childSaiban.forEach(child -> {
			TSaibanLimitRelationEntity childRelation = new TSaibanLimitRelationEntity();
			childRelation.setSaibanSeq(child.getSaibanSeq());
			childRelation.setSaibanLimitSeq(saibanLimitSeq);
			tSaibanLimitRelationDao.insert(childRelation);
		});

		// 出廷不要でない場合は予定を登録
		form.setSaibanLimitSeq(saibanLimitSeq);
		scheduleCommonService.create(form);
	}

	/**
	 * 裁判期日更新処理
	 *
	 * @param form フォーム入力値
	 * @throws AppException
	 */
	public void saveSaibanLimitSchedule(SaibanScheduleInputForm form) throws AppException {

		// 裁判期日を更新
		TSaibanLimitEntity saibanLimitEntity = tSaibanLimitDao.selectBySeq(form.getSaibanLimitSeq());

		if (saibanLimitEntity == null) {
			// 裁判期日が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		populateScheduleInputFormToSaibanLimitEntity(form).accept(saibanLimitEntity);
		tSaibanLimitDao.update(saibanLimitEntity);

		// 予定を更新
		Long scheduleSeq = form.getScheduleSeq();
		ShutteiType shutteiType = form.getShutteiType();
		if (scheduleSeq != null) {
			// 予定が存在する場合
			if (shutteiType != ShutteiType.NOT_REQUIRED) {
				// 出廷不要でなければ、そのまま予定を更新
				scheduleCommonService.update(form);

			} else {
				// 出廷不要の場合は、裁判期日と連動する項目のみ更新(要→不要に変更したケース)
				TScheduleEntity schedule = tScheduleDao.selectBySeq(scheduleSeq);
				schedule.setDateFrom(form.getDateFrom());
				schedule.setTimeFrom(form.getTimeFrom());
				schedule.setRoomId(null);
				schedule.setPlace(form.getPlace());
				tScheduleDao.update(schedule);
			}

		} else {
			// 予定が存在しない場合
			if (shutteiType != ShutteiType.NOT_REQUIRED) {
				// 出廷不要でなければ、予定を新規登録(不要→要に変更したケース)
				scheduleCommonService.create(form);

			} else {
				// 出廷不要の場合は何もしない
			}
		}
	}

	/**
	 * 出廷更新処理
	 *
	 * @param saibanLimitSeq
	 * @param shutteiType
	 * @throws AppException
	 */
	public void saveShutteiType(Long saibanLimitSeq, ShutteiType shutteiType) throws AppException {

		TSaibanLimitEntity saibanLimitEntity = tSaibanLimitDao.selectBySeq(saibanLimitSeq);

		if (saibanLimitEntity == null) {
			// 裁判期日が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		saibanLimitEntity.setShutteiType(shutteiType.getCd());
		tSaibanLimitDao.update(saibanLimitEntity);
	}

	/**
	 * 裁判期日更新処理
	 *
	 * @param form フォーム入力値
	 * @throws AppException
	 */
	public void updateSaibanLimitSchedule(SaibanScheduleInputForm form) throws AppException {

		// 裁判期日を更新
		TSaibanLimitEntity saibanLimitEntity = tSaibanLimitDao.selectBySeq(form.getSaibanLimitSeq());

		if (saibanLimitEntity == null) {
			// 裁判期日が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		populateScheduleInputFormToSaibanLimitEntity(form).accept(saibanLimitEntity);
		tSaibanLimitDao.update(saibanLimitEntity);

		// 予定を更新
		Long scheduleSeq = form.getScheduleSeq();
		ShutteiType shutteiType = form.getShutteiType();
		if (scheduleSeq != null) {
			// 予定が存在する場合
			if (shutteiType != ShutteiType.NOT_REQUIRED) {
				// 出廷不要でなければ、そのまま予定を更新
				scheduleCommonService.update(form);

			} else {
				// 出廷不要の場合は、裁判期日と連動する項目のみ更新(要→不要に変更したケース)
				TScheduleEntity schedule = tScheduleDao.selectBySeq(scheduleSeq);
				schedule.setDateFrom(form.getDateFrom());
				schedule.setTimeFrom(form.getTimeFrom());
				schedule.setRoomId(null);
				schedule.setPlace(form.getPlace());
				tScheduleDao.update(schedule);
			}

		} else {
			// 予定が存在しない場合
			if (shutteiType != ShutteiType.NOT_REQUIRED) {
				// 出廷不要でなければ、予定を新規登録(不要→要に変更したケース)
				scheduleCommonService.create(form);

			} else {
				// 出廷不要の場合は何もしない
			}
		}
	}

	/**
	 * 裁判（民事）の分離/取下処理<br>
	 *
	 * @param saibanSeq
	 * @param childSaibanSeq
	 */
	public void bunriTorisageSaibanMinji(Long saibanSeq, Long childSaibanSeq) {

		// 各裁判情報を取得します
		TSaibanEntity parentSaiban = tSaibanDao.selectBySeq(saibanSeq);
		TSaibanEntity childSaiban = tSaibanDao.selectBySeq(childSaibanSeq);

		// データ取得チェック
		if (parentSaiban == null || childSaiban == null) {
			throw new DataNotFoundException("裁判情報が取得できませんでした。");
		}

		// 紐づけ解除処理
		disConnectSaiban(parentSaiban.getSaibanSeq(), childSaiban.getSaibanSeq());
	}

	/**
	 * 削除処理<br>
	 *
	 * @param saibanSeq
	 * @throws AppException
	 */
	public Long deleteSaiban(Long saibanSeq) throws AppException {

		// 裁判の関連情報を削除
		commonSaibanService.deleteMinjiSaibanRelated(saibanSeq);

		// 裁判情報を削除
		TSaibanEntity entity = tSaibanDao.selectBySeq(saibanSeq);
		// 削除する案件ID
		Long ankenId = entity.getAnkenId();
		tSaibanDao.delete(entity);

		return ankenId;
	}

	/**
	 * 裁判期日削除処理<br>
	 *
	 * <pre>
	 * 裁判情報を削除する時用
	 * </pre>
	 *
	 * @param scheduleSeq 予定SEQ
	 * @param saibanLimitSeq 裁判期日SEQ
	 * @throws AppException
	 */
	public void deleteSaibanLimitSchedule(Long scheduleSeq, Long saibanLimitSeq) throws AppException {

		// 削除対象の裁判期日情報
		TSaibanLimitEntity saibanLimitEntity = tSaibanLimitDao.selectBySeq(saibanLimitSeq);

		if (saibanLimitEntity == null) {
			// 裁判期日が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00066, null);
		}

		// 裁判期日の削除
		tSaibanLimitDao.delete(saibanLimitEntity);

		// 裁判期日の紐付けの削除
		List<TSaibanLimitRelationEntity> saibanLimitRelationEntityList = tSaibanLimitRelationDao.selectBySaibanLimitSeq(saibanLimitSeq);
		saibanLimitRelationEntityList.forEach(tSaibanLimitRelationDao::delete);

		// 予定の削除
		if (scheduleSeq != null) {
			scheduleCommonService.delete(scheduleSeq);
		}
	}

	/**
	 * 裁判（民事）を併合処理<br>
	 *
	 * @param saibanSeq
	 * @param childSaibanSeq
	 * @throws AppException
	 */
	public void heigoSaibanMinji(Long saibanSeq, Long childSaibanSeq) throws AppException {

		// 各裁判情報を取得します
		TSaibanEntity parentSaiban = tSaibanDao.selectBySeq(saibanSeq);
		TSaibanEntity childSaiban = tSaibanDao.selectBySeq(childSaibanSeq);

		// データ取得チェック
		if (parentSaiban == null || childSaiban == null) {
			// 親裁判情報が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00066, null);
		}

		// 併合処理
		connectSaiban(parentSaiban.getSaibanSeq(), childSaiban.getSaibanSeq(), HeigoHansoType.HEIGO);
	}

	/**
	 * 反訴紐づけ
	 *
	 * @param saibanSeq
	 * @param childSaibanSeq
	 * @throws AppException
	 */
	public void hansoSaibanMinji(Long saibanSeq, Long childSaibanSeq) throws AppException {

		// 各裁判情報を取得します
		TSaibanEntity parentSaiban = tSaibanDao.selectBySeq(saibanSeq);
		TSaibanEntity childSaiban = tSaibanDao.selectBySeq(childSaibanSeq);

		// データ取得チェック
		if (parentSaiban == null || childSaiban == null) {
			// 親裁判情報が存在しない（削除されている）場合はエラーとする。
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00066, null);
		}

		// 反訴処理
		connectSaiban(parentSaiban.getSaibanSeq(), childSaiban.getSaibanSeq(), HeigoHansoType.HANSO);
	}

	/**
	 * 関与者を削除できる状態かチェック
	 * 
	 * @param kanyoshaSeq 関与者SEQ
	 * @throws AppException
	 */
	public Map<String, Object> deleteCommonKanyoshaFromAnkenBeforeCheck(Long kanyoshaSeq) throws AppException {

		Map<String, Object> response = commonkanyoshaService.deleteCommonKanyoshaBeforeCheck(kanyoshaSeq);

		return response;
	}

	/**
	 * 関与者の削除処理
	 * 
	 * @param kanyoshaSeq
	 * @throws AppException
	 */
	public void deleteKanyoshaInfo(Long kanyoshaSeq) throws AppException {
		commonkanyoshaService.deleteCommonKanyoshaInfo(kanyoshaSeq);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 裁判ツリー情報を更新(結び付けを外す)します
	 *
	 * @param parentSaibanSeq
	 * @param childSaibanSeq
	 */
	private void disConnectSaiban(Long parentSaibanSeq, Long childSaibanSeq) {

		// DB情報の取得
		TSaibanTreeEntity parentSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(parentSaibanSeq);
		TSaibanTreeEntity childSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(childSaibanSeq);

		// データ取得チェック
		if (parentSaibanTreeEntity == null || childSaibanTreeEntity == null) {
			throw new DataNotFoundException("裁判情報が取得できませんでした。");
		}

		// 他に子供の裁判が存在するかを確認します。
		List<TSaibanTreeEntity> childSaibanList = tSaibanTreeDao.selectChildBySaibanSeq(parentSaibanSeq);
		List<TSaibanTreeEntity> childSaibanTreeEntities = childSaibanList.stream()
				.filter(entity -> !Objects.equals(entity.getSaibanTreeSeq(), childSaibanTreeEntity.getSaibanTreeSeq()))
				.collect(Collectors.toList());
		boolean existsHeigo = childSaibanTreeEntities.stream().anyMatch(entity -> HeigoHansoType.HEIGO.equalsByCode(entity.getConnectType()));
		boolean existsHanso = childSaibanTreeEntities.stream().anyMatch(entity -> HeigoHansoType.HANSO.equalsByCode(entity.getConnectType()));

		// 一件でも存在する場合はflgONで設定する
		parentSaibanTreeEntity.setKihonFlg(SystemFlg.booleanToCode(existsHeigo));
		parentSaibanTreeEntity.setHonsoFlg(SystemFlg.booleanToCode(existsHanso));

		// 紐づけを外す裁判の更新データを設定
		childSaibanTreeEntity.setParentSeq(null);
		childSaibanTreeEntity.setConnectType(null);

		// 親裁判に関連する予定は切り離すときにコピーする
		relatedScheduleCopy(childSaibanTreeEntity.getSaibanSeq());

		// 更新処理
		tSaibanTreeDao.update(parentSaibanTreeEntity);
		tSaibanTreeDao.update(childSaibanTreeEntity);
	}

	/**
	 * 予定のフォーム入力値を裁判期日Entityに反映させる
	 *
	 * @param form フォーム入力値
	 * @return
	 */
	private Consumer<TSaibanLimitEntity> populateScheduleInputFormToSaibanLimitEntity(ScheduleInputForm form) {
		return saibanLimitEntity -> {
			LocalDateTime limitAt = form.getDateFrom().atTime(form.getTimeFrom());
			saibanLimitEntity.setLimitAt(limitAt);
			if (form.isRoomSelected() && form.getRoomId() != null) {
				MRoomEntity room = mRoomDao.selectById(form.getRoomId());
				if (room != null) {
					saibanLimitEntity.setPlace(room.getRoomName());
				}
			} else {
				saibanLimitEntity.setPlace(form.getPlace());
			}
			saibanLimitEntity.setContent(form.getMemo());
			saibanLimitEntity.setShutteiType(DefaultEnum.getCd(form.getShutteiType()));
		};
	}

	/**
	 * 子裁判の裁判期日をコピーする処理
	 *
	 * @param childSaibanSeq
	 */
	private void relatedScheduleCopy(Long childSaibanSeq) {

		// 紐づきを外したい裁判の裁判-関連裁判情報を取得する
		List<TSaibanLimitRelationEntity> tSaibanLimitRelationEntities = tSaibanLimitRelationDao
				.selectBySaibanSeq(childSaibanSeq);

		// 紐づいている裁判期日がない場合は、何もせずに返却
		if (tSaibanLimitRelationEntities.isEmpty()) {
			return;
		}

		// 複合主キーデータのmapper条件
		Function<TSaibanLimitRelationEntity, Keys<Long, Long>> twoKeysMapper = entity -> {
			return new Keys<>(entity.getSaibanSeq(), entity.getSaibanLimitSeq());
		};

		// 複合主キーMap
		Map<Keys<Long, Long>, TSaibanLimitRelationEntity> twoKeysMap = tSaibanLimitRelationEntities.stream()
				.collect(Collectors.toMap(
						twoKeysMapper,
						Function.identity(),
						(former, later) -> later));

		// 取得した裁判期日Seqのリスト作成
		List<Long> saibanLimitSeqList = tSaibanLimitRelationEntities.stream()
				.map(TSaibanLimitRelationEntity::getSaibanLimitSeq)
				.collect(Collectors.toList());

		// 作成した裁判期日Seqリストをキーとして、裁判-期日情報Mapを作成します。
		List<TSaibanLimitEntity> tSaibanLimitEntities = tSaibanLimitDao.selectBySeq(saibanLimitSeqList);
		Map<Long, TSaibanLimitEntity> saibanLimitMap = tSaibanLimitEntities.stream()
				.collect(Collectors.toMap(
						TSaibanLimitEntity::getSaibanLimitSeq,
						Function.identity(),
						(former, latter) -> former));

		// 作成した裁判期日Seqリストをキーとして、予定表データを取得します
		List<TScheduleEntity> tScheduleEntities = tScheduleDao.selectBySaibanLimitSeqList(saibanLimitSeqList);

		// 親裁判に紐づいている予定をList化します。
		List<TScheduleEntity> unConnectSchedules = tScheduleEntities.stream()
				.filter(entity -> {
					return !Objects.equals(entity.getSaibanSeq(), childSaibanSeq);
				})
				.collect(Collectors.toList());

		// コピーするスケジュールに紐づく裁判-アカウント情報を取得します。@key:scheduleSeq
		List<Long> scheduleSeqList = unConnectSchedules.stream()
				.map(TScheduleEntity::getScheduleSeq)
				.collect(Collectors.toList());
		List<TScheduleAccountEntity> unConnectScheduleAccount = tScheduleAccountDao.selectByScheduleSeq(scheduleSeqList);
		Map<Long, List<TScheduleAccountEntity>> unConnectScheduleMap = unConnectScheduleAccount.stream()
				.collect(Collectors.groupingBy(
						TScheduleAccountEntity::getScheduleSeq));

		// コピーを作成
		for (TScheduleEntity entity : unConnectSchedules) {

			// 予定をコピーしない場合は関連情報を削除
			Keys<Long, Long> keys = new Keys<>(childSaibanSeq, entity.getSaibanLimitSeq());
			Optional.ofNullable(twoKeysMap.get(keys)).ifPresent(tSaibanLimitRelationDao::delete);

			// ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
			// ■以下の処理は、スケジュールのコピーを行うDB更新ロジック
			// ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊

			// 裁判期日情報を新たに作成(コピーして、不要項目をnullにする)
			TSaibanLimitEntity tSaibanLimitEntity = saibanLimitMap.get(entity.getSaibanLimitSeq());
			TSaibanLimitEntity tSaibanLimitInsertEntity = new TSaibanLimitEntity();
			BeanUtils.copyProperties(tSaibanLimitEntity, tSaibanLimitInsertEntity);
			tSaibanLimitInsertEntity.setSaibanLimitSeq(null);
			tSaibanLimitInsertEntity.setCreatedAt(null);
			tSaibanLimitInsertEntity.setCreatedBy(null);
			tSaibanLimitInsertEntity.setUpdatedAt(null);
			tSaibanLimitInsertEntity.setUpdatedBy(null);
			tSaibanLimitInsertEntity.setVersionNo(null);
			tSaibanLimitDao.insert(tSaibanLimitInsertEntity);

			// 裁判期日紐づき情報を新たに作成(↑で登録した裁判期日SEQと削除したデータの裁判SEQを設定)
			TSaibanLimitRelationEntity tSaibanLimitRelationInsertEntity = new TSaibanLimitRelationEntity();
			tSaibanLimitRelationInsertEntity.setSaibanLimitSeq(tSaibanLimitInsertEntity.getSaibanLimitSeq());
			tSaibanLimitRelationInsertEntity.setSaibanSeq(childSaibanSeq);
			tSaibanLimitRelationDao.insert(tSaibanLimitRelationInsertEntity);

			// スケジュールをコピー(裁判SEQと裁判期日SEQは、紐づくデータは子裁判の情報に変更)
			TScheduleEntity tScheduleEntity = new TScheduleEntity();
			BeanUtils.copyProperties(entity, tScheduleEntity);
			tScheduleEntity.setScheduleSeq(null);
			tScheduleEntity.setCreatedAt(null);
			tScheduleEntity.setCreatedBy(null);
			tScheduleEntity.setUpdatedAt(null);
			tScheduleEntity.setUpdatedBy(null);
			tScheduleEntity.setVersionNo(null);
			tScheduleEntity.setSaibanSeq(childSaibanSeq);
			tScheduleEntity.setSaibanLimitSeq(tSaibanLimitInsertEntity.getSaibanLimitSeq());
			tScheduleDao.insert(tScheduleEntity);

			// スケジュールの子テーブルも登録を行う
			List<TScheduleAccountEntity> tScheduleAccountEntities = unConnectScheduleMap.get(entity.getScheduleSeq());
			if (!LoiozCollectionUtils.isEmpty(tScheduleAccountEntities)) {
				for (TScheduleAccountEntity childEntity : tScheduleAccountEntities) {
					TScheduleAccountEntity tScheduleAccountEntitiy = new TScheduleAccountEntity();
					tScheduleAccountEntitiy.setScheduleSeq(tScheduleEntity.getScheduleSeq());
					tScheduleAccountEntitiy.setAccountSeq(childEntity.getAccountSeq());
					tScheduleAccountDao.insert(tScheduleAccountEntitiy);
				}

			}

		}

	}

	/**
	 * 裁判タブの名称を取得する
	 *
	 * @param jikenMark 事件番号符号
	 * @param saibanId 裁判ID
	 * @return タブ名称
	 */
	private String getSaibanTabName(String jikenMark, SaibanId saibanId) {

		String saibanTabName = ConstantMaps.JIKEN_MARK_ABBREVIATION.getOrDefault(
				jikenMark,
				Objects.toString(saibanId, "-"));

		return saibanTabName;
	}

	/**
	 * 裁判情報の親子関係を更新(結び付ける)します
	 *
	 * @param parentSaibanSeq
	 * @param childSaibanSeq
	 * @param type
	 */
	private void connectSaiban(Long parentSaibanSeq, Long childSaibanSeq, HeigoHansoType type) {

		// DB情報の取得
		TSaibanTreeEntity parentSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(parentSaibanSeq);
		TSaibanTreeEntity childSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(childSaibanSeq);
		List<TSaibanTreeEntity> grandChildTreeEntities = tSaibanTreeDao.selectChildBySaibanSeq(childSaibanSeq);

		// データ取得チェック
		if (parentSaibanTreeEntity == null || childSaibanTreeEntity == null) {
			throw new DataNotFoundException("裁判情報が取得できませんでした。");
		}

		if (HeigoHansoType.HEIGO.equals(type)) {
			// 併合の場合

			// 親裁判のツリー情報を設定
			parentSaibanTreeEntity.setKihonFlg(SystemFlg.FLG_ON.getCd());

			// 子裁判のツリー情報を設定
			childSaibanTreeEntity.setParentSeq(parentSaibanTreeEntity.getSaibanSeq());
			childSaibanTreeEntity.setConnectType(HeigoHansoType.HEIGO.getCd());
			childSaibanTreeEntity.setKihonFlg(SystemFlg.FLG_OFF.getCd());
			childSaibanTreeEntity.setHonsoFlg(SystemFlg.FLG_OFF.getCd());

			// 一時的にできる孫裁判のツリー情報を設定
			for (TSaibanTreeEntity entity : grandChildTreeEntities) {
				// 親は子の兄弟にする
				entity.setParentSeq(parentSaibanTreeEntity.getSaibanSeq());
				entity.setConnectType(HeigoHansoType.HEIGO.getCd());
				tSaibanTreeDao.update(entity);
			}

		} else if (HeigoHansoType.HANSO.equals(type)) {
			// 反訴の場合

			// 親裁判のツリー情報を設定
			parentSaibanTreeEntity.setHonsoFlg(SystemFlg.FLG_ON.getCd());

			// 子裁判のツリー情報を設定
			childSaibanTreeEntity.setParentSeq(parentSaibanTreeEntity.getSaibanSeq());
			childSaibanTreeEntity.setConnectType(HeigoHansoType.HANSO.getCd());
			childSaibanTreeEntity.setKihonFlg(SystemFlg.FLG_OFF.getCd());
			childSaibanTreeEntity.setHonsoFlg(SystemFlg.FLG_OFF.getCd());

			// 一時的にできる孫裁判のツリー情報を設定
			for (TSaibanTreeEntity entity : grandChildTreeEntities) {
				// 親は子の兄弟にする
				entity.setParentSeq(parentSaibanTreeEntity.getSaibanSeq());
				entity.setConnectType(HeigoHansoType.HANSO.getCd());
				tSaibanTreeDao.update(entity);
			}

		} else {
			// それ以外の場合はない
			throw new RuntimeException("想定されないパターン");
		}

		// 子孫裁判を1つのリストにマージ
		List<TSaibanTreeEntity> descendantSaibanTreeEntities = new ArrayList<>();
		descendantSaibanTreeEntities.add(childSaibanTreeEntity);
		descendantSaibanTreeEntities.addAll(grandChildTreeEntities);

		// 親裁判に紐づく予定を子孫裁判に紐づけます
		linkTrialSchedule(parentSaibanTreeEntity.getSaibanSeq(), descendantSaibanTreeEntities);

		// 更新処理
		tSaibanTreeDao.update(parentSaibanTreeEntity);
		tSaibanTreeDao.update(childSaibanTreeEntity);
	}

	/**
	 * 親裁判に紐づく予定を子孫の裁判にも紐づけます
	 *
	 * @param parentSeq
	 * @param descendantSaibanTreeEntities
	 */
	private void linkTrialSchedule(Long parentSeq, List<TSaibanTreeEntity> descendantSaibanTreeEntities) {

		// 親裁判に紐づく裁判期日情報を取得します。
		List<TSaibanLimitEntity> saibanLimitEntityList = tSaibanLimitDao.selectBySaibanSeq(parentSeq);

		// 取得した裁判期日から、紐づく予定情報を取得します。
		List<Long> saibanLimitSeqList = saibanLimitEntityList.stream()
				.map(TSaibanLimitEntity::getSaibanLimitSeq)
				.collect(Collectors.toList());
		List<TScheduleEntity> scheduleEntityList = tScheduleDao.selectBySaibanLimitSeqList(saibanLimitSeqList);

		// 取得した予定情報から、親裁判の予定を子裁判に紐づける
		for (TScheduleEntity entity : scheduleEntityList) {

			// 日付判定用変数
			LocalDateTime limitDate = LocalDateTime.of(entity.getDateTo(), entity.getTimeTo());

			// 条件：予定終了日時 > 現在日時
			if (limitDate.isAfter(LocalDateTime.now())) {

				// 子孫裁判にも紐づける
				for (TSaibanTreeEntity descendantEntity : descendantSaibanTreeEntities) {
					TSaibanLimitRelationEntity grandChildSaibanLimitRelationEntity = new TSaibanLimitRelationEntity();
					grandChildSaibanLimitRelationEntity.setSaibanSeq(descendantEntity.getSaibanSeq());
					grandChildSaibanLimitRelationEntity.setSaibanLimitSeq(entity.getSaibanLimitSeq());
					tSaibanLimitRelationDao.insert(grandChildSaibanLimitRelationEntity);
				}
			}
		}

	}

	/**
	 * 併合裁判追加可能か、反訴裁判追加可能かを判定し、裁判のオプション情報表示フォームにセットする。
	 * 
	 * @param saibanSeq
	 * @param viewForm
	 */
	private void addHeigoHansoFlg(Long saibanSeq, Long ankenId, TSaibanEntity entity, SaibanEditMinjiViewForm.SaibanOptionViewForm viewForm) {

		// 裁判ツリー情報取得
		TSaibanTreeEntity tSaibanTreeEntity = tSaibanTreeDao.selectBySaibanSeq(saibanSeq);

		// 子裁判の場合、併合裁判は不可
		boolean isAddHeigoSaiban = false;
		boolean isAddHansoSaiban = false;
		if (Objects.isNull(tSaibanTreeEntity.getParentSeq())) {
			// 親裁判なら併合OK
			isAddHeigoSaiban = true;

			// 裁判顧客を取得
			List<SaibanCustomerBean> saibanCustomerBeanList = tSaibanCustomerDao.selectBySeq(saibanSeq);
			List<SaibanCustomerBean> hansoBeanList = saibanCustomerBeanList.stream()
					.filter(bean -> TojishaHyoki.GENKOKU.equalsByCode(bean.getSaibanTojishaHyoki())
							|| TojishaHyoki.HIKOKU.equalsByCode(bean.getSaibanTojishaHyoki())
							|| TojishaHyoki.KOSONIN.equalsByCode(bean.getSaibanTojishaHyoki())
							|| TojishaHyoki.HIKOSONIN.equalsByCode(bean.getSaibanTojishaHyoki()))
					.collect(Collectors.toList());

			// 裁判顧客の状態を判定
			if (hansoBeanList.size() > 0) {
				// 1名でも「原告」「被告」「控訴人」「被控訴人」がいた場合は反訴OK
				isAddHansoSaiban = true;
			}

			// 顧客に反訴対象がいない場合、その他当事者を取得し判定する
			if (!isAddHansoSaiban) {
				// 関与者（その他当事者）を取得
				List<SaibanRelatedKanyoshaBean> saibanRelatedKanyoshaBean = commonSaibanService.getSaibanRelatedKanyoshaBeanList(saibanSeq, KanyoshaType.KYODOSOSHONIN, SystemFlg.FLG_OFF);
				List<SaibanRelatedKanyoshaBean> hansoKanyoBeanList = saibanRelatedKanyoshaBean.stream()
						.filter(bean -> TojishaHyoki.GENKOKU.equalsByCode(bean.getSaibanTojishaHyoki())
								|| TojishaHyoki.HIKOKU.equalsByCode(bean.getSaibanTojishaHyoki())
								|| TojishaHyoki.KOSONIN.equalsByCode(bean.getSaibanTojishaHyoki())
								|| TojishaHyoki.HIKOSONIN.equalsByCode(bean.getSaibanTojishaHyoki()))
						.collect(Collectors.toList());

				// 裁判その他当事者の状態を判定
				if (hansoKanyoBeanList.size() > 0) {
					// 1名でも「原告」「被告」「控訴人」「被控訴人」がいた場合は反訴OK
					isAddHansoSaiban = true;
				}
			}
		}
		viewForm.setAddHeigoSaiban(isAddHeigoSaiban);
		viewForm.setAddHansoSaiban(isAddHansoSaiban);

		// 子裁判となれる裁判の候補を設定する
		if (isAddHeigoSaiban || isAddHansoSaiban) {
			setChildSaibanCandidates(entity, ankenId, viewForm);
		}

	}

	/**
	 * 子裁判になれる裁判情報を裁判のオプション情報表示フォームにセットする。
	 * 
	 * @param entity
	 * @param ankenId
	 * @param viewForm
	 */
	private void setChildSaibanCandidates(TSaibanEntity saibanEntity, Long ankenId, SaibanEditMinjiViewForm.SaibanOptionViewForm viewForm) {

		// 裁判情報を取得
		List<SaibanEditSaibanDto> saibanDtoList = saibanManagementDao.selectSaibanByAnkenId(ankenId);
		Map<SaibanId, SaibanEditSaibanDto> uniqueSaibanEntities = saibanDtoList.stream()
				.collect(Collectors.toMap(
						SaibanEditSaibanDto::getSaibanId,
						Function.identity(),
						(former, latter) -> former));
		saibanDtoList = uniqueSaibanEntities.values().stream()
				.sorted(Comparator.comparing(SaibanEditSaibanDto::getSaibanId))
				.collect(Collectors.toList());

		// 裁判 変換条件
		Function<SaibanEditSaibanDto, Saiban> saibanMapper = dto -> {

			return Saiban.builder()
					.saibanSeq(dto.getSaibanSeq())
					.saibanId(dto.getSaibanId())
					.caseNumber(dto.getCaseNumber())
					.jikenName(dto.getJikenName())
					.status(dto.getSaibanStatus())
					.parentSeq(dto.getParentSeq())
					.connectType(dto.getConnectType())
					.isHonso(SystemFlg.codeToBoolean(dto.getHonsoFlg()))
					.isKihon(SystemFlg.codeToBoolean(dto.getKihonFlg()))
					.tabName(getSaibanTabName(dto.getCaseNumber().getMark(), dto.getSaibanId()))
					.isMainSaiban(SystemFlg.codeToBoolean(dto.getHonsoFlg()) || SystemFlg.codeToBoolean(dto.getKihonFlg()))
					.build();
		};

		// 親裁判SEQでのリストを作成
		Map<Long, List<Saiban>> parentGroupingMap = saibanDtoList.stream()
				.filter(dto -> Objects.nonNull(dto.getParentSeq()))
				.map(saibanMapper)
				.collect(Collectors.groupingBy(Saiban::getParentSeq));

		// 裁判一覧
		List<Saiban> saibanList = saibanDtoList.stream()
				.map(saibanMapper)
				.map(saiban -> {
					saiban.setChild(parentGroupingMap.get(saiban.getSaibanSeq()));
					return saiban;
				})
				.filter(dto -> Objects.isNull(dto.getParentSeq()))
				.collect(Collectors.toList());

		// 案件に紐づく裁判情報マップを作成します。ここで親子関係のソートに変えます。
		Map<Long, Saiban> saibanListMap = new LinkedHashMap<>();// Map<裁判SEQ,画面表示用裁判情報>
		createTreeMap(saibanList, saibanListMap);

		Long branchNumber = saibanEntity.getSaibanBranchNo();

		// 選択した裁判情報を設定
		SaibanEditSaibanDto selectSaibanDto = uniqueSaibanEntities.getOrDefault(new SaibanId(ankenId, branchNumber),
				new SaibanEditSaibanDto());
		Saiban thisSaiban = saibanListMap.get(selectSaibanDto.getSaibanSeq());

		// 子裁判となれる裁判の候補を設定する
		List<Saiban> childSaibanCandidates = saibanList.stream()
				// 子裁判ではない && 選択中の裁判ではない
				.filter(saiban -> !saiban.isChild() && !Objects.equals(saiban, thisSaiban))
				.collect(Collectors.toList());
		viewForm.setChildSaibanCandidates(childSaibanCandidates);

	}

}