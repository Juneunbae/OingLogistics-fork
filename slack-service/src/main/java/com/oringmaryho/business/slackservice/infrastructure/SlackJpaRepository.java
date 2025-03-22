package com.oringmaryho.business.slackservice.infrastructure;

import com.oringmaryho.business.slackservice.domain.SlackMessage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackJpaRepository extends JpaRepository<SlackMessage, UUID> {

}
