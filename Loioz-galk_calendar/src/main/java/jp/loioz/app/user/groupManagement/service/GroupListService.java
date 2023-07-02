package jp.loioz.app.user.groupManagement.service;

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
import jp.loioz.app.user.groupManagement.form.GroupListForm;
import jp.loioz.app.user.groupManagement.form.GroupSearchForm;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.MGroupDao;
import jp.loioz.dto.GroupListDto;
import jp.loioz.entity.MGroupEntity;

/**
 * グループ一覧画面のserviceクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupListService extends DefaultService {

	/* グループ管理用のDaoクラス */
	@Autowired
	private MGroupDao mGroupDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 一覧情報の取得
	 */
	public GroupListForm search(GroupListForm viewForm, GroupSearchForm searchForm) {

		// 一覧情報の取得
		Page<GroupListDto> page = this.getPageList(searchForm);

		// viewにセット
		viewForm.setCount(page.getTotalElements());
		viewForm.setPage(page);
		viewForm.setGroupList(page.getContent());

		return viewForm;
	}

	/**
	 * グループ情報の取得
	 *
	 * @param searchForm
	 * @return
	 */
	public Page<GroupListDto> getPageList(GroupSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// グループ情報を取得
		List<MGroupEntity> mBushoEntity = mGroupDao.selectConditions(searchForm, options);
		// entityをDtoに変換
		List<GroupListDto> groupList = this.convertEntity2Dto(mBushoEntity);

		// 取得したデータをページ情報にセット
		Page<GroupListDto> page = new PageImpl<>(groupList, pageable, options.getCount());

		return page;
	}

	/**
	 * グループ
	 * EntityをDtoに変換
	 *
	 * @param entityList
	 * @return
	 */
	public List<GroupListDto> convertEntity2Dto(List<MGroupEntity> entityList) {

		List<GroupListDto> groupList = new ArrayList<>();

		for (MGroupEntity entity : entityList) {
			GroupListDto dto = new GroupListDto();
			dto.setGroupId(entity.getGroupId());
			dto.setGroupName(entity.getGroupName());
			dto.setDispOrder(entity.getDispOrder());
			dto.setCreatedAt(DateUtils.parseToString(entity.getCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED));
			dto.setUpdatedAt(DateUtils.parseToString(entity.getUpdatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED));
			dto.setVersionNo(entity.getVersionNo());
			groupList.add(dto);
		}
		return groupList;
	}
}