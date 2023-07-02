package jp.loioz.app.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jp.loioz.app.common.DefaultService;
import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.constant.CommonConstant.BunyaType;
import jp.loioz.common.constant.CommonConstant.SystemFlg;
import jp.loioz.common.exception.DataNotFoundException;
import jp.loioz.common.utility.StringUtils;
import jp.loioz.dao.MBunyaDao;
import jp.loioz.dto.BunyaDto;
import jp.loioz.entity.MBunyaEntity;

/**
 * 分野情報関連の共通サービス処理
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonBunyaService extends DefaultService {

	@Autowired
	private MBunyaDao mBunyaDao;

	// =========================================================================
	// public メソッド
	// =========================================================================

	/**
	 * 分野IDから分野区分が刑事か判別する
	 * 
	 * @param bunyaId
	 * @return 判別結果
	 */
	public boolean isKeiji(Long bunyaId) {

		if (bunyaId == null) {
			// 引数がnullの場合false
			return false;
		}

		MBunyaEntity bunya = mBunyaDao.selectById(bunyaId);
		if (bunya == null) {
			throw new DataNotFoundException("分野情報が存在しません。[bunyaId=" + bunyaId + "]");
		}
		// 分野区分を刑事区分で判定
		return BunyaType.KEIJI.equalsByCode(bunya.getBunyaType());
	}

	/**
	 * 分野IDから分野情報を1件取得する
	 * 
	 * @param bunyaId
	 * @return 分野情報Dto
	 * @throws DataNotFoundException
	 */
	public BunyaDto getBunya(Long bunyaId) throws DataNotFoundException {

		if (bunyaId == null) {
			// 引数がnullの場合null返却
			return null;
		}

		MBunyaEntity bunya = mBunyaDao.selectById(bunyaId);
		if (bunya == null) {
			throw new DataNotFoundException("分野情報が存在しません。[bunyaId=" + bunyaId + "]");
		}
		// Dtoに移し替えて返却
		return this.convert2BunyaDto(bunya);
	}

	/**
	 * BunyaDtoから分野名を返却します。（null許容）<br>
	 * 
	 * @param bunyadto (null許容)
	 * @return bunyaId
	 */
	public String getBunyaName(BunyaDto bunyaDto) {

		if (bunyaDto == null) {
			return null;
		}

		return bunyaDto.getBunyaName();
	}

	/**
	 * 分野情報引当用Mapを返却します。<br>
	 * 引数により利用停止データを含むか選択（除く場合はfalse）
	 * 
	 * @param includeDisabled
	 * @return 分野情報一覧Map
	 */
	public Map<Long, BunyaDto> getBunyaMap(boolean includeDisabled) {

		List<MBunyaEntity> entities = mBunyaDao.selectAll();
		if (CollectionUtils.isEmpty(entities)) {
			// マスタデータが存在しない場合
			throw new DataNotFoundException("分野の情報が存在しません。");
		}

		// 分野一覧を取得
		List<BunyaDto> bunyaList = this.getBunyaList(includeDisabled);

		// 分野IdをkeyとするDtoのMapに変換
		Map<Long, BunyaDto> bunyaMap = bunyaList.stream()
				.collect(Collectors.toMap(BunyaDto::getBunyaId, dto -> dto));

		return bunyaMap;

	}

	/**
	 * 分野情報の一覧を取得します。<br>
	 * 利用停止データは含まない
	 * 
	 * @return 分野情報一覧
	 */
	public List<BunyaDto> getBunyaList() {
		return this.getBunyaList(false);
	}

	/**
	 * 分野情報の一覧を取得します。<br>
	 * 引数により利用停止データを含むか選択（含む場合はtrue）
	 * 
	 * @param includeDisabled
	 * @return 分野情報一覧
	 */
	public List<BunyaDto> getBunyaList(boolean includeDisabled) {

		List<MBunyaEntity> entities = mBunyaDao.selectAll();

		if (CollectionUtils.isEmpty(entities)) {
			// マスタデータが存在しない場合
			throw new DataNotFoundException("分野の情報が存在しません。");
		}

		List<BunyaDto> bunyaList = new ArrayList<BunyaDto>();
		if (includeDisabled) {
			// 利用停止を含む分野情報をDtoに詰め替え
			bunyaList = entities.stream().map(this::convert2BunyaDto).collect(Collectors.toList());

		} else {
			// 利用可の分野情報だけDtoに詰め替え
			Predicate<MBunyaEntity> disabledConditions = e -> !SystemFlg.codeToBoolean(e.getDisabledFlg());
			bunyaList = entities.stream().filter(disabledConditions).map(this::convert2BunyaDto).collect(Collectors.toList());
		}

		return bunyaList;
	}

	/**
	 * 分野と案件名を画面表示用に加工します。<br>
	 * １件データ用（処理中DBアクセスあり）
	 *
	 * @param ankenName
	 * @param bunyaId
	 * @return 分野名-案件名
	 */
	public String bunyaAndAnkenName(String ankenName, Long bunyaId) {

		BunyaDto bunyaDto = null;

		try {
			// 分野マスタ検索
			bunyaDto = this.getBunya(bunyaId);

		} catch (DataNotFoundException e) {
			// 分野IDがマスタに存在しない場合
			if (StringUtils.isNotEmpty(ankenName)) {
				// 案件名だけ取得
				return ankenName;
			}
			return "";
		}

		if (StringUtils.isEmpty(ankenName)) {
			// 案件名だけ空の場合
			return bunyaDto.getVal();
		} else {
			// 案件名、分野名どっちもある
			return String.format("%s%s%s", bunyaDto.getVal(), CommonConstant.HYPHEN, ankenName);
		}
	}

	/**
	 * 分野と案件名を画面表示用に加工します。<br>
	 * 複数件ループ中の呼出し用（処理中DBアクセスなし）
	 *
	 * @param ankenName
	 * @param bunyaId
	 * @param bunyaMap 分野情報引当用のMap
	 * @return 分野名-案件名
	 */
	public String bunyaAndAnkenName(String ankenName, Long bunyaId, Map<Long, BunyaDto> bunyaMap) {

		BunyaDto bunyaDto = null;

		if (bunyaMap.containsKey(bunyaId)) {
			// 分野一覧から取得
			bunyaDto = bunyaMap.get(bunyaId);

		} else {
			// 分野IDがマスタに存在しない場合
			if (StringUtils.isNotEmpty(ankenName)) {
				// 案件名だけ取得
				return ankenName;
			}
			return "";
		}

		if (StringUtils.isEmpty(ankenName)) {
			// 案件名だけ空の場合
			return bunyaDto.getVal();
		} else {
			// 案件名、分野名どっちもある
			return String.format("%s%s%s", bunyaDto.getVal(), CommonConstant.HYPHEN, ankenName);
		}
	}

	/**
	 * 分野が有効かどうか
	 * 
	 * @param bunyaId
	 * @return
	 */
	public boolean isEnabled(Long bunyaId) {
		try {
			return !getBunya(bunyaId).isDisabledFlg();
		} catch (Exception e) {
			// エラーが発生した場合は、無効
			return false;
		}
	}

	/**
	 * 分野IDがマスターテーブルに存在するかチェックし存在しない、もしくは削除済みの場合は分野をnullにする
	 * 
	 * @param bunyaId
	 * @param masterBunyaList
	 * @return
	 */
	public String replaceIfBunyaNotExist(String bunyaId, List<BunyaDto> masterBunyaList) {
		// 分野マスターテーブルに存在しない、もしくは削除済みの場合は検索条件の分野をnullにする
		if (!isExistBunya(bunyaId, masterBunyaList)) {
			bunyaId = null;
		}
		return bunyaId;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================

	/** 分野情報をDtoに変換 */
	private BunyaDto convert2BunyaDto(MBunyaEntity entity) {

		BunyaDto bunyaDto = new BunyaDto();
		bunyaDto.setBunyaId(entity.getBunyaId());
		bunyaDto.setBunyaType(entity.getBunyaType());
		bunyaDto.setBunyaName(entity.getBunyaName());
		bunyaDto.setDisabledFlg(SystemFlg.codeToBoolean(entity.getDisabledFlg()));
		bunyaDto.setDispOrder(entity.getDispOrder());

		return bunyaDto;
	}

	/**
	 * 検索条件で設定した分野がマスターテーブルに存在しないか、もしくは削除済みかチェックする
	 *
	 * @param strBunyaId
	 * @param masterBunyaList
	 * @return
	 */
	private boolean isExistBunya(String strBunyaId, List<BunyaDto> masterBunyaList) {

		if (StringUtils.isEmpty(strBunyaId)) {
			return false;
		}

		Long bunyaId = Long.valueOf(strBunyaId);
		for (BunyaDto dto : masterBunyaList) {
			if (dto.getBunyaId().equals(bunyaId)) {
				return true;
			}
		}
		return false;
	}

}
