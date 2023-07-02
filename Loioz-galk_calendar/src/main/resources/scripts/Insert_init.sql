
/* クリア */
TRUNCATE m_saibansho;
TRUNCATE m_saibansho_bu;
TRUNCATE m_select_list;
TRUNCATE m_nyushukkin_komoku;
TRUNCATE m_sosakikan;
TRUNCATE m_file_management_display_priority;

/* オートインクリメント初期化 */
ALTER TABLE m_saibansho AUTO_INCREMENT = 1;
ALTER TABLE m_saibansho_bu AUTO_INCREMENT = 1;
ALTER TABLE m_select_list AUTO_INCREMENT = 1;
ALTER TABLE m_nyushukkin_komoku AUTO_INCREMENT = 1;
ALTER TABLE m_sosakikan AUTO_INCREMENT = 1;
ALTER TABLE m_file_management_display_priority AUTO_INCREMENT = 1;


/* 裁判所テーブル（m_saibansho） */
INSERT INTO m_saibansho(saibansho_id, todofuken_id, saibansho_zip, saibansho_address1, saibansho_address2, saibansho_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version_no)
SELECT
 saibansho_mgt_id, todofuken_id, saibansho_zip, saibansho_address1, saibansho_address2, saibansho_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version_no
FROM service_mgt.m_saibansho_mgt;


/* 裁判所部テーブル（m_saibansho_bu） */
INSERT INTO m_saibansho_bu(keizoku_bu_id,saibansho_id,keizoku_bu_name,keizoku_bu_tel_no,keizoku_bu_fax_no,created_at,created_by,updated_at,updated_by,deleted_at,deleted_by,version_no)
SELECT
 keizoku_bu_mgt_id,saibansho_mgt_id,keizoku_bu_name,keizoku_bu_tel_no,keizoku_bu_fax_no,created_at,created_by,updated_at,updated_by,deleted_at,deleted_by,version_no
FROM service_mgt.m_saibansho_bu_mgt;


/* 捜査機関テーブル（m_sosakikan） */
INSERT INTO m_sosakikan(sosakikan_id, sosakikan_type, todofuken_id, sosakikan_zip, sosakikan_address1, sosakikan_address2, sosakikan_name, sosakikan_tel_no, sosakikan_fax_no, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version_no)
SELECT
 sosakikan_mgt_id, sosakikan_type, todofuken_id, sosakikan_zip, sosakikan_address1, sosakikan_address2, sosakikan_name, sosakikan_tel_no, sosakikan_fax_no, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version_no
FROM service_mgt.m_sosakikan_mgt;

/* 分野テーブル（m_bunya） */
INSERT INTO m_bunya(bunya_id,bunya_type,bunya_name,disp_order,disabled_flg,created_at,created_by,updated_at,updated_by,deleted_at,deleted_by,version_no) VALUES
(1,'0','労働',1,'0',now(),1,now(),1,NULL,NULL,1),
(5,'0','交通事故',2,'0',now(),1,now(),1,NULL,NULL,1),
(10,'0','不動産',3,'0',now(),1,now(),1,NULL,NULL,1),
(3,'0','債務整理',4,'0',now(),1,now(),1,NULL,NULL,1),
(4,'0','破産・再生',5,'0',now(),1,now(),1,NULL,NULL,1),
(11,'0','医療事故',6,'0',now(),1,now(),1,NULL,NULL,1),
(9,'0','その他民事',7,'0',now(),1,now(),1,NULL,NULL,1),
(7,'0','離婚',8,'0',now(),1,now(),1,NULL,NULL,1),
(2,'0','相続',9,'0',now(),1,now(),1,NULL,NULL,1),
(12,'0','後見等',10,'0',now(),1,now(),1,NULL,NULL,1),
(13,'0','その他家事',11,'0',now(),1,now(),1,NULL,NULL,1),
(99,'0','その他',12,'0',now(),1,now(),1,NULL,NULL,1),
(6,'1','刑事',13,'0',now(),1,now(),1,NULL,NULL,1);

/* 選択肢テーブル（m_select_list） */
INSERT INTO m_select_list(select_type,select_val,disp_order,created_at,created_by,updated_at,updated_by,deleted_at,deleted_by,version_no) VALUES
('1', '電話', 1, now(), 1, now(), 1, NULL, NULL, 1),
('1', 'メール', 2, now(), 1, now(), 1, NULL, NULL, 1),
('1', '紹介', 3, now(), 1, now(), 1, NULL, NULL, 1),
('1', '相談会', 4, now(), 1, now(), 1, NULL, NULL, 1),
('1', '広告', 5, now(), 1, now(), 1, NULL, NULL, 1),
('1', 'その他', 6, now(), 1, now(), 1, NULL, NULL, 1);

/* 【旧会計機能のテーブル】 入出金項目テーブル削除不可の情報（m_nyushukkin_komoku） ※新規のテナントでは利用しないが、念のため初期データは登録しておく */
INSERT INTO m_nyushukkin_komoku(nyushukkin_komoku_id, nyushukkin_type, komoku_name, tax_flg, disp_order, disabled_flg, undeletable_flg, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version_no) VALUES
(1, '1', '入金（請求）', '0', 0, '0', '1', now(), 1, now(), 1, NULL, NULL, 1),
(2, '1', '他案件から振替', '0', 0, '0', '1', now(), 1, now(), 1, NULL, NULL, 1),
(3, '1', '回収不能', '0', 0, '0', '1', now(), 1, now(), 1, NULL, NULL, 1),
(4, '2', '出金(返金)', '0', 0, '0', '1', now(), 1, now(), 1, NULL, NULL, 1),
(5, '2', '他案件へ振替', '0', 0, '0', '1', now(), 1, now(), 1, NULL, NULL, 1);

/* 【旧会計機能のテーブル】 入出金項目テーブル（m_nyushukkin_komoku） ※新規のテナントでは利用しないが、念のため初期データは登録しておく */
INSERT INTO m_nyushukkin_komoku(nyushukkin_type, komoku_name, tax_flg, disp_order, disabled_flg, undeletable_flg, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version_no) VALUES
('1', '入金', '0',1, '0', '0', now(), 1, now(), 1, NULL, NULL, 1),
('2', '郵券', '1', 2, '0', '0', now(), 1, now(), 1, NULL, NULL, 1),
('2', '印紙', '1', 3, '0', '0', now(), 1, now(), 1, NULL, NULL, 1),
('2', '交通費', '1', 4, '0', '0', now(), 1, now(), 1, NULL, NULL, 1),
('2', '小為替', '1', 5, '0', '0', now(), 1, now(), 1, NULL, NULL, 1),
('2', '手数料', '1', 6, '0', '0', now(), 1, now(), 1, NULL, NULL, 1),
('2', '送金', '1', 7, '0', '0', now(), 1, now(), 1, NULL, NULL, 1),
('2', 'その他(課税)', '1', 8, '0', '0', now(), 1, now(), 1, NULL, NULL, 1),
('2', 'その他(非課税)', '0', 9, '0', '0', now(), 1, now(), 1, NULL, NULL, 1);

/* ファイル管理表示優先順位マスタテーブル（m_file_management_display_priority） */
INSERT INTO m_file_management_display_priority(file_management_display_priority_id, display_priority, remarks, created_at, created_by, updated_at, updated_by, version_no) VALUES
(1, 1, '顧客ルートフォルダ群', now(), 1, now(), 1, 1),
(2, 2, '案件ルートフォルダ群', now(), 1, now(), 1, 1),
(3, 3, '裁判ルートフォルダ群', now(), 1, now(), 1, 1),
(4, 4, '新規フォルダ群', now(), 1, now(), 1, 1),
(5, 5, 'ファイル群', now(), 1, now(), 1, 1);

/* プラン別機能制限テーブル（m_plan_func_restrict） */
INSERT INTO m_plan_func_restrict(func_restrict_id, func_type, func_name, starter_restrict_flg, standard_restrict_flg, created_at, created_by, updated_at, updated_by, version_no) VALUES
('PF0001', 'view', 'メッセージ', '1', '0', now(), 1, now(), 1, 1),
('PF0002', 'view', '会計管理', '1', '0', now(), 1, now(), 1, 1);

/* 精算書設定テーブル（m_statement_setting） */
INSERT INTO m_statement_setting(statement_setting_seq, default_title, default_sub_text, subject_prefix, statement_no_y_fmt, statement_no_m_fmt, statement_no_d_fmt, statement_no_numbering_type, statement_no_zero_pad_flg, transaction_date_print_flg, refund_date_print_flg, tenant_stamp_print_flg, created_at, created_by, updated_at, updated_by, version_no) VALUES
(1, '精算書', '下記の通り、ご精算申し上げます。', '件名：', 1, 1, 1, 1, 0, 0, 0, 0, now(), 1, now(), 1, 1);

/* 請求書設定テーブル（m_invoice_setting） */
INSERT INTO m_invoice_setting(invoice_setting_seq, default_title, default_sub_text, subject_prefix, invoice_no_y_fmt, invoice_no_m_fmt, invoice_no_d_fmt, invoice_no_numbering_type, invoice_no_zero_pad_flg, transaction_date_print_flg, due_date_print_flg, tenant_stamp_print_flg, created_at, created_by, updated_at, updated_by, version_no) VALUES
(1, '請求書', '下記の通り、ご請求申し上げます。', '件名：', 1, 1, 1, 1, 0, 0, 0, 0, now(), 1, now(), 1, 1);

/* メール設定テーブル（m_mail_setting） */
INSERT INTO m_mail_setting(mail_setting_seq, download_day_count, download_view_password_enable_flg, created_at, created_by, updated_at, updated_by, version_no) VALUES
(1, 60, '1', now(), 1, now(), 1, 1);

/* メールテンプレートテーブル（m_mail_template） */
/* 請求書用 */
INSERT INTO m_mail_template(template_type, template_title, mail_cc, mail_bcc, mail_reply_to, subject, contents, default_use_flg, created_at, created_by, updated_at, updated_by, version_no) 
VALUES ('1', '請求書送付のご案内', NULL, NULL, NULL, '請求書送付のご案内', 
'請求書をお送りいたしますので、ご査収の程よろしくお願い申し上げます。

ご不明な点等ございましたら、お問い合わせください。
引き続き、宜しくお願い致します。'
, '0', now(), 1, now(), 1, 1);
/* 精算書用 */
INSERT INTO m_mail_template( template_type, template_title, mail_cc, mail_bcc, mail_reply_to, subject, contents, default_use_flg, created_at, created_by, updated_at, updated_by, version_no) 
VALUES ('2', '精算書送付のご案内', NULL, NULL, NULL, '精算書送付のご案内', 
'精算書をお送りいたしますので、ご査収の程よろしくお願い申し上げます。

ご不明な点等ございましたら、お問い合わせください。
引き続き、宜しくお願い致します。'
, '0', now(), 1, now(), 1, 1);

/* 報酬項目テーブル（m_fee_item） */
INSERT INTO m_fee_item(fee_item_name, disp_order, created_at, created_by, updated_at, updated_by, version_no) VALUES
('着手金', '1', now(), '1', now(), '1', '1'),
('追加着手金', '2', now(), '1', now(), '1', '1'),
('成功報酬', '3', now(), '1', now(), '1', '1'),
('手数料', '4', now(), '1', now(), '1', '1'),
('日当', '5', now(), '1', now(), '1', '1'),
('顧問報酬', '6', now(), '1', now(), '1', '1'),
('相談料', '7', now(), '1', now(), '1', '1');

/* 預り金項目テーブル（m_deposit_item） */
INSERT INTO m_deposit_item(deposit_type, deposit_item_name, remarks, disp_order, created_at, created_by, updated_at, updated_by, version_no) VALUES
('1', '預り金', NULL, 1, now(), 1, now(), 1, 1),
('1', '解決金', NULL, 1, now(), 1, now(), 1, 1),
('2', '郵券代', NULL, 1, now(), 1, now(), 1, 1),
('2', '印紙代', NULL, 1, now(), 1, now(), 1, 1),
('2', '交通費', NULL, 1, now(), 1, now(), 1, 1),
('2', '通信費', NULL, 1, now(), 1, now(), 1, 1),
('2', '謄写料', NULL, 1, now(), 1, now(), 1, 1),
('2', '手数料', NULL, 1, now(), 1, now(), 1, 1),
('2', '小為替', NULL, 1, now(), 1, now(), 1, 1),
('2', '予納金', NULL, 1, now(), 1, now(), 1, 1),
('2', '供託金', NULL, 1, now(), 1, now(), 1, 1),
('2', '保証金', NULL, 1, now(), 1, now(), 1, 1);

/* テナント機能設定テーブル（m_tenant_func_setting） */
INSERT INTO m_tenant_func_setting(func_setting_id, func_setting_name, func_setting_value, created_at, created_by, updated_at, updated_by, version_no) VALUES
('TF0001', '旧会計機能のON、OFF', '1', now(), 1, now(), 1, 1);

/* ロイオズ管理者制御テーブル（m_loioz_admin_control） */
INSERT INTO m_loioz_admin_control(admin_control_id, admin_control_name, admin_control_value, admin_control_memo, created_at, created_by, updated_at, updated_by, version_no) VALUES
('AC0001', '旧会計機能の利用を許可', '0', '旧会計の利用を許可する場合は1を設定。', now(), 1, now(), 1, 1);
