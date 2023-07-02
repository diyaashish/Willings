-- 検索条件に該当するファイル詳細情報を取得する
select
  tfcm.file_configuration_management_id           -- ファイル構成管理ID
  , tfcm.parent_file_configuration_management_id  -- 親ファイル構成管理ID
  , tfcm.file_kubun                               -- ファイル区分
  , tfdim.file_name                               -- ファイル名
  , tfdim.file_extension                          -- ファイル拡張子
  , tfdim.file_type                               -- ファイルタイプ
  , tfdim.folder_path                             -- フォルダパス
  , tfpim.view_limit                              -- 閲覧制限
  , trfrim.anken_id                               -- 案件ID
  , ta.anken_name                              -- 案件名
  , trfrim.customer_id                            -- 顧客ID
  ,TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name                            -- 顧客名
  , root_folder_tfdim.file_name as root_folder_name -- ルートフォルダ名
from
  t_file_detail_info_management tfdim -- ファイル詳細情報管理
  inner join t_file_configuration_management tfcm -- ファイル構成管理
    using (file_detail_info_management_id)
  left join t_folder_permission_info_management tfpim -- フォルダ権限情報管理
    using (file_configuration_management_id)
  inner join m_file_management_display_priority mfmdp -- ファイル管理表示優先順位マスタ
    using (file_management_display_priority_id)
  inner join t_root_folder_related_info_management trfrim -- ルートフォルダ関連情報管理
    using (root_folder_related_info_management_id) 
  inner join t_file_configuration_management root_folder_tfcm -- ルートフォルダのファイル構成管理
    on root_folder_tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
    and root_folder_tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0' 
  inner join t_file_detail_info_management root_folder_tfdim  -- ルートフォルダのファイル詳細情報管理
    on root_folder_tfdim.file_detail_info_management_id = root_folder_tfcm.file_detail_info_management_id
  left outer join t_anken ta -- 案件情報
    on trfrim.anken_id = ta.anken_id
  left outer join t_person tc -- 名簿情報
    on trfrim.customer_id = tc.customer_id 
where
  tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
  and tfcm.file_kubun !=  /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0' 
  and tfdim.deleted_operation_account_seq is null
/*%if searchCondition.isNotEmpty(searchCondition.fileName) */
  and tfdim.file_name like /* searchCondition.fileName */null
/*%end */
/*%if searchCondition.isNotEmpty(searchCondition.fileKubun) */
  and tfcm.file_kubun = /* searchCondition.fileKubun */null
/*%end */
/*%if searchCondition.isNotEmpty(searchCondition.fileType) */
  and tfdim.file_type like /* searchCondition.fileType */null
/*%end */
/*%if targetSearchPathList.size() > 0 */
  and tfdim.folder_path NOT IN /* targetSearchPathList */(null)
/*%end */
/*%if searchCondition.isNotEmpty(searchCondition.uploadUser) */
  and tfdim.upload_user like /* searchCondition.uploadUser */null
/*%end */
  and tfdim.deleted_by IS NULL
  and tfdim.deleted_at IS NULL
order by
  tfdim.file_name
  , mfmdp.display_priority