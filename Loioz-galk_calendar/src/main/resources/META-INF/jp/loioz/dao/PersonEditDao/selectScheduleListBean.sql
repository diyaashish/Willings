SELECT
    ts.schedule_seq
    , ts.subject
    , ts.date_from
    , ts.date_to
    , ts.time_from
    , ts.time_to
    , ts.all_day_flg
    , ts.room_id
    , ts.place
    , ts.customer_id
    , ts.anken_id
    , ts.saiban_seq
    , tsaiban.saiban_branch_no
    , ts.saiban_limit_seq 
    , tsa.account_seq 
FROM
    t_schedule ts 
    LEFT JOIN t_schedule_account tsa 
        USING (schedule_seq) 
    LEFT JOIN t_saiban tsaiban 
        USING (saiban_seq)
WHERE
    ts.schedule_seq IN /* scheduleSeqList */(null)
ORDER BY
    -- ソートする場合の時間は絞り込みで利用する時間とは異なり、登録されている時間を利用する（終日データは0時とする）
    CASE 
        WHEN ts.all_day_flg = '0' 
            THEN date_add( 
            CAST(ts.date_from AS DATETIME)
            , INTERVAL ts.time_from HOUR_SECOND
        ) 
        ELSE date_add( 
            CAST(ts.date_from AS DATETIME)
            , INTERVAL '00:00:00' HOUR_SECOND
        ) 
        END ASC
    , ts.schedule_seq ASC
;