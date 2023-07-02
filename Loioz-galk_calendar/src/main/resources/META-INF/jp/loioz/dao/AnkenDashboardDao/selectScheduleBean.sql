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
    , tsl.shuttei_type
    , tsa.account_seq 
    , tsj.jiken_gengo
    , tsj.jiken_year
    , tsj.jiken_mark
    , tsj.jiken_no
    , tsj.jiken_name
FROM
    t_schedule ts 
    LEFT JOIN t_saiban_limit tsl
        ON ts.saiban_limit_seq = tsl.saiban_limit_seq
    LEFT JOIN t_schedule_account tsa 
        USING (schedule_seq) 
    LEFT JOIN t_saiban tsaiban 
        USING (saiban_seq) 
    LEFT JOIN t_saiban_tree tst 
        USING (saiban_seq)
    LEFT JOIN ( 
      SELECT
        saiban_seq
        , jiken_gengo
        , jiken_year
        , jiken_mark
        , jiken_no
        , jiken_name 
      FROM
        t_saiban_jiken 
      WHERE
        jiken_seq IN ( 
          SELECT
            MIN(jiken_seq) 
          FROM
            t_saiban_jiken 
          GROUP BY
            saiban_seq
        )
    ) tsj 
      ON tsaiban.saiban_seq = tsj.saiban_seq 
WHERE
    ts.anken_id = /* ankenId */null 
    AND ts.deleted_at IS NULL 
    AND ts.deleted_by IS NULL 
    AND tst.parent_seq IS NULL -- 親裁判のデータのみを取得対象とする(刑事裁判の場合はtstのレコードが存在しないため、左辺は必ずNULLになり特に絞り込みは行われない)
    /*%if accountSeq != null */
    AND ( 
        ts.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@ALL */'0'
        OR tsl.shuttei_type = /* @jp.loioz.common.constant.CommonConstant$ShutteiType@NOT_REQUIRED */'2'
        OR ( 
            ts.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@MEMBER*/'1' 
            AND EXISTS ( 
                SELECT
                    * 
                FROM
                    t_schedule_account 
                WHERE
                    schedule_seq = ts.schedule_seq
                    AND account_seq = /* accountSeq*/null
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
    /*%if isOnlyKijituScheduleList */
    AND ts.saiban_limit_seq IS NOT NULL
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
    , tsa.account_seq ASC
;

