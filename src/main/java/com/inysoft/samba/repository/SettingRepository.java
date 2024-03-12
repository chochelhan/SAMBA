package com.inysoft.samba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inysoft.samba.entity.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {


    public Setting getFindById(Long id);


}
