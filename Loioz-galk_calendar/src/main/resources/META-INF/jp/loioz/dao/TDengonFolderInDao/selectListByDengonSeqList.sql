SELECT
  *
FROM
  t_dengon_folder_in AS tdfi
WHERE
  tdfi.dengon_seq IN /* dengonSeqList */(null)
  AND tdfi.dengon_folder_seq IN (
    SELECT
      tdf.dengon_folder_seq
    FROM
      t_dengon_folder AS tdf
    WHERE
      tdf.account_seq = /* accountSeq */null
  );
