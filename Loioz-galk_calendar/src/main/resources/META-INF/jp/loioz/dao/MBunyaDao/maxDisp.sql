SELECT
    MAX(disp_order) 
FROM
    m_bunya mb 
WHERE
    mb.deleted_at IS NULL 
    AND mb.deleted_by IS NULL;