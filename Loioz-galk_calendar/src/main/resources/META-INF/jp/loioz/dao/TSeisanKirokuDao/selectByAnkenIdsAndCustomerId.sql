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
  , ta.anken_name
  , ta.bunya_id
FROM
  t_seisan_kiroku tsk 
  LEFT OUTER JOIN t_anken ta 
    ON tsk.anken_id = ta.anken_id
WHERE
  tsk.customer_id = /* customerId */NULL 
  AND tsk.anken_id IN /* ankenIds */(NULL)
  AND deleted_at IS NULL
  AND deleted_by IS NULL
ORDER BY
  tsk.seisan_id ASC; 