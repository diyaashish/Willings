SELECT
    * 
FROM
    m_bunya mb 
WHERE
    mb.bunya_id != /* bunyaId */NULL 
    AND mb.bunya_name = /* bunyaName */NULL 
    AND mb.deleted_at IS NULL 
    AND mb.deleted_by IS NULL;