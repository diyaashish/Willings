package jp.loioz.app.user.nyushukkinYotei.shukkin.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.service.CommonAccountService;
import jp.loioz.app.common.service.CommonBunyaService;
import jp.loioz.app.common.service.CommonKaikeiService;
import jp.loioz.app.common.service.CommonSelectBoxService;
import jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchRifineType;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinListSearchForm;
import jp.loioz.app.user.nyushukkinYotei.shukkin.form.ShukkinListViewForm;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.AnkenStatus;
import jp.loioz.common.constant.CommonConstant.KozaRelateType;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MTenantDao;
import jp.loioz.dao.ShukkinListDao;
import jp.loioz.dao.TGinkoKozaDao;
import jp.loioz.domain.condition.ShukkinListSearchCondition;
import jp.loioz.domain.value.AnkenId;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.SeisanId;
import jp.loioz.dto.GinkoKozaDto;
import jp.loioz.dto.ShukkinListBean;
import jp.loioz.dto.ShukkinListDto;
import jp.loioz.entity.MTenantEntity;
import jp.loioz.entity.TGinkoKozaEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class ShukkinListService extends DefaultService {

	/** 共通アカウントServiceクラス */
	@Autowired
	private CommonAccountService commonAccountService;

	/** セレクトボックス 共通Serviceクラス */
	@Autowired
	private CommonSelectBoxService commonSelectBoxService;

	/** 金額の計算用 共通Serviceクラス */
	@Autowired
	private CommonKaikeiService commonKaikeiService;

	/** 分野の共通サービス */
	@Autowired
	private CommonBunyaService commonBunyaService;

	/** テナント情報Daoクラス */
	@Autowired
	private MTenantDao mTenantDao;

	/** 入出金一覧（出金）管理用のDaoクラス */
	@Autowired
	private ShukkinListDao shukkinListDao;

	/** 銀行口座Daoクラス */
	@Autowired
	private TGinkoKozaDao tGinkoKozaDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * テナントの口座情報取得 TODO CommonGinkoKozaService
	 *
	 * @return Map<Long,KozaDto> 口座連番をキーとした、事務所口座情報
	 */
	public List<GinkoKozaDto> getTenantKozaMap() {

		MTenantEntity mTenantEntity = mTenantDao.selectBySeq(SessionUtils.getTenantSeq());
		List<TGinkoKozaEntity> tenantGinkoKoza = tGinkoKozaDao.selectKozaListByTenantSeq(SessionUtils.getTenantSeq());

		// 口座連番をキーとして、Mapを作成
		List<GinkoKozaDto> tenantKozaMap = tenantGinkoKoza.stream()
				.map(entity -> {
					GinkoKozaDto ginkoKozaDto = new GinkoKozaDto();
					ginkoKozaDto.setGinkoAccountSeq(entity.getGinkoAccountSeq());
					ginkoKozaDto.setBranchNo(entity.getBranchNo());
					ginkoKozaDto.setLabelName(entity.getLabelName());
					ginkoKozaDto.setGinkoName(entity.getGinkoName());
					ginkoKozaDto.setShitenName(entity.getShitenName());
					ginkoKozaDto.setShitenNo(entity.getShitenNo());
					ginkoKozaDto.setKozaType(entity.getKozaType());
					ginkoKozaDto.setKozaNo(entity.getKozaNo());
					ginkoKozaDto.setKozaName(entity.getKozaName());
					ginkoKozaDto.setTenantSeq(mTenantEntity.getTenantSeq());
					ginkoKozaDto.setTenantName(mTenantEntity.getTenantName());
					ginkoKozaDto.setKozaRelateType(KozaRelateType.TENANT);
					return ginkoKozaDto;
				})
				.filter(dto -> !dto.isNoneDisp())
				.collect(Collectors.toList());

		return tenantKozaMap;
	}

	/**
	 * viewFormの作成
	 *
	 * @return画面情報のフォーム
	 */
	public ShukkinListViewForm createViewForm(ShukkinListSearchForm searchForm) {

		ShukkinListViewForm viewForm = new ShukkinListViewForm();

		// 各セレクトボックス値の取得
		viewForm.setTantoLawyerOptionList(commonSelectBoxService.getAccountSelectBoxFilterByType(AccountType.LAWYER));
		viewForm.setTantoJimuOptionList(commonSelectBoxService.getAccountSelectBoxFilterByType(AccountType.JIMU));
		viewForm.setAccountKozaList(commonAccountService.getAccountSeqKeyAccountKozaMap());
		viewForm.setTenantKozaList(getTenantKozaMap());

		return viewForm;
	}

	/**
	 * 検索条件からデータ取得
	 *
	 * @param searchForm
	 * @param viewForm
	 */
	public void setData(ShukkinListSearchForm searchForm, ShukkinListViewForm viewForm) {

		// DB取得
		Map<Long, String> accountNameMap = commonAccountService.getAccountNameMap();

		// ページャ情報の設定
		Page<Long> page = this.setPage(searchForm);

		// データの取得
		List<ShukkinListBean> shukkinListBeans = shukkinListDao.selectBeansBySeqList(page.getContent());

		// bean -> dto
		List<ShukkinListDto> shukkinListDtoList = shukkinListBeans.stream()
				.map(bean -> convertBean2Dto(bean, accountNameMap))
				.collect(Collectors.toList());

		// ページャ情報の設定
		viewForm.setOpenSearchArea(true);
		viewForm.setShukkinList(shukkinListDtoList);
		viewForm.setCount(page.getTotalElements());
		viewForm.setPage(page);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * ページャの設定
	 *
	 * @param shukkinListDto
	 * @param searchForm
	 * @return
	 */
	private Page<Long> setPage(ShukkinListSearchForm searchForm) {

		// 検索条件の設定
		ShukkinListSearchCondition shukkinListSearchCondition = searchForm.toShukkinListSearchCondition();

		// ページング情報の取得
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		List<Long> shukkinSeqList = shukkinListDao.selectSeqByConditions(shukkinListSearchCondition, options);

		// 加工したデータ(Dto)からページャ情報を設定
		Page<Long> page = new PageImpl<>(shukkinSeqList, pageable, options.getCount());

		return page;
	}

	/**
	 * DBから取得したデータを一覧表示用に変換します。(Bean -> Dto)
	 *
	 * @param bean
	 * @param accountNameMap
	 * @return Dto
	 */
	private ShukkinListDto convertBean2Dto(ShukkinListBean bean, Map<Long, String> accountNameMap) {

		// オブジェクトの作成
		ShukkinListDto dto = new ShukkinListDto();
		SeisanId seisanId = new SeisanId(bean.getCustomerId(), bean.getSeisanId());

		// 未処理・処理済の判定Mapper
		Function<ShukkinListBean, ShukkinSearchRifineType> refineTypeMapper = data -> {
			if (data.getNyushukkinDate() == null) {
				return ShukkinSearchRifineType.UNPROCESSED;
			} else {
				return ShukkinSearchRifineType.PROCESSED;
			}
		};

		// 精算額の設定Mapper
		Function<ShukkinListBean, BigDecimal> decimalMapper = data -> {
			if (data.getSeisanId() != null) {
				// 総額 - 総入金額
				return bean.getSeisanGaku();
			} else {
				// 精算IDがない場合、入金予定額設定
				return bean.getNyushukkinYoteiGaku();
			}
		};

		// 期限を超えたかの判定Mapper
		Function<ShukkinListBean, Boolean> limitOverMapper = data -> {
			LocalDate shukkinYoteiDate = data.getNyushukkinYoteiDate();
			LocalDate shukkinDate = data.getNyushukkinDate();
			// 予定日が現在日を超えている && 出金日が登録されていない -> true
			return (shukkinYoteiDate.isBefore(LocalDate.now()) && shukkinDate == null);
		};

		// =========＝=＝===| 以下、データの設定 |==＝＝＝============
		// ■顧客情報
		dto.setCustomerId(CustomerId.of(bean.getCustomerId()));
		dto.setCustomerName(bean.getCustomerName());

		// ■案件情報
		dto.setAnkenId(AnkenId.of(bean.getAnkenId()));
		dto.setAnkenName(bean.getAnkenName());
		dto.setBunya(commonBunyaService.getBunya(bean.getBunyaId()));

		// ■支払者
		dto.setShiharaishaName(bean.getShiharaishaName());

		// ■支払先
		List<String> names = Arrays.asList(bean.getShiharaiSakiCustomerName(), bean.getShiharaiSakiKanyoshaName());
		dto.setShiharaiSakiName(names.stream().filter(StringUtils::isNotEmpty).findFirst().orElse(""));

		// ■入金情報
		dto.setNyushukkinYoteiSeq(bean.getNyushukkinYoteiSeq());
		dto.setNyushukkinYoteiDate(bean.getNyushukkinYoteiDate());
		dto.setNyushukkinYoteiGaku(bean.getNyushukkinYoteiGaku());
		dto.setDispNyushukkinYoteiGaku(commonKaikeiService.toDispAmountLabel(bean.getNyushukkinYoteiGaku()));
		dto.setSeisanGaku(decimalMapper.apply(bean));
		dto.setDispSeisanGaku(commonKaikeiService.toDispAmountLabel(decimalMapper.apply(bean)));

		// ■精算情報
		dto.setSeisanSeq(bean.getSeisanSeq());
		dto.setSeisanId(seisanId);

		// ■その他ステータス
		dto.setLimitOver(limitOverMapper.apply(bean));

		ShukkinSearchRifineType refineType = refineTypeMapper.apply(bean);
		dto.setShukkinSearchRifineType(refineType);

		if (refineType == ShukkinSearchRifineType.PROCESSED || AnkenStatus.isSeisanComp(bean.getAnkenStatus())) {
			dto.setCompleted(true);
		}

		return dto;
	}

}
