import requests
from behave import *
import os
from azure.storage.blob import ContainerClient
import utils

parent_directory = os.path.join(os.path.dirname(os.path.abspath(__file__)), os.pardir)
csv_file_path = os.path.join(parent_directory, 'taxonomy.csv')

@given('the infrastructure is up and running')
def step_impl(context):
    url = utils.get_global_conf(context, "url")+'/info'
    response = requests.get(url, verify=False)
    assert response.status_code == 200

@when('someone requests the taxonomy to be generated')
def step_impl(context):
    url = utils.get_global_conf(context, "url")+'/generate'
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
    url = utils.get_global_conf(context, "url")+'/taxonomy'
    Params={}
    Params['version'] = version
    Params['extension'] = extension
    response = requests.get(url, verify = False, params=Params)
    setattr(context, 'response', response)

@given('There is no {file_ext} file in the storage')
def step_impl(context, file_ext):
    if file_ext.lower() == 'csv':
      input_container = ContainerClient.from_connection_string(utils.get_global_conf(context, "connection_string"), utils.get_global_conf(context, "input_container_name"))
      try:
          if not input_container.exists():
              input_container.create_container()
              print("Container created.")
          if input_container.get_blob_client(utils.get_global_conf(context, "csv_blob_name")).exists():
              input_container.delete_blob(blob=utils.get_global_conf(context, "csv_blob_name"))
              print("Deleted taxonomy." + file_ext)
      except Exception as e:
          print(e)
    elif file_ext.lower() == 'json':
      output_container = ContainerClient.from_connection_string(utils.get_global_conf(context, "connection_string"), utils.get_global_conf(context, "output_container_name"))
      try:
          if not output_container.exists():
              output_container.create_container()
              print("Container created.")
          if output_container.get_blob_client(utils.get_global_conf(context, "json_blob_name")).exists():
              output_container.delete_blob(blob=utils.get_global_conf(context, "json_blob_name"))
              print("Deleted taxonomy." + file_ext)
      except Exception as e:
          print(e)
