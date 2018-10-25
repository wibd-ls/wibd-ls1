#!/bin/bash

#install cli from bundle

#unzip awscli-bundle.zip
#sudo ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws


#install cli from aws 
#curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip"

#credentials

export AWS_ACCESS_KEY_ID=secretkey
export AWS_SECRET_ACCESS_KEY=secretkey
export AWS_DEFAULT_REGION=us-east-1
/usr/local/bin/./aws s3 sync patents s3://wibd-ls1/ 
mv  -v patents/* patents-uploadcomplete


