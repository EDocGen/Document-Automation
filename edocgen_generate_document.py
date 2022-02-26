import requests
import uuid
import os.path
import json

url = "https://app.edocgen.com/login"

payload = "{\n\t\"username\":\"support@edocgen.com\",\n\t\"password\": \"password\"\n}"
headers = {
    'content-type': "application/json",
    'cache-control': "no-cache",
    'postman-token': "0ba8be97-557e-fee5-08d6-d05c5eee4710"
    }

response = requests.request("POST", url, data=payload, headers=headers)
x_access_token = response.json()['token']
print ("Using %s for further requests." % x_access_token)

# upload template
url = "https://app.edocgen.com/api/v1/document"
headers = {
    'x-access-token': x_access_token
    }

template_file = "/Users/usrname/Downloads/name.docx"
files = {'documentFile': open(template_file,'rb')}
values = {}
response = requests.post(url, files=files, data=values, headers=headers)
template_id = response.json()['id']
print("uploaded document %s with id: %s" %(template_file, template_id))

#Generate request
url = "https://app.edocgen.com/api/v1/document/generate/bulk"
# the output file extension will be added automatically by the edocgen system
outputFileName = "%s.docx" %uuid.uuid4()
generated_format = "docx"
inputValues = {
  'documentId': template_id,
  'format': generated_format,
  'outputFileName': outputFileName,
  'markers': {
    'name': 'name',
    'password': 'guess_what'
}
}
headers['content-type'] = 'application/json'
response = requests.post(url, data=json.dumps(inputValues), headers=headers)
print("Generate document: %s" % response.json())

#Wait for file to get generated and download
url = "https://app.edocgen.com/api/v1/output/name/%s" % outputFileName
print("Fetching files with name using url: %s" %url)

response = requests.get(url, headers=headers)
output = response.json()["output"][0] if "output" in response.json() and len(response.json()["output"]) > 0 else None
generated_file_id = None
while(not output):
    print("waiting for file to get generated...")
    response = requests.get(url, headers=headers)
    output = response.json()["output"][0] if "output" in response.json() and len(response.json()["output"]) > 0 else None

generated_file_id = output["_id"]
print("generated file id: %s" % generated_file_id)

# Download the generated file
url = 'https://app.edocgen.com/api/v1/output/download/%s' % generated_file_id
download_to_file = "/tmp/downloaded.%s" % generated_format
response = requests.get(url, headers=headers, allow_redirects=True)
open(download_to_file, 'wb').write(response.content) 
print("File downloaded successfully: %s" % os.path.isfile(download_to_file))

# Delete Template
url = "https://app.edocgen.com/api/v1/document/%s" % template_id
response = requests.delete(url, headers=headers)
print("Deleted template: %s" %response.json())
# Logout
url = "https://app.edocgen.com/api/v1/logout"
response = requests.post(url, headers=headers)
print("Logged out of eDocGen: %s" % response.json())
