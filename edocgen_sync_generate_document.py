import requests
import uuid
import os.path
import json


url = "https://app.edocgen.com/login"

payload = "{\n\t\"username\":\"support@edocgen.com\",\n\t\"password\": \"your_random_password\"\n}"
headers = {
    'content-type': "application/json",
    'cache-control': "no-cache",
    }

response = requests.request("POST", url, data=payload, headers=headers)
x_access_token = response.json()['token']
print ("Using %s for further requests." % x_access_token)

# upload template
url = "%s/api/v1/document" %(base_url)
headers = {
    'x-access-token': x_access_token
    }

template_file = "/Users/usrname/Downloads/name.docx"
files = {'documentFile': open(template_file,'rb')}
values = {}
response = requests.post(url, files=files, headers=headers)
template_id = response.json()['id']
print("uploaded document %s with id: %s" %(template_file, template_id))

#Generate request
url = "%s/api/v1/document/generate" %(base_url)
# the output file extension will be added automatically by the edocgen system
outputFileName = "%s.docx" %uuid.uuid4()
generated_format = "docx"
inputValues = {
  'sync': True,
  'documentId': template_id,
  'format': generated_format,
  'outputFileName': outputFileName,
  'markers': {
    'name': 'YourName',
    'password': 'guess_what'
}
}
headers['content-type'] = 'application/json'
response = requests.post(url, data=json.dumps(inputValues), headers=headers)
download_to_file = "/tmp/downloaded.%s" % generated_format
open(download_to_file, 'wb').write(response.content) 
print("File downloaded successfully: %s" % os.path.isfile(download_to_file))

# Delete Template
url = "%s/api/v1/document/%s" %(base_url, template_id)
response = requests.delete(url, headers=headers)
print("Deleted template: %s" %response.json())
# Logout
url = "%s/api/v1/logout" %(base_url)
response = requests.post(url, headers=headers)
print("Logged out of eDocGen: %s" % response.json())
