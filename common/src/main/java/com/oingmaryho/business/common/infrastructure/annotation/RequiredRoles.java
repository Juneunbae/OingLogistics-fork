package com.oingmaryho.business.common.infrastructure.annotation;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import java.lang.annotation.*;

@Target({ElementType.METHOD})  // 메서드에 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임 시 반영
@Documented
public @interface RequiredRoles {
	UserRoleType[] value(); // 필요한 역할 목록
}