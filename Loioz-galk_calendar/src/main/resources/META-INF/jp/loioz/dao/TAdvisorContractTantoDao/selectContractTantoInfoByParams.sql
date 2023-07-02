SELECT
  tac.advisor_contract_seq
  , tact.tanto_type
  , tact.tanto_type_branch_no
  , tact.main_tanto_flg
  , TRIM( 
    CONCAT( 
      COALESCE(ma.account_name_sei, '')
      , ' '
      , COALESCE(ma.account_name_mei, '')
    )
  ) AS tanto_name 
FROM
  t_advisor_contract tac 
  INNER JOIN t_advisor_contract_tanto tact 
    ON tac.advisor_contract_seq = tact.advisor_contract_seq 
  LEFT OUTER JOIN m_account ma 
    ON tact.account_seq = ma.account_seq 
WHERE
  tact.advisor_contract_seq IN /* advisorContractSeqList */(1) 
ORDER BY
  tact.advisor_contract_tanto_seq
  , ma.account_seq;
