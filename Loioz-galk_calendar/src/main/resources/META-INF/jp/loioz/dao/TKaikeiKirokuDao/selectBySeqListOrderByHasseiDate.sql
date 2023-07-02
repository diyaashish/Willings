SELECT
  * 
FROM
  t_kaikei_kiroku 
WHERE
  kaikei_kiroku_seq IN /* kaikeiSeqList */(1) 
ORDER BY
/*%if orderByHasseiDate */
  hassei_date ASC,created_at ASC
/*%else */
  kaikei_kiroku_seq
/*%end */
;
