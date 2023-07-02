SELECT
  count(*) 
FROM
  t_advisor_contract tac 
WHERE
  tac.person_id = /* personId */null
  AND tac.contract_status IN /* contractStatusCdList */('1', '2');