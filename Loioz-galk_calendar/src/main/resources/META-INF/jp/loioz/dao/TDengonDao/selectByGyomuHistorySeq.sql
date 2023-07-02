select
  *
from
  t_dengon
where
  gyomu_history_seq = /*gyomuHistorySeq*/null
ORDER BY created_at
limit 1;