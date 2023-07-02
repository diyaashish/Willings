package jp.loioz.app.user.bushoManagement.service;

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
import jp.loioz.app.user.bushoManagement.form.BushoListForm;
import jp.loioz.app.user.bushoManagement.form.BushoSearchForm;
import jp.loioz.bean.BushoListUserBean;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MBushoDao;
import jp.loioz.dto.BushoListDto;
import jp.loioz.entity.MBushoEntity;

/**
 * 部門画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BushoListService extends DefaultService {

	/* 部署データ用のDaoクラス */
	@Autowired
	private MBushoDao mBushoDao;

	/* ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 一覧情報の取得
	 */
	public BushoListForm search(BushoListForm viewForm, BushoSearchForm searchForm) {

		// 一覧情報の取得
		Page<BushoListDto> page = this.getPageList(searchForm);

		// viewにセット
		viewForm.setPage(page);
		viewForm.setBushoList(page.getContent());

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
		List<MBushoEntity> entityList = mBushoDao.selectAll();

		/* 最新のデータ件数と異なれば排他エラーを呼ぶ */
		if (targetIdArray.length != entityList.size()) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);

			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 変更があるレコードのみ順次更新する */
		try {
			List<MBushoEntity> entities = new ArrayList<MBushoEntity>();
			for (int i = 0; i < indexArray.length; i++) {
				MBushoEntity entity = mBushoDao.selectById(Long.valueOf(targetIdArray[Integer.parseInt(indexArray[i])]));

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

			updateEntitysCount = mBushoDao.update(entities);

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
		entityList = mBushoDao.selectAll();

		// 表示順に合わせて並び替え
		List<Long> dispOrderList = entityList.stream()
				.sorted(Comparator.comparing(MBushoEntity::getDispOrder))
				.map(s -> s.getDispOrder())
				.collect(Collectors.toList());

		return dispOrderList;
	}

	/**
	 * 部署情報の取得
	 *
	 * @param searchForm
	 * @return
	 */
	private Page<BushoListDto> getPageList(BushoSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 部署情報を取得
		List<BushoListUserBean> mBushoEntity = mBushoDao.selectConditions(searchForm, options);

		// entityをDtoに変換
		List<BushoListDto> bushoList = this.convertBean2Dto(mBushoEntity);

		// 取得したデータをページ情報にセット
		Page<BushoListDto> page = new PageImpl<>(bushoList, pageable, options.getCount());

		return page;
	}

	/**
	 * 部署 BeanをDtoに変換
	 *
	 * @param bushoListUserBeanList
	 * @return
	 */
	private List<BushoListDto> convertBean2Dto(List<BushoListUserBean> bushoListUserBeanList) {

		List<BushoListDto> bushoList = new ArrayList<>();
		bushoListUserBeanList.forEach(bean -> {

			BushoListDto dto = new BushoListDto();
			dto.setBushoId(bean.getBushoId());
			dto.setBushoName(bean.getBushoName());
			dto.setShozokuAccountName(bean.getShozokuAccountName());
			dto.setDispOrder(bean.getDispOrder());
			dto.setVersionNo(bean.getVersionNo());
			bushoList.add(dto);

		});
		return bushoList;
	}
}