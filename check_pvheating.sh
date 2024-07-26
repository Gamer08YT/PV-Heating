#!/bin/bash

# sudo systemctl enable cron
# sudo nano /etc/init.d/check_pvheating_startup.sh
# sudo chmod +x /etc/init.d/check_pvheating_startup.sh
# sudo update-rc.d check_pvheating_startup.sh defaults

# sudo nano /etc/systemd/system/pvheating.service
# sudo chmod +x /usr/local/bin/check_pvheating.sh
# sudo systemctl daemon-reload
# sudo systemctl enable pvheating.service
# sudo systemctl start pvheating.service


### BEGIN INIT INFO
# Provides:          check_pvheating_startup
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Checks and starts PVHeating screen session at startup
# Description:       Ensures PVHeating screen session is running after reboot
### END INIT INFO

#!/bin/bash

LOGFILE=/var/log/pvheating.log

{
    echo "$(date): Checking if the screen session 'PVHeating' is running."
    if ! screen -list | grep -q "PVHeating"; then
        echo "$(date): Screen session 'PVHeating' not found. Starting it."
        screen -dmS PVHeating bash -c 'cd /home/pvheating/ && java -jar PV-Heating-0.0.1-SNAPSHOT.jar'
        echo "$(date): Screen session 'PVHeating' started."
    else
        echo "$(date): Screen session 'PVHeating' is already running."
    fi
} >> "$LOGFILE" 2>&1
