#!/bin/sh

cd `dirname $0`

#設定ファイル読み込み
. ./application.conf

tenant_seq=$1
if [ -z "${tenant_seq}" ]; then
    echo "テナント連番を指定して下さい。"
    exit 1
fi

tenant_db_name="${tenant_db_name_prefix}${tenant_seq}"
tenant_user="${tenant_user_prefix}${tenant_seq}"
tenant_pass="${tenant_pass_prefix}${tenant_seq}"

#テナントDBを作成
mysql --defaults-extra-file=login_my.conf -h ${db_host} -P ${db_port} <<EOS
CREATE DATABASE ${tenant_db_name};
USE ${tenant_db_name};
source ${create_sql_path}
exit
EOS
result=$?
if [ $result != 0 ]; then
    echo "テナントDBの作成に失敗しました。(db_name=${tenant_db_name})"
    exit $result
fi

#テナントDB用のユーザを作成
mysql --defaults-extra-file=login_my.conf -h ${db_host} -P ${db_port} <<EOS
CREATE USER '${tenant_user}'@'${tenant_user_host}' IDENTIFIED BY '${tenant_pass}';
GRANT SELECT,UPDATE,INSERT,DELETE ON ${tenant_db_name}.* TO '${tenant_user}'@'${tenant_user_host}';
GRANT SELECT,UPDATE,INSERT,DELETE ON ${mgt_db_name}.* TO '${tenant_user}'@'${tenant_user_host}';
exit
EOS
result=$?
if [ $result != 0 ]; then
    echo "ユーザの作成に失敗しました。(user=${tenant_user})"
    exit $result
fi

echo "${tenant_db_name} を作成しました。"

#初期データの挿入
mysql --defaults-extra-file=login_my.conf -h ${db_host} -P ${db_port} <<EOS
USE ${tenant_db_name};
source ${insert_sql_path}
exit
EOS
result=$?
if [ $result != 0 ]; then
    echo "初期データの挿入に失敗しました。"
    exit $result
fi

exit 0