package jp.loioz.app.common.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.loioz.app.common.DefaultService;
import jp.loioz.app.common.form.SelectOptionForm;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.AccountType;
import jp.loioz.common.constant.CommonConstant.EraType;
import jp.loioz.common.constant.CommonConstant.NyushukkinType;
import jp.loioz.common.constant.CommonConstant.SelectType;
import jp.loioz.common.constant.CommonConstant.SosakikanType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.dao.MAccountDao;
import jp.loioz.dao.MBunyaDao;
import jp.loioz.dao.MBushoDao;
import jp.loioz.dao.MGroupDao;
import jp.loioz.dao.MNyushukkinKomokuDao;
import jp.loioz.dao.MRoomDao;
import jp.loioz.dao.MSaibanshoBuDao;
import jp.loioz.dao.MSaibanshoDao;
import jp.loioz.dao.MSelectListDao;
import jp.loioz.dao.MSosakikanDao;
import jp.loioz.domain.value.PersonName;
import jp.loioz.entity.MAccountEntity;
import jp.loioz.entity.MBunyaEntity;
import jp.loioz.entity.MBushoEntity;
import jp.loioz.entity.MGroupEntity;
import jp.loioz.entity.MNyushukkinKomokuEntity;
import jp.loioz.entity.MRoomEntity;
import jp.loioz.entity.MSaibanshoBuEntity;
import jp.loioz.entity.MSaibanshoEntity;
import jp.loioz.entity.MSelectListEntity;
import jp.loioz.entity.MSosakikanEntity;

/**
 * マスターテーブルからプルダウン用Formの取得共通処理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonSelectBoxService extends DefaultService {

	/** アカウントDaoクラス */
	@Autowired
	private MAccountDao mAccountDao;

	/** 部署Daoクラス */
	@Autowired
	private MBushoDao mBushoDao;

	/** グループDaoクラス */
	@Autowired
	private MGroupDao mGroupDao;

	/** 入出金項目Daoクラス */
	@Autowired
	private MNyushukkinKomokuDao mNyushukkinKomokuDao;

	/** 会議室Daoクラス */
	@Autowired
	private MRoomDao mRoomDao;

	/** 裁判所Daoクラス */
	@Autowired
	private MSaibanshoDao mSaibanshoDao;

	/** 裁判所Daoクラス */
	@Autowired
	private MSaibanshoBuDao mSaibanshoBuDao;

	/** 選択肢Daoクラス */
	@Autowired
	private MSelectListDao mSelectListDao;

	/** 施設Daoクラス */
	@Autowired
	private MSosakikanDao mSosakikanDao;

	/** 分野マスタ用のDaoクラス */
	@Autowired
	private MBunyaDao mBunyaDao;

	/**
	 * 有効なアカウントセレクトボックスの取得
	 *
	 * <pre>
	 * アカウントのセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = accountSeq
	 * セレクトボックスの label値  = accountName
	 * </pre>
	 *
	 * @return アカウントセレクトボックス
	 */
	public List<SelectOptionForm> getAccountSelectBox() {
		List<MAccountEntity> accountList = mAccountDao.selectEnabledAccount();
		if (accountList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = accountList.stream()
				.map(entity -> new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 有効なアカウントセレクトボックスの取得
	 *
	 * <pre>
	 * アカウント種別をキーとして、アカウントのセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = accountSeq
	 * セレクトボックスの label値  = accountName
	 * </pre>
	 *
	 * @return アカウントセレクトボックス
	 */
	public List<SelectOptionForm> getAccountSelectBoxFilterByType(AccountType type) {
		List<MAccountEntity> accountList = mAccountDao.selectEnabledAccountByAccountType(type.getCd());
		if (accountList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = accountList.stream()
				.map(entity -> new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 有効なアカウントセレクトボックスの取得
	 *
	 * <pre>
	 * 指定されたアカウント種別を除いて、アカウントのセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = accountSeq
	 * セレクトボックスの label値  = accountName
	 * </pre>
	 *
	 * @return アカウントセレクトボックス
	 */
	public List<SelectOptionForm> getAccountSelectBoxExcludeByType(AccountType type) {
		List<MAccountEntity> accountList = mAccountDao.selectEnabledAccount();
		if (accountList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = accountList.stream()
				.filter(entity -> !type.equalsByCode(entity.getAccountType()))
				.map(entity -> new SelectOptionForm(entity.getAccountSeq(), PersonName.fromEntity(entity).getName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 部署セレクトボックスの取得
	 *
	 * <pre>
	 * 部署セレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = bushoId
	 * セレクトボックスの label値 = bushoName
	 * </pre>
	 *
	 * @return 部署セレクトボックス
	 */
	public List<SelectOptionForm> getBushoSelectBox() {
		List<MBushoEntity> bushoList = mBushoDao.selectAll();
		if (bushoList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = bushoList.stream()
				.map(entity -> new SelectOptionForm(entity.getBushoId(), entity.getBushoName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * グループセレクトボックスの取得
	 *
	 * <pre>
	 * グループセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = groupSeq
	 * セレクトボックスの label値 = groupName
	 * </pre>
	 *
	 * @return グループセレクトボックス
	 */
	public List<SelectOptionForm> getGroupSelectBox() {
		List<MGroupEntity> groupList = mGroupDao.selectAll();
		if (groupList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = groupList.stream()
				.map(entity -> new SelectOptionForm(entity.getGroupId(), entity.getGroupName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 入出金項目セレクトボックスの取得
	 *
	 * <pre>
	 * 入出金項目セレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = nyushukkinKomokuId
	 * セレクトボックスの label値 = nyushukkinKomokuName
	 * </pre>
	 *
	 * @return 入出金項目セレクトボックス
	 */
	public List<SelectOptionForm> getNyuShukkinKomokuSelectBox() {
		List<MNyushukkinKomokuEntity> nyuShukkinList = mNyushukkinKomokuDao.selectAll();
		if (nyuShukkinList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = nyuShukkinList.stream()
				.filter(entity -> !SystemFlg.codeToBoolean(entity.getUndeletableFlg()))
				.map(entity -> new SelectOptionForm(entity.getNyushukkinKomokuId(), entity.getKomokuName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 *
	 * 入出金項目セレクトボックスの取得
	 *
	 * <pre>
	 * 入出金項目種別をキーとして、セレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = nyushukkinKomokuId
	 * セレクトボックスの label値 = nyushukkinKomokuName
	 * </pre>
	 *
	 * @param 入出金項目種別
	 * @return 種別ごとの入出金項目セレクトボックス
	 */
	public List<SelectOptionForm> getNyuShukkinKomokuSelectBoxFilterByType(NyushukkinType type) {
		List<MNyushukkinKomokuEntity> nyushukkinListByType = mNyushukkinKomokuDao.selectByNyushukkinType(type.getCd());
		if (nyushukkinListByType.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = nyushukkinListByType.stream()
				.filter(entity -> !SystemFlg.codeToBoolean(entity.getUndeletableFlg()))
				.filter(entity -> !SystemFlg.codeToBoolean(entity.getDisabledFlg()))
				.map(entity -> new SelectOptionForm(entity.getNyushukkinKomokuId(), entity.getKomokuName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 会議室セレクトボックスの取得
	 *
	 * <pre>
	 * 会議室のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = roomId
	 * セレクトボックスの label値 = roomName
	 * </pre>
	 *
	 * @return 会議室セレクトボックス
	 */
	public List<SelectOptionForm> getRoomSelectBox() {
		List<MRoomEntity> roomList = mRoomDao.selectEnabled();
		if (roomList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = roomList.stream()
				.map(entity -> new SelectOptionForm(entity.getRoomId(), entity.getRoomName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 裁判所セレクトボックスの取得
	 *
	 * <pre>
	 * 裁判所のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = saibanshoId
	 * セレクトボックスの label値 = saibanshoName
	 * </pre>
	 *
	 * @return
	 */
	public List<SelectOptionForm> getSaibanshoSelectBox() {
		List<MSaibanshoEntity> saibanshoList = mSaibanshoDao.selectEnabled();
		if (saibanshoList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = saibanshoList.stream()
				.map(entity -> new SelectOptionForm(entity.getSaibanshoId(), entity.getSaibanshoName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 係属部セレクトボックスの取得
	 *
	 * <pre>
	 * 裁判所IDに紐づく係属部のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = keizokuBuId
	 * セレクトボックスの label値 = keizokuBuName
	 * </pre>
	 *
	 * @return 係属部セレクトボックス
	 */
	public List<SelectOptionForm> getKeizokuBuSelectBox(Long saibanshoId) {
		List<MSaibanshoBuEntity> saibanshoBuList = mSaibanshoBuDao.selectBySaibanshoId(saibanshoId);
		if (saibanshoBuList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = saibanshoBuList.stream()
				.map(entity -> new SelectOptionForm(entity.getKeizokuBuId(), entity.getKeizokuBuName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 有効な選択肢セレクトボックスの取得
	 *
	 * <pre>
	 * 選択肢のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = selectSeq
	 * セレクトボックスの label値 = selectVal
	 * </pre>
	 *
	 * @return 選択肢セレクトボックス
	 */
	public List<SelectOptionForm> getSelectListSelectBox() {
		List<MSelectListEntity> selectList = mSelectListDao.selectEnabled();
		if (selectList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = selectList.stream()
				.map(entity -> new SelectOptionForm(entity.getSelectSeq(), entity.getSelectVal()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 選択肢種別をキーとして、有効な選択肢セレクトボックスの取得
	 *
	 * <pre>
	 * 選択肢のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = selectSeq
	 * セレクトボックスの label値 = selectVal
	 * </pre>
	 *
	 * @param 選択肢種別
	 * @return 選択肢セレクトボックス
	 */
	public List<SelectOptionForm> getSelectListSelectBoxFilterByType(SelectType type) {
		List<MSelectListEntity> selectList = mSelectListDao.selectEnabledFilterByType(type.getCd());
		if (selectList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = selectList.stream()
				.map(entity -> new SelectOptionForm(entity.getSelectSeq(), entity.getSelectVal()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 施設セレクトボックスの取得
	 *
	 * <pre>
	 * 施設のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = sosakikanIdId
	 * セレクトボックスの label値 = sosakikanIdName
	 * </pre>
	 *
	 * @return 選択肢セレクトボックス
	 */
	public List<SelectOptionForm> getSosakikanSelectBox() {
		List<MSosakikanEntity> sosakikanIdList = mSosakikanDao.selectEnabled();
		if (sosakikanIdList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = sosakikanIdList.stream()
				.map(entity -> new SelectOptionForm(entity.getSosakikanId(), entity.getSosakikanName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 施設セレクトボックスの取得
	 *
	 * <pre>
	 * 施設種別をキーとして、施設のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = sosakikanIdId
	 * セレクトボックスの label値  = sosakikanIdName
	 * </pre>
	 *
	 * @return 選択肢セレクトボックス
	 */
	public List<SelectOptionForm> getSosakikanSelectBoxFilterByType(SosakikanType type) {
		List<MSosakikanEntity> sosakikanIdList = mSosakikanDao.selectEnabled();
		if (sosakikanIdList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = sosakikanIdList.stream()
				.filter(entity -> type.equalsByCode(entity.getSosakikanType()))
				.map(entity -> new SelectOptionForm(entity.getSosakikanId(), entity.getSosakikanName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 施設セレクトボックスの取得
	 *
	 * <pre>
	 * 指定された施設種別を除いて、施設のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = sosakikanIdId
	 * セレクトボックスの label値  = sosakikanIdName
	 * </pre>
	 *
	 * @return 選択肢セレクトボックス
	 */
	public List<SelectOptionForm> getSosakikanSelectBoxExcludeByType(SosakikanType type) {
		List<MSosakikanEntity> sosakikanIdList = mSosakikanDao.selectEnabled();
		if (sosakikanIdList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = sosakikanIdList.stream()
				.filter(entity -> !type.equalsByCode(entity.getSosakikanType()))
				.map(entity -> new SelectOptionForm(entity.getSosakikanId(), entity.getSosakikanName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 分野セレクトボックスの取得
	 *
	 * <pre>
	 * 分野のセレクトボックスを作成します。
	 * 取得結果がなければnullを返します。
	 *
	 * セレクトボックスの value値 = bunyaId
	 * セレクトボックスの label値  = bunyaName
	 * </pre>
	 *
	 * @return 分野セレクトボックス
	 */
	public List<SelectOptionForm> getBunyaSelectBox() {

		List<MBunyaEntity> bunyaList = mBunyaDao.selectAll();

		if (bunyaList.isEmpty()) {
			return Collections.emptyList();
		}
		List<SelectOptionForm> selectBox = bunyaList.stream()
				.map(entity -> new SelectOptionForm(entity.getBunyaId(), entity.getBunyaName()))
				.collect(Collectors.toList());
		return selectBox;
	}

	/**
	 * 裁判画面で使用する年号のセレクトボックスを作成します
	 * 
	 * @return 裁判の年号セレクトボックス
	 */
	public List<SelectOptionForm> getSaibanEraType() {
		EraType[] eraType = CommonConstant.EraType.saibanValues();
		List<SelectOptionForm> list = new ArrayList<SelectOptionForm>();
		for (int i = 0; i < eraType.length; i++) {
			SelectOptionForm option = new SelectOptionForm(eraType[i].getCd(), eraType[i].getVal());
			list.add(option);
		}
		return list;
	}

}
