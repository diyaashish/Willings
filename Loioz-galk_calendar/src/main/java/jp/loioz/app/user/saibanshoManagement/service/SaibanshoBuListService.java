package jp.loioz.app.user.saibanshoManagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.saibanshoManagement.form.SaibanshoBuListForm;
import jp.loioz.dao.MSaibanshoBuDao;
import jp.loioz.dao.MSaibanshoDao;
import jp.loioz.dto.SaibanshoBuListDto;
import jp.loioz.entity.MSaibanshoEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanshoBuListService extends DefaultService {

	/** 裁判所Daoクラス */
	@Autowired
	private MSaibanshoDao mSaibanshoDao;

	/** 裁判所部Daoクラス */
	@Autowired
	private MSaibanshoBuDao mSaibanshoBuDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 裁判所係属部の取得処理
	 * 
	 * @param viewForm
	 * @param saibanId
	 * @return
	 */
	public SaibanshoBuListForm search(SaibanshoBuListForm viewForm, Long saibanId) {
		// データの取得
		MSaibanshoEntity mSaibanshoEntity = mSaibanshoDao.selectById(saibanId);
		List<SaibanshoBuListDto> saibanshoBuList = mSaibanshoBuDao.selectDtoBySaibanshoId(saibanId);

		viewForm.setSaibanshoId(saibanId);
		viewForm.setTodofukenCd(mSaibanshoEntity.getTodofukenId());
		viewForm.setSaibanshoBuList(saibanshoBuList);
		return viewForm;
	}

}