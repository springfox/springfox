package springfox.test.contract.oas.bugs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class Bug3353 {
  @JsonInclude()
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPropertyOrder({"agentCode", "orderNo", "timestamp"})
  public class WithdrawQueryRequestView extends BaseView {

    @JsonView({Request.class, QueryParam.class, QueryParamDes.class, KeyHint.class})
    @JsonProperty("AgentCode")
    @JsonAlias("MerCode")
    @NotBlank(groups = {IRequest.class}, message = "AgentCode {javax.validation.constraints.NotBlank.message}")
    private String agentCode;

    @JsonView({Request.class, QueryParam.class})
    @JsonProperty("Timestamp")
    @NotNull(groups = {IRequest.class}, message = "Timestamp {javax.validation.constraints.NotNull.message}")
    @Digits(groups = {IRequest.class}, fraction = 0, integer = 13,
        message = "Timestamp {javax.validation.constraints.Digits.message}")
    @Positive(groups = {IRequest.class}, message = "Timestamp {javax.validation.constraints.Positive.message}")
    private BigDecimal timestamp;

    @JsonView({Request.class, QueryParam.class, QueryParamDes.class})
    @JsonProperty("OrderNo")
    @NotBlank(groups = {IRequest.class}, message = "OrderNo {javax.validation.constraints.NotBlank.message}")
    @Size(min = 13, max = 30, groups = {IRequest.class}, message = "{javax.validation.constraints.Size.message}")
    private String orderNo;

    public WithdrawQueryRequestView(
        @NotNull(groups = {Request.class}) String sign,
        String key) {
      super(sign, key);
    }

    public WithdrawQueryRequestView(
        @NotNull(groups = {Request.class}) String sign,
        String key,
        @NotBlank(groups = {IRequest.class}, message = "AgentCode {javax.validation.constraints.NotBlank.message}")
            String agentCode,
        @NotNull(groups = {IRequest.class}, message = "Timestamp {javax.validation.constraints.NotNull.message}")
        @Digits(groups = {IRequest.class}, fraction = 0, integer = 13,
            message = "Timestamp {javax.validation.constraints.Digits.message}")
        @Positive(groups = {IRequest.class}, message = "Timestamp {javax.validation.constraints.Positive.message}")
            BigDecimal timestamp,
        @NotBlank(groups = {IRequest.class}, message = "OrderNo {javax.validation.constraints.NotBlank.message}")
        @Size(min = 13, max = 30, groups = {IRequest.class},
            message = "{javax.validation.constraints.Size.message}") String orderNo) {
      super(sign, key);
      this.agentCode = agentCode;
      this.timestamp = timestamp;
      this.orderNo = orderNo;
    }

    public String getAgentCode() {
      return agentCode;
    }

    public void setAgentCode(String agentCode) {
      this.agentCode = agentCode;
    }

    public BigDecimal getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(BigDecimal timestamp) {
      this.timestamp = timestamp;
    }

    public String getOrderNo() {
      return orderNo;
    }

    public void setOrderNo(String orderNo) {
      this.orderNo = orderNo;
    }
  }


  @JsonInclude(JsonInclude.Include.ALWAYS)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public abstract class BaseView {

    @JsonView({Request.class})
    @JsonProperty("Sign")
    @NotNull(groups = {Request.class})
    private String sign;

    @JsonView({QueryParam.class})
    @JsonProperty("key")
    @ApiParam(hidden = true)
    private String key;


    public BaseView(
        @NotNull(groups = {Request.class}) String sign,
        String key) {
      this.sign = sign;
      this.key = key;
    }

    public String getSign() {
      return sign;
    }

    public void setSign(String sign) {
      this.sign = sign;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }
  }

  public static class KeyHintBuilder {
    private StringBuilder builder;

    public KeyHintBuilder() {
      this.builder = new StringBuilder();
    }

    @JsonAnySetter
    public void addProperty(
        String name,
        Object property) {
      if (!StringUtils.isEmpty(property)) {
        this.builder.append(property);
      }
    }

    @Override
    public String toString() {
      return this.builder.toString();
    }
  }

  public interface IRequest {
  }

  public interface IResponse {
  }

  public static class Request {
  }

  public static class RequestDes {
  }

  public static class Response {
  }

  public static class QueryParam {
  }

  public static class QueryParamDes {
  }

  public static class KeyHint {
  }

  public static class QueryParamsBuilder {
    private StringBuilder builder;

    public QueryParamsBuilder() {
      this.builder = new StringBuilder();
    }

    @JsonAnySetter
    public void addToUri(
        String name,
        Object property) {
      if (!StringUtils.isEmpty(property)) {
        if (this.builder.length() > 0) {
          this.builder.append("&");
        }
        this.builder.append(name).append("=").append(property);
      }
    }

    @Override
    public String toString() {
      return this.builder.toString();
    }
  }
}
