import requests
from behave import *
import os
from azure.storage.blob import ContainerClient

connection_string = 'DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1;QueueEndpoint=http://127.0.0.1:10001/devstoreaccount1;TableEndpoint=http://127.0.0.1:10002/devstoreaccount1;'
input_container_name = 'input'
output_container_name = 'output'
parent_directory = os.path.join(os.path.dirname(os.path.abspath(__file__)), os.pardir)
csv_file_path = os.path.join(parent_directory, 'taxonomy.csv')

@given('the infrastructure is up and running')
def step_impl(context):
    url='http://localhost:7071/info'
    response = requests.get(url, verify=False)
    assert response.status_code == 200

@when('someone requests the taxonomy to be generated')
def step_impl(context):
    url = 'http://localhost:7071/generate'
    response = requests.get(url, verify = False)
    setattr(context, 'response', response)

@then('check status code equals {status}')
def step_impl(context, status):
    assert context.response.status_code == int(status)

@given('a json file has already been created')
def step_impl(context):
    pass

@when('a partner requests a taxonomy {extension} as {version} version')
def step_impl(context, extension, version):
    url = 'http://localhost:7071/taxonomy'
    Params={}
    Params['version'] = version
    Params['extension'] = extension
    response = requests.get(url, verify = False, params=Params)
    setattr(context, 'response', response)

@given('There is no {file_ext} file in the storage')
def step_impl(context, file_ext):
    if file_ext.lower() == 'csv':
      input_container = ContainerClient.from_connection_string(connection_string, input_container_name)
      try:
          if not input_container.exists():
              input_container.create_container()
              print("Container created.")
          if input_container.get_blob_client("taxonomy.csv").exists():
              input_container.delete_blob(blob="taxonomy.csv")
              print("Deleted taxonomy." + file_ext)
      except Exception as e:
          print(e)
    elif file_ext.lower() == 'json':
      output_container = ContainerClient.from_connection_string(connection_string, output_container_name)
      try:
          if not output_container.exists():
              output_container.create_container()
              print("Container created.")
          if output_container.get_blob_client("taxonomy.json").exists():
              output_container.delete_blob(blob="taxonomy.json")
              print("Deleted taxonomy." + file_ext)
      except Exception as e:
          print(e)
