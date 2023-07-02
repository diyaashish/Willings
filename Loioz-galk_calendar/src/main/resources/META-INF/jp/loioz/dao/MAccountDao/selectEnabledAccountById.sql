SELECT
  *
FROM
  m_account
WHERE
  account_id = /* accountId */'test1@test.co.jp'
  AND account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1'
  AND deleted_at IS NULL
  AND deleted_by IS NULL;