( 
  SELECT
    ts.saiban_seq
    , tsl.saiban_limit_seq
    , tsch.schedule_seq
    , tsl.limit_date_count
    , tsch.subject
    , tsl.limit_at
    , tsch.date_to
    , tsch.time_to
    , tsl.place
    , mr.room_id
    , mr.room_name
    , tsl.shuttei_type
    , tsl.content
    , tsl.result
    , tsl.created_by
    , tsl.created_at
    , tsl.updated_by
    , tsl.updated_at
    , tsl.version_no 
  FROM
    t_saiban ts 
    INNER JOIN t_saiban_limit_relation tslr 
      USING (saiban_seq) 
    INNER JOIN t_saiban_limit tsl 
      USING (saiban_limit_seq) 
    LEFT JOIN t_schedule tsch 
      USING (saiban_limit_seq) 
    LEFT JOIN m_room mr 
      USING (room_id) 
  WHERE
    ts.saiban_seq = /* saibanSeq */1 
    AND date_add( 
      CAST(tsch.date_from AS DATETIME)
      , INTERVAL tsch.time_to HOUR_SECOND         -- 裁判期日の場合、終日指定はされない（できない）ため、終日予定のケースは考慮しない
    ) < /* now */NULL
    AND (
      tsch.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@ALL */'0'
      OR (
        tsch.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@MEMBER*/'1'
        AND EXISTS (
          SELECT
            *
          FROM
            t_schedule_account
          WHERE
            schedule_seq = tsch.schedule_seq
            AND account_seq = /* accountSeq*/null
        )
      )
    )
  ORDER BY
/*%if !isAllTimeKijitsuList */
    tsl.limit_date_count DESC 
/*%end */
/*%if isAllTimeKijitsuList */
    tsl.limit_date_count 
/*%end */
/*%if !isAllTimeKijitsuList */
  LIMIT
    1                                           -- 直近の過去の期日データを1件取得
/*%end */
) 
UNION ALL ( 
  SELECT
    ts.saiban_seq
    , tsl.saiban_limit_seq
    , tsch.schedule_seq
    , tsl.limit_date_count
    , tsch.subject
    , tsl.limit_at
    , tsch.date_to
    , tsch.time_to
    , tsl.place
    , mr.room_id
    , mr.room_name
    , tsl.shuttei_type
    , tsl.content
    , tsl.result
    , tsl.created_by
    , tsl.created_at
    , tsl.updated_by
    , tsl.updated_at
    , tsl.version_no 
  FROM
    t_saiban ts 
    INNER JOIN t_saiban_limit_relation tslr 
      USING (saiban_seq) 
    INNER JOIN t_saiban_limit tsl 
      USING (saiban_limit_seq) 
    LEFT JOIN t_schedule tsch 
      USING (saiban_limit_seq) 
    LEFT JOIN m_room mr 
      USING (room_id) 
  WHERE
    ts.saiban_seq = /* saibanSeq */1 
    AND date_add( 
      CAST(tsch.date_from AS DATETIME)
      , INTERVAL tsch.time_to HOUR_SECOND         -- 裁判期日の場合、終日指定はされない（できない）ため、終日予定のケースは考慮しない
    ) >= /* now */NULL
    AND (
      tsch.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@ALL */'0'
      OR (
        tsch.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@MEMBER*/'1'
        AND EXISTS (
          SELECT
            *
          FROM
            t_schedule_account
          WHERE
            schedule_seq = tsch.schedule_seq
            AND account_seq = /* accountSeq*/null
        )
      )
    )
  ORDER BY
    tsl.limit_date_count
);
