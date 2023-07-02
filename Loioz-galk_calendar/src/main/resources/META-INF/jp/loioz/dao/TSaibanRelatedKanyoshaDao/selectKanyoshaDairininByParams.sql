SELECT
  * 
FROM
  t_saiban_related_kanyosha 
WHERE
  1 = 1
  /*%if saibanSeq != null */
  AND saiban_seq = /* saibanSeq */'' 
  /*%end */
  /*%if relatedKanyoshaSeq != null */
  AND related_kanyosha_seq = /* relatedKanyoshaSeq */''
  /*%end */
;