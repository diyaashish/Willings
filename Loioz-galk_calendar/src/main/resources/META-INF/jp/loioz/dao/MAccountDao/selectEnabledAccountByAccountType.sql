SELECT
  *
FROM
  m_account
WHERE
  account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */null
  AND account_type = /* accountType */null
  AND deleted_at IS NULL
  AND deleted_by IS NULL;