npm run build
scp -r dist/ springboot:~/kob/web
ssh springboot 'cd ~/kob/web/ && ./move.sh'