package jp.loioz.app.user.meiboList.service;

import java.util.List;
import java.util.stream.Collectors;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.user.meiboList.dto.AdvisorMeiboListDto;
import jp.loioz.app.user.meiboList.dto.AllMeiboListDto;
import jp.loioz.app.user.meiboList.dto.BengoshiMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerAllMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerHojinMeiboListDto;
import jp.loioz.app.user.meiboList.dto.CustomerKojinMeiboListDto;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm.AdvisorMeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm.AllMeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm.BengoshiMeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm.CustomerAllMeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm.CustomerHojinMeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListSearchForm.CustomerKojinMeiboListSearchForm;
import jp.loioz.app.user.meiboList.form.MeiboListViewForm;
import jp.loioz.bean.meiboList.AdvisorMeiboListBean;
import jp.loioz.bean.meiboList.AllMeiboListBean;
import jp.loioz.bean.meiboList.BengoshiMeiboListBean;
import jp.loioz.bean.meiboList.CustomerAllMeiboListBean;
import jp.loioz.bean.meiboList.CustomerHojinMeiboListBean;
import jp.loioz.bean.meiboList.CustomerKojinMeiboListBean;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.meiboList.MeiboListConstant.MeiboMenu;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MeiboListDao;
import jp.loioz.domain.condition.meiboList.AdvisorMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.AdvisorMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.AllMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.AllMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.BengoshiMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.BengoshiMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerAllMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerAllMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerHojinMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerHojinMeiboListSortCondition;
import jp.loioz.domain.condition.meiboList.CustomerKojinMeiboListSearchCondition;
import jp.loioz.domain.condition.meiboList.CustomerKojinMeiboListSortCondition;
import jp.loioz.domain.value.PersonAttribute;
import jp.loioz.domain.value.PersonName;

/**
 * 案件一覧画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MeiboListService {

	/** 名簿一覧Dao */
	@Autowired
	private MeiboListDao meiboListDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 画面表示情報を作成する
	 * 
	 * @return
	 */
	public MeiboListViewForm createViewForm() {
		MeiboListViewForm viewForm = new MeiboListViewForm();
		return viewForm;
	}

	/**
	 * 名簿メニューの画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.MeiboMenuViewForm createMeiboMenuViewForm() {
		MeiboListViewForm.MeiboMenuViewForm meiboMenuViewForm = new MeiboListViewForm.MeiboMenuViewForm();
		return meiboMenuViewForm;
	}

	/**
	 * 名簿検索条件の画面表示用オブジェクトを作成する
	 *
	 * @param meiboMenu
	 * @return
	 */
	public MeiboListViewForm.MeiboSearchViewForm createMeiboSearchViewForm(MeiboMenu meiboMenu) {
		switch (meiboMenu) {
		case ALL:
			return createAllMeiboSearchViewForm();
		case CUSTOMER_KOJIN:
			return createCustomerKojinMeiboSearchViewForm();
		case CUSTOMER_HOJIN:
			return createCustomerHojinMeiboSearchViewForm();
		case BENGOSHI:
			return createBengoshiMeiboSearchViewForm();
		default:
			return null;
		}
	}

	/**
	 * 「名簿検索条件：すべて」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.MeiboSearchViewForm createAllMeiboSearchViewForm() {
		MeiboListViewForm.MeiboSearchViewForm allMeiboSearchViewForm = new MeiboListViewForm.MeiboSearchViewForm();
		return allMeiboSearchViewForm;
	}

	/**
	 * 「名簿検索条件：顧客」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.MeiboSearchViewForm createCustomerAllMeiboSearchViewForm() {
		MeiboListViewForm.MeiboSearchViewForm customerAllMeiboSearchViewForm = new MeiboListViewForm.MeiboSearchViewForm();
		return customerAllMeiboSearchViewForm;
	}

	/**
	 * 「名簿検索条件：個人顧客」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.MeiboSearchViewForm createCustomerKojinMeiboSearchViewForm() {
		MeiboListViewForm.MeiboSearchViewForm customerKojinMeiboSearchViewForm = new MeiboListViewForm.MeiboSearchViewForm();
		return customerKojinMeiboSearchViewForm;
	}

	/**
	 * 「名簿検索条件：法人顧客」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.MeiboSearchViewForm createCustomerHojinMeiboSearchViewForm() {
		MeiboListViewForm.MeiboSearchViewForm customerHojinMeiboSearchViewForm = new MeiboListViewForm.MeiboSearchViewForm();
		return customerHojinMeiboSearchViewForm;
	}

	/**
	 * 「名簿検索条件：顧問」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.MeiboSearchViewForm createAdvisorMeiboSearchViewForm() {
		MeiboListViewForm.MeiboSearchViewForm advisorMeiboSearchViewForm = new MeiboListViewForm.MeiboSearchViewForm();
		return advisorMeiboSearchViewForm;
	}

	/**
	 * 「名簿検索条件：弁護士」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.MeiboSearchViewForm createBengoshiMeiboSearchViewForm() {
		MeiboListViewForm.MeiboSearchViewForm bengoshiMeiboSearchViewForm = new MeiboListViewForm.MeiboSearchViewForm();
		return bengoshiMeiboSearchViewForm;
	}

	/**
	 * 「名簿一覧：すべて」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.AllMeiboListViewForm createAllMeiboListViewForm(MeiboListSearchForm searchForm) {

		// インスタンス化
		MeiboListViewForm.AllMeiboListViewForm allMeiboListViewForm = new MeiboListViewForm.AllMeiboListViewForm();

		// 検索条件をオブジェクト化
		AllMeiboListSearchForm allMeiboSearchForm = searchForm.getAllMeiboListSearchForm();
		AllMeiboListSearchCondition searchConditions = allMeiboSearchForm.toAllMeiboListSearchCondition();
		AllMeiboListSortCondition sortConditions = allMeiboSearchForm.toAllMeiboListSortCondition();

		Pageable pageable = allMeiboSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<AllMeiboListBean> allMeiboListBeanList = null;
		if (StringUtils.isEmpty(searchForm.getQuickSearch())) {
			// 通常の検索の場合
			allMeiboListBeanList = meiboListDao.selectAllMeiboByConditions(searchConditions, sortConditions, options);
		} else {
			// クイック検索の場合
			allMeiboListBeanList = meiboListDao.selectAllMeiboByQuickSearch(StringUtils.removeSpaceCharacter(searchForm.getQuickSearch()), sortConditions, options);
		}

		// convert処理
		List<AllMeiboListDto> allMeiboListDtoList = this.convertAllBean2Dto(allMeiboListBeanList);

		// ページ情報作成
		Page<AllMeiboListBean> page = new PageImpl<>(allMeiboListBeanList, pageable, options.getCount());

		allMeiboListViewForm.setAllMeiboPage(page);
		allMeiboListViewForm.setAllMeiboList(allMeiboListDtoList);
		return allMeiboListViewForm;
	}

	/**
	 * 「名簿一覧：顧客」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.CustomerAllMeiboListViewForm createCustomerAllMeiboListViewForm(MeiboListSearchForm searchForm) {

		// インスタンス化
		MeiboListViewForm.CustomerAllMeiboListViewForm customerAllMeiboListViewForm = new MeiboListViewForm.CustomerAllMeiboListViewForm();

		// 検索条件をオブジェクト化
		CustomerAllMeiboListSearchForm customerAllMeiboListSearchForm = searchForm.getCustomerAllMeiboListSearchForm();
		CustomerAllMeiboListSearchCondition customerAllMeiboListSearchCondition = customerAllMeiboListSearchForm.toCustomerAllMeiboListSearchCondition();
		CustomerAllMeiboListSortCondition customerAllMeiboListSortCondition = customerAllMeiboListSearchForm.toCustomerAllMeiboListSortCondition();

		Pageable pageable = customerAllMeiboListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<CustomerAllMeiboListBean> customerAllMeiboBeanList = meiboListDao.selectCustomerAllMeiboByConditions(customerAllMeiboListSearchCondition, customerAllMeiboListSortCondition, options);

		// convert
		List<CustomerAllMeiboListDto> customerAllMeiboDtoList = this.convertCustomerAllBean2Dto(customerAllMeiboBeanList);

		// ページ情報作成
		List<Long> meiboNoList = customerAllMeiboBeanList.stream().map(bean -> bean.getPersonId()).collect(Collectors.toList());
		Page<Long> page = new PageImpl<>(meiboNoList, pageable, options.getCount());

		customerAllMeiboListViewForm.setCustomerAllMeiboPage(page);
		customerAllMeiboListViewForm.setCustomerAllMeiboListDto(customerAllMeiboDtoList);
		return customerAllMeiboListViewForm;
	}

	/**
	 * 「名簿一覧：個人顧客」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.CustomerKojinMeiboListViewForm createCustomerKojinMeiboListViewForm(MeiboListSearchForm searchForm) {

		// インスタンス化
		MeiboListViewForm.CustomerKojinMeiboListViewForm customerKojinMeiboListViewForm = new MeiboListViewForm.CustomerKojinMeiboListViewForm();

		// 検索条件をオブジェクト化
		CustomerKojinMeiboListSearchForm customerKojinMeiboListSearchForm = searchForm.getCustomerKojinMeiboListSearchForm();
		CustomerKojinMeiboListSearchCondition customerKojinMeiboListSearchCondition = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSearchCondition();
		CustomerKojinMeiboListSortCondition customerKojinMeiboListSortCondition = customerKojinMeiboListSearchForm.toCustomerKojinMeiboListSortCondition();

		Pageable pageable = customerKojinMeiboListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<CustomerKojinMeiboListBean> customerKojinMeiboBeanList = meiboListDao.selectCustomerKojinMeiboByConditions(customerKojinMeiboListSearchCondition, customerKojinMeiboListSortCondition, options);

		// convert処理
		List<CustomerKojinMeiboListDto> customerKojinMeiboListDtoList = this.convertKojinBean2Dto(customerKojinMeiboBeanList);

		// ページ情報作成
		List<Long> meiboNoList = customerKojinMeiboBeanList.stream().map(bean -> bean.getPersonId()).collect(Collectors.toList());
		Page<Long> page = new PageImpl<>(meiboNoList, pageable, options.getCount());

		customerKojinMeiboListViewForm.setCustomerKojinMeiboPage(page);
		customerKojinMeiboListViewForm.setCustomerKojinMeiboListDto(customerKojinMeiboListDtoList);
		return customerKojinMeiboListViewForm;
	}

	/**
	 * 「名簿一覧：法人顧客」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.CustomerHojinMeiboListViewForm createCustomerHojinMeiboListViewForm(MeiboListSearchForm searchForm) {

		// インスタンス化
		MeiboListViewForm.CustomerHojinMeiboListViewForm customerHojinMeiboListViewForm = new MeiboListViewForm.CustomerHojinMeiboListViewForm();

		// 検索条件をオブジェクト化
		CustomerHojinMeiboListSearchForm customerHojinMeiboListSearchForm = searchForm.getCustomerHojinMeiboListSearchForm();
		CustomerHojinMeiboListSearchCondition customerHojinMeiboListSearchCondition = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSearchCondition();
		CustomerHojinMeiboListSortCondition customerHojinMeiboListSortCondition = customerHojinMeiboListSearchForm.toCustomerHojinMeiboListSortCondition();

		Pageable pageable = customerHojinMeiboListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<CustomerHojinMeiboListBean> customerHojinMeiboBeanList = meiboListDao.selectCustomerHojinMeiboByConditions(customerHojinMeiboListSearchCondition, customerHojinMeiboListSortCondition, options);

		// convert処理
		List<CustomerHojinMeiboListDto> customerHojinMeiboListDtoList = this.convertHojinBean2Dto(customerHojinMeiboBeanList);

		// ページ情報作成
		List<Long> meiboNoList = customerHojinMeiboBeanList.stream().map(bean -> bean.getPersonId()).collect(Collectors.toList());
		Page<Long> page = new PageImpl<>(meiboNoList, pageable, options.getCount());

		customerHojinMeiboListViewForm.setCustomerHojinMeiboListDto(customerHojinMeiboListDtoList);
		customerHojinMeiboListViewForm.setCustomerHojinMeiboPage(page);
		return customerHojinMeiboListViewForm;
	}

	/**
	 * 「名簿一覧：顧問」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.AdvisorMeiboListViewForm createAdvisorMeiboListViewForm(MeiboListSearchForm searchForm) {

		// インスタンス化
		MeiboListViewForm.AdvisorMeiboListViewForm advisorMeiboListViewForm = new MeiboListViewForm.AdvisorMeiboListViewForm();

		// 検索条件をオブジェクト化
		AdvisorMeiboListSearchForm advisorMeiboListSearchForm = searchForm.getAdvisorMeiboListSearchForm();
		AdvisorMeiboListSearchCondition advisorMeiboListSearchCondition = advisorMeiboListSearchForm.toAdvisorMeiboListSearchCondition();
		AdvisorMeiboListSortCondition advisorMeiboListSortCondition = advisorMeiboListSearchForm.toAdvisorMeiboListSortCondition();

		Pageable pageable = advisorMeiboListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<AdvisorMeiboListBean> advisorMeiboBeanList = meiboListDao.selectAdvisorMeiboByConditions(advisorMeiboListSearchCondition, advisorMeiboListSortCondition, options);

		// convert
		List<AdvisorMeiboListDto> advisorMeiboDtoList = this.convertAdvisorBean2Dto(advisorMeiboBeanList);

		// ページ情報作成
		List<Long> meiboNoList = advisorMeiboBeanList.stream().map(bean -> bean.getPersonId()).collect(Collectors.toList());
		Page<Long> page = new PageImpl<>(meiboNoList, pageable, options.getCount());

		advisorMeiboListViewForm.setAdvisorMeiboPage(page);
		advisorMeiboListViewForm.setAdvisorMeiboListDto(advisorMeiboDtoList);
		return advisorMeiboListViewForm;
	}

	/**
	 * 「名簿一覧：弁護士」の画面表示用オブジェクトを作成する
	 * 
	 * @return 画面表示用オブジェクト
	 */
	public MeiboListViewForm.BengoshiMeiboListViewForm createBengoshiMeiboListViewForm(MeiboListSearchForm searchForm) {

		// インスタンス化
		MeiboListViewForm.BengoshiMeiboListViewForm bengoshiMeiboListViewForm = new MeiboListViewForm.BengoshiMeiboListViewForm();

		// 検索条件をオブジェクト化
		BengoshiMeiboListSearchForm bengoshiMeiboListSearchForm = searchForm.getBengoshiMeiboListSearchForm();
		BengoshiMeiboListSearchCondition bengoshiMeiboListSearchCondition = bengoshiMeiboListSearchForm.toBengoshiMeiboListSearchCondition();
		BengoshiMeiboListSortCondition bengoshiMeiboListSortCondition = bengoshiMeiboListSearchForm.toBengoshiMeiboListSortCondition();

		Pageable pageable = bengoshiMeiboListSearchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 検索処理
		List<BengoshiMeiboListBean> bengoshiMeiboBeanList = meiboListDao.selectBengoshiMeiboByConditions(bengoshiMeiboListSearchCondition, bengoshiMeiboListSortCondition, options);

		// convert
		List<BengoshiMeiboListDto> bengoshiMeiboDtoList = this.convertBengoshiBean2Dto(bengoshiMeiboBeanList);

		// ページ情報作成
		List<Long> meiboNoList = bengoshiMeiboDtoList.stream().map(bean -> bean.getPersonId()).collect(Collectors.toList());
		Page<Long> page = new PageImpl<>(meiboNoList, pageable, options.getCount());

		bengoshiMeiboListViewForm.setBengoshiMeiboPage(page);
		bengoshiMeiboListViewForm.setBengoshiMeiboListDto(bengoshiMeiboDtoList);
		return bengoshiMeiboListViewForm;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 「すべて」のconvert処理
	 * 
	 * @param allMeiboListBeanList
	 * @return
	 */
	private List<AllMeiboListDto> convertAllBean2Dto(List<AllMeiboListBean> allMeiboListBeanList) {
		return allMeiboListBeanList.stream().map(bean -> {
			AllMeiboListDto allMeiboListDto = new AllMeiboListDto();

			allMeiboListDto.setPersonId(bean.getPersonId());
			PersonName name = new PersonName(bean.getNameSei(), bean.getNameMei(), bean.getNameSeiKana(), bean.getNameMeiKana());
			allMeiboListDto.setName(name.getName());
			// 名簿属性を生成
			PersonAttribute personAttribute = new PersonAttribute(bean.getCustomerFlg(), bean.getAdvisorFlg(), bean.getCustomerType());
			allMeiboListDto.setPersonAttribute(personAttribute);
			allMeiboListDto.setCustomerFlg(bean.getCustomerFlg());
			allMeiboListDto.setZipCode(bean.getZipCode());
			allMeiboListDto.setAddress1(bean.getAddress1());
			allMeiboListDto.setAddress2(bean.getAddress2());
			allMeiboListDto.setCustomerType(CustomerType.of(bean.getCustomerType()));
			allMeiboListDto.setTelNo(bean.getTelNo());
			allMeiboListDto.setFaxNo(bean.getFaxNo());
			allMeiboListDto.setMailAddress(bean.getMailAddress());
			allMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));

			if (SystemFlg.FLG_ON.equalsByCode(bean.getExistsAnkenCustomer())) {
				// 案件顧客として登録されている
				allMeiboListDto.setExistsAnkenCustomer(true);
			} else {
				// 案件顧客として登録されていない
				allMeiboListDto.setExistsAnkenCustomer(false);
			}

			if (SystemFlg.FLG_ON.equalsByCode(bean.getExistsKanyosha())) {
				// 関与者として登録されている
				allMeiboListDto.setExistsKanyosha(true);
			} else {
				// 関与者として登録されていない
				allMeiboListDto.setExistsKanyosha(false);
			}

			return allMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「顧客名簿」のconvert処理
	 * 
	 * @param customerAllMeiboListBeanList
	 * @return
	 */
	private List<CustomerAllMeiboListDto> convertCustomerAllBean2Dto(List<CustomerAllMeiboListBean> customerAllMeiboListBeanList) {

		return customerAllMeiboListBeanList.stream().map(bean -> {

			CustomerAllMeiboListDto customerAllMeiboListDto = new CustomerAllMeiboListDto();

			customerAllMeiboListDto.setPersonId(bean.getPersonId());
			customerAllMeiboListDto.setCustomerName(bean.getCustomerNameSei() + " " + StringUtils.null2blank(bean.getCustomerNameMei()));
			customerAllMeiboListDto.setZipCode(bean.getZipCode());
			customerAllMeiboListDto.setAddress1(bean.getAddress1());
			customerAllMeiboListDto.setAddress2(bean.getAddress2());
			customerAllMeiboListDto.setTelNo(bean.getTelNo());
			customerAllMeiboListDto.setMailAddress(bean.getMailAddress());
			customerAllMeiboListDto.setAnkenTotalCnt(bean.getAnkenTotalCnt());
			customerAllMeiboListDto.setAnkenProgressCnt(bean.getAnkenProgressCnt());
			customerAllMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			customerAllMeiboListDto.setRemarks(bean.getRemarks());

			return customerAllMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「顧客名簿 個人」のconvert処理
	 * 
	 * @param customerKojinMeiboListBeanList
	 * @return
	 */
	private List<CustomerKojinMeiboListDto> convertKojinBean2Dto(List<CustomerKojinMeiboListBean> customerKojinMeiboListBeanList) {

		return customerKojinMeiboListBeanList.stream().map(bean -> {
			CustomerKojinMeiboListDto customerKojinMeiboListDto = new CustomerKojinMeiboListDto();

			customerKojinMeiboListDto.setPersonId(bean.getPersonId());
			PersonName name = new PersonName(bean.getCustomerNameSei(), bean.getCustomerNameMei(), bean.getCustomerNameSeiKana(), bean.getCustomerNameMeiKana());
			customerKojinMeiboListDto.setCustomerName(name.getName());
			customerKojinMeiboListDto.setZipCode(bean.getZipCode());
			customerKojinMeiboListDto.setAddress1(bean.getAddress1());
			customerKojinMeiboListDto.setAddress2(bean.getAddress2());
			customerKojinMeiboListDto.setTelNo(bean.getTelNo());
			customerKojinMeiboListDto.setMailAddress(bean.getMailAddress());
			customerKojinMeiboListDto.setAnkenTotalCnt(bean.getAnkenTotalCnt());
			customerKojinMeiboListDto.setAnkenProgressCnt(bean.getAnkenProgressCnt());
			customerKojinMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			customerKojinMeiboListDto.setRemarks(bean.getRemarks());

			return customerKojinMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「顧客名簿 企業・団体」のconvert処理
	 * 
	 * @param customerHojinMeiboListBeanList
	 * @return
	 */
	private List<CustomerHojinMeiboListDto> convertHojinBean2Dto(List<CustomerHojinMeiboListBean> customerHojinMeiboListBeanList) {

		return customerHojinMeiboListBeanList.stream().map(bean -> {

			CustomerHojinMeiboListDto customerHojinMeiboListDto = new CustomerHojinMeiboListDto();

			customerHojinMeiboListDto.setPersonId(bean.getPersonId());
			customerHojinMeiboListDto.setCustomerName(bean.getCustomerNameSei());
			// 代表名は必須チェックがついていないので、（かな）だけ入力している場合は（かな）のほうを表示する
			if (StringUtils.isNotEmpty(bean.getDaihyoName())) {
				customerHojinMeiboListDto.setDaihyoName(bean.getDaihyoName());
			} else {
				customerHojinMeiboListDto.setDaihyoName(bean.getDaihyoNameKana());
			}
			customerHojinMeiboListDto.setDaihyoPositionName(bean.getDaihyoPositionName());
			customerHojinMeiboListDto.setTantoName(bean.getTantoName());
			customerHojinMeiboListDto.setZipCode(bean.getZipCode());
			customerHojinMeiboListDto.setAddress1(bean.getAddress1());
			customerHojinMeiboListDto.setAddress2(bean.getAddress2());
			customerHojinMeiboListDto.setTelNo(bean.getTelNo());
			customerHojinMeiboListDto.setFaxNo(bean.getFaxNo());
			customerHojinMeiboListDto.setMailAddress(bean.getMailAddress());
			customerHojinMeiboListDto.setAnkenTotalCnt(bean.getAnkenTotalCnt());
			customerHojinMeiboListDto.setAnkenProgressCnt(bean.getAnkenProgressCnt());
			customerHojinMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			customerHojinMeiboListDto.setRemarks(bean.getRemarks());

			return customerHojinMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「顧客名簿 顧問取引先」のconvert処理
	 * 
	 * @param advisorMeiboListBeanList
	 * @return
	 */
	private List<AdvisorMeiboListDto> convertAdvisorBean2Dto(List<AdvisorMeiboListBean> advisorMeiboListBeanList) {

		return advisorMeiboListBeanList.stream().map(bean -> {

			AdvisorMeiboListDto advisorMeiboListDto = new AdvisorMeiboListDto();

			advisorMeiboListDto.setPersonId(bean.getPersonId());
			advisorMeiboListDto.setCustomerType(bean.getCustomerType());
			advisorMeiboListDto.setCustomerName(bean.getCustomerNameSei() + " " + StringUtils.null2blank(bean.getCustomerNameMei()));
			advisorMeiboListDto.setZipCode(bean.getZipCode());
			advisorMeiboListDto.setAddress1(bean.getAddress1());
			advisorMeiboListDto.setAddress2(bean.getAddress2());
			advisorMeiboListDto.setTelNo(bean.getTelNo());
			advisorMeiboListDto.setMailAddress(bean.getMailAddress());
			advisorMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			advisorMeiboListDto.setRemarks(bean.getRemarks());

			return advisorMeiboListDto;
		}).collect(Collectors.toList());
	}

	/**
	 * 「弁護士名簿」のconvert処理
	 * 
	 * @param bengoshiMeiboListBeanList
	 * @return
	 */
	private List<BengoshiMeiboListDto> convertBengoshiBean2Dto(List<BengoshiMeiboListBean> bengoshiMeiboListBeanList) {

		return bengoshiMeiboListBeanList.stream().map(bean -> {

			BengoshiMeiboListDto bengoshiMeiboListDto = new BengoshiMeiboListDto();

			bengoshiMeiboListDto.setPersonId(bean.getPersonId());
			bengoshiMeiboListDto.setJimushoName(bean.getJimushoName());
			if (StringUtils.isNotEmpty(bean.getBushoName())) {
				bengoshiMeiboListDto.setBushoYakushokuName(bean.getBushoName());
			}
			bengoshiMeiboListDto.setBengoshiName(bean.getBengoshiNameSei() + " " + StringUtils.null2blank(bean.getBengoshiNameMei()));
			bengoshiMeiboListDto.setZipCode(bean.getZipCode());
			bengoshiMeiboListDto.setAddress1(bean.getAddress1());
			bengoshiMeiboListDto.setAddress2(bean.getAddress2());
			bengoshiMeiboListDto.setAddressRemarks(bean.getAddressRemarks());
			bengoshiMeiboListDto.setTelNo(bean.getTelNo());
			bengoshiMeiboListDto.setFaxNo(bean.getFaxNo());
			bengoshiMeiboListDto.setMailAddress(bean.getMailAddress());
			bengoshiMeiboListDto.setCustomerCreatedDate(DateUtils.parseToString(bean.getCustomerCreatedDate(), DateUtils.DATE_FORMAT_HYPHEN_DELIMITED));
			bengoshiMeiboListDto.setRemarks(bean.getRemarks());

			return bengoshiMeiboListDto;
		}).collect(Collectors.toList());
	}

}