package org.thingsboard.server.dao.hs.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.thingsboard.server.dao.hs.entity.po.Order;
import org.thingsboard.server.dao.hs.utils.CommonUtil;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = HsModelConstants.ORDER_TABLE_NAME)
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity extends BasePgEntity<OrderEntity> implements ToData<Order> {
    /**
     * 租户Id
     */
    @Column(name = HsModelConstants.GENERAL_TENANT_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID tenantId;

    @Column(name = HsModelConstants.ORDER_NO)
    private String orderNo;

    @Column(name = HsModelConstants.ORDER_TOTAL)
    private BigDecimal total;

    @Column(name = HsModelConstants.GENERAL_PRODUCTION_LINE_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID productionLineId;

    @Column(name = HsModelConstants.GENERAL_WORKSHOP_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID workshopId;

    @Column(name = HsModelConstants.GENERAL_FACTORY_ID, columnDefinition = "uuid")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID factoryId;

    @Column(name = HsModelConstants.ORDER_CONTRACT_NO)
    private String contractNo;

    @Column(name = HsModelConstants.ORDER_REF_ORDER_NO)
    private String refOrderNo;

    @Column(name = HsModelConstants.ORDER_TAKE_TIME)
    private Long takeTime;

    @Column(name = HsModelConstants.ORDER_CUSTOMER_ORDER_NO)
    private String customerOrderNo;

    @Column(name = HsModelConstants.ORDER_CUSTOMER)
    private String customer;

    @Column(name = HsModelConstants.ORDER_TYPE)
    private String type;

    @Column(name = HsModelConstants.ORDER_BIZ_PRACTICE)
    private String bizPractice;

    @Column(name = HsModelConstants.ORDER_CURRENCY)
    private String currency;

    @Column(name = HsModelConstants.ORDER_EXCHANGE_RATE)
    private String exchangeRate;

    @Column(name = HsModelConstants.ORDER_TAX_RATE)
    private String taxRate;

    @Column(name = HsModelConstants.ORDER_TAXES)
    private String taxes;

    @Column(name = HsModelConstants.ORDER_TOTAL_AMOUNT)
    private BigDecimal totalAmount;

    @Column(name = HsModelConstants.ORDER_UNIT)
    private String unit;

    @Column(name = HsModelConstants.ORDER_UNIT_PRICE_TYPE)
    private String unitPriceType;

    @Column(name = HsModelConstants.ORDER_ADDITIONAL_AMOUNT)
    private BigDecimal additionalAmount;

    @Column(name = HsModelConstants.ORDER_PAYMENT_METHOD)
    private String paymentMethod;

    @Column(name = HsModelConstants.ORDER_EMERGENCY_DEGREE)
    private String emergencyDegree;

    @Column(name = HsModelConstants.ORDER_TECHNOLOGICAL_REQUIREMENTS)
    private String technologicalRequirements;

    @Column(name = HsModelConstants.ORDER_NUM)
    private BigDecimal num;

    @Column(name = HsModelConstants.ORDER_SEASON)
    private String season;

    @Column(name = HsModelConstants.ORDER_MERCHANDISER)
    private String merchandiser;

    @Column(name = HsModelConstants.ORDER_SALESMAN)
    private String salesman;

    @Column(name = HsModelConstants.ORDER_SHORT_SHIPMENT)
    private String shortShipment;

    @Column(name = HsModelConstants.ORDER_OVER_SHIPMENT)
    private String overShipment;

    @Column(name = HsModelConstants.ORDER_COMMENT)
    private String comment;

    @Column(name = HsModelConstants.ORDER_INTENDED_TIME)
    private Long intendedTime;

    @Column(name = HsModelConstants.ORDER_STANDARD_AVAILABLE_TIME)
    private BigDecimal standardAvailableTime;

    @Column(name = HsModelConstants.ORDER_IS_DONE)
    private Boolean isDone;

    public OrderEntity() {
    }

    public OrderEntity(String orderNo) {
        super();
        this.orderNo = orderNo;
    }

    public OrderEntity(Order common) {
        this.id = CommonUtil.toUUIDNullable(common.getId());
        this.tenantId = CommonUtil.toUUIDNullable(common.getTenantId());
        this.orderNo = common.getOrderNo();
        this.total = common.getTotal();
        this.productionLineId = CommonUtil.toUUIDNullable(common.getProductionLineId());
        this.workshopId = CommonUtil.toUUIDNullable(common.getWorkshopId());
        this.factoryId = CommonUtil.toUUIDNullable(common.getFactoryId());
        this.contractNo = common.getContractNo();
        this.refOrderNo = common.getRefOrderNo();
        this.takeTime = common.getTakeTime();
        this.customerOrderNo = common.getCustomerOrderNo();
        this.customer = common.getCustomer();
        this.type = common.getType();
        this.bizPractice = common.getBizPractice();
        this.currency = common.getCurrency();
        this.exchangeRate = common.getExchangeRate();
        this.taxRate = common.getTaxRate();
        this.taxes = common.getTaxes();
        this.totalAmount = common.getTotalAmount();
        this.unit = common.getUnit();
        this.unitPriceType = common.getUnitPriceType();
        this.paymentMethod = common.getPaymentMethod();
        this.emergencyDegree = common.getEmergencyDegree();
        this.technologicalRequirements = common.getTechnologicalRequirements();
        this.num = common.getNum();
        this.season = common.getSeason();
        this.merchandiser = common.getMerchandiser();
        this.salesman = common.getSalesman();
        this.shortShipment = common.getShortShipment();
        this.overShipment = common.getOverShipment();
        this.comment = common.getComment();
        this.intendedTime = common.getIntendedTime();
        this.standardAvailableTime = common.getStandardAvailableTime();
        this.additionalAmount = common.getAdditionalAmount();
        this.isDone = common.getIsDone();

        this.setCreatedTimeAndCreatedUser(common);
    }

    /**
     * to data
     */
    public Order toData() {
        Order common = new Order();
        common.setId(id.toString());
        common.setTenantId(CommonUtil.toStrUUIDNullable(tenantId));
        common.setOrderNo(orderNo);
        common.setTotal(total);
        common.setProductionLineId(CommonUtil.toStrUUIDNullable(productionLineId));
        common.setWorkshopId(CommonUtil.toStrUUIDNullable(workshopId));
        common.setFactoryId(CommonUtil.toStrUUIDNullable(factoryId));
        common.setContractNo(contractNo);
        common.setRefOrderNo(refOrderNo);
        common.setTakeTime(takeTime);
        common.setCustomerOrderNo(customerOrderNo);
        common.setCustomer(customer);
        common.setType(type);
        common.setBizPractice(bizPractice);
        common.setCurrency(currency);
        common.setExchangeRate(exchangeRate);
        common.setTaxRate(taxRate);
        common.setTaxes(taxes);
        common.setTotalAmount(totalAmount);
        common.setUnit(unit);
        common.setUnitPriceType(unitPriceType);
        common.setAdditionalAmount(additionalAmount);
        common.setPaymentMethod(paymentMethod);
        common.setEmergencyDegree(emergencyDegree);
        common.setTechnologicalRequirements(technologicalRequirements);
        common.setNum(num);
        common.setSeason(season);
        common.setMerchandiser(merchandiser);
        common.setSalesman(salesman);
        common.setShortShipment(shortShipment);
        common.setOverShipment(overShipment);
        common.setComment(comment);
        common.setStandardAvailableTime(standardAvailableTime);
        common.setIntendedTime(intendedTime);
        common.setIsDone(isDone);

        common.setCreatedTime(createdTime);
        common.setCreatedUser(createdUser);
        common.setUpdatedTime(updatedTime);
        common.setUpdatedUser(updatedUser);
        return common;
    }
}
