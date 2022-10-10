package com.ennea.assignment;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter // Lombok Annotation that would handle getters for this class
@Setter // Lombok Annotation that would handle setters for this class
@Entity // Marker Annotation defining InventoryItem class <-> inventory table relationship
@Table(name = Constants.tableName) // Direction to map this class to the correct table in Database
public class InventoryItem {
    private static final long serialVersion = 0L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id
    Integer id;
    @CsvBindByName // Bind to CSV column having the same name
    private String code;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String batch;
    @CsvBindByName
    private Integer stock;
    @CsvBindByName
    private Integer deal;
    @CsvBindByName
    private Integer free;
    @CsvBindByName
    private Double mrp;
    @CsvBindByName
    private Double rate;
    @CsvCustomBindByName(converter = SafeDateConverter.class)
    // Bind to CSV column having the same name with custom handling
    private Date exp;
    @CsvBindByName
    private String company;
    @CsvBindByName
    private String supplier;

    public InventoryItem() {
    }

}
