# wibd-ls1
WiBD Learn and Share


# S3 Upload scripts notes
The purpose of the scripts is to upload scanned patents to S3 doing the following: 
1. Set up an folder "patents" on local to save the scanned patents
2. Script to run sync command that runs a sync between the local folder and s3 bucket, and moves the files to another local folder
3. Set up a cron job that runs the above script on a cadence (every night)

Scripts:
1. patents-upload : This script contains the following three things:
                    1. Credentails required to run the command
                    2. aws Sync command
                    3. Save the errors to "uploaderror.txt"
                    4. Move the files to another local folder "patents-uploadcomplete"
2. setup-cron : This script has the command to set up the cron job to run the "patents-upload" script.

Steps:
1. Download the AWS CLI and run the package on local
2. Create the patents-upload script on local
3. Create the setup-cron script on local
4. Make sure the source and destination folders are accurate
5. Run the setup-cron 
6. Check the S3 bucket to verify the upload

Debug:
1. Make sure the credentials are correct -> vi .patents-uplaod
2. Make sure the cronjob is set up right -> crontab -e



                 
