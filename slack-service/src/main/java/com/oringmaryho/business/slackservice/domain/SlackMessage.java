package com.oringmaryho.business.slackservice.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.oingmaryho.business.common.domain.entity.BaseEntity;

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
@Table(name = "p_slack_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlackMessage extends BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "receiver_id", nullable = false)
	private Long receiverId;

	@Column(nullable = false)
	private String message;

	@Column(name = "sent_at", nullable = false)
	private LocalDateTime sentAt;

	public void setMessage(String message) {
		this.message = message;
	}
}
