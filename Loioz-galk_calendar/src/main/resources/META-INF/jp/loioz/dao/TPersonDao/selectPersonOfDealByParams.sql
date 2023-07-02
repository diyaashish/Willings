SELECT
  tp.* 
FROM
  t_person tp
  INNER JOIN (SELECT customer_id FROM t_anken_customer GROUP BY customer_id) tac
    ON tp.person_id = tac.customer_id
WHERE
  ( 
    (
      REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%' 
      OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%'
    ) 
    OR ( 
      REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%' 
      OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%'
    )
  ) 
/*%if limitCount != null*/
LIMIT /* limitCount */0 
/*%end */
;
