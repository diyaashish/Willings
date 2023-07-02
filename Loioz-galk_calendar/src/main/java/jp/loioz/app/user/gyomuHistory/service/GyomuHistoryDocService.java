package jp.loioz.app.user.gyomuHistory.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.excel.builder.naibu.En0001ExcelBuilder;
import jp.loioz.app.common.excel.config.ExcelConfig;
import jp.loioz.app.common.excel.dto.GyomuHistoryExcelDto;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.bean.AnkenCustomerRelationBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TGyomuHistoryDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.dao.TSaibanJikenDao;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CaseNumber;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.AnkenDtoForGyomuHistory;
import jp.loioz.dto.BunyaDto;
import jp.loioz.dto.GyomuHistoryDto;
import jp.loioz.dto.GyomuHistoryListBean;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TPersonEntity;
import jp.loioz.entity.TSaibanJikenEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class GyomuHistoryDocService extends DefaultService {

	/** エクセル出力用の設定ファイル */
	@Autowired
	private ExcelConfig excelConfig;

	/** 分野共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** 業務履歴Daoクラス */
	@Autowired
	private TGyomuHistoryDao tGyomuHistoryDao;

	/** 案件情報用のDaoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 名簿情報用のDaoクラス */
	@Autowired
	private TPersonDao tPersonDao;
	
	/** 裁判事件Daoクラス */
	@Autowired
	private TSaibanJikenDao tSaibanJikenDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * Excel出力用データの取得
	 *
	 * @param transitionType
	 * @param searchForm
	 * @return
	 */
	public List<GyomuHistoryDto> setData(List<Long> gyomuHistorySeqs, String transitionType) {

		// 検索条件より業務履歴情報の取得
		List<GyomuHistoryListBean> gyomuHistoryListBean = tGyomuHistoryDao.selectBySeqList(gyomuHistorySeqs);
		List<GyomuHistoryDto> gyomuHistoryList = this.setGyomuHistoryList(gyomuHistoryListBean, transitionType);
		return gyomuHistoryList;
	}

	public void excel(HttpServletResponse response, List<Long> gyomuHistorySeqs, Long ankenId, String ankenName, Long customerId, String customerName,
			String transitionType) throws Exception {

		// ■1.Builderを定義
		En0001ExcelBuilder en0001excelBuilder = new En0001ExcelBuilder();
		en0001excelBuilder.setConfig(excelConfig);

		// ■2.DTOを定義
		GyomuHistoryExcelDto gyomuHistoryExcelDto = en0001excelBuilder.createNewTargetBuilderDto();
		List<GyomuHistoryDto> gyomuHistoryDtoList = new ArrayList<GyomuHistoryDto>();

		// ■3.DTOに設定するデータを取得と設定
		if (CommonConstant.TransitionType.CUSTOMER.equalsByCode(transitionType)) {
			// 顧客軸
			TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(customerId);
			if (personEntity == null) {
				throw new DataNotFoundException("名簿情報が存在しません。[personId=" + customerId + "]");
			}
			
			gyomuHistoryExcelDto.setAnkenId(ankenId != null ? AnkenId.of(ankenId).toString() : CommonConstant.BLANK);
			gyomuHistoryExcelDto.setAnkenName(ankenName);
			gyomuHistoryExcelDto.setCustomerId(CustomerId.of(customerId).toString());
			gyomuHistoryExcelDto.setCustomerName(PersonName.fromEntity(personEntity).getName());
			gyomuHistoryDtoList = this.setData(gyomuHistorySeqs, transitionType);
			gyomuHistoryExcelDto.setTransitionType(transitionType);
			gyomuHistoryExcelDto.setGyomuHistoryDtoList(gyomuHistoryDtoList);
			gyomuHistoryExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));
		} else {
			// 案件軸
			TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
			ankenName = StringUtils.isEmpty(tAnkenEntity.getAnkenName()) ? "(案件名未入力)" : tAnkenEntity.getAnkenName();
			
			// 分野情報取得
			Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));
			
			BunyaDto bunya = bunyaMap.get(tAnkenEntity.getBunyaId());
			gyomuHistoryExcelDto.setCustomerId(customerId != null ? CustomerId.of(customerId).toString() : CommonConstant.BLANK);
			gyomuHistoryExcelDto.setCustomerName(customerName);
			gyomuHistoryExcelDto.setAnkenId(AnkenId.of(ankenId).toString());
			gyomuHistoryExcelDto.setBunyaName(bunya.getVal());
			gyomuHistoryExcelDto.setBunyaName(bunyaMap.get(tAnkenEntity.getBunyaId()).getVal());
			gyomuHistoryExcelDto.setAnkenName(ankenName);
			gyomuHistoryDtoList = this.setData(gyomuHistorySeqs, transitionType);
			gyomuHistoryExcelDto.setTransitionType(transitionType);
			gyomuHistoryExcelDto.setGyomuHistoryDtoList(gyomuHistoryDtoList);
			gyomuHistoryExcelDto.setOutPutRange(DateUtils.getJpDay(LocalDate.now()));
		}
		en0001excelBuilder.setGyomuHistoryExcelDto(gyomuHistoryExcelDto);
		try {
			// Excelファイルの出力処理
			en0001excelBuilder.makeExcelFile(response);
		} catch (Exception e) {
			logger.error("帳票出力時のエラー", e);
			throw new RuntimeException(e);
		}

	}
	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * Bean → Dto データの加工を行う
	 *
	 * @param gyomuHistoryListBean
	 * @param transitionType
	 * @return
	 */
	private List<GyomuHistoryDto> setGyomuHistoryList(List<GyomuHistoryListBean> gyomuHistoryListBean, String transitionType) {

		// 空のリストを作成
		List<GyomuHistoryDto> dtoList = new ArrayList<>();

		// 分野情報取得
		Map<Long, BunyaDto> bunyaMap = commonBunyaService.getBunyaMap(SystemFlg.codeToBoolean(SystemFlg.FLG_ON.getCd()));

		// 取得結果が0件の場合、空でreturn
		if (gyomuHistoryListBean.isEmpty()) {
			return dtoList;
		}
		List<Long> saibanMinjiSeqList = gyomuHistoryListBean.stream().filter(e -> e.getSaibanSeq() != null && !bunyaMap.get(e.getBunyaId()).isKeiji()).map(GyomuHistoryListBean::getSaibanSeq).collect(Collectors.toList());
		List<Long> saibanKeijiSeqList = gyomuHistoryListBean.stream().filter(e -> e.getSaibanSeq() != null && bunyaMap.get(e.getBunyaId()).isKeiji()).map(GyomuHistoryListBean::getSaibanSeq).collect(Collectors.toList());
		List<TSaibanJikenEntity> minjiSaibanList = tSaibanJikenDao.selectBySaibanSeq(saibanMinjiSeqList);
		List<TSaibanJikenEntity> keijiSaibanList = tSaibanJikenDao.selectByKeijiSaibanSeq(saibanKeijiSeqList);
		Map<Long, String> saibanJikenNameMap = new HashMap<Long, String>();
		Map<Long, CaseNumber> saibanJikenNoMap = new HashMap<Long, CaseNumber>();
		minjiSaibanList.forEach(e -> {
			saibanJikenNameMap.put(e.getSaibanSeq(), e.getJikenName());
			saibanJikenNoMap.put(e.getSaibanSeq(), CaseNumber.fromEntity(e));
		});
		keijiSaibanList.forEach(e -> {
			saibanJikenNameMap.put(e.getSaibanSeq(), e.getJikenName());
			saibanJikenNoMap.put(e.getSaibanSeq(), CaseNumber.fromEntity(e));
		});
		// 業務履歴連番が重複する場合重複部を削除
		Map<Long, GyomuHistoryListBean> gyomuhistoryMap = new LinkedHashMap<Long, GyomuHistoryListBean>();
		for (GyomuHistoryListBean bean : gyomuHistoryListBean) {
			if (!gyomuhistoryMap.containsKey(bean.getGyomuHistorySeq())) {
				gyomuhistoryMap.put(bean.getGyomuHistorySeq(), bean);
			}
		}

		// 業務履歴Dtoに値をセットする
		for (GyomuHistoryListBean bean : gyomuhistoryMap.values()) {
			// 業務履歴情報をセット
			GyomuHistoryDto gyomuHistoryDto = new GyomuHistoryDto();
			gyomuHistoryDto.setTransitionType(TransitionType.of(bean.getTransitionType().toString()));
			gyomuHistoryDto.setGyomuHistorySeq(bean.getGyomuHistorySeq());
			gyomuHistoryDto.setSubject(bean.getSubject());
			gyomuHistoryDto.setMainText(bean.getMainText());
			gyomuHistoryDto.setCreaterName(bean.getAccountName());
			gyomuHistoryDto.setCustomerId(CustomerId.of(bean.getCustomerId()));
			gyomuHistoryDto.setCustomerName(bean.getCustomerName());
			gyomuHistoryDto.setSupportedDateTime(bean.getSupportedAt());
			gyomuHistoryDto.setAnkenId(AnkenId.of(bean.getAnkenId()));
			gyomuHistoryDto.setAnkenName(bean.getAnkenName());
			gyomuHistoryDto.setSaibanBranchNo(bean.getSaibanBranchNo());
			gyomuHistoryDto.setJikenName(saibanJikenNameMap.get(bean.getSaibanSeq()));
			gyomuHistoryDto.setCaseNumber(saibanJikenNoMap.get(bean.getSaibanSeq()));
			Optional.ofNullable(bean.getBunyaId()).ifPresent(id -> gyomuHistoryDto.setBunyaName(bunyaMap.get(id).getVal()));
			// 顧客軸の場合
			if (CommonConstant.TransitionType.CUSTOMER.equalsByCode(transitionType)) {
				gyomuHistoryDto.setAnkenDtoList(this.convertAnkenBeanToDto(tGyomuHistoryDao.selectAnkenBeanBySeq(bean.getGyomuHistorySeq()), bunyaMap));
			}
			// 案件軸の場合
			else {
				if (CommonConstant.CustomerType.KOJIN.equalsByCode(bean.getCustomerType())
						|| CommonConstant.CustomerType.HOJIN.equalsByCode(bean.getCustomerType())) {
					gyomuHistoryDto
							.setAnkenDtoList(this.convertAnkenBeanToDto(tGyomuHistoryDao.selectCustomerBeanBySeq(bean.getGyomuHistorySeq()), bunyaMap));
				} else {
					// 顧客情報が存在しない場合
					List<AnkenDtoForGyomuHistory> ankenDtoList = Arrays.asList();
					gyomuHistoryDto
							.setAnkenDtoList(ankenDtoList);
				}
			}
			dtoList.add(gyomuHistoryDto);
		}
		return dtoList;
	}

	/**
	 * 案件Beanから案件Dtoを作成するためのメソッド
	 *
	 * @param ankenBeans
	 * @return List<AnkenDto>
	 */
	private List<AnkenDtoForGyomuHistory> convertAnkenBeanToDto(List<AnkenCustomerRelationBean> ankenBeans,Map<Long, BunyaDto> bunyaMap) {
		List<AnkenDtoForGyomuHistory> ankenDtoList = new ArrayList<>();
		for (AnkenCustomerRelationBean bean : ankenBeans) {
			AnkenDtoForGyomuHistory dto = new AnkenDtoForGyomuHistory();
			dto.setAnkenId(AnkenId.of(bean.getAnkenId()).toString());
			dto.setAnkenName(bean.getAnkenName());
			dto.setBunyaName(
					Optional.ofNullable(bean.getBunyaId()).map(bunya -> bunyaMap.get(bunya).getVal()).orElse(CommonConstant.BLANK));
			dto.setCustomerId(
					CustomerId.of(Optional.ofNullable(bean.getCustomerId()).map(id -> id.toString()).orElse(CommonConstant.BLANK)).toString());
			dto.setCustomerName(bean.getCustomerName());
			ankenDtoList.add(dto);
		}
		return ankenDtoList;
	}

}
