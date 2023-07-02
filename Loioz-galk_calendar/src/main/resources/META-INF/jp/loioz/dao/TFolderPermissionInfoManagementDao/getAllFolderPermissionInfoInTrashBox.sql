-- ゴミ箱内の全てのフォルダ閲覧権限情報を取得する
select
  tfpim.* 
from
  t_folder_permission_info_management tfpim 
  inner join t_file_configuration_management tfcm 
    on tfcm.file_configuration_management_id = tfpim.file_configuration_management_id
	and tfcm.file_kubun != /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
  inner join t_file_detail_info_management tfdim 
    on tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id 
	and tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
	/*%if isSystemManager */
		and tfdim.deleted_operation_account_seq is not null
	/*%else */
		and tfdim.deleted_operation_account_seq = /* loginAccountSeq */null
	/*%end */
