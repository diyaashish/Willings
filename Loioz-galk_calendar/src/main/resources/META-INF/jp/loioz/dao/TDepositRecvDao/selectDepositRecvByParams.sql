SELECT 
  *
FROM
  t_deposit_recv tdr
WHERE
  tdr.anken_id = /*ankenId*/'1'
  AND tdr.person_id = /*personId*/'1'
  /*%if depositType != null */
  AND tdr.deposit_type = /*depositType*/'1'
  /*%end */
;
