-- ファイル構成管理IDから該当のフォルダ配下のフォルダ一覧を取得する
select
  tfcm_child.file_configuration_management_id
      , mfmdp.display_priority as display_priority   -- 表示優先順位
      , tfcm_child.file_kubun                             -- ファイル区分
      , tfdim.file_detail_info_management_id        -- ファイル詳細情報管理ID
      , tfdim.file_name                             -- ファイル名
      , tfdim.folder_path                           -- フォルダパス
      , tfdim.file_created_datetime                 -- ファイル作成日時
      , tfdim.upload_datetime                       -- アップロード日時
      , tfdim.upload_user                           -- アップロードユーザー
      , tfdim.file_size                             -- ファイルサイズ
      , tfpim.view_limit                            -- 閲覧制限
	  , trfrim.anken_id as anken_id                                -- 案件ID
	  , NULL as anken_name                            -- 案件名
	  , trfrim.anken_id as customer_id                            -- 顧客ID
	  , NULL as customer_name                            -- 顧客名
from
  t_file_configuration_management tfcm_parent 
  inner join t_file_configuration_management tfcm_child 
    on tfcm_child.parent_file_configuration_management_id = tfcm_parent.file_configuration_management_id
  inner join t_file_detail_info_management tfdim 
    on tfdim.file_detail_info_management_id = tfcm_child.file_detail_info_management_id 
  inner join t_root_folder_related_info_management trfrim  
    on tfcm_child.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
    and tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0' 
    and tfdim.deleted_by IS NULL 
    and tfdim.deleted_at IS NULL 
    and tfdim.deleted_operation_account_seq is null 
  inner join m_file_management_display_priority mfmdp 
    on mfmdp.file_management_display_priority_id = tfcm_child.file_management_display_priority_id 
  inner join t_folder_permission_info_management tfpim 
    on tfpim.file_configuration_management_id = tfcm_child.file_configuration_management_id 
where
  tfcm_parent.file_configuration_management_id = /* fileConfigurationManagementId */null