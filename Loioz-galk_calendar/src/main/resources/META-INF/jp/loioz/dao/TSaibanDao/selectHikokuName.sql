SELECT
  TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS hikokuName
FROM
  t_saiban_customer tsc
  INNER JOIN t_person tc
    ON tsc.customer_id = tc.customer_id
WHERE
  tsc.saiban_seq = /*saibanSeq*/3
ORDER BY
  tsc.main_flg DESC;