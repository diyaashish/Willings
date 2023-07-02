SELECT
  *
FROM
  m_select_list ms
WHERE
  ms.select_type = /* @jp.loioz.common.constant.CommonConstant$SelectType@SODANKEIRO */'1'
/*%if @isNotEmpty(searchForm.selectVal) */
  and ms.select_val LIKE /* @infix(searchForm.selectVal) */null
/*%end*/
ORDER BY
  ms.disp_order;