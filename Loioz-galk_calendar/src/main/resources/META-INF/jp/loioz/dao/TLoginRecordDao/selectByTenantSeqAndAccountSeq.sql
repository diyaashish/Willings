select
  * 
from
  service_mgt.t_login_record tlr 
where
  tlr.tenant_seq = /* tenantSeq */null
  and tlr.account_seq = /* accountSeq */null;