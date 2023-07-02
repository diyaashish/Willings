SELECT
  *
FROM
  m_group mg
WHERE
  1 = 1
  AND mg.deleted_at IS NULL
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.groupName) */
  AND mg.busho_name = /* searchForm.groupName */null
/*%end */
ORDER BY
  mg.disp_order,
  mg.updated_at desc
;