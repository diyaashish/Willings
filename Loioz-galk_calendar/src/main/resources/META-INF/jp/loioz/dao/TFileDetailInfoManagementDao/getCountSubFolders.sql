-- シンボリックリンクを含めないフォルダ内のフォルダ件数を取得
   select
	count(*)
    from
      t_file_configuration_management tfcm_sub 
      inner join t_file_detail_info_management tfdim_sub
        on tfdim_sub.file_detail_info_management_id = tfcm_sub.file_detail_info_management_id
		and tfdim_sub.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
        and tfdim_sub.deleted_operation_account_seq is null
		and tfdim_sub.deleted_at IS NULL
		and tfdim_sub.deleted_by IS NULL 
	where
     tfcm_sub.parent_file_configuration_management_id = /* fileConfigurationManagementId */null
      and tfcm_sub.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_FOLDER.getCd() */'1'