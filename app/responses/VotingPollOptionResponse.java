package responses;

public class VotingPollOptionResponse {
    private String name;
    private Integer code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "VotingPollOptionResponse{" +
                "name='" + name + '\'' +
                ", code=" + code +
                '}';
    }
}
