SELECT
  taai.anken_item_seq
  , taai.anken_id
  , taai.azukari_status
  , taai.hinmoku
  , taai.azukari_count
  , taai.hokan_place
  , taai.azukari_date
  , taai.azukari_from_type
  , taai.azukari_from_customer_id
  , taai.azukari_from_kanyosha_seq
  , taai.azukari_from
  , taai.azukari_date
  , taai.return_limit_date
  , taai.return_date
  , taai.return_to
  , taai.return_to_customer_id
  , taai.return_to_kanyosha_seq
  , taai.return_to_type
  , taai.remarks
  , taai.last_edit_at
  , taai.last_edit_by
  , taai.created_at
  , taai.created_by
  , taai.updated_at
  , taai.updated_by
  , taai.version_no 
FROM
  t_anken_azukari_item taai
WHERE
  azukari_from_kanyosha_seq IN ( 
    SELECT
      kanyosha_seq 
    FROM
      t_kanyosha 
    WHERE
      person_id = /* personId */1
  ) 
  OR return_to_kanyosha_seq IN ( 
    SELECT
      kanyosha_seq 
    FROM
      t_kanyosha 
    WHERE
      person_id = /* personId */1
  )
;
