import os
import json
from azure.storage.blob import ContainerClient

parent_directory = os.path.join(os.path.dirname(os.path.abspath(__file__)), os.pardir)
csv_file_path = os.path.join(parent_directory, 'taxonomy.csv')

def before_all(context):
    # initialize precondition cache to avoid check systems up for each scenario
    context.precondition_cache = set()

    print('Global settings...')

    more_userdata = json.load(open(os.path.join(context.config.base_dir + "/config/config.json")))
    context.config.update_userdata(more_userdata)

# before feature
def before_feature(context, feature):
    container = ContainerClient.from_connection_string(context.config.userdata.get("global_config").get("connection_string"), context.config.userdata.get("global_config").get("input_container_name"))
    try:
        if not container.exists():
            container.create_container()
            print("Container created.")
        if not container.get_blob_client(context.config.userdata.get("global_config").get("csv_blob_name")).exists():
            with open(file=csv_file_path, mode="rb") as data:
                container.upload_blob(name=context.config.userdata.get("global_config").get("csv_blob_name"), data=data)
                print("Blob Uploaded.")
    except Exception as e:
        print(e)
print("Before feature executed.")