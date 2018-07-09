import boto3
import logging
import decimal


logging.basicConfig(format='%(levelname)s:%(message)s', level=logging.INFO)


# Converts all floats in the text_detections response to decimals per dynamo requirements.

def convert_float_to_decimal(results):

    for i in range(len(results)):
        results[i]["Confidence"] = decimal.Decimal(results[i]["Confidence"])
        for k, v in results[i]["Geometry"]["BoundingBox"].items():
            results[i]["Geometry"]["BoundingBox"][k] = decimal.Decimal(v)
        for point in range(len(results[i]["Geometry"]["Polygon"])):
            for k, v in results[i]["Geometry"]["Polygon"][point].items():
                results[i]["Geometry"]["Polygon"][point][k] = decimal.Decimal(v)
    return results


if __name__ == "__main__":

    # Prepare AWS resources

    s3 = boto3.resource('s3')
    bucket = 'wibd-ls1'

    dynamodb = boto3.resource('dynamodb', region_name='us-east-2')
    table = dynamodb.Table('wibd-ls1')

    rekognition = boto3.client('rekognition', region_name='us-east-2')

    # Iterate through images, detect text and insert response into db

    image_count = 1
    for image in s3.Bucket(bucket).objects.filter(Prefix='1'):
        image_path = 's3://' + bucket + '/' + image.key
        logging.info("Image " + str(image_count) + ": " + image_path)

        logging.info("Extracting text from image")
        response = rekognition.detect_text(Image={'S3Object': {'Bucket': bucket, 'Name': image.key}})
        text_detections = convert_float_to_decimal(response['TextDetections'])

        table.put_item(Item={"image_path": image_path, "text_detections": text_detections})
        logging.info("Output stored in db\n")
        image_count += 1