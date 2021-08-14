package com.gfg.jbdl12employee.portal.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@AllArgsConstructor

@Getter
@Setter
@SuperBuilder
@DiscriminatorValue(value="hr")
public class HR extends Employee{

}
