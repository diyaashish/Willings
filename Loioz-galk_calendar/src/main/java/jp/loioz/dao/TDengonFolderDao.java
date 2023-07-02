package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.dto.DengonFolderDto;
import jp.loioz.entity.TDengonFolderEntity;

/**
 * 伝言-カスタムフォルダ用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TDengonFolderDao {

	/**
	 * 1件のカスタムフォルダーEntityを取得する
	 *
	 * @param dengonFolderSeq
	 * @return カスタムフォルダーEntity
	 */
	@Select
	TDengonFolderEntity selectBySeq(Long dengonFolderSeq);

	/**
	 * 複数件の子カスタムフォルダーEntityを取得する
	 *
	 * @param parentDengonFolderSeq
	 * @return カスタムフォルダーEntitys
	 */
	@Select
	List<TDengonFolderEntity> selectChildFolderListBySeq(Long parentDengonFolderSeq);

	/**
	 * カスタムフォルダの情報取得
	 *
	 * @param loginAccountSeq
	 * @param isParentFolder
	 * @param isTrashed
	 * @return カスタムフォルダ一覧
	 */
	@Select
	List<DengonFolderDto> selectCustomeFolderAsSelectOptions(Long loginAccountSeq, Boolean isParentFolder, Boolean isTrashed);

	/**
	 * 編集用カスタムフォルダー情報の取得
	 *
	 * @param loginAccountSeq
	 * @param dengonFolderSeq
	 * @return 編集用カスタムフォルダー情報
	 */
	@Select
	DengonFolderDto selectFolderEdit(Long loginAccountSeq, Long dengonFolderSeq);

	/**
	 * 登録されている有効なフォルダ数をカウントする
	 *
	 * @param loginAccountSeq
	 * @param isParentFolder カウント対象が親フォルダかサブフォルダか
	 * @return フォルダ数
	 */
	@Select
	Long countFolder(Long loginAccountSeq, boolean isParentFolder);

	/**
	 * 1件のフォルダー情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TDengonFolderEntity entity);

	/**
	 * 1件のフォルダー情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TDengonFolderEntity entity);

	/**
	 * 複数件のフォルダー情報を更新する
	 *
	 * @param entitys
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TDengonFolderEntity> entitys);
}