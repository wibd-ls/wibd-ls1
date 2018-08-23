import boto3
import re

def parse(text):
	print(text)
	patent_boolean = False
	year_boolean = False
	text = re.sub('[,\. :()\?&\']', '', text)
	text = re.match('.*?([0-9]+.*[0-9]+).*?$', text)
	if text is not None:
		text = text.group(1)
		patent = re.match('.*?([0-9]{4,5})$', text)
		if patent is not None:
			print("Patent: ", patent.group(1))
			patent_boolean = True
			text = re.sub(patent.group(1), '', text)
		year = re.match('.*?([0-9]{4}).*?$', text)
		if year is not None:
			print("Year: ", year.group(1))
			year_boolean = True
	print('\n')
	return (patent_boolean, year_boolean)


if __name__ == "__main__":

	dynamodb = boto3.resource('dynamodb', region_name='us-east-2')
	table = dynamodb.Table('wibd-ls1')

	s3 = boto3.resource('s3')
	bucket = 'wibd-ls1'

	patent_count = 0
	year_count = 0

	for image in s3.Bucket(bucket).objects.filter(Prefix='1891(10401-10600)'):
		image_path = 's3://' + bucket + '/' + image.key
		entry = table.get_item(Key={'image_path':image_path})
		text = entry['Item']['text_detections'][0]['DetectedText']
		results = parse(text)
		if results[0]: patent_count += 1
		if results[1]: year_count += 1

	print("Patent count: ", patent_count)
	print("Year count: ", year_count)