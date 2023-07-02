package jp.loioz.app.user.advisorContractPerson.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.WrapHeaderForm;
import jp.loioz.app.user.advisorContractPerson.form.AdvisorContractListSearchForm;
import jp.loioz.app.user.advisorContractPerson.form.AdvisorContractListViewForm;
import jp.loioz.app.user.advisorContractPerson.form.AdvisorContractListViewForm.AdvisorContractListRowDto.AdvisorContractTantoInfoDto;
import jp.loioz.bean.AdvisorContractTantoInfoBean;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.ContractStatus;
import jp.loioz.common.constant.CommonConstant.ContractType;
import jp.loioz.common.constant.CommonConstant.CustomerType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.constant.CommonConstant.TransitionType;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.dao.TAdvisorContractDao;
import jp.loioz.dao.TAdvisorContractTantoDao;
import jp.loioz.dao.TPersonDao;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.TAdvisorContractEntity;
import jp.loioz.entity.TPersonEntity;

/**
 * 顧問契約一覧画面（名簿）のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AdvisorContractListService extends DefaultService {

	/** 名簿Daoクラス */
	@Autowired
	private TPersonDao tPersonDao;
	
	/** 顧問契約用のDaoクラス */
	@Autowired
	private TAdvisorContractDao tAdvisorContractDao;
	
	/** 顧問契約担当用のDaoクラス */
	@Autowired
	private TAdvisorContractTantoDao tAdvisorContractTantoDao;
	
	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 顧問契約画面にアクセス状態かどうかを判定する
	 * 
	 * @param personId
	 * @return true: 可能、false: 不可能
	 */
	public boolean canAccess(Long personId) {
		
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		if (personEntity == null) {
			// 名簿情報が存在しない場合は不可
			throw new DataNotFoundException("名簿情報が存在しません。[personId=" + personId + "]");
		}
		
		CustomerType customerType = CustomerType.of(personEntity.getCustomerType());
		if (CustomerType.LAWYER == customerType) {
			// 名簿情報の種別が「弁護士」の場合は不可（弁護士には顧問契約のメニューも表示しないため）
			return false;
		}
		
		return true;
	}
	
	/**
	 * 顧問契約一覧画面の表示フォームを作成する
	 * 
	 * @param searchForm
	 * @return
	 */
	public AdvisorContractListViewForm createViewForm(AdvisorContractListSearchForm searchForm) {
		
		Long personId = searchForm.getPersonId();
		
		// 名簿ID
		AdvisorContractListViewForm viewForm = new AdvisorContractListViewForm();
		viewForm.setPersonId(personId);
		
		TPersonEntity personEntity = tPersonDao.selectPersonByPersonId(personId);
		
		// 共通-顧客情報部分
		WrapHeaderForm wrapHeader = this.createWrapHeaderForm(personId, personEntity);
		viewForm.setWrapHeader(wrapHeader);
		
		// 顧問契約一覧情報
		// ページ
		Page<TAdvisorContractEntity> page = this.getAdvisorContractListPage(searchForm);

		if (!searchForm.getPage().equals(0)) {
			// 表示しようとしているページが1ページ目以外の場合
			
			List<TAdvisorContractEntity> contractEntityList = page.getContent();
			if (contractEntityList == null || contractEntityList.isEmpty()) {
				// 指定ページの表示データが1件もない場合
				
				// 1ページ目を表示する
				searchForm.setPage(0);
				page = this.getAdvisorContractListPage(searchForm);
			}
		}
		viewForm.setPage(page);
		
		// 一覧データ
		List<AdvisorContractListViewForm.AdvisorContractListRowDto> advisorContractRowDtoList = this.convertEntityToContractListRowDto(page.getContent());
		viewForm.setAdvisorContractRowDtoList(advisorContractRowDtoList);
		
		return viewForm;
	}
	
	// =========================================================================
	// private メソッド
	// =========================================================================
	/**
	 * 共通の名簿情報部分のフォームを作成
	 * 
	 * @param personId
	 * @param personEntity
	 * @return
	 */
	private WrapHeaderForm createWrapHeaderForm(Long personId, TPersonEntity personEntity) {
		
		WrapHeaderForm wrapHeader = new WrapHeaderForm();
		wrapHeader.setTransitionType(TransitionType.CUSTOMER);
		wrapHeader.setCustomerType(CustomerType.of(personEntity.getCustomerType()));
		wrapHeader.setCustomerId(CustomerId.of(personId));
		wrapHeader.setCustomerName(PersonName.fromEntity(personEntity).getName());
		
		return wrapHeader;
	}
	
	/**
	 * 顧問契約一覧の1ページデータを取得
	 * 
	 * @param searchForm
	 * @return
	 */
	private Page<TAdvisorContractEntity> getAdvisorContractListPage(AdvisorContractListSearchForm searchForm) {
		
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();
		
		Long personId = searchForm.getPersonId();
		List<TAdvisorContractEntity> tAdvisorContractEntityList = tAdvisorContractDao.selectContractListPageByPersonId(personId, options);
		
		Page<TAdvisorContractEntity> page = new PageImpl<>(tAdvisorContractEntityList, pageable, options.getCount());
		
		return page;
	}
	
	/**
	 * 顧問契約エンティティを一覧表示用のDtoに変換
	 * 
	 * @param contractEntityList
	 * @return
	 */
	private List<AdvisorContractListViewForm.AdvisorContractListRowDto> convertEntityToContractListRowDto(List<TAdvisorContractEntity> contractEntityList) {
		
		if (contractEntityList == null || contractEntityList.isEmpty()) {
			return Collections.emptyList();
		}
		
		// 各顧問契約ごとの、担当情報Beanを取得
		List<Long> advisorContractSeqList = contractEntityList.stream().map(bean -> bean.getAdvisorContractSeq()).collect(Collectors.toList());
		List<AdvisorContractTantoInfoBean> contractTantoInfoBeanList = tAdvisorContractTantoDao.selectContractTantoInfoByParams(advisorContractSeqList);
		// 各担当情報をまとめたDtoリストに変換
		List<AdvisorContractTantoInfoDto> tantoInfoDtoList = new ArrayList<>();
		contractTantoInfoBeanList.stream()
				.forEach(bean -> { 
					AdvisorContractListViewForm.AdvisorContractListRowDto.AdvisorContractTantoInfoDto tantoInfoDto = 
							new AdvisorContractListViewForm.AdvisorContractListRowDto.AdvisorContractTantoInfoDto();
					tantoInfoDto.setAdvisorContractSeq(bean.getAdvisorContractSeq());
					tantoInfoDto.setTantoType(bean.getTantoType());
					tantoInfoDto.setTantoTypeBranchNo(bean.getTantoTypeBranchNo());
					tantoInfoDto.setMainTantoFlg(SystemFlg.codeToBoolean(bean.getMainTantoFlg()));
					tantoInfoDto.setTantoName(bean.getTantoName());
					tantoInfoDtoList.add(tantoInfoDto);
				});

		List<AdvisorContractListViewForm.AdvisorContractListRowDto> contractRowDtoList = new ArrayList<>();
		
		// EntityをDtoに変換
		for (TAdvisorContractEntity entity : contractEntityList) {
			AdvisorContractListViewForm.AdvisorContractListRowDto dto = new AdvisorContractListViewForm.AdvisorContractListRowDto();
			
			Long advisorContractSeq = entity.getAdvisorContractSeq();
			
			// 顧問契約SEQ
			dto.setAdvisorContractSeq(advisorContractSeq);
			
			// 契約開始日、終了日
			dto.setContractStartDate(entity.getContractStartDate());
			dto.setContractEndDate(entity.getContractEndDate());
			
			// 顧問料金（月額）
			BigDecimal monthChargeDecimal = entity.getContractMonthCharge();
			if (monthChargeDecimal != null) {
				dto.setContractMonthCharge(monthChargeDecimal.longValue());
			}
			
			// 稼働時間（時間/月）
			dto.setContractMonthTime(entity.getContractMonthTime());
			
			// 契約ステータス
			ContractStatus status = ContractStatus.of(entity.getContractStatus());
			dto.setContractStatus(status);
			
			// 契約内容
			dto.setContractContent(entity.getContractContent());
			
			// 契約区分
			ContractType type = ContractType.of(entity.getContractType());
			dto.setContractType(type);
			
			// 契約メモ
			dto.setContractMemo(entity.getContractMemo());
			
			// 売上計上先情報一覧
			List<AdvisorContractTantoInfoDto> salesOwnerList = tantoInfoDtoList.stream()
					.filter(tantoInfoBean -> tantoInfoBean.getAdvisorContractSeq().equals(advisorContractSeq) 
								&& CommonConstant.TantoType.SALES_OWNER.getCd().equals(tantoInfoBean.getTantoType()))
					.collect(Collectors.toList());
			dto.setSalesOwnerList(salesOwnerList);
			
			//担当弁護士情報一覧
			List<AdvisorContractTantoInfoDto> tantoLawyerList = tantoInfoDtoList.stream()
					.filter(tantoInfoBean -> tantoInfoBean.getAdvisorContractSeq().equals(advisorContractSeq) 
							&& CommonConstant.TantoType.LAWYER.getCd().equals(tantoInfoBean.getTantoType()))
					.collect(Collectors.toList());
			dto.setTantoLawyerList(tantoLawyerList);
			
			//担当事務情報一覧
			List<AdvisorContractTantoInfoDto> tantoJimuList = tantoInfoDtoList.stream()
					.filter(tantoInfoBean -> tantoInfoBean.getAdvisorContractSeq().equals(advisorContractSeq) 
							&& CommonConstant.TantoType.JIMU.getCd().equals(tantoInfoBean.getTantoType()))
					.collect(Collectors.toList());
			dto.setTantoJimuList(tantoJimuList);
			
			contractRowDtoList.add(dto);
		}
		
		return contractRowDtoList;
	}
	
}
