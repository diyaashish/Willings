SELECT
  tpal.* 
FROM
  t_person tc
  INNER JOIN t_person_add_lawyer tpal
    ON tc.person_id = tpal.person_id
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER */'2' 
WHERE
  tc.person_id IN /* personId */(NULL)
;