SELECT
  mt.tenant_seq
  , mt.tenant_name
  , mt.tenant_name_kana
  , mt.tenant_type
  , mt.tenant_zip_cd
  , mt.tenant_address1
  , mt.tenant_address2
  , mt.tenant_tel_no
  , mt.tenant_fax_no
  , mt.tenant_invoice_registration_no
  , mt.tenant_stamp_img
  , mt.tenant_stamp_img_extension
  , mt.tenant_daihyo_name_sei
  , mt.tenant_daihyo_name_sei_kana
  , mt.tenant_daihyo_name_mei
  , mt.tenant_daihyo_name_mei_kana
  , mt.tenant_daihyo_mail_address
  , mt.tax_hasu_type
  , mt.hoshu_hasu_type
  , tgk.branch_no
  , tgk.label_name
  , tgk.ginko_name
  , tgk.shiten_name
  , tgk.shiten_no
  , tgk.koza_type
  , tgk.koza_no
  , tgk.koza_name
  , tgk.koza_name_kana
  , tgk.default_use_flg 
FROM
  m_tenant mt 
  LEFT JOIN t_ginko_koza tgk 
    ON mt.tenant_seq = tgk.tenant_seq AND tgk.deleted_at IS NULL AND tgk.deleted_by IS NULL
WHERE
  mt.tenant_seq = /* tenantSeq */1
ORDER BY
  tgk.default_use_flg DESC
  , tgk.created_at 
LIMIT
  1;
