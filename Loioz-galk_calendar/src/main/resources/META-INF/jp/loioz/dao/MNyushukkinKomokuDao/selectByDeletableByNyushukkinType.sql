SELECT
  * 
FROM
  m_nyushukkin_komoku 
WHERE
  nyushukkin_type = /* nyushukkinType */null
  AND deleted_at IS NULL 
  AND deleted_by IS NULL
  AND undeletable_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
  AND disabled_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
ORDER BY
  disp_order ASC;