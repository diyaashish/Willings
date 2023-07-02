SELECT
  *
FROM
    t_accg_doc_last_used_number tadlun
WHERE
    tadlun.accg_doc_type = /* accgDocType */'1'
    AND tadlun.numbering_type = /* numberingType */'1'
;