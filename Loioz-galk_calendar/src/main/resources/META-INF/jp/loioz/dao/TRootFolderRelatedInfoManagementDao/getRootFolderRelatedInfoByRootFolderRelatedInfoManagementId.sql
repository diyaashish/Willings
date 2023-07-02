-- ルートフォルダ関連情報管理IDからルートフォルダ関連情報を取得する
select
  trfrim.root_folder_related_info_management_id     -- ルートフォルダ関連情報管理ID
  , trfrim.anken_id
  , ta.anken_name
  , trfrim.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),COALESCE(tc.customer_name_mei,''))) as customer_name
from
  t_root_folder_related_info_management trfrim
  left outer join t_anken ta -- 案件情報
    on trfrim.anken_id = ta.anken_id
  left outer join t_person tc -- 名簿情報
    on trfrim.customer_id = tc.customer_id 
where
  trfrim.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null