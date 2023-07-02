SELECT
  tcah.person_id
  , tc.customer_id
  , tcah.daihyo_name
  , tcah.daihyo_name_kana
  , tcah.daihyo_position_name
  , tcah.tanto_name
  , tcah.tanto_name_kana
  , tcah.toki_address_type
  , tcah.toki_zip_code
  , tcah.toki_address1
  , tcah.toki_address2
  , tcah.toki_address_remarks
  , tcah.created_at
  , tcah.created_by
  , tcah.updated_at
  , tcah.updated_by
  , tcah.version_no
  , tc.transfer_type 
FROM
  t_person_add_hojin tcah 
  LEFT OUTER JOIN t_person tc 
    USING (person_id)
WHERE
  person_id = /* personId */1;