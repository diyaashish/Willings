SELECT
  tp.person_id
  , tp.customer_flg
  , tp.advisor_flg
  , tp.customer_type
  , tp.customer_name_sei
  , tp.customer_name_mei
  , tp.customer_name_sei_kana
  , tp.customer_name_mei_kana
  , tp.zip_code
  , tp.address1
  , tp.address2
FROM
  t_person tp 
WHERE
  tp.person_id IN /* personIdList */(null)
ORDER BY
  tp.person_id DESC , tp.created_at DESC
;