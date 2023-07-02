-- 現在階層から下の階層に対して、フォルダ名検索を行い、該当するフォルダ一覧を取得する
select
  tfcm.file_configuration_management_id     -- ファイル構成管理ID
  , mfdp.display_priority                   -- 表示優先順位
  , tfcm.file_kubun                         -- ファイル区分
  , tfdim.file_detail_info_management_id    -- ファイル詳細情報管理ID
  , tfdim.file_name                         -- ファイル名
  , tfdim.folder_path                       -- フォルダパス
  , tfdim.file_created_datetime             -- ファイル作成日時
  , tfdim.upload_datetime                   -- アップロード日時
  , tfdim.upload_user                       -- アップロードユーザー
  , tfdim.file_size                         -- ファイルサイズ
  , tfpim.view_limit                        -- 閲覧制限
  , trfrim.anken_id                        -- 案件ID
from
  t_file_detail_info_management tfdim       -- ファイル詳細情報管理
  inner join t_file_configuration_management tfcm -- ファイル構成管理
    on tfcm.file_detail_info_management_id = tfdim.file_detail_info_management_id 
  inner join m_file_management_display_priority mfdp -- ファイル管理表示優先順位マスタ
    on mfdp.file_management_display_priority_id = tfcm.file_management_display_priority_id 
  left join t_folder_permission_info_management tfpim -- フォルダ権限情報管理
    on tfpim.file_configuration_management_id = tfcm.file_configuration_management_id 
  inner join t_root_folder_related_info_management trfrim 
    on tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
where
  tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
  and trfrim.anken_id IS NOT NULL
  and tfdim.deleted_operation_account_seq is null
  and tfdim.deleted_by is NULL
  and tfdim.deleted_at is NULL
order by
  mfdp.display_priority
  ,tfdim.file_name