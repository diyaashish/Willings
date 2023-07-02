SELECT
  *
FROM
  t_ginko_koza tgk
WHERE
  tgk.account_seq = /* accountSeq */1
  AND tgk.deleted_at IS NULL
  AND tgk.deleted_by IS NULL
ORDER BY
  tgk.default_use_flg DESC
  , tgk.created_at 
LIMIT
  1;
