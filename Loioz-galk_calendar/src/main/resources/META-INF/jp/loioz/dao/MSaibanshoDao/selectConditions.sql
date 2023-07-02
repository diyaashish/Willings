SELECT
  * 
FROM
  m_saibansho ms 
where
  1 = 1 
  AND ms.deleted_at IS NULL 
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.todofukenId) */
  AND ms.todofuken_id = /* searchForm.todofukenId */0 
/*%end */
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.saibanshoName) */
  AND ms.saibansho_name LIKE /* @infix(searchForm.saibanshoName) */'%' 
/*%end */
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.saibanshoAddress) */
  AND ( 
    ms.saibansho_address1 LIKE /* @infix(searchForm.saibanshoAddress) */'%' 
    OR ms.saibansho_address2 LIKE /* @infix(searchForm.saibanshoAddress) */'%'
  ) 
/*%end */
ORDER BY
  ms.todofuken_id
  , ms.saibansho_name
