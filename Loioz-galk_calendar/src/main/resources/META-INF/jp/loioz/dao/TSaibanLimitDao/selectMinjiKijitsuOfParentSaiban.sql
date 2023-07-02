( 
  SELECT
    ts.saiban_seq
    , tsl.saiban_limit_seq
    , tsch.schedule_seq
    , tsl.limit_date_count
    , tsch.subject
    , tsl.limit_at
    , tsl.place
    , tsch.date_to
    , tsch.time_to
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
      on ts.saiban_seq = tslr.saiban_seq 
    INNER JOIN t_saiban_limit tsl 
      on tslr.saiban_limit_seq = tsl.saiban_limit_seq 
    LEFT JOIN t_saiban_tree tst 
      on ts.saiban_seq = tst.saiban_seq 
    LEFT JOIN t_schedule tsch 
      on tsl.saiban_limit_seq = tsch.saiban_limit_seq 
    LEFT JOIN m_room mr 
      on tsch.room_id = mr.room_id 
  WHERE
    ts.saiban_seq = /* saibanSeq */1 
    AND tst.parent_seq is null 
    AND date_add( 
      CAST(tsch.date_from AS DATETIME)
      , INTERVAL tsch.time_to HOUR_SECOND         -- 裁判期日の場合、終日指定はされない（できない）ため、終日予定のケースは考慮しない
    ) < /* now */NULL
    AND (
      tsch.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@ALL */'0'
      OR tsl.shuttei_type = /* @jp.loioz.common.constant.CommonConstant$ShutteiType@NOT_REQUIRED */'2'
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
    tsl.created_at DESC
    , tsl.saiban_limit_seq DESC 
/*%end */
/*%if isAllTimeKijitsuList */
    tsl.created_at
    , tsl.saiban_limit_seq 
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
    , tsl.place
    , tsch.date_to
    , tsch.time_to
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
      on ts.saiban_seq = tslr.saiban_seq 
    INNER JOIN t_saiban_limit tsl 
      on tslr.saiban_limit_seq = tsl.saiban_limit_seq 
    LEFT JOIN t_saiban_tree tst 
      on ts.saiban_seq = tst.saiban_seq 
    LEFT JOIN t_schedule tsch 
      on tsl.saiban_limit_seq = tsch.saiban_limit_seq 
    LEFT JOIN m_room mr 
      on tsch.room_id = mr.room_id 
  WHERE
    ts.saiban_seq = /* saibanSeq */1 
    AND tst.parent_seq is null 
    AND date_add( 
      CAST(tsch.date_from AS DATETIME)
      , INTERVAL tsch.time_to HOUR_SECOND         -- 裁判期日の場合、終日指定はされない（できない）ため、終日予定のケースは考慮しない
    ) >= /* now */NULL
    AND (
      tsch.open_range = /* @jp.loioz.common.constant.CommonConstant$SchedulePermission@ALL */'0'
      OR tsl.shuttei_type = /* @jp.loioz.common.constant.CommonConstant$ShutteiType@NOT_REQUIRED */'2'
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
    tsl.created_at,                             -- limit_date_countではなく、作成日でソート。併合/反訴を分離したときに親裁判の期日を子裁判に反映（作成）し回数が変わるため。
    tsl.saiban_limit_seq
);
