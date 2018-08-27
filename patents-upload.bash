#!/bin/bash

#install cli from bundle

#unzip awscli-bundle.zip
#sudo ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws


#install cli from aws 
curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip"

#credentials

export AWS_ACCESS_KEY_ID=AKIAIMEWCCL6FIUVOCGQ
export AWS_SECRET_ACCESS_KEY=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
export AWS_DEFAULT_REGION=us-east-2


 

#cron job

crontab -l > uploadcron
echo "0 2 * * * root aws s3 sync “.\patents\” s3://wibd-ls1/ " >> uploadcron

#install new cron file

crontab uploadcron