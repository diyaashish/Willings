SELECT
    * 
FROM
    t_person 
WHERE
    1=1
    -- 弁護士は除く
    AND customer_type != /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER */'2'
    /*%if customerIdList != null && customerIdList.size() > 0*/
    AND customer_id NOT IN /* customerIdList */(1)
    /*%end */
    AND  (
        ( 
          REPLACE (CONCAT(COALESCE(customer_name_sei,''),COALESCE(customer_name_mei,'')), '　', '') collate utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%' 
          OR REPLACE (CONCAT(COALESCE(customer_name_sei,''),COALESCE(customer_name_mei,'')), ' ', '') collate utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%'
        ) 
        OR ( 
          REPLACE (CONCAT(COALESCE(customer_name_sei_kana,''),COALESCE(customer_name_mei_kana,'')), '　', '') collate utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%' 
          OR REPLACE (CONCAT(COALESCE(customer_name_sei_kana,''),COALESCE(customer_name_mei_kana,'')), ' ', '') collate utf8mb4_unicode_ci LIKE /* @infix(searchName) */'%'
        ) 
    )
/*%if limitCount != null*/
LIMIT /* limitCount */0
/*%end */
;