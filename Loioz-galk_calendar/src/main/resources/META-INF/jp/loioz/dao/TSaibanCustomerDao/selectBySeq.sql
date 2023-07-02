SELECT
  tsc.saiban_seq
  , tsc.customer_id
  , tsc.saiban_tojisha_hyoki
  , tsc.main_flg
  , tc.person_id
  , tc.customer_name_sei
  , tc.customer_name_sei_kana
  , tc.customer_name_mei
  , tc.customer_name_mei_kana
  , tc.customer_flg
  , tc.advisor_flg
  , tc.customer_type
FROM
  t_saiban_customer tsc 
  INNER JOIN t_person tc 
    ON tsc.customer_id = tc.customer_id 
WHERE
  tsc.saiban_seq = /* saibanSeq */1 
ORDER BY
  tsc.customer_id
;
