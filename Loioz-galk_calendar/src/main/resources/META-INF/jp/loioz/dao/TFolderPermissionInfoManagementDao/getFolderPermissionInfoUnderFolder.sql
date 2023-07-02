-- ファイル構成管理IDから、対象のフォルダ配下の全てのフォルダ権限情報を取得する
( 
  select
    tfpim_parent.* 
  from
    t_file_configuration_management tfcm_parent 
    inner join t_folder_permission_info_management tfpim_parent 
      using (file_configuration_management_id) 
	inner join t_file_detail_info_management tfdim_parent
	  on tfdim_parent.file_detail_info_management_id = tfcm_parent.file_detail_info_management_id
	  and tfdim_parent.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
  where
    tfcm_parent.file_configuration_management_id = /* fileConfigurationManagementId */null
) union (
  select 
    tfpim_child.* 
  from
    t_file_configuration_management tfcm_child
	inner join t_folder_permission_info_management tfpim_child
	 using (file_configuration_management_id) 
	inner join t_file_detail_info_management tfdim_child
	 on tfdim_child.file_detail_info_management_id = tfcm_child.file_detail_info_management_id
	 and tfdim_child.folder_path like /* folderPath */null
	 and tfdim_child.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
  where
     tfcm_child.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
) 