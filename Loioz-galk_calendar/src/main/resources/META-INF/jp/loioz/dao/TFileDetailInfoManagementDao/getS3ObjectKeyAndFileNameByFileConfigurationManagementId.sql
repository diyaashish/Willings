-- ファイル構成管理IDからS3オブジェクトキーを取得する
select
  tfdim.folder_path,
  tfdim.s3_object_key,
  tfdim.file_name,
  tfdim.file_extension,
  tfdim.file_type
from
  t_file_configuration_management tfcm -- ファイル構成管理
  inner join t_file_detail_info_management tfdim -- ファイル詳細情報管理
    using (file_detail_info_management_id) 
where
  tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null