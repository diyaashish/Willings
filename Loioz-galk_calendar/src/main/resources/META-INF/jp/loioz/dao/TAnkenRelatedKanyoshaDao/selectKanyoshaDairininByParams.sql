SELECT
  * 
FROM
  t_anken_related_kanyosha 
WHERE
  1 = 1
  /*%if ankenId != null */
  AND anken_id = /* ankenId */'' 
  /*%end */
  /*%if relatedKanyoshaSeq != null */
  AND related_kanyosha_seq = /* relatedKanyoshaSeq */''
  /*%end */
;