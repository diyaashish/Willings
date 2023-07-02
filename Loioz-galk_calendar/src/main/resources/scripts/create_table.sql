SET SESSION FOREIGN_KEY_CHECKS=0;


/* Drop Tables */

DROP TABLE IF EXISTS m_account_img;
DROP TABLE IF EXISTS m_account_setting;
DROP TABLE IF EXISTS t_info_read_history;
DROP TABLE IF EXISTS m_account;
DROP TABLE IF EXISTS m_bunya;
DROP TABLE IF EXISTS t_busho_shozoku_acct;
DROP TABLE IF EXISTS m_busho;
DROP TABLE IF EXISTS m_deposit_item;
DROP TABLE IF EXISTS m_fee_item;
DROP TABLE IF EXISTS m_file_management_display_priority;
DROP TABLE IF EXISTS t_group_shozoku_acct;
DROP TABLE IF EXISTS m_group;
DROP TABLE IF EXISTS m_invoice_setting;
DROP TABLE IF EXISTS m_loioz_admin_control;
DROP TABLE IF EXISTS m_mail_setting;
DROP TABLE IF EXISTS m_mail_template;
DROP TABLE IF EXISTS m_nyushukkin_komoku;
DROP TABLE IF EXISTS m_plan_func_restrict;
DROP TABLE IF EXISTS m_room;
DROP TABLE IF EXISTS m_saibansho_bu;
DROP TABLE IF EXISTS m_saibansho;
DROP TABLE IF EXISTS m_select_list;
DROP TABLE IF EXISTS m_sosakikan;
DROP TABLE IF EXISTS m_statement_setting;
DROP TABLE IF EXISTS t_ginko_koza;
DROP TABLE IF EXISTS t_plan_history;
DROP TABLE IF EXISTS m_tenant;
DROP TABLE IF EXISTS m_tenant_func_setting;
DROP TABLE IF EXISTS t_accg_doc_act_send_file;
DROP TABLE IF EXISTS t_accg_doc_download;
DROP TABLE IF EXISTS t_accg_doc_act_send;
DROP TABLE IF EXISTS t_accg_doc_act;
DROP TABLE IF EXISTS t_accg_doc_file_detail;
DROP TABLE IF EXISTS t_accg_doc_file;
DROP TABLE IF EXISTS t_accg_doc_invoice_deposit_t_deposit_recv_mapping;
DROP TABLE IF EXISTS t_accg_doc_invoice_deposit;
DROP TABLE IF EXISTS t_accg_doc_invoice_fee;
DROP TABLE IF EXISTS t_accg_doc_invoice_other;
DROP TABLE IF EXISTS t_accg_doc_invoice;
DROP TABLE IF EXISTS t_accg_doc_repay_t_deposit_recv_mapping;
DROP TABLE IF EXISTS t_accg_doc_repay;
DROP TABLE IF EXISTS t_accg_invoice_payment_plan;
DROP TABLE IF EXISTS t_accg_invoice_payment_plan_condition;
DROP TABLE IF EXISTS t_accg_invoice;
DROP TABLE IF EXISTS t_accg_invoice_tax;
DROP TABLE IF EXISTS t_accg_invoice_withholding;
DROP TABLE IF EXISTS t_accg_record_detail_over_payment;
DROP TABLE IF EXISTS t_accg_record_detail;
DROP TABLE IF EXISTS t_accg_record;
DROP TABLE IF EXISTS t_accg_statement;
DROP TABLE IF EXISTS t_accg_doc;
DROP TABLE IF EXISTS t_accg_doc_last_used_number;
DROP TABLE IF EXISTS t_account_verification;
DROP TABLE IF EXISTS t_advisor_contract_tanto;
DROP TABLE IF EXISTS t_advisor_contract;
DROP TABLE IF EXISTS t_anken_sosakikan;
DROP TABLE IF EXISTS t_anken_add_keiji;
DROP TABLE IF EXISTS t_anken_azukari_item;
DROP TABLE IF EXISTS t_anken_jiken;
DROP TABLE IF EXISTS t_anken_koryu;
DROP TABLE IF EXISTS t_anken_sekken;
DROP TABLE IF EXISTS t_keiji_anken_customer;
DROP TABLE IF EXISTS t_anken_customer;
DROP TABLE IF EXISTS t_anken_related_kanyosha;
DROP TABLE IF EXISTS t_anken_tanto;
DROP TABLE IF EXISTS t_anken;
DROP TABLE IF EXISTS t_auth_token;
DROP TABLE IF EXISTS t_connected_external_service;
DROP TABLE IF EXISTS t_dengon_account_status;
DROP TABLE IF EXISTS t_dengon_customer;
DROP TABLE IF EXISTS t_dengon_folder_in;
DROP TABLE IF EXISTS t_dengon;
DROP TABLE IF EXISTS t_dengon_folder;
DROP TABLE IF EXISTS t_deposit_recv;
DROP TABLE IF EXISTS t_fee_add_time_charge;
DROP TABLE IF EXISTS t_fee;
DROP TABLE IF EXISTS t_file_detail_info_management;
DROP TABLE IF EXISTS t_folder_permission_info_management;
DROP TABLE IF EXISTS t_file_configuration_management;
DROP TABLE IF EXISTS t_folder_box;
DROP TABLE IF EXISTS t_folder_dropbox;
DROP TABLE IF EXISTS t_folder_google;
DROP TABLE IF EXISTS t_gyomu_history_anken;
DROP TABLE IF EXISTS t_gyomu_history_customer;
DROP TABLE IF EXISTS t_gyomu_history;
DROP TABLE IF EXISTS t_invited_account_verification;
DROP TABLE IF EXISTS t_seisan_kiroku;
DROP TABLE IF EXISTS t_kaikei_kiroku;
DROP TABLE IF EXISTS t_kanyosha;
DROP TABLE IF EXISTS t_mail_send_history;
DROP TABLE IF EXISTS t_nyushukkin_yotei;
DROP TABLE IF EXISTS t_old_address;
DROP TABLE IF EXISTS t_payment_card;
DROP TABLE IF EXISTS t_person_add_hojin;
DROP TABLE IF EXISTS t_person_add_kojin;
DROP TABLE IF EXISTS t_person_add_lawyer;
DROP TABLE IF EXISTS t_person_contact;
DROP TABLE IF EXISTS t_person;
DROP TABLE IF EXISTS t_root_folder_box;
DROP TABLE IF EXISTS t_root_folder_dropbox;
DROP TABLE IF EXISTS t_root_folder_google;
DROP TABLE IF EXISTS t_root_folder_related_info_management;
DROP TABLE IF EXISTS t_saiban_add_keiji;
DROP TABLE IF EXISTS t_saiban_customer;
DROP TABLE IF EXISTS t_saiban_jiken;
DROP TABLE IF EXISTS t_saiban_limit_relation;
DROP TABLE IF EXISTS t_saiban_related_kanyosha;
DROP TABLE IF EXISTS t_saiban_saibankan;
DROP TABLE IF EXISTS t_saiban_tanto;
DROP TABLE IF EXISTS t_saiban;
DROP TABLE IF EXISTS t_saiban_limit;
DROP TABLE IF EXISTS t_saiban_tree;
DROP TABLE IF EXISTS t_sales_detail_tax;
DROP TABLE IF EXISTS t_sales_detail;
DROP TABLE IF EXISTS t_sales;
DROP TABLE IF EXISTS t_schedule_account;
DROP TABLE IF EXISTS t_schedule_jogai;
DROP TABLE IF EXISTS t_schedule;
DROP TABLE IF EXISTS t_task_check_item;
DROP TABLE IF EXISTS t_task_history;
DROP TABLE IF EXISTS t_task_worker;
DROP TABLE IF EXISTS t_task;
DROP TABLE IF EXISTS t_task_anken;
DROP TABLE IF EXISTS t_toiawase_attachment;
DROP TABLE IF EXISTS t_toiawase_detail;
DROP TABLE IF EXISTS t_toiawase;
DROP TABLE IF EXISTS t_uncollectible_detail;
DROP TABLE IF EXISTS t_uncollectible;




/* Create Tables */

-- アカウント
CREATE TABLE m_account
(
	account_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'アカウントSEQ',
	account_id varchar(50) NOT NULL COMMENT 'アカウントID',
	tenant_seq int unsigned NOT NULL COMMENT 'テナント連番',
	-- 暗号化
	password text NOT NULL COMMENT 'パスワード',
	account_name_sei varchar(24) NOT NULL COMMENT 'アカウント姓',
	account_name_sei_kana varchar(64) NOT NULL COMMENT 'アカウント姓（かな）',
	account_name_mei varchar(24) NOT NULL COMMENT 'アカウント名',
	account_name_mei_kana varchar(64) NOT NULL COMMENT 'アカウント名（かな）',
	-- 1:弁護士
	-- 2:パラリーガル
	-- 3:その他
	account_type varchar(1) NOT NULL COMMENT 'アカウント種別',
	-- 0：無効（ライセンスなし）
	-- 1：有効（ライセンスあり）
	account_status varchar(1) NOT NULL COMMENT 'アカウントステータス',
	account_mail_address varchar(256) COMMENT 'アカウントメールアドレス',
	-- １：一般
	-- ２：システム管理者
	account_kengen varchar(1) NOT NULL COMMENT 'アカウント権限',
	-- 1:オーナー
	-- 0:オーナー以外
	account_owner_flg varchar(1) NOT NULL COMMENT 'アカウントオーナフラグ',
	account_color varchar(7) COMMENT 'アカウント色',
	account_invoice_registration_no varchar(13) COMMENT '適格請求書発行事業者登録番号',
	account_lawyer_stamp_img_seq int unsigned COMMENT '弁護士職印画像SEQ',
	account_lawyer_stamp_print_flg varchar(1) COMMENT '帳票印影表示フラグ',
	account_mail_signature text COMMENT 'メール署名',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (account_seq)
) COMMENT = 'アカウント';


-- アカウント画像
CREATE TABLE m_account_img
(
	account_img_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'アカウント画像SEQ',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	img_contents mediumblob NOT NULL COMMENT '画像',
	-- Enum
	-- 1：弁護士職印
	img_type varchar(1) NOT NULL COMMENT '画像タイプ',
	img_extension varchar(64) NOT NULL COMMENT '拡張子',
	created_at datetime COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (account_img_seq)
) COMMENT = 'アカウント画像';


-- アカウント設定
CREATE TABLE m_account_setting
(
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	setting_type varchar(3) NOT NULL COMMENT '設定項目',
	setting_value varchar(100) NOT NULL COMMENT '設定値',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (account_seq, setting_type)
) COMMENT = 'アカウント設定';


-- 分野
CREATE TABLE m_bunya
(
	bunya_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '分野ID',
	bunya_type varchar(1) NOT NULL COMMENT '分野区分',
	bunya_name varchar(30) NOT NULL COMMENT '分野名',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	disabled_flg varchar(1) COMMENT '無効フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (bunya_id)
) COMMENT = '分野';


-- 部署
CREATE TABLE m_busho
(
	busho_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '部署ID',
	-- nullの場合は上位の部署
	parent_busho_id int unsigned COMMENT '親部署ID',
	busho_name varchar(128) NOT NULL COMMENT '部署名',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (busho_id)
) COMMENT = '部署';


-- 預り金項目マスタ
CREATE TABLE m_deposit_item
(
	deposit_item_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '預り金項目マスタSEQ',
	deposit_type varchar(1) NOT NULL COMMENT '入出金タイプ',
	deposit_item_name varchar(30) NOT NULL COMMENT '項目名',
	remarks varchar(100) COMMENT '備考',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (deposit_item_seq)
) COMMENT = '預り金項目マスタ';


-- 報酬項目マスタ
CREATE TABLE m_fee_item
(
	fee_item_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '報酬項目マスタSEQ',
	fee_item_name varchar(30) NOT NULL COMMENT '項目名',
	remarks varchar(100) COMMENT '備考',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (fee_item_seq)
) COMMENT = '報酬項目マスタ';


-- ファイル管理表示優先順位マスタ
CREATE TABLE m_file_management_display_priority
(
	file_management_display_priority_id int unsigned NOT NULL COMMENT 'ファイル管理表示優先順位ID',
	display_priority int unsigned NOT NULL COMMENT '表示優先順位',
	remarks varchar(300) COMMENT '備考',
	created_at datetime NOT NULL COMMENT '登録日',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime NOT NULL COMMENT '更新日',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (file_management_display_priority_id)
) COMMENT = 'ファイル管理表示優先順位マスタ';


-- グループ
CREATE TABLE m_group
(
	group_id int unsigned NOT NULL AUTO_INCREMENT COMMENT 'グループID',
	group_name varchar(128) NOT NULL COMMENT 'グループ名',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (group_id)
) COMMENT = 'グループ';


-- 請求書設定マスタ
CREATE TABLE m_invoice_setting
(
	invoice_setting_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '請求書設定SEQ',
	default_title varchar(20) NOT NULL COMMENT 'タイトル',
	default_sub_text varchar(20) COMMENT '挿入文',
	subject_prefix varchar(5) COMMENT '件名-プレフィックス',
	subject_suffix varchar(5) COMMENT '件名-サフィックス',
	invoice_no_prefix varchar(3) COMMENT '請求番号-接頭辞',
	-- Enum
	-- 1：指定なし
	-- 2：yyyy
	-- 3：yy
	-- 4：和暦
	invoice_no_y_fmt varchar(1) NOT NULL COMMENT '請求番号-年フォーマット',
	-- Enum
	-- 1：指定なし
	-- 2：mm
	invoice_no_m_fmt varchar(1) NOT NULL COMMENT '請求番号-月フォーマット',
	-- Enum
	-- 1：指定なし
	-- 2：dd
	invoice_no_d_fmt varchar(1) NOT NULL COMMENT '請求番号-日フォーマット',
	invoice_no_delimiter varchar(1) COMMENT '請求番号-区切り文字',
	-- Enum
	-- 1：連番
	-- 2：年ごとの連番
	-- 3：月ごとの連番
	-- 4：日ごとの連番
	invoice_no_numbering_type varchar(1) NOT NULL COMMENT '請求番号-連番タイプ',
	invoice_no_zero_pad_flg varchar(1) NOT NULL COMMENT '請求番号-連番ゼロ埋めフラグ',
	invoice_no_zero_pad_digits varchar(1) COMMENT '請求番号-連番ゼロ埋め桁数',
	transaction_date_print_flg varchar(1) NOT NULL COMMENT '取引日表示フラグ',
	due_date_print_flg varchar(1) NOT NULL COMMENT '振込期日表示フラグ',
	tenant_stamp_print_flg varchar(1) NOT NULL COMMENT '事務所印表示フラグ',
	default_remarks text COMMENT '備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (invoice_setting_seq)
) COMMENT = '請求書設定マスタ';


-- ロイオズ管理者制御
CREATE TABLE m_loioz_admin_control
(
	admin_control_id varchar(6) NOT NULL COMMENT '管理者制御ID',
	admin_control_name varchar(100) NOT NULL COMMENT '管理者制御名',
	admin_control_value varchar(1) NOT NULL COMMENT '設定値',
	admin_control_memo varchar(200) COMMENT 'メモ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (admin_control_id)
) COMMENT = 'ロイオズ管理者制御';


-- メール設定
CREATE TABLE m_mail_setting
(
	mail_setting_seq int unsigned NOT NULL COMMENT 'メール設定SEQ',
	download_day_count int unsigned NOT NULL COMMENT 'ダウンロード可能日数',
	download_view_password_enable_flg varchar(1) NOT NULL COMMENT 'ダウンロード画面パスワード有効フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (mail_setting_seq)
) COMMENT = 'メール設定';


-- メールテンプレートマスタ
CREATE TABLE m_mail_template
(
	mail_template_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'メールテンプレートSEQ',
	-- Enum
	-- １：請求書
	-- ２：精算書
	template_type varchar(1) NOT NULL COMMENT 'テンプレート種別',
	template_title varchar(30) COMMENT 'テンプレートタイトル',
	mail_cc varchar(2000) COMMENT 'CC',
	mail_bcc varchar(2000) COMMENT 'BCC',
	mail_reply_to varchar(256) COMMENT 'REPLY-TO',
	subject varchar(100) COMMENT '件名',
	contents text COMMENT '本文',
	default_use_flg varchar(1) NOT NULL COMMENT '既定利用フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (mail_template_seq)
) COMMENT = 'メールテンプレートマスタ';


-- 入出金項目
CREATE TABLE m_nyushukkin_komoku
(
	nyushukkin_komoku_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '入出金項目ID',
	-- 1:入金、2:出金
	nyushukkin_type varchar(1) NOT NULL COMMENT '入出金タイプ',
	komoku_name varchar(10) NOT NULL COMMENT '項目名',
	-- Enum
	-- 0：非課税
	-- 1：課税（内税）
	-- 2：課税（外税）
	tax_flg varchar(1) NOT NULL COMMENT '課税フラグ',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	-- 0：有効
	-- 1：無効
	disabled_flg varchar(1) NOT NULL COMMENT '無効フラグ',
	-- 0：削除可能
	-- 1：削除不可
	undeletable_flg varchar(1) NOT NULL COMMENT '削除不可フラグ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	deleted_at datetime DEFAULT NULL COMMENT '削除日',
	deleted_by int(10) unsigned DEFAULT NULL COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (nyushukkin_komoku_id)
) COMMENT = '入出金項目';


-- プラン別機能制限
CREATE TABLE m_plan_func_restrict
(
	plan_func_restrict_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'プラン別機能制限連番',
	func_restrict_id varchar(6) NOT NULL COMMENT '機能制限ID',
	func_type varchar(10) NOT NULL COMMENT '機能種別',
	func_name varchar(100) NOT NULL COMMENT '機能名',
	starter_restrict_flg varchar(1) NOT NULL COMMENT 'スターター制限フラグ',
	standard_restrict_flg varchar(1) NOT NULL COMMENT 'スタンダード制限フラグ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (plan_func_restrict_seq),
	UNIQUE (func_restrict_id)
) COMMENT = 'プラン別機能制限';


-- 会議室
CREATE TABLE m_room
(
	room_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '会議室ID',
	room_name varchar(64) NOT NULL COMMENT '会議室名',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (room_id)
) COMMENT = '会議室';


-- 裁判所
CREATE TABLE m_saibansho
(
	-- m_court.court_idに外部キー
	saibansho_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '裁判所ID',
	-- enum
	todofuken_id varchar(2) NOT NULL COMMENT '都道府県ID',
	saibansho_zip varchar(8) COMMENT '裁判所郵便番号',
	saibansho_address1 varchar(128) COMMENT '裁判所住所1',
	saibansho_address2 varchar(128) COMMENT '裁判所住所２',
	saibansho_name varchar(100) NOT NULL COMMENT '裁判所名',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saibansho_id)
) COMMENT = '裁判所';


-- 裁判所部
CREATE TABLE m_saibansho_bu
(
	keizoku_bu_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '係属部ID',
	-- m_court.court_idに外部キー
	saibansho_id int unsigned NOT NULL COMMENT '裁判所ID',
	keizoku_bu_name varchar(100) NOT NULL COMMENT '係属部名',
	keizoku_bu_tel_no varchar(20) COMMENT '電話番号',
	keizoku_bu_fax_no varchar(20) COMMENT 'FAX番号',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (keizoku_bu_id)
) COMMENT = '裁判所部';


-- 選択肢
CREATE TABLE m_select_list
(
	select_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '選択肢SEQ',
	-- 1：相談経路
	-- 2：追加情報
	select_type varchar(1) NOT NULL COMMENT '選択肢区分',
	select_val varchar(30) NOT NULL COMMENT '選択肢名',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (select_seq)
) COMMENT = '選択肢';


-- 捜査機関
CREATE TABLE m_sosakikan
(
	sosakikan_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '捜査機関ID',
	-- 01：警察署
	-- 02：拘置所
	-- 03：検察庁
	sosakikan_type varchar(2) NOT NULL COMMENT '捜査機関区分',
	-- enum
	todofuken_id varchar(2) NOT NULL COMMENT '都道府県ID',
	sosakikan_zip varchar(8) COMMENT '捜査機関郵便番号',
	sosakikan_address1 varchar(128) COMMENT '捜査機関住所1',
	sosakikan_address2 varchar(128) COMMENT '捜査機関住所2',
	sosakikan_name varchar(100) NOT NULL COMMENT '捜査機関名',
	sosakikan_tel_no varchar(20) COMMENT '電話番号',
	sosakikan_fax_no varchar(20) COMMENT 'FAX番号',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (sosakikan_id)
) COMMENT = '捜査機関';


-- 精算書設定マスタ
CREATE TABLE m_statement_setting
(
	statement_setting_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '精算書設定SEQ',
	default_title varchar(20) NOT NULL COMMENT 'タイトル',
	default_sub_text varchar(20) COMMENT '挿入文',
	subject_prefix varchar(5) COMMENT '件名-プレフィックス',
	subject_suffix varchar(5) COMMENT '件名-サフィックス',
	statement_no_prefix varchar(3) COMMENT '精算番号-接頭辞',
	-- Enum
	-- 1：指定なし
	-- 2：yyyy
	-- 3：yy
	-- 4：和暦
	statement_no_y_fmt varchar(1) NOT NULL COMMENT '精算番号-年フォーマット',
	-- Enum
	-- 1：指定なし
	-- 2：mm
	statement_no_m_fmt varchar(1) NOT NULL COMMENT '精算番号-月フォーマット',
	-- Enum
	-- 1：指定なし
	-- 2：dd
	statement_no_d_fmt varchar(1) NOT NULL COMMENT '精算番号-日フォーマット',
	statement_no_delimiter varchar(1) COMMENT '精算番号-区切り文字',
	-- Enum
	-- 1：連番
	-- 2：年ごとの連番
	-- 3：月ごとの連番
	-- 4：日ごとの連番
	statement_no_numbering_type varchar(1) NOT NULL COMMENT '精算番号-連番タイプ',
	statement_no_zero_pad_flg varchar(1) NOT NULL COMMENT '精算番号-連番ゼロ埋めフラグ',
	statement_no_zero_pad_digits varchar(1) COMMENT '精算番号-連番ゼロ埋め桁数',
	transaction_date_print_flg varchar(1) NOT NULL COMMENT '取引日表示フラグ',
	refund_date_print_flg varchar(1) NOT NULL COMMENT '返金期限表示フラグ',
	tenant_stamp_print_flg varchar(1) NOT NULL COMMENT '事務所印表示フラグ',
	default_remarks text COMMENT '備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (statement_setting_seq)
) COMMENT = '精算書設定マスタ';


-- テナント情報
CREATE TABLE m_tenant
(
	tenant_seq int unsigned NOT NULL COMMENT 'テナントSEQ',
	tenant_name varchar(64) COMMENT '事務所名',
	-- 2020年1月21日時点で初回登録時は不要となった
	tenant_name_kana varchar(128) COMMENT '事務所名（カナ）',
	-- １：個人
	-- ２：法人
	tenant_type varchar(1) NOT NULL COMMENT '個人・法人区分',
	tenant_zip_cd varchar(8) COMMENT '郵便番号',
	tenant_address1 varchar(256) COMMENT '住所１',
	-- 建物名、ビル名
	tenant_address2 varchar(128) COMMENT '住所２',
	-- ハイフン込み
	tenant_tel_no varchar(13) NOT NULL COMMENT '電話番号',
	-- ハイフン込み
	tenant_fax_no varchar(13) COMMENT 'FAX番号',
	tenant_invoice_registration_no varchar(13) COMMENT '適格請求書発行事業者登録番号',
	tenant_stamp_img mediumblob COMMENT '事務所印画像',
	tenant_stamp_img_extension varchar(64) COMMENT '事務所印画像拡張子',
	tenant_daihyo_name_sei varchar(24) COMMENT '代表姓',
	tenant_daihyo_name_sei_kana varchar(64) COMMENT '代表姓（かな）',
	tenant_daihyo_name_mei varchar(24) COMMENT '代表名',
	tenant_daihyo_name_mei_kana varchar(64) COMMENT '代表名（かな）',
	tenant_daihyo_mail_address varchar(256) COMMENT '代表者メールアドレス',
	-- 1：切り捨て
	-- 2：切り上げ
	-- 3：四捨五入
	tax_hasu_type varchar(1) NOT NULL COMMENT '消費税の端数処理の方法',
	hoshu_hasu_type varchar(1) NOT NULL COMMENT '報酬の端数処理の方法',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (tenant_seq)
) COMMENT = 'テナント情報';


-- テナント機能設定
CREATE TABLE m_tenant_func_setting
(
	func_setting_id varchar(6) NOT NULL COMMENT '機能設定ID',
	func_setting_name varchar(100) NOT NULL COMMENT '機能設定名',
	func_setting_value varchar(1) NOT NULL COMMENT '設定値',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (func_setting_id)
) COMMENT = 'テナント機能設定';


-- 会計書類
CREATE TABLE t_accg_doc
(
	accg_doc_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '会計書類SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	-- Enum
	-- 1:精算書
	-- 2:請求書
	accg_doc_type varchar(1) NOT NULL COMMENT 'ドキュメントタイプ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_seq)
) COMMENT = '会計書類';


-- 会計書類-対応
CREATE TABLE t_accg_doc_act
(
	accg_doc_act_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '会計書類対応SEQ',
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	-- Enum
	-- 1:新規作成
	-- 2:発行
	-- 3:送付
	act_type varchar(1) NOT NULL COMMENT '対応種別',
	act_by int unsigned COMMENT '対応アカウントID',
	act_at datetime COMMENT '対応日時',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_act_seq)
) COMMENT = '会計書類-対応';


-- 会計書類-対応-送付
CREATE TABLE t_accg_doc_act_send
(
	accg_doc_act_send_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '会計書類対応送付SEQ',
	accg_doc_act_seq int unsigned NOT NULL COMMENT '会計書類対応SEQ',
	-- Enum
	-- 1：Web共有
	-- 2：メール添付
	send_type varchar(1) NOT NULL COMMENT '送付種別',
	send_to varchar(256) NOT NULL COMMENT 'TO',
	send_cc varchar(2000) COMMENT 'CC',
	send_bcc varchar(2000) COMMENT 'BCC',
	reply_to varchar(256) NOT NULL COMMENT 'REPLY-TO',
	send_from_name varchar(100) NOT NULL COMMENT '送信元名',
	send_subject varchar(100) NOT NULL COMMENT '件名',
	send_body text NOT NULL COMMENT '本文',
	download_limit_date date COMMENT 'ダウンロード期限',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_act_send_seq)
) COMMENT = '会計書類-対応-送付';


-- 会計書類-対応-送付-ファイル
CREATE TABLE t_accg_doc_act_send_file
(
	accg_doc_act_send_file_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '会計書類対応送付ファイルSEQ',
	accg_doc_act_send_seq int unsigned NOT NULL COMMENT '会計書類対応送付SEQ',
	accg_doc_file_seq int unsigned NOT NULL COMMENT '会計書類ファイルSEQ',
	download_last_at datetime COMMENT '最終ダウンロード時間',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_act_send_file_seq)
) COMMENT = '会計書類-対応-送付-ファイル';


-- 会計書類-ダウンロード情報
CREATE TABLE t_accg_doc_download
(
	accg_doc_download_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '会計書類ダウンロード情報SEQ',
	accg_doc_act_send_seq int unsigned NOT NULL COMMENT '会計書類対応送付SEQ',
	download_view_url_key varchar(128) NOT NULL COMMENT 'ダウンロード画面URLキー',
	download_view_password text COMMENT 'ダウンロード画面パスワード',
	verification_token text NOT NULL COMMENT '検証トークン',
	tenant_name varchar(64) NOT NULL COMMENT '事務所名（差出人名）',
	send_to varchar(256) NOT NULL COMMENT '宛先メールアドレス',
	issue_date date NOT NULL COMMENT '発行日',
	download_limit_date date NOT NULL COMMENT 'ダウンロード期限',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_download_seq),
	UNIQUE (download_view_url_key)
) COMMENT = '会計書類-ダウンロード情報';


-- 会計書類ファイル
CREATE TABLE t_accg_doc_file
(
	accg_doc_file_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '会計書類ファイルSEQ',
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	-- Enum
	-- 1:請求書
	-- 2:精算書
	-- 3:実費明細書
	-- 4:支払計画書
	accg_doc_file_type varchar(1) NOT NULL COMMENT '会計書類ファイル種別',
	file_extension varchar(64) COMMENT '拡張子（PDF、PNG）',
	-- ファイルを再作成する場合：1
	recreate_standby_flg varchar(1) NOT NULL COMMENT '再作成実行待ちフラグ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_file_seq)
) COMMENT = '会計書類ファイル';


-- 会計書類ファイル-詳細
CREATE TABLE t_accg_doc_file_detail
(
	accg_doc_file_detail_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '会計書類ファイル詳細SEQ',
	accg_doc_file_seq int unsigned NOT NULL COMMENT '会計書類ファイルSEQ',
	file_branch_no int unsigned NOT NULL COMMENT 'ファイル枝番',
	s3_object_key varchar(1024) NOT NULL COMMENT 'S3オブジェクトキー',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_file_detail_seq)
) COMMENT = '会計書類ファイル-詳細';


-- 請求項目
CREATE TABLE t_accg_doc_invoice
(
	doc_invoice_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '請求項目SEQ',
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	doc_invoice_order int unsigned NOT NULL COMMENT '並び順',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (doc_invoice_seq)
) COMMENT = '請求項目';


-- 請求項目-預り金
CREATE TABLE t_accg_doc_invoice_deposit
(
	doc_invoice_deposit_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '請求預り金項目SEQ',
	doc_invoice_seq int unsigned NOT NULL COMMENT '請求項目SEQ',
	deposit_transaction_date date COMMENT '取引日',
	deposit_item_name varchar(30) COMMENT '項目名',
	deposit_amount decimal COMMENT '預り金金額',
	-- 1：預り金
	-- 2：実費
	invoice_deposit_type varchar(1) COMMENT '預り金タイプ',
	sum_text text COMMENT '摘要',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (doc_invoice_deposit_seq)
) COMMENT = '請求項目-預り金';


-- 請求項目-預り金（実費）_預り金テーブルマッピング
CREATE TABLE t_accg_doc_invoice_deposit_t_deposit_recv_mapping
(
	doc_invoice_deposit_seq int unsigned NOT NULL COMMENT '請求預り金項目SEQ',
	deposit_recv_seq int unsigned NOT NULL COMMENT '預り金SEQ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (doc_invoice_deposit_seq, deposit_recv_seq),
	UNIQUE (deposit_recv_seq)
) COMMENT = '請求項目-預り金（実費）_預り金テーブルマッピング';


-- 請求項目-報酬
CREATE TABLE t_accg_doc_invoice_fee
(
	doc_invoice_fee_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '請求報酬項目SEQ',
	doc_invoice_seq int unsigned NOT NULL COMMENT '請求項目SEQ',
	fee_seq int unsigned NOT NULL COMMENT '報酬SEQ',
	fee_transaction_date date COMMENT '取引日',
	sum_text text COMMENT '摘要',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (doc_invoice_fee_seq)
) COMMENT = '請求項目-報酬';


-- 請求項目-その他
CREATE TABLE t_accg_doc_invoice_other
(
	doc_invoice_other_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '請求その他項目SEQ',
	doc_invoice_seq int unsigned NOT NULL COMMENT '請求項目SEQ',
	other_transaction_date date COMMENT '取引日',
	-- 1：値引き
	-- 2：テキスト
	other_item_type varchar(1) NOT NULL COMMENT '種類',
	other_item_name varchar(30) COMMENT '項目名',
	other_amount decimal COMMENT '金額',
	-- 1：8%
	-- 2：10%
	-- 3：0%
	discount_tax_rate_type varchar(1) COMMENT '値引き消費税率',
	-- 0：源泉徴収しない
	-- 1：源泉徴収する
	discount_withholding_flg varchar(1) COMMENT '値引き源泉徴収フラグ',
	sum_text text COMMENT '摘要',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (doc_invoice_other_seq)
) COMMENT = '請求項目-その他';


-- 会計書類-最終採番番号
CREATE TABLE t_accg_doc_last_used_number
(
	-- Enum
	-- 1:請求書
	-- 2:精算書
	accg_doc_type varchar(1) NOT NULL COMMENT 'ドキュメントタイプ',
	-- Enum
	-- 
	-- 1:連番
	-- 2:年毎に連番
	-- 3:月毎に連番
	-- 4:日毎に連番
	numbering_type varchar(1) NOT NULL COMMENT '採番タイプ',
	numbering_last_no int unsigned NOT NULL COMMENT '最終採番番号',
	numbering_last_date date NOT NULL COMMENT '最終採番日',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_type, numbering_type)
) COMMENT = '会計書類-最終採番番号';


-- 既入金項目
CREATE TABLE t_accg_doc_repay
(
	doc_repay_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '既入金項目SEQ',
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	repay_transaction_date date COMMENT '取引日',
	repay_item_name varchar(30) COMMENT '項目名',
	repay_amount decimal COMMENT '既入金金額',
	sum_text text COMMENT '摘要',
	doc_repay_order int unsigned NOT NULL COMMENT '並び順',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (doc_repay_seq)
) COMMENT = '既入金項目';


-- 既入金項目_預り金テーブルマッピング
CREATE TABLE t_accg_doc_repay_t_deposit_recv_mapping
(
	doc_repay_seq int unsigned NOT NULL COMMENT '既入金項目SEQ',
	deposit_recv_seq int unsigned NOT NULL COMMENT '預り金SEQ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (doc_repay_seq, deposit_recv_seq),
	UNIQUE (deposit_recv_seq)
) COMMENT = '既入金項目_預り金テーブルマッピング';


-- 請求書
CREATE TABLE t_accg_invoice
(
	invoice_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '請求書SEQ',
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	sales_detail_seq int unsigned COMMENT '売上明細SEQ',
	uncollectible_detail_seq int unsigned COMMENT '回収不能金詳細SEQ',
	-- Enum
	-- 1:下書き
	-- 2:送付待ち
	-- 3:送付済み
	invoice_issue_status varchar(1) NOT NULL COMMENT '発行ステータス',
	-- Enum
	-- 1:取引作成待ち
	-- 2:入金待ち
	-- 3:入金済み
	-- 4:一部入金
	-- 5:過入金
	-- 6:回収不能
	invoice_payment_status varchar(1) NOT NULL COMMENT '入金ステータス',
	invoice_amount decimal NOT NULL COMMENT '請求額',
	-- Enum
	-- 1:一括
	-- 2:分割
	invoice_type varchar(1) COMMENT '請求方法',
	bill_to_person_id int unsigned COMMENT '請求先名簿ID',
	sales_date date COMMENT '売上日',
	sales_account_seq int unsigned COMMENT '売上計上先',
	deposit_detail_attach_flg varchar(1) NOT NULL COMMENT '実費明細添付フラグ',
	payment_plan_attach_flg varchar(1) NOT NULL COMMENT '支払計画添付フラグ',
	invoice_title varchar(20) COMMENT 'タイトル',
	invoice_date date COMMENT '日付',
	invoice_no varchar(21) COMMENT '請求番号',
	invoice_to_name varchar(256) COMMENT '請求先名称',
	-- Enum
	-- １：様
	-- ２：御中
	-- ３：なし
	invoice_to_name_end varchar(1) COMMENT '請求先敬称',
	invoice_to_detail text COMMENT '請求先詳細',
	invoice_from_tenant_name varchar(64) COMMENT '差出人事務所名',
	invoice_from_detail text COMMENT '差出人詳細',
	tenant_stamp_print_flg varchar(1) NOT NULL COMMENT '印影フラグ',
	invoice_sub_text varchar(20) COMMENT '挿入文',
	invoice_subject varchar(160) COMMENT '件名',
	due_date date COMMENT '支払期限',
	due_date_print_flg varchar(1) NOT NULL COMMENT '支払期限印字フラグ',
	tenant_bank_detail text COMMENT '振込先',
	invoice_remarks text COMMENT '備考',
	repay_transaction_date_print_flg varchar(1) NOT NULL COMMENT '既入金取引日印字フラグ',
	invoice_transaction_date_print_flg varchar(1) NOT NULL COMMENT '請求取引日印字フラグ',
	repay_sum_flg varchar(1) NOT NULL COMMENT '既入金項目合算フラグ（既入金）',
	expense_sum_flg varchar(1) NOT NULL COMMENT '実費項目合算フラグ（請求）',
	invoice_memo text COMMENT 'メモ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (invoice_seq)
) COMMENT = '請求書';


-- 支払計画
CREATE TABLE t_accg_invoice_payment_plan
(
	invoice_payment_plan_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '支払計画SEQ',
	invoice_seq int unsigned NOT NULL COMMENT '請求書SEQ',
	payment_schedule_date date COMMENT '支払予定日',
	payment_schedule_amount decimal unsigned COMMENT '支払予定金額',
	sum_text text COMMENT '摘要',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (invoice_payment_plan_seq)
) COMMENT = '支払計画';


-- 請求書-支払分割条件
CREATE TABLE t_accg_invoice_payment_plan_condition
(
	invoice_seq int unsigned NOT NULL COMMENT '請求書SEQ',
	month_payment_amount decimal unsigned NOT NULL COMMENT '月々の支払額',
	month_payment_dd varchar(2) COMMENT '月々の支払日（日のみ）',
	payment_start_date date COMMENT '支払開始日',
	-- Enum
	-- 1：初回
	-- 2：最終
	fractional_month_type varchar(1) COMMENT '端数金額の支払月種別',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (invoice_seq)
) COMMENT = '請求書-支払分割条件';


-- 請求項目-消費税
CREATE TABLE t_accg_invoice_tax
(
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	-- Enum
	-- 1：8%
	-- 2：10%
	tax_rate_type varchar(1) NOT NULL COMMENT '税率',
	-- 値引き前の金額
	taxable_amount decimal unsigned NOT NULL COMMENT '対象額',
	-- 値引き前の金額
	tax_amount decimal unsigned NOT NULL COMMENT '税額',
	discount_taxable_amount decimal unsigned COMMENT '値引き_対象額',
	discount_tax_amount decimal unsigned COMMENT '値引き_税額',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_seq, tax_rate_type)
) COMMENT = '請求項目-消費税';


-- 請求項目-源泉徴収
CREATE TABLE t_accg_invoice_withholding
(
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	source_withholding_amount decimal unsigned NOT NULL COMMENT '対象額',
	withholding_amount decimal unsigned NOT NULL COMMENT '源泉徴収額',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_doc_seq)
) COMMENT = '請求項目-源泉徴収';


-- 取引実績
CREATE TABLE t_accg_record
(
	accg_record_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '取引実績SEQ',
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	fee_amount_expect decimal COMMENT '報酬入金額【見込】',
	deposit_recv_amount_expect decimal COMMENT '預り金入金額【見込】',
	deposit_payment_amount_expect decimal COMMENT '預り金出金額【見込】',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_record_seq)
) COMMENT = '取引実績';


-- 取引実績明細
CREATE TABLE t_accg_record_detail
(
	accg_record_detail_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '取引実績明細SEQ',
	accg_record_seq int unsigned NOT NULL COMMENT '取引実績SEQ',
	record_date date COMMENT '決済日',
	-- Enum
	-- 
	-- 1：入金
	-- 2：出金
	-- 3：振替（預り金）
	-- 4：過入金返金
	record_type varchar(1) NOT NULL COMMENT '取引種別',
	record_separate_input_flg varchar(1) NOT NULL COMMENT '個別入力フラグ',
	record_amount decimal COMMENT '実績入金額',
	record_fee_amount decimal COMMENT '報酬入金額',
	record_deposit_recv_amount decimal COMMENT '預り金入金額',
	record_deposit_payment_amount decimal COMMENT '預り金出金額',
	remarks varchar(100) COMMENT '備考',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_record_detail_seq)
) COMMENT = '取引実績明細';


-- 取引実績明細-過入金
CREATE TABLE t_accg_record_detail_over_payment
(
	accg_record_detail_over_payment_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '取引実績明細過入金SEQ',
	accg_record_detail_seq int unsigned NOT NULL COMMENT '取引実績明細SEQ',
	over_payment_amount decimal unsigned NOT NULL COMMENT '過入金額',
	over_payment_refund_flg varchar(1) NOT NULL COMMENT '過入金返金フラグ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (accg_record_detail_over_payment_seq)
) COMMENT = '取引実績明細-過入金';


-- 精算書
CREATE TABLE t_accg_statement
(
	statement_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '精算書SEQ',
	accg_doc_seq int unsigned NOT NULL COMMENT '会計書類SEQ',
	sales_detail_seq int unsigned COMMENT '売上明細SEQ',
	-- Enum
	-- 1:下書き
	-- 2:送付待ち
	-- 3:送付済み
	statement_issue_status varchar(1) NOT NULL COMMENT '発行ステータス',
	-- Enum
	-- 1:取引作成待ち
	-- 2:精算待ち
	-- 3:精算済み
	statement_refund_status varchar(1) NOT NULL COMMENT '返金ステータス',
	statement_amount decimal NOT NULL COMMENT '精算額',
	refund_to_person_id int unsigned COMMENT '返金先名簿ID',
	sales_date date COMMENT '売上日',
	sales_account_seq int unsigned COMMENT '売上計上先',
	deposit_detail_attach_flg varchar(1) NOT NULL COMMENT '実費明細添付フラグ',
	statement_title varchar(20) COMMENT 'タイトル',
	statement_date date COMMENT '日付',
	statement_no varchar(21) COMMENT '精算番号',
	statement_to_name varchar(256) COMMENT '精算先名称',
	statement_to_name_end varchar(1) COMMENT '精算先敬称',
	statement_to_detail text COMMENT '精算先詳細',
	statement_from_tenant_name varchar(64) COMMENT '差出人事務所名',
	statement_from_detail text COMMENT '差出人詳細',
	tenant_stamp_print_flg varchar(1) NOT NULL COMMENT '印影フラグ',
	statement_sub_text varchar(20) COMMENT '挿入文',
	statement_subject varchar(160) COMMENT '件名',
	refund_date date COMMENT '返金期限',
	refund_date_print_flg varchar(1) NOT NULL COMMENT '返金期限印字フラグ',
	refund_bank_detail text COMMENT '返金先',
	statement_remarks text COMMENT '備考',
	repay_transaction_date_print_flg varchar(1) NOT NULL COMMENT '既入金取引日印字フラグ',
	invoice_transaction_date_print_flg varchar(1) NOT NULL COMMENT '請求取引日印字フラグ',
	repay_sum_flg varchar(1) NOT NULL COMMENT '既入金項目合算フラグ（既入金）',
	expense_sum_flg varchar(1) NOT NULL COMMENT '実費項目合算フラグ（請求）',
	statement_memo text COMMENT 'メモ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (statement_seq)
) COMMENT = '精算書';


-- アカウント認証
CREATE TABLE t_account_verification
(
	verification_key text NOT NULL COMMENT '認証キー',
	-- メールアドレス
	account_id varchar(256) NOT NULL COMMENT 'アカウントID',
	-- 24h
	temp_limit_date datetime NOT NULL COMMENT '有効期限',
	-- 1:認証済み
	complete_flg varchar(1) COMMENT '完了フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (verification_key(768))
) COMMENT = 'アカウント認証';


-- 顧問契約
CREATE TABLE t_advisor_contract
(
	advisor_contract_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '顧問契約SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	contract_start_date date COMMENT '契約開始日',
	contract_end_date date COMMENT '契約終了日',
	-- 1:事務所契約、2:個人契約
	contract_type varchar(1) NOT NULL COMMENT '契約区分',
	-- 1:新規、2:進行中、3:契約解除、4:終了
	contract_status varchar(1) NOT NULL COMMENT '契約ステータス',
	contract_month_charge decimal unsigned COMMENT '顧問料金（月額）',
	contract_month_time int unsigned COMMENT '稼働時間（時間/月）',
	contract_content text COMMENT '契約内容',
	contract_memo varchar(1000) COMMENT 'メモ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (advisor_contract_seq)
) COMMENT = '顧問契約';


-- 顧問契約担当
CREATE TABLE t_advisor_contract_tanto
(
	advisor_contract_tanto_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '顧問契約担当SEQ',
	advisor_contract_seq int unsigned NOT NULL COMMENT '顧問契約SEQ',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	-- 案件の担当と同じEnum値
	-- 1：売上計上先、2：担当弁護士、3：担当事務員
	tanto_type varchar(1) NOT NULL COMMENT '担当種別',
	tanto_type_branch_no int unsigned NOT NULL COMMENT '担当種別枝番',
	main_tanto_flg varchar(1) COMMENT '主担当フラグ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (advisor_contract_tanto_seq)
) COMMENT = '顧問契約担当';


-- 案件
CREATE TABLE t_anken
(
	anken_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '案件ID',
	anken_name varchar(150) COMMENT '案件名',
	-- 0:相談
	-- 1:労働
	-- 5:交通事故
	-- 10:不動産
	-- 3:債務整理
	-- 4:破産・再生
	-- 11:医療事故
	-- 8:顧問
	-- 9:その他民事
	-- 7:離婚
	-- 2:相続
	-- 12:後見等
	-- 13:その他家事
	-- 6:刑事
	bunya_id int unsigned COMMENT '分野ID',
	anken_created_date date COMMENT '案件登録日',
	-- 0：事務所事件
	-- 1：個人事件
	anken_type varchar(1) COMMENT '区分',
	jian_summary text COMMENT '事案概要・方針',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (anken_id)
) COMMENT = '案件';


-- 刑事案件付帯情報
CREATE TABLE t_anken_add_keiji
(
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	-- 1：私選
	-- 2：国選
	lawyer_select_type varchar(1) COMMENT '私選・国選区分',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (anken_id)
) COMMENT = '刑事案件付帯情報';


-- 案件-預かり品
CREATE TABLE t_anken_azukari_item
(
	anken_item_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '預かり品SEQ',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	-- enum
	-- 1：収集中
	-- 2：保管中
	-- 3：返却済
	azukari_status varchar(1) NOT NULL COMMENT 'ステータス',
	hinmoku varchar(50) COMMENT '品目',
	azukari_count varchar(30) COMMENT '数量',
	hokan_place varchar(100) COMMENT '保管場所',
	azukari_date date COMMENT '預り日',
	-- 1 : 案件に紐づく顧客
	-- 2 : 案件に紐づく関与者
	azukari_from_type varchar(1) COMMENT '預り元種別',
	azukari_from_customer_id int(10) unsigned COMMENT '預かり元顧客ID',
	azukari_from_kanyosha_seq int(10) unsigned COMMENT '預かり元関与者SEQ',
	azukari_from varchar(50) COMMENT '預り元',
	return_limit_date date COMMENT '返却期限',
	return_date date COMMENT '返却日',
	-- 1 : 案件に紐づく顧客
	-- 2 : 案件に紐づく関与者
	return_to_type varchar(1) COMMENT '返却先種別',
	return_to_customer_id int(10) unsigned COMMENT '返却先顧客ID',
	return_to_kanyosha_seq int(10) unsigned COMMENT '返却先関与者SEQ',
	return_to varchar(50) COMMENT '返却先',
	remarks text COMMENT '備考',
	last_edit_at datetime COMMENT '最終編集日時',
	last_edit_by int unsigned COMMENT '最終編集アカウントSEQ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (anken_item_seq)
) COMMENT = '案件-預かり品';


-- 案件-顧客
CREATE TABLE t_anken_customer
(
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	shokai_mendan_date date COMMENT '初回面談日',
	shokai_mendan_schedule_seq int unsigned COMMENT '初回面談予定SEQ',
	junin_date date COMMENT '受任日',
	jiken_kanryo_date date COMMENT '事件処理完了日',
	kanryo_date date COMMENT '精算完了日',
	kanryo_flg varchar(1) COMMENT '完了フラグ',
	-- 1：相談
	-- 2：進行中
	-- 3：面談予定
	-- 4：精算待ち
	-- 5：完了待ち
	-- 6：完了
	-- 9：不受任
	anken_status varchar(1) NOT NULL COMMENT '案件ステータス',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (anken_id, customer_id)
) COMMENT = '案件-顧客';


-- 案件-事件
CREATE TABLE t_anken_jiken
(
	jiken_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '事件SEQ',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	jiken_name varchar(100) COMMENT '事件名',
	taiho_date date COMMENT '逮捕日',
	koryu_seikyu_date date COMMENT '勾留請求日',
	koryu_expiration_date date COMMENT '満期日①',
	koryu_extended_expiration_date date COMMENT '満期日②',
	-- 1：起訴（公判請求）
	-- 2：起訴（略式命令請求）
	-- 3：起訴（即決裁判請求）
	-- 4：不起訴
	-- 5：処分保留
	shobun_type varchar(1) COMMENT '処分種別',
	shobun_date date COMMENT '処分日',
	remarks text COMMENT '備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (jiken_seq)
) COMMENT = '案件-事件';


-- 案件-勾留
CREATE TABLE t_anken_koryu
(
	koryu_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '勾留SEQ',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	koryu_date date COMMENT '勾留日',
	hoshaku_date date COMMENT '保釈日',
	sosakikan_id int unsigned COMMENT '捜査機関ID',
	koryu_place_name varchar(100) COMMENT '勾留場所名',
	sosakikan_tel_no varchar(20) COMMENT '捜査機関電話番号',
	remarks text COMMENT '備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (koryu_seq)
) COMMENT = '案件-勾留';


-- 案件-関与者関係者
CREATE TABLE t_anken_related_kanyosha
(
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	kanyosha_seq int unsigned NOT NULL COMMENT '関与者SEQ',
	related_kanyosha_seq int unsigned COMMENT '関連関与者SEQ',
	-- Enum
	-- 2：相手方
	-- 3：共犯者
	-- 4：被害者
	kanyosha_type varchar(1) COMMENT '関与者種別',
	-- 0：代理人・弁護人ではない
	-- 1：代理人・弁護人
	dairi_flg varchar(1) COMMENT '代理フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (anken_id, kanyosha_seq)
) COMMENT = '案件-関与者関係者';


-- 案件-接見
CREATE TABLE t_anken_sekken
(
	sekken_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '接見SEQ',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	sekken_start_at datetime COMMENT '接見開始日時',
	sekken_end_at datetime COMMENT '接見終了日時',
	place varchar(100) COMMENT '場所',
	remarks text COMMENT '備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (sekken_seq)
) COMMENT = '案件-接見';


-- 案件-捜査機関
CREATE TABLE t_anken_sosakikan
(
	sosakikan_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '捜査機関SEQ',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	sosakikan_id int unsigned COMMENT '捜査機関ID',
	sosakikan_name varchar(100) COMMENT '捜査機関名',
	sosakikan_tanto_bu varchar(100) COMMENT '担当部',
	sosakikan_tel_no varchar(20) COMMENT '電話番号',
	sosakikan_extension_no varchar(20) COMMENT '内線番号',
	sosakikan_fax_no varchar(20) COMMENT 'FAX番号',
	sosakikan_room_no varchar(10) COMMENT '号室',
	tantosha1_name varchar(128) COMMENT '担当者①',
	tantosha2_name varchar(128) COMMENT '担当者②',
	remarks text COMMENT '備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (sosakikan_seq)
) COMMENT = '案件-捜査機関';


-- 案件-担当者
CREATE TABLE t_anken_tanto
(
	anken_tanto_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '案件担当者SEQ',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	-- 1：売上計上先、
	-- 2：担当弁護士、
	-- 3：担当事務員
	tanto_type varchar(1) NOT NULL COMMENT '担当種別',
	-- 担当種別毎の枝番
	tanto_type_branch_no int unsigned NOT NULL COMMENT '担当種別枝番',
	anken_main_tanto_flg varchar(1) COMMENT '案件主担当フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (anken_tanto_seq)
) COMMENT = '案件-担当者';


-- 外部サービス認証
CREATE TABLE t_auth_token
(
	auth_token_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '外部サービス認証SEQ',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	-- Enum
	-- Google：1
	-- Box：2
	-- DropBox：3
	external_service_id varchar(2) NOT NULL COMMENT '外部サービスID',
	access_token text NOT NULL COMMENT 'アクセストークン',
	refresh_token text COMMENT 'リフレッシュトークン',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (auth_token_seq)
) COMMENT = '外部サービス認証';


-- 部署所属アカウント
CREATE TABLE t_busho_shozoku_acct
(
	busho_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '部署ID',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (busho_id, account_seq)
) COMMENT = '部署所属アカウント';


-- 接続中外部サービス
CREATE TABLE t_connected_external_service
(
	connected_external_service_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '接続中外部サービス連番',
	-- Enum：
	-- ストレージ：1
	service_type varchar(2) NOT NULL COMMENT 'サービス種別',
	-- Enum
	-- Google：1
	-- Box：2
	-- DropBox：3
	external_service_id varchar(2) NOT NULL COMMENT '外部サービスID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (connected_external_service_seq)
) COMMENT = '接続中外部サービス';


-- 伝言
CREATE TABLE t_dengon
(
	dengon_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '伝言SEQ',
	-- アカウントSEQ
	send_account_seq int unsigned NOT NULL COMMENT '送信者SEQ',
	-- アカウントID、カンマ区切り
	receive_account_seq text NOT NULL COMMENT '受信者SEQ',
	gyomu_history_seq int unsigned COMMENT '業務履歴SEQ',
	title varchar(50) COMMENT '件名',
	body text COMMENT '本文',
	draft_flg varchar(1) NOT NULL COMMENT '下書き',
	-- 0:送信Box、1:ゴミ箱
	send_trashed_flg varchar(1) NOT NULL COMMENT '送信者ごみ箱',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() COMMENT '更新日時',
	updated_by int unsigned COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (dengon_seq)
) COMMENT = '伝言';


-- 伝言アカウントステータス
CREATE TABLE t_dengon_account_status
(
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	-- t_dengon.dengon_seqに外部キー
	dengon_seq int unsigned NOT NULL COMMENT '伝言SEQ',
	dengon_status_id varchar(1) COMMENT '伝言ステータスID',
	important_flg varchar(1) NOT NULL COMMENT '重要',
	open_flg varchar(1) NOT NULL COMMENT '既読',
	trashed_flg varchar(1) NOT NULL COMMENT 'ゴミ箱',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() COMMENT '更新日時',
	updated_by int unsigned COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (account_seq, dengon_seq)
) COMMENT = '伝言アカウントステータス';


-- 伝言-顧客
CREATE TABLE t_dengon_customer
(
	-- t_dengon.dengon_seqに外部キー
	dengon_seq int unsigned NOT NULL COMMENT '伝言SEQ',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (dengon_seq, customer_id)
) COMMENT = '伝言-顧客';


-- カスタムフォルダ
CREATE TABLE t_dengon_folder
(
	dengon_folder_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '伝言フォルダSEQ',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	dengon_folder_name varchar(20) NOT NULL COMMENT '伝言フォルダ名',
	parent_dengon_folder_seq int unsigned COMMENT '親伝言フォルダ',
	trashed_flg varchar(1) NOT NULL COMMENT 'ゴミ箱',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() COMMENT '更新日時',
	updated_by int unsigned COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (dengon_folder_seq)
) COMMENT = 'カスタムフォルダ';


-- カスタムフォルダ中身
CREATE TABLE t_dengon_folder_in
(
	-- t_dengon_folder.dengon_folder_seqに外部キー
	dengon_folder_seq int unsigned NOT NULL COMMENT '伝言フォルダSEQ',
	-- t_message.message_idに外部キー
	dengon_seq int unsigned NOT NULL COMMENT '伝言SEQ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() COMMENT '更新日時',
	updated_by int unsigned COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (dengon_folder_seq, dengon_seq)
) COMMENT = 'カスタムフォルダ中身';


-- 預り金
CREATE TABLE t_deposit_recv
(
	deposit_recv_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '預り金SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	created_accg_doc_seq int unsigned COMMENT '会計書類SEQ（作成元）',
	using_accg_doc_seq int unsigned COMMENT '会計書類SEQ（使用先）',
	-- Enum
	-- 1：ユーザーの手動作成
	-- 2：請求書／精算書の発行による予定データ作成
	-- 3：請求書／精算書の発行による既入金の報酬振替（出金）による作成
	created_type varchar(1) NOT NULL COMMENT '作成タイプ',
	-- 0:実費入金以外のデータ
	-- 1:実費入金のデータ（請求書作成時のみ）
	-- ※請求書発行時に作成される予定データについて、「実費請求の入金予定」と「預り金請求の入金予定」データを区別するための値
	expense_invoice_flg varchar(1) NOT NULL COMMENT '実費入金フラグ',
	deposit_date date COMMENT '発生日',
	deposit_item_name varchar(30) NOT NULL COMMENT '預り金項目名',
	deposit_amount decimal COMMENT '入金額',
	withdrawal_amount decimal COMMENT '出金額',
	-- Enum
	-- 1：入金
	-- 2：出金
	deposit_type varchar(1) NOT NULL COMMENT '入出金タイプ',
	sum_text text COMMENT '摘要',
	deposit_recv_memo text COMMENT 'メモ',
	tenant_bear_flg varchar(1) COMMENT '事務所負担フラグ',
	-- 0:入金待ち or 出金待ち
	-- 1:入金済み or 出金済み
	deposit_complete_flg varchar(1) NOT NULL COMMENT '入出金完了フラグ',
	uncollectible_flg varchar(1) COMMENT '回収不能フラグ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (deposit_recv_seq)
) COMMENT = '預り金';


-- 報酬
CREATE TABLE t_fee
(
	fee_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '報酬SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	accg_doc_seq int unsigned COMMENT '会計書類SEQ',
	sales_detail_seq int unsigned COMMENT '売上明細SEQ',
	fee_date date COMMENT '発生日',
	fee_item_name varchar(30) NOT NULL COMMENT '報酬項目名',
	fee_time_charge_flg varchar(1) NOT NULL COMMENT 'タイムチャージフラグ',
	fee_amount decimal unsigned COMMENT '報酬額（税抜）',
	-- Enum
	-- 1：未請求
	-- 2：入金待ち
	-- 3：入金済み
	-- 4：一部入金
	fee_payment_status varchar(1) COMMENT '入金ステータス',
	tax_amount decimal COMMENT '消費税金額',
	-- Enum
	-- 1：8%
	-- 2：10%
	-- 3：0%
	tax_rate_type varchar(1) COMMENT '消費税率',
	-- Enum
	-- 0：非課税
	-- 1：課税（内税）
	-- 2：課税（外税）
	tax_flg varchar(1) NOT NULL COMMENT '課税フラグ',
	-- Enum
	-- 0：源泉徴収しない
	-- 1：源泉徴収する
	withholding_flg varchar(1) COMMENT '源泉徴収フラグ',
	withholding_amount decimal unsigned COMMENT '源泉徴収額',
	sum_text text COMMENT '摘要',
	fee_memo text COMMENT 'メモ',
	uncollectible_flg varchar(1) COMMENT '回収不能フラグ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (fee_seq)
) COMMENT = '報酬';


-- タイムチャージ-報酬付帯情報
CREATE TABLE t_fee_add_time_charge
(
	fee_seq int unsigned NOT NULL COMMENT '報酬SEQ',
	hour_price decimal unsigned COMMENT '時間単価',
	work_time_minute int COMMENT '作業時間（分）',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (fee_seq)
) COMMENT = 'タイムチャージ-報酬付帯情報';


-- ファイル構成管理
CREATE TABLE t_file_configuration_management
(
	file_configuration_management_id int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ファイル構成管理ID',
	root_folder_related_info_management_id int unsigned NOT NULL COMMENT 'ルートフォルダ関連情報管理ID',
	file_detail_info_management_id int unsigned NOT NULL COMMENT 'ファイル詳細情報管理ID',
	file_management_display_priority_id int unsigned NOT NULL COMMENT 'ファイル管理表示優先順位ID',
	parent_file_configuration_management_id int unsigned COMMENT '親ファイル構成管理ID',
	-- 0 : ルートフォルダ
	-- 1 : サブフォルダ
	-- 2 : ファイル
	file_kubun varchar(1) DEFAULT '0' NOT NULL COMMENT 'ファイル区分',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (file_configuration_management_id)
) COMMENT = 'ファイル構成管理';


-- ファイル詳細情報管理
CREATE TABLE t_file_detail_info_management
(
	file_detail_info_management_id int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ファイル詳細情報管理ID',
	file_name varchar(255) NOT NULL COMMENT 'ファイル名',
	file_extension varchar(64) COMMENT 'ファイル拡張子',
	file_type varchar(2) COMMENT 'ファイルタイプ',
	-- ルートフォルダ:NULL
	-- サブフォルダ:自分のフォルダ名を含まないパスを設定
	-- ファイル:配置されているフォルダまでのパスを設定
	folder_path varchar(3000) COMMENT 'フォルダパス',
	-- Byteで保持
	file_size bigint unsigned COMMENT 'ファイルサイズ',
	s3_object_key varchar(1024) COMMENT 'S3オブジェクトキー',
	-- 0 : 通常ファイル
	-- 1 : ゴミ箱に移動したファイル
	trash_box_flg varchar(1) NOT NULL COMMENT 'ゴミ箱フラグ',
	deleted_operation_account_seq int unsigned COMMENT '削除操作アカウント連番',
	file_created_datetime datetime NOT NULL COMMENT 'ファイル作成日時',
	upload_datetime datetime COMMENT 'アップロード日時',
	upload_user varchar(24) COMMENT 'アップロードユーザー',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (file_detail_info_management_id)
) COMMENT = 'ファイル詳細情報管理';


-- Boxフォルダ
CREATE TABLE t_folder_box
(
	folder_box_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'フォルダBox連番',
	root_folder_box_seq int unsigned NOT NULL COMMENT 'ルートフォルダBox連番',
	anken_id int unsigned COMMENT '案件ID',
	customer_id int unsigned COMMENT '顧客ID',
	anken_customer_id_from int unsigned COMMENT '案件顧客IDFrom',
	anken_customer_id_to int unsigned COMMENT '案件顧客IDTo',
	folder_id varchar(256) NOT NULL COMMENT 'フォルダID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (folder_box_seq),
	UNIQUE (folder_id)
) COMMENT = 'Boxフォルダ';


-- Dropboxフォルダ
CREATE TABLE t_folder_dropbox
(
	folder_dropbox_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'フォルダDropbox連番',
	root_folder_dropbox_seq int unsigned NOT NULL COMMENT 'ルートフォルダDropbox連番',
	anken_id int unsigned COMMENT '案件ID',
	customer_id int unsigned COMMENT '顧客ID',
	anken_customer_id_from int unsigned COMMENT '案件顧客IDFrom',
	anken_customer_id_to int unsigned COMMENT '案件顧客IDTo',
	folder_id varchar(256) NOT NULL COMMENT 'フォルダID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (folder_dropbox_seq),
	UNIQUE (folder_id)
) COMMENT = 'Dropboxフォルダ';


-- Googleフォルダ
CREATE TABLE t_folder_google
(
	folder_google_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'フォルダGoogle連番',
	root_folder_google_seq int unsigned NOT NULL COMMENT 'ルートフォルダGoogle連番',
	anken_id int unsigned COMMENT '案件ID',
	customer_id int unsigned COMMENT '顧客ID',
	anken_customer_id_from int unsigned COMMENT '案件顧客IDFrom',
	anken_customer_id_to int unsigned COMMENT '案件顧客IDTo',
	folder_id varchar(256) NOT NULL COMMENT 'フォルダID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (folder_google_seq),
	UNIQUE (folder_id)
) COMMENT = 'Googleフォルダ';


-- フォルダ権限情報管理
CREATE TABLE t_folder_permission_info_management
(
	folder_permission_info_management_id int unsigned NOT NULL AUTO_INCREMENT COMMENT 'フォルダ権限情報管理ID',
	file_configuration_management_id int unsigned NOT NULL COMMENT 'ファイル構成管理ID',
	view_limit varchar(1) NOT NULL COMMENT '閲覧制限',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (folder_permission_info_management_id)
) COMMENT = 'フォルダ権限情報管理';


-- 銀行口座
CREATE TABLE t_ginko_koza
(
	ginko_account_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '銀行口座SEQ',
	tenant_seq int unsigned COMMENT 'テナントSEQ',
	account_seq int unsigned COMMENT 'アカウントSEQ',
	-- 第一口座と第二口座種別
	branch_no int unsigned NOT NULL COMMENT '口座連番',
	label_name varchar(30) COMMENT '表示名',
	ginko_name varchar(20) COMMENT '銀行名',
	shiten_name varchar(20) COMMENT '支店名',
	shiten_no varchar(6) COMMENT '支店番号',
	koza_type varchar(1) COMMENT '口座種類',
	koza_no varchar(9) COMMENT '口座番号',
	koza_name varchar(30) COMMENT '口座名義',
	koza_name_kana varchar(100) COMMENT '口座名義かな',
	default_use_flg varchar(1) NOT NULL COMMENT '既定利用フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (ginko_account_seq)
) COMMENT = '銀行口座';


-- グループ所属アカウント
CREATE TABLE t_group_shozoku_acct
(
	group_id int unsigned NOT NULL AUTO_INCREMENT COMMENT 'グループID',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (group_id, account_seq)
) COMMENT = 'グループ所属アカウント';


-- 業務履歴
CREATE TABLE t_gyomu_history
(
	gyomu_history_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '業務履歴SEQ',
	-- Enum
	-- 1：顧客軸から登録された場合
	-- 2：案件軸から登録された場合
	transition_type varchar(1) NOT NULL COMMENT '遷移元種別',
	saiban_seq int unsigned COMMENT '裁判SEQ',
	subject varchar(30) NOT NULL COMMENT '件名',
	main_text text COMMENT '本文',
	important_flg varchar(1) NOT NULL COMMENT '重要',
	-- 一覧の上部に固定する
	kotei_flg varchar(1) NOT NULL COMMENT '固定',
	supported_at datetime COMMENT '対応日時',
	dengon_sent_flg varchar(1) NOT NULL COMMENT '伝言送信済みフラグ',
	last_edit_at datetime COMMENT '最終編集日時',
	last_edit_by int unsigned COMMENT '最終編集アカウントSEQ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (gyomu_history_seq)
) COMMENT = '業務履歴';


-- 業務履歴-案件
CREATE TABLE t_gyomu_history_anken
(
	gyomu_history_seq int unsigned NOT NULL COMMENT '業務履歴SEQ',
	-- 
	-- 
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (gyomu_history_seq, anken_id)
) COMMENT = '業務履歴-案件';


-- 業務履歴-顧客
CREATE TABLE t_gyomu_history_customer
(
	gyomu_history_seq int unsigned NOT NULL COMMENT '業務履歴SEQ',
	-- 
	-- 
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (gyomu_history_seq, customer_id)
) COMMENT = '業務履歴-顧客';


-- お知らせ既読履歴
CREATE TABLE t_info_read_history
(
	-- manageDBのt_info_mngと結合
	info_mgt_seq int unsigned NOT NULL COMMENT 'お知らせMGT連番',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (info_mgt_seq, account_seq)
) COMMENT = 'お知らせ既読履歴';


-- 招待アカウント認証
CREATE TABLE t_invited_account_verification
(
	verification_key text NOT NULL COMMENT '認証キー',
	mail_address varchar(256) NOT NULL COMMENT 'メールアドレス',
	temp_limit_date datetime NOT NULL COMMENT '有効期限',
	-- 1:認証済み
	complete_flg varchar(1) COMMENT '完了フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (verification_key(768))
) COMMENT = '招待アカウント認証';


-- 会計記録
CREATE TABLE t_kaikei_kiroku
(
	kaikei_kiroku_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '会計記録SEQ',
	nyushukkin_yotei_seq int unsigned COMMENT '入出金予定SEQ',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	hassei_date date COMMENT '発生日',
	-- Enum
	-- 1：着手金
	-- 2：追加着手金
	-- 3：成功報酬
	-- 4：文書通信費
	-- 5：手数料
	-- 6：タイムチャージ
	-- 7：日当
	-- 8：顧問報酬
	-- 9：相談料
	-- 10：その他
	hoshu_komoku_id varchar(10) COMMENT '報酬項目ID',
	nyushukkin_komoku_id int unsigned COMMENT '入出金項目ID',
	-- Enum
	-- 1 : 入金
	-- 2 : 出金
	nyushukkin_type varchar(1) NOT NULL COMMENT '入出金タイプ',
	nyukin_gaku decimal unsigned COMMENT '入金額',
	shukkin_gaku decimal COMMENT '出金額',
	tax_gaku decimal COMMENT '消費税金額',
	time_charge_tanka decimal unsigned COMMENT 'タイムチャージ単価',
	-- Enum
	-- 1：開始・終了時間を指定する
	-- 2：時間(分)を指定する
	time_charge_time_shitei varchar(1) COMMENT 'タイムチャージ時間指定方法',
	time_charge_start_time datetime COMMENT 'タイムチャージ開始日時',
	time_charge_end_time datetime COMMENT 'タイムチャージ終了日時',
	time_charge_time int(5) COMMENT 'タイムチャージ時間',
	-- Enum
	-- 1：8%
	-- 2：10%
	tax_rate varchar(1) COMMENT '消費税率',
	-- Enum
	-- 0：非課税
	-- 1：課税（内税）
	-- 2：課税（外税）
	tax_flg varchar(1) NOT NULL COMMENT '課税フラグ',
	-- Enum
	-- 0：源泉徴収しない
	-- 1：源泉徴収する
	gensenchoshu_flg varchar(1) COMMENT '源泉徴収フラグ',
	gensenchoshu_gaku decimal unsigned COMMENT '源泉徴収額',
	-- Enum
	-- 1：プール元
	-- 2：プール先
	pool_type varchar(1) COMMENT '預かり金プール種別',
	pool_moto_anken_id int unsigned COMMENT '預り金プール元案件ID',
	pool_saki_anken_id int unsigned COMMENT '預り金プール先案件ID',
	-- Enum
	-- 0：回収不能としない
	-- 1：回収不能とする
	uncollectable_flg varchar(1) COMMENT '回収不能フラグ',
	tekiyo text COMMENT '摘要',
	seisan_date date COMMENT '精算処理日',
	seisan_seq int unsigned COMMENT '精算SEQ',
	last_edit_at datetime DEFAULT NULL COMMENT '最終編集日時',
	last_edit_by int(10) unsigned DEFAULT NULL COMMENT '最終編集アカウントSEQ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (kaikei_kiroku_seq)
) COMMENT = '会計記録';


-- 関与者
CREATE TABLE t_kanyosha
(
	kanyosha_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '関与者SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	disp_order int unsigned NOT NULL COMMENT '表示順',
	kankei varchar(30) COMMENT '関係',
	remarks text COMMENT '備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (kanyosha_seq),
	UNIQUE (person_id, anken_id)
) COMMENT = '関与者';


-- 刑事案件-顧客
CREATE TABLE t_keiji_anken_customer
(
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	-- 0：在宅
	-- 1：勾留中
	-- 2：保釈
	koryu_status varchar(1) COMMENT '勾留ステータス',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (anken_id, customer_id)
) COMMENT = '刑事案件-顧客';


-- メール送信履歴
CREATE TABLE t_mail_send_history
(
	-- auto increment
	mail_send_history_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'メール送信履歴連番',
	mail_id varchar(5) NOT NULL COMMENT 'メールID',
	mail_name varchar(100) NOT NULL COMMENT 'メール名',
	mail_type varchar(1) NOT NULL COMMENT 'メール形式',
	send_from_name varchar(128) COMMENT '送信元表示名(From)',
	send_from varchar(256) NOT NULL COMMENT '送信元(From)',
	send_to varchar(2000) NOT NULL COMMENT '送信先(to)',
	send_cc varchar(2000) COMMENT '送信先(cc)',
	send_bcc varchar(2000) COMMENT '送信先(bcc)',
	mail_title varchar(100) NOT NULL COMMENT 'メールタイトル',
	mail_body text NOT NULL COMMENT 'メール本文',
	send_status varchar(2) NOT NULL COMMENT 'メール送信ステータス',
	send_dt datetime COMMENT 'メール送信日時',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (mail_send_history_seq)
) COMMENT = 'メール送信履歴';


-- 入出金予定
CREATE TABLE t_nyushukkin_yotei
(
	nyushukkin_yotei_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '入出金予定SEQ',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	-- Enum
	-- 1 : 入金
	-- 2 : 出金
	nyushukkin_type varchar(1) NOT NULL COMMENT '入出金タイプ',
	nyushukkin_komoku_id int unsigned COMMENT '入出金項目ID',
	-- Enum
	-- 0：顧客から選択
	-- 1：関与者から選択
	-- 2：自由入力
	nyukin_shiharaisha_type varchar(1) COMMENT '入金-支払者種別',
	nyukin_customer_id int unsigned COMMENT '入金-支払者顧客ID',
	nyukin_kanyosha_seq int unsigned COMMENT '入金-支払者関与者SEQ',
	nyukin_shiharaisha varchar(50) COMMENT '入金-支払者',
	nyukin_saki_koza_seq int unsigned COMMENT '入金-入金先口座SEQ',
	shukkin_saki_koza_seq int unsigned COMMENT '出金-出金先口座SEQ',
	-- Enum
	-- 0：顧客から選択
	-- 1：関与者から選択
	-- 2：自由入力
	shukkin_shiharai_saki_type varchar(1) COMMENT '出金-支払先種別',
	shukkin_customer_id int unsigned COMMENT '出金-支払先顧客ID',
	shukkin_kanyosha_seq int unsigned COMMENT '出金-支払先関与者SEQ',
	shukkin_shiharai_saki varchar(50) COMMENT '出金-支払先',
	nyushukkin_yotei_date date COMMENT '入出金予定日',
	nyushukkin_yotei_gaku decimal unsigned COMMENT '入出金予定額',
	nyushukkin_date date COMMENT '入出金日',
	nyushukkin_gaku decimal unsigned COMMENT '入出金額',
	tekiyo text COMMENT '摘要',
	seisan_seq int unsigned COMMENT '精算SEQ',
	last_edit_at datetime DEFAULT NULL COMMENT '最終編集日時',
	last_edit_by int(10) unsigned DEFAULT NULL COMMENT '最終編集アカウントSEQ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (nyushukkin_yotei_seq)
) COMMENT = '入出金予定';


-- 旧住所
CREATE TABLE t_old_address
(
	old_address_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '旧住所SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	zip_code varchar(8) COMMENT '郵便番号',
	address1 varchar(128) COMMENT '地域',
	address2 varchar(128) COMMENT '番地・建物名',
	remarks text COMMENT '備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (old_address_seq)
) COMMENT = '旧住所';


-- 支払いカードテーブル
CREATE TABLE t_payment_card
(
	payment_card_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '支払いカード連番',
	card_number_last_4 varchar(4) NOT NULL COMMENT 'カード番号',
	expired_year varchar(2) NOT NULL COMMENT '有効期限（年）',
	expired_month varchar(2) NOT NULL COMMENT '有効期限（月）',
	last_name varchar(50) NOT NULL COMMENT '名義（姓）',
	first_name varchar(50) NOT NULL COMMENT '名義（名）',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (payment_card_seq)
) COMMENT = '支払いカードテーブル';


-- 名簿
CREATE TABLE t_person
(
	person_id int unsigned NOT NULL AUTO_INCREMENT COMMENT '名簿ID',
	customer_id int unsigned COMMENT '顧客ID',
	-- 顧客の場合：フラグON
	-- それ以外の場合：フラグOFF
	customer_flg varchar(1) NOT NULL COMMENT '顧客フラグ',
	-- 顧問の場合：フラグON
	-- それ以外の場合：フラグOFF
	advisor_flg varchar(1) NOT NULL COMMENT '顧問フラグ',
	-- 0：個人(自然人)
	-- 1：法人
	-- 2：弁護士
	customer_type varchar(1) COMMENT '個人・法人・弁護士区分',
	customer_name_sei varchar(128) COMMENT '顧客姓',
	customer_name_sei_kana varchar(128) COMMENT '顧客姓かな',
	customer_name_mei varchar(128) COMMENT '顧客名',
	customer_name_mei_kana varchar(128) COMMENT '顧客名かな',
	zip_code varchar(8) COMMENT '郵便番号',
	address1 varchar(128) COMMENT '地域',
	address2 varchar(128) COMMENT '番地・建物名',
	address_remarks text COMMENT '住所備考',
	-- 0：可
	-- 1：不可
	contact_type varchar(1) COMMENT '電話連絡可否',
	contact_remarks text COMMENT '連絡先備考',
	-- 0:「-」(未設定)
	-- 1:居住地
	-- 2:その他
	transfer_address_type varchar(1) COMMENT '郵送先住所区分',
	transfer_zip_code varchar(8) COMMENT '郵送先郵便番号',
	transfer_address1 varchar(128) COMMENT '郵送先地域',
	transfer_address2 varchar(128) COMMENT '郵送先番地・建物名',
	transfer_name varchar(30) COMMENT '宛名',
	-- 0：不明
	-- 1：事務所封筒
	-- 2：個人名・無地
	-- 3：事務所封筒
	-- 4：郵便局留め
	-- 5：来所受取
	-- 6：郵送不可
	transfer_type varchar(1) COMMENT '郵送方法',
	transfer_remarks text COMMENT '郵送先備考',
	customer_created_date date COMMENT '顧客登録日',
	remarks text COMMENT '特記事項',
	sodan_route int unsigned COMMENT '相談経路',
	sodan_remarks text COMMENT '相談経路備考',
	add_info int unsigned COMMENT '追加情報',
	add_info_remarks text COMMENT '追加情報備考',
	ginko_name varchar(20) COMMENT '銀行名',
	shiten_name varchar(20) COMMENT '支店名',
	shiten_no varchar(6) COMMENT '支店番号',
	koza_type varchar(1) COMMENT '口座種類',
	koza_no varchar(9) COMMENT '口座番号',
	koza_name varchar(30) COMMENT '口座名義',
	koza_name_kana varchar(100) COMMENT '口座名義カナ',
	koza_remarks text COMMENT '口座備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (person_id),
	UNIQUE (customer_id)
) COMMENT = '名簿';


-- 法人名簿付帯情報
CREATE TABLE t_person_add_hojin
(
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	daihyo_name varchar(128) COMMENT '代表者',
	daihyo_name_kana varchar(128) COMMENT '代表者かな',
	daihyo_position_name varchar(50) COMMENT '代表者役職',
	tanto_name varchar(128) COMMENT '担当者',
	tanto_name_kana varchar(128) COMMENT '担当者かな',
	-- 0:「-」(未設定)
	-- 1:所在地
	-- 2:その他
	toki_address_type varchar(1) COMMENT '登記住所区分',
	toki_zip_code varchar(8) COMMENT '登記郵便番号',
	toki_address1 varchar(128) COMMENT '登記地域',
	toki_address2 varchar(128) COMMENT '登記番地・建物名',
	toki_address_remarks text COMMENT '登記住所備考',
	old_hojin_name varchar(128) COMMENT '旧商号',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (person_id)
) COMMENT = '法人名簿付帯情報';


-- 個人名簿付帯情報
CREATE TABLE t_person_add_kojin
(
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	old_name varchar(30) COMMENT '旧姓',
	old_name_kana varchar(64) COMMENT '旧姓かな',
	yago varchar(30) COMMENT '屋号・通称',
	yago_kana varchar(64) COMMENT '屋号・通称かな',
	-- 0：不明
	-- 1：男性
	-- 2：女性
	-- 9：適応不能
	gender_type varchar(1) COMMENT '性別',
	birthday date COMMENT '生年月日',
	-- 0：西暦
	-- 5：令和
	-- 4：平成
	-- 3：昭和
	-- 2：大正
	-- 1：明治
	birthday_display_type varchar(1) COMMENT '生年月日表示区分',
	country varchar(30) COMMENT '国籍',
	language varchar(30) COMMENT '言語',
	-- 0:「-」(未設定)
	-- 1:居住地
	-- 2:その他
	juminhyo_address_type varchar(1) COMMENT '住民票登録地住所区分',
	juminhyo_zip_code varchar(8) COMMENT '住民票登録地郵便番号',
	juminhyo_address1 varchar(128) COMMENT '住民票登録地地域',
	juminhyo_address2 varchar(128) COMMENT '住民票登録地番地・建物名',
	juminhyo_remarks text COMMENT '住民票登録地備考',
	job varchar(50) COMMENT '職業',
	work_place varchar(50) COMMENT '勤務先',
	busho_name varchar(50) COMMENT '部署名',
	death_date date COMMENT '死亡日',
	-- 0：西暦
	-- 5：令和
	-- 4：平成
	-- 3：昭和
	-- 2：大正
	-- 1：明治
	death_date_display_type varchar(1) COMMENT '死亡日表示区分',
	-- 故人の場合：1
	-- 故人ではない場合：0
	death_flg varchar(1) COMMENT '死亡フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (person_id)
) COMMENT = '個人名簿付帯情報';


-- 弁護士名簿付帯情報
CREATE TABLE t_person_add_lawyer
(
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	jimusho_name varchar(50) COMMENT '事務所名',
	busho_name varchar(50) COMMENT '部署名・役職',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (person_id)
) COMMENT = '弁護士名簿付帯情報';


-- 名簿連絡先
CREATE TABLE t_person_contact
(
	contact_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '顧客連絡先SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	tel_no varchar(20) COMMENT '電話番号',
	-- 1：自宅
	-- 2：職場
	-- 3：携帯
	-- 9：その他
	tel_no_type varchar(1) COMMENT '電話番号区分',
	yusen_tel_flg varchar(1) COMMENT '優先電話フラグ',
	tel_remarks varchar(100) COMMENT '電話備考',
	fax_no varchar(20) COMMENT 'FAX番号',
	-- 1：自宅
	-- 2：職場
	-- 9：その他
	fax_no_type varchar(1) COMMENT 'FAX番号区分',
	yusen_fax_flg varchar(1) COMMENT '優先FAXフラグ',
	fax_remarks varchar(100) COMMENT 'FAX備考',
	mail_address varchar(256) COMMENT 'メールアドレス',
	-- 1：自宅
	-- 2：職場
	-- 9：その他
	mail_address_type varchar(1) COMMENT 'メールアドレス区分',
	yusen_mail_address_flg varchar(1) COMMENT '優先メールアドレスフラグ',
	mail_address_remarks varchar(100) COMMENT 'メールアドレス備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (contact_seq)
) COMMENT = '名簿連絡先';


-- 利用プラン履歴テーブル
CREATE TABLE t_plan_history
(
	plan_history_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '利用プラン履歴連番',
	tenant_seq int unsigned NOT NULL COMMENT 'テナントSEQ',
	-- 0:無料、1:有効、8:停止、9:解約
	plan_status varchar(2) NOT NULL COMMENT 'プラン契約ステータス',
	plan_type varchar(1) NOT NULL COMMENT 'プランタイプ',
	license_count int(4) unsigned COMMENT 'ライセンス数',
	-- 単位はGB
	storage_capacity int unsigned COMMENT 'ストレージ容量',
	-- 無料期間、解約後の利用可能期間を登録
	expired_at datetime COMMENT '利用期限',
	-- ロボットペイメント側で管理する自動課金情報のキーとなる番号
	auto_charge_number bigint(10) unsigned COMMENT '自動課金番号',
	-- 当月の請求金額
	charge_this_month decimal unsigned COMMENT '当月請求金額',
	-- 翌月以降の請求金額
	charge_after_next_month decimal unsigned COMMENT '翌月以降請求金額',
	history_created_at datetime NOT NULL COMMENT '履歴登録日',
	history_created_by int unsigned NOT NULL COMMENT '履歴登録者ID',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (plan_history_seq)
) COMMENT = '利用プラン履歴テーブル';


-- Boxルートフォルダ
CREATE TABLE t_root_folder_box
(
	root_folder_box_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ルートフォルダBox連番',
	folder_id varchar(256) NOT NULL COMMENT 'フォルダID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (root_folder_box_seq),
	UNIQUE (folder_id)
) COMMENT = 'Boxルートフォルダ';


-- Dropboxルートフォルダ
CREATE TABLE t_root_folder_dropbox
(
	root_folder_dropbox_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ルートフォルダDropbox連番',
	folder_id varchar(256) NOT NULL COMMENT 'フォルダID',
	shared_folder_id varchar(256) NOT NULL COMMENT '共有フォルダID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (root_folder_dropbox_seq),
	UNIQUE (folder_id),
	UNIQUE (shared_folder_id)
) COMMENT = 'Dropboxルートフォルダ';


-- Googleルートフォルダ
CREATE TABLE t_root_folder_google
(
	root_folder_google_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ルートフォルダGoogle連番',
	folder_id varchar(256) NOT NULL COMMENT 'フォルダID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (root_folder_google_seq),
	UNIQUE (folder_id)
) COMMENT = 'Googleルートフォルダ';


-- ルートフォルダ関連情報管理
CREATE TABLE t_root_folder_related_info_management
(
	root_folder_related_info_management_id int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ルートフォルダ関連情報管理ID',
	anken_id int unsigned COMMENT '案件ID',
	customer_id int unsigned COMMENT '顧客ID',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (root_folder_related_info_management_id)
) COMMENT = 'ルートフォルダ関連情報管理';


-- 裁判
CREATE TABLE t_saiban
(
	saiban_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '裁判SEQ',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	-- 案件IDの枝番
	saiban_branch_no int unsigned NOT NULL COMMENT '裁判枝番',
	-- Enum
	-- 1:準備中、2:進行中、3:完了
	-- 
	-- ・準備中　⇒　裁判画面登録時
	-- 
	-- ・進行中　⇒　申立日登録後（③-9）
	-- 
	-- ・完了　　⇒　終了日登録後（③-10）
	saiban_status varchar(1) NOT NULL COMMENT '裁判ステータス',
	saiban_start_date date COMMENT '開始日',
	saiban_end_date date COMMENT '終了日',
	saibansho_id int unsigned COMMENT '裁判所ID',
	saibansho_name varchar(100) COMMENT '裁判所名',
	keizoku_bu_name varchar(100) COMMENT '係属部',
	keizoku_bu_tel_no varchar(20) COMMENT '係属部電話番号',
	keizoku_bu_fax_no varchar(20) COMMENT '係属部FAX番号',
	keizoku_kakari_name varchar(100) COMMENT '係属係',
	tanto_shoki varchar(128) COMMENT '担当書記官',
	saiban_result text COMMENT '結果',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saiban_seq)
) COMMENT = '裁判';


-- 刑事裁判付帯情報
CREATE TABLE t_saiban_add_keiji
(
	saiban_seq int unsigned NOT NULL COMMENT '裁判SEQ',
	main_jiken_seq int unsigned COMMENT '本起訴事件SEQ',
	sosakikan_id int unsigned COMMENT '捜査機関ID',
	kensatsucho_name varchar(100) COMMENT '検察庁名',
	kensatsucho_tanto_bu_name varchar(100) COMMENT '担当部',
	kensatsukan_name varchar(128) COMMENT '検察官名',
	kensatsukan_name_kana varchar(128) COMMENT '検察官名かな',
	jimukan_name varchar(128) COMMENT '事務官名',
	jimukan_name_kana varchar(128) COMMENT '事務官名かな',
	kensatsu_tel_no varchar(20) COMMENT '検察電話番号',
	kensatsu_extension_no varchar(20) COMMENT '検察内線番号',
	kensatsu_fax_no varchar(20) COMMENT '検察FAX番号',
	kensatsu_room_no varchar(10) COMMENT '検察号室',
	kensatsu_remarks text COMMENT '検察備考',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saiban_seq)
) COMMENT = '刑事裁判付帯情報';


-- 裁判-顧客
CREATE TABLE t_saiban_customer
(
	saiban_seq int unsigned NOT NULL COMMENT '裁判SEQ',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	-- 原告とか、控訴人とかEnum管理
	-- 代理人の時はnull
	saiban_tojisha_hyoki varchar(2) COMMENT '裁判当事者表記',
	-- 1:筆頭
	main_flg varchar(1) COMMENT '筆頭フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saiban_seq, customer_id)
) COMMENT = '裁判-顧客';


-- 裁判-事件
CREATE TABLE t_saiban_jiken
(
	jiken_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '事件SEQ',
	saiban_seq int unsigned NOT NULL COMMENT '裁判SEQ',
	jiken_gengo varchar(3) COMMENT '事件番号元号',
	jiken_year varchar(4) COMMENT '事件番号年',
	jiken_mark varchar(2) COMMENT '事件番号符号',
	jiken_no varchar(10) COMMENT '事件番号',
	jiken_name varchar(100) COMMENT '事件名',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (jiken_seq)
) COMMENT = '裁判-事件';


-- 裁判期日
CREATE TABLE t_saiban_limit
(
	saiban_limit_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '裁判期日SEQ',
	limit_at datetime NOT NULL COMMENT '期日',
	place varchar(100) COMMENT '場所',
	-- 1：要
	-- 2：不要
	-- 3：電話会議
	-- 4：ウェブ会議
	shuttei_type varchar(1) COMMENT '出廷(要/不要)',
	content text COMMENT '手続き内容',
	result text COMMENT '期日結果',
	limit_date_count int unsigned COMMENT '回数',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saiban_limit_seq)
) COMMENT = '裁判期日';


-- 裁判-裁判期日
CREATE TABLE t_saiban_limit_relation
(
	saiban_seq int unsigned NOT NULL COMMENT '裁判SEQ',
	saiban_limit_seq int unsigned NOT NULL COMMENT '裁判期日SEQ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saiban_seq, saiban_limit_seq)
) COMMENT = '裁判-裁判期日';


-- 裁判-関与者関係者
CREATE TABLE t_saiban_related_kanyosha
(
	saiban_seq int unsigned NOT NULL COMMENT '裁判SEQ',
	kanyosha_seq int unsigned NOT NULL COMMENT '関与者SEQ',
	related_kanyosha_seq int unsigned COMMENT '関連関与者SEQ',
	-- Enum
	-- 1：共同訴訟人
	-- 2：相手方
	-- 3：共犯者
	kanyosha_type varchar(1) NOT NULL COMMENT '関与者種別',
	-- 原告とか、控訴人とかEnum管理
	-- 代理人の時はnull
	saiban_tojisha_hyoki varchar(2) COMMENT '裁判当事者表記',
	main_flg varchar(1) NOT NULL COMMENT '筆頭フラグ',
	dairi_flg varchar(1) NOT NULL COMMENT '代理人フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saiban_seq, kanyosha_seq)
) COMMENT = '裁判-関与者関係者';


-- 裁判-裁判官
CREATE TABLE t_saiban_saibankan
(
	saibankan_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '裁判官SEQ',
	saiban_seq int unsigned NOT NULL COMMENT '裁判SEQ',
	saibankan_name varchar(128) NOT NULL COMMENT '裁判官名',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saibankan_seq)
) COMMENT = '裁判-裁判官';


-- 裁判-担当者
CREATE TABLE t_saiban_tanto
(
	saiban_tanto_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '裁判担当者SEQ',
	saiban_seq int unsigned NOT NULL COMMENT '裁判SEQ',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	-- 2：担当弁護士、3：担当事務員
	tanto_type varchar(1) NOT NULL COMMENT '担当種別',
	-- 担当種別毎に枝番
	tanto_type_branch_no int unsigned NOT NULL COMMENT '担当種別連番',
	saiban_main_tanto_flg varchar(1) COMMENT '裁判主担当フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saiban_tanto_seq)
) COMMENT = '裁判-担当者';


-- 裁判ツリー
CREATE TABLE t_saiban_tree
(
	saiban_tree_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '裁判ツリーSEQ',
	-- t_saibanと1:1を想定
	saiban_seq int unsigned NOT NULL COMMENT '裁判SEQ',
	parent_seq int unsigned COMMENT '親裁判SEQ',
	-- Enum
	-- １：併合
	-- ２：反訴
	-- 親の場合、null
	connect_type varchar(1) COMMENT '併合反訴種別',
	-- 子に反訴がいる：1
	-- それ以外：0 
	-- 
	honso_flg varchar(1) NOT NULL COMMENT '本訴フラグ',
	-- 子に被併合がいる：1
	-- それ以外：0
	kihon_flg varchar(1) NOT NULL COMMENT '基本事件フラグ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (saiban_tree_seq)
) COMMENT = '裁判ツリー';


-- 売上
CREATE TABLE t_sales
(
	sales_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '売上SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	-- 税込
	sales_amount_expect decimal unsigned NOT NULL COMMENT '売上合計【見込】（税込）',
	-- 税込
	sales_amount_result decimal unsigned NOT NULL COMMENT '売上合計【実績】（税込）',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (sales_seq)
) COMMENT = '売上';


-- 売上明細
CREATE TABLE t_sales_detail
(
	sales_detail_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '売上明細SEQ',
	sales_seq int unsigned NOT NULL COMMENT '売上SEQ',
	-- 税抜
	-- 値引き前の金額
	sales_amount decimal unsigned NOT NULL COMMENT '売上金額（税抜）',
	-- 値引き前の金額
	sales_tax_amount decimal unsigned NOT NULL COMMENT '消費税額',
	sales_discount_amount decimal unsigned COMMENT '値引き_売上金額（税抜）',
	sales_discount_tax_amount decimal unsigned COMMENT '値引き_消費税額',
	sales_withholding_amount decimal unsigned NOT NULL COMMENT '源泉徴収税額',
	sales_date date COMMENT '売上日',
	sales_account_seq int unsigned COMMENT '売上計上先',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (sales_detail_seq)
) COMMENT = '売上明細';


-- 売上明細-消費税
CREATE TABLE t_sales_detail_tax
(
	sales_detail_seq int unsigned NOT NULL COMMENT '売上明細SEQ',
	-- Enum
	-- 1：8%
	-- 2：10%
	tax_rate_type varchar(1) NOT NULL COMMENT '税率',
	-- 値引き前の金額
	taxable_amount decimal unsigned NOT NULL COMMENT '対象額',
	-- 値引き前の金額
	tax_amount decimal unsigned NOT NULL COMMENT '税額',
	discount_taxable_amount decimal unsigned COMMENT '値引き_対象額',
	discount_tax_amount decimal unsigned COMMENT '値引き_税額',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (sales_detail_seq, tax_rate_type)
) COMMENT = '売上明細-消費税';


-- 予定
CREATE TABLE t_schedule
(
	schedule_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '予定SEQ',
	subject varchar(30) COMMENT '件名',
	date_from date COMMENT '日付From',
	date_to date COMMENT '日付To',
	time_from time COMMENT '時間Form',
	time_to time COMMENT '時間To',
	-- 0：終日チェックなし、
	-- 1：終日チェックあり
	all_day_flg varchar(1) NOT NULL COMMENT '終日フラグ',
	-- 0：繰り返しチェックなし
	-- 1：繰り返しチェックあり
	repeat_flg varchar(1) NOT NULL COMMENT '繰返しフラグ',
	-- 1：毎日
	-- 2：毎週
	-- 3：毎月
	repeat_type varchar(1) COMMENT '繰返しタイプ',
	-- 毎週の場合のみ7桁のビットで表現
	-- 左から日曜始まり
	-- 
	-- 例）1010001
	-- 上記の場合は、「日」、「火」、「土」
	repeat_yobi varchar(7) COMMENT '繰返し曜日',
	-- 毎月の場合
	repeat_day_of_month int unsigned COMMENT '繰返し日',
	room_id int unsigned COMMENT '会議室ID',
	-- 会議室で所外、その他選択時の入力
	place varchar(100) COMMENT 'その他入力場所',
	memo text COMMENT 'メモ',
	-- 0：全員に許可
	-- 1：参加者のみ
	open_range varchar(1) NOT NULL COMMENT '公開範囲',
	-- 0：全員に許可
	-- 1：参加者のみ
	edit_range varchar(1) NOT NULL COMMENT '編集許可',
	customer_id int unsigned COMMENT '顧客ID',
	anken_id int unsigned COMMENT '案件ID',
	saiban_seq int unsigned COMMENT '裁判SEQ',
	saiban_limit_seq int unsigned COMMENT '裁判期日SEQ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned COMMENT '登録アカウントSEQ',
	updated_at datetime COMMENT '更新日時',
	updated_by int unsigned COMMENT '更新アカウントSEQ',
	deleted_at datetime COMMENT '削除日時',
	deleted_by int unsigned COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (schedule_seq)
) COMMENT = '予定';


-- 予定-参加者
CREATE TABLE t_schedule_account
(
	schedule_seq int unsigned NOT NULL COMMENT '予定SEQ',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (schedule_seq, account_seq)
) COMMENT = '予定-参加者';


-- 繰返し除外
CREATE TABLE t_schedule_jogai
(
	schedule_seq int unsigned NOT NULL COMMENT '予定SEQ',
	jogai_date date NOT NULL COMMENT '除外日付',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (schedule_seq)
) COMMENT = '繰返し除外';


-- 精算記録
CREATE TABLE t_seisan_kiroku
(
	seisan_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '精算SEQ',
	seisan_id int unsigned NOT NULL COMMENT '精算ID',
	customer_id int unsigned NOT NULL COMMENT '顧客ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	-- 1:仮精算
	-- 2:精算
	seisan_status varchar(1) NOT NULL COMMENT '精算ステータス',
	-- 1:精算(返金)
	-- 2:請求
	seisan_kubun varchar(1) NOT NULL COMMENT '精算区分',
	-- Enum
	-- 1：一括
	-- 2：分割
	seikyu_type varchar(1) COMMENT '請求方法',
	month_shiharai_gaku decimal unsigned COMMENT '請求-月々の支払額',
	month_shiharai_date varchar(2) COMMENT '請求-月々の支払日',
	shiharai_start_dt date COMMENT '請求-支払開始日',
	shiharai_dt date COMMENT '請求-入金予定日',
	-- Enum
	-- 1：初回
	-- 2：最終
	hasu varchar(1) COMMENT '請求-端数月',
	-- Enum
	-- 0：顧客から選択
	-- 1：関与者から選択
	seikyu_shiharaisha_type varchar(1) COMMENT '請求-請求先種別',
	seikyu_customer_id int unsigned COMMENT '請求-請求先顧客ID',
	seikyu_kanyosha_seq int unsigned COMMENT '請求-請求先関与者SEQ',
	seikyu_koza_seq int unsigned COMMENT '請求-口座SEQ',
	shiharai_limit_dt date COMMENT '返金-支払い期日',
	henkin_koza_seq int unsigned COMMENT '返金-口座SEQ',
	-- Enum
	-- 0：顧客から選択
	-- 1：関与者から選択
	-- 2：自由入力
	henkin_saki_type varchar(1) COMMENT '返金-返金先種別',
	henkin_customer_id int unsigned COMMENT '返金-返金先顧客ID',
	henkin_kanyosha_seq int unsigned COMMENT '返金-返金先関与者SEQ',
	seisan_gaku decimal NOT NULL COMMENT '精算額',
	tekiyo text COMMENT '摘要',
	-- Enum
	-- 0：プールしない
	-- 1：プールする
	pool_flg varchar(1) COMMENT '預り金プールフラグ',
	last_edit_at datetime DEFAULT NULL COMMENT '最終編集日時',
	last_edit_by int(10) unsigned DEFAULT NULL COMMENT '最終編集アカウントSEQ',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	deleted_at datetime DEFAULT NULL COMMENT '削除日',
	deleted_by int(10) unsigned DEFAULT NULL COMMENT '削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (seisan_seq)
) COMMENT = '精算記録';


-- タスク
CREATE TABLE t_task
(
	task_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'タスクSEQ',
	anken_id int unsigned COMMENT '案件ID',
	title varchar(255) COMMENT '件名',
	content text COMMENT '内容',
	limit_dt_to datetime COMMENT '期限To',
	all_day_flg varchar(1) COMMENT '期限終日フラグ',
	-- Enum
	-- 1：未着手
	-- 2：進行中
	-- 9：完了
	task_status varchar(1) COMMENT 'タスクステータス',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (task_seq)
) COMMENT = 'タスク';


-- タスク案件
CREATE TABLE t_task_anken
(
	task_anken_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'タスク-案件SEQ',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	account_seq int unsigned NOT NULL COMMENT 'アカウントSEQ',
	disp_order int unsigned COMMENT '表示順',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (task_anken_seq),
	UNIQUE (anken_id, account_seq)
) COMMENT = 'タスク案件';


-- タスク-チェック項目
CREATE TABLE t_task_check_item
(
	task_check_item_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'タスク-チェック項目SEQ',
	task_seq int unsigned NOT NULL COMMENT 'タスクSEQ',
	item_name varchar(100) NOT NULL COMMENT 'チェック項目名',
	-- 0：未完了
	-- 1：完了
	complete_flg varchar(1) NOT NULL COMMENT '完了フラグ',
	disp_order int unsigned NOT NULL COMMENT 'チェック項目表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (task_check_item_seq)
) COMMENT = 'タスク-チェック項目';


-- タスク-履歴
CREATE TABLE t_task_history
(
	task_history_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'タスク履歴SEQ',
	task_seq int unsigned NOT NULL COMMENT 'タスクSEQ',
	-- 1：コメント追加
	-- 2：タスク更新
	task_history_type varchar(1) NOT NULL COMMENT 'タスク履歴種別',
	-- 0：更新していない
	-- 1：更新している
	title_update_flg varchar(1) COMMENT '件名更新フラグ',
	-- 0：更新していない
	-- 1：更新している
	content_update_flg varchar(1) COMMENT '本文更新フラグ',
	-- 0：更新していない
	-- 1：更新している
	worker_update_flg varchar(1) COMMENT '作業者更新フラグ',
	-- 0：更新していない
	-- 1：更新している
	limit_dt_update_flg varchar(1) COMMENT '期日更新フラグ',
	-- 0：更新していない
	-- 1：更新している
	anken_releted_update_flg varchar(1) COMMENT '案件関連付け更新フラグ',
	-- Enum
	anken_releted_update_kbn varchar(1) COMMENT '案件関連付け更新区分',
	-- 0：更新していない
	-- 1：更新した
	check_item_update_flg varchar(1) COMMENT 'チェック項目更新フラグ',
	-- 1：登録
	-- 2：チェックリスト名を変更
	-- 3：ステータスを未完了
	-- 4：ステータスを完了
	-- 5：削除
	check_item_update_kbn varchar(1) COMMENT 'チェック項目更新区分',
	-- 更新時点のチェックリスト名
	check_item_name varchar(100) COMMENT 'チェック項目名',
	-- 0：更新していない
	-- 1：更新している
	status_update_flg varchar(1) COMMENT 'タスクステータス更新フラグ',
	-- Enum
	updated_status varchar(1) COMMENT '更新後ステータス',
	comment text COMMENT 'コメント',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (task_history_seq)
) COMMENT = 'タスク-履歴';


-- タスク-作業者
CREATE TABLE t_task_worker
(
	task_worker_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT 'タスク-作業者SEQ',
	task_seq int unsigned NOT NULL COMMENT 'タスクSEQ',
	worker_account_seq int unsigned NOT NULL COMMENT '作業者(アカウントSEQ)',
	-- 1：作成者
	-- 0：作成者以外
	creater_flg varchar(1) NOT NULL COMMENT '作成者フラグ',
	entrust_flg varchar(1) NOT NULL COMMENT '委任フラグ',
	-- 0：未確認
	-- 1：確認済み
	new_task_kakunin_flg varchar(1) NOT NULL COMMENT '新着タスク確認フラグ',
	-- 0：未確認
	-- 1：確認済み
	new_history_kakunin_flg varchar(1) NOT NULL COMMENT '新着履歴確認フラグ',
	-- 今日やるタスク実施日。システム日付と同じ場合に今日の予定画面で表示する。
	today_task_date date COMMENT '今日のタスク日付',
	-- 今日やるタスク画面の表示順
	today_task_disp_order int unsigned COMMENT '今日のタスク表示順',
	disp_order int COMMENT '表示順',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (task_worker_seq)
) COMMENT = 'タスク-作業者';


-- 問い合わせ
CREATE TABLE t_toiawase
(
	toiawase_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '問い合わせSEQ',
	subject varchar(100) NOT NULL COMMENT '件名',
	-- Enum：未定義
	toiawase_type varchar(2) COMMENT '問い合わせ種別',
	-- Enum：
	-- 1．未解決
	-- 2．返信待ち
	-- 3．解決済み
	toiawase_status varchar(1) NOT NULL COMMENT '問い合わせステータス',
	-- ユーザー側とシステム管理者側の区別をせずに登録するカラム
	shokai_created_at datetime NOT NULL COMMENT '初回作成日時',
	-- ユーザー側とシステム管理者側の区別をせずに登録するカラム
	last_update_at datetime NOT NULL COMMENT '最終更新日時',
	created_at datetime COMMENT '登録日時',
	created_by int unsigned COMMENT '登録アカウントSEQ',
	updated_at datetime COMMENT '更新日時',
	updated_by int unsigned COMMENT '更新アカウントSEQ',
	sys_created_at datetime COMMENT 'システム管理者_登録日時',
	sys_created_by int unsigned COMMENT 'システム管理者_登録アカウントSEQ',
	sys_updated_at datetime COMMENT 'システム管理者_更新日時',
	sys_updated_by int unsigned COMMENT 'システム管理者_更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (toiawase_seq)
) COMMENT = '問い合わせ';


-- 問い合わせ-添付
CREATE TABLE t_toiawase_attachment
(
	toiawase_attachment_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '添付SEQ',
	toiawase_seq int unsigned NOT NULL COMMENT '問い合わせSEQ',
	toiawase_detail_seq int unsigned NOT NULL COMMENT '問い合わせ詳細SEQ',
	file_name varchar(255) NOT NULL COMMENT 'ファイル名',
	file_extension varchar(64) COMMENT 'ファイル拡張子',
	s3_object_key varchar(1024) NOT NULL COMMENT 'S3オブジェクトキー',
	created_at datetime DEFAULT NOW() NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントSEQ',
	updated_at datetime DEFAULT NOW() NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (toiawase_attachment_seq)
) COMMENT = '問い合わせ-添付';


-- 問い合わせ-詳細
CREATE TABLE t_toiawase_detail
(
	toiawase_detail_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '問い合わせ詳細SEQ',
	toiawase_seq int unsigned NOT NULL COMMENT '問い合わせSEQ',
	body text COMMENT '内容',
	-- Enum：
	-- 1.システム管理者
	-- 2.ユーザー
	regist_type varchar(1) NOT NULL COMMENT '登録元種別',
	-- ０：未読、１：既読
	tenant_read_flg varchar(1) COMMENT 'テナント既読フラグ',
	created_at datetime COMMENT '登録日時',
	created_by int unsigned COMMENT '登録アカウントSEQ',
	updated_at datetime COMMENT '更新日時',
	updated_by int unsigned COMMENT '更新アカウントSEQ',
	-- ０：非公開、１：公開
	sys_open_flg varchar(1) COMMENT 'システム管理者_公開フラグ',
	sys_created_at datetime COMMENT 'システム管理者_登録日時',
	sys_created_by int unsigned COMMENT 'システム管理者_登録アカウントSEQ',
	sys_updated_at datetime COMMENT 'システム管理者_更新日時',
	sys_updated_by int unsigned COMMENT 'システム管理者_更新アカウントSEQ',
	-- システム管理者のみ削除可能
	sys_deleted_at datetime COMMENT 'システム管理者_削除日時',
	-- システム管理者のみ削除可能
	sys_deleted_by int unsigned COMMENT 'システム管理者_削除アカウントSEQ',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (toiawase_detail_seq)
) COMMENT = '問い合わせ-詳細';


-- 回収不能金
CREATE TABLE t_uncollectible
(
	uncollectible_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '回収不能金SEQ',
	person_id int unsigned NOT NULL COMMENT '名簿ID',
	anken_id int unsigned NOT NULL COMMENT '案件ID',
	total_uncollectible_amount decimal unsigned NOT NULL COMMENT '回収不能金額合計',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (uncollectible_seq)
) COMMENT = '回収不能金';


-- 回収不能金詳細
CREATE TABLE t_uncollectible_detail
(
	uncollectible_detail_seq int unsigned NOT NULL AUTO_INCREMENT COMMENT '回収不能金詳細SEQ',
	uncollectible_seq int unsigned NOT NULL COMMENT '回収不能金SEQ',
	uncollectible_amount decimal unsigned NOT NULL COMMENT '回収不能金額',
	created_at datetime NOT NULL COMMENT '登録日時',
	created_by int unsigned NOT NULL COMMENT '登録アカウントID',
	updated_at datetime NOT NULL COMMENT '更新日時',
	updated_by int unsigned NOT NULL COMMENT '更新アカウントID',
	version_no int unsigned NOT NULL COMMENT 'バージョンNo',
	PRIMARY KEY (uncollectible_detail_seq)
) COMMENT = '回収不能金詳細';



/* Create Indexes */

CREATE INDEX idx_tai_accg_doc_seq ON t_accg_invoice (accg_doc_seq ASC);
CREATE INDEX idx_tas_accg_doc_seq ON t_accg_statement (accg_doc_seq ASC);
CREATE INDEX idx_tac_customer_id_anken_id ON t_anken_customer (customer_id ASC, anken_id ASC);
CREATE INDEX idx_tark_kanyosha_seq ON t_anken_related_kanyosha (kanyosha_seq ASC);
CREATE INDEX idx_tark_related_kanyosha_seq ON t_anken_related_kanyosha (related_kanyosha_seq ASC);
CREATE INDEX idx_tat_anken_id ON t_anken_tanto (anken_id ASC);
CREATE INDEX idx_tf_anken_id_person_id ON t_deposit_recv (anken_id ASC, person_id ASC);
CREATE INDEX idx_tkk_customer_id ON t_kaikei_kiroku (customer_id ASC);
CREATE INDEX idx_tkk_anken_id ON t_kaikei_kiroku (anken_id ASC);
CREATE INDEX idx_tk_anken_id ON t_kanyosha (anken_id ASC);
CREATE INDEX idx_toa_person_id ON t_old_address (person_id ASC);
CREATE INDEX idx_tc_customer_type ON t_person (customer_type ASC);
CREATE INDEX idx_tcc_person_id ON t_person_contact (person_id ASC);
CREATE INDEX idx_ts_anken_id ON t_saiban (anken_id ASC);
CREATE INDEX idx_tsc_customer_id ON t_saiban_customer (customer_id ASC);
CREATE INDEX idx_tsj_saiban_seq ON t_saiban_jiken (saiban_seq ASC);
CREATE INDEX idx_tsrk_kanyosha_seq ON t_saiban_related_kanyosha (kanyosha_seq ASC);
CREATE INDEX idx_tsrk_related_kanyosha_seq ON t_saiban_related_kanyosha (related_kanyosha_seq ASC);
CREATE INDEX idx_tst_saiban_seq ON t_saiban_tanto (saiban_seq ASC);
CREATE INDEX idx_tst_parent_seq_saiban_seq ON t_saiban_tree (parent_seq ASC, saiban_seq ASC);
CREATE INDEX idx_tstr_saiban_seq ON t_saiban_tree (saiban_seq ASC);
CREATE INDEX idx_tsd_sales_seq ON t_sales_detail (sales_seq ASC);
CREATE INDEX idx_tth_task_seq ON t_task_history (task_seq ASC);



