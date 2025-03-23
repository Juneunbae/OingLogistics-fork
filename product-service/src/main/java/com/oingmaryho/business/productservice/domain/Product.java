package com.oingmaryho.business.productservice.domain;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.oingmaryho.business.common.domain.entity.BaseEntity;
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
	private String companyName;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private UUID manageHubId;

	@Column(nullable = false)
	private Integer stock;

	@Column(nullable = false)
	private Integer price;

	public void update(UUID manageHubId,String companyName, String name, Integer price, Integer stock) {
		Optional.ofNullable(manageHubId)
			.ifPresent(value -> this.manageHubId = value);

		Optional.ofNullable(companyName)
			.ifPresent(value -> {
				if (value.isBlank()) {
					throw new ProductException(ErrorCode.INVALID_COMPANY_NAME);
				}
				this.companyName = value;
			});

		Optional.ofNullable(name)
			.ifPresent(value -> {
				if (value.isBlank()) {
					throw new ProductException(ErrorCode.INVALID_PRODUCT_NAME);
				}
				this.name = value;
			});

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

	public void updateStock(Integer newStock) {
		if (newStock < 0) {
			throw new ProductException(ErrorCode.OUT_OF_STOCK);
		}
		this.stock = newStock;
	}

	public void increaseStock(Integer quantity) {
		if (quantity < 0) throw new IllegalArgumentException("추가 수량은 음수일 수 없습니다.");
		this.stock += quantity;
	}

	public void decreaseStock(Integer quantity) {
		if (quantity <= 0) throw new IllegalArgumentException("차감 수량은 0보다 커야 합니다.");
		if (this.stock < quantity) throw new IllegalStateException("재고 부족");
		this.stock -= quantity;
	}

}
