SELECT
  * 
FROM
  t_task_anken tta 
  INNER JOIN t_anken ta 
    ON tta.anken_id = ta.anken_id 
WHERE
  tta.account_seq = /* accountSeq */'1' 
ORDER BY
  tta.disp_order IS NULL ASC, disp_order
  , tta.created_at
;