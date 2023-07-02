SELECT
  ma.account_seq
  , CONCAT(COALESCE(ma.account_name_sei,''),' ',COALESCE(ma.account_name_mei,'')) AS account_name
  , GROUP_CONCAT(tbsa.busho_id ORDER BY tbsa.busho_id) AS busho_id 
FROM
  m_account ma 
  LEFT JOIN t_busho_shozoku_acct tbsa 
    USING (account_seq) 
WHERE
  ma.account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1' 
  AND deleted_by IS NULL 
GROUP BY
  ma.account_seq 
ORDER BY
  ma.account_seq;