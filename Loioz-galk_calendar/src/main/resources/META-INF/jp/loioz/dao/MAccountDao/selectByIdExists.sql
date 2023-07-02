SELECT
  *
FROM
  m_account ma
WHERE
  ma.account_id = /* accountId */'test@test.co.jp'
/*%if accountSeq != null */
  AND ma.account_seq <> /* accountSeq */0
/*%end */
  AND ma.deleted_at IS NULL
  AND ma.deleted_by IS NULL
ORDER BY
  ma.account_seq;