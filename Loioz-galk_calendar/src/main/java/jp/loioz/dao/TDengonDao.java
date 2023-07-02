package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import jp.loioz.bean.DengonDetailBean;
import jp.loioz.bean.DengonListBean;
import jp.loioz.dto.DengonEditDto;
import jp.loioz.entity.TDengonEntity;

/**
 * 伝言用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface TDengonDao {

	/**
	 * 指定の伝言Seqのデータ取得
	 *
	 * @return 伝言データ
	 */
	@Select
	TDengonEntity selectByDengonSeq(Long dengonSeq);

	/**
	 * 受信した伝言一覧を取得する
	 *
	 * @param loginAccountSeq
	 * @param page
	 * @return 受信した伝言一覧
	 */
	@Select
	List<DengonListBean> selectDengonReceiveList(Long loginAccountSeq, int page);

	/**
	 * 送信した伝言一覧を取得する
	 *
	 * @param loginAccountSeq
	 * @param page
	 * @return 送信した伝言一覧
	 */
	@Select
	List<DengonListBean> selectDengonSendList(Long loginAccountSeq, int page);

	/**
	 * フィルターでの伝言一覧
	 *
	 * @param loginAccountSeq
	 * @param page
	 * @param filterType
	 * @param searchMailText
	 * @param dengonFolderSeq
	 * @return
	 */
	@Select
	List<DengonListBean> selectDengonFilterList(Long loginAccountSeq, int page, String mailFilterType, String searchMailText, Long dengonFolderSeq);

	/**
	 * 下書きの伝言一覧を取得する
	 *
	 * @param loginAccountSeq
	 * @param page
	 * @return 下書きの伝言一覧
	 */
	@Select
	List<DengonListBean> selectDengonDraftList(Long loginAccountSeq, int page);

	/**
	 * ごみ箱の伝言一覧を取得する
	 *
	 * @param loginAccountSeq
	 * @param page
	 * @return ごみ箱の受信した伝言一覧
	 */
	@Select
	List<DengonListBean> selectDengonTrashedList(Long loginAccountSeq, int page);

	/**
	 * ごみ箱のメッセージ一覧（カスタムフォルダ）を取得する
	 *
	 * @param loginAccountSeq
	 * @param dengonFolderSeq
	 * @param page
	 * @return
	 */
	@Select
	List<DengonListBean> selectDengonFolderTrashedList(Long loginAccountSeq, Long dengonFolderSeq, int page);

	/**
	 * カスタムフォルダの伝言一覧を取得する
	 *
	 * @param loginAccountSeq
	 * @param page
	 * @param dengonFolderSeq
	 * @return カスタムフォルダの伝言一覧
	 */
	@Select
	List<DengonListBean> selectDengonFolderList(Long loginAccountSeq, int page, Long dengonFolderSeq);

	/**
	 * 検索ワードを含むメッセージ一覧を取得する
	 *
	 * @param loginAccountSeq
	 * @param page
	 * @param searchMailText
	 * @param mailBoxType
	 * @param dengonFolderSeq
	 * @param searchAccountSeqList
	 * @return
	 */
	@Select
	List<DengonListBean> selectDengonSearchList(Long loginAccountSeq, int page, String searchMailText, String mailBoxType, Long dengonFolderSeq,
			List<Long> searchAccountSeqList);

	/**
	 * 検索ワードを含むごみ箱のメッセージ一覧を取得する
	 *
	 * @param loginAccountSeq
	 * @param page
	 * @param searchMailText
	 * @return
	 */
	@Select
	List<DengonListBean> selectDengonSearchListWhereTrashed(Long loginAccountSeq, int page, String searchMailText, List<Long> searchAccountSeqList);

	/**
	 * 受信した伝言の伝言詳細データを取得する
	 *
	 * @param loginAccountSeq
	 * @param dengonSeq
	 * @return 伝言詳細データ
	 */
	@Select
	DengonDetailBean selectDengonDetailWhereReceive(Long loginAccountSeq, Long dengonSeq);

	/**
	 * 送信した伝言の伝言詳細データを取得する
	 *
	 * @param dengonSeq
	 * @return 伝言詳細データ
	 */
	@Select
	DengonDetailBean selectDengonDetailWhereSend(Long dengonSeq);

	/**
	 * 編集用の伝言詳細データを取得する
	 *
	 * @param loginAccountSeq
	 * @param dengonSeq
	 * @return 編集用の伝言詳細データ
	 */
	@Select
	DengonEditDto selectDengonEdit(Long loginAccountSeq, Long dengonSeq);

	/**
	 * 返信用の伝言詳細データを取得する
	 *
	 * @param dengonSeq
	 * @return 編集用の伝言詳細データ
	 */
	@Select
	DengonEditDto selectDengonReply(Long dengonSeq);

	/**
	 * 複数の伝言情報を取得する
	 *
	 * @param dengonSeqList
	 * @return 伝言情報リスト
	 */
	@Select
	List<TDengonEntity> selectListByDengonSeqList(List<Long> dengonSeqList);

	/**
	 * 下書きの件数を取得する
	 *
	 * @param loginAccountSeq
	 * @return 下書きの件数
	 */
	@Select
	int selectDraftCount(Long loginAccountSeq);

	/**
	 * 1件の伝言情報を登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(TDengonEntity entity);

	/**
	 * 1件の伝言情報を更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(TDengonEntity entity);

	/**
	 * 複数件の伝言情報を更新する
	 *
	 * @param entitys
	 * @return 更新したデータ件数
	 */
	@BatchUpdate
	int[] update(List<TDengonEntity> entitys);

	/**
	 * アカウントSEQをキーにして、伝言の未読件数を取得します。
	 *
	 * @param accountSeq
	 * @return アカウントSEQ未読件数
	 */
	@Select
	int selectUnreadCount(Long accountSeq);

	/**
	 * 受信BOX内の伝言の未読件数を取得します。
	 *
	 * @param loginAccountSeq
	 * @return 受信BOX内の伝言未読件数
	 */
	@Select
	int selectUnreadCountWhereRecieveBox(Long loginAccountSeq);

	/**
	 * 指定の業務履歴Seqのデータ取得
	 *
	 * @return 伝言データ
	 */
	@Select
	TDengonEntity selectByGyomuHistorySeq(Long gyomuHistorySeq);
}