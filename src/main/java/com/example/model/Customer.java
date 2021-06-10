package com.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//To use the @Data annotation you should add the Lombok dependency.
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		@NotNull
		private String firstName;
		private String lastName;
}
