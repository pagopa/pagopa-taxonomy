import os
from azure.storage.blob import ContainerClient

connectionString = 'DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1;QueueEndpoint=http://127.0.0.1:10001/devstoreaccount1;TableEndpoint=http://127.0.0.1:10002/devstoreaccount1;'
storageContainer = 'input'
parent_directory = os.path.join(os.path.dirname(os.path.abspath(__file__)), os.pardir)
csv_file_path = os.path.join(parent_directory, 'taxonomy.csv')

# before feature
def before_feature(context, feature):
    container = ContainerClient.from_connection_string(connectionString, storageContainer)
    try:
        if not container.exists():
            container.create_container()
            print("Container created.")
        if not container.get_blob_client("taxonomy.csv").exists():
            with open(file=csv_file_path, mode="rb") as data:
                container.upload_blob(name="taxonomy.csv", data=data)
                print("Blob Uploaded.")
    except Exception as e:
        print(e)
print("Before feature executed.")