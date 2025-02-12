package vn.minhhai.springb_fskill.dto.request;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SampleDTO implements Serializable {
    private Integer id;
    private String name;
}
