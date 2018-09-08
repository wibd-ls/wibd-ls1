import pytesseract
import PIL.Image
import io
import os
from base64 import b64decode

os.environ['TESSDATA_PREFIX'] = './tessdata'

def lambda_handler(event, context):
  pytesseract.pytesseract.tesseract_cmd=r'./tesseract'
  binary = b64decode(event['image64'])
  image = PIL.Image.open(io.BytesIO(binary))
  text = pytesseract.image_to_string(image)
  return {'text' : text}
