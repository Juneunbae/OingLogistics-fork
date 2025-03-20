package com.oingmaryho.business.productservice.domain;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.oingmaryho.business.common.entity.BaseEntity;
import com.oingmaryho.business.productservice.exception.ErrorCode;
import com.oingmaryho.business.productservice.exception.ProductException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String productCode;

	@Column(nullable = false)
	private UUID companyId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private UUID manageHubId;

	@Column(nullable = false)
	private Long stock;

	@Column(nullable = false)
	private Long price;

	public void update(String name, Long price, Long stock) {
		Optional.ofNullable(name)
			.filter(n -> !n.isBlank())
			.ifPresentOrElse(
				value -> this.name = value,
				() -> { throw new ProductException(ErrorCode.INVALID_PRODUCT_NAME); }
			);

		Optional.ofNullable(price)
			.ifPresent(value -> {
				if (value <= 0) {
					throw new ProductException(ErrorCode.INVALID_PRICE);
				}
				this.price = value;
			});

		Optional.ofNullable(stock)
			.ifPresent(value -> {
				if (value < 0) {
					throw new ProductException(ErrorCode.INVALID_STOCK);
				}
				this.stock = value;
			});
	}
}
