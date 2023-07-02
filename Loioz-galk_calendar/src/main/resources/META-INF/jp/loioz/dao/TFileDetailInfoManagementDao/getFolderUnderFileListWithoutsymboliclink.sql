-- フォルダ配下のファイル一覧を取得する
select
  sub_tfcm.file_configuration_management_id       -- ファイル構成管理ID
  , sub_tfcm.root_folder_related_info_management_id -- ルートフォルダ関連情報管理ID
  , mfdp.display_priority                         -- 表示優先順位
  , sub_tfcm.file_kubun                           -- ファイル区分
  , tfdim.file_detail_info_management_id          -- ファイル詳細情報管理ID
  , tfdim.file_name                               -- ファイル名
  , tfdim.file_extension                         -- ファイル拡張子
  , tfdim.file_type                               -- ファイルタイプ
  , tfdim.file_extension                          -- ファイル拡張子
  , tfdim.file_type                               -- ファイルタイプ
  , tfdim.folder_path                             -- フォルダパス
  , tfdim.file_created_datetime                   -- ファイル作成日時
  , tfdim.upload_datetime                         -- アップロード日時
  , tfdim.upload_user                             -- アップロードユーザー
  , tfdim.file_size                               -- ファイルサイズ
  , tfpim.view_limit                              -- 閲覧制限
  , ta.anken_id                              -- 案件ID
  , ta.anken_name                              -- 案件名
  , tc.customer_id                              -- 顧客ID
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) as customer_name                              -- 顧客名
from
  t_file_configuration_management tfcm -- ファイル構成管理
  inner join t_file_configuration_management sub_tfcm -- ファイル構成管理(子)
    on sub_tfcm.file_kubun != /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
    and sub_tfcm.parent_file_configuration_management_id = tfcm.file_configuration_management_id
  inner join t_file_detail_info_management tfdim -- ファイル詳細情報管理
    on tfdim.file_detail_info_management_id = sub_tfcm.file_detail_info_management_id 
    and tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
    and tfdim.deleted_at IS NULL
    and tfdim.deleted_by IS NULL
  left join t_folder_permission_info_management tfpim -- フォルダ権限情報管理
    on tfpim.file_configuration_management_id = sub_tfcm.file_configuration_management_id 
  inner join m_file_management_display_priority mfdp -- ファイル管理表示優先順位マスタ
    on mfdp.file_management_display_priority_id = sub_tfcm.file_management_display_priority_id 
  inner join t_root_folder_related_info_management trfrim -- マスタ
    on tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
  left outer join t_anken ta -- 案件情報
    on trfrim.anken_id = ta.anken_id
  left outer join t_person tc -- 名簿情報
    on trfrim.customer_id = tc.customer_id 
where
  tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null
order by
  mfdp.display_priority
  , tfdim.file_name