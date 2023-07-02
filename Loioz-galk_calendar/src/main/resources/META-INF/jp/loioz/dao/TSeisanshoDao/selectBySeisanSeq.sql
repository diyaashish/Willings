SELECT
  *
FROM
  t_seisan_kiroku
WHERE
  seisan_seq = /* seisanSeq */NULL
  AND deleted_at IS NULL 
  AND deleted_by IS NULL;