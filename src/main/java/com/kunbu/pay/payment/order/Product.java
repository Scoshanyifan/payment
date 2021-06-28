package com.kunbu.pay.payment.order;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
//@Entity
//@Table(name="product")
public class Product {

    private Long id;

    private String name;

    private Long price;

}
