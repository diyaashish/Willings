SELECT
  * 
FROM
  t_deposit_recv
WHERE
  anken_id = /* ankenId */NULL 
  AND person_id = /* personId */NULL 
  AND deposit_complete_flg != '0' 
  /** tenant_bear_flgはNULLの場合も許容する */
  AND (tenant_bear_flg IS NULL OR tenant_bear_flg != '1')
ORDER BY
 deposit_date ASC
;