package jp.loioz.app.user.selectListManagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.seasar.doma.boot.Pageables;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.selectListManagement.form.SelectListForm;
import jp.loioz.app.user.selectListManagement.form.SelectSearchForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MSelectListDao;
import jp.loioz.dto.SelectListDto;
import jp.loioz.entity.MSelectListEntity;

/**
 * 選択肢設定画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SelectListService extends DefaultService {

	/** 選択肢マスタ画面用のDaoクラス */
	@Autowired
	private MSelectListDao mSelectListDao;

	/** ロガー */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================
	/**
	 * 一覧情報の取得
	 */
	public SelectListForm search(SelectListForm viewForm, SelectSearchForm searchForm) {

		// 一覧情報の取得
		Page<SelectListDto> page = this.getPageList(searchForm);

		// viewにセット
		viewForm.setPage(page);
		viewForm.setSelectList(page.getContent());

		return viewForm;
	}

	/**
	 * 表示順変更処理
	 *
	 * @param targetId
	 * @param index
	 * @return
	 * @throws AppException
	 */
	public List<Long> updateDispOrder(String targetId, String index) throws AppException {
		String[] targetIdArray = targetId.split(",");
		String[] indexArray = index.split(",");

		/* 更新件数 */
		int[] updateEntitysCount = null;

		/* 最新のデータを取得する */
		List<MSelectListEntity> MSelectListEntities = mSelectListDao.selectAll();

		/* 最新のデータ件数と異なれば排他エラーを呼ぶ */
		if (targetIdArray.length != MSelectListEntities.size()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 変更があるレコードのみ順次更新する */
		try {
			List<MSelectListEntity> entities = new ArrayList<MSelectListEntity>();
			for (int i = 0; i < indexArray.length; i++) {
				MSelectListEntity entity = mSelectListDao.selectSelectListBySeq(Long.valueOf(targetIdArray[Integer.parseInt(indexArray[i])]));

				/* 既にデータがなければ排他エラーを呼ぶ */
				if (entity == null) {
					String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
					logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

					throw new AppException(MessageEnum.MSG_E00025, null);

				} else {
					entity.setDispOrder(Long.parseLong(indexArray[i]) + 1);
					entities.add(entity);
				}
			}

			updateEntitysCount = mSelectListDao.update(entities);

		} catch (OptimisticLockingFailureException ex) {
			// 楽観ロックエラー

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ex.getMessage());

			throw new AppException(MessageEnum.MSG_E00013, ex);
		}

		if (Arrays.stream(updateEntitysCount).sum() != updateEntitysCount.length) {
			// 更新処理に失敗した場合

			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + NOT_UPDATED_EXPECTED_COUNT_DATA);

			throw new AppException(MessageEnum.MSG_E00013, null);
		}

		/* 最新のデータを取得する */
		MSelectListEntities = mSelectListDao.selectAll();
		// 表示順に合わせて並び替え
		List<Long> dispOrderList = MSelectListEntities.stream()
				.sorted(Comparator.comparing(MSelectListEntity::getDispOrder))
				.map(s -> s.getDispOrder())
				.collect(Collectors.toList());

		return dispOrderList;
	}

	/**
	 * 選択肢情報の取得
	 *
	 * @param searchForm
	 * @return
	 */
	public Page<SelectListDto> getPageList(SelectSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 選択肢情報を取得
		List<MSelectListEntity> mSelectListEntity = mSelectListDao.selectConditions(searchForm, options);
		// entityをDtoに変換
		List<SelectListDto> selectList = this.convertEntity2Dto(mSelectListEntity);

		// 取得したデータをページ情報にセット
		Page<SelectListDto> page = new PageImpl<>(selectList, pageable, options.getCount());

		return page;
	}

	/**
	 * 選択肢
	 * EntityをDtoに変換
	 *
	 * @param mRoomEntity
	 * @return
	 */
	public List<SelectListDto> convertEntity2Dto(List<MSelectListEntity> mSelectListEntity) {

		List<SelectListDto> selectList = new ArrayList<>();

		for (MSelectListEntity entity : mSelectListEntity) {
			SelectListDto dto = new SelectListDto();
			dto.setSelectSeq(entity.getSelectSeq());
			dto.setSelectType(entity.getSelectType());
			dto.setDispOrder(entity.getDispOrder());
			dto.setSelectVal(entity.getSelectVal());
			dto.setDeleted((entity.getDeletedAt() != null) && (entity.getDeletedBy() != null));
			dto.setVersionNo(entity.getVersionNo());
			selectList.add(dto);
		}
		return selectList;
	}
}