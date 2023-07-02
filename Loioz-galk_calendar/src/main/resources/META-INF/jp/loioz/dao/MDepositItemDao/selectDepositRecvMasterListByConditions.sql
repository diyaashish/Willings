SELECT
  *
FROM
  m_deposit_item
WHERE
  1 = 1
  /*%if conditions.isNotEmpty(conditions.depositType) */
  AND deposit_type = /* conditions.depositType */'' 
  /*%end*/
ORDER BY
  disp_order ASC
;