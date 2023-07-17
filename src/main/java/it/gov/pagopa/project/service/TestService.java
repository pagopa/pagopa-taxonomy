package it.gov.pagopa.project.service;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class TestService {
  Properties properties = new Properties();

  public TestService() throws IOException {
    try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
      properties.load(inputStream);
    }
  }

  public void uploadFile() {
    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
        .connectionString(properties.getProperty("AZURE_CONN_STRING"))
        .buildClient();
    blobServiceClient.createBlobContainerIfNotExists("taxonomyContainer");
    BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("taxonomyContainer");
    blobContainerClient.getBlobClient("taxonomy.json");
  }

  public void downloadFile() throws IOException {
    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(properties.getProperty("AZURE_CONN_STRING"))
            .buildClient();
    String data = Arrays.toString(blobServiceClient.getBlobContainerClient("taxonomyContainer")
            .getBlobClient("taxonomy.json").openInputStream().readAllBytes());
  }
}
