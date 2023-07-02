SELECT
  tgk.ginko_account_seq
  , tgk.label_name
  , tgk.ginko_name
  , tgk.shiten_name
  , tgk.shiten_no
  , tgk.koza_type
  , tgk.koza_no
  , tgk.koza_name
  , tgk.tenant_seq
  , tgk.account_seq
  , mt.tenant_name
  , CONCAT(COALESCE(ma.account_name_sei,''),' ',COALESCE(ma.account_name_mei,'')) AS account_name
FROM
  t_ginko_koza tgk 
  LEFT JOIN m_tenant mt 
    ON mt.tenant_seq = tgk.tenant_seq
  LEFT JOIN m_account ma 
    ON tgk.account_seq = ma.account_seq 
WHERE
  tgk.ginko_account_seq = /* ginkoAccountSeq */null
