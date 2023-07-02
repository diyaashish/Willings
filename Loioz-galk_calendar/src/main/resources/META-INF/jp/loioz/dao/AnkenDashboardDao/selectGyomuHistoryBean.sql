SELECT
    tgh.gyomu_history_seq
    , tgh.transition_type
    , tgh.subject
    , tgh.main_text
    , tgh.supported_at
    , tgh.important_flg
    , tgh.kotei_flg
    , tgh.dengon_sent_flg
    , tgh.created_by
    , tgh.created_at
    , tgh.updated_at
    , tgha.anken_id
    , ta.bunya_id
    , tghc.customer_id
    , TRIM(CONCAT(COALESCE(tc.customer_name_sei, ''), ' ', COALESCE(tc.customer_name_mei, ''))) AS customer_name
    , ts.saiban_seq 
    , ts.saiban_branch_no 
FROM
    t_gyomu_history tgh 
    LEFT JOIN t_gyomu_history_anken tgha 
        USING (gyomu_history_seq) 
    LEFT JOIN t_gyomu_history_customer tghc 
        USING (gyomu_history_seq) 
    LEFT JOIN t_person tc 
        ON tghc.customer_id = tc.customer_id 
    LEFT JOIN t_anken ta
        USING (anken_id)
    LEFT JOIN t_saiban ts
        USING (saiban_seq)
WHERE
    tgha.anken_id = /* ankenId */null
ORDER BY
    tgh.updated_at DESC
    , tgh.supported_at DESC
;