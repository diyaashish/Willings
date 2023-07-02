SELECT
  tc.person_id
  , tc.customer_id
  , tc.customer_type
  , tc.customer_name_sei
  , tc.customer_name_sei_kana
  , tc.customer_name_mei
  , tc.customer_name_mei_kana
  , tc.zip_code
  , tc.address1
  , tc.address2
  , tc.address_remarks
  , tc.contact_type
  , tc.transfer_address_type
  , tc.transfer_zip_code
  , tc.transfer_address1
  , tc.transfer_address2
  , tc.transfer_name
  , tc.transfer_type
  , tc.ginko_name
  , tc.shiten_name
  , tc.shiten_no
  , tc.koza_type
  , tc.koza_no
  , tc.koza_name
  , tc.koza_name_kana
  , tcak.juminhyo_address_type
  , tcak.juminhyo_zip_code
  , tcak.juminhyo_address1
  , tcak.juminhyo_address2
  , tcak.job
  , tcak.work_place
  , tcak.busho_name AS kojin_busho_name
  , tcah.daihyo_name
  , tcah.daihyo_name_kana
  , tcah.daihyo_position_name
  , tcah.tanto_name
  , tcah.tanto_name_kana
  , tcah.toki_address_type
  , tcah.toki_zip_code
  , tcah.toki_address1
  , tcah.toki_address2
  , tpal.jimusho_name
  , tpal.busho_name AS lawyer_busho_name
FROM
  t_person tc
  LEFT JOIN t_person_add_kojin tcak
    USING (person_id)
  LEFT JOIN t_person_add_hojin tcah
    USING (person_id)
  LEFT JOIN t_person_add_lawyer tpal
    USING (person_id)
WHERE
  tc.person_id = /* personId */1;
