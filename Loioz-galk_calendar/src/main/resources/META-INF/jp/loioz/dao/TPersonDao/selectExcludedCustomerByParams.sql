SELECT
  * 
FROM
  t_person 
WHERE
  1=1
  /*%if excludeCustomerIdList != null && excludeCustomerIdList.size() > 0*/
  AND customer_id NOT IN /* excludeCustomerIdList */(1) 
  /*%end */
  AND ( 
    (
      REPLACE (CONCAT(COALESCE(customer_name_sei, ''), COALESCE(customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%' 
      OR REPLACE (CONCAT(COALESCE(customer_name_sei, ''), COALESCE(customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%'
    ) 
    OR ( 
      REPLACE (CONCAT(COALESCE(customer_name_sei_kana, ''), COALESCE(customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%' 
      OR REPLACE (CONCAT(COALESCE(customer_name_sei_kana, ''), COALESCE(customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%'
    )
  ) 
/*%if limitCount != null*/
LIMIT /* limitCount */0 
/*%end */
;
