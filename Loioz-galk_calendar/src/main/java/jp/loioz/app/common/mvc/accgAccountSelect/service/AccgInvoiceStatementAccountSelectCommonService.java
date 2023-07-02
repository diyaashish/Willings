package jp.loioz.app.common.mvc.accgAccountSelect.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccgInvoiceStatementService;
import jp.loioz.app.common.service.CommonAccgService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.common.constant.CommonConstant.AnkenType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.TAnkenDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.dto.GinkoKozaBean;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TAnkenEntity;
import jp.loioz.entity.TGinkoKozaEntity;

/**
 * 口座選択モーダルサービス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccgInvoiceStatementAccountSelectCommonService extends DefaultService {

	/** 請求書Daoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	/** テナントDaoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 案件Daoクラス */
	@Autowired
	private TAnkenDao tAnkenDao;

	/** 会計管理共通サービス */
	@Autowired
	private CommonAccgService commonAccgService;

	/** 会計管理：請求書詳細、精算書詳細の共通サービス */
	@Autowired
	private CommonAccgInvoiceStatementService commonAccgInvoiceStatementService;
	
	/** アカウント共通サービス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** ログの出力を行うクラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 事務所／売上計上先の銀行口座情報を取得します。
	 * 
	 * @param accgDocSeq
	 * @param ankenId
	 * @return
	 */
	public List<GinkoKozaDto> getTenantAccountGinkoKozaList(Long accgDocSeq, Long ankenId) {
		// マスタ情報の取得
		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		Long salesOwnerAccountSeq = commonAccgInvoiceStatementService.getSalesAccountSeq(accgDocSeq);

		// 口座情報の取得
		List<TGinkoKozaEntity> tenantKozaList = tGinkoKozaDao.selectKozaListByTenantSeq(SessionUtils.getTenantSeq());
		List<TGinkoKozaEntity> salesOwnerKozaList = new ArrayList<>();
		if (salesOwnerAccountSeq != null) {
			salesOwnerKozaList = tGinkoKozaDao.selectKozaListByAccountSeqList(Arrays.asList(salesOwnerAccountSeq));
		}

		// 案件情報の取得
		TAnkenEntity tAnkenEntity = tAnkenDao.selectById(ankenId);
		if (tAnkenEntity == null) {
			// データの取得ができませんでした
			throw new DataNotFoundException("ankenId:" + ankenId);
		}
		String ankenType = tAnkenEntity.getAnkenType();
		
		// Entity -> Bean 変換条件
		Function<TGinkoKozaEntity, GinkoKozaBean> mapper = entity -> {
			GinkoKozaBean bean = new GinkoKozaBean();
			comvert2GinkoKozaBean(entity, bean);
			if (entity.getTenantSeq() != null) {
				bean.setTenantName(mTenantEntity.getTenantName());
			} else if (entity.getAccountSeq() != null) {
				bean.setAccountName(accountNameMap.get(entity.getAccountSeq()));
			} else {
				// 想定外のケース
				throw new RuntimeException();
			}
			return bean;
		};
		
		List<GinkoKozaBean> kozaBeanList = Collections.emptyList();
		if (AnkenType.JIMUSHO.equalsByCode(ankenType)) {
			// 事務所の場合、表示する口座情報は「テナント口座」と「売上計上先の口座情報」
			kozaBeanList = LoiozCollectionUtils.mergeLists(tenantKozaList, salesOwnerKozaList).stream()
					.map(entity -> mapper.apply(entity))
					.collect(Collectors.toList());
		} else if (AnkenType.KOJIN.equalsByCode(ankenType)) {
			// 個人の場合、表示する口座情報は「売上計上先の口座情報」
			kozaBeanList = salesOwnerKozaList.stream()
					.map(entity -> mapper.apply(entity))
					.collect(Collectors.toList());
		} else {
			// 想定外のケース
			throw new RuntimeException();
		}

		// Bean -> Dtoにして返却
		return kozaBeanList.stream()
				.map(bean -> setGinkoKozaDto(bean, false))
				.filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toList());
	}

	/**
	 * 銀行口座情報を以下形式の文字列で取得します<br>
	 *   銀行名 支店名（支店番号 xxx） \r\n
	 *   口座種類 xxxxxxxxxx　口座名義 山田 太郎
	 * 
	 * @param ginkoAccountSeq
	 * @throws AppException
	 */
	public String getTenantBankDetail(Long ginkoAccountSeq) throws AppException {
		// 銀行口座情報取得
		GinkoKozaBean ginkoKozaBean = tGinkoKozaDao.selectBeanBySeq(ginkoAccountSeq);
		if (ginkoKozaBean == null) {
			// 楽観ロックエラー
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_NO_DATA);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 振込先
		String tenantBankDetail = commonAccgService.bankToDetail(ginkoKozaBean.getGinkoName(),
				ginkoKozaBean.getShitenName(), ginkoKozaBean.getShitenNo(), ginkoKozaBean.getKozaType(),
				ginkoKozaBean.getKozaNo(), ginkoKozaBean.getKozaName());

		return tenantBankDetail;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 銀行口座情報のBeanに変換する
	 * 
	 * @param entity
	 * @param bean
	 */
	private void comvert2GinkoKozaBean(TGinkoKozaEntity entity, GinkoKozaBean bean) {
		if (entity == null || bean == null) {
			return;
		}

		bean.setAccountSeq(entity.getAccountSeq());
		bean.setGinkoAccountSeq(entity.getGinkoAccountSeq());
		bean.setGinkoName(entity.getGinkoName());
		bean.setKozaName(entity.getKozaName());
		bean.setKozaNo(entity.getKozaNo());
		bean.setKozaType(entity.getKozaType());
		bean.setLabelName(entity.getLabelName());
		bean.setShitenName(entity.getShitenName());
		bean.setShitenNo(entity.getShitenNo());
		bean.setTenantSeq(entity.getTenantSeq());
	}

	/**
	 * GinkoKozaBean -> GinkoKozaDto ※表示用プルダウンで使用することを想定 プルダウン表示条件外のものはNULLの可能性がある
	 *
	 * @param GinkoKozaBean 銀行口座情報のBean
	 * @param isDeleted 編集時のプルダウンは入力情報が空でも表示する
	 * @return 銀行口座情報のDto
	 */
	private Optional<GinkoKozaDto> setGinkoKozaDto(GinkoKozaBean bean, boolean isDeleted) {

		// Entityの値をDtoに設定します。
		GinkoKozaDto ginkoKozaDto = new GinkoKozaDto();

		String labelName = bean.getLabelName();
		String ginkoName = bean.getGinkoName();
		String shitenName = bean.getShitenName();
		String shitenNo = bean.getShitenNo();
		String kozaType = bean.getKozaType();
		String kozaNo = bean.getKozaNo();
		String kozaName = bean.getKozaName();

		if (!StringUtils.isAllEmpty(labelName, ginkoName, shitenName, shitenNo, kozaType, kozaNo, kozaName) || isDeleted) {
			// 表示名、金融機関名、支店名、支店番号、口座種類、口座番号、口座名義 のうち
			// いずれかが入力されている場合のみ リストとして表示する。

			// ---------------------------------------------------------------
			// 銀行口座
			// ---------------------------------------------------------------
			ginkoKozaDto.setGinkoAccountSeq(bean.getGinkoAccountSeq());
			ginkoKozaDto.setLabelName(StringUtils.defaultString(labelName));
			ginkoKozaDto.setGinkoName(StringUtils.defaultString(ginkoName));
			ginkoKozaDto.setShitenName(StringUtils.defaultString(shitenName));
			ginkoKozaDto.setShitenNo(StringUtils.defaultString(shitenNo));
			ginkoKozaDto.setKozaType(StringUtils.defaultString(kozaType));
			ginkoKozaDto.setKozaNo(StringUtils.defaultString(kozaNo));
			ginkoKozaDto.setKozaName(StringUtils.defaultString(kozaName));
			ginkoKozaDto.setTenantSeq(bean.getTenantSeq());
			ginkoKozaDto.setAccountSeq(bean.getAccountSeq());
			ginkoKozaDto.setToolTipContents(ginkoKozaDto.getDisplayGinkoKozaInfo());
			// ---------------------------------------------------------------
			// テナント名
			// ---------------------------------------------------------------
			ginkoKozaDto.setTenantName(bean.getTenantName());
			// ---------------------------------------------------------------
			// アカウント名
			// ---------------------------------------------------------------
			ginkoKozaDto.setAccountName(bean.getAccountName());

			// 表示名の設定
			ginkoKozaDto.setListDisplayName();

		} else {
			ginkoKozaDto = null;
		}
		return Optional.ofNullable(ginkoKozaDto);
	}

}