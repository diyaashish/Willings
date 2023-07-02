SELECT
  tsk.seisan_seq
  , tsk.seisan_id
  , tsk.seisan_status
  , tsk.seisan_kubun
  , tsk.seikyu_type
  , tsk.anken_id
  , tsk.customer_id
  , tsk.seisan_gaku
  , tsk.tekiyo
  , tsk.created_at
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
FROM
  t_seisan_kiroku tsk 
  LEFT OUTER JOIN t_person tc 
    ON tsk.customer_id = tc.customer_id
WHERE
  tsk.anken_id = /* ankenId */NULL 
  AND tsk.customer_id IN /* customerIds */(NULL)
  AND deleted_at IS NULL
  AND deleted_by IS NULL
ORDER BY
  tsk.seisan_id ASC; 