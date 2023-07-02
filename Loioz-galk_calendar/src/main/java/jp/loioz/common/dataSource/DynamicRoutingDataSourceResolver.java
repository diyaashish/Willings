package jp.loioz.common.dataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import jp.loioz.common.constant.CommonConstant;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.SessionUtils;
import jp.loioz.config.DatabaseConfig;
import lombok.Setter;

/**
 * DBの向き先を決定するクラス
 */
@Setter
public class DynamicRoutingDataSourceResolver extends AbstractRoutingDataSource {

	/** DBサーバーURL */
	private String serverUrl;

	/** DBサーバーのURLパラメータ */
	private String urlParameters;

	/** 管理DB名 */
	private String mgtDbName;

	/** テナントDB名（末尾に付与されるテナント連番は含まない） */
	private String tenantDbNameExcludedTnSeq;

	/** テナントDBユーザー名（末尾に付与されるテナント連番は含まない） */
	private String tenantUserNameExcludedTnSeq;

	/** テナントDBユーザーパスワード（末尾に付与されるテナント連番は含まない） */
	private String tenantUserPassExcludedTnSeq;

	/**
	 * DBの向き先を決定するメソッド。<br>
	 * コネクションが取得される際に実行される。
	 */
	@Override
	protected Object determineCurrentLookupKey() {

		String dataSourceName;
		Long tenantSeq = null;

		if (SessionUtils.isAlreadyLoggedUser()) {
			// ユーザーアカウントでログインしている場合

			// アカウントが所属しているテナント連番を取得
			tenantSeq = SessionUtils.getTenantSeq();
			dataSourceName = this.tenantDbNameExcludedTnSeq + tenantSeq;

		} else {
			// ログインしていない、またはシステム管理者アカウントでログインしている場合

			// スレッドからテナント連番を取得
			tenantSeq = SchemaContextHolder.getTenantSeq();

			if (tenantSeq == null) {
				// スレッドにテナント連番が保持されていない場合
				dataSourceName = this.mgtDbName;

			} else {
				dataSourceName = this.tenantDbNameExcludedTnSeq + tenantSeq;
			}
		}

		if (tenantSeq != null && !tenantSeq.equals(CommonConstant.ADMIN_TENANT_SEQ)) {
			// システム管理者用のテナント番号以外の場合はデータソースの存在チェックを行い、存在しない場合はデータソースの追加を行う

			this.checkExistAndPutDataSource(dataSourceName, tenantSeq);
		}

		return dataSourceName;
	}

	/**
	 * 対象のデータソースが存在しているかを確認し、存在しない場合はデータソースを追加する
	 *
	 * @param dataSourceName
	 * @param tenantSeq
	 */
	private void checkExistAndPutDataSource(String dataSourceName, Long tenantSeq) {

		// データソースの存在チェック
		DatabaseConfig databaseConfig = new DatabaseConfig();
		boolean isExistDataSource = databaseConfig.isExistDataSource(dataSourceName);

		if (!isExistDataSource) {
			// データソースが存在しない場合
			// ※APサーバーが複数ある環境では、新規に登録されたテナントのデータソースが、登録処理を行ったサーバー以外には存在しない状況となるため下記処理が必要となる
			try {
				// データソースを追加する
				databaseConfig.putDataSource(tenantSeq, this.serverUrl, this.tenantDbNameExcludedTnSeq, this.urlParameters,
						this.tenantUserNameExcludedTnSeq, this.tenantUserPassExcludedTnSeq);
			} catch (Exception e) {
				Logger log = new Logger();
				log.error("DataSource put error: " + e.getMessage());
			}
		}
	}
}
