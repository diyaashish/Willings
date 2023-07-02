SELECT
  tsak.saiban_seq
  , tsak.main_jiken_seq
  , tsak.sosakikan_id
  , case 
    when tsak.sosakikan_id 
      then ms.sosakikan_name 
    else tsak.kensatsucho_name 
    end as kensatsucho_name 
  , tsak.kensatsucho_tanto_bu_name
  , tsak.kensatsukan_name
  , tsak.kensatsukan_name_kana
  , tsak.jimukan_name
  , tsak.jimukan_name_kana
  , tsak.kensatsu_tel_no
  , tsak.kensatsu_extension_no
  , tsak.kensatsu_fax_no
  , tsak.kensatsu_room_no
  , tsak.kensatsu_remarks 
FROM
  t_saiban_add_keiji tsak 
  LEFT JOIN m_sosakikan ms 
    USING (sosakikan_id) 
WHERE
  saiban_seq = /* saibanSeq */1;
