package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.AccgDocFileBean;
import jp.loioz.common.constant.CommonConstant.AccgDocFileType;
import jp.loioz.entity.TAccgDocFileEntity;

/**
 * 会計書類ファイルDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgDocFileDao {

	/**
	 * 会計書類ファイルSEQをキーとして、会計書類ファイル情報を取得する
	 * 
	 * @param accgDocFileSeq
	 * @return
	 */
	default TAccgDocFileEntity selectAccgDocFileByAccgDocFileSeq(Long accgDocFileSeq) {
		return selectAccgDocFileByAccgDocFileSeq(Arrays.asList(accgDocFileSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * 会計書類ファイルSEQをキーとして、会計書類ファイル情報を取得する
	 * 
	 * @param accgDocFileSeqList
	 * @return
	 */
	@Select
	List<TAccgDocFileEntity> selectAccgDocFileByAccgDocFileSeq(List<Long> accgDocFileSeqList);

	/**
	 * 会計書類SEQに紐づく会計書類ファイル情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<TAccgDocFileEntity> selectAccgDocFileByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく、会計書類ファイル情報をすでに送付済みのPDFを除外し、取得する
	 * 
	 * @return
	 */
	@Select
	List<TAccgDocFileEntity> selectAccgDocFileExcludeSendByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQに紐づく、会計書類ファイル情報をすでに送付済みのPDFを除外し、取得する
	 * 
	 * @return
	 */
	@Select
	List<TAccgDocFileEntity> selectAccgDocFileExcludeSendByAccgDocSeqList(List<Long> accgDocSeqList);

	/**
	 * 会計書類SEQ、ファイル種別に紐づく会計書類ファイル情報を取得します
	 * 
	 * @param accgDocSeqList
	 * @param accgDocFileTypeCd
	 * @return
	 */
	@Select
	List<TAccgDocFileEntity> selectAccgDocFileByParams(List<Long> accgDocSeqList, String accgDocFileTypeCd);

	/**
	 * 請求書、精算書の発行ステータスが「下書き」の会計書類ファイル情報を取得します
	 * 
	 * @param accgDocSeqList
	 * @param accgDocFileTypeCd
	 * @return
	 */
	@Select
	List<TAccgDocFileEntity> selectAccgDocFileWithIssueStatusDraftByParams(List<Long> accgDocSeqList, String accgDocFileTypeCd);

	/**
	 * 会計書類SEQをキーとして、会計書類ファイル情報を取得する
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	List<AccgDocFileBean> selectAccgDocFileBeanByAccgDocSeqAndAccgDocFileType(Long accgDocSeq, AccgDocFileType accgDocFileType);

	/**
	 * 会計書類SEQをキーとして、未送付の会計書類ファイル情報を取得する
	 * 
	 * @param accgDocSeq
	 * @param accgDocFileType
	 * @return
	 */
	default List<AccgDocFileBean> selectAccgDocFileBeanExcludeSendFileByAccgDocSeqAndAccgDocFileType(Long accgDocSeq, AccgDocFileType accgDocFileType) {
		return selectAccgDocFileBeanByAccgDocSeqAndAccgDocFileType(accgDocSeq, accgDocFileType)
				.stream()
				.filter(e -> e.getAccgDocActSendFileSeq() == null)
				.collect(Collectors.toList());
	}

	/**
	 * 会計書類ファイルSEQをキーとして、会計書類ファイル情報を取得する
	 * 
	 * @param accgDocFileSeq
	 * @return
	 */
	@Select
	List<AccgDocFileBean> selectAccgDocFileBeanByAccgDocFileSeq(Long accgDocFileSeq);

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgDocFileEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgDocFileEntity entity);

	/**
	 * 複数更新
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] batchUpdate(List<TAccgDocFileEntity> entityList);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgDocFileEntity entity);

	/**
	 * 複数削除
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchDelete
	int[] batchDelete(List<TAccgDocFileEntity> entityList);

	/**
	 * 会計書類ファイルデータを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgDocFile();
	
}
