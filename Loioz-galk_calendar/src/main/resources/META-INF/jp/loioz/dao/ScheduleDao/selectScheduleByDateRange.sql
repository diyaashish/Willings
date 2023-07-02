SELECT
  * 
FROM
  t_schedule 
WHERE
  ( 
    ( 
      date_from <= /* dateTo */'2020/01/01' 
      OR date_from IS NULL
    ) 
    AND ( 
      date_to >= /* dateFrom */'2019/01/01' 
      OR date_to IS NULL
    )
  ) 
  AND deleted_at IS NULL 
  AND deleted_by IS NULL;