select
  count(*)
from
  m_account ma 
where
  ma.account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1' 
  and ma.deleted_by IS NULL;