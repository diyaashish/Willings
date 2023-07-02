SELECT
  COALESCE(MAX(disp_order), 0) AS disp_max
FROM
  t_task_check_item
WHERE
  task_seq = /*taskSeq*/'1'
;