SELECT
  tgh.gyomu_history_seq
  , tgh.subject
  , tgh.main_text
  , tgh.supported_at
  , tgh.created_by
  , tgh.updated_by
  , tgh.transition_type
  , tgh.saiban_seq
  , tc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
  , tc.customer_type
  , CONCAT(COALESCE(ma.account_name_sei,''),' ',COALESCE(ma.account_name_mei,'')) AS account_name
  , tgha.anken_id
  , ta.anken_name
  ,ta.bunya_id
  ,tsj.jiken_name
  ,ts.saiban_branch_no
FROM
  t_gyomu_history tgh
  LEFT JOIN t_gyomu_history_customer tghc
    ON tgh.gyomu_history_seq = tghc.gyomu_history_seq
  LEFT JOIN t_person tc
    ON tghc.customer_id = tc.customer_id
  LEFT JOIN m_account ma
    ON tgh.created_by = ma.account_seq
  LEFT JOIN t_gyomu_history_anken tgha
    ON tgh.gyomu_history_seq = tgha.gyomu_history_seq
  LEFT JOIN t_anken ta
    ON tgha.anken_id = ta.anken_id
  LEFT JOIN t_saiban ts
    ON tgh.saiban_seq = ts.saiban_seq
  LEFT JOIN t_saiban_jiken tsj
    ON tgh.saiban_seq = tsj.saiban_seq
WHERE
  tgh.gyomu_history_seq IN /* seqList */(1)
ORDER BY
  tgh.kotei_flg DESC,
  tgh.supported_at DESC,
  tgh.updated_at DESC
  ;