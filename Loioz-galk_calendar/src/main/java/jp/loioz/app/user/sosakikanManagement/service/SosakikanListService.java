package jp.loioz.app.user.sosakikanManagement.service;

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
import jp.loioz.app.user.sosakikanManagement.form.SosakikanListForm;
import jp.loioz.app.user.sosakikanManagement.form.SosakikanSearchForm;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MSosakikanDao;
import jp.loioz.dto.SosakikanListDto;
import jp.loioz.entity.MSosakikanEntity;

@Service
@Transactional(rollbackFor = Exception.class)
public class SosakikanListService extends DefaultService {

	/* 捜査機関情報管理用のDaoクラス */
	@Autowired
	private MSosakikanDao mSosakikanDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 一覧情報の取得
	 */
	public SosakikanListForm search(SosakikanListForm viewForm, SosakikanSearchForm searchForm) {
		// 一覧情報の取得
		Page<SosakikanListDto> page = this.getPageList(searchForm);

		// viewにセット
		viewForm.setPage(page);
		viewForm.setSosakikanList(page.getContent());

		return viewForm;
	}

	/**
	 * 捜査機関情報の取得
	 *
	 * @param searchForm
	 * @return
	 */
	public Page<SosakikanListDto> getPageList(SosakikanSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 捜査機関情報を取得
		List<MSosakikanEntity> mSosakikanEntity = mSosakikanDao.selectConditions(searchForm, options);
		// entityをDtoに変換
		List<SosakikanListDto> sosakikanList = this.convertEntity2Dto(mSosakikanEntity);

		// 取得したデータをページ情報にセット
		Page<SosakikanListDto> page = new PageImpl<>(sosakikanList, pageable, options.getCount());

		return page;
	}

	/**
	 * 捜査機関
	 * EntityをDtoに変換
	 *
	 * @param mSosakikanEntity
	 * @return
	 */
	public List<SosakikanListDto> convertEntity2Dto(List<MSosakikanEntity> mSosakikanEntity) {

		List<SosakikanListDto> sosakikanList = new ArrayList<>();

		for (MSosakikanEntity entity : mSosakikanEntity) {
			SosakikanListDto dto = new SosakikanListDto();
			dto.setSosakikanId(entity.getSosakikanId());
			dto.setTodofukenId(entity.getTodofukenId());
			dto.setSosakikanType(entity.getSosakikanType());
			dto.setSosakikanZip(StringUtils.defaultString(entity.getSosakikanZip()));
			dto.setSosakikanAddress1(StringUtils.defaultString(entity.getSosakikanAddress1()));
			dto.setSosakikanAddress2(StringUtils.defaultString(entity.getSosakikanAddress2()));
			dto.setSosakikanName(entity.getSosakikanName());
			dto.setSosakikanTelNo(entity.getSosakikanTelNo());
			dto.setSosakikanFaxNo(entity.getSosakikanFaxNo());
			dto.setVersionNo(entity.getVersionNo());
			sosakikanList.add(dto);
		}
		return sosakikanList;
	}
}
