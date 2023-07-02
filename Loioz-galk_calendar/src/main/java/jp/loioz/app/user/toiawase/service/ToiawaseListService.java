package jp.loioz.app.user.toiawase.service;

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
import jp.loioz.app.user.toiawase.form.ToiawaseListSearchForm;
import jp.loioz.app.user.toiawase.form.ToiawaseListViewForm;
import jp.loioz.common.constant.CommonConstant.ToiawaseStatus;
import jp.loioz.common.constant.CommonConstant.ToiawaseType;
import jp.loioz.common.utility.DateUtils;
import jp.loioz.dao.TToiawaseDao;
import jp.loioz.dto.ToiawaseListDto;
import jp.loioz.entity.TToiawaseEntity;

/**
 * 問い合わせ一覧サービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ToiawaseListService extends DefaultService {

	/** 問い合わせDao */
	@Autowired
	private TToiawaseDao tToiawaseDao;

	/**
	 * 問い合わせ一覧画面用オブジェクトの作成
	 * 
	 * @return 画面用オブジェクト
	 */
	public ToiawaseListViewForm createViewForm() {
		ToiawaseListViewForm viewForm = new ToiawaseListViewForm();
		return viewForm;
	}

	/**
	 * 問い合わせ一覧画面のデータ設定
	 *
	 * @param viewForm
	 * @param searchForm
	 */
	public void search(ToiawaseListViewForm viewForm, ToiawaseListSearchForm searchForm) {

		// データの取得処理等を諸々
		Page<Long> pageList = this.getPageList(searchForm);

		// 未読の問い合わせSEQを取得
		List<Long> nonReadList = tToiawaseDao.selectByNonRead();

		// 表示データの取得処理
		List<TToiawaseEntity> tToiawaseEntities = tToiawaseDao.selectListBySeq(pageList.getContent());
		List<ToiawaseListDto> toiawaseListDtoList = tToiawaseEntities.stream().map(entity -> this.convert2Entity(entity, nonReadList)).collect(Collectors.toList());

		// viewFormに設定
		viewForm.setPage(pageList);
		viewForm.setCount(pageList.getTotalElements());
		viewForm.setToiawaseList(toiawaseListDtoList);
	}

	/**
	 * 検索条件オブジェクトからページャ情報を取得・作成する
	 *
	 * @param searchForm
	 * @return
	 */
	private Page<Long> getPageList(ToiawaseListSearchForm searchForm) {

		// ページャー
		Pageable pageable = searchForm.toPageable();
		SelectOptions options = Pageables.toSelectOptions(pageable).count();

		// 表示する問い合わせのSEQを取得
		List<Long> toiawaseSeqList = tToiawaseDao.selectByConditions(searchForm.toToiawaseListSearchCondition(), options);

		// ページャ情報を作成
		Page<Long> page = new PageImpl<>(toiawaseSeqList, pageable, options.getCount());

		return page;
	}

	/**
	 * Entity -> Dto
	 *
	 * @param tToiawaseEntity
	 * @param hasNoReadToiawaseSeqList
	 * @return
	 */
	private ToiawaseListDto convert2Entity(TToiawaseEntity tToiawaseEntity, List<Long> hasNoReadToiawaseSeqList) {

		ToiawaseListDto toiawaseListDto = new ToiawaseListDto();
		toiawaseListDto.setToiawaseSeq(tToiawaseEntity.getToiawaseSeq());
		toiawaseListDto.setSubject(tToiawaseEntity.getSubject());
		toiawaseListDto.setToiawaseType(ToiawaseType.of(tToiawaseEntity.getToiawaseType()));
		toiawaseListDto.setCreatedAt(DateUtils.parseToString(tToiawaseEntity.getShokaiCreatedAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		toiawaseListDto.setUpdatedAt(DateUtils.parseToString(tToiawaseEntity.getLastUpdateAt(), DateUtils.DATE_TIME_FORMAT_SLASH_DELIMITED_YMDHM));
		toiawaseListDto.setToiawaseStatus(ToiawaseStatus.of(tToiawaseEntity.getToiawaseStatus()));
		toiawaseListDto.setHasNoReadDetail(hasNoReadToiawaseSeqList.contains(tToiawaseEntity.getToiawaseSeq()));

		return toiawaseListDto;
	}

}
