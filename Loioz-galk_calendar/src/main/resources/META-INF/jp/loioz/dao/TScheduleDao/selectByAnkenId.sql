SELECT
  * 
FROM
  t_schedule 
WHERE
  anken_id = /* ankenId */1
AND deleted_at IS NULL
AND deleted_by IS NULL;