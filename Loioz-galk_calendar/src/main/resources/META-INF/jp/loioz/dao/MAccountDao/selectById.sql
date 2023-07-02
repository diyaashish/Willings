SELECT
  * 
FROM
  m_account ma 
WHERE
  ma.account_id = /* accountId */'test1@test.co.jp'
  AND deleted_at IS NULL
  AND deleted_by IS NULL;