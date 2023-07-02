SELECT
    ts.schedule_seq
FROM
    t_schedule ts 
WHERE
    ts.customer_id = /* customerId */null
    AND ts.deleted_at IS NULL 
    AND ts.deleted_by IS NULL 
    /*%if accountSeq != null */
    AND ( 
        ts.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@ALL */'0'
        OR ( 
            ts.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@MEMBER*/'1' 
            AND EXISTS ( 
                SELECT
                    * 
                FROM
                    t_schedule_account 
                WHERE
                    schedule_seq = ts.schedule_seq
                    AND account_seq = /* accountSeq */null
            )
        )
    )
    /*%end */
    /*%if !isAllTimeScheduleList */
    AND CASE 
        WHEN ts.all_day_flg = '0' 
            THEN date_add( 
            CAST(ts.date_from AS DATETIME)
            , INTERVAL ts.time_to HOUR_SECOND
        ) 
        ELSE date_add( 
            CAST(ts.date_to AS DATETIME)
            , INTERVAL '23:59:59' HOUR_SECOND -- 終日予定データは当日終了のものも取得する
        ) 
        END >= NOW() 
    /*%end */
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