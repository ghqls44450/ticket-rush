#!/bin/bash
# ===== Slave가 Master에 레플리케이션 연결하는 스크립트 =====
# Master가 완전히 준비될 때까지 대기 후 연결

set -e

echo "==> Waiting for Master to be ready..."
until mysql -h mysql-master -u root -prootpassword -e "SELECT 1" &>/dev/null; do
    echo "    Master not ready yet, retrying in 3s..."
    sleep 3
done

echo "==> Master is ready! Getting binlog position..."
MASTER_STATUS=$(mysql -h mysql-master -u root -prootpassword -e "SHOW MASTER STATUS\G")
MASTER_LOG_FILE=$(echo "$MASTER_STATUS" | grep "File:" | awk '{print $2}')
MASTER_LOG_POS=$(echo "$MASTER_STATUS" | grep "Position:" | awk '{print $2}')

echo "    Master Log File: $MASTER_LOG_FILE"
echo "    Master Log Position: $MASTER_LOG_POS"

echo "==> Configuring replication..."
mysql -u root -prootpassword -e "
    STOP SLAVE;
    CHANGE MASTER TO
        MASTER_HOST='mysql-master',
        MASTER_USER='repl_user',
        MASTER_PASSWORD='repl_password',
        MASTER_LOG_FILE='$MASTER_LOG_FILE',
        MASTER_LOG_POS=$MASTER_LOG_POS;
    START SLAVE;
"

echo "==> Checking replication status..."
sleep 2
mysql -u root -prootpassword -e "SHOW SLAVE STATUS\G" | grep -E "(Slave_IO_Running|Slave_SQL_Running|Seconds_Behind_Master)"

echo "==> Replication setup complete!"
