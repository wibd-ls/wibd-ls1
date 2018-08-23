import base64
from base64 import b64decode
import pytesseract
import io
from PIL import Image
import boto3
from botocore.exceptions import ClientError
import re

def parseImg(image_object):
    img_data = image_object.get().get('Body').read()
    str = base64.b64encode(img_data)
    binary = b64decode(str)
    image = Image.open(io.BytesIO(binary))
    text = pytesseract.image_to_string(image)
    return text

def getYear(imgstrs):
    strs = imgstrs.split(' ')
    for str in strs:
        nwStr = str.replace('.','')
        if(nwStr.isdigit and len(nwStr) == 4):
            return nwStr

def parsePatent(text):
    text = re.sub('[,\. :()\?&\']', '', text)
    text = re.match('.*?([0-9]+.*[0-9]+).*?$', text)
    if text is not None:
        text = text.group(1)
        patent = re.match('.*?([0-9]{4,5})$', text)
        if patent is not None:
            return patent
    return ""

def getPatent(rekognition, bucket, key):
    response = rekognition.detect_text(Image={'S3Object': {'Bucket': bucket, 'Name': key}})
    patent_text = response['TextDetections'][0]['DetectedText']
    patent = parsePatent(patent_text)
    return patent

def putItemToDb(tableToInsert, imagePath, text, yr, p_number):
    tableToInsert.put_item(Item={"image_path": imagePath, "text_detections": text, "year": yr, "patent_number":p_number})
    
def updateTable(tableToUpdate, imagePath, yr, p_number):
    print("insert "+imagePath+" and year as "+yr)
    try:
        response = tableToUpdate.get_item( Key={
                'image_path' : imagePath
            })
    except ClientError as e:
        print(e.response['Error']['Message'])
    else:
        item = response['Item']
        print("GetItem succeeded:")
        #print(item)
        if item :
            updResponse = tableToUpdate.update_item(
                Key={
                    'image_path': imagePath
                },
                UpdateExpression="set patent_year = :y",
                ExpressionAttributeValues={
                    ':y' : yr
                },
                ReturnValues="UPDATED_NEW"
            )
            print("UpdateItem succeeded:")
        else :
            print("item not found in DB ")
            putItemToDb(tableToUpdate, imagePath, yr, p_number)

def parseS3Bucket(s3, bucketName):
    dynamodb = boto3.resource('dynamodb',region_name='us-east-2')
    table = dynamodb.Table('wibd-ls1')
    rekognition = boto3.client('rekognition', region_name='us-east-2')
    for image in s3.Bucket(bucketName).objects.filter(Prefix='1'):
        patent = getPatent(rekognition, bucketName, image.key)
        image_path = 's3://' + bucketName + '/' + image.key
        text = parseImg(image)
        year = getYear(text)
        print("image path is "+image_path+" and patented year is "+year)
        updateTable(table,image_path,year, patent)
               
if __name__ == "__main__":
    pytesseract.pytesseract.tesseract_cmd=r'D:/Installables/Big_Data/tesseract-Win64/tesseract.exe'
    s3 = boto3.resource('s3')
    bucketName = 'wibd-ls1'
    parseS3Bucket(s3, bucketName)
