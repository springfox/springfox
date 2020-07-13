package springfox.test.contract.swagger.models;

import org.springframework.web.multipart.MultipartFile;

public class Bug1370 {
  private MultipartFile fileToUpload;
  private String name;

  public MultipartFile getFileToUpload() {
    return fileToUpload;
  }

  public void setFileToUpload(MultipartFile fileToUpload) {
    this.fileToUpload = fileToUpload;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
