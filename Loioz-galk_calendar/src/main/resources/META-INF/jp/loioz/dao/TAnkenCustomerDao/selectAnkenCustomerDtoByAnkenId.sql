SELECT
  tac.anken_id
  , tc.person_id
  , tac.customer_id
  , tac.shokai_mendan_date
  , tac.shokai_mendan_schedule_seq
  , tac.junin_date
  , tac.jiken_kanryo_date
  , tac.kanryo_date
  , tac.kanryo_flg
  , tac.anken_status 
FROM
  t_anken_customer tac 
  INNER JOIN t_person tc 
    USING (customer_id)
WHERE
  tac.anken_id IN /* ankenId */(NULL)
;