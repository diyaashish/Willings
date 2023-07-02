SELECT
  * 
FROM
  m_sosakikan 
WHERE
  1 = 1 
  AND deleted_at IS NULL
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.sosakikanName) */
  AND sosakikan_name LIKE /* @infix(searchForm.sosakikanName) */''
/*%end */
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.sosakikanTelNo) */
  AND sosakikan_tel_no LIKE /* @infix(searchForm.sosakikanTelNo) */''
/*%end */
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.sosakikanFaxNo) */
  AND sosakikan_fax_no LIKE /* @infix(searchForm.sosakikanFaxNo) */''
/*%end */
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.todofukenId) */
  AND todofuken_id = /* searchForm.todofukenId */0 
/*%end */
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.sosakikanType) */
  AND sosakikan_type = /* searchForm.sosakikanType */''
/*%end */
ORDER BY
  todofuken_id