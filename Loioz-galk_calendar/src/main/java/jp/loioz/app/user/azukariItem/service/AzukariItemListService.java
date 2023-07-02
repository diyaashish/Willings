package jp.loioz.app.user.azukariItem.service;

import java.util.List;
import java.util.Objects;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.azukariItem.form.AzukariItemSearchForm;
import jp.loioz.app.user.azukariItem.form.AzukariItemViewForm;
import jp.loioz.common.constant.CommonConstant.TargetType;
import jp.loioz.dao.TAnkenAzukariItemDao;
import jp.loioz.dao.TCustomerCommonDao;
import jp.loioz.domain.condition.AzukariItemSearchCondition;
import jp.loioz.dto.AzukariItemListDto;
import jp.loioz.dto.CustomerKanyoshaPulldownDto;

/**
 * 預り品一覧画面のserviceクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AzukariItemListService extends DefaultService {

	/** 預り品用のDaoクラス */
	@Autowired
	private TAnkenAzukariItemDao tAnkenAzukariItemDao;

	/** 顧客井共通のDaoクラス */
	@Autowired
	private TCustomerCommonDao tCustomerCommonDao;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 預り品一覧画面の表示情報を作成する(検索なし)
	 *
	 * @return 画面表示情報
	 */
	public AzukariItemViewForm createViewForm() {
		return new AzukariItemViewForm();
	}

	/**
	 * 預り品一覧画面の表示情報を作成する
	 *
	 * @param searchForm
	 * @return 画面表示情報
	 */
	public AzukariItemViewForm createViewForm(AzukariItemSearchForm searchForm) {

		/* 初期設定 */
		AzukariItemViewForm viewForm = new AzukariItemViewForm();
		Long ankenId = searchForm.getTransitionAnkenId();
		viewForm.setAnkenId(ankenId);

		return viewForm;
	}

	/**
	 * 一覧データ取得・設定処理
	 * 
	 * @param viewForm
	 * @param searchForm
	 */
	public void setData(AzukariItemViewForm viewForm, AzukariItemSearchForm searchForm) {

		// データの検索を行います。
		Page<Long> page = this.setPage(searchForm);

		// 検索処理
		List<AzukariItemListDto> azukariItemDtoList = tAnkenAzukariItemDao.selectAzukariItemListByAnkenItemSeq(page.getContent());

		// 案件に紐づく顧客、関与者一覧を取得する
		List<CustomerKanyoshaPulldownDto> customerList = tCustomerCommonDao.selectCustomerByAnkenId(searchForm.getTransitionAnkenId());
		List<CustomerKanyoshaPulldownDto> kanyoshaList = tCustomerCommonDao.selectKanyoshaPulldownByAnkenId(searchForm.getTransitionAnkenId());
		for (AzukariItemListDto dto : azukariItemDtoList) {
			if (TargetType.CUSTOMER.getCd().equals(dto.getAzukariFromType())) {
				dto.setAzukariFrom(customerList.stream().filter(e -> Objects.equals(e.getId(), dto.getAzukariFromCustomerId())).findFirst()
						.orElse(new CustomerKanyoshaPulldownDto()).getName());
			} else if (TargetType.KANYOSHA.getCd().equals(dto.getAzukariFromType())) {
				dto.setAzukariFrom(kanyoshaList.stream().filter(e -> Objects.equals(e.getId(), dto.getAzukariFromKanyoshaSeq())).findFirst()
						.orElse(new CustomerKanyoshaPulldownDto()).getName());
			} else {
				// なにもしない
			}
			if (TargetType.CUSTOMER.getCd().equals(dto.getReturnToType())) {
				dto.setReturnTo(customerList.stream().filter(e -> Objects.equals(e.getId(), dto.getReturnToCustomerId())).findFirst()
						.orElse(new CustomerKanyoshaPulldownDto()).getName());
			} else if (TargetType.KANYOSHA.getCd().equals(dto.getReturnToType())) {
				dto.setReturnTo(kanyoshaList.stream().filter(e -> Objects.equals(e.getId(), dto.getReturnToKanyoshaSeq())).findFirst()
						.orElse(new CustomerKanyoshaPulldownDto()).getName());
			} else {
				// なにもしない
			}
		}

		// 画面に一覧情報を設定
		viewForm.setAzukariItemDtoList(azukariItemDtoList);
		viewForm.setCount(page.getTotalElements());
		viewForm.setPage(page);

	}

	/**
	 * ページャの設定
	 *
	 * @param 加工した一覧情報
	 * @param 検索条件
	 * @return
	 */
	private Page<Long> setPage(AzukariItemSearchForm searchForm) {

		// 検索用オブジェクトの作成
		AzukariItemSearchCondition azukariItemSearchCondition = searchForm.toSearchCondition();

		// ページング情報の取得
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 一時取得
		List<Long> nyukinIdList = tAnkenAzukariItemDao.selectConditions(azukariItemSearchCondition, options);

		// 加工したデータ(Dto)からページャ情報を設定
		Page<Long> page = new PageImpl<>(nyukinIdList, pageable, options.getCount());

		return page;
	}

}