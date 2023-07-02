package jp.loioz.app.user.saibanshoManagement.service;

import java.util.ArrayList;
import java.util.List;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.saibanshoManagement.form.SaibanshoListForm;
import jp.loioz.app.user.saibanshoManagement.form.SaibanshoSearchForm;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MSaibanshoDao;
import jp.loioz.dto.SaibanshoListDto;
import jp.loioz.entity.MSaibanshoEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class SaibanshoListService extends DefaultService {

	/* 裁判所情報管理用のDaoクラス */
	@Autowired
	private MSaibanshoDao mSaibanshoDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 一覧情報の取得
	 */
	public SaibanshoListForm search(SaibanshoListForm viewForm, SaibanshoSearchForm searchForm) {
		// 一覧情報の取得
		Page<SaibanshoListDto> page = this.getPageList(searchForm);

		// viewにセット
		viewForm.setPage(page);
		viewForm.setSaibanshoList(page.getContent());

		return viewForm;
	}

	/**
	 * 裁判所情報の取得
	 *
	 * @param searchForm
	 * @return
	 */
	public Page<SaibanshoListDto> getPageList(SaibanshoSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 裁判所情報を取得
		List<MSaibanshoEntity> mSaibanshoEntity = mSaibanshoDao.selectConditions(searchForm, options);
		// entityをDtoに変換
		List<SaibanshoListDto> saibanshoList = this.convertEntity2Dto(mSaibanshoEntity);

		// 取得したデータをページ情報にセット
		Page<SaibanshoListDto> page = new PageImpl<>(saibanshoList, pageable, options.getCount());

		return page;
	}

	/**
	 * 裁判所
	 * EntityをDtoに変換
	 *
	 * @param mSaibanshoEntity
	 * @return
	 */
	public List<SaibanshoListDto> convertEntity2Dto(List<MSaibanshoEntity> mSaibanshoEntity) {

		List<SaibanshoListDto> saibanshoList = new ArrayList<>();

		for (MSaibanshoEntity entity : mSaibanshoEntity) {
			SaibanshoListDto dto = new SaibanshoListDto();
			dto.setSaibanshoId(entity.getSaibanshoId());
			dto.setTodofukenId(entity.getTodofukenId());
			dto.setSaibanshoZip(StringUtils.defaultString(entity.getSaibanshoZip()));
			dto.setSaibanshoAddress1(StringUtils.defaultString(entity.getSaibanshoAddress1()));
			dto.setSaibanshoAddress2(StringUtils.defaultString(entity.getSaibanshoAddress2()));
			dto.setSaibanshoName(entity.getSaibanshoName());
			dto.setVersionNo(entity.getVersionNo());
			saibanshoList.add(dto);
		}
		return saibanshoList;
	}
}