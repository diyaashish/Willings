package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.DownloadListBean;
import jp.loioz.entity.TAccgDocDownloadEntity;

/**
 * 会計書類-ダウンロード情報
 */
@ConfigAutowireable
@Dao
public interface TAccgDocDownloadDao {

	/**
	 * SEQをキーとして、会計書類-ダウンロード情報を取得する
	 * 
	 * @param accgDocDownloadSeq
	 * @return
	 */
	@Select
	TAccgDocDownloadEntity selectAccgDocDownloadByAccgDocDownloadSeq(Long accgDocDownloadSeq);

	/**
	 * WEB共有認証URLキーから、会計書類-ダウンロード情報を取得する
	 * 
	 * @param downloadViewUrlKey (UQ)
	 * @return
	 */
	@Select
	TAccgDocDownloadEntity selectAccgDocDownloadByDownloadUrlKey(String downloadViewUrlKey);

	/**
	 * SEQをキーとして、ダンロード一覧情報を取得する
	 * 
	 * @param accgDocDownloadSeq
	 * @return
	 */
	@Select
	List<DownloadListBean> selectDownloadListBeanByAccgDocDownloadSeq(Long accgDocDownloadSeq);

	/**
	 * １件の会計書類ダウンロード期間情報の登録処理
	 * 
	 * @param entity
	 * @return
	 */
	@Insert
	int insert(TAccgDocDownloadEntity entity);

	/**
	 * 会計書類-ダウンロード情報データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocDownload();
	
}
