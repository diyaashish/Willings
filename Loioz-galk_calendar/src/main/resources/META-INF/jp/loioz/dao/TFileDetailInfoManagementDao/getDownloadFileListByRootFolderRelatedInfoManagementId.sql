-- ルートフォルダ関連情報管理IDに紐づくダウンロード対象のファイル一覧を取得する
select
  tfdim.file_detail_info_management_id,
  tfdim.file_name,
  tfdim.file_extension,
  tfdim.file_type,
  tfdim.folder_path,
  tfdim.s3_object_key ,
  tfcm.file_kubun  
from
  t_file_configuration_management tfcm 
  inner join t_file_detail_info_management tfdim 
    on tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
    and tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
    and tfdim.deleted_operation_account_seq is null
where
  tfcm.file_kubun != /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
  and tfcm.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
  /*%if targetSearchPathList.size() > 0 */
  and tfdim.folder_path IN /* targetSearchPathList */(null)
  /*%end */
  and tfdim.deleted_operation_account_seq is null
  and tfdim.deleted_by is NULL
  and tfdim.deleted_at is NULL