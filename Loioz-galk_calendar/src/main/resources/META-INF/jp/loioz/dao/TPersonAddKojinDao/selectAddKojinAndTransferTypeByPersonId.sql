SELECT
  tcak.person_id
  , tc.customer_id
  , tcak.old_name
  , tcak.old_name_kana
  , tcak.yago
  , tcak.yago_kana
  , tcak.gender_type
  , tcak.birthday
  , tcak.birthday_display_type
  , tcak.country
  , tcak.language
  , tcak.juminhyo_address_type
  , tcak.juminhyo_zip_code
  , tcak.juminhyo_address1
  , tcak.juminhyo_address2
  , tcak.juminhyo_remarks
  , tcak.created_at
  , tcak.created_by
  , tcak.updated_at
  , tcak.updated_by
  , tcak.version_no
  , tc.transfer_type 
FROM
  t_person_add_kojin tcak 
  LEFT OUTER JOIN t_person tc 
    USING (person_id)
WHERE
  person_id = /* personId */1;