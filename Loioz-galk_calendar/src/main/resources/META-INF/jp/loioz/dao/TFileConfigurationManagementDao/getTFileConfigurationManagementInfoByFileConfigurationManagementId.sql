-- ファイル構成管理IDからファイル構成管理情報を取得する
select
  tfcm.* 
from
  t_file_configuration_management tfcm 
where
  tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null
