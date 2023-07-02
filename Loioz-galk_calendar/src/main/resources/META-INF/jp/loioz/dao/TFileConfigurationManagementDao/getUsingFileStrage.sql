SELECT
  SUM(file_size) 
FROM
  t_file_configuration_management tfcm 
  INNER JOIN t_file_detail_info_management tfdim 
    USING (file_detail_info_management_id) 
WHERE
  tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_FILE.getCd() */'2'
  AND tfdim.deleted_at IS null 
  AND tfdim.deleted_by IS null
