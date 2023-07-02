SELECT
  COUNT(*) 
FROM
  m_account 
WHERE
  deleted_at IS NULL 
  AND account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1'
  AND account_kengen = /* @jp.loioz.common.constant.CommonConstant$AccountKengen@SYSTEM_MNG */'2'
  /*%if accountSeq != null */
  AND account_seq != /*accountSeq*/'1'
  /*%end */
