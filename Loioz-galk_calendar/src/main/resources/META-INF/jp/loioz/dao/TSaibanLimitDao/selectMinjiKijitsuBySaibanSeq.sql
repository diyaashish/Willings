SELECT
  tslr.saiban_seq
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
  t_saiban_limit tsl 
  INNER JOIN t_saiban_limit_relation tslr 
    on tslr.saiban_limit_seq = tsl.saiban_limit_seq 
    AND tslr.saiban_limit_seq NOT IN ( 
      SELECT
        tslr_s.saiban_limit_seq 
      FROM
        t_saiban_limit_relation tslr_s
      WHERE
        tslr_s.saiban_seq = ( 
          SELECT
            tst_s.parent_seq 
          FROM
            t_saiban_tree tst_s
          WHERE
            tst_s.saiban_seq = /* saibanSeq */1
        )
    ) 
  LEFT JOIN t_schedule tsch 
    on tsl.saiban_limit_seq = tsch.saiban_limit_seq 
  LEFT JOIN m_room mr 
    on tsch.room_id = mr.room_id 
WHERE
  tslr.saiban_seq = /* saibanSeq */1
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
  tsl.created_at,
  tsl.saiban_limit_seq;
