select
  *
from
  t_dengon_account_status AS tdas
where
  tdas.account_seq = /* accountSeq */null
  and tdas.dengon_seq = /* dengonSeq */null;