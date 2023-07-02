package jp.loioz.app.user.dengon.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.bean.DengonCustomerBean;
import jp.loioz.common.utility.LoiozCollectionUtils;
import jp.loioz.dao.TDengonCustomerDao;
import jp.loioz.domain.value.CustomerId;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.DengonCustomerDto;
import jp.loioz.entity.TDengonCustomerEntity;

/**
 * 伝言共通サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DengonCommonService extends DefaultService {

	/** 伝言-顧客情報Daoクラス */
	@Autowired
	private TDengonCustomerDao tDengonCustomerDao;

	/**
	 * 伝言-顧客情報の登録処理
	 * 
	 * @param dengonSeq
	 * @param customerIdList
	 */
	public void registDengonCustomer(Long dengonSeq, List<Long> customerIdList) {

		// 顧客IDが空なら何もせずに返却
		if (ListUtils.isEmpty(customerIdList)) {
			return;
		}

		// 伝言-顧客情報の作成
		List<TDengonCustomerEntity> tDengonCustomerEntities = customerIdList.stream().map(id -> {
			TDengonCustomerEntity tDengonCustomerEntity = new TDengonCustomerEntity();
			tDengonCustomerEntity.setDengonSeq(dengonSeq);
			tDengonCustomerEntity.setCustomerId(id);
			return tDengonCustomerEntity;
		}).collect(Collectors.toList());

		// 伝言-顧客情報の登録処理
		tDengonCustomerDao.insert(tDengonCustomerEntities);

	}

	/**
	 * 伝言-顧客情報の更新処理(削除登録)
	 * 
	 * @param dengonSeq
	 * @param formCustomerIdList 入力された顧客ID
	 */
	public void updateDengonCustomer(Long dengonSeq, List<Long> formCustomerIdList) {

		List<TDengonCustomerEntity> tDengonCustomerEntities = tDengonCustomerDao.selectByDengonSeq(dengonSeq);
		Map<Long, TDengonCustomerEntity> dbDengonCustomerMap = tDengonCustomerEntities.stream()
				.collect(Collectors.toMap(
						TDengonCustomerEntity::getCustomerId,
						Function.identity()));
		Set<Long> dbCustomerIdSet = dbDengonCustomerMap.keySet();

		LoiozCollectionUtils.subtract(formCustomerIdList, dbCustomerIdSet).forEach(customerId -> {
			// 入力データあり、既存データなし
			// 入力データを登録
			TDengonCustomerEntity tDengonCustomerEntity = new TDengonCustomerEntity();
			tDengonCustomerEntity.setDengonSeq(dengonSeq);
			tDengonCustomerEntity.setCustomerId(customerId);
			tDengonCustomerDao.insert(tDengonCustomerEntity);
		});

		LoiozCollectionUtils.subtract(dbCustomerIdSet, formCustomerIdList).forEach(customerId -> {
			// 入力データなし、既存データあり
			// 既存データを削除
			TDengonCustomerEntity entity = dbDengonCustomerMap.get(customerId);
			tDengonCustomerDao.delete(entity);
		});
	}

	/**
	 * 伝言SEQに紐づく、顧客情報を取得する
	 * 
	 * @param dengonSeq
	 * @return
	 */
	public List<DengonCustomerDto> getSelectedCustomer(Long dengonSeq) {
		// 伝言-顧客情報の取得をする
		List<DengonCustomerBean> dengonCustomerBean = tDengonCustomerDao.selectBeanByDengonSeq(dengonSeq);

		if (ListUtils.isEmpty(dengonCustomerBean)) {
			return Collections.emptyList();
		}

		// 顧客情報を加工して返却
		return dengonCustomerBean.stream()
				.map(this::convertBean2Dto)
				.collect(Collectors.toList());
	}

	/**
	 * 伝言SEQに紐づく、顧客情報を取得する
	 * 
	 * @param dengonSeq
	 * @return
	 */
	public Map<Long, List<DengonCustomerDto>> getSelectedCustomer(List<Long> dengonSeqList) {
		// 伝言-顧客情報の取得をする
		List<DengonCustomerBean> dengonCustomerBeans = tDengonCustomerDao.selectBeanByDengonSeq(dengonSeqList);

		if (ListUtils.isEmpty(dengonCustomerBeans)) {
			return Collections.emptyMap();
		}

		// 伝言BeanMap
		Map<Long, List<DengonCustomerDto>> dengonSeqToCustomerMap = dengonCustomerBeans.stream()
				.map(this::convertBean2Dto)
				.collect(Collectors.groupingBy(DengonCustomerDto::getDengonSeq));

		return dengonSeqToCustomerMap;
	}

	/**
	 * DengonCustomerBean -> Customer
	 * 
	 * @param bean
	 * @return
	 */
	private DengonCustomerDto convertBean2Dto(DengonCustomerBean bean) {
		DengonCustomerDto dto = new DengonCustomerDto();
		CustomerId customerId = CustomerId.of(bean.getCustomerId());
		dto.setDengonSeq(bean.getDengonSeq());
		dto.setCustomerId(customerId.asLong());
		dto.setCustomerIdDisp(customerId.toString());
		dto.setCustomerName(new PersonName(
				bean.getCustomerNameSei(),
				bean.getCustomerNameMei(),
				bean.getCustomerNameSeiKana(),
				bean.getCustomerNameMeiKana()).getName());
		return dto;
	}

}
