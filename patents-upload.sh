#!/bin/bash
  
#install cli from bundle
#unzip awscli-bundle.zip
#sudo ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws

#install cli from web
#curl "https://s3.amazonaws.com/aws-cli/awsscli-bundle.zip" -o "awscli-bundle.zip"

echo test
#credentials
export AWS_ACCESS_KEY_ID=AKIAI5WMUTXJ6GEYFHRQ
export AWS_SECRET_ACCESS_KEY=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
export AWS_DEFAULT_REGION=us-east-1
/usr/local/bin/./aws s3 sync patents s3://wibd-ls1/

