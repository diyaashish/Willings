SELECT
  tat.anken_id
  , tat.tanto_type
  -- 担当者名をカンマ区切りで取得 ： 並び順は、主担当、担当の順
  , GROUP_CONCAT(TRIM(CONCAT(COALESCE(ma.account_name_sei, ''), ' ', COALESCE(ma.account_name_mei, ''))) ORDER BY tat.anken_main_tanto_flg DESC, tat.tanto_type_branch_no ASC SEPARATOR ', ') tanto_name
FROM
  t_anken_tanto tat
  LEFT JOIN m_account ma
    ON tat.account_seq = ma.account_seq
WHERE
  tat.tanto_type IN (/* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2', /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3')
  AND tat.anken_id IN /* ankenIdList */(1)
GROUP BY
  tat.anken_id
  , tat.tanto_type
;