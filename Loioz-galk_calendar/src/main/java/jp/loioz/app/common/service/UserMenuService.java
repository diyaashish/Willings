package jp.loioz.app.common.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.dao.TAnkenCustomerDao;
import jp.loioz.entity.TAnkenCustomerEntity;

/**
 * ユーザ側画面の共通メニューサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserMenuService extends DefaultService {

	/** 案件顧客Daoクラス */
	@Autowired
	private TAnkenCustomerDao tAnkenCustomerDao;

	/**
	 * 案件IDに紐づく、名簿ID(CustomerId)を取得する
	 * 
	 * @param ankenId
	 * @return
	 */
	public List<Long> getRelatedPersonIdList(Long ankenId) {

		List<TAnkenCustomerEntity> tAnkenCustomerEntities = tAnkenCustomerDao.selectByAnkenId(ankenId);
		return tAnkenCustomerEntities.stream().map(TAnkenCustomerEntity::getCustomerId).collect(Collectors.toList());
	}

}
