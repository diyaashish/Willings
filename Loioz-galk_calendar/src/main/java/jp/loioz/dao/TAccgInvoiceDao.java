package jp.loioz.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.bean.InvoiceDetailBean;
import jp.loioz.bean.InvoiceListBean;
import jp.loioz.domain.condition.InvoiceListSearchCondition;
import jp.loioz.domain.condition.InvoiceListSortCondition;
import jp.loioz.entity.TAccgInvoiceEntity;

/**
 * 請求書Daoクラス
 */
@ConfigAutowireable
@Dao
public interface TAccgInvoiceDao {

	/**
	 * 請求書データを取得します。<br>
	 * 
	 * @param invoiceSeq
	 * @return
	 */
	@Select
	TAccgInvoiceEntity selectInvoiceByInvoiceSeq(Long invoiceSeq);

	/**
	 * 請求書データを取得します。<br>
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	TAccgInvoiceEntity selectInvoiceByAccgDocSeq(Long accgDocSeq);

	/**
	 * 会計書類SEQをキーとして、請求書情報を取得する
	 * 
	 * @param accgDocSeqList
	 * @return
	 */
	@Select
	List<TAccgInvoiceEntity> selectInvoiceByAccgDocSeqList(List<Long> accgDocSeqList);

	/**
	 * 請求書画面の一覧データを１ページ分取得します。<br>
	 * FeeListBeanで定義してある担当弁護士、担当事務は取得しません。<br>
	 * TAnkenTantoDao.selectAnkenTantoLaywerJimuByIdで取得してください。<br>
	 * 
	 * @param invoiceListSearchCondition
	 * @param invoiceListSortCondition
	 * @param options
	 * @return
	 */
	@Select
	List<InvoiceListBean> selectInvoiceListBySearchConditions(InvoiceListSearchCondition invoiceListSearchCondition,
			InvoiceListSortCondition invoiceListSortCondition, SelectOptions options);

	/**
	 * 請求書詳細の情報を取得します
	 * 
	 * @param accgDocSeq
	 * @return
	 */
	@Select
	InvoiceDetailBean selectInvoiceDetailByAccgDocSeq(Long accgDocSeq);

	/**
	 * 請求先名簿IDをキーとして、請求書情報を取得する
	 * 
	 * @param personId
	 * @return
	 */
	@Select
	List<TAccgInvoiceEntity> selectInvoiceByBillToPersonId(Long personId);

	/**
	 * 請求先名簿IDをキーとして、請求書情報の件数を取得する
	 * 
	 * @param personId
	 * @return
	 */
	default int selectInvoiceCountByBillToPersonId(Long personId) {
		return selectInvoiceByBillToPersonId(personId).size();
	}

	/**
	 * 登録
	 * 
	 * @param entity
	 * @return 登録件数
	 */
	@Insert
	int insert(TAccgInvoiceEntity entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @return 更新件数
	 */
	@Update
	int update(TAccgInvoiceEntity entity);

	/**
	 * 削除
	 * 
	 * @param entity
	 * @return 削除件数
	 */
	@Delete
	int delete(TAccgInvoiceEntity entity);

	/**
	 * 請求書データを全て削除します。<br>
	 * ※全件削除をするため特別にDeleteのSQLファイルを作成しています。<br>
	 * ※この処理を真似して通常レコードを削除するdelete処理を作成しないでください。<br>
	 * 
	 * @return
	 */
	@Delete(sqlFile = true)
	int deleteAllTAccgInvoice();
	
}
