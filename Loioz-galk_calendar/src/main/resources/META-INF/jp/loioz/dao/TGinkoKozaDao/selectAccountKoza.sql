SELECT
  * 
FROM
  t_ginko_koza 
WHERE
  tenant_seq IS NULL
  AND deleted_at IS NULL
  AND deleted_by IS NULL
ORDER BY account_seq,branch_no;
