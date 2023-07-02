SELECT
  COALESCE(MAX(disp_order), 0) AS disp_max 
FROM
  t_task_anken 
WHERE
  account_seq = /*accountSeq*/'1'
;