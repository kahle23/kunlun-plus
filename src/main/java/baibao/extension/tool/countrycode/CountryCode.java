package baibao.extension.tool.countrycode;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 国家/地区编码（ISO 3166-1）.
 * @author Kahle
 */
@Data
@NoArgsConstructor
public class CountryCode implements Serializable {
    private String nameEn;
    private String nameZh;
    private String fullNameEn;
    private String fullNameZh;
    private String alphaCode2;
    private String alphaCode3;
    private String numericCode3;
}
