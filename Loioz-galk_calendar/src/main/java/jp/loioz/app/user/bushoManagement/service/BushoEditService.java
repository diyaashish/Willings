package jp.loioz.app.user.bushoManagement.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.user.bushoManagement.form.BushoEditForm;
import jp.loioz.app.user.bushoManagement.form.BushoEditForm.Account;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.MessageEnum;
import jp.loioz.common.exception.AppException;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.CommonUtils;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MBushoDao;
import jp.loioz.dao.TBushoShozokuAcctDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.dto.BushoEditDto;
import jp.loioz.dto.BushoShozokuDto;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MBushoEntity;
import jp.loioz.entity.TBushoShozokuAcctEntity;

/**
 * 部署データ編集画面のサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BushoEditService extends DefaultService {

	/** 部署データ用のDaoクラス */
	@Autowired
	private MBushoDao mBushoDao;

	/** 部署所属アカウントデータ用のDaoクラス */
	@Autowired
	private TBushoShozokuAcctDao tBushoShozokuAcctDao;

	/** アカウント管理用のDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** ロガークラス */
	@Autowired
	private Logger logger;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * viewFormの作成
	 *
	 * @return
	 */
	public BushoEditForm createViewForm() {
		BushoEditForm viewForm = new BushoEditForm();

		// 全てのアカウント情報を取得する
		List<MAccountEntity> mAccountEntities = mAccountDao.selectEnabledAccount();

		// 初期表示では最初のアカウント種別コードを入力する
		viewForm.setSelectAccountList(this.convertAccountData(mAccountEntities));

		return viewForm;
	}

	/**
	 * 表示データの取得
	 *
	 * @param viewForm
	 * @return
	 * @throws AppException
	 */
	public BushoEditForm setData(BushoEditForm viewForm) throws AppException {

		// 更新元データの取得
		Long bushoId = viewForm.getBushoEditData().getBushoId();
		MBushoEntity mBushoEntity = mBushoDao.selectById(bushoId);
		if (mBushoEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}
		// Entity → Dto
		BushoEditDto bushoEditData = new BushoEditDto();

		// 選択された部署Idを取得
		bushoEditData.setBushoId(bushoId);
		bushoEditData.setBushoName(mBushoEntity.getBushoName());
		bushoEditData.setDispOrder(mBushoEntity.getDispOrder().toString());
		bushoEditData.setVersionNo(mBushoEntity.getVersionNo());
		viewForm.setBushoEditData(bushoEditData);

		/* 所属ユーザの取得 */
		List<BushoShozokuDto> shozokuDtoList = tBushoShozokuAcctDao.selectBushoShozokuAccount(bushoId);
		List<Long> shozokuAccountSeqList = shozokuDtoList.stream()
				.filter(BushoShozokuDto::isFlgRegist)
				.map(BushoShozokuDto::getAccountSeq)
				.collect(Collectors.toList());
		List<MAccountEntity> shozokuAccountEntityList = mAccountDao.selectAccountByAccountSeqList(shozokuAccountSeqList);

		// 初期情報の設定/
		viewForm.setShozokuAccountList(this.convertAccountData(shozokuAccountEntityList));

		// 選択肢オブジェクトからユーザー情報を除く
		List<Account> selectList = viewForm.getSelectAccountList();

		// 一度Map型にして、removeする
		Map<Long, Account> selectMap = selectList.stream().collect(Collectors.toMap(
				Account::getAccountSeq, // キーはSEQ
				Function.identity(),
				(key, value) -> value,
				LinkedHashMap::new));
		for (Long seq : shozokuAccountSeqList) {
			selectMap.remove(seq);
		}

		// 削除したマップをListにして設定
		viewForm.setSelectAccountList(new ArrayList<>(selectMap.values()));

		return viewForm;
	}

	/**
	 * 登録処理（1件）
	 *
	 * @param viewForm
	 * @throws AppException
	 */
	public void regist(BushoEditForm viewForm) throws AppException {

		// エンティティの作成
		MBushoEntity registEntity = new MBushoEntity();
		long dispOrder = mBushoDao.maxDisp();

		// 登録するデータの作成
		BushoEditDto bushoEditData = viewForm.getBushoEditData();
		registEntity.setBushoName(bushoEditData.getBushoName());
		registEntity.setDispOrder(dispOrder + 1);

		try {
			// 登録処理
			mBushoDao.insert(registEntity);

		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// 所属アカウントをすべて取得する
		List<TBushoShozokuAcctEntity> tBushoShozokuAcctEntityList = new ArrayList<TBushoShozokuAcctEntity>();

		List<Long> accountSeqList = bushoEditData.getAccountSeqList();
		int count = 1;
		Long bushoId = registEntity.getBushoId();
		for (Long accountSeq : accountSeqList) {
			TBushoShozokuAcctEntity entity = new TBushoShozokuAcctEntity();
			entity.setAccountSeq(accountSeq);
			entity.setBushoId(bushoId);
			entity.setDispOrder(Long.valueOf(count++));
			tBushoShozokuAcctEntityList.add(entity);
		}

		try {
			// 選択アカウントを新規登録する
			tBushoShozokuAcctDao.insert(tBushoShozokuAcctEntityList);

		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 更新（１件）
	 *
	 * @param viewForm
	 */
	public void update(BushoEditForm viewForm) throws AppException {

		// 更新データの取得
		MBushoEntity updateEntity = mBushoDao.selectById(viewForm.getBushoEditData().getBushoId());
		if (updateEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 更新するデータを作成
		BushoEditDto bushoEditData = viewForm.getBushoEditData();
		Long bushoId = bushoEditData.getBushoId();
		updateEntity.setBushoId(bushoId);
		updateEntity.setBushoName(bushoEditData.getBushoName());

		try {
			// 更新処理
			mBushoDao.update(updateEntity);

		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		/* 所属アカウントをすべて取得する */
		List<TBushoShozokuAcctEntity> tBushoShozokuAcctEntityList = tBushoShozokuAcctDao.selectByBushoId(bushoId);

		try {
			// 所属アカウントをすべて削除する
			tBushoShozokuAcctDao.delete(tBushoShozokuAcctEntityList);

		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		// 登録用のEntityに変換する
		tBushoShozokuAcctEntityList = new ArrayList<TBushoShozokuAcctEntity>();
		List<Long> accountSeqList = bushoEditData.getAccountSeqList();
		int count = 1;
		for (Long accountSeq : accountSeqList) {
			TBushoShozokuAcctEntity entity = new TBushoShozokuAcctEntity();
			entity.setAccountSeq(accountSeq);
			entity.setBushoId(bushoId);
			entity.setDispOrder(Long.valueOf(count++));
			tBushoShozokuAcctEntityList.add(entity);
		}

		try {
			// 選択アカウントを新規登録する
			tBushoShozokuAcctDao.insert(tBushoShozokuAcctEntityList);

		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

	}

	/**
	 * 削除処理（論理）
	 *
	 * @param viewForm
	 */
	public void delete(BushoEditForm viewForm) throws AppException {

		// 削除データの取得
		MBushoEntity deleteEntity = mBushoDao.selectById(viewForm.getBushoEditData().getBushoId());
		List<TBushoShozokuAcctEntity> tBushoShozokuAcctEntities = tBushoShozokuAcctDao.selectByBushoId(viewForm.getBushoEditData().getBushoId());

		// 排他エラーチェック
		if (deleteEntity == null) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + ALREADY_UPDATED_TO_OTHER_USER);
			throw new AppException(MessageEnum.MSG_E00025, null);
		}

		// 削除フラグを立てる
		deleteEntity.setFlgDelete();

		try {
			// 削除処理
			mBushoDao.update(deleteEntity);
			// 削除する部署に紐づいているデータの削除処理
			tBushoShozokuAcctDao.delete(tBushoShozokuAcctEntities);

		} catch (Exception e) {
			String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
			logger.warn(classAndMethodName + e.getMessage());
			throw new AppException(MessageEnum.MSG_E00013, e);
		}

		/* 最新のデータを取得する */
		List<MBushoEntity> entityList = mBushoDao.selectAll();
		// 登録件数を確認
		if (entityList.size() > 0) {

			try {

				// 全ての部門の表示順を更新
				List<MBushoEntity> entities = new ArrayList<MBushoEntity>();
				long orderNum = 1;
				for (MBushoEntity entity : entityList) {
					entity.setDispOrder(Long.valueOf(orderNum));
					entities.add(entity);
					orderNum++;
				}
				// 全ての並び順を更新
				mBushoDao.update(entities);

			} catch (OptimisticLockingFailureException ex) {
				// 楽観ロックエラー
				String classAndMethodName = CommonUtils.getCurrentExecuteClassAndMethodName();
				logger.warn(classAndMethodName + ex.getMessage());

				throw new AppException(MessageEnum.MSG_E00013, ex);
			}

		}

	}

	/**
	 * 登録時件数上限値チェック
	 *
	 * @return 検証結果
	 */
	public boolean registLimitValidate() {
		boolean valid = true;
		long enabledCount = mBushoDao.selectAll().size();

		// 登録上限のDBチェック
		if (enabledCount >= CommonConstant.BUSHO_REGIST_LIMIT) {
			valid = false;
		}
		return valid;
	}

	/**
	 * ユーザーアカウントリストを表示用に変換する
	 *
	 * @param accountEntityList
	 * @return accountDtoList
	 */
	private List<Account> convertAccountData(List<MAccountEntity> accountEntityList) {
		List<Account> accountDtoList = new ArrayList<>();
		for (MAccountEntity entity : accountEntityList) {
			Account dto = new Account();
			dto.setAccountSeq(entity.getAccountSeq());
			PersonName accountName = PersonName.fromEntity(entity);
			dto.setAccountName(accountName.getName());
			dto.setAccountType(AccountType.of(entity.getAccountType()));
			accountDtoList.add(dto);
		}
		return accountDtoList;
	}

}