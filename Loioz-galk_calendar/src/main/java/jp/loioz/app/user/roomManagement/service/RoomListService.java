package jp.loioz.app.user.roomManagement.service;

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
import jp.loioz.app.user.roomManagement.form.RoomListForm;
import jp.loioz.app.user.roomManagement.form.RoomSearchForm;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dto.RoomListDto;
import jp.loioz.entity.MRoomEntity;

/**
 * 会議室一覧画面用のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoomListService extends DefaultService {

	/* 情報管理用のDaoクラス */
	@Autowired
	private MRoomDao mRoomDao;

	/* ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 一覧情報の取得
	 */
	public RoomListForm search(RoomListForm viewForm, RoomSearchForm searchForm) {
		// 一覧情報の取得
		Page<RoomListDto> page = this.getPageList(searchForm);

		// viewにセット
		viewForm.setPage(page);
		viewForm.setRoomList(page.getContent());

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
		List<MRoomEntity> mRoomEntityList = mRoomDao.selectEnabled();

		/* 最新のデータ件数と異なれば排他エラーを呼ぶ */
		if (targetIdArray.length != mRoomEntityList.size()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 変更があるレコードのみ順次更新する */
		try {
			List<MRoomEntity> entities = new ArrayList<MRoomEntity>();
			for (int i = 0; i < indexArray.length; i++) {
				MRoomEntity entity = mRoomDao.selectById(Long.valueOf(targetIdArray[Integer.parseInt(indexArray[i])]));

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

			updateEntitysCount = mRoomDao.update(entities);

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
		mRoomEntityList = mRoomDao.selectEnabled();

		// 表示順に合わせて並び替え
		List<Long> dispOrderList = mRoomEntityList.stream()
				.sorted(Comparator.comparing(MRoomEntity::getDispOrder))
				.map(s -> s.getDispOrder())
				.collect(Collectors.toList());

		return dispOrderList;
	}

	/**
	 * 会議室情報の取得
	 *
	 * @param searchForm
	 * @return
	 */
	private Page<RoomListDto> getPageList(RoomSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 会議室情報を取得
		List<MRoomEntity> mRoomEntity = mRoomDao.selectConditions(searchForm, options);
		// entityをDtoに変換
		List<RoomListDto> roomList = this.convertEntity2Dto(mRoomEntity);

		// 取得したデータをページ情報にセット
		Page<RoomListDto> page = new PageImpl<>(roomList, pageable, options.getCount());

		return page;
	}

	/**
	 * 会議室 EntityをDtoに変換
	 *
	 * @param mRoomEntity
	 * @return
	 */
	private List<RoomListDto> convertEntity2Dto(List<MRoomEntity> mRoomEntity) {

		List<RoomListDto> roomList = new ArrayList<>();

		for (MRoomEntity entity : mRoomEntity) {
			RoomListDto dto = new RoomListDto();
			dto.setRoomId(entity.getRoomId());
			dto.setRoomName(entity.getRoomName());
			dto.setDispOrder(entity.getDispOrder());
			dto.setVersionNo(entity.getVersionNo());
			roomList.add(dto);
		}
		return roomList;
	}
}