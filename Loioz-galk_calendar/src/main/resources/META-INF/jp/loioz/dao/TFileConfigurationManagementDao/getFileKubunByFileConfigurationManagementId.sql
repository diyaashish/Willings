-- ファイル構成管理IDからファイル区分を取得する
select
  tfcm.file_kubun 
from
  t_file_configuration_management tfcm 
where
  tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null