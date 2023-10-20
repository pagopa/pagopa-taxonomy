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

    global_config = context.config.userdata.get("global_config")

    container = ContainerClient.from_connection_string(global_config.get("connection_string"), global_config.get("input_container_name"))
    if container.get_blob_client(global_config.get("csv_blob_name")).exists():
        with open(file=csv_file_path, mode="wb") as csv_file:
            download_stream = container.download_blob(blob=global_config.get("csv_blob_name"))
            csv_file.write(download_stream.readall())

# before feature
def before_feature(context, feature):
    global_config = context.config.userdata.get("global_config")
    container = ContainerClient.from_connection_string(global_config.get("connection_string"), global_config.get("input_container_name"))
    try:
        if not container.exists():
            container.create_container()
            print("Container created.")
        if not container.get_blob_client(global_config.get("csv_blob_name")).exists() or global_config.get("force_taxomy_example"):
            container.upload_blob(name=global_config.get("csv_blob_name"), data=global_config.get("taxonomy_csv_example"), overwrite=True)
            print("Blob Uploaded.")
    except Exception as e:
        print(e)


def after_all(context): 
    global_config = context.config.userdata.get("global_config")
    container = ContainerClient.from_connection_string(global_config.get("connection_string"), global_config.get("input_container_name"))
    try:
        if container.get_blob_client(global_config.get("csv_blob_name")).exists():
            if os.path.exists(csv_file_path):
                with open(file=csv_file_path, mode="rb") as csv_file:
                    container.upload_blob(name=global_config.get("csv_blob_name"), data=csv_file.read(), overwrite=True)
                    print("Blob Restored.")
    except Exception as e:
        print(e)

print("Before feature executed.")