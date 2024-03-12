package com.inysoft.samba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inysoft.samba.entity.AiChat;

import java.util.List;

@Repository
public interface AiChatRepository extends JpaRepository<AiChat, Long> {


    public AiChat getFindByUid(String uid);

}
