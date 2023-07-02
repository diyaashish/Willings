SELECT
  ts.saiban_seq
  , ts.saiban_branch_no
  , ts.saiban_status
  , ts.saiban_start_date
  , ts.saiban_end_date
  , ts.saibansho_id
  , ms.saibansho_name AS saibansho_mst_name
  , ts.saibansho_name
  , ts.keizoku_bu_name
  , ts.keizoku_bu_tel_no
  , ts.keizoku_bu_fax_no
  , ts.keizoku_kakari_name
  , ts.tanto_shoki
  , ts.saiban_result
  , tsj.jiken_seq
  , tsj.jiken_gengo
  , tsj.jiken_year
  , tsj.jiken_mark
  , tsj.jiken_no
  , tsj.jiken_name
  , ts.created_at
  , ts.created_by
  , ts.updated_at
  , ts.updated_by 
FROM
  t_saiban ts 
  LEFT JOIN t_saiban_jiken tsj 
    USING (saiban_seq) 
  INNER JOIN t_saiban_add_keiji tsak 
    ON tsj.jiken_seq = tsak.main_jiken_seq 
  LEFT JOIN m_sosakikan msk 
    USING (sosakikan_id) 
  LEFT JOIN m_saibansho ms 
    USING (saibansho_id) 
WHERE
  ts.saiban_seq = /* saibanSeq */8; 
