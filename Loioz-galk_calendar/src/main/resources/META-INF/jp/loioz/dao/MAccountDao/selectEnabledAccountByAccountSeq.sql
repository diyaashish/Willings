SELECT
  *
FROM
  m_account
WHERE
  account_seq IN /* accountSeqList */(1)
  AND account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1'
  AND deleted_at IS NULL
  AND deleted_by IS NULL;