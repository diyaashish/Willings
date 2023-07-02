SELECT
	MAX(branch_no)
FROM
  t_ginko_koza
WHERE
  tenant_seq = /* tenantSeq */null;