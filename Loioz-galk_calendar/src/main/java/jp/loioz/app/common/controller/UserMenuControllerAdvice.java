package jp.loioz.app.common.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.loioz.app.common.form.FunctionalLazyLoadForm;
import jp.loioz.app.common.service.CommonAnkenService;
import jp.loioz.app.common.service.UserMenuService;
import jp.loioz.common.constant.SessionAttrKeyEnum;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.TSaibanDao;
import jp.loioz.dto.AnkenDto;
import jp.loioz.entity.TSaibanEntity;

/**
 * ユーザ側画面の共通メニュー設定
 */
@ControllerAdvice("jp.loioz.app.user")
public class UserMenuControllerAdvice {

	@Autowired
	private CommonAnkenService commonAnkenService;

	@Autowired
	private TSaibanDao tSaibanDao;

	/** ユーザ側画面の共通メニューサービスクラス */
	@Autowired
	private UserMenuService userMenuService;

	/**
	 * 顧客案件メニュー：開閉プロパティ（クリック）
	 * 
	 * @return
	 */
	@ModelAttribute("customerAnkenMenuOpenClickForSessionValue")
	public String customerAnkenMenuOpenClickForSessionValue() {
		String customerAnkenMenuOpenClick = SessionUtils.getSessionVaule(SessionAttrKeyEnum.CUSTOMER_ANKEN_MENU_OPEN_CLICK);
		return StringUtils.defaultString(customerAnkenMenuOpenClick);
	}

	/**
	 * 顧客案件メニュー：開閉プロパティ（リサイズ）
	 * 
	 * @return
	 */
	@ModelAttribute("customerAnkenMenuOpenResizeForSessionValue")
	public String customerAnkenMenuOpenResizeForSessionValue() {
		String customerAnkenMenuOpenResize = SessionUtils.getSessionVaule(SessionAttrKeyEnum.CUSTOMER_ANKEN_MENU_OPEN_RESIZE);
		return StringUtils.defaultString(customerAnkenMenuOpenResize);
	}

	/**
	 * 顧客IDに紐づく案件のリスト
	 *
	 * @return key:顧客ID => 案件リスト
	 */
	@ModelAttribute("userMenuAnkenList")
	public FunctionalLazyLoadForm<Long, List<AnkenDto>> ankenList() {
		if (SessionUtils.isAlreadyLoggedUser()) {
			return new FunctionalLazyLoadForm<>((customerId) -> {
				List<AnkenDto> ankenList = commonAnkenService.generateAnkenDtoListByCustomerId(customerId);
				return ankenList;
			});
		} else {
			return null;
		}
	}

	/**
	 * 案件IDに紐づく裁判情報が存在するか判定
	 *
	 * @return key:案件ID => true:1件以上存在する / false:存在しない
	 */
	@ModelAttribute("userMenuSaibanBranchNoList")
	public FunctionalLazyLoadForm<Long, List<Long>> saibanBranchNoList() {
		if (SessionUtils.isAlreadyLoggedUser()) {
			return new FunctionalLazyLoadForm<>((ankenId) -> {
				List<TSaibanEntity> saibanEntityList = tSaibanDao.selectByAnkenId(ankenId);
				return saibanEntityList.stream().map(TSaibanEntity::getSaibanBranchNo).collect(Collectors.toList());
			});
		} else {
			return null;
		}
	}

	/**
	 * 案件IDに紐づく名簿IDを取得する
	 *
	 * @return key:案件ID => 名簿ID配列
	 */
	@ModelAttribute("userMenuPersonIdList")
	public FunctionalLazyLoadForm<Long, List<Long>> personIdList() {
		if (SessionUtils.isAlreadyLoggedUser()) {
			return new FunctionalLazyLoadForm<>((ankenId) -> {
				return userMenuService.getRelatedPersonIdList(ankenId);
			});
		} else {
			return null;
		}
	}

}