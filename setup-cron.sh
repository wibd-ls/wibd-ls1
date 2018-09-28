
#write out current crontab
crontab -l > uploadcron
#echo new cron into cron file  
echo "*/1 * * * * cd patents && sh .patents-upload >> uploadcronlog.txt 2>&1" >> uploadcron
#install new cron file
crontab uploadcron
rm uploadcron

