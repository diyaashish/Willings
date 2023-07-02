SELECT 
  *
FROM
  t_task_anken tta 
WHERE
  tta.anken_id = /* ankenId */'1'
  /*%if accountSeq != null */
  AND tta.account_seq = /* accountSeq */'1'
  /*%end */
ORDER BY
  created_at
;