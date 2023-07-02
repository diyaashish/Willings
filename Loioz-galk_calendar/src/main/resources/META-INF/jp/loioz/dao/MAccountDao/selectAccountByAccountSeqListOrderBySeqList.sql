SELECT
  *
FROM
  m_account
WHERE
  account_seq IN /* accountSeqList */(null)
  AND deleted_by IS NULL
  AND account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1' 
   /*# orderByCondition */;