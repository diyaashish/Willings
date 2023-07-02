SELECT
    tdc.dengon_seq
    , tc.customer_id
    , tc.customer_name_sei
    , tc.customer_name_sei_kana
    , tc.customer_name_mei
    , tc.customer_name_mei_kana 
FROM
    t_dengon_customer tdc
    INNER JOIN t_person tc
        USING (customer_id)
WHERE
    tdc.dengon_seq IN /* dengonSeq */(1)
;