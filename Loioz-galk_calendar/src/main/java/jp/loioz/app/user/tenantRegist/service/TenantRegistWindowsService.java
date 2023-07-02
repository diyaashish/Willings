package jp.loioz.app.user.tenantRegist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jp.loioz.common.exception.AppException;
import jp.loioz.config.DatabaseConfig;

/**
 * Windows環境でのアカウント情報詳細入力画面用のサービスクラス(開発用)
 */
@Service
@Profile("default")
public class TenantRegistWindowsService extends TenantRegistService {

	/** データベース設定 */
	@Autowired
	private DatabaseConfig databaseConfig;

	@Override
	public void createTenantDB(Long tenantSeq) throws AppException {
		/*
		 * ローカル環境で動作させる場合は、テナントDBはあらかじめ手動で作成しておく想定
		 *
		 * ・手順
		 * 1.m_tenant_mgtテーブルのAUTO_INCREMENT値を確認(または変更)して、次に発番される企業連番を確認
		 *   ALTER TABLE service_mgt.m_tenant_mgt AUTO_INCREMENT=99;
		 *
		 * 2.テナントDB作成スクリプトを上記企業連番で起動
		 *   sh /opt/benzo/scripts/tenant_create.sh 99
		 *
		 * 3.アカウント申込画面から通常通り申込操作を行う
		 */

		// テナントDBのデータソースを追加
		try {
			databaseConfig.putDataSource(tenantSeq);
		} catch (Exception e) {
		}
	}
}
