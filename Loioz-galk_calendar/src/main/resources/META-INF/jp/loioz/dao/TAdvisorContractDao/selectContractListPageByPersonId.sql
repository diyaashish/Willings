SELECT
  * 
FROM
  t_advisor_contract tkc 
WHERE
  tkc.person_id = /* personId */null
ORDER BY
  tkc.contract_status
  , tkc.contract_start_date DESC 
  , tkc.contract_end_date ASC
  , tkc.contract_month_charge ASC
;