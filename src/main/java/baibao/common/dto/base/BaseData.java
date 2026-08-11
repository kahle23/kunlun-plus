package baibao.common.dto.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础的数据对象.
 * @author Kahle
 */
@Data
public abstract class BaseData implements Serializable {

    /**
     * 序号
     */
    private Long serialNumber;



    /**
     * 平台信息
     */
    private String platform;
    /**
     * 租户ID
     */
    private String tenantId;


    /**
     * 数据的所属人ID
     */
    private Long ownerId;
    /**
     * 数据的所属人名称
     */
    private String ownerName;
    /**
     * 数据的所属机构ID
     */
    private Long ownOrgId;
    /**
     * 数据的所属机构名称
     */
    private String ownOrgName;


    /**
     * 创建者
     */
    private Long createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改者
     */
    private Long modifyUser;
    /**
     * 修改时间
     */
    private Date modifyTime;


    /**
     * 删除状态：0 未删除，1 已删除
     */
    private Integer deleteStatus;

}
