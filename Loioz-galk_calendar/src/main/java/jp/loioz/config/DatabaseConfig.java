package jp.loioz.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SimpleDataSource;
import org.seasar.doma.jdbc.UnknownColumnHandler;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.jdbc.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import jp.loioz.common.dataSource.DynamicRoutingDataSourceResolver;
import jp.loioz.common.log.Logger;
import jp.loioz.common.utility.StringUtils;

/**
 * データベースの接続設定
 */
@Configuration
public class DatabaseConfig implements Config {

	/** データソース */
	private static Map<Object, Object> dataSources = new HashMap<>();
	private static DynamicRoutingDataSourceResolver routingDataSource = new DynamicRoutingDataSourceResolver();

	private static final Dialect dialect = new MysqlDialect();

	// application.propertiesの値を取得
	/** DBサーバーURL */
	@Value("${spring.datasource.server-url}")
	private String serverUrl;
	/** DBサーバーのURLパラメータ */
	@Value("${spring.datasource.url-parameters}")
	private String urlParameters;
	/** DB接続ユーザー */
	@Value("${spring.datasource.username}")
	private String username;
	/** DB接続ユーザーのパスワード */
	@Value("${spring.datasource.password}")
	private String password;
	/** 管理DB名 */
	@Value("${db.name.mgt-db}")
	private String mgtDbName;
	/** テナントDB名（末尾に付与されるテナント連番は含まない） */
	@Value("${db.name.tenant-db-excluded-tn-seq}")
	private String tenantDbNameExcludedTnSeq;
	/** テナントDBユーザー名（末尾に付与されるテナント連番は含まない） */
	@Value("${db.user.tenant-user-name-excluded-tn-seq}")
	private String tenantUserNameExcludedTnSeq;
	/** テナントDBユーザーパスワード（末尾に付与されるテナント連番は含まない） */
	@Value("${db.user.tenant-user-pass-excluded-tn-seq}")
	private String tenantUserPassExcludedTnSeq;

	/** ロガー */
	@Autowired
	private Logger log;

	/**
	 * データソースの取得
	 */
	@Override
	@Bean
	public DataSource getDataSource() {

		// DB情報を設定
		routingDataSource.setServerUrl(this.serverUrl);
		routingDataSource.setUrlParameters(this.urlParameters);
		routingDataSource.setMgtDbName(this.mgtDbName);
		routingDataSource.setTenantDbNameExcludedTnSeq(this.tenantDbNameExcludedTnSeq);
		routingDataSource.setTenantUserNameExcludedTnSeq(this.tenantUserNameExcludedTnSeq);
		routingDataSource.setTenantUserPassExcludedTnSeq(this.tenantUserPassExcludedTnSeq);

		// 管理DBのデータソースを追加
		SimpleDataSource mgtDataSource = new SimpleDataSource();
		mgtDataSource.setUrl(this.serverUrl + this.mgtDbName + this.urlParameters);
		mgtDataSource.setUser(this.username);
		mgtDataSource.setPassword(this.password);
		dataSources.put(this.mgtDbName, mgtDataSource);

		try {
			// 管理DBのコネクションを取得
			Connection mgtConn = mgtDataSource.getConnection();

			// Statementの作成
			Statement stmt = mgtConn.createStatement();

			// sqlの組み立て(全ての企業情報の取得SQL)
			StringBuilder sb = new StringBuilder();
			sb.append("select * from m_tenant_mgt where sys_deleted_at IS NULL and sys_deleted_by IS NULL");

			// Resultsetの作成
			ResultSet rset = stmt.executeQuery(sb.toString());

			while (rset.next()) {

				Long tenantSeq = rset.getLong("tenant_seq");

				// テナントDBのデータソースを追加
				SimpleDataSource tenantDataSource = new SimpleDataSource();
				tenantDataSource.setUrl(this.serverUrl + this.tenantDbNameExcludedTnSeq + tenantSeq + this.urlParameters);
				tenantDataSource.setUser(this.tenantUserNameExcludedTnSeq + tenantSeq);
				tenantDataSource.setPassword(this.tenantUserPassExcludedTnSeq + tenantSeq);
				dataSources.put(this.tenantDbNameExcludedTnSeq + tenantSeq, tenantDataSource);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		routingDataSource.setTargetDataSources(dataSources);
		routingDataSource.setDefaultTargetDataSource(mgtDataSource);
		routingDataSource.afterPropertiesSet();

		return new TransactionAwareDataSourceProxy(routingDataSource);
	}

	@Override
	public Dialect getDialect() {
		return dialect;
	}

	/**
	 * テナント連番に対応したデータソースを追加する
	 *
	 * @param tenantSeq テナント連番
	 * @throws Exception
	 */
	public void putDataSource(Long tenantSeq) throws Exception {
		if (tenantSeq == null) {
			throw new Exception();
		}

		// データソースを追加する
		this.putDataSource(tenantSeq, this.serverUrl, this.tenantDbNameExcludedTnSeq, this.urlParameters, this.tenantUserNameExcludedTnSeq,
				this.tenantUserPassExcludedTnSeq);
	}

	/**
	 * テナント連番に対応したデータソースを追加する
	 *
	 * @param tenantSeq
	 * @param serverUrl
	 * @param tenantDbNameExcludedTnSeq
	 * @param urlParameters
	 * @param tenantUserNameExcludedTnSeq
	 * @param tenantUserPassExcludedTnSeq
	 * @throws Exception
	 */
	public void putDataSource(Long tenantSeq, String serverUrl, String tenantDbNameExcludedTnSeq, String urlParameters,
			String tenantUserNameExcludedTnSeq, String tenantUserPassExcludedTnSeq) throws Exception {

		if (tenantSeq == null || StringUtils.isEmpty(serverUrl) || StringUtils.isEmpty(tenantDbNameExcludedTnSeq)
				|| StringUtils.isEmpty(urlParameters) || StringUtils.isEmpty(tenantUserNameExcludedTnSeq)
				|| StringUtils.isEmpty(tenantUserPassExcludedTnSeq)) {
			throw new Exception("Parameter is null");
		}

		// テナントDBのデータソースを追加
		SimpleDataSource tenantDataSource = new SimpleDataSource();
		tenantDataSource.setUrl(serverUrl + tenantDbNameExcludedTnSeq + tenantSeq + urlParameters);
		tenantDataSource.setUser(tenantUserNameExcludedTnSeq + tenantSeq);
		tenantDataSource.setPassword(tenantUserPassExcludedTnSeq + tenantSeq);
		dataSources.put(tenantDbNameExcludedTnSeq + tenantSeq, tenantDataSource);

		routingDataSource.setTargetDataSources(dataSources);

		// データソースを更新
		routingDataSource.afterPropertiesSet();
	}

	/**
	 * 引数で渡されたキーのデータソースが存在するかどうかを判定する
	 *
	 * @param dataSourceKey
	 * @return true:存在する、false:存在しない
	 */
	public boolean isExistDataSource(String dataSourceKey) {

		return dataSources.containsKey(dataSourceKey);
	}

	@Override
	public UnknownColumnHandler getUnknownColumnHandler() {

		// DomaのMessage.DOMA2002から抜粋
		MessageFormat mf = new MessageFormat("未知のカラムを無視しました。: カラム[{0}]が結果セットに含まれますが、このカラムにマッピングされたプロパティがエンティティクラス[{1}]に見つかりません。");

		return new UnknownColumnHandler() {
			@Override
			public void handle(Query query, EntityType<?> entityType, String unknownColumnName) {
				String message = mf.format(new String[]{unknownColumnName, entityType.getEntityClass().getName()});
				log.warn(message);
			}
		};
	}
}
