SELECT
  tbsa.* 
FROM
  t_busho_shozoku_acct tbsa 
  LEFT JOIN m_account ma 
    ON tbsa.account_seq = ma.account_seq 
WHERE
  ma.account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1' 
  AND ma.deleted_by IS NULL 
  AND ma.deleted_at IS NULL 
ORDER BY
  tbsa.busho_id ASC
  , tbsa.disp_order ASC
;
