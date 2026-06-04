package com.tpfinal.sportcenter_api.repository.servicetype;

import com.tpfinal.sportcenter_api.entity.servicetype.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaServiceTypeRepository extends JpaRepository<ServiceType, Long>, JpaSpecificationExecutor<ServiceType> {
}
