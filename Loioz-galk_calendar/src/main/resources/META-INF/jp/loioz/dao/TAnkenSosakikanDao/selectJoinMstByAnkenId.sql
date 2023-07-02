SELECT
  tas.sosakikan_seq
  , tas.sosakikan_id
  , CASE
    WHEN tas.sosakikan_id IS NULL
      THEN tas.sosakikan_name
    ELSE ms.sosakikan_name
    END AS sosakikan_name
  , tas.sosakikan_tanto_bu
  , tas.sosakikan_tel_no
  , tas.sosakikan_extension_no
  , tas.sosakikan_fax_no
  , tas.sosakikan_room_no
  , tas.tantosha1_name
  , tas.tantosha2_name
  , tas.remarks
  , ms.sosakikan_zip
  , ms.sosakikan_address1
  , ms.sosakikan_address2
FROM
  t_anken_sosakikan tas
  LEFT JOIN m_sosakikan ms
    ON tas.sosakikan_id = ms.sosakikan_id
WHERE
  tas.anken_id = /* ankenId */1
ORDER BY 
  tas.created_at
;
