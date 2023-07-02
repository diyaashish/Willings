SELECT
  * 
FROM
  t_fee 
WHERE
  sales_detail_seq IS NOT NULL 
  AND fee_payment_status = /* @jp.loioz.common.constant.CommonConstant$FeePaymentStatus@DEPOSITED */'3'
  AND person_id = /* personId */NULL 
  AND anken_id = /* ankenId */NULL
;