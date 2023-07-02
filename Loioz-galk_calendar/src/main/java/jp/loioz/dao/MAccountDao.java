package jp.loioz.dao;

import java.util.Arrays;
import java.util.List;

import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import jp.loioz.app.user.officeAccountSetting.form.OfficeAccountSearchForm;
import jp.loioz.dto.AccountJoiningBushoDto;
import jp.loioz.entity.MAccountEntity;

/**
 * アカウント用のDaoクラス
 */
@ConfigAutowireable
@Dao
public interface MAccountDao {

	/**
	 * 全てのアカウント情報を取得する
	 *
	 * @return
	 */
	@Select
	List<MAccountEntity> selectAll();

	/**
	 * 1件のアカウント情報を取得する
	 *
	 * @param accountSeq
	 * @return
	 */
	default MAccountEntity selectBySeq(Long accountSeq) {
		return selectBySeq(Arrays.asList(accountSeq)).stream().findFirst().orElse(null);
	}

	/**
	 * アカウントIDを指定してアカウント情報を取得する
	 *
	 * @param accountSeqList
	 * @return
	 */
	@Select
	List<MAccountEntity> selectBySeq(List<Long> accountSeqList);

	/**
	 * アカウント一覧検索
	 *
	 * @param searchForm
	 * @param options
	 * @return
	 */
	@Select
	List<MAccountEntity> selectConditions(OfficeAccountSearchForm searchForm, SelectOptions options);

	/**
	 * アカウント連番で取得する
	 *
	 * @param accountSeqList アカウント連番
	 * @return アカウント情報
	 */
	@Select
	List<MAccountEntity> selectAccountByAccountSeqList(List<Long> accountSeqList);

	/**
	 * アカウント連番で取得するソート順はリスト順
	 *
	 * @param accountSeqList アカウント連番
	 * @return アカウント情報
	 */
	@Select
	List<MAccountEntity> selectAccountByAccountSeqListOrderBySeqList(List<Long> accountSeqList, String orderByCondition);

	/**
	 * １件のアカウント情報をアカウントIDで取得する
	 *
	 * @param accountId アカウントID
	 * @return アカウント情報
	 */
	@Select
	MAccountEntity selectById(String accountId);

	/**
	 * アカウントIDを取得(重複チェック)
	 *
	 * @param accountId
	 * @param accountSeq
	 * @return
	 */
	@Select
	List<MAccountEntity> selectByIdExists(String accountId, Long accountSeq);

	/**
	 * 有効なアカウント情報をアカウントIDで取得する
	 *
	 * @param accountId アカウントID
	 * @return アカウント情報
	 */
	@Select
	MAccountEntity selectEnabledAccountById(String accountId);

	/**
	 * 有効な全てのアカウント情報を取得する
	 *
	 * @return アカウント情報
	 */
	@Select
	List<MAccountEntity> selectEnabledAccount();

	/**
	 * 有効なアカウント情報をアカウント連番で取得する
	 *
	 * @param accountSeqList アカウント連番
	 * @return アカウント情報
	 */
	@Select
	List<MAccountEntity> selectEnabledAccountByAccountSeq(List<Long> accountSeqList);

	/**
	 * 有効なアカウント情報をアカウント連番で取得する
	 *
	 * @param accountSeq アカウント連番
	 * @return アカウント情報
	 */
	default MAccountEntity selectEnabledAccountByAccountSeq(Long accountSeq) {
		List<MAccountEntity> result = selectEnabledAccountByAccountSeq(Arrays.asList(accountSeq));
		return result.stream().findFirst().orElse(null);
	}

	/**
	 * 有効なアカウント情報を指定したアカウント種別ごとに取得する
	 *
	 * @param accountType
	 * @return アカウント情報
	 */
	@Select
	List<MAccountEntity> selectEnabledAccountByAccountType(String accountType);

	/**
	 * 有効なアカウント情報について指定したアカウント権限のデータを取得する
	 *
	 * @param accountKengen
	 * @return アカウント権限
	 */
	@Select
	List<MAccountEntity> selectEnabledAccountByAccountKengen(String accountKengen);

	/**
	 * １件のアカウントを登録する
	 *
	 * @param entity
	 * @return 登録したデータ件数
	 */
	@Insert
	int insert(MAccountEntity entity);

	/**
	 * １件のアカウントを更新する
	 *
	 * @param entity
	 * @return 更新したデータ件数
	 */
	@Update
	int update(MAccountEntity entity);

	/**
	 * 複数のアカウントを更新する
	 * 
	 * @param entityList
	 * @return
	 */
	@BatchUpdate
	int[] updateList(List<MAccountEntity> entityList);

	/**
	 * 有効なアカウント情報とそのアカウントが所属している部署ID
	 *
	 * <pre>
	 * 部署IDはGROUP_CONCATで取得
	 * </pre>
	 *
	 * @return
	 */
	@Select
	List<AccountJoiningBushoDto> selectAccountAndJoiningBusho();

	/**
	 * アカウント情報とそのアカウントが所属している部署ID
	 *
	 * <pre>
	 * 部署IDはGROUP_CONCATで取得
	 * </pre>
	 *
	 * @return
	 */
	@Select
	List<AccountJoiningBushoDto> selectAccountAndJoiningBushoIncludeDeleted(List<Long> accountSeqList);

	/**
	 * ライセンスが有効なアカウントの数を取得する
	 * 
	 * @return
	 */
	@Select
	Long selectUsingLicenseAccountCount();

	/**
	 * テナントに登録されている「アカウント権限：システム管理者」のアカウント数を取得する
	 * 
	 * <pre>
	 * NULL考慮
	 * </pre>
	 * 
	 * @param accounSeq
	 * @return 件数
	 */
	@Select
	long selectHasSystemMngAccountCount(Long accountSeq);

	/**
	 * 検索ワードを含むアカウント名のアカウントSEQを取得する
	 *
	 * @param searchMailText
	 * @return アカウントSEQリスト
	 */
	@Select
	List<Long> selectAccountSeqListBySearchWord(String searchMailText);

}
