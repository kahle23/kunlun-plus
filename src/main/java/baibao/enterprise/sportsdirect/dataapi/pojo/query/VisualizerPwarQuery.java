package baibao.enterprise.sportsdirect.dataapi.pojo.query;

import kunlun.data.bean.BeanUtil;
import kunlun.data.map.ToSoMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * SDI 可视化 PWAR 查询对象.
 * @author Kahle
 */
@Data
@NoArgsConstructor
public class VisualizerPwarQuery implements Serializable, ToSoMap {

    /**
     * 款号 deptStyl
     */
    private String dept;
    /**
     * 颜色编码
     */
    private String colour;
    /**
     * 尺码编码
     */
    private String size;
    /**
     * 仓库（国家区域）编码
     */
    private String warehouse;
    /**
     * 运行策略：0 正常配置，10 仅连续性，18 仅显示已通过审核的采购订单，23 显示工作台 2.0 中的排演订单，50 不包括隐藏尺寸和停止购买标志
     */
    private String run;

    public VisualizerPwarQuery(String dept, String colour, String size, String warehouse, String run) {
        this.dept = dept;
        this.colour = colour;
        this.size = size;
        this.warehouse = warehouse;
        this.run = run;
    }

    @Override
    public Map<String, Object> toMap() {

        return BeanUtil.beanToMap(this);
    }

}
