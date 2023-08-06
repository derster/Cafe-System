package com.derster.cafesystem.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(name = "Bill.getAllBills", query = "select b from Bill b order by b.id desc")
@NamedQuery(name = "Bill.getBillByCreatedBy", query = "select b from Bill b where b.createdBy =:username order by b.id desc")

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "bill")
@AllArgsConstructor @NoArgsConstructor
public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "contactnumber")
    private String contactNumber;
    @Column(name = "paymentmethod")
    private String paymentMethod;
    @Column(name = "totalamount")
    private Integer totalAmount;
    @Column(name = "paymentdetails", columnDefinition = "json")
    private String productDetails;
    @Column(name = "createdby")
    private String createdBy;
}
