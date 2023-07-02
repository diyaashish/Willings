SELECT
  * 
FROM
  m_mail_template 
WHERE
  default_use_flg = 1 
  AND template_type = /* mailTemplateType.cd */NULL
;