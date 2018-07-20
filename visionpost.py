# To execute from terminal, authenticate cloud storage -
# set environment variable on console as follows -
#export GOOGLE_APPLICATION_CREDENTIALS="gcloudkey.json"

#import requests
from google.cloud import vision
from google.cloud import storage

storageclient = storage.Client()
visionclient = vision.ImageAnnotatorClient()
mybucket = storageclient.get_bucket("wibd-patents")
blobs = mybucket.list_blobs()
for blob in blobs:
        #image_uri = 'http://storage.googleapis.com/wibd-patents/' + blob.name
        request = {
            'image': {
            'source': {'image_uri': 'http://storage.googleapis.com/wibd-patents/' + blob.name},
            }}
        response = visionclient.annotate_image(request)
#if (len(response.annotations) greater than 0)
        print(response.text_annotations[0])

#url = https://vision.googleapis.com/v1/images:annotate
