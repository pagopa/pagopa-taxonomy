package it.gov.pagopa.project.service;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestService {
  Properties properties = new Properties();

  public TestService() throws IOException {
    try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
      properties.load(inputStream);
    }
  }
  public void uploadFile() throws IOException {
    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
        .connectionString(properties.getProperty("AZURE_CONN_STRING"))
        .buildClient();
    blobServiceClient.createBlobContainerIfNotExists("testcontainer");
    BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("testcontainer");
    BlobClient blobClient = blobContainerClient.getBlobClient("test.csv");
  }
}
