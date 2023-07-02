SELECT
  * 
FROM
  m_select_list 
WHERE
  deleted_at IS NULL 
  AND deleted_by IS NULL
  OR select_seq = /* sodanRoute */null
  OR select_seq = /* addInfo */null
ORDER BY
  disp_order;