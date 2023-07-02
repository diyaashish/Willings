SELECT
  * 
FROM
  t_saiban_jiken tsj
  INNER JOIN t_saiban_add_keiji tsak
  ON  tsj.jiken_seq = tsak.main_jiken_seq
WHERE
  tsj.saiban_seq IN /* keijiSaibanSeqList */(1);