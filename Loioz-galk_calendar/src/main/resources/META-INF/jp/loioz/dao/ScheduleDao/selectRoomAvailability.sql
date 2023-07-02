SELECT
  *
FROM
  t_schedule ts
WHERE
/*%if condition.isNotEmpty(condition.dateFrom) && condition.isNotEmpty(condition.dateTo) */
  (
  /*%if condition.isNotEmpty(condition.dateTo) */
    (
      date_from <= /* condition.dateTo */'2020/01/01'
      OR date_from IS NULL
    )
  /*%end*/
  /*%if condition.isNotEmpty(condition.dateFrom) */
    AND (
      date_to >= /* condition.dateFrom */'2019/01/01'
      OR date_to IS NULL
    )
  /*%end*/
  )
/*%end*/
/*%if condition.isNotEmpty(condition.timeFrom) && condition.isNotEmpty(condition.timeTo) */
  AND (
  /*%if condition.isNotEmpty(condition.timeTo) */
    (
      time_from < /* condition.timeTo */'24:00'
      OR time_from IS NULL
    )
  /*%end*/
  /*%if condition.isNotEmpty(condition.timeFrom) */
    AND (
      time_to > /* condition.timeFrom */'00:00'
      OR time_to IS NULL
    )
  /*%end*/
  )
/*%end*/
/*%if condition.isNotEmpty(condition.repeatYobi) */
  AND (
    repeat_type != /* @jp.loioz.common.constant.CommonConstant$ScheduleRepeatType@WEEKLY */'2'
    OR repeat_type IS NULL
    OR CONV(repeat_yobi, 2, 10) & /*# "b" *//* condition.repeatYobi */b'1111111'
  )
/*%end*/
/*%if condition.isNotEmpty(condition.repeatDayOfMonth) */
  AND (
    repeat_type != /* @jp.loioz.common.constant.CommonConstant$ScheduleRepeatType@MONTHLY */'3'
    OR repeat_type IS NULL
    OR repeat_day_of_month =
    /* condition.repeatDayOfMonth */1
  )
/*%end*/
/*%if condition.isNotEmpty(condition.scheduleSeq) */
  AND schedule_seq != /* condition.scheduleSeq */1
/*%end*/
  AND room_id IS NOT NULL
  AND EXISTS (
    SELECT
      *
    FROM
      t_schedule_account tsa
      INNER JOIN m_account ma
        USING (account_seq)
    WHERE
      tsa.schedule_seq = ts.schedule_seq
      AND ma.account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1'
      AND ma.deleted_at IS NULL
      AND ma.deleted_by IS NULL
  )
  AND deleted_at IS NULL
  AND deleted_by IS NULL;