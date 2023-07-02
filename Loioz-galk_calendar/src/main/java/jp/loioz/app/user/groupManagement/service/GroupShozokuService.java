package jp.loioz.app.user.groupManagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.groupManagement.form.GroupShozokuForm;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MGroupDao;
import jp.loioz.dao.TGroupShozokuAcctDao;
import jp.loioz.dto.AccountEditDto;
import jp.loioz.dto.GroupShozokuDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MGroupEntity;
import jp.loioz.entity.TGroupShozokuAcctEntity;

/**
 * 所属グループアカウント画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupShozokuService extends DefaultService {
	/** グループデータ用のDaoクラス */
	@Autowired
	private MGroupDao mGroupDao;

	/** グループ所属アカウント用のDaoクラス */
	@Autowired
	private TGroupShozokuAcctDao tGroupShozokuAcctDao;

	/** アカウント管理用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** Logger */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * グループデータ一覧画面の表示情報を作成する(検索なし)
	 *
	 * @param groupShozokuForm
	 * @return
	 */
	public GroupShozokuForm getShozokuUser(GroupShozokuForm groupShozokuForm) throws AppException {
		// 選択されたグループIdを取得
		Long groupId = groupShozokuForm.getGroupId();
		// グループ情報
		MGroupEntity mGroupEntity = mGroupDao.selectById(groupId);
		if (mGroupEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 所属ユーザの取得 */
		List<GroupShozokuDto> shozokuDtoList = tGroupShozokuAcctDao.selectGroupShozokuAccount(groupId);
		List<Long> shozokuAccountSeqList = shozokuDtoList.stream().filter(GroupShozokuDto::isFlgRegist).map(GroupShozokuDto::getAccountSeq).collect(Collectors.toList());
		List<MAccountEntity> shozokuAccountEntityList = mAccountDao.selectAccountByAccountSeqList(shozokuAccountSeqList);

		/* 初期表示では最初のアカウント種別コードを入力する */
		groupShozokuForm.setSelectAccountList(this.selectEnabledAccountByAccountType(AccountType.LAWYER.getCd(), shozokuAccountSeqList));

		/* 初期情報の設定 */
		groupShozokuForm.setGroupId(groupId);
		groupShozokuForm.setGroupName(mGroupEntity.getGroupName());
		groupShozokuForm.setShozokuAccountList(this.convertAccountManagementDto(shozokuAccountEntityList));
		return groupShozokuForm;
	}

	/**
	 * 更新処理
	 *
	 * @param form
	 */
	public void update(GroupShozokuForm form) throws AppException {
		/** 更新時は共通処理から呼ばれるため、グループIDは以下から取得 */
		Long groupId = form.getTempId();

		// グループが存在しない場合は排他エラー
		MGroupEntity mGroupEntity = mGroupDao.selectById(groupId);
		if (mGroupEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		/* 所属アカウントをすべて取得する */
		List<TGroupShozokuAcctEntity> tGroupShozokuAcctEntityList = tGroupShozokuAcctDao.selectByGroupId(groupId);

		/* 所属アカウントをすべて削除する */
		try {
			tGroupShozokuAcctDao.delete(tGroupShozokuAcctEntityList);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		/* 登録用のEntityに変換する */
		tGroupShozokuAcctEntityList = new ArrayList<TGroupShozokuAcctEntity>();
		List<Long> accountSeqList = form.getAccountSeqList();
		int count = 1;
		for (Long accountSeq : accountSeqList) {
			TGroupShozokuAcctEntity entity = new TGroupShozokuAcctEntity();
			entity.setAccountSeq(accountSeq);
			entity.setGroupId(groupId);
			entity.setDispOrder(Long.valueOf(count++));
			tGroupShozokuAcctEntityList.add(entity);
		}

		/* 選択アカウントを新規登録する */
		try {
			tGroupShozokuAcctDao.insert(tGroupShozokuAcctEntityList);
		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}
	}

	/**
	 * 指定したアカウント種別のユーザーアカウントリストを取得する
	 *
	 * @param accountType
	 * @param accountSeq
	 * @return
	 */
	public List<AccountEditDto> selectEnabledAccountByAccountType(String accountType, String accountSeq) {
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccountByAccountType(accountType);
		List<Long> accountSeqList = this.getAccountSeqList(accountSeq);

		/* アカウントリストに何も追加されていなければそのまま返す */
		if (accountSeqList == null || accountSeqList.isEmpty()) {
			return this.convertAccountManagementDto(accountEntityList);
		}

		/* アカウントリストに含まれていないアカウントのみ表示用の一覧に追加する */
		List<MAccountEntity> selectAccountEntityList = new ArrayList<MAccountEntity>();
		for (MAccountEntity entity : accountEntityList) {
			if (!accountSeqList.contains(entity.getAccountSeq())) {
				selectAccountEntityList.add(entity);
			}
		}

		return this.convertAccountManagementDto(selectAccountEntityList);
	}

	/**
	 * 指定したアカウント種別のユーザーアカウントリストを取得する
	 *
	 * @param accountType
	 * @param accountSeqList
	 * @return List<AccountManagementDto>
	 */
	public List<AccountEditDto> selectEnabledAccountByAccountType(String accountType, List<Long> accountSeqList) {
		List<MAccountEntity> accountEntityList = mAccountDao.selectEnabledAccountByAccountType(accountType);

		/* アカウントリストに何も追加されていなければそのまま返す */
		if (accountSeqList == null || accountSeqList.isEmpty()) {
			return this.convertAccountManagementDto(accountEntityList);
		}

		/* アカウントリストに含まれていないアカウントのみ表示用の一覧に追加する */
		List<MAccountEntity> selectAccountEntityList = new ArrayList<MAccountEntity>();
		for (MAccountEntity entity : accountEntityList) {
			if (!accountSeqList.contains(entity.getAccountSeq())) {
				selectAccountEntityList.add(entity);
			}
		}

		return this.convertAccountManagementDto(selectAccountEntityList);
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/**
	 * 文字列のSEQリストをList<Long>に変換して返す
	 *
	 * @param accountSeq
	 * @return accountSeqList
	 */
	private List<Long> getAccountSeqList(String accountSeq) {
		/* 文字列がnullならnullを返却 */
		return StringUtils.isEmpty(accountSeq) ? null : Arrays.asList(accountSeq.split(",")).stream().map(Long::parseLong).collect(Collectors.toList());
	}

	/**
	 * ユーザーアカウントリストを表示用に変換する
	 *
	 * @param accountEntityList
	 * @return accountDtoList
	 */
	private List<AccountEditDto> convertAccountManagementDto(List<MAccountEntity> accountEntityList) {
		List<AccountEditDto> accountDtoList = new ArrayList<>();
		for (MAccountEntity entity : accountEntityList) {
			AccountEditDto dto = new AccountEditDto();
			dto.setAccountSeq(entity.getAccountSeq());
			dto.setAccountNameSei(entity.getAccountNameSei());
			dto.setAccountNameMei(entity.getAccountNameMei());
			dto.setAccountType(entity.getAccountType());
			accountDtoList.add(dto);
		}
		return accountDtoList;
	}

}